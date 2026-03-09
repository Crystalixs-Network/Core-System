# Roadmap – Economy System

## Projektziel
Dieses Dokument beschreibt eine detaillierte Roadmap für die Umsetzung eines **Economy Systems** mit zwei Währungen:

- **Coins** als Standard-Währung
- **Gems** als Premium-Währung

Aus dem bereitgestellten Konzept ergeben sich aktuell folgende Kernfunktionen:

### Coins
- Standard-Währung
- handelbar
- Commands:
  - `/coins`
  - `/balance`
  - `/pay`

### Gems
- Premium-Währung
- **nicht** per `/pay` transferierbar
- nur über **Admin-Commands** verwaltbar

### Admin-Befehle
- `/eco give <währung> ...`
- `/eco set ...`
- `/eco take ...`

### Permissions
- `crystalixs.eco.admin`
- `crystalixs.eco.balance`
- `crystalixs.eco.pay`

---

## 1. Produktvision
Das Economy System soll eine stabile Grundlage für Handel, Progression, Belohnungen und administrative Steuerung schaffen.

### Erfolgsziele
- klare Trennung zwischen **Coins** und **Gems**
- sichere und nachvollziehbare Kontostände
- einfache Benutzung über Commands
- saubere Rechtevergabe für Nutzer und Admins
- spätere Erweiterbarkeit für Shops, Daily Rewards, Quests, Leaderboards und Web-Interfaces

---

## 2. Scope des ersten Releases (MVP)
Der erste Release sollte nur die Funktionen enthalten, die im Screenshot bereits angelegt sind.

### Im MVP enthalten
- Konten-/Wallet-System pro User
- Speicherung von Coins und Gems
- Anzeige des Kontostands
- Transfer von Coins zwischen Spielern über `/pay`
- Admin-Verwaltung über `/eco give`, `/eco set`, `/eco take`
- Rechteprüfung über Permissions
- Validierung gegen negative Werte und fehlerhafte Eingaben
- Transaktionslogging

---

## 3. Fachliche Regeln

### 3.1 Coins
- Coins sind die **Standard-Währung**.
- Coins dürfen zwischen Spielern transferiert werden.
- Coins werden bei `/pay` auf ausreichendes Guthaben geprüft.

### 3.2 Gems
- Gems sind die **Premium-Währung**.
- Gems dürfen **nicht** direkt zwischen Spielern transferiert werden.
- Gems können nur durch Admin-Aktionen oder definierte Systemquellen vergeben/abgezogen werden.
- Gems benötigen eine besonders strenge Protokollierung.

### 3.3 Rechte
- `crystalixs.eco.admin`: Zugriff auf `/eco give`, `/eco set`, `/eco take`
- `crystalixs.eco.balance`: Zugriff auf `/coins` bzw. `/balance`
- `crystalixs.eco.pay`: Zugriff auf `/pay`

### 3.4 Sicherheitsregeln
- keine negativen Kontostände
- keine Transfers an sich selbst
- keine Transfers mit Wert `0`
- keine ungültigen Währungstypen
- Admin-Commands müssen geloggt werden
- Premium-Währung darf nicht über normale Spieler-Commands manipulierbar sein

---

## 4. Technische Zielarchitektur

### 4.1 Kernmodule
Empfohlene Modulstruktur:

1. **Command Layer**
   - verarbeitet `/coins`, `/balance`, `/pay`, `/eco ...`
2. **Service Layer**
   - enthält Business-Logik für Kontostände, Transfers, Admin-Aktionen
3. **Persistence Layer**
   - Datenbankzugriff für Wallets und Logs
4. **Permission Layer**
   - Rechteprüfung
5. **Audit/Logging Layer**
   - Protokollierung aller Änderungen

### 4.2 Datenmodell
Empfohlene Tabellen/Collections:

#### `wallets`
- `user_id`
- `coins`
- `gems`
- `created_at`
- `updated_at`

#### `transactions`
- `id`
- `type` (`PAY`, `ADMIN_GIVE`, `ADMIN_SET`, `ADMIN_TAKE`, `SYSTEM_REWARD`)
- `currency` (`COINS`, `GEMS`)
- `amount`
- `from_user_id` (optional)
- `to_user_id` (optional)
- `actor_user_id` (Admin/System)
- `reason` (optional, aber empfohlen)
- `created_at`

#### `economy_audit`
- `id`
- `action`
- `payload`
- `status`
- `created_at`


## 5. Command-Design

### 5.1 `/coins`
**Zweck:** Zeigt den Coin-Kontostand an.

**MVP-Verhalten:**
- Nutzer sieht seine Coins
- optional später: formatierte Übersicht aller Währungen

**Validierungen:**
- Permission `crystalixs.eco.balance`

### 5.2 `/balance`
**Zweck:** Allgemeiner Kontostand.

**MVP-Verhalten:**
- zeigt Coins und Gems in einer einheitlichen Ausgabe
- kann `/coins` perspektivisch ersetzen oder erweitern

**Validierungen:**
- Permission `crystalixs.eco.balance`

### 5.3 `/pay <spieler> <amount>`
**Zweck:** Coins an einen anderen Spieler senden.

**Regeln:**
- nur Coins
- Sender != Empfänger
- `amount > 0`
- ausreichendes Guthaben notwendig
- beide Wallets werden atomar aktualisiert
- erfolgreicher Transfer wird geloggt

**Validierungen:**
- Permission `crystalixs.eco.pay`
- Zielspieler existiert
- Betrag ist numerisch und positiv

### 5.4 `/eco give <spieler> <currency> <amount>`
**Zweck:** Admin gibt Coins oder Gems.

**Regeln:**
- Coins und Gems erlaubt
- Betrag > 0
- nur mit Admin-Permission
- Aktion wird im Audit-Log gespeichert

### 5.5 `/eco set <spieler> <currency> <amount>`
**Zweck:** Admin setzt Kontostand direkt.

**Regeln:**
- Zielwert darf nicht negativ sein
- besonders kritisch, deshalb Audit mit vorher/nachher-Wert speichern

### 5.6 `/eco take <spieler> <currency> <amount>`
**Zweck:** Admin zieht Coins oder Gems ab.

**Regeln:**
- Betrag > 0
- Ergebnis darf nicht negativ werden
- vollständig loggen

---

## 6. Roadmap nach Phasen

## Phase 0 – Planung & Spezifikation
**Ziel:** Fachliche und technische Grundlagen final festlegen.

### Aufgaben
- endgültige Definition aller Commands und Syntax
- Festlegen, ob `/coins` nur Coins oder alle Währungen anzeigen soll
- Festlegen, ob `/balance` redundant oder erweitert ist
- Definition der Permission-Matrix
- Definition von Fehlermeldungen und Erfolgsnachrichten
- Festlegen von Naming-Konventionen (`COINS`, `GEMS`, Permission-Namespace)
- Klären, ob Offline-Spieler unterstützt werden sollen

### Deliverables
- Command-Spezifikation
- Permission-Spezifikation
- Datenmodell-Entwurf
- Validierungsregeln

### Aufwand
- klein bis mittel

---

## Phase 1 – Grundlegende Infrastruktur
**Ziel:** Technisches Fundament für Economy-Daten schaffen.

### Aufgaben
- Projektstruktur anlegen
- Config-Datei anlegen (`database`, `messages`, `features`)
- Datenbankanbindung implementieren
- Repository/DAO für Wallets erstellen
- Auto-Erstellung einer Wallet beim ersten Join oder ersten Zugriff
- Migrationen für Tabellen erstellen
- Basales Logger-System einbauen

### Deliverables
- lauffähige Infrastruktur
- Wallet-Speicherung funktionsfähig
- Testdaten lokal anlegbar

### Akzeptanzkriterien
- für einen User kann ein Wallet erstellt, geladen und gespeichert werden
- Coins/Gems bleiben nach Neustart erhalten

---

## Phase 2 – Wallet- und Economy-Service
**Ziel:** Business-Logik kapseln und absichern.

### Aufgaben
- `EconomyService` implementieren
- Methoden definieren:
  - `getBalance(userId)`
  - `getCoins(userId)`
  - `getGems(userId)`
  - `addCurrency(userId, currency, amount)`
  - `setCurrency(userId, currency, amount)`
  - `takeCurrency(userId, currency, amount)`
  - `payCoins(fromUserId, toUserId, amount)`
- atomare Transaktionen für Abbuchung + Gutschrift einbauen
- zentrale Validierungen implementieren
- Fehlerobjekte / Result-Typen definieren

### Deliverables
- stabile Service-Schicht
- wiederverwendbare Business-Methoden

### Akzeptanzkriterien
- keine Methode kann negative Stände erzeugen
- `payCoins` ist transaktionssicher
- Gems können nicht versehentlich per Spieler-Transfer verschoben werden

---

## Phase 3 – Spieler-Commands
**Ziel:** Nutzer können ihren Kontostand sehen und Coins übertragen.

### Aufgaben
- `/coins` implementieren
- `/balance` implementieren
- `/pay <spieler> <amount>` implementieren
- Command-Parsing absichern
- Permissions prüfen
- nutzerfreundliche Antworten ausgeben
- Anti-Spam-Cooldown für `/pay` prüfen (optional, aber sinnvoll)

### Beispiel-Fehlermeldungen
- „Du hast keine Berechtigung für diesen Befehl.“
- „Bitte gib einen gültigen Betrag an.“
- „Du kannst dir selbst keine Coins senden.“
- „Dein Guthaben reicht nicht aus.“
- „Dieser Spieler wurde nicht gefunden.“

### Deliverables
- funktionierende Player-Commands

### Akzeptanzkriterien
- Nutzer kann Coins abfragen
- Nutzer kann Coins an andere senden
- ungültige Eingaben führen nicht zu inkonsistenten Daten

---

## Phase 4 – Admin-Commands
**Ziel:** Sichere Verwaltung von Coins und Gems durch autorisierte Rollen.

### Aufgaben
- `/eco give <spieler> <currency> <amount>`
- `/eco set <spieler> <currency> <amount>`
- `/eco take <spieler> <currency> <amount>`
- Währungsparser für `coins` und `gems`
- vollständiges Audit-Logging
- optionale `reason`-Parameter vorbereiten

### Empfohlene Erweiterung
Admin-Befehle lieber früh mit optionalem Grund planen:
- `/eco give <spieler> <currency> <amount> [reason]`
- `/eco set <spieler> <currency> <amount> [reason]`
- `/eco take <spieler> <currency> <amount> [reason]`

Das verbessert Nachvollziehbarkeit und Moderation.

### Deliverables
- vollständiges Admin-Panel über Commands

### Akzeptanzkriterien
- nur berechtigte Nutzer können Admin-Commands ausführen
- jede Admin-Aktion erscheint im Log
- vorheriger und neuer Kontostand sind nachvollziehbar

---

## Phase 5 – Logging, Monitoring & Audit
**Ziel:** Manipulationen, Fehler und Supportfälle nachvollziehbar machen.

### Aufgaben
- Transaktionshistorie speichern
- separate Audit-Logs für Admin-Aktionen
- Fehlerfälle protokollieren
- Logging-Level definieren (`INFO`, `WARN`, `ERROR`, `AUDIT`)
- Export/Ansicht für Logs vorbereiten

### Wichtige Log-Fälle
- jeder `/pay`-Transfer
- jede `give/set/take`-Aktion
- jeder fehlgeschlagene Admin-Versuch
- ungültige Command-Nutzung
- Datenbankfehler

### Deliverables
- nachvollziehbare Historie
- bessere Debugbarkeit

### Akzeptanzkriterien
- jede Wertänderung ist rückverfolgbar
- Support kann problematische Vorgänge prüfen

---

## Phase 6 – Testing & Qualitätssicherung
**Ziel:** System vor Release absichern.

### Aufgaben
- Unit-Tests für Service-Logik
- Integrationstests für Datenbankzugriffe
- Command-Tests für Parsing und Permissions
- Tests für Randfälle:
  - Betrag = 0
  - Betrag < 0
  - zu wenig Guthaben
  - unbekannte Währung
  - unbekannter Spieler
  - Self-Pay
  - Race Conditions bei parallelen Transaktionen
- Lasttests bei vielen Transaktionen

### Testmatrix (Auszug)
| Fall | Erwartung |
|---|---|
| `/pay Steve 100` bei genug Guthaben | Transfer erfolgreich |
| `/pay Steve 0` | Fehler |
| `/pay Steve -5` | Fehler |
| `/pay sich selbst` | Fehler |
| `/eco give Alex gems 50` | Gems werden gutgeschrieben |
| `/eco take Alex gems 999999` bei zu wenig Gems | Fehler |
| `/eco set Alex coins -1` | Fehler |

### Deliverables
- dokumentierte Testabdeckung
- stabile Release-Kandidaten

---

## Phase 7 – Release des MVP
**Ziel:** Erste produktive Version ausrollen.

### Aufgaben
- Versioning einführen (`v0.1.0` oder `v1.0.0`)
- Release Notes schreiben
- Standard-Config ausliefern
- Permissions dokumentieren
- Setup-Doku für Server-Admins erstellen
- Rollback-Plan vorbereiten

### Deliverables
- produktionsfähiger MVP
- Doku für Betreiber/Admins

### Akzeptanzkriterien
- Installation funktioniert ohne manuelle Hotfixes
- Commands und Permissions sind dokumentiert
- Logs helfen bei Supportfällen

---

## 7. Post-MVP Roadmap
Nach dem MVP kann das System in mehreren Ausbaustufen erweitert werden.

### Version 1.1 – Nutzerkomfort
- formatierte Balance-Ausgabe
- `/balance <spieler>` für Admins oder Moderatoren
- Transaktionshistorie für Nutzer
- konfigurierbare Nachrichten
- Alias-Support für Commands

### Version 1.2 – Gameplay-Integration
- Belohnungen durch Jobs/Quests/Events
- Daily Rewards
- Login-Bonus
- Kill-/Missions-/Quest-Rewards

### Version 1.3 – Wirtschaftsfeatures
- Shopsystem
- Marktplatz
- Gebühren/Transaktionssteuer für Handel
- Leaderboard für Coins
- Sink-Mechaniken gegen Inflation

### Version 1.4 – Premium-Ökonomie
- Gems im Shop einsetzbar
- exklusive Premium-Items
- kontrollierte Systemquellen für Gems
- Purchase-/Voucher-Integration

### Version 1.5 – Tools für Management
- Admin-Historie UI
- Web-Dashboard
- CSV/JSON-Export von Transaktionen
- Metriken zu Geldmenge und Aktivität

---

## 8. Offene Produktentscheidungen
Diese Punkte sollten vor der finalen Umsetzung entschieden werden:

1. Soll `/coins` nur Coins oder alle Wallet-Daten anzeigen?
2. Soll `/balance` identisch zu `/coins` sein oder mehr Informationen liefern?
3. Dürfen Admins auch Offline-Spieler verwalten?
4. Soll es Maximalwerte pro Währung geben?
5. Soll `/pay` gerundet werden oder nur Integer-Beträge erlauben?
6. Soll Gems später über Shops verbraucht werden?
7. Braucht ihr früh eine Historie pro Spieler?
8. Sollen Admin-Aktionen einen Pflicht-Grund haben?

---

## 9. Empfohlene Implementierungsreihenfolge
Die praktisch sinnvollste Reihenfolge ist:

1. Datenmodell & Wallet-Speicherung
2. Economy-Service
3. `/coins` und `/balance`
4. `/pay`
5. `/eco give`
6. `/eco set`
7. `/eco take`
8. Logging & Audit
9. Tests & Hardening
10. Release & Dokumentation

So entsteht zuerst eine stabile Kernlogik, bevor kritische Admin-Funktionen und Transfers produktiv genutzt werden.

---

## 10. Definition of Done
Ein Feature gilt als fertig, wenn:

- Command funktioniert fachlich korrekt
- Rechteprüfung ist eingebaut
- ungültige Eingaben sind abgefangen
- Daten werden persistent gespeichert
- Änderungen werden geloggt
- Tests decken Kernfälle ab
- Nutzer- und Admin-Meldungen sind verständlich
- Dokumentation wurde aktualisiert

---

## 11. Konkreter MVP-Meilensteinplan

### Milestone A – Foundation
- Projektstruktur
- Datenbank
- Wallet-Modell
- Basis-Config

### Milestone B – Core Economy
- Economy-Service
- Coins/Gems lesen und schreiben
- Validierung

### Milestone C – User Commands
- `/coins`
- `/balance`
- `/pay`

### Milestone D – Admin Commands
- `/eco give`
- `/eco set`
- `/eco take`

### Milestone E – Stabilisierung
- Audit-Logs
- Tests
- Fehlerbehandlung
- Doku

### Milestone F – Release
- Deployment
- Live-Tests
- Feedbackrunde
- Bugfixing

---

## 12. Empfehlung für den nächsten Schritt
Als nächstes sollte aus dieser Roadmap direkt ein **technisches Pflichtenheft** oder eine **Ticket-Struktur für GitHub / Jira / Trello** erstellt werden.

Die beste Sofortmaßnahme wäre:
- zuerst das Datenmodell und die Command-Spezifikation finalisieren
- danach direkt die Service-Schicht und `/pay` sauber umsetzen

Denn `/pay` ist die erste Funktion mit echter Transaktionslogik und deckt den Großteil der Kernprobleme früh auf.

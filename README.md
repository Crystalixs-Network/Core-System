# Core System

> Zentrale Netzwerklogik für Velocity und die grundlegenden Systemfunktionen eines Minecraft-Netzwerks.

<br>

## Überblick

| Bereich               | Nutzen                                                                       |
|-----------------------|------------------------------------------------------------------------------|
| Wartungsmodus         | Schließt das Netzwerk kontrolliert und lässt nur berechtigte Personen durch. |
| MOTD-Steuerung        | Zeigt automatisch die passende Darstellung für Normalbetrieb oder Wartung.   |
| Dynamische Tablist    | Befüllt Header und Footer mit aktuellen Netzwerk- und Serverinformationen.   |
| Netzwerk-Commands     | Stellt zentrale Admin- und Team-Befehle direkt auf dem Proxy bereit.         |
| Konfigurations-Reload | Übernimmt Änderungen an Config und Nachrichten ohne kompletten Neustart.     |
| Economy (Paper)       | Verwaltet Coins/Gems mit Transfers, Admin-Befehlen und Persistenz.           |

---
<br>

## Kernfeatures

### Wartungsmodus

Der Wartungsmodus ist die zentrale Betriebsfunktion des Plugins.

Beim Aktivieren passiert Folgendes:

- normale Spieler können dem Proxy nicht mehr beitreten
- bereits verbundene Spieler ohne Freigabe werden getrennt
- die MOTD wechselt automatisch in den Wartungszustand
- ein konfigurierbarer Wartungsscreen mit Hinweistext und Link wird angezeigt

Das eignet sich für Updates, kurzfristige Eingriffe und kontrollierte Tests im Live-Betrieb.

### Dynamische MOTD

Die Serverliste reagiert direkt auf den aktuellen Netzwerkzustand:

- im Normalbetrieb wird die reguläre MOTD angezeigt
- im Wartungsmodus erscheint eine eigene Wartungs-MOTD
- zusätzlich wird eine abweichende Versionsanzeige gesetzt, damit Wartung sofort erkennbar ist

### Dynamische Tablist

Beim Verbinden zu einem Backend wird die Tablist automatisch neu gesetzt:

- konfigurierbarer Header
- konfigurierbarer Footer (& aktueller Servername)

### Globale Netzwerkbefehle

Über den Proxy lassen sich zentrale Informationen schnell abrufen:

- auf welchem Backend ein Spieler gerade online ist
- ob ein registrierter Backend-Server erreichbar ist
- zu welchem Server man einem Spieler folgen möchte

### Reloads ohne Neustart

Konfigurations- und Textänderungen lassen sich direkt übernehmen, ohne den Proxy komplett neu zu starten.

Unterstützt werden Reloads für:

- Config
- Nachrichten
- beides zusammen

Wenn Hot-Reloading in der Config aktiv ist, können Sprachdateien zusätzlich automatisch neu eingelesen werden.

### Economy (Paper)

Das Paper-Modul enthält ein Economy-System für Coins und Gems inklusive Transaktionen und Audit-Logging.

Enthalten sind:

- Kontostandabfragen (`/coins`, `/balance`)
- Coin-Transfers zwischen Spielern (`/pay`)
- Admin-Verwaltung (`/economy give|set|take`)
- Zahlendarstellung basierend auf der aufgelösten Translation-Locale
- Silent logging

### Logging im Hintergrund

Das Projekt bringt strukturiertes Logging mit, damit wichtige Admin-Aktionen und Fehler nachvollziehbar bleiben. Die Details dazu stehen in `LOGGING.md`.

---
<br>

## Projektstruktur

| Modul         | Rolle                                                                       |
|---------------|-----------------------------------------------------------------------------|
| `velocity`    | Zentrale Netzwerkfunktionen wie Commands, MOTD, Join-Kontrolle und Wartung. |
| `paper`       | Backend-seitige Erweiterungen auf Paper inklusive Economy-Commands.         |
| `persistence` | Datenmodelle, Stores und SQL-Migrationen.                                   |
| `common`      | Gemeinsame Infrastruktur (Logging, Translation, Bootstrap).                 |

---
<br>

## Commands

| Command                                          | Zweck                                                      | Typischer Einsatz                            |
|--------------------------------------------------|------------------------------------------------------------|----------------------------------------------|
| `/maintenance <true\|false>`                     | Aktiviert oder deaktiviert den Wartungsmodus.              | Updates, Tests, Notfallarbeiten              |
| `/core reload --config`                          | Lädt nur die Konfiguration neu.                            | Nach Änderungen an `config.json`             |
| `/core reload --messages`                        | Lädt nur die Nachrichten neu.                              | Nach Änderungen an Texten oder Übersetzungen |
| `/core reload --all`                             | Lädt Config und Nachrichten gemeinsam neu.                 | Nach größeren inhaltlichen Anpassungen       |
| `/help` oder `/?`                                | Zeigt das Hilfemenü.                                       | Zum Nachschlagen verfügbarer Befehle         |
| `/global-find <player>` oder `/gfind <player>`   | Zeigt, auf welchem Server ein Spieler ist.                 | Support, Moderation, Teamarbeit              |
| `/global-teleport <player>` oder `/gtp <player>` | Verbindet dich auf den Server des Zielspielers.            | Direktes Wechseln zu einem Spieler           |
| `/online <server>`                               | Prüft, ob ein registrierter Backend-Server erreichbar ist. | Betriebscheck, Fehlersuche                   |
| `/proxy-stop`                                    | Stoppt den Proxy kontrolliert.                             | Geplante Eingriffe oder Wartung              |
| `/coins`                                         | Zeigt den aktuellen Coin-Kontostand.                       | Schnelle Kontostandsprüfung                  |
| `/balance` oder `/bal`                           | Zeigt Coins und Gems an.                                   | Gesamtübersicht für Spieler                  |
| `/pay <player> <amount>`                         | Überweist Coins an einen anderen Spieler.                  | Spieler-zu-Spieler-Transfer                  |
| `/economy give <player> <currency> <amount>`     | Fügt Coins oder Gems hinzu.                                | Admin-Korrekturen, Rewards                   |
| `/economy set <player> <currency> <amount>`      | Setzt Coins oder Gems auf einen festen Wert.               | Moderation, Datenkorrekturen                 |
| `/economy take <player> <currency> <amount>`     | Zieht Coins oder Gems ab.                                  | Moderation, Rückabwicklung                   |

---
<br>

## Permissions

| Permission                     | Bedeutung                                                            |
|--------------------------------|----------------------------------------------------------------------|
| `core.command.maintenance`     | Erlaubt das Ein- und Ausschalten des Wartungsmodus.                  |
| `core.bypass.maintenance`      | Erlaubt den Beitritt trotz aktivem Wartungsmodus.                    |
| `core.command.core`            | Erlaubt Reload-Befehle für Config und Nachrichten.                   |
| `core.command.proxy-stop`      | Erlaubt das kontrollierte Stoppen des Proxy.                         |
| `core.command.global-find`     | Erlaubt das Abfragen des aktuellen Servers eines Spielers.           |
| `core.command.global-teleport` | Erlaubt das Wechseln auf den Server eines anderen Spielers.          |
| `core.command.online`          | Erlaubt die Prüfung registrierter Backend-Server auf Erreichbarkeit. |
| `core.command.coins`           | Erlaubt die Abfrage des eigenen Coin-Kontostands.                    |
| `core.command.balance`         | Erlaubt die Abfrage von Coins und Gems.                              |
| `core.command.pay`             | Erlaubt das Senden von Coins an andere Spieler.                      |
| `core.command.economy`         | Erlaubt administrative Economy-Befehle (`give`, `set`, `take`).      |

---
<br>

## Konfiguration

Die wichtigste Runtime-Datei im Velocity-Modul ist `config.json`. Dort werden unter anderem folgende Bereiche gesteuert:

- Hot-Reloading für Nachrichten
- normale MOTD
- Wartungsstatus
- Wartungs-MOTD
- Wartungsscreen inklusive Hinweistext und Link

Im Paper-Modul werden Nachrichten aus `plugins/Core/lang/messages_<locale>.conf` geladen.
Die Zahlendarstellung in Commands ist an die aufgelöste Translation-Locale gekoppelt.

> [!TIP]
> Die Texte unterstützen [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/) und lassen sich dadurch flexibel gestalten.
> Einen Editor für Liveansichten gibt es als [Adventure Text-Editor](https://adventure.kyori.net/).

---
<br>

## Persistenz und Migrationen

Die SQL-Struktur liegt im Modul `persistence` unter:

- `database/mariadb/<major>/setup.sql`
- `database/mariadb/<major>/migration.sql`
- `database/mariadb/<major>/patch_<n>.sql`

---
<br>

## Technische Basis

- Java 21
- Velocity für Proxy-Funktionen
- Paper für Backend-Erweiterungen
- Cloud Command Framework für Commands
- Configurate für Konfigurationen
- Adventure und MiniMessage für Komponenten und formatierte Texte

---

## Status

Das Projekt deckt die zentralen Netzwerkfunktionen für den Proxy-Betrieb bereits ab und erweitert diese auf Paper um ein persistentes Economy-System mit Commands, Logging und Migrationen.

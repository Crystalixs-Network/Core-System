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

### Logging im Hintergrund

Das Projekt bringt strukturiertes Logging mit, damit wichtige Admin-Aktionen und Fehler nachvollziehbar bleiben. Die Details dazu stehen in `LOGGING.md`.

---
<br>

## Projektstruktur

| Modul      | Rolle                                                                       |
|------------|-----------------------------------------------------------------------------|
| `velocity` | Zentrale Netzwerkfunktionen wie Commands, MOTD, Join-Kontrolle und Wartung. |
| `paper`    | Grundlage für Backend-seitige Erweiterungen auf Paper.                      |

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

---
<br>

## Konfiguration

Die wichtigste Runtime-Datei im Velocity-Modul ist `config.json`.

Dort werden unter anderem folgende Bereiche gesteuert:

- Hot-Reloading für Nachrichten
- normale MOTD
- Wartungsstatus
- Wartungs-MOTD
- Wartungsscreen inklusive Hinweistext und Link
- Tablist-Header und -Footer

> [!TIP]
> Die Texte unterstützen [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/) und lassen sich dadurch flexibel gestalten.
> Einen Editor für Liveansichten gibt es als [Adventure Text-Editor](https://adventure.kyori.net/).

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

Das Projekt deckt die zentralen Netzwerkfunktionen für den Proxy-Betrieb bereits ab und bildet eine saubere Grundlage für weitere Systemfunktionen.

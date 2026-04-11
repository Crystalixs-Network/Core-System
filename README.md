# Core System

> Zentrale Netzwerklogik für Velocity und die grundlegenden Systemfunktionen eines Minecraft-Netzwerks.

<br>

## Überblick

| Feature               | Environment    | Nutzen                                                                                                           |
|-----------------------|----------------|------------------------------------------------------------------------------------------------------------------|
| Wartungsmodus         | Proxy          | Schließt das Netzwerk kontrolliert und lässt nur berechtigte Personen durch.                                     |
| MOTD-Steuerung        | Proxy          | Zeigt automatisch die passende Darstellung für Normalbetrieb oder Wartung.                                       |
| Dynamische Tablist    | Proxy          | Befüllt Header und Footer mit aktuellen Netzwerk- und Serverinformationen sowie mit Prefixen der Ränge           |
| Netzwerk-Commands     | Proxy          | Stellt zentrale Admin- und Team-Befehle direkt auf dem Proxy bereit.                                             |
| Unified Help          | Proxy, Backend | Zeigt Proxy- und Backend-Befehle in einem gemeinsamen Help-Menü mit Paging, Server- und Permission-Filter.       |
| Konfigurations-Reload | Proxy          | Übernimmt Änderungen an Config und Nachrichten ohne kompletten Neustart.                                         |
| Economy               | Backend        | Verwaltet Coins/Gems mit Transfers, Admin-Befehlen und Persistenz.                                               |
| Home-System           | Backend        | Verwaltet Homes inklusive GUI, Umbenennen per Amboss und Icon-Auswahl mit Persistenz.                            |
| Ignore-System         | Backend        | Verwaltet Ignore-Beziehungen zwischen Spielern und blockiert direkte Interaktionen inkl. `unignore`-Vorschlägen. |
| Utility Commands      | Backend        | Stellt QoL-Commands wie Hat, Enderchest, Trash, Sit, Sign, Skull, Invsee, Vanish und Teleport bereit.            |
| Displays              | Backend        | Zeigt einen Rang-Prefix und ein Scoreboard mit den wichtigsten Informationen für den Spieler an.                 |

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

### Unified Help

Das Help-Menü aggregiert Befehle aus Velocity und den angebundenen Paper-Backends. Der Funktionsumfang umfasst:

- einheitliche Darstellung für Proxy- und Backend-Befehle
- Paginierung über `--page` bzw. `-p` (z.B. `/help --page 2`)
- Suche über Query (z.B. `/help economy`)
- Kombination aus Suche + Paginierung (`/help economy --page 2`)
- Filter auf den aktuellen Backend-Server des Spielers
- Filter nach verfügbaren Permissions des Senders
- Redis-basierte Synchronisation der Backend-Command-Kataloge

Wichtige Voraussetzung:

- `core-paper/config.json` → `redis-sync.backend-id` muss exakt dem Velocity-Servernamen entsprechen (z. B. `lobby-1`), sonst werden Backend-Befehle nicht dem richtigen Server zugeordnet.

### Economy

Das Paper-Modul enthält ein Economy-System für Coins und Gems inklusive Transaktionen und Audit-Logging.

Enthalten sind:

- Kontostandabfragen (`/coins`, `/balance`)
- Coin-Transfers zwischen Spielern (`/pay`)
- Admin-Verwaltung (`/economy give|set|take`)
- Zahlendarstellung basierend auf der aufgelösten Translation-Locale
- Silent logging

### Home-System

Das Paper-Modul enthält ein Home-System mit Command- und GUI-Flow.

Enthalten sind:

- Home-Liste und Home-Editor als GUI (`/home`)
- Home anlegen, löschen, umbenennen und Position aktualisieren
- Umbenennen über Amboss-GUI
- Icon-Auswahl pro Home mit persistenter Speicherung
- Strukturierte Logs für Home-Operationen

### Ignore-System

Das Paper-Modul enthält ein Ignore-System auf Basis von Spieler-zu-Spieler-Beziehungen.

Enthalten sind:

- Ignorieren einzelner Spieler über `/ignore <player>`
- Aufheben über `/unignore <player>`
- Tab-Completion bei `/unignore` aus der gespeicherten Ignore-Liste (auch wenn der Spieler aktuell offline ist)
- Persistente Speicherung der Ignore-Beziehungen in `player_ignore`
- Blockieren eingehender Interaktionen für ignorierende Spieler bei privaten Nachrichten (`/message`, `/reply`)
- Blockieren eingehender Interaktionen für ignorierende Spieler bei Coin-Transfers (`/pay`)
- Blockieren eingehender Interaktionen für ignorierende Spieler bei Teleport-Requests (`/tpa`, `/tpahere`)

### Displays

Das Display-Feature rendert Rang- und Wirtschaftsinformationen in Tablist und Sidebar-Scoreboard. Das Layout ist über die `config.json`-Datei konfigurierbar.
In dieser sind Leerzeilen als leere Texte dargestellt. Zeilen mit Inhalt haben ihren Translation-Key als Wert, welcher wiederum in dem Message-Bundle registriert
und konfiguriert werden kann.

Voraussetzung: LuckPerms

- LuckPerms muss auf dem Backend-Server installiert, aktiv und mittels einer Datenbank mit LuckPerms auf dem Proxy synchronisiert sein.
- Gruppen müssen folgende zwei Attribute besitzen:
- `prefix`: Wird für die Darstellung in der Tablist bzw. im Overhead benötigt.
- Metadaten-Attribut `displayname`: Wird für die Anzeige in der Sidebar verwendet. Kann abweichend zum Prefix sein.

### Logging im Hintergrund

Das Projekt bringt strukturiertes Logging mit, damit wichtige Admin-Aktionen und Fehler nachvollziehbar bleiben. Die Details dazu stehen in `LOGGING.md`.

---
<br>

## Projektstruktur

| Modul              | Rolle                                                                       |
|--------------------|-----------------------------------------------------------------------------|
| `core-velocity`    | Zentrale Netzwerkfunktionen wie Commands, MOTD, Join-Kontrolle und Wartung. |
| `core-paper`       | Backend-seitige Erweiterungen auf Paper inklusive Economy-Commands.         |
| `core-persistence` | Datenmodelle, Stores und SQL-Migrationen.                                   |
| `core-common`      | Gemeinsame Infrastruktur (Logging, Translation, Bootstrap).                 |

---
<br>

## Commands

| Command                                                           | Zweck                                                                                   | Typischer Einsatz                             |
|-------------------------------------------------------------------|-----------------------------------------------------------------------------------------|-----------------------------------------------|
| `/maintenance <true\|false>`                                      | Aktiviert oder deaktiviert den Wartungsmodus.                                           | Updates, Tests, Notfallarbeiten               |
| `/core reload --config`                                           | Lädt nur die Konfiguration neu.                                                         | Nach Änderungen an `config.json`              |
| `/core reload --messages`                                         | Lädt nur die Nachrichten neu.                                                           | Nach Änderungen an Texten oder Übersetzungen  |
| `/core reload --all`                                              | Lädt Config und Nachrichten gemeinsam neu.                                              | Nach größeren inhaltlichen Anpassungen        |
| `/help [query] [--page <page>]` oder `/? [query] [--page <page>]` | Zeigt das vereinheitlichte Hilfemenü (Proxy + Backend) mit optionaler Suche und Paging. | Zum Nachschlagen verfügbarer Befehle          |
| `/global-find <player>` oder `/gfind <player>`                    | Zeigt, auf welchem Server ein Spieler ist.                                              | Support, Moderation, Teamarbeit               |
| `/global-teleport <player>` oder `/gtp <player>`                  | Verbindet dich auf den Server des Zielspielers.                                         | Direktes Wechseln zu einem Spieler            |
| `/online <server>`                                                | Prüft, ob ein registrierter Backend-Server erreichbar ist.                              | Betriebscheck, Fehlersuche                    |
| `/proxy-stop`                                                     | Stoppt den Proxy kontrolliert.                                                          | Geplante Eingriffe oder Wartung               |
| `/coins`                                                          | Zeigt den aktuellen Coin-Kontostand.                                                    | Schnelle Kontostandsprüfung                   |
| `/balance` oder `/bal`                                            | Zeigt Coins und Gems an.                                                                | Gesamtübersicht für Spieler                   |
| `/pay <player> <amount>`                                          | Überweist Coins an einen anderen Spieler.                                               | Spieler-zu-Spieler-Transfer                   |
| `/economy give <player> <currency> <amount>`                      | Fügt Coins oder Gems hinzu.                                                             | Admin-Korrekturen, Rewards                    |
| `/economy set <player> <currency> <amount>`                       | Setzt Coins oder Gems auf einen festen Wert.                                            | Moderation, Datenkorrekturen                  |
| `/economy take <player> <currency> <amount>`                      | Zieht Coins oder Gems ab.                                                               | Moderation, Rückabwicklung                    |
| `/home`                                                           | Öffnet die Home-GUI mit Home-Liste und Schnellzugriff.                                  | Navigation und Teleport über GUI              |
| `/home create <name>`                                             | Erstellt ein neues Home an der aktuellen Position.                                      | Neues Teleport-Ziel speichern                 |
| `/home delete <name>`                                             | Löscht ein bestehendes Home.                                                            | Aufräumen/Entfernen alter Homes               |
| `/home rename <old-name> <new-name>`                              | Benennt ein Home um.                                                                    | Umstrukturierung von Home-Namen               |
| `/home update <name>`                                             | Aktualisiert die gespeicherte Position eines Homes auf die aktuelle Position.           | Bestehendes Home verschieben                  |
| `/ignore <player>`                                                | Ignoriert einen bestimmten Spieler.                                                     | Unerwünschte Interaktionen gezielt blockieren |
| `/unignore <player>`                                              | Hebt das Ignorieren für einen bestimmten Spieler wieder auf.                            | Interaktionen gezielt wieder zulassen         |
| `/hat`                                                            | Setzt das Item in der Hand als Helm.                                                    | Cosmetic/QoL                                  |
| `/enderchest` oder `/ec`                                          | Öffnet die eigene Enderchest.                                                           | Schneller Zugriff                             |
| `/enderchest <player>` oder `/ec <player>`                        | Öffnet die Enderchest eines anderen Spielers.                                           | Moderation/Support                            |
| `/workbench` oder `/wb`                                           | Öffnet eine mobile Werkbank.                                                            | Crafting ohne Block                           |
| `/anvil`                                                          | Öffnet einen mobilen Amboss.                                                            | Umbenennen/Reparieren                         |
| `/repair`                                                         | Repariert das Item in der Haupthand.                                                    | Admin-/Team-QoL                               |
| `/skull <player>`                                                 | Gibt den Kopf eines (auch offline) Spielers.                                            | Build/Decoration                              |
| `/trash`                                                          | Öffnet den Mülleimer (Items werden zeitgesteuert gelöscht).                             | Inventar aufräumen                            |
| `/sit`                                                            | Setzt den Spieler auf den Boden.                                                        | Roleplay/QoL                                  |
| `/sign`                                                           | Signiert das Item in der Haupthand einmalig.                                            | Item-Historie                                 |
| `/message <player> <text>`                                        | Sendet eine private Nachricht.                                                          | Direkte Kommunikation                         |
| `/reply <text>`                                                   | Antwortet auf die letzte private Nachricht.                                             | Direkte Kommunikation                         |
| `/tpa <player>`                                                   | Sendet Teleport-Anfrage zu einem Spieler.                                               | Spieler-zu-Spieler-Teleport                   |
| `/tpahere <player>`                                               | Sendet Teleport-Anfrage, damit der Spieler zu dir kommt.                                | Spieler-zu-Spieler-Teleport                   |
| `/tpaccept`                                                       | Nimmt die letzte Teleport-Anfrage an.                                                   | Abschluss Teleport-Request                    |
| `/tpdeny`                                                         | Lehnt die letzte Teleport-Anfrage ab.                                                   | Ablehnen Teleport-Request                     |
| `/tpo <player>`                                                   | Teleportiert dich direkt zu einem Spieler.                                              | Team-/Admin-Moderation                        |
| `/tpohere <player>`                                               | Teleportiert einen Spieler direkt zu dir.                                               | Team-/Admin-Moderation                        |
| `/invsee <player>`                                                | Öffnet das Inventar eines Spielers (read-only/modify).                                  | Moderation/Support                            |
| `/vanish` oder `/v`                                               | Aktiviert/Deaktiviert Vanish für dich.                                                  | Moderation                                    |
| `/vanish <player>` oder `/v <player>`                             | Schaltet Vanish für einen anderen Spieler.                                              | Team-Management                               |

---
<br>

## Permissions

| Permission                               | Bedeutung                                                            |
|------------------------------------------|----------------------------------------------------------------------|
| `core.command.maintenance`               | Erlaubt das Ein- und Ausschalten des Wartungsmodus.                  |
| `core.bypass.maintenance`                | Erlaubt den Beitritt trotz aktivem Wartungsmodus.                    |
| `core.command.core`                      | Erlaubt Reload-Befehle für Config und Nachrichten.                   |
| `core.command.proxy-stop`                | Erlaubt das kontrollierte Stoppen des Proxy.                         |
| `core.command.global-find`               | Erlaubt das Abfragen des aktuellen Servers eines Spielers.           |
| `core.command.global-teleport`           | Erlaubt das Wechseln auf den Server eines anderen Spielers.          |
| `core.command.online`                    | Erlaubt die Prüfung registrierter Backend-Server auf Erreichbarkeit. |
| `core.command.coins`                     | Erlaubt die Abfrage des eigenen Coin-Kontostands.                    |
| `core.command.balance`                   | Erlaubt die Abfrage von Coins und Gems.                              |
| `core.command.pay`                       | Erlaubt das Senden von Coins an andere Spieler.                      |
| `core.command.economy`                   | Erlaubt administrative Economy-Befehle (`give`, `set`, `take`).      |
| `core.command.home`                      | Erlaubt die Nutzung des Home-Systems (`/home` + Subcommands).        |
| `core.command.ignore`                    | Erlaubt `/ignore <player>` zum Ignorieren bestimmter Spieler.        |
| `core.command.unignore`                  | Erlaubt `/unignore <player>` zum Aufheben pro Spieler.               |
| `core.command.hat`                       | Erlaubt `/hat`.                                                      |
| `core.command.enderchest`                | Erlaubt das Öffnen der eigenen Enderchest (`/ec`).                   |
| `core.command.enderchest.other`          | Erlaubt das Öffnen fremder Enderchests.                              |
| `core.command.enderchest.other.interact` | Erlaubt das Bearbeiten fremder Enderchests im geöffneten View.       |
| `core.command.workbench`                 | Erlaubt `/workbench` (`/wb`).                                        |
| `core.command.anvil`                     | Erlaubt `/anvil`.                                                    |
| `core.command.repair`                    | Erlaubt `/repair`.                                                   |
| `core.command.skull`                     | Erlaubt `/skull <player>`.                                           |
| `core.command.trash`                     | Erlaubt `/trash`.                                                    |
| `core.command.sit`                       | Erlaubt `/sit`.                                                      |
| `core.command.sign`                      | Erlaubt `/sign`.                                                     |
| `core.command.message`                   | Erlaubt `/message`.                                                  |
| `core.command.reply`                     | Erlaubt `/reply`.                                                    |
| `core.command.tpa`                       | Erlaubt `/tpa`.                                                      |
| `core.command.tpahere`                   | Erlaubt `/tpahere`.                                                  |
| `core.command.tpaccept`                  | Erlaubt `/tpaccept`.                                                 |
| `core.command.tpdeny`                    | Erlaubt `/tpdeny`.                                                   |
| `core.command.tpo`                       | Erlaubt `/tpo`.                                                      |
| `core.command.tpohere`                   | Erlaubt `/tpohere`.                                                  |
| `core.command.invsee`                    | Erlaubt `/invsee <player>`.                                          |
| `core.command.invsee.modify`             | Erlaubt Bearbeitung im InvSee-View.                                  |
| `core.command.vanish`                    | Erlaubt `/vanish` für sich selbst.                                   |
| `core.command.vanish.other`              | Erlaubt `/vanish <player>`.                                          |
| `core.bypass.vanish`                     | Spieler mit dieser Permission sehen vanished Spieler weiterhin.      |

---
<br>

## Konfiguration

Die wichtigste Runtime-Datei im Velocity-Modul ist `config.json`. Dort werden unter anderem folgende Bereiche gesteuert:

- Hot-Reloading für Nachrichten
- normale MOTD
- Wartungsstatus
- Wartungs-MOTD
- Wartungsscreen inklusive Hinweistext und Link
- Redis-Sync für den Austausch von Backend-Help-Daten

Im Paper-Modul werden Nachrichten aus `plugins/Core/lang/messages_<locale>.conf` geladen.
Die Zahlendarstellung in Commands ist an die aufgelöste Translation-Locale gekoppelt.
Zusätzlich wird in `core-paper/config.json` für den Help-Sync konfiguriert:

> [!TIP]
> Die Texte unterstützen [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/) und lassen sich dadurch flexibel gestalten.
> Einen Editor für Liveansichten gibt es als [Adventure Text-Editor](https://adventure.kyori.net/).

---
<br>

## Persistenz und Migrationen

Die SQL-Struktur liegt im Modul `core-persistence` unter:

- `database/mariadb/<major>/setup.sql`
- `database/mariadb/<major>/migrate.sql`
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

Das Projekt deckt die zentralen Netzwerkfunktionen für den Proxy-Betrieb bereits ab und erweitert diese auf Paper um Economy, Home-System und Utility-Commands.

---
<br>

## Jira-Zeiterfassung über Commits

Für die automatische Zeiterfassung gibt es zwei Bausteine:

1. Lokale Git-Hooks unter `.githooks/`
2. GitHub Workflow `.github/workflows/sync-jira-worklogs.yml`

### Hooks aktivieren

- Windows (PowerShell): `./scripts/setup-git-hooks.ps1`

Danach ergänzt `prepare-commit-msg` Commit-Messages automatisch um:

- `Refers to: CR-<id>` (id aus Branchname zb. feature/CR-123)
- `Time-Spent: <time>` (z.B. 1h 20m)
- Die Zeit basiert auf dem Bearbeitungsfenster der gestagten Dateien (früheste bis späteste Änderung) und reduziert dadurch Idle-Zeit zwischen letzter Aktivität und Commit.

`post-commit` setzt den Startzeitpunkt für den nächsten Commit.

### Jira-API Konfiguration (GitHub Secrets)

- `JIRA_BASE_URL` (z. B. `https://crystalixs.atlassian.net/`)
- `JIRA_USER_EMAIL`
- `JIRA_API_TOKEN`

Der Workflow verarbeitet Push-Commits, liest `Refers to` + `Time-Spent` aus der Commit-Message und schreibt die Zeit als Jira-Worklog auf das jeweilige Ticket.

# Core System

Velocity-Core-Plugin für Netzwerk-Grundfunktionen

## Overview

Dieses Plugin stellt zentrale Proxy-Funktionen für Velocity bereit:

- Verwaltung eines Wartungsmodus
- Dynamische MOTD-Ausgabe (normal / Wartung)
- Login-Blockierung im Wartungsmodus (inkl. Bypass-Recht)
- Befehl zur Laufzeitsteuerung des Wartungsmodus

---

## Aktuell implementierte Funktionen

### 1) Wartungsmodus

Der Wartungsmodus legt fest, ob Spieler sich mit dem Proxy verbinden können (analog zu einer Whitelist). Mit einer entsprechenden Berechtigung kann dieser Filter
umgangen werden und der Spieler kann trotz aktivem Wartungsmodus sich verbinden. <br>

> [!CAUTION]
> Der Wartungsmodus sollte **ausschließlich** über den entsprechenden Befehl getoggelt werden.

#### Permissions

- `core.command.maintenance`: Erlaubt den Command `/maintenance` zu nutzen.
- `core.bypass.maintenance`: Umgeht Login-Sperre bei aktivem Wartungsmodus.

#### Befehl

- /maintenance `<state>`: Toggelt den aktuellen Zustand des Wartungsmodus. Benötigt die Permission `core.command.maintenance`.
    - `state`: boolean (true/false)

#### Bypass

Um trotz aktivem Wartungsmodus zum Proxy verbinden zu können, benötigt man die Permission `core.bypass.maintenance`. Andernfalls verliert er die Verbindung. Ein
Screen wird anschließend gezeigt. Dieser folgt folgendem Muster:

- `HEADER`
- `LEERZEILE`
- `BODY`
- `LEERZEILE`
- `FOOTER`
- `URL` (Link zu weiteren Informationen)

> [!TIP]
> `HEADER`, `BODY`, `FOOTER` und `URL` können in der `config.json` angepasst werden. Ihr Text unterstützt [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/). Eine Vorschau kannst du dir [hier](https://webui.advntr.dev/) anzeigen
> lassen.

---

## 2) MOTD-Anpassung abhängig vom Wartungsstatus

Die MOTD wird abhängig vom Wartungsstatus angepasst. Ist der Wartungsmodus aktiviert, so wird zusätzlich das Protokoll invalidiert und als benötigte Version ein
einstellbarer Text angezeigt. <br>

> [!TIP]
> MOTDs können in der `config.json` angepasst werden. Ihr Text unterstützt [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/). Eine Vorschau kannst du dir [hier](https://webui.advntr.dev/) anzeigen lassen.

---

## 3) Reloading

Die Config-Datei kann mittels eines Befehls neu geladen werden.

#### Befehl

- /core reload: Benötigt die Permission `core.command.core`

---

## 4) Proxy Stopp

Der Proxy kann mit einem Befehl gestoppt werden. Ohne externes Skript (bspw. Crone job) startet sich dieser nicht von alleine neu.

#### Befehl

- /proxy-stop: Benötigt die Permission `core.command.proxy-stop`

---

## 5) Onlinestatus von Backend Servern

Der Onlinestatus von Backend Servern kann mittels eines Befehls abgefragt werden. Es werden nur im Proxy registrierte Server unterstützt.

#### Befehl

- /online `<server>`: Benötigt die Permission `core.command.online`
    - `server`: Der Name des zu prüfenden Servers

---

## Globale Spieler Befehle

Der aktuelle Server eines Spielers kann mittels eines Befehls gefunden werden und ruch einen Weiteren betreten werden.

#### Befehle

- /global-find `<player>`: Findet den aktuellen Server eines Spielers. Benötigt die Permission `core.command.global-find`
    - `player`: Der Spielername
    - Alias: gfind
- /global-teleport `<player>`: Teleportiert dich auf den Server des angegebenen Spielers. Benötigt die Permission `core.command.global-teleport`
    - `player`: Der Spielername
    - Alias: gtp

---

## Derzeitiger Funktionsumfang (kurz)

✅ Wartungsmodus umschaltbar  
✅ Wartungsabhängige MOTD  
✅ Wartungs-Login-Block mit Bypass  
✅ Custom Help-System mit Pagination  
✅ Stoppen des Proxyservers  
✅ Onlinestatus von Servern  
✅ Globale Spielerbefehle
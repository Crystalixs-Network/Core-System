# Datenbankschema

## Schema-Grafik

![DB-Schema](assets/db-diagram.jpg)

---
<br>

## Überblick

| Bereich   | Beschreibung                                                |
|-----------|-------------------------------------------------------------|
| `player`  | Enthält die Basisdaten eines Spielers.                      |
| `homes`   | Speichert benannte Homes eines Spielers inklusive Position. |
| Beziehung | Ein Spieler kann mehrere Homes besitzen.                    |

Das Schema besteht aus einer klaren `1:n`-Beziehung.

---

## Tabellen

### `player`

Die Tabelle `player` bildet die zentralen Stammdaten eines Spielers ab.

| Spalte     | Typ      | Zweck                          |
|------------|----------|--------------------------------|
| `uuid`     | `uuid`   | Primaerschluessel des Spielers |
| `playtime` | `bigint` | Gesammelte Spielzeit           |
| `coins`    | `bigint` | Aktueller Coin-Stand           |
| `gems`     | `bigint` | Aktueller Gem-Stand            |

### `homes`

Die Tabelle `homes` speichert feste Teleport-Ziele eines Spielers.

| Spalte       | Typ        | Zweck                                      |
|--------------|------------|--------------------------------------------|
| `id`         | `bigint`   | Primaerschlüssel des Home-Eintrags         |
| `owner`      | `uuid`     | Referenz auf den Besitzer in `player.uuid` |
| `name`       | `varchar`  | Frei waälbarer Name des Homes              |
| `world`      | `varchar`  | Welt oder Dimension des Homes              |
| `x`          | `double`   | X-Koordinate                               |
| `y`          | `double`   | Y-Koordinate                               |
| `z`          | `double`   | Z-Koordinate                               |
| `yaw`        | `float`    | Horizontale Blickrichtung                  |
| `pitch`      | `float`    | Vertikale Blickrichtung                    |
| `created_at` | `datetime` | Erstellungszeitpunkt des Homes             |

---

## Beziehungen

| Von           | Nach          | Typ            | Bedeutung                                |
|---------------|---------------|----------------|------------------------------------------|
| `homes.owner` | `player.uuid` | Fremdschlüssel | Ordnet jedes Home genau einem Spieler zu |

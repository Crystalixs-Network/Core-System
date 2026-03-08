# Logging

## Regeln

1. Jeder Logeintrag enthält ein `event`-Feld.
2. Die Message bleibt kurz, stabil und menschenlesbar.
3. Variable Daten gehören in `LogMetadata`, nicht in String-Konkatenation.
4. Feldnamen kommen aus `LogMetadata.Key`.
5. `warn` ist für recoverable Probleme, `error` für echte Fehler.
6. Exceptions werden immer als `Throwable` übergeben.
7. Child-Logger grenzen Teilbereiche ab, z. B. `core/config`, `core/translations/watch` oder `core/commands/maintenance`.
8. Zustandsändernde Admin-Aktionen werden mit Audit-Feldern geloggt.

## Pflichtfälle

- Plugin-Startup und Shutdown
- Config erstellen, laden, speichern und Fehlerfälle dabei
- Translation-Reloads und Watcher-Fehler
- Zustandsändernde Admin-Commands
- Recoverable Fehler als `warn`, fatale Fehler als `error`

## Event-Namen

- Format: `<bereich>.<aktion>`
- Beispiele:
    - `plugin.enabled`
    - `plugin.disabled`
    - `config.created`
    - `config.load_failed`
    - `config.save_failed`
    - `translations.reloaded`
    - `translations.reload_failed`
    - `translations.watch.started`
    - `command.reload.config`
    - `command.reload.config_failed`
    - `command.reload.messages`
    - `maintenance.enabled`
    - `maintenance.disabled`
    - `maintenance.toggle_failed`

## Audit-Felder

- `actor` für auslösende Nutzer oder Konsole
- `command` für den ausgeführten Admin-Befehl
- `state` für Zielzustände bei Toggles
- Weitere technische Details wie `file`, `path` oder `description` bleiben separate Metadatenfelder

## Stil

- Messages ohne Punkt am Ende
- Messages kurz und sachlich
- Event-Namen sind die primäre maschinenlesbare Aussage
- Die Message ist die kurze Menschenleseform dazu
- `sync.entry` und ähnliche Detail-Logs verwenden konstante Messages und strukturierte Metadaten

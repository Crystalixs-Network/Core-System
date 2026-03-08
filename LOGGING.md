# Logging

## Regeln

1. Jeder Logeintrag enthält ein `event`-Feld.
2. Die Message bleibt kurz, stabil und menschenlesbar.
3. Variable Daten gehören in `LogMetadata`, nicht in String-Konkatenation.
4. Feldnamen kommen aus `LogMetadata.Key`.
5. `warn` ist für recoverable Probleme, `error` für echte Fehler.
6. Exceptions werden immer als `Throwable` übergeben.
7. Erfolgs-Events werden nur geloggt, wenn der Vorgang tatsächlich erfolgreich abgeschlossen wurde.
8. Child-Logger grenzen Teilbereiche ab, z. B. `core/config`, `core/translations/watch` oder `core/commands/maintenance`.
9. Zustandsändernde Admin-Aktionen werden mit Audit-Feldern geloggt.

## Pflichtfälle

- Plugin-Startup und Shutdown
- Config erstellen, laden, speichern und Fehlerfälle dabei
- Translation-Reloads und Watcher-Fehler
- Zustandsändernde Admin-Commands
- Recoverable Fehler als `warn`, fatale Fehler als `error`

## Event-Namen

- Format: `<bereich>.<aktion>`
- Bei unterschiedlichen Fehlerursachen darf ein Event feiner gesplittet werden, z. B. nach externem Konflikt vs. IO-Fehler
- Beispiele:
    - `plugin.enabled`
    - `config.load_failed`
    - `translations.watch.started`
    - `command.reload.config`
    - `maintenance.enabled`

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
# Logging

## Regeln

1. Jeder Logeintrag enthält ein `event`-Feld.
2. Die Message bleibt kurz, stabil und menschenlesbar.
3. Variable Daten gehören in `LogMetadata`, nicht in String-Konkatenation.
4. Feldnamen kommen aus `LogMetadata.Key`.
5. `warn` ist für recoverable Probleme, `error` für echte Fehler.
6. Exceptions werden immer als `Throwable` übergeben.
7. Child-Logger grenzen Teilbereiche ab, z. B. `core/config` oder `core/translations`.

## Event-Namen

- Format: `<bereich>.<aktion>`
- Beispiele:
    - `plugin.enabled`
    - `plugin.disabled`
    - `config.created`
    - `config.save_failed`
    - `translations.reloaded`
    - `translations.reload_failed`
    - `translations.watch.started`

## Stil

- Messages ohne Punkt am Ende
- Messages kurz und sachlich
- Event-Namen sind die primäre maschinenlesbare Aussage
- Die Message ist die kurze Menschenleseform dazu

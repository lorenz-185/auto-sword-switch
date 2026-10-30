# Auto Sword Switch (Fabric, Minecraft 26.3)

Client-Mod: Wechselt in der Hotbar automatisch auf das naechste Schwert, wenn das
aktuelle fast kaputt ist (nur noch 2 Haltbarkeitspunkte). Wurde ein Schwert doch
zerbrochen, wird ebenfalls auf ein anderes Schwert gewechselt. Ist kein Ersatzschwert
mehr in der Hotbar, erscheint alle 10 Sekunden eine Warnung in der Actionbar.

## Voraussetzungen

- Java 25 (JDK)
- Minecraft 26.3, Fabric Loader >= 0.19.5, Fabric API 0.160.5+26.3

## Bauen

```
./gradlew build
```

Die fertige Mod liegt danach in `build/libs/auto-sword-switch-1.0.0.jar`
(nicht die `-sources.jar`). In den `mods`-Ordner kopieren, zusammen mit der Fabric API.

Entwicklungsumgebung starten: `./gradlew runClient`

## Einstellungen

Konstanten oben in `AutoSwordSwitchClient.java`:

- `SWITCH_AT_REMAINING`: Ab wie vielen Rest-Haltbarkeitspunkten gewechselt wird.
- `WARN_INTERVAL_TICKS`: Abstand der Warnungen (20 Ticks = 1 Sekunde).

## Hinweis

Die Mod arbeitet rein clientseitig. Auf Multiplayer-Servern ist AFK-Automatisierung
oft verboten, bitte die Serverregeln beachten.

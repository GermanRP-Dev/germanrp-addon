# GermanRP Addon

Willkommen beim offiziellen **LabyMod 4 Addon für den [GermanRP.eu](https://germanrp.eu) Minecraft Server**!

Dieses Addon befindet sich aktuell noch in der **Entwicklung** (*Work in Progress*) und soll die Funktionen des früheren
[*GermanRP Utils*](https://germanrp.eu/forum/index.php?thread/17173-germanrp-utils-labymod-addon/) übernehmen und
erweitern.

## 🔧 Ziel des Projekts

Das Ziel des Projekts ist es, ein modernes LabyMod 4 Addon bereitzustellen, welches:

* die Spielerfahrung auf dem **GermanRP Server** verbessert,
* nützliche Funktionen wie Widgets, QoL-Features und Ingame-Informationen bietet,
* und langfristig wartbar sowie offen für Community-Beiträge ist.

## 📦 Aktueller Stand

* 🔄 **Work in Progress**: Die Implementierung der ursprünglichen Features aus *GermanRP Utils* hat Priorität.
* ✅ Ziel: Kompatibilität mit **LabyMod 4 und Minecraft 26.3**

## 👨‍💻 Mitwirken

Zum Bauen wird ein **JDK 25** benötigt (auch als Gradle-JVM in der IDE einstellen):

```powershell
.\gradlew.bat build
```

Das fertige Addon liegt unter `build/libs/germanrpaddon-release.jar`.
Falls ein sauberer Neubau nötig ist, `clean` separat vor `build` ausführen,
da LabyGradle bereits bei der Projektkonfiguration Build-Metadaten erzeugt.
Vor einer Veröffentlichung mit LabyMod für Minecraft 26.3 und installiertem
`labyswaypoints` auf grv9 testen: Durstanzeige (einschließlich Luftblasen unter
Wasser), ATM- und Panik-Sounds, Nametags, Fahrzeug-Hotkeys und Server-Pakete.

Dieses Addon wird **von der Community entwickelt** und vom **GermanRP Serverteam** betreut.

**Pull Requests und Issues sind willkommen!**

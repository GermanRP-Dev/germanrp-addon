plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "eu.germanrp"
version = providers.environmentVariable("VERSION").getOrElse("1.0.0")

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

labyMod {
    defaultPackageName = "eu.germanrp.addon"

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {

                }
            }
        }
    }

    addonInfo {
        namespace = "germanrpaddon"
        displayName = "GermanRP Addon"
        author = "GermanRP Developers"
        description = "Verbessert und erleichtert das Spielerlebnis auf GermanRP.eu"
        minecraftVersion = "1.21.3<*"
        version = rootProject.version.toString()
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version
}

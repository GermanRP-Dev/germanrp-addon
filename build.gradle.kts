plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "eu.germanrp"
version = providers.environmentVariable("VERSION").getOrElse("0.0.0")

labyMod {
    defaultPackageName = "eu.germanrp.addon"

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    // When the property is set to true, you can log in with a Minecraft account
                    // devLogin = true
                }
            }
        }
    }

    addonInfo {
        namespace = "germanrpaddon"
        displayName = "GermanRP Addon"
        author = "GermanRP Developers"
        description = "Verbessert und erleichtert das Spielerlebnis auf GermanRP.eu"
        minecraftVersion = "1.21.8"
        version = rootProject.version.toString()
        addon("labyswaypoints")
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    repositories {
        maven("https://maven.pkg.github.com/GermanRP-Dev/germanrp-addon_labymod4-server-api-integration") {
            name = "GitHubPackages"
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    group = rootProject.group
    version = rootProject.version
}

plugins {
    id("java")
}

group = "me.thiagorigonatti"
version = "0.0.1-RESCUED"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/public/")
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT:shaded@jar")
    implementation(
        fileTree("./lib") {
            include("*.jar")
        }
    )
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("net.md-5:bungeecord-api:1.21-R0.5-SNAPSHOT@jar")
    implementation("net.md-5:bungeecord-protocol:1.21-R0.5-SNAPSHOT@jar")
    implementation("net.md-5:bungeecord-event:1.21-R0.5-SNAPSHOT@jar")
}

tasks {
    named<Jar>("jar") {
        archiveBaseName.set("BossShopPro")
        archiveVersion.set("$version")
    }
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}

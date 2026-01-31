import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("java")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}

// paths
val userHome = System.getProperty("user.home")
var hytaleHome = userHome
val os = org.gradle.internal.os.OperatingSystem.current()
if (os.isWindows) {
    hytaleHome = "$userHome/AppData/Roaming/Hytale"
} else if (os.isMacOsX) {
    hytaleHome = "$userHome/Library/Application Support/Hytale"
} else if (os.isLinux) {
    hytaleHome = "$userHome/.var/app/com.hypixel.HytaleLauncher/data/Hytale"
    if (!file(hytaleHome).exists()) {
        hytaleHome = "$userHome/.local/share/Hytale"
    }
}
val gameHome = "$hytaleHome/install/release/package/game/latest"
val assetPack = "$gameHome/Assets.zip"
var serverDir = "${layout.buildDirectory.get().asFile.path}/server"

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
    withJavadocJar()
}

group = "com.melodyjam"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/HytaleServer.jar"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/resources")
}

idea.project.settings.runConfigurations {
    create("HytaleServer", Application::class) {
        mainClass = "com.hypixel.hytale.Main"
        workingDirectory=serverDir
        moduleName=project.idea.module.name + ".main"
        programParameters="--allow-op --disable-sentry --assets=\"$assetPack\""
    }
}

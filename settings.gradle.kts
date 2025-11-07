pluginManagement {
    repositories {
        maven("https://maven.firstdark.dev/releases")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

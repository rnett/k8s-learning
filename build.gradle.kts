val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31" apply false
    id("com.bmuschko.docker-java-application") version "7.1.0" apply false
}

allprojects {
    group = "com.rnett"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")


    api("io.ktor:ktor-server-core:$ktor_version")
    api("io.ktor:ktor-serialization:$ktor_version")
}
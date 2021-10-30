val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31" apply false
    id("com.bmuschko.docker-java-application") version "7.1.0" apply false
    id("org.unbroken-dome.helm") version "1.6.1"
}

allprojects {
    group = "com.rnett"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

helm{
//    namespace.set(project.name)
    xdgCacheHome.set(file("${project.buildDir}/helm/cache"))
    downloadClient {
        enabled.set(true)
        version.set("3.7.0")
    }
}
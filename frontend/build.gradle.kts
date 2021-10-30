val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.bmuschko.docker-java-application")
}

dependencies {
    implementation(project(":common"))

    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.236-kotlin-1.5.30")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

application {
    mainClass.set("com.rnett.ApplicationKt")
}

docker {
    javaApplication {
        baseImage.set("openjdk:17-slim")
        ports.set(listOf(8081))
        images.set(setOf("k8s-learning/frontend"))
        mainClassName.set("com.rnett.ApplicationKt")
    }
}

tasks.dockerPushImage.configure { enabled = false }

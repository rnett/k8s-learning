val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.bmuschko.docker-java-application")
}

dependencies {
    implementation(project(":common"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")

    implementation("org.postgresql:postgresql:42.2.2")
}

application {
    mainClass.set("com.rnett.ApplicationKt")
}

docker {
    javaApplication {
        baseImage.set("openjdk:17-slim")
        ports.set(listOf(8080))
        images.set(setOf("k8s-learning/backend"))
        mainClassName.set("com.rnett.ApplicationKt")
    }
}

tasks.dockerPushImage.configure { enabled = false }
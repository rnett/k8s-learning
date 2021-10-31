import org.unbrokendome.gradle.plugins.helm.rules.updateDependenciesTaskName

buildscript {
    repositories {
        mavenCentral() // For the ProGuard Gradle Plugin and anything else.
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.0-beta2") // The ProGuard Gradle plugin.
    }
}

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31" apply false
    id("com.bmuschko.docker-java-application") version "7.1.0" apply false
    id("com.bmuschko.docker-remote-api") version "7.1.0" apply false
    id("org.unbroken-dome.helm") version "1.6.1"
    id("org.unbroken-dome.helm-releases") version "1.6.1"
    id("org.beryx.runtime") version "1.12.7" apply false
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
}

allprojects {
    group = "com.rnett"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

val dockerBuildImage by tasks.registering {
    group = "docker"
    description = "Builds the docker image"
    dependsOn(":backend:dockerBuildImage", ":frontend:dockerBuildImage")
}

helm {
    namespace.set(project.name)

    repositories {
        bitnami()
    }

    downloadClient {
        enabled.set(true)
        version.set("3.7.0")
    }

    val main = charts.create(project.name) {
        sourceDir.set(file("${project.projectDir}/src/main/helm"))
    }
    tasks.named(main.updateDependenciesTaskName) {
        dependsOn(dockerBuildImage)
    }
    releases {
        create(project.name) {
            from(main)
            namespace.set(this@helm.namespace)
        }
    }
    releaseTargets {
        create("local") {
            values.put("frontend.replicas", "1")
            values.put("backend.replicas", "1")
        }
        activeReleaseTarget.set("local")
    }
}

tasks.helmPackage {
    dependsOn(dockerBuildImage)
}

tasks.register("helmReinstall") {
    group = "helm"
    description = "Uninstall then reinstall the helm chart."
    dependsOn("helmUninstall")
    finalizedBy("helmInstall")
}

import org.unbrokendome.gradle.plugins.helm.rules.updateDependenciesTaskName

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31" apply false
    id("com.bmuschko.docker-java-application") version "7.1.0" apply false
    id("org.unbroken-dome.helm") version "1.6.1"
    id("org.unbroken-dome.helm-releases") version "1.6.1"
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

    val main = charts.create("k8s-learning") {
        sourceDir.set(file("${project.projectDir}/src/main/helm"))
    }
    tasks.named(main.updateDependenciesTaskName) {
        dependsOn(dockerBuildImage)
    }
    releases {
        create("k8s-learning") {
            from(main)
            namespace.set(this@helm.namespace)
        }
    }
    releaseTargets {
        create("local") {
        }
        activeReleaseTarget.set("local")
    }
}

tasks.helmPackage {
    dependsOn(dockerBuildImage)
}

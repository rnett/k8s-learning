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

helm {
    namespace.set(project.name)

    repositories {
        bitnami()
    }

    downloadClient {
        enabled.set(true)
        version.set("3.7.0")
    }

    val main by charts.creating {
        sourceDir.set(file("${project.projectDir}/src/main/helm"))
    }
    releases {
        create("main") {
            from(main)
            namespace.set(this@helm.namespace)
        }
    }
    releaseTargets {
        create("local") {
//            kubeContext.set("docker-for-desktop")
        }
        activeReleaseTarget.set("local")
    }
}
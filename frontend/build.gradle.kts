import proguard.gradle.ProGuardTask

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.bmuschko.docker-java-application")
    id("org.beryx.runtime")
    id("com.github.johnrengelman.shadow")
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
    implementation("org.slf4j:slf4j-simple:1.7.30")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

application {
    mainClass.set("com.rnett.ApplicationKt")
}

docker {
    javaApplication {
        baseImage.set("alpine:3.13.6")
        ports.set(listOf(8081))
        images.set(setOf("k8s-learning/frontend"))
        mainClassName.set("com.rnett.ApplicationKt")
    }
}

tasks.dockerCreateDockerfile.configure {
    instructions.set(emptyList())
    instructionsFromTemplate(file("$projectDir/src/docker/Dockerfile"))
}

tasks.dockerPushImage.configure { enabled = false }

val useProguard = true

tasks.dockerSyncBuildContext.configure {
    dependsOn("jre", if (useProguard) "minimizedJar" else "shadowJar")
    from("$buildDir/jre")
    from("$buildDir/libs") {
        include {
            it.name.endsWith("$version-all${if (useProguard) "-min" else ""}.jar")
        }
        rename {
            "app.jar"
        }
    }
}

tasks {
    register<ProGuardTask>("minimizedJar") {
        dependsOn(shadowJar)

        injars(shadowJar.get().archiveFile.get().asFile)
        outjars(shadowJar.get().archiveFile.get().asFile.let {
            it.parentFile.resolve("${it.nameWithoutExtension}-min.jar")
        })

        val filter = mapOf(
            "jarfilter" to "!**.jar",
            "filter" to "!module-info.class"
        )
        libraryjars(filter, "${System.getProperty("java.home")}/jmods/java.base.jmod")
        printmapping("$buildDir/libs/mapping.txt")

        ignorewarnings()
        dontobfuscate()
        dontoptimize()
        dontwarn()

        keepclasseswithmembers(
            """
            class ${application.mainClassName} {
                public static void main(java.lang.String[]);
            }
        """.trimIndent()
        )

        keep("class module-info")
        keepattributes("Module*")

        val keepClasses = listOf(
            "kotlin.reflect.jvm.internal.**",
            "kotlin.text.RegexOption",
            "io.ktor.client.engine.apache.ApacheEngineContainer",
            "org.apache.commons.logging.impl.LogFactoryImpl",
            "org.apache.commons.logging.**",
            "ch.qos.logback.core.ConsoleAppender",
            "* implements kotlinx.serialization.KSerializer",
            "org.fusesource.jansi.**"
        )
        keepClasses.forEach {
            keep("class $it { public *; }")
        }

        inputs.property("keepClasses", keepClasses)
    }
    jre.configure {
        dependsOn(shadowJar)
    }
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    targetPlatform("alpine-linux") {
        setJdkHome(
            jdkDownload("https://corretto.aws/downloads/latest/amazon-corretto-17-x64-alpine-jdk.tar.gz")
        )
    }
}

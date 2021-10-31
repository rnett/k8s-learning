val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

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

    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
}

application {
    mainClass.set("com.rnett.ApplicationKt")
}

docker {
    javaApplication {
        baseImage.set("alpine:3.13.6")
        ports.set(listOf(8080))
        images.set(setOf("k8s-learning/backend"))
        mainClassName.set("com.rnett.ApplicationKt")
    }
}

tasks.dockerCreateDockerfile.configure {
    instructions.set(emptyList())
    instructionsFromTemplate(file("$projectDir/src/docker/Dockerfile"))
}

tasks.dockerPushImage.configure { enabled = false }

tasks.dockerSyncBuildContext.configure {
    dependsOn("jre", "minimizedJar")
    from("$buildDir/jre")
    from("$buildDir/libs") {
        include {
            it.name.endsWith("$version-all.jar")
        }
        rename {
            "app.jar"
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    val minimizedJar = register<proguard.gradle.ProGuardTask>("minimizedJar") {
        group = "proguard"
        dependsOn(shadowJar)

        injars(shadowJar.get().archiveFile.get().asFile)
        outjars(jar.get().archiveFile.get().asFile.let {
            it.resolveSibling(it.nameWithoutExtension + "-all.${it.extension}")
        })

        val filter = mapOf(
            "jarfilter" to "!**.jar",
            "filter" to "!module-info.class"
        )
        libraryjars(filter, "${System.getProperty("java.home")}/jmods/java.base.jmod")
        libraryjars(filter, "${System.getProperty("java.home")}/jmods/java.sql.jmod")
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

        val keepPublic = listOf(
            "kotlin.reflect.jvm.internal.**",
            "kotlin.text.RegexOption",
            "io.ktor.client.engine.apache.ApacheEngineContainer",
            "org.apache.commons.logging.impl.LogFactoryImpl",
            "org.apache.commons.logging.**",
            "ch.qos.logback.core.ConsoleAppender",
            "* implements kotlinx.serialization.KSerializer",
            "org.fusesource.jansi.**",
            "org.jetbrains.exposed.jdbc.ExposedConnectionImpl",
            "org.jetbrains.exposed.dao.EntityLifecycleInterceptor",
            "com.impossibl.postgres.system.procs.**",
            "com.impossibl.postgres.jdbc.PGDriver",
            "com.impossibl.jdbc.spy.SpyDriver",
            "java.sql.Driver",
            "com.impossibl.postgres.system.procs.ProcProvider",
            "org.jetbrains.exposed.sql.DdlAware",
            "com.impossibl.postgres.protocol.ssl.ConsolePasswordCallbackHandler",
            "org.jetbrains.exposed.dao.DaoEntityIDFactory"
        )
        keepPublic.forEach {
            keep("class $it { public *; }")
        }
        inputs.property("keepPublic", keepPublic)

        val keepAll = listOf(
            "* extends org.jetbrains.exposed.dao.Entity",
        )
        keepAll.forEach {
            keep("class $it { <init>(...); }")
        }
        inputs.property("keepAll", keepAll);

    }

    shadowJar {
        archiveClassifier.set("shadow")
    }

    jre {
        dependsOn(minimizedJar)
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

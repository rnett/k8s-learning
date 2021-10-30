package com.rnett

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.rnett.plugins.*

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        configureRouting()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
    }.start(wait = true)
}

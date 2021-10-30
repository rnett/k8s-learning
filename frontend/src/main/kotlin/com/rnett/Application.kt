package com.rnett

import com.rnett.common.config.configureCORS
import com.rnett.common.config.configureJson
import com.rnett.common.config.configureLogging
import com.rnett.routes.configureRouting
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        configureRouting()
        configureCORS()
        configureLogging()
        configureJson()
    }.start(wait = true)
}
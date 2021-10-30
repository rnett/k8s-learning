package com.rnett.common.config

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS


fun Application.configureCORS() {
    install(CORS) {
//        method(HttpMethod.Options)
//        method(HttpMethod.Put)
//        method(HttpMethod.Delete)
//        method(HttpMethod.Patch)
//        header(HttpHeaders.Authorization)
//        allowCredentials = true
//        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}
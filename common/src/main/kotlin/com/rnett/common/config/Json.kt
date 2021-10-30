package com.rnett.common.config

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json

fun Application.configureJson() {
    install(ContentNegotiation) {
        json()
    }
}
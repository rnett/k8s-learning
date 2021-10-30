package com.rnett

import com.rnett.common.config.configureJson
import com.rnett.common.config.configureLogging
import com.rnett.routes.configureRouting
import com.rnett.routes.db.TodoTable
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        Database.connect(
            System.getenv("KLB_POSTGRES_URL"), driver = "org.postgresql.Driver",
            user = System.getenv("KLB_POSTGRES_USER"), password = System.getenv("KLB_POSTGRES_PASSWORD")
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(TodoTable)
        }

        configureRouting()
        configureLogging()
        configureJson()
    }.start(wait = true)
}


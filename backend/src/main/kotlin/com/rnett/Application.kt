package com.rnett

import com.rnett.plugins.configureHTTP
import com.rnett.plugins.configureMonitoring
import com.rnett.plugins.configureRouting
import com.rnett.plugins.configureSerialization
import com.rnett.plugins.db.TodoTable
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
        configureHTTP()
        configureMonitoring()
        configureSerialization()
    }.start(wait = true)
}

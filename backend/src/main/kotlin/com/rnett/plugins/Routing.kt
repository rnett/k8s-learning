package com.rnett.plugins

import com.rnett.common.Todo
import com.rnett.plugins.db.TodoEntity
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureRouting() {

    routing {
        get("/hello"){
            call.respondText("Hello World")
        }
        route("todo") {
            get {
                newSuspendedTransaction {
                    TodoEntity.all().map { it.toTodo() }
                }.let {
                    call.respond(it)
                }
            }
            get("{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("id must be an integer")
                newSuspendedTransaction {
                    TodoEntity.findById(id)?.toTodo()
                }?.let {
                    call.respond(it)
                } ?: call.respond(HttpStatusCode.NotFound)
            }

            post {
                val todo = call.receive<Todo>()
                val new = newSuspendedTransaction {
                    TodoEntity.newFromTodo(todo)
                }
                call.respond(HttpStatusCode.Created, new.id.value)
            }

            put {
                val todo = call.receive<Todo>()
                newSuspendedTransaction {
                    TodoEntity[todo.id].updateFromTodo(todo)
                }
                call.respond(HttpStatusCode.OK)
            }

            patch("/complete/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("id must be an integer")
                newSuspendedTransaction {
                    TodoEntity[id].apply {
                        completed = true
                        completedAt = Clock.System.now()
                    }
                }
                call.respond(HttpStatusCode.OK)
            }

        }
    }
}

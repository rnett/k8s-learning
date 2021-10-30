package com.rnett.plugins

import com.rnett.common.Todo
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.css.BorderCollapse
import kotlinx.css.CssBuilder
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.border
import kotlinx.css.borderCollapse
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.span
import kotlinx.css.table
import kotlinx.css.td
import kotlinx.css.th
import kotlinx.css.width
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    install(Logging){
        this.level = LogLevel.ALL
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

fun Application.configureRouting() {
    val serverUrl = System.getenv("KLF_SERVER_URL")

    routing {
        get("/hello"){
            call.respondText("Hello World")
        }
        post("/complete/{id}") {
            val id = call.parameters["id"]!!.toInt()
            client.patch<Unit>("$serverUrl/todo/complete/$id")
            call.respondRedirect("/")
        }

        get("/add") {
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                }
                body {
                    h1 { +"Add a new Todo" }
                    form(action = "./add", method = FormMethod.post) {
                        action = "/add"
                        method = FormMethod.post
                        label {
                            +"Title"
                            br {  }
                            input(InputType.text) {
                                name = "name"
                                placeholder = "Title"
                            }
                        }
                        br {  }
                        br {  }
                        label {
                            +"Description"
                            br {  }
                            input(InputType.text) {
                                name = "description"
                                placeholder = "Description"
                            }
                        }
                        br {  }
                        br {  }
                        input(InputType.submit) {
                            value = "Add"
                        }
                    }
                }
            }
        }

        post("/add") {
            val params = call.receiveParameters()
            client.post<Unit>("$serverUrl/todo") {
                body = buildJsonObject{
                    put("name", params["name"]!!)
                    put("description", params["description"]!!)
                }
                contentType(ContentType.Application.Json)
            }
            call.respondRedirect("/")
        }

        get("/") {
            val todos = client.get<List<Todo>>("$serverUrl/todo")
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                }
                body {
                    h1 { +"Todos" }
                    form(action = "./add", method = FormMethod.get) {
                        input(type = InputType.submit) {
                            value = "Add!"
                        }
                    }
                    br {  }
                    table {
                        thead {
                            tr {
                                th { +"Title" }
                                th { +"Description" }
                                th { +"Created" }
                                th { +"Complete!" }
                            }
                        }
                        tbody {
                            todos.filterNot { it.completed }.forEach { todo ->
                                tr {
                                    td { +todo.name }
                                    td { +todo.description }
                                    td { +todo.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).toString() }
                                    td {
                                        form(action = "./complete/${todo.id}", method = FormMethod.post) {
                                            input(type = InputType.submit) {
                                                value = "Complete!"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    h1 { +"Completed" }
                    table {
                        thead {
                            tr {
                                th { +"Title" }
                                th { +"Description" }
                                th { +"Created" }
                                th { +"Completed" }
                            }
                        }
                        tbody {
                            todos.filter { it.completed }.forEach { todo ->
                                tr {
                                    td { +todo.name }
                                    td { +todo.description }
                                    td { +todo.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).toString() }
                                    td { +todo.completedAt!!.toLocalDateTime(TimeZone.currentSystemDefault()).toString() }
                                }
                            }
                        }
                    }
                }
            }
        }
        get("/styles.css") {
            call.respondCss {
                span {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                }
                table {
                    width = 60.pct
                    borderCollapse = BorderCollapse.collapse
                }
                th {
                    border = "1px solid black"
                    padding = "20px"
                }
                td {
                    border = "1px solid black"
                    padding = "10px"
                }
            }
        }
    }
}

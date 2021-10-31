package com.rnett.routes

import com.rnett.common.Todo
import com.rnett.routes.pages.PageTemplate
import com.rnett.routes.pages.addPage
import com.rnett.routes.pages.styles
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.IgnoreTrailingSlash
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.css.CssBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

val client = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    install(Logging) {
        this.level = LogLevel.ALL
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

fun Application.configureRouting() {

    install(IgnoreTrailingSlash)
    val serverUrl = System.getenv("KLF_SERVER_URL")

    routing {
        get("/hello") {
            call.respondText("Hello from the frontend!")
        }
        get("/hello/backend") {
            val hello = client.get<HttpResponse>("$serverUrl/hello")
            call.respond(hello.status, hello.receive<String>())
        }
        post("/complete/{id}") {
            val id = call.parameters["id"]!!.toInt()
            client.patch<Unit>("$serverUrl/todo/complete/$id")
            call.respondRedirect("/")
        }

        get("/add") {
            call.respondHtmlTemplate(PageTemplate()) {
                content {
                    addPage()
                }
            }
        }

        post("/add") {
            val params = call.receiveParameters()
            client.post<Unit>("$serverUrl/todo") {
                body = buildJsonObject {
                    put("name", params["name"]!!)
                    put("description", params["description"]!!)
                }
                contentType(ContentType.Application.Json)
            }
            call.respondRedirect("/")
        }

        get("/") {
            val todos = client.get<List<Todo>>("$serverUrl/todo")
            call.respondHtmlTemplate(PageTemplate()) {
                content {
                    mainPage(todos)
                }
            }
        }
        get("/styles.css") {
            call.respondCss(CssBuilder::styles)
        }
    }
}

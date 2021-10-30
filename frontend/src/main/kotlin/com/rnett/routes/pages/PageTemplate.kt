package com.rnett.routes.pages

import io.ktor.html.Placeholder
import io.ktor.html.Template
import io.ktor.html.insert
import kotlinx.html.BODY
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link

class PageTemplate : Template<HTML> {
    val content = Placeholder<BODY>()
    override fun HTML.apply() {
        head {
            link(rel = "stylesheet", href = "/styles.css", type = "text/css")
        }
        body {
            insert(content)
        }
    }
}
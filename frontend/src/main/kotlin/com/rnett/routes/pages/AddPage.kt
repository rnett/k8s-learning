package com.rnett.routes.pages

import kotlinx.html.BODY
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.br
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.input
import kotlinx.html.label

fun BODY.addPage() {
    h1 { +"Add a new Todo" }
    form(action = "./add", method = FormMethod.post) {
        action = "/add"
        method = FormMethod.post
        label {
            +"Title"
            br { }
            input(InputType.text) {
                name = "name"
                placeholder = "Title"
            }
        }
        br { }
        br { }
        label {
            +"Description"
            br { }
            input(InputType.text) {
                name = "description"
                placeholder = "Description"
            }
        }
        br { }
        br { }
        input(InputType.submit) {
            value = "Add"
        }
    }
}
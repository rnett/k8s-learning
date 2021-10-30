package com.rnett.routes

import com.rnett.common.Todo
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.BODY
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.br
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.input
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

fun BODY.mainPage(todos: List<Todo>) {
    h1 { +"Todos" }
    form(action = "./add", method = FormMethod.get) {
        input(type = InputType.submit) {
            value = "Add!"
        }
    }
    br { }
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
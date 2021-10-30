package com.rnett.routes.pages

import kotlinx.css.BorderCollapse
import kotlinx.css.CssBuilder
import kotlinx.css.border
import kotlinx.css.borderCollapse
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.table
import kotlinx.css.td
import kotlinx.css.th
import kotlinx.css.width

fun CssBuilder.styles() {
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
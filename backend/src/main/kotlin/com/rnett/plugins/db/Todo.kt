package com.rnett.plugins.db

import com.rnett.common.Todo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.*

object TodoTable : LongIdTable("todos", "id") {
    val name = varchar("name", 255)
    val description = text("description")
    val completed = bool("completed")
    val createdAt = timestamp("created")
    val completedAt = timestamp("completedAt").nullable()
}

class TodoEntity(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, TodoEntity>(TodoTable) {
        fun newFromTodo(todo: Todo, id: Long? = null) = new(id) {
            updateFromTodo(todo)
        }
    }

    var name by TodoTable.name
    var description by TodoTable.description
    var completed by TodoTable.completed
    var createdAt by TodoTable.createdAt
    var completedAt by TodoTable.completedAt

    fun updateFromTodo(todo: Todo){
        this.name = todo.name
        this.description = todo.description
        this.completed = todo.completed
        this.createdAt = todo.createdAt
        this.completedAt = todo.completedAt
    }

    fun toTodo(): Todo = Todo(
        id = id.value,
        name = name,
        description = description,
        completed = completed,
        createdAt = createdAt,
        completedAt = completedAt
    )
}
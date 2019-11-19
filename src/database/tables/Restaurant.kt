package com.andrew.database.tables

import org.jetbrains.exposed.sql.Table

object RestaurantTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 200)
}

data class Restaurant(
    val name: String
)

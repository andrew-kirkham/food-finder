package com.andrew.database.tables

import org.jetbrains.exposed.sql.Table

object FoodTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 200)
    val restaurantId = integer("restaurant_id") references RestaurantTable.id
}

data class Food(
    val name: String,
    val restaurant: Restaurant
)

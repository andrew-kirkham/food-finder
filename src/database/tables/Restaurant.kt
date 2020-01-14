package com.andrew.database.tables

import com.andrew.database.loggedTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object RestaurantTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 200)
}

data class Restaurant(
    val id: Int,
    val name: String
)

fun ResultRow.toRestaurant() = Restaurant(
    id = this[RestaurantTable.id],
    name = this[RestaurantTable.name]
)

fun getOrCreateRestaurant(restaurantName: String): Int {
    val restaurant = loggedTransaction {
        RestaurantTable.select {
            RestaurantTable.name eq restaurantName
        }.map { it.toRestaurant() }.firstOrNull()
    }
    return restaurant?.id ?: createNewRestaurant(restaurantName)
}

fun createNewRestaurant(restaurantName: String): Int {
    return loggedTransaction {
        RestaurantTable.insert {
            it[name] = restaurantName
        } get RestaurantTable.id
    }
}

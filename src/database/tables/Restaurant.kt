package com.andrew.database.tables

import com.andrew.database.loggedTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

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


/**
 * Create a new [Restaurant] with the given [String] name and return the id
 */
fun createNewRestaurant(restaurantName: String): Restaurant {
    return transaction {
        val values = RestaurantTable.insert {
            it[name] = restaurantName
        }.resultedValues ?: emptyList()
        values.map { it.toRestaurant() }.first()
    }
}

/**
 * Get a [Restaurant] with the given [String] name or create if it doesn't exist
 */
fun getOrSaveRestaurant(restaurantName: String): Restaurant {
    val restaurant = transaction {
        RestaurantTable.select {
            RestaurantTable.name eq restaurantName
        }.map { it.toRestaurant() }.firstOrNull()
    }
    return restaurant ?: createNewRestaurant(restaurantName)
}

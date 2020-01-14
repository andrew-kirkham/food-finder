package com.andrew.controllers

import com.andrew.database.loggedTransaction
import com.andrew.database.tables.Restaurant
import com.andrew.database.tables.RestaurantTable
import com.andrew.database.tables.toRestaurant
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

private val logger = KotlinLogging.logger {}

object RestaurantController {

    fun searchByName(name: String): List<Restaurant> {
        logger.info { "$name was requested" }
        return loggedTransaction {
            RestaurantTable
                .select { RestaurantTable.name.eq(name) }
                .map { it.toRestaurant() }
                .toList()
        }
    }

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
}

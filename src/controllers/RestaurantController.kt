package com.andrew.controllers

import com.andrew.database.loggedTransaction
import com.andrew.database.tables.Restaurant
import com.andrew.database.tables.RestaurantTable
import com.andrew.database.tables.toRestaurant
import mu.KotlinLogging
import org.jetbrains.exposed.sql.select

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
     * Create a new [Restaurant] with the given [String] name
     */
    fun createNewRestaurant(restaurantName: String): Int {
        return createNewRestaurant(restaurantName)
    }
}

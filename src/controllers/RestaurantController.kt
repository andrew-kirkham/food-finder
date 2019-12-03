package com.andrew.controllers

import com.andrew.database.loggedTransaction
import com.andrew.database.tables.Restaurant
import com.andrew.database.tables.RestaurantTable
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

private val logger = KotlinLogging.logger {}

@KtorExperimentalLocationsAPI
@Location("/restaurant")
class RestaurantController {

    @Location("/{name}")
    class Search(val name: String) {
        fun searchByName(): List<Restaurant> {
            logger.info { "$name was requested" }
            return loggedTransaction {
                RestaurantTable
                    .select { RestaurantTable.name.eq(name) }
                    .map { Restaurant(it[RestaurantTable.name]) }
                    .toList()
            }
        }
    }

    class Create {
        /**
         * Create a new [Restaurant] with the given [String] name
         */
        fun createNewRestaurant(restaurantName: String): Int {
            return loggedTransaction {
                RestaurantTable.insert {
                    it[name] = restaurantName
                } get RestaurantTable.id
            }
        }
    }
}

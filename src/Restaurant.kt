package com.andrew

import com.andrew.database.tables.Restaurant
import com.andrew.database.tables.RestaurantTable
import io.ktor.locations.Location
import mu.KotlinLogging
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

private val logger = KotlinLogging.logger {}

@Location("/restaurant")
class RestaurantController {

    @Location("/{name}")
    class Search(val name: String) {
        fun search(): List<Restaurant> {
            logger.info { "$name was requested" }
            return transaction {
                addLogger(StdOutSqlLogger)
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
        fun create(restaurantName: String): Int {
            return transaction {
                addLogger(StdOutSqlLogger)
                RestaurantTable.insert {
                    it[name] = restaurantName
                } get RestaurantTable.id
            }
        }
    }
}

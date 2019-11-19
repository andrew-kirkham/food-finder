package com.andrew

import com.andrew.database.tables.Food
import com.andrew.database.tables.FoodTable
import com.andrew.database.tables.Restaurant
import com.andrew.database.tables.RestaurantTable
import io.ktor.locations.Location
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

private val logger = KotlinLogging.logger {}

@Location("/food")
class FoodController {

    @Location("/{name}")
    class Search(val name: String) {
        fun search(): List<Food> {
            logger.info { "$name was requested" }
            return transaction {
                addLogger(StdOutSqlLogger)
                (FoodTable innerJoin RestaurantTable)
                    .select { FoodTable.name.eq(name) and (FoodTable.restaurantId eq RestaurantTable.id) }
                    .map { Food(it[FoodTable.name], Restaurant(it[RestaurantTable.name])) }
                    .toList()
            }
        }
    }

    class Create {
        /**
         * Create a new [Food] with the given [String] name and [RestaurantTable] pk
         */
        fun create(foodName: String, restaurant: Int): Int {
            return transaction {
                addLogger(StdOutSqlLogger)
                FoodTable.insert {
                    it[name] = foodName
                    it[restaurantId] = restaurant
                } get FoodTable.id
            }
        }
    }
}

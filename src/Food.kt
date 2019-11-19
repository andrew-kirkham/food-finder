package com.andrew

import io.ktor.locations.Location
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private var foodMap = HashMap<Food, ArrayList<Restaurant>>()

typealias Food = String

@Location("/food")
class FoodController {

    @Location("/{name}")
    class Search(val name: String) {
        fun search(): List<String>? {
            logger.info { "$name was requested" }
            return when (name) {
                in foodMap.keys -> foodMap[name]
                "steak" -> null
                else -> listOf(name)
            }
        }
    }

    class Create() {
        fun create(food: Food, restaurant: Restaurant) {
            val restaurantList = foodMap.getOrDefault(food, ArrayList())
            restaurantList.add(restaurant)
            foodMap[food] = restaurantList
        }
    }
}

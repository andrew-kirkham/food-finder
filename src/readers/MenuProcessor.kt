package com.andrew.readers

import com.andrew.controllers.RestaurantController.getOrSaveRestaurant
import com.andrew.database.tables.Food
import com.andrew.database.tables.FoodTable
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction

class MenuProcessor {

    fun processMenu(filePath: String, restaurantName: String) {
        val restaurant = getOrSaveRestaurant(restaurantName)
        val menuItems = PdfReader.readAndProcessPdf(filePath, restaurant)
        saveFoodToDatabase(menuItems)
    }

    fun saveFoodToDatabase(foods: List<Food>) {
        transaction {
            FoodTable.batchInsert(foods) { f ->
                this[FoodTable.name] = f.name
                this[FoodTable.restaurantId] = f.restaurant.id
            }
        }
    }
}

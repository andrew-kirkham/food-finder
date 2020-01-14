package com.andrew.readers

import com.andrew.controllers.RestaurantController
import com.andrew.database.tables.FoodTable
import com.andrew.database.tables.RestaurantTable
import com.andrew.database.tables.toRestaurant
import java.io.File
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PdfReader {

    fun readAndProcessPdf() {
        val entries = readPdf()
        processPdfEntries(entries)
    }

    fun readPdf(): List<String> {
        PDDocument.load(File("/Users/andrew/Downloads/rl-menu.pdf")).use { document ->
            if (!document.isEncrypted) {
                val tStripper = PDFTextStripper()
                val pdfFileInText = tStripper.getText(document)
                return pdfFileInText.split("\\d.( PER PERSON)?\\n".toRegex())
            }
        }
        return emptyList()
    }

    fun processPdfEntries(entries: List<String>) {
//        val restaurant = getOrSaveRestaurant("RL")
        val foods = mutableListOf<String>()
        for (food in entries) {
            // remove any notices accidentally picekd up
            if (food.startsWith("*")) {
                continue
            }
            foods.add(getFoodFromString(food))
        }
//        saveFoodToDatabase(foods, restaurant)
    }

    fun getFoodFromString(foodString: String): String {
        // the title is the first entry with the ingredients being the subsequent lines
        val title = foodString.split("\n")[0]
        // remove any bonus characters and then lowercase it
        val toPrint = title.replace("[*$@!]".toRegex(), "").toLowerCase()
        println(toPrint)
        return toPrint
    }

    fun saveFoodToDatabase(foods: List<String>, restaurantId: Int) {
        transaction {
            FoodTable.batchInsert(foods) { f ->
                this[FoodTable.name] = f
                this[FoodTable.restaurantId] = restaurantId
            }
        }
    }

    fun getOrSaveRestaurant(restaurantName: String): Int {
        val restaurant = transaction {
            RestaurantTable.select {
                RestaurantTable.name eq restaurantName
            }.map { it.toRestaurant() }.firstOrNull()
        }
        return restaurant?.id ?: RestaurantController.createNewRestaurant(restaurantName)
    }
}

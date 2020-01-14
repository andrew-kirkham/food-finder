package com.andrew.readers

import com.andrew.database.tables.Food
import com.andrew.database.tables.Restaurant
import java.io.File
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

object PdfReader {

    fun readAndProcessPdf(filePath: String, restaurant: Restaurant): MutableList<Food> {
        val entries = readPdf(filePath)
        return processPdfEntries(entries, restaurant)
    }

    fun readPdf(filePath: String): List<String> {
        PDDocument.load(File(filePath)).use { document ->
            if (!document.isEncrypted) {
                val tStripper = PDFTextStripper()
                val pdfFileInText = tStripper.getText(document)
                return pdfFileInText.split("\\d.( PER PERSON)?\\n".toRegex())
            }
        }
        // unable to parse the pdf - return an empty list
        return emptyList()
    }

    private fun processPdfEntries(
        entries: List<String>,
        restaurant: Restaurant
    ): MutableList<Food> {
        val foods = mutableListOf<Food>()
        for (food in entries) {
            // remove any notices accidentally picked up
            if (food.startsWith("*")) {
                continue
            }
            foods.add(Food(getFoodFromString(food), restaurant))
        }
        return foods
    }

    private fun getFoodFromString(foodString: String): String {
        // the title is the first entry with the ingredients being the subsequent lines
        val title = foodString.split("\n")[0]
        // remove any bonus characters and then lowercase it
        val toPrint = title.replace("[*$@!]".toRegex(), "").toLowerCase()
        println(toPrint)
        return toPrint
    }
}

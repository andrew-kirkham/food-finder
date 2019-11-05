package com.andrew

import io.ktor.locations.Location

@Location("/food")
class Food {

    @Location("/{name}")
    class Search(val name: String) {
        fun search(): String? {
            return when (name) {
                "steak" -> null
                else -> name
            }
        }
    }
}

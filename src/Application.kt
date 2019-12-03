package com.andrew

import com.andrew.controllers.FoodController
import com.andrew.controllers.RestaurantController
import com.andrew.database.connectToDatabase
import com.andrew.database.upgradeDatabase
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import java.text.DateFormat

fun main(args: Array<String>) {
    connectToDatabase()
    upgradeDatabase()
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!", contentType = ContentType.Text.Plain)
        }
        route("/food") {
            get("/search/{name}") {
                val name = call.parameters["name"]
                name?.let {
                    val food = FoodController.searchByName(it)
                    if (food.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respondText("Food: name=${food}")
                    }
                }
            }
            post("") {
                val request = call.receive<FoodRequest>()
                val id =
                    FoodController.createNewFoodAtRestaurant(request.name, request.restaurantId)
                call.respond(HttpStatusCode.Created, "Created Food with id=$id")
            }
        }

        route("/restaurant") {
            get("/search/{name}") {
                val name = call.parameters["name"]
                name?.let {
                    call.respondText("Food: name=${RestaurantController.searchByName(it)}")
                }
            }
            post("/create") {
                val request = call.receive<RestaurantRequest>()
                val id = RestaurantController.createNewRestaurant(request.name)
                call.respond(HttpStatusCode.Created, "Created Restaurant with id=$id")
            }
        }
    }
}

data class FoodRequest(val name: String, val restaurantId: Int)
data class RestaurantRequest(val name: String)

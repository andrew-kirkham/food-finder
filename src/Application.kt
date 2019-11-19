package com.andrew

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import java.text.DateFormat

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }
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

        get<FoodController.Search> {
            call.respondText("Food: name=${it.search()}")
        }

        post<FoodController.Create> {
            val request = call.receive<FoodRequest>()
            it.create(request.name, request.restaurant)
            call.respond(HttpStatusCode.Created)
        }
    }
}

data class FoodRequest(val name: Food, val restaurant: Restaurant)

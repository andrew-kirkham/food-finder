import com.andrew.controllers.FoodController
import com.andrew.database.tables.Food
import com.andrew.database.tables.Restaurant
import com.andrew.module
import com.google.gson.Gson
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val restaurant = Restaurant(1, "restaurant")
    private val food = Food("food", restaurant)

    @AfterTest
    fun cleanUp() {
        unmockkAll()
    }

    @Test
    fun testRoot() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }
    }

    @Test
    fun testGetFood() {
        mockkObject(FoodController)
        every { FoodController.searchByName(any()) } returns listOf(food)
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/food/search/123").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Food: name=[$food]", response.content)
            }
        }
    }

    @Test
    fun testGetFoodNoFoodFound() {
        mockkObject(FoodController)
        every { FoodController.searchByName(any()) } returns emptyList()
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/food/search/123").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun createFood() {
        mockkObject(FoodController)
        every { FoodController.createNewFoodAtRestaurant(any(), any()) } returns 1
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Post, "/food") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Gson().toJson(food))
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
            }
        }
    }
}

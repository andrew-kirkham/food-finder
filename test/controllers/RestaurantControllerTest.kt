package controllers

import com.andrew.database.tables.createNewRestaurant
import io.kotlintest.shouldBe

import org.jetbrains.exposed.sql.Database
import org.junit.BeforeClass
import org.junit.Test


open class DatabaseTest {
    lateinit var database: Database

    @BeforeClass
    fun beforeClass() {
        database = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    }
}

class RestaurantControllerTest : DatabaseTest() {

    @Test
    fun testCreateNewRestaurant() {
        val r = createNewRestaurant("asdf")
        r.name shouldBe 10
    }
}

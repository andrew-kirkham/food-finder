package com.andrew.database

import com.andrew.database.tables.FoodTable
import com.andrew.database.tables.RestaurantTable
import java.sql.Connection
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager

fun connectToDatabase(): Database {
    val db = Database.connect("jdbc:sqlite:./data.db", "org.sqlite.JDBC")
    db.transactionManager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    return db
}

fun upgradeDatabase() =
    loggedTransaction { SchemaUtils.createMissingTablesAndColumns(FoodTable, RestaurantTable) }

/**
 * Log the sql transaction provided by [execute] lambda
 */
fun <T> loggedTransaction(execute: Transaction.() -> T): T {
    return transaction {
        addLogger(StdOutSqlLogger)
        execute()
    }
}

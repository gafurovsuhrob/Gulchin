package com.seros.gulchin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.InputStreamReader

class MyDataBaseHelper2(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "verseDB2.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val assets = context.assets
        assets?.open("your_sql_script.sql")?.use { inputStream ->
            val sqlStatements = BufferedReader(InputStreamReader(inputStream)).readText()
            val statements = sqlStatements.split(";")
            for (statement in statements) {
                db?.execSQL(statement)
            }
        }
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Метод вызывается, если версия базы данных меняется
        // Здесь можно выполнять необходимые обновления схемы
    }
}

package com.seros.gulchin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "verseDB2.db"
        const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase?) {
//        val createTableSQL = "CREATE TABLE Verses (" +
//                "Id INTEGER NOT NULL CONSTRAINT PK_Verses PRIMARY KEY AUTOINCREMENT," +
//                "Title TEXT NOT NULL," +
//                "Verse_Text TEXT NOT NULL," +
//                "Date TEXT NOT NULL," +
//                "Verse_Number TEXT NOT NULL" +
//                ");"
//        db?.execSQL(createTableSQL)
//
//        val copyDataQuery = "INSERT INTO Verses2 (Id, Title, Verse_Text, Date, Verse_Number) SELECT Id, Title, Verse_Text, Date, Verse_Number FROM Verses"
//        db?.execSQL(copyDataQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Метод вызывается, если версия базы данных меняется
        // Здесь можно выполнять необходимые обновления схемы
    }
}

package com.koroden.app7

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context?) : SQLiteOpenHelper(context,"coord.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS coord (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "place_name TEXT, " +
                "latitude TEXT, " +
                "longitude TEXT)");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVer: Int, newVer: Int) {
        // Ничего не делаем

    }
}
package com.wintersoldier13.spendtacker

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.wintersoldier13.spendtacker.schema.SmsRegex.SmsRegex;

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION : Int= 1;
        const val DATABASE_NAME : String= "SpendTracker.db";
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create SMS regex table to store the regex
       db.execSQL(createSmsRegexTable())
//        TODO : add other tables here
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        TODO : not required rn, maybe in the future?
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    private fun createSmsRegexTable() : String {
        return "CREATE TABLE ${SmsRegex.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${SmsRegex.COLUMN_NAME_REGEX} TEXT," +
                "${SmsRegex.COLUMN_NAME_TRANSACTION_TYPE} TEXT" +
                ")";
    }
}
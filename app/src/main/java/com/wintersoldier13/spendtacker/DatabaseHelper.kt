package com.wintersoldier13.spendtacker

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.wintersoldier13.spendtacker.schema.SmsRegex;
import com.wintersoldier13.spendtacker.schema.FilteredSms;
import com.wintersoldier13.spendtacker.schema.SmsTag
import com.wintersoldier13.spendtacker.schema.Tag

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION: Int = 1;
        const val DATABASE_NAME: String = "SpendTracker.db";
    }

    override fun onCreate(db: SQLiteDatabase) {
//         Create SMS regex table to store the regex
        db.execSQL(createSmsRegexTable())
//        Create Table for filtered SMS
        db.execSQL(createFilteredSmsTable())
//        Create Table mapping SMS with Tags
        db.execSQL(createSmsTagTable())
//        Create a table to store known tags
        db.execSQL(createTagTable())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        TODO : not required rn, maybe in the future?
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    private fun createSmsRegexTable(): String {
        return "CREATE TABLE ${SmsRegex.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${SmsRegex.COLUMN_NAME_REGEX} TEXT," +
                "${SmsRegex.COLUMN_NAME_TRANSACTION_TYPE} TEXT" +
                ")";
    }

    private fun createFilteredSmsTable(): String {
        return "CREATE TABLE ${FilteredSms.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FilteredSms.COLUMN_NAME_DAY} INTEGER," +
                "${FilteredSms.COLUMN_NAME_MONTH} INTEGER," +
                "${FilteredSms.COLUMN_NAME_YEAR} INTEGER," +
                "${FilteredSms.COLUMN_NAME_SENDER} TEXT," +
                "${FilteredSms.COLUMN_NAME_BODY} TEXT," +
                "${FilteredSms.COLUMN_NAME_AMOUNT} INTEGER" +
                ")";
    }

    private fun createSmsTagTable(): String {
        return "CREATE TABLE ${SmsTag.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${SmsTag.COLUMN_NAME_SMS_ID} INTEGER," +
                "${SmsTag.COLUMN_NAME_TAG} TEXT," +
                "${SmsTag.COLUMN_NAME_EXPLANATION} TEXT" +
                ")";
    }
    private fun createTagTable(): String {
        return "CREATE TABLE ${Tag.TABLE_NAME} (" +
        "${Tag.COLUMN_NAME_TAG} TEXT" +
        ")";
    }
}
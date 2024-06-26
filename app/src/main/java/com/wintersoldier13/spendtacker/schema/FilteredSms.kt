package com.wintersoldier13.spendtacker.schema

import android.provider.BaseColumns

object FilteredSms : BaseColumns {
    const val TABLE_NAME = "FILTERED_SMS";
    const val COLUMN_NAME_DAY = "DAY";
    const val COLUMN_NAME_MONTH = "MONTH";
    const val COLUMN_NAME_YEAR = "YEAR";
    const val COLUMN_NAME_BODY = "BODY";
    const val COLUMN_NAME_SENDER = "SENDER";
    const val COLUMN_NAME_AMOUNT = "AMOUNT";
}
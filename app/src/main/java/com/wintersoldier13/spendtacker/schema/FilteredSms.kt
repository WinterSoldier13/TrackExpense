package com.wintersoldier13.spendtacker.schema

import android.provider.BaseColumns

object FilteredSms : BaseColumns {
    const val TABLE_NAME = "FILTERED_SMS";
    const val COLUMN_NAME_DATE = "DATE";
    const val COLUMN_NAME_BODY = "BODY";
    const val COLUMN_NAME_SENDER = "SENDER";
}
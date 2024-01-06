package com.wintersoldier13.spendtacker.schema

import android.provider.BaseColumns

object SmsRegex{
    // Table contents are grouped together in an anonymous object.
    object SmsRegex: BaseColumns{
        const val TABLE_NAME = "SMS_REGEX"
        const val COLUMN_NAME_REGEX = "REGEX"
        const val COLUMN_NAME_TRANSACTION_TYPE = "TRANSACTION_TYPE"
    }
}
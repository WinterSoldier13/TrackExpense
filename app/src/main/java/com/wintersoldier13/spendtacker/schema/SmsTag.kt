package com.wintersoldier13.spendtacker.schema

import android.provider.BaseColumns

object SmsTag : BaseColumns {
   const val TABLE_NAME = "SMS_TAG";
   const val COLUMN_NAME_SMS_ID = "SMS_ID";
   const val COLUMN_NAME_TAG = "TAG";
   const val COLUMN_NAME_EXPLANATION = "EXPLANATION";
   const val COLUMN_NAME_UPDATED_AMOUNT = "UPDATED_AMT";
   const val COLUMN_NAME_IS_CREDIT_CARD = "CREDIT_CARD";
}
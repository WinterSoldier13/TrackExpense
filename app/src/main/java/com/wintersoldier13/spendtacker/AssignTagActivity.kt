package com.wintersoldier13.spendtacker

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.wintersoldier13.spendtacker.data.KnownTags
import com.wintersoldier13.spendtacker.schema.FilteredSms
import com.wintersoldier13.spendtacker.schema.SmsTag

class AssignTagActivity : AppCompatActivity() {
    private lateinit var transactionDateTv: TextView
    private lateinit var smsBodyTv: TextView
    private lateinit var amountEt: EditText
    private lateinit var transactionReasonEt: EditText
    private lateinit var tagSpinner: Spinner
    private lateinit var isCreditCardSpinner: Spinner
    private lateinit var deleteBt: Button
    private lateinit var saveBt: Button
    private lateinit var readableDb: SQLiteDatabase
    private lateinit var writeableDb: SQLiteDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_tag)

        readableDb = DatabaseHelper(this).readableDatabase
        writeableDb = DatabaseHelper(this).writableDatabase
        transactionDateTv = findViewById(R.id.assign_tag_textview_date)
        smsBodyTv = findViewById(R.id.assign_tag_textview_description)
        amountEt = findViewById(R.id.assign_tag_edittext_transaction_amount)
        transactionReasonEt = findViewById(R.id.assign_tag_edittext_transaction_explanation)
        tagSpinner = findViewById(R.id.assign_tag_spinner_tag)
        isCreditCardSpinner = findViewById(R.id.assign_tag_spinner_credit_card)
        deleteBt = findViewById(R.id.assign_tag_button_delete)
        saveBt = findViewById(R.id.assign_tag_button_add_expense)

        val smsId = intent.extras?.getInt("sms_id", -1)
        val transactionSms = getSmsById(smsId!!)

        transactionDateTv.text =
            "${transactionSms.day}/${transactionSms.month}/${transactionSms.year}"
        smsBodyTv.text = transactionSms.smsBody
        amountEt.text = transactionSms.amount.toString().toEditable()

//        Populate the credit card spinner
        val isCreditCardSpinnerOptions = ArrayList<String>();
        isCreditCardSpinnerOptions.add("yes")
        isCreditCardSpinnerOptions.add("no")

        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            isCreditCardSpinnerOptions.toMutableList()
        )
        isCreditCardSpinner.adapter = arrayAdapter

//        Populate the tag spinner
        tagSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, KnownTags.getTags())

//        on click button on delete
        deleteBt.setOnClickListener {
//            TODO implement delete from db
            val selection = "_id = $smsId"
            val deletedRows = writeableDb.delete(FilteredSms.TABLE_NAME, selection, null)
            println("Deleted row: $deletedRows from ${FilteredSms.TABLE_NAME}")
            finish()
        }

        saveBt.setOnClickListener {
            if (transactionReasonEt.text.toString() == "") {
                Toast.makeText(this, "Please specify a reason", Toast.LENGTH_SHORT).show()
            } else {
                val values = ContentValues().apply {
                    put(SmsTag.COLUMN_NAME_SMS_ID, smsId)
                    put(SmsTag.COLUMN_NAME_TAG, tagSpinner.selectedItem.toString())
                    put(SmsTag.COLUMN_NAME_EXPLANATION, transactionReasonEt.text.toString())
                    put(SmsTag.COLUMN_NAME_UPDATED_AMOUNT, amountEt.text.toString().toDouble())
                    put(
                        SmsTag.COLUMN_NAME_IS_CREDIT_CARD,
                        if (tagSpinner.selectedItem.toString() == "yes") 1 else 0
                    )
                }

                val rowId = writeableDb.insert(SmsTag.TABLE_NAME, null, values)
                println("Inserted into SmsTag table with rowId: $rowId")
            }

        }

    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun getSmsById(smsId: Int): TransactionSms {
        val query = "SELECT * FROM ${FilteredSms.TABLE_NAME} WHERE _id=${smsId}"
        val cursor = readableDb.rawQuery(query, null)
        var day = 0;
        var month = 0;
        var year = 0;
        var amount = 0.0;
        var smsBody: String = "";
        with(cursor) {
            while (moveToNext()) {
                day = getInt(cursor.getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_DAY))
                month = getInt(cursor.getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_MONTH))
                year = getInt(cursor.getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_YEAR))
                amount = getDouble(cursor.getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_AMOUNT))
                smsBody = getString(cursor.getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_BODY))
            }
        }
        cursor.close()
        return TransactionSms(day, month, year, amount, smsBody)
    }

    class TransactionSms(
        var day: Int,
        var month: Int,
        var year: Int,
        var amount: Double,
        var smsBody: String
    ) {}
}
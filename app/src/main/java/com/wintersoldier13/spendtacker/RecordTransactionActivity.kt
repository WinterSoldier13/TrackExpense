package com.wintersoldier13.spendtacker

import android.app.DatePickerDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.Telephony.Sms
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.wintersoldier13.spendtacker.data.KnownTags
import com.wintersoldier13.spendtacker.schema.SmsTag
import java.text.SimpleDateFormat
import java.time.temporal.TemporalAmount
import java.util.Calendar

class RecordTransactionActivity : AppCompatActivity() {
    private lateinit var dateEt: EditText
    private lateinit var amountEt: EditText
    private lateinit var transactionReason: EditText
    private lateinit var tagSpinner: Spinner
    private lateinit var isCreditCardSpinner: Spinner
    private lateinit var deleteBt: Button
    private lateinit var addBt: Button
    private lateinit var headingTv: TextView
    private lateinit var writeableDb: SQLiteDatabase
    private lateinit var readableDb: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_transaction)

        dateEt = findViewById(R.id.record_transaction_edittext_date)
        amountEt = findViewById(R.id.record_transaction_edittext_transaction_amount)
        transactionReason = findViewById(R.id.record_transaction_edittext_transaction_explanation)
        tagSpinner = findViewById(R.id.record_transaction_spinner_tag)
        isCreditCardSpinner = findViewById(R.id.record_transaction_spinner_credit_card)
        deleteBt = findViewById(R.id.record_transaction_button_delete)
        addBt = findViewById(R.id.record_transaction_button_add)
        headingTv = findViewById(R.id.record_transaction_textview_heading)
        writeableDb = DatabaseHelper(this).writableDatabase
        readableDb = DatabaseHelper(this).readableDatabase
        recordFlow()

    }

    private fun recordFlow() {

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

        dateEt.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our edit text.
                    val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    dateEt.setText(dat)
                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            // at last we are calling show
            // to display our date picker dialog.
            datePickerDialog.show()
        }

        deleteBt.setOnClickListener {
            Toast.makeText(this, "Record Payment operation cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }

        addBt.setOnClickListener {

            if (dateEt.text.toString() == "") {
                Toast.makeText(this, "enter date", Toast.LENGTH_SHORT).show()
            } else if (amountEt.text.toString() == "" || amountEt.text.toString().toInt() == 0) {
                Toast.makeText(this, "amount cannot be null/zero", Toast.LENGTH_SHORT).show()
            } else if (transactionReason.text.toString() == "") {
                Toast.makeText(this, "Enter a transaction explanation", Toast.LENGTH_SHORT).show()
            } else {
                val date = dateEt.text.toString()
                val day = date.substring(0, date.indexOf('/')).toInt()
                val month = date.substring(date.indexOf('/') + 1, date.lastIndexOf('/')).toInt()
                val year = date.substring(date.lastIndexOf('/') + 1).toInt()
                val amount = amountEt.text.toString().toDouble()
                val reason = transactionReason.text.toString()
                val tag = tagSpinner.selectedItem.toString()
                val isCreditCard =
                    if (isCreditCardSpinner.selectedItem.toString() == "yes") 1 else 0

                val values = ContentValues().apply {
                    put(SmsTag.COLUMN_NAME_SMS_ID, 0)
                    put(SmsTag.COLUMN_NAME_TAG, tag)
                    put(SmsTag.COLUMN_NAME_EXPLANATION, reason)
                    put(SmsTag.COLUMN_NAME_UPDATED_AMOUNT, amount)
                    put(
                        SmsTag.COLUMN_NAME_IS_CREDIT_CARD, isCreditCard
                    )
                    put(SmsTag.COLUMN_NAME_DAY, day)
                    put(SmsTag.COLUMN_NAME_MONTH, month)
                    put(SmsTag.COLUMN_NAME_YEAR, year)
                }

                val rowId = writeableDb.insert(SmsTag.TABLE_NAME, null, values)
                if (rowId >= 0) {
                    Toast.makeText(this, "added transaction details to DB", Toast.LENGTH_SHORT)
                        .show()
                }
                println("Inserted into SmsTag table with rowId: $rowId")
                finish()
            }
        }
    }
}
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
    lateinit var dateEt: EditText
    lateinit var amountEt: EditText
    lateinit var transactionReason: EditText
    lateinit var tagSpinner: Spinner
    lateinit var isCreditCardSpinner: Spinner
    lateinit var deleteBt: Button
    lateinit var addBt: Button
    lateinit var headingTv: TextView
    lateinit var writeableDb: SQLiteDatabase
    lateinit var readableDb: SQLiteDatabase
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

    private fun updateFlow(rowId: Int) {
        headingTv.text = "Update your Transaction"

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
                    val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 0) + "/" + year)
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

        val query = "SELECT * FROM ${SmsTag.TABLE_NAME} WHERE ${BaseColumns._ID}=$rowId"
        val cursor = readableDb.rawQuery(query, null)

        with(cursor) {
            while (moveToNext()) {
                val day = getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_DAY))
                val month = getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_MONTH))
                val year = getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_YEAR))
                val amount =
                    getDouble(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_UPDATED_AMOUNT))
                val reason = getString(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_EXPLANATION))
                val tag = getString(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_TAG))
                val isCreditCard =
                    getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_IS_CREDIT_CARD))

                dateEt.text = "${day}/${month}/${year}".toEditable()
                amountEt.text = amount.toString().toEditable()
                transactionReason.text = reason.toString().toEditable()

            }
        }
        cursor.close()

        deleteBt.setOnClickListener {
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
                val day_ = date.substring(0, date.indexOf('/')).toInt()
                val month_ = date.substring(date.indexOf('/') + 1, date.lastIndexOf('/')).toInt()
                val year_ = date.substring(date.lastIndexOf('/') + 1).toInt()
                val amount_ = amountEt.text.toString().toDouble()
                val reason_ = transactionReason.text.toString()
                val tag_ = tagSpinner.selectedItem.toString()
                val isCreditCard_ =
                    if (isCreditCardSpinner.selectedItem.toString() == "yes") 1 else 0

                val query_ = "UPDATE ${SmsTag.TABLE_NAME} SET (" +
                        "${SmsTag.COLUMN_NAME_DAY} = $day_," +
                        "${SmsTag.COLUMN_NAME_MONTH} = $month_," +
                        "${SmsTag.COLUMN_NAME_YEAR} = $year_," +
                        "${SmsTag.COLUMN_NAME_UPDATED_AMOUNT} = $amount_," +
                        "${SmsTag.COLUMN_NAME_EXPLANATION} = $reason_," +
                        "${SmsTag.COLUMN_NAME_TAG} = $tag_," +
                        "${SmsTag.COLUMN_NAME_IS_CREDIT_CARD} = $isCreditCard_ " +
                        ") WHERE ${BaseColumns._ID}=$rowId"

                val c = writeableDb.rawQuery(query_, null)
                c.close()
                finish()
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
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
                    val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 0) + "/" + year)
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
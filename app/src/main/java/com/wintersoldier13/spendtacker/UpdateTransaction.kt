package com.wintersoldier13.spendtacker

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.wintersoldier13.spendtacker.data.KnownTags
import com.wintersoldier13.spendtacker.schema.SmsTag
import java.util.Calendar

class UpdateTransaction : AppCompatActivity() {
    private lateinit var dateEt: EditText
    private lateinit var amountEt: EditText
    private lateinit var transactionReasonEt: EditText
    private lateinit var tagSpinner: Spinner
    private lateinit var isCreditCardSpinner: Spinner
    private lateinit var deleteBt: Button
    private lateinit var addBt: Button
    private lateinit var headingTv: TextView
    private lateinit var writeableDb: SQLiteDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_transaction)


        dateEt = findViewById(R.id.update_transaction_edittext_date)
        amountEt = findViewById(R.id.update_transaction_edittext_transaction_amount)
        transactionReasonEt = findViewById(R.id.update_transaction_edittext_transaction_explanation)
        tagSpinner = findViewById(R.id.update_transaction_spinner_tag)
        isCreditCardSpinner = findViewById(R.id.update_transaction_spinner_credit_card)
        deleteBt = findViewById(R.id.update_transaction_button_delete)
        addBt = findViewById(R.id.update_transaction_button_add)
        headingTv = findViewById(R.id.update_transaction_textview_heading)
        writeableDb = DatabaseHelper(this).writableDatabase

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
        val tagSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, KnownTags.getTags())
        tagSpinner.adapter = tagSpinnerAdapter


//        get the data from the intent that called this activity
        val date = intent.extras?.getString("date")
        val amount = intent.extras?.getDouble("amount")?.toInt()
        val transactionReason = intent.extras?.getString("transaction_reason")
        val transactionTag = intent.extras?.getString("tag")
        val isCC = intent.extras?.getString("isCC")
        val smsId = intent.extras?.getInt("row_id")

//        populate fields with old data except the spinners
        dateEt.text = date!!.toEditable()
        amountEt.text = amount.toString().toEditable()
        transactionReasonEt.text = transactionReason.toString().toEditable()
        tagSpinner.setSelection(tagSpinnerAdapter.getPosition(transactionTag))
        isCreditCardSpinner.setSelection(arrayAdapter.getPosition(isCC))


        deleteBt.setOnClickListener {
//           delete the entry from the db
            try {
                writeableDb.delete(SmsTag.TABLE_NAME, "${BaseColumns._ID}=$smsId", null)
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Something went wrong while deleting", Toast.LENGTH_SHORT)
                    .show()
            }
        }


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

        addBt.setOnClickListener {

            if (dateEt.text.toString() == "") {
                Toast.makeText(this, "enter date", Toast.LENGTH_SHORT).show()
            } else if (amountEt.text.toString() == "" || amountEt.text.toString().toInt() == 0) {
                Toast.makeText(this, "amount cannot be null/zero", Toast.LENGTH_SHORT).show()
            } else if (transactionReasonEt.text.toString() == "") {
                Toast.makeText(this, "Enter a transaction explanation", Toast.LENGTH_SHORT).show()
            } else {
                val date = dateEt.text.toString()
                val day = date.substring(0, date.indexOf('/')).toInt()
                val month = date.substring(date.indexOf('/') + 1, date.lastIndexOf('/')).toInt()
                val year = date.substring(date.lastIndexOf('/') + 1).toInt()
                val amount = amountEt.text.toString().toDouble()
                val reason = transactionReasonEt.text.toString()
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

                writeableDb.update(SmsTag.TABLE_NAME, values, "${BaseColumns._ID}=$smsId", null)

                finish()
            }
        }

    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
package com.wintersoldier13.spendtacker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.Telephony.Sms
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wintersoldier13.spendtacker.schema.SmsTag

class AllExpensesActivity : AppCompatActivity() {
    lateinit var recyclerViewExpenses: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_expenses)

        recyclerViewExpenses =
            findViewById(R.id.all_expenses_recycler_view_list)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewExpenses.layoutManager = linearLayoutManager
//        get the untracked expenses from the database
        try {
            val adapter = AllExpensesRecyclerViewCustomAdapter(getTaggedSms())
            recyclerViewExpenses.adapter = adapter
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerViewExpenses =
            findViewById(R.id.all_expenses_recycler_view_list)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewExpenses.layoutManager = linearLayoutManager
//        get the untracked expenses from the database
        try {
            val adapter = AllExpensesRecyclerViewCustomAdapter(getTaggedSms())
            recyclerViewExpenses.adapter = adapter
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    private fun getTaggedSms(): ArrayList<ExpenseItem> {
        val expenses = ArrayList<ExpenseItem>();
        val db = DatabaseHelper(this).readableDatabase
        val query = "SELECT * FROM ${SmsTag.TABLE_NAME} " +
        "ORDER BY ${SmsTag.COLUMN_NAME_YEAR} DESC, ${SmsTag.COLUMN_NAME_MONTH} DESC, ${SmsTag.COLUMN_NAME_DAY} DESC"

        println(query)
        val cursor = db.rawQuery(query, null)

        with(cursor) {
            while (moveToNext()) {
                val day = getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_DAY))
                val month = getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_MONTH))
                val year = getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_YEAR))
                val transactionReason =
                    getString(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_EXPLANATION))
                val amount =
                    getDouble(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_UPDATED_AMOUNT))
                val tag = getString(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_TAG))
                val isCreditCardExpense =
                    getInt(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_IS_CREDIT_CARD))
                val id = getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))

                expenses.add(
                    ExpenseItem(
                        day,
                        month,
                        year,
                        transactionReason,
                        amount,
                        tag,
                        isCreditCardExpense,
                        id
                    )
                )
            }
        }
        cursor.close()
        return expenses
    }

    class ExpenseItem(
        var day: Int,
        var month: Int,
        var year: Int,
        var transactionReason: String,
        var amount: Double,
        var tag: String,
        var isCreditCardExpense: Int,
        var id: Int
    )
}
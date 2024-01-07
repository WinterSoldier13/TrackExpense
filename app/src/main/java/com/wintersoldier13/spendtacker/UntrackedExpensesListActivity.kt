package com.wintersoldier13.spendtacker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wintersoldier13.spendtacker.schema.FilteredSms
import com.wintersoldier13.spendtacker.schema.SmsTag

class UntrackedExpensesListActivity : AppCompatActivity() {
    private lateinit var recyclerViewUntrackedExpenses: RecyclerView;
    private lateinit var adapter: UntaggedSmsRecyclerViewCustomAdapter;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_untracked_expenses_list)

        recyclerViewUntrackedExpenses =
            findViewById(R.id.untracked_expenses_recyclerview_item_list)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewUntrackedExpenses.layoutManager = linearLayoutManager
//        get the untracked expenses from the database
        try {
            val untrackedExpenses = getUntrackedExpenses()
            adapter = UntaggedSmsRecyclerViewCustomAdapter(untrackedExpenses)
            recyclerViewUntrackedExpenses.adapter = adapter
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    override fun onResume() {
        super.onResume()

        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewUntrackedExpenses.layoutManager = linearLayoutManager
//        get the untracked expenses from the database
        try {
            val untrackedExpenses = getUntrackedExpenses()
            adapter = UntaggedSmsRecyclerViewCustomAdapter(untrackedExpenses)
            recyclerViewUntrackedExpenses.adapter = adapter
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    private fun getUntrackedExpenses(): ArrayList<filteredOutSms> {
        println(">>>>>>>> Extracting UNTAGGED SMS")
        val db = DatabaseHelper(this).readableDatabase
        val query: String = "SELECT *" +
                " FROM " +
                "${FilteredSms.TABLE_NAME} " +
                "WHERE " +
                "${FilteredSms.TABLE_NAME}._id " +
                "NOT IN " +
                "(SELECT ${SmsTag.COLUMN_NAME_SMS_ID} FROM ${SmsTag.TABLE_NAME} )";
        println(query)

        val cursor = db.rawQuery(query, null);
        val filteredOutSmsArray = ArrayList<filteredOutSms>();
        with(cursor) {
            while (moveToNext()) {
                println("Somthing in the way!<><><><><><><><>")
                val day = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_DAY))
                val month = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_MONTH))
                val year = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_YEAR))
                val body = getString(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_BODY))
                val amount = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_AMOUNT))
                val smsId = getInt(getColumnIndexOrThrow("_id"))

                filteredOutSmsArray.add(filteredOutSms(day, month, year, body, amount, smsId))
            }
        }
        cursor.close();
        return filteredOutSmsArray;
    }

    class filteredOutSms(
        val day: Int,
        val month: Int,
        val year: Int,
        val body: String,
        val amount: Int,
        val sms_id: Int
    ) {
        companion object {
//            TODO : not required?
        }
    }
}
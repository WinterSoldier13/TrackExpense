package com.wintersoldier13.spendtacker

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.wintersoldier13.spendtacker.databinding.UntrackedExpensesBinding
import com.wintersoldier13.spendtacker.schema.FilteredSms
import com.wintersoldier13.spendtacker.schema.SmsTag

class UntrackedExpensesActivity : AppCompatActivity() {
    private lateinit var binding: UntrackedExpensesBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = UntrackedExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerViewUntrackedExpenses: RecyclerView =
            findViewById(R.id.untracked_expenses_recyclerview_item_list)
//        get the untracked expenses from the database
        val untrackedExpenses = getUntrackedExpenses()
        recyclerViewUntrackedExpenses.adapter =
            UntaggedSmsRecyclerViewCustomAdapter(untrackedExpenses)
    }

    private fun getUntrackedExpenses(): ArrayList<filteredOutSms> {
        val db = DatabaseHelper(this).readableDatabase
        val query: String = "SELECT " +
                "${FilteredSms.COLUMN_NAME_DAY}, " +
                "${FilteredSms.COLUMN_NAME_MONTH}, " +
                "${FilteredSms.COLUMN_NAME_YEAR}, " +
                "${FilteredSms.COLUMN_NAME_BODY}, " +
                "${FilteredSms.COLUMN_NAME_AMOUNT} " +
                " FROM " +
                "${FilteredSms.TABLE_NAME} " +
                "WHERE " +
                "${FilteredSms.TABLE_NAME}.ID " +
                "NOT IN " +
                "${SmsTag.TABLE_NAME}.${SmsTag.COLUMN_NAME_SMS_ID} ";

        val cursor = db.rawQuery(query, null);
        val filteredOutSmsArray = ArrayList<filteredOutSms>();
        with(cursor) {
            while (moveToNext()) {
                val day = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_DAY))
                val month = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_MONTH))
                val year = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_YEAR))
                val body = getString(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_BODY))
                val amount = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_AMOUNT))

                filteredOutSmsArray.add(filteredOutSms(day, month, year, body, amount))
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
        val amount: Int
    ) {
        companion object {
//            TODO : not required?
        }
    }
}
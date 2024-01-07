package com.wintersoldier13.spendtacker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AllExpensesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_expenses)
    }

    class ExpenseItem(
        var date : String,
        var transactionReason: String,
        var amount: Double,
        var tag: String,
        var isCreditCardExpense: Boolean
    )
}
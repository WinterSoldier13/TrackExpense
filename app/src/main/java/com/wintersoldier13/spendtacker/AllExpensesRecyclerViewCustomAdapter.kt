package com.wintersoldier13.spendtacker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class AllExpensesRecyclerViewCustomAdapter(
    private val dataSet: ArrayList<AllExpensesActivity.ExpenseItem>
) :
    RecyclerView.Adapter<AllExpensesRecyclerViewCustomAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val expenseDateTv: TextView
        val expenseReasonTv: TextView
        val amountTv: TextView
        val tagTv: TextView
        val isCreditCardTv: TextView

        init {
            // Define click listener for the ViewHolder's View
            expenseDateTv = view.findViewById(R.id.expense_item_textview_date)
            expenseReasonTv = view.findViewById(R.id.expense_item_textview_transaction_reason)
            amountTv = view.findViewById(R.id.expense_item_textview_amount)
            tagTv = view.findViewById(R.id.expense_item_textview_tag)
            isCreditCardTv = view.findViewById(R.id.expense_item_textview_is_credit_card)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.expense_item, viewGroup, false)

        return ViewHolder(view)
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        val date = "${dataSet[position].day}/${dataSet[position].month}/${dataSet[position].year}"
        viewHolder.expenseDateTv.text = date;
        viewHolder.expenseReasonTv.text = dataSet[position].transactionReason;
        viewHolder.amountTv.text = dataSet[position].amount.toString()
        viewHolder.tagTv.text = dataSet[position].tag
        viewHolder.isCreditCardTv.text =
            if (dataSet[position].isCreditCardExpense == 1) "yes" else "no"

        viewHolder.itemView.setOnClickListener {
            val updateTransactionIntent =
                Intent(viewHolder.itemView.context, UpdateTransaction::class.java)
            updateTransactionIntent.putExtra("date", date)
            updateTransactionIntent.putExtra("amount", dataSet[position].amount)
            updateTransactionIntent.putExtra(
                "transaction_reason",
                dataSet[position].transactionReason
            )
            updateTransactionIntent.putExtra("tag", dataSet[position].tag)
            updateTransactionIntent.putExtra(
                "isCC",
                if (dataSet[position].isCreditCardExpense == 1) "yes" else "no"
            )
            updateTransactionIntent.putExtra("row_id", dataSet[position].id)

            viewHolder.itemView.context.startActivity(updateTransactionIntent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataSet.size
    }

}
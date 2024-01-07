package com.wintersoldier13.spendtacker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UntaggedSmsRecyclerViewCustomAdapter(
    private val dataSet: ArrayList<UntrackedExpensesListActivity.filteredOutSms>
) :
    RecyclerView.Adapter<UntaggedSmsRecyclerViewCustomAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val untrackedExpenseDate: TextView
        val untrackedExpenseBody: TextView
        val untrackedExpenseAmount: TextView

        init {
            // Define click listener for the ViewHolder's View
            untrackedExpenseDate = view.findViewById(R.id.untracked_expense_item_textview_date)
            untrackedExpenseBody = view.findViewById(R.id.untracked_expense_item_textview_sms_body)
            untrackedExpenseAmount = view.findViewById(R.id.untracked_expense_item_textview_amount)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.untracked_expense_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val date: String =
            "${dataSet[position].day}/${dataSet[position].month}/${dataSet[position].year}"

        println("Setting onBindViewHolder $date")
        viewHolder.untrackedExpenseDate.text = date
        viewHolder.untrackedExpenseBody.text = dataSet[position].body
        viewHolder.untrackedExpenseAmount.text = "Rs. ${dataSet[position].amount}"

        viewHolder.itemView.setOnClickListener {
            val intent: Intent = Intent(viewHolder.itemView.context, AssignTagActivity::class.java)
            intent.putExtra("sms_id", dataSet[position].sms_id)

            viewHolder.itemView.context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        println(">>>> Dataset size: ${dataSet.size}")
        return dataSet.size
    }

}
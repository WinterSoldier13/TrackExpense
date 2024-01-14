package com.wintersoldier13.spendtacker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Sms
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.wintersoldier13.spendtacker.databinding.ActivityMainBinding
import com.wintersoldier13.spendtacker.schema.SmsTag
import java.lang.reflect.Array.getDouble
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewAllExpense: Button
    private lateinit var viewExpenseByMonth: Button
    private lateinit var viewUntaggedExpense: Button
    private lateinit var recordTransactionBt: Button
    private lateinit var pieChart: PieChart
    private lateinit var readableDb: SQLiteDatabase
    private lateinit var todayExpenditureTv: TextView
    private lateinit var monthExpenditureTv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dexOutputDir = codeCacheDir
        dexOutputDir.setReadOnly()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewAllExpense = findViewById(R.id.homepage_button_view_all_expenses)
        recordTransactionBt = findViewById(R.id.homepage_button_record_transaction)
        viewExpenseByMonth = findViewById(R.id.homepage_button_view_monthly_expense)
        viewUntaggedExpense = findViewById(R.id.homepage_button_view_untagged_expenses)
        pieChart = findViewById(R.id.homepage_piechart_monthly_expense)
        todayExpenditureTv = findViewById(R.id.homepage_textview_spent_today)
        monthExpenditureTv = findViewById(R.id.homepage_textview_spent_this_month)
        readableDb = DatabaseHelper(this).readableDatabase

        val untaggedExpenseActivityIntent: Intent =
            Intent(this, UntrackedExpensesListActivity::class.java)
        val allExpensesActivityIntent = Intent(this, AllExpensesActivity::class.java)
        val recordTransactionIntent = Intent(this, RecordTransactionActivity::class.java)

        viewUntaggedExpense.setOnClickListener {
            startActivity(untaggedExpenseActivityIntent)
        }

        viewAllExpense.setOnClickListener {
            startActivity(allExpensesActivityIntent)
        }

        recordTransactionBt.setOnClickListener {
            startActivity(recordTransactionIntent)
        }

        todayExpenditureTv.text = getExpenditureToday().toString()
        monthExpenditureTv.text = getMonthlyExpenditure().toString()
//        setup the pie-chart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.dragDecelerationFrictionCoef = 0.95f

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.BLACK)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f

        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        pieChart.animateY(1400, Easing.EaseInOutQuad)

        pieChart.legend.isEnabled = true
        pieChart.legend.textColor = Color.WHITE
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        val entries: ArrayList<PieEntry> = getPieChartData()
        val dataset = PieDataSet(entries, "Expenditure")

        dataset.setDrawIcons(false)
        dataset.sliceSpace = 3f
        dataset.iconsOffset = MPPointF(0f, 40f)
        dataset.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor(R.color.teal_700))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.button_color))
        colors.add(resources.getColor(R.color.purple_500))

        dataset.colors = colors

        val data = PieData(dataset)
        dataset.valueFormatter = PercentFormatter()
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        pieChart.highlightValue(null)
        pieChart.invalidate()

    }

    private fun getExpenditureToday(): Double {
        val date = LocalDate.now()
        val day = date.dayOfMonth
        val month = date.monthValue
        val year = date.year

        val query =
            "SELECT SUM(${SmsTag.COLUMN_NAME_UPDATED_AMOUNT}) AS total_amount FROM ${SmsTag.TABLE_NAME} " +
                    "WHERE ${SmsTag.COLUMN_NAME_DAY}=$day AND ${SmsTag.COLUMN_NAME_MONTH}=$month AND ${SmsTag.COLUMN_NAME_YEAR}=$year"

        val cursor = readableDb.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val amount = cursor.getDouble(0)
            cursor.close()
            return amount
        }
        cursor.close()
        return 0.0

    }

    private fun getMonthlyExpenditure(): Double {
        val date = LocalDate.now()
        val month = date.monthValue
        val year = date.year

        val query =
            "SELECT SUM(${SmsTag.COLUMN_NAME_UPDATED_AMOUNT}) AS total_amount FROM ${SmsTag.TABLE_NAME} " +
                    "WHERE ${SmsTag.COLUMN_NAME_MONTH}=$month AND ${SmsTag.COLUMN_NAME_YEAR}=$year"

        val cursor = readableDb.rawQuery(query, null)

        var amount: Double = 0.0;
        if (cursor.moveToFirst()) {
            amount = cursor.getDouble(0)
        }

        cursor.close()
        return amount
    }

    private fun getPieChartData(): ArrayList<PieEntry> {
        val entries: ArrayList<PieEntry> = ArrayList()
        val currentMonth: Int = LocalDate.now().monthValue
        val currentYear = LocalDate.now().year

        val query =
            "SELECT ${SmsTag.COLUMN_NAME_TAG}, SUM(${SmsTag.COLUMN_NAME_UPDATED_AMOUNT}) AS total_cost " +
                    "FROM ${SmsTag.TABLE_NAME} " +
                    "WHERE ${SmsTag.COLUMN_NAME_MONTH}=$currentMonth AND ${SmsTag.COLUMN_NAME_YEAR}=$currentYear " +
                    "GROUP BY ${SmsTag.COLUMN_NAME_TAG} " +
                    "ORDER BY total_cost DESC LIMIT 4";

        val cursor = readableDb.rawQuery(query, null)
        val totalMonthExpenditure = getMonthlyExpenditure()
        var acc: Double = 0.0

        with(cursor) {
            while (moveToNext()) {
                val tag = getString(cursor.getColumnIndexOrThrow(SmsTag.COLUMN_NAME_TAG))
                val amount = getDouble(cursor.getColumnIndexOrThrow("total_cost"))
                acc += amount

                entries.add(PieEntry(amount.toFloat(), tag))
            }
        }

        entries.add(PieEntry((totalMonthExpenditure - acc).toFloat(), "others"))
        cursor.close()
        return entries
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}
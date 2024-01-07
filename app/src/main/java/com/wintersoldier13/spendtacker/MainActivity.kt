package com.wintersoldier13.spendtacker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.wintersoldier13.spendtacker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewAllExpense: Button
    private lateinit var viewExpenseByMonth: Button
    private lateinit var viewUntaggedExpense: Button
    private lateinit var viewActiveRegex: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dexOutputDir = codeCacheDir
        dexOutputDir.setReadOnly()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewAllExpense = findViewById(R.id.homepage_button_view_all_expenses)
        viewExpenseByMonth = findViewById(R.id.homepage_button_view_monthly_expense)
        viewUntaggedExpense = findViewById(R.id.homepage_button_view_untagged_expenses)
        viewActiveRegex = findViewById(R.id.homepage_button_view_active_regex)

        val untaggedExpenseActivityIntent: Intent =
            Intent(this, UntrackedExpensesListActivity::class.java)

        viewUntaggedExpense.setOnClickListener {
            startActivity(untaggedExpenseActivityIntent)
        }
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
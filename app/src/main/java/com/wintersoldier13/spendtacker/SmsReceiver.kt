package com.wintersoldier13.spendtacker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import kotlin.random.Random;
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.wintersoldier13.spendtacker.schema.FilteredSms
import java.time.LocalDate

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent?.action) {
            var body = ""
            val currentDate = LocalDate.now()
            val date = currentDate.dayOfMonth
            val month = currentDate.monthValue
            val year = currentDate.year
            var sender = "";

            for (smsMessage: SmsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
//                TODO : Filter out only the transaction messages
                body += smsMessage.messageBody
                sender = smsMessage.originatingAddress!!;
            }
//                TODO : Extract the amount
                val amount = Random.nextInt(100, 1000)
                val db = DatabaseHelper(context!!).writableDatabase

                val values = ContentValues().apply {
                    put(FilteredSms.COLUMN_NAME_DAY, date)
                    put(FilteredSms.COLUMN_NAME_MONTH, month)
                    put(FilteredSms.COLUMN_NAME_YEAR, year)
                    put(FilteredSms.COLUMN_NAME_SENDER, sender)
                    put(FilteredSms.COLUMN_NAME_BODY, body)
                    put(FilteredSms.COLUMN_NAME_AMOUNT, amount)
                }

//                insert a new row into the database
                val newRowId = db?.insert(FilteredSms.TABLE_NAME, null, values)
                println("INFO: >><><><><><><>> Inserted with $newRowId ")
//                Notify the user to add information about the transaction
                val notificationManager = createNotificationChannel(context)
                val notification = NotificationCompat.Builder(context, "SpendTrackerNotification")
                    .setContentTitle("Woah! is that money that I smell")
                    .setContentText("So you burning money again, anyway update info NOW")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()
                notificationManager.notify(1, notification)

        }
    }

    private fun createNotificationChannel(context: Context?): NotificationManager {
        // Create the NotificationChannel.
        val name = "SpendTrackerNotificationChannel"
        val descriptionText = "Notification Channel for the SpendTracker app"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel("SpendTrackerNotification", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
        return notificationManager;
    }

    fun readDb(context: Context?) {
        val db = DatabaseHelper(context!!).readableDatabase;
        val projection = arrayOf(
            BaseColumns._ID,
            FilteredSms.COLUMN_NAME_DAY,
            FilteredSms.COLUMN_NAME_SENDER,
            FilteredSms.COLUMN_NAME_BODY
        );

        val cursor = db.query(FilteredSms.TABLE_NAME, projection, null, null, null, null, null);

        with(cursor) {
            while (moveToNext()) {
                val body = getString(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_BODY))
                val sender = getString(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_SENDER))
                val date_ = getInt(getColumnIndexOrThrow(FilteredSms.COLUMN_NAME_DAY))

                println("READING>>?>?>?<><><><><><>")
                println("$body $sender $date_")
                println("<><><><>")
            }
        }
        cursor.close()
    }
}
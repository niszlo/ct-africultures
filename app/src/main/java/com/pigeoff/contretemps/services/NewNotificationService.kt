package com.pigeoff.contretemps.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.pigeoff.contretemps.R
import com.pigeoff.contretemps.activities.PostActivity
import com.pigeoff.contretemps.client.HTTPClient
import com.pigeoff.contretemps.client.HTTPInterface
import com.pigeoff.contretemps.client.JSONPost
import com.pigeoff.contretemps.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class NewNotificationService : Service() {
    lateinit var service: HTTPClient

    val PREF_ID = "prefid"
    val CHANNEL_ID = "channelnewnotif"

    override fun onCreate() {
        super.onCreate()
        service = HTTPClient()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            val newArticle = checkForNewArticle()
            withContext(Dispatchers.Main) {
                if (newArticle != null) {
                    showNotification(newArticle)
                }
                stopSelf()
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
        alarm.set(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000*60*60*2),
            PendingIntent.getService(this, 0, Intent(this, NewNotificationService::class.java), 0)
        )
    }

    private suspend fun checkForNewArticle() : JSONPost? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this);
        val lastId = pref.getInt(PREF_ID, 0)
        val lastArticle: ArrayList<JSONPost> = service.getLastNewPost()

        return if (lastArticle.count() > 0) {
            if (lastArticle.first().id != lastId) {
                lastArticle.first()
            }
            else {
                null
            }
        } else {
            null
        }
    }

    private fun showNotification(post: JSONPost) : JSONPost? {
        createNotificationChannel()
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra(Util.ACTION_ID, post.id)
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(post.title["rendered"])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        TODO()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notif_channel_title)
            val descriptionText = getString(R.string.notif_channel_str)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
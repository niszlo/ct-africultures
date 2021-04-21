package com.pigeoff.contretemps.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NewNotificationBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, NewNotificationService::class.java))
    }
}
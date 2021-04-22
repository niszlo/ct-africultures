package com.pigeoff.africultures

import android.app.Application
import com.pigeoff.africultures.client.HTTPClient

class AfriApp : Application() {
    lateinit var clientHttp: HTTPClient

    override fun onCreate() {
        super.onCreate()
        clientHttp = HTTPClient()
    }

    fun getHTTPClient() : HTTPClient {
        return clientHttp
    }
}
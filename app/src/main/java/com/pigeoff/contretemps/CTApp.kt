package com.pigeoff.contretemps

import android.app.Application
import com.pigeoff.contretemps.client.HTTPClient

class CTApp : Application() {
    lateinit var clientHttp: HTTPClient

    override fun onCreate() {
        super.onCreate()
        clientHttp = HTTPClient()
    }

    fun getHTTPClient() : HTTPClient {
        return clientHttp
    }
}
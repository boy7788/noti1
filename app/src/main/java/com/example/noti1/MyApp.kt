package com.example.noti1
import android.app.Application
import android.content.Context

class MyApp : Application() {
    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        MyStore.startStore(applicationContext)
    }

    companion object {
        private lateinit var instance: MyApp

        fun getAppContext(): Context = instance.applicationContext
    }
}

package com.workfort.thinkndraw

import android.content.Context
import androidx.multidex.MultiDexApplication

class ThinknDrawApp: MultiDexApplication() {

    init {
        sInstance = this
    }

    companion object {
        private lateinit var sInstance: ThinknDrawApp

        fun getApplicationContext(): Context {
            return sInstance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}
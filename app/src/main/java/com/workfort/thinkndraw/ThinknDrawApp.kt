package com.workfort.thinkndraw

import android.content.Context
import androidx.multidex.MultiDexApplication
import timber.log.Timber

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

        plantTimber()
    }

    private fun plantTimber() {

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return super.createStackElementTag(element) +
                            " - Method:${element.methodName} - Line:${element.lineNumber}"
                }
            })
            return
        }
    }
}
package com.workfort.thinkndraw.util.helper

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.workfort.thinkndraw.ThinknDrawApp
import java.text.SimpleDateFormat
import java.util.*

object AndroidUtil {

    fun vibrate() {
        val vibrator = ThinknDrawApp.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 150), -1)
        }
    }

    fun format(timeInMills: Long, format: String = "mm:ss:SSS"): String {

        return SimpleDateFormat(format, Locale.getDefault()).format(Date(timeInMills))
    }

}
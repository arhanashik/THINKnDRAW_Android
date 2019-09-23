package com.workfort.thinkndraw.util.helper

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.Ringtone
import com.workfort.thinkndraw.ThinknDrawApp


object MediaPlayerUtil {
    private var mPlayer: MediaPlayer? = null

    fun play(context: Context, rawFile: Int) {
        mPlayer?.reset()

        mPlayer = MediaPlayer.create(context, rawFile)
        mPlayer?.start()
        mPlayer?.setOnCompletionListener { mPlayer?.reset() }
    }

    fun playNotification() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(ThinknDrawApp.getApplicationContext(), notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
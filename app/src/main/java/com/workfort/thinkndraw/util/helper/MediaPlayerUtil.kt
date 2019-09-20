package com.workfort.thinkndraw.util.helper

import android.content.Context
import android.media.MediaPlayer


object MediaPlayerUtil {
    private var mPlayer: MediaPlayer? = null

    fun play(context: Context, rawFile: Int) {
        mPlayer?.reset()

        mPlayer = MediaPlayer.create(context, rawFile)
        mPlayer?.start()
        mPlayer?.setOnCompletionListener { mPlayer?.reset() }
    }
}
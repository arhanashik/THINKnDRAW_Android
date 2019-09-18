package com.workfort.thinkndraw.util.helper

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object ImageUtil {

    @Suppress("DEPRECATION")
    fun saveBitmap(bitmap: Bitmap) {
        val path = Environment.getExternalStorageDirectory().absolutePath
        val file = File("$path/THINKnDRAW.png")
        try {
            val outStream = FileOutputStream(file)
            file.createNewFile()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
            outStream.flush()
            outStream.close()
            Log.d("Save Bitmap", "image saved")
        } catch (ex: Exception) {
            Log.d("Save Bitmap", "error")
            ex.printStackTrace()
        }
    }
}
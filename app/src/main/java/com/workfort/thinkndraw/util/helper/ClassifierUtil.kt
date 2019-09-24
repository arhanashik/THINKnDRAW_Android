package com.workfort.thinkndraw.util.helper

import android.graphics.Bitmap
import android.os.SystemClock
import com.workfort.thinkndraw.ThinknDrawApp
import com.workfort.thinkndraw.app.data.local.result.Result
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ClassifierUtil @Throws(IOException::class) constructor(
    model: String, classNum: Int, batchSize: Int = BATCH_SIZE,
    imgWidth: Int = IMG_WIDTH, imgHeight: Int = IMG_HEIGHT,
    channel: Int = NUM_CHANNEL
) {

    private val options = Interpreter.Options()
    private val mInterpreter: Interpreter
    private val mImageData: ByteBuffer?
    private val mImagePixels = IntArray(imgWidth * imgHeight)
    private val mResult = Array(1) { FloatArray(classNum) }

    init {
        mInterpreter = Interpreter(loadModelFile(model), options)
        mImageData = ByteBuffer.allocateDirect(
            4 * batchSize * imgHeight * imgWidth * channel
        )

        mImageData?.order(ByteOrder.nativeOrder())
    }

    fun classify(bitmap: Bitmap): Result {
        convertBitmapToByteBuffer(bitmap)
        val startTime = SystemClock.uptimeMillis()
        mInterpreter.run(mImageData!!, mResult)
        val endTime = SystemClock.uptimeMillis()
        val timeCost = endTime - startTime

        val msg = "classify(): result = " + mResult[0].contentToString() + ", timeCost = " + timeCost
        Timber.v(msg)

        return Result(mResult[0], timeCost)
    }

    @Throws(IOException::class)
    private fun loadModelFile(model: String): MappedByteBuffer {
        val context = ThinknDrawApp.getApplicationContext()
        val fileDescriptor = context.assets.openFd(model)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(
        bitmap: Bitmap, imgWidth: Int = IMG_WIDTH, imgHeight: Int = IMG_HEIGHT
    ) {
        if (mImageData == null) {
            return
        }
        mImageData.rewind()

        bitmap.getPixels(
            mImagePixels, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )

        var pixel = 0
        for (i in 0 until imgWidth) {
            for (j in 0 until imgHeight) {
                val value = mImagePixels[pixel++]
                mImageData.putFloat(convertPixel(value))
            }
        }
    }

    private fun convertPixel(color: Int): Float {
        return (255 - ((color shr 16 and 0xFF) * 0.299f
                + (color shr 8 and 0xFF) * 0.587f
                + (color and 0xFF) * 0.114f)) / 255.0f
    }

    companion object {
        private val LOG_TAG = ClassifierUtil::class.java.simpleName

        const val MODEL_5_CLASS = "thinkndraw_5_classes98V3.tflite"
        const val MODEL_8_CLASS = "thinkndraw8class.tflite"

        private const val BATCH_SIZE = 1
        const val IMG_HEIGHT = 28
        const val IMG_WIDTH = 28
        const val NUM_CHANNEL = 1
        const val NUM_CLASSES_5 = 5
        const val NUM_CLASSES_8 = 8
        const val THRESH_HOLD = 0.7 //min 90% accuracy is required to be a right prediction
    }
}
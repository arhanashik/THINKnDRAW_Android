package com.workfort.thinkndraw.app.data.local.result

import androidx.core.util.forEach
import com.workfort.thinkndraw.app.data.local.constant.Const


class Result(probs: FloatArray, val timeCost: Long) {

    val number: Int
    val probability: Float
    val probabilityArr: FloatArray

    init {
        number = argMax(probs)
        probability = probs[number]
        probabilityArr = probs
    }

    private fun argMax(probs: FloatArray): Int {
        var maxIdx = -1
        var maxProb = 0.0f
        for (i in probs.indices) {
            if (probs[i] > maxProb) {
                maxProb = probs[i]
                maxIdx = i
            }
        }
        return maxIdx
    }

    fun className(): String {

        Const.CLASSIFICATION_CLASSES.forEach { classIndex, className ->
            if(classIndex == number) return className
        }

        return "Unknown"
    }
}
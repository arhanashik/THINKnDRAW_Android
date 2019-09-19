package com.workfort.thinkndraw.app.data.local.result

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
        return when(number) {
            Const.Classes.ICE_CREAM.first -> Const.Classes.ICE_CREAM.second
//            Const.Classes.CAT.first -> Const.Classes.CAT.second
            Const.Classes.SQUARE.first -> Const.Classes.SQUARE.second
            Const.Classes.APPLE.first -> Const.Classes.APPLE.second
            Const.Classes.CAR.first -> Const.Classes.CAR.second
            Const.Classes.BANANA.first -> Const.Classes.BANANA.second
            else -> "Unknown"
        }
    }
}
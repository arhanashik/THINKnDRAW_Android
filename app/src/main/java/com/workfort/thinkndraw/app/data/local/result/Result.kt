package com.workfort.thinkndraw.app.data.local.result


class Result(probs: FloatArray, val timeCost: Long) {

    val number: Int
    val probability: Float

    init {
        number = argMax(probs)
        probability = probs[number]
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
}
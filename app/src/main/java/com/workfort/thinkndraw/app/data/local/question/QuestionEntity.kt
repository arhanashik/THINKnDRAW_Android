package com.workfort.thinkndraw.app.data.local.question

import android.os.Parcel
import android.os.Parcelable

data class QuestionEntity (
    var id: Int = 0,
    var question: String = "",
    var questionType: Int = 0,
    var message: String = "",
    var options: ArrayList<String> = ArrayList(),
    var images: ArrayList<Int> = ArrayList(),
    var answer: Pair<Int, String>? = null
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?: "",
        parcel.readInt(),
        parcel.readString()?: "",
        ArrayList(),
        ArrayList(),
        null
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(question)
        parcel.writeInt(questionType)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionEntity> {
        override fun createFromParcel(parcel: Parcel): QuestionEntity {
            return QuestionEntity(parcel)
        }

        override fun newArray(size: Int): Array<QuestionEntity?> {
            return arrayOfNulls(size)
        }
    }
}
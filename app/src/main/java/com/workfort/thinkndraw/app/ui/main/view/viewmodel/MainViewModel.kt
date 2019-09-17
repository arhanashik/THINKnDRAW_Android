package com.workfort.thinkndraw.app.ui.main.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.question.QuestionEntity

class MainViewModel: ViewModel() {

    val mQuestionsLiveData = MutableLiveData<ArrayList<QuestionEntity>>()

    fun loadQuestions() {
        val questions = arrayListOf(
            QuestionEntity(
                1,
                "Can you guess the movie name?",
                Const.QuestionType.TYPE_B,
                "Draw the missing piece.(No letter :p)"
            )
        )

        mQuestionsLiveData.postValue(questions)
    }

}
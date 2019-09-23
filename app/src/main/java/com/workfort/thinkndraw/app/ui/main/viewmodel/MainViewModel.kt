package com.workfort.thinkndraw.app.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.question.QuestionEntity

class MainViewModel: ViewModel() {

    val mQuestionsLiveData = MutableLiveData<ArrayList<QuestionEntity>>()
    val mCurrentQuestionLiveData = MutableLiveData<QuestionEntity>()

    fun loadQuestions() {
        val questions = arrayListOf (
            QuestionEntity (
                1,
                "Can you guess the movie name from the picture? Hint: The picture is not a part of the movie :p",
                Const.QuestionType.TYPE_B,
                "Draw the missing piece.(No letter :p)",
                images = arrayListOf(R.drawable.img_what, R.drawable.img_war),
                answer = Const.Classes.BANANA
            ),
            QuestionEntity (
                2,
                "Which option matches the best with this picture?",
                Const.QuestionType.TYPE_C,
                "Draw the picture of the option",
                arrayListOf("Banana", "Alien", "Dragon"),
                arrayListOf(R.drawable.img_got_daenerys),
                Const.Classes.APPLE
            )
        )

        mQuestionsLiveData.postValue(questions)
    }

}
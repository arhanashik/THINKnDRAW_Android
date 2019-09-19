package com.workfort.thinkndraw.app.ui.quiz.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.question.QuestionEntity

class QuizViewModel: ViewModel() {

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
                "It's coming for you. What will you do?",
                Const.QuestionType.TYPE_C,
                "Draw your weapon before it really comes :p",
                arrayListOf("Offer Banana", "Offer dinner", "Run away in car"),
                arrayListOf(R.drawable.img_tiger),
                Const.Classes.CAR
            ),
            QuestionEntity (
                3,
                "Can you imagine the missing piece?",
                Const.QuestionType.TYPE_D,
                "Draw the missing piece",
                images = arrayListOf(R.drawable.img_newton, R.drawable.img_what, R.drawable.img_gravity),
                answer = Const.Classes.APPLE
            )
        )

        mQuestionsLiveData.postValue(questions)
    }

}
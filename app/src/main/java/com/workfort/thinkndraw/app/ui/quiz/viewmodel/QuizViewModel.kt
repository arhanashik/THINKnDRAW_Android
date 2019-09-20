package com.workfort.thinkndraw.app.ui.quiz.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.question.QuestionEntity
import kotlin.random.Random

class QuizViewModel: ViewModel() {

    val mQuestionsLiveData = MutableLiveData<ArrayList<QuestionEntity>>()
    val mCurrentQuestionLiveData = MutableLiveData<QuestionEntity>()
    var mCurrentStep = 0
    val mFinishQuizLiveData = MutableLiveData<Boolean>()

    val mCurrentChallengeLiveData = MutableLiveData<Pair<Int, String>>()

    fun loadQuestions() {
        val questions = arrayListOf (
            QuestionEntity (
                1,
                "Can you imagine the missing piece?",
                Const.QuestionType.TYPE_D,
                "Draw the missing piece",
                images = arrayListOf(R.drawable.img_newton, R.drawable.img_what, R.drawable.img_gravity),
                answer = Const.Classes.APPLE,
                successMessage = "Yes you found apple and I found the gravity! Let's post in facebook!",
                failureMessage = "Apple: Now you are responsible for the death of Newton!",
                successGif = R.drawable.img_newton_success,
                failureGif = R.drawable.img_newton_failure
            ),
            QuestionEntity (
                2,
                "It's coming for you. What will you do?",
                Const.QuestionType.TYPE_C,
                "Draw your weapon before it really comes :p",
                arrayListOf("Offer Banana", "Offer dinner", "Run away in car"),
                arrayListOf(R.drawable.img_tiger),
                Const.Classes.CAR,
                "Luckily your Car is fast enough! Careful next time!",
                "Tiger: Come to papa! I am gonna eat you!",
                R.drawable.img_tiger_success,
                R.drawable.img_tiger_failure
            ),
            QuestionEntity (
                3,
                "Which one he want's to eat?",
                Const.QuestionType.TYPE_C,
                "Draw that. Careful about wrong answer :p",
                arrayListOf("Human", "Alien", "Banana"),
                arrayListOf(R.drawable.img_minion),
                Const.Classes.BANANA,
                "Thanks for the food. I love banana.",
                "I don't need this! You are so...",
                R.drawable.img_minion_success,
                R.drawable.img_minion_failure
            )
        )

        mQuestionsLiveData.postValue(questions)
    }

    fun startQuiz(currentStep: Int = 0) {
        mQuestionsLiveData.value?.let { questions ->
            mCurrentStep = currentStep
            if(currentStep < questions.size) {
                mCurrentQuestionLiveData.postValue(questions[currentStep])
            } else {
                mFinishQuizLiveData.postValue(true)
            }
        }
    }

    fun selectChallenge(random: Boolean = true) {
        val randClass = when(Random.nextInt(0, 5)) {
            Const.Classes.ICE_CREAM.first -> Const.Classes.ICE_CREAM
            Const.Classes.SQUARE.first -> Const.Classes.SQUARE
            Const.Classes.APPLE.first -> Const.Classes.APPLE
            Const.Classes.CAR.first -> Const.Classes.CAR
            Const.Classes.BANANA.first -> Const.Classes.BANANA
            else -> Const.Classes.BANANA
        }

        mCurrentChallengeLiveData.postValue(randClass)
    }

}
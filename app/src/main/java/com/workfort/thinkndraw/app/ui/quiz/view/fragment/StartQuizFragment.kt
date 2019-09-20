package com.workfort.thinkndraw.app.ui.quiz.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.ui.quiz.viewmodel.QuizViewModel
import com.workfort.thinkndraw.databinding.FragmentStartQuizBinding

class StartQuizFragment: Fragment() {

    private lateinit var mBinding: FragmentStartQuizBinding

//    private val args: QuestionTypeBFragmentArgs by navArgs()

    private lateinit var mQuizViewModel: QuizViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start_quiz, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            mQuizViewModel = ViewModelProviders.of(it).get(QuizViewModel::class.java)
        }

        mBinding.tvCount.text = "3"
        val timer = object: CountDownTimer(4000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val untilStr = (millisUntilFinished / 1000).toString()
                mBinding.tvCount.text = untilStr
            }

            override fun onFinish() {
                mQuizViewModel.startQuiz()
            }
        }

        timer.start()
    }

}
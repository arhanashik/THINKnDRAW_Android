package com.workfort.thinkndraw.app.ui.quiz.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.question.QuestionEntity
import com.workfort.thinkndraw.app.ui.quiz.viewmodel.QuizViewModel
import com.workfort.thinkndraw.databinding.FragmentQuestionTypeDBinding
import com.workfort.thinkndraw.util.helper.ImageLoader

class QuestionTypeDFragment: Fragment() {

    private lateinit var mBinding: FragmentQuestionTypeDBinding

    private lateinit var mViewModel: QuizViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type_d, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(QuizViewModel::class.java)
        }

        val question = arguments?.getParcelable<QuestionEntity>(Const.Params.QUESTION)

        question?.let {
            mBinding.tvQuestion.text = it.question

            ImageLoader.load(it.images[0], mBinding.img1)
            ImageLoader.loadGif(it.images[1], mBinding.img2)
            ImageLoader.load(it.images[2], mBinding.img3)

            mBinding.tvMessage.text = it.message
        }
    }

}
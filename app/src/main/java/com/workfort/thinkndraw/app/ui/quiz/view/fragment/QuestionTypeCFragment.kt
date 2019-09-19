package com.workfort.thinkndraw.app.ui.quiz.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.question.QuestionEntity
import com.workfort.thinkndraw.databinding.FragmentQuestionTypeCBinding

class QuestionTypeCFragment: Fragment() {

    private lateinit var mBinding: FragmentQuestionTypeCBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type_c, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val question = arguments?.getParcelable<QuestionEntity>(Const.Params.QUESTION)

        question?.let {
            mBinding.tvQuestion.text = it.question

            Glide.with(this)
                .load(it.images[0])
                .into(mBinding.img1)

            mBinding.tvOption1.text = it.options[0]
            mBinding.tvOption2.text = it.options[1]
            mBinding.tvOption3.text = it.options[2]

            mBinding.tvMessage.text = it.message
        }
    }

}
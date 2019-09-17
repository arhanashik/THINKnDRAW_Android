package com.workfort.thinkndraw.app.ui.main.view.fragment

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
import com.workfort.thinkndraw.databinding.FragmentQuestionTypeBBinding

class FragmentQuestionTypeB: Fragment() {

    private lateinit var mBinding: FragmentQuestionTypeBBinding

//    private val navArgs: FragmentQuestionTypeBArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_type_b, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val question = arguments?.getParcelable<QuestionEntity>(Const.Params.QUESTION)

        question?.let {
            mBinding.tvQuestion.text = it.question

            Glide.with(this)
                .asGif()
                .load(R.drawable.img_what)
                .into(mBinding.img1)

            Glide.with(this)
                .load(R.drawable.img_war)
                .into(mBinding.img2)
        }
    }

}
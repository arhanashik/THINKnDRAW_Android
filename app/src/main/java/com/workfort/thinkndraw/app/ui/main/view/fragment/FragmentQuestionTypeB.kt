package com.workfort.thinkndraw.app.ui.main.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.question.QuestionEntity
import com.workfort.thinkndraw.app.ui.main.view.viewmodel.MainViewModel
import com.workfort.thinkndraw.databinding.FragmentQuestionTypeBBinding

class FragmentQuestionTypeB: Fragment() {

    private lateinit var mBinding: FragmentQuestionTypeBBinding

    private lateinit var mViewModel: MainViewModel

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

        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

        val question = arguments?.getParcelable<QuestionEntity>(Const.Params.QUESTION)

        question?.let {
            mViewModel.mCurrentQuestionLiveData.postValue(it)

            mBinding.tvQuestion.text = it.question
            mBinding.tvMessage.text = it.message

            Glide.with(this)
                .asGif()
                .load(it.images[0])
                .into(mBinding.img1)

            Glide.with(this)
                .load(it.images[1])
                .into(mBinding.img2)
        }
    }

}
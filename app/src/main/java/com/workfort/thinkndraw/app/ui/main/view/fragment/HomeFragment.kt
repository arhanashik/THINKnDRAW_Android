package com.workfort.thinkndraw.app.ui.main.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.ui.quiz.view.activity.QuizActivity
import com.workfort.thinkndraw.databinding.FragmentHomeBinding
import com.workfort.thinkndraw.util.helper.ImageLoader

class HomeFragment: Fragment() {

    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ImageLoader.loadGif(R.drawable.img_draw, mBinding.imgBg)

        activity?.let {

        }

        mBinding.btnStart.setOnClickListener {
            startActivity(Intent(context, QuizActivity::class.java))
        }

        mBinding.btnChallenge.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.fragmentHomeToFragmentChallenge()
            )
        }

    }

}
package com.workfort.thinkndraw.app.ui.main.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.result.Result
import com.workfort.thinkndraw.app.ui.quiz.viewmodel.QuizViewModel
import com.workfort.thinkndraw.databinding.FragmentMultiplayerBinding
import com.workfort.thinkndraw.util.helper.ClassifierUtil
import com.workfort.thinkndraw.util.helper.FirebaseDbUtil
import com.workfort.thinkndraw.util.helper.MediaPlayerUtil
import java.io.IOException

class MultiplayerFragment: Fragment() {

    private lateinit var mBinding: FragmentMultiplayerBinding

    private lateinit var mQuizViewModel: QuizViewModel

    private lateinit var mClassifier: ClassifierUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_multiplayer, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            mQuizViewModel = ViewModelProviders.of(it).get(QuizViewModel::class.java)
        }

        initClassifier()
        observeData()

        FirebaseDbUtil.observeMyConnectivity()
        FirebaseDbUtil.getOnlineUsers()

        mQuizViewModel.selectChallenge()

        mBinding.btnCheck.setOnClickListener { classify() }
        mBinding.btnClear.setOnClickListener { clearPaint() }
    }

    private fun initClassifier() {
        try {
            mClassifier = ClassifierUtil(activity!!)
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to create ClassifierUtil", Toast.LENGTH_SHORT).show()
            Log.e("ClassifierUtil", "initClassifier(): Failed to create ClassifierUtil", e)
        }
    }

    private fun observeData() {
        mQuizViewModel.mCurrentChallengeLiveData.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer

            val question = "Draw ${it.second} with 90% accuracy"
            mBinding.tvQuestion.text = question
        })
    }

    private fun classify() {
        val image = mBinding.paintView.exportToBitmap(
            ClassifierUtil.IMG_WIDTH, ClassifierUtil.IMG_HEIGHT
        )

        val result = mClassifier.classify(image)
        renderResult(result)
    }

    private fun renderResult(result: Result) {
        val timeCost = String.format(
            getString(R.string.time_cost_value),
            result.timeCost
        )
        val className = result.className()
        val output = "Class: $className\nProbability: ${result.probability}\nList: ${result.probabilityArr.contentToString()}\nTimeCost: $timeCost"
        Log.e("ClassifierUtil", output)

        val resultTxt = "It's $className with ${result.probability}% accuracy"
        mBinding.tvAnswer.text = resultTxt

        mQuizViewModel.mCurrentChallengeLiveData.value?.let {
            if(result.number == it.first && result.probability >= 0.9) {
                MediaPlayerUtil.play(context!!, R.raw.sound_success)
                var title = "Congratulations"
                var message = "Your drawing is great! Keep up!"

                AlertDialog.Builder(context!!)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok") { _, _ ->
                        mQuizViewModel.selectChallenge()
                        clearPaint()
                    }
                    .create()
                    .show()
            } else {
                MediaPlayerUtil.play(context!!, R.raw.sound_failed)
            }
        }
    }

    private fun clearPaint() {
        mBinding.paintView.clear()
        mBinding.tvAnswer.text = ""
    }

}
package com.workfort.thinkndraw.app.ui.quiz.view.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.workfort.thinkndraw.NavigationQuizDirections
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.result.Result
import com.workfort.thinkndraw.app.ui.quiz.viewmodel.QuizViewModel
import com.workfort.thinkndraw.databinding.ActivityQuizBinding
import com.workfort.thinkndraw.databinding.PromptResultBinding
import com.workfort.thinkndraw.util.helper.ClassifierUtil
import com.workfort.thinkndraw.util.helper.ImageLoader
import com.workfort.thinkndraw.util.helper.ImageUtil
import java.io.IOException


class QuizActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityQuizBinding

    private lateinit var mNavController: NavController

    private lateinit var mQuizViewModel: QuizViewModel

    private lateinit var mClassifier: ClassifierUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz)
        setSupportActionBar(mBinding.toolbar)

        mQuizViewModel = ViewModelProviders.of(this).get(QuizViewModel::class.java)

        mBinding.fab.setOnClickListener {
            classify()
        }

        mNavController = findNavController(R.id.fragment_nav_host)

        initClassifier()
        mBinding.content.fpvPaint.pen.strokeWidth = 10f
        observeData()

        mQuizViewModel.loadQuestions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_normal -> {
                mBinding.content.paintView.normal()
                true
            }
            R.id.action_emboss -> {
                mBinding.content.paintView.emboss()
                true
            }
            R.id.action_blur -> {
                mBinding.content.paintView.blur()
                true
            }
            R.id.action_clear -> {
                mBinding.content.paintView.clear()
//                clearPaint()
                true
            }
            R.id.action_save -> {
                saveDrawing()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initClassifier() {
        try {
            mClassifier = ClassifierUtil(this)
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to create ClassifierUtil", Toast.LENGTH_SHORT).show()
            Log.e("ClassifierUtil", "initClassifier(): Failed to create ClassifierUtil", e)
        }
    }

    private fun observeData() {
        mQuizViewModel.mQuestionsLiveData.observe(this, Observer {
            if(it.isNullOrEmpty()) return@Observer

//            val args = Bundle()
//            args.putParcelable(Const.Params.QUESTION, it[0])
//
//            mNavController.navigate(R.id.fragmentQuestionTypeB, args)
            mNavController.navigate(NavigationQuizDirections.goToFragmentQuestionTypeC(it[1]))
//            mNavController.navigate(R.id.fragmentQuestionTypeC, args)
        })
    }

    private fun saveDrawing() {
        mBinding.content.paintView.exportToBitmap().let {
            ImageUtil.saveBitmap(it)
        }
    }

    private fun classify() {
//        if(mBinding.content.fpvPaint.isEmpty) return

//        val image = mBinding.content.fpvPaint.exportToBitmap(
//            ClassifierUtil.IMG_WIDTH, ClassifierUtil.IMG_HEIGHT
//        )
        val image = mBinding.content.paintView.exportToBitmap(
            ClassifierUtil.IMG_WIDTH, ClassifierUtil.IMG_HEIGHT
        )

//        ImageUtil.saveBitmap(image)

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

        mQuizViewModel.mCurrentQuestionLiveData.value?.let {
            var success = false
//            var imageRes = R.drawable.img_newton_failure
            var imageRes = R.drawable.img_tiger_failure
//            var message = "Apple: Now are responsible for the death of Newton!"
            var message = "Tiger: আহো ভাতিজা আহো! Come to papa!"
            if(result.number == it.answer?.first && result.probability > ClassifierUtil.THRESH_HOLD) {
                success = true
//                imageRes = R.drawable.img_newton_success
                imageRes = R.drawable.img_tiger_success
                message = "Luckily your ${it.answer?.second} is fast enough! Careful next time!"
            }

            showResult(success, imageRes, message)
        }
    }

    private fun showResult(success: Boolean, imgRes: Int, message: String) {
        val binding = DataBindingUtil.inflate<PromptResultBinding>(
            layoutInflater, R.layout.prompt_result, null, false
        )
        val title = if(success) "Congratulations" else "Wrong answer"

        binding.tvTitle.text = message
        if(!success) binding.tvTitle.setTextColor(Color.RED)
        binding.imgResult.clearAnimation()
        ImageLoader.loadGif(imgRes, binding.imgResult)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(binding.root)
            .setPositiveButton("Next") { _, _ ->
            }
            .create()
            .show()
    }

    private fun clearPaint() {
        mBinding.content.fpvPaint.clear()
    }
}

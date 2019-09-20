package com.workfort.thinkndraw.app.ui.quiz.view.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.workfort.thinkndraw.app.data.local.constant.Const
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

        mNavController = findNavController(R.id.fragment_nav_host)

        initClassifier()
        mBinding.content.fpvPaint.pen.strokeWidth = 10f
        observeData()

        mQuizViewModel.loadQuestions()

        mBinding.content.btnCheck.setOnClickListener { classify() }
        mBinding.content.btnClear.setOnClickListener { clearPaint() }
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
                clearPaint()
                true
            }
            R.id.action_save -> {
                saveDrawing()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        if(mNavController.currentDestination?.id == R.id.fragmentHome) {
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Stop Game")
                .setMessage("Are you surely want to stop the game here?")
                .setPositiveButton("Yes") { _, _ ->
                    finish()
                }
                .setNegativeButton("No") { _, _ -> }
                .create()
                .show()
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
//            mNavController.navigate(NavigationQuizDirections.goToFragmentQuestionTypeD(it[2]))
//            mNavController.navigate(R.id.fragmentQuestionTypeC, args)
        })

        mQuizViewModel.mCurrentQuestionLiveData.observe(this, Observer {
            if(it == null) return@Observer

            val dest = when(it.questionType) {
                Const.QuestionType.TYPE_B -> NavigationQuizDirections.goToFragmentQuestionTypeB(it)
                Const.QuestionType.TYPE_C -> NavigationQuizDirections.goToFragmentQuestionTypeC(it)
                Const.QuestionType.TYPE_D -> NavigationQuizDirections.goToFragmentQuestionTypeD(it)
                else -> null
            }
            dest?.let { mNavController.navigate(dest) }
        })

        mQuizViewModel.mFinishQuizLiveData.observe(this, Observer {
            if(it == null) return@Observer

            if(it) finish()
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
            getString(R.string.time_cost_value), result.timeCost
        )
        val className = result.className()
        val output = "Class: $className\nProbability: ${result.probability}\nList: ${result.probabilityArr.contentToString()}\nTimeCost: $timeCost"
        Log.e("ClassifierUtil", output)

        mQuizViewModel.mCurrentQuestionLiveData.value?.let {
            var success = false
            var imageRes = it.failureGif
            var message = it.failureMessage
            if(result.number == it.answer?.first && result.probability > ClassifierUtil.THRESH_HOLD) {
                success = true
                imageRes = it.successGif
                message = it.successMessage
            }

            showResult(success, imageRes, message)
        }
    }

    private fun showResult(success: Boolean, imgRes: Int, message: String) {
        val binding = DataBindingUtil.inflate<PromptResultBinding>(
            layoutInflater, R.layout.prompt_result, null, false
        )
        val title = if(success) "Congratulations" else "Wrong answer"

        binding.tvTitle.text = title
        binding.tvSubtitle.text = message
        if(!success) binding.tvTitle.setTextColor(Color.RED)
        binding.imgResult.clearAnimation()
        ImageLoader.loadGif(imgRes, binding.imgResult)

        AlertDialog.Builder(this)
            .setView(binding.root)
            .setPositiveButton("Next") { _, _ ->
                if(success) {
                    mQuizViewModel.startQuiz(mQuizViewModel.mCurrentStep + 1)
                    clearPaint()
                }
            }
            .create()
            .show()
    }

    private fun clearPaint() {
//        mBinding.content.fpvPaint.clear()
        mBinding.content.paintView.clear()
    }
}

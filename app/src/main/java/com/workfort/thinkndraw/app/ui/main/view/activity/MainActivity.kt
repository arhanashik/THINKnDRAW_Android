package com.workfort.thinkndraw.app.ui.main.view.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.result.Result
import com.workfort.thinkndraw.app.ui.main.view.viewmodel.MainViewModel
import com.workfort.thinkndraw.databinding.ActivityMainBinding
import com.workfort.thinkndraw.util.helper.ClassifierUtil
import com.workfort.thinkndraw.util.helper.ImageUtil
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController

    private lateinit var mViewModel: MainViewModel

    private lateinit var mClassifier: ClassifierUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(mBinding.toolbar)

        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mBinding.fab.setOnClickListener {
            classify()
        }

        mNavController = findNavController(R.id.fragment_nav_host)

        initClassifier()
        mBinding.content.fpvPaint.pen.strokeWidth = 10f
        observeData()

        mViewModel.loadQuestions()
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
        mViewModel.mQuestionsLiveData.observe(this, Observer {
            if(it.isNullOrEmpty()) return@Observer

            val args = Bundle()
            args.putParcelable(Const.Params.QUESTION, it[0])

            mNavController.navigate(R.id.fragmentQuestionTypeB, args)
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
        val output = "Class: ${result.number}\nProbability: ${result.probability}\nTimeCost: $timeCost"

        Log.e("ClassifierUtil", output)
    }

    private fun clearPaint() {
        mBinding.content.fpvPaint.clear()
    }
}

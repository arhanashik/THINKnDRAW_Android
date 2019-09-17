package com.workfort.thinkndraw.app.ui.main.view.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
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
import com.workfort.thinkndraw.app.ui.main.view.viewmodel.MainViewModel
import com.workfort.thinkndraw.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController

    private lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(mBinding.toolbar)

        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mBinding.fab.setOnClickListener { mBinding.content.paintView.clear() }

        mNavController = findNavController(R.id.fragment_nav_host)

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
                true
            }
            R.id.action_save -> {
                saveDrawing()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeData() {
        mViewModel.mQuestionsLiveData.observe(this, Observer {
            if(it.isNullOrEmpty()) return@Observer

            val args = Bundle()
            args.putParcelable(Const.Params.QUESTION, it[0])

            mNavController.navigate(R.id.fragmentQuestionTypeB, args)
        })
    }

    @Suppress("DEPRECATION")
    private fun saveDrawing() {
        val path = Environment.getExternalStorageDirectory().absolutePath
        val file = File("$path/THINKnDRAW.png")
        try {
            val outStream = FileOutputStream(file)
            file.createNewFile()
            val drawingBmp = mBinding.content.paintView.getBitmap()
            drawingBmp?.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
            outStream.flush()
            outStream.close()
            Toast.makeText(this, "image saved", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }
}

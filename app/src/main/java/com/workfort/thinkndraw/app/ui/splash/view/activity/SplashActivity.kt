package com.workfort.thinkndraw.app.ui.splash.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.hanks.htextview.fall.FallTextView
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.ui.main.view.activity.MainActivity
import java.lang.Exception


class SplashActivity : AppCompatActivity() {

    private lateinit var mTvSplash: FallTextView

    private val mHandler = Handler()
    private val mAnimationRunnable = Runnable {
        mTvSplash.animateText(getString(R.string.app_name))

        mTvSplash.setAnimationListener {
            mHandler.postDelayed({mNextActivityRunnable.run()}, 1000)
        }
    }
    private val mNextActivityRunnable = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mTvSplash = findViewById(R.id.tv_splash)
        mTvSplash.animateText(getString(R.string.app_banner))
    }

    override fun onResume() {
        super.onResume()

        mHandler.postDelayed({ mAnimationRunnable.run() }, 2000)
    }

    override fun onPause() {
        super.onPause()

        mHandler.removeCallbacks(mAnimationRunnable)
        mHandler.removeCallbacks(mNextActivityRunnable)
    }
}
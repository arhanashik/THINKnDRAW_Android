package com.workfort.thinkndraw.app.ui.main.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.app.ui.main.viewmodel.MultiplayerViewModel
import com.workfort.thinkndraw.databinding.ActivityMainBinding
import com.workfort.thinkndraw.util.helper.MediaPlayerUtil
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController

    private lateinit var mMultiplayerViewModel: MultiplayerViewModel

    private lateinit var mBroadcastManager: LocalBroadcastManager
    private val mBroadCastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            MediaPlayerUtil.playNotification()
            intent?.let { checkNotificationIntent(intent) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mBroadcastManager = LocalBroadcastManager.getInstance(this)
        mNavController = findNavController(R.id.fragment_nav_host)
        mMultiplayerViewModel = ViewModelProviders.of(this).get(MultiplayerViewModel::class.java)

        observeData()

        checkNotificationIntent(intent)

        mBinding.btnAccept.setOnClickListener {
            mMultiplayerViewModel.mStartTime = System.currentTimeMillis()
            mMultiplayerViewModel.acceptChallenge()
            goToMultiplayer()
        }
    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter(Const.FcmMessaging.Action.Multiplayer.INVITE)
        mBroadcastManager.registerReceiver(mBroadCastReceiver, filter)
    }

    override fun onPause() {
        super.onPause()

        mBroadcastManager.unregisterReceiver(mBroadCastReceiver)
    }

    override fun onBackPressed() {
        if(mNavController.currentDestination?.id == R.id.fragmentHome) {
            finish()
            return
        }

        if(mNavController.currentDestination?.id != R.id.fragmentMultiplayer) {
            mBinding.groupNewChallenge.visibility = View.GONE
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Exit Multiplayer")
                .setMessage("Are you surely want to exit multilayer mood?")
                .setPositiveButton("Yes") { _, _ ->
                    mBinding.groupNewChallenge.visibility = View.GONE
                    mMultiplayerViewModel.clearData()
                    super.onBackPressed()
                }
                .setNegativeButton("No") { _, _ -> }
                .create()
                .show()
        }
    }

    private fun observeData() {

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.e("onNewIntent: ${intent?.action}")

        intent?.let {
            checkNotificationIntent(intent)
        }
    }

    private fun checkNotificationIntent(intent: Intent) {
        val action = intent.action?: "no action"

        val senderId = intent.getStringExtra(Const.FcmMessaging.DataKey.SENDER_ID)
        val senderName = intent.getStringExtra(Const.FcmMessaging.DataKey.SENDER_NAME)?: "Unknown"
        val senderFcmToken = intent.getStringExtra(Const.FcmMessaging.DataKey.SENDER_FCM_TOKEN)?: ""
        val challengeId = intent.getStringExtra(Const.FcmMessaging.DataKey.CHALLENGE_ID)?: "-1"
        val match = intent.getStringExtra(Const.FcmMessaging.DataKey.MATCH)?: "match"
        when(action) {
            Const.FcmMessaging.Action.Multiplayer.INVITE -> {
                val playerEntity = UserEntity(senderName, senderFcmToken)
                val player = Pair(senderId, playerEntity)
                mMultiplayerViewModel.mCurrentPlayerLiveData.postValue(player)
                mMultiplayerViewModel.mCurrentMatch = match
                mMultiplayerViewModel.selectChallenge(false, challengeId.toInt())
                mBinding.groupNewChallenge.visibility = View.VISIBLE
                val newChallengeTxt = getString(R.string.txt_new_challenge).replace(
                    "XYZ", senderName
                )
                mBinding.tvNewChallenge.text = newChallengeTxt
            }
            Const.FcmMessaging.Action.Multiplayer.ACCEPT -> {
                mMultiplayerViewModel.mCurrentChallengeClassId = challengeId.toInt()
                goToMultiplayer()
            }
            else -> Timber.e("Implement action: $action")
        }
    }

    private fun goToMultiplayer() {
        mBinding.groupNewChallenge.visibility = View.GONE
        if(mNavController.currentDestination?.id != R.id.fragmentMultiplayer) {
            mNavController.navigate(R.id.fragmentMultiplayer)
        }
    }
}

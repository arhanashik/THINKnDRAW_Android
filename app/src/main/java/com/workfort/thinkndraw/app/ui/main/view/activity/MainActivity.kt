package com.workfort.thinkndraw.app.ui.main.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.app.ui.main.view.viewmodel.MultiplayerViewModel
import com.workfort.thinkndraw.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController

    private lateinit var mMultiplayerViewModel: MultiplayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mNavController = findNavController(R.id.fragment_nav_host)

        mMultiplayerViewModel = ViewModelProviders.of(this).get(MultiplayerViewModel::class.java)

        observeData()

        checkNotificationIntent(intent)
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
//                val senderImageUrl = it.getStringExtra(Const.FcmMessaging.DataKey.SENDER_IMAGE_URL)?: ""
        val senderFcmToken = intent.getStringExtra(Const.FcmMessaging.DataKey.SENDER_FCM_TOKEN)?: ""
        val challengeId = intent.getStringExtra(Const.FcmMessaging.DataKey.CHALLENGE_ID)?: "-1"
//                val message = it.getStringExtra(Const.FcmMessaging.DataKey.MESSAGE)
        when(action) {
            Const.FcmMessaging.Action.Multiplayer.INVITE -> {
                val playerEntity = UserEntity(senderName, senderFcmToken)
                val player = Pair(senderId, playerEntity)
                mMultiplayerViewModel.mCurrentPlayerLiveData.postValue(player)
                mMultiplayerViewModel.selectChallenge(false, challengeId.toInt())
                mMultiplayerViewModel.acceptChallenge(playerEntity)
                mNavController.navigate(R.id.fragmentMultiplayer)
            }
            Const.FcmMessaging.Action.Multiplayer.ACCEPT -> {
//                Timber.e("challenged accepted by $senderId : $senderName")
                mMultiplayerViewModel.mCurrentChallengeClassId = challengeId.toInt()
                mNavController.navigate(R.id.fragmentMultiplayer)
            }
            else -> Timber.e("Implement action: $action")
        }
    }
}

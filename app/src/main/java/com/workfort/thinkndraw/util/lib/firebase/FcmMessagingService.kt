package com.workfort.thinkndraw.util.lib.firebase

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.pref.PrefProp
import com.workfort.thinkndraw.app.data.local.pref.PrefUtil
import com.workfort.thinkndraw.util.lib.firebase.util.FirebaseDbUtil
import timber.log.Timber


/*
*  ****************************************************************************
*  * Created by : arhan on 2019-09-22 at 12:00PM.
*  * Email : ashik.pstu.cse@gmail.com
*  *
*  * This class is for: 
*  * 1.
*  * 2.
*  * 3.
*  * 
*  * Last edited by : arhan on 2019-09-22.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

class FcmMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.e("From: ${remoteMessage.from}")

        var clickAction: String? = null
        remoteMessage.notification?.let {
            Timber.e("Message Notification Body: ${it.body}")
            clickAction = it.clickAction
        }

        remoteMessage.data.let { data ->
            Timber.e("Message data payload: $data")

            clickAction?.let { clickAction ->
                val intent = Intent(clickAction)
                intent.putExtra(Const.FcmMessaging.DataKey.SENDER_ID, data[Const.FcmMessaging.DataKey.SENDER_ID])
                intent.putExtra(Const.FcmMessaging.DataKey.SENDER_NAME, data[Const.FcmMessaging.DataKey.SENDER_NAME])
                intent.putExtra(Const.FcmMessaging.DataKey.SENDER_FCM_TOKEN, data[Const.FcmMessaging.DataKey.SENDER_FCM_TOKEN])
                intent.putExtra(Const.FcmMessaging.DataKey.CHALLENGE_ID, data[Const.FcmMessaging.DataKey.CHALLENGE_ID])
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.e("Refreshed token: $token")

        PrefUtil.set(PrefProp.LAST_KNOWN_FCM_TOKEN, token)
        FirebaseDbUtil.updateFcmToken()
    }
}
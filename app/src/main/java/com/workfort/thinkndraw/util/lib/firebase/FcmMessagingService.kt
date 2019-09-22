package com.workfort.thinkndraw.util.lib.firebase

import android.text.TextUtils
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

        remoteMessage.data.let {
            Timber.e("Message data payload: $it")

            val dataType = it[Const.FcmMessaging.DataKey.TYPE]
            if(dataType == Const.FcmMessaging.DataType.MESSAGE) {
                val msgId = it[Const.FcmMessaging.DataKey.MESSAGE_ID]?: ""
                if(TextUtils.isDigitsOnly(msgId)) {

                }
            }
        }

        remoteMessage.notification?.let {
            Timber.e("Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.e("Refreshed token: $token")

        PrefUtil.set(PrefProp.LAST_KNOWN_FCM_TOKEN, token)
        FirebaseDbUtil.updateFcmToken()
    }
}
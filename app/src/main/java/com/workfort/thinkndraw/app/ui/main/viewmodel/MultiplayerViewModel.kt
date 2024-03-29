package com.workfort.thinkndraw.app.ui.main.viewmodel

import androidx.core.util.forEach
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.local.pref.PrefProp
import com.workfort.thinkndraw.app.data.local.pref.PrefUtil
import com.workfort.thinkndraw.app.data.local.result.MultiplayerResult
import com.workfort.thinkndraw.app.data.local.result.Result
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.util.lib.firebase.callback.BoardStatusCallback
import com.workfort.thinkndraw.util.lib.firebase.callback.SaveResultCallback
import com.workfort.thinkndraw.util.lib.firebase.util.FirebaseDbUtil
import com.workfort.thinkndraw.util.lib.remote.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import kotlin.random.Random

class MultiplayerViewModel: ViewModel() {

    private val mDisposable: CompositeDisposable = CompositeDisposable()
    private val mPushApiService by lazy { ApiService.createPushApiService() }

    val mCurrentPlayerLiveData = MutableLiveData<Pair<String, UserEntity>>()
    val mCurrentChallengeLiveData = MutableLiveData<Pair<Int, String>>()
    var mCurrentChallengeClassId = -1
    private var mMyBoard = false
    var mCurrentMatch = ""
    var mStartTime = 0L
    var mEndTime = 0L

    val mResultsLiveData = MutableLiveData<HashMap<String, MultiplayerResult?>>()

    fun inviteToChallenge(player: UserEntity) {
        mCurrentMatch = System.currentTimeMillis().toString()
        val senderId = PrefUtil.get<String>(PrefProp.USER_ID, null)?: return
        val senderName = PrefUtil.get<String>(PrefProp.USER_NAME, null)?: "UNKNOWN"
        val senderFcmToken = PrefUtil.get<String>(PrefProp.LAST_KNOWN_FCM_TOKEN, null)?: return

        val action = Const.FcmMessaging.Action.Multiplayer.INVITE
        val notification = getNotification("$senderName challenged you! Let's go!", action)
        val data = getChallengeData(senderId, senderName, senderFcmToken)

        //        val toTopic = Const.FcmMessaging.Topic.MULTIPLAYER
        mMyBoard = true
        player.fcmToken?.let {fcmToken ->
            sendPush(fcmToken, notification, data)
        }
    }

    fun acceptChallenge() {
        val player = mCurrentPlayerLiveData.value?.second?: return
        Timber.e("accept challenge by ${player.name} : ${player.fcmToken}")
        val senderId = PrefUtil.get<String>(PrefProp.USER_ID, null)?: return
        val senderName = PrefUtil.get<String>(PrefProp.USER_NAME, null)?: "UNKNOWN"
        val senderFcmToken = PrefUtil.get<String>(PrefProp.LAST_KNOWN_FCM_TOKEN, null)?: return

        val action = Const.FcmMessaging.Action.Multiplayer.ACCEPT
        val notification = getNotification("$senderName accepted your challenge! Let's play!", action)
        val data = getChallengeData(senderId, senderName, senderFcmToken)

        player.fcmToken?.let {fcmToken ->
            sendPush(fcmToken, notification, data)
        }
    }

    private fun sendPush(receiver: String, notification: JSONObject, data: JSONObject) {

        val push = JSONObject()
//        push.put("to", toTopic)
        push.put("to", receiver)
//        push.put("registration_ids", getReceiverArray())
        push.put("notification", notification)
        push.put("data", data)
        val json = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(json, push.toString())

//        Timber.e("PUSH: $push")
        mDisposable.add(
            mPushApiService.sendPushSingle(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.e("PUSH: $it")
                    }, {
                        Timber.e(it)
                    }
                )
        )
    }

    fun selectChallenge(random: Boolean = true, challengeId: Int = -1) {
        val classes = Const.CLASSIFICATION_CLASSES
        mCurrentChallengeClassId = if(random) Random.nextInt(0, classes.size()) else challengeId
        var challenge = Pair(0, classes[0])
        classes.forEach { classIndex, className ->
            if(classIndex == mCurrentChallengeClassId) {
                challenge = Pair(classIndex, className)
                return@forEach
            }
        }

        mCurrentChallengeLiveData.postValue(challenge)
    }

    private fun getReceiverArray(): JSONArray {
        val receiver1 = "fWzgGKkBRbs:APA91bGh662rEXypzF6vs_w_a43NhvWf9ttF_lASQyuDcCusupN8p3-c314GI9RWPKN_ee8hjkNoJO0Y7umjnTmvvqMCqf-mREkG0UIhyDbwE_wPhCkcfKqPkxne2oR3xlYJCmh0woii"
        val receiver2 = "d-FQGqEQ18Y:APA91bE5pbxPuMd5cK5JQ3g53VgaAMi2MXSEZrQnRkv8GUmGhl3vdDLpN_kLy7qAQ4vTBLSlm2BG2NTFnsy9IMZTqAgCfgsZHGb_5c7bp-7nv0hqhUM-oRLZ4CErSaO6mKbWsGqotpcN"

        val receiver = JSONArray()
        receiver.put(receiver1)
        receiver.put(receiver2)

        return receiver
    }

    private fun getNotification(message: String, action: String): JSONObject {
        val notification = JSONObject()
        notification.put(Const.FcmMessaging.NotificationKey.TITLE, "Challenge")
        notification.put(Const.FcmMessaging.NotificationKey.BODY, message)
        notification.put(Const.FcmMessaging.NotificationKey.CLICK_ACTION, action)

        return notification
    }

    private fun getChallengeData (
        senderId: String,
        senderName: String,
        senderFcmToken: String
    ): JSONObject {
        val data = JSONObject()
        data.put(Const.FcmMessaging.DataKey.SENDER_ID, senderId)
        data.put(Const.FcmMessaging.DataKey.SENDER_NAME, senderName)
        data.put(Const.FcmMessaging.DataKey.SENDER_FCM_TOKEN, senderFcmToken)
        data.put(Const.FcmMessaging.DataKey.CHALLENGE_ID, mCurrentChallengeClassId.toString())
        data.put(Const.FcmMessaging.DataKey.MATCH, mCurrentMatch)
//        data.put(Const.FcmMessaging.DataKey.MESSAGE, message)

        return data
    }

    fun observeChallenge() {
        mCurrentPlayerLiveData.value?.let {
            FirebaseDbUtil.observeMatch(it.first, mCurrentMatch, mMyBoard, object: BoardStatusCallback {
                override fun onResponse(results: HashMap<String, MultiplayerResult?>) {
//                    Timber.e(results.toString())
                    mResultsLiveData.postValue(results)
                }
            })
        }
    }

    fun saveChallengeResult(result: Result) {
        val multiplayerResult = MultiplayerResult(
            result.number, result.className(), result.probability, mEndTime - mStartTime
        )

        mCurrentPlayerLiveData.value?.let {
            FirebaseDbUtil.saveMatchResult(it.first, mCurrentMatch, multiplayerResult, mMyBoard, object: SaveResultCallback {
                override fun onComplete(success: Boolean) {

                }
            })
        }
    }

    fun clearData() {
        mCurrentPlayerLiveData.value = null
        mCurrentChallengeLiveData.value = null
        mCurrentChallengeClassId = -1
        mMyBoard = false
        mStartTime = 0
        mEndTime = 0
        mResultsLiveData.value = null
    }

    override fun onCleared() {
        mDisposable.dispose()
        super.onCleared()
    }
}
package com.workfort.thinkndraw.util.lib.firebase.util

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.google.firebase.messaging.FirebaseMessaging
import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.util.lib.firebase.callback.TopicSubscriptionCallback
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

/*
*  ****************************************************************************
*  * Created by : arhan on 2019-02-22 at 12:46PM.
*  * Email : ashik.pstu.cse@gmail.com
*  *
*  * This class is for: 
*  * 1.
*  * 2.
*  * 3.
*  * 
*  * Last edited by : arhan on 2019-02-22.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

object FcmUtil {

    fun subscribeToTopic(topic: String, callback: TopicSubscriptionCallback) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                callback.onComplete(task.isSuccessful)
            }
    }

    fun unsubscribeFromTopic(topic: String, callback: TopicSubscriptionCallback) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                callback.onComplete(task.isSuccessful)
            }
    }

    @SuppressLint("StaticFieldLeak")
    fun sendMessage(
        recipients: JSONArray,
        title: String,
        body: String,
        icon: String,
        message: String
    ) {

        object : AsyncTask<String, String, ResponseBody>() {
            override fun doInBackground(vararg params: String): ResponseBody? {
                try {
                    val root = JSONObject()
                    val notification = JSONObject()
                    notification.put("body", body)
                    notification.put("title", title)
                    notification.put("icon", icon)

                    val data = JSONObject()
                    data.put("message", message)

                    root.put("notification", notification)
                    root.put("data", data)
                    //                    root.put("registration_ids", recipients);
                    root.put("to", "/topics/leader_board")

                    val result = postToFCM(root.toString())
                    Timber.e("Result: %s", result!!.toString())
                    return result
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                return null
            }

            override fun onPostExecute(result: ResponseBody) {
                try {
                    val resultJson = JSONObject(result.toString())
                    val success: Int
                    val failure: Int
                    success = resultJson.getInt("success")
                    failure = resultJson.getInt("failure")
                    Timber.e("Message Success: $success Message Failed: $failure")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Timber.e("Message Failed, Unknown error occurred.")
                }

            }
        }.execute()
    }

    private val mClient = OkHttpClient()

    internal var refreshedToken = ""//add your user refresh tokens who are logged in with firebase.

    internal var jsonArray = JSONArray()

//    jsonArray.put(refreshedToken);

    @Throws(IOException::class)
    private fun postToFCM(bodyString: String): ResponseBody? {

        val fcmMsgUrl = Const.FcmMessaging.PUSH_URL + Const.FcmMessaging.Api.SEND_PUSH
        val json = MediaType.parse("application/json; charset=utf-8")

        val body = RequestBody.create(json, bodyString)
        val request = Request.Builder()
            .url(fcmMsgUrl)
            .post(body)
            .addHeader("Authorization", Const.FcmMessaging.SERVER_KEY)
            .build()
        val response = mClient.newCall(request).execute()
        return response.body()
    }
}
package com.workfort.thinkndraw.util.lib.remote.client

import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.app.data.remote.push.IdPushResponse
import com.workfort.thinkndraw.app.data.remote.push.TopicPushResponse
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/*
*  ****************************************************************************
*  * Created by : arhan on 2019-02-22 at 12:43PM.
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

interface PushApiClient {

    @POST(Const.FcmMessaging.Api.SEND_PUSH)
    fun sendPushSingle(
        /* body can contain following fields:
         * to: registrationId
         * priority: "normal" or "high"
         * notification: predefined key value pair. Available keys are in Const.Kt file
         * data: custom key value pair
         */
        @Body body: RequestBody
    ): Flowable<IdPushResponse>

    @POST(Const.FcmMessaging.Api.SEND_PUSH)
    fun sendPushTopic(
        /* body can contain following fields:
         * to: topic
         * priority: "normal" or "high"
         * notification: predefined key value pair. Available keys are in Const.Kt file
         * data: custom key value pair
         */
        @Body body: RequestBody
    ): Flowable<TopicPushResponse>

    @POST(Const.FcmMessaging.Api.SEND_PUSH)
    fun sendPushMultiple(
        /* body can contain following fields:
         * registration_ids: Array of registration ids. Range: 1 to 100
         * N. B. For 1 id sendPushSingle should be used
         *
         * priority: "normal" or "high"
         * notification: predefined key value pair. Available keys are in Const.Kt file
         * data: custom key value pair
         */
        @Body body: RequestBody
    ): Flowable<IdPushResponse>

    @POST(Const.FcmMessaging.Api.SEND_PUSH)
    fun sendPushConditional(
        /* body can contain following fields:
         * condition:
         * Supported condition: Topic
         * Supported operators: &&, ||
         * Maximum two operators
         *
         * Example: "topic1 && topic2" or "topic1 || topic2"
         *
         * priority: "normal" or "high"
         * notification: predefined key value pair. Available keys are in Const.Kt file
         * data: custom key value pair
         */
        @Body body: RequestBody
    ): Flowable<IdPushResponse>
}
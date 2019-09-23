package com.workfort.thinkndraw.util.lib.remote

import com.workfort.thinkndraw.app.data.local.constant.Const
import com.workfort.thinkndraw.util.lib.remote.client.PushApiClient
import com.workfort.thinkndraw.util.lib.remote.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/*
*  ****************************************************************************
*  * Created by : arhan on 2019-05-14 at 13:45.
*  * Email : ashik.pstu.cse@gmail.com
*  *
*  * This class is for: 
*  * 1.
*  * 2.
*  * 3.
*  * 
*  * Last edited by : arhan on 2019-05-14.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

object ApiService {

    private val pushOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { HeaderInterceptor().intercept(it) }
        .build()

    private val pushApi = Retrofit.Builder()
        .baseUrl(Const.FcmMessaging.PUSH_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(pushOkHttpClient)
        .build()

    fun createPushApiService(): PushApiClient {
        return pushApi.create(PushApiClient::class.java)
    }
}
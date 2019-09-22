package com.workfort.thinkndraw.util.lib.remote.interceptor

import com.workfort.thinkndraw.app.data.local.constant.Const
import okhttp3.Interceptor
import okhttp3.Response

/*
*  ****************************************************************************
*  * Created by : arhan on 2019-09-22 at 12:41PM.
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

class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", Const.FcmMessaging.SERVER_KEY)
                .addHeader("Content-Type", Const.FcmMessaging.CONTENT_TYPE)
                .build()
        )
    }
}
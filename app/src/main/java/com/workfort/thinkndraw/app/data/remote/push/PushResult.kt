package com.workfort.thinkndraw.app.data.remote.push

import com.google.gson.annotations.SerializedName

/*
*  ****************************************************************************
*  * Created by : arhan on 2019-06-19 at 16:05.
*  * Email : ashik.pstu.cse@gmail.com
*  *
*  * This class is for: 
*  * 1.
*  * 2.
*  * 3.
*  * 
*  * Last edited by : arhan on 2019-06-19.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

data class PushResult (
    @SerializedName("message_id")
    var messageId: String = "",
    @SerializedName("registration_id")
    var registrationId: String = "",
    var error: String = ""
)
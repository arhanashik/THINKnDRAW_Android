package com.workfort.thinkndraw.app.data.remote.push

import com.google.gson.annotations.SerializedName

/*
*  ****************************************************************************
*  * Created by : arhan on 2019-06-19 at 16:02.
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

data class IdPushResponse (
    @SerializedName("multicast_id")
    var multiCastId: Long = 0,
    var success: Long = 0,
    var failure: Long = 0,
    @SerializedName("canonical_ids")
    var canonicalIds: Long = 0,
    var results: ArrayList<PushResult>
)
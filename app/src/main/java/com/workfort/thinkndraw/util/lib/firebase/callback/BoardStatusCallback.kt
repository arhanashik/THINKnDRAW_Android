package com.workfort.thinkndraw.util.lib.firebase.callback

import com.workfort.thinkndraw.app.data.local.result.MultiplayerResult

interface BoardStatusCallback {
    fun onResponse(results: HashMap<String, MultiplayerResult?>)
}
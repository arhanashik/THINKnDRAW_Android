package com.workfort.thinkndraw.util.lib.firebase.callback

interface LastOnlineCallback {
    fun onResponse(lastOnlineList: HashMap<String, Long>)
}
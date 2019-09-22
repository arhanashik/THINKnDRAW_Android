package com.workfort.thinkndraw.util.lib.firebase.callback

import com.workfort.thinkndraw.app.data.local.user.UserEntity

interface UsersCallback {
    fun onResponse(users: HashMap<String, UserEntity?>)
}
package com.workfort.thinkndraw.app.ui.main.callback

import com.workfort.thinkndraw.app.data.local.user.UserEntity

interface SelectPlayerCallback {
    fun onSelect(userId: String, user: UserEntity)
}
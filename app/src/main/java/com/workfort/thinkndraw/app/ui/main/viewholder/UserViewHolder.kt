package com.workfort.thinkndraw.app.ui.main.viewholder

import android.graphics.Color
import android.text.format.DateUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.app.ui.main.callback.SelectPlayerCallback
import com.workfort.thinkndraw.databinding.RowUserBinding
import java.util.*

class UserViewHolder (
    private val binding: RowUserBinding,
    private val callback: SelectPlayerCallback?
): RecyclerView.ViewHolder(binding.root) {

    fun onBind(userId: String, user: UserEntity, online: Boolean, lastSeen: Long) {
        val context = binding.root.context

        binding.tvName.text = user.name

        val onlineStatus: String
        val onlineStatusColor: Int
        if(online) {
            onlineStatus = context.getString(R.string.txt_online)
            onlineStatusColor = ContextCompat.getColor(context, R.color.colorPrimary)

            binding.btnInvite.visibility = View.INVISIBLE
            binding.btnPlay.visibility = View.VISIBLE
        } else {
            onlineStatus = if(lastSeen == 0L) ""
            else {
                DateUtils.getRelativeTimeSpanString(
                    lastSeen , Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS
                ).toString()
            }
            onlineStatusColor = Color.DKGRAY

            binding.btnPlay.visibility = View.INVISIBLE
            binding.btnInvite.visibility = View.VISIBLE
        }

        binding.tvOnlineStatus.text = onlineStatus
        binding.tvOnlineStatus.setTextColor(onlineStatusColor)

        binding.btnPlay.setOnClickListener {
            callback?.onSelect(userId, user)
        }

        binding.btnInvite.setOnClickListener {
            callback?.onSelect(userId, user)
        }
    }
}
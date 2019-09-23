package com.workfort.thinkndraw.app.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.app.ui.main.callback.SelectPlayerCallback
import com.workfort.thinkndraw.app.ui.main.viewholder.UserViewHolder
import com.workfort.thinkndraw.databinding.RowUserBinding

class UserAdapter: RecyclerView.Adapter<UserViewHolder>() {

    private var mUsers = LinkedHashMap<String, UserEntity?>()
    private var mOnlineStatus = ArrayList<String>()
    private var mLastSeenList = LinkedHashMap<String, Long>()

    private var mCallback: SelectPlayerCallback? = null

    fun setUsers(users: HashMap<String, UserEntity?>) {
        mUsers.clear()
        mUsers.putAll(users)

        notifyDataSetChanged()
    }

    fun setOnlineStatus(onlineStatus: ArrayList<String>) {
        mOnlineStatus.clear()
        mOnlineStatus.addAll(onlineStatus)

        notifyDataSetChanged()
    }

    fun setLastSeen(lastSeenList: HashMap<String, Long>) {
        mLastSeenList.clear()
        mLastSeenList.putAll(lastSeenList)

        notifyDataSetChanged()
    }

    fun setCallback(callback: SelectPlayerCallback) {
        mCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<RowUserBinding>(
            inflater, R.layout.row_user, parent, false
        )

        return UserViewHolder(binding, mCallback)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userId: String
        val online: Boolean
        val lastSeen: Long
        mUsers.keys.toTypedArray().let { keys ->
            keys[position].let { key ->
                userId = key
                online = mOnlineStatus.contains(key)
                lastSeen = mLastSeenList[key]?: 0L
            }
        }

        mUsers.values.toTypedArray().let { users ->
            users[position]?.let { user ->
                holder.onBind(userId, user, online, lastSeen)
            }
        }
    }

}
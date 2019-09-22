package com.workfort.thinkndraw.util.helper

import android.util.Log
import com.google.firebase.database.*
import com.workfort.thinkndraw.app.data.local.pref.PrefProp
import com.workfort.thinkndraw.app.data.local.pref.PrefUtil
import com.workfort.thinkndraw.app.data.local.user.UserEntity

object FirebaseDbUtil {
    private const val TAG = "FirebaseDbUtil"

    private val mDatabase by lazy {
        FirebaseDatabase.getInstance().reference
    }

    fun observeMyConnectivity() {
        val userId = PrefUtil.get<Int>(PrefProp.USER_ID, null)?: return

        val database = FirebaseDatabase.getInstance()
        val myConnectionsRef = database.getReference("online_users/$userId")

        val lastOnlineRef = database.getReference("last_online/$userId")

        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d(TAG, "I am connected")
                    val con = myConnectionsRef.push()

                    // When this device disconnects, remove it
                    con.onDisconnect().removeValue()

                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP)

                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(java.lang.Boolean.TRUE)

                } else {
                    Log.d(TAG, "I am not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled")
            }
        })
    }

    fun addUser(id: Int, user: UserEntity, callback: AddUserCallback) {
        mDatabase.child("users").child(id.toString()).setValue(user)
            .addOnSuccessListener { callback.onComplete(true) }
            .addOnFailureListener { callback.onComplete(false) }
    }

    fun getUsers(callback: UsersCallback) {
        val myUserId = PrefUtil.get<Int>(PrefProp.USER_ID, null)

        val onlineUsersDb = mDatabase.child("users").ref

        val users = HashMap<String, UserEntity?>()
        val connectionStatusListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshots: DataSnapshot) {
//                Log.e(TAG, "Count: " + dataSnapshots.childrenCount.toString())

                for (dataSnapshot in dataSnapshots.children) {
//                    Log.e(TAG, dataSnapshot.toString())
                    val userId = dataSnapshot.key?: "UNKNOWN"
//                    if(userId == myUserId.toString()) continue

                    val user = dataSnapshot.getValue(UserEntity::class.java)
                    if(userId == myUserId.toString()) user?.name = user?.name + "(me)"
                    users[userId] = user
                }

                callback.onResponse(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                callback.onResponse(users)
            }
        }

        onlineUsersDb.addValueEventListener(connectionStatusListener)
    }

    fun getOnlineUsers(callback: OnlineUsersCallback) {
        val onlineUsersDb = mDatabase.child("online_users").ref

        val onlineUsers = ArrayList<String>()
        val connectionStatusListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshots: DataSnapshot) {
//                Log.e(TAG, "Count: " + dataSnapshots.childrenCount.toString())

                for (dataSnapshot in dataSnapshots.children) {
//                    Log.e(TAG, dataSnapshot.toString())
                    val userId = dataSnapshot.key?: "UNKNOWN"
                    val devices = dataSnapshot.value as HashMap<*, *>

                    if(!devices.isNullOrEmpty()) {
                        onlineUsers.add(userId)
                    }
                }

                callback.onResponse(onlineUsers)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                callback.onResponse(onlineUsers)
            }
        }

        onlineUsersDb.addValueEventListener(connectionStatusListener)
    }

    fun getLastOnline(callback: LastOnlineCallback) {
        val onlineUsersDb = mDatabase.child("last_online").ref

        val lastOnlineList = HashMap<String, Long>()
        val connectionStatusListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshots: DataSnapshot) {
//                Log.e(TAG, "Count: " + dataSnapshots.childrenCount.toString())

                for (dataSnapshot in dataSnapshots.children) {
//                    Log.e(TAG, dataSnapshot.toString())
                    val userId = dataSnapshot.key?: "UNKNOWN"
                    val time = dataSnapshot.value as Long

                    lastOnlineList[userId] = time
                }

                callback.onResponse(lastOnlineList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                callback.onResponse(lastOnlineList)
            }
        }

        onlineUsersDb.addValueEventListener(connectionStatusListener)
    }

    interface AddUserCallback {
        fun onComplete(success: Boolean)
    }

    interface UsersCallback {
        fun onResponse(users: HashMap<String, UserEntity?>)
    }

    interface OnlineUsersCallback {
        fun onResponse(userIds: ArrayList<String>)
    }

    interface LastOnlineCallback {
        fun onResponse(lastOnlineList: HashMap<String, Long>)
    }
}
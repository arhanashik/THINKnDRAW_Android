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
        val name = PrefUtil.get<String>(PrefProp.USER_NAME, null)?: return

        val database = FirebaseDatabase.getInstance()
        val myConnectionsRef = database.getReference("online_users/$name")

        val lastOnlineRef = database.getReference("last_online/$name")

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

    fun getOnlineUsers() {
        val onlineUsers = mDatabase.child("online_users").ref

        val connectionStatusListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e(TAG, "Count: " + dataSnapshot.childrenCount.toString())

                for (postSnapshot in dataSnapshot.children) {
                    Log.e(TAG, dataSnapshot.value.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        onlineUsers.addValueEventListener(connectionStatusListener)
    }

    interface AddUserCallback {
        fun onComplete(success: Boolean)
    }
}
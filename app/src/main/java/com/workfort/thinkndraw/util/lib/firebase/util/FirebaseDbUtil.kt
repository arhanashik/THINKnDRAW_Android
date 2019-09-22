package com.workfort.thinkndraw.util.lib.firebase.util

import com.google.firebase.database.*
import com.workfort.thinkndraw.app.data.local.pref.PrefProp
import com.workfort.thinkndraw.app.data.local.pref.PrefUtil
import com.workfort.thinkndraw.app.data.local.result.MultiplayerResult
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.util.lib.firebase.callback.*
import timber.log.Timber

object FirebaseDbUtil {

    private val mDatabase by lazy {
        FirebaseDatabase.getInstance().reference
    }

    fun observeMyConnectivity() {
        val userId = PrefUtil.get<String>(PrefProp.USER_ID, null)?: return

        val database = FirebaseDatabase.getInstance()
        val myConnectionsRef = database.getReference("online_users/$userId")

        val lastOnlineRef = database.getReference("last_online/$userId")

        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Timber.d("I am connected")
                    val con = myConnectionsRef.push()

                    // When this device disconnects, remove it
                    con.onDisconnect().removeValue()

                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP)

                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(java.lang.Boolean.TRUE)

                } else {
                    Timber.d("I am not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.d( "Listener was cancelled")
            }
        })
    }

    fun addUser(id: String, user: UserEntity, callback: AddUserCallback) {
        val fcmToken = PrefUtil.get(PrefProp.LAST_KNOWN_FCM_TOKEN, "")
        user.fcmToken = fcmToken
        mDatabase.child("users").child(id).setValue(user)
            .addOnSuccessListener { callback.onComplete(true) }
            .addOnFailureListener { callback.onComplete(false) }
    }

    fun getUsers(callback: UsersCallback) {
        val myUserId = PrefUtil.get<String>(PrefProp.USER_ID, null)

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
                Timber.e(databaseError.toException())
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
                Timber.e(databaseError.toException())
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
                Timber.e( databaseError.toException())
                callback.onResponse(lastOnlineList)
            }
        }

        onlineUsersDb.addValueEventListener(connectionStatusListener)
    }

    fun updateFcmToken() {
        val userId = PrefUtil.get<String>(PrefProp.USER_ID, null)?: return
        val fcmToken = PrefUtil.get(PrefProp.LAST_KNOWN_FCM_TOKEN, "")

        mDatabase.child("users").child(userId).child("fcmToken")
            .setValue(fcmToken)
            .addOnSuccessListener { Timber.d("$it") }
            .addOnFailureListener { Timber.e("$it") }
    }

    fun observeMatch(
        opponentId: String,
        myBoard: Boolean,
        callback: BoardStatusCallback
    ) {
        val userId = PrefUtil.get<String>(PrefProp.USER_ID, null)?: return
        val board = if(myBoard) "${userId}vs$opponentId" else "${opponentId}vs$userId"
        val boardDb = mDatabase.child("matches").child(board).ref
        boardDb.removeValue()

        val boardStatus = HashMap<String, MultiplayerResult?>()
        val boardListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshots: DataSnapshot) {
//                Log.e(TAG, "Count: " + dataSnapshots.childrenCount.toString())

                for (dataSnapshot in dataSnapshots.children) {
//                    Timber.e(dataSnapshot.toString())
                    val payerId = dataSnapshot.key?: "UNKNOWN"
//                    if(userId == myUserId.toString()) continue

                    val playerResult = dataSnapshot.getValue(MultiplayerResult::class.java)
                    boardStatus[payerId] = playerResult
                }

                callback.onResponse(boardStatus)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.e(databaseError.toException())
                callback.onResponse(boardStatus)
            }
        }

        boardDb.addValueEventListener(boardListener)
    }

    fun saveMatchResult(
        opponentId: String,
        result: MultiplayerResult,
        myBoard: Boolean,
        callback: SaveResultCallback
    ) {
        val userId = PrefUtil.get<String>(PrefProp.USER_ID, null)?: return
        val board = if(myBoard) "${userId}vs$opponentId" else "${opponentId}vs$userId"
        mDatabase.child("matches").child(board).child(userId).setValue(result)
            .addOnSuccessListener { callback.onComplete(true) }
            .addOnFailureListener { callback.onComplete(false) }
    }
}
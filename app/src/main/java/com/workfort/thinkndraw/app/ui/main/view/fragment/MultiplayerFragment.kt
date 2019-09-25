package com.workfort.thinkndraw.app.ui.main.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.pref.PrefProp
import com.workfort.thinkndraw.app.data.local.pref.PrefUtil
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.app.ui.main.adapter.UserAdapter
import com.workfort.thinkndraw.app.ui.main.callback.SelectPlayerCallback
import com.workfort.thinkndraw.app.ui.main.viewmodel.MultiplayerViewModel
import com.workfort.thinkndraw.databinding.FragmentMultiplayerBinding
import com.workfort.thinkndraw.databinding.PromptUsersBinding
import com.workfort.thinkndraw.util.helper.ClassifierUtil
import com.workfort.thinkndraw.util.helper.MediaPlayerUtil
import com.workfort.thinkndraw.util.helper.Toaster
import com.workfort.thinkndraw.util.lib.firebase.callback.LastOnlineCallback
import com.workfort.thinkndraw.util.lib.firebase.callback.OnlineUsersCallback
import com.workfort.thinkndraw.util.lib.firebase.callback.UsersCallback
import com.workfort.thinkndraw.util.lib.firebase.util.FirebaseDbUtil
import timber.log.Timber
import java.io.IOException

class MultiplayerFragment: Fragment() {

    private lateinit var mBinding: FragmentMultiplayerBinding

    private lateinit var mMultiplayerViewModel: MultiplayerViewModel

    private lateinit var mUsersDialog: BottomSheetDialog
    private lateinit var mUserAdapter: UserAdapter

    private lateinit var mClassifier: ClassifierUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_multiplayer, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseDbUtil.observeMyConnectivity()
        FirebaseDbUtil.updateFcmToken()

        activity?.let {
            mMultiplayerViewModel = ViewModelProviders.of(it).get(MultiplayerViewModel::class.java)
        }

        initClassifier()
        if(mMultiplayerViewModel.mCurrentPlayerLiveData.value == null) initUsers()

        observeData()

        loadUsers()

        mBinding.btnCheck.setOnClickListener {
            mMultiplayerViewModel.mEndTime = System.currentTimeMillis()
            classify()
        }
        mBinding.btnClear.setOnClickListener { clearPaint() }
    }

    private fun initClassifier() {
        try {
            mClassifier = ClassifierUtil(ClassifierUtil.MODEL_5_CLASS, ClassifierUtil.NUM_CLASSES_5)
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to create ClassifierUtil", Toast.LENGTH_SHORT).show()
            Timber.e(e)
        }
    }

    private fun observeData() {
        mMultiplayerViewModel.mCurrentPlayerLiveData.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer

//            Timber.e("Multiplayer ${it.first} : ${it.second.name}")
            mMultiplayerViewModel.observeChallenge()
        })

        mMultiplayerViewModel.mCurrentChallengeLiveData.observe(viewLifecycleOwner, Observer {
            if(it == null) {
                mBinding.groupCounter.visibility = View.VISIBLE
                mBinding.groupCanvas.visibility = View.INVISIBLE
                return@Observer
            }

//            Timber.e("Multiplayer challenge ${it.first} : ${it.second}")
            mBinding.groupCounter.visibility = View.INVISIBLE
            mBinding.groupCanvas.visibility = View.VISIBLE
            mBinding.layoutMultiplayerResult.containerResult.visibility = View.INVISIBLE
            val question = "Draw ${it.second}"
            mBinding.tvQuestion.text = question
        })

        mMultiplayerViewModel.mResultsLiveData.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer

            renderResult()
        })
    }

    private fun initUsers() {

        mUserAdapter = UserAdapter()
        mUserAdapter.setCallback(object: SelectPlayerCallback {
            override fun onSelect(userId: String, user: UserEntity) {
                mUsersDialog.dismiss()
                val player = Pair(userId, user)
                mMultiplayerViewModel.mCurrentPlayerLiveData.postValue(player)
                mMultiplayerViewModel.selectChallenge()
                mMultiplayerViewModel.inviteToChallenge(user)
                Toaster.showToast("Invitation sent to ${user.name}")
                mMultiplayerViewModel.mStartTime = System.currentTimeMillis()
            }
        })

        val binding = DataBindingUtil.inflate<PromptUsersBinding>(
            LayoutInflater.from(context), R.layout.prompt_users, null, false
        )

        binding.rvUsers.adapter = mUserAdapter

        mUsersDialog = BottomSheetDialog(context!!)
        mUsersDialog.setContentView(binding.root)
        mUsersDialog.show()
    }

    private fun loadUsers() {
        FirebaseDbUtil.getUsers(object: UsersCallback {
            override fun onResponse(users: HashMap<String, UserEntity?>) {
                if(::mUserAdapter.isInitialized) mUserAdapter.setUsers(users)
            }
        })

        FirebaseDbUtil.getOnlineUsers(object: OnlineUsersCallback {
            override fun onResponse(userIds: ArrayList<String>) {
                if(::mUserAdapter.isInitialized) mUserAdapter.setOnlineStatus(userIds)
            }
        })

        FirebaseDbUtil.getLastOnline(object: LastOnlineCallback {
            override fun onResponse(lastOnlineList: HashMap<String, Long>) {
                if(::mUserAdapter.isInitialized) mUserAdapter.setLastSeen(lastOnlineList)
            }
        })
    }

    private fun classify() {
        val image = mBinding.paintView.exportToBitmap(
            ClassifierUtil.IMG_WIDTH, ClassifierUtil.IMG_HEIGHT
        )

        val result = mClassifier.classify(image)
        mMultiplayerViewModel.saveChallengeResult(result)
        mBinding.groupCounter.visibility = View.INVISIBLE
        mBinding.groupCanvas.visibility = View.INVISIBLE
        mBinding.layoutMultiplayerResult.containerResult.visibility = View.VISIBLE
    }

    private fun renderResult() {
        val opponent = mMultiplayerViewModel.mCurrentPlayerLiveData.value?: return
        mMultiplayerViewModel.mResultsLiveData.value?.let { results ->
            results.forEach {
                val resultView = mBinding.layoutMultiplayerResult
                val timeStr = "${(it.value?.time?: 0L) / 1000 } second(s)"
                if(it.key == opponent.first) {
                    resultView.pbPlayer2.visibility = View.GONE
                    resultView.tvPlayer2Name.text = opponent.second.name
                    resultView.tvPlayer2Class.text = it.value?.className
                    resultView.tvPlayer2Accuracy.text = it.value?.accuracy.toString()
                    resultView.tvPlayer2Time.text = timeStr
                } else {
                    resultView.pbPlayer1.visibility = View.GONE
                    val name = PrefUtil.get(PrefProp.USER_NAME, "") + "(me)"
                    resultView.tvPlayer1Name.text = name
                    resultView.tvPlayer1Class.text = it.value?.className
                    resultView.tvPlayer1Accuracy.text = it.value?.accuracy.toString()
                    resultView.tvPlayer1Time.text = timeStr
                }
            }

            if(results.size > 1) {
                val playerIds = results.keys.toTypedArray()
                val resultValues = results.values.toTypedArray()
                val player1Accuracy = resultValues[0]?.accuracy?: 0f
                val player2Accuracy = resultValues[1]?.accuracy?: 0f

                var winner = false
                val finalResult = if(player1Accuracy == player2Accuracy) {
                    "Draw"
                } else if(player1Accuracy > player2Accuracy) {
                    if(playerIds[0] == opponent.first) "Couldn't win this time"
                    else {
                        winner = true
                        "Yes, you are the winner!"
                    }
                } else {
                    if(playerIds[0] == opponent.first) {
                        winner = true
                        "Yes, you are the winner!"
                    }
                    else "Couldn't win this time"
                }

                MediaPlayerUtil.play(
                    context!!, if(winner) R.raw.sound_success else R.raw.sound_failed
                )

                AlertDialog.Builder(context!!)
                    .setTitle("Result")
                    .setMessage(finalResult)
                    .setPositiveButton("Go to home") { _, _ ->
                        mMultiplayerViewModel.clearData()
                        findNavController().navigate(
                            MultiplayerFragmentDirections.fragmentMultiplayerToFragmentHome()
                        )
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun clearPaint() {
        mBinding.paintView.clear()
        mBinding.tvAnswer.text = ""
    }

}
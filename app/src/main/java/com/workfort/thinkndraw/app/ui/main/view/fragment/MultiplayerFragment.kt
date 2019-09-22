package com.workfort.thinkndraw.app.ui.main.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.pref.PrefProp
import com.workfort.thinkndraw.app.data.local.pref.PrefUtil
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.app.ui.main.adapter.UserAdapter
import com.workfort.thinkndraw.app.ui.main.callback.SelectPlayerCallback
import com.workfort.thinkndraw.app.ui.main.view.viewmodel.MultiplayerViewModel
import com.workfort.thinkndraw.databinding.FragmentMultiplayerBinding
import com.workfort.thinkndraw.databinding.PromptUsersBinding
import com.workfort.thinkndraw.util.helper.ClassifierUtil
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
        initUsers()

        observeData()

        loadUsers()

        mBinding.btnCheck.setOnClickListener { classify() }
        mBinding.btnClear.setOnClickListener { clearPaint() }
    }

    private fun initClassifier() {
        try {
            mClassifier = ClassifierUtil(activity!!)
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

            Timber.e("Multiplayer challenge ${it.first} : ${it.second}")
            mBinding.groupCounter.visibility = View.INVISIBLE
            mBinding.groupCanvas.visibility = View.VISIBLE
            val question = "Draw ${it.second} with 90% accuracy"
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
                val player = Pair(userId, user)
                mMultiplayerViewModel.mCurrentPlayerLiveData.postValue(player)
                mMultiplayerViewModel.selectChallenge()
                mMultiplayerViewModel.inviteToChallenge(user)
                Toaster.showToast("Invitation sent to ${user.name}")
            }
        })

        val binding = DataBindingUtil.inflate<PromptUsersBinding>(
            LayoutInflater.from(context), R.layout.prompt_users, null, false
        )

        binding.rvUsers.adapter = mUserAdapter

        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(binding.root)
        dialog.show()
    }

    private fun loadUsers() {
        FirebaseDbUtil.getUsers(object: UsersCallback {
            override fun onResponse(users: HashMap<String, UserEntity?>) {
                mUserAdapter.setUsers(users)
            }
        })

        FirebaseDbUtil.getOnlineUsers(object: OnlineUsersCallback {
            override fun onResponse(userIds: ArrayList<String>) {
                mUserAdapter.setOnlineStatus(userIds)
            }
        })

        FirebaseDbUtil.getLastOnline(object: LastOnlineCallback {
            override fun onResponse(lastOnlineList: HashMap<String, Long>) {
                mUserAdapter.setLastSeen(lastOnlineList)
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
                if(it.key == opponent.first) {
                    resultView.pbPlayer2.visibility = View.GONE
                    resultView.tvPlayer2Name.text = opponent.second.name
                    resultView.tvPlayer2Class.text = it.value?.className
                    resultView.tvPlayer2Accuracy.text = it.value?.accuracy.toString()
                } else {
                    resultView.pbPlayer1.visibility = View.GONE
                    val name = PrefUtil.get(PrefProp.USER_NAME, "") + "(me)"
                    resultView.tvPlayer1Name.text = name
                    resultView.tvPlayer1Class.text = it.value?.className
                    resultView.tvPlayer1Accuracy.text = it.value?.accuracy.toString()
                }
            }
        }
    }

    private fun clearPaint() {
        mBinding.paintView.clear()
        mBinding.tvAnswer.text = ""
    }

}
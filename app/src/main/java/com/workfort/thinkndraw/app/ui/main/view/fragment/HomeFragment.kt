package com.workfort.thinkndraw.app.ui.main.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.workfort.thinkndraw.R
import com.workfort.thinkndraw.app.data.local.pref.PrefProp
import com.workfort.thinkndraw.app.data.local.pref.PrefUtil
import com.workfort.thinkndraw.app.data.local.user.UserEntity
import com.workfort.thinkndraw.app.ui.quiz.view.activity.QuizActivity
import com.workfort.thinkndraw.databinding.FragmentHomeBinding
import com.workfort.thinkndraw.databinding.PromptInputNameBinding
import com.workfort.thinkndraw.util.lib.firebase.util.FirebaseDbUtil
import com.workfort.thinkndraw.util.helper.ImageLoader
import com.workfort.thinkndraw.util.lib.firebase.callback.AddUserCallback
import java.util.*
import kotlin.random.Random

class HomeFragment: Fragment() {

    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ImageLoader.loadGif(R.drawable.img_draw, mBinding.imgBg)

        activity?.let {

        }

        mBinding.btnStart.setOnClickListener {
            startActivity(Intent(context, QuizActivity::class.java))
        }

        mBinding.btnMultiplayer.setOnClickListener { startMultiplayer() }

        mBinding.btnChallenge.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.fragmentHomeToFragmentChallenge()
            )
        }

    }

    private fun startMultiplayer() {

        PrefUtil.get<String>(PrefProp.USER_NAME, null).let { username ->
            if (TextUtils.isEmpty(username)) inputUsername()
            else {
                findNavController().navigate(
                    HomeFragmentDirections.fragmentHomeToFragmentMultiplayer()
                )
            }
        }
    }

    private fun inputUsername() {
       val binding = DataBindingUtil.inflate<PromptInputNameBinding>(
           layoutInflater, R.layout.prompt_input_name, null, false
       )

        val dialog = AlertDialog.Builder(context!!)
            .setView(binding.root)
            .create()

        binding.btnNext.setOnClickListener {
            val name = binding.etName.text
            if(TextUtils.isEmpty(name)) {
                binding.tilName.error = "Name required"
            } else {
                binding.tilName.error = null

                val id = Random.nextInt(1000, 9999).toString()
                val nameStr = name.toString().toUpperCase(Locale.getDefault())

                FirebaseDbUtil.addUser(id, UserEntity(nameStr), object: AddUserCallback {
                    override fun onComplete(success: Boolean) {
                        if(success) {
                            PrefUtil.set(PrefProp.USER_ID, id)
                            PrefUtil.set(PrefProp.USER_NAME, nameStr)

                            dialog.dismiss()
                            startMultiplayer()
                        } else {
                            Toast.makeText(
                                context!!, "Opps, try again", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }
        }

        dialog.show()
    }

}
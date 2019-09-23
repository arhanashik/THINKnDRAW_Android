package com.workfort.thinkndraw.util.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import java.util.ArrayList

import androidx.fragment.app.Fragment

object PermissionUtil {

    private const val REQUEST_CODE_PERMISSION_DEFAULT = 1

    fun request(
        activity: Activity,
        requestCode: Int = REQUEST_CODE_PERMISSION_DEFAULT,
        vararg permissions: String
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val finalArgs = ArrayList<String>()
        for (permission in permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                finalArgs.add(permission)
            }
        }

        if (finalArgs.isEmpty()) return

        ActivityCompat.requestPermissions(activity, finalArgs.toTypedArray(), requestCode)
    }

    fun request(
        fragment: Fragment,
        requestCode: Int = REQUEST_CODE_PERMISSION_DEFAULT,
        vararg permissions: String
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val finalArgs = ArrayList<String>()
        for (aStr in permissions) {
            if (fragment.context?.checkSelfPermission(aStr) != PackageManager.PERMISSION_GRANTED) {
                finalArgs.add(aStr)
            }
        }

        if (finalArgs.isEmpty()) return

        fragment.requestPermissions(finalArgs.toTypedArray(), requestCode)
    }

    fun isAllowed(context: Context, permission: String): Boolean {

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}

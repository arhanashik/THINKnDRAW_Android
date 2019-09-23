package com.workfort.thinkndraw.util.view

import android.app.Dialog
import android.content.DialogInterface
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FullScreenBottomSheetDialog(private val parent: View) : BottomSheetDialogFragment() {

    var mDismissListener: DismissCallback? = null

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        dialog.setContentView(parent)

        val params = parent.layoutParams as FrameLayout.LayoutParams

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        params.height = screenHeight
        parent.layoutParams = params

        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        mDismissListener?.onDismiss()
    }

    interface DismissCallback {
        fun onDismiss()
    }
}
package com.workfort.thinkndraw.util.helper

import android.content.Context
import android.widget.Toast
import com.workfort.thinkndraw.ThinknDrawApp

/*
*  ****************************************************************************
*  * Created by : arhan on 2019-05-12 at 11:09.
*  * Email : ashik.pstu.cse@gmail.com
*  *
*  * This class is for: 
*  * 1.
*  * 2.
*  * 3.
*  * 
*  * Last edited by : arhan on 2019-05-12.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

object Toaster {

    private fun getDefaultContext(): Context {
        return ThinknDrawApp.getApplicationContext()
    }

    fun showToast(message: String, context: Context = getDefaultContext()) {
        show(context, message, Toast.LENGTH_SHORT)
    }

    fun showToast(message: Int, context: Context = getDefaultContext()) {
        show(context, message, Toast.LENGTH_SHORT)
    }

    fun showLongToast(message: String, context: Context = getDefaultContext()) {
        show(context, message, Toast.LENGTH_LONG)
    }

    fun showLongToast(message: Int, context: Context = getDefaultContext()) {
        show(context, message, Toast.LENGTH_LONG)
    }

    private fun show(context: Context, message: String, length: Int) {
        Toast.makeText(context, message, length).show()
    }

    private fun show(context: Context, message: Int, length: Int) {
        Toast.makeText(context, message, length).show()
    }

}
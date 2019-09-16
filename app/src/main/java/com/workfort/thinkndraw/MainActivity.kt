package com.workfort.thinkndraw

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { paint_view.clear() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_normal -> {
                paint_view.normal()
                true
            }
            R.id.action_emboss -> {
                paint_view.emboss()
                true
            }
            R.id.action_blur -> {
                paint_view.blur()
                true
            }
            R.id.action_clear -> {
                paint_view.clear()
                true
            }
            R.id.action_save -> {
                saveDrawing()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("DEPRECATION")
    private fun saveDrawing() {
        val path = Environment.getExternalStorageDirectory().absolutePath
        val file = File("$path/THINKnDRAW.png")
        try {
            val outStream = FileOutputStream(file)
            file.createNewFile()
            val drawingBmp = paint_view.getBitmap()
            drawingBmp?.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
            outStream.flush()
            outStream.close()
            Toast.makeText(this, "image saved", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }
}

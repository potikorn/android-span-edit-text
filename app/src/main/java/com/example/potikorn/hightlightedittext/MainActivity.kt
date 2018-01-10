package com.example.potikorn.hightlightedittext

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTest.textChangeListener = {
            Log.e("BEST", "Enter this")
            when {
                editTextTest.length() == 0 -> {
                    tvWordCount.text = getString(R.string.max_count)
                    tvWordCount.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.tab_indicator_text))
                }
                editTextTest.length() in 1..160 -> {
                    tvWordCount.text = "${getString(R.string.max_count)} (${160 - editTextTest.length()} ${getString(R.string.remaining)})"
                    tvWordCount.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.tab_indicator_text))
                }
                editTextTest.length() > 160 -> {
                    val end = editTextTest.selectionEnd
                    val flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    editTextTest.text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.holo_red_dark)), 160, end, flag)
                    editTextTest.text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, android.R.color.holo_red_light)), 160, end, flag)
                    tvWordCount.text = "${getString(R.string.max_count)} (${editTextTest.text.length - 160} ${getString(R.string.over)})"
                    tvWordCount.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))
                }
            }

        }


    }
}

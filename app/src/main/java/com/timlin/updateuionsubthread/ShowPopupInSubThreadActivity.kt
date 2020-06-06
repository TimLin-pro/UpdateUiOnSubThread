package com.timlin.updateuionsubthread

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.TypedValue
import android.view.Gravity
import android.widget.PopupWindow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_show_popup_in_sub_thread.*

private const val MSG_SHOW_POPUP = 1
private const val MSG_UPDATE_POPUP = 2
class ShowPopupInSubThreadActivity : AppCompatActivity() {
    private lateinit var mSubThreadHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_popup_in_sub_thread)
        showPopupWindowInSubThread()
    }

    private fun showPopupWindowInSubThread() {
        val handlerThread = HandlerThread("handler thread")
        handlerThread.start()

        val popupWindow = PopupWindow(null, resources.displayMetrics.widthPixels, 100, false)

        mSubThreadHandler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_SHOW_POPUP -> {
                        popupWindow
                                .apply {
                                    val tv = TextView(this@ShowPopupInSubThreadActivity)
                                    tv.text = "show Popup window"
                                    tv.setTextColor(Color.BLACK)
                                    tv.textSize = TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            10f,
                                            resources.displayMetrics
                                    )
                                    contentView = tv
                                    contentView.setBackgroundColor(Color.LTGRAY)
                                    showAtLocation(btnShowViewInSubThread, Gravity.CENTER, 200, 0)
                                }
                    }
                    MSG_UPDATE_POPUP -> {
                        popupWindow
                                .apply {
                                    contentView.setBackgroundColor(Color.CYAN)
                                }
                    }
                }
            }
        }

        btnShowViewInSubThread.setOnClickListener {
            mSubThreadHandler.sendEmptyMessage(MSG_SHOW_POPUP)
        }
        btnUpdatePopupInSubThread.setOnClickListener {
            mSubThreadHandler.sendEmptyMessage(MSG_UPDATE_POPUP)
        }
    }
}
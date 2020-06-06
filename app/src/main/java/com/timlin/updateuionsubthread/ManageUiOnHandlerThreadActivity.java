package com.timlin.updateuionsubthread;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Printer;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.timlin.updateuionsubthread.databinding.ActivityManageUiOnHandlerThreadBinding;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by linjintian on 2020/04/18.
 */
@SuppressLint("LongLogTag")
public class ManageUiOnHandlerThreadActivity extends AppCompatActivity {
    private static final String TAG = "ManageUiOnHandlerThreadActivity";
    public static final int CREATE_VIEW = 1;
    public static final int UPDATE_VIEW = 2;
    private Handler mHandler;
    private TextView mTextView;
    private WindowManager mWindowManager;
    //使用 viewbinding
    private ActivityManageUiOnHandlerThreadBinding mBinding;
    private HandlerThread mHandlerThread;
    private boolean mHasClickCreateViewBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityManageUiOnHandlerThreadBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initClickListener();
//        initTextView();
        mWindowManager = getWindowManager();
        mHandlerThread = new HandlerThread("my handler thread");
        mHandlerThread.start();

        initHandler(mHandlerThread);
        printLooperLog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandlerThread.quitSafely();
    }

    private void initClickListener() {
        mBinding.btnCreateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHasClickCreateViewBtn = true;
                Log.d(TAG, "onClick: create view");
                mHandler.sendEmptyMessage(CREATE_VIEW);
            }
        });

        mBinding.btnSubthreadUpdateUi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasClickCreateViewBtn){
                    Toast.makeText(ManageUiOnHandlerThreadActivity.this, "需要先 add View 然后再 update", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "onClick: update view");
                mHandler.sendEmptyMessage(UPDATE_VIEW);
            }
        });
        mBinding.btnCreateViewInNonLooperThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 如果没有调用 Looper#prepare，会导致 创建 ViewRootImpl 过程中实例化 ViewRootHandler 的步骤 crash
                        // （因为不能在没有 Looper 的线程通过无参的构造函数去创建一个 Handler）
                        Looper.prepare();

                        v.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ManageUiOnHandlerThreadActivity.this, "没有开启消息循环，队列中监听垂直同步信号的消息无法被执行，也就无法更新 UI", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d(TAG, "run: create View in thread");
                        final TextView textView = new TextView(ManageUiOnHandlerThreadActivity.this);
                        textView.setText("view created in sub thread");

                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
                        layoutParams.format = PixelFormat.TRANSPARENT;//设置为 透明，默认效果是 黑色的
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//设置window透传，也就是当前view所在的window不阻碍底层的window获得触摸事件。

                        mWindowManager.addView(textView, layoutParams);
                    }
                }).start();
            }
        });
    }

    private void initTextView() {
        mTextView = new TextView(ManageUiOnHandlerThreadActivity.this);
        mTextView.setTextColor(Color.BLACK);
        mTextView.setBackground(new ColorDrawable(Color.CYAN));
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageUiOnHandlerThreadActivity.this, "I am clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initHandler(final HandlerThread handlerThread) {
        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "handleMessage: " + msg);
                super.handleMessage(msg);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
                layoutParams.format = PixelFormat.TRANSPARENT;//设置为 透明，默认效果是 黑色的
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//设置window透传，也就是当前view所在的window不阻碍底层的window获得触摸事件。
                switch (msg.what) {
                    case CREATE_VIEW:
                        initTextView();
                        Log.d(TAG, "handleMessage: CREATE_VIEW");
                        mTextView.setText("created at non-ui-thread");
                        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity = Gravity.CENTER;
                        mWindowManager.addView(mTextView, layoutParams);
                        break;
                    case UPDATE_VIEW:
                        Log.d(TAG, "handleMessage: UPDATE_VIEW");
                        mTextView.setText("updated at non-ui-thread");
                        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity = Gravity.END;
                        mWindowManager.updateViewLayout(mTextView, layoutParams);
                        break;
                }
            }
        };
    }

    /**
     * 打印指定 Looper 中 MessageQueue 中的 Message 信息
     */
    private void printLooperLog() {
        mHandler.getLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {
                if (x == null) {
                    return;
                }
                Log.d(TAG, "Looper MessageLogging : " + x);
            }
        });
    }

}
package com.timlin.updateuionsubthread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.timlin.updateuionsubthread.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //ViewRootImpl 还没创建出来的时候，在子线程更新 ImageView，将 xml 中指定的微信图标修改为 QQ
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBinding.iv.setImageResource(R.drawable.ic_qq);//更新 ui
            }
        }).start();
        initListener();
    }

    private void initListener() {
        mBinding.btnEntrance1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ManageUiOnHandlerThreadActivity.class));
            }
        });
        mBinding.btnEntranceUpdatePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowPopupInSubThreadActivity.class));
            }
        });
    }
}
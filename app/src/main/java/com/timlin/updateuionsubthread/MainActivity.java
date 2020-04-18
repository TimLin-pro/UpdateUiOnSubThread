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

        initListener();
    }

    private void initListener() {
        mBinding.btnEntrance1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ManageUiOnHandlerThreadActivity.class));
            }
        });
    }
}
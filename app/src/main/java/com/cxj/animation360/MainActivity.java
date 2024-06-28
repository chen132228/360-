package com.cxj.animation360;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cxj.animation360.service.MyFloatService;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button show_float;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();//去掉标题栏

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        show_float = findViewById(R.id.show_float);
    }

    private void initData() {
        show_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MyFloatService.class);
                startService(intent);
//                finish();
            }
        });
    }

}
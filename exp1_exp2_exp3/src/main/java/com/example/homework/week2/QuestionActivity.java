package com.example.homework.week2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homework.R;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {
    Button yesButton, noButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自动适应横竖屏24
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        setContentView(R.layout.activity_question);
        yesButton = findViewById(R.id.yes_button);
        noButton = findViewById(R.id.no_button);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        Log.d("Lifecycle", "onCreate: " + getClass().getSimpleName());
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.yes_button) {
            //TODO 进行活动的跳转
            //无需传递数据,单纯一个Intent意图即可.
            startActivity(new Intent(this, QuestionActivity2.class));
        } else if (v.getId() == R.id.no_button) {
            //TODO 当前活动的结束
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle", "onStart: " + getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume: " + getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause: " + getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Lifecycle", "onStop: " + getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "onDestroy: " + getClass().getSimpleName());
    }


}
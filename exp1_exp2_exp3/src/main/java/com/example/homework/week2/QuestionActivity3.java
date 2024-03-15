package com.example.homework.week2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homework.R;

public class QuestionActivity3 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);


        setContentView(R.layout.activity_question3);



        Button quit = findViewById(R.id.quit);
        Button restart = findViewById(R.id.restart);
        quit.setOnClickListener(this);
        restart.setOnClickListener(this);
        //TODO:获取上个活动传过来的bundle,this.getIntent()
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        String score = (String) bundle.getString("score");
        String questions = (String) bundle.getString("questions");
        String[] prompts = bundle.getStringArray("prompt");
        String[] wrongs = bundle.getStringArray("wrong");
        String[] rights = bundle.getStringArray("right");
        showAllArraysContent(prompts,wrongs, rights);
        TextView textView = findViewById(R.id.score_num);
        textView.setText(score);
        assert score != null;
        assert questions != null;
        Toast.makeText(this, "你的正确率: " + new DecimalFormat("00.00%").format(Double.parseDouble(score) / Double.parseDouble(questions)) + "🎇", Toast.LENGTH_LONG).show();
        //TODO:点击quit按钮 直接退出app finish()
        //TODO:点击RESTART按钮,需要传递信息返回然后将之前的index和score重新初始化





        Log.d("Lifecycle", "onCreate: " + getClass().getSimpleName());

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.quit) {
            finish();
        } else if (v.getId() == R.id.restart)
            startActivity(new Intent(this, QuestionActivity2.class));
    }

    private void showAllArraysContent(String[]... strArrays) {
        LinearLayout linearLayout = findViewById(R.id.scrowview_inner_layout);
        for (String[] strArray : strArrays) {
            String name = strArray[strArray.length - 1];
            TextView nameTextView = new TextView(this);
            nameTextView.setTextSize(40);
            if (name.equals("wrong")){
                nameTextView.setTextColor(Color.parseColor("#fa758c"));
            }else if (name.equals("right")){
                nameTextView.setTextColor(Color.parseColor("#9AFF9A"));
            }else if (name.equals("prompt")){
                nameTextView.setTextColor(Color.parseColor("#FFFAF0"));
            }
            nameTextView.setText(name);
            linearLayout.addView(nameTextView);

            for (int i = 0;i<strArray.length-1;i++) {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                textView.setTextSize(30);
                textView.setText(strArray[i]);
                linearLayout.addView(textView);
            }
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
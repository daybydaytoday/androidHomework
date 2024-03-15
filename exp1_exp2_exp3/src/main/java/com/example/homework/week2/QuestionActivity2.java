package com.example.homework.week2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homework.R;

import java.util.ArrayList;

public class QuestionActivity2 extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {
    Boolean submitFlag = true;
    // 获取答案和问题数组
    String[] questions;
    String[] answers;
    TextView questionText;
    TextView answer_input;
    int score; // 记录分数.
    int index = 0; // 问题数组的下标
    boolean[] isAnswered;
    ArrayList<String> answerTemps;
    String[] hints;
    String[] prompt;
    String[] wrong;
    String[] right;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        int orientation = getResources().getConfiguration().orientation;



        setContentView(R.layout.activity_question2);
        //数组资源的获取
        answers = getResources().getStringArray(R.array.answers);
        questions = getResources().getStringArray(R.array.questions);
        isAnswered = new boolean[questions.length];
        questionText = findViewById(R.id.question_text);
        answerTemps = new ArrayList<>();
        questionText.setText((index + 1) + " : " + questions[index]);//初始第一个问题
        answer_input = findViewById(R.id.answer_input);
        hints = new String[questions.length];
        //末尾用来存放数组的名字
        prompt = new String[questions.length + 1];
        wrong = new String[questions.length + 1];
        right = new String[questions.length + 1];
        Button nextButton = findViewById(R.id.next);
        Button preButton = findViewById(R.id.pre);
        Button submitButton = findViewById(R.id.submit);
        ToggleButton lockButton = findViewById(R.id.lock);
        Button promptButton = findViewById(R.id.prompt_call);
        submitButton.setOnClickListener(this);
        preButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        answer_input.setOnEditorActionListener(this);
        answer_input.setOnFocusChangeListener(this);
        lockButton.setOnCheckedChangeListener(this);
        promptButton.setOnClickListener(this);
        //TODO 用户可能不会点击< 或者 > 而是直接提交,所以必须显示本次他输入的答案和判断分数


        Log.d("Lifecycle", "onCreate: " + getClass().getSimpleName());
    }
    @SuppressLint("SetTextI18n")//硬编码导致的国际化问题暂时忽略
    @Override
    public void onClick(View v) {
        //TODO 不能点击pre或者next才会计算分数,有可能有人回答1题直接提交
        //TODO 作答过的题目hint显示之前输入的答案
        if (v.getId() == R.id.submit) {
            //停止问题循环
            submitFlag = false;
            //TODO 活动的跳转结算页面,但是需要存储得分信息,也就是需要Bundle来存储得分数据
            Bundle bundle = new Bundle();
            bundle.putString("score", String.valueOf(score));
            bundle.putString("questions", String.valueOf(questions.length));
            prompt[prompt.length - 1] = "prompt";
            wrong[wrong.length - 1] = "wrong";
            right[right.length - 1] = "right";
            bundle.putStringArray("prompt", prompt);
            bundle.putStringArray("wrong", wrong);
            bundle.putStringArray("right", right);
            Intent intent = new Intent(this, QuestionActivity3.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//栈中所有实例都被清空,同时开辟新任务的活动栈
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.next && submitFlag) {
            //TODO 下一个问题 , 在这里需要顺便判断答案的正确错误与否,判断正确与否应该是在index进行操作之前,取出文本域的内容进行判断2
            index = (++index + questions.length) % questions.length;
            questionText.setText((index + 1) + " : " + questions[index]);
            showHint(index);
        } else if (v.getId() == R.id.pre && submitFlag) {
            //TODO 上一个问题 , 在这里需要顺便判断答案的正确错误与否
            index = Math.abs(--index + questions.length) % questions.length;
            questionText.setText((index + 1) + " : " + questions[index]);
            showHint(index);
        } else if (v.getId() == R.id.prompt_call) {  // 提供场外援助
//            根据answer[]显示当前index的答案
            answer_input.setText("");
            answer_input.setHint("提示: " + questions[index] + " " + answers[index]);
            //进行提示之后,提示的数组在该index下设置为true
            prompt[index] = index + ":" + questions[index];
        }
    }
    //TODO 这里有个很恶心的细节就是必须置为空才能显示!!
    private void showHint(int index) {
        if (isAnswered[index]) {
            answer_input.setText(null);
            answer_input.setHint(hints[index]);
        } else {
            answer_input.setText(null);
            answer_input.setHint(R.string.hint);
        }
    }
    private void check(int index) { //TODO 记录得分
        if (answerTemps.get(index).equals(answers[index])) {
            score++;
            right[index] = index + ":" + questions[index];
        } else {
            if (score > 0) {
                score--;
                wrong[index] = index + ":" + questions[index];
            }
        }
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // 隐藏输入法键盘
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            // 设置提示
            hints[index] = "你输入的答案: " + answer_input.getText();
            answerTemps.add(index, answer_input.getText().toString());
            check(index);
            isAnswered[index] = true;
            answer_input.clearFocus(); // 失去焦点
            showHint(index);
            return true;
        }
        return false;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            showHint(index);
        }
    }
    //TODO 选中 lock 禁用文本框输入
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isChecked = buttonView.isChecked();
        if (isChecked) {
            answer_input.setEnabled(false);
        } else {
            answer_input.setEnabled(true);
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

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("score",score);
//        outState.putInt("index",index);
//        outState.putStringArrayList("answerTemps",answerTemps);
//        outState.putStringArray("hints",hints);
//        outState.putStringArray("prompt",prompt);
//        outState.putStringArray("right",right);
//        outState.putStringArray("wrong",wrong);
//        outState.putBooleanArray("isAnswered", isAnswered);
//    }
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        score = savedInstanceState.getInt("score");
//        index = savedInstanceState.getInt("index");
//        answerTemps = savedInstanceState.getStringArrayList("answerTemps");
//        hints = savedInstanceState.getStringArray("hints");
//        prompt=savedInstanceState.getStringArray("prompt");
//        right = savedInstanceState.getStringArray("right");
//        wrong=savedInstanceState.getStringArray("wrong");
//        isAnswered = savedInstanceState.getBooleanArray("isAnswered");
//        // 在此处恢复其他需要的数据
//        answer_input.setHint(hints[index]);
//
//    }



}
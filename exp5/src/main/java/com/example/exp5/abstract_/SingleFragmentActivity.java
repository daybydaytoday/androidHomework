package com.example.exp5.abstract_;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.exp5.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_input);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        //因为我是用代码形式加载的,所以fragment没有内容
        if (fragment == null) {
            fragment = createFragment(); // 这里就是耦合性很强了,换成createFragment解决了问题
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();//创建一个新的Fragment事务,添加对应的布局然后提交
        }
    }
    protected abstract Fragment createFragment();  // 为了填充具体的Fragment
}

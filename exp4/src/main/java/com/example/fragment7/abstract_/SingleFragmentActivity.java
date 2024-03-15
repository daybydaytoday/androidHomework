package com.example.fragment7.abstract_;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fragment7.R;
// 用来解决 Fragment代码冗余的问题
//

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //切换横竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);


        //获取Fragment 来管理Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);// 加载的是活动的装载那个 fragment的 布局
        if (fragment == null) {
            fragment = createFragment();  // 这里就是this了
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
            //事务管理fragment的动态移除,添加,附加,分离,替换fragment队列中的fragment,为了动态组装和重新组装
            //可理解为创建事务,执行添加方法,然后提交事务
        }
    }
}

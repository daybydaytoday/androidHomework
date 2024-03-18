package com.example.exp5.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.exp5.R;
import com.example.exp5.activity.StuListActivity;
import com.example.exp5.database.StuSqlHelper;
import com.example.exp5.model.Stu;
import com.example.exp5.model.StuLab;

public class StuInputFragment extends Fragment implements TextWatcher, View.OnClickListener {
    private Stu stu;
    private EditText id_edit;
    private EditText name_edit;
    private EditText tel_edit;
    private StuSqlHelper stuSqlHelper;

    public StuInputFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onStart();
        stu = new Stu();
        stuSqlHelper = StuSqlHelper.getInstance(getActivity());
        //打开读写
        stuSqlHelper.openRead();
        System.out.println("  stuSqlHelper.getmRDB() 输入的mRDB 有了吗 = " + stuSqlHelper.getmRDB());

    }
    @Override
    public void onStop() {
        super.onStop();
        //stuSqlHelper.close();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stuSqlHelper != null) {
            stuSqlHelper.close();
        }
    }

    //这个生命周期方法才会加载视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //记载Fragment类对应的布局
        View view = inflater.inflate(R.layout.fragment_stu_input, container, false);
        //Fragment布局取出里面的小组件

        id_edit = view.findViewById(R.id.id_edit);
        name_edit = view.findViewById(R.id.name_edit);
        tel_edit = view.findViewById(R.id.tel_edit);


        //给三个文本框编辑栏添加监听器
        id_edit.addTextChangedListener(this);
        name_edit.addTextChangedListener(this);
        tel_edit.addTextChangedListener(this);
// TODO  修改的时候，原来数据的回显
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String id = bundle.getString("id");
                String name = bundle.getString("name");
                String tel = bundle.getString("tel");
                String img = bundle.getString("img");
                System.out.println("img = " + img);
                // TODO 让图片能够放在当前学生的对象中 ，等会跳转回去是会在ListFragment回显的 有用的
                stu.setImg(img);
                System.out.println("stu 有img吗? = " + stu);
                System.out.println("长按头像回显有img属性？ = " + stu.getImg());
                id_edit.setText(id);
                name_edit.setText(name);
                tel_edit.setText(tel);
            }
        }
        Button ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(this);

        Button pink = view.findViewById(R.id.pink);
        Button blue = view.findViewById(R.id.blue);
        pink.setOnClickListener(this);
        blue.setOnClickListener(this);
        return view;  // 返回加载的视图给谁
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //当文本变化的时候,将stu对象设置对应的属性值
        stu.setId(id_edit.getText().toString());
        stu.setName(name_edit.getText().toString());
        stu.setTel(tel_edit.getText().toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        //这里是用户输入的内容,是要传到item内的
    }

    private Bundle data = new Bundle();

    @Override
    public void onClick(View v) {

        //TODO 点击按钮,具体是将stu对象传递到List活动然后显示,这里有个活动调用
        Intent intent = new Intent(getActivity(), StuListActivity.class);
        if (v.getId() == R.id.ok) {

//            if (stuSqlHelper == null || stuSqlHelper.getmRDB() == null) {
//                Toast.makeText(getActivity(), "数据库mRDB未正确初始化", Toast.LENGTH_SHORT).show();
//                return;
//            }


            data.putString("id", stu.getId());

            // TODO 写上判断该id是否存在本地或者列表中 ??还有远端数据库中
            //TODO 这里添加活动的时候也是需要验证的,万一本地数据库有呢 ? 甚至服务端数据库也有呢
            //TODO 本地数据库添加 根据id搜索的查询

            if (stu.getId().trim().equals("") || stu.getId() == null) {
                Toast.makeText(getActivity(), "请输入学号,不能为空格", Toast.LENGTH_SHORT).show();
                return;
            }
            System.out.println("stuSqlHelper 被实例化了吗 = " + stuSqlHelper);
            System.out.println("输入活动的stuSqlHelper = " + stuSqlHelper);
            System.out.println("stuSqlHelper        selectById  = " + stuSqlHelper);
            Stu selectById = stuSqlHelper.selectById(stu.getId());
            System.out.println("selectById本地有这个学生吗 = " + selectById);
            boolean isInRecycleView = false;
            StuLab stuLab = StuLab.get(getActivity());
            for (int i = 0; i < stuLab.getStus().size(); i++) {
                if (stu.getId().equalsIgnoreCase(stuLab.getStus().get(i).getId())) {
                    isInRecycleView = true;
                }
            }
            if (isInRecycleView) {
                Toast.makeText(getActivity(), "该学生已经在列表中", Toast.LENGTH_SHORT).show();
                return;
            } else if (selectById != null) {
                Toast.makeText(getActivity(), "该学生在本地数据中已存在", Toast.LENGTH_SHORT).show();
                return;
            } // TODO 远端数据库的校验


            data.putString("name", stu.getName());
            data.putString("tel", stu.getTel());
            data.putString("img", stu.getImg());
            System.out.println("stu INput ???? = " + stu);
            //TODO 现在是在输入界面，点击ok返回
            intent.putExtras(data);
            startActivity(intent);
        } else if (v.getId() == R.id.blue) {
            //TODO 蓝色按钮返回 blue去那个List活动
            // blue.setEnabled(false);
            stu.setImg("blue");
        } else if (v.getId() == R.id.pink) {
            //TODO 粉按钮返回 pink去那个List活动
            stu.setImg("pink");
            // pink.setEnabled(false);
        }
    }


}
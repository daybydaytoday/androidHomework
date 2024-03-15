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
import androidx.fragment.app.Fragment;
import com.example.exp5.R;
import com.example.exp5.activity.StuListActivity;
import com.example.exp5.model.Stu;
public class StuInputFragment extends Fragment implements TextWatcher, View.OnClickListener {
    private Stu stu;
    private EditText id_edit;
    private EditText name_edit;
    private EditText tel_edit;

    public StuInputFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stu = new Stu();

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
            data.putString("id", stu.getId());
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
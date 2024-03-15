package com.example.fragment7.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.fragment7.R;
import com.example.fragment7.model.Crime;
import com.example.fragment7.model.CrimeLab;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CrimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrimeFragment extends Fragment implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    private Crime crime; // 因为要展示Crime实例的信息
    private EditText titleField;
    private Button dateButton;
    private CheckBox solveCheckBox;

    //使用Bundle传递数据
    private static final String ARG_CRIME_ID = "crime_id";

    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID crime_id) {
        //Bundle先装数据,然后fragment实例携带这个bundle
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crime_id);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 当该界面加载时,实例化
        //其实应该是获取活动的传来的Intent

        // new Fragment实例我需要uuid,谁给我的? FragmentActivity获得一个意图然后拿到数据构建我这个Fragment,我自身返回让Activity去创建
        // 活动在启动的时候,去看SingleFragment代码
        UUID uuid = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(uuid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        //该生命周期才生成Fragment视图并填充布局,第二个参数是视图的父视图,第三个参数是因为用代码添加视图

        titleField = view.findViewById(R.id.crime_title);
        titleField.setText(crime.getmTitle());
        titleField.addTextChangedListener(this);

        dateButton = view.findViewById(R.id.crime_date);
        dateButton.setText(crime.getmDate().toString()); // 模型提供数据,将数据显示在按钮中
        dateButton.setEnabled(false); // 按钮不可编辑

        solveCheckBox = view.findViewById(R.id.crime_solved);
        solveCheckBox.setOnCheckedChangeListener(this);
        solveCheckBox.setChecked(crime.getmSolved());


        // 是因为fragment 才是加载这些小组件的父视图.
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
    @Override
    public void afterTextChanged(Editable s) {
        //这里是那本书遗漏的点
        crime.setmTitle(s.toString());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        crime.setmSolved(isChecked);
        //这里反而是根据按钮点击来对model赋数据
    }
}
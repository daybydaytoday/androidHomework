package com.example.exp5.activity;

import androidx.fragment.app.Fragment;

import com.example.exp5.abstract_.SingleFragmentActivity;
import com.example.exp5.fragment.StuInputFragment;

public class StuInputActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new StuInputFragment();
    }
}
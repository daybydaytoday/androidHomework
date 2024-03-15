package com.example.fragment7.activity;

import androidx.fragment.app.Fragment;

import com.example.fragment7.abstract_.SingleFragmentActivity;
import com.example.fragment7.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

}
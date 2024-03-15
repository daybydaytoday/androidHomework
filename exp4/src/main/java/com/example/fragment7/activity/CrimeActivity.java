package com.example.fragment7.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.fragment7.abstract_.SingleFragmentActivity;
import com.example.fragment7.fragment.CrimeFragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "EXTRA_CRIME_ID";

    public static Intent newIntent(Context othercontext, UUID crimeID) {
        Intent intent = new Intent(othercontext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        // 这里返回的一定要注意了.返回对应的Fragment
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        return CrimeFragment.newInstance(crimeId);
    }

}
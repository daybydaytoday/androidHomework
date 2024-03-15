package com.example.fragment7.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;



    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);

        }
        return sCrimeLab;
    }
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    //    这里的 return 语句只会从当前的 lambda 表达式中返回，而不是从 getCrime() 方法中返回。它实际上等效于一个 lambda 表达式中的局部 return 语句，它只会退出当前的 lambda 表达式，并没有影响到包含它的方法
    public Crime getCrime(UUID uuid) {
        for (Crime crime : mCrimes) {
            if (crime.getmId().equals(uuid)) {
                return crime;
            }
        }
        return null;
    }
}

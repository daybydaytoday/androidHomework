package com.example.exp5.model;

import android.content.Context;

import java.util.ArrayList;


// TODO 显示框的排版问题
// TODO 图片的选择
public class StuLab {
   private ArrayList<Stu> stus;
    private static StuLab sStuLab;
    private StuLab(Context context){
        stus = new ArrayList<>();
        // 我得往stus添加一些数据,这些数据肯定是活动传过来的.
    }
    public static StuLab get(Context context){
        //TODO 耻辱！
//        if ( sStuLab == null) return new StuLab(context);
//        return sStuLab;
        if (sStuLab == null)sStuLab = new StuLab(context);
        return sStuLab;
    }
    public Stu getStu(String id){  // 点击List活动的项,需要回传单个item信息
        for(Stu s : stus){
            if (id.equals(s.getId())){
                return s;
            }
        }
        return null;
    }
    public ArrayList<Stu> getStus(){
        return stus;
    }

    public void setsStuList(ArrayList<Stu> stuList){
        stus = stuList;
    }
    public void setStus(ArrayList<Stu> stus) {
        this.stus = stus;
    }
}

package com.example.exp5.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.exp5.model.Stu;
import com.example.exp5.model.StuLab;

import java.util.ArrayList;
import java.util.List;

public class StuSqlHelper extends SQLiteOpenHelper {
    private static StuSqlHelper stuSqlHelper;
    private static final String DB_NAME = "stu.db";
    private static final String TABLE_NAME = "stu_info";
    private static Integer VERSION = 1;
    private SQLiteDatabase mRDB;
    private SQLiteDatabase wRDB;
    private ArrayList<Stu> stus;

    // TODO 打开数据库的读连接
    public SQLiteDatabase openRead() {
        // 没有读的实例或者未打开
        if (mRDB == null || !mRDB.isOpen()) mRDB = stuSqlHelper.getReadableDatabase();
        return mRDB;
    }

    public SQLiteDatabase writeOpen() {
        // 没有读的实例或者未打开
        if (wRDB == null || !wRDB.isOpen()) wRDB = stuSqlHelper.getWritableDatabase();
        return wRDB;
    }

    // TODO 关闭数据库连接
    public void close() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;  // 方便GC回收
        }
        if (wRDB != null && wRDB.isOpen()) {
            wRDB.close();
            wRDB = null;
        }
    }


    // TODO CRUD
    public long insert(Stu stu) {
        //TODO 循环插入好了,遇到文件中存在的id,就执行update操作
        //TODO 所以还是要进行主键的查询,分成两类,一类是未存在的,直接插入,一类是已存在的,执行update操作
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", stu.getName());
        contentValues.put("id", stu.getId());
        contentValues.put("img", stu.getImg());
        contentValues.put("tel", stu.getTel());
        //TODO 第二个参数就是当什么信息也没插入,那就是自动给个非主键的字段让他插入null结束这条语句
        long l = wRDB.insert(TABLE_NAME, null, contentValues);
        return l;
    }

//    public Stu select(String id) {
//
//    }


    private StuSqlHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
        stus = StuLab.get(context).getStus(); // 获取列表数据
    }

    public static StuSqlHelper getInstance(Context context) {
        //TODO 没什么并发量,不加锁也可以的
        if (stuSqlHelper == null) stuSqlHelper = new StuSqlHelper(context);
        return stuSqlHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + TABLE_NAME + "(id varchar primary key,name varchar(20),tel varchar(11),img varchar)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //TODO 根据id 删除
    public int deleteById(String id) {
        int delete = wRDB.delete(TABLE_NAME, "id=?", new String[]{id});
        return delete;
    }

    public int update(Stu stu) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", stu.getId());
        contentValues.put("name", stu.getName());
        contentValues.put("tel", stu.getTel());
        contentValues.put("img", stu.getImg());
        //TODO 根据id更新
        int update = wRDB.update(TABLE_NAME, contentValues, "id=?", new String[]{stu.getId()});
        return update;
    }

    public SQLiteDatabase getmRDB() {
        return mRDB;
    }

    public SQLiteDatabase getwRDB() {
        return wRDB;
    }

    public void setmRDB(SQLiteDatabase mRDB) {
        this.mRDB = mRDB;
    }

    public void setwRDB(SQLiteDatabase wRDB) {
        this.wRDB = wRDB;
    }

    public List<Stu> selectAll() {
        //TODO 用到游标了
        List<Stu> stusss = new ArrayList<>();
        Cursor cursor = mRDB.query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) { // 下一行有内容

            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String tel = cursor.getString(2);
            String img = cursor.getString(3);
            Stu stu = new Stu(name, tel, id, img);
            stusss.add(stu);
        }
        return stusss;
    }
    public Stu selectById(String id) {
        System.out.println("mRDB 有吗  = " + mRDB);
        Cursor cursor = mRDB.query(TABLE_NAME, null, "id=?", new String[]{id}, null, null, null);
        Stu stu = null;
        while (cursor.moveToNext()){
            String sid = cursor.getString(0);
            String name = cursor.getString(1);
            String tel = cursor.getString(2);
            String img = cursor.getString(3);
            stu = new Stu(name, tel, sid, img);
        }
        return stu;
    }
}

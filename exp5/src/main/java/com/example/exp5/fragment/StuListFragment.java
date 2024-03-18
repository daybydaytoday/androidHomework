package com.example.exp5.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exp5.R;
import com.example.exp5.activity.StuInputActivity;
import com.example.exp5.database.StuSqlHelper;
import com.example.exp5.model.Res;
import com.example.exp5.model.Stu;
import com.example.exp5.model.StuLab;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StuListFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private RecyclerView recyclerView;
    private StuAdapter stuAdapter;
    private final StuLab stuLab = StuLab.get(getActivity());
    //TODO 这样路径才对
    //private String mDataBaseName;
    private StuSqlHelper stuSqlHelper;

    //连接Web
    public StuListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onStart();
        stuSqlHelper = StuSqlHelper.getInstance(getActivity());
        stuSqlHelper.openRead();
        stuSqlHelper.writeOpen();

//        mDataBaseName = getActivity().getFilesDir() + "/stu.db";

    }


    // TODO 该方法仍需要进行数据校验,一个问题是在列表中存在的数据,和数据库中存在的数据冲突,那么有可能不会上传
    public void infoSubmit() {
        if (stuAdapter.getItemCount() == 0) {  // 观察数据列表中的数据有没有呢
            Toast.makeText(getActivity(), "列表中暂时没有数据可上传......", Toast.LENGTH_SHORT).show();
        }
        Log.d("submit", "Start commit................");
        ArrayList<Stu> stus = stuLab.getStus();
        //TODO 进行数据校验  , 访问一次数据库,拿到所有的stu
        ArrayList<Stu> stusNeedload = new ArrayList<>();

        System.out.println("在进行和远程数据库进行数据校验时 stuLab 现在有多少数据 ?= " + stus);

        AtomicReference<ArrayList<Stu>> stuListFromMysql = new AtomicReference<>(null);
        // TODO 控制下方的线程同步,下面有两个线程


        // TODO 发送请求获取信息
        // ...

        Thread thread_getInfoFromMysql = new Thread(() -> {
            //TODO 发送请求

            URL url = null;//生成一个URL实例，指向我们刚才设定的地址URL
            Gson gson = new Gson();
            try {
                url = new URL("http://192.168.232.1:8080/stus/info");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET"); //设置请求方式为
                conn.setReadTimeout(1000);//设置超时信息
                conn.setConnectTimeout(1000);//设置超时信息
                conn.setDoInput(true);//设置输入流，允许输入
                conn.setDoOutput(false);//设置输出流，不允许输出
                conn.setUseCaches(false);//设置POST请求方式不能够使用缓存
                // TODO 接收数据
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();
                String stusJson = response.toString();
                Type type = new TypeToken<List<Stu>>() {
                }.getType();
                stuListFromMysql.set(gson.fromJson(stusJson, type));
                stus.forEach(stu -> {
                    int i = 0;
                    // 在访问 stuListFromMysql 之前，确保它不为 null
                    if (stuListFromMysql.get() != null) {
                        for (; i < stuListFromMysql.get().size(); i++) {
                            // TODO 如果列表中的id在数据库中不存在，则添加进stusNeedload，那么本地又该如何处理
                            if (stu.getId().equalsIgnoreCase(stuListFromMysql.get().get(i).getId())) {
                                break;
                            }
                        }


                        if (i == stuListFromMysql.get().size()) {
                            stusNeedload.add(stu);
                            System.out.println("stusNeedload新成员  = " + stu);
                        }
                    }

                });


            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread_getInfoFromMysql.start();
        System.out.println("添加新成员后stusNeedload = " + stusNeedload);


        //TODO 线程同步
        try {
            thread_getInfoFromMysql.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // TODO 发送POST请求，使用获取到的stus
        Thread thread_PostToMysql = new Thread(() -> {
            try {
                URL url = new URL("http://192.168.232.1:8080/stus/listinfo");//生成一个URL实例，指向我们刚才设定的地址URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); //设置请求方式为post
                conn.setReadTimeout(5000);//设置超时信息
                conn.setConnectTimeout(5000);//设置超时信息
                conn.setDoInput(true);//设置输入流，允许输入
                conn.setDoOutput(true);//设置输出流，允许输出
                conn.setUseCaches(false);//设置POST请求方式不能够使用缓存
                conn.setRequestProperty("Content-Type", "application/json");
                OutputStream out = conn.getOutputStream();
                Gson gson = new Gson();
                // TODO 上传的是列表中不存在的数据
                String jsonStus = gson.toJson(stusNeedload);
                System.out.println("stusNeedload 提交给远程数据库的内容 是 = " + stusNeedload);
                // TODO 如果提交内容是空,服务端会发生sql异常,因为插入空他的sql语句不正确的.

                out.write(jsonStus.getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.close();
                conn.connect();  // 刷新。。。。。。。。。。。。。。。
                //TODO 数据库返回异常
                if (conn.getResponseCode() != 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                    System.out.println("conn.getResponseCode() = " + conn.getResponseCode());
                    String errorMsg = response.toString();
                    Res res = gson.fromJson(errorMsg, Res.class);
                    String errorMessage = res.getMessage();
                    if (errorMessage != null)
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show());
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                    String msg = response.toString();
                    if (msg != null)
                        getActivity().runOnUiThread(
                                () -> Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread_PostToMysql.start();


// 在此处可以继续执行主线程的后续代码


        //TODO 这里需要线程同步,避免主线程先执行,但是子线程还没获取到数据
        //TODO 这里需要线程同步,避免主线程先执行,但是子线程还没获取到数据
        //TODO 这里需要线程同步,避免主线程先执行,但是子线程还没获取到数据
        //TODO 这里需要线程同步,避免主线程先执行,但是子线程还没获取到数据,我直接放入子线程执行


//        List<Stu> stusFromLocal = stuSqlHelper.selectAll();
//        for (int i = 0; i < stusFromLocal.size(); i++) {
//            int i1 = 0;
//            for (; i1 < stusNeedload.size(); i1++) {
//                if (stusFromLocal.get(i).getId().equalsIgnoreCase(stusNeedload.get(i1).getId())){
//                    //TODO  那么我得把这个数据从stusNeedload除去
//                    stusNeedload.remove(i1);
//                    break;
//                }
//            }
//            //TODO 数据只根据列表中的来,以列表显示为主
//           // if (i1 == stusFromLocal.size())stusNeedload.add(stusFromLocal.get(i));
//        }
//        // TODO 也就是列表中存在远端服务器的数据,不会重复上传,但是本地数据库有的,远端数据库没有,那么只有将这些数据添加至列表才能进一步操作


    }

    @Override
    public void onClick(View v) { // 添加item 跳转活动到输入界面
        if (v.getId() == R.id.add) {
            System.out.println("add");
            Intent intent = new Intent(getActivity(), StuInputActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.upload) {
            //TODO 上传stus
            infoSubmit();
        }
    }

    //TODO download 的同步数据
    //TODO 活动的download ，因为这个download布局是在活动中。
    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.download) {
            new Thread(() -> {
                //TODO 发送请求
                URL url = null;//生成一个URL实例，指向我们刚才设定的地址URL
                Gson gson = new Gson();
                try {
                    url = new URL("http://192.168.232.1:8080/stus/info");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET"); //设置请求方式为
                    conn.setReadTimeout(1000);//设置超时信息
                    conn.setConnectTimeout(1000);//设置超时信息
                    conn.setDoInput(true);//设置输入流，允许输入
                    conn.setDoOutput(false);//设置输出流，不允许输出
                    conn.setUseCaches(false);//设置POST请求方式不能够使用缓存
                    // TODO 接收数据
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                    String stusJson = response.toString();
                    Type type = new TypeToken<List<Stu>>() {
                    }.getType();
                    List<Stu> stuListFromMysql = gson.fromJson(stusJson, type);
                    // TODO 合并！！！！！！
                    //TODO 数据的合并需要注意双端的一个重复校验，数据库传来的数据内容可能会重复
                    //TODO 取出目前列表中的id

                    String[] viewHasIds = new String[stuLab.getStus().size()];
                    int viewHasIdsIndex = 0;
                    for (int i = 0; i < stuLab.getStus().size(); i++) {
                        viewHasIds[viewHasIdsIndex++] = stuLab.getStus().get(i).getId();
                    }
                    stuListFromMysql.forEach(stu -> {
                        //TODO 如果列表中没有该id，则添加到显示列表中
                        if (!Arrays.asList(viewHasIds).contains(stu.getId())) {
                            stuLab.getStus().add(stu);
                        }
                    });
                    getActivity().runOnUiThread(() -> {
                        // Update the RecyclerView with the new data
                        updateUI();
                        Toast.makeText(getActivity(), "Data downloaded successfully", Toast.LENGTH_SHORT).show();
                    });
// updateUI();  // TODO 必须在程序的主线程(也就是 UI 线程)中进行更新界面显示的工作!!!!!!!!
// TODO 数据注入到stus 刷新UI 显示

                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (ProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Not connected to the database..", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        } else if (v.getId() == R.id.local) {
            //TODO 进行本地化的数据回显
            List<Stu> stusInLocal = stuSqlHelper.selectAll();
            System.out.println("本地数据库中的 stus = " + stusInLocal);
            // TODO 那么本地的数据会不会在列表中已经存在了?

            for (Stu stu : stusInLocal) {
                int i = 0;
                for (; i < stuLab.getStus().size(); i++) {
                    if (stu.getId().equalsIgnoreCase(stuLab.getStus().get(i).getId())) {
                        break;
                    }
                }
                // TODO 索引来到最后意味着 全部验证完
                if (i == stuLab.getStus().size()) {
                    stuLab.getStus().add(stu);
                }
            }
            //TODO 告知Adapter 页面数据发生了变化
            updateUI();

        } else if (v.getId() == R.id.save) {
            //TODO 进行数据保存至本地,为什么方法直接调用,因为属于上下文的方法,活动也是一个上下文,游标工厂暂时不需要,又不查询
            //TODO 拿到SQLiteDatabase对象进行数据库的操作,但其实用StuSqlHelper 这个类更加方便
//            SQLiteDatabase database = getActivity().openOrCreateDatabase(mDataBaseName, Context.MODE_PRIVATE, null);
//            System.out.println("database = " + database);
//            if (database != null)
//                Log.d("database create ", String.format("数据库创建成功,路径为%s", database.getPath()));
            //TODO 现在在stus内的有很多种可能,其实.比如有被修改过的还未存入数据库的,还有未被修改需要新增的,还有就是未被修改的但是已经在数据库内的.
            ArrayList<Stu> needInsert = new ArrayList<>();
            ArrayList<Stu> needUpdate = new ArrayList<>();
            ArrayList<Stu> noOperation = new ArrayList<>();
            ArrayList<Stu> stusFromRecycleView = stuLab.getStus();
            //TODO 还需要一步,查询本地数据库的内容
            List<Stu> stusFromLocal = stuSqlHelper.selectAll();


            stusFromRecycleView.forEach(stu -> {
                if (stu.isUpdate()) { // TODO 被修改过的,放进needUpdate
                    needUpdate.add(stu);
                } else {
                    needInsert.add(stu);
                }
                // TODO 你这若是本地数据库一开始就没数据?????????岂不是BUG了,解决办法在上方
                // TODO 你这若是本地数据库一开始就没数据?????????岂不是BUG了
                // TODO 你这若是本地数据库一开始就没数据?????????岂不是BUG了
                // TODO 你这若是本地数据库一开始就没数据?????????岂不是BUG了
                for (int i = 0; i < stusFromLocal.size(); i++) {
                    //TODO 如果本地有并且未被修改那就不操作
                    if (!(stu.getId().equalsIgnoreCase(stusFromLocal.get(i).getId()))) {
                        needInsert.add(stu);
                    } else {
                        noOperation.add(stu);
                    }
                }


            });
            System.out.println("noOperation = " + noOperation);
            System.out.println("needInsert = " + needInsert);
            System.out.println("needUpdate = " + needUpdate);
            needUpdate.forEach(stu -> {
                stuSqlHelper.update(stu);
            });
            needInsert.forEach(stu -> {
                stuSqlHelper.insert(stu);
            });
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show());

        }
        return true;
    }

    private class StuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView nameTextView;
        private TextView idTextView;
        private TextView telTextView;
        private Button head;
        private Stu stu;

        public StuViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.stu_item, parent, false));
            nameTextView = itemView.findViewById(R.id.nameshow);
            idTextView = itemView.findViewById(R.id.idshow);
            telTextView = itemView.findViewById(R.id.telshow);

            Button callBtn = itemView.findViewById(R.id.call);
            callBtn.setOnClickListener(this);

            Button delBtn = itemView.findViewById(R.id.del);
            delBtn.setOnLongClickListener(this);


            head = itemView.findViewById(R.id.head);
            head.setOnLongClickListener(this);
        }

        private void bind(Stu stu) {
            this.stu = stu;
            nameTextView.setText(stu.getName());
            idTextView.setText(stu.getId());
            telTextView.setText(stu.getTel());
            String img = this.stu.getImg();
            if (img != null) {
                if ("blue".equals(img)) {
                    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.blue);
                    head.setBackground(drawable);
                } else if ("pink".equals(img)) {
                    Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.pink);
                    head.setBackground(drawable);
                }
            }
        }

        //TODO 拨号动作
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.call) {
                //TODO 切换到拨号界面
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                Uri uri = Uri.parse("tel:" + stuLab.getStus().get(this.getAdapterPosition()).getTel());
                intent.setData(uri);
                startActivity(intent);
            }
        }


        //TODO 长按事件 删除 和 长按头像回到输入页面。
        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.del) {
                //TODO 去list集合删除元素
                int nowPos = this.getAdapterPosition();
                Stu stuShouldbeDeled = stuLab.getStus().get(nowPos);
                stuLab.getStus().remove(nowPos);
                updateUI(); // 更新界面
                // TODO 把本地数据库的也删除了
                int delete = stuSqlHelper.deleteById(stuShouldbeDeled.getId());
                if (delete > 0) {
                    Toast.makeText(getActivity(), "本地删除成功", Toast.LENGTH_SHORT).show();
                }
                AtomicReference<ArrayList<Stu>> stuListFromMysql = new AtomicReference<>(new ArrayList<>());
                //TODO  远端数据库也需要删除,如果有的话
                Thread thread_getInfoFromMysql = new Thread(() -> {
                    //TODO 发送请求


                    URL url = null;//生成一个URL实例，指向我们刚才设定的地址URL
                    Gson gson = new Gson();
                    try {
                        url = new URL("http://192.168.232.1:8080/stus/info");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET"); //设置请求方式为
                        conn.setReadTimeout(1000);//设置超时信息
                        conn.setConnectTimeout(1000);//设置超时信息
                        conn.setDoInput(true);//设置输入流，允许输入
                        conn.setDoOutput(false);//设置输出流，不允许输出
                        conn.setUseCaches(false);//设置POST请求方式不能够使用缓存
                        // TODO 接收数据
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                conn.getInputStream()));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        conn.disconnect();
                        String stusJson = response.toString();
                        Type type = new TypeToken<List<Stu>>() {
                        }.getType();
                        stuListFromMysql.set(gson.fromJson(stusJson, type));

                    } catch (ProtocolException e) {
                        throw new RuntimeException(e);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread_getInfoFromMysql.start();
                try {
                    thread_getInfoFromMysql.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Thread thread_DelFromMysql;
                for (Stu s : stuListFromMysql.get()) {
                    System.out.println("我要删除的时候远程数据库的内容有哪些stuListFromMysql = " + stuListFromMysql);
                    System.out.println("这是我要删除的学生stuShouldbeDeled = " + stuShouldbeDeled);
                    System.out.println("thread_DelFromMysql循环进去了吗 = ");
                    if (stuShouldbeDeled.getId().equals(s.getId())) {
                        System.out.println("thread_DelFromMysql if 进去了吗  = ");
                        // 发请求去删除
                        thread_DelFromMysql = new Thread(() -> {
                            System.out.println("thread_DelFromMysql线程执行了吗 = ");
                            try {
                                String u = "http://192.168.232.1:8080/stus/del/" + stuShouldbeDeled.getId();
                                System.out.println("列表中删除的学生是 = " + u);
                                URL url = new URL(u);//生成一个URL实例，指向我们刚才设定的地址URL
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("DELETE"); //DELETE
                                conn.setReadTimeout(1000);//设置超时信息
                                conn.setConnectTimeout(1000);//设置超时信息
                                conn.setDoInput(true);//设置输入流，允许输入
                                conn.setDoOutput(true);//设置输出流，允许输出
                                conn.setUseCaches(false);//设置POST请求方式不能够使用缓存
                                conn.setRequestProperty("Content-Type", "application/json");
                                conn.connect();
                                int responseCode = conn.getResponseCode();
                                if (responseCode != 200) {
                                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "远程数据库删除失败", Toast.LENGTH_SHORT).show());
                                } else {
                                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "远程数据库删除成功", Toast.LENGTH_SHORT).show());
                                }
                            } catch (ProtocolException e) {
                                throw new RuntimeException(e);
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        });
                        thread_DelFromMysql.start();
                        try {
                            thread_DelFromMysql.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                }


                Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();

                //TODO  点击的是头像，就跳回Input输入框，重新输入
            } else if (v.getId() == R.id.head) {
                int adapterPosition = this.getAdapterPosition();


                Intent intent = new Intent(getActivity(), StuInputActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", stuLab.getStus().get(adapterPosition).getId());
                bundle.putString("name", stuLab.getStus().get(adapterPosition).getName());
                bundle.putString("tel", stuLab.getStus().get(adapterPosition).getTel());
                bundle.putString("img", stuLab.getStus().get(adapterPosition).getImg());
                System.out.println("stuLab = " + stuLab.getStus());
                intent.putExtras(bundle);
                startActivity(intent);

//TODO 通过将启动新活动的代码放在删除数据之前，确保了在新活动中使用的数据仍然存在，避免了 java.lang.IndexOutOfBoundsException 错误
                stuLab.getStus().remove(adapterPosition);
                Toast.makeText(getActivity(), "Updating.....", Toast.LENGTH_SHORT).show();
                updateUI(); // 更新界面
            }
            return true;
        }
    }

    private class StuAdapter extends RecyclerView.Adapter<StuViewHolder> {
        private ArrayList<Stu> stus;

        public StuAdapter(ArrayList<Stu> stus) {
            this.stus = stus;
        }

        @NonNull
        @Override
        public StuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater from = LayoutInflater.from(getActivity());
            return new StuViewHolder(from, parent);
        }

        @Override
        public void onBindViewHolder(StuViewHolder holder, int position) {

            holder.bind(stus.get(position)); // 取出List的学生列表，根据索引位置加载对应位置
        }

        @Override
        public int getItemCount() {
            return stus.size();
        }

        public void setStus(ArrayList<Stu> stus) {
            this.stus = stus;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stu_list, container, false);

        Button addBtn = view.findViewById(R.id.add);
        addBtn.setOnClickListener(this);

        Button upload = view.findViewById(R.id.upload);
        upload.setOnClickListener(this);

        Button download = view.findViewById(R.id.download);
        download.setOnLongClickListener(this);

        Button local = view.findViewById(R.id.local);
        local.setOnLongClickListener(this);

        Button save = view.findViewById(R.id.save);
        save.setOnLongClickListener(this);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        System.out.println("传过来的bundle = " + bundle);
        if (bundle != null) {
            String id = bundle.getString("id");
            String name = bundle.getString("name");
            String tel = bundle.getString("tel");
            String img = bundle.getString("img");
            Stu stu = new Stu(name, tel, id, img);
            System.out.println("stu = " + stu);
            stuLab.getStus().add(stu);
            System.out.println("传过来的 stuLab = " + stuLab.getStus());
        }
        updateUI();

        return view;
    }

    //TODO 搞错生命周期的顺序 ? 页面能看到,透明度 ?
    //    @Override
//    public void onResume() {
//        super.onResume();
//        updateUI();
//    }
    private void updateUI() {//获取List数据然后将Adapter装载并设置进recycleView

        ArrayList<Stu> stus = stuLab.getStus();
        if (stuAdapter == null) {
            stuAdapter = new StuAdapter(stus);
            recyclerView.setAdapter(stuAdapter);
        } else {
            stuAdapter.setStus(stus);
            stuAdapter.notifyDataSetChanged();
        }
    }

    // TODO sql的本地持久化 的工具


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

}
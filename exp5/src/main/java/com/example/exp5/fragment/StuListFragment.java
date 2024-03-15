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

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StuListFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private RecyclerView recyclerView;
    private StuAdapter stuAdapter;
    private final StuLab stuLab = StuLab.get(getActivity());

    //连接Web
    public StuListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void infoSubmit() {
        if (stuAdapter.getItemCount() == 0){  // 观察数据列表中的数据有没有呢
            Toast.makeText(getActivity(), "列表中暂时没有数据可上传......", Toast.LENGTH_SHORT).show();
        }
        Log.d("submit", "Start commit................");
        ArrayList<Stu> stus = stuLab.getStus();
        try {
            final String URL = "http://192.168.232.1:8080/stus/listinfo";//根据自己的项目需要修改
            new Thread(() -> {
                try {
                    URL url = new URL(URL);//生成一个URL实例，指向我们刚才设定的地址URL
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
                    String jsonStus = gson.toJson(stus);
                    out.write(jsonStus.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    out.close();
                    conn.connect();  // 刷新。。。。。。。。。。。。。。。
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
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    conn.setReadTimeout(5000);//设置超时信息
                    conn.setConnectTimeout(5000);//设置超时信息
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
                    throw new RuntimeException(e);
                }
            }).start();

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
                stuLab.getStus().remove(nowPos);
                updateUI(); // 更新界面
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
            System.out.println("stus.get(position) = " + stus.get(position));
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


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String id = bundle.getString("id");
            String name = bundle.getString("name");
            String tel = bundle.getString("tel");
            String img = bundle.getString("img");
            Stu stu = new Stu(name, tel, id, img);
            System.out.println("stu = " + stu);
            stuLab.getStus().add(stu);
        }
        updateUI();
        return view;
    }

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
}
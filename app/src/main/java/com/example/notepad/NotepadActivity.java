package com.example.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.adapter.NotepadAdapter;
import com.example.notepad.bean.NotepadBean;
import com.example.notepad.database.SQLiteHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NotepadActivity extends AppCompatActivity {
    ListView listView;
    List<NotepadBean> list;
    SQLiteHelper mSQLiteHelper;
    NotepadAdapter adapter;
    SQLiteDatabase db;
    public static final MediaType JSON
            = MediaType.get("text/plain; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        //用于显示记录的列表
        listView = (ListView) findViewById(R.id.listview);
        ImageView add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotepadActivity.this, com.example.notepad.RecordActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        initData();
    }

    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(this); //创建数据库
        httpQuery();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotepadBean notepadBean = list.get(position);
                Intent intent = new Intent(NotepadActivity.this, com.example.notepad.RecordActivity.class);
                intent.putExtra("id", notepadBean.getId());
                intent.putExtra("time", notepadBean.getNotepadTime());
                //记录的内容
                intent.putExtra("content", notepadBean.getNotepadContent());
                //跳转到修改记录页面
                NotepadActivity.this.startActivityForResult(intent, 1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                db = mSQLiteHelper.getWritableDatabase();
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(NotepadActivity.this)
                        .setMessage("是否删除此记录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NotepadBean notepadBean = list.get(position);
//                                if (mSQLiteHelper.deleteData(notepadBean.getId(), db)) {
                                    httpdelete(notepadBean.getId());
                                    list.remove(position);//删除对应的Item
                                    adapter.notifyDataSetChanged();//更新记事本界面
                                    Toast.makeText(NotepadActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
//                                    db.close();
//                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();//关闭对话框
                            }
                        });

                dialog = builder.create();
                dialog.show();
                return true;
            }

        });
    }


    //删除
    public void httpdelete(String id) {
//        Gson gson = new Gson();
//        String json = gson.toJson(id);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, id);

        Request request = new Request.Builder()
                .url("http://121.199.44.171:8585/delete")
                .post(body)
                .addHeader("content-type", "text/plain")
                .build();

        Call call = client.newCall(request);

        //andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();

//                    if (res.equals("true")) {
//                        addFlag = true;
//                    } else {
//                        addFlag = false;
//                    }
//                    System.out.println("子线程修改的" + addFlag);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    //查询
    public void httpQuery() {
        adapter = new NotepadAdapter(this,list);
        listView.setAdapter(adapter);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://121.199.44.171:8585/getAll").build();

        Call call = client.newCall(request);

        //andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //如果list有内容先清空
                if (list != null){
                    list.clear();
                }
                //获取响应值
                String res = response.body().string();
                //解析res
                Gson gson = new Gson();
                list = gson.fromJson(res, new TypeToken<List<NotepadBean>>() {
                }.getType());
                //展示列表
//                showQueryData(list);
                adapter.setList(list);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }




    private void showQueryData(List<NotepadBean> list) {
        adapter = new NotepadAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            httpQuery();
        }
    }
}
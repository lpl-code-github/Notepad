package com.example.notepad;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.adapter.NotepadAdapter;
import com.example.notepad.bean.NotepadBean;
import com.example.notepad.database.SQLiteHelper;
import com.example.notepad.utils.DBUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView note_back;
    TextView note_time;
    EditText content;
    ImageView delete;
    ImageView note_save;
    SQLiteHelper mSQLiteHelper;
    TextView noteName;
    String id;
    SQLiteDatabase db;
    static Boolean addFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        note_back = (ImageView) findViewById(R.id.note_back);
        note_time = (TextView) findViewById(R.id.tv_time);
        content = (EditText) findViewById(R.id.note_content);
        delete = (ImageView) findViewById(R.id.delete);
        note_save = (ImageView) findViewById(R.id.note_save);
        noteName = (TextView) findViewById(R.id.note_name);

        note_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        note_save.setOnClickListener(this);
        initData();
    }

    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(this);
        noteName.setText("添加记录");

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            if (id != null) {
                noteName.setText("修改记录");
                content.setText(intent.getStringExtra("content"));
                note_time.setText(intent.getStringExtra("time"));
                note_time.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.note_back:// 后退按钮
            finish();
            break;
        case R.id.delete:// 清空按钮
            content.setText("");
            break;
        case R.id.note_save:
            // db = mSQLiteHelper.getWritableDatabase();
            // 获取输入内容
            String noteContent = content.getText().toString().trim();
            NotepadBean notepadBean = new NotepadBean();
            notepadBean.setId(id);
            notepadBean.setNotepadContent(noteContent);
            notepadBean.setNotepadTime(DBUtils.getTime());

            // 向数据库中添加内容
            if (id != null) {
                if (noteContent.length() > 0) {
                    httpUpdate(notepadBean);
                } else {
                    errorToast("修改内容不能为空");
                }
            } else { // 添加记录界面的保存操作
                // 向数据库中添加数据
                if (noteContent.length() > 0) {
                    httpAdd(notepadBean);
                } else {
                    errorToast("填写内容不能为空");
                }
            }
            // db.close();
            break;
        }
    }

    // 增加
    public void httpAdd(NotepadBean req) {
        httpAddOrUpdate("add", req);
    }

    // 更新
    public void httpUpdate(NotepadBean req) {
        httpAddOrUpdate("update", req);
    }

    private void httpAddOrUpdate(String action, NotepadBean req) {
        Call call = new OkHttpClient().newCall(HttpUtils.postRequestBuilder(action, req));

        // andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 错误处理
                    showToast(action, "失败");
                    return;
                }

                showToast(action, "成功");
                setResult(2);
                finish();

                String res = response.body().string();

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void showToast(String action, String message) {
        Toast.makeText(RecordActivity.this, String.format("%s %s", action, message), Toast.LENGTH_SHORT).show();
    }

    public void errorToast(String message) {
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

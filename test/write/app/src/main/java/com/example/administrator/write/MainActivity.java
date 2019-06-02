package com.example.administrator.write;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editname;
    private EditText editdetail;
    private TextView texthing;
    private Button btnsave;
    private Button btnclean;
    private Button btnread;
    private Button newpage;
    private Context mContext;
    public String acceptData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        bindViews();
    }


    private void bindViews() {
        editdetail = (EditText) findViewById(R.id.editdetail);
        editname = (EditText) findViewById(R.id.editname);
        texthing =(TextView) findViewById(R.id.inputhing);
        btnclean = (Button) findViewById(R.id.btnclean);
        btnsave = (Button) findViewById(R.id.btnsave);
        btnread = (Button) findViewById(R.id.btnread);
        newpage = (Button) findViewById(R.id.newpage);

        btnclean.setOnClickListener(this);
        btnsave.setOnClickListener(this);
        btnread.setOnClickListener(this);
        newpage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnclean:
                editdetail.setText("");
                editname.setText("");
                texthing.setText("");
                break;
            case R.id.btnsave:
                FileHelper fHelper = new FileHelper(mContext);
                String filename = editname.getText().toString();
                String filedetail = editdetail.getText().toString();
                try {
                    texthing.setText("输入的内容为："+filedetail);
                    fHelper.save(filename, filedetail);
                    Toast.makeText(getApplicationContext(),"数据写入成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "数据写入失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnread:
                String detail = "";
                FileHelper fHelper2 = new FileHelper(getApplicationContext());
                try {
                    String fname = editname.getText().toString();
                    detail = fHelper2.read(fname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), detail, Toast.LENGTH_SHORT).show();
                break;
            case R.id.newpage:
                try {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,Main2Activity.class);
                    startActivity(intent);
                    SendGetRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),"打开页面", Toast.LENGTH_SHORT).show();
                break;
        }
    }



    Handler handler = new Handler(){  //此函数是属于MainActivity.java所在线程的函数方法，所以可以直接条调用MainActivity的 所有方法。
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x01){   //
                //System.out.println("handleMessage（）："+acceptData);
                //可调用  Toast.makeText(MainActivity.this,"返回内容是："+acceptData,Toast.LENGTH_SHORT)).show();    来显示到UI
                //每次数据读取完毕，将acceptData置空。 具体原因，当下还未理解
                acceptData = "";  //
            }else{
                Toast.makeText(MainActivity.this, "请重新输入地址：", Toast.LENGTH_SHORT).show();
            }
        }
    };



    public void SendGetRequest() {
        new Thread(){
            @Override
            public void run() {
                String pathString =  "http://ip.tianqiapi.com/?ip=27.193.13.255,123.125.71.38";  //请求天气信息
                Log.i("pathString","get请求");
                //String pathString = url + content;
                HttpURLConnection connection;
                try {
                    URL url = new URL(pathString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    //接受数据
                    if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                        String line;
                        while((line=bufferedReader.readLine())!=null){ //不为空进行操作
                            acceptData+=line;
                        }
                        System.out.println("接受到的数据："+acceptData);
                        Log.i("abc",acceptData);
                        //handler.sendEmptyMessage(0x01);  //请求完毕，返回自己自定义的信息 id
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}

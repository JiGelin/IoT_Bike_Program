package com.IoTAPP.IoT_Program;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.random.FallbackThreadLocalRandom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class lock_fragment extends AppCompatActivity implements View.OnClickListener {

    private String token = new String("version=2018-10-31&res=products%2F446074&et=1629454881&method=md5&sign=t1jrHMoRBUjZWNowEBt4kg%3D%3D");
    private String url = "https://api.heclouds.com/devices/742737661/datapoints?datastream_id=Lock_state,Pressure,Temperature";

    private ImageView lockpage_icon;
    private TextView lock_state_message;
    private SwitchButton button;
    private TextView time;
    private ImageView wo_lao_po;
    private boolean state = false;
    private int click_time = 0;

    private void setState(boolean state){
        this.state = state;
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_fragment);
        this.getSupportActionBar().hide();

        initView();

        setButton();

        getData();

        setWife();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getData();
                Message message = Message.obtain();
                message.what = 101 ;
                handler.sendMessage(message);

            }
        },4000,4000);

    }




    private void initView() {
        lockpage_icon = findViewById(R.id.lockpage_icon);
        lock_state_message = findViewById(R.id.lock_page_tint);
        button = findViewById(R.id.lock_page_lock_btn);
        time = findViewById(R.id.lock_page_time);
        wo_lao_po = findViewById(R.id.laopo);

    }

    private void setButton() {
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                button.setEnabled(false);
                if(isChecked)
                {
                    postCommand(!state);

                }
                button.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(true);
                        button.setChecked(false);
                    }
                },4000);

            }

            private void postCommand(boolean key) {
                if (key){
                    //打开开关，下发ON
                    showMsg("正在打开车锁");
                    Command("ON");
                }else{
                    //关闭开关，下发OFF
                    showMsg("正在锁车中");
                    Command("OFF");
                }

            }

            private void Command(String word) {
                MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
                String posturl = "https://api.heclouds.com/v1/synccmds?device_id=742737661&timeout=30";
                OkHttpClient okHttpClient = new OkHttpClient();
                //textView.setText();
                final Request request = new Request.Builder()
                        .url(posturl)
                        .post(RequestBody.create(mediaType,word))
                        .addHeader("Authorization",token)
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        goToUIThread(e.toString(),0);
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        goToUIThread(response.body().string(),1);
                    }
                    private void goToUIThread(Object object, int key) {
                        lock_fragment.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (key == 0){ }else { }
                            }
                        });
                    }
                });
            }
        });
    }

    private void getData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                goToUIThread(e.toString(), 0);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                goToUIThread(response.body().string(), 1);
            }

            private void goToUIThread(final Object object, int key) {
                lock_fragment.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (key == 0){
                            showMsg("failed,"+object.toString());
                            Log.e("MainActivity",object.toString());
                        }else {
                            GsonCatch(object);
                        }
                    }
                });
            }
        });

    }

    private void GsonCatch(Object object) {
        JsonRootBean jsonData = new Gson().fromJson(object.toString(),JsonRootBean.class);
        List<Datastreams> datastreams = jsonData.getData().getDatastreams();
        int count = jsonData.getData().getCount();
        StringBuilder stringBuilder = new StringBuilder();


        //for (int i = 0 ;i < count; i++){
        //    stringBuilder.append(datastreams.get(i).getId()+":"+datastreams.get(i).getDatapoints().get(0).getValue()+"\n");
        //}

        //textView.setText(stringBuilder);
        Bundle bundle = new Bundle();
        bundle.putString("Lock_State",datastreams.get(0).getDatapoints().get(0).getValue());
        //bundle.putString("Temperature",datastreams.get(1).getDatapoints().get(0).getValue());
        //bundle.putString("Pressure",datastreams.get(2).getDatapoints().get(0).getValue());
        bundle.putString("Time",datastreams.get(0).getDatapoints().get(0).getAt());

        Message message =Message.obtain();
        message.what = 100;
        message.setData(bundle);
        handler.sendMessage(message);

    }



    private void showMsg(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 100:
                    Bundle bundle = msg.getData();
                    String this_lock_state = bundle.getString("Lock_State");
                    String this_time = bundle.getString("Time");
                    time.setText(this_time);
                    if (this_lock_state.equals("1")){
                        lockpage_icon.setImageResource(R.drawable.unlock);
                        lock_state_message.setText("车锁已打开");
                        setState(true);

                    }else {
                        lockpage_icon.setImageResource(R.drawable.lock);
                        lock_state_message.setText("车锁已锁");
                        setState(false);
                    }
                    break;
                case 101:
                    break;
            }
        }
    };

    private void setWife() {
        wo_lao_po.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.laopo:
                wo_lao_po.setEnabled(false);
                click_time++;
                if (click_time<10){
                    talk(1);
                    wo_lao_po.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wo_lao_po.setEnabled(true);
                        }
                    },1000);
                }else if (click_time<20){
                    wo_lao_po.setImageResource(R.drawable.xier_state2);
                    talk(2);
                    wo_lao_po.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wo_lao_po.setEnabled(true);
                        }
                    },1000);
                }else {
                    wo_lao_po.setImageResource(R.drawable.naocan);
                    talk(3);
                }
                break;

        }
    }

    private void talk(int key) {
        switch (key){
            case 1:
                showMsg("好好学习，天天向上！");
                break;
            case 2:
                showMsg("在碰我，就把你吃掉！");
                break;
            case 3:
                showMsg("这人他妈逼脑残？");
                break;
            default:
                break;
        }

    }
}
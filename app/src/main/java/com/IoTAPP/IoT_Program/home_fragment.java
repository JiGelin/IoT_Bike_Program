package com.IoTAPP.IoT_Program;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class home_fragment extends AppCompatActivity{

    private String token = new String("version=2018-10-31&res=products%2F446074&et=1629454881&method=md5&sign=t1jrHMoRBUjZWNowEBt4kg%3D%3D");
    private String url = "https://api.heclouds.com/devices/742737661/datapoints?datastream_id=Lock_state,Pressure,Temperature,Warn";

    private NotificationManager manager;
    private NotificationChannel notificationChannel;
    private Notification.Builder builder;
    private String message_whale = "您的轮胎状态异常，请尽快检查！";
    private String message_lock = "您的车锁状态异常，请尽快查看！";

    private TextView press;
    private TextView temperate;
    private ImageView whale_light;
    private TextView whale_message;

    private ImageView lock_icon;
    private TextView lock_state_message;
    private ImageView lock_light;
    private TextView lock_message;

    private final int max_Press = 101000;//101000; //气压高于该阈值时警报
    private final int min_Press = 100000; //气压低于该阈值时报警
    private final int max_Temperate = 40; //温度高于该阈值时报警

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_fragment);
        this.getSupportActionBar().hide();

        initView();

        getData();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getData();
                //showMsg("111");
                Message message = Message.obtain();
                message.what = 101 ;
                handler.sendMessage(message);

            }
        },4000,4000);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() {
        press = findViewById(R.id.whale_press);
        temperate = findViewById(R.id.whale_temperate);
        whale_light = findViewById(R.id.light_whale);
        whale_message = findViewById(R.id.whale_message);
        lock_icon = findViewById(R.id.lock_icon);
        lock_state_message = findViewById(R.id.lock_state_hint);
        lock_light = findViewById(R.id.light_lock);
        lock_message = findViewById(R.id.lock_message);
        lock_icon.setImageResource(R.drawable.unlock);

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationChannel = new NotificationChannel("1","name",NotificationManager.IMPORTANCE_LOW);
        manager.createNotificationChannel(notificationChannel);

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
                home_fragment.this.runOnUiThread(new Runnable() {
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
        bundle.putString("Temperature",datastreams.get(1).getDatapoints().get(0).getValue());
        bundle.putString("Pressure",datastreams.get(2).getDatapoints().get(0).getValue());
        bundle.putString("Warn",datastreams.get(3).getDatapoints().get(0).getValue());
        //showMsg(datastreams.get(3).getDatapoints().get(0).getValue());
        Message message =Message.obtain();
        message.what = 100;
        message.setData(bundle);
        handler.sendMessage(message);
    }


    Handler handler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    Bundle bundle = msg.getData();
                    String this_lock_state = bundle.getString("Lock_State");
                    String this_temperature = bundle.getString("Temperature");
                    String this_pressure = bundle.getString("Pressure");
                    String this_warn = bundle.getString("Warn");
                    press.setText(this_pressure);
                    temperate.setText(this_temperature);
                    checkWhaleStation(this_pressure,this_temperature);
                    if (this_lock_state.equals("1")){
                        lock_icon.setImageResource(R.drawable.unlock);
                        lock_state_message.setText("车锁已打开");
                    }else {
                        lock_icon.setImageResource(R.drawable.lock);
                        lock_state_message.setText("车锁已锁上");
                    }
                    if (this_warn.equals("1")){
                        lock_light.setImageResource(R.drawable.light_right);
                        lock_message.setText("您的锁状态异常！请立刻检查是否被盗！");
                        builder = new Notification.Builder(home_fragment.this,"1");
                        builder.setSmallIcon(R.drawable.alarm)
                                .setContentText(message_lock)
                                .setContentTitle("您的爱车出现异常！")
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.app_icon));
                        Notification notification1 = builder.build();
                        manager.notify(1,notification1);

                    }else {
                        lock_light.setImageResource(R.drawable.light_green);
                        lock_message.setText("当前车锁状态正常");
                    }

                    break;
                case 101:


                    break;
                default:
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private void checkWhaleStation(String getpress, String gettemperate) {
        int press = Integer.parseInt(getpress,10);
        double temperate = stringToDouble(gettemperate);
        if (press>=max_Press||press<=min_Press||temperate>=max_Temperate){
            whale_light.setImageResource(R.drawable.light_right);
            whale_message.setText("您的车胎状态异常！请及时检查！");
            whale_message.setTextColor(R.color.red);
            whale_message.setTextSize(12);
            builder = new Notification.Builder(this,"1");
            builder.setSmallIcon(R.drawable.alarm)
                    .setContentText(message_whale)
                    .setContentTitle("您的爱车出现异常！")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.app_icon));
            Notification notification = builder.build();
            manager.notify(1,notification);

        }else {
            whale_light.setImageResource(R.drawable.light_green);
            whale_message.setText("当前轮胎状态正常");
            whale_message.setTextColor(R.color.black);
            whale_message.setTextSize(15);
        }

    }

    private void showMsg(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    public static double stringToDouble(String a){
        double b = Double.valueOf(a);

        DecimalFormat df = new DecimalFormat("#.0");//此为保留1位小数，若想保留2位小数，则填写#.00 ，以此类推

        String temp = df.format(b);

        b = Double.valueOf(temp);

        return b;

    }

    @Override
    protected void onPause() {
        super.onPause();
        showMsg("pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //showMsg("destroy");
    }
}
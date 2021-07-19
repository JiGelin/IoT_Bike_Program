package com.IoTAPP.IoT_Program;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
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

public class map_fragment extends AppCompatActivity implements View.OnClickListener {

    private MapView mapView = null;
    BaiduMap myMap;
    LatLng position = new LatLng(35.95,120.18);
    ImageView back2positon;

    void setPosition(double jing,double wei){
        position = new LatLng(jing,wei);
    }


    private String token = new String("version=2018-10-31&res=products%2F446074&et=1629454881&method=md5&sign=t1jrHMoRBUjZWNowEBt4kg%3D%3D");
    private String url = "https://api.heclouds.com/devices/742737661/datapoints?datastream_id=Lock_state,Pressure,Temperature";


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        this.getSupportActionBar().hide();

        initView();
        initMap();
        //getPosition();
        setMark();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //getPosition();
                setMark();
                Message message = Message.obtain();
                message.what = 101 ;
                handler.sendMessage(message);

            }
        },4000,4000);

    }


    private void initView() {
        mapView =findViewById(R.id.map_page_map);
        back2positon = findViewById(R.id.postion_btn);
        myMap = mapView.getMap();
        myMap.setMyLocationEnabled(true);
    }

    private void initMap() {
        MapStatus mapStatus = new MapStatus.Builder().target(position).zoom(18).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        myMap.setMapStatus(mapStatusUpdate);
        back2positon.setOnClickListener(this);

    }

    private void getPosition() {
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
                map_fragment.this.runOnUiThread(new Runnable() {
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



        Message message =Message.obtain();
        message.what = 100;
        message.setData(bundle);
        handler.sendMessage(message);


    }




    private void setMark() {
        myMap.clear();
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.marker);
        MarkerOptions option = new MarkerOptions()
                .position(position)
                .icon(bitmap)
                .perspective(true);
        option.animateType(MarkerOptions.MarkerAnimateType.grow);
        myMap.addOverlay(option);
        myMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return true;
            }
        });

    }

    private void showMsg(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 101:

                    break;
                default:
                    break;
            }
        }
    };

    public static double stringToDouble(String a) {
        double b = Double.valueOf(a);
        DecimalFormat df = new DecimalFormat("#.0");//此为保留1位小数，若想保留2位小数，则填写#.00 ，以此类推
        String temp = df.format(b);
        b = Double.valueOf(temp);
        return b;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postion_btn:
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(position);
                myMap.animateMapStatus(mapStatusUpdate);
                break;
        }
    }
}
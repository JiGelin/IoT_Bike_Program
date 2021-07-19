package com.IoTAPP.IoT_Program;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class new_Main_Activity extends Activity implements View.OnClickListener {

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private List<View> mTabs = new ArrayList<View>();
    private LocalActivityManager manager;

    private LinearLayout home_Tab;
    private LinearLayout lock_Tab;
    private LinearLayout map_Tab;

    private MyWebView webView;
    private RelativeLayout relativeLayout;

    private ImageButton home_page_btn;
    private ImageButton lock_page_btn;
    private ImageButton map_page_btn;

    private Intent intent_home;
    private Intent intent_lock;
    private Intent intent_map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_homepage);

        manager = new LocalActivityManager(this,true);
        manager.dispatchCreate(savedInstanceState);

        initView();
        initData();
        initEvent();

    }

    private void initData() {
        {
            //初始化ViewPager的适配器
            adapter = new PagerAdapter() {
                //放回页面数量
                @Override
                public int getCount() {
                    return mTabs.size();
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    View view = mTabs.get(position);
                    container.addView(view);
                    return view;
                }
                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(mTabs.get(position));
                }
            };
            viewPager.setAdapter(adapter);

        }
    }

    private void initView() {
        home_Tab = findViewById(R.id.home_page);
        lock_Tab = findViewById(R.id.lock_page);
        map_Tab = findViewById(R.id.map_page);

        home_page_btn = findViewById(R.id.home_page_btn);
        lock_page_btn = findViewById(R.id.lock_page_btn);
        map_page_btn = findViewById(R.id.map_page_btn);

        webView = findViewById(R.id.web);

        relativeLayout = findViewById(R.id.relative);

        viewPager = findViewById(R.id.viewpager);

        intent_home = new Intent(new_Main_Activity.this,home_fragment.class);
        View tab1 = manager.startActivity("viewID",intent_home).getDecorView();
        intent_lock = new Intent(new_Main_Activity.this,lock_fragment.class);
        View tab2 = manager.startActivity("viewID",intent_lock).getDecorView();
        intent_map = new Intent(new_Main_Activity.this,map_fragment.class);
        View tab3 = manager.startActivity("viewID",intent_map).getDecorView();

        mTabs.add(tab1);
        mTabs.add(tab2);
        mTabs.add(tab3);

        //webView.loadUrl("file:///android_asset/live2d.html");
        webView.loadUrl("file:///android_asset/live2d.html");
        webView .setBackgroundColor(0);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);//支持JS

    }
    private void initEvent() {
        home_Tab.setOnClickListener(this);
        lock_Tab.setOnClickListener(this);
        map_Tab.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = viewPager.getCurrentItem();
                restButton();
                switch (currentItem){
                    case 0:
                        home_page_btn.setImageResource(R.drawable.home_click);
                        break;
                    case 1:
                        lock_page_btn.setImageResource(R.drawable.unlock);
                        break;
                    case 2:
                        map_page_btn.setImageResource(R.drawable.map_click);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        restButton();
        switch (v.getId()){
            case R.id.home_page:
                viewPager.setCurrentItem(0);
                home_page_btn.setImageResource(R.drawable.home_click);
                break;
            case R.id.lock_page:
                viewPager.setCurrentItem(1);
                lock_page_btn.setImageResource(R.drawable.lock_click);
                break;
            case R.id.map_page:
                viewPager.setCurrentItem(2);
                map_page_btn.setImageResource(R.drawable.map_click);
                break;
        }
    }

    private void restButton() {
        home_page_btn.setImageResource(R.drawable.home_before);
        lock_page_btn.setImageResource(R.drawable.lock);
        map_page_btn.setImageResource(R.drawable.map_before);
    }
    private void showMsg(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

}

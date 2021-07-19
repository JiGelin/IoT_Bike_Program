package com.IoTAPP.IoT_Program;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Acitivity_Loading extends Activity {

    private TextView title;
    private TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_page);
        initView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Typeface typeface = Typeface.createFromAsset(getAssets(),"tittle_asset.TTF");
                title.setTypeface(typeface);
                text.setTypeface(typeface);
                Intent intent = new Intent(Acitivity_Loading.this, new_Main_Activity.class);
                Acitivity_Loading.this.startActivity(intent);
                Acitivity_Loading.this.finish();
            }
        }, 3000);

    }

    private void initView() {
        title = findViewById(R.id.loading_title);
        text = findViewById(R.id.loading_text);
    }
}

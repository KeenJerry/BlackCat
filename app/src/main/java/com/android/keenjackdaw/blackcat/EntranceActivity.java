package com.android.keenjackdaw.blackcat;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.keenjackdaw.blackcat.ui.LoadingView;

public class EntranceActivity extends AppCompatActivity {

    Handler handler = null;
    LoadingView loadingView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        final long DELAY_MILLS = 3000;
        loadingView = findViewById(R.id.loading_view);
        final Intent intent = new Intent(this, MainActivity.class);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, DELAY_MILLS);
    }
}

package com.android.keenjackdaw.blackcat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.keenjackdaw.blackcat.ui.LoadingView;

public class EntranceActivity extends AppCompatActivity {

    Handler handler = null;
    private final long DELAY_MILLS = 1000;
    LoadingView loadingView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        loadingView = findViewById(R.id.loading_view);
    }
}

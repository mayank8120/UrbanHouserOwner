package com.uh.urbanhouserowner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashact);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splashact.this, login_act.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}

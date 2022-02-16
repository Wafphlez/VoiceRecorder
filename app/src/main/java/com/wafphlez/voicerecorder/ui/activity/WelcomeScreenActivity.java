package com.wafphlez.voicerecorder.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wafphlez.voicerecorder.R;

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        getSupportActionBar().hide();
    }


    public void startUsing(View view) {
        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("FirstTimeInstall", "No");
        editor.apply();

        Intent intent = new Intent(WelcomeScreenActivity.this, MainActivity.class);
        startActivity(intent);
    }

}

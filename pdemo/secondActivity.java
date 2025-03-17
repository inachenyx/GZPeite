package com.peite.pdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.entity.DATAB;
import me.f1reking.serialportlib.entity.FLOWCON;
import me.f1reking.serialportlib.entity.PARITY;
import me.f1reking.serialportlib.entity.STOPB;

public class secondActivity extends AppCompatActivity {
    private Button buttonFirst;
    private Button buttonSecond;
    private Button buttonsetoff;
    private Button buttonreturn3;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        buttonFirst=findViewById(R.id.button_first);
        buttonSecond=findViewById(R.id.button_other);
        buttonsetoff=findViewById(R.id.button_poweroff1);
        buttonreturn3=findViewById(R.id.button_return3);

        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secondActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secondActivity.this,testActivity.class);
                startActivity(intent);
            }
        });
        buttonreturn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secondActivity.this, Firstactivity.class);
                startActivity(intent);
            }
        });
    }

    public void system_poweroff(View view)
    {
        Intent w_intent = new Intent();
        w_intent.setAction("gzpeite.intent.action.shutdown");
        w_intent.putExtra("confirm", false);
//        w_intent.putExtra("confirm", true);
        sendBroadcast(w_intent);
    }




}

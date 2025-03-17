package com.peite.pdemo;

import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class Firstactivity extends AppCompatActivity {
//    private Button buttonstart2;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        View rootLayout = findViewById(R.id.root_layout);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 SecondActivity
                Intent intent = new Intent(Firstactivity.this, secondActivity.class);
                startActivity(intent);
            }
        });
//        buttonstart2=findViewById(R.id.button_begin2);
//        buttonstart2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Firstactivity.this, secondActivity.class);
//                startActivity(intent);
//            }
//        });

    }
}

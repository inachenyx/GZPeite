package com.peite.pdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import androidx.appcompat.app.AppCompatActivity;

import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.entity.DATAB;
import me.f1reking.serialportlib.entity.FLOWCON;
import me.f1reking.serialportlib.entity.PARITY;
import me.f1reking.serialportlib.entity.STOPB;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import android.util.Log;
import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.entity.DATAB;
import me.f1reking.serialportlib.entity.FLOWCON;
import me.f1reking.serialportlib.entity.PARITY;
import me.f1reking.serialportlib.entity.STOPB;
import me.f1reking.serialportlib.listener.IOpenSerialPortListener;
import me.f1reking.serialportlib.listener.ISerialPortDataListener;
import me.f1reking.serialportlib.listener.Status;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class thirdActivity extends AppCompatActivity {
    private int MIN_MARK = 0;
    private int MAX_MARK = 255;
    private SerialPortHelper mSerialPortHelper;
    private String recv; // 类的成员变量
    private String recvBuffer = "";
    private Button buttonopen;
    private Button buttonclose;
    private Button buttonreturn1;
    private Button buttonsendnumber;
    protected EditText etNumberInput;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        buttonopen=findViewById(R.id.button_yingguangjiance1);
        buttonclose=findViewById(R.id.button_guanbiyingguangjiance1);
        buttonreturn1=findViewById(R.id.button_return1);
        buttonsendnumber=findViewById(R.id.button_sendnumber1);
        etNumberInput = findViewById(R.id.editText_numberinput1);
        mSerialPortHelper = new SerialPortHelper();
        mSerialPortHelper.setStopBits(STOPB.getStopBit(STOPB.B1));
        mSerialPortHelper.setDataBits(DATAB.getDataBit(DATAB.CS8));
        mSerialPortHelper.setParity(PARITY.getParity(PARITY.NONE));
        mSerialPortHelper.setFlowCon(FLOWCON.getFlowCon(FLOWCON.NONE));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        buttonopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_file("/sys/class/gpio/gpio49/direction", "out");
                write_file("/sys/class/gpio/gpio49/value", "3");
            }
        });
        buttonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_file("/sys/class/gpio/gpio49/direction", "out");
                write_file("/sys/class/gpio/gpio49/value", "0");
            }
        });
        buttonreturn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thirdActivity.this, secondActivity.class);
                startActivity(intent);
            }
        });
        buttonsendnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNumber();
            }
        });
    }
    public void onDestroy()
    {
        if (mSerialPortHelper != null) {
            mSerialPortHelper.close();
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 99) {
                //   uartOutput.setText((String)msg.obj);
            } else if (msg.what == 98) {
                //   uartOutput.setText("");
            } else if (msg.what == 100) {
                // 延时一分钟后执行发送操作
            }
        }
    };
    private void sendNumber() {
        // 获取用户输入的数字
        String input = etNumberInput.getText().toString();

        // 构建发送的字符串
        String sendString = "TC1:TCADJTEMP=" + input + "\r";

        // 将字符串转换为字节数组
        byte[] byteArray = sendString.getBytes();
        String hexString = bytesToHex(byteArray);

        if (mSerialPortHelper != null) {
            // 发送十六进制字符串
            mSerialPortHelper.sendHex(hexString);
        }
    }
    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
    public void write_file(String w_file, String value) {
        try
        {
            FileWriter ffw = new FileWriter(w_file);
            BufferedWriter fw = new BufferedWriter(ffw);
            fw.write(value);
            fw.close();
        }catch(IOException e){};
    }
}

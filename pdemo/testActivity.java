package com.peite.pdemo;

import androidx.appcompat.app.AppCompatActivity;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class testActivity extends AppCompatActivity {
    private int MIN_MARK = 0;
    private int MAX_MARK = 255;
    private float weight=0;
    private static final String TAG = "MainActivity";
    private SerialPortHelper mSerialPortHelper;
    private String recv; // 类的成员变量
    private String recvBuffer = "";
private  Button liusu1;
private  Button liusu2;
private  Button liusu3;
private  Button liusu4;
private Button liusu5;
private Button chengzhong;
private Button qingling;
private Button qipaijiance;
private Button yalijiance;
private TextView TEST;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        liusu1=findViewById(R.id.button_liusu1);
        liusu2=findViewById(R.id.button_liusu2);
        liusu3=findViewById(R.id.button_liusu3);
        liusu4=findViewById(R.id.button_liusu4);
        liusu5=findViewById(R.id.button_liusu5);
        chengzhong=findViewById(R.id.button_chengzhong1);
        qingling=findViewById(R.id.button_qingling);
        qipaijiance=findViewById(R.id.button_qipaojiance1);
        yalijiance=findViewById(R.id.button_dusejiance);
        TEST=findViewById(R.id.testresult);
        mSerialPortHelper = new SerialPortHelper();
        mSerialPortHelper.setStopBits(STOPB.getStopBit(STOPB.B1));
        mSerialPortHelper.setDataBits(DATAB.getDataBit(DATAB.CS8));
        mSerialPortHelper.setParity(PARITY.getParity(PARITY.NONE));
        mSerialPortHelper.setFlowCon(FLOWCON.getFlowCon(FLOWCON.NONE));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        liusu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        liusu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        liusu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openpump();
                rotatespeed5();
            }
        });
        liusu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openpump();
                rotatespeed40();
            }
        });
        liusu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        chengzhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testweight();
            }
        });
        qingling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearweight();
            }
        });
        qipaijiance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qipaojiance();
            }
        });
        yalijiance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yalijiance();
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
    public String read_file(String r_file) {
        String ret="";
        try
        {
            FileReader ffr= new FileReader(r_file);
            BufferedReader fr = new BufferedReader(ffr);
            ret = fr.readLine();
            fr.close();
            return ret;
        }catch(IOException e){};
        return ret;
    }
    private void openpump() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress = 0    ;  // 起始地址
        int quantity =1 ;  //
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//打开开关
        uart_close();
    }
    private void rotatespeed5() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =50;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速5转rpm
        uart_close();
    }
    private void rotatespeed40() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =400;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    public void sendModbusRequest(int slaveId, int functionCode, int startAddress, int quantity) {
        // 构建Modbus RTU请求帧
        byte[] request = buildModbusRequest(slaveId, functionCode, startAddress, quantity);

        if (mSerialPortHelper != null) {
            // 将字节数组转换为16进制字符串并发送
            String hexString = bytesToHex(request);
            mSerialPortHelper.sendHex(hexString);
            Log.i(TAG, "Modbus Request Sent: " + hexString);
            try {
                // 添加100毫秒的延时，确保Modbus通信的稳定性
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Serial port is not open. Unable to send Modbus request.");
        }
    }
    private byte[] buildModbusRequest(int slaveId, int functionCode, int startAddress, int quantity) {
        byte[] request = new byte[8];

        request[0] = (byte) slaveId;  // 从站地址
        request[1] = (byte) functionCode;  // 功能码
        request[2] = (byte) (startAddress >> 8);  // 起始地址高位
        request[3] = (byte) (startAddress & 0xFF);  // 起始地址低位
        request[4] = (byte) (quantity >> 8);  // 数据高位
        request[5] = (byte) (quantity & 0xFF);  // 数据低位

        // 计算CRC，只针对前6个字节进行CRC校验
        int crc = calculateCRC(request, 6);  // 计算前6个字节的CRC

        // 按小端序排列CRC
        request[6] = (byte) (crc & 0xFF);  // CRC低位
        request[7] = (byte) ((crc >> 8) & 0xFF);  // CRC高位

        return request;
    }
    private int calculateCRC(byte[] data, int length) {
        int crc = 0xFFFF;  // 初始化CRC值

        for (int i = 0; i < length; i++) {
            crc ^= data[i] & 0xFF;  // 按位异或当前字节
            for (int j = 0; j < 8; j++) {  // 对每一位进行处理
                if ((crc & 0x01) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;  // 异或多项式
                } else {
                    crc >>= 1;
                }
            }
        }

        return crc;  // 返回最终计算的CRC值
    }
    private void testweight() {
        uart_open2();
        int slaveId = 1;
        int functionCode = 3;
        int startAddress = 80 ;
        int quantity =2 ;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);
        uart_close();
    }
    private void clearweight() {
        uart_open3();
        byte[] command = new byte[]{(byte) 0x01, (byte) 0x10, (byte) 0x00, (byte) 0x5E, (byte) 0x00,
                (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x6A, (byte) 0xEE};
        mSerialPortHelper.sendBytes(command);
        uart_close();
        uart_close();
    }
    private void qipaojiance() {
        write_file("/sys/class/gpio/gpio46/direction", "in");
        String a =read_file("/sys/class/gpio/gpio46/value");
        float a1 = Float.parseFloat(a);
        if(a1>0){
            TEST.setText("检测有气泡");
        }
        else{
            TEST.setText("检测无气泡");
        }
    }
    private void yalijiance() {
        write_file("/sys/class/gpio/gpio45/direction", "in");
        String a =read_file("/sys/class/gpio/gpio45/value");
        float a1 = Float.parseFloat(a);
        if(a1>0){
            TEST.setText("检测有压力变化");
        }
        else{
            TEST.setText("检测无压力变化");
        }
    }
    public void uart_open1() {
        mSerialPortHelper.close();
        mSerialPortHelper.setPort("/dev/ttyS4"); // 设置端口号为确定值 "/dev/ttyS4"
        mSerialPortHelper.setBaudRate(9600);    // 设置波特率为确定值 9600
        mSerialPortHelper.setIOpenSerialPortListener(new IOpenSerialPortListener() {
            @Override
            public void onSuccess(final File device) {
                Log.i(TAG, device.getPath() + " :串口打开成功");
                // 如果需要通知其他部分可以使用Handler或者其他方式
                //btnSend.setEnabled(true);
                //btnClose.setEnabled(true);
                //btnOpen.setEnabled(false);
            }

            @Override
            public void onFail(final File device, final Status status) {
                switch (status) {
                    case NO_READ_WRITE_PERMISSION:
                        Log.e(TAG, device.getPath() + " :没有读写权限");
                        break;
                    case OPEN_FAIL:
                    default:
                        Log.e(TAG, device.getPath() + " :串口打开失败");
                        break;
                }
            }
        });

        mSerialPortHelper.setISerialPortDataListener(new ISerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X", b));
                }
                recv = sb.toString();
                recvBuffer += recv;
                Message msg = mHandler.obtainMessage();
                msg.arg1 = 1;
                msg.what = 99;
                msg.obj = recvBuffer;
                mHandler.sendMessage(msg);
                Log.i(TAG, "onDataReceived: " + recv);
            }

            @Override
            public void onDataSend(byte[] bytes) {
                Log.i(TAG, "onDataSend: " + Arrays.toString(bytes));
            }
        });

        boolean isOpen = mSerialPortHelper.open();
        Log.i(TAG, "open: " + isOpen);
    }
    public void uart_open2() {
        mSerialPortHelper.close();
        mSerialPortHelper.setPort("/dev/ttyS5"); // 设置端口号为确定值 "/dev/ttyS5"
        mSerialPortHelper.setBaudRate(9600);    // 设置波特率为确定值 9600
        mSerialPortHelper.setIOpenSerialPortListener(new IOpenSerialPortListener() {
            @Override
            public void onSuccess(final File device) {
                Log.i(TAG, device.getPath() + " :串口打开成功");
                // 如果需要通知其他部分可以使用Handler或者其他方式
                //btnSend.setEnabled(true);
                //btnClose.setEnabled(true);
                //btnOpen.setEnabled(false);
            }

            @Override
            public void onFail(final File device, final Status status) {
                switch (status) {
                    case NO_READ_WRITE_PERMISSION:
                        Log.e(TAG, device.getPath() + " :没有读写权限");
                        break;
                    case OPEN_FAIL:
                    default:
                        Log.e(TAG, device.getPath() + " :串口打开失败");
                        break;
                }
            }
        });

        mSerialPortHelper.setISerialPortDataListener(new ISerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X", b));
                }
                recv = sb.toString();
                recvBuffer += recv;
                if (recv.length() >= 14) {
                    String dataBytes = recv.substring(6, 14);
                    long dataValue = Long.parseLong(dataBytes, 16);

                    // 将 dataValue 转换为带小数的格式
                    weight = dataValue / 10.0f;
                    // 使用 Handler 切换到主线程更新 UI
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            TEST.setText("称重结果为："+weight);
                        }
                    });
                }
                Message msg = mHandler.obtainMessage();
                msg.arg1 = 1;
                msg.what = 99;
                msg.obj = recvBuffer;
                mHandler.sendMessage(msg);
                Log.i(TAG, "onDataReceived: " + recv);
            }

            @Override
            public void onDataSend(byte[] bytes) {
                Log.i(TAG, "onDataSend: " + Arrays.toString(bytes));
            }
        });

        boolean isOpen = mSerialPortHelper.open();
        Log.i(TAG, "open: " + isOpen);
    }
    public void uart_open3() {
        mSerialPortHelper.close();
        mSerialPortHelper.setPort("/dev/ttyS5"); // 设置端口号为确定值 "/dev/ttyS5"
        mSerialPortHelper.setBaudRate(9600);    // 设置波特率为确定值 9600
        mSerialPortHelper.setIOpenSerialPortListener(new IOpenSerialPortListener() {
            @Override
            public void onSuccess(final File device) {
                Log.i(TAG, device.getPath() + " :串口打开成功");
                // 如果需要通知其他部分可以使用Handler或者其他方式
                //btnSend.setEnabled(true);
                //btnClose.setEnabled(true);
                //btnOpen.setEnabled(false);
            }

            @Override
            public void onFail(final File device, final Status status) {
                switch (status) {
                    case NO_READ_WRITE_PERMISSION:
                        Log.e(TAG, device.getPath() + " :没有读写权限");
                        break;
                    case OPEN_FAIL:
                    default:
                        Log.e(TAG, device.getPath() + " :串口打开失败");
                        break;
                }
            }
        });

        mSerialPortHelper.setISerialPortDataListener(new ISerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X", b));
                }
                recv = sb.toString();
                recvBuffer += recv;
                Message msg = mHandler.obtainMessage();
                msg.arg1 = 1;
                msg.what = 99;
                msg.obj = recvBuffer;
                mHandler.sendMessage(msg);
                Log.i(TAG, "onDataReceived: " + recv);
            }

            @Override
            public void onDataSend(byte[] bytes) {
                Log.i(TAG, "onDataSend: " + Arrays.toString(bytes));
            }
        });

        boolean isOpen = mSerialPortHelper.open();
        Log.i(TAG, "open: " + isOpen);
    }
    public void uart_close() {
        if (mSerialPortHelper != null) {
            mSerialPortHelper.close();
            // 如果需要通知其他部分可以使用Handler或者其他方式
            //btnSend.setEnabled(false);
            // btnClose.setEnabled(false);
            // btnOpen.setEnabled(true);
        }
    }
}

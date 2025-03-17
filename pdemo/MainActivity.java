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
import android.os.StrictMode;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStream;
import java.net.MalformedURLException;
import android.os.Looper;

 class HttpExample {

    // 定义访问URL的方法
    protected String accessUrl(String urlString) {
        String response = "";
        HttpURLConnection urlConnection = null;

        try {
            System.out.println("准备连接到 URL: " + urlString);

            // 创建 URL 对象
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET"); // 使用GET请求
            urlConnection.setConnectTimeout(15000); // 设置连接超时时间为15秒
            urlConnection.setReadTimeout(15000);    // 设置读取超时时间为15秒

            System.out.println("连接成功，发送请求...");

            // 获取响应码
            int responseCode = urlConnection.getResponseCode();
            System.out.println("响应码: " + responseCode);

            // 检查响应码是否为200（HTTP_OK）
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应
                try (InputStream inputStream = urlConnection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    response = buffer.toString();
                }
            } else {
                System.err.println("请求失败，响应码：" + responseCode);
            }

        } catch (MalformedURLException e) {
            System.err.println("URL 格式错误: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO 异常: " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }

    public static void main1(String[] args) {
        // 创建 HttpExample 实例
        HttpExample httpExample = new HttpExample();

        // 要访问的 URL
        String url = "http://119.3.4.78:9975/api/WorkStation/Sign/GetMobileDeviceSimpleData?type=SIGN&code=O_I&value=25";

        // 调用 accessUrl 方法并获取响应
        String response = httpExample.accessUrl(url);

        // 打印返回结果
        if (response != null && !response.isEmpty()) {
            System.out.println("服务器返回的响应:");
            System.out.println(response);
        } else {
            System.out.println("没有获得响应或响应为空！");
        }
    }
}
class DataUploader {

    public static void uploadData(String type, String code, double value) {
        // API 地址
        String apiUrl = "http://119.3.4.78:9975/api/WorkStation/Sign/GetMobileDeviceSimpleData?type=SIGN&code=O_I&value=25";

        try {
            // 建立连接
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // 准备 POST 参数
            String postData = "Type=" + type +
                    "&Code=" + code +
                    "&Value=" + value;

            // 发送数据
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
            }

            // 获取响应码
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 读取服务器响应内容
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            // 打印响应
            System.out.println("Response Content: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class MainActivity extends AppCompatActivity{

    private int MIN_MARK = 0;
    private int MAX_MARK = 255;
    private final static String TAG ="GzpeiteDemao";
//    private Spinner spinner_dev;
//    private Spinner spinner_baud;
   // protected EditText uartInput;
    //protected EditText uartOutput;
    //protected Button btnSend;
    //protected Button btnOpen;
   // protected Button btnClose;
    private Button buttonBegin;
//    private Button buttonTEST;
//    private Button buttonClosetest;
    private Button buttonresult;
    private Button buttonreturn;
    private Button buttontestqipao;
    private Button buttontestyali;
    private Button buttonweightzero;
    private String recv; // 类的成员变量
   // protected Button btnSendNumber; // 新添加的发送按钮
   // protected EditText etNumberInput; // 新添加的输入数字的文本框
    private TextView tvSentArray;
    private TextView testresult;
    private TextView timeresult;
    private TextView qipaojiance;
    private TextView yalijiance;
    private TextView textViewTemperatureReached;
    private SerialPortHelper mSerialPortHelper;
    private String recvBuffer = "";
    private float weight = 0.0f;  // 定义为类的成员变量，初始值为 0
    private float weight1=0.0f;
    private Date nowtime1 = null;  // 第一次获取时间时的时间
    private Date nowtime2 = null;  // 每次更新时的当前时间
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        spinner_dev = (Spinner) findViewById(R.id.uart_dev);
//        spinner_dev.setSelection(1);
       // spinner_baud = (Spinner) findViewById(R.id.uart_baud);
//        spinner_baud.setSelection(0);
       // uartInput = (EditText) findViewById(R.id.editText_uartsend);
       // uartOutput = (EditText) findViewById(R.id.editText_uartreceive);
      //  btnOpen = (Button) findViewById(R.id.button_uartopen);
      //  btnClose = (Button) findViewById(R.id.button_uartclose);
       // btnSend = (Button) findViewById(R.id.button_uartsend);
        // 初始化新添加的控件
     //   btnSendNumber = findViewById(R.id.button_sendnumber);
    //    etNumberInput = findViewById(R.id.editText_numberinput);
//        tvSentArray = findViewById(R.id.tvSentArray);
        testresult = findViewById(R.id.testresult1);
        testresult.setText(""); // 初始设置为空
        timeresult=findViewById(R.id.time);
        timeresult.setText("");
        textViewTemperatureReached = findViewById(R.id.Tep42);
        textViewTemperatureReached.setText(""); // 初始设置为空
        qipaojiance=findViewById(R.id.qipaojiance);
        yalijiance=findViewById(R.id.yalijiance);
        buttonBegin = findViewById(R.id.button_begin);
        buttontestqipao=findViewById(R.id.button_qipaojiance);
        buttontestyali=findViewById(R.id.button_yalijiance);
//        buttonTEST = findViewById(R.id.button_yingguangjiance);
//        buttonClosetest=findViewById(R.id.button_guanbiyingguangjiance);
//        buttonresult = findViewById(R.id.button_result);
        buttonreturn=findViewById(R.id.button_return);
        buttonweightzero=findViewById(R.id.button_weightzero);

        //uartOutput.setKeyListener(null);
        mSerialPortHelper = new SerialPortHelper();
        mSerialPortHelper.setStopBits(STOPB.getStopBit(STOPB.B1));
        mSerialPortHelper.setDataBits(DATAB.getDataBit(DATAB.CS8));
        mSerialPortHelper.setParity(PARITY.getParity(PARITY.NONE));
        mSerialPortHelper.setFlowCon(FLOWCON.getFlowCon(FLOWCON.NONE));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //新的控件
//        buttonBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 在按钮点击事件中执行相应的操作
//                beginProcess1(); // 例如开始某个过程
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        textViewTemperatureReached.setText("反应已经完成，现在升温至50°");
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                beginProcess2();
//                                mHandler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        textViewTemperatureReached.setText("反应已经完成");
//                                        CLOSE();
//                                    }
//                                }, 1200000);
//                            }
//                        }, 10000);
//                    }
//                }, 1200000);
//            }
//        });
        buttonBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginProcess1();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        testweight();
                        updateTime();
                        qipaojiance();
                        yalijiance();
                        if (weight > 500.5) {
                            rotatespeed100();
                        } else {
                            rotatespeed5();
                        }
                        mHandler.postDelayed(this, 60000);
                    }
                };
                mHandler.postDelayed(runnable, 60000);
            }
        });
        buttontestqipao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                qipaojiance();
                fetchData();
            }
        });
        buttontestyali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yalijiance();
            }
        });
        buttontestyali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearweight();
            }
        });
//        btnSendNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendNumber();
//            }
//        });
//        buttonTEST.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                write_file("/sys/class/gpio/gpio49/direction", "out");
//                write_file("/sys/class/gpio/gpio49/value", "3");
//            }
//        });
//        buttonClosetest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                write_file("/sys/class/gpio/gpio49/direction", "out");
//                write_file("/sys/class/gpio/gpio49/value", "0");
//            }
//        });
//        buttonresult.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                write_file("/sys/class/gpio/gpio49/direction", "out");
//                write_file("/sys/class/gpio/gpio49/value", "3");
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView name=findViewById(R.id.tvSentArray);
//                        name.setText(read_file("/sys/class/gzpeite/user/adc1_vol"));
//                        TEST();
//                    }
//                }, 30000);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        write_file("/sys/class/gpio/gpio49/direction", "out");
//                        write_file("/sys/class/gpio/gpio49/value", "0");
//                    }
//                }, 30000);
//            }
//        });
        buttonreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, secondActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
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
                beginProcess1();
            }
        }
    };


    private void setRegion( final EditText et)
    {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start > 1)
                {
                    if (MIN_MARK != -1 && MAX_MARK != -1)
                    {
                        int num = Integer.parseInt(s.toString());
                        if (num > MAX_MARK)
                        {
                            s = String.valueOf(MAX_MARK);
                            et.setText(s);
                        }
                        else if(num < MIN_MARK)
                            s = String.valueOf(MIN_MARK);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s != null && !s.equals(""))
                {
                    if (MIN_MARK != -1 && MAX_MARK != -1)
                    {
                        int markVal = 0;
                        try
                        {
                            markVal = Integer.parseInt(s.toString());
                        }
                        catch (NumberFormatException e)
                        {
                            markVal = 0;
                        }
                        if (markVal > MAX_MARK)
                        {
                            Toast.makeText(getBaseContext(), "超出范围", Toast.LENGTH_SHORT).show();
                            et.setText(String.valueOf(MAX_MARK));
                        }
                        return;
                    }
                }
            }
        });
    }
    //daima1
//    private void sendNumber() {
//        // 获取用户输入的数字
//        String input = etNumberInput.getText().toString();
//
//        // 构建发送的字符串
//        String sendString = "TC1:TCADJTEMP=" + input + "\r";
//
//        // 将字符串转换为字节数组
//        byte[] byteArray = sendString.getBytes();
//        String hexString = bytesToHex(byteArray);
//
//        if (mSerialPortHelper != null) {
//            // 发送十六进制字符串
//            mSerialPortHelper.sendHex(hexString);
//        }
//
//        // 更新 tvSentArray 的文本内容为发送的数组
//        tvSentArray.setText("Sent Array: " + hexString);
//        mHandler.sendEmptyMessageDelayed(100, 60000);
//    }
    private void updateTime() {
        // 获取当前时间
        nowtime2 = new Date();

        // 如果 nowtime1 不是 null，则计算时间差
        if (nowtime1 != null) {
            // 计算时间差（毫秒）
            long timeDifference = nowtime2.getTime() - nowtime1.getTime();

            // 转换时间差为小时、分钟、秒
            long seconds = timeDifference / 1000;
            long hours = seconds / 3600;
            seconds %= 3600; // 剩余的秒数
            long minutes = seconds / 60;
            seconds %= 60; // 剩余的秒数
            timeresult.setText("现在的时间是"+sdf.format(nowtime2)+"距上次测量时间差为: " + hours + " 小时 " + minutes + " 分 " + seconds + " 秒\n" );
        }
        else{
            timeresult.setText("首次运行，现在是时间是："+sdf.format(nowtime2));
        }

        // 更新 nowtime1 为 nowtime2
        nowtime1 = nowtime2;
    }
//    private void getCurrentTime() {
//        // 格式化当前时间，仅获取小时、分钟和秒钟
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        nowtime= sdf.format(new Date());
//        textViewTemperatureReached.setText("现在的时间是："+nowtime);
//    }
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
    private void closepump() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress = 0    ;  // 起始地址
        int quantity =0 ;  //
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
    private void rotatespeed10() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =100;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速10转rpm
        uart_close();
    }
    private void rotatespeed15() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =150;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速15转rpm
        uart_close();
    }
    private void rotatespeed20() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =200;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速20转rpm
        uart_close();
    }
    private void rotatespeed25() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =250;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速25转rpm
        uart_close();
    }
    private void rotatespeed30() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =300;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void rotatespeed35() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =350;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
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
    private void rotatespeed45() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =450;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void rotatespeed50() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =500;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void rotatespeed60() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =600;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void rotatespeed70() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =700;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void rotatespeed80() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =800;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void rotatespeed90() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =900;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void rotatespeed100() {
        uart_open1();
        int slaveId = 1;  // 从站地址
        int functionCode = 6;  // 功能码：读取保持寄存器
        int startAddress =2    ;  // 起始地址
        int quantity =1000;
        sendModbusRequest(slaveId, functionCode, startAddress, quantity);//转速30转rpm
        uart_close();
    }
    private void beginProcess1() {
        uart_open();
        TCSW0();
        TCMAXV8();
        TCOTPHT60();
        MAXTEMP60();
        TC37();
        TCSW1();
        // 开始查询温度并等待温度达到 37 度
        queryTemperatureAndWaitFor37();
    }


//    private void queryTemperatureAndWaitFor42() {
//        // 查询当前温度
//        sendStringToSerialPort();
//
//        // 检查温度是否达到 42 度，如果未达到则延迟一段时间后再次查询
//        if (!isTemperatureReached42()) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    queryTemperatureAndWaitFor42(); // 递归调用以继续查询温度
//                }
//            }, 5000); // 每隔2s查询一次
//        }
//    }
private void queryTemperatureAndWaitFor37() {
    // 查询当前温度
    sendStringToSerialPort();

    // 检查温度是否达到 42 度，如果未达到则延迟一段时间后再次查询
    if (!isTemperatureReached37()) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryTemperatureAndWaitFor37(); // 递归调用以继续查询温度
            }
        }, 5000); // 每隔5秒查询一次
    } else {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                openpump();
                rotatespeed50();
            }
        });
    }
}


    private boolean isTemperatureReached37() {

        if (recvBuffer != null && recvBuffer.length() >= 12) {
            // 从回复中提取温度值部分（假设回复格式固定，温度值部分在第14个字符之后）
            int a=recvBuffer.length();
            String temperatureStr = recvBuffer.substring(a-12,a-2);
            String temperatureString=hexToString(temperatureStr);
            float temperature = Float.parseFloat(temperatureString);
            // 判断温度是否达到42度
            if(temperature>=36){
                textViewTemperatureReached.setText("目前已到预定温度37°");
                return true;
            }
            else {
                textViewTemperatureReached.setText("目前尚未达到目标，当前为："+temperature+"°");
                return false;
            }
        }
        // 如果未收到正确的温度回复，则返回 false
        else{
            textViewTemperatureReached.setText("错误");
            return true;
        }
    }
    private void beginProcess2() {
        TCSW0();
        TC50();
        TCSW1();
        queryTemperatureAndWaitFor50();

    }
    private void qipaojiance() {
        write_file("/sys/class/gpio/gpio46/direction", "in");
        String a =read_file("/sys/class/gpio/gpio46/value");
        float a1 = Float.parseFloat(a);
        if(a1>0){
            qipaojiance.setText("检测有气泡");
        }
        else{
            qipaojiance.setText("检测无气泡");
        }
    }
    private void yalijiance() {
        write_file("/sys/class/gpio/gpio45/direction", "in");
        String a =read_file("/sys/class/gpio/gpio45/value");
        float a1 = Float.parseFloat(a);
        if(a1>0){
            yalijiance.setText("检测有压力变化");
        }
        else{
            yalijiance.setText("检测无压力变化");
        }
    }
//    private void LightTest() {
//        write_file("/sys/class/gpio/gpio49/direction", "out");
//        write_file("/sys/class/gpio/gpio49/value", "3");
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                TextView name=findViewById(R.id.tvSentArray);
//                name.setText(read_file("/sys/class/gzpeite/user/adc1_vol"));
//                TEST();
//            }
//        }, 30000);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                write_file("/sys/class/gpio/gpio49/direction", "out");
//                write_file("/sys/class/gpio/gpio49/value", "0");
//            }
//        }, 30000);
//    }
    private void TEST() {
        String text = tvSentArray.getText().toString();
        int value = Integer.parseInt(text);
        int value1=value*5;
        if(value1>=10){
            testresult.setText("检测为有荧光");
        }
        else{
            testresult.setText("检测为无荧光");
        }
    }

    private void queryTemperatureAndWaitFor50() {
        // 查询当前温度
        sendStringToSerialPort();
        // 检查温度是否达到 50 度，如果未达到则延迟一段时间后再次查询
        if (!isTemperatureReached50()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryTemperatureAndWaitFor50(); // 递归调用以继续查询温度
                }
            }, 5000); // 每隔2s查询一次
        }
    }

    private boolean isTemperatureReached50() {
        // 判断是否收到了温度回复
        if (recvBuffer != null && recvBuffer.length() >= 12) {
            // 从回复中提取温度值部分（假设回复格式固定，温度值部分在第14个字符之后）
            int a=recvBuffer.length();
            String temperatureStr = recvBuffer.substring(a-12,a-2);
            String temperatureString=hexToString(temperatureStr);
            float temperature = Float.parseFloat(temperatureString);
            // 判断温度是否达到42度
            if(temperature>=49.50){
                textViewTemperatureReached.setText("目前已到预定温度50°,现在反应20分钟");
                return true;
            }
            else {
                textViewTemperatureReached.setText("目前尚未达到目标，当前为："+temperature+"°");
                return false;
            }
        }
        // 如果未收到正确的温度回复，则返回 false
        else{
            textViewTemperatureReached.setText("错误");
            return true;
        }
    }
    private void fetchData() {
        String url = "http://119.3.4.78:9975/api/WorkStation/Sign/GetMobileDeviceSimpleData?type=SIGN&code=O_I&value=25";

        // 在子线程中执行网络请求
        new Thread(() -> {
            // 调用网络请求方法
            String response = accessUrl(url);

            // 切换到主线程更新UI
            new Handler(Looper.getMainLooper()).post(() -> {
                if (response != null && !response.isEmpty()) {
                    testresult.setText(response); // 更新 TextView 显示响应
                } else {
                    testresult.setText("获取数据失败或为空！");
                }
            });
        }).start();
    }
    protected String accessUrl(String urlString) {
        String response = "";
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    response = buffer.toString();
                }
            } else {
                System.err.println("HTTP 错误响应码: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }


    public static String hexToString(String hexString) {
        int length = hexString.length();
        StringBuilder decodedString = new StringBuilder();
        for (int i = 0; i < length; i += 2) {
            String hex = hexString.substring(i, i + 2);
            int decimal = Integer.parseInt(hex, 16);
            decodedString.append((char) decimal);
        }
        return decodedString.toString();
    }
    private void sendStringToSerialPort() {
        // 构建要发送的字符串
        String command = "TC1:TCACTTEMP?"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void CLOSE(){
        write_file("/sys/class/gpio/gpio49/direction", "out");
        write_file("/sys/class/gpio/gpio49/value", "0");
        TCSW0();
        TC25();
        uart_close();
    }
    private void TC42() {
        // 构建要发送的字符串
        String command = "TC1:TCADJTEMP=43"+"\r";
        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void TC37() {
        // 构建要发送的字符串
        String command = "TC1:TCADJTEMP=37"+"\r";
        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void TC47() {
        // 构建要发送的字符串
        String command = "TC1:TCADJTEMP=47"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void TC50() {
        // 构建要发送的字符串
        String command = "TC1:TCADJTEMP=51"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void TC25() {
        // 构建要发送的字符串
        String command = "TC1:TCADJTEMP=25"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void MAXTEMP60() {
        // 构建要发送的字符串
        String command = "TC1:TCMAXTEMP=60"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void TCSW0() {
        // 构建要发送的字符串
        String command = "TC1:TCSW=0" + "\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void TCSW1() {
        // 构建要发送的字符串
        String command = "TC1:TCSW=1"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void TCMAXV8() {
        // 构建要发送的字符串
        String command = "TC1:TCMAXV=8"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void TCOTPHT60() {
        // 构建要发送的字符串
        String command = "TC1:TCOTPHT=60"+"\r";

        // 将字符串转换为字节数组
        byte[] byteArray = command.getBytes();
        String hexString = bytesToHex(byteArray);

        // 如果串口辅助类不为 null，则发送数据
        if (mSerialPortHelper != null) {
            mSerialPortHelper.sendHex(hexString);

            try {
                // 添加延时100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 将字符串转换为ASCII码
    private String stringToAscii(String input) {
        StringBuilder ascii = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            ascii.append((int) c);
        }
        return ascii.toString();
    }

    // 将ASCII码转换为16进制
    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
    protected void write_file(String w_file, String value) {
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

//    public void gpio1_output_high(View view)
//    {
//        write_file("/sys/class/gpio/gpio49/direction", "out");
//        write_file("/sys/class/gpio/gpio49/value", "3");
//    }

   // public void gpio1_output_low(View view)
   // {
   //     write_file("/sys/class/gpio/gpio49/direction", "out");
   //     write_file("/sys/class/gpio/gpio49/value", "0");
//}

    //public void gpio1_input(View view)
    //{
    //    write_file("/sys/class/gpio/gpio49/direction", "in");
    //}

    //public void gpio1_get_value(View view)
    //{
    //    TextView name=findViewById(R.id.textView1);
     //   name.setText(read_file("/sys/class/gpio/gpio49/value"));
    //}

//    public void wathcdog_open(View view)
//    {
//        write_file("/sys/class/gzpeite/user/watch_dog", "1");
//    }
//
//    public void wathcdog_feed(View view)
//    {
//        write_file("/sys/class/gzpeite/user/watch_dog", "2");
//    }
//
//    public void wathcdog_close(View view)
//    {
//        write_file("/sys/class/gzpeite/user/watch_dog", "0");
//    }

//    public void fullscreen_on(View view)
//    {
//        Intent w_intent = new Intent();
//        w_intent.setAction("gzpeite.intent.systemui.hidenavigation");
//        sendBroadcast(w_intent);
//        w_intent.setAction("gzpeite.intent.systemui.hidestatusbar");
//        sendBroadcast(w_intent);
//    }

//    public void fullscreen_off(View view)
//    {
//        Intent w_intent = new Intent();
//        w_intent.setAction("gzpeite.intent.systemui.shownavigation");
//        sendBroadcast(w_intent);
//        w_intent.setAction("gzpeite.intent.systemui.showstatusbar");
//        sendBroadcast(w_intent);
//    }

//    public void system_reboot(View view)
//    {
//        Intent w_intent = new Intent();
//        w_intent.setAction("gzpeite.intent.action.reboot");
//        w_intent.putExtra("confirm", false);
////        w_intent.putExtra("confirm", true);
//        sendBroadcast(w_intent);
//    }

    public void system_poweroff(View view)
    {
        Intent w_intent = new Intent();
        w_intent.setAction("gzpeite.intent.action.shutdown");
        w_intent.putExtra("confirm", false);
//        w_intent.putExtra("confirm", true);
        sendBroadcast(w_intent);
    }


//    public  void RootCommand(String command)
//    {
//        Process process = null;
//        DataOutputStream os = null;
//
//        try {
//            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists()))
//            {
//                Log.d("PeiTe CheckRoot", "not found su");
//                Toast.makeText(getApplicationContext(), "没有找到 su 文件", Toast.LENGTH_LONG).show();
//                return;
//            } else {
//                Log.d("PeiTe CheckRoot", "has found su");
//                Toast.makeText(getApplicationContext(), "已经找到 su 文件", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//        }
//        try{
//
//            Toast.makeText(getApplicationContext(), "正在获取ROOT权限...", Toast.LENGTH_LONG).show();
//            process = Runtime.getRuntime().exec("su");
//            os = new DataOutputStream(process.getOutputStream());
//            os.writeBytes(command + "\n");
//            os.writeBytes("exit\n");
//            os.flush();
//            int value = process.waitFor();
//            Log.d("PeiTe CheckRoot", "su return " + String.valueOf(value));
//            if(value != 0)
//            {
//                Log.d("PeiTe CheckRoot", "run su error");
//                Toast.makeText(getApplicationContext(), "获取ROOT权限时出错!", Toast.LENGTH_LONG).show();
//                return;
//            }
//        }
//        catch (Exception e){
//            Log.d("PeiTe CheckRoot", "run su error");
//            Toast.makeText(getApplicationContext(), "获取ROOT权限时出错!", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//            return;
//        }finally
//        {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//                process.destroy();
//            }catch (Exception e)
//            {
//                return;
//            }
//        }
//        Toast.makeText(getApplicationContext(), "成功获取ROOT权限", Toast.LENGTH_LONG).show();
//        Log.d("PeiTe CheckRoot", "get root success");
//        return;
//    }

//    public void system_getroot(View view)
//    {
//        String apkRoot="chmod 777 "+getPackageCodePath();
//        Log.d("PeiTe CheckRoot", apkRoot);
//        RootCommand(apkRoot);
//    }

//    public void uninstall_app(View view)
//    {
//        Intent w_intent = new Intent();
//        w_intent.setAction("gzpeite.intent.action.uninstall_apk");
//        w_intent.putExtra("pkg_name", "com.peite.pdemo");
//        sendBroadcast(w_intent);
//    }

//    public void uart_open(View view)
//    {
//        mSerialPortHelper.close();
//        mSerialPortHelper.setPort("/dev/ttyS2"); // 设置端口号为确定值 "/dev/ttyS2"
//        mSerialPortHelper.setBaudRate(57600);    // 设置波特率为确定值 57600
//        mSerialPortHelper.setIOpenSerialPortListener(new IOpenSerialPortListener() {
//            @Override
//            public void onSuccess(final File device) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, device.getPath() + " :串口打开成功", Toast.LENGTH_SHORT).show();
//                     //   btnSend.setEnabled(true);
//                        btnClose.setEnabled(true);
//                        btnOpen.setEnabled(false);
//                    }
//                });
//            }
//
//            @Override
//            public void onFail(final File device, final Status status) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        switch (status) {
//                            case NO_READ_WRITE_PERMISSION:
//                                Toast.makeText(MainActivity.this, device.getPath() + " :没有读写权限", Toast.LENGTH_SHORT).show();
//                                break;
//                            case OPEN_FAIL:
//                            default:
//                                Toast.makeText(MainActivity.this, device.getPath() + " :串口打开失败", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                    }
//                });
//            }
//        });
//        mSerialPortHelper.setISerialPortDataListener(new ISerialPortDataListener() {
//            @Override
//            public void onDataReceived(byte[] bytes) {
//                StringBuilder sb = new StringBuilder();
//                for (byte b : bytes)
//                    sb.append(String.format("%02X", b));
//                recv = sb.toString();
//                recvBuffer += recv;
//                Message msg = mHandler.obtainMessage();
//                msg.arg1 = 1;
//                msg.what = 99;
//                msg.obj = recvBuffer;
//                mHandler.sendMessage(msg);
//                Log.i(TAG, "onDataReceived: " + recv);
//            }
//
//            @Override
//            public void onDataSend(byte[] bytes) {
//                Log.i(TAG, "onDataSend: " + Arrays.toString(bytes));
//            }
//        });
//        Log.i(TAG, "open: " + mSerialPortHelper.open());
//    }
public void uart_open() {
    mSerialPortHelper.close();
    mSerialPortHelper.setPort("/dev/ttyS2"); // 设置端口号为确定值 "/dev/ttyS2"
    mSerialPortHelper.setBaudRate(57600);    // 设置波特率为确定值 57600
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
                            if(weight>=weight1){
                                float weight2=weight-weight1;
                                String formattedWeight = String.format("%.1f", weight2);
                                testresult.setText("本次测量重量为: "+ weight+"本时间段累计尿液量为："+formattedWeight);
                                weight1=weight;
                            }
                            else{
                                float weight2=Math.abs(weight-weight1);
                                String formattedWeight = String.format("%.1f", weight2);
                                testresult.setText("本次测量重量为: "+ weight+"本时间段累计尿液量为："+formattedWeight);
                                weight1=weight;
                            }
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
    public void uart_close() {
        if (mSerialPortHelper != null) {
            mSerialPortHelper.close();
            // 如果需要通知其他部分可以使用Handler或者其他方式
            //btnSend.setEnabled(false);
            // btnClose.setEnabled(false);
            // btnOpen.setEnabled(true);
        }
    }
//    public void uart_close(View view)
//    {
//        if (mSerialPortHelper != null) {
//            mSerialPortHelper.close();
//       //     btnSend.setEnabled(false);
//           // btnClose.setEnabled(false);
//            //btnOpen.setEnabled(true);
//        }
//    }

//    public void uart_send(View view)
//    {
//        String str = uartInput.getText().toString();
//        if (mSerialPortHelper != null) {
////            mSerialPortHelper.sendTxt(str);
//            mSerialPortHelper.sendHex(str);
//        }
//    }
//    public void uart_bufclean(View view)
//    {
//        mHandler.sendEmptyMessage(98);
//    }

}

package com.example.serviceTest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class WebSocketService extends Service {
    String serverAddress ="ws://192.168.50.141:1102"; //创建变量：指向服务器地址
    MyWebSocket client; // 创建websocket 对象
    private myBinder mBinder=new myBinder();


class myBinder extends Binder {
    public MyWebSocket getClient(){
        return client;
    }

}

    public void onCreate(){
        super.onCreate();
        initSocketClient();
    }

    public int onStartCommand(Intent intent,int flags,int stariId){

        return super.onStartCommand(intent,flags,stariId);

    }

    public void onDestroy(){
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void initSocketClient()  {
        URI url = null;
        try {
            url = new URI(serverAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client =new MyWebSocket(url){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMessage(String message) {
                try {
                    String s=EncryptDecrypt.aesDecrypt(message);
                    Log.d("tag","Server return message encrypted: "+message);
                    Log.d("tag","Server return message encrypted: "+EncryptDecrypt.aesDecrypt(message));

                    Gson gson = new Gson();
                    Map<String,String> messageMap=gson.fromJson(s,Map.class);
                    Intent intent = new Intent();
                    intent.setAction(messageMap.get("type"));
                    intent.putExtra("message", s);

                    sendBroadcast(intent);
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

class WebSocketServiceConnection implements ServiceConnection {
    MyWebSocket client=null;
    private static WebSocketServiceConnection wssc=new WebSocketServiceConnection();

    private WebSocketServiceConnection() {
    }

    public static WebSocketServiceConnection getInstance(){
        return wssc;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if(client==null){
            WebSocketService.myBinder binder=(WebSocketService.myBinder)iBinder;
            client=binder.getClient();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    public void sendMessage(String s){
        client.send(s);
    }
}

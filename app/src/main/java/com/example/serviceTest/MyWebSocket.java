package com.example.serviceTest;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class MyWebSocket extends WebSocketClient {


  public MyWebSocket(URI serverUri){
      super(serverUri);
  }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onOpen(ServerHandshake handshakedata) {
      try {
        //when connected , sent to server client aes key(encrypt with RSA)
        Log.d("tag", "Generate new AES key:  "+EncryptDecrypt.getAesKey());
        String t=EncryptDecrypt.rsaEncrypt(EncryptDecrypt.getAesKey());
        Log.d("tag","AES key encrypted by Server RSA key:  "+t);
        send(t);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
    }

}

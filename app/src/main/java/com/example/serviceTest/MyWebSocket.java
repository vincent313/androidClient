package com.example.serviceTest;
import android.util.Log;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MyWebSocket extends WebSocketClient {


  public MyWebSocket(URI serverUri){
      super(serverUri);
  }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("myservice","WebSocket Status OPEN");
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

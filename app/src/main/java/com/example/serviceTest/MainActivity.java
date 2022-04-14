package com.example.serviceTest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private WebSocketServiceConnection connection= WebSocketServiceConnection.getInstance();


    public MainActivity() {
    }

    protected void onCreate(Bundle b){

        super.onCreate(b);
        setContentView(R.layout.activity_main);
        Button start=(Button) findViewById(R.id.loginbutton);
        Button stop=(Button) findViewById(R.id.signupbutton);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        bindService();
        doRegisterReceiver();

}

// clear edit text
    protected void onResume() {
        super.onResume();
        ((EditText)findViewById(R.id.singinusername)).setText("");
        ((EditText)findViewById(R.id.signinpassword)).setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.loginbutton:
                JsonObject jsonObject = new JsonObject();
                String username=((EditText)findViewById(R.id.singinusername)).getText().toString();
                String password=((EditText)findViewById(R.id.signinpassword)).getText().toString();
                String passwordMD5="";
                try {
                    passwordMD5= Utils.getMD5(password);
                } catch (NoSuchAlgorithmException e) {
                    break;
                }
                JsonObject loginInfo=new JsonObject();
                loginInfo.addProperty("type","login");
                loginInfo.addProperty("user",username);
                loginInfo.addProperty("pas",passwordMD5);
               connection.sendMessage(loginInfo.toString());
                 break;
            case R.id.signupbutton:
                Intent stopIntent = new Intent(this, SignUpActivity.class);
              startActivity(stopIntent);
                break;
            default:
                break;
        }
    }

    private void bindService(){
    Intent bindIntent =new Intent(MainActivity.this, WebSocketService.class);
    bindService(bindIntent,connection,BIND_AUTO_CREATE);
    }
    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            Gson gson = new Gson();
            Map<String,String> messageMap=gson.fromJson(message,Map.class);
            if (messageMap.get("content").equals("Login fail")){
                onResume();
            }else{
                Intent goMainMenu = new Intent(MainActivity.this, MainMenu.class);
                startActivity(goMainMenu);
            }
            Toast.makeText(MainActivity.this,messageMap.get("content"),Toast.LENGTH_LONG).show();
        }
    }

    private void doRegisterReceiver() {
        ChatMessageReceiver chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("login");
        registerReceiver(chatMessageReceiver, filter);
    }
}


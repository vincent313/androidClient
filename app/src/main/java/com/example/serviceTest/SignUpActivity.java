package com.example.serviceTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private WebSocketServiceConnection connection= WebSocketServiceConnection.getInstance();

    protected void onCreate(Bundle b){

        super.onCreate(b);

        setContentView(R.layout.activity_signup);

        Button start=(Button) findViewById(R.id.sumitSignup);

        start.setOnClickListener(this);
        bindService();

        doRegisterReceiver();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sumitSignup:
                JsonObject jsonObject = new JsonObject();
                String username=((EditText)findViewById(R.id.singupusername)).getText().toString();
                String password=((EditText)findViewById(R.id.signuppassword)).getText().toString();
                String passwordMD5="";
                try {
                    passwordMD5= Utils.getMD5(password);
                } catch (NoSuchAlgorithmException e) {
                    break;
                }
                JsonObject loginInfo=new JsonObject();
                loginInfo.addProperty("type","signup");
                loginInfo.addProperty("user",username);
                loginInfo.addProperty("pas",passwordMD5);
                 connection.sendMessage(loginInfo.toString());
                break;
            default:
                break;
        }
    }

    private void bindService(){
        Intent bindIntent =new Intent(SignUpActivity.this, WebSocketService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
    }
    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            Gson gson = new Gson();
            Map<String,String> messageMap=gson.fromJson(message,Map.class);
            if(messageMap.get("content").equals("Registration success"))
            {
               finish();
            }
            Toast.makeText(SignUpActivity.this,messageMap.get("content"),Toast.LENGTH_LONG).show();
        }
    }

    private void doRegisterReceiver() {
        SignUpActivity.ChatMessageReceiver chatMessageReceiver = new SignUpActivity.ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("signUp");
        registerReceiver(chatMessageReceiver, filter);
    }
}

package com.example.serviceTest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
//***** Branch.Ziming.Mao *****//
//Do Not Edit It//
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private WebSocketServiceConnection connection= WebSocketServiceConnection.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle b){
        // init keys in EncryptDecrypt
        try {
            EncryptDecrypt.initKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        Button signIn=(Button) findViewById(R.id.loginbutton);
        Button signUp=(Button) findViewById(R.id.signupbutton);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        bindService();
        doRegisterReceiver();
}

// clear edit text
    protected void onResume() {
        super.onResume();
        ((EditText)findViewById(R.id.singinusername)).setText("");
        ((EditText)findViewById(R.id.signinpassword)).setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        //ActionListener
        switch (view.getId()){

            case R.id.loginbutton:
                //Action for login in button
                JsonObject jsonObject = new JsonObject();
                String username=((EditText)findViewById(R.id.singinusername)).getText().toString();
                String password=((EditText)findViewById(R.id.signinpassword)).getText().toString();
                String passwordSha1="";
                passwordSha1= Utils.getSha1(password);
                JsonObject loginInfo=new JsonObject();
                loginInfo.addProperty("type","login");
                loginInfo.addProperty("user",username);
                loginInfo.addProperty("pas",passwordSha1);

                try {
                    Log.d("tag","Plain Login info :"+EncryptDecrypt.aesDecrypt(EncryptDecrypt.aesEncry(loginInfo.toString())));

                    Log.d("tag","AES Encrypt Login info :"+EncryptDecrypt.aesEncry(loginInfo.toString()));

                    connection.sendMessage(EncryptDecrypt.aesEncry(loginInfo.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            //测试用，无账号密码跳转主界面
            //Intent goMainMenu = new Intent(MainActivity.this, MainMenu.class);
            //startActivity(goMainMenu);

        }
    }

    private void doRegisterReceiver() {
        ChatMessageReceiver chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("login");
        registerReceiver(chatMessageReceiver, filter);
    }
}


package com.example.serviceTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {
    private List<Bean> friendListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //按下用户列表图标，获取用户列表，以下用于模拟
        for (int i = 0; i < 20; i++) {
            Bean bean = new Bean();
            bean.setFriendUserName("Friend: " + i);
            friendListData.add(bean);

        }
        //按下用户列表图标，获取用户列表，以下用于模拟


    }

    public void ViewFriend(View view) {
        ListView ListViewFriendList = findViewById(R.id.friendListView);
        ListViewFriendList.setAdapter(new MyAdapter(friendListData, this));
        Log.e("test", "Clicked icon of friend");

        //定义按下好友的事件
        ListViewFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("Test", "Click" + i);
            }
        });
    }

    public void ViewMessage(View view) {
        //定义按下聊天的事件
        Log.e("Test", "按下聊天");


    }

    public void addNewFriend(View view) {
        Intent goAddNewFriend = new Intent(MainMenu.this, AddNewFriend.class);
        startActivity(goAddNewFriend);
    }
}
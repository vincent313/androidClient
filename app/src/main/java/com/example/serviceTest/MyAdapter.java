package com.example.serviceTest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {

    private List<Bean> Data;
    private Context context;

    public MyAdapter(List<Bean> data, Context context) {
        Data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return Data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.friend_item, viewGroup, false);
        }
        TextView textView = view.findViewById(R.id.friend_item);
        textView.setText(Data.get(i).getFriendUserName());

        return view;
    }
}

package com.example.testtt;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class geek extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] maintitle;
    private final int d=0;
    private final Integer[] imgid;

    public geek(Activity context, String[] maintitle, Integer[] imgid) {
        super(context, R.layout.activity_geek, maintitle);
        this.context=context;
        this.maintitle=maintitle;

        this.imgid=imgid;

    }
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_geek, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);


        titleText.setText(maintitle[position]);
        imageView.setImageResource(imgid[d]);

        return rowView;

    };
}
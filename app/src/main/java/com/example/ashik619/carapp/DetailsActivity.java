package com.example.ashik619.carapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    String make = null;
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        tv1 = (TextView)findViewById(R.id.textView1);
        Intent intent = getIntent();
        make = intent.getStringExtra("make");
        tv1.setText(make);

    }
}

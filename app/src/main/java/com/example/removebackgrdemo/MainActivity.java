package com.example.removebackgrdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.example.removebackgrdemo.rmtensorflow.ImageSegmentationModelExecutor;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button remove;
    Button ghep;
    Intent intentremove,intentghep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        remove = (Button) findViewById(R.id.btn_Remove);
        ghep = (Button) findViewById(R.id.btn_ghep);
        remove.setOnClickListener(this);
        ghep.setOnClickListener(this);
        intentremove = new Intent(this, ActivityRemove.class);
        intentghep = new Intent(this, ActivityGhep.class);
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.btn_Remove:
            startActivity(intentremove);
            break;
        case R.id.btn_ghep:
            startActivity(intentghep);
            break;
    }
    }
}
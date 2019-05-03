package com.chuson.fantazaka;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.isseiaoki.simplecropview.CropImageView;

import java.net.URI;


public class CropActivity extends AppCompatActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        CropImageView cropImageView = findViewById(R.id.cropImageView);

        Intent intent = getIntent();
        URI uri = (URI) intent.getSerializableExtra("URI");

        

    }
}

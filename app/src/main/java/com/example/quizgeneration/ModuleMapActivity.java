package com.example.quizgeneration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ModuleMapActivity extends AppCompatActivity {

    private Button mapBtn;
    private Button moduleBtn;
    private Button backBtn;
    private Button introBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_map);

        mapBtn = findViewById(R.id.mapBtn);
        moduleBtn = findViewById(R.id.moduleBtn);
        introBtn = findViewById(R.id.intro);
        backBtn = findViewById(R.id.backBtn);

        introBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModuleMapActivity.this, SanPabloIntroActivity.class);
                startActivity(intent);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModuleMapActivity.this, SanPabloActivity.class);
                startActivity(intent);
            }
        });

        moduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModuleMapActivity.this, ViewModuleActivity.class);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModuleMapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
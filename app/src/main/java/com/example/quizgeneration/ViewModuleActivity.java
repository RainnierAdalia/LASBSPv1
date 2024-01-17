package com.example.quizgeneration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ViewModuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_module);

        Button chapter1Button = findViewById(R.id.chapter1Button);
        Button chapter2Button = findViewById(R.id.chapter2Button);
        Button chapter3Button = findViewById(R.id.chapter3Button);
        Button chapter4Button = findViewById(R.id.chapter4Button);
        Button chapter5Button = findViewById(R.id.chapter5Button);

        chapter1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the module list for Chapter 1
                openModuleListForChapter(1);
            }
        });

        chapter2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the module list for Chapter 2
                openModuleListForChapter(2);
            }
        });
        chapter3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the module list for Chapter 3
                openModuleListForChapter(3);
            }
        });
        chapter4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the module list for Chapter 4
                openModuleListForChapter(4);
            }
        });
        chapter5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the module list for Chapter 3
                openModuleListForChapter(5);
            }
        });
    }


    private void openModuleListForChapter(int chapter) {
        Intent intent = new Intent(this, ModuleListActivity.class);
        intent.putExtra("selectedChapter", chapter);
        startActivity(intent);
    }
}

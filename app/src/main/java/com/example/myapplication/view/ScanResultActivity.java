package com.example.myapplication.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.model.TrafficSign;
import com.example.myapplication.controller.ScanResultController;

import java.util.ArrayList;

public class ScanResultActivity extends AppCompatActivity {
    private ScanResultController controller;
    private ArrayList<String> classNames;
    private int currentIndex = 0;
    private String imagePath;
    private LinearLayout navBar;
    private LinearLayout nextSign;
    private LinearLayout backSign;
    private LinearLayout turnBack;
    private ImageView resultImageView;
    private ImageView imageSign;
    private TextView codeSign;
    private TextView description;
    private TextView nameSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        controller = new ScanResultController(this);
        classNames = getIntent().getStringArrayListExtra("class_names");
        imagePath = getIntent().getStringExtra("image_path");

        resultImageView = findViewById(R.id.resultImageView);
        codeSign = findViewById(R.id.signCodeTextView);
        imageSign = findViewById(R.id.imageContent);
        nameSign = findViewById(R.id.signNameTextView);
        description = findViewById(R.id.signDescriptionTextView);

        navBar = findViewById(R.id.nav_bar);
        nextSign = findViewById(R.id.nextSign);
        backSign = findViewById(R.id.backSign);

        turnBack = findViewById(R.id.turnBack);

        turnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ScanResultActivity.this, ScanSignActivity.class);
            startActivity(intent);
            finish();
        });

        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            resultImageView.setImageBitmap(bitmap);
        }

        if (classNames != null && classNames.size() > 1) {
            navBar.setVisibility(View.VISIBLE);
            nextSign.setVisibility(View.VISIBLE);
            backSign.setVisibility(View.VISIBLE);
        }

        if (classNames != null && !classNames.isEmpty()) {
            String idCur = classNames.get(currentIndex);
            displaySignInfo(idCur);
        }

        nextSign.setOnClickListener(v -> {
            if (classNames != null && currentIndex < classNames.size() - 1) {
                currentIndex++;
                displaySignInfo(classNames.get(currentIndex));
            }
        });

        backSign.setOnClickListener(v -> {
            if (classNames != null && currentIndex > 0) {
                currentIndex--;
                displaySignInfo(classNames.get(currentIndex));
            }
        });
    }
    private void displaySignInfo(String id) {
        TrafficSign sign = controller.getTrafficSignById(id);
        if (sign != null) {
            codeSign.setText(sign.getId());
            nameSign.setText(sign.getName());
            description.setText(sign.getDescription());

            imageSign.setImageResource(getResources().getIdentifier(
                    sign.getImage(), "drawable", getPackageName()));
        }
    }
}
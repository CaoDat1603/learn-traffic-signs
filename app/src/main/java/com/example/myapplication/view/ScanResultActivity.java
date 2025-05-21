package com.example.myapplication.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

import kotlin.io.LineReader;

public class ScanResultActivity extends AppCompatActivity {

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

        ImageView resultImageView = findViewById(R.id.resultImageView);
        TextView codeSign = findViewById(R.id.signCodeTextView);
        TextView nameSign = findViewById(R.id.signNameTextView);
        TextView description = findViewById(R.id.signDescriptionTextView);

        LinearLayout turnBack = findViewById(R.id.turnBack);

        turnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ScanResultActivity.this, ScanSignActivity.class);
            startActivity(intent);
            finish();
        });

        ArrayList<String> classNames = getIntent().getStringArrayListExtra("class_names");

        // Nhận path ảnh từ intent
        String imagePath = getIntent().getStringExtra("image_path");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            resultImageView.setImageBitmap(bitmap);
        }

        if (classNames != null && !classNames.isEmpty()) {
            String title = android.text.TextUtils.join(", ", classNames);
            codeSign.setText(title);
        }
    }
}
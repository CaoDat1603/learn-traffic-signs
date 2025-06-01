package com.example.myapplication.view;

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
import com.example.myapplication.data.model.TrafficSign;

public class DetailSignActivity extends AppCompatActivity {
    private TrafficSign sign;
    private LinearLayout turnBack;
    private ImageView imageSign;
    private TextView codeTitle;
    private TextView codeSign;
    private TextView nameTitle;
    private TextView nameSign;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_sign);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageSign = findViewById(R.id.imageContent);
        codeTitle = findViewById(R.id.codeTitle);
        codeSign = findViewById(R.id.signCodeTextView);
        nameTitle = findViewById(R.id.nameTile);
        nameSign = findViewById(R.id.signNameTextView);
        description = findViewById(R.id.signDescriptionTextView);

        turnBack = findViewById(R.id.turnBack);

        turnBack.setOnClickListener(v -> finish());

        sign = (TrafficSign) getIntent().getSerializableExtra("sign");
        imageSign.setImageResource(getResources().getIdentifier(sign.getImage(), "drawable", getPackageName()));
        codeSign.setText(sign.getId());
        codeTitle.setText(sign.getId());
        nameSign.setText(sign.getName());
        nameTitle.setText(sign.getName());
        description.setText(sign.getDescription());
    }
}
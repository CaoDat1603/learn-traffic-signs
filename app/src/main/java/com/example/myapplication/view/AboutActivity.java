package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AboutActivity extends AppCompatActivity {
    private FloatingActionButton scan;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scan = findViewById(R.id.scan);
        bottomNavigationView = findViewById(R.id.nav_view);

        MenuItem scanItem = bottomNavigationView.getMenu().findItem(R.id.navigation_scan);
        if (scanItem != null) {
            scanItem.setEnabled(false);
        }

        bottomNavigationView.setSelectedItemId(R.id.about);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_scan) {
                return false; // Không chọn item này
            }

            if (item.getItemId() == R.id.navigation_home) {
                finish();
                return true;
            } else if (item.getItemId() == R.id.about) {
                return true;
            }

            return false; // Trường hợp không khớp ID nào
        });

        scan.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, ScanSignActivity.class);
            startActivity(intent);
        });
    }
}
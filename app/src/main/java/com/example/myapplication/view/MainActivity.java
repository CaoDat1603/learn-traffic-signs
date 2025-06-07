package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.controller.MainController;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private MainController controller;
    private FloatingActionButton scan;
    private LinearLayout random;
    private LinearLayout prohibition;
    private LinearLayout danger;
    private LinearLayout command;
    private LinearLayout direction;
    private LinearLayout addition;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        controller = new MainController(this);
        controller.importFromAssets();

        scan = findViewById(R.id.scan);
        random = findViewById(R.id.random);
        prohibition = findViewById(R.id.prohibition);
        danger = findViewById(R.id.danger);
        command = findViewById(R.id.command);
        direction = findViewById(R.id.direction);
        addition = findViewById(R.id.addition);

        bottomNavigationView = findViewById(R.id.nav_view);

        MenuItem scanItem = bottomNavigationView.getMenu().findItem(R.id.navigation_scan);
        if (scanItem != null) {
            scanItem.setEnabled(false);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_scan) {
                return false; // Không chọn item này
            }

            if (item.getItemId() == R.id.navigation_home) {
                return true;
            } else if (item.getItemId() == R.id.about) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }

            return false; // Trường hợp không khớp ID nào
        });


        scan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanSignActivity.class);
            startActivity(intent);
        });

        random.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("typeSign", "all");
            startActivity(intent);
        });

        prohibition.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("typeSign", "prohibition");
            startActivity(intent);
        });

        danger.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("typeSign", "danger");
            startActivity(intent);
        });

        command.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("typeSign", "command");
            startActivity(intent);
        });

        direction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("typeSign", "direction");
            startActivity(intent);
        });

        addition.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("typeSign", "addition");
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home); // chọn lại mục "Trang chủ"
    }
}
package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.controller.HomeController;
import com.example.myapplication.data.model.TrafficSign;
import com.example.myapplication.view.adapter.SignAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private HomeController controller;
    private String type;
    private List<TrafficSign> signList;
    private FloatingActionButton scan;
    private RecyclerView recyclerView;
    private LinearLayout turnback;
    private LinearLayout learn;
    private LinearLayout exam;
    private BottomNavigationView bottomNavigationView;
    private MenuItem scanItem;
    private MenuItem homeItem;
    private TextView tile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        controller = new HomeController(this);
        signList = Collections.emptyList();


        scan = findViewById(R.id.scan);
        turnback = findViewById(R.id.turnBack);
        learn = findViewById(R.id.learn);
        exam = findViewById(R.id.exam);

        recyclerView = findViewById(R.id.signAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigationView = findViewById(R.id.nav_view);
        tile = findViewById(R.id.title_home);

        scanItem = bottomNavigationView.getMenu().findItem(R.id.navigation_scan);
        if (scanItem != null) {
            scanItem.setEnabled(false);
        }

        // Lấy menu item "home"
        homeItem = bottomNavigationView.getMenu().findItem(R.id.navigation_home);

        type = getIntent().getStringExtra("typeSign");

        switch (type) {
            case "all":
                tile.setText("BIỂN BÁO NGẪU NHIÊN");
                homeItem.setTitle("Ngẫu nhiên");
                homeItem.setIcon(R.drawable.random);
                learn.setVisibility(View.GONE);
                signList = controller.getTrafficSigns("all");
                break;
            case "prohibition":
                tile.setText("BIỂN BÁO CẤM");
                homeItem.setTitle("Cấm");
                homeItem.setIcon(R.drawable.prohibition);
                signList = controller.getTrafficSigns("Biển báo cấm");
                break;
            case "danger":
                tile.setText("BIỂN BÁO NGUY HIỂM");
                homeItem.setTitle("Nguy hiểm");
                homeItem.setIcon(R.drawable.danger);
                signList = controller.getTrafficSigns("Biển báo nguy hiểm");
                break;
            case "command":
                tile.setText("BIỂN BÁO HIỆU LỆNH");
                homeItem.setTitle("Hiệu lệnh");
                homeItem.setIcon(R.drawable.command);
                signList = controller.getTrafficSigns("Biển hiệu lệnh");
                break;
            case "direction":
                tile.setText("BIỂN BÁO CHỈ DẪN");
                homeItem.setTitle("Chỉ dẫn");
                homeItem.setIcon(R.drawable.direction);
                signList = controller.getTrafficSigns("Biển chỉ dẫn");
                break;
            case "addition":
                tile.setText("BIỂN BÁO PHỤ");
                homeItem.setTitle("Phụ");
                homeItem.setIcon(R.drawable.addition);
                signList = controller.getTrafficSigns("Biển báo phụ");
                break;
            default:
                tile.setText("TRANG CHÍNH");
                homeItem.setTitle("Ngẫu nhiên");
                homeItem.setIcon(R.drawable.ic_home);
                break;
        }

        SignAdapter adapter = new SignAdapter(this, signList, new SignAdapter.OnSignClickListener() {
            @Override
            public void onSignClick(TrafficSign sign) {
                Intent intent = new Intent(HomeActivity.this, DetailSignActivity.class);
                intent.putExtra("sign", (Serializable) sign);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        
        scan.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ScanSignActivity.class);
            startActivity(intent);
        });

        turnback.setOnClickListener(v -> finish());

        exam.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ExamActivity.class);
            intent.putExtra("signList", (Serializable) signList);
            startActivity(intent);
        });

        learn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LearnActivity.class);
            intent.putExtra("typeSign", type);
            startActivity(intent);
        });
    }

    @Override
    public void finish() {
        super.finish();
    }
}
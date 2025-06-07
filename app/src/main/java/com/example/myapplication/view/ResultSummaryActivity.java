package com.example.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class ResultSummaryActivity extends AppCompatActivity {
    private int percent;
    private String type;
    private LinearLayout turnBack;
    private ImageView yesAll;
    private PieChart pieChart;
    private TextView percentTextView;
    private TextView textTotal;
    private TextView sizeYes;
    private TextView sizeNo;
    private TextView sizeOther;
    private Button testExam;
    private Button buttonReset;
    private Button learnAgain;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pieChart = findViewById(R.id.pieChart);
        percentTextView = findViewById(R.id.centerText);
        yesAll = findViewById(R.id.yesAll);
        textTotal = findViewById(R.id.textTotal);
        sizeYes = findViewById(R.id.indexYes);
        sizeNo = findViewById(R.id.indexNo);
        sizeOther = findViewById(R.id.indexOther);

        testExam = findViewById(R.id.testExam);
        buttonReset = findViewById(R.id.buttonReset);
        learnAgain = findViewById(R.id.learnAgain);
        turnBack = findViewById(R.id.turnBack);

        type = getIntent().getStringExtra("typeSign");
        float known = getIntent().getIntExtra("known", 0);
        float learning = getIntent().getIntExtra("learning", 0);
        float remaining = getIntent().getIntExtra("remaining", 0);

        sizeYes.setText(String.valueOf((int) known));
        sizeNo.setText(String.valueOf((int) learning));
        sizeOther.setText(String.valueOf((int) remaining));

        percent = drawChart(known, learning, remaining); //Vẽ hình và xuất % học tập

        if (percent == 100) {
            yesAll.setVisibility(View.VISIBLE);
            percentTextView.setVisibility(View.GONE);
            learnAgain.setVisibility(View.GONE);
            textTotal.setText("Thật tuyệt vời!\nBạn đã hoàn thành khóa học rồi.");
        }  else {
            if (percent <= 60) {
                textTotal.setText("Bạn học rất tốt!\nHãy tiếp tục học tập tiếp nhé.");
                if (percent == 0) buttonReset.setVisibility(View.GONE);
            } else {
                textTotal.setText("Cố lên!\nBạn gần hoàn thành khóa học rồi.");
            }
            percentTextView.setVisibility(View.VISIBLE);
            yesAll.setVisibility(View.GONE);

            percentTextView.setText(percent + "%");
        }

        turnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        testExam.setOnClickListener(v -> {
            Intent intent = new Intent(ResultSummaryActivity.this, ExamActivity.class);
            intent.putExtra("typeSign", type);
            startActivity(intent);
        });

        learnAgain.setOnClickListener(v -> {
            onBackPressed();
        });

        buttonReset.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("action", "reset");
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

     private int drawChart(float known, float learning, float remaining) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if (known > 0) {
            entries.add(new PieEntry(known, "Đã biết"));
            colors.add(Color.parseColor("#00BC09")); // Xanh lá
        }

        if (learning > 0) {
            entries.add(new PieEntry(learning, "Đang học"));
            colors.add(Color.parseColor("#BC0000")); // Đỏ
        }

        if (remaining > 0) {
            entries.add(new PieEntry(remaining, "Còn lại"));
            colors.add(Color.parseColor("#2e3856")); // Xanh đậm
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setDrawEntryLabels(false);

        int total = (int) (known + learning + remaining);
        int centerPercent = total > 0 ? (int) (known * 100 / total) : 0;
        pieChart.setCenterText("Nội dung");
        pieChart.setCenterTextSize(24f);
        pieChart.setCenterTextColor(Color.WHITE);

        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();

        return centerPercent;
    }


    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("action", "continue");
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
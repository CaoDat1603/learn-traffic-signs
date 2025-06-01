package com.example.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.controller.LearnController;
import com.example.myapplication.data.model.TrafficSign;

import java.util.List;

public class LearnActivity extends AppCompatActivity {
    private LearnController controller;
    private List<TrafficSign> signList;
    private List<TrafficSign> signListCur;
    private String type;
    private int currentIndex = 0;
    private int sizeNo = 0;
    private int sizeYes = 0;
    private int sizeAllSign = 0;
    private boolean isImageVisible = true;
    private LinearLayout turnBack;
    private LinearLayout allContent;
    private LinearLayout imageContent;
    private LinearLayout scrollView;
    private LinearLayout space;
    private LinearLayout bar;
    private LinearLayout buttonNar1;
    private LinearLayout buttonNar2;
    private ImageView imageView;
    private ImageView backTap;
    private TextView indexCur;
    private TextView sizeAll;
    private TextView indexNo;
    private TextView indexYes;
    private TextView signCodeTextView;
    private TextView signNameTextView;
    private TextView signDescriptionTextView;
    private Button buttonNo;
    private Button buttonYes;
    private Button buttonNext;
    private Button buttonBack;
    private SwitchCompat switchSetting;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        turnBack = findViewById(R.id.turnBack);
        imageContent = findViewById(R.id.imageContent);
        scrollView = findViewById(R.id.scrollView);
        imageView = findViewById(R.id.imageView);
        indexCur = findViewById(R.id.indexCur);
        sizeAll = findViewById(R.id.sizeAll);
        indexNo = findViewById(R.id.indexNo);
        indexYes = findViewById(R.id.indexYes);
        allContent = findViewById(R.id.allContent);
        backTap = findViewById(R.id.backTap);

        signCodeTextView = findViewById(R.id.signCodeTextView);
        signNameTextView = findViewById(R.id.signNameTextView);
        signDescriptionTextView = findViewById(R.id.signDescriptionTextView);
        buttonNo = findViewById(R.id.buttonNo);
        buttonYes = findViewById(R.id.buttonYes);
        buttonNext = findViewById(R.id.buttonNext);
        buttonBack = findViewById(R.id.buttonBack);

        bar = findViewById(R.id.bar2);
        space = findViewById(R.id.space1);
        buttonNar1 = findViewById(R.id.buttonNav1);
        buttonNar2 = findViewById(R.id.buttonNav2);
        switchSetting = findViewById(R.id.switchSetting);
        // Lấy trạng thái đã lưu (nếu có)
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isChecked = prefs.getBoolean("switch_state", true); // true là mặc định on
        if (!isChecked) {
            bar.setVisibility(View.GONE);
            space.setVisibility(View.VISIBLE);
            buttonNar1.setVisibility(View.GONE);
            buttonNar2.setVisibility(View.VISIBLE);
        } else {
            bar.setVisibility(View.VISIBLE);
            space.setVisibility(View.GONE);
            buttonNar1.setVisibility(View.VISIBLE);
            buttonNar2.setVisibility(View.GONE);
        }
        switchSetting.setChecked(isChecked);

        // Lắng nghe thay đổi trạng thái và lưu lại
        switchSetting.setOnCheckedChangeListener((buttonView, isCheckedNew) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("switch_state", isCheckedNew);
            editor.apply();

            if (!isCheckedNew) {
                bar.setVisibility(View.GONE);
                space.setVisibility(View.VISIBLE);
                buttonNar1.setVisibility(View.GONE);
                buttonNar2.setVisibility(View.VISIBLE);
            } else {
                bar.setVisibility(View.VISIBLE);
                space.setVisibility(View.GONE);
                buttonNar1.setVisibility(View.VISIBLE);
                buttonNar2.setVisibility(View.GONE);
            }
        });

        reloadData(false);

        turnBack.setOnClickListener(v -> finish());

        backTap.setOnClickListener(v -> {
            if (signListCur != null && currentIndex > 0) {
                boolean co;
                if (controller.isHaveStudying) {
                    co = controller.backSign(currentIndex, true);
                    sizeNo++;
                } else {
                    co = controller.backSign(currentIndex, false);
                }
                if (co) {
                    sizeNo--;
                } else {
                    sizeYes--;
                }
                currentIndex--;
                displaySignInfo(signListCur.get(currentIndex));

                imageContent.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                isImageVisible = true;
            }
        });

        allContent.setOnClickListener(v -> {
            if (isImageVisible) {
                imageContent.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            } else {
                imageContent.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            }
            isImageVisible = !isImageVisible;
        });

        buttonNo.setOnClickListener(v -> {
           if (signListCur != null && currentIndex < sizeAllSign - 1) {
               controller.noSign(currentIndex);
               currentIndex++;
               sizeNo++;
               if (controller.isHaveStudying) sizeNo--;
               displaySignInfo(signListCur.get(currentIndex));

               imageContent.setVisibility(View.VISIBLE);
               scrollView.setVisibility(View.GONE);
               isImageVisible = true;

           } else if (currentIndex == sizeAllSign - 1) {
               controller.endSign(currentIndex, false);
               sizeNo++;
               currentIndex = 0;
               navigationSummary();
           }

        });

        buttonYes.setOnClickListener(v -> {
            if (signListCur != null && currentIndex < sizeAllSign - 1) {
                controller.nextSign(currentIndex);
                currentIndex++;
                sizeYes++;
                if (controller.isHaveStudying) sizeNo--;

                displaySignInfo(signListCur.get(currentIndex));

                imageContent.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                isImageVisible = true;
            } else if (currentIndex == sizeAllSign - 1) {
                controller.endSign(currentIndex, true);
                sizeYes++;
                currentIndex = 0;
                navigationSummary();
            }


        });

        buttonNext.setOnClickListener(v -> {
            if (signListCur != null && currentIndex < sizeAllSign - 1) {
                controller.nextSign(currentIndex);
                currentIndex++;
                sizeYes++;
                if (controller.isHaveStudying) sizeNo--;
                displaySignInfo(signListCur.get(currentIndex));

                imageContent.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                isImageVisible = true;
            }   else if (currentIndex == sizeAllSign - 1) {
                controller.endSign(currentIndex, true);
                sizeYes++;
                currentIndex = 0;
                navigationSummary();
            }
        });

        buttonBack.setOnClickListener(v -> {
            if (signListCur != null && currentIndex > 0) {
                boolean co;
                if (controller.isHaveStudying) {
                    co = controller.backSign(currentIndex, true);
                    sizeNo++;
                } else {
                    co = controller.backSign(currentIndex, false);
                }
                if (co) {
                    sizeNo--;
                } else {
                    sizeYes--;
                }
                currentIndex--;
                displaySignInfo(signListCur.get(currentIndex));

                imageContent.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                isImageVisible = true;
            }
        });
    }

    private void reloadData(boolean isReset) {
        controller = new LearnController(this);
        type = getIntent().getStringExtra("typeSign");

        switch (type) {
            case "all":
                signList = controller.getTrafficSigns("all");
                break;
            case "prohibition":
                signList = controller.getTrafficSigns("Biển báo cấm");
                break;
            case "danger":
                signList = controller.getTrafficSigns("Biển báo nguy hiểm");
                break;
            case "command":
                signList = controller.getTrafficSigns("Biển hiệu lệnh");
                break;
            case "direction":
                signList = controller.getTrafficSigns("Biển chỉ dẫn");
                break;
            case "addition":
                signList = controller.getTrafficSigns("Biển báo phụ");
                break;
            default:
                signList = controller.getTrafficSigns("all");
                break;
        }

        controller.setSignList(signList);
        signListCur = controller.getSignListCur(isReset);
        currentIndex = controller.curIndexInProcess();
        sizeYes = controller.sizeSignListIsLeaned();
        sizeNo = controller.sizeSignListIsStudying();
        sizeAllSign = signListCur.size();

        sizeAll.setText(sizeAllSign + "");

        displaySignInfo(signListCur.get(currentIndex));
    }

    private void displaySignInfo(TrafficSign sign) {
        indexNo.setText(sizeNo + "");
        indexYes.setText(sizeYes + "");
        indexCur.setText((currentIndex + 1) + "");

        if (sign != null) {
            signCodeTextView.setText(sign.getId());
            signNameTextView.setText(sign.getName());
            signDescriptionTextView.setText(sign.getDescription());

            imageView.setImageResource(getResources().getIdentifier(
                    sign.getImage(), "drawable", getPackageName()));
        }

    }

    private void navigationSummary() {
        Intent intent = new Intent(LearnActivity.this, ResultSummaryActivity.class);
        intent.putExtra("known", sizeYes);
        intent.putExtra("learning", sizeNo);
        intent.putExtra("remaining", sizeAllSign - sizeYes - sizeNo);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String action = data.getStringExtra("action");
            if ("continue".equals(action)) {
                reloadData(false);
            } else if ("reset".equals(action)) {
                reloadData(true);
            }
        }
    }
}
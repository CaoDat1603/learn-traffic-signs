package com.example.myapplication.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    // Khai báo controller xử lý logic học
    private LearnController controller;
    // Danh sách tất cả và danh sách đang học
    private List<TrafficSign> signList;// Toàn bộ biển báo (lọc theo loại)
    private List<TrafficSign> signListCur;// Danh sách biển báo hiện tại đang học (có thể bị cắt bớt khi tiếp tục học)
    // Loại biển báo (truyền từ HomeActivity)
    private String type;
    // Biến chỉ số và trạng thái học
    private int currentIndex = 0; // Hiện tại
    private int curIndexNone = 0; // Tạm
    private int curIndexNoneTow = 0; // Tạm đặt biệt (trường hợp có Đang học tiếp tục)
    private int curIndexStart = 0; // Cố định giá trị indexInPercess lần đầu tiên khởi tạo
    private int sizeNo = 0;
    private int sizeNoCn = 0; // Tạm đặt biệt
    private int sizeYes = 0;
    private int sizeYesCn = 0;
    private int sizeAllSign = 0;
    // Cờ hiệu giao diện
    private boolean isImageVisible = true; // Hiện ảnh hay không
    private boolean isCheckSetting = true; // Có phải là đang ở chế độ lưu tiến trình học không
    private boolean isContinueLearn; // Có phải trường hợp tiếp tục học lại những cái đang học không
    // Giao diện (LinearLayout, TextView, Button...)
    private LinearLayout turnBack;
    private LinearLayout allContent;
    private LinearLayout imageContent;
    private LinearLayout scrollView;
    private LinearLayout space;
    private LinearLayout bar;
    private LinearLayout buttonNar1;
    private LinearLayout buttonNar2;
    private ImageView imageView;
    private LinearLayout backTap;
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
    private Button btnExam;
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
        btnExam = findViewById(R.id.testExam);

        bar = findViewById(R.id.bar2);
        space = findViewById(R.id.space1);
        buttonNar1 = findViewById(R.id.buttonNav1);
        buttonNar2 = findViewById(R.id.buttonNav2);

        // Nút chuyển sang màn hình làm bài kiểm tra
        btnExam.setOnClickListener(v -> {
            Intent intent = new Intent(LearnActivity.this, ExamActivity.class);
            intent.putExtra("typeSign", type);
            startActivity(intent);
        });

        switchSetting = findViewById(R.id.switchSetting);

        reloadData(false); //Khởi tạo dữ liệu ban đầu

        // Lấy trạng thái đã lưu (nếu có)
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isChecked = prefs.getBoolean("switch_state", true); // true là mặc định on
        if (!isChecked) {
            bar.setVisibility(View.GONE);
            space.setVisibility(View.VISIBLE);
            buttonNar1.setVisibility(View.GONE);
            buttonNar2.setVisibility(View.VISIBLE);
            backTap.setVisibility(View.GONE);

            isCheckSetting = false;
            curIndexNone = currentIndex;
            curIndexNoneTow = 1;
            displaySignInfo(signListCur.get(curIndexNone), isContinueLearn, curIndexNone);
        } else {
            bar.setVisibility(View.VISIBLE);
            space.setVisibility(View.GONE);
            buttonNar1.setVisibility(View.VISIBLE);
            buttonNar2.setVisibility(View.GONE);
            isCheckSetting = true;
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
                backTap.setVisibility(View.GONE);

                isCheckSetting = false;
                curIndexNone = currentIndex;
                curIndexNoneTow = sizeYesCn + sizeNoCn + 1;
            } else {
                bar.setVisibility(View.VISIBLE);
                space.setVisibility(View.GONE);
                buttonNar1.setVisibility(View.VISIBLE);
                buttonNar2.setVisibility(View.GONE);
                backTap.setVisibility(View.VISIBLE);

                isCheckSetting = true;
                displaySignInfo(signListCur.get(currentIndex), isContinueLearn, currentIndex);
                imageContent.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                isImageVisible = true;
            }
        });

        turnBack.setOnClickListener(v -> finish());

        backTap.setOnClickListener(v -> {
            if (signListCur != null && currentIndex > 0) {
                if (controller.isHaveStudying && currentIndex > curIndexStart) {
                    boolean co;

                    co = controller.backSign(currentIndex, true);
                    sizeNo++;

                    if (co) {
                        sizeNo--;
                        if (controller.isHaveStudying) sizeNoCn--;
                    } else {
                        sizeYes--;
                        if (controller.isHaveStudying) sizeYesCn--;
                    }

                    currentIndex--;
                    displaySignInfo(signListCur.get(currentIndex), isContinueLearn, currentIndex);

                    Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                    allContent.setVisibility(View.VISIBLE);
                    allContent.startAnimation(slideIn);

                    imageContent.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    isImageVisible = true;
                } else if (!controller.isHaveStudying) {
                    boolean co;
                    co = controller.backSign(currentIndex, false);

                    if (co) {
                        sizeNo--;
                        if (controller.isHaveStudying) sizeNoCn--;
                    } else {
                        sizeYes--;
                        if (controller.isHaveStudying) sizeYesCn--;
                    }

                    currentIndex--;
                    displaySignInfo(signListCur.get(currentIndex), isContinueLearn, currentIndex);

                    Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                    allContent.setVisibility(View.VISIBLE);
                    allContent.startAnimation(slideIn);

                    imageContent.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    isImageVisible = true;
                }
            }
        });


        AnimatorSet flip = new AnimatorSet();

        Animator outAnim = AnimatorInflater.loadAnimator(this, R.animator.flip_out_left);
        Animator inAnim = AnimatorInflater.loadAnimator(this, R.animator.flip_in_left);

        // Gán target là allContent
        outAnim.setTarget(allContent);
        inAnim.setTarget(allContent);

        // Khi animation đầu kết thúc → bắt đầu animation thứ 2
        outAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inAnim.start();
            }
        });

        float scale = getResources().getDisplayMetrics().density;
        allContent.setCameraDistance(8000 * scale);

        allContent.setOnClickListener(v -> {
            outAnim.start();

            allContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isImageVisible) {
                        imageContent.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    } else {
                        imageContent.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    }
                    isImageVisible = !isImageVisible;
                }
            }, 150);
        });


        buttonNo.setOnClickListener(v -> {
           if (signListCur != null && currentIndex < sizeAllSign - 1) {
               controller.noSign(currentIndex);
               currentIndex++;
               sizeNo++;
               if (controller.isHaveStudying) {
                   sizeNo--;
                   sizeNoCn++;
               }
               displaySignInfo(signListCur.get(currentIndex), isContinueLearn, currentIndex);

               Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
               allContent.setVisibility(View.VISIBLE);
               allContent.startAnimation(slideIn);

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
                if (controller.isHaveStudying) {
                    sizeNo--;
                    sizeYesCn++;
                }

                displaySignInfo(signListCur.get(currentIndex), isContinueLearn, currentIndex);

                Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
                allContent.setVisibility(View.VISIBLE);
                allContent.startAnimation(slideIn);

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
            if (signListCur != null && curIndexNone < sizeAllSign - 1) {
                curIndexNone++;

                if (controller.isHaveStudying) curIndexNoneTow++;
                displaySignInfo(signListCur.get(curIndexNone), isContinueLearn, curIndexNone);

                Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
                allContent.setVisibility(View.VISIBLE);
                allContent.startAnimation(slideIn);

                imageContent.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                isImageVisible = true;
            }   else if (curIndexNone == sizeAllSign - 1) {
                btnExam.setVisibility(View.VISIBLE);
            }
        });

        buttonBack.setOnClickListener(v -> {
            if (signListCur != null && curIndexNone > 0) {
                if (!controller.isHaveStudying) {
                    curIndexNone--;

                    displaySignInfo(signListCur.get(curIndexNone), isContinueLearn, curIndexNone);

                    Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                    allContent.setVisibility(View.VISIBLE);
                    allContent.startAnimation(slideIn);

                    imageContent.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    isImageVisible = true;
                } else if (curIndexNone > curIndexStart) {
                    curIndexNone--;
                    curIndexNoneTow--;

                    displaySignInfo(signListCur.get(curIndexNone), isContinueLearn, curIndexNone);

                    Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                    allContent.setVisibility(View.VISIBLE);
                    allContent.startAnimation(slideIn);

                    imageContent.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    isImageVisible = true;
                }

                if (curIndexNone == sizeAllSign - 2) {
                    btnExam.setVisibility(View.GONE);
                }
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
        isContinueLearn = controller.isHaveStudying;
        currentIndex = controller.curIndexInProcess();
        curIndexStart = currentIndex;
        sizeYes = controller.sizeSignListIsLeaned();
        sizeNo = controller.sizeSignListIsStudying();
        sizeNoCn = 0;
        sizeYesCn = 0;
        sizeAllSign = signListCur.size();

        Log.e("sizeYes", sizeYes + "");
        Log.e("sizeNo", sizeNo + "");
        Log.e("isContinueLearn", isContinueLearn + "");
        if (isContinueLearn) {
            sizeAll.setText((sizeNo + 1) + "");
        } else {
            sizeAll.setText(sizeAllSign + "");
        }

        displaySignInfo(signListCur.get(currentIndex), isContinueLearn, currentIndex);
    }

    private void displaySignInfo(TrafficSign sign, boolean isContinueLearn, int curIndex) {
        if (!isContinueLearn) {
            indexNo.setText(sizeNo + "");
            indexYes.setText(sizeYes + "");
            indexCur.setText((curIndex + 1) + "");
        } else {
            indexNo.setText(sizeNoCn + "");
            indexYes.setText(sizeYesCn + "");
            indexCur.setText((sizeYesCn + sizeNoCn + 1) + "");

            if (!isCheckSetting) {
                indexCur.setText(curIndexNoneTow + "");
            }
        }

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
        intent.putExtra("typeSign", type); // Truyền typeSign
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
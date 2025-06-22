// ExamActivity.java - Thực hiện bài thi biển báo giao thông
package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.controller.ExamController;
import com.example.myapplication.data.model.TrafficSign;
import com.example.myapplication.view.adapter.QuestionTabAdapter;
import com.example.myapplication.data.model.AnswerResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamActivity extends AppCompatActivity {
    private String type;
    private RecyclerView questionTabRecycler;
    private LinearLayout turnBack;
    private ImageView image;
    private RadioButton answer1, answer2, answer3, answer4;
    private RadioGroup answerGroup;
    private TextView progressText, questionTitle;
    private Button btnEnd, btnNext;

    private ExamController controller;
    private QuestionTabAdapter tabAdapter;
    private List<TrafficSign> signList;
    private List<TrafficSign> signListCur;

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private Map<Integer, AnswerResult> answerResults = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        image = findViewById(R.id.question_image);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        answerGroup = findViewById(R.id.answer_group);
        progressText = findViewById(R.id.text_question_progress);
        questionTitle = findViewById(R.id.question_title);
        btnEnd = findViewById(R.id.button_end);
        btnNext = findViewById(R.id.button_next);

        questionTabRecycler = findViewById(R.id.question_tab_recycler);
        turnBack = findViewById(R.id.turnBack);

        type = getIntent().getStringExtra("typeSign");

        turnBack.setOnClickListener(v -> finish());

        questionTabRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        reloadData(true);

        tabAdapter = new QuestionTabAdapter(signListCur.size(), position -> {
            currentQuestionIndex = position;
            showQuestion(position);
            questionTabRecycler.scrollToPosition(position);
        });
        questionTabRecycler.setAdapter(tabAdapter);

        if (!signListCur.isEmpty()) {
            currentQuestionIndex = 0;
            questionTabRecycler.scrollToPosition(currentQuestionIndex);
            showQuestion(currentQuestionIndex);
        }

        btnNext.setOnClickListener(v -> {
            if (answerGroup.getCheckedRadioButtonId() == -1) {
                showAlert("Vui lòng chọn một đáp án trước khi tiếp tục.");
                return;
            }
            checkAnswer();
            tabAdapter.markAnswered(currentQuestionIndex);

            if (currentQuestionIndex < signListCur.size() - 1) {
                currentQuestionIndex++;
                tabAdapter.updateSelectedIndex(currentQuestionIndex);
                showQuestion(currentQuestionIndex);
                questionTabRecycler.scrollToPosition(currentQuestionIndex);
                updateProgressText();
            } else {
                updateProgressText();
                navigateToResult();
            }
        });

        btnEnd.setOnClickListener(v -> {
            if (answerGroup.getCheckedRadioButtonId() == -1) {
                showAlert("Vui lòng chọn một đáp án trước khi nộp bài.");
                return;
            }
            checkAnswer();
            navigateToResult();
        });
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void reloadData(boolean isReset) {
        controller = new ExamController(this);
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
    }

    private void updateProgressText() {
        int answeredCount = 0;
        for (int i = 0; i < signListCur.size(); i++) {
            if (tabAdapter.isAnswered(i)) {
                answeredCount++;
            }
        }
        progressText.setText("Bạn đã làm được " + answeredCount + "/" + signListCur.size() + " câu hỏi");
    }

    private void showQuestion(int index) {
        TrafficSign question = signListCur.get(index);
        int imageResId = getResources().getIdentifier(question.getImage(), "drawable", getPackageName());
        image.setImageResource(imageResId);

        List<String> options;

        AnswerResult previousAnswer = answerResults.get(index);
        if (previousAnswer != null) {
            options = previousAnswer.getOptions(); // <-- lấy từ AnswerResult
        } else {
            options = getRandomOptions(question.getName()); // nếu chưa có thì random mới
        }

        answer1.setText(options.get(0));
        answer2.setText(options.get(1));
        answer3.setText(options.get(2));
        answer4.setText(options.get(3));

        questionTitle.setText("Câu hỏi " + (index + 1));
        answerGroup.clearCheck();

        // ✅ Đánh dấu lại đáp án đã chọn (nếu có)
        if (previousAnswer != null) {
            String selected = previousAnswer.getSelectedAnswer();
            if (selected != null) {
                if (selected.equals(answer1.getText().toString())) {
                    answer1.setChecked(true);
                } else if (selected.equals(answer2.getText().toString())) {
                    answer2.setChecked(true);
                } else if (selected.equals(answer3.getText().toString())) {
                    answer3.setChecked(true);
                } else if (selected.equals(answer4.getText().toString())) {
                    answer4.setChecked(true);
                }
            }
        }
    }


    private List<String> getRandomOptions(String correctAnswer) {
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);

        List<String> allNames = new ArrayList<>();
        for (TrafficSign s : signList) {
            if (!s.getName().equals(correctAnswer)) {
                allNames.add(s.getName());
            }
        }
        Collections.shuffle(allNames);
        for (int i = 0; i < 3 && i < allNames.size(); i++) {
            options.add(allNames.get(i));
        }
        Collections.shuffle(options);
        return options;
    }

    private void checkAnswer() {
        TrafficSign question = signListCur.get(currentQuestionIndex);
        String correct = question.getName();
        String selected = null;

        if (answer1.isChecked()) selected = answer1.getText().toString();
        if (answer2.isChecked()) selected = answer2.getText().toString();
        if (answer3.isChecked()) selected = answer3.getText().toString();
        if (answer4.isChecked()) selected = answer4.getText().toString();

        if (selected != null && selected.equals(correct)) {
            correctAnswers++;
        }

        List<String> options = new ArrayList<>();
        options.add(answer1.getText().toString());
        options.add(answer2.getText().toString());
        options.add(answer3.getText().toString());
        options.add(answer4.getText().toString());

        String imageName = question.getImage();

        answerResults.put(
                currentQuestionIndex,
                new AnswerResult(correct, selected, options, imageName)
        );
    }



    private void navigateToResult() {
        Intent intent = new Intent(ExamActivity.this, ResultActivity.class);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("totalQuestions", signListCur.size());
        intent.putExtra("answerResultsMap", (Serializable) answerResults); // Gửi map đã chứa đủ thông tin
        intent.putExtra("typeSign", type);
        startActivity(intent);
        finish();
    }



}
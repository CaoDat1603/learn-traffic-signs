package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.model.AnswerResult;

import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView scoreText, cheerText, answerStatus;
    private Button buttonRetry, buttonBack;
    private LinearLayout resultContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreText = findViewById(R.id.score_text);
        resultContainer = findViewById(R.id.result_container);
        cheerText = findViewById(R.id.cheer_text);
        buttonRetry = findViewById(R.id.button_retry);
        buttonBack = findViewById(R.id.button_back);

        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        Map<Integer, AnswerResult> answerResults =
                (Map<Integer, AnswerResult>) getIntent().getSerializableExtra("answerResultsMap");

        scoreText.setText("Bạn trả lời đúng " + correctAnswers + " / " + totalQuestions + " câu");
        if(correctAnswers <=20 ){
            cheerText.setText("Cố lên đừng nản, chúng ta học lại nhé");
        }else if (correctAnswers <=40){
            cheerText.setText("Còn một tí nữa thôi cố lên nào");
        }else{
            cheerText.setText("Chúc mừng bạn đã hoàn thành bài thi biển báo giao thông");
        }

        if (answerResults != null) {
            for (int i = 0; i < answerResults.size(); i++) {
                AnswerResult result = answerResults.get(i);
                if (result != null) addResultView(i, result);
            }
        }
        buttonRetry.setOnClickListener(v -> {
            // Mở lại ExamActivity, truyền lại type nếu cần
            Intent intent = new Intent(ResultActivity.this, ExamActivity.class);
            intent.putExtra("typeSign", getIntent().getStringExtra("typeSign")); // Nếu có loại
            startActivity(intent);
            finish();
        });

        buttonBack.setOnClickListener(v -> {
            // Quay lại MainActivity hoặc màn hình Home
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // optional: xoá ngăn xếp
            startActivity(intent);
            finish();
        });

    }

    private void addResultView(int index, AnswerResult result) {
        // Inflate item layout
        LinearLayout itemView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_result_question, null);

        // Set image
        ImageView imageView = itemView.findViewById(R.id.question_image);
        int imageResId = getResources().getIdentifier(result.imageName, "drawable", getPackageName());
        imageView.setImageResource(imageResId);

        // Set question title
        TextView title = itemView.findViewById(R.id.question_title);
        title.setText("Câu " + (index + 1));
        TextView answerStatus = itemView.findViewById(R.id.answer_status);

        // Set 4 answers
        TextView[] answerViews = new TextView[]{
                itemView.findViewById(R.id.answer1),
                itemView.findViewById(R.id.answer2),
                itemView.findViewById(R.id.answer3),
                itemView.findViewById(R.id.answer4),
        };

        for (int i = 0; i < result.options.size() && i < 4; i++) {
            String option = result.options.get(i);
            answerViews[i].setText(option);

            if (option.equals(result.correctAnswer)) {
                answerViews[i].setBackgroundColor(0xFFA5D6A7); // xanh lá: đúng
            }
            if (option.equals(result.selectedAnswer) && !option.equals(result.correctAnswer)) {
                answerViews[i].setBackgroundColor(0xFFFFCDD2); // đỏ: sai
            }
        }
        if (result.selectedAnswer == null || !result.selectedAnswer.equals(result.correctAnswer)) {
            answerStatus.setText("Sai");
            answerStatus.setTextColor(0xFFFF5252); // đỏ
        } else {
            answerStatus.setText("Đúng");
            answerStatus.setTextColor(0xFF4CAF50); // xanh
        }

        resultContainer.addView(itemView);
    }
}

package com.example.myapplication.data.model;

import java.io.Serializable;
import java.util.List;

public class AnswerResult implements Serializable {
    public String correctAnswer;
    public String selectedAnswer;
    public List<String> options;
    public String imageName;

    public AnswerResult(String correctAnswer, String selectedAnswer, List<String> options, String imageName) {
        this.correctAnswer = correctAnswer;
        this.selectedAnswer = selectedAnswer;
        this.options = options;
        this.imageName = imageName;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public List<String> getOptions() { return options; }
}

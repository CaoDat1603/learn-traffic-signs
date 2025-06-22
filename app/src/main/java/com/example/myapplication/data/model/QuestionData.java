package com.example.myapplication.data.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionData {
    private TrafficSign.QuestionType questionType;
    private String question;
    private String answer;
    private List<String> options;

    public QuestionData(TrafficSign.QuestionType questionType, String question, String answer, List<String> options) {
        this.questionType = questionType;
        this.question = question;
        this.answer = answer;
        this.options = options;
    }
    public String getAnswer() {
        return answer;
    }

}

package com.example.myapplication.data.model;

import java.util.List;

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


    public TrafficSign.QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(TrafficSign.QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}

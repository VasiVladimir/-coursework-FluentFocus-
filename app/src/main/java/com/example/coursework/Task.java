package com.example.coursework;

import java.util.List;

public class Task {
    private String question;
    private List<String> options; // Изменили тип на List<String>
    private int correctAnswer;

    public Task() {
        // Пустой конструктор требуется для Firebase
    }

    public Task(String question, List<String> options, int correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}

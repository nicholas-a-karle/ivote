package com.ivote;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String text;
    private List<String> answers;

    // Constructors
    public Question() {
        this.answers = new ArrayList<>();
    }

    public Question(String text, List<String> answers) {
        this.text = text;
        this.answers = answers != null ? new ArrayList<>(answers) : new ArrayList<>();
    }

    // Getter for text
    public String getText() {
        return text;
    }

    // Setter for text
    public void setText(String text) {
        this.text = text;
    }

    // Getter for answers
    public List<String> getAnswers() {
        return new ArrayList<>(answers);
    }

    // Setter for answers
    public void setAnswers(List<String> answers) {
        this.answers = answers != null ? new ArrayList<>(answers) : new ArrayList<>();
    }

    // Method to add an answer
    public void addAnswer(String answer) {
        if (answer != null && !answer.isEmpty()) {
            this.answers.add(answer);
        }
    }

    // Method to remove an answer
    public boolean removeAnswer(String answer) {
        return this.answers.remove(answer);
    }

    // Override toString for better readability
    @Override
    public String toString() {
        return "Question{" +
               "text='" + text + '\'' +
               ", answers=" + answers +
               '}';
    }
}

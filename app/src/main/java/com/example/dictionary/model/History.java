package com.example.dictionary.model;

public class History {
    private String english_word;
    private int id;

    public int getId() {
        return id;
    }

    public History() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public History(String english_word) {
        this.english_word = english_word;
    }

    public void setEnglish_word(String english_word) {
        this.english_word = english_word;
    }

    public String getEnglish_word() {
        return english_word;
    }
}
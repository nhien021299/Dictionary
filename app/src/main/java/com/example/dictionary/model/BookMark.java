package com.example.dictionary.model;

public class BookMark {
    private int id;
    private String bookMark_word;

    public BookMark() {
    }

    public BookMark(String bookMark_word) {
        this.bookMark_word = bookMark_word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBookMark_word(String bookMark_word) {
        this.bookMark_word = bookMark_word;
    }

    public String getBookMark_word() {
        return bookMark_word;
    }
}

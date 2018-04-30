package com.michaellundie.fabapp;

import java.util.ArrayList;

/*
  This is a custom object class for
 */
public class BookItem {
    private String title;
    private ArrayList<String> authors;
    private int thumbnailResourceId;

    public BookItem(String title, ArrayList<String> author, int thumbnailResourceId) {
        this.title = title;
        this.authors = author;
        this.thumbnailResourceId = thumbnailResourceId;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getAuthor() {
        return authors;
    }

    public int getThumbnailResourceId() {
        return thumbnailResourceId;
    }
}

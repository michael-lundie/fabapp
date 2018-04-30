package com.michaellundie.fabapp;

import java.util.ArrayList;

/*
  This is a custom object class for
 */
public class BookItem {
    private String title;
    private ArrayList<String> authors;
    private String thumbnailURL;

    public BookItem(String title, ArrayList<String> author, String thumbnailURL) {
        this.title = title;
        this.authors = author;
        this.thumbnailURL = thumbnailURL;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getAuthor() {
        return authors;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }
}

package com.michaellundie.fabapp;

import java.util.ArrayList;

/*
  This is a custom object class for
 */
public class BookItem {
    private String title;
    private ArrayList<String> authors;
    private String thumbnailURL;
    private int itemID;

    public BookItem(String title, ArrayList<String> author, String thumbnailURL, int itemID) {
        this.title = title;
        this.authors = author;
        this.thumbnailURL = thumbnailURL;
        this.itemID = itemID;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public int getItemID() { return itemID; }
}

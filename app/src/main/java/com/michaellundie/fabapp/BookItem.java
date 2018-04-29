package com.michaellundie.fabapp;

/*
  This is a custom object class for
 */
public class BookItem {
    private String title;
    private String author;
    private int thumbnailResourceId;

    public BookItem(String title, String author, int thumbnailResourceId) {
        this.title = title;
        this.author = author;
        this.thumbnailResourceId = thumbnailResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getThumbnailResourceId() {
        return thumbnailResourceId;
    }
}

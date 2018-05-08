package com.michaellundie.fabapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.jar.Pack200;

/*
 * A simple class to store and handle BookItem data.
 * Implements parcelable to allow for data restoration after a screen rotation.
 */
public class BookItem implements Parcelable {
    private String title;
    private ArrayList<String> authors;
    private String thumbnailURL;
    private int itemID;

    //Default Constructor
    public BookItem(String title, ArrayList<String> author, String thumbnailURL, int itemID) {
        this.title = title;
        this.authors = author;
        this.thumbnailURL = thumbnailURL;
        this.itemID = itemID;
    }

    //Constructor object taking parcelable (from returned bundle on instanceSaved) as an argument.
    private BookItem(Parcel in) {
        title = in.readString();
        authors = in.readArrayList(BookItem.class.getClassLoader());
        thumbnailURL = in.readString();
        itemID = in.readInt();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeList(authors);
        out.writeString(thumbnailURL);
        out.writeInt(itemID);
    }

    public static final Parcelable.Creator<BookItem> CREATOR = new Parcelable.Creator<BookItem>() {
        public BookItem createFromParcel(Parcel in) {
            return new BookItem(in);
        }

        public BookItem[] newArray(int size) {
            return new BookItem[size];
        }
    };

    public int describeContents() {
        return 0;
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

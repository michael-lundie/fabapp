package com.michaellundie.fabapp;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import static com.michaellundie.fabapp.MainActivity.setmLanguage;

//Answer to prevent spinner from triggering on screen rotation found here:
//https://stackoverflow.com/a/28466764/9738433
public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    boolean userSelect = false;
    final static int ENGLISH = 0;
    final static int JAPANESE = 1;
    private static int mSelectedLanguage;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        userSelect = true;
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (userSelect) {
            int selectionId = (int) id;
            switch (selectionId){
                case 0:
                    mSelectedLanguage = ENGLISH; //English
                    break;
                case 1:
                    mSelectedLanguage = JAPANESE; //Japanese
                    break;
                default:
                    mSelectedLanguage = ENGLISH;
                    break;
            }

            // Prevent our toast from triggering on screen rotation when the language hasn't changed.

            String currentItem = parent.getItemAtPosition(pos).toString();
            Toast.makeText(view.getContext(), "Search language is " + currentItem + ".", Toast.LENGTH_SHORT).show();
            userSelect = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static int getSelectedLanguage() {
        return mSelectedLanguage;
    }

}
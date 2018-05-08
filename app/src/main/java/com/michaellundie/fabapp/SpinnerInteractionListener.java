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
    final int ENGLISH = 0;
    final int JAPANESE = 1;

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
                    setmLanguage(ENGLISH); //English
                    break;
                case 1:
                    setmLanguage(JAPANESE); //Japanese
                    break;
                default:
                    setmLanguage(2);
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


}
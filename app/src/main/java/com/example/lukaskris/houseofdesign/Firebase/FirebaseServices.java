package com.example.lukaskris.houseofdesign.Firebase;

import android.content.Context;
import com.example.lukaskris.houseofdesign.Callback.Callback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Lukaskris on 28/07/2017.
 */

public class FirebaseServices {
    private static FirebaseServices instance;

    public static FirebaseServices getInstance() {
        if (instance == null) {
            instance = new FirebaseServices();
        }
        return instance;
    }

    public void getItem(final Context context, final String id, final Callback callback){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("item");

    }
}

package com.example.lukaskris.houseofdesign.Util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import endpoint.backend.itemApi.model.Item;

/**
 * Created by xub on 04/08/17.
 */

public class AdapterCachingUtil {

    public static void store(Context context, String fileName, List<Item> object){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        }catch (Exception e){
            Log.d("AdapterCachingUtil",e.toString());
        }
    }
    public static List<Item> load(Context context, String fileName){
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Item> object = (List<Item>) objectInputStream.readObject();
            objectInputStream.close();
            return object;
        }catch (Exception e){
            Log.d("AdapterCachingUtil",e.toString());
        }
        return null;
    }
}

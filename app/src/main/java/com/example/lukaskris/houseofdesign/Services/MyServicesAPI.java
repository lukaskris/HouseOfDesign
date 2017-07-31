package com.example.lukaskris.houseofdesign.Services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lukaskris.houseofdesign.Callback.Callback;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import endpoint.backend.itemApi.ItemApi;
import endpoint.backend.itemApi.model.Item;


/**
 * Created by Lukaskris on 31/07/2017.
 */

public class MyServicesAPI {
    private static MyServicesAPI instance;
    private static ItemApi myApiService = null;
    public static MyServicesAPI getInstance(){
        if(instance == null){
            instance = new MyServicesAPI();
            if(myApiService == null) {
                ItemApi.Builder builder = new ItemApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://default-demo-app-db53e.appspot.com/_ah/api/");
                myApiService = builder.build();
            }
        }
        return instance;
    }

    public void getItems(final Context context, final String category, final int offset, final Callback callback){
        //AsyncTask<Param,Progress,Result>
        new AsyncTask<String,Boolean, List<Item>>(){

            @Override
            protected List<Item> doInBackground(String... params) {
                try {
                    List<Item> items= myApiService.getAllItem().setCategory(category).setOffset(offset).execute().getItems();
                    callback.onSuccess(items);
                } catch (IOException e) {
                    Log.d("Errorfromendpointasync",e.getMessage());
//                    ExceptionUtil.handleException(e);
                    callback.onError(Collections.EMPTY_LIST);
                }
                return null;
            }
        }.execute();
    }

}

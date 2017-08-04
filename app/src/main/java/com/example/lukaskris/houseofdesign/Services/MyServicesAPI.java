package com.example.lukaskris.houseofdesign.Services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.lukaskris.houseofdesign.Callback.Callback;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;

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
//                        .setRootUrl("https://default-demo-app-db53e.appspot.com/_ah/api/");
                        .setRootUrl("https://utility-time-161403.appspot.com/_ah/api/");
                myApiService = builder.build();
            }
        }
        return instance;
    }

    public void getItems(final Context context, final String category, final int offset, final Callback callback){
        //AsyncTask<Param,Progress,Result>
        new AsyncTask<String,Boolean, List<Item>>(){
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setMessage("Retrieving Item...");
                pd.show();
            }

            @Override
            protected List<Item> doInBackground(String... params) {
                List<Item> items = null;
                try {
                    items= myApiService.getAllItem().setCategory(category).setOffset(offset).execute().getItems();

                } catch (IOException e) {
                    Log.d("Errorfromendpointasync",e.getMessage());
                    callback.onError(Collections.EMPTY_LIST);
                }
                return items;
            }

            @Override
            protected void onPostExecute(List<Item> items) {
                pd.dismiss();

                callback.onSuccess(items);
            }
        }.execute();
    }

    public void getItem(final Context context, final String id, final Callback callback){
        new AsyncTask<String, Void, Item>(){
            ProgressDialog pd;
            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setMessage("Retrieving Item...");
                pd.show();
            }

            @Override
            protected Item doInBackground(String... strings) {
                Item item=null;
                try{
                    item = myApiService.getItem(id).execute();
//                    callback.onSuccess(item);
                }catch (Exception e){
                    callback.onError(e.getMessage());
                    Log.d("Could not retrieve Item", e.getMessage(), e);
                }

                return item;
            }

            @Override
            protected void onPostExecute(Item item) {
                pd.dismiss();
                callback.onSuccess(item);
            }
        }.execute();

    }
}

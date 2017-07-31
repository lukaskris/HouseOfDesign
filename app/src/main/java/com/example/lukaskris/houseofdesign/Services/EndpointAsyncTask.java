package com.example.lukaskris.houseofdesign.Services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import endpoint.backend.itemApi.ItemApi;
import endpoint.backend.itemApi.model.Item;

/**
 * Created by Lukaskris on 31/07/2017.
 */

public class EndpointAsyncTask extends AsyncTask<Void, Void, List<Item>> {
    private static ItemApi myApiService = null;
    private Context context;

    public EndpointAsyncTask(Context context) {
        this.context = context;
    }
    @Override
    protected List<Item> doInBackground(Void... params) {
        if(myApiService == null) {
            ItemApi.Builder builder = new ItemApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://default-demo-app-db53e.appspot.com/_ah/api/");
            myApiService = builder.build();
        }
        try {
            List<Item> items= myApiService.getAllItem().setCategory("Pria").setOffset(0).execute().getItems();
            return items;
        } catch (IOException e) {
            Log.d("Errorfromendpointasync",e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<Item> result) {
        for (Item q : result) {
            Toast.makeText(context, q.getId() + ":" + q.getName(),Toast.LENGTH_LONG).show();
        }
    }
}

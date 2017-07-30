package com.example.lukaskris.houseofdesign.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.example.Lukaskris.myapplication.backend.ItemEndpoint;
import com.example.lukaskris.houseofdesign.Model.Item;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

/**
 * Created by Lukaskris on 30/07/2017.
 */

public class EndpointsAsyncTask extends AsyncTask<Void, Void, List<Item>> {
    private static ItemEndpoint myApiService = null;
    private Context context;

    EndpointsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<Item> doInBackground(Void... params) {
//        if(myApiService == null) { // Only do this once
//            ItemEndpoint.Builder builder = new
//                    ItemEndpoint().Builder(AndroidHttp.newCompatibleTransport(),
//                    new AndroidJsonFactory(), null)
//                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                @Override
//                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                    abstractGoogleClientRequest.setDisableGZipContent(true);
//                }
//            });
//        }
        return null;
    };
}

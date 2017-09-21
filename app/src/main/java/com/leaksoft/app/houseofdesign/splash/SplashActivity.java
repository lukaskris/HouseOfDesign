package com.leaksoft.app.houseofdesign.splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.leaksoft.app.houseofdesign.HomeActivity;
import com.leaksoft.app.houseofdesign.model.Category;
import com.leaksoft.app.houseofdesign.model.Items;
import com.leaksoft.app.houseofdesign.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.leaksoft.app.houseofdesign.services.ServiceFactory.service;

public class SplashActivity extends AppCompatActivity {
    List<Category> categoryList;
    List<Items> itemsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        categoryList = new ArrayList<>();
        itemsList = new ArrayList<>();
        fetchData();
    }

    private void fetchData(){

        service.getCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        if(categories.size()>0){
                            categoryList.addAll(categories);
                            getItem();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        Toast.makeText(SplashActivity.this,throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("error", "No Internet Connection");
                        startActivity(intent);
                        finish();
                    }
                });


    }

    private void getItem(){
        service.getItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<Items>>() {
                @Override
                public void accept(List<Items> itemses) throws Exception {
                    if (itemses.size() > 0) {
                        itemsList.addAll(itemses);
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("category", (Serializable) categoryList);
                        intent.putExtra("items", (Serializable) itemsList);
                        Log.e("debug category", categoryList.toString());
                        Log.e("debug items", itemsList.toString());
                        startActivity(intent);
                        finish();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.e("error ", throwable.getLocalizedMessage());
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.putExtra("error", throwable.getLocalizedMessage());
                    startActivity(intent);
                    finish();
                }
            });

    }
}

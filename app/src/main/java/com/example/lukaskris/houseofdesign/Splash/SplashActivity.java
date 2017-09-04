package com.example.lukaskris.houseofdesign.Splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lukaskris.houseofdesign.HomeActivity;
import com.example.lukaskris.houseofdesign.Model.Category;
import com.example.lukaskris.houseofdesign.Model.CategoryItem;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.example.lukaskris.houseofdesign.R;
import com.example.lukaskris.houseofdesign.Util.NetworkUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import endpoint.backend.itemApi.model.Item;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.lukaskris.houseofdesign.Services.ServiceFactory.service;

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
                        Toast.makeText(SplashActivity.this,throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void getItem(){
        if(NetworkUtil.isOnline(this)) {
            service.getItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Items>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Items> value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


//                    .subscribe(new Consumer<List<Items>>() {
//                @Override
//                public void accept(List<Items> itemses) throws Exception {
//                    if (itemses.size() > 0) {
//                        itemsList.addAll(itemses);
//                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                        intent.putExtra("category", (Serializable) categoryList);
//                        intent.putExtra("items", (Serializable) itemsList);
//                        Log.e("debug category", categoryList.toString());
//                        Log.e("debug items", itemsList.toString());
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }, new Consumer<Throwable>() {
//                @Override
//                public void accept(Throwable throwable) throws Exception {
//                    Log.e("error ", throwable.getLocalizedMessage());
//                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                    intent.putExtra("error", throwable.getLocalizedMessage());
//                    startActivity(intent);
//                    finish();
//                }
//            });
        }else{

            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.putExtra("error", "No Internet Connection");
            startActivity(intent);
            finish();
        }
    }
}

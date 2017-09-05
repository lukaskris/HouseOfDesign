package com.example.lukaskris.houseofdesign.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lukaskris.houseofdesign.Model.CategoryItem;
import com.example.lukaskris.houseofdesign.Model.Items;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import endpoint.backend.itemApi.model.Item;


/**
 * Created by Lukaskris on 25/08/2017.
 */

public class PreferencesUtil {
    static final String CART = "LOCAL_CART";
    static final String CART_KEY = "carts";
    static final String FAVORITES = "LOCAL_FAVORITES";
    static final String FAVORITES_KEY = "favorites";
    static final String HOME = "LOCAL_HOME";
    static final String HOME_KEY = "home";

    private PreferencesUtil(){}

    public static void saveCart(Context context, List<Item> carts){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(CART,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCart = gson.toJson(carts);
        editor.putString(CART_KEY,jsonCart);
        editor.apply();
    }

    public static void saveHome(Context context, List<CategoryItem> category){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(HOME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCart = gson.toJson(category);
        editor.putString(HOME_KEY,jsonCart);
        editor.apply();
    }

    public static void addCart(Context context, Item cart){
        List<Item> carts = getCarts(context);
        if (carts == null)
            carts = new ArrayList<>();
        carts.add(cart);
        saveCart(context,carts);
    }

    public static void removeCart(Context context, Item cart){
        List<Item> carts = getCarts(context);
        if (carts != null) {
            carts.remove(cart);
            saveCart(context,carts);
        }
    }

    public static ArrayList<Item> getCarts(Context context){
        SharedPreferences sharedPreferences;
        List<Item> carts;
        sharedPreferences = context.getSharedPreferences(CART, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(CART_KEY)){
            String json = sharedPreferences.getString(CART_KEY,null);
            Gson gson = new Gson();
            Item[] cart = gson.fromJson(json, Item[].class);
            carts = Arrays.asList(cart);
            carts = new ArrayList<>(carts);
        }
        else
            return null;

        return (ArrayList<Item>)carts;
    }

    public static ArrayList<CategoryItem> getHome(Context context){
        SharedPreferences sharedPreferences;
        List<CategoryItem> home;
        sharedPreferences = context.getSharedPreferences(HOME, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(HOME_KEY)){
            String json = sharedPreferences.getString(HOME_KEY,null);
            Gson gson = new Gson();
            CategoryItem[] categoryItems = gson.fromJson(json, CategoryItem[].class);
            home = Arrays.asList(categoryItems);
            home = new ArrayList<>(home);
        }
        else
            return null;

        return (ArrayList<CategoryItem>)home;
    }


}

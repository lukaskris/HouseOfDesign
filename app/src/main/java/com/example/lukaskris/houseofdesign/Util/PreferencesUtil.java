package com.example.lukaskris.houseofdesign.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lukaskris.houseofdesign.Model.Cart;
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
    private static final String CART = "LOCAL_CART";
    private static final String CART_KEY = "carts";
    static final String FAVORITES = "LOCAL_FAVORITES";
    static final String FAVORITES_KEY = "favorites";
    private static final String HOME = "LOCAL_HOME";
    private static final String HOME_KEY = "home";

    private PreferencesUtil(){}

    public static void saveCart(Context context, List<Cart> carts){
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

    public static void addCart(Context context, Cart cart){
        List<Cart> carts = getCarts(context);

        if (carts == null) {
            carts = new ArrayList<>();
            carts.add(cart);
        }else if(containsCart(carts,cart)){
            for(Cart c:carts){
                if(c.getSubitem_id() == cart.getSubitem_id()){
                    if(cart.getQuantity_max() >= c.getQuantity() + cart.getQuantity()){
                        c.setQuantity(c.getQuantity() + cart.getQuantity());
                    }else {
                        c.setQuantity(cart.getQuantity_max());
                        c.setQuantity_max(cart.getQuantity_max());
                    }
                }
            }
        }else {
            carts.add(cart);
        }
        saveCart(context,carts);
    }

    private static boolean containsCart(List<Cart> carts, Cart cart){
        for(Cart c : carts){
            if(c.getSubitem_id() == cart.getSubitem_id()){
                return true;
            }
        }

        return false;
    }

    public static void removeCart(Context context, Cart cart){
        List<Cart> carts = getCarts(context);
        if (carts != null) {
            carts.remove(cart);
            saveCart(context,carts);
        }
    }

    public static ArrayList<Cart> getCarts(Context context){
        SharedPreferences sharedPreferences;
        List<Cart> carts;
        sharedPreferences = context.getSharedPreferences(CART, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(CART_KEY)){
            String json = sharedPreferences.getString(CART_KEY,null);
            Gson gson = new Gson();
            Cart[] cart = gson.fromJson(json, Cart[].class);
            carts = Arrays.asList(cart);
            carts = new ArrayList<>(carts);
        }
        else
            return null;

        return (ArrayList<Cart>)carts;
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

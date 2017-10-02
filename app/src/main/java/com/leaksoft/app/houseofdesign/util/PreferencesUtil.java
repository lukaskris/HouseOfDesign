package com.leaksoft.app.houseofdesign.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.leaksoft.app.houseofdesign.model.Cart;
import com.leaksoft.app.houseofdesign.model.CategoryItem;
import com.leaksoft.app.houseofdesign.model.Customer;
import com.google.gson.Gson;
import com.leaksoft.app.houseofdesign.model.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferencesUtil {
    private static final String CART = "LOCAL_CART";
    private static final String CART_KEY = "carts";
    static final String FAVORITES = "LOCAL_FAVORITES";
    static final String FAVORITES_KEY = "favorites";
    private static final String HOME = "LOCAL_HOME";
    private static final String HOME_KEY = "home";
    private static final String USER = "LOCAL_USER";
    private static final String USER_KEY = "user";
    private static final String SEARCH ="LOCAL_SEARCH";
    private static final String SEARCH_KEY ="search";

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

    public static void saveSearch(Context context, List<String> search){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(SEARCH,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCart = gson.toJson(search);
        editor.putString(SEARCH_KEY,jsonCart);
        editor.apply();
    }

    public static void saveFavorites(Context context, List<Items> favorites){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(FAVORITES,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCart = gson.toJson(favorites);
        editor.putString(FAVORITES_KEY,jsonCart);
        editor.apply();
    }


    public static void saveUser(Context context, Customer customer){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(USER,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCart = gson.toJson(customer);
        editor.putString(USER_KEY,jsonCart);
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

    public static void addSearch(Context context, String item){
        List<String> search = getSearch(context);
        if(search == null){
            search = new ArrayList<>();
            search.add(item);
        }else if(containsSearch(search, item)){
            for(String s : search){
                if(s.equalsIgnoreCase(item)){
                    List<String> temp = new ArrayList<>();
                    temp.addAll(search);
                    temp.remove(s);
                    temp.add(s);
                    search = temp;
                    break;
                }
            }
        }else {
            search.add(item);
        }
        saveSearch(context, search);
    }

    public static void addFavorites(Context context, Items item){
        List<Items> favorites = getFavorites(context);

        if (favorites == null) {
            favorites = new ArrayList<>();
            favorites.add(item);
        }else if(!containsFavorites(favorites,item)){
            favorites.add(item);
        }
        saveFavorites(context,favorites);
    }


    private static boolean containsCart(List<Cart> carts, Cart cart){
        for(Cart c : carts){
            if(c.getSubitem_id() == cart.getSubitem_id()){
                return true;
            }
        }

        return false;
    }

    private static boolean containsSearch(List<String> search, String item){
        for(String s : search){
            if(s.equalsIgnoreCase(item)){
                return true;
            }
        }

        return false;
    }

    private static boolean containsFavorites(List<Items> items, Items item){
        for(Items c : items){
            if(c.getId() == item.getId()){
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

    public static void removeFavorites(Context context, Items item){
        List<Items> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(item);
            saveFavorites(context,favorites);
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
            return new ArrayList<>();

        return (ArrayList<Cart>)carts;
    }

    public static ArrayList<String> getSearch(Context context){
        SharedPreferences sharedPreferences;
        List<String> search;
        sharedPreferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(SEARCH_KEY)){
            String json = sharedPreferences.getString(SEARCH_KEY, null);
            Gson gson = new Gson();
            String[] item = gson.fromJson(json, String[].class);
            search = Arrays.asList(item);
            search = new ArrayList<>(search);
        }else {
            return new ArrayList<>();
        }
        return (ArrayList<String>)search;
    }

    public static ArrayList<Items> getFavorites(Context context){
        SharedPreferences sharedPreferences;
        List<Items> favorites;
        sharedPreferences = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(FAVORITES_KEY)){
            String json = sharedPreferences.getString(FAVORITES_KEY,null);
            Gson gson = new Gson();
            Items[] favorite = gson.fromJson(json, Items[].class);
            favorites = Arrays.asList(favorite);
            favorites = new ArrayList<>(favorites);
        }
        else
            return new ArrayList<>();

        return (ArrayList<Items>)favorites;
    }


    public static Customer getUser(Context context){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(USER, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(USER_KEY)){
            String json = sharedPreferences.getString(USER_KEY,null);
            Gson gson = new Gson();
            return gson.fromJson(json, Customer.class);
        }
        return null;
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
            return new ArrayList<>();

        return (ArrayList<CategoryItem>)home;
    }


}

package com.leaksoft.app.houseofdesign.model;

import java.util.ArrayList;

/**
 * Created by Lukaskris on 03/09/2017.
 */

public class CategoryItem{
    private String headerTitle;
    private int idCategory;
    private ArrayList<Items> allItemsInSection;

    public CategoryItem(int id, String headerTitle) {
        this.headerTitle = headerTitle;
        idCategory = id;
        allItemsInSection = new ArrayList<>();
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<Items> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<Items> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

    public int getIdCategory() {
        return idCategory;
    }
}
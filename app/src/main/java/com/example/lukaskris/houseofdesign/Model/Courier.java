package com.example.lukaskris.houseofdesign.Model;

/**
 * Created by Lukaskris on 12/08/2017.
 */

public class Courier {
    String mNameService;
    String mCost;
    String mEstimate;
    String mCodeService;
    public Courier(){}

    public String getmCodeService() {
        return mCodeService;
    }

    public void setmCodeService(String mCodeService) {
        this.mCodeService = mCodeService;
    }

    public Courier(String mCodeService, String mNameService, String mCost, String mEstimate) {
        this.mNameService = mNameService;
        this.mCost = mCost;
        this.mEstimate = mEstimate;
        this.mCodeService = mCodeService;
    }

    public String getmNameService() {
        return mNameService;
    }

    public void setmNameService(String mNameService) {
        this.mNameService = mNameService;
    }

    public String getmCost() {
        return mCost;
    }

    public void setmCost(String mCost) {
        this.mCost = mCost;
    }

    public String getmEstimate() {
        return mEstimate;
    }

    public void setmEstimate(String mEstimate) {
        this.mEstimate = mEstimate;
    }
}

package com.android.termeloiwebshop;

public class ShoppingItem {
    private String name;
    private String info;
    private String price;
    private float ratingInfo;
    private int imageRes;
    private int darab;

    public ShoppingItem() {
    }

    public ShoppingItem(String name, String info, String price,float ratingInfo, int imageRes) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratingInfo = ratingInfo;
        this.imageRes = imageRes;
        this.darab = 1;
    }

    public ShoppingItem(String name, String info, String price, float ratingInfo, int imageRes, int darab) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratingInfo = ratingInfo;
        this.imageRes = imageRes;
        this.darab = darab;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRatingInfo() {
        return ratingInfo;
    }

    public void setRatingInfo(float ratingInfo) {
        this.ratingInfo = ratingInfo;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getDarab() {
        return darab;
    }

    public void setDarab(int darab) {
        this.darab = darab;
    }
}

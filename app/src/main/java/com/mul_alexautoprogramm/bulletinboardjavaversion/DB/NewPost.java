package com.mul_alexautoprogramm.bulletinboardjavaversion.DB;

import java.io.Serializable;

public class NewPost implements Serializable {
    private String imId;
    private String imId2;
    private String imId3;
    private String title;
    private String price;
    private String tel_numb;
    private String desc;
    private String key;
    private String uid;
    private String email;
    private String totalViews = "0";
    private String totalEmails = "0";
    private String totalCalls = "0";
    private long favCounter = 0;
    private boolean isFav = false;

    public String getImId2() {
        return imId2;
    }

    public void setImId2(String imId2) {
        this.imId2 = imId2;
    }

    public String getImId3() {
        return imId3;
    }

    public void setImId3(String imId3) {
        this.imId3 = imId3;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTel_numb() {
        return tel_numb;
    }

    public void setTel_numb(String tel_numb) {
        this.tel_numb = tel_numb;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTotalEmails() {
        return totalEmails;
    }

    public void setTotalEmails(String totalEmails) {
        this.totalEmails = totalEmails;
    }

    public String getTotalCalls() {
        return totalCalls;
    }

    public void setTotalCalls(String totalCalls) {
        this.totalCalls = totalCalls;
    }

    public long getFavCounter() {
        return favCounter;
    }

    public void setFavCounter(long favCounter) {
        this.favCounter = favCounter;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }
}

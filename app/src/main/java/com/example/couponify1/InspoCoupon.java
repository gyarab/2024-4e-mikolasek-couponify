package com.example.couponify1;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class InspoCoupon {
    String title, desc, genre;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public InspoCoupon(String title, String desc, String genre) {
        this.title = title;
        this.desc = desc;
        this.genre = genre;
    }
    public InspoCoupon() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

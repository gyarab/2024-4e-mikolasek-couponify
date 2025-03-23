package com.example.couponify1;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Coupon {
    String title, desc, writtenby, writtento, createdon;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Coupon(String title, String desc, String writtenby, String writtento) {
        this.title = title;
        this.desc = desc;
        this.writtenby = writtenby;
        this.writtento = writtento;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM. yyyy");
        this.createdon = LocalDate.now().format(formatter);
    }

    public Coupon() {
    }

    public String getCreatedon() {
        return createdon;
    }

/*
    public String getCreatedon() {
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd.MM. yyyy");
        }
        String formattedString = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formattedString = createdon.format(formatter);
        }
        return formattedString;
    }*/

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

    public String getWrittenby() {
        return writtenby;
    }

    public void setWrittenby(String writtenby) {
        this.writtenby = writtenby;
    }

    public String getWrittento() {
        return writtento;
    }

    public void setWrittento(String writtento) {
        this.writtento = writtento;
    }
}

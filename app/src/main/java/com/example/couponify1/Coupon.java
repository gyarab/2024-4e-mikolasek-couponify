package com.example.couponify1;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Coupon {
    String title, desc, writtenby, writtento, createdon, type;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Coupon(String title, String desc, String writtenby, String writtento) {
        this.title = title;
        this.desc = desc;
        this.writtenby = writtenby;
        this.writtento = writtento;
        this.type = "Coupon";
        //convert date into string, firebase cannot save LocalDates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM. yyyy");
        this.createdon = LocalDate.now().format(formatter);
    }

    public Coupon() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedon() {
        return createdon;
    }
    public void setCreatedon(String createdon) {
        this.createdon = createdon;
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

    public String getType() {
        return type;
    }
}

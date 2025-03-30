package com.example.couponify1;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class GameCoupon extends Coupon{
    boolean isactive;
    String type;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameCoupon(String title, String desc, String writtenby, String writtento) {
        super(title, desc, writtenby, writtento);
        this.type = "GameCoupon";
    }
    public GameCoupon() {
    }

    public void activate() {
        isactive = true;
    }

    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }

    @Override
    public String getType() {
        return type;
    }
}

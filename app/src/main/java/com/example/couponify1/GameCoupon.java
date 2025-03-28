package com.example.couponify1;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class GameCoupon extends Coupon{
    boolean active;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameCoupon(String title, String desc, String writtenby, String writtento) {
        super(title, desc, writtenby, writtento);
    }

    public GameCoupon() {
    }
}

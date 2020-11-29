package com.henu.android.utils;

import android.os.Bundle;

import com.henu.android.entity.User;

public class BundleUtils {

    public static Bundle setUserToBundle(Bundle bundle,User user) {
        bundle.putCharSequence("password",user.getPassword());
        bundle.putCharSequence("username",user.getUsername());
        bundle.putCharSequence("id",String.valueOf(user.getId()));
        bundle.putCharSequence("telNumber",user.getTelNumber());
        return bundle;
    }


    public static User getUserFromBundle(Bundle bundle) {
        User user = null;
        if(bundle != null) {
            user = new User();
            user.setUsername(bundle.getString("username"));
            user.setPassword(bundle.getString("password"));
            user.setId(Integer.parseInt(bundle.getString("id")));
            user.setTelNumber(bundle.getString("telNumber"));
        }
        return user;
    }
}

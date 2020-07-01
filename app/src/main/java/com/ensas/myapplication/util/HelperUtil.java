package com.ensas.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ensas.myapplication.ui.login.LoginActivity;

public class HelperUtil {
    public static void removeToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.mypreference,
                Context.MODE_PRIVATE);
        if(sharedPreferences.contains("myToken")){
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.remove("myToken");
            myEdit.apply();
        }

    }
}

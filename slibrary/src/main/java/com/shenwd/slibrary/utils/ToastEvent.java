package com.shenwd.slibrary.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastEvent {

    public static void show(Context context, String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }

}

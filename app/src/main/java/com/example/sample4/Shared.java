package com.example.sample4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class Shared {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    int mode = 0;
    String Filename = "sdfile";
    String Data = "b";
    public Shared(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Filename,mode);
        editor = sharedPreferences.edit();
    }
    public void secondtime(String s)
    {
        editor.putString(Filename,s);
        editor.putBoolean(Data, true);
        editor.commit();
        Log.d("USERNAME",sharedPreferences.getString(Filename,"NONE"));
    }
    public void firsttime()
    {
        if(!this.login())
        {
            Intent intent = new Intent(context,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
    public String getstr()
    {
        return Filename;
    }
    private boolean login() {
        return sharedPreferences.getBoolean(Data,false);
    }
}

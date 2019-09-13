package com.stg.bloodbank;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedit {
    Context shr;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public sharedit(Context shr){
        this.shr = shr;

    }
    public void addpreference(String preference, String key){
        sharedPref = shr.getSharedPreferences("mitapp", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString(key, preference.toString());
        editor.apply();
    }
    public void addintpreference(int preference, String key){
        sharedPref = shr.getSharedPreferences("mitapp", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putInt(key, preference);
        editor.apply();
    }

    public String extractpreference(String preference){
        sharedPref = shr.getSharedPreferences("mitapp", Context.MODE_PRIVATE);
        String extracted_preference = sharedPref.getString(preference, "");
        return extracted_preference;
    }
    public int extractintpreference(String key){
        sharedPref = shr.getSharedPreferences("mitapp", Context.MODE_PRIVATE);
        int extracted_preference = Integer.parseInt(String.valueOf(sharedPref.getInt(key, 1)));
        return extracted_preference;
    }
    public void commitpreference(){
        sharedPref = shr.getSharedPreferences("mitapp", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

}

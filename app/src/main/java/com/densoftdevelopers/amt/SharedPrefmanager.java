package com.densoftdevelopers.amt;

import android.content.Context;
import android.content.SharedPreferences;

import static android.provider.Telephony.Carriers.PASSWORD;

public class SharedPrefmanager {

    public static final String SHARED_PREF_NAME = "antenna_shared_pref";
    public static final String NAME = "UserName";
    public static final String EMAIL = "UserEmail";
    public static final String PHONE = "UserPhone";
    public static   String ATTEMPTS = "UserAttempts";


    private static SharedPrefmanager mInstance;
    private static Context mCtx;

    public SharedPrefmanager(Context context) {

        mCtx = context;
    }

    public static synchronized SharedPrefmanager getInstance(Context context){
        if (mInstance == null){
            mInstance = new SharedPrefmanager(context);
        }
        return  mInstance;
    }

    //method to let the user login
    //method that will store the new user data in shared preferences

    public void userLogin(User user){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, user.getName());
        editor.putString(EMAIL,user.getEmail());
        editor.putString(PHONE,user.getPhone());
        editor.putString(PASSWORD,user.getPassword());
        editor.putString(ATTEMPTS,user.getAttempts());
        editor.apply();
    }

    //this method will check whether the user is already logged in or not

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME, null) != null;
    }


    //this method will give a logged in user
    public User getUser(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(NAME,null),
                sharedPreferences.getString(EMAIL,null),
                sharedPreferences.getString(PHONE,null),
                sharedPreferences.getString(PASSWORD,null),
                sharedPreferences.getString(ATTEMPTS,null)
        );
    }

    public  void Logout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        //send to login Activity
    }

}

package com.jb.dev.jhmemory.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.jb.dev.jhmemory.activities.UserRegister;

public class AuthHelper {
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static String TAG = "AuthHelper";

    public static boolean isLogin(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("isSet", false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void isSetLogin(Context context, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(context), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isSet", value);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void isClear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(context), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}

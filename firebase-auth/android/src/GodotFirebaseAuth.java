package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.DisplayMetrics;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.Display;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.File;
import java.util.Currency;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;
import java.lang.Exception;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FacebookAuthProvider;

public class GodotFirebaseAuth extends Godot.SingletonBase {

    private Godot activity = null;
    private FirebaseAuth mAuth;

    static public Godot.SingletonBase initialize(Activity p_activity) 
    { 
        return new GodotFirebaseAuth(p_activity); 
    } 

    public GodotFirebaseAuth(Activity p_activity) 
    {
        registerClass("FirebaseAuth", new String[]{
                "sign_in_anonymously",
                "sign_in_facebook",
                "is_logged_in",
                "user_name",
                "photo_url",
                "email",
                "uid",
                "sign_out"
            });
        addSignal("FirebaseAuth", "logged_in", new String[] {});
        activity = (Godot)p_activity;
        mAuth = FirebaseAuth.getInstance();
    }

    // Public methods

    public void init(final String key)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //mAuth = FirebaseAuth.getInstance();
                } catch (Exception e) {
                    Log.e("godot", "Exception: " + e.getMessage());  
                }
            }
        });
    }

    public void sign_in_anonymously()
    {
        mAuth.signInAnonymously()
        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("godot", "signInAnonymously:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    emitSignal("FirebaseAuth", "logged_in", new Object[]{});
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("godot", "signInAnonymously:failure", task.getException());
                    
                }
            }
        });
    }

    public void sign_in_facebook(final String token)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("godot", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        emitSignal("FirebaseAuth", "logged_in", new Object[]{});
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("godot", "signInWithCredential:failure", task.getException());

                    }

                }
            });
    }

    public boolean is_logged_in()
    {
        return mAuth.getCurrentUser() != null;
    }

    public String user_name()
    {
        return mAuth.getCurrentUser().getDisplayName();
    }

    public String photo_url()
    {
        return mAuth.getCurrentUser().getPhotoUrl().toString();
    }

    public String email()
    {
        return mAuth.getCurrentUser().getEmail();
    }

    public String uid()
    {
        return mAuth.getCurrentUser().getUid();
    }

    public void sign_out()
    {
        mAuth.signOut();
    }

    // Internal methods

    public void callbackSuccess(String ticket, String signature, String sku) {
		//GodotLib.callobject(facebookCallbackId, "purchase_success", new Object[]{ticket, signature, sku});
        //GodotLib.calldeferred(purchaseCallbackId, "consume_fail", new Object[]{});
	}
    @Override protected void onMainActivityResult (int requestCode, int resultCode, Intent data)
    {
    }
}

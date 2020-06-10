package ru.mobilap.firebaseauth;

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
import android.view.View;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;
import java.util.Set;
import java.lang.Exception;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FacebookAuthProvider;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

public class FirebaseAuth extends GodotPlugin {

    private final String TAG = FirebaseAuth.class.getName();
    final private SignalInfo loggedInSignal = new SignalInfo("logged_in");
    private com.google.firebase.auth.FirebaseAuth mAuth;

    public FirebaseAuth(Godot godot) 
    {
        super(godot);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    }

    @Override
    public String getPluginName() {
        return "FirebaseAuth";
    }

    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                              "sign_in_anonymously",
                              "sign_in_facebook",
                              "is_logged_in",
                              "user_name",
                              "photo_url",
                              "email",
                              "uid",
                              "sign_out");
    }

    @Override
    public Set<SignalInfo> getPluginSignals() {
        return Collections.singleton(loggedInSignal);
    }

    @Override
    public View onMainCreateView(Activity activity) {
        return null;
    }

    // Public methods

    public void sign_in_anonymously()
    {
        mAuth.signInAnonymously()
            .addOnCompleteListener(getGodot(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            emitSignal(loggedInSignal.getName());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                    
                        }
                    }
                });
    }

    public void sign_in_facebook(final String token)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(getGodot(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        emitSignal(loggedInSignal.getName());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

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
}

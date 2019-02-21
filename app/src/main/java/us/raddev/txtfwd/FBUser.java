package us.raddev.txtfwd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by roger.deutsch on 3/30/2016.
 */
public class FBUser {
    private String userId;
    private String sessionPassword;
    private boolean isForwardOn;
    private boolean isNumberCaptureOn;

    public boolean getIsNumberCaptureOn() {
        return isNumberCaptureOn;
    }

    public void setIsNumberCaptureOn(boolean isNumberCaptureOn) {
        this.isNumberCaptureOn = isNumberCaptureOn;
        SharedPreferences txtFwdPrefs =
                context.getApplicationContext().getSharedPreferences("isNumberCaptureOn", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = txtFwdPrefs.edit();
        edit.putBoolean("isNumberCaptureOn", this.isNumberCaptureOn);
        Log.d("MainActivity", "before commit numberCapture: " + String.valueOf(this.isNumberCaptureOn));
        edit.commit();

    }

    public boolean getIsForwardOn() {
        return this.isForwardOn;
    }

    public void setIsForwardOn(boolean isForwardOn) {
        this.isForwardOn = isForwardOn;
        SharedPreferences txtFwdPrefs =
                context.getApplicationContext().getSharedPreferences("isForwardOn", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = txtFwdPrefs.edit();
        edit.putBoolean("isForwardOn", this.isForwardOn);
        Log.d("MainActivity", "before commit : " + String.valueOf(this.isForwardOn));
        edit.commit();
    }


    private Context context;

    public FBUser(Context context) {
        this.context = context;
        Initialize();
    }

    private void Initialize(){
        SharedPreferences txtFwdPrefs =
                context.getApplicationContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        userId = txtFwdPrefs.getString("userId", null);
        txtFwdPrefs = context.getApplicationContext().getSharedPreferences("isForwardOn", Context.MODE_PRIVATE);
        isForwardOn = txtFwdPrefs.getBoolean("isForwardOn", false);

        txtFwdPrefs = context.getApplicationContext().getSharedPreferences("isNumberCaptureOn", Context.MODE_PRIVATE);
        isNumberCaptureOn = txtFwdPrefs.getBoolean("isNumberCaptureOn", false);
        Log.d("MainActivity", "isNumberCaptureOn : "  + String.valueOf(isNumberCaptureOn));

        Log.d("MainActivity", "FBUSer.isForwardOn : " + String.valueOf(isForwardOn));
        if (userId == null)
        {
            generateUserId();
        }

        Log.d("MainActivity", "loaded userId : " + userId);
    }

    private void generateUserId(){
        Random rand = new Random();
        userId = String.valueOf(rand.nextLong() + 1);
        SharedPreferences txtFwdPrefs =
                context.getApplicationContext().getSharedPreferences("userId", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = txtFwdPrefs.edit();
        edit.putString("userId",userId);
        edit.commit();
        Log.d("MainActivity", "Committed: " + userId);
    }


    public FBUser(String userId, String sessionPassword) {
        this.userId = userId;
        this.sessionPassword = sessionPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionPassword() {
        return sessionPassword;
    }

    public String encodeBytes(byte[] msgBytes)
    {
        return Base64.encodeToString(msgBytes, Base64.NO_WRAP);
    }

    public void setSessionPassword(String sessionPassword) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(sessionPassword.getBytes());
            String encryptedString = encodeBytes(messageDigest.digest());
            this.sessionPassword = encryptedString;
            FirebaseDatabase fbd = FirebaseDatabase.getInstance();
            DatabaseReference myFirebaseRef = fbd.getReference("demo/" +
                    String.valueOf(userId));

            myFirebaseRef.child("sessionPwd").setValue(encryptedString);
        }
        catch (NoSuchAlgorithmException nsa){
            Log.d("MainActivity", "No such algo");
        }
    }




}

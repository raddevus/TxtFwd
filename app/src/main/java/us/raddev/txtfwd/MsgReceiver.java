package us.raddev.txtfwd;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MsgReceiver extends BroadcastReceiver {
    public static FBUser mainUser;

    @Override
    public void onReceive(Context context, Intent intent) {

        SmsMessage message = null;
        String from = null;

        if (mainUser == null) {
            mainUser = new FBUser(context);
        }

        if (mainUser.getIsNumberCaptureOn()) {
            message = GetMessage(intent);
            from = message.getOriginatingAddress();
            saveNumber(from,context);
        }

        if (mainUser.getIsForwardOn()) {

            if (message == null){
                message = GetMessage(intent);
            }

            from = message.getOriginatingAddress();
            String body = message.getMessageBody();
            long receivedDate = message.getTimestampMillis();
            Date date = new Date(receivedDate);
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
            String formattedDate = formatter.format(date);
            //FirebaseApp.initializeApp(context);
            FirebaseDatabase fbd = FirebaseDatabase.getInstance();
            DatabaseReference myFirebaseRef = fbd.getReference("https://txtfwd.firebaseio.com/demo/" + String.valueOf(mainUser.getUserId()) + "/Messages/" + from + "/");


            myFirebaseRef.child("dateTime").setValue(formattedDate);
            myFirebaseRef.child("body").setValue(body);
            myFirebaseRef.child("from").setValue(from);
            //myFirebaseRef.child("")

            Message msg = new Message(from, body, formattedDate, true);
            Log.d("MainActivity", "from : " + from);
            Log.d("MainActivity", "body : " + body);
            Log.d("MainActivity", "receivedDate : " + formattedDate);
        }
        else
        {
            Log.d("MainActivity", "Fowarding is NOT on.");
        }
    }

    private SmsMessage GetMessage(Intent intent)
    {
        SmsMessage message = null;
        if (Build.VERSION.SDK_INT >= 19) {
            Log.d("MainActivity", "I'm a NEWER API LEVEL : " + Build.VERSION.SDK_INT);
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            message = msgs[0];
        } else {
            Log.d("MainActivity", "I'm an older API LEVEL : " + Build.VERSION.SDK_INT);
            Bundle bundle = intent.getExtras();
            Object pdus[] = (Object[]) bundle.get("pdus");
            message = SmsMessage.createFromPdu((byte[]) pdus[0]);
        }
        return message;
    }

    public void saveNumber(String number, Context context) {
        TxtFwdDatabaseHelper dbh = new TxtFwdDatabaseHelper(context);

        Log.d("MainActivity", "Saving number to database...");
        SQLiteDatabase db = dbh.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TxtFwdNumberDatabaseContract.TxtFwdTableDef.COLUMN_NAME_CONTACT_NUMBER, number);
        values.put(TxtFwdNumberDatabaseContract.TxtFwdTableDef.COLUMN_NAME_FILTER_ON, 0);
        long newRowId = db.insert(
                TxtFwdNumberDatabaseContract.TxtFwdTableDef.TABLE_NAME,
                null,
                values);
        Log.d("MainActivity", "newRowId : " + String.valueOf(newRowId));
    }
}

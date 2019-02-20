package us.raddev.txtfwd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by roger.deutsch on 4/4/2016.
 */
public class TxtFwdDatabaseHelper extends SQLiteOpenHelper{
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "txtfwd.db";

    public TxtFwdDatabaseHelper(Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MainActivity", "creating DB...");
        db.execSQL(TxtFwdNumberDatabaseContract.SQL_CREATE_TXTFWD_NUMBER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

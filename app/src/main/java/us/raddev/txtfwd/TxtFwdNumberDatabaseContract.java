package us.raddev.txtfwd;

import android.provider.BaseColumns;

/**
 * Created by roger.deutsch on 4/4/2016.
 */
public class TxtFwdNumberDatabaseContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TXTFWD_NUMBER =
            "CREATE TABLE " + TxtFwdTableDef.TABLE_NAME + " (" +
                    TxtFwdTableDef._ID + " INTEGER PRIMARY KEY," +
                    TxtFwdTableDef.COLUMN_NAME_CONTACT_NUMBER + TEXT_TYPE + " UNIQUE, " +
                    TxtFwdTableDef.COLUMN_NAME_FILTER_ON + INT_TYPE +   " )";


    /* inner classes define tables */
    public static abstract class TxtFwdTableDef implements BaseColumns {
        public static final String TABLE_NAME = "capturedNumber";
        public static final String COLUMN_NAME_CONTACT_NUMBER = "contactNumber";
        public static final String COLUMN_NAME_FILTER_ON = "isFilterOn";
    }
}

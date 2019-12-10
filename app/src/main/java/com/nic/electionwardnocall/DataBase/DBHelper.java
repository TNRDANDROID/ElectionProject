package com.nic.electionwardnocall.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ElectionWardNoCall";
    private static final int DATABASE_VERSION = 1;


    public static final String RO_USER_TABLE_NAME = "RO_UserTable";
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE " + RO_USER_TABLE_NAME + " ("
                + "district_code INTEGER," +
                "district_name TEXT," +
                "localbody_no INTEGER," +
                "localbody_name TEXT," +
                "ro_user_name TEXT," +
                "ro_mobile_no TEXT," +
                "localbody_type TEXT," +
                "localbody_abbr TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + RO_USER_TABLE_NAME);
            onCreate(db);
        }
    }


}

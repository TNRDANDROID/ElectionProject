package com.nic.electionwardnocall.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.util.Log;

import com.nic.electionwardnocall.constant.AppConstant;
import com.nic.electionwardnocall.pojo.ElectionWardNoCall;

import java.util.ArrayList;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public dbData(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteReadOnlyDatabaseException e) {

        }

    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }



    /****** ROUser TABLE *****/
    public ElectionWardNoCall insertROUserDetails(ElectionWardNoCall odfMonitoringListValue) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.RO_DISTRICT_CODE, odfMonitoringListValue.getDistictCode());
        values.put(AppConstant.RO_DISTRICT_NAME, odfMonitoringListValue.getDistrictName());
        values.put(AppConstant.LOCALBODY_NO, odfMonitoringListValue.getLocalBodyNo());
        values.put(AppConstant.LOCALBODY_NAME, odfMonitoringListValue.getLocalBodyName());
        values.put(AppConstant.RO_MOBILE_NO, odfMonitoringListValue.getRoMobileNo());
        values.put(AppConstant.LOCALBODY_TYPE, odfMonitoringListValue.getLocalBodyType());
        values.put(AppConstant.LOCALBODY_ABBR, odfMonitoringListValue.getLocalBodyAbbr());

        long id = db.insert(DBHelper.RO_USER_TABLE_NAME, null, values);
        Log.d("Inserted_id_ROUSer", ""+id);

        return odfMonitoringListValue;
    }

    public ArrayList<ElectionWardNoCall> getAll_ROUSerDetails() {

        ArrayList<ElectionWardNoCall> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from " + DBHelper.RO_USER_TABLE_NAME, null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ElectionWardNoCall card = new ElectionWardNoCall();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.RO_DISTRICT_CODE)));
                    card.setDistrictName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.RO_DISTRICT_NAME)));
                    card.setLocalBodyNo(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LOCALBODY_NO)));
                    card.setLocalBodyName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LOCALBODY_NAME)));
                    card.setRoUserName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.RO_USER_NAME)));
                    card.setRoMobileNo(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.RO_MOBILE_NO)));
                    card.setLocalBodyType(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LOCALBODY_TYPE)));
                    card.setLocalBodyAbbr(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LOCALBODY_ABBR)));


                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public void deleteAllTables(){
        deleteROUserTable();
    }


    public void deleteROUserTable() {
        db.execSQL("delete from " + DBHelper.RO_USER_TABLE_NAME);
    }

}

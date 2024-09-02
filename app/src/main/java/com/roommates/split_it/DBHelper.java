package com.roommates.split_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "shareUp.db";
    private static final int DB_VERSION = 1 ;

    private static final String RECORDS_TABLE = "records";
    private static final String USERS_TABLE = "users";

    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_STATUS = "user_status";
    private static final String USER_SPEND = "user_spend";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + USERS_TABLE +" ("
            + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + USER_NAME + " TEXT, "
            + USER_SPEND + " INTEGER, "
            + USER_STATUS + " INTEGER );";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String ITEMS_NAME = "items_name";
    private static final String PRICE = "price";

    private static final String CREATE_TABLE = "CREATE TABLE " + RECORDS_TABLE + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + TYPE + " TEXT, "
            + ITEMS_NAME + " TEXT, "
            + PRICE + " INTEGER);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ RECORDS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ USERS_TABLE);
        onCreate(db);
    }


    public long insertUser(String name,int spend, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAME, name);
        contentValues.put(USER_STATUS, price);
        contentValues.put(USER_SPEND, spend);
        long result = db.insert(USERS_TABLE, null, contentValues);
        db.close();
        return result;
    }
    public long insertItem(String name, String type, String itemsName, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(TYPE, type);
        contentValues.put(ITEMS_NAME, itemsName);
        contentValues.put(PRICE, price);
        long result = db.insert(RECORDS_TABLE, null, contentValues);
        Log.e("intert Item : ", " "+name+", "+type+", "+itemsName+", "+price);
        db.close();
        return result;
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + RECORDS_TABLE + " ORDER BY " + ID ;
        return db.rawQuery(query, null);
    }
    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USERS_TABLE + " ORDER BY " + USER_ID ;
        return db.rawQuery(query,null);
    }



    public int getSpend_of_user(String user_name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + USER_SPEND + " FROM " + USERS_TABLE + " WHERE " + USER_NAME + " = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{user_name});

            if (cursor != null && cursor.moveToFirst()) {
                int spend_Index = cursor.getColumnIndex(USER_SPEND);
                if (spend_Index != -1) {
                    return cursor.getInt(spend_Index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return 0;
    }
    public int get_totalSpend_by_all_user() {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + USER_SPEND + " FROM " + USERS_TABLE ;
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int spend_Index = cursor.getColumnIndex(USER_SPEND);
                    if (spend_Index != -1) {
                        total += cursor.getInt(spend_Index);
                    }
                }while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return total;
    }
    public void updateUserStatus(String buyer_name, int spend){
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE " + USERS_TABLE +
                " SET " + USER_SPEND + " = " + USER_SPEND + " + " + spend +
                " WHERE " + USER_NAME + " = ?";

        db.execSQL(query, new String[]{buyer_name});
    }

}

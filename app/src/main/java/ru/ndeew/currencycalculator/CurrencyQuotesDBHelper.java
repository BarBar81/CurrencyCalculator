package ru.ndeew.currencycalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class CurrencyQuotesDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "currency_quotes"; // Имя базы данных
    public static final int DB_VERSION = 1; // Версия базы данных

    public CurrencyQuotesDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE APP_SETTNGS ("
                    + "VERSION INTEGER PRIMARY KEY, "
                    + "QUOTE_URL TEXT, "
                    + "UPD_PERIOD INTEGER);");
            ContentValues settings = new ContentValues();
            settings.put("VERSION", DB_VERSION);
            settings.put("UPD_PERIOD", 0);
            settings.put("QUOTE_URL", "https://www.cbr-xml-daily.ru/daily_json.js");
            db.insert("APP_SETTNGS", null, settings);
            db.execSQL("CREATE TABLE QUOTES ("
                    + "id INTEGER PRIMARY KEY, "
                    + "JSON_TEXT TEXT);");
        }
    }
}

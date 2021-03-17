package ru.ndeew.currencycalculator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Spinner spinerUpdatePeriod = (Spinner) findViewById(R.id.spUpdatePeriod);
        spinerUpdatePeriod.setSelection(Integer.parseInt(MainActivity.quotesUpdatePriod));
        Button btnCancel = (Button) findViewById(R.id.btnSettingsCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.UPDATE_FLAG_NAME, false);
                startActivity(intent);
                finishActivity(R.layout.activity_settings);
            }
        });
        Button btnSave = (Button) findViewById(R.id.btnSettingsSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSettings();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.UPDATE_FLAG_NAME, true);
                startActivity(intent);
                finishActivity(R.layout.activity_settings);
            }
        });
    }

    protected void SaveSettings() {
        Spinner spinerUpdatePeriod = (Spinner) findViewById(R.id.spUpdatePeriod);
        String temp = MainActivity.quotesUpdatePriod;
        MainActivity.quotesUpdatePriod = Integer.toString(spinerUpdatePeriod.getSelectedItemPosition());
        try {
            SQLiteDatabase db = MainActivity.currencyQuotesDBHelper.getWritableDatabase();
            ContentValues settingValues = new ContentValues();
            settingValues.put("UPD_PERIOD", MainActivity.quotesUpdatePriod);
            db.update("APP_SETTNGS",
                    settingValues,
                    "VERSION = ?",
                    new String[]{Integer.toString(CurrencyQuotesDBHelper.DB_VERSION)});
            db.close();
        } catch(SQLiteException e) {
            MainActivity.quotesUpdatePriod = temp;
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

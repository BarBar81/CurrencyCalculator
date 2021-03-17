package ru.ndeew.currencycalculator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String UPDATE_FLAG_NAME = "UPDATE_FLAG";
    private static List<CurrencyQuote> CurrencyQuoteList;
    public static String quotesJsonUrl;
    public static String quotesUpdatePriod = "0";
    public static CurrencyQuotesDBHelper currencyQuotesDBHelper = null;

    boolean flagUpdateRunning = false;
    public static Handler updateHandler = new Handler();
    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            GetData(1);

            if (quotesUpdatePriod.equals("0")) {
                updateHandler.removeCallbacksAndMessages(null);
                return;
            }

            long period = 0;
            switch (quotesUpdatePriod) {
                case "1": // 1 minute
                    period = 1000 * 60;
                    break;
                case "2": // 10 minutes
                    period = 1000 * 60 * 10;
                    break;
                case "3": // 30 minutes
                    period = 1000 * 60 * 30;
                    break;
                case "4": // 1 hour
                    period = 1000 * 60 * 60;
                    break;
                case "5": // 3 hours
                    period = 1000 * 60 * 60 * 3;
                    break;
                default:
                    return;
            }
            while (flagUpdateRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateHandler.postDelayed(this, period);
        }
    };

    public static CurrencyQuote GetCurrencyQuote(long id) {
        return CurrencyQuoteList.get((int) id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currencyQuotesDBHelper == null)
            currencyQuotesDBHelper = new CurrencyQuotesDBHelper(this);
        LoadSettings();
        if (getIntent().hasExtra(UPDATE_FLAG_NAME)) {
            if (getIntent().getExtras().getBoolean(UPDATE_FLAG_NAME)) {
                updateHandler.removeCallbacksAndMessages(updateRunnable);
                updateHandler.postDelayed(updateRunnable, 0);
            } else {
                GetData(3);
            }
            getIntent().removeExtra(UPDATE_FLAG_NAME);
        } else {
            updateHandler.removeCallbacksAndMessages(updateRunnable);
            updateHandler.postDelayed(updateRunnable, 0);
        }
        setContentView(R.layout.activity_main);
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> listView,
                                            View v,
                                            int position,
                                            long id) {
                        if (position >= 0) {
                            Intent intent = new Intent(MainActivity.this, CalcActivity.class);
                            intent.putExtra(CalcActivity.EXTRA_ID, (long) id);
                            startActivity(intent);
                        }
                    }
                };
        ListView listView = (ListView) findViewById(R.id.lwCurrencyQuotes);
        listView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.menu_update_now:
                GetData(2);
                return true;
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void LoadSettings() {
        quotesJsonUrl = null;
        quotesUpdatePriod = null;
        try {
            SQLiteDatabase db = currencyQuotesDBHelper.getReadableDatabase();
            Cursor cursor = db.query ("APP_SETTNGS",
                    new String[] {"QUOTE_URL", "UPD_PERIOD"},
                    "VERSION = ?",
                    new String[] {Integer.toString(CurrencyQuotesDBHelper.DB_VERSION)},
                    null, null,null);
             //Переход к первой записи в курсоре
            if (cursor.moveToFirst()) {
                //Получение URL
                quotesJsonUrl = cursor.getString(0);
                quotesUpdatePriod = cursor.getString(1);
            }
            cursor.close();
            db.close();
        } catch(SQLiteException e) {
            quotesJsonUrl = null;
            quotesUpdatePriod = null;
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void GetData(int loadMode) {
        // loadMode: 1 - URL&DB, 2 - URL, 3 - DB
        flagUpdateRunning = true;
        try {
            new GetDataSync(loadMode) {
                public void onPostExecute() {
                    CurrencyQuoteList = getCurrencyQuoteList();

                    ListView listView = (ListView) findViewById(R.id.lwCurrencyQuotes);
                    listView.setAdapter(null);
                    listView.invalidateViews();

                    if (CurrencyQuoteList != null) {
                        CurrencyQuotesAdapter adapter = new CurrencyQuotesAdapter(MainActivity.this, CurrencyQuoteList);
                        listView.setAdapter(adapter);
                    }
                }
            }.execute();
        } finally {
            flagUpdateRunning = false;
        }
    }

}

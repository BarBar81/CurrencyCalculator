package ru.ndeew.currencycalculator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GetDataSync extends AsyncTask<Void, Void, Void> {

    public static final String TAG = "GetDataSync";
    protected JSONObject JsonRoot;
    protected int LoadMode;

    public GetDataSync(int loadMode) {
        super();
        // loadMode: 1 - URL&DB, 2 - URL, 3 - DB
        LoadMode = loadMode;
    }

    @Override
    protected Void doInBackground(Void... params) {
        getData();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.onPostExecute();
    }

    public abstract void onPostExecute();

    public List<CurrencyQuote> getCurrencyQuoteList() {
        if (JsonRoot == null) return null;

        List<CurrencyQuote> cqList = new ArrayList<CurrencyQuote>();

        try {
            String cqCharCode, cqName, cqNominal, cqValue;
            JSONObject valuteJSONObj = JsonRoot.getJSONObject("Valute");
            Iterator keys = valuteJSONObj.keys();
            while(keys.hasNext()) {
                String key = (String)keys.next();

                JSONObject dummyObj = valuteJSONObj.getJSONObject(key);

                cqCharCode = dummyObj.getString("CharCode");
                cqName = dummyObj.getString("Name");
                cqNominal = dummyObj.getString("Nominal");
                cqValue = dummyObj.getString("Value");

                // Create an entry objects (entryObj)
                CurrencyQuote cqObj = new CurrencyQuote();
                cqObj.setCharCode(cqCharCode);
                cqObj.setName(cqName);
                try {
                    cqObj.setNominal(Integer.parseInt(cqNominal));
                } catch (NumberFormatException e) {
                    //
                }
                try {
                    cqObj.setValue(Double.parseDouble(cqValue));
                } catch (NumberFormatException e) {
                    //
                }
                // Add the entry object to the list
                cqList.add(cqObj);
            }

            return cqList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getData() {
        if (LoadMode == 1 || LoadMode == 2)
            try {
                JsonRoot = readJsonFromUrl(MainActivity.quotesJsonUrl);
                if (!JsonRoot.toString().isEmpty())
                    try {
                        SQLiteDatabase db = MainActivity.currencyQuotesDBHelper.getWritableDatabase();
                        db.delete("QUOTES", null, null);
                        ContentValues quoteValues = new ContentValues();
                        quoteValues.put("id", 1);
                        quoteValues.put("JSON_TEXT", JsonRoot.toString());
                        db.insert("QUOTES", null, quoteValues);
                        db.close();
                    } catch(SQLiteException e) {
                        Log.e(TAG,"Database error: cannot update table QUOTES", e);
                    }

            } catch (IOException e) {
                Log.e(TAG, "Error IOException", e);
            } catch (JSONException e) {
                Log.e(TAG, "Error JSONException", e);
            }
        if ((JsonRoot == null && LoadMode == 1) || LoadMode == 3)
            try {
                JsonRoot = readJsonFromDB();
            } catch (JSONException e) {
                Log.e(TAG, "Error JSONException", e);
            }
    }

    private JSONObject readJsonFromDB() throws JSONException {
        JSONObject json = null; //new JSONObject();
        try {
            SQLiteDatabase db = MainActivity.currencyQuotesDBHelper.getReadableDatabase();
            Cursor cursor = db.query ("QUOTES",
                    new String[] {"JSON_TEXT"},
                    null,
                    null,
                    null, null,null);
            if (cursor.moveToFirst()) {
                String jsonText = cursor.getString(0);
                if (!jsonText.isEmpty()) {
                    json = new JSONObject(jsonText);
                }
            }
            cursor.close();
            db.close();
        } catch(SQLiteException e) {
            Log.e(TAG,"Database error: cannot update table QUOTES", e);
        }
        return json;
    }

    private String readAll(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int CharP;
        while ((CharP = reader.read()) != -1) {
            stringBuilder.append((char) CharP);
        }
        return stringBuilder.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = null;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            if (is != null) is.close();
        }
    }

}

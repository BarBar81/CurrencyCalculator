package ru.ndeew.currencycalculator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CurrencyQuotesAdapter extends BaseAdapter {

    protected Context _context;
    List<CurrencyQuote> currencyQuoteList;

    public CurrencyQuotesAdapter(Context context, List<CurrencyQuote> cqList){
        _context = context;
        currencyQuoteList = cqList;
    }

    @Override
    public int getCount() {
        return currencyQuoteList.size();
    }

    @Override
    public Object getItem(int position) {
        return currencyQuoteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.adapter_currency_quotes, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView desc = (TextView) convertView.findViewById(R.id.descTextView);

        CurrencyQuote currencyQuote = currencyQuoteList.get(position);
        title.setText(currencyQuote.getCharCode() + " " + currencyQuote.getName());
        desc.setText(currencyQuote.getNominal() + "/" + currencyQuote.getValue());

        return convertView;
    }
}

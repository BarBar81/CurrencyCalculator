package ru.ndeew.currencycalculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class NumberTextWatcher<T> implements TextWatcher {

    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private EditText et;

    public NumberTextWatcher(T target)
    {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(' ');
        df = new DecimalFormat("#,##0.00", decimalFormatSymbols);
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,##0", decimalFormatSymbols);
        this.et = (EditText) target;
        hasFractionalPart = false;
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        et.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            if(!v.isEmpty()) {
                Number n = df.parse(v);
                int cp = et.getSelectionStart();
                if (hasFractionalPart) {
                    et.setText(df.format(n));
                } else {
                    et.setText(dfnd.format(n));
                }
                endlen = et.getText().length();
                int sel = (cp + (endlen - inilen));
                if (sel > 0 && sel <= et.getText().length()) {
                    et.setSelection(sel);
                } else {
                    // place cursor at the end?
                    et.setSelection(et.getText().length() - 1);
                }
            }
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

        et.addTextChangedListener(this);
        this.onTextChanged((T) et, s);
    }

    public abstract void onTextChanged(T target, Editable s);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
        {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
    }

}
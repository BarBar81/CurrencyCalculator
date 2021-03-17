package ru.ndeew.currencycalculator;

import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CalcActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "ID";

    protected long ID;
    protected CurrencyQuote currencyQuote;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        if ((ID = getIntent().getExtras().getLong(EXTRA_ID)) == 0L && savedInstanceState != null) {
            ID = savedInstanceState.getLong(EXTRA_ID);
        }
        currencyQuote = MainActivity.GetCurrencyQuote(ID);
        TextView textView = (TextView) findViewById(R.id.twCounterCurrency);
        textView.setText(currencyQuote.getCharCode() + " " + currencyQuote.getNominal() + "/" + currencyQuote.getValue());
        EditText editText = (EditText) findViewById(R.id.etRubVolume);
        editText.addTextChangedListener(new NumberTextWatcher<EditText>(editText){
            public void onTextChanged(EditText target, Editable s) {
                CalcRubVolume(s);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(EXTRA_ID, ID);
    }

    private void CalcRubVolume(Editable s) {
        TextView textView = (TextView) findViewById(R.id.twCounterCurrencyVolume);
        EditText editText = (EditText) findViewById(R.id.etRubVolume);
        if(editText.getText().toString().isEmpty()) {
            textView.setText("");
            textView.invalidate();
            textView.requestLayout();
            return;
        }
        try {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormatSymbols.setGroupingSeparator(' ');
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
            Double CounterCurrencyVolume;
            CounterCurrencyVolume = (Double.parseDouble(editText.getText().toString().replace(" ", "")) * currencyQuote.getNominal()) / currencyQuote.getValue();
            textView.setText(decimalFormat.format(CounterCurrencyVolume));
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(this, "Ошибка преобразования числа: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }

    }

}

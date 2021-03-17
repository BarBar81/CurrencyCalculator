package ru.ndeew.currencycalculator;

public class CurrencyQuote {
    private String CharCode;
    private Integer Nominal;
    private String Name;
    private Double Value;


    public String getCharCode() {
        return CharCode;
    }

    public void setCharCode(String charCode) {
        CharCode = charCode;
    }

    public Integer getNominal() {
        return Nominal;
    }

    public void setNominal(Integer nominal) {
        Nominal = nominal;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getValue() {
        return Value;
    }

    public void setValue(Double value) {
        Value = value;
    }
}

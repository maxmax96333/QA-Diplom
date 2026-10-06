package ru.netology.diploma.data;

public class CardData {
    private final String number;
    private final String month;
    private final String year;
    private final String holder;
    private final String cvc;

    public CardData(String number, String month, String year, String holder, String cvc) {
        this.number = number;
        this.month = month;
        this.year = year;
        this.holder = holder;
        this.cvc = cvc;
    }

    public String getNumber() {
        return number;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getHolder() {
        return holder;
    }

    public String getCvc() {
        return cvc;
    }
}

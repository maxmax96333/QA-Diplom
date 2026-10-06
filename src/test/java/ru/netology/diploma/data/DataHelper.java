package ru.netology.diploma.data;

public class DataHelper {
    private DataHelper() {
    }

    public static CardData approvedCard() {
        return new CardData("4444 4444 4444 4441", "12", "30", "IVAN IVANOV", "123");
    }

    public static CardData declinedCard() {
        return new CardData("4444 4444 4444 4442", "12", "30", "IVAN IVANOV", "123");
    }
}

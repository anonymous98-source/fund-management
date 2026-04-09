package fundsmanager.util;

import fundsmanager.model.CurrencyItem;

public class CurrencyContext {

    private static CurrencyItem selectedCurrency;

    public static CurrencyItem get() {
        return selectedCurrency;
    }

    public static void set(CurrencyItem currency) {
        selectedCurrency = currency;
    }
}

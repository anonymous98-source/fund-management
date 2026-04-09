package fundsmanager.util;

import fundsmanager.model.CurrencyItem;

import java.util.*;

public class CurrencyProvider {

    public static List<CurrencyItem> loadAll() {

        Map<String, CurrencyItem> map = new HashMap<>();

        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                if (currency != null && !locale.getCountry().isEmpty()) {
                    map.putIfAbsent(
                            currency.getCurrencyCode(),
                            new CurrencyItem(currency, locale)
                    );
                }
            } catch (Exception ignored) {
            }
        }

        List<CurrencyItem> list = new ArrayList<>(map.values());
        list.sort(Comparator.comparing(CurrencyItem::getCode));
        return list;
    }
}

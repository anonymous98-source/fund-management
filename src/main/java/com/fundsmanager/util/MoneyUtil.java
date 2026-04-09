package fundsmanager.util;

import java.text.NumberFormat;


public class MoneyUtil {

    public static String format(double amount) {
        if (CurrencyContext.get() == null) {
            return String.format("%.2f", amount);
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setCurrency(CurrencyContext.get().getCurrency());
        return nf.format(amount);
    }
}

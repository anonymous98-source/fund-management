package fundsmanager.util;


import fundsmanager.model.Category;
import fundsmanager.model.GroupType;

import java.util.List;

public class DefaultCategories {

    public static List<Category> load() {
        return List.of(
                new Category("Necessary Expenses", 40, true, GroupType.EXPENSE),
                new Category("Emergency Fund", 10, true, GroupType.SAVING),
                new Category("Health Emergency", 10, true, GroupType.SAVING),
                new Category("EMIs", 15, true, GroupType.EXPENSE),
                new Category("Savings", 10, false, GroupType.SAVING),
                new Category("Investments", 10, false, GroupType.SAVING),
                new Category("Miscellaneous", 5, false, GroupType.EXPENSE)
        );
    }
}

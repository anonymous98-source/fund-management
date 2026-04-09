package fundsmanager.service;

import fundsmanager.model.Category;
import fundsmanager.model.FundAllocation;
import fundsmanager.model.GroupSummary;
import fundsmanager.model.GroupType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FundCalculationService {

    public List<FundAllocation> calculate(double income, List<Category> categories) {

        List<FundAllocation> result = new ArrayList<>();

        for (Category c : categories) {
            double amount = income * c.getPercentage() / 100;
            result.add(new FundAllocation(
                    c.getName(),
                    c.getPercentage(),
                    amount
            ));
        }
        return result;
    }

    public List<GroupSummary> calculateGroupSummary(
            double income,
            List<Category> categories
    ) {

        Map<GroupType, Double> percentMap = new EnumMap<>(GroupType.class);
        Map<GroupType, Double> amountMap = new EnumMap<>(GroupType.class);

        for (GroupType gt : GroupType.values()) {
            percentMap.put(gt, 0.0);
            amountMap.put(gt, 0.0);
        }

        for (Category c : categories) {
            double percent = c.getPercentage();
            double amount = income * percent / 100;

            percentMap.put(
                    c.getGroupType(),
                    percentMap.get(c.getGroupType()) + percent
            );

            amountMap.put(
                    c.getGroupType(),
                    amountMap.get(c.getGroupType()) + amount
            );
        }

        List<GroupSummary> result = new ArrayList<>();
        for (GroupType gt : GroupType.values()) {
            result.add(new GroupSummary(
                    gt,
                    round(percentMap.get(gt)),
                    round(amountMap.get(gt))
            ));
        }

        return result;
    }

    public boolean validatePercentage(List<Category> categories) {
        double sum = categories.stream()
                .mapToDouble(Category::getPercentage)
                .sum();
        return Math.abs(sum - 100.0) < 0.01;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}



package fundsmanager.model;

public class FundAllocation {
    private String category;
    private double percentage;
    private double amount;

    public FundAllocation(String category, double percentage, double amount) {
        this.category = category;
        this.percentage = percentage;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public double getPercentage() {
        return percentage;
    }

    public double getAmount() {
        return amount;
    }
}

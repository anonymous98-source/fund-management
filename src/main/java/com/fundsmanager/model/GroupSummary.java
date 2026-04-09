package fundsmanager.model;

public class GroupSummary {

    private final GroupType groupType;
    private final double totalPercentage;
    private final double totalAmount;

    public GroupSummary(GroupType groupType, double totalPercentage, double totalAmount) {
        this.groupType = groupType;
        this.totalPercentage = totalPercentage;
        this.totalAmount = totalAmount;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public double getTotalPercentage() {
        return totalPercentage;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}

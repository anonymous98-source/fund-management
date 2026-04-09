package fundsmanager.model;

public class Category {

    private String name;
    private double percentage;
    private boolean locked;
    private GroupType groupType; // 🔥 NEW

    public Category(String name, double percentage, boolean locked, GroupType groupType) {
        this.name = name;
        this.percentage = percentage;
        this.locked = locked;
        this.groupType = groupType;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isLocked() { return locked; }

    public GroupType getGroupType() { return groupType; }
    public void setGroupType(GroupType groupType) { this.groupType = groupType; }
}

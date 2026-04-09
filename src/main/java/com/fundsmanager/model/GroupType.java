package fundsmanager.model;

public enum GroupType {
    EXPENSE("Expenses"),
    SAVING("Savings");

    private final String displayName;

    GroupType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

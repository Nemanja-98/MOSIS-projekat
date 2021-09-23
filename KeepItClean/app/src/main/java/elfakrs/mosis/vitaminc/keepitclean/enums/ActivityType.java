package elfakrs.mosis.vitaminc.keepitclean.enums;

public enum ActivityType {
    GRAFFITI_CLEANING("Graffiti cleaning", 200),
    PARK_CLEANING("Park cleaning", 400),
    STATUE_CLEANING("Statue cleaning", 150),
    STREET_CLEANING("Street cleaning", 100)
    ;

    public final String type;
    public final int points;

    ActivityType(String type, int points) {
        this.type = type;
        this.points = points;
    }
}

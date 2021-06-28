package elfakrs.mosis.vitaminc.vitatake.enumerations;

public enum NutrientValues {
    CALCIUM_1_70_MAN(1000, 1),
    CALCIUM_71_PLUS_MAN(1200, 1),
    CALCIUM_1_50_WOMAN(1000, 1),
    CALCIUM_51_PLUS_WOMAN(1200, 1),
    CHOLINE_MAN(550, 1),
    CHOLINE_WOMAN(425, 1),
    FIBER_MAN(34000, 1),
    FIBER_WOMAN(23000, 1),
    MAGNESIUM_MAN(420, 1),
    MAGNESIUM_WOMAN(320, 1),
    POTASSIUM_ADULT(4200, 1),
    SELENIUM_ADULT(400, 3),
    SODIUM_ADULT(2300, 1),
    SUGAR_ADULT(24, 1),
    ZINK_MAN(11000, 3),
    ZINK_WOMAN(8000, 3);

    public final int label;
    public final int magnitude;

    private NutrientValues(int label, int magnitude)
    {
        this.label = label;
        this.magnitude = magnitude;
    }

}

package elfakrs.mosis.vitaminc.vitatake.enumerations;

public enum VitaminValues {
    VITAMIN_A_MAN(3000,1),
    VITAMIN_A_WOMAN(2330,1),
    VITAMIN_C_ADULT(75520,1),
    VITAMIN_D_ADULT(1500,1),
    VITAMIN_E_4_8(350,1),
    VITAMIN_E_9_13(700,1),
    VITAMIN_E_14_18(1000,1),
    VITAMIN_E_19_PLUS(1300,1),
    VITAMIN_K_MAN(164, 1),
    VITAMIN_K_WOMAN(182,1),
    VITAMIN_B1_9_13(900,3),
    VITAMIN_B1_14_PLUS_MAN(1200, 3),
    VITAMIN_B1_14_PLUS_WOMAN(1000,3),
    VITAMIN_B2_MAN(1300, 3),
    VITAMIN_B2_WOMAN(1100, 3),
    VITAMIN_B3_MAN(16000, 3),
    VITAMIN_B3_WOMAN(14000, 3),
    VITAMIN_B5_ADULT(5000, 3),
    VITAMIN_B6_9_13(60000 ,3),
    VITAMIN_B6_14_18(80000 ,3),
    VITAMIN_B6_18_PLUS(100000 ,3),
    VITAMIN_B9_ADULT(400, 1),
    VITAMIN_B12_ADULT(2400, 3);

    public final int value;
    public final int magnitude;

    private VitaminValues(int value, int magnitude)
    {
        this.value = value;
        this.magnitude = magnitude;
    }
}

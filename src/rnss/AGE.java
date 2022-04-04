package rnss;

/**
 * AGE is enum class for Age category
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public enum AGE {
    AGE_20_29("20-29"),
    AGE_30_39("30-39"),
    AGE_40_49("40-49"),
    AGE_50_59("50-59");

    private final String group;

    AGE(String group) {
        this.group = group;
    }

    static AGE getType(String ageGroup) {
        if (ageGroup != null) {
            for (AGE age : AGE.values()) {
                if (ageGroup.equalsIgnoreCase(age.group)) {
                    return age;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.group;
    }
}
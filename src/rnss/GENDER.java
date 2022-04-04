package rnss;

/**
 * GENDER is enum class for GENDER category
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public enum GENDER {
    MALE("Male"),
    FEMALE("Female");

    private final String name;

    GENDER(String name) {
        this.name = name;
    }

    static GENDER getType(String genderType) {
        if (genderType != null) {
            for (GENDER gender : GENDER.values()) {
                if (genderType.equalsIgnoreCase(gender.name)) {
                    return gender;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

enum UserRole {
    REPORTER("Reporter"),
    SERVICE_PROVIDER("Service Provider");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    static UserRole getType(String roleType) {
        if (roleType != null) {
            for (UserRole userRole : UserRole.values()) {
                if (roleType.equalsIgnoreCase(userRole.role)) {
                    return userRole;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.role;
    }
}

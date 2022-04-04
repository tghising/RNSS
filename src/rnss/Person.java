package rnss;

import java.io.Serializable;

/**
 * Person: serializable abstract class-super class to record person details
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 3219284088827818900L;
    protected GENDER gender;
    protected AGE ageCategory;

    // default constructor
    public Person() {

    }

    // parameterized constructor
    public Person(AGE ageCategory, GENDER gender) {
        this.ageCategory = ageCategory;
        this.gender = gender;
    }
}

package rnss;

import java.io.Serializable;

/**
 * ResidenceNeedful: A class extends serializable class Person for Residence
 * Need Solution System (RNSS) recording
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class ResidenceNeedful extends Person implements Serializable {

    private static final long serialVersionUID = 5655965646314105741L;
    private int clientId; //the client Id for reading data from the database
    private String fullName; //the name of the residence needful person
    private String currentResidenceStatus; //the current residence status
    private String disabled; //the disabled or not description
    private String chronicAtEntry; //the chronic at entry description
    private String skills; //the skills description of residence needful person
    private double income; //the income of residence needful person
    private String incomeSource; //the income source of residence needful person
    private int numberOfChildrenAccompanied; //the number of children accompanied by residence needful person

    // default constructor
    public ResidenceNeedful() {
        super();
    }

    // copy constructor
    public ResidenceNeedful(String fullName) {
        this.fullName = fullName;
    }

    // constructor to initialize the AGE
    public ResidenceNeedful(AGE age, GENDER gender) {
        super(age, gender);
    }

    // constructor to initialize the residence needful person fullname, age category, current residence status, gender, disabled or not, chronic at entry, skills, income, income source and children accompanied
    public ResidenceNeedful(String fullName, AGE ageCategory, String residenceStatus, GENDER gender, String disabled, String chronic, String skills, double income, String incomeSource, int numberOfChildrenAccompanied) {
//    public ResidenceNeedful(int clientId, String fullName, AGE ageCategory, String residenceStatus, GENDER gender, String disabled, String chronic, String skills, double income, String incomeSource, int numberOfChildrenAccompanied) {
        super.ageCategory = ageCategory;
        super.gender = gender;
        this.fullName = fullName;
        this.currentResidenceStatus = residenceStatus;
        this.disabled = disabled;
        this.chronicAtEntry = chronic;
        this.skills = skills;
        if (income < 0.0) {
            throw new IllegalArgumentException("Income must be greater than 0.0");
        }
        this.income = income;
        this.incomeSource = incomeSource;
        this.numberOfChildrenAccompanied = numberOfChildrenAccompanied;
    }

    // copy constructor
    public ResidenceNeedful(ResidenceNeedful another) {
        this(another.fullName, another.ageCategory, another.currentResidenceStatus, another.gender, another.disabled, another.chronicAtEntry, another.skills, another.income, another.incomeSource, another.numberOfChildrenAccompanied);
}

    // setters method
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public void setCurrentResidenceStatus(String currentResidenceStatus) {
        this.currentResidenceStatus = currentResidenceStatus;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setChronicAtEntry(String chronicAtEntry) {
        this.chronicAtEntry = chronicAtEntry;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    public void setNumberOfChildrenAccompanied(int numberOfChildrenAccompanied) {
        this.numberOfChildrenAccompanied = numberOfChildrenAccompanied;
    }

    // getters method
    public int getClientId() {
        return clientId;
    }

    public String getFullName() {
        return fullName;
    }

    public AGE getAgeCategory() {
        return super.ageCategory;
    }

    public GENDER getGender() {
        return super.gender;
    }

    public String getCurrentResidenceStatus() {
        return currentResidenceStatus;
    }

    public String getDisabled() {
        return disabled;
    }

    public String getChronicAtEntry() {
        return chronicAtEntry;
    }

    public String getSkills() {
        return skills;
    }

    public double getIncome() {
        return income;
    }

    public String getIncomeSource() {
        return incomeSource;
    }

    public int getNumberOfChildrenAccompanied() {
        return numberOfChildrenAccompanied;
    }

    @Override
    public String toString() {
        return String.format("ClientId: %d \tName:%s \tAge Category:%s \tCurrent residence status:%s \tGender:%s \tDisabled:%s "
                + "\tChronic at entry:%s \tSkills:%s \tIncome:%.2f \tIncome source:%s \tNumber of children accompanied:%d\t",
                this.clientId, this.fullName, super.ageCategory, this.currentResidenceStatus, super.gender, this.disabled, this.chronicAtEntry,
                this.skills, this.income, this.incomeSource, this.numberOfChildrenAccompanied);
    }
}

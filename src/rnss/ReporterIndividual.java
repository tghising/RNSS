package rnss;

import java.io.Serializable;

/**
 * ReporterIndividual: A class implements serializable for individual reporter
 * in the Residence Need Solution System (RNSS)
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class ReporterIndividual implements Serializable {

    private static final long serialVersionUID = 3381967745218620683L;
    protected int reporterId;
    protected String fullName;
    protected String email;
    protected String password;
    protected String phoneNumber;
    protected String postcode;
    protected byte[] encryptPassword;

    // default constructor
    public ReporterIndividual() {
    }

    // constructor to initialize
    public ReporterIndividual(String reporterName, String reporterEmail, String reporterPassword, String reporterPhoneNumber, String reporterPostcode) {
        this.fullName = reporterName;
        this.email = reporterEmail;
        this.password = reporterPassword;
        this.phoneNumber = reporterPhoneNumber;
        this.postcode = reporterPostcode;
    }

    //setters method
    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEncryptPassword(byte[] encryptPassword) {
        this.encryptPassword = encryptPassword;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    //getters method
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getReporterId() {
        return reporterId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getEncryptPassword() {
        return encryptPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public String toString() {
        return String.format("Reporter Id: %d \tFull Name:%s \tEmail:%s \tPhone Number:%s \tPostcode:%s",
                this.reporterId, this.fullName, this.email, this.phoneNumber, this.postcode);
    }
}

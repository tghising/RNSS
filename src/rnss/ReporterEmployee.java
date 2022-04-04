package rnss;

import java.io.Serializable;

/**
 * ReporterEmployee: A class extends ReporterIndividual and implements
 * Serializable for record of Employee reporter in the Residence Need Solution
 * System (RNSS)
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class ReporterEmployee extends ReporterIndividual implements Serializable {

    private static final long serialVersionUID = 1652585765178318763L;
    private int employeeId;
    private String organization;
    private String department;
    private String supervisorName;

    // default constructor
    public ReporterEmployee() {
        super();
    }

    // constructor to initialize
    public ReporterEmployee(String reporterName, String reporterEmail, String reporterPassword, String reporterPhoneNumber, String reporterPostcode,
            String organization, String department, String supervisorName) {
        super(reporterName, reporterEmail, reporterPassword, reporterPhoneNumber, reporterPostcode);
        this.organization = organization;
        this.department = department;
        this.supervisorName = supervisorName;
    }

    //setters method
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    //getters method
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getOrganization() {
        return organization;
    }

    public String getDepartment() {
        return department;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    @Override
    public String toString() {
        return String.format("Reporter Id: %d \tFull Name:%s \tEmail:%s \tPhone Number:%s \tPostcode:%s"
                + "\tEmployee Id: %d\tOrganization:%s \tDepartment:%s \tSupervisor:%s",
                super.reporterId, super.fullName, super.email, super.phoneNumber, super.postcode,
                this.employeeId, this.organization, this.department, this.supervisorName);
    }
}

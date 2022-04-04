package rnss;

import java.sql.*;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

/**
 * DatabaseUtility: This class is for implementing the database operations to
 * store the Client details received from the clients. This file has the
 * necessary methods to insert, and read entries from the MySQL database, named
 * “residenceneeds” in the Residence Need Solution System (RNSS)
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class DatabaseUtility {

    static final String DATABASE_NAME = "residenceneeds";
    static final String DATABASE_URL = "jdbc:mysql://localhost:3306";
    static final String USER_NAME = "root";
    static final String USER_PASSWORD = "root";

    private Connection connection; // Connection object creation
    private PreparedStatement createDatabase;
    private PreparedStatement createReporterIndividualTable;
    private PreparedStatement createReporterEmployeeTable;
    private PreparedStatement createResidenceNeedfulTable;
    private PreparedStatement findReporterByEmailId;
    private PreparedStatement findReporterEmployeeByReporterId;
    private PreparedStatement insertReporterIndividual;
    private PreparedStatement insertReporterEmployee;
    private PreparedStatement insertResidenceNeedful;
    private PreparedStatement selectAllResidenceNeedful;
    private PreparedStatement selectAllResidenceNeedfulByChronicAtEntry;
    private PreparedStatement loginReporterByEmailAndPassword;

    // constructor connect MySQL database, create database if not exist, create tables if not exist, insert records into tables and
    public DatabaseUtility() {

        try {
            // MySQL connection without specified database name
            connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, USER_PASSWORD);
            if (connection != null) {
                System.out.println("MySQL database has been connected .........");
                createDatabase = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME); // database creation if not exist
                createDatabase.executeUpdate();
                System.out.println(DATABASE_NAME + " database created successfully IF NOT EXISTS ...");
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed! " + e.getMessage());
            return;
        }

        try {
            // database connection or selection with the specified database name
            connection = DriverManager.getConnection(DATABASE_URL + "/" + DATABASE_NAME, USER_NAME, USER_PASSWORD);
            if (connection != null) {
                System.out.println(DATABASE_NAME + " database has been connected successfully ...");

                // reporter_individual table creation
                createReporterIndividualTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reporter_individual "
                        + "(reporter_id int NOT NULL AUTO_INCREMENT UNIQUE,"
                        + "full_name VARCHAR(100) NOT NULL,"
                        + "email VARCHAR(100) NOT NULL UNIQUE,"
                        + "phone_number VARCHAR(10) NOT NULL,"
                        + "postcode VARCHAR(4) NOT NULL,"
                        + "password VARCHAR(45) NOT NULL,"
                        + "PRIMARY KEY (reporter_id) )"
                );
                createReporterIndividualTable.executeUpdate();

                // reporter_employee table creation
                createReporterEmployeeTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reporter_employee "
                        + "(employee_id INT NOT NULL AUTO_INCREMENT UNIQUE,"
                        + "organization VARCHAR(100) NOT NULL,"
                        + "department VARCHAR(50) NOT NULL,"
                        + "supervisor_name VARCHAR(100) NOT NULL,"
                        + "reporter_id INT NOT NULL,"
                        + "PRIMARY KEY (employee_id),"
                        + "CONSTRAINT reporter_id FOREIGN KEY (reporter_id) REFERENCES reporter_individual (reporter_id) )"
                );
                createReporterEmployeeTable.executeUpdate();

                // residence_needful table creation
                createResidenceNeedfulTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS residence_needful "
                        + "(client_id int NOT NULL AUTO_INCREMENT UNIQUE,"
                        + "full_name VARCHAR(100) NOT NULL,"
                        + "current_residential_status VARCHAR(50) NOT NULL,"
                        + "disabled VARCHAR(15) NOT NULL,"
                        + "chronic_at_entry VARCHAR(50) NOT NULL,"
                        + "skills VARCHAR(255) DEFAULT NULL,"
                        + "income DOUBLE DEFAULT NULL,"
                        + "number_of_children_accompanied INT DEFAULT NULL,"
                        + "gender VARCHAR(6) NOT NULL,"
                        + "age_category VARCHAR(5) NOT NULL,"
                        + "income_source VARCHAR(15) DEFAULT NULL,"
                        + "PRIMARY KEY (client_id) )"
                );
                createResidenceNeedfulTable.executeUpdate();

                insertReporterIndividual = connection.prepareStatement("INSERT INTO reporter_individual(full_name, email, phone_number, postcode, password) "
                        + "VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                insertReporterEmployee = connection.prepareStatement("INSERT INTO reporter_employee(organization, department, supervisor_name, reporter_id) "
                        + "VALUES(?, ?, ?, ?)");
                insertResidenceNeedful = connection.prepareStatement("INSERT INTO residence_needful(full_name, current_residential_status, gender, age_category,"
                        + "disabled, chronic_at_entry, skills, income, income_source, number_of_children_accompanied) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                selectAllResidenceNeedful = connection.prepareStatement("SELECT * FROM residence_needful");
                selectAllResidenceNeedfulByChronicAtEntry = connection.prepareStatement("SELECT * FROM residence_needful WHERE chronic_at_entry = ?");
                findReporterByEmailId = connection.prepareStatement("SELECT * FROM reporter_individual WHERE email = ?");
                findReporterEmployeeByReporterId = connection.prepareStatement("SELECT * FROM reporter_employee WHERE reporter_id = ?");
                loginReporterByEmailAndPassword = connection.prepareStatement("SELECT * FROM reporter_individual WHERE email = ? AND password = ?");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            System.exit(1);
        }
    }

    // This method writes list of  ResidenceNeedful people to the database table
    public void addResidenceNeedfulPeople(List<ResidenceNeedful> people) {
        try {
            for (ResidenceNeedful person : people) {
                insertResidenceNeedful.setString(1, person.getFullName());
                insertResidenceNeedful.setString(2, person.getCurrentResidenceStatus());
                insertResidenceNeedful.setString(3, person.getGender().toString());
                insertResidenceNeedful.setString(4, person.getAgeCategory().toString());
                insertResidenceNeedful.setString(5, person.getDisabled());
                insertResidenceNeedful.setString(6, person.getChronicAtEntry());
                insertResidenceNeedful.setString(7, person.getSkills());
                insertResidenceNeedful.setDouble(8, person.getIncome());
                insertResidenceNeedful.setString(9, person.getIncomeSource());
                insertResidenceNeedful.setInt(10, person.getNumberOfChildrenAccompanied());

                insertResidenceNeedful.executeUpdate();  // execute the prepared statement insert
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    // This method return all the LinkedList of ResidenceNeedful from the database
    public LinkedList<ResidenceNeedful> getAllResidenceNeedfulPeople() {

        LinkedList<ResidenceNeedful> people = new LinkedList<>();
        try {
            ResultSet peopleResult = selectAllResidenceNeedful.executeQuery();
            System.out.println("Residence needful people details reading from the database.");
            while (peopleResult.next()) {
                int clientId = peopleResult.getInt("client_id");
                String fullName = peopleResult.getString("full_name");
                String currentResidenceStatus = peopleResult.getString("current_residential_status");
                String gender = peopleResult.getString("gender");
                String ageCategory = peopleResult.getString("age_category");
                String disabled = peopleResult.getString("disabled");
                String chronicAtEntry = peopleResult.getString("chronic_at_entry");
                String skills = peopleResult.getString("skills");
                double income = peopleResult.getDouble("income");
                String incomeSource = peopleResult.getString("income_source");
                int numberOfChildrenAccompanied = peopleResult.getInt("number_of_children_accompanied");

                ResidenceNeedful person = new ResidenceNeedful(fullName, AGE.getType(ageCategory), currentResidenceStatus, GENDER.getType(gender),
                        disabled, chronicAtEntry, skills, income, incomeSource, numberOfChildrenAccompanied);
                person.setClientId(clientId); // set client id

                people.add(person);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

        return people;
    }

    // This method return all the LinkedList of ResidenceNeedful which have the given chronic at entry from the binary file
    public LinkedList<ResidenceNeedful> getAllResidenceNeedfulByChronicAtEntry(String searchChronicAtEntry) {
        LinkedList<ResidenceNeedful> people = new LinkedList<>();
        try {
            // query to search all doors for the room with given roomId
            selectAllResidenceNeedfulByChronicAtEntry.setString(1, searchChronicAtEntry);
            ResultSet peopleResult = selectAllResidenceNeedfulByChronicAtEntry.executeQuery();
            System.out.println("Residence needful people details reading from the database with given chronic at entry:" + searchChronicAtEntry);
            while (peopleResult.next()) {
                int clientId = peopleResult.getInt("client_id");
                String fullName = peopleResult.getString("full_name");
                String currentResidenceStatus = peopleResult.getString("current_residential_status");
                String gender = peopleResult.getString("gender");
                String ageCategory = peopleResult.getString("age_category");
                String disabled = peopleResult.getString("disabled");
                String chronicAtEntry = peopleResult.getString("chronic_at_entry");
                String skills = peopleResult.getString("skills");
                double income = peopleResult.getDouble("income");
                String incomeSource = peopleResult.getString("income_source");
                int numberOfChildrenAccompanied = peopleResult.getInt("number_of_children_accompanied");

                ResidenceNeedful person = new ResidenceNeedful(fullName, AGE.getType(ageCategory), currentResidenceStatus, GENDER.getType(gender),
                        disabled, chronicAtEntry, skills, income, incomeSource, numberOfChildrenAccompanied);
                person.setClientId(clientId); // set client id

                people.add(person);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

        return people;
    }

    public void registerReporterIndividual(ReporterIndividual reporter) {

        try {
            insertReporterIndividual.setString(1, reporter.getFullName());
            insertReporterIndividual.setString(2, reporter.getEmail());
            insertReporterIndividual.setString(3, reporter.getPhoneNumber());
            insertReporterIndividual.setString(4, reporter.getPostcode());
            insertReporterIndividual.setString(5, reporter.getPassword());

            insertReporterIndividual.executeUpdate();  // execute the prepared statement insert
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public void registerReporterEmployee(ReporterEmployee employee) {

        try {
            insertReporterIndividual.setString(1, employee.getFullName());
            insertReporterIndividual.setString(2, employee.getEmail());
            insertReporterIndividual.setString(3, employee.getPhoneNumber());
            insertReporterIndividual.setString(4, employee.getPostcode());
            insertReporterIndividual.setString(5, employee.getPassword());

            insertReporterIndividual.executeUpdate();  // execute the prepared statement insert

            ResultSet rs = insertReporterIndividual.getGeneratedKeys();
            if (rs != null && rs.next()) {
                employee.setReporterId(rs.getInt(1)); // setting Reporter Id for Employee Reporter from the primary key of reporter_individual table
                if (employee.getReporterId() > 0) {
                    insertReporterEmployee.setString(1, employee.getOrganization());
                    insertReporterEmployee.setString(2, employee.getDepartment());
                    insertReporterEmployee.setString(3, employee.getSupervisorName());
                    insertReporterEmployee.setInt(4, employee.getReporterId());

                    insertReporterEmployee.executeUpdate();  // execute the prepared statement insert
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public boolean findReporterByEmailId(String email) {
        boolean isReporterExist = false;
        try {
            findReporterByEmailId.setString(1, email);
            ResultSet loginResult = findReporterByEmailId.executeQuery();
            if (loginResult.next()) {
                isReporterExist = true;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

        return isReporterExist;
    }

    public ReporterIndividual loggedInReporterIndividual(String email, String password) {
        ReporterIndividual reporter = new ReporterIndividual();
        try {
            loginReporterByEmailAndPassword.setString(1, email);
            loginReporterByEmailAndPassword.setString(2, password);
            ResultSet loginResult = loginReporterByEmailAndPassword.executeQuery();
            if (loginResult.next()) {
                int reporterId = loginResult.getInt("reporter_id");
                String fullName = loginResult.getString("full_name");
                String phoneNumber = loginResult.getString("phone_number");
                String emailId = loginResult.getString("email");
                String postcode = loginResult.getString("postcode");

                reporter = new ReporterIndividual(fullName, emailId, null, phoneNumber, postcode); // set returned Report Individual password as null for security purpose
                reporter.setReporterId(reporterId);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

        return reporter;
    }

    public ReporterEmployee findReporterEmployeeByReporterIndividual(ReporterIndividual individual) {
        ReporterEmployee employee = new ReporterEmployee();
        try {
            findReporterEmployeeByReporterId.setInt(1, individual.getReporterId());
            ResultSet resultSet = findReporterEmployeeByReporterId.executeQuery();
            if (resultSet.next()) {
                int employeeId = resultSet.getInt("employee_id");
                String organization = resultSet.getString("organization");
                String department = resultSet.getString("department");
                String supervisorName = resultSet.getString("supervisor_name");

                // setting Reporter Employee from Reporter Individual
                employee.setReporterId(individual.getReporterId());
                employee.setFullName(individual.getFullName());
                employee.setEmail(individual.getEmail());
                employee.setPhoneNumber(individual.getPhoneNumber());
                employee.setPostcode(individual.getPostcode());

                // setting Reporter Employee from Reporter Employee database table
                employee.setEmployeeId(employeeId);
                employee.setOrganization(organization);
                employee.setDepartment(department);
                employee.setSupervisorName(supervisorName);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }

        return employee;
    }
}

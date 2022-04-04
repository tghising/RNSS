package rnss;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * RNSSController: controller used to initialize the JavaFX GUI, and
 * interconnect with Client of Residence Need Solution System (RNSS)
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class RNSSController implements Initializable {

    private ToggleGroup gender;
    private ToggleGroup disabled;
    private ToggleGroup reporterType;

    @FXML
    private Pane mainLayout;

    @FXML
    private AnchorPane reporterLayout;

    @FXML
    private TextField fullNameTextField;

    @FXML
    private ComboBox<AGE> ageCategoriesComboBox;

    @FXML
    private ComboBox<String> currentResidentialStatusComboBox;

    @FXML
    private ComboBox<UserRole> userRoleComboBox;

    @FXML
    private RadioButton male;

    @FXML
    private RadioButton female;

    @FXML
    private RadioButton disabledRadioButton;

    @FXML
    private RadioButton notDisabledRadioButton;

    @FXML
    private ComboBox<String> chronicAtEntryComboBox;

    @FXML
    private TextField skillsTextField;

    @FXML
    private TextField incomeTextField;

    @FXML
    private ComboBox<String> incomeSourceComboBox;

    @FXML
    private TextField childrenAccompaniedTextField;

    @FXML
    private Label nameSignUpValidationLabel;

    @FXML
    private Label ageCategorySignUpValidationLabel;

    @FXML
    private Label currentResidentialStatusSignUpValidationLabel;

    @FXML
    private Label genderSignUpValidationLabel;

    @FXML
    private Label disabledSignUpValidationLabel;

    @FXML
    private Label chronicAtEntrySignUpValidationLabel;

    @FXML
    private Label incomeSignUpValidationLabel;

    @FXML
    private Label incomeSourceSignUpValidationLabel;

    @FXML
    private Label childrenAccompaniedSignUpValidationLabel;

    @FXML
    private Label addedPersonalInfoLabel;

    @FXML
    private Label userRoleValidationLabel;

    @FXML
    private TextArea personAddedTextArea;

    @FXML
    private AnchorPane serviceProviderLayout;

    @FXML
    private ComboBox<String> searchChronicAtEntryComboBox;

    @FXML
    private TableView<ResidenceNeedful> personDetailsTableView;

    @FXML
    private TableColumn<ResidenceNeedful, String> personFullName;

    @FXML
    private TableColumn<ResidenceNeedful, AGE> personAgeCategory;

    @FXML
    private TableColumn<ResidenceNeedful, GENDER> personGender;

    @FXML
    private TableColumn<ResidenceNeedful, String> personResidentialStatus;

    @FXML
    private TableColumn<ResidenceNeedful, Boolean> personDisabled;

    @FXML
    private TableColumn<ResidenceNeedful, String> personChronicAtEntry;

    @FXML
    private TableColumn<ResidenceNeedful, String> personSkills;

    @FXML
    private TableColumn<ResidenceNeedful, Double> personIncome;

    @FXML
    private TableColumn<ResidenceNeedful, String> personIncomeSource;

    @FXML
    private TableColumn<ResidenceNeedful, Integer> personChildrenAccompanied;

    @FXML
    private AnchorPane signUpReporterFormLayout;

    @FXML
    private TextField fullNameSignUpReporter;

    @FXML
    private TextField emailSignUpReporter;

    @FXML
    private TextField phoneNumberSignUpReporter;

    @FXML
    private TextField postCodeSignUpReporter;

    @FXML
    private PasswordField passwordSignUpReporter;

    @FXML
    private Group employmentReporterGroup;

    @FXML
    private TextField organizationOfEmployee;

    @FXML
    private TextField departmentOfEmployee;

    @FXML
    private TextField supervisorOfEmployee;

    @FXML
    private AnchorPane signInReporterFormLayout;

    @FXML
    private AnchorPane mainSignLayout;

    @FXML
    private TextField emailReporter;

    @FXML
    private Label reporterSignUpValidationSignUp;

    @FXML
    private Label signInReporterEmailLabel;

    @FXML
    private Label signInReporterPasswordLabel;

    @FXML
    private Label employeeOrganization;

    @FXML
    private PasswordField passwordReporter;

    @FXML
    private RadioButton individualReporterType;

    @FXML
    private RadioButton employeeReporterType;

    @FXML
    private MenuButton loggedInReporterMenuButton;

    @FXML
    private MenuButton loggedInServiceProvider;

    private LinkedList<ResidenceNeedful> residenceNeedfulsPersons;
    private LinkedList<ResidenceNeedful> searchResidenceNeedfulsList;
    private ReporterIndividual loggedInExistReporter;
    private ReporterEmployee loggedInExistEmployee;

    private RNSSClient rnssClientProcess;
    private Stage mainStage;

    private static final String MAIN_TITLE = "Residence Need Solution System";

    private static final String PASSWORD_REGEX = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}";
    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String FULL_NAME_REGEX = "^[A-Z][a-zA-Z]{2,}(?: [A-Z][a-zA-Z]*){0,2}$";
    private static final String PHONE_REGEX = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

    @FXML
    void confirmUserRole(ActionEvent event) {
        String userRole = "";
        if (userRoleComboBox.getSelectionModel().isEmpty()) {
            userRoleValidationLabel.setText("Please select a User Role");
            userRoleComboBox.requestFocus();
            return;
        } else {
            userRoleValidationLabel.setText("");
            userRole = userRoleComboBox.getValue().toString();
            mainSignLayout.setVisible(false);
            if (UserRole.getType(userRole).equals(UserRole.REPORTER)) {
                this.mainStage.setTitle("Sign In (Reporter) - " + MAIN_TITLE);
                signInReporterFormLayout.setVisible(true);
            } else {
                this.mainStage.setTitle("Service Provider - " + MAIN_TITLE);
                rnssClientProcess.createProviderThread("FETCH_ALL");
                searchResidenceNeedfulsList = rnssClientProcess.getResidenceNeedful();
                displayDataOnTableView(searchResidenceNeedfulsList);
                serviceProviderLayout.setVisible(true);
            }
        }
    }

    // method start game by initialize all world rooms and corresponding room and set Room 1 as current room
    public void initializeStage(Stage stage) {
        this.mainStage = stage;
        mainStage.setTitle(MAIN_TITLE);
    }

    @FXML
    void signOut(ActionEvent event) {
        //TODO
        reporterLayout.setVisible(false);
        serviceProviderLayout.setVisible(false);
        mainSignLayout.setVisible(true);
    }

    @FXML
    void signInReporter(ActionEvent event) {

        String email = emailReporter.getText();
        String password = passwordReporter.getText();

        if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
            signInReporterEmailLabel.setText("Invalid email address");
            return;
        } else {
            signInReporterEmailLabel.setText("");
        }

        if (password.isEmpty()) {
            signInReporterPasswordLabel.setText("The password that you've entered is incorrect");
            return;
        } else {
            signInReporterPasswordLabel.setText("");
        }

        ReporterIndividual loginReporter = new ReporterIndividual();
        loginReporter.setEmail(email);
        loginReporter.setPassword(password);

        rnssClientProcess.loginReporterClientThread(loginReporter, "SIGN_IN_REPORTER");
        loggedInExistReporter = rnssClientProcess.getReporterIndividual(); // returns value as Individual Reporter if signed reporter is individual
        loggedInExistEmployee = rnssClientProcess.getReporterEmployee(); // returns value as Employee Reporter if signed reporter is Employee of organization

        if (loggedInExistEmployee.getEmail() != null && loggedInExistEmployee.getEmployeeId() > 0 && loggedInExistEmployee.getOrganization() != null) {
            loggedInReporterMenuButton.setText(loggedInExistEmployee.getEmail());
            employeeOrganization.setText(loggedInExistEmployee.getOrganization());
            signInReporterFormLayout.setVisible(false);
            reporterLayout.setVisible(true);
        } else if (loggedInExistReporter.getEmail() != null && loggedInExistReporter.getReporterId() >= 0) {
            loggedInReporterMenuButton.setText(loggedInExistReporter.getEmail());
            employeeOrganization.setText("");
            signInReporterFormLayout.setVisible(false);
            reporterLayout.setVisible(true);
        } else {
            signInReporterEmailLabel.setText(rnssClientProcess.getConfirmationMessage());
            return;
        }
    }

    @FXML
    void signInReporterForm(ActionEvent event) {
        signInReporterFormLayout.setVisible(true);
        signUpReporterFormLayout.setVisible(false);

        organizationOfEmployee.clear();
        departmentOfEmployee.clear();
        supervisorOfEmployee.clear();

        fullNameSignUpReporter.clear();
        emailSignUpReporter.clear();
        phoneNumberSignUpReporter.clear();
        postCodeSignUpReporter.clear();
        passwordSignUpReporter.clear();
        passwordReporter.clear();
    }

    @FXML
    void signUpReporter(ActionEvent event) {
        String fullName = fullNameSignUpReporter.getText();
        String email = emailSignUpReporter.getText();
        String phoneNumber = phoneNumberSignUpReporter.getText();
        String postCode = postCodeSignUpReporter.getText();
        String password = passwordSignUpReporter.getText();

        String organization = organizationOfEmployee.getText();
        String department = departmentOfEmployee.getText();
        String supervisor = supervisorOfEmployee.getText();
        String message = "";

        reporterSignUpValidationSignUp.setText("");
        reporterSignUpValidationSignUp.setTextFill(Color.RED);

        if (fullName.isEmpty() || !fullName.matches(FULL_NAME_REGEX)) {
            reporterSignUpValidationSignUp.setText("Full name must be non-numeric and start with uppercase letter \nand at least 3 characters.");
            fullNameSignUpReporter.requestFocus();
            return;
        } else if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
            reporterSignUpValidationSignUp.setText("Please enter a valid email address.");
            emailSignUpReporter.requestFocus();
            return;
        } else if (phoneNumber.isEmpty() || !phoneNumber.matches(PHONE_REGEX)) {
            reporterSignUpValidationSignUp.setText("Please enter a valid phone number.");
            phoneNumberSignUpReporter.requestFocus();
            return;
        } else if (postCode.isEmpty() || postCode.length() != 4) {
            reporterSignUpValidationSignUp.setText("Please enter a valid postcode.");
            postCodeSignUpReporter.requestFocus();
            return;
        } else if (password.isEmpty() || !password.matches(PASSWORD_REGEX)) {
            reporterSignUpValidationSignUp.setText("Password must contain at least one number and one uppercase\nand lowercase letter, and at least 8 or more characters.");
            passwordSignUpReporter.requestFocus();
            return;
        } else {
            if (employeeReporterType.isSelected()) {
                if (organization.isEmpty()) {
                    reporterSignUpValidationSignUp.setText("Please enter a organization name.");
                    organizationOfEmployee.requestFocus();
                    return;
                } else if (department.isEmpty()) {
                    reporterSignUpValidationSignUp.setText("Please enter a department name.");
                    departmentOfEmployee.requestFocus();
                    return;
                } else if (supervisor.isEmpty()) {
                    reporterSignUpValidationSignUp.setText("Please enter a supervisor name.");
                    supervisorOfEmployee.requestFocus();
                    return;
                }
            }
        }

        if (employeeReporterType.isSelected()) {
            ReporterEmployee employee = new ReporterEmployee(fullName, email, password, phoneNumber, postCode, organization, department, supervisor);
            rnssClientProcess.createSignUpClientThread(employee, "SIGNUP_EMPLOYEE_REPORTER");
            loggedInExistEmployee = rnssClientProcess.getReporterEmployee();
            message = rnssClientProcess.getConfirmationMessage();
            if (loggedInExistEmployee.getEmail() != null) {
                emailSignUpReporter.requestFocus();
                reporterSignUpValidationSignUp.setText(message);
                return;
            } else {
                clearSignUpFormFields();
                reporterSignUpValidationSignUp.setTextFill(Color.GREEN);
                reporterSignUpValidationSignUp.setText(message);
            }
        } else {
            ReporterIndividual individual = new ReporterIndividual(fullName, email, password, phoneNumber, postCode);
            rnssClientProcess.createSignUpClientThread(individual, "SIGNUP_INDIVIDUAL_REPORTER");
            loggedInExistReporter = rnssClientProcess.getReporterIndividual();
            message = rnssClientProcess.getConfirmationMessage();
            if (loggedInExistReporter.getEmail() != null) {
                emailSignUpReporter.requestFocus();
                reporterSignUpValidationSignUp.setText(message);
                return;
            } else {
                clearSignUpFormFields();
                reporterSignUpValidationSignUp.setTextFill(Color.GREEN);
                reporterSignUpValidationSignUp.setText(message);
            }
        }
    }

    @FXML
    void signUpReporterForm(ActionEvent event) {
        this.mainStage.setTitle("Sign Up (Reporter) - " + MAIN_TITLE);
        if (individualReporterType.isSelected()) {
            employmentReporterGroup.setDisable(true);
        } else {
            employmentReporterGroup.setDisable(false);
        }
        signInReporterFormLayout.setVisible(false);
        signUpReporterFormLayout.setVisible(true);
    }

    @FXML
    void hideEmploymentForm(ActionEvent event) {
        employmentReporterGroup.setDisable(true);

        organizationOfEmployee.clear();
        departmentOfEmployee.clear();
        supervisorOfEmployee.clear();
    }

    @FXML
    void showEmploymentForm(ActionEvent event) {
        employmentReporterGroup.setDisable(false);
    }

    @FXML
    void addPerson(ActionEvent event) throws IOException {
        String fullName = fullNameTextField.getText();
        String skills = skillsTextField.getText();
        String ageCategory = "";
        String currentResidentialStatus = "";
        String gender = "";
        String disabled = "";
        String chronicAtEntry = "";
        String incomeSource = "";
        double income = 0.0;
        int numberOfChildren = 0;

        addedPersonalInfoLabel.setText("");
        nameSignUpValidationLabel.setText("");

        if (fullName.isEmpty() || !fullName.matches(FULL_NAME_REGEX)) {
            nameSignUpValidationLabel.setText("Name must be non-numeric and start with \nUppercase letter and at least 3 characters.");
            fullNameTextField.requestFocus();
            return;
        }

        if (ageCategoriesComboBox.getSelectionModel().isEmpty()) {
            ageCategorySignUpValidationLabel.setText("Age category must be selected.");
            ageCategoriesComboBox.requestFocus();
            return;
        } else {
            ageCategory = ageCategoriesComboBox.getValue().toString();
            ageCategorySignUpValidationLabel.setText("");
        }

        if (currentResidentialStatusComboBox.getSelectionModel().isEmpty()) {
            currentResidentialStatusSignUpValidationLabel.setText("Current residential status must be selected.");
            currentResidentialStatusComboBox.requestFocus();
            return;
        } else {
            currentResidentialStatus = currentResidentialStatusComboBox.getValue().toString();
            currentResidentialStatusSignUpValidationLabel.setText("");
        }

        genderSignUpValidationLabel.setText("");
        if (male.isSelected()) {
            gender = male.getText();
        } else if (female.isSelected()) {
            gender = female.getText();
        } else {
            genderSignUpValidationLabel.setText("Gender must be selected.");
            male.requestFocus();
            return;
        }

        disabledSignUpValidationLabel.setText("");
        if (disabledRadioButton.isSelected()) {
            disabled = "Yes";
        } else if (notDisabledRadioButton.isSelected()) {
            disabled = "No";
        } else {
            disabledSignUpValidationLabel.setText("Disabled status must be selected.");
            return;
        }

        if (chronicAtEntryComboBox.getSelectionModel().isEmpty()) {
            chronicAtEntrySignUpValidationLabel.setText("Chronic at entry must be selected.");
            chronicAtEntryComboBox.requestFocus();
            return;
        } else {
            chronicAtEntry = chronicAtEntryComboBox.getValue().toString();
            chronicAtEntrySignUpValidationLabel.setText("");
        }

        if (!incomeTextField.getText().isEmpty() || !incomeTextField.getText().isBlank()) {
            if (incomeTextField.getText().matches("[0-9]+") || incomeTextField.getText().matches("[0-9]{1,13}(\\.[0-9]*)?")) {
                income = Double.parseDouble(incomeTextField.getText());
                incomeSignUpValidationLabel.setText("");
                if (income > 0.0) {
                    if (incomeSourceComboBox.getSelectionModel().isEmpty()) {
                        incomeSourceSignUpValidationLabel.setText("Choose the source of income.");
                        incomeSourceComboBox.requestFocus();
                        return;
                    } else {
                        incomeSource = incomeSourceComboBox.getValue().toString();
                        incomeSourceSignUpValidationLabel.setText("");
                    }
                } else {
                    incomeSourceSignUpValidationLabel.setText("");
                }
            } else {
                incomeSignUpValidationLabel.setText("Income must be a greater than zero.");
                incomeSourceSignUpValidationLabel.setText("");
                incomeSourceComboBox.requestFocus();
                return;
            }
        } else {
            incomeSourceSignUpValidationLabel.setText("");
        }

        if (!childrenAccompaniedTextField.getText().isEmpty()) {
            if (childrenAccompaniedTextField.getText().matches("[0-9]+")) {
                numberOfChildren = Integer.parseInt(childrenAccompaniedTextField.getText());
                childrenAccompaniedSignUpValidationLabel.setText("");
            } else {
                childrenAccompaniedSignUpValidationLabel.setText("Children accompanied must be a positive number.");
                childrenAccompaniedTextField.requestFocus();
                return;
            }
        } else {
            childrenAccompaniedSignUpValidationLabel.setText("");
        }

        ResidenceNeedful person = new ResidenceNeedful(fullName, AGE.getType(ageCategory), currentResidentialStatus, GENDER.getType(gender),
                disabled, chronicAtEntry, skills, income, incomeSource, numberOfChildren);

        residenceNeedfulsPersons.add(person);
        clearRegistrationFields();
        addedPersonalInfoLabel.setText("The following person has been added successfully to RNSS.");
        String peopleList = "";
        for (ResidenceNeedful residenceNeedful : residenceNeedfulsPersons) {
            peopleList += residenceNeedful.toString() + "\n";
        }
        personAddedTextArea.setText(peopleList);
    }

    private void clearRegistrationFields() {
        fullNameTextField.clear();
        incomeTextField.clear();
        skillsTextField.clear();
        childrenAccompaniedTextField.clear();
        if (male.isSelected() || female.isSelected()) {
            gender.getSelectedToggle().setSelected(false);
        }

        if (disabledRadioButton.isSelected() || notDisabledRadioButton.isSelected()) {
            disabled.getSelectedToggle().setSelected(false);
        }
        currentResidentialStatusComboBox.getSelectionModel().clearSelection();
        currentResidentialStatusComboBox.setPromptText("Select");
        ageCategoriesComboBox.getSelectionModel().clearSelection();
        ageCategoriesComboBox.setPromptText("Select");
        chronicAtEntryComboBox.getSelectionModel().clearSelection();
        chronicAtEntryComboBox.setPromptText("Select");
        incomeSourceComboBox.getSelectionModel().clearSelection();
        incomeSourceComboBox.setPromptText("Select");
    }

    private void clearSignUpFormFields() {
        fullNameSignUpReporter.clear();
        emailSignUpReporter.clear();
        phoneNumberSignUpReporter.clear();
        postCodeSignUpReporter.clear();
        passwordSignUpReporter.clear();
        organizationOfEmployee.clear();
        departmentOfEmployee.clear();
        supervisorOfEmployee.clear();
    }

    @FXML
    void submitEntries(ActionEvent event) {
        rnssClientProcess.createRNSSClientThread(this.residenceNeedfulsPersons, "ADD_RESIDENCE_NEEDFUL_PEOPLE");
        addedPersonalInfoLabel.setText("The residence needful people list has been added successfully to RNSS server database.");
        personAddedTextArea.setText("");
    }

    @FXML
    void searchResidenceNeedfulPersonList(ActionEvent event) {
        if (!searchChronicAtEntryComboBox.getSelectionModel().isEmpty()) {
            String chronicAtEntry = searchChronicAtEntryComboBox.getValue().toString();
            rnssClientProcess.createProviderThread(chronicAtEntry);
            searchResidenceNeedfulsList = rnssClientProcess.getResidenceNeedful();
            displayDataOnTableView(searchResidenceNeedfulsList);
        }
    }

    @FXML
    void exitRNSS(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void incomeSourceOnIncomeChange(KeyEvent event) {
        this.incomeSourceComboBox.setDisable(true);
        if (!incomeTextField.getText().isEmpty() || !incomeTextField.getText().isBlank()) {
            if (incomeTextField.getText().matches("[0-9]+") || incomeTextField.getText().matches("[0-9]{1,13}(\\.[0-9]*)?")) {
                double income = Double.parseDouble(incomeTextField.getText());
                if (income > 0.0) {
                    this.incomeSourceComboBox.setDisable(false);
                } else {
                    this.incomeSourceComboBox.setDisable(true);
                }
            }
        } else {
            incomeSourceSignUpValidationLabel.setText("");
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        residenceNeedfulsPersons = new LinkedList<>();
        searchResidenceNeedfulsList = new LinkedList<>();
        loggedInExistReporter = new ReporterIndividual();
        mainStage = new Stage();
        try {
            rnssClientProcess = new RNSSClient();
        } catch (IOException | NoSuchAlgorithmException ioe) {
            System.out.println(ioe.getMessage());
        }

        // initializing the combobox items for User Role
        List<UserRole> userRoles = Arrays.asList(UserRole.values());
        userRoleComboBox.getItems().addAll(userRoles);
        serviceProviderLayout.setVisible(false);
        reporterLayout.setVisible(false);
        mainSignLayout.setVisible(true);
        loggedInServiceProvider.setText(UserRole.SERVICE_PROVIDER.toString()); // demo service provider

        // initializing the combobox items for age category
        List<AGE> ageCategories = Arrays.asList(AGE.values());
        ageCategoriesComboBox.getItems().addAll(ageCategories);
        serviceProviderLayout.setVisible(false);
        reporterLayout.setVisible(false);
        mainSignLayout.setVisible(true);

        signUpReporterFormLayout.setVisible(false);
        signInReporterFormLayout.setVisible(false);

        //gender toggle grouping
        gender = new ToggleGroup();
        this.male.setText(GENDER.MALE.toString());
        this.male.setToggleGroup(gender);
        this.female.setText(GENDER.FEMALE.toString());
        this.female.setToggleGroup(gender);

        //disabled toggle grouping
        disabled = new ToggleGroup();
        this.disabledRadioButton.setText("Yes");
        this.disabledRadioButton.setToggleGroup(disabled);
        this.notDisabledRadioButton.setText("No");
        this.notDisabledRadioButton.setToggleGroup(disabled);

        // reporterType toggle grouping
        reporterType = new ToggleGroup();
        this.individualReporterType.setText("Individual");
        this.individualReporterType.setToggleGroup(reporterType);
        individualReporterType.setSelected(true); // set default
        this.employeeReporterType.setText("Employee");
        this.employeeReporterType.setToggleGroup(reporterType);

        this.incomeSourceComboBox.setDisable(true);

        employmentReporterGroup.setDisable(true);
        signInReporterFormLayout.setVisible(false);

        currentResidentialStatusComboBox.getItems().addAll("Rough sleeping", "Temporary housing");
        chronicAtEntryComboBox.getItems().addAll("Domestic violence", "Mental illness", "Severe illness");
        searchChronicAtEntryComboBox.getItems().addAll("Domestic violence", "Mental illness", "Severe illness");
        incomeSourceComboBox.getItems().addAll("Welfare", "Employment", "Savings");
    }

    private void displayDataOnTableView(List<ResidenceNeedful> residenceNeedfuls) {

        personFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        personAgeCategory.setCellValueFactory(new PropertyValueFactory<>("ageCategory"));
        personGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        personResidentialStatus.setCellValueFactory(new PropertyValueFactory<>("currentResidenceStatus"));
        personDisabled.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        personChronicAtEntry.setCellValueFactory(new PropertyValueFactory<>("chronicAtEntry"));
        personSkills.setCellValueFactory(new PropertyValueFactory<>("skills"));
        personIncome.setCellValueFactory(new PropertyValueFactory<>("income"));
        personIncomeSource.setCellValueFactory(new PropertyValueFactory<>("incomeSource"));
        personChildrenAccompanied.setCellValueFactory(new PropertyValueFactory<>("numberOfChildrenAccompanied"));

        ObservableList<ResidenceNeedful> residenceList = FXCollections.observableList(residenceNeedfuls);
        personDetailsTableView.setItems(residenceList);
        if (residenceList.isEmpty()) {
            personDetailsTableView.setPlaceholder(new Label("No records found!"));
        }
    }
}

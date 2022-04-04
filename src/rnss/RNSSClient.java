package rnss;

import javax.crypto.Cipher;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedList;

/**
 * RNSSClient: Class for client side TCP communication with multiple thread
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class RNSSClient {

    private static LinkedList<ResidenceNeedful> resultResidenceNeedful; // list of residence needful person from the server
    private static ReporterIndividual individual; // reporter return from the server
    private static ReporterEmployee employee; // reporter return from the server
    private static String confirmationMessage = "";
    ServerConnection conn;
    Thread providerThread;

    // constructor
    public RNSSClient() throws NoSuchAlgorithmException, UnknownHostException, IOException {
        this.resultResidenceNeedful = new LinkedList<>();
        this.individual = new ReporterIndividual();
        this.employee = new ReporterEmployee();
        System.out.println("Client started.....");
    }

    // This void method create a client thread with linkedList of residence needful person and action
    public void createRNSSClientThread(LinkedList<ResidenceNeedful> persons, String action) {
        conn = new ServerConnection(persons, action);
        persons.clear(); // clear or empty lists of persons
    }

    // This void method create a client thread with Reporter Individual and action
    public void createSignUpClientThread(ReporterIndividual reporter, String action) {
        conn = new ServerConnection(reporter, action);
    }

    // This void method create a client thread with ReporterEmployee and action
    public void createSignUpClientThread(ReporterEmployee employee, String action) {
        conn = new ServerConnection(employee, action);
    }

    // This void method create a client thread with Reporter Individual and action
    public void loginReporterClientThread(ReporterIndividual reporter, String action) {
        conn = new ServerConnection(reporter, action);
    }

    // This method returns LinkedList of ResidenceNeedful person
    @SuppressWarnings("empty-statement")
    public LinkedList<ResidenceNeedful> getResidenceNeedful() {
        while (providerThread.isAlive())
            ;
        return resultResidenceNeedful;
    }

    // This method returns Reporter Individual
    @SuppressWarnings("empty-statement")
    public ReporterIndividual getReporterIndividual() {
        while (conn.isAlive())
            ;
        return individual;
    }

    // This method returns Reporter Employee
    @SuppressWarnings("empty-statement")
    public ReporterEmployee getReporterEmployee() {
        while (conn.isAlive())
            ;
        return employee;
    }

    // This method returns LinkedList of ResidenceNeedful person
    @SuppressWarnings("empty-statement")
    public String getConfirmationMessage() {
        while (conn.isAlive())
            ;
        return confirmationMessage;
    }

    // This method to create the client provider thread to search the list of residence needful person on the basis of searchoption i.e. chronic at entry
    public void createProviderThread(String searchOption) {
        providerThread = new Thread(new ProviderConnection(searchOption));
        providerThread.start();
        System.out.println(providerThread.getName() + " has been started at Client.....");
    }

    // thread for send the ResidenceNeedful data (ObjectOutputStream) into the server from the client
    class ServerConnection extends Thread {

        private int serverPort;
        ObjectInputStream in;
        ObjectOutputStream out;
        private Socket socket = null;
        private LinkedList<ResidenceNeedful> residenceNeedfulsList = new LinkedList<>();
        private ReporterIndividual reporterIndividual = new ReporterIndividual();
        private ReporterEmployee reporterEmployee = new ReporterEmployee();
        private String option;

        private PublicKey publicKey;
        byte[] bytesPublicKey = null;
        int publicKeyLength;
        private boolean isReporterExist = false;
        private boolean validReporter = false;
        private boolean isReporterEmployee = false;

        public ServerConnection(LinkedList<ResidenceNeedful> residenceNeedfuls, String action) {
            try {
                serverPort = 9999;
                socket = new Socket("localhost", serverPort);
                System.out.println("Client has been connected with server.");
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                this.residenceNeedfulsList.addAll(residenceNeedfuls); // added list of Residence needful people into local registry
                this.start();
                option = action;
                System.out.println(this.getName() + " has been started at Client.....");
            } catch (IOException e) {
                System.out.println("Connection to Server " + e.getMessage());
            }
        }

        public ServerConnection(ReporterIndividual reporter, String action) {
            individual = new ReporterIndividual();
            employee = new ReporterEmployee();
            try {
                serverPort = 9999;
                socket = new Socket("localhost", serverPort);
                System.out.println("Client has been connected with server.");
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                this.reporterIndividual = reporter; // added ReporterIndividual local registry of ReporterIndividual
                this.option = action;
                this.start();
                System.out.println(this.getName() + " has been started at Client.....");
            } catch (IOException e) {
                System.out.println("Connection to Server " + e.getMessage());
            }
        }

        public ServerConnection(ReporterEmployee reporter, String action) {
            individual = new ReporterIndividual();
            employee = new ReporterEmployee();
            try {
                serverPort = 9999;
                socket = new Socket("localhost", serverPort);
                System.out.println("Client has been connected with server.");
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                this.reporterEmployee = reporter; // added ReporterIndividual local registry of ReporterIndividual
                this.option = action;
                this.start();
                System.out.println(this.getName() + " has been started at Client.....");
            } catch (IOException e) {
                System.out.println("Connection to Server " + e.getMessage());
            }
        }

        // method to encrypt the data using public key sent by server
        public byte[] encrypt(PublicKey publicKey, String data) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            return encrypted;
        }

        @Override
        public void run() {
            try {

                if (option.equals("SIGN_IN_REPORTER")) {
                    out.writeObject(option);
                    out.writeObject(reporterIndividual.getEmail()); // send email as username to the server
                    validReporter = (boolean) in.readObject();

                    if (validReporter) {
                        publicKeyLength = Integer.parseInt(in.readObject().toString());
                        bytesPublicKey = new byte[publicKeyLength];
                        bytesPublicKey = (byte[]) in.readObject();
                        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey); // extract a public key using key specification
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                        //extract the PublicKey
                        publicKey = keyFactory.generatePublic(pubKeySpec);
                        byte[] encryptedPassword = encrypt(publicKey, reporterIndividual.getPassword()); // encrypt the password using public key

                        reporterIndividual.setEncryptPassword(encryptedPassword); //set encryptedPassword
                        reporterIndividual.setPassword(null); // set password to null
                        out.writeObject(reporterIndividual);

                        boolean isReporterMatched = (boolean) in.readObject(); // to check reporter with given email and password
                        if (isReporterMatched) {
                            isReporterEmployee = (boolean) in.readObject();
                            if (isReporterEmployee) {
                                employee = (ReporterEmployee) in.readObject();
                            } else {
                                individual = (ReporterIndividual) in.readObject();
                            }
                        } else {
                            individual = new ReporterIndividual();
                            employee =  new ReporterEmployee();
                            confirmationMessage = (String) in.readObject();
                        }
                    } else {
                        individual = new ReporterIndividual();
                        employee =  new ReporterEmployee();
                        confirmationMessage = (String) in.readObject();
                    }
                } else if (option.equals("SIGNUP_INDIVIDUAL_REPORTER")) {
                    out.writeObject(option);
                    out.writeObject(reporterIndividual.getEmail());
                    isReporterExist = (boolean) in.readObject();

                    if (isReporterExist) {
                        individual.setEmail(reporterIndividual.getEmail());
                        confirmationMessage = (String) in.readObject();
                    } else {
                        publicKeyLength = Integer.parseInt(in.readObject().toString());
                        bytesPublicKey = new byte[publicKeyLength];
                        bytesPublicKey = (byte[]) in.readObject();
                        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey); // extract a public key using key specification
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                        //extract the PublicKey
                        publicKey = keyFactory.generatePublic(pubKeySpec);
                        byte[] encryptedPassword = encrypt(publicKey, reporterIndividual.getPassword());

                        reporterIndividual.setEncryptPassword(encryptedPassword); //set encryptedPassword
                        reporterIndividual.setPassword(null); // set password to null
                        out.writeObject(reporterIndividual);
                        confirmationMessage = (String) in.readObject();
                    }
                } else if (option.equals("SIGNUP_EMPLOYEE_REPORTER")) {
                    out.writeObject(option);
                    out.writeObject(reporterEmployee.getEmail());
                    isReporterExist = (boolean) in.readObject();

                    if (isReporterExist) {
                        employee.setEmail(reporterEmployee.getEmail());
                        confirmationMessage = (String) in.readObject();
                    } else {
                        publicKeyLength = Integer.parseInt(in.readObject().toString());
                        bytesPublicKey = new byte[publicKeyLength];
                        bytesPublicKey = (byte[]) in.readObject();
                        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey); // extract a public key using key specification
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                        //extract the PublicKey
                        publicKey = keyFactory.generatePublic(pubKeySpec);
                        byte[] encryptedPassword = encrypt(publicKey, reporterEmployee.getPassword()); // encrypt the password using public key
                        reporterEmployee.setEncryptPassword(encryptedPassword); // set encryptedPassword
                        reporterEmployee.setPassword(null); // set password to null
                        out.writeObject(reporterEmployee);
                        confirmationMessage = (String) in.readObject();
                    }
                } else {
                    out.writeObject("ADD_RESIDENCE_NEEDFUL_PEOPLE");
                    while (residenceNeedfulsList.size() > 0) {
                        out.writeObject(residenceNeedfulsList.removeFirst());
                    }
                    if (residenceNeedfulsList.size() == 0) {
                        out.writeObject(new ResidenceNeedful("FINISHED_DATA"));
                    }
                }
            } catch (UnknownHostException e) {
                System.out.println("Unknown Host: " + e.getMessage());
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO:" + e.getMessage());
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Close: " + e.getMessage());
                    }
                }
            }
        }
    }

    // thread class which implements the Runnable interface used for send the search option (i.e. chronic at entry) into the server from the client
    // and receive the list of residence needful people from the server
    class ProviderConnection implements Runnable {

        private int serverPort;
        ObjectInputStream in;
        ObjectOutputStream out;
        private Socket socket = null;
        private String searchOption;
        private ReporterIndividual reporterIndividual = new ReporterIndividual();
        private ReporterEmployee reporterEmployee = new ReporterEmployee();

        // constructor which initialize the search option of chronic at entry
        public ProviderConnection(String searchOption) {
            resultResidenceNeedful = new LinkedList<>();
            try {
                serverPort = 9999;
                socket = new Socket("localhost", serverPort);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                this.searchOption = new String(searchOption);
            } catch (IOException e) {
                System.out.println("Connection to Server " + e.getMessage());
            }
        }

        public ProviderConnection(ReporterIndividual reporter, String option) {
            try {
                serverPort = 9999;
                socket = new Socket("localhost", serverPort);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                reporterIndividual = reporter;
                this.searchOption = option;
            } catch (IOException e) {
                System.out.println("Connection to Server " + e.getMessage());
            }
        }

        public ProviderConnection(ReporterEmployee employee, String option) {
            try {
                serverPort = 9999;
                socket = new Socket("localhost", serverPort);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                reporterEmployee = employee;
                this.searchOption = option;
            } catch (IOException e) {
                System.out.println("Connection to Server " + e.getMessage());
            }
        }

        @Override
        public void run() {
            ResidenceNeedful residenceNeedfulPerson;
            try {
                out.writeObject(searchOption);
                while ((residenceNeedfulPerson = (ResidenceNeedful) in.readObject()) != null) {
                    resultResidenceNeedful.add(new ResidenceNeedful(residenceNeedfulPerson));
                }
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
            }
        }
    }
}

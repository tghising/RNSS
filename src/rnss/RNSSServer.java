package rnss;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * RNSSServer: Class for server side TCP communication with a single thread
 *
 * @author Tikaraj Ghising - 12129239 / Priteshkumar Patel - 12129477 Maruf Ahmed Siddique - 12132167
 */
public class RNSSServer {

    private final KeyPairGenerator keyPairGen;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RNSSServer() throws IOException, NoSuchAlgorithmException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        keyPairGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGen.genKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        System.out.println("Server started.....");
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    // main method() for the server side
    public static void main(String args[]) {
        try {
            final int SERVER_PORT = 9999;
            ServerSocket listenSocket = new ServerSocket(SERVER_PORT);
            RNSSServer server = new RNSSServer();
            PublicKey publicKey = server.getPublicKey();
            PrivateKey pKey = server.getPrivateKey();

            while (true) {
                Socket clientSocket = listenSocket.accept();
                Connection conn = new Connection(clientSocket, publicKey, pKey);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

//this is for the tcp connection of multiple clients.
class Connection extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;

    private LinkedList<ResidenceNeedful> residenceNeedfulPeople = new LinkedList<>();
    private DatabaseUtility databaseUtility;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private final static String REGISTERED_SUCCESSFUL = "Your account has been successfully registered. Please Sign In with your email.";
    private final static String ALREADY_REGISTERED = "Your email is already registered. Please Sign Up with another email.";

    public Connection(Socket cSocket, PublicKey pubKey, PrivateKey pKey) {
        this.publicKey = pubKey;
        this.privateKey = pKey;
        try {
            clientSocket = cSocket;
            //the streams are not used in this program.
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            databaseUtility = new DatabaseUtility();
            this.start();
            System.out.println(this.getName() + " has been started at Server.....");
        } catch (IOException e) {
            System.out.println("Server Connection: " + e.getMessage());
        }
    }

    //This method can open the file and load all the LinkedList of data from the file
    private List<ResidenceNeedful> getAllResidenceNeedfulEntries() {
        LinkedList<ResidenceNeedful> allResidenceNeedful = databaseUtility.getAllResidenceNeedfulPeople();
        return allResidenceNeedful;
    }

    //This method can open the file and load the LinkedList of data from the file which has the given search chronic at entry matching.
    private List<ResidenceNeedful> searchByChronicAtEntry(String searchOption) {
        LinkedList<ResidenceNeedful> result = databaseUtility.getAllResidenceNeedfulByChronicAtEntry(searchOption);
        return result;
    }

    // method to decrypt encrypted data using private key
    public String decrypt(PrivateKey pKey, byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pKey, cipher.getParameters()); //  decodes using the privateKey.
        String decrypted = new String(cipher.doFinal(encrypted));
        return decrypted;
    }

    @Override
    public void run() {
        ResidenceNeedful residenceNeedfulPerson;
        ReporterIndividual reporterIndividual;
        ReporterEmployee reporterEmployee;
        List<ResidenceNeedful> residenceNeedfulList = new ArrayList<>();

        //generate the encoded key
        byte[] bytesPubKey = publicKey.getEncoded();
        boolean isReporterExist = false;

        try {
            String option = (String) in.readObject();

            if (option.equals("SIGN_IN_REPORTER")) {
                String email = (String) in.readObject();

                isReporterExist = databaseUtility.findReporterByEmailId(email);

                out.writeObject(isReporterExist);
                if (isReporterExist) {
                    out.writeObject(bytesPubKey.length);
                    out.writeObject(bytesPubKey); // server sends an encoded PublicKey to the client for encoding

                    while ((reporterIndividual = (ReporterIndividual) in.readObject()) != null) {
                        email = reporterIndividual.getEmail();
                        String password = decrypt(privateKey, reporterIndividual.getEncryptPassword()); // decrypts using private key

                        ReporterIndividual dbIndividual = databaseUtility.loggedInReporterIndividual(email, password);

                        if (dbIndividual.getEmail() != null && dbIndividual.getFullName() != null && dbIndividual.getReporterId() > 0) {
                            out.writeObject(true); // send true: Given reporter with email and password matched the existing reporter

                            ReporterEmployee dbEmployee = databaseUtility.findReporterEmployeeByReporterIndividual(dbIndividual);

                            if (dbEmployee.getEmail() != null && dbEmployee.getEmployeeId() > 0 && dbEmployee.getOrganization() != null) {
                                out.writeObject(true); // send true: Given reporter with email and password belongs to Reporter Employee
                                out.writeObject(dbEmployee);
                            } else {
                                out.writeObject(false); // send false: Given reporter with email and password belongs to Reporter Individual
                                out.writeObject(dbIndividual);
                            }
                        } else {
                            out.writeObject(false);  // send false: Given reporter with email and password does not matched the existing reporter
                            out.writeObject("Does not match your Email and Password with any account.");
                        }
                    }
                } else {
                    out.writeObject(email + " is not associated with any reporter.");
                }
            } else if (option.equals("SIGNUP_INDIVIDUAL_REPORTER")) {
                String email = (String) in.readObject();
                isReporterExist = databaseUtility.findReporterByEmailId(email);
                out.writeObject(isReporterExist);
                if (isReporterExist) {
                    out.writeObject(ALREADY_REGISTERED);
                } else {
                    out.writeObject(bytesPubKey.length);
                    out.writeObject(bytesPubKey); // server sends an encoded PublicKey to the client for encoding

                    while ((reporterIndividual = (ReporterIndividual) in.readObject()) != null) {
                        String password = decrypt(privateKey, reporterIndividual.getEncryptPassword()); // decrypts using private key
                        reporterIndividual.setPassword(password);
                        databaseUtility.registerReporterIndividual(reporterIndividual); // register a individual reporter

                        out.writeObject(REGISTERED_SUCCESSFUL); // send registered success message to client
                    }
                }
            } else if (option.equals("SIGNUP_EMPLOYEE_REPORTER")) {
                String email = (String) in.readObject();
                isReporterExist = databaseUtility.findReporterByEmailId(email);
                out.writeObject(isReporterExist);
                if (isReporterExist) {
                    out.writeObject(ALREADY_REGISTERED);
                } else {
                    out.writeObject(bytesPubKey.length);
                    out.writeObject(bytesPubKey); // server sends an encoded PublicKey to the client for encoding

                    while ((reporterEmployee = (ReporterEmployee) in.readObject()) != null) {
                        String password = decrypt(privateKey, reporterEmployee.getEncryptPassword()); // decrypts using private key
                        reporterEmployee.setPassword(password);
                        databaseUtility.registerReporterEmployee(reporterEmployee); // register reporter of any organization employee

                        out.writeObject(REGISTERED_SUCCESSFUL); // send registered success message to client
                    }
                }
            } else if (option.equals("ADD_RESIDENCE_NEEDFUL_PEOPLE")) {
                while ((residenceNeedfulPerson = (ResidenceNeedful) in.readObject()) != null) {
                    if (residenceNeedfulPerson.getFullName().equalsIgnoreCase("FINISHED_DATA")) {
                        break;
                    }
                    residenceNeedfulList.add(residenceNeedfulPerson);
                }
                databaseUtility.addResidenceNeedfulPeople(residenceNeedfulList); // added lis of Residence Needful people into database table
            } else if (option.equals("FETCH_ALL")) {
                residenceNeedfulList = getAllResidenceNeedfulEntries();
                for (ResidenceNeedful eachPerson : residenceNeedfulList) {
                    out.writeObject(eachPerson);
                }
            } else {
                System.out.println("Find all the Residence needful person from the database with chronic at entry: " + option);
                residenceNeedfulList = searchByChronicAtEntry(option);
                for (ResidenceNeedful eachPerson : residenceNeedfulList) {
                    out.writeObject(eachPerson);
                }
            }
        } catch (EOFException eoe) {
            System.out.println("EOF:" + eoe.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("IO:" + e.getMessage());
        } catch (NoSuchAlgorithmException algorithmException) {
            System.out.println("No Algorithm:" + algorithmException.getMessage());
        } catch (InvalidKeyException ike) {
            System.out.println("Invalid Ex:" + ike.getMessage());
        } catch (InvalidAlgorithmParameterException parameterException) {
            System.out.println("Invalid Algorithm Params:" + parameterException.getMessage());
        } catch (NoSuchPaddingException paddingException) {
            System.out.println("No such Padding:" + paddingException.getMessage());
        } catch (BadPaddingException badPaddingException) {
            System.out.println("Bad Padding:" + badPaddingException.getMessage());
        } catch (IllegalBlockSizeException ibse) {
            System.out.println("IllegalBlock:" + ibse.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Socket close: " + e.getMessage());
            }
        }
    }
}

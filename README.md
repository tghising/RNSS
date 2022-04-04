# RNSS
Residence Need Solution System (RNSS) is an application where user can interact via click button commands with JavaFX Graphical User Interface (GUI), data persist and read from the binary file

## Residence Need Solution System (RNSS) is an application where user can interact via click button commands with JavaFX Graphical User Interface (GUI), data persist and read from the binary file.   RNSS to accurately capture and store data from available sources and make it accessible to government agencies and other relevant service providers. 

# RNSS Architecture
![image](https://user-images.githubusercontent.com/38654670/161457495-2028e5fe-9437-4f65-8898-f0665f7dcdb9.png)

Residence Need Solution System (RNSS) is the distributed application using networking, inter-process communication using TCP client server communication using socket, and remote invocation, and multiple thread in the client side and single thread in the server side. 

TCP Socket and Server Socket are used to client/server communication in the RNSS application. Both client and server used Object Input Stream and Object Output Stream for reading and writing an information or serialized objects respectively for client/server communication. 
For Sign in and Sign Up, client sends email as username to the server then server searches given email in the database. The server sends an encoded Public Key to the client for sending an encoded password if server finds the email in the database. Then, client extract the public key using the key specification and use it to encrypt the password. Then the encrypted password, and other details sent to the server. Finally, the server decodes the user’s password using the Private Key and other details receive from the client. The server saves of data synchronized or searches the records in the MySQL database.
A remote object registry is used by Remote Method Invocation (RMI) client/server to access/invoke an object running on another remote client/server.

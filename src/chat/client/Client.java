package chat.client;

import chat.Connection;
import chat.KeyDTO;
import chat.RSA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Paweł Dąbrowski on 28.12.2016.
 */
public class Client extends Connection {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private ConnectionThread connectionThread = new ConnectionThread();

    private String host;

    private int port;

    private RSA rsa;

    public Client(String aHost, int aPort) {
        host = aHost;
        port = aPort;
        rsa = new RSA();
        rsa.createKeys(BigInteger.probablePrime(RSA.LENGTH, new Random()), BigInteger.probablePrime(RSA.LENGTH, new Random()));
    }

    public RSA getRsa() {
        return rsa;
    }

    @Override public void startConnection() {
        connectionThread.start();
    }

    @Override public void send(Serializable data) {
        connectionThread.send((String) data);
    }

    @Override public void closeConnection() {
        try {
            connectionThread.socket.close();
        } catch (IOException aE) {
            LOGGER.log(Level.WARNING, "Failed to close connection: " + aE.toString());
        }
    }

    private class ConnectionThread extends Thread {

        private Socket socket;

        private ObjectOutputStream outputStream;

        private KeyDTO serverKey;

        @Override public void run() {
            try (Socket pSocket = new Socket(host, port);
                 ObjectOutputStream pOutputStream = new ObjectOutputStream(pSocket.getOutputStream());
                 ObjectInputStream pInputStream = new ObjectInputStream(pSocket.getInputStream())) {
                socket = pSocket;
                outputStream = pOutputStream;
                outputStream.writeObject(rsa.getPublicKey());
                serverKey = (KeyDTO) pInputStream.readObject();
                while (true) {
                    Serializable pData = (Serializable) pInputStream.readObject();
                    callback.accept(pData);
                }
            } catch (Exception aE) {
                LOGGER.log(Level.WARNING, "Connection closed: " + aE.toString());
            }
        }

        public void send(String aMessage) {
            try {
                outputStream.writeObject(encrypt(aMessage));
            } catch (IOException aE) {
                LOGGER.log(Level.WARNING, "Failed to send client message: " + aE.toString());
            }
        }

        private String encrypt(String aMessage) {
            return new BigInteger(aMessage.getBytes()).modPow(this.serverKey.getValue(), this.serverKey.getN()).toString();
        }
    }
}

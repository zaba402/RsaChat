package chat.server;

import chat.Connection;
import chat.KeyDTO;
import chat.RSA;
import chat.client.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Paweł Dąbrowski on 28.12.2016.
 */
public class Server extends Connection {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private ConnectionThread connectionThread = new ConnectionThread();

    private int port;

    private RSA rsa;

    public Server(int aPort) {
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
        connectionThread.getEchoThreads().stream().forEach(aEchoThread -> aEchoThread.send((String) data));
    }

    @Override public void closeConnection() {
        connectionThread.getEchoThreads().stream().forEach(aEchoThread -> {
            try {
                aEchoThread.socket.close();
            } catch (IOException aE) {
                LOGGER.log(Level.WARNING, "Failed to close connection: " + aE.toString());
            }
        });
    }

    private class ConnectionThread extends Thread {

        private List<EchoThread> echoThreads = new ArrayList<>();

        public List<EchoThread> getEchoThreads() {
            return echoThreads;
        }

        @Override public void run() {
            try (ServerSocket pServerSocket = new ServerSocket(port)) {
                while (true) {
                    Socket pSocket = pServerSocket.accept();
                    EchoThread pEchoThread = new EchoThread(pSocket);
                    echoThreads.add(pEchoThread);
                    pEchoThread.start();
                }
            } catch (Exception aE) {
                LOGGER.log(Level.WARNING, "Connection closed: " + aE.toString());
            }
        }

        private class EchoThread extends Thread {

            private Socket socket;

            private ObjectOutputStream outputStream;

            private KeyDTO clientKey;

            EchoThread(Socket aSocket) {
                socket = aSocket;
            }

            @Override public void run() {
                try (ObjectOutputStream pOutputStream = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream pInputStream = new ObjectInputStream(socket.getInputStream())) {
                    outputStream = pOutputStream;
                    outputStream.writeObject(rsa.getPublicKey());
                    clientKey = (KeyDTO) pInputStream.readObject();
                    while (true) {
                        Serializable pData = (Serializable) pInputStream.readObject();
                        callback.accept(pData);
                    }
                } catch (Exception aE) {
                    echoThreads.remove(this);
                    LOGGER.log(Level.WARNING, "Connection closed: " + aE.toString());
                }
            }

            public void send(String aMessage) {
                try {
                    outputStream.writeObject(encrypt(aMessage));
                } catch (IOException aE) {
                    LOGGER.log(Level.WARNING, "Failed to send server message: " + aE.toString());

                }
            }

            private String encrypt(String aMessage) {
                return new BigInteger(aMessage.getBytes()).modPow(clientKey.getValue(), clientKey.getN()).toString();
            }
        }
    }
}

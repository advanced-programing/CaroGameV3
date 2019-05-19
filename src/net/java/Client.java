/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author admin
 */
abstract public class Client {

    protected int[] connectedPlayerIDs = new int[0];
    private volatile boolean autoreset;
    private ConnectionToHub connection;
    
    public Client(String hubHostName, int hubPort) throws IOException {
        connection = new ConnectionToHub(hubHostName, hubPort); 
    }
    
    protected void serverShutdown(String message) {
    }

    protected void playerDisconnected(int departingPlayerID) {
    }

    ; 
        protected void playerConnected(int departingPlayerID) {
    }

    protected void connectionClosedByError(String message) {
    }
    abstract protected void messageReceived(Object message); 
    
    public int getID() {
        return connection.id_number; 
    }
    public void send(Object message) {
        if (message == null) {
            throw new IllegalArgumentException("Null cannot be send as a message"); 
        }
        if (! (message instanceof Serializable)) {
            throw new IllegalArgumentException("Message should be implemented in serializable interface"); 
        }
        if (connection.closed) 
            throw new IllegalStateException("Message cannot be sent. Connection alreadly terminated"); 
        connection.send(message); 
    }   
    private class ConnectionToHub {

        private int id_number;
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private SendThread sendThread;
        private ReceiveThread receiveThread;
        private LinkedBlockingQueue<Object> outgoingMessages;
        private volatile boolean closed;

        public ConnectionToHub(String host, int port) throws IOException{ 
            outgoingMessages = new LinkedBlockingQueue<Object>(); 
            
            System.out.println("CTH " + "hubName " + host);
            System.out.println("CTH" + "hubport " + port);
            socket = new Socket(host, port); 
            out = new ObjectOutputStream(socket.getOutputStream()); 
            out.writeObject("Hello Hub");
            out.flush();
            in = new ObjectInputStream(socket.getInputStream()); 
            try {
                Object response = in.readObject(); 
                id_number = ((Integer)response).intValue();
            } catch (Exception e) {
                throw new IOException("Illegal respond from server."); 
            }
            sendThread = new SendThread(); 
            receiveThread = new ReceiveThread(); 
            sendThread.start();
            receiveThread.start();
        }   
        void close() {
            closed = true;
            sendThread.interrupt();
            receiveThread.interrupt();
            try {
                socket.close();
            } catch (IOException e) {
            }
        }

        synchronized void closedByError(String message) {
            if (!closed) {
                connectionClosedByError(message);
                close();
            }
        }

        void send(Object message) {
            outgoingMessages.add(message); 
        }

        private class SendThread extends Thread {

            @Override
            public void run() {
                System.out.println("Client send thread started.");
                try {
                    while (!closed) {
                        Object message = outgoingMessages.take();
                        if (message instanceof ResetSignal) {
                            out.reset();
                        } else {
                            if (autoreset) {
                                out.reset();
                            }
                            out.writeObject(message);
                            out.flush();
                            if (message instanceof DisconnectMessage) {
                                close();
                            }
                        }
                    }
                } catch (Exception e) {
                    if (!closed) {
                        closedByError("Unexpected internal error in send thread: " + e);
                        System.out.println("\nUnexpected error shuts down client send thread:");
                        e.printStackTrace();
                    }
                } finally {
                    System.out.println("Client send thread terminated.");
                }
            }
        }

        private class ReceiveThread extends Thread {

            public void run() {
                System.out.println("Client Received Thread started");
                try {
                    while (!closed) {
                        Object obj = in.readObject();
                        if (obj instanceof DisconnectMessage) {
                            close();
                            serverShutdown(((DisconnectMessage) obj).message);
                            System.out.println(((DisconnectMessage) obj).message);
                        } else if (obj instanceof StatusMessage) {
                            StatusMessage msg = (StatusMessage) obj;
                            connectedPlayerIDs = msg.players;
                            if (msg.connecting) {
                                playerConnected(msg.playerID);
                            } else {
                                playerDisconnected(msg.playerID);
                            }
                        } else {
                            messageReceived(obj);
                        }
                    }
                }
                catch (Exception e) {
                    if ( ! closed ) {
                        closedByError("Unexpected internal error in receive thread: " + e);
                        System.out.println("\nUnexpected error shuts down client receive thread:");
                        e.printStackTrace();
                    }
                }
                finally {
                    System.out.println("Client receive thread terminated.");
                }
            }
        }
    }

}

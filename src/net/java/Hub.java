/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java;

import java.io.*;
import java.net.*;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author admin
 */
public class Hub {
    private TreeMap<Integer, ConnectionToClient> playerConnections;
    private int nextClientID = 1;
    private LinkedBlockingQueue<Message> incomingMessage; 
    private ServerSocket serverSocket; 
    private Thread serverThread; 
    
    /**
     * if the autoreset = true then the objectOutputStream that are used for
     * transmitting messages to clients are reset before each object is sented
     */
    private volatile boolean autoreset;
    private volatile boolean shutdown; 
    
    
    public Hub(int port) throws IOException{
        playerConnections = new TreeMap<Integer, ConnectionToClient>(); 
        incomingMessage = new LinkedBlockingQueue<Message>(); 
        serverSocket = new ServerSocket(port); 
        System.out.println("Listening for client connections on port " + port);
        serverThread = new ServerThread(); 
        serverThread.setDaemon(true);
        serverThread.start();
        Thread readerThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Message msg = incomingMessage.take(); 
                        messageReceived(msg.playerConnection, msg.message);
                    } catch (Exception e) {
                        System.out.println("Exception while handling received message: " );
                        e.printStackTrace();
                    }
                }
            }
        }; 
        readerThread.setDaemon(true);
        readerThread.start();
    }
    
    public void setAutoreset(boolean auto) {
        autoreset = auto; 
    }
    
    protected void messageReceived(int playerID, Object message) {
        sendToAll(new ForwardMessage(playerID, message));
    }
    
    synchronized private void messageReceived(ConnectionToClient fromConnection, Object message) {
        int sender = fromConnection.getPlayer(); 
        messageReceived(sender, message);
    }
    protected void extraHandshake(int playerID, ObjectInputStream in,
            ObjectOutputStream out) throws IOException {
    }

    ; 

    synchronized public void sendToAll(Object message) {
        if (message == null) {
            throw new IllegalArgumentException("Null cannot be sent as a message!");
        }
        if (!(message instanceof Serializable)) {
            throw new IllegalArgumentException("Message must implement the serializable interface!");
        }
        for (ConnectionToClient pc : playerConnections.values()) {
            pc.send(message);
        }
    }

    protected void playerConnected(int ID) {
    }
    synchronized private void connectionToClientClosedWithError(ConnectionToClient playerConnection, String message) {
        int ID = playerConnection.getPlayer(); 
        if(playerConnections.remove(ID) != null) {
            StatusMessage sm = new StatusMessage(ID, false, getPlayerList()); 
            sendToAll(sm);
        }
    }

    private void playerDisconnected(int playerID) {
    }
    private class Message {
        ConnectionToClient playerConnection;
        Object message;
    }

    synchronized public int[] getPlayerList() {
        int[] players = new int[playerConnections.size()];
        int i = 0;
        for (int p : playerConnections.keySet()) {
            players[i++] = p;
        }
        return players;
    }

    synchronized private void acceptConnection(ConnectionToClient newConnection) {
        int ID = newConnection.getPlayer();
        playerConnections.put(ID, newConnection);
        StatusMessage sm = new StatusMessage(ID, true, getPlayerList());
        sendToAll(sm);
        playerConnected(ID);
        System.out.println("Connection just accept form client " + ID);
    }
    synchronized private void clientDisconnected(int playerID) {
    if (playerConnections.containsKey(playerID)) {
        playerConnections.remove(playerID); 
        StatusMessage sm = new StatusMessage(playerID, false, getPlayerList()); 
        sendToAll(sm);
        playerDisconnected(playerID); 
        System.out.println("Connection with client number "  + playerID + " closed by DisconnectMessage from client" ); 
        }
    }
    
    public void shutdownHub() {
        shutdownServerSocket(); 
        sendToAll(new DisconnectMessage("*shutdown*"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
        }
        for (ConnectionToClient pc: playerConnections.values())
            pc.close();
    }
    public void shutdownServerSocket() {
        if (serverThread == null)  
            return; 
        incomingMessage.clear();
        shutdown = true; 
        try {
            serverSocket.close();
        } catch(IOException e) {
        }
        serverThread = null; 
        serverSocket = null; 
    }
    private class ServerThread extends Thread{
        @Override
        public void run() {
            try {
                while (!shutdown) {
                    Socket connection = serverSocket.accept(); 
                    if (shutdown) {
                        System.out.println("Listener socket has shutdown");
                        break; 
                    }
                    new ConnectionToClient(incomingMessage, connection); 
                }
            } catch(Exception e) {
                if (shutdown) 
                    System.out.println("Listener socket has shutdown");
                else 
                    System.out.println("Listener socket has shutdown by errow: " + e);
            }
        }
    }
        
    private class ConnectionToClient {
        private int playerID;
        private BlockingQueue<Message> incomingMessages;
        private LinkedBlockingQueue<Object> outgoingMessages;
        private Socket connection;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private volatile boolean closed;
        private Thread sendThread;
        private volatile Thread receiveThread;

        public ConnectionToClient(BlockingQueue<Message> receivedMessageQueue, Socket connection) {
            this.connection = connection;
            incomingMessages = receivedMessageQueue;
            outgoingMessages = new LinkedBlockingQueue<Object>();
            sendThread = new SendThread();
            sendThread.start();
        }

        private int getPlayer() {
            return playerID;
        }

        void send(Object obj) {
            if (obj instanceof DisconnectMessage) {
                outgoingMessages.clear();
            }
            outgoingMessages.add(obj);
        }

        void close() {
            closed = true;
            sendThread.interrupt();
            if (receiveThread != null) {
                receiveThread.interrupt();
            }
            try {
                connection.close();
            } catch (IOException e) {

            }
        }

        private void closedWithError(String message) {
            connectionToClientClosedWithError(this, message);
            close();
        }

        private class SendThread extends Thread {

            public void run() {
                try {
                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());
                    String handle = (String) in.readObject();
                    if (!"Hello Hub".equals(handle)) {
                        throw new Exception("Incorrect hello string received from client.");
                    }
                    synchronized (Hub.this) {
                        playerID = nextClientID++;
                    }
                    out.writeObject(playerID);
                    out.flush();
                    extraHandshake(playerID, in, out);
                    acceptConnection(ConnectionToClient.this);
                    receiveThread = new ReceiveThread(); 
                    receiveThread.start(); 
                } 
                catch (Exception e) {
                    try {
                        closed = true;
                        connection.close();
                    } 
                    catch (Exception e1) {
                    }
                    System.out.println("\nError while setting up connection: " + e);
                    e.printStackTrace();
                    return;
                }
                try {
                    while (!closed) {
                        try {
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
                        catch (InterruptedException e) {
                            //connection is closed. 
                        }
                    }
                } 
                catch (Exception e) {
                    if (!closed) {
                        closedWithError("Error while sending data to client");
                        System.out.println("Hub send thread terminated");
                        e.printStackTrace();
                    }
                }
            }
        }
        private class ReceiveThread extends Thread {
            public void run() {
                try{
                    while (!closed) {
                        try {
                            Object message = in.readObject(); 
                            Message msg = new Message(); 
                            msg.playerConnection = ConnectionToClient.this; 
                            msg.message = message; 
                            if (! (message instanceof DisconnectMessage)) 
                              incomingMessages.put(msg);
                            else {
                                closed = true; 
                                outgoingMessages.clear();
                                out.writeObject("*goodbye*");
                                out.flush();
                                clientDisconnected(playerID); 
                                close(); 
                            }
                        }
                        catch(InterruptedException e) {
                        }                        
                    }
                } catch (Exception e) {
                    if (!closed) {
                        closedWithError("Error while sending data to client");
                        System.out.println("Hub send thread terminated");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

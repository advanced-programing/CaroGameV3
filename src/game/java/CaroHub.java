/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.java;
import java.io.IOException;
import net.java.Hub; 


/**
 *
 * @author admin
 */
public class CaroHub extends Hub {
    private CaroState state; 
    public CaroHub(int port) throws IOException {
        super(port); 
        state = new CaroState(); 
        setAutoreset(true); 
    }
    @Override
    protected void messageReceived(int playerID, Object message) {
        state.applyMessage(playerID, message);
        sendToAll(state);
    }
    @Override
    protected void playerConnected(int playerID) {
        if (getPlayerList().length == 2) {
            shutdownServerSocket(); 
            state.startFristGame();
            sendToAll(state);
            System.out.println("Enjoy your game guys");
        }
    }
    
    
}

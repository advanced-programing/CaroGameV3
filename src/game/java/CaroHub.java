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
    
}

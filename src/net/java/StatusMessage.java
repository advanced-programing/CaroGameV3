/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java;

import java.io.Serializable;

/**
 *
 * @author admin
 */
class StatusMessage implements Serializable{
    public final int playerID;
    /**
     * True if the player has just connected,  
     * false if the player has just disconnected. 
     */
    public final boolean connecting; 
    public final int []players; 
    public StatusMessage(int playerID, boolean connecting, int[] players) {
        this.playerID = playerID; 
        this.connecting = connecting; 
        this.players = players; 
    }
}

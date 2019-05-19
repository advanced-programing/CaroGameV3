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
public class DisconnectMessage implements Serializable{
    final public String message; 
    public DisconnectMessage(String message) {
        this.message = message; 
    }
    
}

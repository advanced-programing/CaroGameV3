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

public class ForwardMessage implements Serializable {
    public final Object message; 
    public final int senderID; 
/**
 * wrap a message which is sent by client
 * @param senderID
 * @param message 
 */
    public ForwardMessage(int senderID, Object message) {
        this.senderID = senderID; 
        this.message = message; 
    }
}

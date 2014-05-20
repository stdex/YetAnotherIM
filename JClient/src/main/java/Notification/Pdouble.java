/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Notification;

/**
 *
 * @author user
 */
public class Pdouble {
      
    private Object idTray;
    private String mode;

    public Pdouble(Object idTray, String mode) {
        this.idTray = idTray;
        this.mode = mode;
    }    

    public Object getIdTray() {
        return idTray;
    }

    public void setIdTray(Object idTray) {
        this.idTray = idTray;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
    
}

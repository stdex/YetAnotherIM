/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Notification;

import Core.Contact;
import java.awt.SystemTray;
import java.awt.TrayIcon;

/**
 *
 * @author Rostunov_Sergey
 */
public class Tdouble {
    
    private Object idTray;
    private TrayIcon trayIcon;
    private SystemTray tray;

    public Tdouble(Object idTray, TrayIcon trayIcon, SystemTray tray) {
        this.idTray = idTray;
        this.trayIcon = trayIcon;
        this.tray = tray;
    }

    public void setTray(SystemTray tray) {
        this.tray = tray;
    }

    public SystemTray getTray() {
        return tray;
    }

    public Object getIdTray() {
        return idTray;
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public void setIdTray(Object idTray) {
        this.idTray = idTray;
    }

    public void setTrayIcon(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }

    @Override
    public String toString() {
        return "Tdouble:\n" + ((Contact)idTray).getUsername();
    }

}
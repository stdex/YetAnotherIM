package Notification;


import Core.Contact;
import Core.UICore;
import UI.ChatUI;
import UI.SendSubUI;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;


public class TrayUtil {

/*
    private static Image nullImage = new ImageIcon("").getImage();
    private static Image defalutImage = new ImageIcon(Core.Main.class.getResource("/Images/icon_msg.png")).getImage();
    private static TrayIcon trayIcon;
    private static Timer timer;
*/

    public static Map<Object,Tdouble> mTrays = new HashMap<Object,Tdouble>();
    public static HashSet<Object> listContacts = new HashSet<Object>();
    public static HashSet<SystemTray> lTray = new HashSet<SystemTray>();
    
    public TrayUtil(String mode, Contact s_contact, String titlef) {

        if(mode == "chat") {
            if(!listContacts.contains(s_contact)) {
                createTray(mode, s_contact, titlef);
            }
            listContacts.add(s_contact);
        }
        else if(mode == "subscribe") {
            if(!listContacts.contains(titlef)) {
                createTray(mode, s_contact, titlef);
            }
            listContacts.add(titlef);
        }

    }

    public void createTray(final String mode, final Contact s_contact, final String titlef) {
    
    final Image nullImage = new ImageIcon("").getImage();
    final Image defalutImage = new ImageIcon(Core.Main.class.getResource("/Images/icon_msg.png")).getImage();
    final TrayIcon trayIcon = new TrayIcon(defalutImage, "");
    final Object idTray = (s_contact != null)?s_contact:titlef;
          System.out.println(idTray.toString());      
        Timer timer = null;
        if (SystemTray.isSupported()) {
            try {
                final SystemTray tray = SystemTray.getSystemTray();
                //lTray.add(tray);
                mTrays.put(idTray, new Tdouble(idTray, trayIcon, tray));
                //trayIcon = new TrayIcon(defalutImage, "");
                trayIcon.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        
                   if(mode == "chat") {
                        ChatUI targetUI = UICore.getChatUIList().findUI(s_contact);

                        if (targetUI == null)
                            UICore.getChatUIList().add(targetUI = new ChatUI(s_contact));
                    
                    // Output the message in sender ChatUI.
                    //targetUI.append(s_contact.getTitle(), to, message, datetime);
                    targetUI.toFront();
                   }
                   else if (mode == "subscribe") {
                        SendSubUI targetUI = UICore.getSubsUIList().findUI(titlef);
        
                        if (targetUI == null)
                            UICore.getSubsUIList().add(targetUI = new SendSubUI(titlef));
                        
                        targetUI.toFront();
                   }
                        
                        
                        tray.remove(trayIcon);
                        mTrays.remove(idTray);
                        listContacts.remove(idTray);
                        /*
                        TrayUtil frame = (TrayUtil) UIPools.getUI("mainFrame");
                        if (frame.isVisible()) {
                            frame.setVisible(false);
                        } else {
                            frame.setVisible(true);
                            frame.setExtendedState(JFrame.NORMAL);
                        }
                                */
                    }
                });
                tray.add(trayIcon);
            } catch (AWTException ex) {
                System.err.println(ex.getMessage());
            }
            if (timer == null) {
                timer = new Timer(500, new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (trayIcon.getImage() == nullImage) {
                            trayIcon.setImage(defalutImage);
                        } else {
                            trayIcon.setImage(nullImage);
                        }
                    }
                });
            }
        }
        
        startBlink(timer);
    }

    public void startBlink(Timer timer) {
        if (timer != null) {
            timer.start();
        }
    }

    public void stopBlink(Timer timer) {
        if (timer != null) {
            timer.stop();
        }
        //trayIcon.setImage(defalutImage);
    }
    
    public static void disposeAllMsgTray() {
        
        System.out.println(mTrays);
        
        for(Entry<Object, Tdouble> entry : mTrays.entrySet()) {
            entry.getValue().getTray().remove((TrayIcon) entry.getValue().getTrayIcon());
        }
        
        mTrays.clear();
        listContacts.clear();

    }
    
    public static void disposeMsgTray(Contact s_contact, String titlef) {
    
        Object idTray = (s_contact != null)?s_contact:titlef;

        for(Entry<Object, Tdouble> entry : mTrays.entrySet()) {
            if(idTray.equals(entry.getValue().getIdTray())) {
                entry.getValue().getTray().remove((TrayIcon) entry.getValue().getTrayIcon());
                mTrays.remove(idTray);
                listContacts.remove(idTray);
            }
        }
        
    }
}
package Notification;


import Core.Contact;
import Core.UICore;
import UI.ChatUI;
import UI.SendSubUI;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static Map<Object,Pdouble> aTrays = new HashMap<Object,Pdouble>();
    public static HashSet<Object> listContacts = new HashSet<Object>();
    public static HashSet<SystemTray> lTray = new HashSet<SystemTray>();
    public static int showTray = 0;
    public static boolean isFirstClick = false;
    
    public static TrayIcon tIcon;
    public static SystemTray tR;
    

     /*
    final static Image nullImage = new ImageIcon("").getImage();
    final static URL resource = TrayUtil.class.getResource("/Images/icon_msg.png");
    final static Image defalutImage = Toolkit.getDefaultToolkit().getImage(resource);
    final static TrayIcon trayIcon = new TrayIcon(defalutImage);
    final static SystemTray tray = SystemTray.getSystemTray();
    */  
    
    public TrayUtil(String mode, Contact s_contact, String titlef) {
        
        if(showTray == 0) {
            createOneTray();
        }
        
        showTray = 1;
        
        if(mode == "chat") {
            aTrays.put(s_contact, new Pdouble(s_contact, mode));
        }
        else if(mode == "subscribe") {
            aTrays.put(titlef, new Pdouble(titlef, mode));
        }
        
        //System.out.println(aTrays.toString());
        /*    
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
        */

    }
    
    public void createOneTray() {

    final Image nullImage = new ImageIcon("").getImage();
    URL resource = getClass().getResource("/Images/icon_msg.png");
    final Image defalutImage = Toolkit.getDefaultToolkit().getImage(resource);
  
    final TrayIcon trayIcon = new TrayIcon(defalutImage);
    final SystemTray tray = SystemTray.getSystemTray();
    
    tIcon = trayIcon;
    tR = tray;
    
    Timer timer = null;
        
        if (SystemTray.isSupported()) {
            try {
                
                trayIcon.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                            for(Entry<Object, Pdouble> entry : aTrays.entrySet()) {
                                
                                if("chat".equals(entry.getValue().getMode())) {
                                 ChatUI targetUI = UICore.getChatUIList().findUI((Contact) entry.getValue().getIdTray());

                                 if (targetUI == null)
                                     UICore.getChatUIList().add(targetUI = new ChatUI((Contact) entry.getValue().getIdTray()));


                                 targetUI.toFront();
                                }
                                else if ("subscribe".equals(entry.getValue().getMode())) {
                                     SendSubUI targetUI = UICore.getSubsUIList().findUI((String) entry.getValue().getIdTray());

                                     if (targetUI == null)
                                         UICore.getSubsUIList().add(targetUI = new SendSubUI((String) entry.getValue().getIdTray()));

                                     targetUI.toFront();
                                }
                                
                                aTrays.remove(entry.getKey());
                                
                                if(aTrays.isEmpty())
                                {
                                  tray.remove(trayIcon);
                                  aTrays.clear();
                                  showTray = 0;
                                }
                                
                                break;
                            }                        
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

    /*
    public void createTray(final String mode, final Contact s_contact, final String titlef) {
  
      
    final Image nullImage = new ImageIcon("").getImage();
    URL resource = getClass().getResource("/Images/icon_msg.png");
    System.out.println(resource.toString());
    final Image defalutImage = Toolkit.getDefaultToolkit().getImage(resource);
    //System.out.println(defalutImage);  
    final TrayIcon trayIcon = new TrayIcon(defalutImage, "");
    final Object idTray = (s_contact != null)?s_contact:titlef;
                
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

                        TrayUtil frame = (TrayUtil) UIPools.getUI("mainFrame");
                        if (frame.isVisible()) {
                            frame.setVisible(false);
                        } else {
                            frame.setVisible(true);
                            frame.setExtendedState(JFrame.NORMAL);
                        }

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
    */

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
    
        
    public static void disposeOneTray(Contact s_contact, String titlef) {
    
        Object idTray = (s_contact != null)?s_contact:titlef;
        
        System.out.println(idTray);
        
        aTrays.remove(idTray);
        System.out.println(aTrays);
        
        if(aTrays.isEmpty())
        {
            tR.remove(tIcon);
            aTrays.clear();
            showTray = 0;
        }
        
    }
}
package UI;

import Core.AccountDetail;
import Core.Contact;
import Core.NetworkManager;
import Core.Opcode;
import Core.Packet;
import Core.TextAndIcon;
import Core.UICore;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class MasterUI extends JFrame implements Opcode
{
    private static JPanel loginPanel;
    private static JPanel contactPanel;
    
    /* Menu */
    private static JMenuBar menuBar;
    
    private JMenu roomMenu;
    
    private JMenuItem miRoomJoin;
    private JMenuItem miRoomCreate;
    
    private static boolean isLoginUI;
    
    /* Login Interface */
    private JButton btnLogin;
    private JButton btnExit;
    private JButton btnReg;
    private JButton btnSubscribe;
    private JButton btnOptions;
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    //private JComboBox cbLoginAsStatus;
    
    private JLabel lblUsername;
    private JLabel lblPassword;
    //private JLabel lblLoginAsStatus;
    
    /* Contact List Interface */
    private JComboBox cbStatus;
    public static JList contactList;
    private JScrollPane contactListPane;
    
    private JLabel lblTitle;
    private JLabel lblPSM;
    
    private JTextField txtTitle;
    private JTextField txtPSM;
    
    private JButton btnAddContact;
    private JButton btnRemoveContact;
    
    public static DefaultListModel model;
    
    //private String[] loginAsStatus = {"Доступен", "Отсутствует", "Занят", "Offline"};
    public static String[] status = {"Доступен", "Отсутствует", "Занят", "Offline", "Выход"};
    public static ImageIcon[] images;

    public static DefaultListModel sortedmodel;


    
    public MasterUI()
    {
        setTitle("Вход");
        setLayout(null);
        
        isLoginUI = true;
        
        loginPanel = new JPanel(null);
        contactPanel = new JPanel(null);
        
        //miRoomJoin = new JMenuItem("Join An Existing Room");
        //miRoomCreate = new JMenuItem("Create Room");
        
        //roomMenu = new JMenu("Room");
        //roomMenu.add(miRoomJoin);
        //roomMenu.add(miRoomCreate);
        
        menuBar = new JMenuBar();
        //menuBar.add(roomMenu);
        
        BufferedImage wPic;
        try {
            wPic = ImageIO.read(this.getClass().getResource("/Images/icon_main.png"));
            JLabel wIcon = new JLabel(new ImageIcon(wPic));
            loginPanel.add(wIcon);
            wIcon.setBounds(45, 50, 180, 120);
        } catch (IOException ex) {
            Logger.getLogger(MasterUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        /*
        BufferedImage myPicture;
        try {
            //System.out.println(getClass().getResource("/Images/icon_main.png").getPath());
            myPicture = ImageIO.read(new File(getClass().getResource("/Images/icon_main.png").getPath()));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            loginPanel.add(picLabel);
            picLabel.setBounds(45, 50, 180, 120);
        } catch (IOException ex) {
            Logger.getLogger(MasterUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
  
        /* Login UI */
        lblUsername = new JLabel("Логин");
        lblPassword = new JLabel("Пароль");
        //lblLoginAsStatus = new JLabel("Статус");
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        //cbLoginAsStatus = new JComboBox(loginAsStatus);
        btnLogin = new JButton("Вход");
        btnReg = new JButton("Регистрация");
        //btnExit = new JButton("Выход");
        btnSubscribe = new JButton("Подписки");
        btnOptions = new JButton("Настройки");
        
        btnLogin.setBackground(new Color(59, 89, 182));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        btnReg.setBackground(new Color(59, 89, 182));
        btnReg.setForeground(Color.WHITE);
        btnReg.setFocusPainted(false);
        btnReg.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        btnSubscribe.setBackground(new Color(59, 89, 182));
        btnSubscribe.setForeground(Color.WHITE);
        btnSubscribe.setFocusPainted(false);
        btnSubscribe.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        btnOptions.setBackground(new Color(59, 89, 182));
        btnOptions.setForeground(Color.WHITE);
        btnOptions.setFocusPainted(false);
        btnOptions.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        loginPanel.add(lblPassword);
        loginPanel.add(lblUsername);
        //loginPanel.add(lblLoginAsStatus);
        loginPanel.add(txtPassword);
        loginPanel.add(txtUsername);
        //loginPanel.add(cbLoginAsStatus);
        loginPanel.add(btnLogin);
        loginPanel.add(btnReg);
        //loginPanel.add(btnExit);
        
        lblPassword.setBounds(25, 260, 100, 25);
        lblUsername.setBounds(25, 210, 100, 25);
        //lblLoginAsStatus.setBounds(25, 230, 100, 25);
        txtPassword.setBounds(25, 280, 220, 25);
        txtUsername.setBounds(25, 230, 220, 25);
        //cbLoginAsStatus.setBounds(25, 250, 220, 25);
        btnLogin.setBounds(25, 330, 80, 25);
        btnReg.setBounds(120, 330, 125, 25);
        //btnExit.setBounds(150, 420, 100, 25);
        
        /* Contact List Interface */
        lblTitle = new JLabel();
        //lblPSM = new JLabel();
        
        //txtTitle = new JTextField();
        //txtPSM = new JTextField();
        
        cbStatus = new JComboBox(status);
        
        //btnAddContact = new JButton("Добавить контакт");
        //btnRemoveContact = new JButton("Удалить контакт");
        
        model = new DefaultListModel();
        contactList = new JList(model);
        contactListPane = new JScrollPane(contactList);
        
        contactPanel.add(lblTitle);
        //contactPanel.add(txtTitle);
        //contactPanel.add(lblPSM);
        //contactPanel.add(txtPSM);
        contactPanel.add(cbStatus);
        //contactPanel.add(btnAddContact);
        //contactPanel.add(btnRemoveContact);
        contactPanel.add(contactListPane);
        contactPanel.add(btnSubscribe);
        contactPanel.add(btnOptions);
        
        lblTitle.setBounds(10, 10, 240, 25);
        //txtTitle.setBounds(10, 10, 240, 25);
        //lblPSM.setBounds(15, 35, 240, 25);
        //txtPSM.setBounds(15, 35, 240, 25);
        cbStatus.setBounds(10, 40, 245, 25);
        //btnAddContact.setBounds(10, 100, 120, 25);
        //btnRemoveContact.setBounds(135, 100, 120, 25);
        contactListPane.setBounds(10, 110, 245, 230);
        btnSubscribe.setBounds(10, 75, 110, 25);
        btnOptions.setBounds(145, 75, 110, 25);
        
        loginPanel.setBounds(0, 0, 270, 500);
        contactPanel.setBounds(270 , 0, 270, 350);
        
        lblTitle.setFont(new Font("sansserif", Font.BOLD, 16));
        lblTitle.setVisible(true);
        //lblPSM.setFont(new Font("sansserif", Font.PLAIN, 12));
        
        //txtTitle.setVisible(false);
        //txtPSM.setVisible(false);
        
//        miRoomJoin.setEnabled(false);
//        miRoomCreate.setEnabled(false);
        
        add(loginPanel);
        add(contactPanel);
        
        setJMenuBar(menuBar);
        setSize(270, 420);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        
        btnLogin.addActionListener(actListener);
        cbStatus.addActionListener(actListener);
        //btnExit.addActionListener(actListener);
        //btnAddContact.addActionListener(actListener);
        //btnRemoveContact.addActionListener(actListener);
        btnSubscribe.addActionListener(actListener);
        
//        miRoomJoin.addActionListener(menuListener);
//        miRoomCreate.addActionListener(menuListener);
        
        //txtTitle.addFocusListener(focusListener);
//      txtPSM.addFocusListener(focusListener);
        
        txtUsername.addKeyListener(loginKeyListener);
        txtPassword.addKeyListener(loginKeyListener);
        
        //txtTitle.addKeyListener(contactKeyListener);
//      txtPSM.addKeyListener(contactKeyListener);
        
        lblTitle.addMouseListener(mouseListener);
//      lblPSM.addMouseListener(mouseListener);
        contactList.addMouseListener(mouseListener);
        
        addWindowListener(winListener);
        
            
        btnReg.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent arg0)   
            {  
                new RegistrationUI();
            }  
        }); 
        
        
        btnSubscribe.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent arg0)   
            {  
                new SubscribeUI();
            }  
        });

        /*
        http://skype2gmail.blogspot.ru/2011/05/java-tray-icon-transparency.html
        
        <groupId>com.github.taksan</groupId>
        <artifactId>native-tray-adapter</artifactId>
        <version>1.1</version>
        
        https://github.com/taksan/native-tray-adapter
        
        SystemTrayAdapter trayAdapter = SystemTrayProvider.getSystemTray();  
        URL imageUrl = getClass().getResource("myImage.svg");  
        String tooltip = "I'm transparent under linux!";   
        PopupMenu popup = produceMyPopupMenu();  
        TrayIconAdapter trayIconAdapter = trayAdapter.createAndAddTrayIcon(  
           imageUrl,   
           tooltip,  
           popup); 
        */

            URL resource = getClass().getResource("/Images/icon_tray.png");
            Image image = Toolkit.getDefaultToolkit().getImage(resource);
            //Image image = Toolkit.getDefaultToolkit().createImage("/Resource/Images/icon_tray.png");
            setIconImage(image);
            //JLabel lbl;
            ActionListener exitAL = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };
            if (SystemTray.isSupported()) {
                PopupMenu pm = new PopupMenu();
                MenuItem miExit = new MenuItem("Exit");
                miExit.addActionListener(exitAL);
                MenuItem miRestore = new MenuItem("Restore");
                miRestore.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        restoreWindow();
                    }
                });
                //pm.add(miRestore);
                //pm.addSeparator();
                //pm.add(miExit);
                //lbl = new JLabel("<html><font color=\"blue\">System tray is supported</font></html>");
                SystemTray st = SystemTray.getSystemTray();
                //TrayIcon ti = new TrayIcon(image, "Double click to restore window", pm);
                TrayIcon ti = new TrayIcon(image);
                ti.addMouseListener(new TrayMouseListener());
                try {
                    st.add(ti);
                    addWindowListener(new WindowMinimizeListener());
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            } else {
            //    lbl = new JLabel("<html><font color=\"red\">System tray is NOT supported</font></html>");
            }
            //lbl.setVerticalAlignment(JLabel.CENTER);
            //lbl.setHorizontalAlignment(JLabel.CENTER);
            //JButton btn = new JButton("Click to close application");
            //btn.addActionListener(exitAL);
            //getContentPane().setBackground(Color.white);
            //getContentPane().add(lbl, BorderLayout.CENTER);
            //getContentPane().add(btn, BorderLayout.SOUTH);
            //setSize(300, 100);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
 
            contactList.setCellRenderer(new TextAndIconListCellRenderer(2));

		// setup mappings for which icon to use for each value
		Map<Object, Icon> icons = new HashMap<Object, Icon>();
		icons.put("Доступен", new ImageIcon(getClass().getResource("/Images/icon_online.png")));
		icons.put("Отсутствует", new ImageIcon(getClass().getResource("/Images/icon_away.png")));
		icons.put("Занят", new ImageIcon(getClass().getResource("/Images/icon_busy.png")));
                icons.put("Offline", new ImageIcon(getClass().getResource("/Images/icon_offline.png")));
                icons.put("Выход", new ImageIcon(getClass().getResource("/Images/icon_exit.png")));
	
		cbStatus.setRenderer(new IconListRenderer(icons));
        
    }
    
    public void setAccountDetail(int guid, String username, String title, String psm, int status)
    {
        AccountDetail.init(guid, username, title, psm, status);
        
        lblTitle.setText(AccountDetail.getDisplayTitle());
        
        //lblPSM.setText(psm.equals("") ? "<Здесь персональный статус>" : psm);
        //lblPSM.setFont(new Font("sansserif", psm.equals("") ? Font.ITALIC : Font.PLAIN, 12));
        //lblPSM.setForeground(psm.equals("") ? Color.GRAY : Color.BLACK);
        
        cbStatus.setSelectedIndex(AccountDetail.getStatus());
        
        updateUITitle();
    }
    
    public void updateUITitle()
    {
        setTitle(AccountDetail.getUITitle());
    }
    
    public void enableLoginInput(boolean enable)
    {
        txtUsername.setEnabled(enable);
        txtPassword.setEnabled(enable);
        //cbLoginAsStatus.setEnabled(enable);
    }
    
    public void addContact(Contact c)
    {
        model.addElement(c);
    }
    
    public Contact searchContact(int guid)
    {

        for (int i = 0; i < model.getSize(); i++)
            if (((Contact)model.elementAt(i)).getGuid() == guid)
                return (Contact)model.elementAt(i);
        
        return null;
    }
    
    public void UpdateContactStatus(int guid, int status)
    {
        Contact c = searchContact(guid);
        
        if (c != null)
        {
            c.setStatus(status);
            
            //TODO: sort contact list 
            /*
            ArrayList<Contact> sortedListCont = new ArrayList<Contact>();
            
            //System.out.println("sortedListCont:" + sortedListCont);
            sortedListCont.clear();
            
            for (int i = 0; i < model.getSize(); i++) {
                sortedListCont.add((Contact)model.elementAt(i));
            }
         
            Collections.sort(sortedListCont, new ContactComp());
  
            model.clear();
            
            for(Contact p : sortedListCont){
                model.addElement(p);
            }
            
            //System.out.println("model:" + model); 
            
            contactList.setModel(model);    
            */
            
            ListCellRenderer renderer = contactList.getCellRenderer();
            
            contactList.repaint();
            contactList.setModel(model);
            contactList.setCellRenderer(renderer);
            contactList.repaint();
        }
        //System.out.println(model);
    }
    
    public void UpdateContactDetail(int guid, String title, String psm)
    {
        Contact c = searchContact(guid);
        ChatUI ui = UICore.getChatUIList().findUI(c);
        
        if (c != null)
        {
            if (title != null)
                c.setTitle(title);
            
            /*
            if (psm != null)
                c.setPSM(psm);
            */
            
            contactList.repaint();
        }
        
        if (ui != null)
            ui.UpdateTitle();
    }

    
    public void login()
    {
        enableLoginInput(false);
        
        if (txtUsername.getText().equals(""))
        {
            UICore.showMessageDialog("Введите ваш логин.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            enableLoginInput(true);
            return;
        }
        
        if (txtPassword.getPassword().length == 0)
        {
            UICore.showMessageDialog("Введите ваш пароль.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            enableLoginInput(true);
            return;
        }
        
        NetworkManager.login(txtUsername.getText(), new String(txtPassword.getPassword()), 0);
        //NetworkManager.login(txtUsername.getText(), new String(txtPassword.getPassword()), cbLoginAsStatus.getSelectedIndex());
    }
    
    public void resetUI()
    {
        if (isLoginUI)
        {
            txtPassword.setText("");
        }
        else
        {
            model.clear();
            lblTitle.setText("");
//          lblPSM.setText("");
            cbStatus.setSelectedIndex(0);
        }
    }
    
    public void enableRoomMenu(boolean enable)
    {
//        miRoomJoin.setEnabled(enable);
//        miRoomCreate.setEnabled(enable);
    }

    
    ActionListener actListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnLogin))
                login();
    /*        
            if(e.getSource().equals(btnReg))
            {
                //JFrame frame = new JFrame("Table");
                //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //registration();
                //System.out.println("123");
                //new RegistrationUI();
                
                //frame.showRegUI()
                //UICore.getRegistrationUI().showRegUI();
                //new RegistrationUI();
                //frame.setVisible(true);
            }   
      */      
            /*
            if (e.getSource().equals(btnAddContact))
                new AddContactUI();
            
            if (e.getSource().equals(btnExit))
                System.exit(0);
            */
            /*
            if (e.getSource().equals(btnRemoveContact))
            {
                if (contactList.getSelectedIndex() > -1)
                {
                    if (JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить этоот контакт?", "Удаление контакта", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                        return;
                    
                    Contact c = (Contact)model.getElementAt(contactList.getSelectedIndex());
                    ChatUI chatUI = UICore.getChatUIList().findUI(c);
                    
                    int guid = c.getGuid();
                    
                    if (chatUI != null)
                        UICore.getChatUIList().remove(chatUI);
                    
                    
                    Packet p = new Packet(CMSG_REMOVE_CONTACT);
                    p.put(guid);
                    
                    NetworkManager.SendPacket(p);
                    
                    model.removeElementAt(contactList.getSelectedIndex());
                    
                    contactList.repaint();
                }
                else
                    JOptionPane.showMessageDialog(null, "Не выбрано ни одного контакта!", "Удаление контакта", JOptionPane.ERROR_MESSAGE);
            }
            */
            
            if (e.getSource().equals(cbStatus))
            {
                // You can only change status when you are logged in
                if (isLoginUI)
                    return;
                
                // Only logout have special handle, status change only inform server
                if (cbStatus.getSelectedIndex() != 4)
                {
                    Packet p = new Packet(CMSG_STATUS_CHANGED);
                    p.put(cbStatus.getSelectedIndex());
                    
                    NetworkManager.SendPacket(p);
                }
                else 
                    NetworkManager.SendPacket(new Packet(CMSG_LOGOUT));
            }
        }
    };
    
    /*
    ActionListener menuListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(miRoomJoin))
                new RoomFormUI(RoomFormUI.JOIN_ROOM);
            
            if (e.getSource().equals(miRoomCreate))
                new RoomFormUI(RoomFormUI.CREATE_ROOM);
        }
    };
    */
    
    FocusListener focusListener = new FocusAdapter()
    {
        public void focusLost(FocusEvent e)
        {
            /*
            if (e.getSource().equals(txtTitle))
            {
                //txtTitle.setVisible(false);
                lblTitle.setVisible(true);
                
                return;
            }
           
            if (e.getSource().equals(txtPSM))
            {
                txtPSM.setVisible(false);
                lblPSM.setVisible(true);
                
                return;
            }*/
        }
    };
    
    KeyListener loginKeyListener = new KeyAdapter()
    {
        public void keyReleased(KeyEvent e)
        {
            // Only handle enter key in Chat Interface.
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
                login();
        }
    };
    
    KeyListener contactKeyListener = new KeyAdapter()
    {
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                /*
                if (e.getSource().equals(txtTitle))
                {
                    txtTitle.setVisible(false);
                    lblTitle.setVisible(true);
                    
                    return;
                }
                
                if (e.getSource().equals(txtPSM))
                {
                    txtPSM.setVisible(false);
                    lblPSM.setVisible(true);
                    
                    return;
                }
                */
            }
            
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                
                 /*
                if (e.getSource().equals(txtTitle))
                {
                    String newTitle = txtTitle.getText().trim();
                    
                    if (!newTitle.equals(AccountDetail.getTitle()))
                    {
                        AccountDetail.setTitle(newTitle);
                        
                        lblTitle.setText(AccountDetail.getDisplayTitle());
                        
                        Packet p = new Packet(CMSG_TITLE_CHANGED);
                        p.put(newTitle);
                        
                        NetworkManager.SendPacket(p);
                    }
                    
                    txtTitle.setVisible(false);
                    lblTitle.setVisible(true);
                }
                
               
                if (e.getSource().equals(txtPSM))
                {
                    String newPSM = txtPSM.getText().trim();
                    
                    if (!newPSM.equals(AccountDetail.getPSM()))
                    {
                        AccountDetail.setPSM(newPSM);
                        
                        lblPSM.setText(newPSM.equals("") ? "<Здесь персональный статус>" : newPSM);
                        lblPSM.setFont(new Font("sansserif", newPSM.equals("") ? Font.ITALIC : Font.PLAIN, 12));
                        lblPSM.setForeground(newPSM.equals("") ? Color.GRAY : Color.BLACK);
                        
                        Packet p = new Packet(CMSG_PSM_CHANGED);
                        p.put(newPSM);
                        
                        NetworkManager.SendPacket(p);
                    }
                    
                    txtPSM.setVisible(false);
                    lblPSM.setVisible(true);
                }
                */
                updateUITitle();
            }
        }
    };
    
    MouseListener mouseListener = new MouseAdapter()
    {
        public void mouseClicked(MouseEvent e)
        {
            /*
            if (e.getSource().equals(lblTitle))
            {
                
                lblTitle.setVisible(false);
                txtTitle.setVisible(true);
                
                txtTitle.setText(AccountDetail.getTitle());
                txtTitle.requestFocusInWindow();
               
            }
            
            /*
            if (e.getSource().equals(lblPSM))
            {
                lblPSM.setVisible(false);
                txtPSM.setVisible(true);
                
                txtPSM.setText(AccountDetail.getPSM());
                txtPSM.requestFocusInWindow();
            }
            */
            
            // Handle double click event of contact list.
            // Open contact ChatUI when client is double click on contact detail.
            if (e.getClickCount() == 2)
            {
                // Get the contact detail first.
                int index = contactList.locationToIndex(e.getPoint());
                Contact c = (Contact)model.getElementAt(index);
                
                ChatUI ui = UICore.getChatUIList().findUI(c);
                
                if (ui != null)
                {
                    if (ui.getState() == JFrame.ICONIFIED)
                        ui.setState(JFrame.NORMAL);
                    
                    ui.toFront();
                }
                else
                    UICore.getChatUIList().add(new ChatUI(c));
            }
        }
    };
    
    WindowListener winListener = new WindowAdapter()
    {
        public void windowClosing(WindowEvent e) 
        {
            // Inform the server that client is ready to logout when client is close that Contact List.
            // Only logout when client is logged in
            if (!isLoginUI)
                NetworkManager.SendPacket(new Packet(CMSG_LOGOUT));
            
            System.exit(0);
        }
    };
    
    public static final class UISwitcher implements Runnable
    {
        public void run()
        {
            int movement = isLoginUI ? -3 : 3;
            
            // 270 / 3 = 90
            for(int i = 0; i < 90; i++)
            {
                loginPanel.setLocation(loginPanel.getX() + movement, 0);
                contactPanel.setLocation(contactPanel.getX() + movement, 0);
                
                try { Thread.sleep(2); }
                catch(Exception e) {}
            }
            
            UICore.getMasterUI().resetUI();
            UICore.getMasterUI().enableRoomMenu(isLoginUI); // inverse logic, switch from login to contact = true, switch from contact to login = false.
            
            isLoginUI = !isLoginUI;
 
        }
    }

    /**
     * Hides frame
     */
    private void hideWindow() {
        setVisible(false);
    }

    /**
     * Shows frame. Restores frame state (normal or maximized)
     */
    private void restoreWindow() {
        setVisible(true);
        setExtendedState(getExtendedState() & (JFrame.ICONIFIED ^ 0xFFFF));
        requestFocus();
    }

    /**
     * Mouse listener for tray icon. Restores frame on double click.
     */
    class TrayMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                restoreWindow();
            }
        }
    }

    /**
     * Window event listener. Hides frame in iconfying and window closing events
     */
    class WindowMinimizeListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            hideWindow();
        }
        @Override
        public void windowIconified(WindowEvent e) {
            hideWindow();
        }
    }

    
}

    
    class TextAndIconListCellRenderer extends JLabel implements ListCellRenderer {
    private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

    private Border insideBorder;

    public TextAndIconListCellRenderer() {
        this(0, 0, 0, 0);
    }

    public TextAndIconListCellRenderer(int padding) {
        this(padding, padding, padding, padding);
    }

    public TextAndIconListCellRenderer(int topPadding, int rightPadding, int bottomPadding, int leftPadding) {
        insideBorder = BorderFactory.createEmptyBorder(topPadding, leftPadding, bottomPadding, rightPadding);
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
    int index, boolean isSelected, boolean hasFocus) {
        // The object from the combo box model MUST be a TextAndIcon.
        Contact tai = (Contact) value;
        //TextAndIcon tai = new TextAndIcon(d.getUsername(), d.getIcon());

        // Sets text and icon on 'this' JLabel.
        setText(tai.getUsername());
        setIcon(tai.getIcon());
        
        //System.out.println(tai.getIcon());
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        Border outsideBorder;

        if (hasFocus) {
            outsideBorder = UIManager.getBorder("List.focusCellHighlightBorder");
        } else {
            outsideBorder = NO_FOCUS_BORDER;
        }

        setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));
        setComponentOrientation(list.getComponentOrientation());
        setEnabled(list.isEnabled());
        setFont(list.getFont());

        return this;
    }

    // The following methods are overridden to be empty for performance
    // reasons. If you want to understand better why, please read:
    //
    // http://java.sun.com/javase/6/docs/api/javax/swing/DefaultListCellRenderer.html#override
   
    
    @Override
    public void repaint() {
       MasterUI.contactList.setModel(MasterUI.model);  
    }
    
}

class IconListRenderer 
	extends DefaultListCellRenderer {

	private Map<Object, Icon> icons = null;
	
	public IconListRenderer(Map<Object, Icon> icons) {
		this.icons = icons;
	}
	
	@Override
	public Component getListCellRendererComponent(
		JList list, Object value, int index, 
		boolean isSelected, boolean cellHasFocus) {
		
		// Get the renderer component from parent class
		
		JLabel label = 
			(JLabel) super.getListCellRendererComponent(list, 
				value, index, isSelected, cellHasFocus);
		
		// Get icon to use for the list item value
		
		Icon icon = icons.get(value);
		
		// Set icon to display for value
		
		label.setIcon(icon);
		return label;
	}

}

class ContactComp implements Comparator<Contact> {

    /*
    public int compare(ArrayList<String> o1, ArrayList<String> o2) {
        return o1.toString().compareTo(o2.toString());
    }
    */

    public int compare(Contact o1, Contact o2) {
        /*
        return o1.getStatus()- o2.getStatus();
        */
        
        if(o1.getStatus() > o2.getStatus()){
            return 1;
        } else if (o1.getStatus() == o2.getStatus()) {
            return o1.getUsername().compareTo(o1.getUsername());
        }
        else {
            return -1;
        }
        
    }

}
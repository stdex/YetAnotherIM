package UI;

import Core.NetworkManager;
import Core.Opcode;
import Core.Packet;
import Core.UICore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class AddContactUI extends JFrame implements Opcode
{
    JLabel lblUsername;
    
    JTextField txtUsername;
    
    JButton btnOK;
    JButton btnCancel;
    
    public AddContactUI()
    {
        setTitle("Добавление нового контакта");
        setLayout(null);
        
        lblUsername = new JLabel("Ввведите имя пользователя");
        txtUsername = new JTextField();
        btnOK = new JButton("ОК");
        btnCancel = new JButton("Отмена");
        
        lblUsername.setBounds(10, 10, 275, 25);
        txtUsername.setBounds(10, 45, 275, 25);
        btnOK.setBounds(60, 100, 80, 25);
        btnCancel.setBounds(150, 100, 100, 25);
        
        add(lblUsername);
        add(txtUsername);
        add(btnOK);
        add(btnCancel);
        
        btnOK.addActionListener(actListener);
        btnCancel.addActionListener(actListener);
        
        txtUsername.addKeyListener(keyListener);
        
        setSize(300, 170);
        setResizable(false);
        setLocationRelativeTo(UICore.getMasterUI());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    public void submitData()
    {
        String username = txtUsername.getText().trim();
        
        if (!username.equals(""))
        {
            Packet p = new Packet(CMSG_ADD_CONTACT);
            p.put(username);
            
            NetworkManager.SendPacket(p);
            
            dispose();
        }
        else
            JOptionPane.showMessageDialog(null, "Пользователь не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    
    ActionListener actListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e) 
        {
            if (e.getSource().equals(btnOK))
                submitData();
        
            if (e.getSource().equals(btnCancel))
                dispose();
        }
    };
    
    KeyListener keyListener = new KeyAdapter()
    {
        public void keyReleased(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
                submitData();
        }   
    };
}

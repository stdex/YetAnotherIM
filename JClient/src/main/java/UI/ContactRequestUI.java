package UI;

import Core.NetworkManager;
import Core.Opcode;
import Core.Packet;
import Core.UICore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class ContactRequestUI extends JFrame implements Opcode
{
    ButtonGroup grpSelection;
    
    JLabel lblContent;
    JLabel lblSelection;
    
    JButton btnOk;
    JButton btnLater;
    
    JRadioButton rbAllow;
    JRadioButton rbBlock;
    
    int guid;
    
    public ContactRequestUI(int guid, String username)
    {
        this.guid = guid;
        
        setTitle("Запрос добавления контакта");
        setLayout(null);
        
        lblContent = new JLabel(String.format("%s отправпил вам запрос на добавление в свой контакт лист", username));
        lblSelection = new JLabel("Ваши действия:");
        
        rbAllow = new JRadioButton("Разрешить добавить вас");
        rbBlock = new JRadioButton("Заблокировать");
        
        btnOk = new JButton("ОК");
        btnLater = new JButton("Спросить потом...");
        
        grpSelection = new ButtonGroup();
        grpSelection.add(rbAllow);
        grpSelection.add(rbBlock);
        
        lblContent.setBounds(10, 10, 475, 25);
        lblSelection.setBounds(10, 40, 475, 25);
        rbAllow.setBounds(15, 65, 475, 20);
        rbBlock.setBounds(15, 85, 475, 20);
        btnOk.setBounds(270, 130, 80, 25);
        btnLater.setBounds(360, 130, 120, 25);
        
        add(lblContent);
        add(lblSelection);
        add(rbAllow);
        add(rbBlock);
        add(btnOk);
        add(btnLater);
        
        rbAllow.setSelected(true);
        
        btnOk.addActionListener(actListener);
        btnLater.addActionListener(actListener);
        
        setSize(500, 200);
        setResizable(false);
        setLocationRelativeTo(UICore.getMasterUI());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    ActionListener actListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnOk))
            {
                Packet p = new Packet(rbAllow.isSelected() ?  CMSG_CONTACT_ACCEPT : CMSG_CONTACT_DECLINE);
                p.put(guid);
                
                NetworkManager.SendPacket(p); 
            }

            dispose();
        }
    };
}

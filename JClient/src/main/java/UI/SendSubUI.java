package UI;

import Core.AccountDetail;
import Core.Contact;
import Core.NetworkManager;
import Core.Opcode;
import static Core.Opcode.CMSG_SEND_CHAT_MESSAGE;
import static Core.Opcode.СMSG_SEND_IN_SUB;
import Core.Packet;
import Core.UICore;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public final class SendSubUI extends JFrame implements Opcode
{
    String c;
    
    JScrollPane paneOutput;
    JScrollPane paneInput;
    
    JTextArea txtOutput;
    JTextArea txtInput;
    
    private javax.swing.JOptionPane jOptionPane1;
    
    
    public SendSubUI(String c)
    {
        this.c = c;
        
        UpdateTitle();
        
        setLayout(null);
        
        txtOutput = new JTextArea();
        txtInput = new JTextArea();
        
        paneOutput = new JScrollPane(txtOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        paneInput = new JScrollPane(txtInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(paneOutput);
        add(paneInput);
        
        paneOutput.setBounds(10, 10, 350, 300);
        paneInput.setBounds(10, 315, 350, 100);
        
        txtOutput.setEditable(false);
        
        setSize(375, 450);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        
        addWindowListener(winListener);
        txtInput.addKeyListener(keyListener);
    }
    
    public void append(String from, String message, String currentTime)
    {
        message = message.replaceAll("\n", "\n     ");
        txtOutput.append(String.format("%s:: ", currentTime));
        txtOutput.append(String.format("%s :", from));
        txtOutput.append(String.format("     %s\n", message));
    }
    
    public String getTitle()
    {
        return this.c;
    }
    
    public void UpdateTitle()
    {
        setTitle(c.toString());
    }
    
    public void close()
    {
        UICore.getSubsUIList().remove(this);
    }
    
    KeyListener keyListener = new KeyAdapter()
    {
        public void keyReleased(KeyEvent e)
        {
            // Only handle enter key in Chat Interface.
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                // Shift + Enter = next line.
                if (e.isShiftDown())
                {
                    txtInput.append("\n");
                    return;
                }
                
                // Trim the message, cancel the message sending if message is empty.
                if (txtInput.getText().trim().equals(""))
                {
                    txtInput.setText("");
                    return;
                }
 
                
                String message = txtInput.getText().trim();

                if (!message.equals(""))
                {
                    Packet p = new Packet(СMSG_SEND_IN_SUB);
                    p.put(c.toString());
                    p.put(message);

                    NetworkManager.SendPacket(p);
                }
                
/*
                Packet p = new Packet(CMSG_SEND_CHAT_MESSAGE);
                p.put(c.getGuid());
                p.put(txtInput.getText().trim());
                
                NetworkManager.SendPacket(p);
*/                
                message = message.replaceAll("\n", "\n     ");
                
                // Output the message to Chat Interface too.
                Date dt = new java.util.Date();
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String nowTime = sdf.format(dt);
                
                
               // append(c.toString(), message, nowTime);
                
                // Reset the input text area.
                txtInput.setText("");
            }
        }   
    };
    
    WindowListener winListener = new WindowAdapter()
    {
        public void windowClosing(WindowEvent e) 
        {
            close();
        }
    };
}
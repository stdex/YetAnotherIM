package UI;

import Core.AccountDetail;
import Core.Contact;
import Core.NetworkManager;
import Core.Opcode;
import Core.Packet;
import Core.UICore;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class ChatUI extends JFrame implements Opcode
{
    Contact c;
    
    JScrollPane paneOutput;
    JScrollPane paneInput;
    
    JTextArea txtOutput;
    JTextArea txtInput;
    
    public ChatUI(Contact c)
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
        
        try {
            readAllHistory(AccountDetail.getDisplayTitle(), c.getUsername());
        } catch (IOException ex) {
            Logger.getLogger(ChatUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        txtOutput.setEditable(false);
        
        setSize(375, 450);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        
        addWindowListener(winListener);
        txtInput.addKeyListener(keyListener);
    }
    
    public void append(String from, String to, String message, String currentTime)
    {
        message = message.replaceAll("\n", "\n     ");
        txtOutput.append(String.format("%s:: ", currentTime));
        txtOutput.append(String.format("%s --> ", from));
        txtOutput.append(String.format("%s\n", to));
        txtOutput.append(String.format("     %s\n", message));

        //String outputMSG = new StringBuilder(String.format("%s:: ", currentTime)).append(String.format("%s --> ", from)).append(String.format("%s\n", to)).append(String.format("     %s\n", message)).toString();
        //logChat(outputMSG, from, to);
        
    }

    public static void logChat(String message, String from, String to, String mode) {
        try {
            
            Runtime.getRuntime().exec("chmod 777 file");
            
            boolean success = true;
            
            //File dir = new File(from);
            //success = dir.mkdir();
            
            String fl = "";
            
            if(mode == "out")
                fl = to;
            else
                fl = from;
            
                if (success) {
                    //System.out.println("DIR: OK");
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new FileWriter(new File("uhistory_" + fl + ".txt"), true)));
                    if (message != null && !message.equals("")) {
                            out.println(message);
                        }
                    out.close();
                }
        } catch (IOException e) {
            e.getMessage();
        }
    }
    
    public void readAllHistory(String from, String to) throws FileNotFoundException, IOException {

        File file = new File("uhistory_" + to + ".txt");
        if(!file.exists()) return;
        
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int)file.length()];
        fis.read(data);
        fis.close();
        //
        String s = new String(data, "UTF-8");

        txtOutput.append(s);
    }
    
    
    public Contact getContact()
    {
        return this.c;
    }
    
    public void UpdateTitle()
    {
        setTitle(c.toString());
    }
    
    public void close()
    {
        UICore.getChatUIList().remove(this);
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
                
                // Send the message to server.
                Packet p = new Packet(CMSG_SEND_CHAT_MESSAGE);
                p.put(c.getGuid());
                p.put(txtInput.getText().trim());
                
                NetworkManager.SendPacket(p);
                
                String message = txtInput.getText().trim();
                message = message.replaceAll("\n", "\n     ");
                
                // Output the message to Chat Interface too.
                    Date dt = new java.util.Date();
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String nowTime = sdf.format(dt);
                
                
                append(AccountDetail.getDisplayTitle(), c.getTitle().toString(), message, nowTime);
                
                
                String outputMSG = new StringBuilder(String.format("%s:: ", nowTime)).append(String.format("%s --> ", AccountDetail.getDisplayTitle())).append(String.format("%s\n", c.getTitle().toString())).append(String.format("     %s\n", message)).toString();
                logChat(outputMSG, AccountDetail.getDisplayTitle(), c.getTitle().toString(), "out");
                
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

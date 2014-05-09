package UI;

import Core.AccountDetail;
import Core.Contact;
import Core.Main;
import Core.NetworkManager;
import Core.Opcode;
import Core.Packet;
import Core.UICore;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class ChatUI extends JFrame implements Opcode
{
    Contact c;
    
    JScrollPane paneOutput;
    JScrollPane paneInput;
    
    JTextArea txtOutput;
    JTextArea txtInput;
    private JButton btnSend;
    
    public ChatUI(Contact c)
    {
        this.c = c;
        
        UpdateTitle();
        
        setLayout(null);
        
        txtOutput = new JTextArea();
        txtInput = new JTextArea();
        
        btnSend = new JButton("1");
        
        btnSend.setBackground(new Color(59, 89, 182));
        btnSend.setForeground(Color.WHITE);
        btnSend.setFocusPainted(false);
        btnSend.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        btnSend.setBounds(300, 315, 60, 100);
        
        paneOutput = new JScrollPane(txtOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        paneInput = new JScrollPane(txtInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        paneOutput.setBounds(10, 10, 350, 300);
        paneInput.setBounds(10, 315, 290, 100);
        
        add(paneOutput);
        add(paneInput);
        add(btnSend);
        
        try {
            readAllHistory(AccountDetail.getDisplayTitle(), c.getUsername());
        } catch (IOException ex) {
            Logger.getLogger(ChatUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
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
        
         btnSend.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent arg0)   
            {  
                SendMSG();
            }  
        });
        
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
            
            CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();

            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            File jDir = new File(jarDir);
            System.out.println(jarDir);

            ///Runtime.getRuntime().exec("chmod 777 file");
  
            String folder = "";
            String file = "";
            
            if(mode == "out") {
                folder = from;
                file = to;
            }
            else {
                folder = to;
                file = from;
            }
            System.out.println(folder + " : " + file);
            
            File dir = new File(jarDir+"/"+folder);
            dir.mkdir();
            
            /*
            String[] chmod = { "su", "-c","chmod 777 "+dir };
            try {
                Runtime.getRuntime().exec(chmod);
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            

                if (dir.exists()) {
                    System.out.println(dir);
                    System.out.println(new File(jDir+"/"+folder+"/uhistory_" + file + ".txt"));

                    if (message != null && !message.equals("")) {
                            
                        File bfile = new File(jDir+"/"+folder+"/uhistory_" + file + ".txt");
                        FileWriter fileWriter = new FileWriter(bfile,true);
                        BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
                        fileWriter.append(message);
                        bufferFileWriter.close();
       
                        }
                }
        } catch (IOException e) {
            e.getMessage();
        } catch (URISyntaxException ex) {
            Logger.getLogger(ChatUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readAllHistory(String from, String to) throws FileNotFoundException, IOException, URISyntaxException {

        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();

        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        String jarDir = jarFile.getParentFile().getPath();
        File jDir = new File(jarDir);
        
        File file = new File(jDir+"/"+from+"/uhistory_" + to + ".txt");
        if(!file.exists()) return;
        
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int)file.length()];
        fis.read(data);
        fis.close();
        //
        String s = new String(data);

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
                
                SendMSG();

            }
        }   
    };
    
    public void SendMSG()
    {
                
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
    
    
    WindowListener winListener = new WindowAdapter()
    {
        public void windowClosing(WindowEvent e) 
        {
            close();
        }
    };
}

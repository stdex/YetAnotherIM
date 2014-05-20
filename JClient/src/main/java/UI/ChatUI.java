package UI;

import Core.AccountDetail;
import Core.Contact;
import Core.Main;
import Core.NetworkManager;
import Core.Opcode;
import Core.Packet;
import Core.UICore;
import HistoryParser.ExParser;
import HistoryParser.Message;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
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
import java.net.URL;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public final class ChatUI extends JFrame implements Opcode
{
    Contact c;
    
    JScrollPane paneOutput;
    JScrollPane paneInput;
    private static JEditorPane paneOutputHtml;
    
    JTextArea txtOutput;
    JTextArea txtInput;
    private JButton btnSend;
    private static HTMLEditorKit kit;
    private static Document doc;
    
    public ChatUI(Contact c)
    {
        this.c = c;
        
        UpdateTitle();
        
        setLayout(null);
        
        txtOutput = new JTextArea();
        txtInput = new JTextArea();
        paneOutputHtml = new JEditorPane();
        
        btnSend = new JButton();
        
        Image img;
        try {
            img = ImageIO.read(getClass().getResource("/Images/icon_send.png"));
            btnSend.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
            Logger.getLogger(ChatUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        btnSend.setBackground(new Color(59, 89, 182));
        btnSend.setForeground(Color.WHITE);
        btnSend.setFocusPainted(false);
        btnSend.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        btnSend.setBounds(300, 315, 60, 99);
 
        paneOutput = new JScrollPane(paneOutputHtml, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        paneInput = new JScrollPane(txtInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        paneOutput.setBounds(10, 10, 350, 300);
        paneInput.setBounds(10, 315, 290, 100);
        
        add(paneOutput);
        add(paneInput);
        add(btnSend);
        
        paneOutputHtml.setEditable(false);
        kit = new HTMLEditorKit();
        paneOutputHtml.setEditorKit(kit);
        
        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule(".jchat_out {background-color: #F3FAE8; width: 100%; text-align: left; font-size: 12px;  border-bottom: 1px solid #C2CEA6;}");
        styleSheet.addRule(".jchat_in {background-color: #F8F8F8; width: 100%; text-align: left; font-size: 12px;  border-bottom: 1px solid #D4D4D4;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        styleSheet.addRule(".jchat_color_out {color : blue;}");
        styleSheet.addRule(".jchat_color_in {color : red;}");

        try {
            readAllHistory(AccountDetail.getUsername(), c.getUsername());
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
        //String text = "";
        //text = text+"<div><div class=\"jchat_color_out\">"+from+" ("+currentTime+")</div>"+message.replaceAll("(\r\n|\n)", "<br />")+"</div><br/>";
        try {
            readAllHistory(AccountDetail.getUsername(), c.getUsername());
    
        } catch (IOException ex) {
            Logger.getLogger(ChatUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ChatUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
            
            //paneOutputHtml.setText(paneOutputHtml.getText() + text);
            
            /*
            try {
            doc = paneOutputHtml.getDocument();
            doc.insertString(doc.getLength(), text, null);
            } catch(BadLocationException exc) {
            exc.printStackTrace();
            }*/
            
            //paneOutputHtml.getStyledDocument().insertString(text);
            
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
            String separator = "";
            
            if(mode == "out") {
                folder = from;
                file = to;
                separator = "-------------------------------------->-\n";
            }
            else {
                folder = to;
                file = from;
                separator = "--------------------------------------<-\n";
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
                        
                        message = message+separator;
                            
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
    
    public static void readAllHistory(String from, String to) throws FileNotFoundException, IOException, URISyntaxException {

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
        /*
        URL graphicURL = null;
        File faccept = new File(getClass().getResource("/Images/icon_accept.png").getPath());  
        graphicURL = faccept.toURL();
        <img src=\""+graphicURL+"\"></img>
        */
        
        String sdata = new String(data);
        
        ExParser ep = new ExParser();
                
                if(ep.parse(sdata))
                    {
                        String text = "";
                            // System.out.println(ep.getList());
                        for (Message s : ep.getList()) {
                            text = text+"<div><div class=\"jchat_color_"+s.getType()+"\">"+s.getUsername()+" ("+s.getDatetime()+")</div>"+s.getMessage().replaceAll("(\r\n|\n)", "<br />")+"</div><br/>";
                        }
                                
        
                        doc = kit.createDefaultDocument();
                        paneOutputHtml.setDocument(doc);
                        paneOutputHtml.setText(text);

                    }


        //txtOutput.append(s);
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
                    //txtInput.append("\n");
                    return;
                }
                
                //txtInput.append("\n");
                return;
                //SendMSG();

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
                
                // Output the message to Chat Interface too.
                    Date dt = new java.util.Date();
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

                    String nowTime = sdf.format(dt);
                
                String outputMSG = new StringBuilder(String.format("%s ", AccountDetail.getUsername())).append(String.format("(%s)\n", nowTime)).append(String.format("%s\n", message)).toString();
                logChat(outputMSG, AccountDetail.getUsername(), c.getUsername().toString(), "out");
    
                append(AccountDetail.getUsername(), c.getUsername().toString(), message, nowTime);
            
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

package Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

public class NetworkManager implements Opcode
{
    private static Socket socket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    
    public NetworkManager()
    {
    }
    
    public static void destroy()
    {
        try { socket.close(); }
        catch (Exception e) {}
        
        socket = null;
        in = null;
        out = null;
    }
   
    public static void login(String username, String password,int status)
    {
        try
        {
            
            Config.loadConfig();
            String ServerIP = Config.getStringDefault("ServerIP", "192.168.1.170");
            int ServerPort = Config.getIntDefault("ServerPort", 6769);
            
            System.out.println(ServerIP + ":" + ServerPort);
            
            
            // Connect to the server.
            socket = new Socket(ServerIP, ServerPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            
            // 5 mins timeout
            socket.setSoTimeout(5 * 60 * 1000);
            
            // If connection is create succefully, send the login detail to the server.
            Packet loginPacket = new Packet(CMSG_LOGIN);
            loginPacket.put(username);
            loginPacket.put(password);
            loginPacket.put(status);
            
            SendPacket(loginPacket);
            
            // Create input stream.
            in = new ObjectInputStream(socket.getInputStream());
            
            Packet p = (Packet)in.readObject();
            
            switch(p.getOpcode())
            {
                case SMSG_LOGIN_SUCCESS: /* Login is success */
                    int accountGuid = (Integer)p.get();
                    String accountUsername = (String)p.get();
                    String accountTitle = (String)p.get();
                    String accountPSM = (String)p.get();
                    int accountStatus = (Integer)p.get();
                    
                    new Thread(new NetworkThread()).start();
                    System.out.println("Login: " + accountUsername + ": SUCCESS");
                    
                    break;
                case SMSG_LOGIN_FAILED: /* Login failed */
                    NetworkManager.destroy();
                    
                    System.out.println("Login: FAIL");                    
                    break;
                case SMSG_MULTI_LOGIN: /* Account is already login on other computer. */
                    NetworkManager.destroy();
                    
                    System.out.println("Login: FAIL: Account is already login on other computer.");                     
                    break;
                default: /* Server problem? */
                    
                    System.out.println("Login: FAIL: Server problem?");    
                    break;
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Error: Unable to connect to server. Please try again later.");
        }
        catch (Exception e)
        {
            System.out.println("Error: Unknown error occur, please try again later.");
        }
    }
    
    public static void logout()
    {
        NetworkThread.stop();
        destroy();
        
        System.out.println("LOGOUT: SUCCESS");
    }
    
    public static void getContactList()
    {
        SendPacket(new Packet(CMSG_GET_CONTACT_LIST));
    }
    
    public static void SendMessageToSubs()
    {
        
         if (!Main.subsMessage.equals(""))
         {
                    Packet p = new Packet(СMSG_SEND_IN_SUB);
                    p.put(Main.subsName);
                    p.put(Main.subsMessage);

                    SendPacket(p);
          }
        
    }

    
    public static void SendPacket(Packet p)
    {
        try
        {
            out.writeObject(p);
            out.flush();
        }
        catch (Exception e){}
    }
    
    public static Packet ReceivePacket() throws Exception
    {
        return (Packet)in.readObject();
    }
}

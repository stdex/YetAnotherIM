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
    
    /**
     * Метод регистрации пользователя
     * @param username
     * @param password
     * @param title 
     */
    public static void registration(String username, String password, String title)
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
            
            // Send the registration detail to the server.
            Packet registrationPacket = new Packet(CMSG_REGISTRATION);
            registrationPacket.put(username);
            registrationPacket.put(password);
            registrationPacket.put(title);
            
            System.out.println(username+"::"+password+"::"+title);
            
            SendPacket(registrationPacket);
            
            // Create input stream.
            in = new ObjectInputStream(socket.getInputStream());
            
            Packet p = (Packet)in.readObject();
            
            switch(p.getOpcode())
            {
                
                case SMSG_REGISTRATION_SUCCESS: /* Registration is success */
                    NetworkManager.destroy();
                    UICore.showMessageDialog("Регистрация завершена успешно.", "Регистрация", JOptionPane.ERROR_MESSAGE);
                    break;

                case SMSG_REGISTRATION_ALREADY: /* Account is already in database. */
                    NetworkManager.destroy();
                    UICore.showMessageDialog("Такой логин уже есть в базе данных.", "Регистрация", JOptionPane.ERROR_MESSAGE);
                    break;
                    
                default: /* Server problem? */
                    NetworkManager.destroy();
                    UICore.showMessageDialog("Неизвестная ошибка, попробуйте позже.", "Регистрация", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        catch (IOException ioe)
        {
            UICore.showMessageDialog("Unable to connect to server. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            UICore.showMessageDialog("Unknown error occur, please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
                    
                    UICore.getMasterUI().setAccountDetail(accountGuid, accountUsername, accountTitle, accountPSM, accountStatus);
                    UICore.switchUI();

                    break;
                case SMSG_LOGIN_FAILED: /* Login failed */
                    NetworkManager.destroy();
                    
                    UICore.showMessageDialog("The infomation you entered is not valid.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    UICore.getMasterUI().enableLoginInput(true);
                    
                    break;
                case SMSG_MULTI_LOGIN: /* Account is already login on other computer. */
                    NetworkManager.destroy();
                    
                    UICore.showMessageDialog("Your account is currently logged in on another computer. To log in here, please log out from the other computer.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    UICore.getMasterUI().enableLoginInput(true);
                    
                    break;
                default: /* Server problem? */
                    UICore.showMessageDialog("Unknown error occur, please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                    UICore.getMasterUI().enableLoginInput(true);
                    break;
            }
        }
        catch (IOException ioe)
        {
            UICore.showMessageDialog("Unable to connect to server. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            UICore.getMasterUI().enableLoginInput(true);
        }
        catch (Exception e)
        {
            UICore.showMessageDialog("Unknown error occur, please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            UICore.getMasterUI().enableLoginInput(true);
        }
    }
    
    public static void logout()
    {
        NetworkThread.stop();
        destroy();
        
        UICore.switchUI();
        UICore.getChatUIList().disposeAllUI();
        UICore.getRoomChatUIList().disposeAllUI();
        UICore.getMasterUI().setTitle("Вход");
        UICore.getMasterUI().enableLoginInput(true);
        AccountDetail.clear();
    }
    
    public static void getContactList()
    {
        SendPacket(new Packet(CMSG_GET_CONTACT_LIST));
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

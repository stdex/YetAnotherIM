package Core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;

import java.io.EOFException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class NetworkThread implements Runnable, Opcode
{
    private static ArrayList<Packet> PacketStorage;
    private static volatile Thread thread;
    private static Timer timer;
    private static SessionStatus sessionStatus;
    private int counter;
    
    public static ArrayList<SubTable> titles;
    
    public static void stop()
    {
        timer.cancel();
        
        thread = null;
        timer = null;
    }
    
    public void run()
    {
        thread = Thread.currentThread();
        
        sessionStatus = SessionStatus.LOGGEDIN;
        
        PacketStorage = new ArrayList<Packet>();
        
        // The server will first send SMSG_CONTACT_DETAIL signal to inform client that this is a client detail data.
        NetworkManager.getContactList();
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new PeriodicTimeSyncResp(), 0, 10 * 1000);
        
        Packet p;
        
        while(thread == Thread.currentThread())
        {
            try
            {
                p = NetworkManager.ReceivePacket();
                
                if (p.getOpcode() < 0x00 || p.getOpcode() >= opcodeTable.length)
                    continue;
                
                OpcodeDetail opcode = opcodeTable[p.getOpcode()];
                
                if (p.size() != opcode.length)
                    continue;
                
                if (opcode.sessionStatus == sessionStatus.INSTANT)
                {
                    if (p.getOpcode() == SMSG_PING)
                    {
                        NetworkManager.SendPacket(new Packet(CMSG_PING));
                        continue;
                    }
                }
                
                if (!IsOpcodeCanProcessNow(opcode))
                {
                    PacketStorage.add(p);
                    continue;
                }
                
                ProcessPacket(p);
            }
            catch (EOFException eof)
            {
                NetworkManager.logout();
                System.out.println("Error: You have been disconnected from the server.");
            }
            catch (SocketException se)
            {
                NetworkManager.logout();
                System.out.println("Error: You have been disconnected from the server.");
            }
            catch (SocketTimeoutException ste)
            {
                NetworkManager.logout();
                System.out.println("Error: You have been disconnected from the server.");
            }
            catch (Exception e) {}
        }
    }
    
    boolean IsOpcodeCanProcessNow(OpcodeDetail detail)
    {
        if (sessionStatus == detail.sessionStatus || sessionStatus == SessionStatus.READY || detail.sessionStatus == SessionStatus.INSTANT)
            return true;
        
        return false;
    }
    
    void ProcessQueuePacket() throws Exception
    {
        for (ListIterator<Packet> packet = PacketStorage.listIterator(); packet.hasNext(); )
        {
            Packet p = packet.next();
            
            OpcodeDetail opcode = opcodeTable[p.getOpcode()];
            
            if (!IsOpcodeCanProcessNow(opcode))
                continue;
            
            ProcessPacket(p);
            packet.remove();
        }
    }
    
    void ProcessPacket(Packet p) throws Exception
    {
        OpcodeDetail opcode = opcodeTable[p.getOpcode()];
        
        if (opcode.handler != null)
        {
            Class[] types = new Class[] { Packet.class };
            Object[] args = new Object[] { p };
        
            this.getClass().getDeclaredMethod(opcode.handler, types).invoke(this, args);
        }
    }
    
    void HandleContactDetailOpcode(Packet packet)
    {
        int guid = (Integer)packet.get();
        String c_username = (String)packet.get();
        String c_title = (String)packet.get();
        String c_psm = (String)packet.get();
        int c_status = (Integer)packet.get();
        
        Contact c = new Contact(guid, c_username, c_title, c_psm, c_status);

    }
    
    void HandleContactListEndedOpcode(Packet packet) throws Exception
    {
      sessionStatus = SessionStatus.READY;
      ProcessQueuePacket();

      //Packet p = new Packet(CMSG_GET_OFFLINE_MSG);
      //NetworkManager.SendPacket(p);
      
      NetworkManager.SendMessageToSubs();          
    }
    
    void HandleContactAlreadyInListOpcode(Packet packet)
    {
       
    }
    
    void HandleContactNotFoundOpcode(Packet packet)
    {

    }
    
    void HandleChatMessageOpcode(Packet packet)
    {

        int messageid = (Integer)packet.get();
        int reciverGuid = (Integer)packet.get();
        int senderGuid = (Integer)packet.get();
        String message = (String)packet.get();
        String currentTime = (String)packet.get();
        
        System.out.println(currentTime + "::" + messageid + ":: " + senderGuid + " --> " + reciverGuid + ":: " + message);      
                
        // Send the message to server.
        Packet p = new Packet(CMSG_GET_CHAT_MESSAGE);
        p.put(messageid);
        NetworkManager.SendPacket(p);
 
    }
    
    
    
    void HandleSubscrubeMessageOpcode(Packet packet)
    {
        int messageid = (Integer)packet.get();
        String title = (String)packet.get();
        int reciverGuid = (Integer)packet.get();
        String senderGuid = (String)packet.get();
        String message = (String)packet.get();
        String currentTime = (String)packet.get();
        
        System.out.println(currentTime + "::" + messageid + ":: " + senderGuid + " --> " + reciverGuid + ":: " + message);

        // Send approve message to server
        Packet p = new Packet(CMSG_GET_IN_SUB);
        p.put(messageid);
        NetworkManager.SendPacket(p);

    }
    
    void HandleGetSubscribeListOpcode(Packet packet)
    {
        titles = (ArrayList<SubTable>)packet.get();
        
    }
  
    void HandleSubscribeSuccessOpcode(Packet packet)
    {
        Packet p = new Packet(CMSG_GET_SUBLIST);
        NetworkManager.SendPacket(p);        
    }

    void HandleUnSubscribeSuccessOpcode(Packet packet)
    {
        Packet p = new Packet(CMSG_GET_SUBLIST);
        NetworkManager.SendPacket(p);        
    }
    
    void HandleSendInSubSuccessOpcode(Packet packet)
    {
        NetworkManager.logout();
    }
    
    void HandleContactStatusChangedOpcode(Packet packet)
    {
        int guid = (Integer)packet.get();
        int status = (Integer)packet.get();
    }
    
    void HandleAddContactSuccessOpcode(Packet packet)
    {
        int guid = (Integer)packet.get();
        String username = (String)packet.get();
        String title = (String)packet.get();
        String psm = (String)packet.get();
        int c_status = (Integer)packet.get();

        Contact c = new Contact(guid, username, title, psm, c_status);
    }

    void HandleContactRequestOpcode(Packet packet)
    {
        int r_guid = (Integer)packet.get();
        String r_username = (String)packet.get();
    }
    
    void HandleContactDetailChangedOpcode(Packet packet)
    {
        int guid = (Integer)packet.get();
        String data = (String)packet.get();
    }
    
    void HandleCreateRoomFailOpcode(Packet packet)
    {
    }
    
    void HandleJoinRoomOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String userName = (String)packet.get();

    }
    
    void HandleLeaveRoomOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String userName = (String)packet.get();
    }
    
    void HandleJoinRoomSuccessOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String roomName = (String)packet.get();
    }
    
    void HandleLeaveRoomSuccessOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
    }
    
    void HandleRoomChatOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String sender = (String)packet.get();
        String message = (String)packet.get();
    }
    
    void HandleRoomNotFoundOpcode(Packet packet)
    {
        String roomName = (String)packet.get();
    }
    
    void HandleWrongRoomPasswordOpcode(Packet packet)
    {
        String roomName = (String)packet.get();
    }
    
    void HandleRoomMemberDetailOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String userName = (String)packet.get();
    }
    
    void HandleAlreadyInRoomOpcode(Packet packet)
    {
        String roomName = (String)packet.get();
    }
    
    void HandleLogoutCompleteOpcode(Packet packet)
    {
        NetworkManager.logout();
    }
    
    class PeriodicTimeSyncResp extends TimerTask 
    {
        public PeriodicTimeSyncResp()
        {
            counter = 0;
        }
        
        public void run() 
        {
            counter = 0;
            Packet p = new Packet(CMSG_TIME_SYNC_RESP);
            p.put(counter++);
            p.put(System.currentTimeMillis());
            
            NetworkManager.SendPacket(p);
        }
    }
}

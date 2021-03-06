package Core;

import Notification.NotificationWindow;
import Notification.TrayUtil;
import UI.ChatUI;
import static UI.ChatUI.logChat;
import UI.ContactRequestUI;
import UI.MasterUI;
import UI.RoomChatUI;
import UI.RoomFormUI;
import UI.SendSubUI;
import static UI.SendSubUI.logChat;
import UI.SubscribeUI;
import static UI.SubscribeUI.jTable1;
import static UI.SubscribeUI.tableModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
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

                UICore.showMessageDialog("You have been disconnected from the server.", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (SocketException se)
            {
                NetworkManager.logout();

                UICore.showMessageDialog("You have been disconnected from the server.", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (SocketTimeoutException ste)
            {
                NetworkManager.logout();

                UICore.showMessageDialog("You have been disconnected from the server.", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
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
        
        UICore.getMasterUI().addContact(c);
    }
    
    void HandleContactListEndedOpcode(Packet packet) throws Exception
    {
        sessionStatus = SessionStatus.READY;
        ProcessQueuePacket();
        UICore.getMasterUI().contactList.repaint();
        //System.out.println(UICore.getMasterUI().model); 
                            
      // If login is succefully, check offline messages
                   
      Packet p = new Packet(CMSG_GET_OFFLINE_MSG);
      NetworkManager.SendPacket(p);
      MasterUI.contactList.repaint();
      MasterUI.contactList.setModel(MasterUI.model);    
      ListCellRenderer renderer = MasterUI.contactList.getCellRenderer();
            
      MasterUI.contactList.repaint();
      MasterUI.contactList.setModel(MasterUI.model);
      MasterUI.contactList.setCellRenderer(renderer);
      MasterUI.contactList.repaint();
    }
    
    void HandleContactAlreadyInListOpcode(Packet packet)
    {
        UICore.showMessageDialog("Пользователь уже находиться у вас в контакт листе.", "Добавление нового контакта", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void HandleContactNotFoundOpcode(Packet packet)
    {
        UICore.showMessageDialog("Пользователь не найден.", "Добавление нового контакта", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void HandleChatMessageOpcode(Packet packet) throws ParseException, IOException, FileNotFoundException, URISyntaxException
    {

        int messageid = (Integer)packet.get();
        int reciverGuid = (Integer)packet.get();
        int senderGuid = (Integer)packet.get();
        String message = (String)packet.get();
        String currentTime = (String)packet.get();
        
        System.out.println(currentTime + "::" + messageid + ":: " + senderGuid + " --> " + reciverGuid + ":: " + message);
 
        
        Contact s_contact = null;
        
        // Search contact list have this contact detail or not.
        // This help the client to deny chat message if the contact is deleted.
        s_contact = UICore.getMasterUI().searchContact(senderGuid);
        
        // Cant find sender contact detail in list. Possible deleted.
        if (s_contact == null)
            return;
        /*
        ChatUI targetUI = UICore.getChatUIList().findUI(s_contact);
        
        if (targetUI == null)
            UICore.getChatUIList().add(targetUI = new ChatUI(s_contact));
        
        
        // Send the message to server.
        Packet p = new Packet(CMSG_GET_CHAT_MESSAGE);
        p.put(messageid);
        NetworkManager.SendPacket(p);
        
        // Output the message in sender ChatUI.
        targetUI.append(s_contact.getTitle(), AccountDetail.getTitle(), message, currentTime);
        targetUI.toFront();
        */
        
                
        // Send the message to server.
        Packet p = new Packet(CMSG_GET_CHAT_MESSAGE);
        p.put(messageid);
        NetworkManager.SendPacket(p);
        
        /*
        NotificationWindow theWindow1 = new NotificationWindow(
                                s_contact.getTitle(),
                                message,
                                2000);
        */
        
        
            Date nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            String nTime = sdf.format(nowTime);
            
            String outputMSG = new StringBuilder(String.format("%s ", s_contact.getUsername())).append(String.format("(%s)\n", nTime)).append(String.format("%s\n", message)).toString();            
            ChatUI.logChat(outputMSG, s_contact.getUsername(), AccountDetail.getUsername(), "in");
        
        new NotificationWindow(s_contact.getUsername()+" ("+nTime+")", message, 4000, "chat", s_contact, "");
        new TrayUtil("chat", s_contact, "");
        ChatUI.readAllHistory(AccountDetail.getUsername(), s_contact.getUsername());
        //TrayUtil.createTray();
        /*
        try {
            Notification.NotificationPopup.showNotificationMSG(s_contact.getTitle(), AccountDetail.getTitle(), currentTime, , s_contact);
        } catch (ParseException ex) {
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        
    }
    
    
    
    void HandleSubscrubeMessageOpcode(Packet packet) throws ParseException, IOException, FileNotFoundException, URISyntaxException
    {
        int messageid = (Integer)packet.get();
        String title = (String)packet.get();
        String reciverGuid = (String)packet.get();
        String senderGuid = (String)packet.get();
        String message = (String)packet.get();
        String currentTime = (String)packet.get();
        
        System.out.println(currentTime + "::" + messageid + ":: " + senderGuid + " --> " + reciverGuid + ":: " + message);


        // Send approve message to server
        Packet p = new Packet(CMSG_GET_IN_SUB);
        p.put(messageid);
        NetworkManager.SendPacket(p);
        
        Date nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            String nTime = sdf.format(nowTime);
        
        String outputMSG = new StringBuilder(String.format("%s ", senderGuid)).append(String.format("(%s)\n", nTime)).append(String.format("%s\n", message)).toString();
        //String outputMSG = new StringBuilder(String.format("%s ", currentTime)).append(String.format("%s :: ", senderGuid)).append(String.format("     %s\n", message)).toString();
        SendSubUI.logChat(outputMSG, title, senderGuid, "in");
        
        Contact s_contact = null;
        SendSubUI targetUI = UICore.getSubsUIList().findUI(title);

        if( !reciverGuid.equals(senderGuid) ) {
            // TODO: check if windws is focused
            //if( !(targetUI.isFocusableWindow()) )
        new TrayUtil("subscribe", s_contact, title);
        new NotificationWindow(senderGuid+" ("+nTime+")", message, 4000, "subscribe", s_contact, title);
        }

        SendSubUI.readAllSubHistory(reciverGuid,title);
        // Output the message in sender ChatUI.
        //targetUI.append(Integer.toString(senderGuid), message, currentTime);
        //targetUI.append(senderGuid, message, currentTime);
        //targetUI.toFront();
    }
    
    void HandleGetSubscribeListOpcode(Packet packet) throws IOException
    {
        titles = (ArrayList<SubTable>)packet.get();
                
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel = (DefaultTableModel) UI.SubscribeUI.jTable1.getModel();
        tableModel.setNumRows(0);
      
/*
        BufferedImage originalImage1;
        BufferedImage originalImage2;
        originalImage1 = ImageIO.read(this.getClass().getResource("/Images/icon_accept.png"));
        originalImage2 = ImageIO.read(this.getClass().getResource("/Images/icon_exit.png"));
        ByteArrayOutputStream baos1=new ByteArrayOutputStream();
        ByteArrayOutputStream baos2=new ByteArrayOutputStream();
        ImageIO.write(originalImage1, "png", baos1);
        ImageIO.write(originalImage1, "png", baos2);
        byte[] imageInByte1=baos1.toByteArray();
        byte[] imageInByte2=baos2.toByteArray();
        byte[] imageInByte;
        
                    
        System.out.println(titles.get(count).getStatus());
            
            if(titles.get(count).getStatus() == "1") {
                imageInByte = imageInByte1;
            }
            else
            {
                imageInByte = imageInByte2;
            }
            */
        
        for (int count = 0; count < titles.size(); count++){

            
            tableModel.addRow(new Object[]{1});
            tableModel.setValueAt(titles.get(count).getTitle(), count, 0);
            tableModel.setValueAt(titles.get(count).getStatus(), count, 1);
            tableModel.setValueAt("-->", count, 2);
            
        }
        
        UI.SubscribeUI.jTable1.setModel(tableModel);
        
        
   //     jTable1.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());
        
    }
  
    void HandleSubscribeSuccessOpcode(Packet packet)
    {
        Packet p = new Packet(CMSG_GET_SUBLIST);
        NetworkManager.SendPacket(p);    
        
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel = (DefaultTableModel) UI.SubscribeUI.jTable1.getModel();
        tableModel.setNumRows(0);
      
        for (int count = 0; count < titles.size(); count++){
            tableModel.addRow(new Object[]{1});
            tableModel.setValueAt(titles.get(count).getTitle(), count, 0);
            tableModel.setValueAt(titles.get(count).getStatus(), count, 1);
            tableModel.setValueAt("-->", count, 2);
        }
        
        UI.SubscribeUI.jTable1.setModel(tableModel);
        
    }

    void HandleUnSubscribeSuccessOpcode(Packet packet) throws IOException
    {
        Packet p = new Packet(CMSG_GET_SUBLIST);
        NetworkManager.SendPacket(p);    
        
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel = (DefaultTableModel) UI.SubscribeUI.jTable1.getModel();
        tableModel.setNumRows(0);
      

        /*
        BufferedImage originalImage1;
        BufferedImage originalImage2;
        originalImage1 = ImageIO.read(this.getClass().getResource("/Images/icon_accept.png"));
        originalImage2 = ImageIO.read(this.getClass().getResource("/Images/icon_exit.png"));
        ByteArrayOutputStream baos1=new ByteArrayOutputStream();
        ByteArrayOutputStream baos2=new ByteArrayOutputStream();
        ImageIO.write(originalImage1, "png", baos1);
        ImageIO.write(originalImage1, "png", baos2);
        byte[] imageInByte1=baos1.toByteArray();
        byte[] imageInByte2=baos2.toByteArray();
        byte[] imageInByte = null;
        
                    
            if(titles.get(count).getStatus() == "1") {
                imageInByte = imageInByte1;
            }
            else
            {
                imageInByte = imageInByte2;
            }
        */
        
        for (int count = 0; count < titles.size(); count++){

            tableModel.addRow(new Object[]{1});
            tableModel.setValueAt(titles.get(count).getTitle(), count, 0);
            tableModel.setValueAt(titles.get(count).getStatus(), count, 1);
            tableModel.setValueAt("-->", count, 2);
        }
        
        UI.SubscribeUI.jTable1.setModel(tableModel);
        
    }
    
    void HandleSendInSubSuccessOpcode(Packet packet)
    {        
    }
    
    void HandleContactStatusChangedOpcode(Packet packet)
    {
        int guid = (Integer)packet.get();
        int status = (Integer)packet.get();
        
        //System.out.println(guid+" :: "+status);
        
        UICore.UpdateContactStatus(guid, status);
    }
    
    void HandleAddContactSuccessOpcode(Packet packet)
    {
        int guid = (Integer)packet.get();
        String username = (String)packet.get();
        String title = (String)packet.get();
        String psm = (String)packet.get();
        int c_status = (Integer)packet.get();

        Contact c = new Contact(guid, username, title, psm, c_status);
       
        UICore.getMasterUI().addContact(c);
    }

    void HandleContactRequestOpcode(Packet packet)
    {
        int r_guid = (Integer)packet.get();
        String r_username = (String)packet.get();
        
        new ContactRequestUI(r_guid, r_username);
    }
    
    void HandleContactDetailChangedOpcode(Packet packet)
    {
        int guid = (Integer)packet.get();
        String data = (String)packet.get();
        
        if (packet.getOpcode() == SMSG_TITLE_CHANGED)
            UICore.getMasterUI().UpdateContactDetail(guid, data, null);
        else if (packet.getOpcode() == SMSG_PSM_CHANGED)
            UICore.getMasterUI().UpdateContactDetail(guid, null, data);
    }
    
    void HandleCreateRoomFailOpcode(Packet packet)
    {
        UICore.showMessageDialog("Fail to create room, a room with same name is already exists.", "Create Room", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void HandleJoinRoomOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String userName = (String)packet.get();
        
        RoomChatUI ui = UICore.getRoomChatUIList().findUI(roomID);
        
        if (ui != null)
            ui.addMember(userName, true);
    }
    
    void HandleLeaveRoomOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String userName = (String)packet.get();
        
        RoomChatUI ui = UICore.getRoomChatUIList().findUI(roomID);
        
        if (ui != null)
            ui.removeMember(userName, true);
    }
    
    void HandleJoinRoomSuccessOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String roomName = (String)packet.get();
        
        Room r = new Room(roomID, roomName);
        RoomChatUI ui = new RoomChatUI(r, "abc");
        
        UICore.getRoomChatUIList().add(ui);
    }
    
    void HandleLeaveRoomSuccessOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        
        RoomChatUI ui = UICore.getRoomChatUIList().findUI(roomID);
        
        if (ui != null)
        {
            ui.dispose();
            UICore.getRoomChatUIList().remove(ui);
        }
    }
    
    void HandleRoomChatOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String sender = (String)packet.get();
        String message = (String)packet.get();
        
        RoomChatUI ui = UICore.getRoomChatUIList().findUI(roomID);
        
        if (ui != null)
            ui.append(sender, message);
    }
    
    void HandleRoomNotFoundOpcode(Packet packet)
    {
        String roomName = (String)packet.get();
        
        UICore.showMessageDialog(String.format("Could not found a room with name %s!", roomName), "Join An Existing Room", JOptionPane.INFORMATION_MESSAGE);
        
        new RoomFormUI(RoomFormUI.JOIN_ROOM, roomName);
    }
    
    void HandleWrongRoomPasswordOpcode(Packet packet)
    {
        String roomName = (String)packet.get();
        
        UICore.showMessageDialog(String.format("Wrong password for room %s!", roomName), "Join An Existing Room", JOptionPane.INFORMATION_MESSAGE);
        
        new RoomFormUI(RoomFormUI.JOIN_ROOM, roomName);
    }
    
    void HandleRoomMemberDetailOpcode(Packet packet)
    {
        int roomID = (Integer)packet.get();
        String userName = (String)packet.get();
        
        RoomChatUI ui = UICore.getRoomChatUIList().findUI(roomID);
        
        if (ui != null)
            ui.addMember(userName, false);
    }
    
    void HandleAlreadyInRoomOpcode(Packet packet)
    {
        String roomName = (String)packet.get();
        UICore.showMessageDialog(String.format("You are already a member of room %s!", roomName), "Join An Existing Room", JOptionPane.INFORMATION_MESSAGE);
    }
    
    void HandleLogoutCompleteOpcode(Packet packet)
    {
        NetworkManager.logout();
        TrayUtil.disposeAllMsgTray();
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

/*
class ImageRenderer extends DefaultTableCellRenderer
{
 
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected,boolean hasFocus, int row, int column)
    {
        JLabel label = new JLabel();
 
        if (value!=null) {
        label.setHorizontalAlignment(JLabel.CENTER);
        //value is parameter which filled by byteOfImage
        label.setIcon(new ImageIcon((byte[])value));
        }
 
        return label;
    }
}
*/
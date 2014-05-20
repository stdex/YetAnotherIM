package Core;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class Session implements Runnable, Opcode
{
    private Client c;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Timer timer;
    
    private volatile Thread session;
    
    private long pingTicks;
    
    public Session(Client c, ObjectInputStream in, ObjectOutputStream out)
    {
        this.c = c;
        this.in = in;
        this.out = out;
    }
    
    public void stop()
    {
        timer.cancel();
        
        session = null;
        timer = null;
    }
    
    public Packet ReceivePacket() throws Exception
    {
        return (Packet)in.readObject();
    }
    
    public void SendPacket(Packet p) throws Exception
    {
        out.writeObject(p);
        out.flush();
    }
    
    public void run()
    {
        this.session = Thread.currentThread();
        
        while(session == Thread.currentThread())
        {
            try
            {
                Packet p = ReceivePacket();
                
                if (p.getOpcode() < 0x00 || p.getOpcode() >= opcodeTable.length)
                {
                    System.out.printf("\nUnknown Opcode Receive: 0x%02X\n", p.getOpcode());
                    continue;
                }
                
                OpcodeDetail opcode = opcodeTable[p.getOpcode()];
                
                System.out.printf("\nOpcode: %s\n", opcode.name);
                
                if (opcode.sessionStatus != SessionStatus.LOGGEDIN)
                {
                    System.out.printf("Invalid Opcode Receive: %s\n", opcode.name);
                    continue;
                }
                
                if (p.size() != opcode.length)
                {
                    System.out.printf("Client %s (guid: %d) send a packet with wrong size, should be %d, but receive %d. (Attemp to crash server?)\n", c.getUsername(), c.getGuid(), opcode.length, p.size());
                    continue;
                }
                
                if (opcode.handler != null)
                {
                    Class[] types = new Class[] { Packet.class };
                    Object[] args = new Object[] { p };
                
                    this.getClass().getDeclaredMethod(opcode.handler, types).invoke(this, args);
                }
                else
                {
                    System.out.printf("Processing is not require for this packet.\n");
                    continue;
                }
            }
            catch (InvocationTargetException ite)
            {
                // Throw when an exception occur while processing packet.
                Throwable t = ite.getCause();
                
                if (t instanceof ClassCastException)
                    System.out.printf("Client %s (guid: %d) send a packet with wrong structure. (Attemp to crash server?)", c.getUsername(), c.getGuid());
                else
                    System.out.printf("Unhandler exception occur while processing packet data.\nException message: %s\n", ite.getCause());
            }
            catch (EOFException eof)
            {
                System.out.printf("\nClient %s (guid: %d) unexpected EOF while waiting for packet. (possible disconnected?)\n", c.getUsername(), c.getGuid());
                
                Logout();
            }
            catch (SocketException se)
            {
                System.out.printf("\nClient %s (guid: %d) connection was closed unexpectedly. (possible disconnected?)\n", c.getUsername(), c.getGuid());
                
                Logout();
            }
            catch (SocketTimeoutException ste)
            {
                // Client will send a Time Sync every 10 sec.
                // Every 30 sec, the server will request the client to send a ping acknowledgement.
                // If a client does not send any packet for 60 seconds, we consider that it is disconnected.
                System.out.printf("\nClient %s (guid: %d) is not respond for 60 seconds. (possible disconnected?)\n", c.getUsername(), c.getGuid());
                
                Logout();
            }
            catch (Exception e){e.printStackTrace();}
        }
        
        System.out.printf("Session thread of %s (guid: %d) stopped successfully.\n", c.getUsername(), c.getGuid());
    }
    
    void HandleGetContactListOpcode(Packet packet) throws Exception
    {
        
        //ResultSet rs = Main.db.query("SELECT a.guid, a.username, a.title, a.psm FROM contact AS c LEFT JOIN account AS a ON c.c_guid = a.guid WHERE c.o_guid = %d", c.getGuid());
        
        ResultSet rs = Main.db.query("SELECT guid, username, title, psm FROM account WHERE guid != %d AND adapter != %d  ORDER BY online DESC", c.getGuid(), 1);
        
        Packet p;
        
        while(rs.next())
        {
            int guid = rs.getInt(1);
            String username = rs.getString(2);
            String title = rs.getString(3);
            String psm = rs.getString(4);
            
            Client target = Main.clientList.findClient(rs.getInt(1));
            
            int status = target != null ? target.getStatus() : 3;
            
            p = new Packet(SMSG_CONTACT_DETAIL);
            p.put(guid);
            p.put(username);
            p.put(title);
            p.put(psm);
            p.put(status);
            
            SendPacket(p);
            
            System.out.printf("Send Contact: %s to client %d\n", rs.getString(2), c.getGuid());
            
            Thread.sleep(10);
        }
        
        rs.close();
        
        System.out.print("Send Opcode: SMSG_CONTACT_LIST_ENDED\n");
        
        SendPacket(new Packet(SMSG_CONTACT_LIST_ENDED));
        
        System.out.printf("Send contact: Finish\n");
        
        /*
        
        System.out.printf("Send recent contact request to client %d.\n", c.getGuid());
        
        ResultSet requestRS = Main.db.query("SELECT a.guid, a.username FROM contact_request AS c LEFT JOIN account AS a ON c.r_guid = a.guid WHERE c.o_guid = %d", c.getGuid());
        
        while (requestRS.next())
        {
            System.out.printf("Send Contact Request: %s to client %d\n", requestRS.getString(2), c.getGuid());
            
            p = new Packet(SMSG_CONTACT_REQUEST);
            p.put(requestRS.getInt(1));
            p.put(requestRS.getString(2));
            
            SendPacket(p);
            
            Thread.sleep(10);
        }
        
        requestRS.close();
        */
        
        // We start a latency check after 1 sec.
        timer = new Timer();
        timer.schedule(new PeriodicLatencyCheck(), 1000);
    }
    
    void HandleLogoutOpcode(Packet packet) throws Exception
    {
        SendPacket(new Packet(SMSG_LOGOUT_COMPLETE));
        
        Logout();
    }
    
    void HandleStatusChangedOpcode(Packet packet) throws Exception
    {
        int toStatus = (Integer)packet.get();
        
        c.setStatus(toStatus);
        
        System.out.printf("Client %d change status to %d.\n" , c.getGuid(), toStatus);
        
        InformOthersForStatusChange();
        
        System.out.printf("Client %d update status to %d: Finish.\n", c.getGuid(), toStatus);
    }
    
    void HandleAddContactOpcode(Packet packet) throws Exception
    {
        String username = (String)packet.get();
        
        // Contact to add is self
        if (c.getUsername().equalsIgnoreCase(username))
        {
            System.out.printf("Client %d add self to contact list.\n", c.getGuid());
            
            ResultSet rs = Main.db.query("SELECT id FROM contact WHERE o_guid = %d AND c_guid= %d", c.getGuid(), c.getGuid());
            
            if (rs.first())
            {
                Packet p = new Packet(SMSG_ADD_CONTACT_SUCCESS);
                p.put(c.getGuid());
                p.put(c.getUsername());
                p.put(c.getTitle());
                p.put(c.getPSM());
                p.put(c.getStatus());
                
                SendPacket(p);
                
                Main.db.execute("INSERT INTO contact(o_guid, c_guid) VALUES(%d, %d)", c.getGuid(), c.getGuid());
            }
            
            rs.close();
            
            return;
        }
        
        ResultSet ars = Main.db.query("SELECT guid, username, title, psm FROM account WHERE username = '%s'", username);
        
        if (ars.first())
        {
            int guid = ars.getInt(1);
            username = ars.getString(2);
            String title = ars.getString(3);
            String psm = ars.getString(4);
            
            ResultSet acrs = Main.db.query("SELECT id FROM contact WHERE o_guid = %d and c_guid = %d", c.getGuid(), guid);
            
            if (acrs.first())
                SendPacket(new Packet(SMSG_CONTACT_ALREADY_IN_LIST));
            else
            {
                System.out.printf("Send Contact: %s to client %d\n", username, c.getGuid());
                
                Main.db.execute("INSERT INTO contact(o_guid, c_guid) VALUES(%d, %d)", c.getGuid(), guid);
                
                Client target = Main.clientList.findClient(guid);
                
                int currentStatus = 3;
                
                ResultSet ccrs = Main.db.query("SELECT id FROM contact WHERE o_guid = %d and c_guid = %d", guid, c.getGuid());
                
                if (!ccrs.first())
                {
                    if (target != null)
                    {
                        System.out.printf("Send Contact Request: %s to client %d\n", c.getUsername(), guid);
                        
                        Packet p = new Packet(SMSG_CONTACT_REQUEST);
                        p.put(c.getGuid());
                        p.put(c.getUsername());
                        
                        target.getSession().SendPacket(p);
                    }
                    else
                        Main.db.execute("INSERT INTO contact_request(o_guid, r_guid) VALUES(%d, %d)", guid, c.getGuid());
                }
                else
                {
                    System.out.printf("Send Contact Request Cancel: %s is already in contact list of %s.\n", c.getUsername(), username);
                    
                    if (target != null)
                    {
                        currentStatus = target.getStatus();
                        
                        Packet statusPacket = new Packet(SMSG_STATUS_CHANGED);
                        statusPacket.put(c.getGuid());
                        statusPacket.put(c.getStatus());
                        
                        target.getSession().SendPacket(statusPacket);
                    }
                }
                
                Packet p = new Packet(SMSG_ADD_CONTACT_SUCCESS);
                p.put(guid);
                p.put(username);
                p.put(title);
                p.put(psm);
                p.put(currentStatus);
                
                SendPacket(p);
            }
            
            acrs.close();
        }
        else
        {
            SendPacket(new Packet(SMSG_CONTACT_NOT_FOUND));
        }
        
        ars.close();
    }
    /*
    void HandleGetSubListOpcode(Packet packet) throws Exception
    {
        List<String> titleList = new ArrayList<String>();
        
        ResultSet rs = Main.db.query("SELECT * FROM subscribe;");
        
        while(rs.next()) {
            titleList.add(rs.getString("title"));
        }
        System.out.printf("Client request list of the subscribe\n");
        System.out.println(titleList);
            
        Packet p = new Packet(SMSG_GET_SUBLIST_SUCCESS);
        p.put(titleList);
        SendPacket(p);
    }
    */
    
    void HandleGetSubListOpcode(Packet packet) throws Exception
    {
        //List<String> titleList = new ArrayList<String>();
        List<SubTable> values = new ArrayList<SubTable>();
        
        ResultSet rs = Main.db.query("SELECT * FROM subscribe;");
        
        SubTable temp = null;
        
        while(rs.next()) {
            temp = new SubTable(rs.getInt("sid"), rs.getString("title"), "");
            values.add(temp);
        }
        
        rs = null;
        
        rs = Main.db.query("SELECT t.sid as sid1, t.title as title, m.sid as sid2, m.guid as guid FROM subscribe AS t LEFT JOIN subscribe_account AS m ON t.sid = m.sid WHERE m.guid = %d", c.getGuid());
     
        String ids = "";
        List<String> rStatus = new ArrayList<String>();
        
        while(rs.next()) {
            rStatus.add(Integer.toString(rs.getInt("sid1")));
        }
        
        for (int count = 0; count < values.size(); count++){
            
            //System.out.println(values.get(count).getId());
            
            if(rStatus.contains(Integer.toString(values.get(count).getId()))) {
            //  System.out.println(values.get(count).getTitle());
              values.get(count).setStatus("1");  
            }
            else {
              values.get(count).setStatus("0");
            }
        }
        
        System.out.printf("Client request list of the subscribe\n");
        System.out.println(values);
            
        Packet p = new Packet(SMSG_GET_SUBLIST_SUCCESS);
        p.put(values);
        SendPacket(p);
    }
    
    void HandleAddNewSubOpcode(Packet packet) throws Exception
    {
        String subtitle = (String)packet.get();
        System.out.println(subtitle);
      
        ResultSet rs = Main.db.query("SELECT * FROM subscribe WHERE title='%s'", subtitle);
        System.out.println(rs.first());
        if (!(rs.first())) {        
            Main.db.execute("INSERT INTO subscribe (title) VALUES('%s')", subtitle);
        }
    }
    
    void HandleSubscribeOpcode(Packet packet) throws Exception
    {

        String title = (String)packet.get();
        System.out.printf("Subscribe topic: %s to client %d\n", title, c.getGuid());
        
        ResultSet rs = Main.db.query("SELECT * FROM subscribe WHERE title='%s'", title);
        System.out.println(rs.first());
        
        if (rs.first()) {
            int idSubscrube = rs.getInt("sid");

            System.out.println(idSubscrube);
            System.out.println(c.getGuid());

            ResultSet rsf = Main.db.query("SELECT * FROM subscribe_account WHERE sid='%d' AND guid='%d'", idSubscrube, c.getGuid());
            //System.out.println(rsf.first());

            if (!(rsf.first())) {
            Main.db.execute("INSERT INTO subscribe_account(sid, guid) VALUES('%d', '%d')", idSubscrube, c.getGuid());
            //System.out.println(rs.first());
            }

            System.out.printf("Subscribe success!");
            Packet p = new Packet(SMSG_SUBSCRIBE_SUCCESS);
            SendPacket(p);
        
        }
    }
    
    void HandleUnSubscribeOpcode(Packet packet) throws Exception
    {

        String title = (String)packet.get();
        System.out.printf("Unsubscribe topic: %s to client %d\n", title, c.getGuid());
        
        ResultSet rs = Main.db.query("SELECT * FROM subscribe WHERE title='%s'", title);
        System.out.println(rs.first());
        
        if (rs.first()) {
            int idSubscrube = rs.getInt("sid");

            System.out.println(idSubscrube);
            System.out.println(c.getGuid());

            ResultSet rsf = Main.db.query("SELECT * FROM subscribe_account WHERE sid='%d' AND guid='%d'", idSubscrube, c.getGuid());
            //System.out.println(rsf.first());

            if (rsf.first()) {
            Main.db.execute("DELETE FROM subscribe_account WHERE sid='%d' AND guid='%d'", idSubscrube, c.getGuid());
            //System.out.println(rs.first());
            }

            System.out.printf("Unsubscribe success!");
            Packet p = new Packet(SMSG_UNSUBSCRIBE_SUCCESS);
            SendPacket(p);
        
        }
    }
    
    void HandleGetOfflineMsgOpcode(Packet packet) throws Exception
    {
         System.out.printf("Send offline messages:\n");
         ResultSet rs = Main.db.query("SELECT * FROM messages WHERE r_guid='%d' AND fcg='%d'", c.getGuid(), 0);
         
         while(rs.next()) {
             
            Client target = Main.clientList.findClient(c.getGuid());

            if (target != null)
                   {
                       Packet p = new Packet(SMSG_SEND_CHAT_MESSAGE);
                       p.put(rs.getInt("id"));
                       p.put(rs.getInt("r_guid"));
                       p.put(rs.getInt("o_guid"));
                       p.put(rs.getString("message"));
                       p.put(rs.getString("datetime"));

                       target.getSession().SendPacket(p);
                       //SendPacket(p);


                       System.out.printf("Send message success\n");
                  }
             else
                      System.out.printf("Send Chat Message Cancel: Client %d is currently offline.\n", c.getGuid());
             
         }
 
         System.out.printf("Send offline subscribe messages:\n");
         //SELECT * FROM subscribe_messages LEFT JOIN account ON subscribe_messages.o_guid = account.guid WHERE r_guid='3' AND fcg='0'
         ResultSet srs = Main.db.query("SELECT * FROM subscribe_messages LEFT JOIN account ON subscribe_messages.o_guid = account.guid WHERE r_guid='%d' AND fcg='%d'", c.getGuid(), 0);
         
         while(srs.next()) {
             
            Client target = Main.clientList.findClient(c.getGuid());

            int subsID = srs.getInt("sid");
            ResultSet rsb = null;
            
            if (target != null)
                   {
                    rsb = Main.db.query("SELECT * FROM subscribe WHERE sid='%d'", subsID);
                            if (rsb.first()) {
                                Packet p = new Packet(SMSG_SEND_IN_SUB);
                                p.put(srs.getInt("id"));
                                p.put(rsb.getString("title"));
                                p.put(target.getUsername());
                                //p.put(c.getGuid());
                                //p.put(c.getUsername());
                                p.put(srs.getString("username"));
                                p.put(srs.getString("message"));
                                p.put(srs.getString("datetime"));

                                target.getSession().SendPacket(p);

                                System.out.printf("Send message success\n");
                            }
                  }
             else
                      System.out.printf("Send Chat Message Cancel: Client %d is currently offline.\n", c.getGuid());
             
         }
         
    }
    
    void HandleSendInSubOpcode(Packet packet) throws Exception
    {

        String title = (String)packet.get();
        String msg = (String)packet.get();
        
        System.out.printf("Send message: %s in topic %s\n", msg, title);
        
        
        System.out.printf("Subscribe message get success!");
        Packet s = new Packet(SMSG_SEND_IN_SUB_SUCCESS);
        SendPacket(s);
        
        ResultSet rs = Main.db.query("SELECT * FROM subscribe WHERE title='%s'", title);
        System.out.println(rs.first());
        
                
        Date dt = new java.util.Date();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
     
        
        if (rs.first()) {
            int idSubscrube = rs.getInt("sid");

            System.out.println(idSubscrube);
            System.out.println(c.getGuid());

            rs = null;
            
            rs = Main.db.query("SELECT * FROM subscribe_account WHERE sid=%d", idSubscrube);
            //System.out.println(rs.first());


            //List<String> subscrubers = new ArrayList<String>();
            Client target;
            
            ClientList clist = Main.clientList;

            while(rs.next()) {

                ResultSet rsf = Main.db.query("SELECT NEXTVAL('subscribe_messages_id_seq');");
                int idmsg = 0;
                if (rsf.next()) {
                    idmsg = rsf.getInt("nextval");
                }
                System.out.println("Generate new subscribe message id: " + idmsg);
                Main.db.execute("INSERT INTO subscribe_messages(id, o_guid, r_guid, sid, message, fsg, fcg, datetime) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", idmsg, c.getGuid(), rs.getInt("guid"), idSubscrube, msg, "1", "0", currentTime);
                                
                System.out.println(rs.getInt("guid"));
                target = Main.clientList.findClient(rs.getInt("guid"));
                //System.out.println(target.getUsername());
                
                if (target != null)
                {

                    Packet p = new Packet(SMSG_SEND_IN_SUB);
                    p.put(idmsg);
                    p.put(title);
                    p.put(target.getUsername());
                    //p.put(c.getGuid());
                    p.put(c.getUsername());
                    p.put(msg);
                    p.put(currentTime);

                    target.getSession().SendPacket(p);

                    System.out.printf("Send message success\n");
                }
                else
                    System.out.printf("Send Chat Message Cancel: Client %d is currently offline.\n", rs.getInt("guid"));
            }

  
        }
    }
    
    
    
    void HandleContactAcceptOpcode(Packet packet) throws Exception
    {
        int guid = (Integer)packet.get();
        
        Main.db.execute("DELETE FROM contact_request WHERE o_guid = %d and r_guid = %d", c.getGuid(), guid);
        Main.db.execute("INSERT INTO contact(o_guid, c_guid) VALUES(%d, %d)", c.getGuid(), guid);
        
        ResultSet rrs = Main.db.query("SELECT username, title, psm FROM account WHERE guid = %d", guid);
        
        Client requestor = Main.clientList.findClient(guid);
        
        int requestorStatus = requestor != null ? requestor.getStatus() : 3;
        
        if (rrs.first())
        {
            System.out.printf("Send Contact: %s to client %d\n", rrs.getString(1), c.getGuid());
            Packet p = new Packet(SMSG_ADD_CONTACT_SUCCESS);
            p.put(guid);
            p.put(rrs.getString(1));
            p.put(rrs.getString(2));
            p.put(rrs.getString(3));
            p.put(requestorStatus);
            
            SendPacket(p);
        }
        
        if (requestor != null)
        {
            Packet statusPacket = new Packet(SMSG_STATUS_CHANGED);
            statusPacket.put(c.getGuid());
            statusPacket.put(c.getStatus());
            
            requestor.getSession().SendPacket(statusPacket);
        }
        
        rrs.close();
    }
    
    void HandleContactDeclineOpcode(Packet packet) throws Exception
    {
        int guid = (Integer)packet.get();
        
        Main.db.execute("DELETE FROM contact_request WHERE o_guid = %d and r_guid = %d", c.getGuid(), guid);
    }
    
    void HandleRemoveContactOpcode(Packet packet) throws Exception
    {
        int guid = (Integer)packet.get();
        
        Main.db.execute("DELETE FROM contact WHERE o_guid = %d AND c_guid = %d", c.getGuid(), guid);
        
        Client target = Main.clientList.findClient(guid);
        
        if (target != null)
        {
            Packet p = new Packet(SMSG_STATUS_CHANGED);
            p.put(c.getGuid());
            p.put(3);
            
            target.getSession().SendPacket(p);
        }
    }
    
    void HandleChatMessageOpcode(Packet packet) throws Exception
    {
        int from = c.getGuid();
        int to = (Integer)packet.get();
        String message = (String)packet.get();
        
        Date dt = new java.util.Date();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        
        System.out.printf(currentTime + ":" + "Chat Message Receive From: %d, To %d, Message: %s\n", from, to, message);
        
        Client target = Main.clientList.findClient(to);
        
        ResultSet rs = Main.db.query("SELECT NEXTVAL('messages_id_seq');");
        int idmsg = 0;
        if (rs.next()) {
            idmsg = rs.getInt("nextval");
        }
        System.out.println("Generate new message id: " + idmsg);
        Main.db.execute("INSERT INTO messages(id, o_guid, r_guid, message, fsg, fcg, datetime) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s')", idmsg, from, to, message, "1", "0", currentTime);

        
        /*
        Main.db.execute("INSERT INTO messages(o_guid, r_guid, message, fsg, fcg, datetime) VALUES('%s', '%s', '%s', '%s', '%s', '%s')", from, to, message, "1", "0", currentTime);
        ResultSet rs = Main.db.query("SELECT MAX(id) AS id FROM messages;");
        
        int idmsg = 0;
        if (rs.next()) {
            idmsg = rs.getInt("id");
        }
        */
        
        if (target != null)
        {
            Packet p = new Packet(SMSG_SEND_CHAT_MESSAGE);
            p.put(idmsg);
            p.put(to);
            p.put(from);
            p.put(message);
            p.put(currentTime);

            target.getSession().SendPacket(p);
            
            System.out.printf("Send message success\n");
        }
        else
            System.out.printf("Send Chat Message Cancel: Client %d is currently offline.\n", to);
    }
    
    void HandleChatMessageApprove(Packet packet) throws Exception
    {
        int msgid = (Integer)packet.get();

        System.out.printf("Approve message recived %d \n", msgid);

        Main.db.execute("UPDATE messages SET fcg = 1 WHERE id = %d", msgid);
    }
    
    void HandleGetInSubOpcode(Packet packet) throws Exception
    {
        int msgid = (Integer)packet.get();

        System.out.printf("Approve subscrube message recived %d \n", msgid);

        Main.db.execute("UPDATE subscribe_messages SET fcg = 1 WHERE id = %d", msgid);
    }
    
    void HandleTimeSyncRespOpcode(Packet packet)
    {
        int counter = (Integer) packet.get();
        long ticks = (Long) packet.get();
        
        System.out.printf("From Client: %s (guid: %d)\n", c.getUsername(), c.getGuid());
        
        // first time receive this opcode
        if (counter == 0)
        {
            c.setCounter(counter);
            c.setTicks(ticks);
            System.out.printf("First time sync received: counter %d, client ticks %d\n", counter, ticks);
            return;
        }
        
        if (counter != c.getCounter() + 1)
            System.out.printf("Wrong time sync counter: should be %d, but receive %d\n", c.getCounter() + 1, counter);
        
        System.out.printf("Time sync received: counter %d, client ticks %d, time since last sync %d\n", counter, ticks, ticks - c.getTicks());
        
        c.setCounter(counter);
        c.setTicks(ticks);
    }
    
    void HandlePingOpcode(Packet packet)
    {
        int latency = (int)(System.currentTimeMillis() - pingTicks);
        
        System.out.printf("From client: %s (guid: %d), latency: %dms\n", c.getUsername(), c.getGuid(), latency);
        
        c.setLatency(latency);
        
        // Check latency 30 sec later.
        timer.schedule(new PeriodicLatencyCheck(), 30 * 1000);
    }
    
    void HandleClientDetailChangedOpcode(Packet packet) throws Exception
    {
        String data = (String)packet.get();
        String str = null;
        Packet p = null;
        
        if (packet.getOpcode() == CMSG_TITLE_CHANGED)
        {
            str = "title";
            p = new Packet(SMSG_TITLE_CHANGED);
            c.setTitle(data);
        }
        else if (packet.getOpcode() == CMSG_PSM_CHANGED)
        {
            str = "psm";
            p = new Packet(SMSG_PSM_CHANGED);
            c.setPSM(data);
        }
        else
        {
            System.out.printf("Opcode 0x%02X shouldn't be process in this handler!", packet.getOpcode());
            return;
        }
        
        System.out.printf("Client %d change %s to %s.\n", c.getGuid(), str, data);
        Main.db.execute("UPDATE account SET %s = '%s' WHERE guid = '%d'", str, data, c.getGuid());
        
        p.put(c.getGuid());
        p.put(data);
        
        ResultSet rs = Main.db.query("SELECT c_guid FROM contact WHERE o_guid = %d", c.getGuid());
        
        while(rs.next())
        {
            int guid = rs.getInt(1);
            
            Client target = Main.clientList.findClient(guid);
            
            if (target != null)
            {
                System.out.printf("Send %s change From: %d, To: %d, Data: %s\n", str, c.getGuid(), guid, data);
                target.getSession().SendPacket(p);
            }
        }
    }
    
    void HandleCreateRoomOpcode(Packet packet) throws Exception
    {
        String roomName = (String)packet.get();
        String roomPassword = (String)packet.get();
        
        System.out.printf("From Client: %s (guid: %d)\n", c.getUsername(), c.getGuid());
        System.out.printf("Room name: %s, password: %s\n", roomName, roomPassword.equals("") ? "*NONE*" : roomPassword);
        
        if (Main.roomList.findRoom(roomName) != null)
        {
            System.out.printf("Failed to create room %s, a room with same name is already create.\n", roomName);
            SendPacket(new Packet(SMSG_CREATE_ROOM_FAILED));
            return;
        }
        
        System.out.printf("Creating room %s.\n", roomName);
        Room r = new Room(roomName, roomPassword);
        
        System.out.printf("Register client %d into room %d.\n", c.getGuid(), r.getRoomID());
        r.addClient(c);
        
        Main.roomList.add(r);
        
        Packet p = new Packet(SMSG_JOIN_ROOM_SUCCESS);
        p.put(r.getRoomID());
        p.put(r.getRoomName());
        
        SendPacket(p);
        
        System.out.printf("Room %d created successfully.\n", r.getRoomID());
    }
    
    void HandleJoinRoomOpcode(Packet packet) throws Exception
    {
        String roomName = (String)packet.get();
        String roomPassword = (String)packet.get();
        
        System.out.printf("From Client: %s (guid: %d)\n", c.getUsername(), c.getGuid());
        System.out.printf("Room name: %s, password: %s\n", roomName, roomPassword.equals("") ? "*NONE*" : roomPassword);
        
        Room room = Main.roomList.findRoom(roomName);
        
        if (room == null)
        {
            System.out.printf("Room with name %s is not found!\n", roomName);
            
            Packet p = new Packet(SMSG_ROOM_NOT_FOUND);
            p.put(roomName);
            
            SendPacket(p);
            
            return;
        }
        
        if (!room.getRoomPassword().equals(roomPassword))
        {
            System.out.printf("Client %d supplied a wrong password for room %d.\n", c.getGuid(), room.getRoomID());
            
            Packet p = new Packet(SMSG_WRONG_ROOM_PASSWORD);
            p.put(roomName);
            
            SendPacket(p);
            
            return;
        }
        
        if (room.findClient(c.getGuid()) != null)
        {
            System.out.printf("Client %d is already in room %s.\n", c.getGuid(), roomName);
            
            Packet p = new Packet(SMSG_ALREADY_IN_ROOM);
            p.put(roomName);
            
            SendPacket(p);
            
            return;
        }
        
        Packet joinPacket = new Packet(SMSG_JOIN_ROOM_SUCCESS);
        joinPacket.put(room.getRoomID());
        joinPacket.put(room.getRoomName());
        
        SendPacket(joinPacket);
        
        Packet p = new Packet(SMSG_JOIN_ROOM);
        p.put(room.getRoomID());
        p.put(c.getUsername());
        
        for (ListIterator<Client> client = room.clientListIterator(); client.hasNext(); )
        {
            Client member = client.next();
            
            Packet memberPacket = new Packet(SMSG_ROOM_MEMBER_DETAIL);
            memberPacket.put(room.getRoomID());
            memberPacket.put(member.getUsername());
            
            member.getSession().SendPacket(p);
            SendPacket(memberPacket);
        }
        
        System.out.printf("Register client %d into room %d.\n", c.getGuid(), room.getRoomID());
        room.addClient(c);
        
        System.out.printf("Client %d join room %d successfully.\n", c.getGuid(), room.getRoomID());
    }
    
    void HandleLeaveRoomOpcode(Packet packet) throws Exception
    {
        int roomID = (Integer)packet.get();
        
        System.out.printf("From Client: %s (guid: %d)\n", c.getUsername(), c.getGuid());
        System.out.printf("Room ID: %d.\n", roomID);
        
        Room room = Main.roomList.findRoom(roomID);
        
        if (room == null)
        {
            System.out.printf("Room with ID %d is not found!\n", roomID);
            return;
        }
        
        if (room.findClient(c.getGuid()) == null)
        {
            // no in room?? Wrong packet??
            System.out.printf("Room %d does not contain client %d. (cheater?)\n", roomID, c.getGuid());
            return;
        }
        
        System.out.printf("Remove client %d from room %d.\n", c.getGuid(), room.getRoomID());
        room.removeClient(c);
        
        Packet p = new Packet(SMSG_LEAVE_ROOM);
        p.put(room.getRoomID());
        p.put(c.getUsername());
        
        for (ListIterator<Client> client = room.clientListIterator(); client.hasNext(); )
            client.next().getSession().SendPacket(p);
        
        Packet leavePacket = new Packet(SMSG_LEAVE_ROOM_SUCCESS);
        leavePacket.put(room.getRoomID());
        
        SendPacket(leavePacket);
        
        // Delete room if the room does not contain any client.
        if (room.getRoomSize() == 0)
        {
            System.out.printf("Room %d is empty, remove.\n", room.getRoomID());
            Main.roomList.remove(room);
        }
    }
    
    void HandleRoomChatOpcode(Packet packet) throws Exception
    {
        int roomID = (Integer)packet.get();
        String message = (String)packet.get();
        
        Room room = Main.roomList.findRoom(roomID);
        
        if (room == null)
            return;
        
        if (room.findClient(c.getGuid()) == null)
        {
            // no in room?? Wrong packet??
            System.out.printf("Room %d does not contain client %d. (cheater?)\n", roomID, c.getGuid());
            return;
        }
        
        System.out.printf("Room Chat Message Receive From: %d, Room ID: %d, Message: %s\n", c.getGuid(), roomID, message);
        
        Packet p = new Packet(SMSG_ROOM_CHAT);
        p.put(room.getRoomID());
        p.put(c.getUsername());
        p.put(message);
        
        for (ListIterator<Client> client = room.clientListIterator(); client.hasNext(); )
            client.next().getSession().SendPacket(p);
    }
    
    void InformOthersForStatusChange() throws Exception
    {
        Packet p = new Packet(SMSG_STATUS_CHANGED);
        p.put(c.getGuid());
        p.put(c.getStatus());
        
        //ResultSet rs = Main.db.query("SELECT c_guid FROM contact WHERE o_guid = %d", c.getGuid());
        ResultSet rs = Main.db.query("SELECT guid FROM account WHERE guid != %d", c.getGuid());
        
        while(rs.next())
        {
            Client target = Main.clientList.findClient(rs.getInt(1));

            if (target != null)
                target.getSession().SendPacket(p);
        }
        
        rs.close();
    }
    
    void LeaveAllRoom() throws Exception
    {
        /*  TODO: Optimize Required */
        
        for (ListIterator<Room> room = Main.roomList.listIterator(); room.hasNext(); )
        {
            Room r = room.next();
            
            if (r.findClient(c.getGuid()) != null)
            {
                r.removeClient(c);
                
                if (r.getRoomSize() == 0)
                {
                    System.out.printf("Room %d is empty, remove.\n", r.getRoomID());
                    Main.roomList.remove(r);
                }
                
                Packet p = new Packet(SMSG_LEAVE_ROOM);
                p.put(r.getRoomID());
                p.put(c.getUsername());
                
                for (ListIterator<Client> client = r.clientListIterator(); client.hasNext(); )
                    client.next().getSession().SendPacket(p);
            }
        }
    }
    
    void Logout()
    {
        try
        {
            Main.clientList.remove(c);
            
            System.out.printf("Closing client socket %d.\n", c.getGuid());
            c.getSocket().close();
            
            Main.db.execute("UPDATE account SET online = 0 WHERE guid = %d", c.getGuid());
            System.out.printf("Stopping session thread of %s (guid: %d).\n", c.getUsername(), c.getGuid());
            
            c.setStatus(3);
            
            InformOthersForStatusChange();
            LeaveAllRoom();
            
            stop();
        }
        catch (Exception e){}
    }
    
    class PeriodicLatencyCheck extends TimerTask 
    {
        public void run()
        {
            try
            {
                pingTicks = System.currentTimeMillis();
                SendPacket(new Packet(SMSG_PING));
            }
            catch (Exception e){}
        }
    }
}

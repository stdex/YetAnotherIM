package Core;

import java.util.ListIterator;

public class Room
{
    private int roomID;
    private String roomName;
    private String roomPassword;
    private ClientList clientList;
    
    public Room(String roomName, String roomPassword)
    {
        this.roomID = Main.roomList.getNextRoomID();
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        
        clientList = new ClientList();
    }
    
    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }
    
    public void setRoomPassword(String roomPassword)
    {
        this.roomPassword = roomPassword;
    }
    
    public int getRoomID()
    {
        return this.roomID;
    }
    
    public String getRoomName()
    {
        return this.roomName;
    }
    
    public String getRoomPassword()
    {
        return this.roomPassword;
    }
    
    public int getRoomSize()
    {
        return clientList.size();
    }
    
    public void addClient(Client c)
    {
        clientList.add(c);
    }
    
    public void removeClient(Client c)
    {
        clientList.remove(c);
    }
    
    public Client findClient(String username)
    {
        return clientList.findClient(username);
    }
    
    public Client findClient(int guid)
    {
        return clientList.findClient(guid);
    }
    
    public ListIterator<Client> clientListIterator()
    {
        return clientList.listIterator();
    }
}

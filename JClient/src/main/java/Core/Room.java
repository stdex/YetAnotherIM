package Core;

public class Room
{
    private int roomID;
    private String roomName;
    
    public Room(int roomID, String roomName)
    {
        this.roomID = roomID;
        this.roomName = roomName;
    }
    
    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }
    
    public int getRoomID()
    {
        return this.roomID;
    }
    
    public String getRoomName()
    {
        return this.roomName;
    }
}

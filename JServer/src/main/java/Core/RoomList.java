package Core;

import java.util.ArrayList;
import java.util.ListIterator;

public class RoomList
{
    private ArrayList<Room> list;
    
    public RoomList()
    {
        list = new ArrayList<Room>();
    }
    
    public boolean add(Room r)
    {
        return list.add(r);
    }
    
    public boolean remove(Room r)
    {
        return list.remove(r);
    }
    
    public int size()
    {
        return list.size();
    }
    
    public int getNextRoomID()
    {
        if (size() == 0)
            return 0;
        
        return list.get(size() - 1).getRoomID() + 1;
    }
    
    public Room findRoom(int roomID)
    {
        for (ListIterator<Room> r = list.listIterator(); r.hasNext(); )
            if (r.next().getRoomID() == roomID)
                return r.previous();
        
        return null;
    }
    
    public Room findRoom(String roomName)
    {
        for (ListIterator<Room> r = list.listIterator(); r.hasNext(); )
            if (r.next().getRoomName().equalsIgnoreCase(roomName))
                return r.previous();
        
        return null;
    }
    
    public ListIterator<Room> listIterator()
    {
        return list.listIterator();
    }
}

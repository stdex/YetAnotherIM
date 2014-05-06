package Core;

import UI.RoomChatUI;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * 
 * @author user
 */
public class RoomChatUIList
{
    private ArrayList<RoomChatUI> list;
    
    public RoomChatUIList()
    {
        list = new ArrayList<RoomChatUI>();
    }
    
    public boolean add(RoomChatUI ui)
    {
        return list.add(ui);
    }
    
    public boolean remove(RoomChatUI ui)
    {
        ui.dispose();
        return list.remove(ui);
    }
    
    public void disposeAllUI()
    {
        for (ListIterator<RoomChatUI> ui = list.listIterator(); ui.hasNext(); )
            ui.next().dispose();
        
        list = new ArrayList<RoomChatUI>();
    }
    
    public RoomChatUI findUI(int roomID)
    {
        for (ListIterator<RoomChatUI> ui = list.listIterator(); ui.hasNext(); )
            if (ui.next().getRoom().getRoomID() == roomID)
                return ui.previous();
        
        return null;
    }
    
    public int size()
    {
        return list.size();
    }
}

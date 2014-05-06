package Core;

import UI.SendSubUI;

import java.util.ArrayList;
import java.util.ListIterator;

public class SubsUIList
{
    private ArrayList<SendSubUI> list;
    
    public SubsUIList()
    {
        list = new ArrayList<SendSubUI>();
    }
    
    public boolean add(SendSubUI ui)
    {
        return list.add(ui);
    }
    
    public boolean remove(SendSubUI ui)
    {
        ui.dispose();
        return list.remove(ui);
    }
    
    public void disposeAllUI()
    {
        for (ListIterator<SendSubUI> ui = list.listIterator(); ui.hasNext(); )
            ui.next().dispose();
        
        list = new ArrayList<SendSubUI>();
    }
    
    public SendSubUI findUI(String c)
    {
        for (ListIterator<SendSubUI> ui = list.listIterator(); ui.hasNext(); )
            if (ui.next().getTitle().equals(c))
                return ui.previous();
        
        return null;
    }
    
    public int size()
    {
        return list.size();
    }
}

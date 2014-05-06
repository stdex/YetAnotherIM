package Core;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Contact
{
    private int guid;
    private String username;
    private String title;
    private String psm;
    private int status;
    private Icon icon;
    
    public Contact(int guid, String username, String title, String psm, int status)
    {
        this.guid = guid;
        this.username = username;
        this.title = title;
        this.psm = psm;
        this.status = status;
        
        switch (status)
        {
            case 0:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_online.png"));
                break;
            case 1:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_away.png"));
                break;
            case 2:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_busy.png"));
                break;
            case 3:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_offline.png"));
                break;
        }
        // this.icon = new ImageIcon(getClass().getResource("/Images/icon_online.png"));
    }
    
    public int getGuid()
    {
        return this.guid;
    }
    
    public Icon getIcon() {
        return this.icon;
    }
    
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    
    public String getUsername()
    {
        return this.username;
    }
    
    public String getTitle()
    {
        return this.title;
    }
    
    public String getPSM()
    {
        return this.psm;
    }
    
    public int getStatus()
    {
        return this.status;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public void setPSM(String psm)
    {
        this.psm = psm;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
        
        switch (status)
        {
            case 0:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_online.png"));
                break;
            case 1:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_away.png"));
                break;
            case 2:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_busy.png"));
                break;
            case 3:
                this.icon = new ImageIcon(getClass().getResource("/Images/icon_offline.png"));
                break;
        }
        
    }
    
    // Overide toString() method, so the Contact list can show proper contact detail instead of instance memory location.
    /* Possible combination
     * <STATUS>USERNAME
     * <STATUS>USERNAME - PSM
     * <STATUS>TITLE
     * <STATUS>TITLE - PSM
     */
    
    public String toString()
    {
        String str = "";
        
        switch (status)
        {
            case 0:
                str += "<Доступен>";
                break;
            case 1:
                str += "<Отстутствует>";
                break;
            case 2:
                str += "<Занят>";
                break;
            case 3:
                str += "<Offline>";
                break;
        }
        
        if (title.isEmpty())
            str += username;
        else
            str += title;
        
        if (!psm.isEmpty())
            str += " - " + psm;
        
        return str;
    }
}

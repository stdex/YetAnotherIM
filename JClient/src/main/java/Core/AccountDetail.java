
package Core;

public final class AccountDetail
{
    private static int guid;
    private static String username;
    private static String title;
    private static String psm;
    private static int status;
    
    public static void init(int cGuid, String cUsername, String cTitle, String cPSM, int cStatus)
    {
        guid = cGuid;
        username = cUsername;
        title = cTitle;
        psm = cPSM;
        status = cStatus;
    }
    
    public static void clear()
    {
        guid = 0;
        username = null;
        title = null;
        psm = null;
        status = 0;
    }
    
    public static int getGuid()
    {
        return guid;
    }
    
    public static String getUsername()
    {
        return username;
    }
    
    public static String getTitle()
    {
        return title;
    }
    
    public static String getPSM()
    {
        return psm;
    }
    
    public static int getStatus()
    {
        return status;
    }
    
    public static void setTitle(String newTitle)
    {
        title = newTitle;
    }
    
    public static void setPSM(String newPSM)
    {
        psm = newPSM;
    }
    
    public static void setStatus(int newStatus)
    {
        status = newStatus;
    }
    
    public static String getDisplayTitle()
    {
        return title.equals("") ? username : title;
    }
    
    public static String getUITitle()
    {
        return String.format("YetIM <%s>", getUsername());
    }
}

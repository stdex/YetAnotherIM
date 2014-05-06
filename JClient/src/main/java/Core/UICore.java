package Core;

import UI.ChatUI;
import UI.MasterUI;
import UI.MasterUI.UISwitcher;
import UI.RegistrationUI;

import javax.swing.JOptionPane;

public class UICore
{
    private static MasterUI ui;
    private static ChatUIList chatList;
    private static SubsUIList subsList;
    private static RoomChatUIList roomChatUIList;
    //private static RegistrationUI registrationUI;
    
    public UICore()
    {
    }
    
    public static void initiate()
    {
        ui = new MasterUI();
        chatList = new ChatUIList();
        subsList = new SubsUIList();
        roomChatUIList = new RoomChatUIList();
        //registrationUI = new RegistrationUI();
    }
    
    public static MasterUI getMasterUI()
    {
        return ui;
    }
    /*
    public static RegistrationUI getRegistrationUI()
    {
        
        return registrationUI;
    }
    */
    
    public static ChatUIList getChatUIList()
    {
        return chatList;
    }
    
    public static SubsUIList getSubsUIList()
    {
        return subsList;
    }
    
    public static RoomChatUIList getRoomChatUIList()
    {
        return roomChatUIList;
    }
    
    public static void UpdateContactStatus(int guid, int status)
    {
        Contact c = getMasterUI().searchContact(guid);
        
        if (c != null)
        {
            UICore.getMasterUI().UpdateContactStatus(guid, status);
            
            ChatUI chatUI =  getChatUIList().findUI(c);
            
            if (chatUI != null)
                chatUI.UpdateTitle();
        }
    }
    
    public static void switchUI()
    {
        new Thread(new UISwitcher()).start();
    }
    
    public static void showMessageDialog(Object message, String title, int messageType)
    {
        JOptionPane.showMessageDialog(ui, message, title, messageType);
    }
}

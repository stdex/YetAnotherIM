package Core;

public class Main
{
    public static String login;
    public static String password;
    public static String subsName;
    public static String subsMessage;
    
    public static void main(String[] args) throws Exception
    {

        /*
        if(args.length < 4) {
        return;
        }
        
        System.out.println("Login:" + args[0]);
        System.out.println("Password:" + args[1]);
        System.out.println("Subscribe name:" + args[2]);
        System.out.println("Message:" + args[3]);
        
        login = args[0];
        password = args[1];
        subsName = args[2];
        subsMessage = args[3];
        */
        
        login = "test2";
        password = "test2";
        subsName = "Тестовая";
        subsMessage = "Message123!";
        
        
        NetworkManager.login(login, password, 0);
    }
}

package Core;

import static Core.Main.serverSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CliHandler implements Runnable
{
    private BufferedReader in;
    
    public CliHandler() throws Exception
    {
        System.out.printf("\nInitializing Cli Command Handler.\n");
        in = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public void run()
    {
        System.out.printf("Cli Command Handler started.\n");
        
        String command;
        
        try
        {
            while(true)
            {
                command = in.readLine().trim();
                
                if (command.isEmpty())
                    continue;
                
                System.out.printf("Command Receive: %s\n", command);
                
                if (command.equals("uptime"))
                    System.out.printf("Server Uptime: %dsec.\n", (System.currentTimeMillis() - Main.startupTime) / 1000);
                else if (command.equals("clist"))
                    System.out.printf("Current Client List Size: %d\n", Main.clientList.size());
                else if (command.equals("quit") || command.equals("exit"))
                {
                    destroy();
                    System.exit(0);
                }
                else
                    System.out.printf("Unknown Command: %s\n", command);
            }
        }
        catch(Exception e){}
    }
    
        
    public static void destroy()
    {
        try { serverSocket.close(); }
        catch (Exception e) {}
        
        serverSocket = null;
    }
}

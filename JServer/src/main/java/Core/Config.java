
package Core;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;

public class Config
{
    private static Properties config;
    
    public static void loadConfig() throws Exception
    {
        config = new Properties();
        
        System.out.printf("Begin to load config.\n");
        
        Scanner scanner = new Scanner(new File("config.conf"));
        
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine().trim();
            
            // All comment start with #, also ignore empty line
            if (line.startsWith("#") || line.equals(""))
                continue;
            
            // No key? No value? We dont accept this...
            if (!line.contains("="))
            {
                System.out.printf("An error occur while reading config file!\n");
                System.exit(0);
            }
            
            String key = line.substring(0,line.indexOf("=")).trim();
            String value = line.substring(line.indexOf("=") + 1).trim().replace("\"", "");
            
            config.setProperty(key, value);
        }
        
        System.out.printf("Config loaded successfully.\n");
    }
    
    public static boolean getBoolDefault(String key, boolean defaultValue)
    {
        String value = config.getProperty(key);
        
        if (value == null)
            return defaultValue;
        
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"))
            return true;
        else
            return false;
    }
    
    public static int getIntDefault(String key, int defaultValue)
    {
        String value = config.getProperty(key);
        
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
    
    public static String getStringDefault(String key, String defaultValue)
    {
        return config.getProperty(key, defaultValue);
    }
}
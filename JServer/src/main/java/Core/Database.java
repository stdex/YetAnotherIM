package Core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class Database
{
    private Connection conn;
    
    public Database(String connString, String login, String password) throws Exception
    {
        System.out.printf("\nInitializing database connection.\n");
        System.out.printf("Connection String: %s\n", connString);
        
        //Class.forName("com.mysql.jdbc.Driver").newInstance();
        Class.forName("org.postgresql.Driver").newInstance();
        
        conn = DriverManager.getConnection(connString, login, password);
        //conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/yetAIM", "postgres","postgres");
        
        System.out.printf("Database Connection Successful\n");
    }
    
    public int execute(String sql, Object... args)
    {
        try
        {
            System.out.printf("SQL: " + sql + "\n", args);
            //Statement s = conn.createStatement();
            Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            //System.out.println(String.format(Locale.forLanguageTag("ru_RU"), sql, args));
            return s.executeUpdate(String.format(Locale.forLanguageTag("ru_RU"), sql, args));
        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
            System.err.println("Message: " + e.getMessage());
            
            return 0;
        }
    }
    
    public ResultSet query(String sql, Object... args)
    {
        try
        {
            System.out.printf("SQL: " + sql + "\n", args);
            //Statement s = conn.createStatement();
            Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            return s.executeQuery(String.format(Locale.forLanguageTag("ru_RU"), sql, args));
        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
            System.err.println("Message: " + e.getMessage());
            
            return null;
        }
    }
}
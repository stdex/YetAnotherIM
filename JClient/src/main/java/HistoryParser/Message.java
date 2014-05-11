package HistoryParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rostunov_Sergey
 */
public class Message {
    
    private String username;
    private String datetime;
    private String message;
    private String type;

    public Message() {
    }

    public Message(String username, String datetime, String message, String type) {
        this.username = username;
        this.datetime = datetime;
        this.message = message;
        this.type = type;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return type+"::"+username+"::"+datetime+"\n"+message.trim();
        //return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}

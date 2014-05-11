package HistoryParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExParser {

	private static final Pattern outMark = Pattern.compile("-------------------------------------->-");
	private static final Pattern inMark = Pattern.compile("--------------------------------------<-");
	private static final Pattern name = Pattern.compile("[A-Za-z0-9_]{3,15}");
	private static final Pattern datetime = Pattern.compile("([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])\\s((0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d))");
//	private static final Pattern msg = Pattern.compile("(.+?){1,}");
        
	private List<Message> list = new ArrayList<Message>();
	
	
	public boolean parse(String input) {
		list.clear();
                
                String type = "";
                String username = "";
                String dt = "";
                String message = "";
                
                Scanner scanner = new Scanner(input);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    
                    int state = 0;
	
                    Matcher outMatch = outMark.matcher(line);
                    Matcher inMatch = inMark.matcher(line);
                    Matcher nameMatch = name.matcher(line);
                    Matcher datetimeMatch = datetime.matcher(line);
//                  Matcher msgMatch = msg.matcher(line);
                    
                    if (datetimeMatch.find()){
                            dt = datetimeMatch.group();
                            state = 1;
                            
                                if(nameMatch.find()){ 
					username = nameMatch.group();
                                        state = 2;
				}
                            
                    }

                    
                    //System.out.println(line);
                    else if (outMatch.find()){
                        
                        state = 4;
                        type = "out";

                        list.add(new Message(username, dt, message, type));
                        
                        type = "";
                        username = "";
                        dt = "";
                        message = "";
                    }
                    else if(inMatch.find()) {
                        
                        state = 4;
                        type = "in";
                        
                        list.add(new Message(username, dt, message, type));
                        type = "";
                        username = "";
                        dt = "";
                        message = "";
                    }
                    
                    else { 
                            message = message + line+"\n";
                            state = 3;
                    }
                  
                }
                scanner.close();
                
		
		return true;
	}
	
	public List<Message> getList() {
		return list;
	}

}

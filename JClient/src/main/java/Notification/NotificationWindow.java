package Notification;

import Core.Contact;
import Core.UICore;
import UI.ChatUI;
import UI.SendSubUI;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class NotificationWindow extends javax.swing.JDialog implements TimedEventCallback {
	
	// this code is static to manage the number of windows that are open
	protected static int numOpen;
	
	protected static int getOpen() { return numOpen; }
	protected static void windowOpen() { numOpen++; }
	protected static void windowClose() { numOpen--; }
	
	
	
	
	private static final long serialVersionUID = 1L;
	private JPanel jPanel1;
	private JLabel jlSubject;
	private JLabel jlMessage;
//	private Canvas canvas1;
	
	
	protected TimedEventThread myThread;
	
	public NotificationWindow(/*Image theImage, */String subject, String message, int timeToLive, final String mode, final Contact s_contact, final String titlef) {
		super();
                		
                this.setUndecorated(true);
		//initGUI(theImage);
                initGUI();
		// image x y width height bgcolor null
		jlSubject.setText(subject);
		jlMessage.setText(message);
		
		myThread = new TimedEventThread(timeToLive, this);
		myThread.start();

		Dimension ourDim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)ourDim.getWidth() - this.getWidth(), 10 + ((this.getHeight() + 34) * NotificationWindow.getOpen()));
	     
		NotificationWindow.windowOpen();

		this.setVisible(true);
                //
                
                

            this.addMouseListener(new MouseAdapter() {  
                      public void mousePressed(MouseEvent me){                   
                          System.out.println("Click!");
              
                   if(mode == "chat") {
                        ChatUI targetUI = UICore.getChatUIList().findUI(s_contact);

                        if (targetUI == null)
                            UICore.getChatUIList().add(targetUI = new ChatUI(s_contact));
                    
                    // Output the message in sender ChatUI.
                    //targetUI.append(s_contact.getTitle(), to, message, datetime);
                    targetUI.toFront();
                   }
                   else if (mode == "subscribe") {
                        SendSubUI targetUI = UICore.getSubsUIList().findUI(titlef);
        
                        if (targetUI == null)
                            UICore.getSubsUIList().add(targetUI = new SendSubUI(titlef));
                        
                        targetUI.toFront();
                   }
                          

                          }
                  }); 
                
                //setLocationRelativeTo(null);
                //setOpacity(0.70f);
		
	}
	
	
	private void initGUI(/*Image toShow*/) {
            
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
                            
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				GroupLayout jPanel1Layout = new GroupLayout((JComponent)jPanel1);
				jPanel1.setLayout(jPanel1Layout);
				jPanel1.setPreferredSize(new java.awt.Dimension(337, 67));
				/*
                                {
					canvas1 = new ImageDrawCanvas(toShow);
					canvas1.setSize(48, 48);
				}
                               */
				{
					jlSubject = new JLabel();
					jlSubject.setText("Subject");
				}
				{
					jlMessage = new JLabel();
					jlMessage.setText("Message");
				}
				jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
					.addContainerGap()
					//.addComponent(canvas1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(jPanel1Layout.createParallelGroup()
					    .addComponent(jlSubject, GroupLayout.Alignment.LEADING, 0, 245, Short.MAX_VALUE)
					    .addComponent(jlMessage, GroupLayout.Alignment.LEADING, 0, 245, Short.MAX_VALUE))
					.addContainerGap());
				jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(jPanel1Layout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
					        .addComponent(jlSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					        .addGap(0, 16, Short.MAX_VALUE)
					        .addComponent(jlMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					        .addGap(0, 12, GroupLayout.PREFERRED_SIZE))
					 //   .addComponent(canvas1, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        ));


			}
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void timeUp() {

		this.setVisible(false);
		NotificationWindow.windowClose();
	}

}

package Notification;

import Core.Contact;
import Core.UICore;
import UI.ChatUI;
import static UI.ChatUI.logChat;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class NotificationPopup extends JDialog {
  private final LinearGradientPaint lpg;
 
  public NotificationPopup() {
    setUndecorated(true);
    setSize(300, 60);
    // size of the screen
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
 
    // height of the task bar
    final Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
    final int taskBarSize = scnMax.bottom;
 
    setLocation(screenSize.width - getWidth(), screenSize.height - taskBarSize  - getHeight());
 
    // background paint
    lpg = new LinearGradientPaint(0, 0, 0, getHeight() / 2,
 new float[] { 0f, 0.3f, 1f }, new Color[]
 { new Color(1f, 1f, 1f), new Color(1f, 1f, 1f),
  new Color(1f, 1f, 1f) });
 
    // blue background panel
    setContentPane(new BackgroundPanel());
  }
 
  private class BackgroundPanel extends JPanel {
    public BackgroundPanel() {
      setOpaque(true);
    }
 
    @Override
    protected void paintComponent(final Graphics g) {
      final Graphics2D g2d = (Graphics2D) g;
      // background
      g2d.setPaint(lpg);
      g2d.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
      g2d.setColor(Color.RED); //border color
 
      // border
      g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }
 
      
    public static void showNotificationMSG(String from, String to, String datetime, String msg, Contact s_contact) throws ParseException {
             
            final  NotificationPopup f = new NotificationPopup();
            final  Container c = f.getContentPane();
            c.setLayout(new GridBagLayout());
 
            final GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0f;
            constraints.weighty = 1.0f;
            constraints.insets = new Insets(5, 5, 5, 5);
            constraints.fill = GridBagConstraints.BOTH;
 
            final JLabel l = new JLabel("<html><i>"+datetime+"</i> :: <b>"+from+"</b> --> <b>"+to+"<br/>"+msg+"</html>");
            l.setOpaque(false);
 
            c.add(l, constraints);
 
            constraints.gridx++;
            constraints.weightx = 0f;
            constraints.weighty = 0f;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.NORTH;
 
            f.setVisible(true);

            Date nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            String nTime = sdf.format(nowTime);
                                    
                   ChatUI targetUI = UICore.getChatUIList().findUI(s_contact);
                   
        
                    if (targetUI == null)
                        UICore.getChatUIList().add(targetUI = new ChatUI(s_contact));
                    
                    // Output the message in sender ChatUI.
                    targetUI.append(s_contact.getTitle(), to, msg, datetime);
                    targetUI.toFront();
        String outputMSG = new StringBuilder(String.format("%s ", s_contact.getTitle())).append(String.format("(%s)\n", nTime)).append(String.format("%s\n", msg)).toString();            
        logChat(outputMSG, from, to, "in");

            c.addMouseListener(new MouseAdapter() {  
                      public void mousePressed(MouseEvent me){  
                 
                          //
                          //System.out.println("Remove this label");  

                          }  
                  });  
        
            new Thread(){ 
                    @Override
                    public void run() {

                         try {
                                Thread.sleep(4000); // time after which pop up will be disappeared.
                                f.dispose();
                         } catch (InterruptedException e) {
                                e.printStackTrace();
                         }
                    };
              }.start();
  
        
    }
  
}
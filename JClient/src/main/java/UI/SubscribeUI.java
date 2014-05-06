/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import Core.NetworkManager;
import static Core.NetworkThread.titles;
import Core.Opcode;
import static Core.Opcode.CMSG_GET_SUBLIST;
import static Core.Opcode.CMSG_SUBSCRIBE;
import static Core.Opcode.СMSG_ADD_NEW_SUBSCRIBE;
import Core.Packet;
import Core.UICore;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author user
 */
public class SubscribeUI extends javax.swing.JFrame {

    
    private JTable target;
    //public Map<Integer, List> select_value = new HashMap<Integer, List>();
    public static String select_value;
    public Integer selectedRow;
    public Integer selectedColumn;
    public int[] selectedRows;
    public static SendSubUI ui;
    
    /**
     * Creates new form SubscribeUI
     */
    public SubscribeUI() {
        initComponents();
        
       this.setVisible(true);
        
       final JFrame frameme = this;
       frameme.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
       //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       setLocationRelativeTo(UICore.getMasterUI());
       
       frameme.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                frameme.dispose();
            }
        });
        frameme.setVisible(true);

        
        
        createForm();
    }
    
 
    public void createForm() {   
        /*
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setNumRows(0);
      
        for (int count = 0; count < titles.size(); count++){
            tableModel.addRow(new Object[]{1});
            tableModel.setValueAt(titles.get(count), count, 0);
        }
        
       jTable1.setModel(tableModel);
        */
        
       jTable1.addMouseListener(new MouseAdapter()  
       {  
                  public void mouseClicked(MouseEvent e)  
                    {  
                      if (e.getClickCount() == 1)  
                      {  
                          //String selectedData = null;
                          //Object selectedCellValue;

                       target = (JTable)e.getSource();  

                       selectedRow = target.getSelectedRow();  
                       selectedColumn = target.getSelectedColumn();  
                       
                       select_value = (String)target.getValueAt(selectedRow, 0);

                       System.out.println("Selected row "+ " "+ selectedRow);  
                       System.out.println("Selected column"+ " "+ selectedColumn);
                       System.out.println("Selected value"+ " "+ select_value);
                       System.out.println("------------------------------------");
                       
                        if (selectedColumn == 2) {

                            SendSubUI ui = UICore.getSubsUIList().findUI(select_value);
                
                            if (ui != null)
                            {
                                if (ui.getState() == JFrame.ICONIFIED)
                                    ui.setState(JFrame.NORMAL);

                                ui.toFront();
                            }
                            else
                                UICore.getSubsUIList().add(new SendSubUI(select_value));

                             //new UI.SendSubscribeUI();
                             //ui = new UI.SendSubUI(select_value);
                            
                        } 

                       }  
                     }
   
      });
       
      // TODO: bullshit...
      //while(titles == null) {
         tryGetSubscribeList();  
      //}
      
      System.out.println(titles);
      
      /*
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setNumRows(0);
      
        for (int count = 0; count < titles.size(); count++){
            tableModel.addRow(new Object[]{1});
            tableModel.setValueAt(titles.get(count), count, 0);
        }
        
      jTable1.setModel(tableModel);
      */
      
      //populateTable();
       
      jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                tryAddNewSubscrube();
                //jOptionPane1.showMessageDialog(null, "Вы не выбрали запись в таблице.");

            }
        });
      
      
     jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                trySubscrube();
                //jOptionPane1.showMessageDialog(null, "Вы не выбрали запись в таблице.");
                
            }
        });

}
    
    public void populateTable() {
        
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setNumRows(0);
      
        for (int count = 0; count < titles.size(); count++){
            tableModel.addRow(new Object[]{1});
            tableModel.setValueAt(titles.get(count), count, 0);
            
            //UICore.getSubsUIList().add(new SendSubUI(titles.get(count).getTitle()));
        }
        
        jTable1.setModel(tableModel);
    }
    
    public void tryGetSubscribeList() {
        Packet p = new Packet(CMSG_GET_SUBLIST);
        NetworkManager.SendPacket(p);
    }
    
    public void trySubscrube()
    {

        if (!select_value.equals(""))
        {
            Packet p = new Packet(CMSG_SUBSCRIBE);
            p.put(select_value);
            
            NetworkManager.SendPacket(p);
            
            tryGetSubscribeList();
            populateTable();
            //dispose();
        }
        else {
            jOptionPane1.showMessageDialog(null, "Вы не выбрали подписку.", "Ошибка", jOptionPane1.ERROR_MESSAGE);
        }

    }
        
    public void tryAddNewSubscrube()
    {
        String title = jTextField1.getText().trim();
        
        //jOptionPane1.showMessageDialog(null, title, "Ошибка", jOptionPane1.ERROR_MESSAGE);

        if (!title.equals(""))
        {
            Packet p = new Packet(СMSG_ADD_NEW_SUBSCRIBE);
            p.put(title);
            
            NetworkManager.SendPacket(p);
            
            tryGetSubscribeList();
            populateTable();
            //dispose();
        }
        else {
            jOptionPane1.showMessageDialog(null, "Название подписки не введено", "Ошибка", jOptionPane1.ERROR_MESSAGE);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jOptionPane1 = new javax.swing.JOptionPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Название", "Статус", "Написать"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("Добавить");

        jButton3.setText("Подписаться");

        jButton4.setText("Отписаться");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addGap(132, 132, 132))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SubscribeUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SubscribeUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SubscribeUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SubscribeUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SubscribeUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}

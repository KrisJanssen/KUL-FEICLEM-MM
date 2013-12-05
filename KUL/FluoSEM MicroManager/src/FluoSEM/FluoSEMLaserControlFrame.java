/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FluoSEM;


import mmcorej.CMMCore;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import javax.swing.Timer;
import org.micromanager.api.ScriptInterface;
/**
 *
 * @author supervisor
 */
public class FluoSEMLaserControlFrame extends javax.swing.JFrame {

   private final ScriptInterface gui_;
   private final CMMCore core_;
   
   
   private static String nm405Label = "Laser 405nm";
   private static String nm445Label = "Laser 445nm";
   private static String nm488Label = "Laser 488nm";
   private static String nm532Label = "Laser 532nm";
   private static String nm561Label = "Laser 561nm";
   private static String nm642Label = "Laser 642nm";
   
   private static String nm405ENLabel = "Laser 405nmEN";
   private static String nm445ENLabel = "Laser 445nmEN";
   private static String nm488ENLabel = "Laser 488nmEN";
   private static String nm532ENLabel = "Laser 532nmEN";
   private static String nm561ENLabel = "Laser 561nmEN";
   private static String nm642ENLabel = "Laser 642nmEN";
   
   private static String nm365PLabel = "Diode 365nmP";
   private static String nm405PLabel = "Laser 405nmP";
   private static String nm445PLabel = "Laser 445nmP";
   private static String nm488PLabel = "Laser 488nmP";
   private static String nm532PLabel = "Laser 532nmP";
   private static String nm561PLabel = "Laser 561nmP";
   private static String nm642PLabel = "Laser 642nmP";
    /**
     * Creates new form FluoSEMLaserControlFrame
     */
    public FluoSEMLaserControlFrame(ScriptInterface gui) {

        gui_ = gui;
        core_ = gui_.getMMCore();

        initComponents();
        setLocation(0, 0);

        setBackground(gui_.getBackgroundColor());
        gui_.addMMBackgroundListener(this);

        try {
            core_.setProperty(nm405Label, "Laser Power Set-point Select [mW]", "120.0");
        } catch (Exception e) {
            gui_.logError(e);
        }
        try {
            core_.setProperty(nm445Label, "Laser Power Set-point Select [mW]", "100.0");
        } catch (Exception e) {
            gui_.logError(e);
        }
        try {
            core_.setProperty(nm488Label, "Laser Power Set-point Select [mW]", "200.0");
        } catch (Exception e) {
            gui_.logError(e);
        }
        try {
            core_.setProperty(nm642Label, "Laser Power Set-point Select [mW]", "140.0");
        } catch (Exception e) {
            gui_.logError(e);
        }
                try {
            core_.setProperty(nm532Label, "Power", 0.3);
        } catch (Exception e) {
            gui_.logError(e);
        }
                        try {
            core_.setProperty(nm561Label, "Power", 0.15);
        } catch (Exception e) {
            gui_.logError(e);
        }
        try {
                core_.setProperty(nm365PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }        
        try {
                core_.setProperty(nm405ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm405PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm445ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm445PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm488ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm488PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm532ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm532PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm561ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm561PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm642ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm642PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
       

        
        
                ActionListener taskPerformer = new ActionListener(){
            public void actionPerformed(ActionEvent d) {
                // 1 Sekunde abziehen 

                try {
                    jPower1.setText(core_.getProperty(nm405Label, "Laser Power Status [mW]"));
                } catch (Exception e) {
                    gui_.logError(e);
                }
                try {
                    jPower2.setText(core_.getProperty(nm445Label, "Laser Power Status [mW]"));
                } catch (Exception e) {
                    gui_.logError(e);
                }
                try {
                    jPower3.setText(core_.getProperty(nm488Label, "Laser Power Status [mW]"));
                } catch (Exception e) {
                    gui_.logError(e);
                }
                try {
                    jPower12.setText(core_.getProperty(nm642Label, "Laser Power Status [mW]"));
                } catch (Exception e) {
                    gui_.logError(e);
                }

            }
                };
        
        new Timer(1000, taskPerformer).start();
    }
                                           

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Title = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPower1 = new javax.swing.JLabel();
        jLasermWSet1 = new javax.swing.JTextField();
        jLaserPercentSet1 = new javax.swing.JTextField();
        jLaserOnOff1 = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLasermWSet10 = new javax.swing.JTextField();
        jLaserOnOff10 = new javax.swing.JToggleButton();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLaserPercentSet10 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPower2 = new javax.swing.JLabel();
        jLasermWSet2 = new javax.swing.JTextField();
        jLaserPercentSet2 = new javax.swing.JTextField();
        jLaserOnOff2 = new javax.swing.JToggleButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLasermWSet11 = new javax.swing.JTextField();
        jLaserOnOff11 = new javax.swing.JToggleButton();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLaserPercentSet11 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPower3 = new javax.swing.JLabel();
        jLasermWSet3 = new javax.swing.JTextField();
        jLaserPercentSet3 = new javax.swing.JTextField();
        jLaserOnOff3 = new javax.swing.JToggleButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPower12 = new javax.swing.JLabel();
        jLasermWSet12 = new javax.swing.JTextField();
        jLaserPercentSet12 = new javax.swing.JTextField();
        jLaserOnOff12 = new javax.swing.JToggleButton();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLasermWSet13 = new javax.swing.JTextField();
        jLaserOnOff13 = new javax.swing.JToggleButton();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLaserPercentSet13 = new javax.swing.JTextField();

        setMinimumSize(new java.awt.Dimension(650, 454));

        Title.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Title.setText("FluoSEM Laser Control");

        jPower1.setText("jPower1");

        jLasermWSet1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet1.setText("0.0");
        jLasermWSet1.setAutoscrolls(false);
        jLasermWSet1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet1KeyPressed(evt);
            }
        });

        jLaserPercentSet1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet1.setText("0");
        jLaserPercentSet1.setAutoscrolls(false);
        jLaserPercentSet1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jLaserPercentSet1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet1KeyPressed(evt);
            }
        });

        jLaserOnOff1.setText("ON");
        jLaserOnOff1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("405 nm");

        jLabel2.setText("Set Power");

        jLabel3.setText("mW");

        jLabel4.setText("%");

        jLabel5.setText("Actual");

        jLasermWSet10.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet10.setText("0.0");
        jLasermWSet10.setAutoscrolls(false);
        jLasermWSet10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet10KeyPressed(evt);
            }
        });

        jLaserOnOff10.setText("ON");
        jLaserOnOff10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff10ActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("532 nm");

        jLabel47.setText("Set Power");

        jLabel48.setText("mW");

        jLabel16.setText("%");

        jLaserPercentSet10.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet10.setText("0");
        jLaserPercentSet10.setAutoscrolls(false);
        jLaserPercentSet10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet10KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLasermWSet10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel48))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jLaserOnOff10)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLasermWSet1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jPower1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPower1)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(jLaserOnOff1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPower2.setText("jPower1");

        jLasermWSet2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet2.setText("0.0");
        jLasermWSet2.setAutoscrolls(false);
        jLasermWSet2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet2KeyPressed(evt);
            }
        });

        jLaserPercentSet2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet2.setText("0");
        jLaserPercentSet2.setAutoscrolls(false);
        jLaserPercentSet2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet2KeyPressed(evt);
            }
        });

        jLaserOnOff2.setText("ON");
        jLaserOnOff2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("445 nm");

        jLabel7.setText("Set Power");

        jLabel8.setText("mW");

        jLabel9.setText("%");

        jLabel10.setText("Actual");

        jLasermWSet11.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet11.setText("0.0");
        jLasermWSet11.setAutoscrolls(false);
        jLasermWSet11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet11KeyPressed(evt);
            }
        });

        jLaserOnOff11.setText("ON");
        jLaserOnOff11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff11ActionPerformed(evt);
            }
        });

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("561 nm");

        jLabel52.setText("Set Power");

        jLabel53.setText("mW");

        jLaserPercentSet11.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet11.setText("0");
        jLaserPercentSet11.setAutoscrolls(false);
        jLaserPercentSet11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet11KeyPressed(evt);
            }
        });

        jLabel17.setText("%");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel52)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLasermWSet11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel53))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17)))
                        .addGap(0, 24, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLaserOnOff11)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLasermWSet2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(jPower2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPower2)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addComponent(jLaserOnOff2)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPower3.setText("jPower1");

        jLasermWSet3.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet3.setText("0.0");
        jLasermWSet3.setAutoscrolls(false);
        jLasermWSet3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet3KeyPressed(evt);
            }
        });

        jLaserPercentSet3.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet3.setText("0");
        jLaserPercentSet3.setAutoscrolls(false);
        jLaserPercentSet3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet3KeyPressed(evt);
            }
        });

        jLaserOnOff3.setText("ON");
        jLaserOnOff3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff3ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("488 nm");

        jLabel12.setText("Set Power");

        jLabel13.setText("mW");

        jLabel14.setText("%");

        jLabel15.setText("Actual");

        jPower12.setText("jPower1");

        jLasermWSet12.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet12.setText("0.0");
        jLasermWSet12.setAutoscrolls(false);
        jLasermWSet12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet12KeyPressed(evt);
            }
        });

        jLaserPercentSet12.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet12.setText("0");
        jLaserPercentSet12.setAutoscrolls(false);
        jLaserPercentSet12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet12KeyPressed(evt);
            }
        });

        jLaserOnOff12.setText("ON");
        jLaserOnOff12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff12ActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("642 nm");

        jLabel57.setText("Set Power");

        jLabel58.setText("mW");

        jLabel59.setText("%");

        jLabel60.setText("Actual");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLasermWSet12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel58))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel59))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addGap(18, 18, 18)
                                .addComponent(jPower12, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(jPower12))
                .addGap(18, 18, 18)
                .addComponent(jLaserOnOff12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLasermWSet3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(jPower3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPower3)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addComponent(jLaserOnOff3)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLasermWSet13.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet13.setText("0.0");
        jLasermWSet13.setAutoscrolls(false);
        jLasermWSet13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet13KeyPressed(evt);
            }
        });

        jLaserOnOff13.setText("ON");
        jLaserOnOff13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff13ActionPerformed(evt);
            }
        });

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("365 nm");

        jLabel50.setText("Set Power");

        jLabel54.setText("mW");

        jLabel18.setText("%");

        jLaserPercentSet13.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet13.setText("0");
        jLaserPercentSet13.setAutoscrolls(false);
        jLaserPercentSet13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet13KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLasermWSet13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel54))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(51, 51, 51)
                .addComponent(jLaserOnOff13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Title)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        setBounds(0, 0, 660, 489);
    }// </editor-fold>//GEN-END:initComponents

    private void jLasermWSet1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet1KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm405PLabel, "Volts", Float.parseFloat(jLasermWSet1.getText())/24);
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                jLaserPercentSet1.setText(Double.toString(Math.round((Double.parseDouble(jLasermWSet1.getText())/1.2)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
       /*     try {
                jLasermWSet1.setText(core_.getProperty(nm405Label, "Laser Power Set-point Select [mW]"));
            } catch (Exception e) {
                gui_.logError(e);
            }*/
        }
    }//GEN-LAST:event_jLasermWSet1KeyPressed

    private void jLaserPercentSet1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet1KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm405PLabel, "Volts", Float.parseFloat(jLaserPercentSet1.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
           /* try {
                jLaserPercentSet1.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLasermWSet1.setText(Double.toString(Math.round((Double.parseDouble(jLaserPercentSet1.getText())*1.2)*100)/100.d ));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserPercentSet1KeyPressed

    private void jLaserOnOff1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff1ActionPerformed
        if (jLaserOnOff1.isSelected()) {
                        try {
            core_.setProperty(nm405Label, "Laser Operation Select", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm405ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        } else {
                        try {
            core_.setProperty(nm405Label, "Laser Operation Select", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm405ENLabel, "Volts", "0.0");
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserOnOff1ActionPerformed

    private void jLasermWSet2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet2KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm445PLabel, "Volts", Float.parseFloat(jLasermWSet2.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                jLaserPercentSet2.setText(Float.toString(Math.round((Float.parseFloat(jLasermWSet2.getText()))*100)/100.f));
            } catch (Exception e) {
                gui_.logError(e);
            }
           /* try {
                jLasermWSet2.setText(core_.getProperty(nm445Label, "Laser Power Set-point Select [mW]"));
            } catch (Exception e) {
                gui_.logError(e);
            }*/
        }
    }//GEN-LAST:event_jLasermWSet2KeyPressed

    private void jLaserPercentSet2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet2KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm445PLabel, "Volts", Float.parseFloat(jLaserPercentSet2.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
            /*try {
                jLaserPercentSet2.setText(core_.getProperty(nm445Label, "Laser Power Set-point Select [%]").substring(0, core_.getProperty(nm445Label, "Laser Power Set-point [%]").length() - 2));
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLasermWSet2.setText(Float.toString(Math.round((Float.parseFloat(jLaserPercentSet2.getText()))*100)/100.f));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
        
    }//GEN-LAST:event_jLaserPercentSet2KeyPressed

    private void jLaserOnOff2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff2ActionPerformed
           if (jLaserOnOff2.isSelected()) {
                           try {
            core_.setProperty(nm445Label, "Laser Operation Select", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm445ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        } else {
                           try {
            core_.setProperty(nm445Label, "Laser Operation Select", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm445ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserOnOff2ActionPerformed

    private void jLasermWSet3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet3KeyPressed
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm488PLabel, "Volts", Float.parseFloat(jLasermWSet3.getText())/40);
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                jLaserPercentSet3.setText(Double.toString(Math.round((Double.parseDouble(jLasermWSet3.getText())/2)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
         /*   try {
                jLasermWSet3.setText(Double.toString(Double.parseDouble(jLasermWSet1.getText())/2));
            } catch (Exception e) {
                gui_.logError(e);
            }*/
        }
    }//GEN-LAST:event_jLasermWSet3KeyPressed

    private void jLaserPercentSet3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet3KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm488PLabel, "Volts", Float.parseFloat(jLaserPercentSet3.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
          /*  try {
                jLaserPercentSet3.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLasermWSet3.setText(Double.toString(Math.round((Double.parseDouble(jLaserPercentSet3.getText())*2)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    
    }//GEN-LAST:event_jLaserPercentSet3KeyPressed

    private void jLaserOnOff3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff3ActionPerformed
               if (jLaserOnOff3.isSelected()) {
                               try {
            core_.setProperty(nm488Label, "Laser Operation Select", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm488ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        } else {
                               try {
            core_.setProperty(nm488Label, "Laser Operation Select", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm488ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserOnOff3ActionPerformed

    private void jLasermWSet10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet10KeyPressed
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm532PLabel, "Volts", (Float.parseFloat(jLasermWSet10.getText()))/90.9);
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
            jLaserPercentSet10.setText(Double.toString(Math.round((Double.parseDouble(jLasermWSet10.getText())/3)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLasermWSet10KeyPressed

    private void jLaserOnOff10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff10ActionPerformed
                   if (jLaserOnOff10.isSelected()) {
                                   try {
            core_.setProperty(nm532Label, "Laser", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm532ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        } else {
                                   try {
            core_.setProperty(nm532Label, "Laser", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm532ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserOnOff10ActionPerformed

    private void jLasermWSet11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet11KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm561PLabel, "Volts", (Float.parseFloat(jLasermWSet11.getText()))/45.45);
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
            jLaserPercentSet11.setText(Double.toString(Math.round((Double.parseDouble(jLasermWSet11.getText())/1.5)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLasermWSet11KeyPressed

    private void jLaserOnOff11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff11ActionPerformed
               if (jLaserOnOff11.isSelected()) {
                               try {
            core_.setProperty(nm561Label, "Laser", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm561ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        } else {
                               try {
            core_.setProperty(nm561Label, "Laser", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm561ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserOnOff11ActionPerformed

    private void jLaserPercentSet10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet10KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm532PLabel, "Volts", Float.parseFloat(jLaserPercentSet10.getText())/30.3);
            } catch (Exception e) {
                gui_.logError(e);
            }
          /*  try {
                jLaserPercentSet3.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLasermWSet10.setText(Double.toString(Math.round((Double.parseDouble(jLaserPercentSet10.getText())*3)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserPercentSet10KeyPressed

    private void jLaserPercentSet11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet11KeyPressed
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm561PLabel, "Volts", Float.parseFloat(jLaserPercentSet11.getText())/30.3);
            } catch (Exception e) {
                gui_.logError(e);
            }
          /*  try {
                jLaserPercentSet3.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLasermWSet11.setText(Double.toString(Math.round((Double.parseDouble(jLaserPercentSet11.getText())*1.5)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserPercentSet11KeyPressed

    private void jLaserOnOff12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff12ActionPerformed
        if (jLaserOnOff12.isSelected()) {
            try {
                core_.setProperty(nm642Label, "Laser Operation Select", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }

            try {
                core_.setProperty(nm642ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }

        } else {
            try {
                core_.setProperty(nm642Label, "Laser Operation Select", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm642ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserOnOff12ActionPerformed

    private void jLaserPercentSet12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet12KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {

                core_.setProperty(nm642PLabel, "Volts", Float.parseFloat(jLaserPercentSet12.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
            /*  try {
                jLaserPercentSet3.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLasermWSet12.setText(Double.toString(Math.round((Double.parseDouble(jLaserPercentSet12.getText())*1.4)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserPercentSet12KeyPressed

    private void jLasermWSet12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet12KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm642PLabel, "Volts", Float.parseFloat(jLasermWSet12.getText())/28);
            } catch (Exception e) {
                gui_.logError(e);
            }
            /*  try {
                jLaserPercentSet3.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLaserPercentSet12.setText(Double.toString(Math.round((Double.parseDouble(jLasermWSet12.getText())/1.4)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLasermWSet12KeyPressed

    private void jLaserPercentSet13KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet13KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && jLaserOnOff13.isSelected()) {
            try {
                core_.setProperty(nm365PLabel, "Volts", Float.parseFloat(jLaserPercentSet13.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
            /*  try {
                jLaserPercentSet3.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLasermWSet13.setText(Double.toString(Math.round((Double.parseDouble(jLaserPercentSet13.getText())*0.03)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserPercentSet13KeyPressed

    private void jLaserOnOff13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff13ActionPerformed
        if (jLaserOnOff13.isSelected()) {
            try {

                core_.setProperty(nm365PLabel, "Volts", Float.parseFloat(jLaserPercentSet13.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
        } else {
            try {
                core_.setProperty(nm365PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLaserOnOff13ActionPerformed

    private void jLasermWSet13KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet13KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && jLaserOnOff13.isSelected()) {
            try {
                core_.setProperty(nm365PLabel, "Volts", Float.parseFloat(jLasermWSet13.getText())/28);
            } catch (Exception e) {
                gui_.logError(e);
            }
            /*  try {
                jLaserPercentSet3.setText();
            } catch (Exception e) {
                gui_.logError(e);
            }*/
            try {
                jLaserPercentSet13.setText(Double.toString(Math.round((Double.parseDouble(jLasermWSet13.getText())/1.4)*100)/100.d));
            } catch (Exception e) {
                gui_.logError(e);
            }
        }
    }//GEN-LAST:event_jLasermWSet13KeyPressed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Title;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JToggleButton jLaserOnOff1;
    private javax.swing.JToggleButton jLaserOnOff10;
    private javax.swing.JToggleButton jLaserOnOff11;
    private javax.swing.JToggleButton jLaserOnOff12;
    private javax.swing.JToggleButton jLaserOnOff13;
    private javax.swing.JToggleButton jLaserOnOff2;
    private javax.swing.JToggleButton jLaserOnOff3;
    private javax.swing.JTextField jLaserPercentSet1;
    private javax.swing.JTextField jLaserPercentSet10;
    private javax.swing.JTextField jLaserPercentSet11;
    private javax.swing.JTextField jLaserPercentSet12;
    private javax.swing.JTextField jLaserPercentSet13;
    private javax.swing.JTextField jLaserPercentSet2;
    private javax.swing.JTextField jLaserPercentSet3;
    private javax.swing.JTextField jLasermWSet1;
    private javax.swing.JTextField jLasermWSet10;
    private javax.swing.JTextField jLasermWSet11;
    private javax.swing.JTextField jLasermWSet12;
    private javax.swing.JTextField jLasermWSet13;
    private javax.swing.JTextField jLasermWSet2;
    private javax.swing.JTextField jLasermWSet3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jPower1;
    private javax.swing.JLabel jPower12;
    private javax.swing.JLabel jPower2;
    private javax.swing.JLabel jPower3;
    // End of variables declaration//GEN-END:variables
}


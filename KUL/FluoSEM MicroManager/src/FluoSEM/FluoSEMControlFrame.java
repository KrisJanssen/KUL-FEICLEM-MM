/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package FluoSEM;

import bsh.EvalError;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.prefs.Preferences;
import javax.swing.Timer;
import mmcorej.CMMCore;
import mmcorej.DeviceType;
import mmcorej.StrVector;
import org.micromanager.api.ScriptInterface;
import bsh.Interpreter; 
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Frank
 */
public class FluoSEMControlFrame extends javax.swing.JFrame {

    /**
     * Creates new form FluoSEMControl
     */
    
    private final ScriptInterface gui_;
   private final CMMCore core_;
   Interpreter bsh = new Interpreter();  // Construct an interpreter 
   
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
   
   private Preferences prefs_;

   private double smallMovement_ = 1.0;
   private double mediumMovement_ = 10.0;
   private double largeMovement_ = 100.0;
   private double smallMovementZ_ = 1.0;
   private double mediumMovementZ_ = 10.0;
   private double smallMovementLR_ = 1.0;
   private double mediumMovementLR_ = 10.0;

   
   private NumberFormat nf_;

   private int frameXPos_ = 100;
   private int frameYPos_ = 100;

   private static final String FRAMEXPOS = "FRAMEXPOS";
   private static final String FRAMEYPOS = "FRAMEYPOS";
   private static final String SMALLMOVEMENT = "SMALLMOVEMENT";
   private static final String MEDIUMMOVEMENT = "MEDIUMMOVEMENT";
   private static final String LARGEMOVEMENT = "LARGEMOVEMENT";
   private static final String SMALLMOVEMENTZ = "SMALLMOVEMENTZ";
   private static final String MEDIUMMOVEMENTZ = "MEDIUMMOVEMENTZ";

   private static String XYStageLabel = "FluoSEM-Stage-XY";
   private static String ZStageLabel = "FluoSEM-Stage-Z";
   private static String LRStageLabel = "FluoSEM-Stage-LR";
    
    
    public FluoSEMControlFrame(ScriptInterface gui) {

        gui_ = gui;
        core_ = gui_.getMMCore();
        nf_ = NumberFormat.getInstance();
       prefs_ = Preferences.userNodeForPackage(this.getClass());

       // Read values from PREFS
       frameXPos_ = prefs_.getInt(FRAMEXPOS, frameXPos_);
       frameYPos_ = prefs_.getInt(FRAMEYPOS, frameYPos_);
        initComponents();
        setLocation(0, 0);
        
        jTabbedPane1.setEnabledAt(3, false);
        jTabbedPane1.setEnabledAt(4,false);

       setBackground(gui_.getBackgroundColor());
       gui_.addMMBackgroundListener(this);

       jTextField1.setText(nf_.format(smallMovement_));
       jTextField2.setText(nf_.format(mediumMovement_));
       jTextField3.setText(nf_.format(largeMovement_));
       jTextField4.setText(nf_.format(smallMovementZ_));
       jTextField5.setText(nf_.format(mediumMovementZ_));
       jTextField6.setText(nf_.format(smallMovementLR_));
       jTextField7.setText(nf_.format(mediumMovementLR_));
       
       
       
       
       try {
            core_.setXYStageDevice(XYStageLabel);
       } catch(Exception e) {
          gui_.logError(e);
       }
       try {
            core_.setProperty(ZStageLabel, "Step Voltage", 30);
       } catch(Exception e) {
          gui_.logError(e);
       }
              try {
            core_.setProperty(LRStageLabel, "Step Voltage", 30);
       } catch(Exception e) {
          gui_.logError(e);
       }

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
                core_.setProperty(nm532ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm532PLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm561ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
        try {
                core_.setProperty(nm561PLabel, "Volts", 5.0);
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
                    jLabel64.setText(jPower1.getText());
                } catch (Exception e) {
                    gui_.logError(e);
                }
                try {
                    jPower2.setText(core_.getProperty(nm445Label, "Laser Power Status [mW]"));
                    jLabel65.setText(jPower2.getText());
                } catch (Exception e) {
                    gui_.logError(e);
                }
                try {
                    jPower3.setText(core_.getProperty(nm488Label, "Laser Power Status [mW]"));
                    jLabel66.setText(jPower3.getText());
                } catch (Exception e) {
                    gui_.logError(e);
                }
                try {
                    jPower12.setText(core_.getProperty(nm642Label, "Laser Power Status [mW]"));
                    jLabel72.setText(jPower12.getText());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jSlider3 = new javax.swing.JSlider();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jButton19 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jSlider2 = new javax.swing.JSlider();
        jLabel17 = new javax.swing.JLabel();
        jSlider4 = new javax.swing.JSlider();
        jLabel19 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jButton30 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPower3 = new javax.swing.JLabel();
        jLasermWSet3 = new javax.swing.JTextField();
        jLaserPercentSet3 = new javax.swing.JTextField();
        jLaserOnOff3 = new javax.swing.JToggleButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
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
        jLabel21 = new javax.swing.JLabel();
        jLaserPercentSet13 = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jPower2 = new javax.swing.JLabel();
        jLasermWSet2 = new javax.swing.JTextField();
        jLaserPercentSet2 = new javax.swing.JTextField();
        jLaserOnOff2 = new javax.swing.JToggleButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLasermWSet11 = new javax.swing.JTextField();
        jLaserOnOff11 = new javax.swing.JToggleButton();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLaserPercentSet11 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPower1 = new javax.swing.JLabel();
        jLasermWSet1 = new javax.swing.JTextField();
        jLaserPercentSet1 = new javax.swing.JTextField();
        jLaserOnOff1 = new javax.swing.JToggleButton();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLasermWSet10 = new javax.swing.JTextField();
        jLaserOnOff10 = new javax.swing.JToggleButton();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLaserPercentSet10 = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jLabel80 = new javax.swing.JLabel();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        LaserToggle = new javax.swing.JToggleButton();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        CameraLoadToggle = new javax.swing.JToggleButton();
        jLabel70 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        EMGain = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        LiveView = new javax.swing.JToggleButton();
        Exposure = new javax.swing.JTextField();
        Binning = new javax.swing.JComboBox();
        ToggleShutter = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel18 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        SEMBlanking = new javax.swing.JToggleButton();
        jPanel19 = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        FilterLoadToggle = new javax.swing.JToggleButton();
        jLabel82 = new javax.swing.JLabel();
        LCWavelength = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FluoSEM Control");
        setMaximumSize(new java.awt.Dimension(1030, 850));
        setMinimumSize(new java.awt.Dimension(1030, 850));
        setPreferredSize(new java.awt.Dimension(1030, 850));
        setResizable(false);

        jTabbedPane1.setName("Stage Control"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(850, 797));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-sl.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-sd.png"))); // NOI18N
        jButton10.setBorderPainted(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-tl.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-dl.png"))); // NOI18N
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-su.png"))); // NOI18N
        jButton7.setBorderPainted(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-dd.png"))); // NOI18N
        jButton11.setBorderPainted(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-tu.png"))); // NOI18N
        jButton8.setBorderPainted(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-sr.png"))); // NOI18N
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-td.png"))); // NOI18N
        jButton12.setBorderPainted(false);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-tr.png"))); // NOI18N
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-du.png"))); // NOI18N
        jButton9.setBorderPainted(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-dr.png"))); // NOI18N
        jButton6.setBorderPainted(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("XY Stage Control");

        jLabel6.setText("Large Movement Delay");

        jTextField3.setText("jTextField1");
        jTextField3.setMinimumSize(new java.awt.Dimension(69, 22));

        jLabel5.setText("Medium Movement Delay");

        jTextField2.setText("jTextField1");
        jTextField2.setMinimumSize(new java.awt.Dimension(69, 22));

        jLabel4.setText("Small Movement Delay");

        jTextField1.setText("jTextField1");
        jTextField1.setMinimumSize(new java.awt.Dimension(69, 22));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Stage Step Movement Size");

        jSlider3.setMaximum(11000);
        jSlider3.setMinimum(5000);
        jSlider3.setValue(9500);
        jSlider3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSlider3MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider3MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSlider3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(61, 61, 61)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Step");

        jPanel6.setPreferredSize(new java.awt.Dimension(80, 222));

        jButton19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-dd.png"))); // NOI18N
        jButton19.setBorderPainted(false);
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-du.png"))); // NOI18N
        jButton16.setBorderPainted(false);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-sd.png"))); // NOI18N
        jButton18.setBorderPainted(false);
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-su.png"))); // NOI18N
        jButton17.setBorderPainted(false);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Focus Control");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Fine");

        jSlider1.setMaximum(55);
        jSlider1.setMinimum(-55);
        jSlider1.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider1.setValue(0);
        jSlider1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jSlider1MouseDragged(evt);
            }
        });

        jLabel7.setText("Small Movement Steps");

        jLabel8.setText("Large Movement Steps");

        jTextField5.setText("jTextField1");
        jTextField5.setMinimumSize(new java.awt.Dimension(69, 22));
        jTextField5.setName(""); // NOI18N

        jTextField4.setText("jTextField1");
        jTextField4.setMinimumSize(new java.awt.Dimension(69, 22));

        jSlider2.setMaximum(55);
        jSlider2.setValue(30);
        jSlider2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSlider2MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider2MouseReleased(evt);
            }
        });

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Objective Step Down Movement Size");

        jSlider4.setMaximum(55);
        jSlider4.setValue(30);
        jSlider4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSlider4MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider4MouseReleased(evt);
            }
        });

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Objective Step Up Movement Size");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 3, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(56, 56, 56)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel17))
                .addGap(45, 45, 45)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        jButton30.setText("Reset ALL stages");
        jButton30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton30MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 80, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(243, 243, 243)
                .addComponent(jButton30)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addComponent(jButton30)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Stage Control", jPanel2);

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

        jLaserOnOff3.setText("STATE: OFF");
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

        jLabel20.setText("Actual");

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

        jLaserOnOff12.setText("STATE: OFF");
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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLasermWSet3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(jPower3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPower3)
                    .addComponent(jLabel20))
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

        jLaserOnOff13.setText("STATE: OFF");
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

        jLabel21.setText("%");

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
                                .addComponent(jLabel21)))
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
                    .addComponent(jLabel21))
                .addGap(51, 51, 51)
                .addComponent(jLaserOnOff13)
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

        jLaserOnOff2.setText("STATE: OFF");
        jLaserOnOff2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff2ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("445 nm");

        jLabel10.setText("Set Power");

        jLabel22.setText("mW");

        jLabel23.setText("%");

        jLabel24.setText("Actual");

        jLasermWSet11.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet11.setText("0.0");
        jLasermWSet11.setAutoscrolls(false);
        jLasermWSet11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet11KeyPressed(evt);
            }
        });

        jLaserOnOff11.setText("STATE: OFF");
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

        jLabel25.setText("%");

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
                                .addComponent(jLabel25)))
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
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLaserOnOff11)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLasermWSet2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel22))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(jPower2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPower2)
                    .addComponent(jLabel24))
                .addGap(18, 18, 18)
                .addComponent(jLaserOnOff2)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        jLaserOnOff1.setText("STATE: OFF");
        jLaserOnOff1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLaserOnOff1ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("405 nm");

        jLabel27.setText("Set Power");

        jLabel28.setText("mW");

        jLabel29.setText("%");

        jLabel30.setText("Actual");

        jLasermWSet10.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLasermWSet10.setText("0.0");
        jLasermWSet10.setAutoscrolls(false);
        jLasermWSet10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLasermWSet10KeyPressed(evt);
            }
        });

        jLaserOnOff10.setText("STATE: OFF");
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

        jLabel31.setText("%");

        jLaserPercentSet10.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jLaserPercentSet10.setText("0");
        jLaserPercentSet10.setAutoscrolls(false);
        jLaserPercentSet10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLaserPercentSet10KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jLasermWSet10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel48))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jLaserOnOff10)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLaserOnOff1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLasermWSet1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel28))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLaserPercentSet1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel29))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(18, 18, 18)
                                .addComponent(jPower1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLasermWSet1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLaserPercentSet1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPower1)
                    .addComponent(jLabel30))
                .addGap(18, 18, 18)
                .addComponent(jLaserOnOff1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(336, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Laser Control", jPanel7);

        jLabel32.setText("Small Movement Steps");

        jTextField6.setText("jTextField1");
        jTextField6.setMinimumSize(new java.awt.Dimension(69, 22));

        jTextField7.setText("jTextField1");
        jTextField7.setMinimumSize(new java.awt.Dimension(69, 22));

        jLabel33.setText("Large Movement Steps");

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-sl.png"))); // NOI18N
        jButton13.setBorderPainted(false);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-sd.png"))); // NOI18N
        jButton14.setBorderPainted(false);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-dl.png"))); // NOI18N
        jButton20.setBorderPainted(false);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-su.png"))); // NOI18N
        jButton21.setBorderPainted(false);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-dd.png"))); // NOI18N
        jButton22.setBorderPainted(false);
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-sr.png"))); // NOI18N
        jButton24.setBorderPainted(false);
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-du.png"))); // NOI18N
        jButton27.setBorderPainted(false);
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FluoSEM/icons/arrowhead-dr.png"))); // NOI18N
        jButton28.setBorderPainted(false);
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(68, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton22)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jButton21)
                        .addContainerGap(62, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("Manual Objective Control");

        jButton15.setText("Calibrate LR movement steps");
        jButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton15MouseClicked(evt);
            }
        });

        jButton23.setText("Perform 2 point alignment");
        jButton23.setEnabled(false);

        jButton25.setText("Perform rotation / FOV adjustment");
        jButton25.setEnabled(false);

        jButton26.setText("Select points for 2 point alignment");
        jButton26.setEnabled(false);

        jButton29.setText("Physically centre objective");
        jButton29.setEnabled(false);

        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel80.setText("Automatic Alignments / Calibration");

        jButton31.setText("EO centre objective");
        jButton31.setEnabled(false);

        jButton32.setText("Reset ALL stages");
        jButton32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton32MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel32))
                                .addGap(60, 60, 60)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)))
                        .addGap(71, 71, 71)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel80, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(jButton32)))
                .addContainerGap(84, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jLabel80))
                .addGap(18, 18, 18)
                .addComponent(jButton15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addComponent(jButton29)
                        .addGap(18, 18, 18)
                        .addComponent(jButton31)
                        .addGap(18, 18, 18)
                        .addComponent(jButton25)
                        .addGap(44, 44, 44)
                        .addComponent(jButton26)
                        .addGap(18, 18, 18)
                        .addComponent(jButton23)))
                .addGap(18, 18, 18)
                .addComponent(jButton32)
                .addGap(368, 368, 368))
        );

        jTabbedPane1.addTab("Alignments", jPanel15);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 661, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 767, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("LC Filter", jPanel20);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 661, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 767, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Scripting", jPanel21);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("STATUS");

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel35.setText("Lasers");

        jLabel36.setText("365 nm");

        jLabel37.setText("405 nm");

        jLabel38.setText("445 nm");

        jLabel39.setText("488 nm");

        jLabel40.setText("532 nm");

        jLabel41.setText("561 nm");

        jLabel42.setText("?");

        jLabel43.setText("?");

        jLabel44.setText("?");

        jLabel45.setText("?");

        jLabel55.setText("?");

        jLabel61.setText("?");

        jLabel62.setText("?");

        jLabel63.setText("?");

        jLabel64.setText("?");

        jLabel65.setText("?");

        jLabel66.setText("?");

        jLabel67.setText("?");

        jLabel68.setText("Toggle enabled lasers ON/OFF");

        LaserToggle.setText("STATE: OFF");
        LaserToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LaserToggleActionPerformed(evt);
            }
        });

        jLabel71.setText("642 nm");

        jLabel72.setText("?");

        jLabel73.setText("?");

        jLabel83.setText("?");

        jLabel84.setText("?");

        jLabel85.setText("?");

        jLabel86.setText("?");

        jLabel87.setText("?");

        jLabel88.setText("?");

        jLabel89.setText("?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel68)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel37)
                                    .addComponent(jLabel38)
                                    .addComponent(jLabel39)
                                    .addComponent(jLabel40)
                                    .addComponent(jLabel41)
                                    .addComponent(jLabel71))
                                .addGap(77, 77, 77)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel63)
                                    .addComponent(jLabel64)
                                    .addComponent(jLabel65)
                                    .addComponent(jLabel66)
                                    .addComponent(jLabel67)
                                    .addComponent(jLabel62)
                                    .addComponent(jLabel72))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel83)
                                    .addComponent(jLabel84)
                                    .addComponent(jLabel85)
                                    .addComponent(jLabel86)
                                    .addComponent(jLabel87)
                                    .addComponent(jLabel88)
                                    .addComponent(jLabel89))
                                .addGap(91, 91, 91)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel43)
                                    .addComponent(jLabel44)
                                    .addComponent(jLabel45)
                                    .addComponent(jLabel55)
                                    .addComponent(jLabel61)
                                    .addComponent(jLabel42)
                                    .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(LaserToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel35)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel36)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel37)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel38)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel39)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel40)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel41)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel71)
                                .addComponent(jLabel73)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel43)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel44)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel45)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel55)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel61)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel42)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel67)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel72))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel83)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel84)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel85)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel86)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel87)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel88)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel89)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(LaserToggle))
                .addContainerGap())
        );

        jLabel69.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel69.setText("Camera");

        CameraLoadToggle.setText("STATE:UNLOADED");
        CameraLoadToggle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CameraLoadToggleStateChanged(evt);
            }
        });

        jLabel70.setText("EM Gain");

        jLabel74.setText("Toggle shutter OPEN/CLOSED");

        EMGain.setText("?");
        EMGain.setEnabled(false);
        EMGain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EMGainKeyPressed(evt);
            }
        });

        jLabel75.setText("Exposure");

        jLabel76.setText("Binning");

        jLabel77.setText("Toggle live view");

        LiveView.setText("STATE:OFF");
        LiveView.setEnabled(false);
        LiveView.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LiveViewStateChanged(evt);
            }
        });

        Exposure.setText("?");
        Exposure.setEnabled(false);
        Exposure.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ExposureKeyPressed(evt);
            }
        });

        Binning.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1x1", "2x2", "4x4" }));
        Binning.setEnabled(false);
        Binning.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                BinningItemStateChanged(evt);
            }
        });

        ToggleShutter.setText("STATE:CLOSED");
        ToggleShutter.setEnabled(false);
        ToggleShutter.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ToggleShutterStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel70)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(EMGain, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel75)
                            .addComponent(jLabel69)
                            .addComponent(jLabel76))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(Exposure, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                                .addComponent(Binning, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(CameraLoadToggle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel74)
                            .addComponent(jLabel77))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ToggleShutter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LiveView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addComponent(jSeparator1)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(CameraLoadToggle))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(EMGain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75)
                    .addComponent(Exposure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Binning, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel76))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74)
                    .addComponent(ToggleShutter))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(LiveView))
                .addContainerGap())
        );

        jLabel78.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel78.setText("SEM");

        jLabel79.setText("Toggle beam blanking");

        SEMBlanking.setText("STATE:OFF");
        SEMBlanking.setEnabled(false);
        SEMBlanking.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SEMBlankingStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel78)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel79)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SEMBlanking, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel78)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79)
                    .addComponent(SEMBlanking))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jLabel81.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel81.setText("LC Filter");

        FilterLoadToggle.setText("STATE:UNLOADED");
        FilterLoadToggle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                FilterLoadToggleStateChanged(evt);
            }
        });

        jLabel82.setText("Wavelength");

        LCWavelength.setText("?");
        LCWavelength.setEnabled(false);
        LCWavelength.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LCWavelengthKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel81)
                    .addComponent(jLabel82))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(FilterLoadToggle, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(LCWavelength))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FilterLoadToggle)
                    .addComponent(jLabel81))
                .addGap(18, 18, 18)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(LCWavelength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

       private void setRelativeXYStagePosition(double x, double y) {
      try {
            core_.setRelativeXYPosition(XYStageLabel, x, y);
       } catch(Exception e) {
          gui_.logError(e);
       }
   }
   
       private void setRelativeStagePosition(double z) {
      try {
            core_.setRelativePosition(ZStageLabel, z);
       } catch(Exception e) {
          gui_.logError(e);
       }
      jSlider1.setValue(0);
   }
    
   private void setRelativeLRStagePosition(double x, double y) {
              try {
            core_.setXYStageDevice(LRStageLabel);
       } catch(Exception e) {
          gui_.logError(e);
       }
       try {
            core_.setRelativeXYPosition(LRStageLabel, x, y);
       } catch(Exception e) {
          gui_.logError(e);
       }
              try {
            core_.setXYStageDevice(XYStageLabel);
       } catch(Exception e) {
          gui_.logError(e);
       }
   }
    
    private void jLaserPercentSet10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet10KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm532PLabel, "Volts", (5.0-Float.parseFloat(jLaserPercentSet10.getText())/20));
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

    private void jLaserOnOff10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff10ActionPerformed
        if (jLaserOnOff10.isSelected() && LaserToggle.isSelected()) {
            try {
                core_.setProperty(nm532Label, "Laser", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            jLaserOnOff10.setText("STATE: ON");
            /* try {
                core_.setProperty(nm532ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }*/
        } else {
            try {
                core_.setProperty(nm532Label, "Laser", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            jLaserOnOff10.setText("STATE: OFF");
            /*try {
                core_.setProperty(nm532ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }*/
        }
    }//GEN-LAST:event_jLaserOnOff10ActionPerformed

    private void jLasermWSet10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet10KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm532PLabel, "Volts", (5.0-(Float.parseFloat(jLasermWSet10.getText())/60)));
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

    private void jLaserOnOff1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff1ActionPerformed
        if (jLaserOnOff1.isSelected() && LaserToggle.isSelected()) {
            try {
                core_.setProperty(nm405Label, "Laser Operation Select", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            try {
                core_.setProperty(nm405ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
                jLaserOnOff1.setText("STATE: ON");
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
            jLaserOnOff1.setText("STATE: OFF");
        }
    }//GEN-LAST:event_jLaserOnOff1ActionPerformed

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

    private void jLaserPercentSet11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLaserPercentSet11KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm561PLabel, "Volts", (5.0-Float.parseFloat(jLaserPercentSet11.getText())/20));
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

    private void jLaserOnOff11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff11ActionPerformed
        if (jLaserOnOff11.isSelected() && LaserToggle.isSelected()) {
            try {
                core_.setProperty(nm561Label, "Laser", "On");
            } catch (Exception e) {
                gui_.logError(e);
            }
            jLaserOnOff11.setText("STATE: ON");
            /* try {
                core_.setProperty(nm561ENLabel, "Volts", 5.0);
            } catch (Exception e) {
                gui_.logError(e);
            }*/
        } else {
            try {
                core_.setProperty(nm561Label, "Laser", "Off");
            } catch (Exception e) {
                gui_.logError(e);
            }
            jLaserOnOff11.setText("STATE: OFF");
            /*try {
                core_.setProperty(nm561ENLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }*/
        }
    }//GEN-LAST:event_jLaserOnOff11ActionPerformed

    private void jLasermWSet11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLasermWSet11KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                core_.setProperty(nm561PLabel, "Volts", 5.0-(Float.parseFloat(jLasermWSet11.getText())/30));
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

    private void jLaserOnOff2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff2ActionPerformed
        if (jLaserOnOff2.isSelected() && LaserToggle.isSelected()) {
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
            jLaserOnOff2.setText("STATE: ON");
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
            jLaserOnOff2.setText("STATE: OFF");
        }
    }//GEN-LAST:event_jLaserOnOff2ActionPerformed

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
        if (jLaserOnOff13.isSelected() && LaserToggle.isSelected()) {
            try {

                core_.setProperty(nm365PLabel, "Volts", Float.parseFloat(jLaserPercentSet13.getText())/20);
            } catch (Exception e) {
                gui_.logError(e);
            }
            jLaserOnOff13.setText("STATE: ON");
        } else {
            try {
                core_.setProperty(nm365PLabel, "Volts", 0.0);
            } catch (Exception e) {
                gui_.logError(e);
            }
            jLaserOnOff13.setText("STATE: OFF");
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

    private void jLaserOnOff12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff12ActionPerformed
        if (jLaserOnOff12.isSelected() && LaserToggle.isSelected()) {
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
            jLaserOnOff12.setText("STATE: ON");

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
            jLaserOnOff12.setText("STATE: OFF");
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

    private void jLaserOnOff3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLaserOnOff3ActionPerformed
        if (jLaserOnOff3.isSelected() && LaserToggle.isSelected()) {
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
            jLaserOnOff3.setText("STATE: ON");
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
            jLaserOnOff3.setText("STATE: ON");
        }
    }//GEN-LAST:event_jLaserOnOff3ActionPerformed

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

    private void jSlider4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider4MouseReleased
        try {
            core_.setProperty(ZStageLabel, "Step Voltage P", jSlider4.getValue());
        } catch(Exception e) {
            gui_.logError(e);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jSlider4MouseReleased

    private void jSlider4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jSlider4MouseClicked

    private void jSlider2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider2MouseReleased
        // TODO add your handling code here:
        try {
            core_.setProperty(ZStageLabel, "Step Voltage N", jSlider2.getValue());
        } catch(Exception e) {
            gui_.logError(e);
        }
    }//GEN-LAST:event_jSlider2MouseReleased

    private void jSlider2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jSlider2MouseClicked

    private void jSlider1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseDragged
        // TODO add your handling code here:
        try {
            core_.setProperty(ZStageLabel, "Fine Voltage", jSlider1.getValue());
        } catch(Exception e) {
            gui_.logError(e);
        }
    }//GEN-LAST:event_jSlider1MouseDragged

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        setRelativeStagePosition(smallMovementZ_);
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        setRelativeStagePosition(-smallMovementZ_);
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        setRelativeStagePosition(mediumMovementZ_);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        setRelativeStagePosition(-mediumMovementZ_);
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jSlider3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider3MouseReleased
        // TODO add your handling code here:
        try {
            core_.setProperty(XYStageLabel, "Stage Voltage XP", jSlider3.getValue());
            core_.setProperty(XYStageLabel, "Stage Voltage XN", jSlider3.getValue());
            core_.setProperty(XYStageLabel, "Stage Voltage YP", jSlider3.getValue());
            core_.setProperty(XYStageLabel, "Stage Voltage YN", jSlider3.getValue());
        } catch(Exception e) {
            gui_.logError(e);
        }
    }//GEN-LAST:event_jSlider3MouseReleased

    private void jSlider3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jSlider3MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        setRelativeXYStagePosition(mediumMovement_, 0.0);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        setRelativeXYStagePosition(0.0, -mediumMovement_);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        setRelativeXYStagePosition(largeMovement_, 0.0);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        setRelativeXYStagePosition(0.0, largeMovement_);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        setRelativeXYStagePosition(smallMovement_, 0.0);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        setRelativeXYStagePosition(0.0, -largeMovement_);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        setRelativeXYStagePosition(0.0, mediumMovement_);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        setRelativeXYStagePosition(0.0, -smallMovement_);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        setRelativeXYStagePosition(-mediumMovement_, 0.0);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setRelativeXYStagePosition(-largeMovement_, 0.0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        setRelativeXYStagePosition(0.0, smallMovement_);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setRelativeXYStagePosition(-smallMovement_, 0.0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        setRelativeLRStagePosition(mediumMovementLR_, 0.0);
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        setRelativeLRStagePosition(0.0, mediumMovementLR_);
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        setRelativeLRStagePosition(smallMovementLR_, 0.0);
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        setRelativeLRStagePosition(0.0, -mediumMovementLR_);
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        setRelativeLRStagePosition(0.0,smallMovementLR_);
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        setRelativeLRStagePosition(-mediumMovementLR_, 0.0);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        setRelativeLRStagePosition(0.0, -smallMovement_);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        setRelativeLRStagePosition(-smallMovement_, 0.0);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void UpdateCameraProperties()
    {
    try
    {
        Exposure.setText(core_.getProperty("Camera", "Exposure"));
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
    try
    {
       EMGain.setText(core_.getProperty("Camera", "EMGain"));
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
    try
    {
        String binning = core_.getProperty("Camera", "Binning");
        for(int i = 0; i<Binning.getItemCount(); i++)
        {
            if(Binning.getItemAt(i).equals(binning))
            {
                Binning.setSelectedIndex(i);
            }
      
        }
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
    try
    {
       String shutter = core_.getProperty("Camera", "MECHANICAL SHUTTER");
       ToggleShutter.setSelected(shutter.equals("OPEN"));
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
    
    }
    
    private void SetCameraProperties()
    {
        try
    {
        core_.setProperty("Camera", "EMGain", Integer.valueOf(EMGain.getText()));
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
        try
    {
        core_.setProperty("Camera", "Exposure", Float.valueOf(Exposure.getText()));
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
        try
    {
        core_.setProperty("Camera", "Binning", Binning.getSelectedItem().toString());
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
        
        if(ToggleShutter.isSelected())
        {
        try
        {
            core_.setProperty("Camera", "MECHANICAL SHUTTER", "OPEN");
        }
        catch(Exception e) 
        {
            gui_.logError(e);
        }
        }
        else
        {
            try
            {
            core_.setProperty("Camera", "MECHANICAL SHUTTER", "CLOSE");
            }
            catch(Exception e) 
            {
                gui_.logError(e);
            }
        }
        
    }
    
    private void CameraLoadToggleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CameraLoadToggleStateChanged
    boolean loaded;
    if(CameraLoadToggle.isSelected())
    {   
        //String devicename = "";
        StrVector loadedDevicesOfType = core_.getLoadedDevicesOfType(DeviceType.CameraDevice);
        for(int i = 0; i < loadedDevicesOfType.size(); i++)
        {
            if(loadedDevicesOfType.get(i).equals("Camera"))
            {
                try
                {
                    core_.unloadDevice("Camera");
                }
                catch(Exception e) 
                {
                    gui_.logError(e);
                }
            }
        }
        try
        {
            core_.loadDevice("Camera","HamamatsuHam","HamamatsuHam_DCAM");
            core_.initializeDevice("Camera");
            core_.setProperty("Core", "Camera", "Camera");
            loaded = true;
        }
        catch(Exception e) 
        {
            gui_.logError(e);
            loaded = false;
        }
        if(loaded)
        {
            UpdateCameraProperties();
            EMGain.setEnabled(true);
            Exposure.setEnabled(true);
            Binning.setEnabled(true);
            ToggleShutter.setEnabled(true);
            LiveView.setEnabled(true);
            
            CameraLoadToggle.setText("STATE:LOADED");
        }
    }
    else
    {
        try
        {
            core_.unloadDevice("Camera");
            loaded =false;
        }
        catch(Exception e) 
        {
            gui_.logError(e);
            loaded =true;
        }
        if(!loaded)
        {
            EMGain.setEnabled(false);
            Exposure.setEnabled(false);
            Binning.setEnabled(false);
            ToggleShutter.setEnabled(false);
            LiveView.setEnabled(false);
        }
        CameraLoadToggle.setText("STATE:UNLOADED");
    }// TODO add your handling code here:
    gui_.refreshGUI();
    }//GEN-LAST:event_CameraLoadToggleStateChanged

    private void FilterLoadToggleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_FilterLoadToggleStateChanged
    boolean loaded;
    if(FilterLoadToggle.isSelected())
    {
        
        try
        {
            core_.loadDevice("VariSpec","VariSpec","VariSpec");
            core_.loadDevice("COM12","SerialManager","COM12");
            core_.setProperty("VariSpec", "Port", "COM12");
            core_.setProperty("COM12", "AnswerTimeout", 500.000);
            core_.setProperty("COM12", "BaudRate", 115200);
            core_.setProperty("COM12", "DelayBetweenCharsMs", 0.0000);
            core_.setProperty("COM12", "Handshaking", "Off");
            core_.setProperty("COM12", "Parity", "None");
            core_.setProperty("COM12", "StopBits", 1);
            core_.setProperty("COM12", "Verbose", 1);
            core_.initializeDevice("COM12");
            core_.initializeDevice("VariSpec");
            loaded = true;
        }
        catch(Exception e) 
        {
                gui_.logError(e);
                loaded = false;
        }
        if(loaded)
        {
            UpdateLCWavelength();
            LCWavelength.setEnabled(true);
            FilterLoadToggle.setText("STATE:LOADED");
        }
            
    }
    else
    {
        try
        {
            core_.unloadDevice("VariSpec");
            core_.unloadDevice("COM12");
            loaded = false;
        }
        catch(Exception e) 
        {
            gui_.logError(e);
            loaded = true;
        }
        if(!loaded)
        {
            LCWavelength.setEnabled(false);
            FilterLoadToggle.setText("STATE:UNLOADED");        // TODO add your handling code here:
        }
        
    }
    }//GEN-LAST:event_FilterLoadToggleStateChanged
    private void UpdateLCWavelength()
    {
        try
    {
        LCWavelength.setText(core_.getProperty("VariSpec", "Wavelength"));
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
    }
    private void SetLCWavelength()
{
    try
    {
        core_.setProperty("VariSpec", "Wavelength", Float.valueOf(LCWavelength.getText()));
    }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
 
}
    private void LCWavelengthKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LCWavelengthKeyPressed
     if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        SetLCWavelength();
        UpdateLCWavelength();
     }
    }//GEN-LAST:event_LCWavelengthKeyPressed

    private void EMGainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EMGainKeyPressed
       if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        SetCameraProperties();
        UpdateCameraProperties();
       }
    }//GEN-LAST:event_EMGainKeyPressed

    private void jButton15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton15MouseClicked
        
        try { 
            bsh.source("C:\\Program Files\\Micro-Manager-1.4\\KULScripts\\CalibrateLR.bsh");
        } catch (IOException ex) {
            gui_.logError(ex);
        } catch (EvalError ex) {
            gui_.logError(ex);
        }
    }//GEN-LAST:event_jButton15MouseClicked
private void SetSEMBlanking(boolean state)
{

        try {
            core_.setProperty("FEISEM Controller", "Beam Blank", Boolean.toString(state));
        } catch (Exception ex) {
            gui_.logError(ex);
        }
 
}
    private void SEMBlankingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SEMBlankingStateChanged
        if(SEMBlanking.isSelected())
        {
          
        }
    }//GEN-LAST:event_SEMBlankingStateChanged

    private void ToggleShutterStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ToggleShutterStateChanged
        if(ToggleShutter.isSelected())
        {
            ToggleShutter.setText("STATE:OPEN");
            
        }
        else
        {
            ToggleShutter.setText("STATE:CLOSED");
        }
        SetCameraProperties();
        UpdateCameraProperties();
    }//GEN-LAST:event_ToggleShutterStateChanged

    private void jButton30MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton30MouseClicked
        try
        {
            core_.setProperty(XYStageLabel, "Reset?", 1);
            core_.setProperty(LRStageLabel, "Reset?", 1);
            core_.setProperty(ZStageLabel, "Reset?", 1);
        }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
    }//GEN-LAST:event_jButton30MouseClicked

    private void jButton32MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton32MouseClicked
                try
        {
            core_.setProperty(XYStageLabel, "Reset?", 1);
            core_.setProperty(LRStageLabel, "Reset?", 1);
            core_.setProperty(ZStageLabel, "Reset?", 1);
        }
    catch(Exception e) 
    {
        gui_.logError(e);
    }
    }//GEN-LAST:event_jButton32MouseClicked

    private void ExposureKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ExposureKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        SetCameraProperties();
        UpdateCameraProperties();
       }
    }//GEN-LAST:event_ExposureKeyPressed

    private void BinningItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_BinningItemStateChanged
        SetCameraProperties();
        UpdateCameraProperties();
    }//GEN-LAST:event_BinningItemStateChanged

    private void LaserToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LaserToggleActionPerformed
        jLaserOnOff1ActionPerformed(evt);
        jLaserOnOff2ActionPerformed(evt);
        jLaserOnOff3ActionPerformed(evt);
        jLaserOnOff10ActionPerformed(evt);
        jLaserOnOff11ActionPerformed(evt);
        jLaserOnOff12ActionPerformed(evt);
        jLaserOnOff13ActionPerformed(evt);
    }//GEN-LAST:event_LaserToggleActionPerformed

    private void LiveViewStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_LiveViewStateChanged
            if(LiveView.isSelected())
            {
                LiveView.setText("STATE:ENABLED");
                if(gui_.isLiveModeOn())
                {
                    
                }
                else
                {
                    gui_.enableLiveMode(true);
                }
            }
            else
            {
                LiveView.setText("STATE:DISABLED");
                if(gui_.isLiveModeOn())
                {
                    gui_.enableLiveMode(false);
                }
            }
            
    }//GEN-LAST:event_LiveViewStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox Binning;
    private javax.swing.JToggleButton CameraLoadToggle;
    private javax.swing.JTextField EMGain;
    private javax.swing.JTextField Exposure;
    private javax.swing.JToggleButton FilterLoadToggle;
    private javax.swing.JTextField LCWavelength;
    private javax.swing.JToggleButton LaserToggle;
    private javax.swing.JToggleButton LiveView;
    private javax.swing.JToggleButton SEMBlanking;
    private javax.swing.JToggleButton ToggleShutter;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
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
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
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
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel jPower1;
    private javax.swing.JLabel jPower12;
    private javax.swing.JLabel jPower2;
    private javax.swing.JLabel jPower3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSlider jSlider4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}

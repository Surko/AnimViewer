/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package animviewer;

import plugins.AnimPlugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import plugins.AnimPluginManager;
import updater.Updater;

/**
 *
 * @author kirrie
 */
public class AnimViewer {
    
    private static final Logger LOG = Logger.getLogger(AnimViewer.class.getClass().getName());
    private static final String defName = "AnimViewer";    
    private static final String defW = "800", defH = "600";
    
    private static final String noPropErr = "Could not find property file with config --> default values used";
    private static final String noLookErr = "We cannot set %s look for the app";    
    private static final String infoRemove = "List of loaded plugins were resetted\n";
    private static final String uptoDate = "Your app is up-to date";
    private static final String newVersion = "New version is available. Do you want to download?";
    private static final String newVersionTitle = "New version available";
    private static final String noConnection = "Connection was interrupted";
    
    private static final String versionFormat = "%s - version %d by Surko";
    
    private static final String app_version = "app_version";
    
    public static Properties prop;
    public static AnimPluginManager plugManager;
    public static AnimPlugin activePlugin;    
    public static int version;
    
    public static boolean stopped = true;
    public static boolean paused = true;
    
    public static int fpsMin = 0,fpsMax = 30,fpsInit = 0;
    public static int w,h;
    public static JFrame animViewerFrame;
    public static JPanel panel1,panel2;
    public static JMenuBar animMenuBar;
    public static JScrollPane animScrollList;
    public static JList animList;
    public static JToolBar animToolbar;
    public static JPanel animPanel;
    public static JSlider animSlider;
    public static JScrollPane animScrollText;
    public static JTextArea animText;
    
    static class WindowListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {                        
            updateLengths();
        }

        @Override
        public void componentMoved(ComponentEvent e) {           
        }
        @Override
        public void componentShown(ComponentEvent e) {            
            //updateLengths();
        }
        @Override
        public void componentHidden(ComponentEvent e) {            
        }
        
    }    
    
    public static void updateLengths() {
        if (animToolbar != null) {
            animToolbar.setPreferredSize(new Dimension(animViewerFrame.getContentPane().getWidth(),30));
            animToolbar.setSize(new Dimension(animViewerFrame.getContentPane().getWidth(),30));
        }
        if (animPanel != null) {
            animPanel.setPreferredSize(new Dimension(animViewerFrame.getContentPane().getWidth()-100,
                    animViewerFrame.getContentPane().getHeight()-170));            
                      
        }
        if (animScrollList != null) {
            animScrollList.setPreferredSize(new Dimension(100,
                    animViewerFrame.getContentPane().getHeight()-170)); 
            animScrollList.setSize(new Dimension(100,
                    animViewerFrame.getContentPane().getHeight()-170)); 
                        
        }
        if (animScrollText != null) {            
            animScrollText.setPreferredSize(new Dimension(animScrollText.getParent().getWidth(),100));     
            animScrollText.setSize(new Dimension(animScrollText.getParent().getWidth(),100));
        }
        
        if (animSlider != null) {            
            animSlider.setPreferredSize(new Dimension(animSlider.getParent().getWidth(), 30));                                
                                          
        }        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        prop = new Properties();
        try {
            InputStream propIn = new FileInputStream("properties/viewer.properties");  
            prop.load(propIn);
        } catch(Exception e) {
            LOG.log(Level.WARNING, noPropErr);
        }
        plugManager = new AnimPluginManager();
        
        w = Integer.parseInt(prop.getProperty("app_width", defW));
        h = Integer.parseInt(prop.getProperty("app_height", defH));
        version = Integer.parseInt(prop.getProperty(app_version,"0"));
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        animViewerFrame = new JFrame(String.format(versionFormat,prop.getProperty("app_name", defName), version));        
        animViewerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        animViewerFrame.setPreferredSize(new Dimension(w, h));                
                        
        initMenuBar(); 
        initToolbar();
        initList();
        initSlider();
        initGraphicsPane();
        initTextArea();
        
        panel1 = new JPanel(new BorderLayout());
        panel1.add(animToolbar, BorderLayout.PAGE_START);
        panel1.add(animScrollList, BorderLayout.LINE_START);
        panel1.add(animPanel, BorderLayout.LINE_END);
        panel2 = new JPanel(new BorderLayout());        
        panel2.add(animSlider, BorderLayout.PAGE_START);
        panel2.add(animScrollText, BorderLayout.PAGE_END);
        
        animViewerFrame.setJMenuBar(animMenuBar);                                                
        animViewerFrame.getContentPane().add(panel1, BorderLayout.PAGE_START);
        animViewerFrame.getContentPane().add(panel2,BorderLayout.PAGE_END);                    
                
        animViewerFrame.pack();                                                      
        animViewerFrame.addComponentListener(new WindowListener());
        updateLengths();        
        
        animViewerFrame.setVisible(true);        
                
    }
    
    private static void initLookandFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            LOG.log(Level.WARNING,noLookErr);            
        }
    }
    
    private static void initMenuBar() {
        animMenuBar = new JMenuBar();
        
        // Vytvorene nove menu
        JMenu menu = new JMenu("File");        
        // Pridanie tlacidla na pridanie pluginov
        JMenuItem item = new JMenuItem("Add Plugin");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();                
                FileNameExtensionFilter filter = new FileNameExtensionFilter("JAR files", "jar");
                chooser.setFileFilter(filter);
                int retValue = chooser.showOpenDialog(animViewerFrame);
                
                if (retValue == JFileChooser.APPROVE_OPTION) {
                    DefaultListModel<AnimPlugin> model = (DefaultListModel<AnimPlugin>) animList.getModel();                    
                    model.addElement(plugManager.addPlugin(chooser.getSelectedFile()));
                }
            }
        });
        menu.add(item);
        // Pridanie tlacidla na vymazanie vsetkych pluginov
        item = new JMenuItem("Remove All");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                animList.setModel(new DefaultListModel<AnimPlugin>());
                plugManager.removeAll();
                animText.append(infoRemove);
            }
        });
        menu.add(item);
        // Pridanie separatoru
        menu.add(new JSeparator());
        //Pridanie tlacidla na ukoncenie
        item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(item);
        
        // Pridanie celeho jedneho menu do menu baru
        animMenuBar.add(menu);
        
        // Vytvorene nove menu
        menu = new JMenu("Help");
        item = new JMenuItem("Check for Updates");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {                
                Updater update = new Updater(prop);  
                try {
                    if (update.getLatestVersion() == version) {
                        JOptionPane.showMessageDialog(animViewerFrame, uptoDate);
                    } else {
                        int answer = JOptionPane.showConfirmDialog(animViewerFrame, newVersion,newVersionTitle,JOptionPane.YES_NO_OPTION);
                        if (answer == 0) {
                            return;
                        } else {
                            animText.append("Downloading");
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(animViewerFrame, noConnection);
                    animText.append(noConnection);
                }
            }
        });
        menu.add(item);
        
        // Pridanie help okna do menu baru
        animMenuBar.add(menu);
    }

    private static void initToolbar() {
        animToolbar = new JToolBar();
        animToolbar.add(new JButton("sk"));
    }
    
    private static void initSlider() {
        animSlider = new JSlider(JSlider.HORIZONTAL,fpsMin,fpsMax, fpsInit);                
        animSlider.setMinorTickSpacing(1);
        animSlider.setPaintTicks(true);                        
    }
    
    private static void initList() {
        animList = new JList();        
        DefaultListModel<AnimPlugin> animModel = new DefaultListModel<>();        
        animList.setModel(animModel);       
        
        animScrollList = new JScrollPane(animList,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    private static void initGraphicsPane() {
        animPanel = new JPanel();                 
    }
    
    private static void initTextArea() {
        animText = new JTextArea();              
        animText.setEditable(false);        
        animScrollText = new JScrollPane(animText,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);                                
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package updater;

import animviewer.AnimViewer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import plugins.AnimPluginManager;

/**
 *
 * @author kirrie
 */
public class Updater implements Runnable {
    
    private static final Logger LOG = Logger.getLogger(Updater.class.getClass().getName());  
    
    private static final String updServer = "update_server";    
    private static final String delim = "[;]";
    private String serverLinks;
    
    
    public Updater(Properties prop) {
        serverLinks=prop.getProperty(updServer);        
    }
    
    public int getLatestVersion() throws Exception{
        String[] links = serverLinks.split(delim);                
                
        int retVersion = AnimViewer.version;
        for (String link : links) {
            String replaceAll = getData(link);
            if (replaceAll == null) {
                continue;            
            }
            String[] replaceAllN = replaceAll.split("\n");
            switch (replaceAllN.length) {
                case 0 : continue;
                default : replaceAll = replaceAllN[0];                   
            }
                          
            retVersion = Math.max(retVersion, Integer.parseInt(replaceAll.replaceAll("\\[version\\]", "")));                                                    
        }
        return retVersion;
    }
    
    public String getLatestHistory() throws Exception {
        Map<Integer,String> releases = getAllReleases();
        
        if (releases.size() == 0) {
            return "";
        }
        
        int max = 0;
        for (Integer i : releases.keySet()) {
            if (i > max) {
                max = i;
            }
        }
        
        return releases.get(max);
    }
    
    public Map<Integer,String> getAllReleases() throws Exception {
        String[] links = serverLinks.split(delim);                
        
        Map<Integer,String> hist = new HashMap<>();
        for (String link : links) {
            String replaceAll = getData(link);
            if (replaceAll == null) {
                continue;            
            }
            String[] replaceAllN = replaceAll.split("\n");
            switch (replaceAllN.length) {
                case 0 : continue;
                case 1 : 
                    hist.put(Integer.parseInt(replaceAllN[0].replaceAll("\\[version\\]", "")),"");                    
                    break;
                case 2 :
                    hist.put(Integer.parseInt(replaceAllN[0].replaceAll("\\[version\\]", "")),
                            replaceAllN[1].replaceAll("\\[history\\]",""));                     
                    break;
            }                                                
        }    
        return hist;        
    }
    
    private String getData(String link) throws Exception {        
        
        URL url = new URL(link);

        BufferedReader html = new BufferedReader(new InputStreamReader(url.openStream()));                
        StringBuilder buffer = new StringBuilder();
        String line;                
        while ((line = html.readLine())!=null) {
            buffer.append(line);
        }                
        return buffer.toString();
        
    }
    
    
    @Override
    public void run() {
        
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package updater;

import animviewer.AnimViewer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

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
    
    public String getLatestVersion() throws Exception{
        String[] links = serverLinks.split(delim);                
                
        String retVersion = AnimViewer.version;
        for (String link : links) {
            String replaceAll = getInfoData(link);
            if (replaceAll == null) {
                continue;            
            }
            String[] replaceAllN = replaceAll.split("(\n)*\\[release\\](\n)*");
            String[] replaceAllV = null;
            String newVersion;
            switch (replaceAllN.length) {
                case 0 :
                case 1 :
                    replaceAllV = replaceAllN[0].split("\n");                    
                    switch (replaceAllV.length) {
                        case 0 :
                            break;
                        default :
                             newVersion = replaceAllV[0].replaceAll("\\[version\\]", "");
                             retVersion = String.CASE_INSENSITIVE_ORDER.compare(retVersion, newVersion)==1 ? retVersion : newVersion;                        
                    }
                default : 
                    for (String s : replaceAllN) {
                        replaceAllV = s.split("\n");
                        switch (replaceAllV.length) {
                            case 0 :  
                                continue;
                            default :
                                newVersion = replaceAllV[0].replaceAll("\\[version\\]", "");
                                retVersion = String.CASE_INSENSITIVE_ORDER.compare(retVersion, newVersion)==1 ? retVersion : newVersion;
                        }                         
                    }
            }                                                                                        
        }
        return retVersion;
    }
    
    public String getLatestHistory() throws Exception {
        Map<String,String> releases = getAllReleases();
        
        if (releases.size() == 0) {
            return "";
        }
        
        String max = "";
        for (String s : releases.keySet()) {
            if (String.CASE_INSENSITIVE_ORDER.compare(max, s) == -1) {
                max = s;
            }
        }
        
        return releases.get(max);
    }
    
    public Map<String,String> getAllReleases() throws Exception {
        String[] links = serverLinks.split(delim);                
        
        Map<String,String> hist = new HashMap<>();
        for (String link : links) {
            String replaceAll = getInfoData(link);
            if (replaceAll == null) {
                continue;            
            }
            String[] replaceAllN = replaceAll.split("(\n)*\\[release\\](\n)*");
            String[] replaceAllV = null;
            switch (replaceAllN.length) {
                case 0 :  
                    continue;
                default :
                    for (String s : replaceAllN) {                        
                        replaceAllV = s.split("\n");
                        switch (replaceAllV.length) {
                            case 0 : continue;
                            case 1 : 
                                hist.put(replaceAllV[0].replaceAll("\\[version\\]", ""),"");                    
                                break;
                            case 2 :
                                hist.put(replaceAllV[0].replaceAll("\\[version\\]", ""),
                                        replaceAllV[1].replaceAll("\\[history\\]",""));                     
                                break;
                        }                          
                    }
            }                                                  
        }    
        return hist;        
    }
    
    private String getInfoData(String link) throws Exception {        
        
        URL url = new URL(link+"\\version.html");

        BufferedReader html = new BufferedReader(new InputStreamReader(url.openStream()));                
        StringBuilder buffer = new StringBuilder();
        String line;                
        while ((line = html.readLine())!=null) {            
            buffer.append(line);
            buffer.append("\n");
        }                
        return buffer.toString();
        
    }
    
    
    @Override
    public void run() {
        
    }
    
}

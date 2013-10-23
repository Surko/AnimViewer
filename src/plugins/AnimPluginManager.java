/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author kirrie
 */
public class AnimPluginManager {
    private static final Logger LOG = Logger.getLogger(AnimPluginManager.class.getClass().getName());   
    private static final String mainClassName = "Animation";
    private static final String plugLoadErr = "Can't load plugin with name {0}. It does not exist.";
    private static final String plugInitErr = "Can't load plugin with name {0}. It doesn't meet requirements to be a plugin.";
    private ArrayList<AnimPlugin> animPlugins;
    
    public AnimPluginManager() {
        animPlugins = new ArrayList<>();
    }
    
    public AnimPlugin addPlugin(File file) {
        try {
            System.out.println(file.toURI().toURL());
            URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL()}); 
            AnimPlugin plugin = (AnimPlugin)authorizedLoader.loadClass(
                    animviewer.AnimViewer.prop.getProperty("mainClassName",mainClassName)).newInstance();
            animPlugins.add(plugin);
            return plugin;
        } catch (Exception e) {
            LOG.log(Level.WARNING,plugInitErr,file.getName());
        }        
        return null;
    }
    
    public AnimPlugin addPlugin(String name) {
        try {
            File _toLoad = new File(name);
            return addPlugin(_toLoad);
        } catch (Exception e) {
            LOG.log(Level.WARNING,plugLoadErr,name);
        }
        return null;
    }
    
    public void removePlugin(String name) {
        for (Iterator<AnimPlugin> iter = animPlugins.iterator();iter.hasNext();) {
            if (iter.next().toString().equals(name)) {
                iter.remove();
            }
        }
    }
    
    public void removePlugin(int index) {
        animPlugins.remove(index);
    }
    
    public void removeAll() {
        animPlugins.clear();
    }
    
    public DefaultListModel<AnimPlugin> getListModel() {
        DefaultListModel<AnimPlugin> returnModel = new DefaultListModel<>();
        for (Iterator<AnimPlugin> iter = animPlugins.iterator();iter.hasNext();) {
            returnModel.addElement(iter.next());
        }
        return returnModel;
    }
}

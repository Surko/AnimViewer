/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins;

import animviewer.AnimViewer;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author kirrie
 */
public class AnimPluginManager {
    private static final Logger LOG = Logger.getLogger(AnimPluginManager.class.getClass().getName());   
    private static final String mainClassName = "Animation";
    private static final String plugLoadErr = "Cant load plugin with name %s. It does not exist.\n";
    private static final String plugInitErr = "Cant load plugin with name %s. It doesn't meet requirements to be a plugin.\n";
    private ArrayList<AnimPlugin> animPlugins;
    
    public AnimPluginManager() {
        animPlugins = new ArrayList<>();
    }
    
    public AnimPlugin addPlugin(File file) {
        try {            
            URLClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL()}); 
            AnimPlugin plugin = (AnimPlugin)authorizedLoader.loadClass(
                    animviewer.AnimViewer.prop.getProperty("mainClassName",mainClassName)).newInstance();
            animPlugins.add(plugin);
            return plugin;
        } catch (Exception e) {
            AnimViewer.animText.append(String.format(plugInitErr,file.getName()));            
        }        
        return null;
    }
    
    public AnimPlugin addPlugin(String name) {
        try {
            File _toLoad = new File(name);
            return addPlugin(_toLoad);
        } catch (Exception e) {
            AnimViewer.animText.append(String.format(plugLoadErr,name));
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
    
    public DefaultTreeModel getListModel() {
        DefaultTreeModel returnModel = new DefaultTreeModel(AnimViewer.rootNode);
        for (Iterator<AnimPlugin> iter = animPlugins.iterator();iter.hasNext();) {
            AnimViewer.rootNode.insert(new DefaultMutableTreeNode(iter.next()),AnimViewer.rootNode.getChildCount());
        }
        return returnModel;
    }
}

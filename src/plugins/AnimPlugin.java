/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins;

import animviewer.AnimViewer;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kirrie
 */
public abstract class AnimPlugin implements Runnable{        
    
    private static final Logger LOG = Logger.getLogger(AnimPlugin.class.getName());
    
    public AnimPlugin() {}
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    public abstract void animate(Graphics g);    
    @Override
    public abstract String toString();            
    // </editor-fold>

    @Override
    public void run() {
        if (AnimViewer.activePlugin != this || AnimViewer.stopped) {
            return;
        }
        
        while (!AnimViewer.stopped) {
            while(AnimViewer.paused) {
                try {
                    Thread.sleep(100l);
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

            try {            
                animate(AnimViewer.animPanel.getGraphics());
                Thread.sleep((long)(1000/AnimViewer.animSlider.getValue()));
            } catch (InterruptedException ex) {

            }
        }
    }
    
}

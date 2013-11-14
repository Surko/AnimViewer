/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins;

import animviewer.AnimViewer;
import java.awt.Container;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author kirrie
 */
public abstract class AnimPlugin implements Runnable{        
    
    protected Container panel;
    private static final Logger LOG = Logger.getLogger(AnimPlugin.class.getName());
    
    public AnimPlugin() {        
    }
        
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">    
    public abstract void animate();    
    @Override
    public abstract String toString();            
    // </editor-fold>

    public void setPanel(Container panel) {
        this.panel = panel;
    }
    
    public void showMsg(String msg) {
        AnimViewer.animText.append(msg);
    }
    
    public void exit() {
        synchronized(this) {
            if (AnimViewer.animState == AnimViewer.STOP) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @Override
    public void run() {
        if (AnimViewer.activePlugin != this || AnimViewer.animState == AnimViewer.STOP) {
            return;
        }
        
        while (AnimViewer.animState != AnimViewer.STOP) {
            while(AnimViewer.animState == AnimViewer.PAUSE) {
                try {
                    Thread.sleep(100l);
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

            try {         
                if (AnimViewer.animSlider.getValue() == 0) {
                    Thread.sleep(1000);
                    continue;
                }
                animate();                
                Thread.sleep((long)(1000/AnimViewer.animSlider.getValue()));
            } catch (InterruptedException ex) {

            }
        }
        
        System.out.println("Done");
        synchronized(this) {
            notifyAll();
        }
    }
    
}

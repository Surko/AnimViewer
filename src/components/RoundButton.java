/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import javax.swing.JComponent;

/**
 *
 * @author kirrie
 */
public class RoundButton extends JComponent {
    ActionListener actionListener;     // Post action events to listeners
    BufferedImage icon1,icon2;                     // The Button's text
    protected boolean pressed = false; // true if the button is detented.
    
    
    public RoundButton(Image icon1) {
        this.icon1=new BufferedImage(icon1.getWidth(null), icon1.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        this.icon1.getGraphics().drawImage(icon1, 0, 0, null);
        this.icon2 = new BufferedImage(icon1.getWidth(null), icon1.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        this.icon2.getGraphics().drawImage(icon1, 0, 0, null);
        RescaleOp op = new RescaleOp(1f, -50f, null);
        op.filter(this.icon1, this.icon2);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }
    /**
     * Constructs a RoundedButton with the specified label.
     *
     * @param label the label of the button
     */
    public RoundButton(Image icon1, Image icon2) {
        this.icon1=new BufferedImage(icon1.getWidth(null), icon1.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        this.icon1.getGraphics().drawImage(icon1, 0, 0, null);
        this.icon2 = new BufferedImage(icon1.getWidth(null), icon1.getHeight(null), BufferedImage.TYPE_INT_ARGB);        
        this.icon2.getGraphics().drawImage(icon2, 0, 0, null);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    /**
     * paints the RoundedButton
     */
    @Override
    public void paint(Graphics g) {

        // paint the interior of the button
        if (pressed) {
            g.drawImage(icon2, 0, 0, null);
        } else {           
            g.drawImage(icon1, 0, 0, null);            
        }        
        
    }

    
    /**
     * The preferred size of the button.
     */
    @Override
    public Dimension getPreferredSize() {       
        if (icon1 != null) {
            return new Dimension(icon1.getWidth(null),icon1.getHeight(null));
        } else {
            return new Dimension(32, 32);
        }
    }

    @Override
    public Dimension getMaximumSize() {       
        if (icon1 != null) {
            return new Dimension(icon1.getWidth(null),icon1.getHeight(null));
        } else {
            return new Dimension(32, 32);
        }
    }
    
    @Override
    public Dimension getSize() {       
        if (icon1 != null) {
            return new Dimension(icon1.getWidth(null),icon1.getHeight(null));
        } else {
            return new Dimension(32, 32);
        }
    }
    
    /**
     * The minimum size of the button.
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(32, 32);
    }
    
    /**
     * Adds the specified action listener to receive action events from this
     * button.
     *
     * @param listener the action listener
     */
    public void addActionListener(ActionListener listener) {
        actionListener = AWTEventMulticaster.add(actionListener, listener);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    /**
     * Removes the specified action listener so it no longer receives action
     * events from this button.
     *
     * @param listener the action listener
     */
    public void removeActionListener(ActionListener listener) {
        actionListener = AWTEventMulticaster.remove(actionListener, listener);
    }

    /**
     * Determine if click was inside round button.
     */
    @Override
    public boolean contains(int x, int y) {
        int mx = getSize().width / 2;
        int my = getSize().height / 2;
        return (((mx - x) * (mx - x) + (my - y) * (my - y)) <= mx * mx);
    }

    /**
     * Paints the button and distribute an action event to all listeners.
     */
    @Override
    public void processMouseEvent(MouseEvent e) {
        Graphics g;
        switch (e.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                // render myself inverted....
                pressed = true;

                // Repaint might flicker a bit. To avoid this, you can use
                // double buffering (see the Gauge example).
                repaint();
                break;
            case MouseEvent.MOUSE_RELEASED:
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(
                            this, ActionEvent.ACTION_PERFORMED, null));
                }
                // render myself normal again
                if (pressed == true) {
                    pressed = false;

                    // Repaint might flicker a bit. To avoid this, you can use
                    // double buffering (see the Gauge example).
                    repaint();
                }
                break;
            case MouseEvent.MOUSE_ENTERED:

                break;
            case MouseEvent.MOUSE_EXITED:
                if (pressed == true) {
                    // Cancel! Don't send action event.
                    pressed = false;

                    // Repaint might flicker a bit. To avoid this, you can use
                    // double buffering (see the Gauge example).
                    repaint();

                    // Note: for a more complete button implementation,
                    // you wouldn't want to cancel at this point, but
                    // rather detect when the mouse re-entered, and
                    // re-highlight the button. There are a few state
                    // issues that that you need to handle, which we leave
                    // this an an excercise for the reader (I always
                    // wanted to say that!)
                }
                break;
        }
        super.processMouseEvent(e);
    }
}

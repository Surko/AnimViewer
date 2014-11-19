/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author kirrie
 */
public class ColorLabel extends JPanel{
    
    public static final int W = 40, H = 20;
    private Color rectColor,textColor;
    private String value;
    
    public ColorLabel(Color rectColor, int value) {
        this.rectColor = rectColor;
        this.textColor = rectColor;
        this.value = value + "";
    }
    
    public ColorLabel(Color rectColor, String s) {
        this.rectColor = rectColor;
        this.value = s;
    }
    
    public ColorLabel(Color rectColor, Color textColor, Object s) {
        this.rectColor = rectColor;
        this.textColor = textColor;
        this.value = s.toString();
    }
    
    @Override
    public int getWidth() {
        return ColorLabel.W + 50;
    }
    
    @Override
    public int getHeight() {
        return ColorLabel.H;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public Dimension getSize(Dimension rv) {
        return new Dimension(getWidth(), getHeight());
    }
    
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(rectColor);
        g.fillRect(0, 0, W, H);
        g.setColor(textColor);
        g.drawString(value, W, H - 5);
    }
    
        
}

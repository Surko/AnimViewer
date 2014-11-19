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
public class GridColorPanel extends JPanel{
    public static final int W = 20, H = 20;
    private Color c;
    private String value;
    private int m, l;
    private Color[][] colors;
    
    public GridColorPanel(int m,int l) {
        this.m = m;
        this.l = l;
        colors = new Color[m][l];
    }    
    
    @Override
    public int getWidth() {
        return l * W;
    }
    
    @Override
    public int getHeight() {
        return m * H;
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
    
    public void setColor(int i, int j, Color c) {
        colors[i][j] = c;
    }
    
    public void removeColors() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < l; j++) {
                colors[i][j] = Color.BLACK;
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < l; j++) {
                g.setColor(colors[i][j]);
                g.fillRect(j * W, i * H, W, H);        
            }
        }
        
    }
}

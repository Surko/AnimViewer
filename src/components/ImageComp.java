/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import static components.ImageComp.base;
import static components.ImageComp.pixel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.Random;
import javax.swing.JPanel;

/**
 *
 * @author kirrie
 */
/**
 *
 * @author kirrie
 */
public class ImageComp extends JPanel {
    
    public BufferedImage image, original, secondLayer;
    private BufferedImage workingImage;
    private WritableRaster raster;
    
    public static ImageComp.Base base = new ImageComp.Base();
    public double sx,sy,ex,ey;
    public int Ox,Oy,scale;
    public int baseX = 134, baseY = 160, baseZ = 90;
    
    public static double[] pixel = new double[3];
    
    static class Base {
        public double Xx,Xy,Yx,Yy,Zx,Zy;
    }
    
    public double transform(double x, double y, double z, int type) {
        switch (type) {
            case 0 :
                return x * base.Xx + y * base.Yx + z * base.Zx;
            case 1 :
                return x * base.Xy + y * base.Yy + z * base.Zy;
        }
        
        return 0;
    } 
    
    public double rotate(double alfa, double beta, double gamma, double x, double y, double z, int type) {
        switch (type) {
            case 0 :
                return Math.cos(gamma)*(x*Math.cos(beta)-(z*Math.cos(alfa)-y*Math.sin(alfa))
                        *Math.sin(beta))+(y*Math.cos(alfa)+z*Math.sin(alfa))*Math.sin(gamma);
            case 1 :
                return Math.cos(gamma)*(y*Math.cos(alfa)+z*Math.sin(alfa))-(x*Math.cos(beta)
                        -(z*Math.cos(alfa)-y*Math.sin(alfa))*Math.sin(beta))*Math.sin(gamma);

            case 2 :
                return Math.cos(beta)*(z*Math.cos(alfa)-y*Math.sin(alfa))+x*Math.sin(beta);            

        }
        return 0;
    }
    
    public void setBase(double alfa, double beta, double gamma) {
        base.Xx = rotate(alfa, beta, gamma, 1, 0, 0, 0);
        base.Xy = rotate(alfa, beta, gamma, 1, 0, 0, 1);        
        base.Yx = rotate(alfa, beta, gamma, 0, 1, 0, 0);
        base.Yy = rotate(alfa, beta, gamma, 0, 1, 0, 1);        
        base.Zx = rotate(alfa, beta, gamma, 0, 0, 1, 0);
        base.Zy = rotate(alfa, beta, gamma, 0, 0, 1, 1);        
    }
    
    public void setScale() {
        scale = (int)Math.round((double)image.getWidth() / 2 / Math.sqrt(3));
        Ox = image.getWidth()/2 - 10;
        Oy = image.getHeight()/2 + 10;
        scale = Math.round(150 * scale);
    }
    
    public ImageComp(int w, int h) {
        image = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
        secondLayer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
    }
    
    public ImageComp(BufferedImage image) {
        this.image = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.image.getGraphics().drawImage(image, 0, 0, null);
        secondLayer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }
    
    @Override
    public int getWidth() {
        return image.getWidth();
    }
    
    @Override
    public int getHeight() {
        return image.getHeight();
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(getWidth(),getHeight());
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(),getHeight());
    }
    
    @Override
    public Dimension getSize() {
        return new Dimension(getWidth(),getHeight());
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public double[] getPixel(int i, int j) {
        return image.getRaster().getPixel(j, i, pixel);
    }
    
    
    public WritableRaster getRaster() {
        return image.getRaster();
    }
    
    public double[] getRandomPixel(Random rand) {        
        return image.getRaster().getPixel(rand.nextInt(getWidth()), rand.nextInt(getHeight()), pixel);        
        
    }
    
    public void setPixel(int i, int j, double[] pixel) {
        image.getRaster().setPixel(j, i, pixel);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);  
        if (secondLayer != null) {                    
            g.drawImage(secondLayer, 0, 0, null);
        }
    }
    
    public void start() {
        workingImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        raster = workingImage.getRaster();
    }
    
    public void done() {
        if (workingImage.getWidth() != image.getWidth() || workingImage.getHeight() != image.getHeight()) {
            BufferedImageOp op = new RescaleOp(image.getWidth()/workingImage.getWidth(), 1.0f, null);
            op.filter(workingImage, image);
        } else {
            image = workingImage;                
        }
        raster = null;
        this.repaint();
    }
    
    public void setOriginal(BufferedImage original) {
        this.original = original;
    }
    
    public void resizePaintAr(int[] pixels,int scale, int defSize) {        
        int[] setPixel = new int[scale * scale];
        WritableRaster raster = image.getRaster();
        for(int i = 0; i < pixels.length;i++) {            
            for (int j = 0; j < scale * scale; j++) {
                setPixel[j] = pixels[i];
            }
            raster.setPixels((i % defSize) * scale, (i/defSize) * scale, scale, scale, setPixel);
        }
        
        
    }
    
    public void resetBackground() {
        Graphics g = image.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    public void resetPoints() {
        Graphics2D g = (Graphics2D) secondLayer.getGraphics();        
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    public void resizePaint(BufferedImage bi) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(bi,0,0, image.getWidth(), image.getHeight(), this);
    }
    
    public void drawPoint(int x, int y, Color color) {
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.drawLine(x,y,x,y);
    }
    
    public void drawPointonLayer(int x, int y, Color color) {
        Graphics g = secondLayer.getGraphics();
        g.setColor(color);
        g.drawLine(x,y,x,y);
    }
    
    public void drawLine(int startX, int startY, int endX, int endY, Color color) {        
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.drawLine(startX, startY, endX, endY);
    }
    
    public void drawLine(int startX, int startY, int endX, int endY, float[] color) {        
        Graphics g = image.getGraphics();
        g.setColor(new Color(color[0], color[1], color[2], 1));
        g.drawLine(startX, startY, endX, endY);
    }
    
    public void draw3dLine(double Sx,double Sy,double Sz,double Ex,double Ey,double Ez, Color color) {                
        sx = Ox + Ox * transform(Sx, Sy, Sz, 0);
        sy = Oy - Oy * transform(Sx, Sy, Sz, 1);
        ex = Ox + Ox * transform(Ex, Ey, Ez, 0);
        ey = Oy - Oy * transform(Ex, Ey, Ez, 1);
        
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.drawLine((int)sx, (int)sy, (int)ex, (int)ey);
    }
    
    public void draw3dPoint(double x, double y, double z, Color color) {
        sx = Ox + Ox * transform(x, y, z, 0);
        sy = Oy - Oy * transform(x, y, z, 1);
        
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.drawLine((int)sx,(int)sy,(int)sx,(int)sy);
    }
    
    public void draw3dLayerPoint(double x, double y, double z, Color color) {
        sx = Ox + Ox * transform(x, y, z, 0);
        sy = Oy - Oy * transform(x, y, z, 1);
        
        if (sx >= getWidth() || sx < 0 || sy >= getHeight() || sy < 0) {
            return;
        }
        
        Graphics g = secondLayer.getGraphics();
        g.setColor(color);
        g.drawLine((int)sx,(int)sy,(int)sx,(int)sy);
    }
    
    public void draw3dLayerPoint(double x, double y, double z, double[] color) {
        sx = Ox + Ox * transform(x, y, z, 0);
        sy = Oy - Oy * transform(x, y, z, 1);
        
        if (sx >= getWidth() || sx < 0 || sy >= getHeight() || sy < 0) {
            return;
        }
        
        secondLayer.getRaster().setPixel((int)sx, (int)sy, color);
    }
    
    public void draw3dAxis() {
        draw3dLine(0, 0, 0, 0, 0, 1, Color.white);
        draw3dLine(0, 0, 0, 0, 1, 0, Color.white);
        draw3dLine(0, 0, 0, 1, 0, 0, Color.white);
    }
    
    public void change(int x, int y, double[] values) {                
        raster.setPixel(x, y, values);
    }
    
    public void change(int x, int y, int[] values) {                
        raster.setPixel(x, y, values);
    }
}


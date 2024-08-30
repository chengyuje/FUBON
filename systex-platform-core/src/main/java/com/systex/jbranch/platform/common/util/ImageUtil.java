package com.systex.jbranch.platform.common.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageUtil {
    
   public static byte[] imageToByteArr(Image image) throws IOException{

	   BufferedImage buf = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);

	   ByteArrayOutputStream bas = new ByteArrayOutputStream();
	   Graphics2D g = (Graphics2D) buf.getGraphics();
	   g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
	   ImageIO.write(buf,"jpg", bas);
	   return bas.toByteArray();
   }
	
	public static BufferedImage ImateToBufferedImage(Image image) {
		
		return imageToBufferedImage(image, false);
	}
	
	public static BufferedImage imageToBufferedImage(Image image, boolean hasAlpha) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	    }
	
	    // This code ensures that all the pixels in the image are loaded
	    image = new ImageIcon(image).getImage();
	
	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see Determining If an Image Has Transparent Pixels
	
	    // Create a buffered image with a format that's compatible with the screen
	    BufferedImage bimage = null;
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	        if (hasAlpha) {
	            transparency = Transparency.BITMASK;
	        }
	
	        // Create the buffered image
	        GraphicsDevice gs = ge.getDefaultScreenDevice();
	        GraphicsConfiguration gc = gs.getDefaultConfiguration();
	        bimage = gc.createCompatibleImage(
	            image.getWidth(null), image.getHeight(null), transparency);
	    } catch (HeadlessException e) {
	        // The system does not have a screen
	    }
	
	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	    }
	
	    // Copy image to buffered image
	    Graphics g = bimage.createGraphics();
	
	    // Paint the image onto the buffered image
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	
	    return bimage;
	}

}

package com.systex.jbranch.platform.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lowagie.text.pdf.Barcode39;

public class BarcodeUtil {

	/**
	 * 產生Barcode
	 * @param data 欲產生的號碼
	 * @return
	 */
	public static Image createBarcode(String data){
		Barcode39 code39 = new Barcode39();
		code39.setCode(data); 
		code39.setX(1);
		code39.setBarHeight(18);
		return code39.createAwtImage(Color.BLACK, Color.WHITE);
	}
	
	/**
	 * @param args
	 * @throws BarcodeException 
	 * @throws OutputException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub


		Image image = createBarcode("1234567890");
		System.out.println(image instanceof BufferedImage);
		
//        Barcode barcode = BarcodeFactory.createCodabar("123456789012");
//        barcode.setToolTipText(text)
        BufferedImage bufImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D) bufImage.getGraphics();
        File f = new File("d:/mybarcode.jpg");
//        barcode.draw(g, 0, 0);
        g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
        ImageIO.write(bufImage,"jpg",f);
        System.out.println("finshed");
	}

}

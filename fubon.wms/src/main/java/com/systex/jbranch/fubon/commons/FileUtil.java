package com.systex.jbranch.fubon.commons;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileUtil {
	
	public static void removeBOM (File file)  throws  IOException {
		 
		byte [] bs = FileUtils.readFileToByteArray(file);    
		if  (bs[ 0 ] == - 17  && bs[ 1 ] == - 69  && bs[ 2 ] == - 65 ) {    
			byte [] nbs =  new  byte [bs.length -  3 ];    
			System.arraycopy(bs,  3 , nbs,  0 , nbs.length);    
			FileUtils.writeByteArrayToFile(file, nbs);    
		}    
	}   

    public static boolean isUTF8(File file) {  
    	
        try {  
            byte[] buf = FileUtils.readFileToByteArray(file);  
            String UTF8Cntent = FileUtils.readFileToString(file, "UTF-8");  
            String big5Cntent = new String(buf, "Big5");  
            String defCntent = new String(buf); //Default is UTF8  
              
            if(buf.length == UTF8Cntent.getBytes().length) {  
                byte[] buf_utf8 = UTF8Cntent.getBytes();  
                for(int i=0; i < buf_utf8.length; i++) {
                    if(buf_utf8[i] != buf[i]){  
                        return false;  
                    }  
                }  
                return true;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        return false;  
    }  
}

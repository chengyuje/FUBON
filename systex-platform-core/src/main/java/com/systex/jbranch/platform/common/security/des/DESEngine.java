package com.systex.jbranch.platform.common.security.des;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * @author Alex Lin
 * @version 2011/01/20 1:53 PM
 */
public class DESEngine {
    private Cipher ecipher;
    private Cipher dcipher;

    public DESEngine(byte[] key) throws Exception {
        ecipher = Cipher.getInstance("DES/ECB/NoPadding");
        dcipher = Cipher.getInstance("DES/ECB/NoPadding");
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(new DESKeySpec(key));
        ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
        dcipher.init(Cipher.DECRYPT_MODE, secretKey);
    }

    public byte[] encrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return ecipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return dcipher.doFinal(data);
    }
    public static void main(String[] args) throws Exception {
    	  byte[] initKey = {0x00, 0x0a, 0x27, (byte) 0xd2, 0x29, (byte) 0xdc, (byte) 0xff, (byte) 0xf6};
    	  byte[] bytes = {0x0c, (byte) 0xb9, (byte) 0xfe, 0x5c, 0x35, 0x00, 0x00, 0x00};
		DESEngine engine = new DESEngine(initKey);
		System.out.println(Hex.encodeHexString(engine.decrypt(bytes)));
	}
//    public static void main(String[] args) throws Exception {
//		String data = "1uRgxywSoW+H6XZ92o6CviEznnaDbTbF6tjNl21kNkUqV4lXKSLDzt1/rKruH0MR75kKlu1BvcmHc+UetAzhupqRbY3jcMTejYdB3ku7mvWRVb34Qul1oZFVvfhC6XWhTqTUoyaXWM+RVb34Qul1oZFVvfhC6XWhkVW9+ELpdaGRVb34Qul1oZFVvfhC6XWhkVW9+ELpdaGRVb34Qul1oV5VIrHPCZkLi+yEIR/fT62RVb34Qul1oZFVvfhC6XWhkVW9+ELpdaGLIlc+/uVcBw==";
//		String keyStr = "ABC123CBDFEF887D";
//		byte[] keyByteArr = Hex.decodeHex(keyStr.toCharArray());//keyStr.getBytes("MS950");
//		byte[] dataByteArr = Base64.decodeBase64(data);
//		
//		keyByteArr = testEecrypt(keyByteArr, "01030526".getBytes("MS950"));
//		
//		byte[] enData = testEecrypt(keyByteArr, "29999    0050418 20000010105240000000004100110010400000000010000000000009999                    A-4    1                                                              07553316                          ".getBytes("MS950"));
//		System.out.println(new String(Base64.encodeBase64(enData), "MS950"));
//        byte[] deData = testDecrypt(keyByteArr,Base64.encodeBase64(dataByteArr));
//        System.out.println(new String(deData, "MS950"));
//	}
//
//	private static byte[] testEecrypt(byte[] keyByteArr, byte[] dataByteArr)throws Exception {
////		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//	
//	    DESKeySpec desKeySpec = new DESKeySpec(keyByteArr);
//	
//	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//	    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
////	    IvParameterSpec iv = new IvParameterSpec(keyByteArr);
//	    Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//	    
//	    return cipher.doFinal(dataByteArr);
//	}
//
//	private static byte[] testDecrypt(byte[] keyByteArr, byte[] dataByteArr)
//			throws Exception {
////		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//
//        DESKeySpec desKeySpec = new DESKeySpec(keyByteArr);
////        SecureRandom sr = new SecureRandom(keyByteArr);  
////        IvParameterSpec iv = new IvParameterSpec(keyByteArr);
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
//        IvParameterSpec iv = new IvParameterSpec(keyByteArr);
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
//
//        return cipher.doFinal(dataByteArr);
//	}
}
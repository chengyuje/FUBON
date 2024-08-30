package com.systex.jbranch.comutil.encrypt.aes;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

public class AesEncryptDecryptUtils {
	public static final String AES_ENCRYPT = "AES";
	public static final String AES_ECB_PKCS7_PADDING = "AES/ECB/PKCS7Padding";
	public static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    
	/** AES NO PADDING encrypt 128 **/
    public static String encryptAes128(String key ,String val) throws Exception {
    	return encryptAes(key , val , 128);
    }

    /** AES NO PADDING decrypt 128 **/
    public static String decryptAes128(String secretKey , String base64) throws Exception{
		return decryptAes(secretKey , base64 , 128);
    }
    
    /** AES NO PADDING encrypt 192**/
    public static String encryptAes192(String key ,String val) throws Exception {
    	return encryptAes(key , val , 192);
    }

    /** AES NO PADDING decrypt 192 **/
    public static String decryptAes192(String secretKey , String base64) throws Exception{
		return decryptAes(secretKey , base64 , 192);
    }
  
    /** AES NO PADDING encrypt 256 **/
    public static String encryptAes256(String key ,String val) throws Exception {
    	return encryptAes(key ,val , 256);
    }

    /** AES NO PADDING decryp 256 **/
    public static String decryptAes256(String secretKey , String base64) throws Exception{
		return decryptAes(secretKey , base64 , 256);
    }
    
    /** AES NO PADDING encrypt **/   
    public static String encryptAes(String secretKey ,String encryptStr , int size) throws Exception {
    	Cipher cipher = genDefCipher(Cipher.ENCRYPT_MODE , size , secretKey);
		return new String(org.apache.commons.codec.binary.Base64.encodeBase64(cipher.doFinal(encryptStr.getBytes("UTF-8"))));
    }

    /** AES NO PADDING decryp **/
    public static String decryptAes(String secretKey , String encryptStr , int size) throws Exception{
    	Cipher cipher = genDefCipher(Cipher.DECRYPT_MODE , size , secretKey);
		return new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encryptStr)));
    }
    
    public static Cipher genDefCipher(int model , int size , String secretKey) throws Exception{
		KeyGenerator keyGen = KeyGenerator.getInstance(AES_ENCRYPT);
		keyGen.init(size , new SecureRandom(secretKey.getBytes("UTF-8")));
		Cipher cipher = Cipher.getInstance(AES_ENCRYPT);
		cipher.init(model, new SecretKeySpec(keyGen.generateKey().getEncoded() , AES_ENCRYPT));
		return cipher;
    }
     
    /** AES加密：傳入密鑰(會做ASCII轉byte[])、val、偏移量、以及指定用那種編碼(128、192、256) **/
    public static String encryptAesCbcPkcs5PaddingAscii(String key , String val , String ivParameter , int size) throws Exception {
    	Cipher cipher = genCbcPkcs5PadAsciiCipher(Cipher.ENCRYPT_MODE , key , ivParameter , size);
    	return new String(org.apache.commons.codec.binary.Base64.encodeBase64(cipher.doFinal(val.getBytes("UTF-8"))));
    }
    
    /** AES解密：傳入密鑰(會做ASCII轉byte[])、val、偏移量、以及指定用那種編碼(128、192、256) **/
    public static String decryptAesCbcPkcs5PaddingAscii(String key , String encryptStr , String ivParameter , int size) throws Exception{
		Cipher cipher = genCbcPkcs5PadAsciiCipher(Cipher.DECRYPT_MODE , key , ivParameter , size);
		return new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encryptStr)));
    }
 
    /** AES加解密：model為指定加密或解密的參數，key會轉ASCII byte[]，val、偏移量、以及指定用那種編碼(128、192、256) **/
    public static Cipher genCbcPkcs5PadAsciiCipher(int model , String key , String ivParameter , int size) throws Exception{
    	return genCbcPkcs5PadCipher(model , key , "ASCII" , ivParameter , size);
    }
    
    /** AES CBC_PKCS5_PADDING 加解密 **/
    public static Cipher genCbcPkcs5PadCipher(int model , String key , String rawEncode , String ivParameter , int size) throws Exception{
		KeyGenerator keyGen = KeyGenerator.getInstance(AES_ENCRYPT);
		keyGen.init(size , new SecureRandom(key.getBytes("UTF-8")));
		byte[] raw = key.getBytes(rawEncode);
		
		Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
		IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes("UTF-8"));
		cipher.init(model , new SecretKeySpec(raw , AES_ENCRYPT) , iv);
		
		return cipher;
    }
    
    /** AES ECB_PKCS7_PADDING 加密 **/
    public static String encryptAesEcbPkcs7Padding(String secretKey , String val) throws Exception {
	    return encryptAesEcbPkcs7Padding(secretKey.getBytes("UTF-8") , val);
    }

    /** AES ECB_PKCS7_PADDING 解密 **/
    public static String decryptAesEcbPkcs7Padding(String secretKey , String val) throws Exception{
	    return decryptAesEcbPkcs7Padding(secretKey.getBytes("UTF-8") , val);
    }
        
    public static String encryptAesEcbPkcs7Padding(byte [] keyBytes , String val) throws Exception{
	    Cipher cipher = doGetAesEcbPkcs7PaddingCipher(Cipher.ENCRYPT_MODE , keyBytes);
	    return new String(Base64.encode(cipher.doFinal(val.getBytes("UTF-8"))));
    }
    
    public static String decryptAesEcbPkcs7Padding(byte [] keyBytes , String val) throws Exception{
	    Cipher cipher = doGetAesEcbPkcs7PaddingCipher(Cipher.DECRYPT_MODE , keyBytes);
	    return new String(cipher.doFinal(Base64.decode(val)));
    }
    
    public static Cipher doGetAesEcbPkcs7PaddingCipher(int model , byte[] keyBytes) throws Exception{
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    	SecretKeySpec key = new SecretKeySpec(keyBytes , AES_ENCRYPT);
 	    Cipher cipher = Cipher.getInstance(AES_ECB_PKCS7_PADDING);
 	    cipher = Cipher.getInstance(AES_ECB_PKCS7_PADDING);
 	    cipher.init(model , key);
 	    return cipher;
    }
    
    public static byte[] secretKeyToByteArray(String encode , int byteArrLength , String secretKey) throws UnsupportedEncodingException{
    	byte[] bkey = new byte[byteArrLength];
    	byte[] tmp = secretKey.getBytes(encode);
    	
        for (int i = 0; i < 16 && i < tmp.length; i++)
        {
            bkey[i] = tmp[i];
        }
        
        return bkey;
    }
}

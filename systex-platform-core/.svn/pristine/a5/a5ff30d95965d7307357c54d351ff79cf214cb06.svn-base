package com.systex.jbranch.platform.common.security.impl;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecukeystorVO;
import com.systex.jbranch.platform.common.security.JBranchSecurityHelper;
import com.systex.jbranch.platform.common.security.crypto.CryptologyIF;
import com.systex.jbranch.platform.common.security.key.KeyManagementIF;
import com.systex.jbranch.platform.common.util.PlatformContext;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;


/**
 * 預設的加解密類別，實作了CryptologyIF
 *
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class JBranchCryptology implements CryptologyIF {
// ------------------------------ FIELDS ------------------------------

    public static final char KEY_TYPE_PRIVATE = '2';

    public static final char KEY_TYPE_PUBLIC = '1';
    private static Logger logger = LoggerFactory.getLogger(JBranchCryptology.class);
    private static final String DEFAULT_ASYMM_KEY_ID = "jbranchdefaultasymmkey";//預設key pair id

    private static final String DEFAULT_KEY_ID = "jbranchdefaultkey";//預設Key ID
    private static final String KEY_DEF_ALGORITHM = "defaultalgorithm";
    private static final String KEY_DEF_IV = "defaultiv";
    private static final String KEY_DEF_MOD = "defaultmod";

    private static final String KEY_DEF_PADDING = "defaultpadding";

    private DataAccessManager dam = null;
    private JBranchKeyManagement keymanagement = null;

    private IvParameterSpec piv = null;//預設IV

// -------------------------- STATIC METHODS --------------------------
    public static String encode(String value , String skey) throws IllegalBlockSizeException, BadPaddingException {
    	return new String(Hex.encodeHex(genCipher(value  , skey , Cipher.ENCRYPT_MODE).doFinal(value.getBytes())));
    }

    public static String decode(String value  , String skey) throws IllegalBlockSizeException, BadPaddingException, DecoderException  {
    	return new String(genCipher(value  , skey , Cipher.DECRYPT_MODE).doFinal(Hex.decodeHex(value.toCharArray())));
    }
    
    public static Cipher genCipher(String value  , String skey , int mode) {
        try {
        	byte[] keyBytes = skey.getBytes("UTF-8");
            IvParameterSpec piv = new IvParameterSpec(keyBytes);
            
            SecretKey s = new SecretKeySpec(keyBytes, JBranchSecurityHelper.CRYPTO_ALGORITHM_DES);

            Cipher cid = Cipher.getInstance(JBranchSecurityHelper.CRYPTO_ALGORITHM_DES
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_MODE_CBC
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_PADDING_PKCS5PADDING);
            cid.init(mode, s, piv);

            return cid;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    

    public static String encodePassword(String password) {
        String result = password;
        try {
            IvParameterSpec piv = new IvParameterSpec(new byte[]{0, 1, 2, 4, 3, 5, 6, 7});
            byte[] parameterKey = new byte[]{7, 6, 5, 3, 4, 2, 1, 0};
            SecretKey s = new SecretKeySpec(parameterKey,
                    JBranchSecurityHelper.CRYPTO_ALGORITHM_DES);
            Cipher cie = Cipher.getInstance(JBranchSecurityHelper.CRYPTO_ALGORITHM_DES
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_MODE_CBC
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_PADDING_PKCS5PADDING);
            cie.init(Cipher.ENCRYPT_MODE, s, piv);

            String DEFAULT_ALGORITHM = JBranchSecurityHelper.CRYPTO_ALGORITHM_DES;

            result = new String(Hex.encodeHex(cie.doFinal(result.getBytes())));
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    public static String decodePassword(String password) {
        String result = password;
        try {
            IvParameterSpec piv = new IvParameterSpec(new byte[]{0, 1, 2, 4, 3, 5, 6, 7});
            byte[] parameterKey = new byte[]{7, 6, 5, 3, 4, 2, 1, 0};
            SecretKey s = new SecretKeySpec(parameterKey, JBranchSecurityHelper.CRYPTO_ALGORITHM_DES);

            Cipher cid = Cipher.getInstance(JBranchSecurityHelper.CRYPTO_ALGORITHM_DES
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_MODE_CBC
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_PADDING_PKCS5PADDING);
            cid.init(Cipher.DECRYPT_MODE, s, piv);

            result = new String(cid.doFinal(Hex.decodeHex(result.toCharArray())));
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public JBranchCryptology() {

    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CryptologyIF ---------------------


    public byte[] asymmetricPublicEncrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException {
        return asymmetricEncrypt(pairID, algorithm, mode, padding, data, KEY_TYPE_PUBLIC);
    }

    public byte[] asymmetricPrivateEncrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException {
        return asymmetricEncrypt(pairID, algorithm, mode, padding, data, KEY_TYPE_PRIVATE);
    }

    public byte[] asymmetricPublicDecrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException {
        return asymmetricDecrypt(pairID, algorithm, mode, padding, data, KEY_TYPE_PUBLIC);
    }

    public byte[] asymmetricPrivateDecrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException {
        return asymmetricDecrypt(pairID, algorithm, mode, padding, data, KEY_TYPE_PRIVATE);
    }

    /**
     * 請參照CryptologyIF
     */
    public byte[] getMsgDigest(String algorithm, byte[] data) throws JBranchException {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照CryptologyIF
     */
    public byte[] symmetricDecrypt(String keyID, byte[] data) throws JBranchException {
        try {
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            piv = new IvParameterSpec(Parameters.getParameter(KEY_DEF_IV));
            Key key = keymanagement.getKey(keyID)[0];
            String algorithm = keymanagement.getAlgorithm(keyID);
            Cipher cipher = Cipher.getInstance(algorithm + "/" +
                    new String(Parameters.getParameter(KEY_DEF_MOD)) + "/" +
                    new String(Parameters.getParameter(KEY_DEF_PADDING)));
            cipher.init(Cipher.DECRYPT_MODE, key, piv);
            return cipher.doFinal(data);
        }
        catch (InvalidKeyException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (IllegalBlockSizeException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (BadPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (InvalidAlgorithmParameterException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
    }

    public byte[] symmetricEncrypt(byte[] data) throws JBranchException {
        try {
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            piv = new IvParameterSpec(Parameters.getParameter(KEY_DEF_IV));
            Key key = keymanagement.getKey(DEFAULT_KEY_ID)[0];
            Cipher cipher = Cipher.getInstance(
                    new String(Parameters.getParameter(KEY_DEF_ALGORITHM)) + "/" +
                            new String(Parameters.getParameter(KEY_DEF_MOD)) + "/" +
                            new String(Parameters.getParameter(KEY_DEF_PADDING)));
            cipher.init(Cipher.ENCRYPT_MODE, key, piv);
            return cipher.doFinal(data);
        }
        catch (InvalidKeyException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (IllegalBlockSizeException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (BadPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (InvalidAlgorithmParameterException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
    }

    public byte[] symmetricDecrypt(byte[] data) throws JBranchException {
        try {
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            piv = new IvParameterSpec(Parameters.getParameter(KEY_DEF_IV));
            Key key = keymanagement.getKey(DEFAULT_KEY_ID)[0];
            Cipher cipher = Cipher.getInstance(
                    new String(Parameters.getParameter(KEY_DEF_ALGORITHM)) + "/" +
                            new String(Parameters.getParameter(KEY_DEF_MOD)) + "/" +
                            new String(Parameters.getParameter(KEY_DEF_PADDING)));
            cipher.init(Cipher.DECRYPT_MODE, key, piv);
            return cipher.doFinal(data);
        }
        catch (InvalidKeyException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (IllegalBlockSizeException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (BadPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (InvalidAlgorithmParameterException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
    }

    public byte[] symmetricEncrypt(String keyID, byte[] data) throws JBranchException {
        try {
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            piv = new IvParameterSpec(Parameters.getParameter(KEY_DEF_IV));
            Key key = keymanagement.getKey(keyID)[0];
            String algorithm = keymanagement.getAlgorithm(keyID);
            Cipher cipher = Cipher.getInstance(algorithm + "/" +
                    new String(Parameters.getParameter(KEY_DEF_MOD)) + "/" +
                    new String(Parameters.getParameter(KEY_DEF_PADDING)));
            cipher.init(Cipher.ENCRYPT_MODE, key, piv);
            return cipher.doFinal(data);
        }
        catch (InvalidKeyException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (NoSuchPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (IllegalBlockSizeException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (BadPaddingException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        catch (InvalidAlgorithmParameterException e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
    }

    public byte[] asymmetricPublicEncrypt(String pairID, byte[] data) throws JBranchException {
        return asymmetricEncrypt(pairID, data, KEY_TYPE_PUBLIC);
    }

    public byte[] asymmetricPrivateEncrypt(String pairID, byte[] data) throws JBranchException {
        return asymmetricEncrypt(pairID, data, KEY_TYPE_PRIVATE);
    }

    public byte[] asymmetricPublicDecrypt(String pairID, byte[] data) throws JBranchException {
        return asymmetricDecrypt(pairID, data, KEY_TYPE_PUBLIC);
    }

    public byte[] asymmetricPrivateDecrypt(String pairID, byte[] data) throws JBranchException {
        return asymmetricDecrypt(pairID, data, KEY_TYPE_PRIVATE);
    }

    public byte[] asymmetricPublicDecrypt(byte[] data) throws JBranchException {
        return asymmetricDecrypt(DEFAULT_ASYMM_KEY_ID, data, KEY_TYPE_PUBLIC);
    }

    public byte[] asymmetricPrivateDecrypt(byte[] data) throws JBranchException {
        return asymmetricDecrypt(DEFAULT_ASYMM_KEY_ID, data, KEY_TYPE_PRIVATE);
    }

// -------------------------- OTHER METHODS --------------------------

    public byte[] asymmetricPrivateEncrypt(byte[] data) throws JBranchException {
        return asymmetricEncrypt(DEFAULT_ASYMM_KEY_ID, data, KEY_TYPE_PRIVATE);
    }

    /**
     * 請參照CryptologyIF
     */
    private byte[] asymmetricEncrypt(String pairID, byte[] data, char keyType) throws JBranchException {
        TbsyssecukeystorVO TbsyssecukeystorVO = new TbsyssecukeystorVO();
        byte[] bytCipherText = null;
        try {
            if (dam == null) {
                dam = new DataAccessManager();
            }
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            TbsyssecukeystorVO = (TbsyssecukeystorVO) dam.findByPKey(TbsyssecukeystorVO.TABLE_UID, pairID);
            Cipher desCipherEncrypt = Cipher.getInstance("DES");
            //JBranchKeyManagement jBranchKeyManagement=new JBranchKeyManagement();
            //使用公鑰加密
            if (keyType == 1) {
                SecretKey pubKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue1().getBytes(), "DES");
                desCipherEncrypt.init(Cipher.ENCRYPT_MODE, pubKey);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }
            if (keyType == 2) {
                //使用私鑰加密
                SecretKey priKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue2().getBytes(), "DES");
                desCipherEncrypt.init(Cipher.ENCRYPT_MODE, priKey);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }
        }
        catch (Exception e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        return bytCipherText;
    }

    public byte[] asymmetricPublicEncrypt(byte[] data) throws JBranchException {
        return asymmetricEncrypt(DEFAULT_ASYMM_KEY_ID, data, KEY_TYPE_PUBLIC);
    }

    public byte[] deCode(String str) {
        Hex hex = new Hex();
        byte[] decodeStr = null;
        try {
            decodeStr = hex.decodeHex(str.toCharArray());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return decodeStr;
    }

    public String enCode(byte[] str) {
        Hex hex = new Hex();
        String encodeStr = new String(hex.encodeHex(str));
        return encodeStr;
    }

    /**
     * 請參照CryptologyIF
     */
    private byte[] asymmetricDecrypt(String pairID, byte[] data, char keyType) throws JBranchException {
        TbsyssecukeystorVO TbsyssecukeystorVO = new TbsyssecukeystorVO();
        byte[] bytCipherText = null;
        try {
            if (dam == null) {
                dam = new DataAccessManager();
            }
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            TbsyssecukeystorVO = (TbsyssecukeystorVO) dam.findByPKey(TbsyssecukeystorVO.TABLE_UID, pairID);
            Cipher desCipherEncrypt = Cipher.getInstance("DES");
            //JBranchKeyManagement jBranchKeyManagement=new JBranchKeyManagement();
            //使用公鑰解密
            if (keyType == 1) {
                SecretKey pubKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue1().getBytes(), "DES");
                desCipherEncrypt.init(Cipher.DECRYPT_MODE, pubKey);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }

            //使用私鑰解密
            if (keyType == 2) {
                SecretKey priKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue2().getBytes(), "DES");
                desCipherEncrypt.init(Cipher.DECRYPT_MODE, priKey);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }
        }
        catch (Exception e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        return bytCipherText;
    }

    /**
     * 請參照CryptologyIF
     *
     * @throws JBranchException
     * @throws DAOException
     */
    private byte[] asymmetricDecrypt(String pairID, String algorithm, String mode, String padding, byte[] data, char keyType) throws DAOException, JBranchException {
        StringBuffer sbf = new StringBuffer();
        TbsyssecukeystorVO TbsyssecukeystorVO = new TbsyssecukeystorVO();
        sbf.append(algorithm + "/" + mode + "/" + padding);
        //JBranchKeyManagement jBranchKeyManagement=new JBranchKeyManagement();
        byte[] bytCipherText = null;
        try {
            if (dam == null) {
                dam = new DataAccessManager();
            }
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            TbsyssecukeystorVO = (TbsyssecukeystorVO) dam.findByPKey(TbsyssecukeystorVO.TABLE_UID, pairID);
            Cipher desCipherEncrypt = Cipher.getInstance(sbf.toString());
            IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{10, 20, 30, 40, 50, 60, 70, 80});
            if (keyType == 1) {
                SecretKey secretKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue1().getBytes(), algorithm);
                desCipherEncrypt.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }
            if (keyType == 2) {
                SecretKey secretKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue2().getBytes(), algorithm);
                desCipherEncrypt.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }
        }
        catch (Exception e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        return bytCipherText;
    }

    /**
     * 請參照CryptologyIF
     *
     * @throws JBranchException
     * @throws DAOException
     */
    private byte[] asymmetricEncrypt(String pairID, String algorithm, String mode, String padding, byte[] data, char keyType) throws DAOException, JBranchException {
        StringBuffer sbf = new StringBuffer();
        TbsyssecukeystorVO TbsyssecukeystorVO = new TbsyssecukeystorVO();
        sbf.append(algorithm + "/" + mode + "/" + padding);
        //JBranchKeyManagement jBranchKeyManagement=new JBranchKeyManagement();
        byte[] bytCipherText = null;
        try {
            if (dam == null) {
                dam = new DataAccessManager();
            }
            keymanagement = (JBranchKeyManagement) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
            TbsyssecukeystorVO = (TbsyssecukeystorVO) dam.findByPKey(TbsyssecukeystorVO.TABLE_UID, pairID);
            Cipher desCipherEncrypt = Cipher.getInstance(sbf.toString());
            IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{10, 20, 30, 40, 50, 60, 70, 80});
            if (keyType == 1) {
                SecretKey secretKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue1().getBytes(), algorithm);
                desCipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }
            if (keyType == 2) {
                SecretKey secretKey = keymanagement.generateSecretKey(TbsyssecukeystorVO.getKeyvalue2().getBytes(), algorithm);
                desCipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                bytCipherText = desCipherEncrypt.doFinal(data);
            }
        }
        catch (Exception e) {
            JBranchException je = new JBranchException("");
            je.setException(e);
            throw je;
        }
        return bytCipherText;
    }

// --------------------------- main() method ---------------------------

    public static void main(String[] args) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String in = System.currentTimeMillis() + "";

            md.update(("1234").getBytes("UTF-8"));
            if (logger.isDebugEnabled()) {
                logger.debug(new String(Hex.encodeHex(md.digest())));
            }

            byte[] array = "1234".getBytes("UTF-8");
            if (logger.isDebugEnabled()) {
                for (byte anArray : array) {
                    logger.debug(String.valueOf(anArray));
                }
            }


//			JBranchCryptology a = new JBranchCryptology();
//			String in = System.currentTimeMillis()+"";
//			LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, new String(Hex.encodeHex(a.getMsgDigest(JBranchSecurityHelper.MSG_DIGEST_ALGORITHM_SHA1, (in+"userid"+in).getBytes()))));
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    	
    	/*
    	 *3.          加密前URL：
HTTPS://BA.FUBONLIFE.COM.TW/BOFILEDOWNLOAD.ASPX?FILE=10250033680000176078.pdf&timer=20170603123255&partner_code=209&authorkey=1019044781
 
4.          加密後URL：
HTTPS://BA.FUBONLIFE.COM.TW/BOFILEDOWNLOAD.ASPX?FILE=10250033680000176078.pdf&PA=pz7ubsPfeYxe/PCL8vL1pKSy/+qNkjIlPm2O8vjOMOFLsP6k4Gto+Na+7u4kXFQZRt6O1G0WZA12JCTS
    	 */
        
        //測試des加解密
//    	String path = "HTTPS://BA.FUBONLIFE.COM.TW/BOFILEDOWNLOAD.ASPX?FILE=10250033680000176078.pdf&";
//    	String param = "timer=20170603123255&partner_code=209&authorkey=1019044781";
//    	String skey = "8bytekey";
//    	System.out.println(path + (param = encode(param, skey)));
//    	System.out.println(path + decode(param, skey));
    }
}

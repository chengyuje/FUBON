package com.systex.jbranch.platform.common.security.impl;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.EnumErrInputType;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecucryparaVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecukeystorVO;
import com.systex.jbranch.platform.common.security.JBranchSecurityHelper;
import com.systex.jbranch.platform.common.security.key.KeyManagementIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 預設提供的密鑰管理類別，實作KeyManagementIF
 *
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class JBranchKeyManagement implements KeyManagementIF {
// ------------------------------ FIELDS ------------------------------

    private KeyStore ks;
    private Key lmk;
    private FileInputStream inStream;
    private Map<String, Key[]> keyMap = new Hashtable<String, Key[]>();
    private Map<String, String> keyAlgor = new Hashtable<String, String>();
    private String keystoreFile;
	private Logger logger = LoggerFactory.getLogger(JBranchKeyManagement.class);
    private byte[] parameterKey = new byte[]{7, 6, 5, 3, 4, 2, 1, 0};
    private IvParameterSpec piv = new IvParameterSpec(new byte[]{0, 1, 2, 4, 3, 5, 6, 7});
    private SecretKey s = new SecretKeySpec(parameterKey, JBranchSecurityHelper.CRYPTO_ALGORITHM_DES);
    private DataAccessManager dataAccessManager;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface KeyManagementIF ---------------------

    /**
     * 請參照KeyManagementIF
     */
    public SecretKey generateSecretKey(int keySize, SecureRandom random) throws JBranchException {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            keygen.init(keySize, random);
            return keygen.generateKey();
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    public SecretKey generateSecretKey(int keySize) throws JBranchException {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            keygen.init(keySize);
            return keygen.generateKey();
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    public KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom random) throws JBranchException {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
            keyPairGen.initialize(keySize, random);
            return keyPairGen.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    public KeyPair generateKeyPair(String algorithm, int keySize) throws JBranchException {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
            keyPairGen.initialize(keySize);
            return keyPairGen.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    public String wrapKey(Key key) throws JBranchException {
        try {
            Key lmk = ks.getKey("lmk", new String(Parameters.getParameter("lpw")).toCharArray());
            IvParameterSpec piv = new IvParameterSpec(new byte[]{0, 9, 8, 7, 6, 5, 4, 3});
            Cipher cipher = Cipher.getInstance(JBranchSecurityHelper.CRYPTO_ALGORITHM_TRIPLE_DES
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_MODE_CBC
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_PADDING_PKCS5PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, lmk, piv);
            return new String(Hex.encodeHex(cipher.doFinal(key.getEncoded())));
        }
        catch (InvalidKeyException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (KeyStoreException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (UnrecoverableKeyException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (NoSuchPaddingException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (InvalidAlgorithmParameterException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (IllegalBlockSizeException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (BadPaddingException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    public SecretKey getSecretKey(String keyID) throws DAOException, JBranchException {
        try {
            TbsyssecukeystorVO vo = null;
            vo = (TbsyssecukeystorVO) dataAccessManager.findByPKey(TbsyssecukeystorVO.TABLE_UID, keyID);
            if (vo == null) {
                throw new DAOException(EnumErrInputType.MSG, "key " + keyID + " not found");
            }
            byte[] skey = hexString2ByteArray(vo.getKeyvalue1());
            return generateSecretKey(skey, "DES");
        }
        catch (DecoderException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    public void deleteCertificate(String certID) {
    }

    /**
     * 請參照KeyManagementIF
     */
    public boolean addCertificate(String certID, Certificate cert) {
        return false;
    }

    /**
     * 請參照KeyManagementIF
     */
    public void deleteKeyPair(String pairID) throws DAOException, JBranchException {
        TbsyssecukeystorVO vo = new TbsyssecukeystorVO();
        vo.setKeyid(pairID);
        vo.setKeytype(KeyManagementIF.KEY_TYPE_KEYPAIR);
        dataAccessManager.delete(vo);
    }

    /**
     * 請參照KeyManagementIF
     */
    public void deleteSecretKey(String keyID) throws DAOException, JBranchException {
        TbsyssecukeystorVO vo = new TbsyssecukeystorVO();
        vo.setKeyid(keyID);
        vo.setKeytype(KeyManagementIF.KEY_TYPE_SECRET);
        dataAccessManager.delete(vo);
    }

    /**
     * 請參照KeyManagementIF
     */
    public void addKeyPair(String pairID, KeyPair keyPair, String algorithm) throws DAOException, JBranchException {
        TbsyssecukeystorVO vo = new TbsyssecukeystorVO();
        PrivateKey priKey = keyPair.getPrivate();
        PublicKey pubKey = keyPair.getPublic();
        vo.setKeyid(pairID);
        vo.setKeytype(KeyManagementIF.KEY_TYPE_KEYPAIR);
        vo.setKeyvalue1(byteArray2HexString(pubKey.getEncoded()));
        vo.setKeyvalue2(byteArray2HexString(priKey.getEncoded()));
        vo.setAlgorithm(algorithm);
        dataAccessManager.create(vo);
    }

    /**
     * 請參照KeyManagementIF
     */
    public void addSecretKey(String keyID, SecretKey secretKey, String algorithm) throws DAOException, JBranchException {
        TbsyssecukeystorVO vo = new TbsyssecukeystorVO();
        vo.setKeyid(keyID);
        vo.setKeytype(KeyManagementIF.KEY_TYPE_SECRET);
        vo.setAlgorithm(algorithm);
        vo.setKeyvalue1(byteArray2HexString(secretKey.getEncoded()));
        dataAccessManager.create(vo);
    }

    /**
     * 請參照KeyManagementIF
     */
    public void setLMK(SecretKey lmk) throws JBranchException {
        try {
            KeyStore ks = KeyStore.getInstance("JCEKS");
            //ks.load(null, null); 第一次產生keystore file時使用
            ks.load(null, null);
            ks.setKeyEntry("SecretKey", lmk, "pass".toCharArray(), null);
            ks.store(new FileOutputStream("c:/Zdss.ks"), "1234".toCharArray());
        }
        catch (KeyStoreException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (CertificateException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public void init() throws Exception {
        //解密所有資料庫中的加解密參數，存至Map中
        List<TbsyssecucryparaVO> voList;
        byte[] value;
        Cipher desCipherDecrypt = Cipher
                .getInstance(JBranchSecurityHelper.CRYPTO_ALGORITHM_DES
                        + "/"
                        + JBranchSecurityHelper.CRYPTO_MODE_CBC
                        + "/"
                        + JBranchSecurityHelper.CRYPTO_PADDING_PKCS5PADDING);
        desCipherDecrypt.init(Cipher.DECRYPT_MODE, s, piv);
        voList = (List<TbsyssecucryparaVO>) dataAccessManager.findAll(TbsyssecucryparaVO.TABLE_UID);
        for (TbsyssecucryparaVO vo : voList) {
            value = desCipherDecrypt.doFinal(Hex.decodeHex(vo
                    .getParametervalue().toCharArray()));
            Parameters.setParameter(vo.getParameterkey(), value);
        }

        //初始化KeyManagement
        this.loadKeyStore();
        this.loadKeys();
    }

    void loadKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, Exception {
        try {
            inStream = new FileInputStream(keystoreFile);
            ks = KeyStore.getInstance("JCEKS");
            ks.load(inStream,
                    new String(Parameters.getParameter("hpw")).toCharArray());
            lmk = ks.getKey("lmk", new String(Parameters.getParameter("lpw")).toCharArray());
        }
        finally {
            try {
                inStream.close();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 將key從資料庫Load至memory中
     *
     * @throws DAOException
     * @throws DecoderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    void loadKeys() throws DAOException, JBranchException {
        try {
            keyMap.clear();
            List<TbsyssecukeystorVO> voList = (List<TbsyssecukeystorVO>) dataAccessManager.findAll(TbsyssecukeystorVO.TABLE_UID);
            if (voList == null || voList.size() == 0) {
                return;
            }
            for (TbsyssecukeystorVO vo : voList) {
                if (vo.getKeytype().equals(KeyManagementIF.KEY_TYPE_SECRET)) {//secret key
                    Key key = generateSecretKey(unWrapKey(vo.getKeyvalue1()), vo.getAlgorithm());
                    keyMap.put(vo.getKeyid(), new Key[]{key});
                    keyAlgor.put(vo.getKeyid(), vo.getAlgorithm());
                }
                else {//key pair
                    Key key1 = this.generatePublicKey(unWrapKey(vo.getKeyvalue1()), vo.getAlgorithm());
                    Key key2 = this.generatePrivateKey(unWrapKey(vo.getKeyvalue2()), vo.getAlgorithm());
                    keyMap.put(vo.getKeyid(), new Key[]{key1, key2});
                    keyAlgor.put(vo.getKeyid(), vo.getAlgorithm());
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    public SecretKey generateSecretKey(byte[] keyValue, String algorithm) {
        return new SecretKeySpec(keyValue, algorithm);
    }

    public PublicKey generatePublicKey(byte[] keyValue, String algorithm) throws JBranchException {
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyValue);
            return kf.generatePublic(pubKeySpec);
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    public PrivateKey generatePrivateKey(byte[] keyValue, String algorithm) throws JBranchException {
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(keyValue);
            return kf.generatePrivate(priPKCS8);
        }
        catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    /**
     * 請參照KeyManagementIF
     */
    //
    public byte[] unWrapKey(String wrappedKey) throws JBranchException {
        try {
            IvParameterSpec piv = new IvParameterSpec(new byte[]{0, 9, 8, 7, 6, 5, 4, 3});
            Cipher cipher = Cipher.getInstance(JBranchSecurityHelper.CRYPTO_ALGORITHM_TRIPLE_DES
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_MODE_CBC
                    + "/"
                    + JBranchSecurityHelper.CRYPTO_PADDING_PKCS5PADDING);
            cipher.init(Cipher.DECRYPT_MODE, lmk, piv);
            return cipher.doFinal(Hex.decodeHex(wrappedKey.toCharArray()));
        }
        catch (InvalidKeyException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (NoSuchAlgorithmException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (NoSuchPaddingException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (InvalidAlgorithmParameterException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (IllegalBlockSizeException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (BadPaddingException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
        catch (DecoderException e) {
            JBranchException je = new JBranchException(EnumErrInputType.MSG, e.getMessage());
            je.setException(e);
            throw je;
        }
    }

    String getAlgorithm(String keyId) {
        return keyAlgor.get(keyId);
    }

    Key[] getKey(String keyId) {
        return keyMap.get(keyId);
    }

    /**
     * 取得轉換成Hex形式的字串
     *
     * @param value
     * @return
     */
    private String byteArray2HexString(byte[] value) {
        return new String(Hex.encodeHex(value));
    }

    /**
     * 取得Hex形式的字串轉換的byte array
     *
     * @param value
     * @return
     */
    private byte[] hexString2ByteArray(String hexString) throws DecoderException {
        return Hex.decodeHex(hexString.toCharArray());
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }
}

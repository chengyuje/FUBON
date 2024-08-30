package com.systex.jbranch.platform.common.security.key;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

import org.apache.commons.codec.DecoderException;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;

/** 
 * 密鑰管理介面
 * 規範產生、刪除、存取密鑰介面
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public interface KeyManagementIF {

    public static final String KEY_MANAGEMENT_ID = "keymanagement";
    public static final String KEY_TYPE_SECRET = "1";
    public static final String KEY_TYPE_KEYPAIR = "2";

	public javax.crypto.SecretKey generateSecretKey(byte[] keyValue, String algorithm);
	
	/**
	 * 建立SecretKey
	 * @param keySize
	 * @param random 亂數
	 * @reutrn secretKey 
	*/
	public javax.crypto.SecretKey generateSecretKey(int keySize, java.security.SecureRandom random)  throws JBranchException;

	/**
	 * 建立SecretKey
	 * @param keySize
	 * @reutrn secretKey 
	*/
	public javax.crypto.SecretKey generateSecretKey(int keySize)  throws JBranchException;

	/**
	 * 建立KeyPair
	 * @param algorithm 演算法
	 * @param keySize
	 * @param random 亂數
	 * @return keyPair 
	*/
	public java.security.KeyPair generateKeyPair(String algorithm, int keySize, java.security.SecureRandom random)  throws JBranchException;

	/**
	 * 產生KeyPair(public,private Key)
	 * @param algorithm 演算法
	 * @param keySize 密鑰大小
	 * @return KeyPair 
	*/
	public java.security.KeyPair generateKeyPair(String algorithm, int keySize)   throws JBranchException;

	/**
	 * 以LML解密Key值
	 * @param byte array 被wrap的key值
	 * @return 
	*/
	public byte[] unWrapKey(String wrappedKey)  throws JBranchException;

	/**
	 * 用LMK加密KEY值
	 * @param key 需要被加密的密鑰
	 * @return byte array 被加密後的key值 
	*/
	public String wrapKey(java.security.Key key)   throws JBranchException;

	/**
	 * 取得指定keyID的Key
	 * @param keyID
	 * @return SecretKey 
	*/
	public javax.crypto.SecretKey getSecretKey(String keyID)   throws JBranchException;

	/**
	 * 刪除指定憑證
	 * @param certID 
	*/
	public void deleteCertificate(String certID);

	/**
	 * 存入憑證
	 * @param certID 憑證ID
	 * @param cert 憑證 
	*/
	public boolean addCertificate(String certID, java.security.cert.Certificate cert);

	/**
	 * 刪除KeyPair
	 * @param pairID
	 * @return 是否刪除成功 
	*/
	public void deleteKeyPair(String pairID)    throws DAOException, JBranchException;

	/**
	 * 刪除KEY
	 * @param keyID
	 * @return 是否刪除成功 
	*/
	public void deleteSecretKey(String keyID)  throws DAOException, JBranchException;

	/**
	 * 存入KeyPair
	 * @param pairID
	 * @parma keyPair
	*/
	public void addKeyPair(String pairID, java.security.KeyPair keyPair, String algorithm) throws DAOException, JBranchException;

	/**
	 * 存入SecretKey
	 * @param keyID
	 * @param secretKey 
	 * @return 是否存入成功 
	*/
	public void addSecretKey(String keyID, javax.crypto.SecretKey secretKey, String algorithm)  throws DAOException, JBranchException;

	/**
	 * 設定LMK
	 * @param lmk
	*/
	public void setLMK(javax.crypto.SecretKey lmk)   throws JBranchException;

	
	public PublicKey generatePublicKey(byte[] keyValue, String algorithm)   throws JBranchException;
	
	public PrivateKey generatePrivateKey(byte[] keyValue, String algorithm)   throws JBranchException;
}

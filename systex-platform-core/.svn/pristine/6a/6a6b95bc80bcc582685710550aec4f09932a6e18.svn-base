package com.systex.jbranch.platform.common.security.util;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.key.KeyManagementIF;
import com.systex.jbranch.platform.common.util.PlatformContext;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 密鑰管理實際呼叫使用類別，可取得不同實作物件
 * @author Hong-jie
 * @version 1.0 2008/04/14
 */
public class KeyManagementUtil implements KeyManagementIF {

	private KeyManagementIF            keymanagement;
	private static KeyManagementUtil   instance;

	/**
	 * 取得預設的KeyManagementIF實體
	 * @return CryptoUtil object
	*/
	public static KeyManagementUtil getInstance() throws JBranchException{
		if(instance == null)
			instance = new KeyManagementUtil();
		return instance;
	}

	private KeyManagementUtil() throws JBranchException{

		keymanagement = (KeyManagementIF) PlatformContext.getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
	}

	public javax.crypto.SecretKey generateSecretKey(byte[] keyValue, String algorithm){
		return keymanagement.generateSecretKey(keyValue, algorithm);
	}

	/**
	 * 建立SecretKey
	 * @param keySize
	 * @param random 亂數
	 * @reutrn secretKey
	*/
	public javax.crypto.SecretKey generateSecretKey(int keySize, java.security.SecureRandom random)  throws JBranchException{

		return keymanagement.generateSecretKey(keySize, random);
	}

	/**
	 * 建立SecretKey
	 * @param keySize
	 * @reutrn secretKey
	*/
	public javax.crypto.SecretKey generateSecretKey(int keySize)  throws JBranchException{

		return keymanagement.generateSecretKey(keySize);
	}

	/**
	 * 建立KeyPair
	 * @param algorithm 演算法
	 * @param keySize
	 * @param random 亂數
	 * @return keyPair
	*/
	public java.security.KeyPair generateKeyPair(String algorithm, int keySize, java.security.SecureRandom random)  throws JBranchException{

		return keymanagement.generateKeyPair(algorithm, keySize, random);
	}

	/**
	 * 產生KeyPair(public,private Key)
	 * @param algorithm 演算法
	 * @param keySize 密鑰大小
	 * @return KeyPair
	*/
	public java.security.KeyPair generateKeyPair(String algorithm, int keySize)  throws JBranchException{

		return keymanagement.generateKeyPair(algorithm, keySize);
	}

	/**
	 * 以LML解密Key值
	 * @param byte array 被wrap的key值
	 * @return
	*/
	public byte[] unWrapKey(String wrappedKey)throws JBranchException{

		return keymanagement.unWrapKey(wrappedKey);
	}

	/**
	 * 用LMK加密KEY值
	 * @param key 需要被加密的密鑰
	 * @return byte array 被加密後的key值
	*/
	public String wrapKey(java.security.Key key) throws JBranchException{

		return keymanagement.wrapKey(key);
	}

	/**
	 * 取得指定keyID的Key
	 * @param keyID
	 * @return SecretKey
	*/
	public javax.crypto.SecretKey getSecretKey(String keyID) throws JBranchException{

		return keymanagement.getSecretKey(keyID);
	}

	/**
	 * 刪除指定憑證
	 * @param certID
	*/
	public void deleteCertificate(String certID){

		keymanagement.deleteCertificate(certID);
	}

	/**
	 * 存入憑證
	 * @param certID 憑證ID
	 * @param cert 憑證
	*/
	public boolean addCertificate(String certID, java.security.cert.Certificate cert){

		return keymanagement.addCertificate(certID, cert);
	}

	/**
	 * 刪除KeyPair
	 * @param pairID
	 * @return 是否刪除成功
	*/
	public void deleteKeyPair(String pairID)  throws DAOException, JBranchException{

		keymanagement.deleteKeyPair(pairID);
	}

	/**
	 * 刪除KEY
	 * @param keyID
	 * @return 是否刪除成功
	*/
	public void deleteSecretKey(String keyID)  throws DAOException, JBranchException{

		keymanagement.deleteSecretKey(keyID);
	}

	/**
	 * 存入KeyPair
	 * @param pairID
	 * @parma keyPair
	*/
	public void addKeyPair(String pairID, java.security.KeyPair keyPair, String algorithm) throws DAOException, JBranchException{

		keymanagement.addKeyPair(pairID,keyPair,algorithm);
	}

	/**
	 * 存入SecretKey
	 * @param keyID
	 * @param secretKey
	 * @return 是否存入成功
	*/
	public void addSecretKey(String keyID, javax.crypto.SecretKey secretKey, String algorithm)  throws DAOException, JBranchException{

		keymanagement.addSecretKey(keyID,secretKey,algorithm);
	}

	/**
	 * 設定LMK
	 * @param lmk
	*/
	public void setLMK(javax.crypto.SecretKey lmk) throws JBranchException{

		keymanagement.setLMK(lmk);
	}

	public PublicKey generatePublicKey(byte[] keyValue, String algorithm) throws JBranchException{

		return keymanagement.generatePublicKey(keyValue,algorithm);
	}

	public PrivateKey generatePrivateKey(byte[] keyValue, String algorithm) throws JBranchException{

		return keymanagement.generatePrivateKey(keyValue,algorithm);
	}
}

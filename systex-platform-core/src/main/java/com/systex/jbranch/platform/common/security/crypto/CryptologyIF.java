package com.systex.jbranch.platform.common.security.crypto;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/** 
 * 加解密介面，規範了平台使用到的加解密功能介面
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public interface CryptologyIF {

    public static final String CRYPTOLOGY_ID = "cryptology";
	



	/**
	 * 非對稱式，以Public key加密
	 * @param pairID
	 * @param algorithm 演算法
	 * @param mode
	 * @param padding
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	 * @throws JBranchException 
	 * @throws DAOException 
	*/
	public byte[] asymmetricPublicEncrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException;

	/**
	 * 非對稱式，以Private key加密
	 * @param pairID
	 * @param algorithm 演算法
	 * @param mode
	 * @param padding
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	 * @throws JBranchException 
	 * @throws DAOException 
	*/
	public byte[] asymmetricPrivateEncrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException;
	
	/**
	 * 非對稱式，以Public keu解密
	 * @param pairID
	 * @param algorithm
	 * @param mode
	 * @param padding
	 * @param data 需要被解密的資料
	 * @throws JBranchException 
	 * @throws DAOException 
	 * @reutrn byte array, 解密後的資料 
	*/
	public byte[] asymmetricPublicDecrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException;


	/**
	 * 非對稱式，以Private key解密
	 * @param pairID
	 * @param algorithm
	 * @param mode
	 * @param padding
	 * @param data 需要被解密的資料
	 * @throws JBranchException 
	 * @throws DAOException 
	 * @reutrn byte array, 解密後的資料 
	*/
	public byte[] asymmetricPrivateDecrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException;
	
	/**
	 * 取MessageDigest
	 * @param algorithm 演算法
	 * @param data
	 * @return byte array  
	*/
	public byte[] getMsgDigest(String algorithm, byte[] data)  throws JBranchException;

	/**
	 * 對稱式解密
	 * @param keyID
	 * @param data 被加密的資料
	 * @return 解密後的資料 
	*/
	public byte[] symmetricDecrypt(String keyID, byte[] data)throws JBranchException;
	
	/**
	 * 使用預設對稱式key做加密
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] symmetricEncrypt(byte[] data)throws JBranchException;
	
	/**
	 * 使用預設對稱式key做解密
	 * @param data 被加密的資料
	 * @return 解密後的資料 
	*/
	public byte[] symmetricDecrypt(byte[] data)throws JBranchException;
	
	/**
	 * 對稱式加密
	 * @param keyID
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] symmetricEncrypt(String keyID, byte[] data)throws JBranchException;


	/**
	 * 非對稱式Public key加密
	 * @param pairID
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] asymmetricPublicEncrypt(String pairID, byte[] data)throws JBranchException;
	
	/**
	 * 非對稱式Private key加密
	 * @param pairID
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] asymmetricPrivateEncrypt(String pairID, byte[] data)throws JBranchException;

	/**
	 * 非對稱式Public key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPublicDecrypt(String pairID, byte[] data)throws JBranchException;
	
	/**
	 * 非對稱式Private key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPrivateDecrypt(String pairID, byte[] data)throws JBranchException;

	/**
	 * 使用預設非對稱式Public key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPublicDecrypt(byte[] data)throws JBranchException;
	
	/**
	 * 使用預設非對稱式Private key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPrivateDecrypt(byte[] data)throws JBranchException;

}

package com.systex.jbranch.platform.common.security.util;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.crypto.CryptologyIF;
import com.systex.jbranch.platform.common.security.impl.JBranchKeyManagement;
import com.systex.jbranch.platform.common.security.key.KeyManagementIF;
import com.systex.jbranch.platform.common.util.PlatformContext;

import java.security.Key;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import javax.crypto.SecretKey;

/** 
 * 加解密實際呼叫使用類別，可取得不同實作物件
 * @author Hong-jie
 * @version 1.0 2008/04/14
 */
public class CryptoUtil implements CryptologyIF{

	private CryptologyIF        cryptology;
	private static CryptoUtil   instance;
	
	/**
	 * 取得預設的CryptologyIF實體
	 * @return CryptoUtil object 
	*/
	public static CryptoUtil getInstance() throws JBranchException{
		if(instance == null)
			instance = new CryptoUtil();
		return instance;
	}

	
	private CryptoUtil() throws JBranchException{

		cryptology = (CryptologyIF) PlatformContext.getBean(CryptologyIF.CRYPTOLOGY_ID);
	}
	
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
	public byte[] asymmetricPublicEncrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException{

		return cryptology.asymmetricPublicEncrypt(pairID, algorithm, mode, padding, data);
	}

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
	public byte[] asymmetricPrivateEncrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException{

		return cryptology.asymmetricPrivateEncrypt(pairID, algorithm, mode, padding, data);
	}
	
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
	public byte[] asymmetricPublicDecrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException{

		return cryptology.asymmetricPublicDecrypt(pairID, algorithm, mode, padding, data);
	}


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
	public byte[] asymmetricPrivateDecrypt(String pairID, String algorithm, String mode, String padding, byte[] data) throws DAOException, JBranchException{

		return cryptology.asymmetricPrivateDecrypt(pairID, algorithm, mode, padding, data);
	}
	
	/**
	 * 取MessageDigest
	 * @param algorithm 演算法
	 * @param data
	 * @return byte array  
	*/
	public byte[] getMsgDigest(String algorithm, byte[] data)  throws JBranchException{

		return cryptology.getMsgDigest(algorithm, data);
	}

	/**
	 * 對稱式解密
	 * @param keyID
	 * @param data 被加密的資料
	 * @return 解密後的資料 
	*/
	public byte[] symmetricDecrypt(String keyID, byte[] data)throws JBranchException{

		return cryptology.symmetricDecrypt(keyID,data);
	}
	
	/**
	 * 使用預設對稱式key做加密
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] symmetricEncrypt(byte[] data)throws JBranchException{

		return cryptology.symmetricEncrypt(data);
	}
	
	/**
	 * 使用預設對稱式key做解密
	 * @param data 被加密的資料
	 * @return 解密後的資料 
	*/
	public byte[] symmetricDecrypt(byte[] data)throws JBranchException{

		return cryptology.symmetricDecrypt(data);
	}
	
	/**
	 * 對稱式加密
	 * @param keyID
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] symmetricEncrypt(String keyID, byte[] data)throws JBranchException{

		return cryptology.symmetricEncrypt(keyID,data);
	}


	/**
	 * 非對稱式Public key加密
	 * @param pairID
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] asymmetricPublicEncrypt(String pairID, byte[] data)throws JBranchException{

		return cryptology.asymmetricPublicEncrypt(pairID, data);
	}
	
	/**
	 * 非對稱式Private key加密
	 * @param pairID
	 * @param data 需要被加密的資料
	 * @return 加密後的資料 
	*/
	public byte[] asymmetricPrivateEncrypt(String pairID, byte[] data)throws JBranchException{

		return cryptology.asymmetricPrivateEncrypt(pairID, data);
	}

	/**
	 * 非對稱式Public key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPublicDecrypt(String pairID, byte[] data)throws JBranchException{

		return cryptology.asymmetricPublicDecrypt(pairID, data);
	}
	
	/**
	 * 非對稱式Private key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPrivateDecrypt(String pairID, byte[] data)throws JBranchException{

		return cryptology.asymmetricPrivateDecrypt(pairID, data);
	}

	/**
	 * 使用預設非對稱式Public key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPublicDecrypt(byte[] data)throws JBranchException{

		return cryptology.asymmetricPublicDecrypt(data);
	}
	
	/**
	 * 使用預設非對稱式Private key解密
	 * @param pairID
	 * @param data 被加密的資料
	 * @return 被解密的資料
	 *  
	*/
	public byte[] asymmetricPrivateDecrypt(byte[] data)throws JBranchException{

		return cryptology.asymmetricPrivateDecrypt(data);
	}
}

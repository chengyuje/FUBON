package com.systex.jbranch.platform.common.security;

/** 
 * 紀錄JBranchCryptology, JBranchKeyManagement類別所用到的常數
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class JBranchSecurityHelper {

public static final String MSG_DIGEST_ALGORITHM_SHA1 = "SHA-1";
public static final String MSG_DIGEST_ALGORITHM_SHA256 = "SHA-256";
public static final String MSG_DIGEST_ALGORITHM_SHA512 = "SHA-512";
public static final String MSG_DIGEST_ALGORITHM_MD2  = "MD2";
public static final String MSG_DIGEST_ALGORITHM_MD5  = "MD5";

public static final String CRYPTO_PADDING_PKCS5PADDING = "PKCS5Padding";
public static final String CRYPTO_PADDING_NOPADDING    = "NoPadding";
 
public static final String CRYPTO_ALGORITHM_PBE_WITH_MD5_AN_DDES = "PBEWithMD5AndDES";
public static final String CRYPTO_ALGORITHM_RSA                  = "RSA";
public static final String CRYPTO_ALGORITHM_TRIPLE_DES           = "DESede";
public static final String CRYPTO_ALGORITHM_DES                  = "DES";

public static final String CRYPTO_MODE_PCBC = "PCBC";
public static final String CRYPTO_MODE_OFB  = "OFB";
public static final String CRYPTO_MODE_CFB  = "CFB";
public static final String CRYPTO_MODE_CBC  = "CBC";
public static final String CRYPTO_MODE_ECB  = "ECB";

public static final String KEY_ID_AUTHUID_KEY = "authuidkey";

}

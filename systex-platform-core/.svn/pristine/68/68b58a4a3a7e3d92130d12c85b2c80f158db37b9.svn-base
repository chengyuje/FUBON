package com.systex.jbranch.platform.common.security.impl;


import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.util.ExceptionMessageUtil;
import com.systex.jbranch.platform.common.initiation.InitiatorIF;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecucryparaVO;
import com.systex.jbranch.platform.common.security.JBranchSecurityHelper;
import com.systex.jbranch.platform.common.security.key.KeyManagementIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

public class SecurityInitiator implements InitiatorIF {
// ------------------------------ FIELDS ------------------------------

    private byte[] parameterKey = new byte[]{7, 6, 5, 3, 4, 2, 1, 0};
    private IvParameterSpec piv = new IvParameterSpec(new byte[]{0, 1, 2, 4, 3, 5, 6, 7});
    private SecretKey s = new SecretKeySpec(parameterKey, JBranchSecurityHelper.CRYPTO_ALGORITHM_DES);
	private Logger logger = LoggerFactory.getLogger(SecurityInitiator.class);
    private int retryCount;
    private long interval;
    private DataAccessManager dataAccessManager;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface InitiatorIF ---------------------

    /**
     * 解密所有資料庫中的加解密參數，存至Map中，並初始化keymanagement
     */
    public void execute() throws Exception {
        int count = 0;
        while (count++ < retryCount || retryCount <= 0) {
            try {
                //解密所有資料庫中的加解密參數，存至Map中
                List<TbsyssecucryparaVO> voList = null;
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
                JBranchKeyManagement keymanagement = (JBranchKeyManagement) PlatformContext
                        .getBean(KeyManagementIF.KEY_MANAGEMENT_ID);
                keymanagement.loadKeyStore();
                keymanagement.loadKeys();
                break;
            }
            catch (Exception e) {
                if ("Connection refused: connect".equals(ExceptionMessageUtil.getNativeMessage(e))) {
                    logger.warn("Security Initiator failed");
                    logger.warn("waiting for [" + (int) (interval / 1000) + "] second retry[" + count + "]...");
                    Thread.sleep(interval);
                }
                else {
                    throw e;
                }
            }
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public String encryptParameter(byte[] para) throws Exception {
        byte[] value;
        Cipher desCipherDecrypt = Cipher.getInstance(JBranchSecurityHelper.CRYPTO_ALGORITHM_DES
                + "/"
                + JBranchSecurityHelper.CRYPTO_MODE_CBC
                + "/"
                + JBranchSecurityHelper.CRYPTO_PADDING_PKCS5PADDING);
        desCipherDecrypt.init(Cipher.DECRYPT_MODE, s, piv);
        value = desCipherDecrypt.doFinal(para);
        return new String(Hex.encodeHex(value));
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}

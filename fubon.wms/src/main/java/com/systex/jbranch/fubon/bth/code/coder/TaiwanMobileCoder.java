/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systex.jbranch.fubon.bth.code.coder;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * TaiwanMobileCoder's transformation is "AES/ECB/PKCS5Padding"
 *
 * 20190314 decode new String() 添加第二個參數指定編碼，否則採用 BatchServer 上的編碼會導致亂碼
 *
 * provide new constructor which offers second argument to appoint the charset
 *
 * @author Eli
 * @since 20180305
 */
public class TaiwanMobileCoder implements Coder {
    private final Cipher cipher;
    private final String key;
    private final String ALGORITHM = "AES";
    private Charset ENCODING = StandardCharsets.UTF_8; // default charset

    public TaiwanMobileCoder(String key) throws Exception {
        this.cipher = Cipher.getInstance(ALGORITHM);
        this.key = key;
    }

    public TaiwanMobileCoder(String key, Charset charset) throws Exception {
        this.cipher = Cipher.getInstance(ALGORITHM);
        this.key = key;
        this.ENCODING = charset;
    }

    @Override
    public StringBuffer encode(StringBuffer content) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, initSKeySpec(key));
        return new StringBuffer(
                Base64.encodeBase64String(
                        cipher.doFinal(content.toString().getBytes(ENCODING))));
    }

    @Override
    public StringBuffer decode(StringBuffer content) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, initSKeySpec(key));
        return new StringBuffer(
                new String(cipher.doFinal(Base64.decodeBase64(
                        content.toString().getBytes())), ENCODING));
    }

    private SecretKeySpec initSKeySpec(String inKey) {
        byte[] keyBytes = inKey.getBytes(ENCODING);
        byte[] keyBytes16 = Arrays.copyOf(keyBytes, Math.min(keyBytes.length, 16));
        return new SecretKeySpec(keyBytes16, ALGORITHM);
    }

}

package com.systex.jbranch.fubon.bth.code.coder;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PdfKeyCoder implements Coder {
    private final Cipher cipher;
    private final String key;
    private final String ALGORITHM = "AES";
    private Charset ENCODING = StandardCharsets.UTF_8;

    public PdfKeyCoder(String key) throws Exception {
        this.cipher = Cipher.getInstance(ALGORITHM);
        this.key = key;
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

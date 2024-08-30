package com.systex.jbranch.fubon.bth.code.service;

import com.systex.jbranch.fubon.bth.code.coder.Coder;
import com.systex.jbranch.fubon.bth.code.coder.TaiwanMobileCoder;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * 使用 TaiwanMobile 台灣大哥大的加解密方法
 */
@Service
public class TaiwanMobile extends CoderService {
    @Override
    public Coder provideCoder(String key, Charset charset) throws Exception {
        return new TaiwanMobileCoder(key, charset);
    }
}

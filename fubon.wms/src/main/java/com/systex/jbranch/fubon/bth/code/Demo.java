/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systex.jbranch.fubon.bth.code;

import java.nio.file.Paths;

import com.systex.jbranch.fubon.bth.code.coder.TaiwanMobileCoder;
import com.systex.jbranch.fubon.bth.code.coder.Coder;
import com.systex.jbranch.fubon.bth.code.config.FileCoderConfig;
import com.systex.jbranch.fubon.bth.code.service.FileCoder;

/**
 * Demo how to use the coder kit
 * @author Eli
 * @since 20180305
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        //encode
//        encode();
        //decode
        decode();
    }

    private static void encode() throws Exception {
        FileCoderConfig cfg = new FileCoderConfig();
        cfg.setCodeType(Coder.ENCODE);
        cfg.setSourcePath(Paths.get("D:\\testAES\\EimMember_201803020111.csv"));
        cfg.needBak(true);
        FileCoder.code(new TaiwanMobileCoder("2b51f2ae0b6d61ea"), cfg);
        System.out.println("End encode!");
    }

    private static void decode() throws Exception {
        FileCoderConfig cfg = new FileCoderConfig();
        cfg.setCodeType(Coder.DECODE);
//        cfg.setSourcePath(Paths.get("D:\\testAES\\EimMember_201803020111.csv"));
        cfg.setSourcePath(Paths.get("D:\\testAES\\EimMember_201803020111_aes.csv"));
        cfg.needBak(true);
        cfg.setTargetPath(Paths.get("D:\\testAES\\TWMB.csv"));
        cfg.setBakPath(Paths.get("D:\\testAES\\thisBak.csv"));
        FileCoder.code(new TaiwanMobileCoder("2b51f2ae0b6d61ea"), cfg);
        System.out.println("End decode!");
    }
}

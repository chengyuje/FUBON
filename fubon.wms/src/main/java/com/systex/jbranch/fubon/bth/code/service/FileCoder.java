/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systex.jbranch.fubon.bth.code.service;


import com.systex.jbranch.fubon.bth.code.coder.Coder;
import com.systex.jbranch.fubon.bth.code.config.FileCoderConfig;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import org.apache.commons.lang.ObjectUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * provide transform content of file with specific algorithm method
 * @author Eli
 * @since 20180305
 */
public class FileCoder {
    /**
     * Code file with specific coder
     * @param coder the method of decoding, implement of Coder interface
     * @param config  the data object of decoding information
     * @return config the modified FileCoderConfig
     */
    public static FileCoderConfig code(Coder coder, FileCoderConfig config) throws Exception {
        checkCoder(coder);
        checkConfig(config);
        backup(config);
        read(config);
        codeFile(coder, config);
        write(config);
        delete(config);
        return config;
    }

    /**check config whether enough*/
    private static void checkConfig(FileCoderConfig config) {
        if (config == null) throw new RuntimeException("config is not setting!");
        if (config.getCodeType() == null) throw new RuntimeException("codeType must to set!");
        if (config.getSourcePath() == null ) throw new RuntimeException("source file is not selected!");
        if (config.getSourcePath().equals(config.getBakPath())) 
            throw new RuntimeException("backup path is same as source path!");
        if (config.getTargetPath() != null) {
              if (config.getTargetPath().equals(config.getBakPath())) 
                  throw new RuntimeException("backup path is same as target path!");
              if (config.getTargetPath().equals(config.getSourcePath())) 
                  throw new RuntimeException("target path is same as source path!");
        }
    }
    /**if source file need to backup, execute backup*/
    private static void backup(FileCoderConfig config) throws IOException {
        if (!config.isNeedBak()) return;
        if (config.getBakPath() != null) Files.copy(config.getSourcePath(), config.getBakPath());
        else Files.copy(config.getSourcePath(), config.getDefalutBakPath());
    }

    /**check decoder injection*/
    private static void checkCoder(Coder coder) {
        if (coder == null) throw new RuntimeException("coder is not setting!");
    }

    /**get source file content*/
    private static void read(FileCoderConfig config) throws IOException {
        try (
            BufferedReader read = Files.newBufferedReader(config.getSourcePath(), getCharset(config))
        ) {
            String str;
            while ((str = read.readLine()) != null)
                config.getOriginalContent().append(str + System.getProperty("line.separator"));
        }
    }

    /** if config's charset is null, use the default charset UTF-8 **/
    private static Charset getCharset(FileCoderConfig config) {
        return (Charset) ObjectUtils.defaultIfNull(config.getCharset(), StandardCharsets.UTF_8);
    }

    /**use specific coder to transform content of source file*/
    private static void codeFile(Coder coder, FileCoderConfig config) throws Exception {
        if (isEncode(config)) 
            config.setEncodedContent(
                    coder.encode(config.getOriginalContent()));
        else config.setDecodedContent(
                coder.decode(config.getOriginalContent()));
    }
    
    /**return true when code type is encode*/
    private static boolean isEncode(FileCoderConfig config) {
        return config.getCodeType().equals(Coder.ENCODE);
    }

    /**write the coded content to file*/
    private static void write(FileCoderConfig config) throws IOException {
        try (
                BufferedWriter writer = getBufferedWriter(config)
        ) {
            if (isEncode(config)) 
               writer.write(config.getEncodedContent().toString());
            else 
               writer.write(config.getDecodedContent().toString());
            
            writer.flush();
        }
    }

    /** if config's charset is null, use the UTF-8 (include BOM) as the charset of BufferedWriter **/
    private static BufferedWriter getBufferedWriter(FileCoderConfig config) throws IOException {
        Path writePath = getWritePath(config);
        if (config.getCharset() == null)
            return AccessContext.getBomBufferedWriter(writePath);
        return Files.newBufferedWriter(writePath, config.getCharset());
    }

    /**get the writing path*/
    private static Path getWritePath(FileCoderConfig config) {
        if (config.getTargetPath() != null) return config.getTargetPath();
        else return config.getSourcePath();
    }

    /**delete source file if fit the logic*/
    private static void delete(FileCoderConfig config) throws IOException {
        if (isDelete(config)) return;
        Files.deleteIfExists(config.getSourcePath());
    }

    /**delete logic*/
    private static boolean isDelete(FileCoderConfig config) {
        return config.getTargetPath() == null;
    }
   
}

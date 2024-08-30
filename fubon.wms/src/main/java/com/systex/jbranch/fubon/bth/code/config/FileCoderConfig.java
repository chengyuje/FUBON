/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systex.jbranch.fubon.bth.code.config;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The class is the parameter for {@link FileCoder#code(Coder, FileCoderConfig)}
 * coderType、sourcePath are both required to be used
 * targetPath is optional, it can let user appoint specific path to store the coded file
 * bakPath is optional, it can let user appoint specific path to backup the source file
 * (default path is same directory of source file and extend is .ORGBAK)
 * needBak default false, if want to backup, set the property true
 * originalContent is the stored place of original source file content
 * decodedContent is the stored place of decoding content
 * encodedContent is the stored place of encoding content
 * charset is optional, it can determine the charset of file
 * @author Eli
 * @since 20180305
 * 
 */
public class FileCoderConfig {
    private static final String D_EXT = ".ORGBAK";
    private Path sourcePath;
    private Path targetPath;
    private Path bakPath;
    private boolean needBak;
    private String codeType;
    private StringBuffer originalContent = new StringBuffer();
    private StringBuffer decodedContent = new StringBuffer();
    private StringBuffer encodedContent = new StringBuffer();
    private Charset charset;

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    public Path getBakPath() {
        return bakPath;
    }

    public void setBakPath(Path bakPath) {
        this.bakPath = bakPath;
    }
    
    public Path getDefalutBakPath () {
        if (sourcePath != null) return Paths.get(sourcePath.toString() + D_EXT);
        else return null;
    }
    
    public boolean isNeedBak() {
        return needBak;
    }

    public void needBak(boolean needBak) {
        this.needBak = needBak;
    }

    public StringBuffer getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(StringBuffer originalContent) {
        this.originalContent = originalContent;
    }

    public StringBuffer getDecodedContent() {
        return decodedContent;
    }

    public void setDecodedContent(StringBuffer decodedContent) {
        this.decodedContent = decodedContent;
    }

    public StringBuffer getEncodedContent() {
        return encodedContent;
    }

    public void setEncodedContent(StringBuffer encodedContent) {
        this.encodedContent = encodedContent;
    }
    
    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}

package com.systex.jbranch.fubon.commons;

import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;

public interface PdfFunction {
    void config(PdfEncryptionSettings pdf) throws Exception;
}

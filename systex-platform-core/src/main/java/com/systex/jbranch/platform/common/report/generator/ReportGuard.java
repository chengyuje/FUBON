package com.systex.jbranch.platform.common.report.generator;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ReportGuard extends Thread {
// ------------------------------ FIELDS ------------------------------

    String reportPath = null;
    String encriptPath = null;
	private Logger logger = LoggerFactory.getLogger(ReportGuard.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public ReportGuard() {
    }

    ReportGuard(String reportPath, String encriptPath) {
        this.reportPath = reportPath;
        this.encriptPath = encriptPath;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Runnable ---------------------

    @Override
    public void run() {
        encryptReport(reportPath, encriptPath);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * 解密報表檔
     *
     * @param inputReportPath input
     * @param outReportPath   output
     * @author Richard
     * @since 2009/06/03
     */
    public void decryptReport(String inputReportPath, String outReportPath) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(inputReportPath));
            byte[] fileArray = new byte[in.available()];
            in.read(fileArray);

            try {
                fileArray = CryptoUtil.getInstance().symmetricDecrypt(fileArray);
            }
            catch (JBranchException e) {
                logger.error(e.getMessage(), e);
            }

            out = new BufferedOutputStream(new FileOutputStream(outReportPath));
            out.write(fileArray);
            out.flush();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            try {
                in.close();
            }
            catch (Exception e) {
            }
            try {
                out.close();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 加密報表檔
     *
     * @param inputReportPath input
     * @param outReportPath   output
     * @author Richard
     * @since 2009/06/03
     */
    public void encryptReport(String inputReportPath, String outReportPath) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(inputReportPath));
            byte[] fileArray = new byte[in.available()];
            in.read(fileArray);

            try {
                fileArray = CryptoUtil.getInstance().symmetricEncrypt(fileArray);
            }
            catch (JBranchException e) {
                logger.error(e.getMessage(), e);
            }

            out = new BufferedOutputStream(new FileOutputStream(outReportPath));
            out.write(fileArray);
            out.flush();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            try {
                in.close();
            }
            catch (Exception e) {
            }
            try {
                out.close();
            }
            catch (Exception e) {
            }
        }
    }
}

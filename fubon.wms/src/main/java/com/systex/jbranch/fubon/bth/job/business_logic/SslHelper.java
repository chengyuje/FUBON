package com.systex.jbranch.fubon.bth.job.business_logic;


import javax.net.ssl.*;

/**
 * 忽略 SSL 憑證邏輯
 *
 * @author Eli
 * @date 20190503
 */
public class SslHelper {
    /**
     * 忽略 HTTPS 請求的 SSL 憑證，需在開啟連線時調用此方法
     */
    public static void ignoreVerify() throws Exception {
        ignoreVerifyHttpsTrustManager();
        ignoreVerifyHttpsHostName();
    }

    /**
     * 忽略驗證 https
     */
    private static void ignoreVerifyHttpsHostName() {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    /**
     * 忽略驗證 https
     */
    private static void ignoreVerifyHttpsTrustManager() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}


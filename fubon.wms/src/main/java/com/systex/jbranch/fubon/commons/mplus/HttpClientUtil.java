package com.systex.jbranch.fubon.commons.mplus;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    public static final String TLSv1_2 = "TLSv1.2";

    public static CloseableHttpClient getTrustAnyHttpClient(String type) throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = getTrustManagers();

        SSLContext sc = SSLContext.getInstance(type);
        sc.init(null, trustAllCerts, new java.security.SecureRandom());

        return HttpClientBuilder
                .create()
                // 憑證會有時效性問題，為了避免需要關注時刻憑證是否過期，決議使用忽略所有憑證的作法
                .setSSLHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String urlHostName, SSLSession session) {
                        logger.info("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                        return true;
                    }
                })
                .setSSLContext(sc)
                .build();
    }

    private static TrustManager[] getTrustManagers() {
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
        return trustAllCerts;
    }
}

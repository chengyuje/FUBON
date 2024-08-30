package com.systex.jbranch.app.server.fps.sot815;

import com.lowagie.text.DocumentException;
import com.systex.jbranch.fubon.bth.job.business_logic.UrlHelper;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SOT815MoneyDJ {
    private static final Logger logger = LoggerFactory.getLogger(SOT815MoneyDJ.class);
    private final CloseableHttpClient httpClient;

    public SOT815MoneyDJ () throws NoSuchAlgorithmException, KeyManagementException {
        this.httpClient = getCustomHttpClient();
    }

    /**
     * 富邦資訊的封面 + 嘉實下載的後收級別費用結構聲明書
     * @param prodId 商品代號
     */
    public List<String> printReport(String prodId, String fundCName) throws JBranchException, DocumentException, IOException {
        List<String> pdfs = new ArrayList<>();
        List<String> tempFiles = new ArrayList<>();

        try {
            String backendPdf = downloadFileFromMoneydj(prodId);
            tempFiles.add(backendPdf);

            PdfHelper helper = new PdfHelper();

            String part1Cover = generateCV("1", fundCName);
            tempFiles.add(part1Cover);
            pdfs.add(helper.mergePdf(part1Cover, backendPdf));

            String part2Cover = generateCV("2", fundCName);
            tempFiles.add(part2Cover);
            pdfs.add(helper.mergePdf(part2Cover, backendPdf));
        } finally {
            deleteFiles(tempFiles);
        }
        return pdfs;
    }

    /** 除了合併出來的報表外，其餘檔案均刪除 **/
    private void deleteFiles(List<String> tempFiles) {
        for (String pdfUrl : tempFiles) {
            try {
                Files.deleteIfExists(Paths.get((String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH), pdfUrl));
                logger.info(pdfUrl + " 刪除成功！ ");
            } catch (IOException | JBranchException e) {
                logger.info(pdfUrl + " 刪除失敗： " + e.getMessage());
            }
        }
    }

    /** 從嘉實下載的 PDF 前面會附上一個富邦相關資料的封面 **/
    private String generateCV(String part, String fundCName) throws JBranchException {
        ReportDataIF data = new ReportData();
        data.addParameter("Part", part);
        data.addParameter("FundCName", fundCName);

        return ReportFactory
                .getGenerator()
                .generateReport("SOT815", "CV", data)
                .getLocation();
    }

    public static void main(String[] args) throws IOException, JDOMException {
        /*
          BN01: 駿利亨德森資產管理基金 - 駿利亨德森高收益基金
          BN05: 駿利亨德森資產管理基金-駿利亨德森靈活入息基金V3月配美元

          5901: 聯博-國際科技基金A級別美元（查無該基金[後收級別費用結構聲明書]可供下載）

          **/
        String prdID = "";
        try {
            prdID = "BN01";
            new SOT815MoneyDJ().downloadFileFromMoneydj(prdID, Paths.get("D:\\test\\嘉實下載的後收報表測試.pdf"));
        } catch (Exception e) {
            System.out.println("商品代號：" + prdID + "，" + e.getMessage());
            e.printStackTrace();
            System.out.println("查無該基金[後收級別費用結構聲明書]可供下載");
        }
    }

    public String downloadFileFromMoneydj(String prdID) throws JBranchException, IOException {
        String root = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
        String report = "temp" + File.separator + "reports" + File.separator + UUID.randomUUID() + ".pdf";

        downloadFileFromMoneydj(prdID, Paths.get(root, report));

        return report;
    }

    /**
     * 從嘉實系統下載境外後收基金 PDF
     * 嘉實提供的網址規則為
     * http://fund.taipeifubon.com.tw/w/CustMap/CustFundIDMap.djhtm?A=北富銀境外後收級別基金代碼&DownFile=8
     * <p>
     * 實際會跳轉到兩次，最後目的可以為：
     * https://fundreport.moneydj.com/FileOpen.aspx（開啟分頁）
     * https://fundreport.moneydj.com/FileDownload.aspx（下載檔案）
     * <p>
     * 這裡將利用 Apache HttpClient 呼叫到最終目的地 FileDownload.aspx 以下載 PDF 檔案
     *
     * @param prdID    後收基金商品代號
     * @param filePath PDF 檔案路徑
     */
    public void downloadFileFromMoneydj(String prdID, Path filePath) throws IOException {
        // 初始路徑
        String initialUrl = "http://fund.taipeifubon.com.tw/w/CustMap/CustFundIDMap.djhtm?A=" + prdID + "&DownFile=8";

        // 取得第一次跳轉的 HTML，並且擷取轉址路徑
        String url = extract1st(initialUrl);

        // 取得第二次跳轉的 HTML，並且擷取 Form 的相關參數
        UrlEncodedFormEntity formPairs = extract2nd(url);

        // 將 Form 參數 post 到 moneydj 以下載 PDF
        postToDownload(formPairs, filePath);
    }

    private void postToDownload(UrlEncodedFormEntity formPairs, Path filePath) throws IOException {
        HttpPost httpPost = new HttpPost("https://fundreport.moneydj.com/FileDownload.aspx");
        httpPost.setEntity(formPairs);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity httpEntity = response.getEntity();
            UrlHelper.download(httpEntity.getContent(), Files.newOutputStream(filePath));
            EntityUtils.consume(httpEntity);
        }
    }

    private UrlEncodedFormEntity extract2nd(String url2nd) throws IOException {
        HttpGet httpGet = new HttpGet(url2nd);

        String html = "";
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity httpEntity = response.getEntity();
            html = EntityUtils.toString(httpEntity, "UTF-8");

            /*
              第二次返回的內容類似如下，在這裡擷取 Form 的輸入參數
              <form id="form1" name="form1" action="FileDownload.aspx" method="post"><input type="hidden" id="aa" value=Fundcharge%5C07%2DJAZA9%2D20210401%2Epdf name="aa"><input type="hidden" id="bb" value=%C2%40%A7Q%A6%EB%BCw%B4%CB%B8%EA%B2%A3%BA%DE%B2z%B0%F2%AA%F7+%2D+%C2%40%A7Q%A6%EB%BCw%B4%CB%C6F%AC%A1%A4J%AE%A7%B0%F2%AA%F7+V3+%A4%EB%B0t+%AC%FC%A4%B8%2D%AB%E1%A6%AC%AF%C5%A7O%B6O%A5%CE%B5%B2%BAc%C1n%A9%FA%AE%D1%2Epdf name="bb"></form>
              <SCRIPT LANGUAGE=javascript>
              <!--
              form1.submit();
              //-->
              </SCRIPT>
             */
            List<NameValuePair> pairs = new ArrayList<>();
            // 第一個 input 參數
            int value1ndIndex = html.indexOf("value=");
            int name2ndIndex = html.indexOf(" name=", html.indexOf(" name=") + 6);
            pairs.add(new BasicNameValuePair("aa", html.substring(value1ndIndex + 6, name2ndIndex)));

            // 第二個 input 參數
            int value2ndIndex = html.lastIndexOf("value=");
            int name3rdIndex = html.lastIndexOf(" name=");
            pairs.add(new BasicNameValuePair("bb", html.substring(value2ndIndex + 6, name3rdIndex)));

            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(httpEntity);

            return new UrlEncodedFormEntity(pairs, "UTF-8");
        } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            throw new StringIndexOutOfBoundsException("第二次解析網頁發生錯誤，網頁內容：\n" + html);
        }
    }

    private String extract1st(String initialUrl) throws IOException {
        HttpGet httpGet = new HttpGet(initialUrl);
        String html1st = "";
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity httpEntity = response.getEntity();
            html1st = EntityUtils.toString(httpEntity, "UTF-8");

            /*
              第一次返回的內容類似如下，在這裡擷取轉址的 URL
              <SCRIPT LANGUAGE=javascript><!--
                   document.location = 'https://fundreport.moneydj.com/GetFundInfo.asp?A=JAZA9&b=7&c=406';
              //--></SCRIPT>
             */
            html1st = html1st.substring(html1st.indexOf("https"), html1st.lastIndexOf("'"));
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(httpEntity);
            return html1st;
        } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            throw new StringIndexOutOfBoundsException("第一次解析網頁發生錯誤，網頁內容：\n" + html1st);
        }
    }

    private CloseableHttpClient getCustomHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = getTrustManagers();

        SSLContext sc = SSLContext.getInstance("SSL");
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

    private TrustManager[] getTrustManagers() {
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

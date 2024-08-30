package com.systex.jbranch.fubon.bth.job.business_logic;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 提供 Url 相關方法以及服務
 *
 * @author Eli
 * @date 20190425
 */
@Scope
@Component
public class UrlHelper extends BizLogic {
    static {
        try {
            /** Server 一開啟就自動忽略所有 SSL 憑證 **/
            SslHelper.ignoreVerify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批次服務
     **/
    public void downloadFileFromUrl(Object body, IPrimitiveMap<?> header) throws JBranchException {
        Map<String, String> inputMap = ((Map<String, Map>) body).get(SchedulerHelper.JOB_PARAMETER_KEY);

        List<Map> urls = exeQueryForQcf(genDefaultQueryConditionIF()
                .setQueryString("select URL, FILE_NAME from TBSYS_URL_MAPPING where CODE = :code")
                .setObject("code", inputMap.get("arg")));

        if (CollectionUtils.isNotEmpty(urls)) {
            StringBuilder errMsg = new StringBuilder();
            for (Map<String, String> each : urls) {
                try {
                    downloadFileFromUrl(each.get("URL"), AccessContext.tempReportPath, each.get("FILE_NAME"));
                } catch (Exception e) {
                    errMsg.append(e.getMessage() + "\n");
                }
            }
            if (StringUtils.isNotBlank(errMsg.toString()))
                throw new JBranchException(errMsg.toString());
        }
    }

    /**
     * 從 Url 下載檔案
     **/
    public static void downloadFileFromUrl(String url, String targetDir, String targetName) throws IOException {
        String fileName = getChildUrl(url);
        String encUrl = url.replace(fileName, "") + getEncodeUrl(fileName);

        /** 取得指定網址的連線物件並連線 **/
        URL fileUrl = new URL(encUrl);
        HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();

        /** 設定輸出檔案 **/
        Path target = Paths.get(targetDir).resolve(null == targetName ? fileName : targetName);
        download(conn.getInputStream(), Files.newOutputStream(target));
        conn.disconnect();
    }

    /**
     * 下載
     **/
    public static void download(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buf = new byte[1024];
        int length;
        try (BufferedOutputStream bos = new BufferedOutputStream(outputStream);
             BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            while ((length = bis.read(buf)) != -1)
                bos.write(buf, 0, length);
        }
    }

    /**
     * 取 Url 最後一段
     **/
    public static String getChildUrl(String url) {
        return url.replaceAll(".+/", "");
    }

    /**
     * percent-encode URL
     **/
    public static String getEncodeUrl(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "UTF-8");
    }
}

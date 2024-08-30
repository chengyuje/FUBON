package com.systex.jbranch.app.server.fps.sot815;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

public class PdfHelper {
    /**
     * server path
     **/
    private String root;

    public String mergePdf(String... pdfUrls) throws DocumentException, IOException, JBranchException {
        return mergePdf(Arrays.asList(pdfUrls));
    }

    /**
     * 因需求：後收境外 PDF 與富邦封面使用 PdfUtil.mergePDF 合併，之後再次使用 PdfUtil.mergePDF 合併會發生錯誤訊息：
     * 「PDF header signature not found.」
     * 所以建立該方法以達成合併需求
     * <p>
     * 合併 PDF
     *
     * @param urlList 欲合併的報表 Url List
     * @return
     * @throws DocumentException
     * @throws IOException
     * @throws JBranchException
     */
    public String mergePdf(List<String> urlList) throws DocumentException, IOException, JBranchException {
        setRoot((String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH));

        String mergedUrl = "temp" + File.separator + "reports" + File.separator + UUID.randomUUID() + ".pdf";
        PdfWriter writer = null;
        Document document = new Document(PageSize.A4,0,0,0,0);

        try {
            writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(getRoot(), mergedUrl)));
            document.open();
            for (int index = 0; index < urlList.size(); index++) {
                String pdfUrl = urlList.get(index);

                if (pdfUrl == null) continue;

                paste(writer, document, pdfUrl);

                // 最後的 PDF 不需要再另開新頁
                if (index < urlList.size() - 1)
                    document.newPage();
            }

            return mergedUrl;
        } finally {
            document.close();
            if (writer != null) writer.close();
        }
    }

    /**
     * 將報表 pdfUrl paste 到 writer
     *
     * edit : #1240: 結構聲明書版型調整  Sam Tu 2022.08.15
     * edit : #1927: 結構聲明書排版調整  Sam Tu 2024.03.18
     * Document document = new Document(PageSize.A4,0,0,0,0); 預設情況定位在左上角
     * @param writer
     * @param document
     * @param pdfUrl
     * @throws IOException
     * @throws DocumentException 
     */
    private void paste(PdfWriter writer, Document document, String pdfUrl) throws IOException, DocumentException {
        PdfReader reader = null;

        try {
            reader = new PdfReader(Files.newInputStream(Paths.get(getRoot(), pdfUrl)));
            int pages = reader.getNumberOfPages();
            for (int pageNumber = 1; pageNumber <= pages; pageNumber++) {
                // 讀取既有的 PDF 內容並添加
                PdfImportedPage page = writer.getImportedPage(reader, pageNumber);
                //writer.getDirectContent().addTemplate(page, 0, 0);
                Image image = Image.getInstance(page);
                float scaledWidth = image.getScaledWidth();
                float scaledHeight = image.getScaledHeight();
                //System.out.println("--原先PFD大小是寬："+scaledWidth+",高是:"+scaledHeight);
                
                float m = 0;
                while (scaledWidth > 600 || scaledHeight > 847 ) { // image A4尺寸是 595*842  PDF通常會留邊所以抓大一點
                    image.scalePercent(100-m);
                    m = m + 0.5f;
                    scaledHeight = image.getScaledHeight();
                    scaledWidth = image.getScaledWidth();
                }
               // System.out.println("--處理後PFD大小是寬："+scaledWidth+",高是:"+scaledHeight);

                image.setAlignment(Image.ALIGN_CENTER);
                document.add(image);
                // 避免最後面多一頁空白頁
                if (pageNumber < pages) document.newPage();
            }
        } finally {
            if (reader != null) reader.close();
        }
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}

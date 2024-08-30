package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("btiot_trspdf")
@Scope("prototype")
public class BTIOT_TRSPDF extends BizLogic {
	/** 图片格式 */
    public static final String IMG_FORMAT = "tiff";
    /** 打印精度设置 */
    public static final int DPI = 300; //图片的像素
    
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void onetime(Object body, IPrimitiveMap<?> header) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String yyyyMMdd = sdf.format(new Date());
		
		// 根據以下SQL產生 XML資料 及 TIFF檔
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT B.CUST_ID, A.APPLY_DATE AS IOT_VALIDDATE, B.INS_ID, C.PDF_FILE ");
		sb.append(" FROM TBIOT_TRSTOPDF_ONETIME A INNER JOIN TBIOT_MAIN B ON A.INS_ID = B.INS_ID ");
		sb.append(" INNER JOIN TBIOT_MAPP_PDF C ON A.CASE_ID = C.CASE_ID ");
		sb.append(" INNER JOIN TBIOT_BATCH_INFO D ON D.BATCH_INFO_KEYNO = B.BATCH_INFO_KEYNO ");
		sb.append(" WHERE D.OP_BATCH_NO LIKE 'S%' ");
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports" + File.separator;
		int i = 0;
		for (Map<String, Object> map : list) {
			// 每個保險文件編號都要有一個『保險文件編號_要保書申請日.zip』ex.70423A001_20240523.zip，裡面放：保險文件編號.xml、保險文件編號.tiff
			// 檔名為保險文件編號
			String ins_id = map.get("INS_ID").toString().toUpperCase();				// 保險文件編號 
			String fileName = ins_id + "_" + map.get("IOT_VALIDDATE").toString();	// 保險文件編號_要保書申請日
			String xmlName = ins_id + ".xml";	// 保險文件編號.xml
			String pdfName = ins_id + ".pdf";	// 保險文件編號.pdf
			
			// 1. 建一個 INS_ID_IOT_VALIDDATE (保險文件編號_要保書申請日 ex.70423A001_20240523)資料夾
			// 若此目錄不存在，則建立之 (裡面放：ins_id.xml、ins_id.tiff)
			String filePath = tempPath + fileName;
			File path = new File(filePath); 
			if (!path.exists()) {
				path.mkdir();
			}
			
			File xmlFile = new File(filePath + File.separator + xmlName);
			//若檔案已存在則刪除重新產生
			if(xmlFile.exists()) xmlFile.delete();
			xmlFile.createNewFile();
			
			// 2. 先將 XML 放至：保險文件編號_要保書申請日 ex.70423A001_20240523
			// 將名單寫到檔案
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile, true), "UTF-8"));	// UTF-8編碼
			
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.append("\r\n");
			writer.append("<indexattribute>");
			writer.append("\r\n");
			writer.append("<id>" + map.get("CUST_ID") + "</id>");
			writer.append("\r\n");
			writer.append("<validdate>" + map.get("IOT_VALIDDATE") + "</validdate>");
			writer.append("\r\n");
			writer.append("<cBarcode>52-1001-99</cBarcode>");
			writer.append("\r\n");
			writer.append("<accno>" + map.get("INS_ID") + "</accno>");
			writer.append("\r\n");
			writer.append("</indexattribute>");
			
			writer.close();
			i++;
			
			// 下載PDF
			if (null != map.get("PDF_FILE")) {
				Blob blob = (Blob) map.get("PDF_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				
				String pdfFilePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports";
				File targetFile = new File(pdfFilePath, pdfName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
				// 3. 將 PDF 轉 TIFF 後放至：保險文件編號_要保書申請日 ex.70423A001_20240523
				String pdfPath  = pdfFilePath + File.separator + pdfName;
				// pdf 轉 tiff
				// 參數：pdf目錄(含.pdf)、結果目錄
				this.PdfToTiffConverter(pdfPath, filePath);
				
				// 4. 建一個 YYYYMMDD (系統日期)資料夾
				String targetPath = tempPath + yyyyMMdd;
				File tPath = new File(targetPath); 
				// 若此目錄不存在，則建立之
				if (!tPath.exists()) {
					tPath.mkdir();
				}
				// 5. 壓縮 70423A001_20240523 放至：YYYYMMDD (系統日期)
				// xml、tiff 產生完畢打包成一個 INS_ID_IOT_VALIDDATE.zip檔
				// 參數：要壓縮的檔案路徑, 壓縮至何處, 壓縮檔名稱
				this.zipFiles(filePath, targetPath, fileName);
			}
			i++;
		}
		// 5. 壓縮 YYYYMMDD (系統日期)
		// 產生完畢打包成一個YYYYMMDD.zip檔
		String sourcePath = tempPath + yyyyMMdd;
		// 參數：要壓縮的檔案路徑, 壓縮至何處, 壓縮檔名稱
		this.zipFiles(sourcePath, "D:\\INS_PDF", yyyyMMdd);
//		this.zipFiles(sourcePath, tempPath, yyyyMMdd);
	}
	
	public void daily(Object body, IPrimitiveMap<?> header) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String yyyyMMdd = sdf.format(new Date());
		// 根據以下SQL產生 XML資料 及 TIFF檔
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT B.CUST_ID, TO_CHAR(B.APPLY_DATE, 'YYYYMMDD') as IOT_VALIDDATE, B.INS_ID, C.PDF_FILE ");
		sb.append(" FROM TBIOT_MAIN B ");
		sb.append(" INNER JOIN TBIOT_MAPP_PDF C ON B.CASE_ID = C.CASE_ID ");
		sb.append(" INNER JOIN TBIOT_BATCH_INFO D ON D.BATCH_INFO_KEYNO = B.BATCH_INFO_KEYNO ");
		sb.append(" WHERE D.OP_BATCH_NO LIKE 'S%' ");
		sb.append(" AND TO_CHAR(AFT_SIGN_DATE, 'YYYYMMDD') = TO_CHAR(SYSDATE, 'YYYYMMDD') ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports" + File.separator;
		int i = 0;
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				// 每個保險文件編號都要有一個『保險文件編號_要保書申請日.zip』ex.70423A001_20240523.zip，裡面放：保險文件編號.xml、保險文件編號.tiff
				// 檔名為保險文件編號
				String ins_id = map.get("INS_ID").toString().toUpperCase();
				String fileName = ins_id + "_" + map.get("IOT_VALIDDATE").toString();
				String xmlName = ins_id + ".xml";
				String pdfName = ins_id + ".pdf";
				
				// 1. 建一個 INS_ID_IOT_VALIDDATE (保險文件編號_要保書申請日 ex.70423A001_20240523)資料夾
				// 若此目錄不存在，則建立之 (裡面放：ins_id.xml、ins_id.tiff)
				String filePath = tempPath + fileName;
				File path = new File(filePath); 
				if (!path.exists()) {
					path.mkdir();
				}
				
				File xmlFile = new File(filePath + File.separator + xmlName);
				//若檔案已存在則刪除重新產生
				if (xmlFile.exists()) xmlFile.delete();
				xmlFile.createNewFile();
				
				// 2. 先將 XML 放至：保險文件編號_要保書申請日資料夾中 ex.70423A001_20240523
				// 將名單寫到檔案
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile, true), "UTF-8"));	// UTF-8編碼
				
				writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				writer.append("\r\n");
				writer.append("<indexattribute>");
				writer.append("\r\n");
				writer.append("<id>" + map.get("CUST_ID") + "</id>");
				writer.append("\r\n");
				writer.append("<validdate>" + map.get("IOT_VALIDDATE") + "</validdate>");
				writer.append("\r\n");
				writer.append("<cBarcode>52-1001-99</cBarcode>");
				writer.append("\r\n");
				writer.append("<accno>" + map.get("INS_ID") + "</accno>");
				writer.append("\r\n");
				writer.append("</indexattribute>");
				writer.close();
				
				// 下載PDF
				if (null != map.get("PDF_FILE")) {
					Blob blob = (Blob) map.get("PDF_FILE");
					int blobLength = (int) blob.length();
					byte[] blobAsBytes = blob.getBytes(1, blobLength);
					
					String pdfFilePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports";
					File targetFile = new File(pdfFilePath, pdfName);
					FileOutputStream fos = new FileOutputStream(targetFile);
					fos.write(blobAsBytes);
					fos.close();
					
					// 3. 將 PDF 轉 TIFF 後放至：保險文件編號_要保書申請日 ex.70423A001_20240523
					String pdfPath  = pdfFilePath + File.separator + pdfName;
					// pdf 轉 tiff
					// 參數：pdf目錄(含.pdf)、結果目錄
					this.PdfToTiffConverter(pdfPath, filePath);
					
					// 4. 壓縮 70423A001_20240523 放至：D:\\INS_PDF
					// xml、tiff 產生完畢打包成一個 [INS_ID]_[IOT_VALIDDATE].zip檔
					// 參數：要壓縮的檔案路徑, 壓縮至何處, 壓縮檔名稱
					this.zipFiles(filePath, "D:\\INS_PDF", fileName);
				}
				i++;
			}
		}
	}
	
	public void PdfToTiffConverter(String pdfFilePath, String tiffFilePath) {
		String dpi = "150";  // the resolution of the output image in DPI
		
		File finalFile = new File(tiffFilePath); 
		// 若此目錄不存在，則建立之
		if (!finalFile.exists()) {
			finalFile.mkdir();
		}
		Process process = null;
		try {
            ProcessBuilder pb = new ProcessBuilder(
            	"C:\\Program Files\\ImageMagick-7.1.1-Q16-HDRI\\magick.exe", 
                "-density", dpi,
                pdfFilePath,
                "-background", "white", "-alpha", "remove",
                "-monochrome", "-compress", "lzw", "-scene", "1",
                tiffFilePath + File.separator +  "%03d.tiff"
            );
            process = pb.start();
            process.waitFor();
            System.out.println("Image conversion completed.");
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
        	process.destroy();
        }
	}
	
	/*
	 * 將產出的未符合檔案變成一個zip檔
	 */
	public void zipFiles(String sourcePath, String targetPath, String zipName) throws Exception {
		// 建立檔案輸出流
		FileOutputStream fos = new FileOutputStream(targetPath + File.separator + zipName + ".zip"); // 輸出檔名
		ZipOutputStream zipOut = new ZipOutputStream(fos); // 用檔案輸出流建立出 Zip 輸出流

		File folder = new File(sourcePath);
		String[] list = folder.list();
//		System.out.println("list的長度" + list.length);
		for(String name : list) {
			byte[] str2Bytes = Files.readAllBytes(Paths.get(sourcePath + File.separator + name)); // 檔案轉為 Bytes
			zipOut.putNextEntry(new ZipEntry(name));
			zipOut.write(str2Bytes);
		}
		zipOut.close();
		fos.close();
	}
}

/**
 *
 */
package com.systex.jbranch.fubon.commons;

import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.systex.jbranch.fubon.bth.code.coder.Coder;
import com.systex.jbranch.fubon.bth.code.coder.PdfKeyCoder;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PathUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

/**
 * @author 1500617
 * @date 4/1/2017
 */
public class PdfUtil {
	private static Logger logger = LoggerFactory.getLogger(PdfUtil.class);

	/**
	 *
	 * @param fileUrl
	 * @param oddOrEven 1 odd; 0 even
	 * @return 檔案相對位置
	 */
	public static String addBlank(String fileUrl, int oddOrEven) {
		String blankUrl = fileUrl;
		String srvPath = PathUtil.getServerHome();
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			reader = new PdfReader(new File(srvPath, fileUrl).toString());
			if ((reader.getNumberOfPages() & 1) == oddOrEven) {
				blankUrl += ".blank";
				stamper = new PdfStamper(reader, new FileOutputStream(new File(
						srvPath, blankUrl).toString()));
				stamper.insertPage(reader.getNumberOfPages() + 1,
						reader.getPageSizeWithRotation(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (stamper != null) {
				try {
					stamper.close();
				} catch (DocumentException | IOException e) {
					// TODO Auto-generated catch block
				}
			}

			if (reader != null) {
				reader.close();
			}
		}

		return blankUrl;
	}

	/**
	 *
	 * @param fileUrl
	 * @return 檔案相對位置
	 */
	public static String addBlankDelimiter(String fileUrl) {
		String blankUrl = fileUrl;
		String srvPath = PathUtil.getServerHome();
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			reader = new PdfReader(new File(srvPath, fileUrl).toString());
			blankUrl += ".blank2";
			int pageNum = reader.getNumberOfPages();
			stamper = new PdfStamper(reader, new FileOutputStream(new File(
					srvPath, blankUrl).toString()));
			for (int i = 1; i <= pageNum * 2; i = i + 2) {
				stamper.insertPage(i + 1,
						reader.getPageSizeWithRotation(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (stamper != null) {
				try {
					stamper.close();
				} catch (DocumentException | IOException e) {
					// TODO Auto-generated catch block
				}
			}

			if (reader != null) {
				reader.close();
			}
		}

		return blankUrl;
	}

	/**
	 *
	 * @return 處理完的 pdf 檔案位置
	 * @throws JBranchException
	 * @param pdfConfigVO
	 */
	public static String process(final PdfConfigVO pdfConfigVO) throws JBranchException {
		// 依照「是否添加空白頁」決定待處理的報表
		List<String> peddingList = pdfConfigVO.isAddBlank()? blankPageProcess(pdfConfigVO.getFiles()): pdfConfigVO.getFiles();

		String reportPath = "";
		if (peddingList.isEmpty())
			return reportPath;
		else if (peddingList.size() == 1) { // 僅加密
			reportPath = encrypt(peddingList.get(0) ,pdfFunc(pdfConfigVO));
		} else { // 加密兼合併
			reportPath = merge(peddingList, pdfFunc(pdfConfigVO));
		}
		// 將不必要的檔案刪除
		deleteFiles(CollectionUtils.union(pdfConfigVO.getFiles(), peddingList));

		return reportPath;
	}

	private static String encrypt(String url, PdfFunction pdfFunction) throws JBranchException {
		String serverHome = PathUtil.getServerHome();
		UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
		if(uuid == null){
			uuid = new UUID();
		}
		String outputName = "temp" + File.separator + uuid.toString() + ".pdf";
		File outputPath = new File(serverHome, outputName);

		// 讀取原始 PDF 檔
		PdfReader reader = null;
		// 創建 PdfStamper 來加密 PDF
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(new File(serverHome, url).getAbsolutePath());
			stamper = new PdfStamper(reader, new FileOutputStream(outputPath));
			// 設置加密
			pdfFunction.config(stamper);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw new JBranchException(e.getMessage(), e);
		} finally {
			// 關閉 PdfStamper 和 PdfReader
			try {
				if (stamper != null) {
					stamper.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return outputName;
	}

	private static PdfFunction pdfFunc(final PdfConfigVO pdfConfigVO) {
		return new PdfFunction() {
			@Override
			public void config(PdfEncryptionSettings pdf) throws DocumentException, JBranchException {
				if (pdfConfigVO.isEncrypt()) {
					// 加密處理
					encryptProcess(pdf,
							// 如果沒有使用者密碼，預設為登入 ID
							isBlank(pdfConfigVO.getUserPassword()) ?
									defaultString((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID)) :
									pdfConfigVO.getUserPassword(),
							// 如果沒有擁有者密碼，則從資料庫搜尋
							isNotBlank(pdfConfigVO.getOwnerPassword()) ? pdfConfigVO.getOwnerPassword() : getOwnerPassword(pdfConfigVO.getDam()));
				}
			}
		};
	}

	private static void encryptProcess(PdfEncryptionSettings pdf, String userPassword, String ownerPassword) throws DocumentException, JBranchException {
		pdf.setEncryption(
				userPassword.getBytes(),
				ownerPassword.getBytes(),
				PdfWriter.ALLOW_PRINTING,
				PdfWriter.ENCRYPTION_AES_128);
	}

	private static String getOwnerPassword(DataAccessManager dam) throws JBranchException {
		Map<String, String> keys = queryPdfKey(dam);
		return decryptKey(keys);
	}

	private static String decryptKey(Map<String, String> keys) throws JBranchException {
		try {
			Coder coder = new PdfKeyCoder(keys.get("PARAM_CODE")); // AES KEY
			return coder.decode(
					new StringBuffer(keys.get("PARAM_NAME"))
			).toString();
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("PDF Key 解密失敗。");
		}
	}

	private static Map<String, String> queryPdfKey(DataAccessManager dam) throws JBranchException {
		List<Map<String, String>> data = Manager.manage(dam)
				.append("select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER ")
				.append("where PARAM_TYPE = 'FUBONSYS.PDF_KEY' ")
				.query();

		if (data.isEmpty())
			throw new APException("缺少 PDF Key。");
		return data.get(0);
	}

	private static List<String> blankPageProcess(List<String> files) {
		List<String> blankUrlList = new ArrayList<String>();
		for (String url : files) {
			if (url == null) {
				continue;
			}
			blankUrlList.add(addBlank(url, 1));
		}
		return blankUrlList;
	}

	/**
	 *
	 * @param files  PDF檔案清單
	 * @param IsAddBlank 是否加空白頁
	 * @return merge完的pdf檔案位置
	 * @throws JBranchException
	 */
	public static String mergePDF(List<String> files, boolean IsAddBlank) throws JBranchException {
		// 依照「是否添加空白頁」決定待處理的報表
		List<String> peddingList = IsAddBlank? blankPageProcess(files): files;
		String reportPath = merge(peddingList, null);

		// 將不必要的檔案刪除
		deleteFiles(CollectionUtils.union(files, peddingList));

		return reportPath;
	}

	private static void deleteFiles(Collection<String> deletedList) throws JBranchException {
		if (deletedList.isEmpty()) return;

		String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
		for(String url : deletedList){
			try{
				File file = new File(serverPath+url);
				if(file.exists()) {
					if(file.delete()){
						System.out.println(file.getName() + " is deleted!");
					}else{
						System.out.println(file.getName() + ". Delete operation is failed.");
					}
				}else{
					System.out.println(file.getName() + ". Delete operation is failed. File is not exists!");
				}
			}catch(Exception e){
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}

	/**
	 * 合併所有 PDF ＆ 加密功能
	 * @param paths pdf合併路徑
	 * @param password pdf密碼
	 * */
	public static String mergePDF (List<String> paths, final String password) throws JBranchException, DocumentException{
		String reportPath = merge(paths, new PdfFunction() {
			@Override
			public void config(PdfEncryptionSettings pdf) throws DocumentException, JBranchException {
				encryptProcess(pdf, password, "World");
			}
		});
		deleteFiles(paths);

		return reportPath;
	}

	private static String merge(List<String> paths, PdfFunction func) throws JBranchException {
		String serverHome = PathUtil.getServerHome();
		UUID uuid = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);
		if(uuid == null){
			uuid = new UUID();
		}
		String outputName = "temp" + File.separator + uuid.toString() + ".pdf";
		File outputPath = new File(serverHome, outputName);
		PdfCopyFields mergePDF = null;
		try {
			mergePDF = new PdfCopyFields(new FileOutputStream(outputPath));

			if (func != null)
				func.config(mergePDF);

			for (int i = 0; i < paths.size(); i++) {
				File f = new File(serverHome, paths.get(i));
				mergePDF.addDocument(new PdfReader(f.getAbsolutePath()));
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw new JBranchException(e.getMessage(), e);
		} finally {
			if (mergePDF != null) {
				mergePDF.close();
			}
		}
		return outputName;
	}
}

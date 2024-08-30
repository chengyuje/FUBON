package com.systex.jbranch.fubon.bth.job.business_logic;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
// import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
// import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 *
 * @author Eli
 * @date 2018/01/12 寄送報表服務
 * @date 2019/04/03 1、可選擇需要將報表內容呈現在信件上，還是照原本需求壓縮附檔
 * 					2、壓縮檔每 10M 將分割
 *
 */
@Component
@Scope("prototype")
@SuppressWarnings({"rawtypes", "unchecked"})
public class RptSender extends FubonWmsBizLogic {
	/**rar 副檔名*/
	private final String ZIP_EXT = ".zip";
	/**csv 副檔名*/
	private final String CSV_EXT = ".csv";
	/**預設壓縮檔檔名*/
	private String zipFileName = UUID.randomUUID().toString();
	/**壓縮檔加密密碼*/
	private String password;
	/**是否加密*/
	private boolean isEncrypt;
	/**因為檔案過大會採用分割檔，所以使用集合來存放檔案路徑*/
	private List<String> target = new ArrayList<String>();
	/**附檔報表List*/
	private List<String> stored;
	/**存放收信者容器*/
	private List<Map<String, String>> mailList = new ArrayList();

	/*=====初始化物件=====*/
	private FubonMail zipMail = new FubonMail();
	private FubonMail passwordMail = new FubonMail();
	/** 附檔大小限制，每 10 MB 分割 */
	private final int FILE_SIZE_LIMIT =  10 * 1024 * 1024;

	/** 報表內容是否要呈現在信件上面（否則是附檔方式呈現） **/
	private boolean notAttached = false;

	/**use list to store rpt names*/
	public void setReports(List<String> reportCodes) throws Exception {
		stored = reportCodes;
	}

	/**設定密碼*/
	public void setPassword(String password) {
		if (StringUtils.isNotBlank(password)) {
			this.password = password;
			setEncrypt(true);
		}
	}

	/**
	 * 取得此類別實例
	 * @return RptSender
	 * @throws JBranchException
	 */
	public static RptSender getInstance() throws JBranchException {
		return (RptSender) PlatformContext.getBean("rptSender");
	}

	/**
	 * 壓縮檔是否加密
	 */
	public void setEncrypt(boolean isEncrypt) {
		this.isEncrypt = isEncrypt;
	}

	/**
	 * 設定壓縮檔名，預設檔名為default
	 * @param zipFileName
	 */
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	/**
	 * 確認檔案source → 產生檔案 → 壓縮 → 寄信 → 刪除檔案
	 * @throws Exception
	 */
	public void send() throws Exception {
		checkList();
		zip();
		mail();
		delZip();
	}

	/**刪除產生的壓縮檔*/
	private void delZip() throws IOException {
		if (notAttached) return;
		for (String zipPath: target)
    		Files.deleteIfExists(getRptZipPath(zipPath));
	}

	/**確認有無source*/
	private void checkList() throws APException {
		if (stored.size() == 0) throw new APException("無資料可執行");
	}

	/**壓縮檔案*/
	private void zip() throws ZipException, IOException {
		if (notAttached) return;

		ZipFile zipFile = new ZipFile(AccessContext.tempReportPath + zipFileName + ZIP_EXT,
				StringUtils.defaultString(this.password).toCharArray());
		ZipParameters parameters = new ZipParameters();
		setZipParameters(parameters);

		ArrayList<File> fileList = new ArrayList<File>();
		for (String rpt: stored) {
			Path path = Paths.get(AccessContext.tempReportPath + rpt);
			if (Files.exists(path)) fileList.add(path.toFile());
		}
		if (fileList.size() > 0) {
			zipFile.createSplitZipFile(fileList, parameters, true, FILE_SIZE_LIMIT);
			List<File> files = zipFile.getSplitZipFiles();
			target.clear();
			for (File f : files) target.add(f.getAbsolutePath());
			// target = zipFile.getSplitZipFiles(); // zipFile.getSplitZipFiles();
		}
		for (Object each: fileList) Files.deleteIfExists(Paths.get(each.toString()));
	}

	/**設定parameters*/
	private void setZipParameters(ZipParameters parameters) {
		//定義壓縮方式
		parameters.setCompressionMethod(CompressionMethod.DEFLATE); // Zip4jConstants.COMP_DEFLATE);
		//壓縮等級
		parameters.setCompressionLevel(CompressionLevel.ULTRA); //Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		if (isEncrypt) setEncrptyParameter(parameters);
	}

	/**設置parameters 加密部分*/
	private void setEncrptyParameter(ZipParameters parameters) {
		//將壓縮檔案加密
		parameters.setEncryptFiles(isEncrypt);
		//定義壓縮檔案加密方式
		parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // Zip4jConstants.ENC_METHOD_STANDARD);
		//加入密碼部分
		// parameters.setPassword(password);	// 2.9.1沒有這個method
	}

	/**mail process*/
	private void mail() throws Exception {
		zipMail();
		passwordMail();
	}

	/**password mail process*/
	private void passwordMail() throws Exception {
		if (!notAttached && isEncrypt) {
			passwordMail.setLstMailTo(mailList);
			sendMail(passwordMail, new HashMap());
		}
    }

	/**zip mail process*/
	private void zipMail() throws Exception {
		zipMail.setLstMailTo(mailList);
		configureDefaultContent();

		if (notAttached) {
			setRptContent();
            sendMail(zipMail, new HashMap());
        } else {
			for (String zipPath: target)
				sendMail(zipMail, getAttachment(zipPath));
		}
	}

	/** 配置預設內文 **/
	private void configureDefaultContent() {
		String userContent = zipMail.getContent();
		String defMsg = new StringBuilder()
				.append("<ul>")
				.append("    <li>")
				.append("        <span>使用 <b>WinRAR</b> 軟體有可能無法順利解壓縮。解壓縮建議使用軟體：")
				.append("            <a href=\"https://www.winzip.com/win/en/downwz.html\" target=\"_blank\" title=\"前去下載 WinZip！\">WinZip</a>")
				.append("            or")
				.append("            <a href=\"https://www.7-zip.org\" target=\"_blank\" title=\"前去下載 7-Zip！\">7-Zip</a>！")
				.append("        </span>")
				.append("    </li>")
				.append("</ul>")
				.append("<hr/>")
				.toString();
		zipMail.setContent(defMsg + userContent);
	}

	/** 呼叫 FubonSendJavaMail.send 方法 **/
    private void sendMail(FubonMail zipMail, Map attachment) throws Exception {
        new FubonSendJavaMail().sendMail(zipMail, attachment);
    }

    /** 將報表內容寫在 Mail 上面 **/
	private void setRptContent() {
		StringBuilder content = new StringBuilder();
		content.append(zipMail.getContent()).append("<hr/>");

		for (String eachFile: stored) {
			StringBuilder report = new StringBuilder();
			RptResolver.readReport(eachFile, report);
			content.append(addHtmlTable(report.toString()));
		}
		zipMail.setContent(content.toString());
	}

	/** 將報表內容以 Html Table 的形式產出，如果報表不符合解析的格式，則返回原本形式 **/
	private String addHtmlTable(String reportContent) {
		try {
			StringBuilder tableHtml = new StringBuilder();
			int index = 0;

			tableHtml.append("<table style=\"border: 1px solid #666; border-collapse: collapse;\">");
			for (String row : reportContent.split("\n")) {
				String trStyle = index == 0 ? "background: rgb(123, 189, 224); color: white;" :
						index % 2 == 0 ? "background: #DDD" : "background-color: #FAFAFA";

				tableHtml.append("<tr style=\"" + trStyle + "\">");
				for (String col : row.split(",")) {
					tableHtml.append("<td style=\"border: 1px solid #666;\">" + col + "</td>");
				}
				tableHtml.append("<tr/>");
				index++;
			}
			tableHtml.append("</table><hr/>");
			return tableHtml.toString();
		} catch (Exception e) {
			return reportContent;
		}
	}

	/**
	 * 取得附件檔案
	 * @param filepath 檔案路徑
	 * @throws IOException
	 */
	public Map getAttachment(String filepath) throws IOException {
		Path rptPath = getRptZipPath(filepath);
		Map annexData = new HashMap();
		annexData.put(rptPath.getFileName().toString(), Files.readAllBytes(rptPath));
		return annexData;
	}

	/**
	 * Zip4j 目前為 1.3.2 版本，在取得第十個分割檔其實際副檔名為 .z10，方法 getSplitZipFiles 取到的則是 .z010
	 * 這裡修正這個 Library 的錯誤，將副檔名 .z010 更改為 .z10
	 *
	 **/
	private Path getRptZipPath(String filepath) {
		if (filepath.matches(".+\\.z010$"))
			return Paths.get(filepath.replaceAll(".z010$", ".z10"));
		else
			return Paths.get(filepath);
	}

	/**
	 * 設定收信者
	 * @param email
	 */
	public void setReceiver(String email) {
		Map mailMap = new HashMap();
		mailMap.put(FubonSendJavaMail.MAIL, email);
		mailList.add(mailMap);
	}

	/**
	 * 設定多位收信者
	 * @param emails : List
	 */
	public void setReceivers(List emails) {
		for (Object each: emails) setReceiver((String) each);
	}

	/**
	 * 設定Zip信件主旨
	 * @param subject
	 */
	public void setZipMailSubject(String subject) {
		zipMail.setSubject(subject);
	}

	/**
	 * 設定Zip信件內容
	 * @param content
	 */
	public void setZipMailContent(String content) {
		zipMail.setContent(content);
	}

	/**
	 * 設定password信件主旨
	 * @param subject
	 */
	public void setPasswordMailSubject(String subject) {
		passwordMail.setSubject(subject);
	}

	/**
	 * 設定password信件內容
	 * @param content
	 */
	public void setPasswordMailContent(String content) {
		passwordMail.setContent(content + "\r\n 附件密碼 :" + password);
	}

	public boolean isNotAttached() {
		return notAttached;
	}

	public void setNotAttached(boolean notAttached) {
		this.notAttached = notAttached;
	}
}

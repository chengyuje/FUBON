package com.systex.jbranch.fubon.bth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.code.coder.Coder;
import com.systex.jbranch.fubon.bth.code.coder.TaiwanMobileCoder;
import com.systex.jbranch.fubon.bth.code.config.FileCoderConfig;
import com.systex.jbranch.fubon.bth.code.service.FileCoder;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @version 2017/01/16
 * @author Sebastian 依需求調整開發此批次流程 此版本批次流程為,直接於畫面參數欄位使用pckName/pckArg參數帶入PACKAGE
 *         NAME與input argument
 *         
 *         
 * @version 2018/04/17 
 * @author Eli Refactory and add function of oracle to return CLOB type data       
 *         
 *         
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Repository("bthmain2")
@Scope("prototype")
public class BthMain2 extends BizLogic {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private AuditLogUtil audit = null;
	private DataAccessManager dam = null;
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();

	private String ftpCode; 	// TBSYSFTP.FTPSETTINGID (for sqlldr)
	private String ftpGetCode; 	// TBSYSFTP.FTPSETTINGID (download)
	private String ftpPutCode; 	// TBSYSFTP.FTPSETTINGID (upload)
	private Path ctlDir; 		// ctl 檔案目錄
	private String ctlName; 	// ctl 檔案名稱
	private Path ctlLogDir; 	// ctl.log 檔案目錄
	private String pckName; 	// PACKAGE NAME
	private String pckArg; 		// PACKAGE Argument, split by ","
	private String arg;			// 取得報表名稱
	
	/**
	 * entrace of executing job
	 * 執行各項動作所需傳入參數如下表:
	 * ----------------------------------------------------------------------------------
	 * | action | ftpGetCode | ftpPutCode | ftpCode | ctlName | pckName | pckArg | arg | 
	 * |        |            |            |         |         |         |        |     | 
	 * |  FTP   |     V      |     V      |         |         |         |        |     | 
	 * |  LDR   |            |            |    V    |    V    |         |        |     | 
	 * |  PCK   |            |            |         |         |    V    |    V   |     | 
	 * |  RPT   |            |            |         |         |    V    |    V   |  V  | 
	 * ----------------------------------------------------------------------------------
	 * action參數說明 :
	 * FTP 遠端上/下載檔案
	 * LDR 執行SQLLDR
	 * PCK 執行Package
	 * RPT 執行Package.Function 產生報表
	 * 
	 * 組合action 範例說明 : 
	 * FTP(GET) + LDR + PCK + FTP(PUT) : 下載檔案並執行Sqlldr，再執行package完成後 將相關檔案上傳FTP
	 *  
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		init(body);			// 初始參數
		getFileAction();	// FTP 下載檔案
		sqlldrAction();		// 執行 ctl 檔
		pckAction();		// 執行 package
		putFileAction();	// FTP 上傳檔案
	}

	/** 執行FTP上傳特定檔案 (TBSYSFTP WHERE FTPSETTING = ftpCode)*/
	private void putFileAction() throws Exception {
		if (StringUtils.isNotBlank(ftpPutCode)) {
			audit.audit("執行 FTP 上傳檔案");
			try {
				ftpJobUtil.ftpPutFile(ftpPutCode);
			} finally {
				audit.audit(ftpJobUtil.getAuditMsg().toString());
			}
		}
	}

	/**execute pck*/
	private void pckAction() throws Exception {
		if (StringUtils.isNotBlank(pckName)) {
			if (StringUtils.isNotBlank(pckArg) 
					&& StringUtils.isNotBlank(arg) ) {
				audit.audit("產出報表");
				executePckGenFile();
			} else {
				audit.audit("執行 package");
				executePck();
			}
		}
	}

	/**
	 * 執行sqlldr
	 * @throws Exception
	 */
	private void sqlldrAction() throws Exception {
		if (StringUtils.isNotBlank(ftpCode)
				&& StringUtils.isNotBlank(ctlName)) {
			audit.audit("執行 sqlldr");
			executeSqlldr();
		}
	}

	/** 執行FTP下載特定檔案 (TBSYSFTP WHERE FTPSETTING = ftpGetCode) */
	private void getFileAction() throws Exception {
		if (StringUtils.isNotBlank(ftpGetCode)) {
			audit.audit("執行 FTP 下載檔案");
			try {
				ftpJobUtil.ftpGetFile(ftpGetCode);
				codedFile();
			} finally {
				audit.audit(ftpJobUtil.getAuditMsg().toString());
			}		
		}
	}

	/**
	 * initialize action
	 * @param body
	 * @throws JBranchException 
	 * @throws IOException 
	 */
	private void init(Object body) throws JBranchException, IOException {
		// 取得傳入參數
		Map<String, Map> inputMap = (Map)body;
		Map<String, String> jobParameter = inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);

		// 排程使用參數
		ftpCode = jobParameter.get("ftpCode");
		ftpGetCode = jobParameter.get("ftpGetCode");
		ftpPutCode = jobParameter.get("ftpPutCode");
		
		// 20170802 ctlDir、ctlLogDir參數將從TBSYSPARAMETER 中取得，不從頁面上輸入
		setCtl();
		ctlName = jobParameter.get("ctlName");
		
		pckName = jobParameter.get("pckName");
		pckArg =  jobParameter.get("pckArg");
		arg = jobParameter.get("arg");
	}

	/**
	 * 設定控制檔目錄相關參數
	 * @throws JBranchException
	 * @throws IOException 
	 */
	private void setCtl() throws JBranchException, IOException {
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> pathMap = xmlInfo.doGetVariable("SYS.CTL_SETTING", FormatHelper.FORMAT_3);

		// 取得ctrl、log目錄位置
		ctlDir = Paths.get(ftpJobUtil.replaceRootPath(pathMap.get("FILE_PATH")));
		ctlLogDir = Paths.get(ftpJobUtil.replaceRootPath(pathMap.get("LOG_PATH")));
		
		if (!Files.exists(ctlDir)) Files.createDirectories(ctlDir);
		if (!Files.exists(ctlLogDir)) Files.createDirectories(ctlLogDir);
	}

	/**檔案如果有密鑰則解之*/
	private void codedFile() throws Exception {
		String encryptKey = inquireEntryptKey(ftpGetCode);
		if(encryptKey != null){
			List<TBSYSFTPVO> list = getSourceInfo(ftpGetCode); 
			String sourcePath = ftpJobUtil.replaceRootPath(
					list.get(0).getDESDIRECTORY() + "\\" + list.get(0).getDESFILENAME());
			FileCoderConfig cfg = new FileCoderConfig();
		    cfg.setCodeType(Coder.DECODE);
		    cfg.setSourcePath(Paths.get(sourcePath));
		    FileCoder.code(new TaiwanMobileCoder(encryptKey), cfg);
		    System.out.println("End decode!");
		}
	}

	/**
	 * 呼叫Pck.fn回傳CLOB 產出報表
	 * @throws JBranchException 
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws DAOException 
	 */
	private void executePckGenFile() throws JBranchException, SQLException, IOException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(String.format("select %s.%s() REPORT from dual", pckName, pckArg));
		List<Map> result = dam.exeQuery(condition);
		Clob report = (Clob)result.get(0).get("REPORT");
		System.out.printf("%s.%s's Clob.length = %s%n ", pckName, pckArg, report.length());
		ClobCovertFile(report.getCharacterStream());
	}

	/**
	 * let data of Clob be written to file
	 * @param characterStream
	 * @throws IOException
	 */
	private void ClobCovertFile(Reader characterStream) throws IOException {
		try (
			BufferedWriter writer = AccessContext.getBomBufferedWriter(Paths.get(AccessContext.tempReportPath, arg));
			BufferedReader reader = new BufferedReader(characterStream))
		{
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
			writer.flush();	
		}
	}

	/**
	 * 觸發PABTH_BATCHCTRL.PR_BatchExec啟動Oracle排程
	 * @throws JBranchException
	 */
	private void executePck() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(String.format("CALL %s.MAIN(%s) ", pckName, getPckArg(condition)));
		dam.exeUpdate(condition);
	}

	/**
	 * formatted pckArg to parameter of package and setting condition
	 * @param condition
	 */
	private String getPckArg(QueryConditionIF condition) {
		StringBuffer argBuffer = new StringBuffer();
		if (StringUtils.isNotBlank(pckArg)) {
			String[] argAry = pckArg.split(",");
			
			for (int i = 0; i < argAry.length; i++) {
				if (argBuffer.length() > 0) {
					argBuffer.append(", ");
				}
				argBuffer.append("?");
				condition.setString(i + 1, argAry[i]);
			}
		}
		return argBuffer.toString();
	}

	/**
	 * 由資料庫取得並Decode Batch資料庫連結帳密字串
	 *
	 * @return
	 * @throws JBranchException
	 */
	private String getDecodePwd() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_HQL);

		StringBuffer sql = new StringBuffer("SELECT o.PARAM_NAME ")
									.append("FROM TBSYSPARAMETERVO o ")
									.append("WHERE 1=1 ")
									.append("AND o.comp_id.PARAM_TYPE = :param_type ")
									.append("AND o.comp_id.PARAM_CODE = :param_code");

		condition.setQueryString(sql.toString());
		condition.setObject("param_type", "BTH_SETTING");
		condition.setObject("param_code", "BTH_DB_CONNECTION");

		List<?> results = dam.exeQuery(condition);

		// 檢核資料庫設定是否正確
		if (results.size() != 1) {
			throw new JBranchException("TBSYSPARAMETER.PARAM_TYPE 欄位設定不正確");
		}

		// 因設定頁改為明碼設定，在此不做解密
		// String paramName = JBranchCryptology.decodePassword((String)results.get(0));
		return (String) results.get(0);
	}

	/**
	 * 依順序執行CTL檔匯入資料
	 *
	 * @throws JBranchException
	 */
	private void executeSqlldr() throws Exception {
		// 取得FTP上,資料來源檔相關資訊
		List<TBSYSFTPVO> ftpVOs = getSourceInfo(ftpCode);

		for (TBSYSFTPVO ftpVO : ftpVOs) {
			String desFileDir, desFileName, checkFile;

			desFileDir = ftpVO.getDESDIRECTORY();
			desFileName = ftpVO.getDESFILENAME();
			excCmd(ctlName, desFileDir, desFileName);

			// 是否執行控制檔
			checkFile = ftpVO.getCHECKFILE();
			if (checkFile != null && !checkFile.isEmpty()) {
				excCmd(getZCtlFileName(ctlName), desFileDir, checkFile);
			}
		}
	}

	/**
	 * 取控制檔的 ctl File Name
	 * 
	 * @param ctlName
	 * @return
	 */
	private String getZCtlFileName(String ctlName) {
		String zCtlName = "";

		String[] nameSplit = ctlName.split("\\.");

		int cnt = 1;
		for (String names : nameSplit) {
			if (cnt < nameSplit.length) {
				zCtlName = zCtlName + names;
			} else {
				zCtlName = zCtlName + "_SQLLDR." + names;
			}
			cnt++;
		}
		return zCtlName;
	}

	private List<TBSYSFTPVO> getSourceInfo(String ftpCode) throws Exception {
		List<TBSYSFTPVO> realNames = new ArrayList<TBSYSFTPVO>();

		StringBuffer sql = new StringBuffer().append("FROM TBSYSFTPVO o ").append("WHERE FTPSETTINGID = :ftpCode ");

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_HQL);
		condition.setQueryString(sql.toString());
		condition.setObject("ftpCode", ftpCode);

		List<?> result = dam.exeQuery(condition);

		if (CollectionUtils.isNotEmpty(result)) {
			TBSYSFTPVO ftpVo = (TBSYSFTPVO) result.get(0);
			ftpVo = (TBSYSFTPVO) ftpVo.clone();
			String srcfileDirectory = ftpVo.getSRCDIRECTORY();
			String desfileDirectory = ftpVo.getDESDIRECTORY();

			// 若畫面路徑位置不完全,拋出錯誤訊息
			if (StringUtils.isBlank(srcfileDirectory) || StringUtils.isBlank(desfileDirectory)) {
				throw new JBranchException("需輸入路徑位置");
			}

			// 是否為模糊查詢
			if (ftpJobUtil.isFuzzyFile(ftpVo.getSRCFILENAME()) || ftpJobUtil.isFuzzyFile(ftpVo.getDESFILENAME())) {

				File desDir = new File(ftpJobUtil.replaceRootPath(ftpVo.getDESDIRECTORY()));
				String[] desfiles = desDir.list();

				// 取目標目路徑下之檔案做模糊查詢
				realNames = ftpJobUtil.fuzzySearchCompare(desfiles, ftpVo.getDESFILENAME(), ftpVo.getCHECKFILE());

				// 逐筆放入FTP Info
				for (TBSYSFTPVO vo : realNames) {
					vo.setDESDIRECTORY(desfileDirectory);
					vo.setSRCDIRECTORY(srcfileDirectory);
				}
			}
			// 非模糊查詢直接帶入回傳的ftpVO
			else {
				// 轉換檔案名稱日期
				String desFileName = ftpJobUtil.getRealFileName(ftpVo.getDESFILENAME());
				String srcFileName = ftpJobUtil.getRealFileName(ftpVo.getSRCFILENAME());

				String checkfile = ftpVo.getCHECKFILE();
				if (checkfile != null && !checkfile.trim().isEmpty()) {
					ftpVo.setDESFILENAME(desFileName);
					checkfile = ftpJobUtil.getRealFileName(checkfile);

					ftpVo.setCHECKFILE(checkfile);
				}

				ftpVo.setDESFILENAME(desFileName);
				ftpVo.setSRCFILENAME(srcFileName);

				realNames.add(ftpVo);
			}
		} else {
			logger.info("查無FTP相關資料: FTPSETTINGID not find");
		}

		return realNames;
	}

	private void excCmd(String fileName, String desFileDir, String desFileName) throws Exception {
		// 組合sqlldr執行語句
		String ldrCmd = 
				String.format("cmd /c sqlldr %s CONTROL=%s LOG=%s DATA=%s",
						getDecodePwd(),
						ctlDir.resolve(fileName),
						ctlLogDir.resolve(fileName + ".log"),
						Paths.get(desFileDir, desFileName));
		try {
			// execute terminal command
			Process executeInfo = Runtime.getRuntime().exec(ldrCmd);
			executeInfo.waitFor();

			// feedback FIXME: Sebastian DELETE LOG
			System.out.println(">>>>>>>>>>Command: " + ldrCmd);

			// show error in console
			logDesAndMemo(executeInfo);
		} catch (Exception e) {
			throw new JBranchException(e);
		}
	}

	/**
	 * 顯示Process的錯誤訊息
	 *
	 * @param executeInfo
	 * @throws IOException
	 * @throws JBranchException
	 */
	private void logDesAndMemo(Process executeInfo) throws JBranchException {
		StringBuffer memoMsg = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		String line;
		
		try (
			// log Customized msg in TBSYSSCHDADMASTER.MEMO
			BufferedReader inReader = new BufferedReader(new InputStreamReader(executeInfo.getInputStream(), "Big5"));
			// log Error in TBSYSSCHDADMASTER.DESCRIPTION	
			BufferedReader errReader = new BufferedReader(new InputStreamReader(executeInfo.getErrorStream(), "Big5"));
		) {
			//執行訊息
			while ((line = inReader.readLine()) != null) {
				memoMsg.append(line);
			}
			audit.audit(memoMsg.toString());

			//錯誤訊息
			while ((line = errReader.readLine()) != null) {
				errorMsg.append(line);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 

		if (errorMsg.length() > 0) {
			throw new JBranchException("[Exe] SQLLDR ERR: " + errorMsg.toString());
		}
	}
    
	/**供bthmain2撈加密key值*/
	private String inquireEntryptKey(String ftpsysID) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select KEY from TBSYSFTPKEY ");
		sql.append("where FTPSETTINGID = :ftpsysId ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("ftpsysId", ftpsysID);
		List<Map> encryptKey = dam.exeQuery(queryCondition);
		if (encryptKey.size() > 0) {
			return encryptKey.get(0).get("KEY").toString();
		} else {
			return null;
		}
	}
}

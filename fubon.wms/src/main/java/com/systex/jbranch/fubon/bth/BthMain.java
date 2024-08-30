/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.security.impl.JBranchCryptology;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("bthmain")
@Scope("prototype")
public class BthMain extends BizLogic {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private AuditLogUtil audit = null;

	private DataAccessManager dam = null;

	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();

	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		//取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParameter = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);

		//排程使用參數
		String ftpType 	= (String) jobParameter.get("ftpType");		// ftp上傳/下載 (get/put)
		String ftpCode 	= (String) jobParameter.get("ftpCode");		// TBSYSFTP.FTPSETTINGID
		String ctlDir 	= (String) jobParameter.get("ctlDir");		// ctl 檔案路徑
		String ctlName 	= (String) jobParameter.get("ctlName");		// ctl 檔案名稱
		String module 	= (String) jobParameter.get("module");		// Module Type
		String main		= (String) jobParameter.get("main");		// Job Main
		String sub 		= (String) jobParameter.get("sub");			// Job Sub

		/* 執行各項動作所需傳入參數如下表:
		 * ------------------------------------------------------------------------------------------------------------------
		 * |	action						|	ftpType	|	ftpCode	|	ctlDir	|	ctlName	|	module	|	main	|	sub	|
		 * ------------------------------------------------------------------------------------------------------------------
		 * | FTP							|	  V		|	  V		|  	  X		|	   X	|	  X		|  	  X		|    X	|
		 * ------------------------------------------------------------------------------------------------------------------
		 * | Loader							|	  X		|	  X		|  	  V		|	   V	|	  X		|  	  X		|	 X 	|
		 * ------------------------------------------------------------------------------------------------------------------
		 * | FTP+Loader						|	  V		|	  V		|  	  V		|	   V	|	  X		|  	  X		|    X	|
		 * ------------------------------------------------------------------------------------------------------------------
		 * | Package						|	  X		|	  X		|  	  X		|	   X	|	  V		|  	  V		|    V	|
		 * ------------------------------------------------------------------------------------------------------------------
		 * | FTP+Package					|	  V		|	  V		|  	  X		|	   X	|	  V		|  	  V		|    V	|
		 * ------------------------------------------------------------------------------------------------------------------
		 * | Loader+Package					|	  X		|	  V		|  	  X		|	   X	|	  V		|  	  V		|    V	|
		 * ------------------------------------------------------------------------------------------------------------------
		 * | FTP+Loader+Package				|	  V		|	  V		|  	  V		|	   V	|	  V		|  	  V		|    V	|
		 * ------------------------------------------------------------------------------------------------------------------
		 */

		//分割ftpType的內容 (get/put)
		Boolean useFtp = Boolean.FALSE;
		String[] ftpTypes = null;
		if(ftpType != null && ftpCode != null) {
			ftpTypes = ftpType.split("/");
			useFtp = Boolean.TRUE;
		}

		//執行FTP下載特定檔案 (TBSYSFTP WHERE FTPSETTING = ftpCode)
		if(useFtp && Arrays.binarySearch(ftpTypes, "get") >= 0){
			audit.audit("執行 FTP 下載檔案");
			
			try {
				ftpJobUtil.ftpGetFile(ftpCode);
			} finally {
				audit.audit(ftpJobUtil.getAuditMsg().toString());
			}
		}

		//執行sqlldr
		if(ftpCode!=null && ctlDir!=null && ctlName!=null){
			audit.audit("執行 sqlldr");
			executeSqlldr(ftpCode, ctlDir, ctlName);
		}

		//execute pck
		if(module!=null && main!=null && sub!=null){
			audit.audit("執行 package");
			executePck(module, main, sub);
		}

		//執行FTP上傳特定檔案 (TBSYSFTP WHERE FTPSETTING = ftpCode)
		if(useFtp && Arrays.binarySearch(ftpTypes, "put") >= 0){
			audit.audit("執行 FTP 上傳檔案");
			
			try {
				ftpJobUtil.ftpPutFile(ftpCode);
			} finally {
				audit.audit(ftpJobUtil.getAuditMsg().toString());
			}
		}
	}

	/**
	 * 觸發PABTH_BATCHCTRL.PR_BatchExec啟動Oracle排程
	 *
	 */
	private void executePck(String module, String main, String sub) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		StringBuffer sql = new StringBuffer()
				.append("{CALL PABTH_BATCHCTRL.PR_BatchExec(?, ?, ?)}");
		condition.setQueryString(sql.toString());

		condition.setString(1, module);
		condition.setString(2, main);
		condition.setString(3, sub);

		dam.exeUpdate(condition);
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

		List results = dam.exeQuery(condition);

		//檢核資料庫設定是否正確
		if(results.size() != 1){
			throw new JBranchException("TBSYSPARAMETER.PARAM_TYPE 欄位設定不正確");
		}
		
		//因設定頁改為明碼設定，在此不做解密
		//String paramName = JBranchCryptology.decodePassword((String) results.get(0));
		String paramName = (String) results.get(0);

		return paramName;
	}

	/**
	 * 依順序執行CTL檔匯入資料
	 *
	 * @throws JBranchException
     */
	private void executeSqlldr(String ftpCode, String ctlDir, String ctlName) throws Exception {
		//取得FTP上,資料來源檔相關資訊
		List<TBSYSFTPVO> ftpVOs = getSourceInfo(ftpCode);

		//使用FTP回傳資訊取
		for(TBSYSFTPVO ftpVO : ftpVOs) {
			String desFileDir, desFileName, checkFile;

			desFileDir = ftpVO.getDESDIRECTORY();
			desFileName = ftpVO.getDESFILENAME();
			excCmd(ctlDir, ctlName, desFileDir, desFileName);

			//是否執行控制檔
			checkFile = ftpVO.getCHECKFILE();
			if (checkFile != null && !checkFile.isEmpty()) {
				excCmd(ctlDir, getZCtlFileName(ctlName), desFileDir, checkFile);
			}
		}
	}

	/**
	 * 取控制檔的 ctl File Name
	 * @param ctlName
	 * @return
     */
	private String getZCtlFileName(String ctlName) {
		String zCtlName = "";

		String[] nameSplit = ctlName.split("\\.");

		int cnt = 1;
		for(String names : nameSplit) {
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
		List<TBSYSFTPVO> realNames = new ArrayList();

		StringBuffer sql = new StringBuffer()
				.append("FROM TBSYSFTPVO o ")
				.append("WHERE FTPSETTINGID = :ftpCode ");

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_HQL);
		condition.setQueryString(sql.toString());
		condition.setObject("ftpCode", ftpCode);

		List result = dam.exeQuery(condition);

		if(CollectionUtils.isNotEmpty(result)) {
			TBSYSFTPVO ftpVo = (TBSYSFTPVO) result.get(0);
			String srcfileDirectory = ftpVo.getSRCDIRECTORY();
			String desfileDirectory = ftpVo.getDESDIRECTORY();

			//若畫面路徑位置不完全,拋出錯誤訊息
			if (StringUtils.isBlank(srcfileDirectory) ||
					StringUtils.isBlank(desfileDirectory)) {
				throw new JBranchException("需輸入路徑位置");
			}

			//是否為模糊查詢
			if (ftpJobUtil.isFuzzyFile(ftpVo.getSRCFILENAME()) ||
					ftpJobUtil.isFuzzyFile(ftpVo.getDESFILENAME())) {

				File desDir = new File(ftpVo.getDESDIRECTORY());
				String[] desfiles = desDir.list();

				//取目標目路徑下之檔案做模糊查詢
				realNames = ftpJobUtil.fuzzySearchCompare(desfiles, ftpVo.getDESFILENAME(), ftpVo.getCHECKFILE());

				//逐筆放入FTP Info
				for (TBSYSFTPVO vo : realNames) {
					vo.setDESDIRECTORY(desfileDirectory);
					vo.setSRCDIRECTORY(srcfileDirectory);
				}
			}
			//非模糊查詢直接帶入回傳的ftpVO
			else {
				//轉換檔案名稱日期
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
		}
		else{
			logger.info("查無FTP相關資料: FTPSETTINGID not find");
		}

		return realNames;
	}

	private void excCmd(String ctlDir, String fileName, String desFileDir, String desFileName) throws Exception {
		//取得DB連線帳密字組
		String pwd = getDecodePwd();

		String ctlPath = new File(ctlDir, fileName).toString();
		String logPath = new File(ctlDir, fileName + ".log").toString();
		String dataPath = new File(desFileDir, desFileName).toString();
		
		//組合sqlldr執行語句
		StringBuffer ldrCommand = new StringBuffer()
				.append("cmd /c ")
				.append("sqlldr ")
				.append(pwd)
				.append(" CONTROL=")
				.append(ctlPath)
				.append(" LOG=")
				.append(logPath)
				.append(" DATA=")
				.append(dataPath);

		try {
			//execute terminal command
			Process executeInfo = Runtime.getRuntime().exec(ldrCommand.toString());

			executeInfo.waitFor();

			//feedback FIXME: Sebastian DELETE LOG
			System.out.println(">>>>>>>>>>Command: " + ldrCommand);

			//show error in console
			logDesAndMemo(executeInfo);
		}catch (Exception e){
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
		String result = "";
		BufferedReader inReader = null;
		BufferedReader errReader = null;
		String memoMsg;
		String errorMsg;

		try {
			//log Customized msg in TBSYSSCHDADMASTER.MEMO
			inReader = new BufferedReader(new InputStreamReader(executeInfo.getInputStream(), "Big5"));
			
			while((memoMsg = inReader.readLine()) != null) {
				result += memoMsg;
			}
			
			audit.audit(result);
			
			//log Error in TBSYSSCHDADMASTER.DESCRIPTION
			result = "";
			errReader = new BufferedReader(new InputStreamReader(executeInfo.getErrorStream(), "Big5"));
			
			while((errorMsg = errReader.readLine()) != null) {
				result += errorMsg;
			}
			
			// close stream
			executeInfo.getInputStream().close();
			executeInfo.getErrorStream().close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					;
				}
			}
			
			if (errReader != null) {
				try {
					errReader.close();
				} catch (IOException e) {
					;
				}
			}
		}

		if(!result.isEmpty()) {
			throw new JBranchException("[Exe] SQLLDR ERR: " + result);
		}
	}
}

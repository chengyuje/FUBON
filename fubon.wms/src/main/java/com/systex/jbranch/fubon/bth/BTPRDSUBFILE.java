package com.systex.jbranch.fubon.bth;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_SHARED_LINKPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_SHARED_LINKVO;
import com.systex.jbranch.app.server.fps.prd220.PRD220;
import com.systex.jbranch.app.server.fps.prd220.PRD220InputVO;
import com.systex.jbranch.app.server.fps.prd220.PRD220OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/*
 * 20230601_#1485_俊達_輔銷文件自動上傳   
 * by Sam Tu
 */
@Repository("btprdsubfile")
@Scope("prototype")
public class BTPRDSUBFILE extends BizLogic {

	private DataAccessManager dam;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String name;
	
	

	
	/*
	 * SI/SN 輔消文件自動批次上傳
	 * 1. 處理temp路徑下的有效檔名
	 * 2. 根據檔名分為 單筆商品 OR 兩筆商品的處理流程
	 * 3. 參考PRD220的editData以及savaData進行update or insert流程
	 */
	public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String sysdate = sdf.format(new Date());
		int count = 0;
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		File folder = new File(tempPath);
		String[] list = folder.list();
		for(String tempname : list) {
			try {
				if(Pattern.matches(".*(Confirmation_" + sysdate + ")+.*", tempname)) {
					if(Pattern.matches(".*(\\.pdf)+", tempname)) {
						name = tempname;
						logger.info("BTPRDSUBFILE_處理檔案:  " + name);		
						if(name.split("_")[2].equals("Confirmation")) {
							process("single");
						} else {
							process("multy");
						}			
						count = count + 1;
					} else {
						logger.info("BTPRDSUBFILE_檔案非pdf格式,不進行處理.  " + name);				
					}			
				}		
			} catch (Exception e) {
				logger.error("BTPRDSUBFILE_檔案處理_出現錯誤:  " + name);			
				logger.error("BTPRDSUBFILE_檔案處理_出現錯誤:  " + e.toString());  
			}
	
		}
		logger.info("BTPRDSUBFILE在" + sysdate + "共處理" + count +"個檔案");		
	}


	private void process(String type) throws Exception {
		// TODO Auto-generated method stub
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> urlConfig = xmlInfo.doGetVariable("PRD.SUB_FILE_URL", FormatHelper.FORMAT_3);
		String[] StrList = name.split("_");
		
		String ptype = StrList[0];
		if("single".equals(type)) {
			String prdId = StrList[1];
			String sDate = StrList[3];
			String url = urlConfig.get(ptype)+prdId;
			
			dealSql(ptype,prdId,sDate,url);
		} else {
			String prdId = StrList[1];
			String prdId2 = StrList[2];
			String sDate = StrList[4];
			String url = urlConfig.get(ptype+"TWO1") + prdId + urlConfig.get(ptype+"TWO2") + prdId2;
			
			dealSql(ptype,prdId,sDate,url);
			dealSql(ptype,prdId2,sDate,url);
		}	
	}
	
	private void dealSql(String ptype, String prdId, String sDate, String url) throws Exception {
		// TODO Auto-generated method stub
		
		PRD220InputVO inputVO = initPRD220inputVO(ptype,prdId,sDate);
		
		List<Map<String, Object>> fileQuery = getFile_DOC_ID(prdId);
		if(fileQuery.size() > 0) {
			inputVO.setDoc_id((String) fileQuery.get(0).get("DOC_ID"));		
			inputVO.setType("1");
			inputVO.setFileName(name);
			inputVO.setRealfileName(name);
			
			editData(inputVO);
		} else {
			inputVO.setDoc_id(getDOC_ID());  //交易確認書跟配息通知是不同doc_id
			inputVO.setDoc_name("交易確認書");
			inputVO.setType("1");
			inputVO.setFileName(name);
			inputVO.setRealfileName(name);
			
			saveData(inputVO);
		}
		
		List<Map<String, Object>> urlQuery = getURL_DOC_ID(prdId);
		if(urlQuery.size() > 0) {
			inputVO.setDoc_id((String) urlQuery.get(0).get("DOC_ID"));
			inputVO.setType("2");
			inputVO.setWeb(url);
			
			editData(inputVO);
		} else {
			inputVO.setDoc_id(getDOC_ID());  //交易確認書跟配息通知是不同doc_id
			inputVO.setDoc_name("配息通知");
			inputVO.setType("2");
			inputVO.setWeb(url);
			saveData(inputVO);
		}
		
	}

	private PRD220InputVO initPRD220inputVO(String ptype, String prdId, String sDate) throws ParseException {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		PRD220InputVO inputVO = new PRD220InputVO();

		inputVO.setDoc_sDate(new java.sql.Date(sdf.parse(sDate).getTime()));
		inputVO.setDoc_eDate(new java.sql.Date(sdf.parse("20991231").getTime()));
		inputVO.setShared("N");
		inputVO.setPtype(ptype);
		inputVO.setPrd_id(prdId);
		inputVO.setDoc_type("02");
		inputVO.setDoc_sDate(new java.sql.Date(sdf.parse(sDate).getTime()));
		inputVO.setDoc_eDate(new java.sql.Date(sdf.parse("20991231").getTime()));
		
		return inputVO;
		
	}


	public void editData(PRD220InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		
		// update TBSYS_FILE_DETAIL
		TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
		dvo = (TBSYS_FILE_DETAILVO) dam.findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, inputVO.getDoc_id());
		if (dvo != null) {
			if(inputVO.getDoc_sDate() != null)
				dvo.setDOC_START_DATE(new Timestamp(inputVO.getDoc_sDate().getTime()));
			if(inputVO.getDoc_eDate() != null)
				dvo.setDOC_DUE_DATE(new Timestamp(inputVO.getDoc_eDate().getTime()));
			dam.update(dvo);
		} else
			throw new APException("ehl_01_common_001");
		// have real but don't have name => ori file
		if("1".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getRealfileName()) && StringUtils.isBlank(inputVO.getFileName())) {
			
		}
		// upload
		else if("1".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getFileName())) {
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
			
			// TBSYS_FILE_DETAILVO
			dvo.setFILE_NAME(inputVO.getRealfileName());
			dvo.setDOC_FILE_TYPE("D");
			dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
			dvo.setDOC_URL(null);
			dam.update(dvo);
		}
		if("2".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getWeb())) {
			// TBSYS_FILE_DETAILVO
			dvo.setFILE_NAME(null);
			dvo.setDOC_FILE_TYPE("U");
			dvo.setDOC_FILE(null);
			dvo.setDOC_URL(inputVO.getWeb());
			dam.update(dvo);
		}
	}
	
	public void saveData(PRD220InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		
		if("N".equals(inputVO.getShared())) {
			// check again
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			switch(inputVO.getPtype()) {
				case "MFD":
					sql.append("SELECT PRD_ID FROM TBPRD_FUND where PRD_ID = :id ");
					break;
				case "ETF":
					sql.append("SELECT PRD_ID FROM TBPRD_ETF where PRD_ID = :id ");
					break;
				case "STOCK":
					sql.append("SELECT PRD_ID,STOCK_CNAME FROM TBPRD_STOCK where PRD_ID = :id ");
					break;
				case "BND":
					sql.append("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id ");
					break;
				case "SI":
					sql.append("SELECT PRD_ID FROM TBPRD_SI where PRD_ID = :id ");
					break;
				case "SN":
					sql.append("SELECT PRD_ID FROM TBPRD_SN where PRD_ID = :id ");
					break;
				case "INS":
					sql.append("SELECT PRD_ID FROM TBPRD_INS where PRD_ID = :id ");
					break;
			}
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() == 0)
				throw new APException("ehl_01_common_026");
		}
		
		// byte data
		if("1".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getFileName())) {
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
			
			// TBSYS_FILE_MAIN
			TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
			fvo.setDOC_ID(inputVO.getDoc_id());
			fvo.setDOC_NAME(inputVO.getDoc_name() == null ? "FILE" : inputVO.getDoc_name());
			fvo.setSUBSYSTEM_TYPE("PRD");
			fvo.setDOC_TYPE(inputVO.getDoc_type());
			dam.create(fvo);
			TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
			dvo.setDOC_ID(inputVO.getDoc_id());
			if(inputVO.getDoc_sDate() != null)
				dvo.setDOC_START_DATE(new Timestamp(inputVO.getDoc_sDate().getTime()));
			if(inputVO.getDoc_eDate() != null)
				dvo.setDOC_DUE_DATE(new Timestamp(inputVO.getDoc_eDate().getTime()));
			dvo.setDOC_VERSION_STATUS("2");
			dvo.setFILE_NAME(inputVO.getRealfileName());
			dvo.setDOC_FILE_TYPE("D");
			dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
			dam.create(dvo);
		}
		if("2".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getWeb())) {
			// TBSYS_FILE_MAIN
			TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
			fvo.setDOC_ID(inputVO.getDoc_id());
			fvo.setDOC_NAME(inputVO.getDoc_name() == null ? "URL" : inputVO.getDoc_name());
			fvo.setSUBSYSTEM_TYPE("PRD");
			fvo.setDOC_TYPE(inputVO.getDoc_type());
			dam.create(fvo);
			TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
			dvo.setDOC_ID(inputVO.getDoc_id());
			if(inputVO.getDoc_sDate() != null)
				dvo.setDOC_START_DATE(new Timestamp(inputVO.getDoc_sDate().getTime()));
			if(inputVO.getDoc_eDate() != null)
				dvo.setDOC_DUE_DATE(new Timestamp(inputVO.getDoc_eDate().getTime()));
			dvo.setDOC_VERSION_STATUS("2");
			dvo.setDOC_FILE_TYPE("U");
			if(inputVO.getWeb().indexOf("http://") == -1 && inputVO.getWeb().indexOf("https://") == -1)
				dvo.setDOC_URL("http://"+inputVO.getWeb());
			else
				dvo.setDOC_URL(inputVO.getWeb());
			dam.create(dvo);
		}
		
		// TBSYS_PRD_SHARED_LINK
		if("Y".equals(inputVO.getShared())) {
			TBSYS_PRD_SHARED_LINKVO vo = new TBSYS_PRD_SHARED_LINKVO();
			TBSYS_PRD_SHARED_LINKPK pk = new TBSYS_PRD_SHARED_LINKPK();
			pk.setDOC_ID(inputVO.getDoc_id());
			pk.setPTYPE(inputVO.getPtype());
			vo.setcomp_id(pk);
			dam.create(vo);
		}
		// TBSYS_PRD_LINK
		else {
			TBSYS_PRD_LINKVO vo = new TBSYS_PRD_LINKVO();
			TBSYS_PRD_LINKPK pk = new TBSYS_PRD_LINKPK();
			pk.setDOC_ID(inputVO.getDoc_id());
			pk.setPRD_ID(inputVO.getPrd_id());
			pk.setPTYPE(inputVO.getPtype());
			vo.setcomp_id(pk);
			dam.create(vo);
		}
	}
	
	public String getDOC_ID() throws JBranchException {
		PRD220OutputVO return_VO = new PRD220OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	public List<Map<String, Object>> getFile_DOC_ID(String PRD_ID) throws JBranchException {
		PRD220OutputVO return_VO = new PRD220OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAIN.DOC_ID, LINK.PRD_ID FROM TBSYS_FILE_MAIN MAIN, TBSYS_PRD_LINK LINK ");
		sql.append("WHERE MAIN.DOC_ID = LINK.DOC_ID ");
		sql.append("AND MAIN.DOC_NAME = '交易確認書' ");
		sql.append("AND LINK.PRD_ID = :PRD_ID ");
		queryCondition.setObject("PRD_ID", PRD_ID);
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}
	
	public List<Map<String, Object>> getURL_DOC_ID(String PRD_ID) throws JBranchException {
		PRD220OutputVO return_VO = new PRD220OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAIN.DOC_ID, LINK.PRD_ID FROM TBSYS_FILE_MAIN MAIN, TBSYS_PRD_LINK LINK ");
		sql.append("WHERE MAIN.DOC_ID = LINK.DOC_ID ");
		sql.append("AND MAIN.DOC_NAME = '配息通知' ");
		sql.append("AND LINK.PRD_ID = :PRD_ID ");
		queryCondition.setObject("PRD_ID", PRD_ID);
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

}

package com.systex.jbranch.app.server.fps.crm3a1;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_CHGAO_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_ROTATION_MAINVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm3a1")
@Scope("request")
public class CRM3A1 extends FubonWmsBizLogic {

	public void init(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		CRM3A1InputVO inputVO = (CRM3A1InputVO) body;
		CRM3A1OutputVO outputVO = new CRM3A1OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT A.PRJ_ID AS DATA, A.PRJ_NAME AS LABEL  ");
		sql.append(" FROM TBCRM_TRS_PRJ_MAST A "); 
		sql.append(" LEFT JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'CRM.2022CMDT_PRJ_NAME' ");
		sql.append(" WHERE A.PRJ_NAME = P.PARAM_NAME "); 
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);

		outputVO.setPRJIDList(list);
		sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		CRM3A1OutputVO outputVO = new CRM3A1OutputVO();
		outputVO = this.query(body);

		sendRtnObject(outputVO);
	}

	private CRM3A1OutputVO query(Object body) throws JBranchException, ParseException {
		initUUID();

		CRM3A1InputVO inputVO = (CRM3A1InputVO) body;
		CRM3A1OutputVO outputVO = new CRM3A1OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		String roleID = inputVO.getLoginRole();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專

		try {

			sql.append(" Select DISTINCT A.CUST_ID,"); // 客戶ID
			sql.append(" D.CUST_NAME,"); // 客戶姓名
			sql.append(" A.ORG_AO_CODE,"); // 原理專CODE
			sql.append(" AO.EMP_NAME AS ORG_EMP_NAME,"); // 原理專姓名
			sql.append(" A.ORG_AO_BRH,"); // 原分行代碼
			sql.append(" ORG.BRANCH_NAME AS ORG_BRANCH_NAME,"); // 原分行名稱
			sql.append(" A.NEW_AO_CODE,"); // 新理專CODE
			sql.append(" AO2.EMP_NAME AS NEW_EMP_NAME,"); // 新理專姓名
			sql.append(" A.NEW_AO_BRH,"); // 新分行代碼
			sql.append(" ORG2.BRANCH_NAME AS NEW_BRANCH_NAME,"); // 新分行名稱
			sql.append(" B.PRJ_NAME,"); // 專案名稱
			sql.append(" C.REC_SEQ,"); // 錄音序號
			sql.append(" C.REC_DATE,"); // 錄音日期
			sql.append(" (CASE WHEN C.CMDT_FILE IS NULL THEN 'N' ELSE 'Y' END) AS FILE_UPLOADED, "); // 是否有上傳檔案
			sql.append(" C.CMDT_FILE_NAME, "); // 上傳的檔案名稱
			sql.append(" C.CMDT_FILE_NAME||'.'||C.CMDT_FILE_TYPE as SHOW_FILE_NAME, "); // 上傳的檔案名稱
			sql.append(" A.SEQ, ");
			sql.append(" ORG.BRANCH_AREA_ID, ");
			sql.append(" ORG.REGION_CENTER_ID, ");
			sql.append(" P.PARAM_CODE AS PRJ_TYPE "); //1:核心客戶 2:非核心客戶
			sql.append(" from TBCRM_TRS_AOCHG_PLIST A ");
			sql.append(" INNER JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'CRM.2022CMDT_PRJ_NAME' ");
			sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID AND B.PRJ_NAME = P.PARAM_NAME ");
			sql.append(" LEFT JOIN TBCRM_10CMDT_CHGAO_FILE C ON C.PLIST_SEQ = A.SEQ ");
			sql.append(" LEFT JOIN TBCRM_CUST_MAST D ON D.CUST_ID = A.CUST_ID ");
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ON ORG.BRANCH_NBR = A.ORG_AO_BRH ");
			sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG2 ON ORG2.BRANCH_NBR = A.NEW_AO_BRH ");
			sql.append(" LEFT JOIN VWORG_AO_INFO AO ON AO.AO_CODE = A.ORG_AO_CODE ");
			sql.append(" LEFT JOIN VWORG_AO_INFO AO2 ON AO2.AO_CODE = A.NEW_AO_CODE ");
			// 核心客戶：專案匯入時不可匯入相同CODE號，所以會先匯入維護CODE再匯入新CODE。顯示時不須顯示維護CODE中間過程
			sql.append(" WHERE ((P.PARAM_CODE = '1' AND A.ORG_AO_BRH <> A.NEW_AO_BRH) OR P.PARAM_CODE = '2') "); //核心客戶排除新舊分行相同，因為會先暫時移入維護CODE
			
			// 專案代號
			if (StringUtils.isNotBlank(inputVO.getPRJ_ID())) {
				sql.append(" AND A.PRJ_ID = :PRJ_ID ");
				condition.setObject("PRJ_ID", inputVO.getPRJ_ID());
			}
			
			// 客戶姓名
			if (StringUtils.isNotBlank(inputVO.getCUST_ID())) {
				sql.append(" AND A.CUST_ID = :CUST_ID ");
				condition.setObject("CUST_ID", inputVO.getCUST_ID());
			}

			/*
			 * 可視範圍四個層級 業務處>營運區>分行>理專
			 * 1. 理專身分: 只考慮有無選擇理專來決定限制條件
			 * 2. 其他身分: 從理專往上看,有選擇的當限制條件
			 * 3. 其他身分若四個層級都沒有,則用業務處清單當限制條件
			 */
			//理專
			boolean conditionEnd = false;
			if (fcMap.containsKey(roleID)) {
				if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sql.append(" AND A.ORG_AO_CODE = :AO_CODE ");
					condition.setObject("AO_CODE", inputVO.getAo_code());
				} else {
					List<String> aoCodeList = new ArrayList();
					for (Map map : inputVO.getAoCodeList()) {
						if (!checkIsNull(map, "DATA").equals("")) {
							aoCodeList.add(checkIsNull(map, "DATA"));
						}
					}
					sql.append(" AND A.ORG_AO_CODE IN (:AO_CODE) ");
					condition.setObject("AO_CODE", aoCodeList);
				}
				conditionEnd = true;
			} else { //其他身分				
				//理專
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getAo_code())) {
					sql.append(" AND A.ORG_AO_CODE = :AO_CODE ");
					condition.setObject("AO_CODE", inputVO.getAo_code());
					conditionEnd = true;
				}
				// 分行
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and A.ORG_AO_BRH = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
					conditionEnd = true;
				}
				// 區
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
					sql.append(" and ORG.BRANCH_AREA_ID = :branch_area_id ");
					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
					conditionEnd = true;
				}
				// 處
				if (!conditionEnd && StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
					sql.append(" and ORG.REGION_CENTER_ID = :region_center_id ");
					condition.setObject("region_center_id", inputVO.getRegion_center_id());
					conditionEnd = true;
				}
			}
			//非理專且四項都沒選就用業務處清單
			if(!conditionEnd) {
				List<String> regionList = new ArrayList();
				for (Map map : inputVO.getRegionList()) {
					if (!checkIsNull(map, "DATA").equals("")) {
						regionList.add(checkIsNull(map, "DATA"));
					}
				}
				sql.append(" and ORG.REGION_CENTER_ID IN (:region_center_list) ");
				condition.setObject("region_center_list", regionList);
			}

			condition.setQueryString(sql.toString()); 
			List<Map<String, Object>> list = dam.exeQuery(condition);

			outputVO.setResultList(list);
			return outputVO;

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void edit(Object body, IPrimitiveMap header) throws JBranchException, ParseException, IOException, SerialException, SQLException {
		CRM3A1InputVO inputVO = (CRM3A1InputVO) body;
		CRM3A1OutputVO outputVO = new CRM3A1OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		StringBuffer sql = new StringBuffer();

		boolean doUpdate = false;
		TBCRM_10CMDT_CHGAO_FILEVO mainVO = null;

		sql.append("select SEQ from TBCRM_10CMDT_CHGAO_FILE ");
		sql.append("where PLIST_SEQ = :PLIST_SEQ ");
		condition.setObject("PLIST_SEQ", new BigDecimal(inputVO.getSeq()));
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		if (list.size() > 0) {
			doUpdate = true;
			mainVO = (TBCRM_10CMDT_CHGAO_FILEVO) dam.findByPKey(TBCRM_10CMDT_CHGAO_FILEVO.TABLE_UID, new BigDecimal(list.get(0).get("SEQ").toString()));
		} else {
			mainVO = new TBCRM_10CMDT_CHGAO_FILEVO();
		}

		// /檔案
		if (StringUtils.isNotBlank(inputVO.getFileName())) {
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Path path = Paths.get(joinedPath);
			byte[] data = Files.readAllBytes(path);
			mainVO.setCMDT_FILE_NAME(getFileRealName(inputVO.getFileRealName()));
			mainVO.setCMDT_FILE(ObjectUtil.byteArrToBlob(data));
			mainVO.setCMDT_FILE_TYPE(FilenameUtils.getExtension(inputVO.getFileName()));
		} else if(StringUtils.isBlank(inputVO.getFileRealName())){ 
			mainVO.setCMDT_FILE_NAME(null);
			mainVO.setCMDT_FILE(null);
			mainVO.setCMDT_FILE_TYPE(null);
		}
		// 錄音序號
		if (StringUtils.isNotBlank(inputVO.getRecSeq())) {
			mainVO.setREC_SEQ(inputVO.getRecSeq());
		} else {
			mainVO.setREC_SEQ(null);
		}
		// 錄音日期
		if (null != inputVO.getRecDate()) {
			mainVO.setREC_DATE(new Timestamp(inputVO.getRecDate().getTime()));
		} else {
			mainVO.setREC_DATE(null);
		}

		// update or create
		if (doUpdate) {
			dam.update(mainVO);
		} else {
			mainVO.setSEQ(getNextSeq());
			mainVO.setPLIST_SEQ(new BigDecimal(inputVO.getSeq()));
			dam.create(mainVO);
		}

		sendRtnObject(outputVO);
	}

	public void download(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		CRM3A1InputVO inputVO = (CRM3A1InputVO) body;
		CRM3A1OutputVO outputVO = new CRM3A1OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("select CMDT_FILE_NAME, CMDT_FILE, CMDT_FILE_TYPE from TBCRM_10CMDT_CHGAO_FILE ");
		sql.append("where PLIST_SEQ = :PLIST_SEQ ");
		condition.setObject("PLIST_SEQ", new BigDecimal(inputVO.getSeq()));
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);

		if (list.size() < 1) {
			throw new APException("查無對應下載檔案");
		}

		List resList = new ArrayList();
		Map map = new HashMap();
		if (CollectionUtils.isNotEmpty(list)) {
			// ByteArrayOutputStream out = null;
			try {
				byte[] byteArr = ObjectUtil.blobToByteArr((Blob) list.get(0).get("CMDT_FILE"));
				// out = new ByteArrayOutputStream();
				// out.write(byteArr);

				map.put("DOWNLOAD_FILE", byteArr);
				resList.add(map);
				// } catch (IOException e) {
				// e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// out.close();
			}
		}
		outputVO.setDownloadList(resList);
		outputVO.setFileType(list.get(0).get("CMDT_FILE_TYPE").toString());
		outputVO.setFileRealName(list.get(0).get("CMDT_FILE_NAME").toString());
		sendRtnObject(outputVO);
	}

	private BigDecimal getNextSeq() throws JBranchException {
		List<Map<String, BigDecimal>> result = Manager.manage(this.getDataAccessManager()).append("select MAX(SEQ) + 1 SEQ from TBCRM_10CMDT_CHGAO_FILE ").query();
		return new BigDecimal(result.get(0).get("SEQ").toString());
	}

	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	private String getFileRealName(String fileName) {
		String[] splitStr = fileName.split("[.]");
		String returnStr = "";
		for(int i = 0 ; i<splitStr.length -1 ; i++) {
			if(i == 0) {
				returnStr = returnStr + splitStr[0];
			} else {
				returnStr = returnStr + "." + splitStr[i];
			}
			
		}
		
		return returnStr;		
	}
}

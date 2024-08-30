package com.systex.jbranch.app.server.fps.crm3b1;

import java.io.File;
import java.io.FileOutputStream;
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
import java.util.UUID;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_CHGAO_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_EXCLUDEVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_ROTATION_MAINVO;
import com.systex.jbranch.app.server.fps.crm371.CRM371InputVO;
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

@Component("crm3b1")
@Scope("request")
public class CRM3B1 extends FubonWmsBizLogic {

	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		CRM3B1OutputVO outputVO = new CRM3B1OutputVO();
		outputVO = this.query(body);

		sendRtnObject(outputVO);
	}

	private CRM3B1OutputVO query(Object body) throws JBranchException, ParseException {
		CRM3B1InputVO inputVO = (CRM3B1InputVO) body;
		CRM3B1OutputVO outputVO = new CRM3B1OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		String roleID = inputVO.getLoginRole();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專

		sql.append(" Select DISTINCT A.CUST_ID,"); // 客戶ID
		sql.append(" B.CUST_NAME,"); // 客戶姓名
		sql.append(" B.AO_CODE,"); // 理專CODE
		sql.append(" B.BRA_NBR,"); // 分行代碼
		sql.append(" A.SEQ_KEY_NO, ");
		sql.append(" A.DEL_YN, ");
		sql.append(" AO.EMP_NAME AS EMP_NAME,"); // 原理專姓名
		sql.append(" ORG.BRANCH_NAME AS BRANCH_NAME,"); // 原分行名稱
		sql.append(" ORG.BRANCH_AREA_ID, ");
		sql.append(" ORG.REGION_CENTER_ID, ");
		sql.append(" (CASE WHEN A.ATT_FILE IS NULL THEN 'N' ELSE 'Y' END) AS ATTACH_YN, ");
		sql.append(" A.CREATETIME, ");
		sql.append(" A.LASTUPDATE ");
		sql.append(" from TBCRM_TRS_CUST_EXCLUDE A ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST B ON B.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ON ORG.BRANCH_NBR = B.BRA_NBR ");
		sql.append(" LEFT JOIN VWORG_AO_INFO AO ON AO.AO_CODE = B.AO_CODE ");
		sql.append(" WHERE 1 = 1 ");
		
		// 客戶ID
		if (StringUtils.isNotBlank(inputVO.getCUST_ID())) {
			sql.append(" AND A.CUST_ID = :CUST_ID ");
			condition.setObject("CUST_ID", inputVO.getCUST_ID());
		}
		
		// 是否已刪除
		if (StringUtils.isNotBlank(inputVO.getDEL_YN())) {
			sql.append(" AND NVL(A.DEL_YN, 'N') = :delYN ");
			condition.setObject("delYN", inputVO.getDEL_YN());
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
				sql.append(" AND B.AO_CODE = :AO_CODE ");
				condition.setObject("AO_CODE", inputVO.getAo_code());
			} else {
				List<String> aoCodeList = new ArrayList();
				for (Map map : inputVO.getAoCodeList()) {
					if (!checkIsNull(map, "DATA").equals("")) {
						aoCodeList.add(checkIsNull(map, "DATA"));
					}
				}
				sql.append(" AND B.AO_CODE IN (:AO_CODE) ");
				condition.setObject("AO_CODE", aoCodeList);
			}
			conditionEnd = true;
		} else { //其他身分				
			//理專
			if (!conditionEnd && StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append(" AND B.AO_CODE = :AO_CODE ");
				condition.setObject("AO_CODE", inputVO.getAo_code());
				conditionEnd = true;
			}
			// 分行
			if (!conditionEnd && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append(" and B.BRA_NBR = :branch_nbr ");
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

		sql.append(" ORDER BY A.CUST_ID, DEL_YN ");
		
		condition.setQueryString(sql.toString()); 
		List<Map<String, Object>> list = dam.exeQuery(condition);

		outputVO.setResultList(list);
		return outputVO;
	}

	public void edit(Object body, IPrimitiveMap header) throws JBranchException, ParseException, IOException, SerialException, SQLException {
		CRM3B1InputVO inputVO = (CRM3B1InputVO) body;
		CRM3B1OutputVO outputVO = new CRM3B1OutputVO();
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//新增
		if(StringUtils.equals("ADD", inputVO.getSAVE_TYPE())) {
			//先做檢核
			outputVO = validateCust(inputVO);
			if(StringUtils.isNotBlank(outputVO.getErrMsg())) {
				throw new APException(outputVO.getErrMsg());
			}
			
			outputVO = new CRM3B1OutputVO();
			
			TBCRM_TRS_CUST_EXCLUDEVO vo = new TBCRM_TRS_CUST_EXCLUDEVO();
			vo.setSEQ_KEY_NO(getTBCRM_TRS_CUST_EXCLUDE_SEQ());
			vo.setCUST_ID(inputVO.getCUST_ID());
			vo.setDEL_YN("N");
			//上傳檔案
			if (StringUtils.isNotBlank(inputVO.getFileName())) {
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				vo.setATT_FILE(ObjectUtil.byteArrToBlob(data));
			}
			dam.create(vo);
		}
		
		//刪除
		if(StringUtils.equals("DEL", inputVO.getSAVE_TYPE())) {
			TBCRM_TRS_CUST_EXCLUDEVO vo = new TBCRM_TRS_CUST_EXCLUDEVO();
			vo = (TBCRM_TRS_CUST_EXCLUDEVO) dam.findByPKey(TBCRM_TRS_CUST_EXCLUDEVO.TABLE_UID, inputVO.getSEQ_KEY_NO());
			if(vo != null) {
				vo.setDEL_YN("Y");
				dam.update(vo);
			}
		}
				
		sendRtnObject(outputVO);
	}

	/** 取得 TBIOT_MAIN_SEQ.NEXTVAL **/
	private BigDecimal getTBCRM_TRS_CUST_EXCLUDE_SEQ() throws JBranchException {
		List<Map> result = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString("select TBCRM_TRS_CUST_EXCLUDE_SEQ.NEXTVAL from dual "));
		return new BigDecimal(result.get(0).get("NEXTVAL").toString());
	}
	
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	/***
	 * 檢核客戶是否可以加入ONCODE排除列表中
	 */
	public void validateCust(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		CRM3B1InputVO inputVO = (CRM3B1InputVO) body;
		CRM3B1OutputVO outputVO = new CRM3B1OutputVO();
		
		outputVO = this.validateCust(inputVO);

		sendRtnObject(outputVO);
	}

	private CRM3B1OutputVO validateCust(CRM3B1InputVO inputVO) throws JBranchException, ParseException {
		CRM3B1OutputVO outputVO = new CRM3B1OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//檢核客戶主檔中是否有此客戶
		sql.append("SELECT CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :custId ");
		condition.setObject("custId", inputVO.getCUST_ID());
		condition.setQueryString(sql.toString()); 
		List<Map<String, Object>> list = dam.exeQuery(condition);
		if(CollectionUtils.isNotEmpty(list)) {
			outputVO.setCUST_NAME(ObjectUtils.toString(list.get(0).get("CUST_NAME")));
		} else {
			outputVO.setErrMsg("無此客戶資料");
		}
		
		//檢查此客戶是否已存在排除資料中
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT 1 FROM TBCRM_TRS_CUST_EXCLUDE WHERE CUST_ID = :custId AND NVL(DEL_YN, 'N') = 'N' ");
		condition.setObject("custId", inputVO.getCUST_ID());
		condition.setQueryString(sql.toString()); 
		list = dam.exeQuery(condition);
		if(CollectionUtils.isNotEmpty(list)) {
			outputVO.setErrMsg("此客戶已存在ON CODE排除列表中");
		}
		
		return outputVO;
	}
	
	//下載附件
	public void download(Object body, IPrimitiveMap header) throws Exception {
		CRM3B1InputVO inputVO = (CRM3B1InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ_KEY_NO, ATT_FILE FROM TBCRM_TRS_CUST_EXCLUDE where SEQ_KEY_NO = :seq ");
		queryCondition.setObject("seq", inputVO.getSEQ_KEY_NO());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(list)) {
			Blob blob = (Blob) list.get(0).get("ATT_FILE");
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			String fileName = "ONCODE排除客戶申請書_" + ObjectUtils.toString(list.get(0).get("SEQ_KEY_NO")) + ".pdf";
	
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(blobAsBytes);
			fos.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		}
		
		this.sendRtnObject(null);
	}
}

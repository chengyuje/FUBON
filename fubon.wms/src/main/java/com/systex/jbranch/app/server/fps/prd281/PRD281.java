package com.systex.jbranch.app.server.fps.prd281;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_FCIVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd281")
@Scope("request")
public class PRD281  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PRD281.class);
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD281InputVO inputVO = (PRD281InputVO) body;
		PRD281OutputVO outputVO = new PRD281OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//只顯示已生效或尚未生效的
		sql.append("SELECT A.*, P.PARAM_DESC AS TARGET_CURR_NAME ");
		sql.append(" FROM TBPRD_FCI A ");
		sql.append(" LEFT JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'PRD.FCI_CURRENCY_PAIR' AND P.PARAM_CODE = A.CURR_ID AND P.PARAM_NAME = A.TARGET_CURR_ID ");
		sql.append(" WHERE (A.EFFECTIVE_YN = 'Y' OR TRUNC(A.EFFECTIVE_DATE) > TRUNC(SYSDATE)) ");
		
		// where
		if (inputVO.getSEQ_NO() != null) {
			sql.append(" AND A.SEQ_NO = :seqNo ");
			queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		}
		if (StringUtils.isNotBlank(inputVO.getCURR_ID())) {
			sql.append(" AND A.CURR_ID = :currId ");
			queryCondition.setObject("currId", inputVO.getCURR_ID());
		}
		
		sql.append(" ORDER BY A.CURR_ID, A.EFFECTIVE_YN DESC ");		
		queryCondition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢報價說明&報價時間&交易時間
	public void inquireParam(Object body, IPrimitiveMap header) throws JBranchException {
		PRD281OutputVO outputVO = inquireParam();
		this.sendRtnObject(outputVO);
	}
	
	//查詢報價說明&報價時間&交易時間
	public PRD281OutputVO inquireParam() throws JBranchException {
		PRD281OutputVO outputVO = new PRD281OutputVO();
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sql = new StringBuffer();
		
		//報價說明
		sql.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FCI_PRICE_REMARK' AND PARAM_CODE = '1' ");		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list)) outputVO.setPRICE_REMARK(list.get(0).get("PARAM_NAME").toString());
		
		//交易時間
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FCI_TRADE_TIME' ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list1)) {
			for(Map<String, Object> map : list1) {
				if(StringUtils.equals("START_HOUR", map.get("PARAM_CODE").toString())) outputVO.setT_START_HOUR(map.get("PARAM_NAME").toString());
				if(StringUtils.equals("START_MIN", map.get("PARAM_CODE").toString())) outputVO.setT_START_MIN(map.get("PARAM_NAME").toString());
				if(StringUtils.equals("END_HOUR", map.get("PARAM_CODE").toString())) outputVO.setT_END_HOUR(map.get("PARAM_NAME").toString());
				if(StringUtils.equals("END_MIN", map.get("PARAM_CODE").toString())) outputVO.setT_END_MIN(map.get("PARAM_NAME").toString());
			}
		}
		
		//報價時間
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FCI_PRICE_TIME' ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list2)) {
			for(Map<String, Object> map : list2) {
				if(StringUtils.equals("START_HOUR", map.get("PARAM_CODE").toString())) outputVO.setP_START_HOUR(map.get("PARAM_NAME").toString());
				if(StringUtils.equals("START_MIN", map.get("PARAM_CODE").toString())) outputVO.setP_START_MIN(map.get("PARAM_NAME").toString());
				if(StringUtils.equals("END_HOUR", map.get("PARAM_CODE").toString())) outputVO.setP_END_HOUR(map.get("PARAM_NAME").toString());
				if(StringUtils.equals("END_MIN", map.get("PARAM_CODE").toString())) outputVO.setP_END_MIN(map.get("PARAM_NAME").toString());
			}
		}
		
		return outputVO;
	}
	
	//取得預覽資料
	public void inquirePreview(Object body, IPrimitiveMap header) throws JBranchException {
		PRD281InputVO inputVO = (PRD281InputVO) body;
		PRD281OutputVO outputVO = new PRD281OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.*, TO_CHAR(B.LASTUPDATE, 'YYYY/MM/DD') AS DR_LASTUPDATE, C.PARAM_NAME AS PRICE_REMARK, ");
		sql.append(" CEIL(B.MON_RATE_1 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_1, ");
		sql.append(" CEIL(B.MON_RATE_2 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_2, ");
		sql.append(" CEIL(B.MON_RATE_3 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_3, ");
		sql.append(" CEIL(B.MON_RATE_4 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_4, ");
		sql.append(" CEIL(B.MON_RATE_5 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_5, ");
		sql.append(" CEIL(B.MON_RATE_6 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_6, ");
		sql.append(" CEIL(B.MON_RATE_7 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_7, ");
		sql.append(" CEIL(B.MON_RATE_8 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_8, ");
		sql.append(" CEIL(B.MON_RATE_9 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_9, ");
		sql.append(" CEIL(B.MON_RATE_10 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_10, ");
		sql.append(" CEIL(B.MON_RATE_11 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_11, ");
		sql.append(" CEIL(B.MON_RATE_12 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_12, ");
		sql.append(" (E.PARAM_NAME || '兌'|| D.PARAM_NAME || '即期匯率') AS TARGET_CNAME,  ");
		sql.append(" TRUNC(((F.MON_RATE_1 - (CEIL(B.MON_RATE_1 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_1) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_1, ");
		sql.append(" TRUNC(((F.MON_RATE_2 - (CEIL(B.MON_RATE_2 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_2) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_2, ");
		sql.append(" TRUNC(((F.MON_RATE_3 - (CEIL(B.MON_RATE_3 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_3) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_3, ");
		sql.append(" TRUNC(((F.MON_RATE_4 - (CEIL(B.MON_RATE_4 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_4) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_4, ");
		sql.append(" TRUNC(((F.MON_RATE_5 - (CEIL(B.MON_RATE_5 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_5) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_5, ");
		sql.append(" TRUNC(((F.MON_RATE_6 - (CEIL(B.MON_RATE_6 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_6) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_6, ");
		sql.append(" TRUNC(((F.MON_RATE_7 - (CEIL(B.MON_RATE_7 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_7) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_7, ");
		sql.append(" TRUNC(((F.MON_RATE_8 - (CEIL(B.MON_RATE_8 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_8) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_8, ");
		sql.append(" TRUNC(((F.MON_RATE_9 - (CEIL(B.MON_RATE_9 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_9) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_9, ");
		sql.append(" TRUNC(((F.MON_RATE_10 - (CEIL(B.MON_RATE_10 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_10) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_10, ");
		sql.append(" TRUNC(((F.MON_RATE_11 - (CEIL(B.MON_RATE_11 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_11) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_11, ");
		sql.append(" TRUNC(((F.MON_RATE_12 - (CEIL(B.MON_RATE_12 * (1 + A.REF_PRICE_Y) * 100)/100) - 0.01) * ((TRUNC(G.MON_DATE_12) - TRUNC(G.VALUE_DATE))/360) - A.TRADER_CHARGE), 2) AS RM_PROFEE_12, ");
		sql.append(" A.MIN_UF AS MINUF1, ");
		sql.append(" A.MIN_UF * 2 AS MINUF2, ");
		sql.append(" A.MIN_UF * 3 AS MINUF3, ");
		sql.append(" A.MIN_UF * 4 AS MINUF4, ");
		sql.append(" A.MIN_UF * 5 AS MINUF5, ");
		sql.append(" A.MIN_UF * 6 AS MINUF6, ");
		sql.append(" A.MIN_UF * 7 AS MINUF7, ");
		sql.append(" A.MIN_UF * 8 AS MINUF8, ");
		sql.append(" A.MIN_UF * 9 AS MINUF9, ");
		sql.append(" A.MIN_UF * 10 AS MINUF10, ");
		sql.append(" A.MIN_UF * 11 AS MINUF11, ");
		sql.append(" A.MIN_UF * 12 AS MINUF12 ");
		sql.append(" FROM TBPRD_FCI A ");
		sql.append(" INNER JOIN TBPRD_FCI_DEPOSIT_RATE B ON B.CURR_ID = A.CURR_ID ");
		sql.append(" LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE = 'PRD.FCI_PRICE_REMARK' AND C.PARAM_CODE = '1' ");
		sql.append(" LEFT JOIN TBSYSPARAMETER D ON D.PARAM_TYPE = 'PRD.FCI_CURRENCY' AND D.PARAM_CODE = A.CURR_ID ");
		sql.append(" LEFT JOIN TBSYSPARAMETER E ON E.PARAM_TYPE = 'PRD.FCI_CURRENCY' AND E.PARAM_CODE = A.TARGET_CURR_ID ");
		sql.append(" LEFT JOIN TBPRD_FCI_FTP_RATE F ON F.CURR_ID = A.CURR_ID ");
		sql.append(" LEFT JOIN TBPRD_FCI_EXPIRE_DATE G ON G.CURR_ID = A.CURR_ID ");
		sql.append(" WHERE A.SEQ_NO = :seqNo ");
		queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		queryCondition.setQueryString(sql.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//取得所有上傳文件資料
	public void getUploadList(Object body, IPrimitiveMap header) throws JBranchException {
		PRD281InputVO inputVO = (PRD281InputVO) body;
		PRD281OutputVO outputVO = new PRD281OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT SEQ_NO, DOC_TYPE, CURR_ID FROM TBPRD_FCI_DOC ORDER BY DOC_TYPE, CURR_ID ");
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//檢視上傳文件檔案
	public void viewFile(Object body, IPrimitiveMap header) throws Exception {
		PRD281InputVO inputVO = (PRD281InputVO) body;
		PRD281OutputVO outputVO = new PRD281OutputVO();
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.SEQ_NO, A.DOC_FILE, P.PARAM_NAME AS DOC_TYPE ");
		sql.append(" FROM TBPRD_FCI_DOC A ");
		sql.append(" LEFT JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'PRD.FCI_DOC_TYPE' AND P.PARAM_CODE = A.DOC_TYPE ");
		sql.append(" WHERE SEQ_NO = :seqNo ");
		queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(list)) {
			Blob blob = (Blob) list.get(0).get("DOC_FILE");
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			String fileName = "FCI" + ObjectUtils.toString(list.get(0).get("DOC_TYPE")) + "_" + ObjectUtils.toString(list.get(0).get("SEQ_NO")) + ".pdf";
	
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(blobAsBytes);
			fos.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		}
		
		this.sendRtnObject(outputVO);
	}
	
	//資料儲存
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD281InputVO inputVO = (PRD281InputVO) body;
		PRD281OutputVO outputVO = new PRD281OutputVO();
		dam = this.getDataAccessManager();
		
		if(inputVO.getSaveType().matches("1")) { //儲存
			//新增：檢查是否有相同幣別相同生效日資料
			if(!validateSameCurrData(inputVO)) {
				throw new APException("此幣別已有相同生效日資料");
			}
			
			TBPRD_FCIVO vo = new TBPRD_FCIVO();
			if(inputVO.getSEQ_NO() == null) {
				inputVO.setSEQ_NO(new BigDecimal(getSEQ()));
				vo.setSEQ_NO(inputVO.getSEQ_NO());
			} else {
				vo = (TBPRD_FCIVO) dam.findByPKey(TBPRD_FCIVO.TABLE_UID, inputVO.getSEQ_NO());
				if(vo == null) {
					throw new APException("查無此序號資料");
				}
			}
			
			vo.setCURR_ID(inputVO.getCURR_ID());
			vo.setRISKCATE_ID(inputVO.getRISKCATE_ID());
			vo.setREF_PRICE_Y(inputVO.getREF_PRICE_Y());
			vo.setMIN_UF(inputVO.getMIN_UF());
			vo.setBASE_AMT(inputVO.getBASE_AMT());
			vo.setUNIT_AMT(inputVO.getUNIT_AMT());
			vo.setTRADER_CHARGE(inputVO.getTRADER_CHARGE());
			vo.setSTRIKE_PRICE(inputVO.getSTRIKE_PRICE());
			vo.setTARGET_CURR_ID(inputVO.getTARGET_CURR_ID());
			vo.setEFFECTIVE_DATE(new Timestamp(inputVO.getEFFECTIVE_DATE().getTime()));
			vo.setEFFECTIVE_YN("N"); //可新增修改的都尚未生效
			
			try {
				dam.update(vo);
			} catch(Exception e) {
				dam.create(vo);
			}
		} else if(inputVO.getSaveType().matches("2")) { //刪除
			TBPRD_FCIVO vo = (TBPRD_FCIVO) dam.findByPKey(TBPRD_FCIVO.TABLE_UID, inputVO.getSEQ_NO());
			if(vo == null) {
				throw new APException("查無此序號資料");
			} else {
				dam.delete(vo);
			}
		} else if(inputVO.getSaveType().matches("3")) { //儲存說明與交易時間起訖
			String loginID = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID));
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			//更新報價說明
			sql.append("UPDATE TBSYSPARAMETER SET PARAM_NAME = :priceRmk, PARAM_NAME_EDIT = :priceRmk, LASTUPDATE = SYSDATE, MODIFIER = :loginId ");
			sql.append(" WHERE PARAM_TYPE = 'PRD.FCI_PRICE_REMARK' AND PARAM_CODE = '1' ");
			condition.setQueryString(sql.toString());
			condition.setObject("priceRmk", inputVO.getPRICE_REMARK());
			condition.setObject("loginId", loginID);
			dam.exeUpdate(condition);
			
			//更新交易時間起訖
			updateParamTime("PRD.FCI_TRADE_TIME", "START_HOUR", inputVO.getT_START_HOUR());
			updateParamTime("PRD.FCI_TRADE_TIME", "START_MIN", inputVO.getT_START_MIN());
			updateParamTime("PRD.FCI_TRADE_TIME", "END_HOUR", inputVO.getT_END_HOUR());
			updateParamTime("PRD.FCI_TRADE_TIME", "END_MIN", inputVO.getT_END_MIN());
			
			//更新報價時間起訖
			updateParamTime("PRD.FCI_PRICE_TIME", "START_HOUR", inputVO.getP_START_HOUR());
			updateParamTime("PRD.FCI_PRICE_TIME", "START_MIN", inputVO.getP_START_MIN());
			updateParamTime("PRD.FCI_PRICE_TIME", "END_HOUR", inputVO.getP_END_HOUR());
			updateParamTime("PRD.FCI_PRICE_TIME", "END_MIN", inputVO.getP_END_MIN());
		} else if(inputVO.getSaveType().matches("4")) { //儲存上傳文件檔案
			String loginID = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID));
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			
			//檢查此文件類別是否存在
			sql.append("SELECT SEQ_NO FROM TBPRD_FCI_DOC WHERE DOC_TYPE = :docType ");
			if(StringUtils.equals("1", inputVO.getDOC_TYPE())) {
				sql.append(" AND CURR_ID = :currId ");
				condition.setObject("currId", inputVO.getCURR_ID());
			}
			condition.setObject("docType", inputVO.getDOC_TYPE());
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Path path = Paths.get(joinedPath);
			byte[] data = Files.readAllBytes(path);
			
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			if(CollectionUtils.isEmpty(list)) {
				sql.append("INSERT INTO TBPRD_FCI_DOC (SEQ_NO, DOC_TYPE, CURR_ID, DOC_FILE, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
				sql.append(" VALUES (:seqNo, :docType, :currID, :docFile, 1, :loginID, SYSDATE, :loginID, SYSDATE) ");
				
				condition.setQueryString(sql.toString());
				condition.setObject("seqNo", getDocFileSEQ());
				condition.setObject("docType", inputVO.getDOC_TYPE());
				condition.setObject("currID", inputVO.getCURR_ID());
				condition.setObject("docFile", ObjectUtil.byteArrToBlob(data));
				condition.setObject("loginID", loginID);
			} else {
				sql.append("UPDATE TBPRD_FCI_DOC ");
				sql.append(" SET DOC_FILE = :docFile, MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
				sql.append(" WHERE SEQ_NO = :seqNo ");
				
				condition.setQueryString(sql.toString());
				condition.setObject("seqNo", ObjectUtils.toString(list.get(0).get("SEQ_NO")));
				condition.setObject("docFile", ObjectUtil.byteArrToBlob(data));
				condition.setObject("loginID", loginID);
			}
			dam.exeUpdate(condition);
		}
		
		this.sendRtnObject(outputVO);
	}
	
	private void updateParamTime(String paramType, String paramCode, String data) throws JBranchException {
		String loginID = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID));
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("UPDATE TBSYSPARAMETER SET PARAM_NAME = :data, PARAM_NAME_EDIT = :data, LASTUPDATE = SYSDATE, MODIFIER = :loginId ");
		sql.append(" WHERE PARAM_TYPE = :paramType AND PARAM_CODE = :paramCode ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("data", data);
		condition.setObject("paramType", paramType);
		condition.setObject("paramCode", paramCode);
		condition.setObject("loginId", loginID);
		
		dam.exeUpdate(condition);
	}
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBPRD_FCI_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	/**產生上傳文件資料檔seq No */
	private String getDocFileSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBPRD_FCI_DOC_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	/**
	 * 檢查是否有相同幣別相同生效日資料
	 * @return true:沒有相同資料 flase:有相同資料
	 * @throws JBranchException
	 */
	private boolean validateSameCurrData(PRD281InputVO inputVO) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 1 FROM TBPRD_FCI ");
		sql.append(" WHERE CURR_ID = :currId AND TRUNC(EFFECTIVE_DATE) = TRUNC(:effDate) ");
		if(inputVO.getSEQ_NO() != null) {
			sql.append(" AND SEQ_NO <> :seqNo ");
			queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		}
		queryCondition.setObject("currId", inputVO.getCURR_ID());
		queryCondition.setObject("effDate", inputVO.getEFFECTIVE_DATE());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isEmpty(list) ? true : false;
	}

	public void inquireTargetCurrs(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD281InputVO inputVO = (PRD281InputVO) body;
		PRD281OutputVO outputVO = new PRD281OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT P.PARAM_NAME AS DATA, P.PARAM_DESC AS LABEL ");
		sql.append(" FROM TBSYSPARAMETER P ");
		sql.append(" WHERE P.PARAM_TYPE = 'PRD.FCI_CURRENCY_PAIR' AND P.PARAM_CODE = :currId  ");
		queryCondition.setObject("currId", inputVO.getCURR_ID());
		queryCondition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
}


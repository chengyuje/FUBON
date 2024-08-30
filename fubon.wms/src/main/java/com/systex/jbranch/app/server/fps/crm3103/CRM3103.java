package com.systex.jbranch.app.server.fps.crm3103;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_DVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MVO;
import com.systex.jbranch.app.server.fps.crm341.CRM341;
import com.systex.jbranch.app.server.fps.crm341.CRM341InputVO;
import com.systex.jbranch.app.server.fps.crm381.CRM381InputVO;
import com.systex.jbranch.app.server.fps.crm381.CRM381OutputVO;
import com.systex.jbranch.app.server.fps.pms429.PMS429;
import com.systex.jbranch.app.server.fps.pms429.PMS429InputVO;
import com.systex.jbranch.app.server.fps.pms429.PMS429OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm3103")
@Scope("request")
public class CRM3103  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(CRM3103.class);
	private List<Map<String, Object>> scsslist = null ; 
	
	/**下拉式選單:所有專案**/
	public void getAllPRJ(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3103InputVO inputVO = (CRM3103InputVO) body;
		CRM3103OutputVO return_VO = new CRM3103OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, PRJ_STATUS FROM TBCRM_TRS_PRJ_MAST ");
		sql.append(" WHERE PRJ_TYPE = '1' "); //必輪調專案
		sql.append("ORDER BY PRJ_ID DESC ");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setAllPRJ(list);
		sendRtnObject(return_VO);
	}
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3103InputVO inputVO = (CRM3103InputVO) body;
		CRM3103OutputVO return_VO = new CRM3103OutputVO();
		
		if (StringUtils.isBlank(inputVO.getPRJ_ID())) {//專案代碼為必輸欄位
			throw new APException("請先選擇專案");
		}
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.PRJ_ID, B.PRJ_NAME, B.PRJ_NOTE, B.PRJ_DATE_BGN, B.PRJ_DATE_END, ");
		sql.append(" A.BRANCH_NBR, D.BRANCH_NAME, A.EMP_ID, C.EMP_NAME, A.STEP_STATUS, A.BRH_REMARKS, A.RGN_REMARKS, ");
		sql.append(" CASE WHEN TRUNC(SYSDATE) BETWEEN TRUNC(B.PRJ_DATE_BGN) AND TRUNC(B.PRJ_DATE_END) THEN 'Y' ELSE 'N' END AS EDITABLE_YN, ");
		sql.append(" (SELECT COUNT(1) FROM TBCRM_TRS_PRJ_ROTATION_D WHERE PRJ_ID = A.PRJ_ID AND BRANCH_NBR = A.BRANCH_NBR AND EMP_ID = A.EMP_ID) AS CUST_CNT, ");
		sql.append(" (SELECT COUNT(1) FROM TBCRM_TRS_PRJ_ROTATION_D WHERE PRJ_ID = A.PRJ_ID AND BRANCH_NBR = A.BRANCH_NBR AND EMP_ID = A.EMP_ID AND GO_CUST_YN = 'Y') AS GO_CUST_CNT ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_M A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID ");
		sql.append(" LEFT JOIN TBORG_MEMBER C ON C.EMP_ID = A.EMP_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO D ON D.BRANCH_NBR = A.BRANCH_NBR ");
		sql.append(" WHERE A.PRJ_ID = :prjId AND A.IMP_SUCCESS_YN = 'Y' "); //必輪調專案且匯入成功員編
		
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		// where
		if (!StringUtils.isBlank(inputVO.getEmp_id())) {
			sql.append(" AND A.EMP_ID = :empId ");
			queryCondition.setObject("empId", inputVO.getEmp_id());
		}
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND D.REGION_CENTER_ID = :center_id ");
			queryCondition.setObject("center_id", inputVO.getRegion_center_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND D.BRANCH_AREA_ID = :bra_area ");
			queryCondition.setObject("bra_area", inputVO.getBranch_area_id());
		}
		if (!StringUtils.isBlank(inputVO.getBra_nbr())) {
			sql.append(" AND A.BRANCH_NBR = :bra_nbr ");
			queryCondition.setObject("bra_nbr", inputVO.getBra_nbr());
		}
		sql.append(" ORDER BY A.BRANCH_NBR, A.EMP_ID ");
		
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}
	
	/**查詢明細資料 **/
	public void inquireDetail(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3103InputVO inputVO = (CRM3103InputVO) body;
		CRM3103OutputVO return_VO = new CRM3103OutputVO();
		return_VO.setResultList(getDetailData(inputVO));
		
		this.sendRtnObject(return_VO);
	}
	
	private List<Map<String, Object>> getDetailData(CRM3103InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT A.PRJ_ID, C.PRJ_NAME, C.PRJ_NOTE, C.PRJ_DATE_BGN, C.PRJ_DATE_END, ");
		sql.append(" A.BRANCH_NBR, E.BRANCH_NAME, A.EMP_ID, D.EMP_NAME, A.STATUS, ");
		sql.append(" A.ROT_SEQ, A.CUST_ID, F.CUST_NAME, A.AO_CODE, A.AO_TYPE, A.GO_CUST_YN, ");
		sql.append(" A.REC_SEQ, A.REC_DATE, CASE WHEN G.ATTACH_FILE IS NULL THEN 'N' ELSE 'Y' END AS ATTACH_YN, A.MODIFIER ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST C ON C.PRJ_ID = A.PRJ_ID ");
		sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D ON D.EMP_ID = A.EMP_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO E ON E.BRANCH_NBR = A.BRANCH_NBR ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST F ON F.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN TBCRM_TRS_PRJ_ROTATION_FILE G ON G.ROT_SEQ = A.ROT_SEQ ");
		sql.append(" WHERE A.PRJ_ID = :prjId "); //必輪調專案
		
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		
		if(StringUtils.isNotBlank(inputVO.getBra_nbr())) {
			sql.append(" AND A.BRANCH_NBR = :branchNbr "); 
			queryCondition.setObject("branchNbr", inputVO.getBra_nbr());
		}
		
		if(StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append(" AND A.EMP_ID = :empId "); 
			queryCondition.setObject("empId", inputVO.getEmp_id());
		}
		
		if(StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append(" AND A.CUST_ID = :custId "); 
			queryCondition.setObject("custId", inputVO.getCustId());
		}
		
		sql.append(" ORDER BY A.AO_TYPE, A.CUST_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list;
	}
	
	public void aoInquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3103InputVO inputVO = (CRM3103InputVO) body;
		CRM3103OutputVO outputVO = new CRM3103OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.EMP_ID, A.AO_CODE, A.EMP_NAME, A.TYPE, ");
		sql.append("       A.AO_CODE || '(' || CASE WHEN A.TYPE = '1' THEN '主' WHEN A.TYPE = '2' THEN '副' END || ')' AS AO_CODE_SHOW ");
		sql.append(" FROM VWORG_AO_INFO A ");
		sql.append(" WHERE A.TYPE IN ('1', '2') ");
		
		if(StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append(" AND A.EMP_ID = :empId ");
			queryCondition.setObject("empId", inputVO.getEmp_id());
		}
		if(StringUtils.isNotBlank(inputVO.getBra_nbr())) {
			sql.append(" AND A.BRA_NBR = :branchNbr ");
			queryCondition.setObject("branchNbr", inputVO.getBra_nbr());
		}
		
		sql.append(" ORDER BY A.EMP_ID, A.TYPE ");
		
		
		queryCondition.setQueryString(sql.toString());
		outputVO.setList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	//明細資料儲存
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		CRM3103InputVO inputVO = (CRM3103InputVO) body;
		CRM3103OutputVO return_VO = new CRM3103OutputVO();
		dam = this.getDataAccessManager();
		
		if(inputVO.getSaveType().matches("1|2")) { //第一階段理專儲存or送出覆核
			//檢核勾選之副CODE，是否為勾選主CODE的關係戶
			String sCodeMsg = validMinorCode(inputVO);
			if(StringUtils.isNotBlank(sCodeMsg)) {
				throw new APException("勾選之副CODE客戶ID：" + sCodeMsg + "，需為已勾選主CODE之關係戶");
			}
			
			//儲存勾選核心客戶
			for (Map<String,Object> map : inputVO.getCustList()) {
				TBCRM_TRS_PRJ_ROTATION_DVO vo = new TBCRM_TRS_PRJ_ROTATION_DVO();
				vo = (TBCRM_TRS_PRJ_ROTATION_DVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_DVO.TABLE_UID, new BigDecimal(map.get("ROT_SEQ").toString()));
				String goCustYN = ObjectUtils.toString(map.get("GO_CUST_YN"));
				
				vo.setGO_CUST_YN(StringUtils.isBlank(goCustYN) ? "N" : goCustYN);				
		        
				dam.update(vo);
			}
			//送出覆核
			TBCRM_TRS_PRJ_ROTATION_MVO rotMvo = new TBCRM_TRS_PRJ_ROTATION_MVO();
			TBCRM_TRS_PRJ_ROTATION_MPK rotMpk = new TBCRM_TRS_PRJ_ROTATION_MPK();
			rotMpk.setPRJ_ID(inputVO.getPRJ_ID());
			rotMpk.setBRANCH_NBR(inputVO.getBra_nbr());
			rotMpk.setEMP_ID(inputVO.getEmp_id());
			rotMvo = (TBCRM_TRS_PRJ_ROTATION_MVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_MVO.TABLE_UID, rotMpk);
			
			rotMvo.setSTEP_STATUS(inputVO.getSTEP_STATUS());
			
			dam.update(rotMvo);
			
		} else if(StringUtils.equals("3", inputVO.getSaveType())) { //第一階段：主管核可/退回，儲存說明欄位
			TBCRM_TRS_PRJ_ROTATION_MVO rotMvo = new TBCRM_TRS_PRJ_ROTATION_MVO();
			TBCRM_TRS_PRJ_ROTATION_MPK rotMpk = new TBCRM_TRS_PRJ_ROTATION_MPK();
			rotMpk.setPRJ_ID(inputVO.getPRJ_ID());
			rotMpk.setBRANCH_NBR(inputVO.getBra_nbr());
			rotMpk.setEMP_ID(inputVO.getEmp_id());
			rotMvo = (TBCRM_TRS_PRJ_ROTATION_MVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_MVO.TABLE_UID, rotMpk);
			
			rotMvo.setSTEP_STATUS(inputVO.getSTEP_STATUS());
			rotMvo.setBRH_REMARKS(inputVO.getBRH_REMARKS());
			rotMvo.setRGN_REMARKS(inputVO.getRGN_REMARKS());
			
			dam.update(rotMvo);
			
			//第一階段處長覆核完成後
			if(StringUtils.equals("6", inputVO.getSTEP_STATUS())) {
				//第一階段處長覆核完成後，帶走的客戶需要下載檔案/上傳檔案/輸入錄音日期序號，將狀態改為"理專下載同意書、申請書"
				//第一階段處長覆核完成後，不帶走的客戶都算"覆核完成"，不需要下載檔案/上傳檔案/輸入錄音日期序號
				QueryConditionIF queryCondition_close = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql_close = new StringBuffer();
				sql_close.append("UPDATE TBCRM_TRS_PRJ_ROTATION_D ");
				sql_close.append(" SET STATUS = (CASE WHEN GO_CUST_YN = 'Y' THEN '1' ELSE '5' END), ");
				sql_close.append(" MODIFIER = :loginId, LASTUPDATE = SYSDATE ");
				sql_close.append("WHERE PRJ_ID = :prjId AND EMP_ID = :empId AND BRANCH_NBR = :branchNbr ");
				queryCondition_close.setQueryString(sql_close.toString());
				queryCondition_close.setObject("loginId", getUserVariable(FubonSystemVariableConsts.LOGINID));
				queryCondition_close.setObject("prjId", inputVO.getPRJ_ID());
				queryCondition_close.setObject("empId", inputVO.getEmp_id());
				queryCondition_close.setObject("branchNbr", inputVO.getBra_nbr());
				dam.exeUpdate(queryCondition_close);
			}
			
		} else if(inputVO.getSaveType().matches("4|11")) { //第二階段：分行主管上傳申請書資料&儲存錄音序號、日期資料，暫存或送出覆核
			TBCRM_TRS_PRJ_ROTATION_DVO vo = (TBCRM_TRS_PRJ_ROTATION_DVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_DVO.TABLE_UID, inputVO.getROT_SEQ());
			TBCRM_TRS_PRJ_ROTATION_FILEVO fvo = (TBCRM_TRS_PRJ_ROTATION_FILEVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_FILEVO.TABLE_UID, inputVO.getROT_SEQ());
			
			if(vo == null) {
				throw new APException("查無此筆名單資料");
			} else if(StringUtils.equals("11", inputVO.getSaveType())) { //送出覆核
				//需有上傳同意書申請書，才可送出覆核
				if((fvo == null || fvo.getATTACH_FILE() == null) && StringUtils.isBlank(inputVO.getFileName())) {
					throw new APException("需上傳同意書、申請書資料，才可送出覆核");
				}
			} 
			
			//錄音序號
			if((inputVO.getREC_DATE() == null && StringUtils.isNotBlank(inputVO.getREC_SEQ())) ||
					(inputVO.getREC_DATE() != null && StringUtils.isBlank(inputVO.getREC_SEQ()))) {
				throw new APException("錄音序號與錄音日期須同時輸入");
			} else if(inputVO.getREC_DATE() != null && StringUtils.isNotBlank(inputVO.getREC_SEQ())) {
				//檢核錄音序號
				PMS429InputVO inputVO429 = new PMS429InputVO();
				inputVO429.setCustId(vo.getCUST_ID());
				inputVO429.setRecSeq(inputVO.getREC_SEQ());
				inputVO429.setRecDate(inputVO.getREC_DATE());
				PMS429 pms429 = (PMS429) PlatformContext.getBean("pms429");
				PMS429OutputVO outputVO429 = pms429.validateRecseq(inputVO429);
				if(StringUtils.isNotBlank(outputVO429.getValidateRecseqMsg())) {
					throw new APException(outputVO429.getValidateRecseqMsg());
				}
			}
			
			//儲存檔案
			if (StringUtils.isNotBlank(inputVO.getFileName())) {
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				
				if(fvo == null) {
					fvo = new TBCRM_TRS_PRJ_ROTATION_FILEVO();
					fvo.setROT_SEQ(inputVO.getROT_SEQ());
					fvo.setATTACH_FILE(ObjectUtil.byteArrToBlob(data));
					dam.create(fvo);
				} else {
					fvo.setATTACH_FILE(ObjectUtil.byteArrToBlob(data));
					dam.update(fvo);
				}
			}	
			
			vo.setREC_SEQ(inputVO.getREC_SEQ());
			vo.setREC_DATE(inputVO.getREC_DATE());	
			if(StringUtils.equals("11", inputVO.getSaveType())) { //送出覆核才變更狀態
				vo.setSTATUS("3"); //分行主管覆核中
			}
			dam.update(vo);
			
		} else if(StringUtils.equals("5", inputVO.getSaveType())) { //種行刪除理專資料
			//刪除必輪調理專轄下客戶資料
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("DELETE TBCRM_TRS_PRJ_ROTATION_D ");
			sql.append(" WHERE PRJ_ID = :prjId AND BRANCH_NBR = :branchNbr AND EMP_ID = :empID ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("prjId", inputVO.getPRJ_ID());
			queryCondition.setObject("branchNbr", inputVO.getBra_nbr());
			queryCondition.setObject("empID", inputVO.getEmp_id());
			dam.exeUpdate(queryCondition);
			
			//刪除必輪調理專資料
			TBCRM_TRS_PRJ_ROTATION_MVO rotMvo = new TBCRM_TRS_PRJ_ROTATION_MVO();
			TBCRM_TRS_PRJ_ROTATION_MPK rotMpk = new TBCRM_TRS_PRJ_ROTATION_MPK();
			rotMpk.setPRJ_ID(inputVO.getPRJ_ID());
			rotMpk.setBRANCH_NBR(inputVO.getBra_nbr());
			rotMpk.setEMP_ID(inputVO.getEmp_id());
			rotMvo = (TBCRM_TRS_PRJ_ROTATION_MVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_MVO.TABLE_UID, rotMpk);
			if(rotMvo != null) {
				dam.delete(rotMvo);
			}
		} else if(StringUtils.equals("6", inputVO.getSaveType())) { //第二階段：理專下載資況表申請書同意書後，將狀態改為"有權以上主管上傳同意書、申請書"
			TBCRM_TRS_PRJ_ROTATION_DVO vo = new TBCRM_TRS_PRJ_ROTATION_DVO();
			vo = (TBCRM_TRS_PRJ_ROTATION_DVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_DVO.TABLE_UID, inputVO.getROT_SEQ());
			
			vo.setSTATUS("2"); //有權以上主管上傳同意書、申請書
			dam.update(vo);
		} else if(inputVO.getSaveType().matches("7|8")) { //第一階段：處主管於主頁面核可/退回
			//儲存勾選客戶
			for (Map<String,Object> map : inputVO.getCustList()) {
				TBCRM_TRS_PRJ_ROTATION_MVO rotMvo = new TBCRM_TRS_PRJ_ROTATION_MVO();
				TBCRM_TRS_PRJ_ROTATION_MPK rotMpk = new TBCRM_TRS_PRJ_ROTATION_MPK();
				rotMpk.setPRJ_ID(ObjectUtils.toString(map.get("PRJ_ID")));
				rotMpk.setBRANCH_NBR(ObjectUtils.toString(map.get("BRANCH_NBR")));
				rotMpk.setEMP_ID(ObjectUtils.toString(map.get("EMP_ID")));
				rotMvo = (TBCRM_TRS_PRJ_ROTATION_MVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_MVO.TABLE_UID, rotMpk);
				
				rotMvo.setSTEP_STATUS(StringUtils.equals("7", inputVO.getSaveType()) ? "6" : "5");
				
				dam.update(rotMvo);
				
				if(StringUtils.equals("7", inputVO.getSaveType())) {
					//第一階段處長覆核完成後，帶走的客戶需要下載檔案/上傳檔案/輸入錄音日期序號，將狀態改為"理專下載同意書、申請書"
					//第一階段處長覆核完成後，不帶走的客戶都算"覆核完成"，不需要下載檔案/上傳檔案/輸入錄音日期序號
					QueryConditionIF queryCondition_close = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql_close = new StringBuffer();
					sql_close.append("UPDATE TBCRM_TRS_PRJ_ROTATION_D ");
					sql_close.append(" SET STATUS = (CASE WHEN GO_CUST_YN = 'Y' THEN '1' ELSE '5' END), ");
					sql_close.append(" MODIFIER = :loginId, LASTUPDATE = SYSDATE ");
					sql_close.append("WHERE PRJ_ID = :prjId AND EMP_ID = :empId AND BRANCH_NBR = :branchNbr ");
					queryCondition_close.setQueryString(sql_close.toString());
					queryCondition_close.setObject("loginId", getUserVariable(FubonSystemVariableConsts.LOGINID));
					queryCondition_close.setObject("prjId", ObjectUtils.toString(map.get("PRJ_ID")));
					queryCondition_close.setObject("empId", ObjectUtils.toString(map.get("EMP_ID")));
					queryCondition_close.setObject("branchNbr", ObjectUtils.toString(map.get("BRANCH_NBR")));
					dam.exeUpdate(queryCondition_close);
				}
			}
		} else if(inputVO.getSaveType().matches("9|10")) { //第二階段：行主管核可/退回
			TBCRM_TRS_PRJ_ROTATION_DVO vo = new TBCRM_TRS_PRJ_ROTATION_DVO();
			vo = (TBCRM_TRS_PRJ_ROTATION_DVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_DVO.TABLE_UID, inputVO.getROT_SEQ());
			
			//若副CODE客戶勾選為核心客戶且為覆核完成狀態，需檢核主戶是否已覆核完成，否則跳錯誤訊息
			if(StringUtils.equals("2", vo.getAO_TYPE()) && 
					StringUtils.equals("Y", vo.getGO_CUST_YN()) && 
					StringUtils.equals("9", inputVO.getSaveType())) {
				if(!isAuthMajorCode(inputVO.getROT_SEQ(), vo.getPRJ_ID(), true)) {
					throw new APException("關係戶主CODE客戶尚未覆核完成");
				}
			}
			
			vo.setSTATUS(StringUtils.equals("9", inputVO.getSaveType()) ? "5" : "4"); //第二階段分行主管核可/退回
			dam.update(vo);
			
			//檢查是否理專轄下所有客戶都已覆核完成
			//都已覆核完成，將主檔中該理專狀態改為"覆核完成"
			updateMainStatusDone(inputVO.getPRJ_ID(), inputVO.getBra_nbr(), inputVO.getEmp_id());
		} else if(StringUtils.equals("12", inputVO.getSaveType())) { //分行人員管理科(總行)刪除名單中客戶
			TBCRM_TRS_PRJ_ROTATION_DVO vo = new TBCRM_TRS_PRJ_ROTATION_DVO();
			vo = (TBCRM_TRS_PRJ_ROTATION_DVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_DVO.TABLE_UID, inputVO.getROT_SEQ());
			
			if(vo != null) {
				dam.delete(vo);
			}
		} else if(StringUtils.equals("13", inputVO.getSaveType())) { //總行人員(分行管理科)修改明細資料
			TBCRM_TRS_PRJ_ROTATION_DVO vo = new TBCRM_TRS_PRJ_ROTATION_DVO();
			vo = (TBCRM_TRS_PRJ_ROTATION_DVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_DVO.TABLE_UID, inputVO.getROT_SEQ());
			
			if(StringUtils.equals("1", inputVO.getHeadEditType())) {
				//調整名單中核心/非核心狀態
				if(!StringUtils.equals(vo.getGO_CUST_YN(), inputVO.getGO_CUST_YN())) { //有調整
					vo.setGO_CUST_YN(inputVO.getGO_CUST_YN());
					if(StringUtils.equals("Y", inputVO.getGO_CUST_YN())) {
						//若為副CODE改為核心客戶，需先檢核關係戶主CODE是否有勾選為核心客戶
						if(StringUtils.equals("2", inputVO.getAO_TYPE())) {
							if(!isAuthMajorCode(inputVO.getROT_SEQ(), vo.getPRJ_ID(), false)) {
								throw new APException("勾選之副CODE客戶ID:" + inputVO.getCUST_ID() + "，需為已勾選主CODE之關係戶");
							}
						}
						vo.setSTATUS("1"); //調整為核心狀態，該名單狀態改為"理專下再同意書、申請書"
					} else if(StringUtils.equals("N", inputVO.getGO_CUST_YN())) {
						vo.setSTATUS("5"); //調整為非核心狀態，該名單狀態改為"覆核完成"
					}
					
					dam.update(vo);
				}
			} else if(StringUtils.equals("2", inputVO.getHeadEditType())) {
				//調整理專主CODE/副CODE
				if(!StringUtils.equals(vo.getAO_CODE(), inputVO.getAO_CODE_CHG())) { //有調整
					vo.setAO_CODE(inputVO.getAO_CODE_CHG());
					vo.setAO_TYPE(inputVO.getAO_TYPE_CHG());
					dam.update(vo);
				}
			} else if(StringUtils.equals("3", inputVO.getHeadEditType())) {
				//調整名單狀態
				if(!StringUtils.equals(vo.getSTATUS(), inputVO.getSTATUS())) { //有調整
					vo.setSTATUS(inputVO.getSTATUS());
					dam.update(vo);
					
					//若狀態改為"覆核完成"
					if(StringUtils.equals("5", inputVO.getSTATUS())) {
						//若副CODE客戶勾選為核心客戶且為覆核完成狀態，需檢核主戶是否已覆核完成，否則跳錯誤訊息
						if(StringUtils.equals("2", vo.getAO_TYPE()) && 
								StringUtils.equals("Y", vo.getGO_CUST_YN())) {
							if(!isAuthMajorCode(inputVO.getROT_SEQ(), vo.getPRJ_ID(), true)) {
								throw new APException("勾選之副CODE客戶ID:" + inputVO.getCUST_ID() + "，需為已勾選主CODE之關係戶");
							}
						}
						//檢查是否理專轄下所有客戶都已覆核完成
						//都已覆核完成，將主檔中該理專狀態改為"覆核完成"
						updateMainStatusDone(vo.getPRJ_ID(), vo.getBRANCH_NBR(), vo.getEMP_ID());
					}
				}
			}
		} else if(StringUtils.equals("14", inputVO.getSaveType())) { //總行人員(分行管理科)新增客戶資料至名單中
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			//CALL PABTH_BTCRM3102.PR_ADD_CUST
			//新增客戶於理專名單明細檔中TBCRM_TRS_PRJ_ROTATION_D
			//新增客戶名單時，這個客戶仍在移轉中的流程作廢
			//如果該客戶原本不是掛這個CODE，需要同步移CODE(走移轉流程)
			sb.append(" CALL PABTH_BTCRM3102.PR_ADD_CUST(? ,? ,? ,? ,?) ");
			queryCondition.setString(1, inputVO.getPRJ_ID());
			queryCondition.setString(2, inputVO.getNEW_EMP_ID());
			queryCondition.setString(3, inputVO.getNEW_AO_CODE());
			queryCondition.setString(4, inputVO.getNEW_CUST_ID());
			queryCondition.registerOutParameter(5, Types.VARCHAR);
			queryCondition.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(queryCondition);
			String errMsg = (String) resultMap.get(5); //回傳錯誤訊息
			if(StringUtils.isNotBlank(errMsg)) {
				throw new APException(errMsg);
			}
		}
		
		this.sendRtnObject(return_VO);
	}
	
	//下載空白表單:『資況表申請書』、『主要往來分行/服務人員異動暨『客戶資產現況表』申請書』
	public void download(Object body, IPrimitiveMap header) throws Exception {
		CRM3103InputVO inputVO = (CRM3103InputVO) body;
		CRM341InputVO inputVO341 = new CRM341InputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.CUST_ID, A.BRANCH_NBR, A.AO_CODE, B.CUST_NAME, C.EMP_NAME, C.BRANCH_NAME ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST B ON B.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN VWORG_AO_INFO C ON C.AO_CODE = A.AO_CODE ");
		sql.append(" WHERE A.ROT_SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getROT_SEQ());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(list)) {
			inputVO341.setCMDTCust(true); //與CRM341下載中十保客戶邏輯相同
			inputVO341.setCust_id(ObjectUtils.toString(list.get(0).get("CUST_ID")));
			inputVO341.setFromCRM3103YN("Y");
			Map<String, String> oldAO = new HashMap<String, String>();
			Map<String, String> newAO = new HashMap<String, String>();
			oldAO.put("BRA_NBR", ObjectUtils.toString(list.get(0).get("BRANCH_NBR")));
			oldAO.put("BRANCH_NAME", ObjectUtils.toString(list.get(0).get("BRANCH_NAME")));
			oldAO.put("CUST_ID", ObjectUtils.toString(list.get(0).get("CUST_ID")));
			oldAO.put("CUST_NAME", ObjectUtils.toString(list.get(0).get("CUST_NAME")));
			oldAO.put("EMP_NAME", ObjectUtils.toString(list.get(0).get("EMP_NAME")));
			oldAO.put("AO_CODE", ObjectUtils.toString(list.get(0).get("AO_CODE")));
			newAO.put("NEW_BRA_NBR", "");
			newAO.put("PRINT_BRA_NBR", ObjectUtils.toString(list.get(0).get("BRANCH_NBR"))); //與之前列印分行為新分行不同
			newAO.put("NEW_AO_CODE", ObjectUtils.toString(list.get(0).get("AO_CODE")));
			newAO.put("NEW_BRANCH_NAME", "");
			newAO.put("NEW_EMP_NAME", ObjectUtils.toString(list.get(0).get("EMP_NAME")));
			inputVO341.setOldVOList(oldAO);
			inputVO341.setNewVOList(newAO);
			CRM341 crm341 = (CRM341) PlatformContext.getBean("crm341");
			String url = crm341.download(inputVO341);
			notifyClientToDownloadFile(url, "資況表申請書暨自主聲明書.pdf");
		}
		
		//下載後，將此筆資料狀態改為: 2:有權以上主管上傳同意書、申請書
		inputVO.setSaveType("6");
		save(inputVO, null);
		
		this.sendRtnObject(null);
	}
	
	//檢視附件:客戶已簽屬之『資況表申請書』、『客戶指定個金客戶經理自主聲明書』
	public void viewFile(Object body, IPrimitiveMap header) throws Exception {
		CRM3103InputVO inputVO = (CRM3103InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROT_SEQ, ATTACH_FILE FROM TBCRM_TRS_PRJ_ROTATION_FILE where ROT_SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getROT_SEQ());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(list)) {
			Blob blob = (Blob) list.get(0).get("ATTACH_FILE");
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			String fileName = "申請書_" + ObjectUtils.toString(list.get(0).get("ROT_SEQ")) + ".pdf";
	
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(blobAsBytes);
			fos.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		}
		
		this.sendRtnObject(null);
	}
	
	/***
	 * 檢查是否理專轄下所有客戶都已覆核完成
	 * 都已覆核完成，將主檔中該理專狀態改為"覆核完成"
	 * @param prjId
	 * @param branchNbr
	 * @param empId
	 * @throws JBranchException 
	 */
	private void updateMainStatusDone(String prjId, String branchNbr, String empId) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//檢查是否理專轄下所有客戶都已覆核完成
		sql.append("SELECT 1 FROM TBCRM_TRS_PRJ_ROTATION_D ");
		sql.append(" WHERE PRJ_ID = :prjId AND EMP_ID = :empId AND BRANCH_NBR = :branchNbr ");
		sql.append(" AND STATUS <> '5' ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("prjId", prjId);
		queryCondition.setObject("branchNbr", branchNbr);
		queryCondition.setObject("empId", empId);
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(CollectionUtils.isEmpty(list)) {
			//都已覆核完成，將主檔中該理專狀態改為"覆核完成"
			TBCRM_TRS_PRJ_ROTATION_MVO rotMvo = new TBCRM_TRS_PRJ_ROTATION_MVO();
			TBCRM_TRS_PRJ_ROTATION_MPK rotMpk = new TBCRM_TRS_PRJ_ROTATION_MPK();
			rotMpk.setPRJ_ID(prjId);
			rotMpk.setBRANCH_NBR(branchNbr);
			rotMpk.setEMP_ID(empId);
			rotMvo = (TBCRM_TRS_PRJ_ROTATION_MVO) dam.findByPKey(TBCRM_TRS_PRJ_ROTATION_MVO.TABLE_UID, rotMpk);
			if(rotMvo != null) {
				rotMvo.setSTEP_STATUS("7");
				dam.update(rotMvo);
			}
		}
	}
	
	/***
	 * 若副CODE客戶勾選為核心客戶且為覆核完成狀態，需檢核主戶是否已覆核完成，否則跳錯誤訊息
	 * 檢核關係戶主戶是否已覆核完成
	 * @param rotSeq
	 * @param prjId
	 * @param isChkCoreFinished 是否需檢核主CODE已覆核完成(若總行將副CODE由非核心客戶改為核心客戶，不須檢和主CODE已覆核完成)
	 * @return true:覆核完成	false:尚未覆核完成
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private boolean isAuthMajorCode(BigDecimal rotSeq, String prjId, boolean isChkCoreFinished) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 1 FROM ");
		sql.append(" (SELECT B.CUST_ID_S AS CUST_ID ");
		sql.append(" 	FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" 	INNER JOIN TBCRM_CUST_REL B ON B.CUST_ID_M = A.CUST_ID ");
		sql.append(" 	WHERE A.ROT_SEQ = :rotSeq AND B.REL_STATUS = 'RSN' ");
		sql.append("  UNION ");
		sql.append("  SELECT B.CUST_ID_M AS CUST_ID ");
		sql.append(" 	FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" 	INNER JOIN TBCRM_CUST_REL B ON B.CUST_ID_S = A.CUST_ID ");
		sql.append(" 	WHERE A.ROT_SEQ = :rotSeq AND B.REL_STATUS = 'RSN') A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_ROTATION_D B ON B.CUST_ID = A.CUST_ID ");
		sql.append(" WHERE B.PRJ_ID = :prjId AND B.GO_CUST_YN = 'Y' AND B.AO_TYPE = '1' ");
		if(isChkCoreFinished) sql.append(" AND B.STATUS = '5' ");
		
		queryCondition.setObject("rotSeq", rotSeq);
		queryCondition.setObject("prjId", prjId);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isEmpty(list) ? false : true;
	}
	
	/***
	 * 檢核勾選之副CODE，是否為勾選主CODE的關係戶
	 * 回傳非關係戶副CODE客戶ID
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String validMinorCode(CRM3103InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		String rtnCustId = "";
		
		if(CollectionUtils.isEmpty(inputVO.getSelectedMList())) {
			//都沒有選主CODE，所有副CODE都是非關係戶
			for (Map<String,Object> map : inputVO.getSelectedSList()) {
				String sCustId = ObjectUtils.toString(map.get("CUST_ID"));
				rtnCustId += StringUtils.isBlank(rtnCustId) ? sCustId : ("," + sCustId);
			}
		} else {
			//取得主CODE LIST
			List<String> mCustList = new ArrayList();
			for (Map map : inputVO.getSelectedMList()) {
				if (StringUtils.isNotBlank(ObjectUtils.toString(map.get("CUST_ID")))) {
					mCustList.add(ObjectUtils.toString(map.get("CUST_ID")));
				}
			}
			
			//檢查主副CODE是否互為關係戶
			for (Map<String,Object> map : inputVO.getSelectedSList()) {
				String sCustId = ObjectUtils.toString(map.get("CUST_ID"));
				if(StringUtils.isNotBlank(sCustId)) {
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql = new StringBuffer();
					sql.append("SELECT 1 FROM TBCRM_CUST_REL ");
					sql.append(" WHERE CUST_ID_M = :sCustId AND CUST_ID_M <> CUST_ID_S ");
					sql.append(" AND CUST_ID_S IN (:mCustList) AND REL_STATUS = 'RSN' ");
					sql.append(" UNION ");
					sql.append("SELECT 1 FROM TBCRM_CUST_REL ");
					sql.append(" WHERE CUST_ID_S = :sCustId AND CUST_ID_M <> CUST_ID_S ");
					sql.append(" AND CUST_ID_M IN (:mCustList) AND REL_STATUS = 'RSN' ");
					
					queryCondition.setObject("sCustId", sCustId);
					queryCondition.setObject("mCustList", mCustList);
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					
					if(CollectionUtils.isEmpty(list)) { //非關係戶，回傳副CODE CUST_ID
						rtnCustId += StringUtils.isBlank(rtnCustId) ? sCustId : ("," + sCustId);
					}
				}
			}
		}
		
		return rtnCustId;
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {

		CRM3103InputVO inputVO = (CRM3103InputVO) body;

		if (StringUtils.isBlank(inputVO.getPRJ_ID())) {//專案代碼為必輸欄位
			throw new APException("請先選擇專案");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = "輪調專案名單_" + sdf.format(new Date()) + ".csv";
		XmlInfo xmlInfo = new XmlInfo();

		Map<String, String> comm_yn = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
		Map<String, String> STEP_STATUS = xmlInfo.doGetVariable("CRM.TRS_PRJ_ROT_STEP_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> STATUS_D = xmlInfo.doGetVariable("CRM.TRS_PRJ_ROT_STATUS_D", FormatHelper.FORMAT_3);

		String[] csvHeader = getCsvHeader();
		String[] csvMain = getCsvMain();
		List<Object[]> csvData = new ArrayList<Object[]>();

		for (Map<String, Object> map : inputVO.isPrintAllData() ? getExportAllData(inputVO.getPRJ_ID()) : inputVO.getPrintList()) {

			String[] records = new String[csvHeader.length];
			map.put("BRANCH", checkIsNull(map, "BRANCH_NBR") + " - " + checkIsNull(map, "BRANCH_NAME"));
			map.put("EMP", checkIsNull(map, "EMP_ID") + " - " + checkIsNull(map, "EMP_NAME"));

			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
				case "GO_CUST_YN":
					records[i] = comm_yn.get(checkIsNull(map, csvMain[i]));
					break;
				case "STEP_STATUS":
					records[i] = STEP_STATUS.get(checkIsNull(map, csvMain[i]));
					break;
				case "STATUS":
					records[i] = STATUS_D.get(checkIsNull(map, csvMain[i]));
					break;
				case "REC_DATE":
					if (!inputVO.isPrintAllData() && StringUtils.isNotBlank(checkIsNull(map, csvMain[i]))) {
						records[i] = sdf2.format(new Date(changeStringToLong(checkIsNull(map, csvMain[i]))));
					} else {
						records[i] = checkIsNull(map, csvMain[i]);
					}
					break;
				default:
					records[i] = checkIsNull(map, csvMain[i]);
					break;
				}
			}
			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);
	}
	
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	private long changeStringToLong(String str) {
		BigDecimal bd = new BigDecimal(str);
		return bd.longValue();
	}
	
	private String[] getCsvHeader() {
		String[] str = { "業務處", "區別", "歸屬行", "服務理專", "AOCODE", "身分證號", "客戶姓名", "狀態", "第二階段狀態", "帶走的客戶", "錄音序號", "錄音日期"};
		return str;
	}

	private String[] getCsvMain() {
		String[] str = { "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH", "EMP", "AO_CODE", "CUST_ID", "CUST_NAME", "STEP_STATUS", "STATUS", "GO_CUST_YN", "REC_SEQ", "REC_DATE"};
		return str;
	}
	
	private List<Map<String, Object>> getExportAllData(String prjID) throws JBranchException, ParseException {
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT A.PRJ_ID, C.PRJ_NAME, C.PRJ_NOTE, C.PRJ_DATE_BGN, C.PRJ_DATE_END, ");
		sql.append(" A.BRANCH_NBR, ORG.BRANCH_NAME, A.EMP_ID, D.EMP_NAME, ");
		sql.append(" A.ROT_SEQ, A.CUST_ID, F.CUST_NAME, A.AO_CODE, A.AO_TYPE, A.GO_CUST_YN, ");
		sql.append(" A.REC_SEQ, A.REC_DATE, CASE WHEN G.ATTACH_FILE IS NULL THEN 'N' ELSE 'Y' END AS ATTACH_YN, ");
		sql.append(" B.STEP_STATUS, ORG.REGION_CENTER_NAME, ORG.BRANCH_AREA_NAME, A.STATUS ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_ROTATION_M B ON B.PRJ_ID = A.PRJ_ID AND B.BRANCH_NBR = A.BRANCH_NBR AND B.EMP_ID = A.EMP_ID ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST C ON C.PRJ_ID = A.PRJ_ID ");
		sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D ON D.EMP_ID = A.EMP_ID ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST F ON F.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN TBCRM_TRS_PRJ_ROTATION_FILE G ON G.ROT_SEQ = A.ROT_SEQ ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO ORG ON ORG.BRANCH_NBR = A.BRANCH_NBR ");
		sql.append(" WHERE A.PRJ_ID = :prjId "); //必輪調專案
		sql.append(" ORDER BY A.AO_TYPE, A.CUST_ID ");
		queryCondition.setObject("prjId", prjID);
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list;
	}
}

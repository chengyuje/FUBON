package com.systex.jbranch.app.server.fps.org111;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBER_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBER_ROLEPK;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBER_ROLEVO;
import com.systex.jbranch.app.common.fps.table.TBORG_RESIGN_MEMOVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org111")
@Scope("request")
public class ORG111 extends FubonWmsBizLogic {

	public void empRo(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG111OutputVO return_VO = new ORG111OutputVO();
		ORG111InputVO inputVO = (ORG111InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.ROLE_ID ||'-'|| B.ROLE_NAME AS NAME, A.ROLE_ID, B.ROLE_NAME, B.JOB_TITLE_NAME ");
		sql.append("FROM( ");
		sql.append("  SELECT ROLE.ROLE_ID ");
		sql.append("  FROM TBORG_ROLE ROLE ");
		sql.append("  MINUS ");
		sql.append("  SELECT MEMBER.ROLE_ID ");
		sql.append("  FROM TBORG_MEMBER_ROLE MEMBER ");
		sql.append("  WHERE MEMBER.EMP_ID LIKE :empID ");
		sql.append(") A ");
		sql.append("LEFT JOIN TBORG_ROLE B ON A.ROLE_ID = B.ROLE_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND JOB_TITLE_NAME IS NULL ");
		sql.append("ORDER BY ROLE_ID ");

		if (!StringUtils.isBlank(inputVO.getEMP_ID())) {
			queryCondition.setObject("empID", "%" + inputVO.getEMP_ID() + "%");
		} else {
			queryCondition.setObject("empID", "");
		}
		
		queryCondition.setQueryString(sql.toString());

		return_VO.setRolist(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(return_VO);
	}

	/**
	 * 
	 * 上傳照片
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private BigDecimal seqNo = null;
	
	public void addData(Object body, IPrimitiveMap header) throws Exception {
		ORG111InputVO inputVO = (ORG111InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBORG_MEMBER_REVIEWVO reviewVO = new TBORG_MEMBER_REVIEWVO();
			
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQNO, EMP_ID ");
		sql.append("FROM TBORG_MEMBER_REVIEW ");
		sql.append("WHERE REVIEW_STATUS LIKE 'W' ");
		sql.append("AND EMP_ID LIKE :empID ");
		queryCondition.setObject("empID", inputVO.getEMP_ID());
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> temp = dam.exeQuery(queryCondition);
		if(temp.size() > 0){
			Map<String, Object> seqNoMap = temp.get(0);
			seqNo = (BigDecimal) seqNoMap.get("SEQNO");
			reviewVO = (TBORG_MEMBER_REVIEWVO) dam.findByPKey(TBORG_MEMBER_REVIEWVO.TABLE_UID, seqNo);
			if (!StringUtils.isBlank(inputVO.getPictureName())) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);			
				String joinedPath = new File(tempPath, inputVO.getPictureName()).toString();				
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				reviewVO.setEMP_PHOTO(ObjectUtil.byteArrToBlob(data));			
			}
			dam.update(reviewVO);
		}else{
			seqNo = new BigDecimal(getSN(dam));
			if (!StringUtils.isBlank(inputVO.getPictureName())) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String joinedPath = new File(tempPath, inputVO.getPictureName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				reviewVO.setEMP_PHOTO(ObjectUtil.byteArrToBlob(data));	
			}
			reviewVO.setSEQNO(seqNo);
			reviewVO.setEMP_ID(inputVO.getEMP_ID());
			reviewVO.setREVIEW_STATUS("W");
			dam.create(reviewVO);
		}
		
		// 在commit後更新long raw欄位 --Jemmy 2017-6-23
		if (!TransactionSynchronizationManager.getSynchronizations().isEmpty()) {	// 避免重複註冊，暫訂只有一個Adapter
			TransactionSynchronizationManager.getSynchronizations().clear();
		}
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
            public void afterCommit(){
            	updateEmpPhotoRaw(seqNo, null);
            }
        });
		// end --Jemmy 2017-6-23
		this.sendRtnObject(null);
	}

	/**
	 * 
	 * 判斷表TBSYSPARAMTYPE是否包含PARAM_TYPE為指定值的資料。
	 * 
	 * @param paramType
	 * @return
	 * @throws JBranchException
	 */
	private boolean checkContainemp(String EMP_ID) throws JBranchException {
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT 1 ");
		sb.append("FROM TBORG_RESIGN_MEMO A ");
		sb.append("WHERE A.EMP_ID = :empID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("empID", EMP_ID);
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 主畫面UPDATE
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void updateEmpData(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG111InputVO inputVO = (ORG111InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBORG_MEMBERVO mvo = new TBORG_MEMBERVO();
		mvo = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, inputVO.getEMP_ID());
		
		if (mvo != null) {
			TBORG_MEMBER_REVIEWVO paramVO = null;
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SEQNO, EMP_ID ");
			sql.append("FROM TBORG_MEMBER_REVIEW ");
			sql.append("WHERE REVIEW_STATUS LIKE 'W' ");
			sql.append("AND EMP_ID LIKE :empID ");
			queryCondition.setObject("empID", inputVO.getEMP_ID());
			
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> temp = dam.exeQuery(queryCondition);
			
			if(temp.size() > 0){
				Map<String, Object> seqNoMap = temp.get(0);
				seqNo = (BigDecimal) seqNoMap.get("SEQNO");
				paramVO =  (TBORG_MEMBER_REVIEWVO) dam.findByPKey(TBORG_MEMBER_REVIEWVO.TABLE_UID, seqNo);
				if (null != paramVO) {
					paramVO.setEMP_ID(mvo.getEMP_ID());
					paramVO.setCUST_ID(mvo.getCUST_ID());
					paramVO.setEMP_NAME(mvo.getEMP_NAME());
					paramVO.setDEPT_ID(mvo.getDEPT_ID());
					paramVO.setEMP_CELL_NUM(inputVO.getEMP_CELL_NUM());
					paramVO.setEMP_DEPT_EXT(inputVO.getEMP_DEPT_EXT());
					paramVO.setEMP_FAX(mvo.getEMP_FAX());
					paramVO.setJOB_TITLE_NAME(mvo.getJOB_TITLE_NAME());
					paramVO.setJOB_RANK(mvo.getJOB_RANK());
					paramVO.setEXPECTED_END_DATE(mvo.getEXPECTED_END_DATE());
					paramVO.setGROUP_TYPE(mvo.getGROUP_TYPE());
					paramVO.setONBOARD_DATE(mvo.getONBOARD_DATE());
					paramVO.setJOB_ONBOARD_DATE(mvo.getJOB_ONBOARD_DATE());
					paramVO.setJOB_RESIGN_DATE(mvo.getJOB_RESIGN_DATE());
					paramVO.setSALES_SUP_EMP_ID(mvo.getSALES_SUP_EMP_ID());
					paramVO.setEMP_MAIL_ADDRESS(mvo.getEMP_MAIL_ADDRESS());
					paramVO.setEMP_EMAIL_ADDRESS(mvo.getEMP_EMAIL_ADDRESS());
					paramVO.setEMP_PHONE_NUM(mvo.getEMP_PHONE_NUM());
					paramVO.setPERF_EFF_DATE(mvo.getPERF_EFF_DATE());
					paramVO.setHANDOVER_FLAG(mvo.getHANDOVER_FLAG());
					paramVO.setHANDOVER_DATE(mvo.getHANDOVER_DATE());
					paramVO.setREMARK(inputVO.getREMARK());
					paramVO.setSERVICE_FLAG(mvo.getSERVICE_FLAG());
					paramVO.setCHANGE_FLAG(mvo.getCHANGE_FLAG());
					paramVO.setREVIEW_STATUS("W");
					paramVO.setJOB_POSITION(mvo.getJOB_POSITION());
					paramVO.setJOB_GOAL_DATE(mvo.getJOB_GOAL_DATE());
					if (paramVO.getEMP_PHOTO() == null)			// Add by Jemmy at 2017-07-04, 避免上傳照片被覆蓋
						paramVO.setEMP_PHOTO(mvo.getEMP_PHOTO());
//					paramVO.setEMP_PHOTO_RAW(mvo.getEMP_PHOTO_RAW());					
					dam.update(paramVO);
				}
			} else {
				seqNo = new BigDecimal(getSN(dam));
				paramVO = new TBORG_MEMBER_REVIEWVO();
				paramVO.setSEQNO(seqNo);
				paramVO.setEMP_ID(mvo.getEMP_ID());
				paramVO.setCUST_ID(mvo.getCUST_ID());
				paramVO.setEMP_NAME(mvo.getEMP_NAME());
				paramVO.setDEPT_ID(mvo.getDEPT_ID());
				paramVO.setEMP_CELL_NUM(inputVO.getEMP_CELL_NUM());
				paramVO.setEMP_DEPT_EXT(inputVO.getEMP_DEPT_EXT());
				paramVO.setEMP_FAX(mvo.getEMP_FAX());
				paramVO.setJOB_TITLE_NAME(mvo.getJOB_TITLE_NAME());
				paramVO.setJOB_RANK(mvo.getJOB_RANK());
				paramVO.setEXPECTED_END_DATE(mvo.getEXPECTED_END_DATE());
				paramVO.setGROUP_TYPE(mvo.getGROUP_TYPE());
				paramVO.setONBOARD_DATE(mvo.getONBOARD_DATE());
				paramVO.setJOB_ONBOARD_DATE(mvo.getJOB_ONBOARD_DATE());
				paramVO.setJOB_RESIGN_DATE(mvo.getJOB_RESIGN_DATE());
				paramVO.setSALES_SUP_EMP_ID(mvo.getSALES_SUP_EMP_ID());
				paramVO.setEMP_MAIL_ADDRESS(mvo.getEMP_MAIL_ADDRESS());
				paramVO.setEMP_EMAIL_ADDRESS(mvo.getEMP_EMAIL_ADDRESS());
				paramVO.setEMP_PHONE_NUM(mvo.getEMP_PHONE_NUM());
				paramVO.setPERF_EFF_DATE(mvo.getPERF_EFF_DATE());
				paramVO.setHANDOVER_FLAG(mvo.getHANDOVER_FLAG());
				paramVO.setHANDOVER_DATE(mvo.getHANDOVER_DATE());
				paramVO.setREMARK(inputVO.getREMARK());
				paramVO.setSERVICE_FLAG(mvo.getSERVICE_FLAG());
				paramVO.setCHANGE_FLAG(mvo.getCHANGE_FLAG());
				paramVO.setREVIEW_STATUS("W");
				paramVO.setJOB_POSITION(mvo.getJOB_POSITION());
				paramVO.setJOB_GOAL_DATE(mvo.getJOB_GOAL_DATE());
				if (paramVO.getEMP_PHOTO() == null)		// Add by Jemmy at 2017-07-04, 避免上傳照片被覆蓋
					paramVO.setEMP_PHOTO(mvo.getEMP_PHOTO());
//				paramVO.setEMP_PHOTO_RAW(mvo.getEMP_PHOTO_RAW());		
				dam.create(paramVO);
			}
		}
		sendRtnObject(null);
	}

	/** 產生seq No */
	private String getSN(DataAccessManager dam) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_MEMBER_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	/**
	 * 
	 * 新增角色
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void addRole(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG111OrgRoleVO inputVO = (ORG111OrgRoleVO) body;
	
		TBORG_MEMBER_ROLEPK pk = new TBORG_MEMBER_ROLEPK();
		DataAccessManager dam = this.getDataAccessManager();
		pk.setEMP_ID(inputVO.getEMP_ID());
		pk.setROLE_ID(inputVO.getROLE_ID());

		if (inputVO.getIsPrimaryRole() != null) {
			pk.setIS_PRIMARY_ROLE(inputVO.getIsPrimaryRole());
		} else {
			pk.setIS_PRIMARY_ROLE("N");
		}
		TBORG_MEMBER_ROLEVO paramVO = new TBORG_MEMBER_ROLEVO();
		paramVO.setcomp_id(pk);
		
		dam.create(paramVO);
		
		sendRtnObject(null);
	}

	/**
	 * 
	 * 離職交接
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void resignHandover(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG111ResignMemoVO inputVO = (ORG111ResignMemoVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		if (!checkContainemp(inputVO.getEMP_ID())) {
			TBORG_RESIGN_MEMOVO paramVO = new TBORG_RESIGN_MEMOVO();
			paramVO.setEMP_ID(inputVO.getEMP_ID());
			paramVO.setDESTINATION_BANK_ID(inputVO.getDESTINATION_BANK_ID());			
			paramVO.setRESIGN_HANDOVER(inputVO.getRESIGN_HANDOVER());		
			paramVO.setRESIGN_DESTINATION(inputVO.getRESIGN_DESTINATION());
			paramVO.setRESIGN_REASON(inputVO.getRESIGN_REASON());
			paramVO.setRESIGN_DATE(new Timestamp(System.currentTimeMillis()));
			dam.create(paramVO);
		} else {
			TBORG_RESIGN_MEMOVO paramVO = (TBORG_RESIGN_MEMOVO) dam.findByPKey(TBORG_RESIGN_MEMOVO.TABLE_UID, inputVO.getEMP_ID());
			paramVO.setDESTINATION_BANK_ID(inputVO.getDESTINATION_BANK_ID());
			paramVO.setRESIGN_HANDOVER(inputVO.getRESIGN_HANDOVER());
			paramVO.setRESIGN_DESTINATION(inputVO.getRESIGN_DESTINATION());
			paramVO.setRESIGN_REASON(inputVO.getRESIGN_REASON());
			dam.update(paramVO);
		}
		
		sendRtnObject(null);
	}
	
	public void getResignMemo(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG111ResignMemoVO inputVO = (ORG111ResignMemoVO) body;
		ORG111ResignMemoVO outputVO = null;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT EMP_ID, RESIGN_HANDOVER, RESIGN_REASON, RESIGN_DESTINATION, DESTINATION_BANK_ID ");
		sql.append("FROM TBORG_RESIGN_MEMO ");
		sql.append("WHERE EMP_ID LIKE :empID ");
		
		queryCondition.setObject("empID", "%" + inputVO.getEMP_ID() + "%");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> temp = dam.exeQuery(queryCondition);
		if(temp.size() > 0){
			outputVO = new ORG111ResignMemoVO();
			outputVO.setResignList(temp);			
		}
		sendRtnObject(outputVO);
	}
	

	/**
	 * 
	 * 主畫面刪除
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void delRole(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG111InputVO inputVO = (ORG111InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();

		TBORG_MEMBER_ROLEPK pk = new TBORG_MEMBER_ROLEPK();
		pk.setEMP_ID(inputVO.getEMP_ID());
		pk.setROLE_ID(inputVO.getROLE_ID());
		pk.setIS_PRIMARY_ROLE(inputVO.getIS_PRIMARY_ROLE());
		TBORG_MEMBER_ROLEVO mvo = new TBORG_MEMBER_ROLEVO();
		mvo = (TBORG_MEMBER_ROLEVO) dam.findByPKey(TBORG_MEMBER_ROLEVO.TABLE_UID, pk);
		if (mvo != null) {
			dam.delete(mvo);
		} else {
			throw new APException("ehl_01_common_005");	// 顯示資料不存在
		}
		
		sendRtnObject(null);
	}

	/**
	 * 
	 * 主畫面SELECT
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 * 
	 */

	public void confirmEmp(Object body, IPrimitiveMap header) throws JBranchException, SQLException {

		ORG111InputVO inputVO = (ORG111InputVO) body;
		ORG111OutputVO outputVO = new ORG111OutputVO();
		outputVO.setQueryType(inputVO.getQueryType());
		DataAccessManager dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.SEQNO, ROLE.IS_PRIMARY_ROLE, A.REVIEW_STATUS, A.EMP_ID, A.EMP_NAME, A.JOB_TITLE_NAME, ");
		sql.append("	   A.JOB_ONBOARD_DATE, PABTH_BTPMS902_EMP_N.FC_GET_SH_EMPID(A.EMP_ID) AS SALES_SUP_EMP_ID, SUP.EMP_NAME AS SALES_SUP_EMP_NAME, ");
		sql.append("	   A.GROUP_TYPE, A.PERF_EFF_DATE, A.EMP_MAIL_ADDRESS, A.EMP_EMAIL_ADDRESS, ");
		sql.append("	   A.EMP_PHONE_NUM, A.EMP_FAX, A.EMP_CELL_NUM, A.EMP_DEPT_EXT, A.REMARK, ");
		sql.append("	   B.BRANCH_NBR, B.BRANCH_NAME, A.EMP_PHOTO ");
		sql.append("FROM TBORG_MEMBER_REVIEW A ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.EMP_ID = B.EMP_ID ");
		sql.append("LEFT JOIN (SELECT EMP_ID, IS_PRIMARY_ROLE FROM TBORG_MEMBER_ROLE WHERE IS_PRIMARY_ROLE = 'Y') ROLE ON A.EMP_ID = ROLE.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER SUP ON PABTH_BTPMS902_EMP_N.FC_GET_SH_EMPID(A.EMP_ID) = SUP.EMP_ID ");
		sql.append("WHERE A.REVIEW_STATUS LIKE 'W' ");

		if (!"".equals(inputVO.getEMP_ID())) {
			sql.append("AND A.EMP_ID LIKE :empID ");
			queryCondition.setObject("empID", inputVO.getEMP_ID());
		}

		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0){
			outputVO.setEmpDataLst(list);
			outputVO.setLoginPrivilegeID(getPrivilegeID("confirm"));
		} else {
			throw new APException("該員工暫時無員工資料");
		}

		this.sendRtnObject(outputVO);

	}
	
	public void reviewEmp(Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		
		ORG111InputVO inputVO = (ORG111InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		logger.info("JIMMY:1 REVIEW_STATUS = {}, {} @" + inputVO.getEMP_ID(), inputVO.getREVIEW_STATUS(), inputVO.getSEQNO());
		if("Y".equals(inputVO.getREVIEW_STATUS())){
			// 修改 TBORG_MEMBER_REVIEW 的 REVIEW_STATUS
			TBORG_MEMBER_REVIEWVO rewVO = (TBORG_MEMBER_REVIEWVO) dam.findByPKey(TBORG_MEMBER_REVIEWVO.TABLE_UID, new BigDecimal(inputVO.getSEQNO()));
			if(rewVO != null){
				logger.info("JIMMY:2 REVIEW_STATUS = {}", inputVO.getREVIEW_STATUS());
				rewVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
				dam.update(rewVO);				
			}
			
			// 修改 TBORG_MEMBER 的員工資料
			TBORG_MEMBERVO memberVO = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, inputVO.getEMP_ID());
			logger.info("JIMMY:3 EMPID = {}", inputVO.getEMP_ID());
			if(memberVO != null){
				memberVO.setEMP_CELL_NUM(rewVO.getEMP_CELL_NUM());
				memberVO.setEMP_DEPT_EXT(rewVO.getEMP_DEPT_EXT());
				memberVO.setREMARK(rewVO.getREMARK());
				memberVO.setEMP_PHOTO(rewVO.getEMP_PHOTO());
//				memberVO.setEMP_PHOTO_RAW(rewVO.getEMP_PHOTO_RAW());
				dam.update(memberVO);	
				// 在commit後更新long raw欄位 --Jemmy 2017-6-30
				logger.info("JIMMY:4 size = {}", TransactionSynchronizationManager.getSynchronizations().size());
				if (!TransactionSynchronizationManager.getSynchronizations().isEmpty()) {	// 避免重複註冊，暫訂只有一個Adapter
					logger.info("JIMMY:5 clear");
					TransactionSynchronizationManager.getSynchronizations().clear();
				}
				final String empId = inputVO.getEMP_ID();
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
		            public void afterCommit(){
		            	logger.info("JIMMY:6 EMPID = {}", empId);
				    	updateEmpPhotoRaw(null, empId);
		            }
		        });
				logger.info("JIMMY:7 finish");
				// end --Jemmy 2017-6-30		
			}			
			logger.info("JIMMY:8 Status = Y finish");
		}
		
		if("N".equals(inputVO.getREVIEW_STATUS())){
			// 修改 TBORG_MEMBER_REVIEW 的 REVIEW_STATUS
			TBORG_MEMBER_REVIEWVO rewVO = (TBORG_MEMBER_REVIEWVO) dam.findByPKey(TBORG_MEMBER_REVIEWVO.TABLE_UID, new BigDecimal(inputVO.getSEQNO()));
			if(rewVO != null){
				logger.info("JIMMY:9 REVIEW_STATUS = {}", inputVO.getREVIEW_STATUS());
				rewVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
				dam.update(rewVO);				
			}
			logger.info("JIMMY:10 Status = N finish");
		}
		logger.info("JIMMY:11 Finish");
		this.sendRtnObject(null);
	}
	
	// === 查詢登入者的PRIVILEGEID
	private String getPrivilegeID(String type) throws JBranchException, SQLException{
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :loginRoleID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("loginRoleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
		if (tempList.size() > 0) {
			return (String) tempList.get(0).get("PRIVILEGEID");
		}
		
		return null;
	}

	public void getEmpData(Object body, IPrimitiveMap header) throws JBranchException, SQLException {

		ORG111InputVO inputVO = (ORG111InputVO) body;
		ORG111OutputVO outputVO = new ORG111OutputVO();
		outputVO.setQueryType(inputVO.getQueryType());
		DataAccessManager dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROLE.IS_PRIMARY_ROLE, A.REVIEW_STATUS, A.EMP_ID, A.EMP_NAME, A.JOB_TITLE_NAME, ");
		sql.append("       A.JOB_ONBOARD_DATE, PABTH_BTPMS902_EMP_N.FC_GET_SH_EMPID(A.EMP_ID) AS SALES_SUP_EMP_ID, SUP.EMP_NAME AS SALES_SUP_EMP_NAME, ");
		sql.append("       A.GROUP_TYPE, A.PERF_EFF_DATE, A.EMP_MAIL_ADDRESS, A.EMP_EMAIL_ADDRESS, ");
		sql.append("       A.EMP_PHONE_NUM, A.EMP_FAX, A.EMP_CELL_NUM, A.EMP_DEPT_EXT, A.REMARK, ");
		sql.append("       B.BRANCH_NBR, B.BRANCH_NAME, A.EMP_PHOTO ");
		sql.append("FROM TBORG_MEMBER A ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.EMP_ID = B.EMP_ID ");
		sql.append("LEFT JOIN (SELECT EMP_ID, IS_PRIMARY_ROLE FROM TBORG_MEMBER_ROLE WHERE IS_PRIMARY_ROLE = 'Y') ROLE ON A.EMP_ID = ROLE.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER SUP ON PABTH_BTPMS902_EMP_N.FC_GET_SH_EMPID(A.EMP_ID) = SUP.EMP_ID ");
		sql.append("WHERE 1 = 1 ");

		if (!"".equals(inputVO.getEMP_ID())) {
			sql.append("AND A.EMP_ID LIKE :empID ");
			queryCondition.setObject("empID", inputVO.getEMP_ID());
		}

		sql.append("ORDER BY IS_PRIMARY_ROLE DESC ");

		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0){
			outputVO.setEmpDataLst(list);
			outputVO.setLoginPrivilegeID(getPrivilegeID("query"));
		} else {
			throw new APException("該員工暫時無員工資料");
		}
		
		this.sendRtnObject(outputVO);
	}

	/**
	 * 
	 * 主畫面角色DATA
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */

	public void getRoData(Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		
		ORG111InputVO inputVO = (ORG111InputVO) body;
		ORG111OutputVO outputVO = new ORG111OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT A.EMP_ID, A.ROLE_ID, B.ROLE_NAME, A.IS_PRIMARY_ROLE ");
		sb.append("FROM TBORG_MEMBER_ROLE A ");
		sb.append("LEFT JOIN TBORG_ROLE B ON A.ROLE_ID = B.ROLE_ID ");
		sb.append("WHERE 1 = 1 ");

		if (!"".equals(inputVO.getEMP_ID())) {
			sb.append("AND A.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEMP_ID());
		}

		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				map.put("privID", getPrivilegeID(""));
			}		
		} else {
			throw new APException("查無該職員角色資料");
		}

		outputVO.setEmpRoleLst(list);
		outputVO.setQueryType(inputVO.getQueryType());

		this.sendRtnObject(outputVO);
	}

	/**
	 * 
	 * 主畫面照片DATA
	 * 原程式有個Issue是記錄與照片檔對不上，係因list預設排序邏輯沒對到。是故先找Y再找W最後才找N, 兩筆Y以上則選最近的seqNo
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */
	public void getPoData(Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		ORG111InputVO inputVO = (ORG111InputVO) body;
		ORG111OutputVO outputVO = new ORG111OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CASE WHEN MEM.EMP_PHOTO IS NOT NULL THEN MEM.EMP_PHOTO ELSE MEMR.EMP_PHOTO END AS EMP_PHOTO ");
		sb.append("FROM TBORG_MEMBER MEM ");
		sb.append("LEFT JOIN (SELECT EMP_ID, EMP_PHOTO FROM TBORG_MEMBER_REVIEW WHERE REVIEW_STATUS = 'W') MEMR ON MEMR.EMP_ID = MEM.EMP_ID ");
		sb.append("WHERE MEM.EMP_ID = :empID ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("empID", inputVO.getEMP_ID());
		
		List<Map<String, Object>> listPhoto = dam.exeQuery(queryCondition);
		if(listPhoto.size() > 0) {
			for (Map<String, Object> map : listPhoto) {
				if (null != map.get("EMP_PHOTO")) {			
					try {
						Blob blob = (Blob) map.get("EMP_PHOTO");
						int blobLength = (int) blob.length();
						byte[] blobAsBytes = blob.getBytes(1, blobLength);
						
						map.put("EMP_PHOTO", blobAsBytes);
						blob.free();
					} catch (Exception e) {
						throw new APException("圖片忙碌中請重新整理!");
					}
				}
			}
		}
		
		outputVO.setResultList(listPhoto);
		outputVO.setQueryType(inputVO.getQueryType());
		this.sendRtnObject(outputVO);
	}
	
	public void getReviewPhoto(Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		
		ORG111InputVO inputVO = (ORG111InputVO) body;
		ORG111OutputVO outputVO = new ORG111OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQNO, EMP_ID, CUST_ID, EMP_NAME, DEPT_ID, EMP_CELL_NUM, EMP_DEPT_EXT, EMP_FAX, JOB_TITLE_NAME, JOB_RANK, EXPECTED_END_DATE, GROUP_TYPE, ONBOARD_DATE, JOB_ONBOARD_DATE, JOB_RESIGN_DATE, SALES_SUP_EMP_ID, EMP_MAIL_ADDRESS, EMP_EMAIL_ADDRESS, EMP_PHONE_NUM, PERF_EFF_DATE, HANDOVER_FLAG, HANDOVER_DATE, REMARK, EMP_PHOTO, SERVICE_FLAG, CHANGE_FLAG, REVIEW_STATUS ");
		sb.append("FROM TBORG_MEMBER_REVIEW ");
		sb.append("WHERE REVIEW_STATUS = 'W' ");
		
		if (StringUtils.isNotBlank(inputVO.getEMP_ID())) {
			sb.append("AND EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEMP_ID());
		}
		
		queryCondition.setQueryString(sb.toString());
				
		List<Map<String, Object>> listPhoto = dam.exeQuery(queryCondition);

		if(listPhoto.size() > 0){
			for (Map<String, Object> map : listPhoto) {
				if (null != map.get("EMP_PHOTO")) {
					try {
						Blob blob = (Blob) map.get("EMP_PHOTO");
						int blobLength = (int) blob.length();
						byte[] blobAsBytes = blob.getBytes(1, blobLength);
						
						map.put("EMP_PHOTO", blobAsBytes);
						blob.free();
					} catch (Exception e) {
						throw new APException("圖片忙碌中請重新整理!");
					}
				}
			}
		}
		
		outputVO.setResultList(listPhoto);
		outputVO.setQueryType(inputVO.getQueryType());
		
		this.sendRtnObject(outputVO);
	}

	/**
	 * 
	 * 證照
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */
	public void getTiData(Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		
		ORG111InputVO inputVO = (ORG111InputVO) body;
		ORG111OutputVO outputVO = new ORG111OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("WITH CERT AS ( ");
		sb.append("  SELECT MR.IS_PRIMARY_ROLE, M.EMP_ID, MR.ROLE_ID, R.ROLE_NAME, PRI.PRIVILEGEID, CERT_TYPE.PARAM_TYPE AS CERT_LIST_NAME, CERT_LIST.PARAM_CODE AS CERT_ID, CERT_LIST.PARAM_NAME AS CERT_NAME, CERT_LIST.PARAM_DESC AS CERT_DESC, CERT_LIST.PARAM_ORDER AS CERT_ORDER, ");
		sb.append("         CASE WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL AND (MC.UNREG_DATE IS NOT NULL AND TO_CHAR(MC.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC.UNREG_DATE, 'yyyyMMdd')) THEN NULL "); // 若為 必要取得證照 且 人資證照檔登錄日期有值 且(註銷日有值 且 登錄日 < 註銷日) → 顯示空值
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') "); // 若為 必要取得證照 且 人資證照檔登錄日期有值 → 顯示人資證照檔登錄日期
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL AND (MC_INS.UNREG_DATE IS NOT NULL AND TO_CHAR(MC_INS.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC_INS.UNREG_DATE, 'yyyyMMdd')) THEN NULL "); // 若為 必要取得證照 且 人資證照檔登錄日期為空值 且 保險證照為01(Cert0011)/02(Cert0014)/03(Cert0051)/04(Cert0014) 且 人資證照檔登錄日期有值 且(註銷日有值 且 登錄日 < 註銷日) → 顯示空值
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL THEN TO_CHAR(MC_INS.REG_DATE, 'yyyy/MM/dd') "); // 若為 必要取得證照 且 人資證照檔登錄日期為空值 且 保險證照為01(Cert0011)/02(Cert0014)/03(Cert0051)/04(Cert0014)  → 顯示保險證照檔登錄日期
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') "); 
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NOT NULL THEN TO_CHAR(MC.CERTIFICATE_GET_DATE, 'yyyy/MM/dd') "); // 若為 非必要取得證照 且 人資證照檔登錄日期為空值 且 人資證照檔取得日期有值 → 顯示人資證照檔取得日期
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NULL AND MC.APPLY_DATE IS NOT NULL THEN TO_CHAR(MC.APPLY_DATE, 'yyyy/MM/dd') "); // 若為 非必要取得證照 且 人資證照檔登錄日期為空值 且 人資證照檔取得日期為空值 且 人資證照檔申請日期有值 → 顯示人資證照檔申請日期
		sb.append("         ELSE NULL END AS REG_DATE "); // 若以上條件皆不符合 → 顯示人資證照檔登錄日期
		sb.append("  FROM TBORG_MEMBER M ");
		sb.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID "); //AND MR.IS_PRIMARY_ROLE = 'Y' 
		sb.append("  LEFT JOIN TBORG_ROLE R ON MR.ROLE_ID = R.ROLE_ID ");
		sb.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON R.ROLE_ID = PRI.ROLEID ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_TYPE ON CERT_TYPE.PARAM_TYPE LIKE 'ORG.CERT_%' AND PRI.PRIVILEGEID IN CERT_TYPE.PARAM_CODE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_LIST ON CERT_TYPE.PARAM_TYPE||'_LIST' = CERT_LIST.PARAM_TYPE ");
		sb.append("  LEFT JOIN TBORG_MEMBER_CERT MC ON M.EMP_ID = MC.EMP_ID AND CERT_LIST.PARAM_CODE = MC.CERTIFICATE_CODE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_MAPP ON CERT_MAPP.PARAM_TYPE = 'ORG.CERT_MAPPING' AND CERT_LIST.PARAM_CODE = CERT_MAPP.PARAM_CODE ");
		sb.append("  LEFT JOIN TBORG_MEMBER_CERT MC_INS ON M.EMP_ID = MC_INS.EMP_ID AND CERT_MAPP.PARAM_DESC = MC_INS.CERTIFICATE_CODE ");
		sb.append("  WHERE MR.ROLE_ID IS NOT NULL ");
		sb.append("  AND M.EMP_ID = :empID ");
		sb.append("  AND R.JOB_TITLE_NAME IS NOT NULL ");
		sb.append(") ");
	
		sb.append("SELECT EMP_ID, IS_PRIMARY_ROLE, ROLE_ID, ROLE_NAME, CERT_NAME, CERT_DESC, CERT_ORDER, REG_DATE ");
		sb.append("FROM ( ");
		sb.append("  SELECT EMP_ID, IS_PRIMARY_ROLE, ROLE_ID, ROLE_NAME, CERT_NAME, CERT_DESC, CERT_ORDER, REG_DATE ");
		sb.append("  FROM CERT ");
		sb.append("  UNION ");
		sb.append("  SELECT DISTINCT EMP_ID, IS_PRIMARY_ROLE, ROLE_ID, ROLE_NAME, '以下擇一', 'CHOOSE_ONE', 0, NULL ");
		sb.append("  FROM CERT ");
		sb.append("  WHERE CERT.CERT_DESC = 'CHOOSE_ONE' ");
		sb.append(") ");
		//WHERE EMP_ID = :empID 
		sb.append("ORDER BY EMP_ID, DECODE(IS_PRIMARY_ROLE, 'Y', 1, 99), ROLE_ID, ROLE_NAME, DECODE(CERT_DESC, 'ESSENTIAL', 1, 99), CERT_ORDER ");
		
		System.out.println("123");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("empID", inputVO.getEMP_ID());
		
		outputVO.setEmpCertLst(dam.exeQuery(queryCondition));
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("SELECT BASE.EMP_ID, BASE.PARAM_NAME AS CERT_NAME, BASE.PARAM_CODE AS CERT_ID, TO_CHAR(CERT.REG_DATE, 'yyyy/MM/dd') AS REG_DATE ");
		sb.append("FROM ( ");
		sb.append("  SELECT A.EMP_ID, B.PARAM_CODE, B.PARAM_NAME, B.PARAM_ORDER ");
		sb.append("  FROM TBORG_MEMBER A ");
		sb.append("  LEFT JOIN TBSYSPARAMETER B ON B.PARAM_TYPE = 'ORG.CERT' ");
		sb.append("  WHERE A.EMP_ID = :empID ");
		sb.append(") BASE ");
		sb.append("LEFT JOIN TBORG_MEMBER_CERT CERT ON BASE.EMP_ID = CERT.EMP_ID AND BASE.PARAM_CODE = CERT.CERTIFICATE_CODE ");
		sb.append("ORDER BY BASE.EMP_ID, BASE.PARAM_ORDER ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("empID", inputVO.getEMP_ID());
		
		outputVO.setInsCertLst(dam.exeQuery(queryCondition));
		
		outputVO.setQueryType(inputVO.getQueryType());
		
		this.sendRtnObject(outputVO);
	}

	public void checkRole(Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		
		ORG111OrgRoleVO inputVO = (ORG111OrgRoleVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT IS_PRIMARY_ROLE ");
		sb.append("FROM TBORG_MEMBER_ROLE ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("AND IS_PRIMARY_ROLE = 'Y' ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("empID", inputVO.getEMP_ID());
		
		sendRtnObject(dam.exeQuery(queryCondition));
	}

	/**
	 * 更新Long Raw欄位，使用JDBC而非DAO，需new新的Connection，係Hibernate不支援Long Raw欄位所致。
	 * @param seqNo TBORG_MEMBER_REVIEW的SEQNO
	 * @param empId TBORG_MEMBER的EMP_ID
	 * @since 2017-6-23
	 * @author jemmy
	 */
	private void updateEmpPhotoRaw(BigDecimal seqNo, String empId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		FileInputStream fin = null;
		try {
			com.systex.jbranch.platform.common.dataaccess.datasource.DataSource ds = new
                    com.systex.jbranch.platform.common.dataaccess.datasource.DataSource();
            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
			DataSource dataSource = SessionFactoryUtils.getDataSource(sf);
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			if (seqNo != null) {
				logger.info("JIMMY: Update TBORG_MEMBER_REVIEW SEQNO = {}", seqNo.intValue());
				pstmt = conn.prepareStatement("select EMP_PHOTO from TBORG_MEMBER_REVIEW where SEQNO = ?");
				pstmt.setBigDecimal(1, seqNo);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					Blob blob = rs.getBlob(1);
					byte[] ba = blob.getBytes(1, (int) blob.length());
					blob.free();
					rs.close();
					pstmt.close();
					pstmt1 = conn.prepareStatement("update TBORG_MEMBER_REVIEW set EMP_PHOTO_RAW = ? where SEQNO = ?");
					pstmt1.setBinaryStream(1, new ByteArrayInputStream(ba), ba.length);
					pstmt1.setBigDecimal(2, seqNo);
					int rows = pstmt1.executeUpdate();
					logger.info("JIMMY: TBORG_MEMBER_REVIEW = {}, update RAW = {}", seqNo.intValue(), rows);
					conn.commit();
					pstmt1.close();
				} else {
					logger.info("JIMMY: TBORG_MEMBER_REVIEW = {} doesn't exists!", seqNo.intValue());
				}	
			} else {
				logger.info("JIMMY: Update TBORG_MEMBER EMP_ID = {}", empId);
				pstmt = conn.prepareStatement("select EMP_PHOTO from TBORG_MEMBER where EMP_ID = ?");
				pstmt.setString(1, empId);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					Blob blob = rs.getBlob(1);
					byte[] ba = blob.getBytes(1, (int) blob.length());
					blob.free();
					rs.close();
					pstmt.close();
					pstmt1 = conn.prepareStatement("update TBORG_MEMBER set EMP_PHOTO_RAW = ? where  EMP_ID = ?");
					pstmt1.setBinaryStream(1, new ByteArrayInputStream(ba), ba.length);
					pstmt1.setString(2, empId);
					int rows = pstmt1.executeUpdate();
					logger.info("JIMMY: TBORG_MEMBER = {}, update RAW = {}", empId, rows);
					conn.commit();
					pstmt1.close();
				} else {
					logger.info("JIMMY: TBORG_MEMBER = {} doesn't exists!", empId);
				}
			}
	      	return;
		} catch (Exception e) {
			this.logger.warn("updateMemberReviewLongRaw error", e);
	    } finally {
	        if (fin != null) try { fin.close(); } catch (Exception e) {}
	        if (rs != null) try { rs.close(); } catch (Exception e) {}
	        if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
	        if (pstmt1 != null) try { pstmt1.close(); } catch (Exception e) {}
	        if (conn != null) try { conn.close(); } catch (Exception e) {}
	    }
	}
}

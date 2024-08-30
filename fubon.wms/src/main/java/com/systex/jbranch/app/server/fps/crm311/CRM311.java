package com.systex.jbranch.app.server.fps.crm311;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SETVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTLPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_MGMT_SET_DTLVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Stella
 * @date 2016/05/23
 * @spec null
 */
	
@Component("crm311")
@Scope("request")

public class CRM311 extends FubonWmsBizLogic {
	private  DataAccessManager dam = null;
	private  Logger logger = LoggerFactory.getLogger(CRM311.class);
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM311InputVO inputVO = (CRM311InputVO) body;
		CRM311OutputVO return_VO = 	new CRM311OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.* FROM TBCRM_TRS_CUST_MGMT_SET a ");
		sql.append("left join (select b.ROLE_ID as AO_JOB_RANK, b.ROLE_NAME as AO_NAME from TBSYSSECUROLPRIASS a left JOIN TBORG_ROLE b on a.ROLEID = b.ROLE_ID where a.PRIVILEGEID = '002') b ");
		sql.append("on a.AO_JOB_RANK = b.AO_JOB_RANK ");
		sql.append("ORDER BY AO_NAME ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList1(list);
		this.sendRtnObject(return_VO);
	}
	
	public void initial2(Object body, IPrimitiveMap header) throws JBranchException {
		CRM311InputVO inputVO = (CRM311InputVO) body;
		CRM311OutputVO return_VO = 	new CRM311OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TTCMSD.*, TTCMS.TTL_CUST_NO_LIMIT_UP, DECODE(TTCMSD.VIP_DEGREE, 'V', 1, 'A', 2, 'B', 3, 'M', 4) AS VIP_DEG_ORDER FROM TBCRM_TRS_CUST_MGMT_SET_DTL TTCMSD ");
		sql.append("LEFT JOIN TBCRM_TRS_CUST_MGMT_SET TTCMS ON TTCMS.AO_JOB_RANK = TTCMSD.AO_JOB_RANK ");
		sql.append("LEFT JOIN (select b.ROLE_ID as AO_JOB_RANK, b.ROLE_NAME as AO_NAME from TBSYSSECUROLPRIASS a left JOIN TBORG_ROLE b on a.ROLEID = b.ROLE_ID where a.PRIVILEGEID = '002') c ");
		sql.append("on TTCMS.AO_JOB_RANK = c.AO_JOB_RANK ");
		sql.append("ORDER BY TTCMSD.AO_JOB_RANK, VIP_DEG_ORDER ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList2(list);
		this.sendRtnObject(return_VO);
	}
	
	/** 理專職級修改 **/
	public void modify(Object body, IPrimitiveMap header)throws JBranchException{ 
		CRM311InputVO inputVO = (CRM311InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		//用於判斷是否存在相同主鍵
		
		TBCRM_TRS_CUST_MGMT_SETVO paramVO = new TBCRM_TRS_CUST_MGMT_SETVO();
		
		paramVO =(TBCRM_TRS_CUST_MGMT_SETVO) dam.findByPKey(
				TBCRM_TRS_CUST_MGMT_SETVO.TABLE_UID,inputVO.getAo_ao_job_rank());
		
		if(paramVO!=null){
			
			BigDecimal big1 = new BigDecimal(StringUtils.isNotBlank(inputVO.getAum_limit_up())?inputVO.getAum_limit_up():null);
			BigDecimal big2 = new BigDecimal(StringUtils.isNotBlank(inputVO.getTtl_cust_no_limit_up())?inputVO.getTtl_cust_no_limit_up():null);
			paramVO.setLIMIT_BY_AUM_YN(StringUtils.isNotBlank(inputVO.getLimit_by_aum_yn())?inputVO.getLimit_by_aum_yn():null);
			paramVO.setAUM_LIMIT_UP(big1);
			paramVO.setTTL_CUST_NO_LIMIT_UP(big2);
			dam.update(paramVO);
			
		}else{
			
			throw new APException("ehl_01_common_007");
		}
		
		
			
		this.sendRtnObject(null);

	}	
	
	
	/** 客戶理專級別修改設定 **/
	public void custmodify(Object body, IPrimitiveMap header)throws JBranchException{ 
		CRM311InputVO inputVO = (CRM311InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		//用於判斷是否存在相同主鍵
		TBCRM_TRS_CUST_MGMT_SET_DTLPK paramPK_DTL = new TBCRM_TRS_CUST_MGMT_SET_DTLPK();
		TBCRM_TRS_CUST_MGMT_SET_DTLVO paramVO_DTL = new TBCRM_TRS_CUST_MGMT_SET_DTLVO();
	   
	    paramPK_DTL.setAO_JOB_RANK(inputVO.getCust_ao_job_rank());
		paramPK_DTL.setVIP_DEGREE(inputVO.getVip_degree());
		paramVO_DTL = (TBCRM_TRS_CUST_MGMT_SET_DTLVO) dam.findByPKey(
				TBCRM_TRS_CUST_MGMT_SET_DTLVO.TABLE_UID,paramPK_DTL);
		if(paramVO_DTL!=null){
			BigDecimal big1 = new BigDecimal(StringUtils.isNotBlank(inputVO.getCust_no_flex_prcnt())?inputVO.getCust_no_flex_prcnt():null);
			BigDecimal big2 = new BigDecimal(StringUtils.isNotBlank(inputVO.getCust_no_limit_up())?inputVO.getCust_no_limit_up():null);
			paramVO_DTL.setCUST_NO_FLEX_PRCNT(big1);
			paramVO_DTL.setCUST_NO_LIMIT_UP(big2);
			dam.update(paramVO_DTL);
			
		}else{
			
			throw new APException("ehl_01_common_007");
		}
		this.sendRtnObject(null);
	}
	
	
	/** 理專設定刪除  **/
	public void aoDelete(Object body, IPrimitiveMap header)throws JBranchException{ 
		CRM311InputVO inputVO = (CRM311InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		TBCRM_TRS_CUST_MGMT_SETVO paramVO = new TBCRM_TRS_CUST_MGMT_SETVO();
		paramVO =(TBCRM_TRS_CUST_MGMT_SETVO) dam.findByPKey(
				TBCRM_TRS_CUST_MGMT_SETVO.TABLE_UID,inputVO.getAo_ao_job_rank());
		
		if(paramVO!=null){
			dam.delete(paramVO);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
		}
		this.sendRtnObject(null);
	}
	
	
	/** 客戶理專級別刪除 **/
	public void custDelete(Object body, IPrimitiveMap header)throws JBranchException{ 
		CRM311InputVO inputVO = (CRM311InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		//用於判斷是否存在相同主鍵
		TBCRM_TRS_CUST_MGMT_SET_DTLPK paramPK_DTL = new TBCRM_TRS_CUST_MGMT_SET_DTLPK();
		TBCRM_TRS_CUST_MGMT_SET_DTLVO paramVO_DTL = new TBCRM_TRS_CUST_MGMT_SET_DTLVO();
	   
	    paramPK_DTL.setAO_JOB_RANK(inputVO.getCust_ao_job_rank());
		paramPK_DTL.setVIP_DEGREE(inputVO.getVip_degree());
		paramVO_DTL = (TBCRM_TRS_CUST_MGMT_SET_DTLVO) dam.findByPKey(
				TBCRM_TRS_CUST_MGMT_SET_DTLVO.TABLE_UID,paramPK_DTL);
		
		if(paramVO_DTL!=null){
			dam.delete(paramVO_DTL);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
			
		}
		this.sendRtnObject(null);
				
	}
	
	
	/**理專設定新增**/
	public void aoAdd(Object body, IPrimitiveMap header)throws JBranchException{ 
		CRM311InputVO inputVO = (CRM311InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		TBCRM_TRS_CUST_MGMT_SETVO paramVO = new TBCRM_TRS_CUST_MGMT_SETVO();
		
		paramVO =(TBCRM_TRS_CUST_MGMT_SETVO) dam.findByPKey(
				TBCRM_TRS_CUST_MGMT_SETVO.TABLE_UID,inputVO.getAo_ao_job_rank());
		
		if(paramVO==null){
			
			TBCRM_TRS_CUST_MGMT_SETVO paramVO1 = new TBCRM_TRS_CUST_MGMT_SETVO();
			paramVO1.setAO_JOB_RANK(inputVO.getAo_ao_job_rank());
			BigDecimal big1 = new BigDecimal(StringUtils.isNotBlank(inputVO.getAum_limit_up())?inputVO.getAum_limit_up():null);
			BigDecimal big2 = new BigDecimal(StringUtils.isNotBlank(inputVO.getTtl_cust_no_limit_up())?inputVO.getTtl_cust_no_limit_up():null);
			paramVO1.setLIMIT_BY_AUM_YN(StringUtils.isNotBlank(inputVO.getLimit_by_aum_yn())?inputVO.getLimit_by_aum_yn():null);
			paramVO1.setAUM_LIMIT_UP(big1);
			paramVO1.setTTL_CUST_NO_LIMIT_UP(big2);
			dam.create(paramVO1);
		}else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_008");
		}
		this.sendRtnObject(null);
	}
	
	
	/** 客戶理專級別新增 **/
	public void custAdd(Object body, IPrimitiveMap header)throws JBranchException{ 
		CRM311InputVO inputVO = (CRM311InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		//用於判斷是否存在相同主鍵
		TBCRM_TRS_CUST_MGMT_SET_DTLPK paramPK_DTL = new TBCRM_TRS_CUST_MGMT_SET_DTLPK();
		TBCRM_TRS_CUST_MGMT_SET_DTLVO paramVO_DTL = new TBCRM_TRS_CUST_MGMT_SET_DTLVO();
	   
	    paramPK_DTL.setAO_JOB_RANK(inputVO.getCust_ao_job_rank());
		paramPK_DTL.setVIP_DEGREE(inputVO.getVip_degree());
		paramVO_DTL = (TBCRM_TRS_CUST_MGMT_SET_DTLVO) dam.findByPKey(
				TBCRM_TRS_CUST_MGMT_SET_DTLVO.TABLE_UID,paramPK_DTL);
		
		if(paramVO_DTL==null){
			TBCRM_TRS_CUST_MGMT_SET_DTLPK paramPK_DTL1 = new TBCRM_TRS_CUST_MGMT_SET_DTLPK();
			TBCRM_TRS_CUST_MGMT_SET_DTLVO paramVO_DTL1 = new TBCRM_TRS_CUST_MGMT_SET_DTLVO();
		    paramPK_DTL1.setAO_JOB_RANK(inputVO.getCust_ao_job_rank());
			paramPK_DTL1.setVIP_DEGREE(inputVO.getVip_degree());
			BigDecimal big1 = new BigDecimal(StringUtils.isNotBlank(inputVO.getCust_no_flex_prcnt())?inputVO.getCust_no_flex_prcnt():null);
			BigDecimal big2 = new BigDecimal(StringUtils.isNotBlank(inputVO.getCust_no_limit_up())?inputVO.getCust_no_limit_up():null);
			paramVO_DTL1.setCUST_NO_FLEX_PRCNT(big1);
			paramVO_DTL1.setCUST_NO_LIMIT_UP(big2);
			paramVO_DTL1.setcomp_id(paramPK_DTL1);
			dam.create(paramVO_DTL1);
			
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_008");
		}
		this.sendRtnObject(null);
	}
	
	// 2017/2/15 add AO
	public void getAo(Object body, IPrimitiveMap header) throws JBranchException {
		CRM311InputVO inputVO = (CRM311InputVO) body;
		CRM311OutputVO return_VO = new CRM311OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select b.ROLE_ID as DATA, b.ROLE_NAME as LABEL from TBSYSSECUROLPRIASS a left JOIN TBORG_ROLE b on a.ROLEID = b.ROLE_ID where a.PRIVILEGEID = '002'");
		return_VO.setResultList1(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}
	
	
}

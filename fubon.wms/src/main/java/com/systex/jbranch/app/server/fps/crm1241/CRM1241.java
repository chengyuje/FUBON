package com.systex.jbranch.app.server.fps.crm1241;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_AS_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author 俊緯
 * @date 2017/02/18
 * @spec null
 */
@Component("crm1241")
@Scope("request")
public class CRM1241 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM1241.class);
	String Type = "";
	String RoleID = "";
	
	/**輔銷行事曆會議記錄 不再是查詢單月陪訪紀錄**/
	//查詢當月陪訪紀錄
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1241InputVO inputVO = (CRM1241InputVO) body;
		CRM1241OutputVO return_VO = new CRM1241OutputVO();
		
		RoleID = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();
		
		/**判斷角色權限:
		 * 		FaiaType 1 = 輔銷科長
		 * 		FaiaType 2 = 輔銷人員
		 */
		if (inputVO.getQuerytype() == null){
			if ("FA9".equals(RoleID) || "IA9".equals(RoleID)){
				Type = "1";
			}else if("FA".equals(RoleID) || "IA".equals(RoleID)){
				Type = "2";
			}
		}else{
			Type = inputVO.getQuerytype();
		}	
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
//		Type =	inputVO.getQuerytype();
		
		sql.append(" SELECT A.* , B.BRANCH_NBR, B.BRANCH_NAME, B.AO_JOB_RANK, B.EMP_NAME, C.CUST_NAME, ");
		sql.append(" (A.AS_EMP_ID || '-' || T.EMP_NAME || '陪同' || B.EMP_NAME || '拜訪' || C.CUST_NAME) AS TITLE ");
		sql.append(" FROM TBCRM_WKPG_AS_MAST A  ");
		sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.AO_CODE = B.AO_CODE ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST C ON A.CUST_ID = C.CUST_ID ");
		sql.append(" LEFT JOIN TBORG_MEMBER T ON A.AS_EMP_ID = T.EMP_ID ");
//		sql.append(" WHERE TO_CHAR(A.AS_DATE, 'YYYY/MM') = TO_CHAR( :date , 'YYYY/MM')  ");
		sql.append(" WHERE TO_CHAR(A.AS_DATE, 'YYYY/MM/DD') = TO_CHAR( :date , 'YYYY/MM/DD')  ");	
		
		
		//輔銷人員
		if(Type.equals("2")){
			sql.append("AND A.STATUS in ('1A', '2A', '3A', '4A', '4C') ");
			sql.append("AND A.AS_EMP_ID = :emp_id ");
			condition.setObject("emp_id", inputVO.getEmp_id());
		}else{
			sql.append("AND A.STATUS in ('1A', '2A', '3A', '4A') ");
			sql.append("AND A.COMPLETE_YN = 'Y' ");
			if ("FA9".equals(RoleID)){
				sql.append("AND A.AS_TYPE = 'F' ");
			}
			if ("IA9".equals(RoleID)){
				sql.append("AND A.AS_TYPE = 'I' ");
			}
			
		}	
		sql.append("ORDER BY A.AS_DATE ASC, A.AS_DATETIME_BGN ASC ");

		condition.setQueryString(sql.toString());	
		condition.setObject("date", new Timestamp(inputVO.getViewDate().getTime()));
		List list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	//完成
	public void confirm(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1241InputVO inputVO = (CRM1241InputVO) body;
		dam = this.getDataAccessManager();
		TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();
		vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
				TBCRM_WKPG_AS_MASTVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			vo.setCOMPLETE_YN("Y");
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}		
		this.sendRtnObject(null);
	}
	
	//查詢紀錄明細
	public void inquire_record(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1241InputVO inputVO = (CRM1241InputVO) body;
		CRM1241OutputVO return_VO = new CRM1241OutputVO();
		dam = this.getDataAccessManager();
//		Type = inputVO.getQuerytype();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.SEQ, A.CUST_ID, A.AO_CODE, A.AS_TYPE, A.SALES_PLAN_SEQ, ");
		sql.append("A.AS_DATE, A.AS_DATETIME_BGN, A.AS_DATETIME_END, A.PTYPE, A.PRO_ID, A.SELL_OUT_YN, ");
		sql.append("A.VISIT_MEMO AS VISIT_MEMO_AS, V.VISIT_MEMO AS VISIT_MEMO_AO, A.COMPLETE_YN, D.PNAME, ");
		sql.append("CASE WHEN A.STATUS = '4A' THEN A.VISIT_PURPOSE ELSE P.VISIT_PURPOSE END AS VISIT_PURPOSE, ");
		sql.append("CASE WHEN A.STATUS = '4A' THEN A.VISIT_PURPOSE_OTHER ELSE P.VISIT_PURPOSE_OTHER END AS VISIT_PURPOSE_OTHER, ");
		sql.append("CASE WHEN A.STATUS = '4A' THEN A.KEY_ISSUE ELSE P.KEY_ISSUE END AS KEY_ISSUE, ");
		sql.append("A.STATUS, B.BRANCH_NBR, B.BRANCH_NAME, B.AO_JOB_RANK, B.EMP_NAME, C.CUST_NAME ");
		sql.append("FROM TBCRM_WKPG_AS_MAST A ");
		sql.append("LEFT JOIN TBCRM_CUST_VISIT_RECORD V ON A.CUST_ID = V.CUST_ID AND TRUNC(A.AS_DATE) <= TRUNC(V.CREATETIME) ");
		sql.append("LEFT JOIN TBPMS_SALES_PLAN P ON A.SALES_PLAN_SEQ = P.SEQ ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO B ON A.AO_CODE = B.AO_CODE ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C ON A.CUST_ID = C.CUST_ID ");
		sql.append("LEFT JOIN VWPRD_MASTER D ON A.PTYPE = D.PTYPE AND A.PRO_ID = D.PRD_ID ");
		sql.append("WHERE A.SEQ = :seq ");
		sql.append("AND A.STATUS in ('1A', '2A', '3A', '4A', '4C') ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("seq", inputVO.getSeq());
		List list = dam.exeQuery(condition);
		return_VO.setResultList_record(list);
		this.sendRtnObject(return_VO);
	}
	
	//查詢建議商品
	public void inquire_product(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM1241InputVO inputVO = (CRM1241InputVO) body;
		CRM1241OutputVO return_VO = new CRM1241OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT PNAME, PRD_ID, PTYPE  ");
		sql.append("FROM VWPRD_MASTER ");
		sql.append("WHERE  1=1 ");
		sql.append("AND  PNAME is not null ");
		sql.append("AND  PNAME != '-' ");
		sql.append("AND  PTYPE is not null ");
		sql.append("AND  PNAME LIKE :product_name ");
		if(!"".equals(inputVO.getPtype()) && inputVO.getPtype() != null){
			sql.append("AND PTYPE = :ptype ");
			condition.setObject("ptype", inputVO.getPtype());
		}
		if(!"".equals(inputVO.getPrd_id()) && inputVO.getPrd_id() != null){
			sql.append("AND PRD_ID = :prd_id ");
			condition.setObject("prd_id", inputVO.getPrd_id());
		}
		condition.setQueryString(sql.toString());
		String Prodname_check = "";
		
		if(inputVO.getProduct_name() != null){
			Prodname_check = inputVO.getProduct_name();
		}

		condition.setObject("product_name", "%" + Prodname_check + "%");
		
		List list = dam.exeQuery(condition);
		return_VO.setResultList_product(list);
		this.sendRtnObject(return_VO);
	}
	
	//建議商品儲存
	public void save_prd_id(Object body, IPrimitiveMap header) throws JBranchException {		
		CRM1241InputVO inputVO = (CRM1241InputVO) body;
		
		dam = this.getDataAccessManager();
		TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();
		vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
				TBCRM_WKPG_AS_MASTVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			vo.setPTYPE(inputVO.getPtype());
			vo.setPRO_ID(inputVO.getPrd_id());	
			dam.update(vo);			
		} 
		this.sendRtnObject(null);
	}
	
	
	
	
	//儲存
	public void save_record(Object body, IPrimitiveMap header) throws JBranchException {		
		CRM1241InputVO inputVO = (CRM1241InputVO) body;
		dam = this.getDataAccessManager();
		TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();
		vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
				TBCRM_WKPG_AS_MASTVO.TABLE_UID, inputVO.getSeq());
		
		if (vo != null) {
			vo.setSELL_OUT_YN(inputVO.getSell_out_yn());
			vo.setVISIT_MEMO(inputVO.getVisit_memo_as());
			vo.setKEY_ISSUE(inputVO.getKey_issue());
			vo.setVISIT_PURPOSE(inputVO.getVisit_purpose());
			dam.update(vo);			
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}
		this.sendRtnObject(null);
	}
	
}
package com.systex.jbranch.app.server.fps.crm351;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_HO_CUST_NOTEVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/06/21
 * 
 */
@Component("crm351")
@Scope("request")

public class CRM351 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM351.class);

	//查詢
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM351InputVO inputVO = (CRM351InputVO) body;
		CRM351OutputVO outputVO = new CRM351OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT C.CUST_ID, C.CUST_NAME, C.AO_CODE, VWA.EMP_NAME, C.VIP_DEGREE, C.CON_DEGREE, H.CONTACT_CUST_YN, H.OTH_CONTACT_INFO, H.CUST_HO_NOTE, EMP.BRA_NBR as RESIGN_BRA_NBR, EMP.BRANCH_NAME as RESIGN_BRANCH_NAME, H.SEQ ");
		sql.append("FROM TBCRM_CUST_MAST C, VWORG_AO_INFO EMP, TBCRM_TRS_HO_CUST_NOTE H, TBORG_RESIGN_MEMO R, VWORG_BRANCH_EMP_DETAIL_INFO VWA ");
		sql.append("WHERE 1=1 AND C.AO_CODE = EMP.AO_CODE AND C.CUST_ID = H.CUST_ID(+) AND NVL(H.REVIEW_STATUS, 'N') != 'Y' AND EMP.EMP_ID = R.EMP_ID  AND R.RESIGN_HANDOVER in ( '01', 'A1', 'A2') AND C.AO_CODE = VWA.AO_CODE ");
		
		
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append("AND EMP.CENTER_ID = :center_id ");
			queryCondition.setObject("center_id", inputVO.getRegion_center_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append("AND EMP.AREA_ID = :bra_area ");
			queryCondition.setObject("bra_area", inputVO.getBranch_area_id());
		}
		if (!StringUtils.isBlank(inputVO.getBra_nbr())) {
			sql.append("AND EMP.BRA_NBR = :bra_nbr ");
			queryCondition.setObject("bra_nbr", inputVO.getBra_nbr());
		}
		
		if (!StringUtils.isBlank(inputVO.getAo_code())) {			
			sql.append("AND C.AO_CODE = :ao_code ");
			queryCondition.setObject("ao_code", inputVO.getAo_code());
		}
		
		// where
		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("AND C.CUST_ID like :cust_id ");
			queryCondition.setObject("cust_id", "%" + inputVO.getCust_id() + "%");
		}	
		if (!StringUtils.isBlank(inputVO.getCust_name())) {
			sql.append("AND C.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}	
		if (!StringUtils.isBlank(inputVO.getCon_degree())) {
			sql.append("AND C.CON_DEGREE = :con_degree ");
			queryCondition.setObject("con_degree", inputVO.getCon_degree());
		}
		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			sql.append("AND C.VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}
		
		//排序依照EIPOS
		sql.append("ORDER BY DECODE(C.CON_DEGREE,'E',1,'I',2,'P',3,'O',4,'S',5,C.CON_DEGREE) ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		CRM351InputVO inputVO = (CRM351InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String,Object> map : inputVO.getRow_data()) {
			// add
			if(map.get("SEQ") == null) {
				// seq
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT SQ_TBCRM_TRS_HO_CUST_NOTE.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				//
				TBCRM_TRS_HO_CUST_NOTEVO vo = new TBCRM_TRS_HO_CUST_NOTEVO();
				vo.setSEQ(seqNo);
				vo.setCUST_ID(ObjectUtils.toString(map.get("CUST_ID")));
				vo.setRESIGN_AO_CODE(ObjectUtils.toString(map.get("AO_CODE")));
				vo.setRESIGN_BRA_NBR(ObjectUtils.toString(map.get("RESIGN_BRA_NBR")));
				vo.setCONTACT_CUST_YN(ObjectUtils.toString(map.get("CONTACT_CUST_YN")));
				vo.setOTH_CONTACT_INFO(ObjectUtils.toString(map.get("OTH_CONTACT_INFO")));
				vo.setCUST_HO_NOTE(ObjectUtils.toString(map.get("CUST_HO_NOTE")));
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
			}
			// edit
			else {
				TBCRM_TRS_HO_CUST_NOTEVO vo = new TBCRM_TRS_HO_CUST_NOTEVO();
				vo = (TBCRM_TRS_HO_CUST_NOTEVO) dam.findByPKey(TBCRM_TRS_HO_CUST_NOTEVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(map.get("SEQ"))));
				if(vo != null) {
					vo.setCONTACT_CUST_YN(map.get("CONTACT_CUST_YN") == null ? "" : map.get("CONTACT_CUST_YN").toString());
					vo.setOTH_CONTACT_INFO(map.get("OTH_CONTACT_INFO") == null ? "" : map.get("OTH_CONTACT_INFO").toString());
					vo.setCUST_HO_NOTE(map.get("CUST_HO_NOTE") == null ? "" : map.get("CUST_HO_NOTE").toString());
					vo.setREVIEW_STATUS("W");
					dam.update(vo);
				}
			}
		}
		this.sendRtnObject(null);
	}
	
	public void saveReviewStatus(Object body, IPrimitiveMap header) throws JBranchException {
		CRM351InputVO inputVO = (CRM351InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String,Object> map : inputVO.getRow_data()) {
			TBCRM_TRS_HO_CUST_NOTEVO vo = new TBCRM_TRS_HO_CUST_NOTEVO();
			vo = (TBCRM_TRS_HO_CUST_NOTEVO) dam.findByPKey(TBCRM_TRS_HO_CUST_NOTEVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(map.get("SEQ"))));
			if(vo != null) {
				vo.setREVIEW_STATUS(inputVO.getCheck());
				dam.update(vo);
			}
		}
		this.sendRtnObject(null);
	}
	
	public void check(Object body, IPrimitiveMap header) throws JBranchException {
		CRM351InputVO inputVO = (CRM351InputVO) body;
		CRM351OutputVO outputVO = new CRM351OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT R.EMP_ID, EMP.BRA_NBR ");
		sql.append("FROM TBORG_RESIGN_MEMO R, VWORG_AO_INFO EMP ");
		sql.append("WHERE R.EMP_ID = EMP.EMP_ID AND R.RESIGN_HANDOVER in ('01', 'A1', 'A2') ");
		// IF登入者為理專身份 LoginUser.PRIVELEGE_ID in ( 001, 002, 003 )
		List<String> prd_idList = new ArrayList<String>();
		prd_idList.add("001");prd_idList.add("002");prd_idList.add("003");
		List<String> prd_idList2 = new ArrayList<String>();
		prd_idList2.add("011");
		if(prd_idList.contains(inputVO.getPri_id())) {
			sql.append("AND R.EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
		}
		else if(prd_idList2.contains(inputVO.getPri_id())) {
			sql.append("AND EMP.BRA_NBR = :bra_nbr ");
			queryCondition.setObject("bra_nbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));
		}
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		outputVO.setCheckList(list);
		this.sendRtnObject(outputVO);
	}
	
	

}

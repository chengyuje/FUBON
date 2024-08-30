package com.systex.jbranch.app.server.fps.crm411;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUPPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_SETUPVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Stella
 * @date 2016/09/23
 * @spec null
 */
@Component("crm411")
@Scope("request")
public class CRM411 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void insert(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM411InputVO inputVO = (CRM411InputVO) body;
		dam = this.getDataAccessManager();

		//先刪除後新增
		for (int i = 0; i < inputVO.getProdType().size(); i++) {
			for (int j = 0; j < inputVO.getConList().size(); j++) {
				for (int k = 1; k <= 4; k++) {
					TBCRM_BRG_SETUPPK pk = new TBCRM_BRG_SETUPPK();
					TBCRM_BRG_SETUPVO vo = new TBCRM_BRG_SETUPVO();
					pk.setPROD_TYPE(inputVO.getProdType().get(i).get("DATA").toString().trim());
					pk.setCON_DEGREE(inputVO.getConList().get(j).get("DATA").toString().trim());
					pk.setLEVEL_NO(Integer.toString(k));
					pk.setSETUP_TYPE(inputVO.getSetupType());

					vo = (TBCRM_BRG_SETUPVO) dam.findByPKey(TBCRM_BRG_SETUPVO.TABLE_UID, pk);
					if (null != vo) {
						dam.delete(vo);
					}
				}
			}
		}

		List<String> list = new ArrayList<String>();
		if (inputVO.getRoleList().size() > 0) {
			list = new ArrayList<String>();
			for (Map<String, Object> a : inputVO.getRoleList()) {
				list.add((String) a.get("data").toString());
			}
			create("1", inputVO, list);
		}
		
		if (inputVO.getRoleList_a().size() > 0) {
			list = new ArrayList<String>();
			for (Map<String, Object> b : inputVO.getRoleList_a()) {
				list.add((String) b.get("data").toString());
			}
			create("2", inputVO, list);
		}
		
		if (inputVO.getRoleList_b().size() > 0) {
			list = new ArrayList<String>();
			for (Map<String, Object> c : inputVO.getRoleList_b()) {
				list.add((String) c.get("data").toString());
			}
			create("3", inputVO, list);
		}

		if (inputVO.getRoleList_c().size() > 0) {
			list = new ArrayList<String>();
			for (Map<String, Object> c : inputVO.getRoleList_c()) {
				list.add((String) c.get("data").toString());
			}
			create("4", inputVO, list);
		}
		
		this.sendRtnObject(null);
	}

	private void create(String type, CRM411InputVO body, List<String> list) throws JBranchException {
		
		CRM411InputVO inputVO = (CRM411InputVO) body;
		dam = this.getDataAccessManager();

		for (int i = 0; i < inputVO.getProdType().size(); i++) {
			for (int j = 0; j < inputVO.getConList().size(); j++) {
				TBCRM_BRG_SETUPPK pk = new TBCRM_BRG_SETUPPK();
				TBCRM_BRG_SETUPVO vo = new TBCRM_BRG_SETUPVO();
				pk.setPROD_TYPE(inputVO.getProdType().get(i).get("DATA").toString().trim());
				pk.setCON_DEGREE(inputVO.getConList().get(j).get("DATA").toString().trim());
				pk.setLEVEL_NO(type);
				pk.setSETUP_TYPE(inputVO.getSetupType());
				
				vo.setcomp_id(pk);
				vo = (TBCRM_BRG_SETUPVO) dam.findByPKey(TBCRM_BRG_SETUPVO.TABLE_UID, vo.getcomp_id());
				
				if (null == vo) {
					pk = new TBCRM_BRG_SETUPPK();
					pk.setPROD_TYPE(inputVO.getProdType().get(i).get("DATA").toString().trim());
					pk.setCON_DEGREE(inputVO.getConList().get(j).get("DATA").toString().trim());
					pk.setLEVEL_NO(type);
					pk.setSETUP_TYPE(inputVO.getSetupType());
					
					vo = new TBCRM_BRG_SETUPVO();
					vo.setcomp_id(pk);
					vo.setROLE_LIST(TextUtils.join(",", list));
					
					// moron 2016/11/18 for old code r.i.p
					if ("1".equals(type)) {
						vo.setDISCOUNT(inputVO.getDISCOUNT_a());
						vo.setDISCOUNT_RNG_TYPE(inputVO.getDISCOUNT_RNG_TYPE_a());
					} else if ("2".equals(type)) {
						vo.setDISCOUNT(inputVO.getDISCOUNT_b());
						vo.setDISCOUNT_RNG_TYPE(inputVO.getDISCOUNT_RNG_TYPE_b());
					} else if ("3".equals(type)) {
						vo.setDISCOUNT(inputVO.getDISCOUNT_c());
						vo.setDISCOUNT_RNG_TYPE(inputVO.getDISCOUNT_RNG_TYPE_c());
					} else if ("4".equals(type)) {
						vo.setDISCOUNT(inputVO.getDISCOUNT_d());
						vo.setDISCOUNT_RNG_TYPE(inputVO.getDISCOUNT_RNG_TYPE_d());
					}
					//
					
					dam.create(vo);
				}
			}
		}
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM411InputVO inputVO = (CRM411InputVO) body;
		CRM411OutputVO return_VO = new CRM411OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		List<String> listrole = new ArrayList<String>();
		for (Map<String, Object> a : inputVO.getRole_List()) {
			String prod_type = a.get("DATA").toString();
			listrole.add(prod_type);
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select * ");
		sql.append("from ( ");
		sql.append("  select PROD_TYPE, ");
		sql.append("         LEVEL_NO, ");
		sql.append("         ROLE_LIST, ");
		sql.append("         CON_DEGREE,  ");
		sql.append("         (D.PARAM_NAME || T.PARAM_NAME) as val ");
		sql.append("  FROM TBCRM_BRG_SETUP S ");
		sql.append("  left outer join TBSYSPARAMETER D on D.PARAM_TYPE = 'CRM.DISCOUNT' and D.PARAM_CODE = S.DISCOUNT ");
		sql.append("  left outer join TBSYSPARAMETER T on T.PARAM_TYPE = 'CRM.DISCOUNT_RNG_TYPE' and T.PARAM_CODE = S.DISCOUNT_RNG_TYPE ");
		sql.append("  WHERE S.PROD_TYPE IN (:list ) ");
		if (StringUtils.isNotBlank(inputVO.getSetupType())) {
			sql.append("AND S.SETUP_TYPE = :setupType");
			queryCondition.setObject("setupType", inputVO.getSetupType());
		}
		sql.append(") pivot (max(val) for CON_DEGREE in ('E' AS E, 'I' AS I , 'P' AS P , 'O' AS O, 'S' AS S, 'OTH' AS OTH )) piv ");
		sql.append("order by PROD_TYPE ");
		
		queryCondition.setObject("list", listrole);
		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		sendRtnObject(return_VO);
	}
}

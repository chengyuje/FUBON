package com.systex.jbranch.app.server.fps.ins441;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBINS_SPP_INSLISTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * INS441
 * 
 * @author Loui
 * @date 2017/11/23
 */
@Component("ins441")
@Scope("request")
public class INS441 extends FubonWmsBizLogic {
	
	public void getParam (Object body, IPrimitiveMap<Object> header) throws JBranchException, Exception {
		
		INS441InputVO inputVO = (INS441InputVO) body;
		INS441OutputVO outputVO = new INS441OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (StringUtils.equals("COMPANY", inputVO.getType())) {
			sb.append("SELECT COM_ID AS DATA, ");
			sb.append("       COM_NAME AS LABEL ");
			sb.append("FROM TBPRD_INSDATA_COMPANY ");
			
			queryCondition.setQueryString(sb.toString());
			outputVO.setCompList(dam.exeQuery(queryCondition));
		} else {
			sb.append("SELECT PRD_ID AS DATA, ");
			sb.append("       PRD_NAME AS LABEL ");
			sb.append("FROM TBPRD_INSDATA_PROD_MAIN ");
			sb.append("WHERE COM_ID = :compID AND  IFCHS <> 'F'");
			
			queryCondition.setObject("compID", inputVO.getCompID());
			queryCondition.setQueryString(sb.toString());
			outputVO.setPrdList(dam.exeQuery(queryCondition));
		}
		
		sendRtnObject(outputVO);
	}
	
	/*
	 * 查詢現有公司
	 */
	public void query (Object body, IPrimitiveMap<Object> header) throws JBranchException, Exception {
		
		INS441InputVO inputVO = (INS441InputVO) body;
		INS441OutputVO outputVO = new INS441OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT MAIN.KEY_NO, ");
		sb.append("       MAIN.CUST_ID, ");
		sb.append("       MAIN.PRD_KEYNO, ");
		sb.append("       MAIN.OTH_ITEM, ");
		sb.append("       MAIN.AGE_S, ");
		sb.append("       MAIN.AGE_E, ");
		sb.append("       MAIN.ARTL_DEBT_AMT_ONCE, ");
		sb.append("       MAIN.ARTL_DEBT_AMT_MONTHLY, ");
		sb.append("       MAIN.PRD_ID, ");
		sb.append("       MAIN.PRD_NAME, ");
		sb.append("       MAIN.COM_ID, ");
		sb.append("       MAIN.COM_NAME, ");
		sb.append("       PROD.IS_MAIN ");
		sb.append("FROM VWINS_SPP_INSLIST MAIN ");
		sb.append("LEFT JOIN TBPRD_INSDATA_PROD_MAIN PROD ON PROD.KEY_NO = MAIN.PRD_KEYNO ");
		sb.append("WHERE CUST_ID = :custID ");
		
		if (StringUtils.equals("2", inputVO.getType())) {
			sb.append("AND MAIN.OTH_ITEM IS NOT NULL ");
		} else {
			sb.append("AND MAIN.OTH_ITEM IS NULL ");
		}
		queryCondition.setObject("custID", inputVO.getCustID());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		sendRtnObject(outputVO);
	}
	
	public void save (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		System.out.println("11111");
		INS441InputVO inputVO = (INS441InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;
		List<Map<String, Object>> keyNO = new ArrayList<Map<String,Object>>();
		
		TBINS_SPP_INSLISTVO vo = null;
		if (StringUtils.equals("1", inputVO.getType())) {
			// 其他退休保險
			for (Map<String, Object> map : inputVO.getInsList()) {
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT KEY_NO ");
				sb.append("FROM TBPRD_INSDATA_PROD_MAIN ");
				sb.append("WHERE COM_ID = :comID ");
				sb.append("AND PRD_ID = :prdID ");
				
				queryCondition.setObject("comID", map.get("COM_ID"));
				queryCondition.setObject("prdID", map.get("PRD_ID"));
				queryCondition.setQueryString(sb.toString());
				
				keyNO = dam.exeQuery(queryCondition);
				
				if (StringUtils.isNotBlank(checkIsNull(map, "KEY_NO"))) {
					vo = (TBINS_SPP_INSLISTVO) dam.findByPKey(TBINS_SPP_INSLISTVO.TABLE_UID, new BigDecimal(checkIsNull(map, "KEY_NO")));
					
					vo.setPRD_KEYNO(keyNO.get(0).get("KEY_NO").toString());
					vo.setAGE_S(new BigDecimal(checkIsNull(map, "AGE_S")));
					vo.setAGE_E(new BigDecimal(checkIsNull(map, "AGE_E")));
//					if (StringUtils.isNotBlank(checkIsNullByNumber(map, "ARTL_DEBT_AMT_ONCE"))) {
						vo.setARTL_DEBT_AMT_ONCE(new BigDecimal(checkIsNullByNumber(map, "ARTL_DEBT_AMT_ONCE")));
//						vo.setARTL_DEBT_AMT_MONTHLY(null);
//					}
					
//					if (StringUtils.isNotBlank(checkIsNullByNumber(map, "ARTL_DEBT_AMT_MONTHLY"))) {
//						vo.setARTL_DEBT_AMT_ONCE(null);
						vo.setARTL_DEBT_AMT_MONTHLY(new BigDecimal(checkIsNullByNumber(map, "ARTL_DEBT_AMT_MONTHLY")));
//					}
					
					dam.update(vo);
				} else {
					vo = new TBINS_SPP_INSLISTVO();
					
					vo.setKEY_NO(new BigDecimal(getSEQ(dam)));
					vo.setCUST_ID(inputVO.getCustID());
					vo.setPRD_KEYNO(keyNO.get(0).get("KEY_NO").toString());
					vo.setAGE_S(new BigDecimal(checkIsNull(map, "AGE_S")));
					vo.setAGE_E(new BigDecimal(checkIsNull(map, "AGE_E")));
//					if (StringUtils.isNotBlank(checkIsNullByNumber(map, "ARTL_DEBT_AMT_ONCE"))) {
					vo.setARTL_DEBT_AMT_ONCE(new BigDecimal(checkIsNullByNumber(map, "ARTL_DEBT_AMT_ONCE")));
//						vo.setARTL_DEBT_AMT_MONTHLY(null);
//					}
					
//					if (StringUtils.isNotBlank(checkIsNullByNumber(map, "ARTL_DEBT_AMT_MONTHLY"))) {
//						vo.setARTL_DEBT_AMT_ONCE(null);
					vo.setARTL_DEBT_AMT_MONTHLY(new BigDecimal(checkIsNullByNumber(map, "ARTL_DEBT_AMT_MONTHLY")));
//					}			
					dam.create(vo);
				}
			}
		} else {
			// 退休後其他收入
			for (Map<String, Object> map : inputVO.getOthList()) {
				if (StringUtils.isNotBlank(checkIsNull(map, "KEY_NO"))) {
					vo = (TBINS_SPP_INSLISTVO) dam.findByPKey(TBINS_SPP_INSLISTVO.TABLE_UID, new BigDecimal(checkIsNull(map, "KEY_NO")));
					
					vo.setOTH_ITEM(checkIsNull(map, "OTH_ITEM"));
					vo.setARTL_DEBT_AMT_ONCE(new BigDecimal(checkIsNullByNumber(map, "ARTL_DEBT_AMT_ONCE")));
					vo.setARTL_DEBT_AMT_MONTHLY(new BigDecimal(checkIsNullByNumber(map,"ARTL_DEBT_AMT_MONTHLY")));
					
					dam.update(vo);
				} else {
					vo = new TBINS_SPP_INSLISTVO();
					
					vo.setKEY_NO(new BigDecimal(getSEQ(dam)));
					vo.setCUST_ID(inputVO.getCustID());
					vo.setOTH_ITEM(checkIsNull(map, "OTH_ITEM"));
					vo.setARTL_DEBT_AMT_ONCE(new BigDecimal(checkIsNullByNumber(map, "ARTL_DEBT_AMT_ONCE")));
					vo.setARTL_DEBT_AMT_MONTHLY(new BigDecimal(checkIsNullByNumber(map,"ARTL_DEBT_AMT_MONTHLY")));
					dam.create(vo);
				}
			}
		}
		sendRtnObject(null);
	}
	
	/**
	 * 刪除
	 * */
	public void delete (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		INS441InputVO inputVO = (INS441InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		TBINS_SPP_INSLISTVO vo = (TBINS_SPP_INSLISTVO) dam.findByPKey(TBINS_SPP_INSLISTVO.TABLE_UID, new BigDecimal(inputVO.getKeyNO()));
		
		if (null != vo) {
			dam.delete(vo);
		}
		
		sendRtnObject(null);
	}
	
	/**
	 * 取流水號
	 * */
	private String getSEQ (DataAccessManager dam) throws DAOException, JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT TBINS_SPP_INSLIST_SEQ.NEXTVAL AS SEQ FROM DUAL");
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> seqNO = dam.exeQuery(queryCondition);
		
		String seq = seqNO.get(0).get("SEQ").toString();
		
		return seq;
	}
	
	/**
	 * 檢核是否為空值
	 * */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
				return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	/**
	 * 檢核是否為空值
	 * */
	private String checkIsNullByNumber(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
				return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}
}
package com.systex.jbranch.app.server.fps.crm321;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SETPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_CUST_ASS_BRH_SETVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 19/5/2016
 * 
 */
@Component("crm321")
@Scope("request")

public class CRM321 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM321.class);

	//查詢
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM321InputVO inputVO = (CRM321InputVO) body;
		CRM321OutputVO outputVO = new CRM321OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT FCH_MAST_BRH, ASS_BRH, PRIORITY_ORDER FROM TBCRM_TRS_CUST_ASS_BRH_SET WHERE 1 = 1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getFch_mast_brh())){
			sql.append("AND FCH_MAST_BRH like :fch_mast_brh ");
			condition.setObject("fch_mast_brh", "%" + inputVO.getFch_mast_brh() + "%");		
		}
		sql.append("ORDER BY FCH_MAST_BRH, PRIORITY_ORDER ");
		condition.setQueryString(sql.toString());		
		List FCHLis = dam.exeQuery(condition);
		outputVO.setFCHList(FCHLis); // data
		
		this.sendRtnObject(outputVO);
	}
	
	//新增
	public void addConfirm(Object body, IPrimitiveMap header) throws JBranchException {		
		CRM321InputVO inputVO = (CRM321InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIORITY_ORDER FROM TBCRM_TRS_CUST_ASS_BRH_SET WHERE FCH_MAST_BRH LIKE :fch ");
		queryCondition.setObject("fch", inputVO.getFch_branchNbr());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(list.size() > 0){
			String[] arr = new String[list.size()];
			for(int i = 0; i < list.size(); i++){
				arr[i] = list.get(i).get("PRIORITY_ORDER").toString();
			}
			for(String s: arr){
				if(s.equals(inputVO.getPriority_order())){
					throw new APException("ehl_01_common_016");
				}
			}		        	
		}
		TBCRM_TRS_CUST_ASS_BRH_SETVO vo = new TBCRM_TRS_CUST_ASS_BRH_SETVO();
		TBCRM_TRS_CUST_ASS_BRH_SETPK pk = new TBCRM_TRS_CUST_ASS_BRH_SETPK();
		pk.setFCH_MAST_BRH(inputVO.getFch_branchNbr());
		pk.setASS_BRH(inputVO.getBranchNbr());
		
		vo = (TBCRM_TRS_CUST_ASS_BRH_SETVO) dam.findByPKey(
				TBCRM_TRS_CUST_ASS_BRH_SETVO.TABLE_UID, pk);
		
		if(vo == null){
			TBCRM_TRS_CUST_ASS_BRH_SETVO vos = new TBCRM_TRS_CUST_ASS_BRH_SETVO();
			vos.setcomp_id(pk);
			vos.setPRIORITY_ORDER(new BigDecimal(inputVO.getPriority_order()));				
			dam.create(vos);
		}else{
			throw new APException("ehl_01_common_016");
		}			        	
		this.sendRtnObject(null);
	}
	
	//修改
	public void editConfirm(Object body, IPrimitiveMap header) throws JBranchException {
		CRM321InputVO inputVO = (CRM321InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIORITY_ORDER FROM TBCRM_TRS_CUST_ASS_BRH_SET WHERE FCH_MAST_BRH LIKE :fch  ");
		queryCondition.setObject("fch", inputVO.getFch_branchNbr());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(list.size() > 0){
			String[] arr = new String[list.size()];
			for(int i = 0; i < list.size(); i++){
				arr[i] = list.get(i).get("PRIORITY_ORDER").toString();
			}
			for(String s: arr){
				if(s.equals(inputVO.getPriority_order()) && 
						!inputVO.getOripriority_order().equals(inputVO.getPriority_order())){
					throw new APException("ehl_01_common_016");
				}
			}		        	
		}
		
		TBCRM_TRS_CUST_ASS_BRH_SETVO vo = new TBCRM_TRS_CUST_ASS_BRH_SETVO();
		TBCRM_TRS_CUST_ASS_BRH_SETPK pk = new TBCRM_TRS_CUST_ASS_BRH_SETPK();
		
		pk.setFCH_MAST_BRH(inputVO.getOrifch_mast_brh());
		pk.setASS_BRH(inputVO.getOriass_brh());
		vo = (TBCRM_TRS_CUST_ASS_BRH_SETVO) dam.findByPKey(TBCRM_TRS_CUST_ASS_BRH_SETVO.TABLE_UID, pk);
		
		if(vo != null) {
			if (!inputVO.getOrifch_mast_brh().equals(inputVO.getFch_mast_brh()) || 
					!inputVO.getOriass_brh().equals(inputVO.getAss_brh()) || 
					!inputVO.getOripriority_order().equals(inputVO.getPriority_order()) ) {			
				
				TBCRM_TRS_CUST_ASS_BRH_SETVO editVO = new TBCRM_TRS_CUST_ASS_BRH_SETVO();
				TBCRM_TRS_CUST_ASS_BRH_SETPK editPk = new TBCRM_TRS_CUST_ASS_BRH_SETPK();
				editPk.setFCH_MAST_BRH(inputVO.getFch_branchNbr());
				editPk.setASS_BRH(inputVO.getBranchNbr());
				editVO = (TBCRM_TRS_CUST_ASS_BRH_SETVO) dam.findByPKey(TBCRM_TRS_CUST_ASS_BRH_SETVO.TABLE_UID, editPk);
				
				if(editVO == null){
					dam.delete(vo);
					editVO = new TBCRM_TRS_CUST_ASS_BRH_SETVO();
					editVO.setcomp_id(editPk);
					BigDecimal priority = new BigDecimal(inputVO.getPriority_order());
					editVO.setPRIORITY_ORDER(priority);
					dam.create(editVO);					
				}else{
					if(!inputVO.getOripriority_order().equals(inputVO.getPriority_order())){
						BigDecimal priority = new BigDecimal(inputVO.getPriority_order());
						editVO.setPRIORITY_ORDER(priority);
						dam.update(editVO);
					}else{
						throw new APException("ehl_01_common_016");						
					}
				}				
			}
		} else{
			throw new APException("ehl_01_common_024");					
		}
		this.sendRtnObject(null);
	}
	
	//全行FCH分派客戶數設定
	public void setting(Object body, IPrimitiveMap header) throws JBranchException {
		CRM321InputVO inputVO = (CRM321InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE TBSYSPARAMETER SET PARAM_CODE = :param_code WHERE PARAM_TYPE = 'CRM.TRS_TOTAL_FCH_CUST_NO' ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("param_code", inputVO.getCrm_trs_total_fch_cust_no());
		
		dam.exeUpdate(condition);
		this.sendRtnObject(null);
	}
	
	public void init(Object body, IPrimitiveMap header) throws JBranchException {
		CRM321OutputVO outputVO = new CRM321OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE from TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.TRS_TOTAL_FCH_CUST_NO' ");
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		String ans = "";
		if(list.size() > 0)
			ans = ObjectUtils.toString(list.get(0).get("PARAM_CODE"));
		outputVO.setCode(ans);
		this.sendRtnObject(outputVO);
	}
	
	//刪除
	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		CRM321InputVO inputVO = (CRM321InputVO) body;
		dam = this.getDataAccessManager();
		TBCRM_TRS_CUST_ASS_BRH_SETVO vo = new TBCRM_TRS_CUST_ASS_BRH_SETVO();
		TBCRM_TRS_CUST_ASS_BRH_SETPK pk = new TBCRM_TRS_CUST_ASS_BRH_SETPK();
		pk.setFCH_MAST_BRH(inputVO.getFch_branchNbr());
		pk.setASS_BRH(inputVO.getBranchNbr());
		vo = (TBCRM_TRS_CUST_ASS_BRH_SETVO) dam.findByPKey(
				TBCRM_TRS_CUST_ASS_BRH_SETVO.TABLE_UID, pk);
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
		}
		this.sendRtnObject(null);
	}
	
	//匯出
	public void exportList(Object body, IPrimitiveMap header) throws JBranchException {
		CRM321InputVO inputVO = (CRM321InputVO) body;
			
		// gen csv
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "FCH客戶分派行順序名單_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		
		for (Map<String, Object> map : inputVO.getList()) {
				// 3 column
			String[] records = new String[3];
			int i = 0;
			records[i] = checkIsNull(map, "FCH_MAST_BRH"); // 
			records[++i] = checkIsNull(map, "ASS_BRH"); // 
			records[++i] = checkIsNull(map, "PRIORITY_ORDER"); //
			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[3];
		int j = 0;
		csvHeader[j] = "FCH駐點行";
		csvHeader[++j] = "分派行";
		csvHeader[++j] = "優先順序";
			
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);  
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		// download
		notifyClientToDownloadFile(url, fileName);
		
		this.sendRtnObject(null);
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key)))){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	
}

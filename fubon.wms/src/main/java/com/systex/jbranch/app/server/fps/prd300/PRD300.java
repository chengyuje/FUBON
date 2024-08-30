package com.systex.jbranch.app.server.fps.prd300;

import com.systex.jbranch.app.common.fps.table.TBPRD_NANOVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * prd300
 * 
 * @author Jacky
 * @date 2020/1/9
 * @spec 奈米投商品維護
 */
@Component("prd300")
@Scope("request")
public class PRD300 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD300.class);
	
	/**
	 * 查詢奈米投所有資料
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD300InputVO inputVO = (PRD300InputVO) body;
		PRD300OutputVO return_VO = new PRD300OutputVO();
		dam = this.getDataAccessManager();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// TBSYS_PRD_LINK
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from TBPRD_NANO ORDER BY PRD_ID");
		queryCondition.setQueryString(sql.toString());
		list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	
	/**
	 * 給前端呼叫檢查產品代碼是否存在, 已存在則回傳產品名稱, 不存在則回傳空值
	 * @param inputVO
	 * @return outputVO
	 * @throws JBranchException
	 * 
	 */
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD300InputVO inputVO = (PRD300InputVO) body;
		PRD300OutputVO return_VO = new PRD300OutputVO();
		Map<String, String> map = checkProdID(inputVO.getPrd_id());
		return_VO.setName(map.get("PRD_NAME"));
		return_VO.setRiskcate_id(map.get("RISKCATE_ID"));
		return_VO.setCurrency(map.get("CURRENCY_STD_ID"));
		return_VO.setInv_level(map.get("INV_LEVEL"));
		return_VO.setCanEdit(true);
		this.sendRtnObject(return_VO);
	}
	
	/**
	 * 傳入產品ID, 取回產品代碼
	 * @param prd_id
	 * @return prod_name
	 * @throws JBranchException
	 */
	private Map<String,String> checkProdID(String prd_id)  throws JBranchException{
		dam = this.getDataAccessManager();

		Map<String, String> map = new HashMap<String,String>();
				
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID,PRD_NAME,RISKCATE_ID,CURRENCY_STD_ID,INV_LEVEL FROM TBPRD_NANO where PRD_ID = :id ");
		queryCondition.setObject("id", prd_id);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			map.put("PRD_NAME", ObjectUtils.toString(list.get(0).get("PRD_NAME")));
			map.put("RISKCATE_ID", ObjectUtils.toString(list.get(0).get("RISKCATE_ID")));
			map.put("CURRENCY_STD_ID", ObjectUtils.toString(list.get(0).get("CURRENCY_STD_ID")));
			map.put("INV_LEVEL", ObjectUtils.toString(list.get(0).get("INV_LEVEL")));
		}
		
		return map;
	}
	/**
	 * 新增奈米投產品資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void add(Object body, IPrimitiveMap header) throws Exception {
		PRD300InputVO inputVO = (PRD300InputVO) body;
		dam = this.getDataAccessManager();
		
		//檢查輸入值 1.PRD_ID不可重覆且不為空值 2.幣別不可為空值
		if(StringUtils.isBlank(inputVO.getPrd_id())){
			throw new APException("商品代碼不可為空值");
		}else{
			if(!this.checkProdID(inputVO.getPrd_id()).isEmpty()){
				throw new APException("商品代碼不可重覆");
			}
		}
		if(StringUtils.isBlank(inputVO.getCurrency_std_id())){
			throw new APException("計價幣別不可為空值");
		}
		TBPRD_NANOVO prdvo = new TBPRD_NANOVO();
		prdvo.setPRD_ID(inputVO.getPrd_id());
		prdvo.setPRD_NAME(inputVO.getPrd_name());
		prdvo.setRISKCATE_ID(inputVO.getRiskcate_id());
		prdvo.setINV_LEVEL(inputVO.getInv_level());
		prdvo.setSTOCK_BOND_TYPE(inputVO.getStock_bond_type());
		prdvo.setCORE_TYPE(inputVO.getCore_type());
		prdvo.setCURRENCY_STD_ID(inputVO.getCurrency_std_id());
		prdvo.setIS_SALE(inputVO.getIs_sale());
		dam.create(prdvo);
		this.sendRtnObject(null);
	}
	
	/**
	 * 修改奈米投產品資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void update(Object body, IPrimitiveMap header) throws Exception {
		PRD300InputVO inputVO = (PRD300InputVO) body;
		dam = this.getDataAccessManager();
		
		//檢查輸入值 1.PRD_ID必須存在且不為空值 2.幣別不可為空值
		if(StringUtils.isBlank(inputVO.getPrd_id())){
			throw new APException("商品代碼不可為空值");
		}
		if(StringUtils.isBlank(inputVO.getCurrency_std_id())){
			throw new APException("計價幣別不可為空值");
		}
		
		TBPRD_NANOVO prdvo;
		prdvo = (TBPRD_NANOVO) dam.findByPKey(TBPRD_NANOVO.TABLE_UID, inputVO.getPrd_id());
		if(prdvo == null){
			throw new APException("商品代碼不存在無法修改");
		}else{
			prdvo.setPRD_NAME(inputVO.getPrd_name());
			prdvo.setRISKCATE_ID(inputVO.getRiskcate_id());
			prdvo.setINV_LEVEL(inputVO.getInv_level());
			prdvo.setSTOCK_BOND_TYPE(inputVO.getStock_bond_type());
			prdvo.setCORE_TYPE(inputVO.getCore_type());
			prdvo.setCURRENCY_STD_ID(inputVO.getCurrency_std_id());
			prdvo.setIS_SALE(inputVO.getIs_sale());
			dam.update(prdvo);
		}
		this.sendRtnObject(null);
	}
	
	/**
	 * 刪除奈米投產品資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		PRD300InputVO inputVO = (PRD300InputVO) body;
		dam = this.getDataAccessManager();
		
		//檢查輸入值 1.PRD_ID必須存在且不為空值
		if(StringUtils.isBlank(inputVO.getPrd_id())){
			throw new APException("商品代碼不可為空值");
		}
		TBPRD_NANOVO prdvo;
		prdvo = (TBPRD_NANOVO) dam.findByPKey(TBPRD_NANOVO.TABLE_UID, inputVO.getPrd_id());
		if(prdvo == null){
			throw new APException("商品代碼不存在無法刪除");
		}else{
			dam.delete(prdvo);
		}
		this.sendRtnObject(null);
	}
}

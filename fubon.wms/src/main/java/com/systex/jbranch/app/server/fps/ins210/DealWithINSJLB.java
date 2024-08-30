package com.systex.jbranch.app.server.fps.ins210;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.insjlb.INSJLB;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOldItemListOutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;


@Component("dealwithinsjlb")
@Scope("request")
public class DealWithINSJLB extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(DealWithINSJLB.class);
	
	/**呼叫INSJLB取得既有保單*/
	public void callINSJLB(String custId, String LoginBraNbr, ArrayList<String> loginAO, String planTypes, List<Map<String, Object>> planList, String jobGrade) throws JBranchException{
		INSJLB insjlb = (INSJLB) PlatformContext.getBean("insjlb");
		GetOdItemListInputVO getOdItemListInputVO = new GetOdItemListInputVO();
		GetOldItemListOutputVO getOldItemListOutputVO = new GetOldItemListOutputVO();
		
		getOdItemListInputVO.setCustId(custId);
		getOdItemListInputVO.setPlanTypes(planTypes);
		getOdItemListInputVO.setLoginBranch(LoginBraNbr);
		getOdItemListInputVO.setLoginAOCode(loginAO);
		getOdItemListInputVO.setJobGrade(jobGrade);
		getOldItemListOutputVO = insjlb.getOldItemList(getOdItemListInputVO);
		
		if(getOldItemListOutputVO.getLstLogTable() == null){
			List<Map<String, Object>> oldItemList = getOldItemListOutputVO.getOldItemLlist();
			int i = 0;
			for(Map<String, Object> map:oldItemList) {
				putplanList(map, planList, i);
				i++;
			}
		}else{
			System.out.println(getOldItemListOutputVO.getLstLogTable());
		}

	}
	
	/**將INSJLB取出的資料塞進planList*/
	private void putplanList(Map<String, Object> oldItem,List<Map<String, Object>> planList,int index){
		Map<String, Object> plan = new HashMap<String, Object>();
		
		plan.put("index", index);
		plan.put("INSSEQ", oldItem.get("POLICYNO"));	//保單號碼
		plan.put("PRD_NAME", oldItem.get("PROD_NAME"));	//商品名稱
		plan.put("INSCO", oldItem.get("INSCO")); // 保險公司代號
		plan.put("INSCO_NAME", oldItem.get("INSCO_NAME")); // 保險公司名稱
		
		
		//保障初始化為0
		plan.put("coverage_C", 0);
		plan.put("coverage_D", 0);
		plan.put("coverage_W", 0);
		plan.put("coverage", 0);
		//保額
		plan.put("INSUREDAMT", String.valueOf(oldItem.get("QUANTITY")));
		//單位
		plan.put("unit", String.valueOf(oldItem.get("COVERUNIT")));
		//幣別
		plan.put("CURR_CD", String.valueOf(oldItem.get("CURR_CD")));
		
		//保障
		List<Map<String, Object>> lstCoverage = (List<Map<String, Object>>) oldItem.get("lstCoverage");
		if(lstCoverage.size()>0){
			List<String> planTypeList = new ArrayList<String>();
			for(Map<String, Object> coverage:lstCoverage){
				if("C".equals(coverage.get("planTypes"))){	//C:癌症住院 
					plan.put("coverage_C", coverage.get("coverage"));
					
				}else if("D".equals(coverage.get("planTypes"))){	//D:重大疾病
					plan.put("coverage_D", coverage.get("coverage"));
					
				}else if("W".equals(coverage.get("planTypes"))){	//W:長期看護
					plan.put("coverage_W", coverage.get("coverage"));
				
				}else{
					plan.put("coverage", lstCoverage.get(0).get("coverage"));
				}
				planTypeList.add(String.valueOf(coverage.get("planTypes")));
			}
			
			plan.put("planTypes", planTypeList);

		}
		
		//原幣保費 & 台幣保費 資訊源回傳前已算好直接取 
		List<Map<String, Object>> lstCoverAgePrem = (List<Map<String, Object>>) oldItem.get("lstCoverAgePrem");
		if(lstCoverAgePrem.size()>0) {
			plan.put("INSYEARFEE", oldItem.get("PREMIUM"));
			plan.put("LOCAL_INSYEARFEE", oldItem.get("LOCAL_INSYEARFEE"));
		}
		plan.put("IS_HR", oldItem.get("IS_HR"));
		planList.add(plan);
	}

	/**計算保額<br>
	 *COVERUNIT:保額單位<br>
	 * QUANTITY:保額 
	 * 2018/01/11停用*/
	private void calculateInsuredamt(Map<String, Object> plan,String COVERUNIT,String QUANTITY){
		String getCoverUnit = null;
		BigDecimal INSUREDAMT = new BigDecimal(0);
		if(COVERUNIT.length()>1){
			getCoverUnit = COVERUNIT.substring(0, 1);
		}else{
			getCoverUnit = COVERUNIT;
		}
		
		if("百".equals(getCoverUnit) || "佰".equals(getCoverUnit)){
			BigDecimal hundred = new BigDecimal(100);
			INSUREDAMT = new BigDecimal(QUANTITY).multiply(hundred);
		}else if("千".equals(getCoverUnit) || "仟".equals(getCoverUnit)){
			BigDecimal thousand = new BigDecimal(1000);
			INSUREDAMT = new BigDecimal(QUANTITY).multiply(thousand);
		}else if("萬".equals(getCoverUnit)){
			BigDecimal tenthousand = new BigDecimal(10000);
			INSUREDAMT = new BigDecimal(QUANTITY).multiply(tenthousand);
		}
		
		plan.put("INSUREDAMT", INSUREDAMT);
	}
	
	
	private void getCurrency(Map<String, Object> plan,String PROD_KEYNO){

		dam = getDataAccessManager();
		List<Map<String, Object>> currencyList = new ArrayList<Map<String,Object>>();
		try {
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			
			sb.append(" select LIST_E from TBPRD_INSDATA_PROD_MAIN ")
				.append(" where KEY_NO = :key_no ");
			qc.setObject("key_no", PROD_KEYNO);
			qc.setQueryString(sb.toString());
			currencyList = dam.exeQuery(qc);
			plan.put("CURR_CD", currencyList.get(0).get("LIST_E"));
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}

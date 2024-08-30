package com.systex.jbranch.app.server.fps.prd190;

import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 奈米投商品查詢
 * @author Jacky
 *
 */
@Component("prd190")
@Scope("request")
public class PRD190 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD110.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		PRD190InputVO inputVO = (PRD190InputVO) body;
		PRD190OutputVO outputVO = new PRD190OutputVO();
		dam = this.getDataAccessManager();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from TBPRD_NANO "); 
		sql.append(" where 1=1 ");
		
		//產品代碼
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getPrd_id() + "%");
		}
		//產品名稱
		if (!StringUtils.isBlank(inputVO.getPrd_name())) {
			sql.append("and PRD_NAME like :prd_name ");
			queryCondition.setObject("prd_name", "%" + inputVO.getPrd_name() + "%");
		}
		//商品風險等級
		if (!StringUtils.isBlank(inputVO.getRisk_level())){
			sql.append("and RISKCATE_ID = :riskcate_id ");
			queryCondition.setObject("riskcate_id", inputVO.getRisk_level());
		}
		//計價幣別
		if (!StringUtils.isBlank(inputVO.getCurrency())){
			sql.append("and CURRENCY_STD_ID = :currency ");
			queryCondition.setObject("currency", inputVO.getCurrency());
		}
		//類股票.類債券
		if (!StringUtils.isBlank(inputVO.getStock_bond_type())){
			sql.append("and STOCK_BOND_TYPE = :stock_bond_type ");
			queryCondition.setObject("stock_bond_type", inputVO.getStock_bond_type());
		}
		//核心.衛星
		if (!StringUtils.isBlank(inputVO.getCore_type())){
			sql.append("and CORE_TYPE = :core_type ");
			queryCondition.setObject("core_type", inputVO.getCore_type());			
		}
		
		//投資策略
		if (!StringUtils.isBlank(inputVO.getInv_level())){
			sql.append("and INV_LEVEL = :inv_level ");
			queryCondition.setObject("inv_level", inputVO.getInv_level());			
		}
				
		//依客戶ID查詢
		if (StringUtils.equals("1", inputVO.getType())){
			//KYC邏輯判斷
			CustKYCDataVO kycVO = new CustKYCDataVO();
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			kycVO = sot701.getCustKycData(inputVO.getCust_id());
			String kycStr = null;

			if(kycVO.getRISK_FIT() != null){
				//處理成SQL
				String[] kycArr = kycVO.getRISK_FIT().split(",");
				int i = 1;
				for(String str : kycArr) {
					if(i==1){
						kycStr="'" + str + "'";
					}else{
						kycStr += "'" + str + "'";
					}
					if(i<kycArr.length){
						kycStr += ",";
					}
					i++;
				}
			}
			sql.append("and RISKCATE_ID IN ("+kycStr+") ");
			sql.append("and IS_SALE = 'Y' ");
		}
		
		//from FPSProd_NANO進行查詢
		if (StringUtils.isNotBlank(inputVO.getRiskType())){
			XmlInfo xmlInfo = new XmlInfo();
	        String riskFit = (String) xmlInfo.getVariable("SOT.RISK_FIT_CONFIG", inputVO.getRiskType(), "F3");
	        String kycStr = null;

			if(riskFit != null){
				//處理成SQL
				String[] kycArr = riskFit.split(",");
				int i = 1;
				for(String str : kycArr) {
					if(i==1){
						kycStr="'" + str + "'";
					}else{
						kycStr += "'" + str + "'";
					}
					if(i<kycArr.length){
						kycStr += ",";
					}
					i++;
				}
			}
			sql.append("and RISKCATE_ID IN ("+kycStr+") ");
		}
		 
		//依可申購查詢
		if (StringUtils.equals("2", inputVO.getType())){
			sql.append("and IS_SALE = 'Y' ");
		}
		
		//依不可申購查詢
		if (StringUtils.equals("3", inputVO.getType())){
			sql.append("and IS_SALE = 'N' ");
		}
		
		sql.append(" ORDER BY PRD_ID");
		
		queryCondition.setQueryString(sql.toString());
		list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
}

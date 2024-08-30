package com.systex.jbranch.app.server.fps.prd110;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.fubon.commons.fitness.ProdFitnessOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd110
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd110")
@Scope("request")
public class PRD110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD110.class);
	
	public void getFundName(Object body, IPrimitiveMap header) throws JBranchException {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO return_VO = new PRD110OutputVO();
		return_VO.setFund_name(getFundCName(inputVO.getFund_id()));

		this.sendRtnObject(return_VO);	
	}

	public String getFundCName(String fundId) throws JBranchException {
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select FUND_CNAME from TBPRD_FUND where PRD_ID = :id ");
		queryCondition.setObject("id", fundId);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String fundCName;
		if (list.size() > 0)
			fundCName = ObjectUtils.toString(list.get(0).get("FUND_CNAME"));
		else
			fundCName = null;

		return fundCName;
	}
	
	// #1865_商品主檔篩選錄音提醒
	public void getCustInfo(Object body, IPrimitiveMap header) throws Exception {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO outputVO = new PRD110OutputVO();
		
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		inputVO_701.setCustID(inputVO.getCust_id());
		inputVO_701.setProdType(inputVO.getProd_type());
		
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		
		Boolean isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);
		
		FP032675DataVO fp032675DataVO = sot701.getInvestType(inputVO_701);
		
		// 首購
		outputVO.setIsFirstTrade(isFirstTrade ? "Y" : "N");
		
		// 特定客戶
		outputVO.setSpecialCust(fp032675DataVO.getInvestType());
		
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		inputVO_712.setCustID(inputVO.getCust_id());
		inputVO_712.setProdType("NF");
		inputVO_712.setCustRemarks(fp032675DataVO.getInvestType());
		inputVO_712.setOvsPrivateYN(getOVS_PRIVATE_YN(inputVO.getFund_id()));
		inputVO_712.setProfInvestorYN(fp032675DataVO.getCustProFlag());
		inputVO_712.setIsFirstTrade(outputVO.getIsFirstTrade());
		
		outputVO.setRecNeeded(sot712.getIsRecNeeded(inputVO_712));
		
		this.sendRtnObject(outputVO);
		
	}
	
	//#1865 是否是境外私募基金
	private String getOVS_PRIVATE_YN(String prdID) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT f.OVS_PRIVATE_YN ");
		sb.append("FROM TBPRD_FUND f ");
		sb.append("WHERE f.PRD_ID = :prodID ");
		queryCondition.setObject("prodID", prdID);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return (String) list.get(0).get("OVS_PRIVATE_YN");
	}

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO return_VO = new PRD110OutputVO();
		
		return_VO = this.inquire(inputVO);
		this.sendRtnObject(return_VO);
	}
	
	public PRD110OutputVO inquire(PRD110InputVO inputVO) throws Exception {
		PRD110OutputVO outputVO = new PRD110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from (select distinct CASE ");
	    sql.append("	WHEN TRUNC(SYSDATE) BETWEEN MAIN_PRD_SDATE AND MAIN_PRD_EDATE THEN 'H' ");
	    sql.append("	WHEN TRUNC(SYSDATE) BETWEEN RAISE_FUND_SDATE AND RAISE_FUND_EDATE THEN 'W' ");
	    sql.append("	ELSE 'N' END AS MAIN_PRD, ");
		sql.append("a.PRD_ID,a.FUND_CNAME,a.RISKCATE_ID,a.CURRENCY_STD_ID,a.FUND_TYPE,a.DIVIDEND_FREQUENCY, a.CUSTOMER_LEVEL, a.PROJECT1, a.PROJECT2, ");
		sql.append("a.OBU_PROD, a.OBU_BUY, a.OBU_PRO, a.OBU_AGE, a.BUY_TWD, a.QUOTAS, a.FLAG, a.HIGH_YIELD, b.FUS40, b.SELLING, b.VIGILANT, p1.PARAM_ORDER PORDER1, p2.PARAM_ORDER PORDER2, ");
		sql.append("(CASE WHEN f.PRD_ID IS NULL THEN 'N' ELSE 'Y' END) AS NFS100_YN, "); //是否為新興市場之非投資等級債券型基金
		sql.append("case when p1.PARAM_ORDER is not null and p2.PARAM_ORDER is not null and p1.PARAM_ORDER < p2.PARAM_ORDER then a.PROJECT1||';'||a.PROJECT2 ");
		sql.append("when p1.PARAM_ORDER is not null and p2.PARAM_ORDER is not null and p1.PARAM_ORDER > p2.PARAM_ORDER then a.PROJECT2||';'||a.PROJECT1 ");
		sql.append("when p1.PARAM_ORDER is not null and p2.PARAM_ORDER is null then a.PROJECT1 ");
		sql.append("when p1.PARAM_ORDER is null and p2.PARAM_ORDER is not null then a.PROJECT2 ");
		sql.append("else null end as PROJECT, NVL(a.OVS_PRIVATE_YN, 'N') as OVS_PRIVATE_YN, NVL(a.DYNAMIC_M, 'N') as DYNAMIC_M, NVL(a.DYNAMIC_C, 'N') AS DYNAMIC_C ");
		sql.append("from TBPRD_FUND a ");
		sql.append("left join TBPRD_FUNDINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append("left join TBPRD_FUNDRETURN c on a.PRD_ID = c.PRD_ID ");
		sql.append("left join TBPRD_FUNDRETURN_STAT d on a.LIPPER_ID = d.LIPPER_ID ");
		sql.append("left join TBPRD_FUND_BONUSINFO e on a.PRD_ID = e.PRD_ID ");
		sql.append("left join TBPRD_NFS100 f on a.PRD_ID = f.PRD_ID "); //有在裡面的基金就是新興市場之非投資等級債券型基金
		sql.append("left join (select PARAM_CODE, PARAM_ORDER from TBSYSPARAMETER where PARAM_TYPE = 'PRD.FUND_PROJECT') p1 on a.PROJECT1 = p1.PARAM_CODE "); //專案名稱1 取param_order
		sql.append("left join (select PARAM_CODE, PARAM_ORDER from TBSYSPARAMETER where PARAM_TYPE = 'PRD.FUND_PROJECT') p2 on a.PROJECT2 = p2.PARAM_CODE "); //專案名稱2 取param_order
		sql.append(" where 1=1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getFund_id())) {
			sql.append("and a.PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getFund_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getFund_name())) {
			sql.append("and a.FUND_CNAME like :name ");
			queryCondition.setObject("name", "%" + inputVO.getFund_name() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getRisk_level())) {								
			sql.append("and a.RISKCATE_ID in ( :level) ");
			queryCondition.setObject("level", inputVO.getRisk_level().split(";"));
		}
		if (!StringUtils.isBlank(inputVO.getCurrency())) {
			sql.append("and a.CURRENCY_STD_ID = :curr ");
			queryCondition.setObject("curr", inputVO.getCurrency());
		}
		if (!StringUtils.isBlank(inputVO.getDividend_type())) {
			sql.append("and a.DIVIDEND_TYPE = :div_type ");
			queryCondition.setObject("div_type", inputVO.getDividend_type());
		}
		if (!StringUtils.isBlank(inputVO.getDividend_fre())) {
			sql.append("and a.DIVIDEND_FREQUENCY = :div_fre ");
			queryCondition.setObject("div_fre", inputVO.getDividend_fre());
		}
		if (!StringUtils.isBlank(inputVO.getFund_type())) {
			sql.append("and a.FUND_TYPE = :fund_type ");
			queryCondition.setObject("fund_type", inputVO.getFund_type());
		}
		if (!StringUtils.isBlank(inputVO.getInv_area())) {
			sql.append("and a.INV_AREA = :inv_area ");
			queryCondition.setObject("inv_area", inputVO.getInv_area());
		}
		if (!StringUtils.isBlank(inputVO.getInv_target())) {
			sql.append("and a.INV_TARGET = :inv_tar ");
			queryCondition.setObject("inv_tar", inputVO.getInv_target());
		}
		if (!StringUtils.isBlank(inputVO.getTrust_com())) {
			sql.append("and substr(a.PRD_ID,1,2) = :trust_com ");
			queryCondition.setObject("trust_com", inputVO.getTrust_com());
		}
		
		if (!StringUtils.isBlank(inputVO.getRoi_dt())) {
			// 1D TBPRD_FUNDRETURN 
			if(StringUtils.equals("1D", inputVO.getRoi_dt())) {
				switch (inputVO.getRoi()) {
					case "01":
						sql.append("and c.RETURN <= -5 ");
						break;
					case "02":
						sql.append("and c.RETURN between -5 and -3 ");
						break;
					case "03":
						sql.append("and c.RETURN between -3 and 0 ");
						break;
					case "04":
						sql.append("and c.RETURN between 0 and 3 ");
						break;
					case "05":
						sql.append("and c.RETURN between 3 and 5 ");
						break;
					case "06":
						sql.append("and c.RETURN >= 5 ");
						break;
				}
			} else {
				sql.append("and d.RETURN_TYPE = :ret_type ");
				queryCondition.setObject("ret_type", inputVO.getRoi_dt());
				switch (inputVO.getRoi()) {
					case "01":
						sql.append("and d.RETURN <= -5 ");
						break;
					case "02":
						sql.append("and d.RETURN between -5 and -3 ");
						break;
					case "03":
						sql.append("and d.RETURN between -3 and 0 ");
						break;
					case "04":
						sql.append("and d.RETURN between 0 and 3 ");
						break;
					case "05":
						sql.append("and d.RETURN between 3 and 5 ");
						break;
					case "06":
						sql.append("and d.RETURN >= 5 ");
						break;
				}
			}
		}
		if (!StringUtils.isBlank(inputVO.getObu_YN())) {
			sql.append("and a.OBU_BUY = :obu_yn ");
			queryCondition.setObject("obu_yn", inputVO.getObu_YN());
		}
		if (inputVO.getType().matches("1|4")) {
			
			//由商品查詢進入Type=1，有輸入ID時，商品風險等級需使用客戶風險等級向下判斷
			//由下單進入時Type=4，此檢查在適配，因需顯示訊息
			if("1".equals(inputVO.getType())) {
				//動態鎖利母基金
				if (StringUtils.isNotBlank(inputVO.getDynamicType()) && inputVO.getDynamicType().matches("M")) {
					sql.append("and a.DYNAMIC_M = 'Y' ");
				}
				//動態鎖利子基金
				if (StringUtils.isNotBlank(inputVO.getDynamicType()) && inputVO.getDynamicType().matches("C")) {
					sql.append("and a.DYNAMIC_C = 'Y' ");
				}
				
				sql.append("and RISKCATE_ID in ");
				//KYC邏輯判斷
				CustKYCDataVO kycVO = new CustKYCDataVO();
				SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
				kycVO = sot701.getCustKycData(inputVO.getCust_id());
				String kycStr = null;
				String riskFits = kycVO.getRISK_FIT();
				//取得高資產客戶註記資料
				SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
				CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCust_id());
				
				//高資產客戶可越級適配
				if(hnwcData != null && StringUtils.equals("Y", hnwcData.getValidHnwcYN()) && StringUtils.equals("Y", hnwcData.getHnwcService())
						//動態鎖利下單不可越級適配；動態鎖利適配可越級
						&& (StringUtils.isBlank(inputVO.getDynamicType()) || StringUtils.equals("Y", inputVO.getFromPRD111YN())) ) { 
					//金錢信託不可越級適配
					if(!StringUtils.equals("M", inputVO.getTrustTS()) && kycVO != null && StringUtils.isNotBlank(kycVO.getKycLevel())) {
						String level = kycVO.getKycLevel(); //高資產特定客戶不得越級適配
						if(!StringUtils.equals("C4", kycVO.getKycLevel()) && !StringUtils.equals("Y", hnwcData.getSpFlag())) {
							//非特定客戶只能越一級
							level = "C" + ObjectUtils.toString((Integer.parseInt(kycVO.getKycLevel().substring(1)) + 1));
						}
						XmlInfo xmlInfo = new XmlInfo();
						riskFits = (String) xmlInfo.getVariable("SOT.RISK_FIT_CONFIG", level, "F3");
					}
				}
				
				if(riskFits != null){
					//處理成SQL
					String[] kycArr = riskFits.split(",");
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

				sql.append("("+kycStr+") ");
				
				if (StringUtils.equals("Y", inputVO.getSameSerialYN())) {	//轉換
					//同系列商品為基金代碼前兩碼相同
					sql.append("and (a.PRD_ID like :prd_id or ");					
					sql.append("substr(a.PRD_ID, 1, 2) in ");
					sql.append("(WITH Temp1 AS (select PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE='SOT.SAME_SERIAL_FUND' ");
					sql.append("				and PARAM_CODE = :assetPrdId ) ");
					sql.append("	select regexp_substr(PARAM_NAME,'[^,]+',1,rownum) from Temp1 ");
					sql.append("connect by rownum <= length(regexp_replace(PARAM_NAME,'[^,]+')) + 1 ) ) ");
					queryCondition.setObject("prd_id", StringUtils.substring(inputVO.getSameSerialProdId(), 0, 2) + "%");
					queryCondition.setObject("assetPrdId", StringUtils.substring(inputVO.getSameSerialProdId(), 0, 2));
					
					if(StringUtils.isNotBlank(inputVO.getDynamicType())) {
						//動態鎖利不須檢核可轉入
						//動態鎖利子基金與母基金需要相同計價幣別
						sql.append("and a.CURRENCY_STD_ID = :prodCurrM ");
						queryCondition.setObject("prodCurrM", inputVO.getDynamicProdCurrM());
						//需檢查是否可申購
						sql.append("and (a.IS_SALE = '1' ");
						sql.append("     OR (NVL(a.OVS_PRIVATE_YN, 'N') = 'Y' AND EXISTS (SELECT 1 FROM TBPRD_FUND_OVS_PRIVATE ");
						sql.append(" 			WHERE PRD_ID = a.PRD_ID AND TRADE_TYPE = '1' "); //申購
						sql.append(" 			AND TRUNC(SYSDATE) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE))))"); //境外私募基金在可交易期間中
					} else {
						//轉入帳號需要檢查是否可轉入
						sql.append("and b.NO_E_IN = 'N' ");
					}
				} else {
					//非轉換，轉換不需檢查是否可申購
					sql.append("and (a.IS_SALE = '1' ");
					sql.append("     OR (NVL(a.OVS_PRIVATE_YN, 'N') = 'Y' AND EXISTS (SELECT 1 FROM TBPRD_FUND_OVS_PRIVATE ");
					sql.append(" 			WHERE PRD_ID = a.PRD_ID AND TRADE_TYPE = '1' "); //申購
					sql.append(" 			AND TRUNC(SYSDATE) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE))))"); //境外私募基金在可交易期間中
				}
			}
		}
		// 若修改請同時修改prd230 java checkID // sort
		else if ("2".equals(inputVO.getType())) {
			sql.append("and (a.IS_SALE = '1' ");
			sql.append("     OR (NVL(a.OVS_PRIVATE_YN, 'N') = 'Y' AND EXISTS (SELECT 1 FROM TBPRD_FUND_OVS_PRIVATE ");
			sql.append(" 			WHERE PRD_ID = a.PRD_ID AND TRADE_TYPE = '1' "); //申購
			sql.append(" 			AND TRUNC(SYSDATE) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE))))"); //境外私募基金在可交易期間中
		} else if ("3".equals(inputVO.getType())) {
			sql.append("and (a.IS_SALE is null or a.IS_SALE != '1') ");
		}
		// for ocean
		if (StringUtils.isNotBlank(inputVO.getMain_prd())) {
			if(StringUtils.equals("Y", inputVO.getMain_prd()))
				sql.append("and sysdate between b.MAIN_PRD_SDATE and b.MAIN_PRD_EDATE ");
			else
				sql.append("and sysdate not between b.MAIN_PRD_SDATE and b.MAIN_PRD_EDATE ");
		}
		
		//基金轉換－僅提供後收型基金全部轉換同系列後收型基金功能 by Carley 2017.10.23
		if (!StringUtils.isBlank(inputVO.getIsBackend())) {
			if("Y".equals(inputVO.getIsBackend())){
				sql.append("and a.IS_BACKEND = 'Y' ");
			}
		}
		
		//股債類型 by Carley 2018.12.25
		if (!StringUtils.isBlank(inputVO.getStock_bond_type())) {
			sql.append("and b.STOCK_BOND_TYPE = :stock_bond_type ");
			queryCondition.setObject("stock_bond_type", inputVO.getStock_bond_type());
		}
		//#1339 基金標籤 主題&專案名稱  2022.1028
		if (!StringUtils.isBlank(inputVO.getFund_subject())) {
			sql.append("and REGEXP_LIKE(a.SUBJECT1 || a.SUBJECT2 || a.SUBJECT3, :fund_subject) ");
//			sql.append("and (a.SUBJECT1 = :fund_subject or a.SUBJECT2 = :fund_subject or a.SUBJECT3 = :fund_subject) ");
			queryCondition.setObject("fund_subject", inputVO.getFund_subject().replace(";", "|"));
		}
		if (!StringUtils.isBlank(inputVO.getFund_project())) {
			sql.append("and REGEXP_LIKE(a.PROJECT1 || a.PROJECT2, :fund_project) ");
//			sql.append("and (a.PROJECT1 = :fund_project or a.PROJECT2 = :fund_project) ");
			queryCondition.setObject("fund_project", inputVO.getFund_project().replace(";", "|"));
		}
		//#1404 客群標籤
		if (!StringUtils.isBlank(inputVO.getFund_customer_level())) {
			sql.append("and REGEXP_LIKE(a.CUSTOMER_LEVEL, :customer_level) ");
			queryCondition.setObject("customer_level", inputVO.getFund_customer_level().replace(";", "|")); 
		}
		
		/* Add order by 2017-04-24
		 * 20230420 #1404: 專案名稱變為order by第一條件
		 * 20230428 #1404: MAIN_PRD 改成 核心 → 衛星 → 一般
		 * 20230523 #1404: PROJECT1、 PROJECT1　取二者最小排序後　再看擁有的PROJECT數量(最多是2)
		 */
		sql.append(") order by least(coalesce(PORDER1,PORDER2),coalesce(PORDER2,PORDER1)), "); // 1
		sql.append("case when PORDER1 is not null and PORDER2 is not null then '1' when PORDER1 is null and PORDER2 is null then '3' else '2' end, "); // 2
		sql.append("decode(NVL(MAIN_PRD, 'N'), 'W', 1, 'H', 2, 3), "); // 3
		sql.append("decode(RISKCATE_ID, 'P1', 1, 'P2', 2, 'P3', 3, 'P4', 4, 5),  CUSTOMER_LEVEL, PRD_ID asc "); //else
		//
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		//客戶可申購商品
		if (inputVO.getType().matches("1|4")) {
			if(CollectionUtils.isEmpty(list)) {
				// 查無資料，Exception整段跳出
				throw new APException("ehl_01_common_009");
			} else {
				//初始化適配檢核
				ProdFitness prodFitness = (ProdFitness) PlatformContext.getBean("ProdFitness");
				ProdFitnessOutputVO fOutputVO = null;
				fOutputVO = prodFitness.validFundETFCustRiskAttr(inputVO.getCust_id());//客戶風險屬性檢核
				if(fOutputVO.getIsError()) {
					// 客戶資料適配檢核失敗，Exception整段跳出
					throw new APException(fOutputVO.getErrorID());
				}
				
				//基金、美國籍及商品代號查詢
				List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
				if (StringUtils.isNotBlank(inputVO.getFund_id())) {
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuilder sb = new StringBuilder();
					sb.append(" SELECT * FROM TBPRD_NATIONALITY	WHERE SOU_TYPE = 'NF' AND COUNTRY_ID = 'US' AND PROD_ID = :PRD_ID ");
					qc.setObject("PRD_ID", inputVO.getFund_id());
					qc.setQueryString(sb.toString());
					listMap = dam.exeQuery(qc);										
				}
				
				if (listMap.size() > 0){
					//客戶資料適配檢核
					fOutputVO = prodFitness.validFundETFCustFATCA(inputVO.getCust_id());//客戶FATCA註記檢核
					if(fOutputVO.getIsError()) {
						// 客戶資料適配檢核失敗，Exception整段跳出
						throw new APException(fOutputVO.getErrorID());
					}
				}
												
				//逐筆檢核商品適配
				List<Map<String, Object>> resultlist = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> product : list) {
					//傳入商品適配資料
					Boolean fromSOT = StringUtils.equals("4", inputVO.getType()) ? Boolean.TRUE : Boolean.FALSE;
					fOutputVO = prodFitness.validProdFund(product, fromSOT, inputVO);
					
					product.put("errorID", fOutputVO.getErrorID());
					product.put("warningMsg", fOutputVO.getWarningMsg().toString());
					
					if(BooleanUtils.isFalse(fOutputVO.getIsError())	|| StringUtils.equals("4", inputVO.getType())) {		
						resultlist.add(product);
					}
				}
						
				outputVO.setResultList(resultlist);
			}			
		} else {
			outputVO.setResultList(list);
		}
		
		return outputVO;
	}
	
	/*
	 * #1339 基金標籤 查詢語句增加欄位
	 */
	public void getFundInfo(Object body, IPrimitiveMap header) throws JBranchException {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO return_VO = new PRD110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// ,b.OTHER_RESTRICTION,b.SELLING
		sql.append("select a.PRD_ID,a.FUND_ENAME,a.FUND_CNAME,a.FUND_CNAME_A,a.IS_SALE,a.RISKCATE_ID,a.CURRENCY_STD_ID,a.DIVIDEND_TYPE,a.DIVIDEND_FREQUENCY,a.FUND_TYPE,a.INV_AREA,a.INV_TARGET,a.TRUST_COM,a.OBU_BUY,a.LAUNCH_DATE,a.FUND_SIZE, a.IS_BACKEND, c.FEE,c.FEE1,trunc(c.FEE2, 2) as FEE2,d.BASE_AMT_OF_PURCHASE ");
		sql.append(",b.ALLOTMENT_RATIO,b.MAIN_PRD_SDATE,b.MAIN_PRD_EDATE,b.RAISE_FUND_SDATE,b.RAISE_FUND_EDATE,b.IPO,b.IPO_SDATE,b.IPO_EDATE,b.CNR_YIELD,b.CNR_MULTIPLE,b.CNR_FEE,b.NO_E_PURCHASE,b.NO_E_OUT,b.NO_E_IN,b.NO_E_BUYBACK,b.QUOTA_CONTROL ");
		sql.append(",CASE WHEN TRUNC(SYSDATE) BETWEEN MAIN_PRD_SDATE AND MAIN_PRD_EDATE THEN 'Y' ELSE 'N' END AS MAIN_PRD ");
		sql.append(",CASE WHEN TRUNC(SYSDATE) BETWEEN RAISE_FUND_SDATE AND RAISE_FUND_EDATE THEN 'Y' ELSE 'N' END AS RAISE_FUND ");
		sql.append(",a.SUBJECT1, a.SUBJECT2, a.SUBJECT3, a.PROJECT1, a.PROJECT2, a.CUSTOMER_LEVEL, ");
		sql.append("case when p1.PARAM_ORDER is not null and p2.PARAM_ORDER is not null and p1.PARAM_ORDER < p2.PARAM_ORDER then a.PROJECT1||';'||a.PROJECT2 ");
		sql.append("when p1.PARAM_ORDER is not null and p2.PARAM_ORDER is not null and p1.PARAM_ORDER > p2.PARAM_ORDER then a.PROJECT2||';'||a.PROJECT1 ");
		sql.append("when p1.PARAM_ORDER is not null and p2.PARAM_ORDER is null then a.PROJECT1 ");
		sql.append("when p1.PARAM_ORDER is null and p2.PARAM_ORDER is not null then a.PROJECT2 ");
		sql.append("else null end as PROJECT ");
		sql.append("from (select * from TBPRD_FUND where PRD_ID = :prd_id) a ");
		sql.append("left join TBPRD_FUNDINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append("left join TBPRD_FUND_BONUSINFO c on a.PRD_ID = c.PRD_ID ");
		sql.append("left join (select PARAM_CODE, PARAM_NAME as BASE_AMT_OF_PURCHASE from TBSYSPARAMETER where PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1') d on a.CURRENCY_STD_ID = d.PARAM_CODE ");
		sql.append("left join (select PARAM_CODE, PARAM_ORDER from TBSYSPARAMETER where PARAM_TYPE = 'PRD.FUND_PROJECT') p1 on a.PROJECT1 = p1.PARAM_CODE "); //專案名稱1 取param_order
		sql.append("left join (select PARAM_CODE, PARAM_ORDER from TBSYSPARAMETER where PARAM_TYPE = 'PRD.FUND_PROJECT') p2 on a.PROJECT2 = p2.PARAM_CODE "); //專案名稱2 取param_order
		queryCondition.setObject("prd_id", inputVO.getFund_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getFundRestriction(Object body, IPrimitiveMap header) throws JBranchException {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO return_VO = new PRD110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("select b.FUS40, a.IS_SALE, b.NO_E_PURCHASE, b.NO_E_OUT, b.NO_E_IN, b.NO_E_BUYBACK, b.QUOTA_CONTROL ");
		sql.append("from (select * from TBPRD_FUND where PRD_ID = :prd_id) a ");
		sql.append("left join TBPRD_FUNDINFO b on a.PRD_ID = b.PRD_ID ");
		queryCondition.setObject("prd_id", inputVO.getFund_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getWorth(Object body, IPrimitiveMap header) throws JBranchException {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO return_VO = new PRD110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// TBPRD_FUNDPRICE
		if("1".equals(inputVO.getType())) {
			sql.append("select * from TBPRD_FUNDPRICE where PRD_ID = :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getFund_id());
			// 預設值
			if (!StringUtils.isBlank(inputVO.getStime())) {
				switch (inputVO.getStime()) {
					case "1":
						sql.append("and months_between(PRICE_DATE,sysdate) <= 1 ");
						break;
					case "2":
						sql.append("and months_between(PRICE_DATE,sysdate) <= 2 ");
						break;
					case "3":
						sql.append("and months_between(PRICE_DATE,sysdate) <= 3 ");
						break;
					case "4":
						sql.append("and months_between(PRICE_DATE,sysdate) <= 6 ");
						break;
					case "5":
						sql.append("and to_char(extract(day from (PRICE_DATE - SYSDATE) day to second) / 365, '0.999') <= 1 ");
						break;
					case "6":
						sql.append("and to_char(extract(day from (PRICE_DATE - SYSDATE) day to second) / 365, '0.999') <= 2 ");
						break;
					case "7":
						sql.append("and to_char(extract(day from (PRICE_DATE - SYSDATE) day to second) / 365, '0.999') <= 3 ");
						break;
					case "8":
						sql.append("and to_char(extract(day from (PRICE_DATE - SYSDATE) day to second) / 365, '0.999') <= 5 ");
						break;
				}
			}
			// 區間
			else {
				sql.append(" AND PRICE_DATE BETWEEN :start AND :end ");
				if (inputVO.getsDate() != null) {
					if (inputVO.geteDate() != null) {
						queryCondition.setObject("start", inputVO.getsDate());
						queryCondition.setObject("end", inputVO.geteDate());
					}
					else {
						queryCondition.setObject("start", inputVO.getsDate());
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(inputVO.getsDate());
						calendar.add(Calendar.YEAR, 5);
						queryCondition.setObject("end", calendar.getTime());
					}
				}
				if (inputVO.geteDate() != null) {
					if (inputVO.getsDate() != null) {
						queryCondition.setObject("start", inputVO.getsDate());
						queryCondition.setObject("end", inputVO.geteDate());
					}
					else {
						queryCondition.setObject("end", inputVO.geteDate());
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(inputVO.geteDate());
						calendar.add(Calendar.YEAR, -5);
						queryCondition.setObject("start", calendar.getTime());
					}
				}
				// all null default
				if(inputVO.getsDate() == null && inputVO.geteDate() == null) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(new Date());
					queryCondition.setObject("start", calendar.getTime());
					calendar.add(Calendar.YEAR, 5);
					queryCondition.setObject("end", calendar.getTime());
				}
			}
			sql.append("order by PRICE_DATE ");
		}
		// TBPRD_FUNDRETURN
		else if("2".equals(inputVO.getType())) {
			sql.append("select * from TBPRD_FUNDRETURN where PRD_ID = :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getFund_id());
			// 預設值
			if (!StringUtils.isBlank(inputVO.getStime())) {
				switch (inputVO.getStime()) {
					case "1":
						sql.append("and months_between(RETURN_DATE,sysdate) <= 1 ");
						break;
					case "2":
						sql.append("and months_between(RETURN_DATE,sysdate) <= 2 ");
						break;
					case "3":
						sql.append("and months_between(RETURN_DATE,sysdate) <= 3 ");
						break;
					case "4":
						sql.append("and months_between(RETURN_DATE,sysdate) <= 6 ");
						break;
					case "5":
						sql.append("and to_char(extract(day from (RETURN_DATE - SYSDATE) day to second) / 365, '0.999') <= 1 ");
						break;
					case "6":
						sql.append("and to_char(extract(day from (RETURN_DATE - SYSDATE) day to second) / 365, '0.999') <= 2 ");
						break;
					case "7":
						sql.append("and to_char(extract(day from (RETURN_DATE - SYSDATE) day to second) / 365, '0.999') <= 3 ");
						break;
					case "8":
						sql.append("and to_char(extract(day from (RETURN_DATE - SYSDATE) day to second) / 365, '0.999') <= 5 ");
						break;
				}
			}
			// 區間
			else {
				sql.append(" AND RETURN_DATE BETWEEN :start AND :end ");
				if (inputVO.getsDate() != null) {
					if (inputVO.geteDate() != null) {
						queryCondition.setObject("start", inputVO.getsDate());
						queryCondition.setObject("end", inputVO.geteDate());
					}
					else {
						queryCondition.setObject("start", inputVO.getsDate());
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(inputVO.getsDate());
						calendar.add(Calendar.YEAR, 5);
						queryCondition.setObject("end", calendar.getTime());
					}
				}
				if (inputVO.geteDate() != null) {
					if (inputVO.getsDate() != null) {
						queryCondition.setObject("start", inputVO.getsDate());
						queryCondition.setObject("end", inputVO.geteDate());
					}
					else {
						queryCondition.setObject("end", inputVO.geteDate());
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(inputVO.geteDate());
						calendar.add(Calendar.YEAR, -5);
						queryCondition.setObject("start", calendar.getTime());
					}
				}
				// all null default
				if(inputVO.getsDate() == null && inputVO.geteDate() == null) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(new Date());
					queryCondition.setObject("start", calendar.getTime());
					calendar.add(Calendar.YEAR, 5);
					queryCondition.setObject("end", calendar.getTime());
				}
			}
			sql.append("order by RETURN_DATE ");
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getArea(Object body, IPrimitiveMap header) throws JBranchException {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO return_VO = new PRD110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select A.NEXT_TIER_VALUE,B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
		sql.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER2' ");
		sql.append("where A.TIER = '1' and A.VALUE = :value ");
		queryCondition.setObject("value", inputVO.getFund_type());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getTarget(Object body, IPrimitiveMap header) throws JBranchException {
		PRD110InputVO inputVO = (PRD110InputVO) body;
		PRD110OutputVO return_VO = new PRD110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select A.NEXT_TIER_VALUE,B.PARAM_NAME from TBPRD_INVEST_AREA_MAPPING A ");
		sql.append("right join TBSYSPARAMETER B on B.PARAM_CODE = A.NEXT_TIER_VALUE and B.PARAM_TYPE='PRD.MKT_TIER3' ");
		sql.append("where A.TIER = '2' and A.VALUE = :value ");
		queryCondition.setObject("value", inputVO.getInv_area());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getCompany(Object body, IPrimitiveMap header) throws JBranchException {
		PRD110OutputVO return_VO = new PRD110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct FUND_COMPANY_ID,FUND_COMPANY_NAME from TBPRD_FUND_COMPANY WHERE FUND_COMPANY_NAME IS NOT NULL ORDER BY FUND_COMPANY_ID");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
}
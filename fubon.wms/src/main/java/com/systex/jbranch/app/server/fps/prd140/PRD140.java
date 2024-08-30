package com.systex.jbranch.app.server.fps.prd140;

import com.systex.jbranch.app.server.fps.prd120.PRD120;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.comutil.collection.CustomComparator;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.fubon.commons.fitness.ProdFitnessOutputVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * prd140
 *
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd140")
@Scope("request")
public class PRD140 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsService;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD140.class);

	public void getSnName(Object body, IPrimitiveMap header) throws JBranchException {
		PRD140InputVO inputVO = (PRD140InputVO) body;
		PRD140OutputVO return_VO = new PRD140OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select SN_CNAME from TBPRD_SN where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getSn_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			return_VO.setSn_name(ObjectUtils.toString(list.get(0).get("SN_CNAME")));
		else
			return_VO.setSn_name(null);
		this.sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		PRD140OutputVO return_VO = new PRD140OutputVO();
		return_VO = this.inquire(body);

		this.sendRtnObject(return_VO);
	}

	public PRD140OutputVO inquire(Object body) throws Exception {
		PRD140InputVO inputVO = (PRD140InputVO) body;
		PRD140OutputVO outputVO = new PRD140OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append(" select ");
		sql.append(" 	a.BOND_VALUE, a.RECORD_FLAG, "); //新增RECORD_FLAG來判別是否為客製化商品 Y = 是
		sql.append(" 	a.PRD_ID, a.SN_CNAME, a.IS_SALE, ");
		sql.append(" 	CASE WHEN (to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(b.INV_SDATE) and trunc(b.INV_EDATE)) then 'Y' ELSE 'N' END AS IS_EXPIRED, ");
		sql.append(" 	a.CURRENCY_STD_ID, ");
		sql.append(" 	a.RISKCATE_ID, a.OBU_BUY, a.PRD_RANK, ");
//		sql.append(" 	ROUND((a.DATE_OF_MATURITY - to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd')) / 365, 2) as DATE_OF_MATURITY, ");
//		sql.append("	c.BDA43 as DATE_OF_MATURITY, ");
		sql.append(" 	CASE WHEN c.BDA43 IS NULL THEN a.YEAR_OF_MATURITY ");
		sql.append(" 	WHEN c.BDA43 IS NOT NULL AND a.YEAR_OF_MATURITY IS NOT NULL AND TO_CHAR(c.LASTUPDATE,'yyyyMMddHH24miss') >= TO_CHAR(a.LASTUPDATE,'yyyyMMddHH24miss') then c.BDA43 ");
		sql.append(" 	WHEN c.BDA43 IS NOT NULL AND a.YEAR_OF_MATURITY IS NOT NULL AND TO_CHAR(c.LASTUPDATE,'yyyyMMddHH24miss') < TO_CHAR(a.LASTUPDATE,'yyyyMMddHH24miss') then a.YEAR_OF_MATURITY ");
		sql.append(" 	else c.BDA43 END AS DATE_OF_MATURITY, ");
		sql.append(" 	a.OBU_BUY as OBU_BUY_2, a.RATE_GUARANTEEPAY, ");
		sql.append(" 	CASE WHEN a.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY, ");
		sql.append(" 	a.SN_TYPE, b.INVESTMENT_TARGETS, ");
		sql.append(" 	a.DATE_OF_MATURITY as MATURITY_DATE, a.GLCODE, ");
		sql.append(" 	a.PROJECT, a.CUSTOMER_LEVEL, ");
		sql.append(" 	a.HNWC_BUY "); //限制高資產客戶申購 (Y/ )
		sql.append(" from TBPRD_SN a ");
		sql.append(" 	left join TBPRD_SNINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append(" 	left join TBPRD_BDS010 c on a.PRD_ID = c.BDA01 ");
		sql.append(" where 1=1 ");


//		sql.append(" select a.BOND_VALUE ,a.PRD_ID,a.SN_CNAME,a.IS_SALE,CASE WHEN (trunc(sysdate) between trunc(b.INV_SDATE) and trunc(b.INV_EDATE)) then 'Y' ELSE 'N' END AS IS_EXPIRED,a.CURRENCY_STD_ID,a.RISKCATE_ID,CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY, a.PRD_RANK, ");
//		sql.append(" ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) as DATE_OF_MATURITY, a.OBU_BUY as OBU_BUY_2, ");
//		sql.append(" a.RATE_GUARANTEEPAY,CASE WHEN a.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY,a.SN_TYPE,b.INVESTMENT_TARGETS, a.DATE_OF_MATURITY as MATURITY_DATE, a.GLCODE from TBPRD_SN a ");
//		sql.append(" left join TBPRD_SNINFO b on a.PRD_ID = b.PRD_ID where 1=1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getSn_id())) {
			sql.append("and a.PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getSn_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getSn_name())) {
			sql.append("and a.SN_CNAME like :name ");
			queryCondition.setObject("name", "%" + inputVO.getSn_name() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getSn_type())) {
			sql.append("and a.SN_TYPE = :sn_type ");
			queryCondition.setObject("sn_type", inputVO.getSn_type());
		}
		if (!StringUtils.isBlank(inputVO.getCurrency())) {
			sql.append("and a.CURRENCY_STD_ID = :curr ");
			queryCondition.setObject("curr", inputVO.getCurrency());
		}
		// 剩餘年期
		if (!StringUtils.isBlank(inputVO.getMaturity())) {
			switch (inputVO.getMaturity()) {
				case "01":
					sql.append("and ROUND((a.DATE_OF_MATURITY - to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd')) / 365, 2) <= 1 ");
					break;
				case "02":
					sql.append("and ROUND((a.DATE_OF_MATURITY - to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd')) / 365, 2) between 1 and 5 ");
					break;
				case "03":
					sql.append("and ROUND((a.DATE_OF_MATURITY - to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd')) / 365, 2) between 5 and 10 ");
					break;
				case "04":
					sql.append("and ROUND((a.DATE_OF_MATURITY - to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd')) / 365, 2) between 10 and 15 ");
					break;
				case "05":
					sql.append("and ROUND((a.DATE_OF_MATURITY - to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd')) / 365, 2) >= 15 ");
					break;
			}
		}
		if (!StringUtils.isBlank(inputVO.getRate_guaran())) {
			if("Y".equals(inputVO.getRate_guaran()))
				sql.append("and a.RATE_GUARANTEEPAY >= 100 ");
			else
				sql.append("and (a.RATE_GUARANTEEPAY is null or a.RATE_GUARANTEEPAY < 100) ");
		}
		if (!StringUtils.isBlank(inputVO.getRisk_level())) {
			sql.append("and a.RISKCATE_ID in ( :level) ");
			queryCondition.setObject("level", inputVO.getRisk_level().split(";"));
		}
		if (!StringUtils.isBlank(inputVO.getPi_YN())) {
			sql.append("and CASE WHEN a.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END = :pi_yn ");
			queryCondition.setObject("pi_yn", inputVO.getPi_YN());
		}
		if (!StringUtils.isBlank(inputVO.getHnwc_YN())) {
			sql.append("and a.HNWC_BUY = :hnwc_yn ");
			queryCondition.setObject("hnwc_yn", inputVO.getHnwc_YN());
		}
		if (!StringUtils.isBlank(inputVO.getObu_YN())) {
			sql.append("and a.OBU_BUY = :obu_yn ");
			queryCondition.setObject("obu_yn", inputVO.getObu_YN());
		}
		if (StringUtils.isNotBlank(inputVO.getSnProject())) {
			sql.append("and REGEXP_LIKE(a.PROJECT, :project) ");
			queryCondition.setObject("project", inputVO.getSnProject().replace(";", "|"));
		}

		if (StringUtils.isNotBlank(inputVO.getSnCustLevel())) {
			sql.append("and REGEXP_LIKE(a.CUSTOMER_LEVEL, :custLevel) ");
			queryCondition.setObject("custLevel", inputVO.getSnCustLevel().replace(";", "|"));
		}
		if (inputVO.getType().matches("1|4")) {
			sql.append("and a.IS_SALE = 'Y' ");
			sql.append("and (to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(B.INV_SDATE) and trunc(B.INV_EDATE)) ");

			//由商品查詢進入Type=1，有輸入ID時，商品風險等級需使用客戶風險等級向下判斷
			//由下單進入時Type=4，此檢查在適配，因需顯示訊息
			if("1".equals(inputVO.getType())) {
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
				if(hnwcData != null && StringUtils.equals("Y", hnwcData.getValidHnwcYN()) && StringUtils.equals("Y", hnwcData.getHnwcService())) {
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

				sql.append("(" + kycStr + ") ");
			}
		}
		else if ("2".equals(inputVO.getType()))
			sql.append("and a.IS_SALE = 'Y' and (to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(B.INV_SDATE) and trunc(B.INV_EDATE)) ");
		else if ("3".equals(inputVO.getType()))
			sql.append("and (a.IS_SALE is null or a.IS_SALE != 'Y' or not (to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(B.INV_SDATE) and trunc(B.INV_EDATE))) ");

//		sql.append("ORDER BY a.CUSTOMER_LEVEL, a.PRD_ID "); // 改用 Java 進行排序

		SimpleDateFormat sdft = new SimpleDateFormat("yyyyMMdd");
		queryCondition.setObject("simulatedDate", sdft.format(new Date()));
//		queryCondition.setObject("simulatedDate", cbsService.getCBSTestDate());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		PRD120 prd120 = PlatformContext.getBean(PRD120.class);
		Map<String, Object> tagsMap = prd120.getProjectTagsMap("PRD.SN_PROJECT");

		for (Map<String, Object> eachRaw: list) {
			prd120.orderProjectTags(eachRaw, tagsMap);
		}
		
		if(queryCondition.getQueryString().indexOf(" order by ") == -1){
			Collections.sort(list, new CustomComparator<>(
					PRD120.projComparator(tagsMap),
					CustomComparator.byField("CUSTOMER_LEVEL", CustomComparator.ORDER.ASC),
					CustomComparator.byField("PRD_ID", CustomComparator.ORDER.ASC)
			));		
		}

		//客戶可申購商品
		if (inputVO.getType().matches("1|4")) {
			if(CollectionUtils.isEmpty(list)) {
				// 查無資料，Exception整段跳出
				throw new APException("ehl_01_common_009");
			} else {
				//初始化適配檢核
				ProdFitness prodFitness = (ProdFitness) PlatformContext.getBean("ProdFitness");

				//客戶資料適配檢核
				ProdFitnessOutputVO fOutputVO = prodFitness.validCustBondSISN(inputVO.getCust_id());
				if(fOutputVO.getIsError()) {
					// 客戶資料適配檢核失敗，Exception整段跳出
					throw new APException(fOutputVO.getErrorID());
				}
				fOutputVO = prodFitness.validFundETFCustFATCA(inputVO.getCust_id());//客戶FATCA註記檢核 
				if(fOutputVO.getIsError()) {
					// 客戶資料適配檢核失敗，Exception整段跳出
					throw new APException(fOutputVO.getErrorID());
				}

				//債券年期&客戶年齡檢核
				ProdFitnessOutputVO fOutputVO_2 = new ProdFitnessOutputVO();

				//逐筆檢核商品適配
				List<Map<String, Object>> resultlist = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> product : list) {
					//傳入商品適配資料
					Boolean fromSOT = StringUtils.equals("4", inputVO.getType()) ? Boolean.TRUE : Boolean.FALSE;
					fOutputVO = prodFitness.validProdSN(product, fromSOT, inputVO);
					if(StringUtils.equals("M", inputVO.getTrustTS())) {
						//金錢信託檢核客戶年齡及商品年期
						if(!fOutputVO.getIsError()) {
							fOutputVO = prodFitness.validSNCustAgeM();
						}
					} else {
						//特金檢核客戶年齡及商品年期
						fOutputVO_2 = prodFitness.validCustAge();
					}
					product.put("errorID", fOutputVO.getErrorID());

					//原檢核 + 債券年期&客戶年齡檢核
					if (StringUtils.isNotBlank(fOutputVO.getWarningMsg().toString())) {
						if (StringUtils.isNotBlank(fOutputVO_2.getWarningMsg().toString())) {
							fOutputVO.setWarningMsg(fOutputVO_2.getWarningMsg().toString());
						}
						product.put("warningMsg", fOutputVO.getWarningMsg().toString());
					} else {
						product.put("warningMsg", fOutputVO_2.getWarningMsg().toString());
					}

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

	public void getSnInfo(Object body, IPrimitiveMap header) throws JBranchException {
		PRD140InputVO inputVO = (PRD140InputVO) body;
		PRD140OutputVO return_VO = new PRD140OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.PRD_ID,a.SN_CNAME,a.SN_TYPE,a.CURRENCY_STD_ID,a.DATE_OF_MATURITY, ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) as SURPLUS, a.YEAR_OF_MATURITY,a.RATE_GUARANTEEPAY,a.RISKCATE_ID,a.PI_BUY,CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY,a.IS_SALE,a.W8BEN_MARK, a.PROJECT, a.CUSTOMER_LEVEL ");
		sql.append(",b.ISIN_CODE,b.CREDIT_RATING_SP,b.CREDIT_RATING_MODDY,b.CREDIT_RATING_FITCH,b.AVOUCH_CREDIT_RATING_SP,b.AVOUCH_CREDIT_RATING_MODDY,b.AVOUCH_CREDIT_RATING_FITCH,b.INSTITION_OF_FLOTATION,b.INSTITION_OF_AVOUCH,b.TRANS_DATE,b.DATE_OF_FLOTATION,b.START_DATE_OF_BUYBACK,b.BASE_AMT_OF_PURCHASE,b.UNIT_AMT_OF_PURCHASE,b.BASE_AMT_OF_BUYBACK,b.UNIT_AMT_OF_BUYBACK,b.FREQUENCY_OF_INTEST_PAY,b.FIXED_RATE_DURATION,b.FIXED_DIVIDEND_RATE,b.CURRENCY_EXCHANGE,b.FLOATING_DIVIDEND_RATE,b.INVESTMENT_TARGETS,b.CNR_YIELD,b.RATE_OF_CHANNEL ");
		sql.append("from (select * from TBPRD_SN where PRD_ID = :prd_id) a ");
		sql.append("left join TBPRD_SNINFO b on a.PRD_ID = b.PRD_ID ");
		queryCondition.setObject("prd_id", inputVO.getSn_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getSnRestriction(Object body, IPrimitiveMap header) throws JBranchException {
		PRD140InputVO inputVO = (PRD140InputVO) body;
		PRD140OutputVO return_VO = new PRD140OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.IS_SALE,CASE WHEN (to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(b.INV_SDATE) and trunc(b.INV_EDATE)) then 'Y' ELSE 'N' END AS IS_EXPIRED,a.PI_BUY,CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY, ");
		sql.append(" a.HNWC_BUY "); //限制高資產客戶申購 (Y/ )
		sql.append("from (select * from TBPRD_SN where PRD_ID = :prd_id) a ");
		sql.append("left join TBPRD_SNINFO b on a.PRD_ID = b.PRD_ID ");
		queryCondition.setObject("prd_id", inputVO.getSn_id());
		queryCondition.setObject("simulatedDate", cbsService.getCBSTestDate());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	/*
	 * 20230425 #1454: 兩檔不顯示配息
	 */
	public void getSnDividend(Object body, IPrimitiveMap header) throws JBranchException {
		PRD140InputVO inputVO = (PRD140InputVO) body;
		PRD140OutputVO return_VO = new PRD140OutputVO();
		dam = this.getDataAccessManager();

		ArrayList<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
		// 累積配息率
		BigDecimal ACCRATE = new BigDecimal(0);

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.BDPE2, b.UNIT_OF_VALUE AS BDF08, ");
		sql.append("CASE b.FREQUENCY_OF_INTEST_PAY WHEN 1 THEN a.BDPE9/2 WHEN 3 THEN a.BDPE9/4 WHEN 4 THEN a.BDPE9/12 ELSE a.BDPE9 END AS BDPE9, c.BDASO ");
		sql.append("from TBPRD_BDS650 a left join TBPRD_SNINFO b on a.BDPE1 = b.PRD_ID  left join TBPRD_BDS019 c on a.BDPE1 = C.BDAS1 where a.BDPE1 = :prd_id and TRUNC(a.BDPE2) <= TRUNC(SYSDATE) and a.BDPEA is not null ");
		sql.append(" and a.snap_date = (select max(snap_date) from TBPRD_BDS650 ) ");
		sql.append("order by a.BDPE2 ");
		queryCondition.setObject("prd_id", inputVO.getSn_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			// 累積配息率
			BigDecimal rate = new BigDecimal(ObjectUtils.toString(map.get("BDPE9")));
			ACCRATE = ACCRATE.add(rate);
			map.put("ACCRATE", ACCRATE);
		}
		Collections.reverse(list);
		if("WMGS99999220".equals(inputVO.getSn_id()) || "WMGS99999927".equals(inputVO.getSn_id())) {
			list.clear();
		}
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getSnStocks(Object body, IPrimitiveMap header) throws JBranchException {
		PRD140InputVO inputVO = (PRD140InputVO) body;
		PRD140OutputVO return_VO = new PRD140OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		//登入角色
		String loginRole = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
//		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//業務處
//		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);	//營運區
//		Map<String, String> bmmgrMap   = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);	//分行主管業務主管作業主管
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);		//理專
		Map<String, String> fchMap     = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2);		//理專FCH
//		Map<String, String> psopMap    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);	//作業人員
//		Map<String, String> faiaMap    = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2);	//輔銷人員

		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT BOND_NBR, REGION_CENTER_ID, REGION_CENTER_NAME, ");
		sql.append("BRANCH_AREA_ID, BRANCH_AREA_NAME, BRA_NBR, BRANCH_NAME, ");
		sql.append("AO_CODE, EMP_ID, SUM(DENO_AMT) AS DENO_AMT, ");
		sql.append("SUM(DENO_AMT_TWD) AS DENO_AMT_TWD, COUNT(*) AS CUST_NBR FROM ( ");
		sql.append("SELECT SN.*, CUST.BRA_NBR, ORG.BRANCH_NAME, ");
		sql.append("ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME, ");
		sql.append("ORG.REGION_CENTER_ID, ORG.REGION_CENTER_NAME, CUST.AO_CODE, AO.EMP_ID FROM ( ");
		sql.append("SELECT A.*, CUR.BUY_RATE, A.DENO_AMT * CUR.BUY_RATE AS DENO_AMT_TWD FROM ( ");
		sql.append("SELECT AST.CUST_ID, AST.BOND_NBR, AST.DENO_AMT, INV_CRCY_TYPE ");
		sql.append("FROM TBCRM_AST_INV_FBOND AST ");

		switch(inputVO.getFrom()){
			case "PRD130":		//海外債
				sql.append("INNER JOIN TBPRD_BOND ");
				break;
			case "PRD140":		//SN
				sql.append("INNER JOIN TBPRD_SN ");
				break;
//			case "PRD150":		//SI
//				sql.append("INNER JOIN TBPRD_SI ");
//				break;
		}
		sql.append("PRD ON AST.BOND_NBR = PRD.PRD_ID ");
		sql.append("WHERE AST.BOND_NBR = :prd_id ) A ");
		sql.append("LEFT JOIN (SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 ");
		sql.append("WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053)) CUR  ");
		sql.append("ON A.INV_CRCY_TYPE = CUR.CUR_COD ) SN ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON SN.CUST_ID = CUST.CUST_ID ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ON CUST.BRA_NBR = ORG.BRANCH_NBR ");
		sql.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ) ");
		sql.append("WHERE 1 = 1 ");

		//非總行
		if (!headmgrMap.containsKey(loginRole)) {
			//理專、理專FCH
			if(fcMap.containsKey(loginRole) || fchMap.containsKey(loginRole)) {
				sql.append("AND EMP_ID = :loginID ");
				queryCondition.setObject("loginID", ws.getUser().getUserID());	//登入ID
			} else {
				sql.append("AND BRA_NBR IN ( :brNbrList ) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		}

		sql.append("GROUP BY BOND_NBR, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, ");
		sql.append("BRANCH_AREA_NAME, BRA_NBR, BRANCH_NAME, AO_CODE, EMP_ID ");
		sql.append("ORDER BY BOND_NBR, AO_CODE ");

		queryCondition.setObject("prd_id", inputVO.getSn_id());
		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}

	//下載庫存明細.csv
	public void download (Object body, IPrimitiveMap header) throws JBranchException {
		PRD140InputVO inputVO = (PRD140InputVO) body;

		List<Map<String,String>> downloadList = inputVO.getDownloadList();
		List<Object[]> csvData = new ArrayList<Object[]>();

		String[] csvHeader = new String[] { "商品代號", "處別", "區別", "分行", "AO Code", "原幣庫存", "台幣庫存", "客戶數"};
		String[] csvMain = new String[] { "BOND_NBR", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "AO_CODE", "DENO_AMT", "DENO_AMT_TWD", "CUST_NBR"};

		if (downloadList.size() > 0) {
			for (Map<String, String> map : downloadList) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					records[i] = checkIsNull(map, csvMain[i]);
				}

				csvData.add(records);
			}

			CSVUtil csv = new CSVUtil();

			// 設定表頭
			csv.setHeader(csvHeader);
			// 添加明細的List
			csv.addRecordList(csvData);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();

			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "商品庫存明細.csv");
		}

		sendRtnObject(null);
	}

	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull(Map<String, String> map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			if("BRANCH_NAME".equals(key)){
				String bra_nbr = "";
				String branch_name = "";

				if(map.get("BRA_NBR") != null){
					bra_nbr = map.get("BRA_NBR").toString();
				}

				if(map.get("BRANCH_NAME") != null){
					branch_name = map.get("BRANCH_NAME").toString();
				}
				return bra_nbr + "-" + branch_name;

			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}
}

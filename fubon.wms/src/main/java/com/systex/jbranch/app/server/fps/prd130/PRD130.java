package com.systex.jbranch.app.server.fps.prd130;

import java.util.ArrayList;
import java.util.Collections;
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

import com.systex.jbranch.app.server.fps.prd120.PRD120;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.comutil.collection.CustomComparator;
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
 * prd130
 *
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd130")
@Scope("request")
public class PRD130 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD130.class);

	public void getBondName(Object body, IPrimitiveMap header) throws JBranchException {
		PRD130InputVO inputVO = (PRD130InputVO) body;
		PRD130OutputVO return_VO = new PRD130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select BOND_CNAME from TBPRD_BOND where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			return_VO.setBond_name(ObjectUtils.toString(list.get(0).get("BOND_CNAME")));
		else
			return_VO.setBond_name(null);
		this.sendRtnObject(return_VO);
	}
	
	// #1865_商品主檔篩選錄音提醒
	public void getCustInfo(Object body, IPrimitiveMap header) throws Exception {
		PRD130InputVO inputVO = (PRD130InputVO) body;
		PRD130OutputVO outputVO = new PRD130OutputVO();
		
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		inputVO_701.setCustID(inputVO.getCust_id());
		inputVO_701.setProdType(inputVO.getProd_type());
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		FP032675DataVO fp032675DataVO = sot701.getInvestType(inputVO_701);
		
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		inputVO_707.setCustId(inputVO.getCust_id());
		inputVO_707.setProdId(inputVO.getPrd_id());
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
//		Boolean isFirstTrade = sot707.getIsCustFirstTrade(inputVO_707);
		Boolean isFirstTrade = false;
		if (sot701.isObu(inputVO.getCust_id())) {
			isFirstTrade = sot707.getIsCustFirstTradeOBU(inputVO_707);
		} else {
			isFirstTrade = sot707.getIsCustFirstTrade(inputVO_707);
		}
		// 首購
		outputVO.setIsFirstTrade(isFirstTrade ? "Y" : "N");

		// 特定客戶
		outputVO.setSpecialCust(fp032675DataVO.getInvestType());
		
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		inputVO_712.setCustID(inputVO.getCust_id());
		inputVO_712.setProdType("BN");
		inputVO_712.setCustRemarks(fp032675DataVO.getInvestType());
		inputVO_712.setProfInvestorYN(fp032675DataVO.getCustProFlag());
		inputVO_712.setIsFirstTrade(outputVO.getIsFirstTrade());
		
		outputVO.setRecNeeded(sot712.getIsRecNeeded(inputVO_712));

		this.sendRtnObject(outputVO);

	}

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		PRD130OutputVO return_VO = new PRD130OutputVO();
		return_VO = this.inquire(body);

		this.sendRtnObject(return_VO);
	}

	public PRD130OutputVO inquire(Object body) throws Exception {
		PRD130InputVO inputVO = (PRD130InputVO) body;
		PRD130OutputVO outputVO = new PRD130OutputVO();
		dam = this.getDataAccessManager();
		
		// 20170417 Sharon回覆：剩餘年期=(到期日-today())/360，四捨五入取到小數第二位
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.BOND_VALUE,a.PRD_ID,a.BOND_CNAME,a.IS_SALE,a.CURRENCY_STD_ID,a.RISKCATE_ID,CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY, a.PRD_RANK, a.PRD_RANK_DATE, ");
		sql.append(" ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) as DATE_OF_MATURITY, ");
		sql.append(" b.BOND_CREDIT_RATING_SP,a.YTM,CASE WHEN a.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY,a.BOND_CATE_ID, a.DATE_OF_MATURITY as MATURITY_DATE, ");
		sql.append(" a.OBU_BUY as OBU_BUY_2, NVL(a.IS_WEB_SALE, 'N') as IS_WEB_SALE, a.PROJECT, a.CUSTOMER_LEVEL, ");
		sql.append(" NVL(a.FACE_VALUE, 0) AS FACE_VALUE, b.BASE_AMT_OF_PURCHASE, a.UNIT_OF_PURCHASE, b.COUPON_TYPE, b.FREQUENCY_OF_INTEST_PAY, d.YTM_YTC, ");	// for 海外債試算
		sql.append(" TO_CHAR(A.DATE_OF_MATURITY, 'YYYYMMDD') AS END_DATE, TO_CHAR(b.NEXT_INTEREST_PAY_DATE, 'YYYYMMDD') AS NEXT_INTEREST_PAY_DATE, ");			// for 海外債試算
		sql.append(" a.HNWC_BUY "); //限制高資產客戶申購 (Y/ )
		sql.append(" from TBPRD_BOND a ");
		sql.append(" left join TBPRD_BONDINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append(" left join TBSYSPARAMETER c on b.BOND_CREDIT_RATING_SP = c.PARAM_CODE and c.PARAM_TYPE = 'PRD.CREDIT_RATING_SP_DTL' ");
		// WMS-CR-20250307-02_擬新增業管系統債券試算表功能
		sql.append(" LEFT JOIN ( ");
		sql.append(" SELECT BDEF1, BD056, TO_NUMBER(SUBSTR(BD056, 23, 1) || SUBSTR(BD056, 24, 9)) / 1000000 YTM_YTC ");
		sql.append(" FROM TBPMS_BDS056 ");
		sql.append(" WHERE BDEF3 = (SELECT MAX(BDEF3) FROM TBPMS_BDS056) ");
		sql.append(" ) D ON A.PRD_ID = D.BDEF1 ");
		
		sql.append(" WHERE 1 = 1 ");
		// sharon.lo 2017-06-02 10:37 確認只要顯示WMBB開頭的商品即可
		sql.append("and a.PRD_ID like 'WMBB%' ");
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and a.PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getPrd_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getBond_name())) {
			sql.append("and a.BOND_CNAME like :name ");
			queryCondition.setObject("name", "%" + inputVO.getBond_name() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getBond_cate())) {
			sql.append("and a.BOND_CATE_ID = :bond_cate ");
			queryCondition.setObject("bond_cate", inputVO.getBond_cate());
		}
		if (!StringUtils.isBlank(inputVO.getCurrency())) {
			sql.append("and a.CURRENCY_STD_ID = :curr ");
			queryCondition.setObject("curr", inputVO.getCurrency());
		}
		if (!StringUtils.isBlank(inputVO.getFace_value())) {
			switch(inputVO.getFace_value()) {
				case "01":
					sql.append("and a.FACE_VALUE between 1 and 2.9999999 ");
					break;
				case "02":
					sql.append("and a.FACE_VALUE between 3 and 4.9999999 ");
					break;
				case "03":
					sql.append("and a.FACE_VALUE between 5 and 6.9999999 ");
					break;
				case "04":
					sql.append("and a.FACE_VALUE >=7 ");
					break;
			}
		}
		// 剩餘年期
		if (!StringUtils.isBlank(inputVO.getMaturity())) {
			switch (inputVO.getMaturity()) {
				case "01":
					sql.append("and ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) <= 1 ");
					break;
				case "02":
					sql.append("and ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) between 1 and 5 ");
					break;
				case "03":
					sql.append("and ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) between 5 and 10 ");
					break;
				case "04":
					sql.append("and ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) between 10 and 15 ");
					break;
				case "05":
					sql.append("and ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) >= 15 ");
					break;
			}
		}
		if (!StringUtils.isBlank(inputVO.getYtm())) {
			sql.append("and a.YTM = :ytm ");
			queryCondition.setObject("ytm", inputVO.getYtm());
		}
		if (!StringUtils.isBlank(inputVO.getRating_sp())) {
			switch(inputVO.getRating_sp()) {
				case "01":
					sql.append("and c.PARAM_NAME in ('01','02','03','04') ");
					break;
				case "02":
					sql.append("and c.PARAM_NAME in ('01','02','03','04','05','06','07') ");
					break;
				case "03":
					sql.append("and c.PARAM_NAME in ('01','02','03','04','05','06','07','08','09','10') ");
					break;
				case "04":
					sql.append("and c.PARAM_NAME in ('11','12','13','14','15','16','17','18','19') ");
					break;
				case "05":
					sql.append("and b.BOND_CREDIT_RATING_SP is null ");
					break;
			}
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
			sql.append("and CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END = :obu_yn ");
			queryCondition.setObject("obu_yn", inputVO.getObu_YN());
		}

		if (StringUtils.isNotBlank(inputVO.getBondProject())) {
			sql.append("and REGEXP_LIKE(a.PROJECT, :project) ");
			queryCondition.setObject("project", inputVO.getBondProject().replace(";", "|"));
		}

		if (StringUtils.isNotBlank(inputVO.getBondCustLevel())) {
			sql.append("and REGEXP_LIKE(a.CUSTOMER_LEVEL, :custLevel) ");
			queryCondition.setObject("custLevel", inputVO.getBondCustLevel().replace(";", "|"));
		}

		//客戶可申購商品
		if (inputVO.getType().matches("1|4")) {
			sql.append("and a.IS_SALE = 'Y' and SYSDATE <= a.DATE_OF_MATURITY ");

			//由商品查詢進入Type=1，有輸入ID時，商品風險等級需使用客戶風險等級向下判斷
			//由下單進入時Type=4，此檢查在適配，因需顯示訊息
			if("1".equals(inputVO.getType())) {
				sql.append("and a.RISKCATE_ID in ");
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

				sql.append("("+kycStr+") ");
			}
		}
		// 若修改請同時修改prd250 java checkID // sort
		else if ("2".equals(inputVO.getType()))
			sql.append("and a.IS_SALE = 'Y' and SYSDATE <= a.DATE_OF_MATURITY ");
		else if ("3".equals(inputVO.getType()))
			sql.append("and (a.IS_SALE is null or a.IS_SALE != 'Y' or SYSDATE > a.DATE_OF_MATURITY) ");

		//		sql.append("order by a.CUSTOMER_LEVEL, a.PRD_ID "); 改用 Java 進行排序

		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		PRD120 prd120 = PlatformContext.getBean(PRD120.class);
		Map<String, Object> tagsMap = prd120.getProjectTagsMap("PRD.BOND_PROJECT");

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
				
				//針對新CRS/FATCA美國來源所得交易管控
				fOutputVO = prodFitness.validUSACustAndProd(inputVO.getCust_id());//客戶FATCA註記檢核
				if(fOutputVO.getIsError()) {
					// 客戶資料適配檢核失敗，Exception整段跳出
					throw new APException(fOutputVO.getErrorID());
				}

				//逐筆檢核商品適配
				List<Map<String, Object>> resultlist = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> product : list) {
					//傳入商品適配資料
					Boolean fromSOT = StringUtils.equals("4", inputVO.getType()) ? Boolean.TRUE : Boolean.FALSE;
					fOutputVO = prodFitness.validProdBond(product, fromSOT, inputVO);

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

	public void getBondInfo(Object body, IPrimitiveMap header) throws JBranchException {
		PRD130InputVO inputVO = (PRD130InputVO) body;
		PRD130OutputVO return_VO = new PRD130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.PRD_ID,a.BOND_CNAME,a.BOND_CATE_ID,a.CURRENCY_STD_ID,a.FACE_VALUE,a.DATE_OF_MATURITY, ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) as SURPLUS,a.YTM,a.RISKCATE_ID,a.PI_BUY,CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY,a.IS_SALE,a.W8BEN_MARK ");
		sql.append(",b.ISIN_CODE,b.BOND_PRIORITY,b.CREDIT_RATING_SP,b.CREDIT_RATING_MODDY,b.CREDIT_RATING_FITCH,b.BOND_CREDIT_RATING_SP, ");
		sql.append("b.BOND_CREDIT_RATING_MODDY,b.BOND_CREDIT_RATING_FITCH,b.ISSUER_BUYBACK,b.RISK_CHECKLIST,b.INSTITION_OF_FLOTATION,b.INSTITION_OF_AVOUCH, ");
		sql.append("b.DATE_OF_FLOTATION,b.BASE_AMT_OF_PURCHASE,b.BASE_AMT_OF_BUYBACK,b.UNIT_OF_VALUE,b.COUPON_TYPE,b.FREQUENCY_OF_INTEST_PAY,b.LAST_INTEREST_PAY_DATE, ");
		sql.append("b.NEXT_INTEREST_PAY_DATE, (CASE WHEN c.PRD_ID IS NOT NULL THEN 'Y' ELSE 'N' END) AS RISK_FILE_YN, d.PARAM_NAME AS W8BEN_URL, ");
		sql.append("a.CUSTOMER_LEVEL, a.PROJECT ");
		sql.append("from (select * from TBPRD_BOND where PRD_ID = :prd_id) a ");
		sql.append("left join TBPRD_BONDINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append("left join TBPRD_BOND_RISK_FILE c on c.PRD_ID = a.PRD_ID ");
		sql.append("left join TBSYSPARAMETER d on d.PARAM_TYPE = 'PRD.BOND_W8BEN_FILE_URL' AND PARAM_CODE='1' ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getBondRestriction(Object body, IPrimitiveMap header) throws JBranchException {
		PRD130InputVO inputVO = (PRD130InputVO) body;
		PRD130OutputVO return_VO = new PRD130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.IS_SALE,a.DATE_OF_MATURITY,a.W8BEN_MARK,b.RISK_CHECKLIST,a.PI_BUY, ");
		sql.append(" a.HNWC_BUY, "); //限制高資產客戶申購 (Y/ )
		sql.append(" CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY ");
		sql.append("from (select * from TBPRD_BOND where PRD_ID = :prd_id) a ");
		sql.append("left join TBPRD_BONDINFO b on a.PRD_ID = b.PRD_ID ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getBondPrice(Object body, IPrimitiveMap header) throws JBranchException {
		PRD130InputVO inputVO = (PRD130InputVO) body;
		PRD130OutputVO return_VO = new PRD130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select BARGAIN_DATE,SELL_PRICE,BUY_PRICE from TBPRD_BONDPRICE where PRD_ID = :prd_id ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		// date
		sql.append("and BARGAIN_DATE BETWEEN :start AND :end order by BARGAIN_DATE ");
		queryCondition.setObject("start", inputVO.getsDate());
		queryCondition.setObject("end", inputVO.geteDate());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getBondDividend(Object body, IPrimitiveMap header) throws JBranchException {
		PRD130InputVO inputVO = (PRD130InputVO) body;
		PRD130OutputVO return_VO = new PRD130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.BDPE2, b.UNIT_OF_VALUE AS BDF08, ");
		sql.append(" CASE b.FREQUENCY_OF_INTEST_PAY WHEN 1 THEN a.BDPE9/2 WHEN 3 THEN a.BDPE9/4 WHEN 4 THEN a.BDPE9/12 ELSE a.BDPE9 END AS BDPE9 ");
		sql.append(" from TBPRD_BDS650 a left join TBPRD_BONDINFO b on a.BDPE1 = b.PRD_ID where a.BDPE1 = :prd_id ");
		sql.append(" and a.snap_date = (select max(snap_date) from TBPRD_BDS650 where BDPE1 = :prd_id ) ");
		sql.append(" order by a.BDPE2 desc ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
}

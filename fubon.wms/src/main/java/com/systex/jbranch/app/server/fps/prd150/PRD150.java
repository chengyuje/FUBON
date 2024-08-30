package com.systex.jbranch.app.server.fps.prd150;

import com.systex.jbranch.app.server.fps.prd120.PRD120;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
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
import java.util.*;

/**
 * prd150
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd150")
@Scope("request")
public class PRD150 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsService;

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD150.class);

	public void getSiName(Object body, IPrimitiveMap header) throws JBranchException {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO return_VO = new PRD150OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select SI_CNAME from TBPRD_SI where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getSi_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			return_VO.setSi_name(ObjectUtils.toString(list.get(0).get("SI_CNAME")));
		else
			return_VO.setSi_name(null);
		this.sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		PRD150OutputVO return_VO = new PRD150OutputVO();
		return_VO = this.inquire(body);

		this.sendRtnObject(return_VO);
	}

	public PRD150OutputVO inquire(Object body) throws Exception {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO outputVO = new PRD150OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();


		sql.append(" select ");
		sql.append(" 	a.PRD_ID, a.SI_CNAME, a.CURRENCY_STD_ID, a.RISKCATE_ID, ");
		sql.append(" 	a.OBU_BUY, a.PRD_RANK, ");
//		sql.append(" 	ROUND((a.DATE_OF_MATURITY - to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd')) / 365, 2) as DATE_OF_MATURITY, ");
//		sql.append("	c.F12NPD as DATE_OF_MATURITY, ");
		sql.append(" 	CASE WHEN c.F12NPD IS NULL THEN a.YEAR_OF_MATURITY ");
		sql.append(" 	WHEN c.F12NPD IS NOT NULL AND a.YEAR_OF_MATURITY IS NOT NULL AND TO_CHAR(c.LASTUPDATE,'yyyyMMddHH24miss') >= TO_CHAR(a.LASTUPDATE,'yyyyMMddHH24miss') then c.F12NPD ");
		sql.append(" 	WHEN c.F12NPD IS NOT NULL AND a.YEAR_OF_MATURITY IS NOT NULL AND TO_CHAR(c.LASTUPDATE,'yyyyMMddHH24miss') < TO_CHAR(a.LASTUPDATE,'yyyyMMddHH24miss') then a.YEAR_OF_MATURITY ");
		sql.append(" 	else c.F12NPD END AS DATE_OF_MATURITY,  ");
		sql.append(" 	case when to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(b.INV_SDATE) and trunc(b.INV_EDATE) then 'Y' else null end as IS_SALE, ");
		sql.append(" 	CASE WHEN a.RATE_GUARANTEEPAY is null THEN 0 ELSE a.RATE_GUARANTEEPAY END AS RATE_GUARANTEEPAY, ");
		sql.append(" 	CASE WHEN a.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY, ");
		sql.append("	a.SI_TYPE, b.INVESTMENT_TARGETS, a.OBU_BUY as OBU_BUY_2,  ");
		sql.append("	a.DATE_OF_MATURITY as MATURITY_DATE, a.GLCODE, a.STAKEHOLDER, a.RECORD_FLAG, a.PROJECT, A.CUSTOMER_LEVEL, ");
		sql.append(" 	a.HNWC_BUY "); //限制高資產客戶申購 (Y/ )
		sql.append(" from TBPRD_SI a ");
		sql.append(" 	left join TBPRD_SIINFO b on a.PRD_ID = b.PRD_ID ");
		sql.append(" 	left join TBPRD_SDNPDMP2 c on c.SDPRD = a.PRD_ID ");
		sql.append(" where 1 = 1 ");

		// where
		if (!StringUtils.isBlank(inputVO.getSi_id())) {
			sql.append("and a.PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getSi_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getSi_name())) {
			sql.append("and a.SI_CNAME like :name ");
			queryCondition.setObject("name", "%" + inputVO.getSi_name() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getSi_type())) {
			sql.append("and a.SI_TYPE = :si_type ");
			queryCondition.setObject("si_type", inputVO.getSi_type());
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
			sql.append("and a.RISKCATE_ID in (:level) ");
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
		//#1404
		if (!StringUtils.isBlank(inputVO.getProject())) {
			sql.append("and REGEXP_LIKE(a.PROJECT, :project) ");
			queryCondition.setObject("project", inputVO.getProject().replace(";", "|"));
		}
		if (!StringUtils.isBlank(inputVO.getCustomer_level())) {
			sql.append("and REGEXP_LIKE(a.CUSTOMER_LEVEL, :customer_level) ");
			queryCondition.setObject("customer_level", inputVO.getCustomer_level().replace(";", "|"));
		}

		if (inputVO.getType().matches("1|4")) {
			sql.append("and to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(b.INV_SDATE) and trunc(b.INV_EDATE) ");
			// 2017/8/16 OBU客戶應帶出限OBU客戶申購之商品
			QueryConditionIF OBUQC = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			OBUQC.setQueryString("select OBU_YN from TBCRM_CUST_NOTE where cust_id = :cust_id");
			OBUQC.setObject("cust_id", inputVO.getCust_id());
			List<Map<String, Object>> qc_list = dam.exeQuery(OBUQC);
			if(qc_list.size() > 0) {
				// TBCRM_CUST_NOTE PK cust_id only one
				if(StringUtils.equals("Y", ObjectUtils.toString(qc_list.get(0).get("OBU_YN"))))
					sql.append("and a.OBU_BUY = 'O' ");
			}

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
					if(kycVO != null && StringUtils.isNotBlank(kycVO.getKycLevel())) {
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
		if ("2".equals(inputVO.getType()))
			sql.append("and to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(b.INV_SDATE) and trunc(NVL(b.INV_EDATE, sysdate)) ");
		else if ("3".equals(inputVO.getType()))
			sql.append("and to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') not between trunc(b.INV_SDATE) and trunc(NVL(b.INV_EDATE, sysdate)) ");
		//
		SimpleDateFormat sdft = new SimpleDateFormat("yyyyMMdd");
		queryCondition.setObject("simulatedDate", sdft.format(new Date()));
//		queryCondition.setObject("simulatedDate", cbsService.getCBSTestDate());

		//ordey by
//		sql.append(" ORDER BY a.CUSTOMER_LEVEL, a.PRD_ID" ); // 改用 Java 進行排序
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		PRD120 prd120 = PlatformContext.getBean(PRD120.class);
		Map<String, Object> tagsMap = prd120.getProjectTagsMap("PRD.SI_PROJECT");

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
				ProdFitnessOutputVO fOutputVO_2;

				//逐筆檢核商品適配
				List<Map<String, Object>> resultlist = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> product : list) {
					//傳入商品適配資料
					Boolean fromSOT = StringUtils.equals("4", inputVO.getType()) ? Boolean.TRUE : Boolean.FALSE;
					fOutputVO = prodFitness.validProdSI(product, fromSOT);
					fOutputVO_2 = prodFitness.validCustAge();
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

					if(BooleanUtils.isFalse(fOutputVO.getIsError()) || StringUtils.equals("4", inputVO.getType())) {
						resultlist.add(product);
					}
				}

				outputVO.setResultList(resultlist);

				//推介檢核邏輯
				SOT701InputVO inputVO_701 = new SOT701InputVO();
				FP032675DataVO fp032675DataVO = new FP032675DataVO();

				inputVO_701.setCustID(inputVO.getCust_id());

				SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
				fp032675DataVO = sot701.getFP032675Data(inputVO_701);

				outputVO.setFp032675DataVO(fp032675DataVO);
			}
		} else {
			outputVO.setResultList(list);
		}

		return outputVO;
	}

	public void getSiInfo(Object body, IPrimitiveMap header) throws JBranchException {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO return_VO = new PRD150OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.PRD_ID,a.SI_CNAME,a.SI_TYPE,a.CURRENCY_STD_ID,a.DATE_OF_MATURITY, ROUND((a.DATE_OF_MATURITY - SYSDATE) / 365, 2) as SURPLUS, a.YEAR_OF_MATURITY,CASE WHEN a.RATE_GUARANTEEPAY is null THEN 0 ELSE a.RATE_GUARANTEEPAY END AS RATE_GUARANTEEPAY,a.RISKCATE_ID,CASE WHEN a.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY,CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY, a.PROJECT, a.CUSTOMER_LEVEL ");
		sql.append(",b.TRANS_DATE,b.VALUE_DATE,b.START_DATE_OF_BUYBACK,b.BASE_AMT_OF_PURCHASE,b.UNIT_AMT_OF_PURCHASE,b.FREQUENCY_OF_INTEST_PAY,b.FIXED_DIVIDEND_RATE,b.FIXED_RATE_DURATION,b.FLOATING_DIVIDEND_RATE,b.CURRENCY_EXCHANGE,b.INVESTMENT_TARGETS,b.CNR_YIELD ");
		sql.append("from (select * from TBPRD_SI where PRD_ID = :prd_id) a ");
		sql.append("left join TBPRD_SIINFO b on a.PRD_ID = b.PRD_ID ");
		queryCondition.setObject("prd_id", inputVO.getSi_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getSiRestriction(Object body, IPrimitiveMap header) throws JBranchException {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO return_VO = new PRD150OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select CASE WHEN a.PI_BUY = 'Y' THEN 'Y' ELSE 'N' END AS PI_BUY,CASE WHEN a.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY, ");
		sql.append(" case when to_date(substr(:simulatedDate, 1, 8), 'yyyyMMdd') between trunc(b.INV_SDATE) and trunc(b.INV_EDATE) then 'Y' else null end as IS_SALE, ");
		sql.append(" a.HNWC_BUY "); //限制高資產客戶申購 (Y/ )
		sql.append(" from (select * from TBPRD_SI where PRD_ID = :prd_id) a ");
		sql.append(" left join TBPRD_SIINFO b on a.PRD_ID = b.PRD_ID ");
		queryCondition.setObject("simulatedDate", cbsService.getCBSTestDate());
		queryCondition.setObject("prd_id", inputVO.getSi_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getSiPrice(Object body, IPrimitiveMap header) throws JBranchException {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO return_VO = new PRD150OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from TBPRD_SIPRICE where SDPRD = :id ");
		sql.append("and SDDTE BETWEEN :start AND :end order by SDDTE ");
		queryCondition.setObject("id", inputVO.getSi_id());
		queryCondition.setObject("start", inputVO.getsDate());
		queryCondition.setObject("end", inputVO.geteDate());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getSiPrice2(Object body, IPrimitiveMap header) throws JBranchException {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO return_VO = new PRD150OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("WITH BASE AS ( ");
		sql.append("select a.A01001, a.A01002, max(a.A01003) as A01003 from TBPMS_SDA01MP0 a ");
		sql.append("where a.A01001 = :id group by a.A01001, a.A01002 ");
		sql.append(") ");
		sql.append("SELECT BASE.A01001, to_date(BASE.A01002, 'yyyyMMdd') as A01002, BASE.A01003, B.A01004 FROM BASE ");
		sql.append("LEFT JOIN TBPMS_SDA01MP0 B ON B.A01001 = BASE.A01001 AND B.A01002 = BASE.A01002 AND B.A01003 = BASE.A01003 ");
		sql.append("WHERE to_date(BASE.A01002, 'yyyyMMdd') BETWEEN :start AND :end order by BASE.A01002 ");
		queryCondition.setObject("id", inputVO.getSi_id());
		queryCondition.setObject("start", inputVO.getS2Date());
		queryCondition.setObject("end", inputVO.getE2Date());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void getSiDividend(Object body, IPrimitiveMap header) throws JBranchException {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO return_VO = new PRD150OutputVO();
		dam = this.getDataAccessManager();

		ArrayList<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
		// 累積配息率
		BigDecimal ACCRATE = new BigDecimal(0);

		// 配息日
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select  ");
		sql.append("PLADTE,  ");
		sql.append("(PLALAM/IVAMT2*100) as IVAMT2");

		sql.append(" from (  ");
		sql.append(" select A.SDPRD, sum(A.IVAMT2) IVAMT2, B.PLADTE, B.PLALAM from ");
		sql.append(" (SELECT T1.IVRDTE, T1.SDPRD, SUM(T1.IVAMT2) IVAMT2 ");
		sql.append(" FROM ");
		sql.append(" (select ");
		sql.append(" case ivsts1 ");
		sql.append(" when 'S3' then to_char(sysdate+1,'yyyy/mm/dd')  ");
		sql.append(" when 'S4' then to_char(ivdte1,'yyyy/mm/dd')  ");
		sql.append(" when 'S5' then to_char(ivdte4,'yyyy/mm/dd')  ");
		sql.append(" when 'S9' then to_char(ivdte4,'yyyy/mm/dd')  ");
		sql.append(" end IVRDTE, SDPRD, IVAMT2 ");
		//sql.append(" from ods_bank.SDINVMP0_day@ODSTOWMS ");
		sql.append(" from TBPMS_SDINVMP0_day ");
		//sql.append("where snap_date = (select max(snap_date) from ods_bank.SDINVMP0_day@ODSTOWMS) ");
		sql.append("where snap_date = (select max(snap_date) from TBPMS_SDINVMP0_day) ");
		sql.append("  and IVSTS1 IN ('S3','S4','S5','S9')  ");
		sql.append(" ) T1 ");
		sql.append("  GROUP BY T1.IVRDTE, T1.SDPRD) a,  ");
		sql.append(" (select f.PLAPRD, sum(f.PLALAM) as PLALAM, to_char(f.PLADTE,'yyyy/mm/dd') as PLADTE ");
		//sql.append(" from ods_bank.sdplamp0_ME@ODSTOWMS f  ");
		sql.append(" from (select * from TBPRD_SIDIVIDEND where import_flag='F') f  ");
		//sql.append("where f.snap_date = (select max(snap_date) from ods_bank.sdplamp0_ME@ODSTOWMS) ");
		sql.append("where f.snap_date = (select max(snap_date) from (select * from TBPRD_SIDIVIDEND where import_flag='F')) ");
		sql.append("  and (f.platyp='1' or f.platyp='2' or (f.platyp='F' and EXISTS ");
		//sql.append(" (select G.ivdte4 from ods_bank.SDINVMP0_day@ODSTOWMS G WHERE F.PLAPRD = G.SDPRD AND F.PLADTE = G.IVDTE4))) ");
		sql.append(" (select G.ivdte4 from TBPMS_SDINVMP0_day G WHERE F.PLAPRD = G.SDPRD AND F.PLADTE = G.IVDTE4))) ");
		sql.append(" group by f.PLAPRD, f.PLADTE order by f.PLAPRD) b  ");
		sql.append(" where a.SDPRD = b.PLAPRD  ");
		sql.append(" AND A.IVRDTE >= B.PLADTE  ");
		sql.append(" AND b.PLAPRD = :PRD_ID ");
		sql.append(" GROUP BY A.SDPRD, B.PLADTE, B.PLALAM  ");
		sql.append(" ORDER BY B.PLADTE,A.SDPRD) ");

//		sql.append("select PLADTE, PLALAM from TBPRD_SIDIVIDEND where PLAPRD = :si_id order by PLADTE ");
		queryCondition.setObject("PRD_ID", inputVO.getSi_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			HashMap<String, Object> ansMap = new HashMap<String, Object>();
//			// 配息日
			ansMap.put("DATE", map.get("PLADTE"));
//			
//			// 配息率
//			QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			StringBuffer sql2 = new StringBuffer();
//			sql2.append("select SUM(CONT_AMT) as IVAMT2 from TBPMS_WMG_SI_TXN where SNAP_DATE = :snap_date and PROD_CD = :prod_cd ");
//			queryCondition2.setObject("snap_date", map.get("PLADTE"));
//			queryCondition2.setObject("prod_cd", inputVO.getSi_id());
//			queryCondition2.setQueryString(sql2.toString());
//			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition2);
//			// 配息率
//			if(map.get("PLALAM") != null && list2.get(0).get("IVAMT2") != null) {
//			BigDecimal PLALAM = new BigDecimal(ObjectUtils.toString(map.get("PLALAM")));
			BigDecimal IVAMT2 = new BigDecimal(ObjectUtils.toString(map.get("IVAMT2")));
//				if(IVAMT2.compareTo(new BigDecimal(0)) == 0)
			ansMap.put("RATE", IVAMT2);
//				else
//					ansMap.put("RATE", PLALAM.divide(IVAMT2).multiply(new BigDecimal(100)).add(new BigDecimal(-0.00005)));
//			}
//			else
//				ansMap.put("RATE", -0.00005);

			// 每單位面額配息金額
			QueryConditionIF queryCondition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql3 = new StringBuffer();
			sql3.append("select BASE_AMT_OF_PURCHASE from TBPRD_SIINFO where PRD_ID = :prd_id ");
			queryCondition3.setObject("prd_id", inputVO.getSi_id());
			queryCondition3.setQueryString(sql3.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition3);
			if(list3.get(0).get("BASE_AMT_OF_PURCHASE") != null) {
				BigDecimal rate = new BigDecimal(ObjectUtils.toString(ansMap.get("RATE")));
				BigDecimal base = new BigDecimal(ObjectUtils.toString(list3.get(0).get("BASE_AMT_OF_PURCHASE")));
				ansMap.put("PERRATE", rate.multiply(base));
			} else
				ansMap.put("PERRATE", 0);

			// 累積配息率
			BigDecimal rate = new BigDecimal(ObjectUtils.toString(ansMap.get("RATE")));
			ACCRATE = ACCRATE.add(rate);
			ansMap.put("ACCRATE", ACCRATE);
			ansList.add(ansMap);
		}
		Collections.reverse(ansList);
		return_VO.setResultList(ansList);
		this.sendRtnObject(return_VO);
	}

	public void getSiStocks (Object body, IPrimitiveMap header) throws JBranchException {
		PRD150InputVO inputVO = (PRD150InputVO) body;
		PRD150OutputVO return_VO = new PRD150OutputVO();

		WorkStation ws = DataManager.getWorkStation(uuid);
		//登入角色
		String loginRole = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);        //理專
		Map<String, String> fchMap     = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2);        //理專FCH

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAI.*, MAI.DENO_AMT * CUR.BUY_RATE AS DENO_AMT_TWD FROM ( ");
		sql.append("SELECT DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, ");
		sql.append("DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, ");
		sql.append("CUST.BRA_NBR, DEFN.BRANCH_NAME, ");
		sql.append("AO.AO_CODE, AO.EMP_ID, SI.PROD_ID AS BOND_NBR, ");
		sql.append("SUM(SI.PRCH_AMT_ORGD) AS DENO_AMT, CRCY_TYPE, ");
		sql.append("COUNT(*) AS CUST_NBR FROM ( ");
		sql.append("SELECT PROD_ID, CUST_ID, PRCH_AMT_ORGD, PRCH_AMT_TWD, CRCY_TYPE ");
		sql.append("FROM TBCRM_AST_INV_SI WHERE PROD_ID = :prod_id ) SI ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST CUST ON SI.CUST_ID = CUST.CUST_ID ");
		sql.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON CUST.BRA_NBR = DEFN.BRANCH_NBR ");
		sql.append("WHERE 1 = 1 ");
		sql.append("GROUP BY PROD_ID, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, ");
		sql.append("BRANCH_AREA_NAME, BRA_NBR, BRANCH_NAME, AO.AO_CODE, EMP_ID, CRCY_TYPE ");
		sql.append("ORDER BY BRA_NBR, AO_CODE ) MAI ");
		sql.append("LEFT JOIN (SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 ");
		sql.append("WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053)) CUR ");
		sql.append("ON MAI.CRCY_TYPE = CUR.CUR_COD ");
		sql.append("WHERE 1 = 1 ");

		queryCondition.setObject("prod_id", inputVO.getSi_id());

		//非總行
		if (!headmgrMap.containsKey(loginRole)) {
			//理專、理專FCH
			if(fcMap.containsKey(loginRole) || fchMap.containsKey(loginRole)) {
				sql.append("AND MAI.EMP_ID = :loginID ");
				queryCondition.setObject("loginID", ws.getUser().getUserID());    //登入ID
			} else {
				sql.append("AND MAI.BRA_NBR IN ( :brNbrList ) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		}

		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
}
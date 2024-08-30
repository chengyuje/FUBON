package com.systex.jbranch.app.server.fps.prd179;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PRD179 原日盛保代壽險保單管理_分行總行端
 *
 * @date 2022/09/30
 */

@Component("prd179")
@Scope("request")
public class PRD179 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD179.class);

	public void init(Object body, IPrimitiveMap header) throws JBranchException {
		PRD179OutputVO return_VO = new PRD179OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// 保險產品類型
		sql.append("select distinct(POLICY_TYPE) LABEL, POLICY_TYPE DATA  from TBJSB_INS_SALES where POLICY_TYPE is not null ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> policeTypeList = dam.exeQuery(queryCondition);
		return_VO.setPoliceTypeList(policeTypeList);

		// 保單狀況
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select distinct(TRIM('　' FROM TRIM(POLICYSTATUS))) DATA, TRIM('　' FROM TRIM(POLICYSTATUS)) LABEL  from TBJSB_INS_SALES where POLICYSTATUS is not null ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> policeStatusList = dam.exeQuery(queryCondition);
		return_VO.setPoliceStatusList(policeStatusList);

		// 公司名稱
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT distinct(CNAME) LABEL, CNAME DATA FROM TBJSB_INS_PROD_COMPANY ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> companyNameList = dam.exeQuery(queryCondition);
		return_VO.setCompanyNameList(companyNameList);

		this.sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD179InputVO inputVO = (PRD179InputVO) body;
		PRD179OutputVO return_VO = new PRD179OutputVO();
		dam = this.getDataAccessManager();

		List<Map<String, Object>> resultList = getMainSqlResult(inputVO);
		return_VO.setResultList(resultList);
		this.sendRtnObject(return_VO);
	}

	private List getMainSqlResult(PRD179InputVO inputVO) throws DAOException, JBranchException {
		// TODO Auto-generated method stub
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		String roleID = inputVO.getLoginRole();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專

		StringBuffer sql = new StringBuffer();
		// 主查詢
		sql.append(" select  ");
		sql.append(" a.BRANCH_NAME BRANCH_NAME,  ");
		sql.append(" o.EMP_NAME SALES_NAME,  ");
		sql.append(" a.SALES_ID ,  ");
		sql.append(" a.RECOMMENDER_ID,  ");
		sql.append(" b.MemoDate MEMODATE,  ");
		sql.append(" b.MemoStr MEMOSTR,  ");
		sql.append(" case b.Payer_IC when 'I' then '自然人' when 'C' then '法人' end PAYER_IC, ");
		sql.append(" a.APPL_NAME PAYER_APPL_NAME,  ");
		sql.append(" f1.birth_date PAYER_BIRTH_DATE,  ");
		sql.append(" b.Payer_FIncome PAYER_FINCOME, ");
		sql.append(" a.APPL_ID PAYER_APPL_ID,  ");
		sql.append(" b.PayerTypeSerialNum PAYERTYPESERIALNUM,  ");
		sql.append(" f1.ANNUAL_INCOME_AMT PAYER_ANNUAL_INCOME_AMT, ");
		sql.append(" (g1.CEN_ZIP_CODE || ' ' || g1.CEN_ADDRESS) PAYER_CEN_ADDRESS,  ");
		sql.append(" (g1.COM_ZIP_CODE || ' ' || g1.COM_ADDRESS) PAYER_COM_ADDRESS,  ");
		sql.append(" g1.MOBILE_NO PAYER_MOBILE_NO,  ");
		sql.append(" g1.TEL_NO || case when g1.EXT_NO is not null then '#' || g1.EXT_NO end PAYER_TEL_NO,  ");
		sql.append(" g1.EMAIL PAYER_EMAIL,  ");
		sql.append(" (b.OLDAAG_PAYER || ',' || b.OLDAAG_INSURED) OLDAAG_INSURED,  ");
		sql.append(" b.Payer_Risk PAYER_RISK,  ");
		sql.append(" a.INSURED_NAME INSURED_NAME,  ");
		sql.append(" a.INSURED_ID INSURED_ID,  ");
		sql.append(" f2.birth_date INSURE_BIRTH_DATE,  ");
		sql.append(" case f2.gender when '1' then '男' when '2' then '女' when 'M' then '男' when 'F' then '女' end INSUTE_GENDER, ");
		sql.append(" c.InsuredTypeSerialNum INSUREDTYPESERIALNUM,  ");
		sql.append(" c.InsuredOccupationClass INSUREDOCCUPATIONCLASS,  ");
		sql.append(" f2.ANNUAL_INCOME_AMT INSURED_ANNUAL_INCOME_AMT,  ");
		sql.append(" b.Insured_FIncome INSURED_FINCOME,  ");
		sql.append(" (g2.CEN_ZIP_CODE || ' ' || g2.CEN_ADDRESS) INSURED_CEN_ADDRESS,  ");
		sql.append(" (g2.COM_ZIP_CODE || ' ' || g2.COM_ADDRESS) INSURED_COM_ADDRESS,  ");
		sql.append(" g2.MOBILE_NO INSURED_MOBILE_NO,  ");
		sql.append(" g2.TEL_NO || case when g2.EXT_NO is not null then '#' || g2.EXT_NO end INSURED_TEL_NO,  ");
		sql.append(" g2.EMAIL INSURED_EMAIL,  ");
		sql.append(" a.NewAddDate NEWADDDATE,  ");
		sql.append(" a.ACCEPTDATE,  ");
		sql.append(" a.ACCEPTID ,  ");
		sql.append(" a.APPLY_DATE,  ");
		sql.append(" a.CURRENCY, ");
		sql.append(" a.EXACT_MONEYRATE,  ");
		sql.append(" a.POLICYMODE,  ");
		sql.append(" a.DUEPREMIUM_ORI,  ");
		sql.append(" (a.DUEPREMIUM_ORI * a.EXACT_MONEYRATE) DUEPREMIUM, ");
		sql.append(" a.PROJECTID ,  ");
		sql.append(" a.PRODUCTNAME ,  ");
		sql.append(" (a.PREM_YEAR || a.PREM_TYPE) REM_YEAR,  ");
		sql.append(" (d.ProductPUD || d.ProductPUDType) PRODUCTPUD, ");
		sql.append(" a.CURRENCY SETTING_CURRENCY,  ");
		sql.append(" a.POLICY_TYPE ,  ");
		sql.append(" e.RiskRate RISK_TYPE,  ");
		sql.append(" (a.SA || a.SA_UNIT) SA_UNIT,  ");
		sql.append(" a.PERM , ");
		sql.append(" a.FIRST_PAY_WAY ,  ");
		sql.append(" NULL CONTINUE_PREMIUN,  ");
		sql.append(" b.PayMethod_Relation PAYMETHOD_RELATION,  ");
		sql.append(" b.Premiun_Source PREMIUN_SOURCE, ");
		sql.append(" a.FUSER,  ");
		sql.append(" a.FDATE,  ");
		sql.append(" a.PREMIUMTABLE,  ");
		sql.append(" a.ADFEERATE,  ");
		sql.append(" b.Discount DISCOUNT, ");
		sql.append(" b.Income_diffMemo INCOME_DIFFMEMO, ");
		sql.append(" c.BENEFITNAME BENEFITNAME1, ");
		sql.append(" c.BENEFITIDNO BENEFITIDNO1 , ");
		sql.append(" c.BenefitOccupationClass BENEFITOCCUPATIONCLASS1, ");
		sql.append(" c.BENEFITNAME2, ");
		sql.append(" c.BENEFITIDNO2, ");
		sql.append(" c.BenefitOccupationClass2 BENEFITOCCUPATIONCLASS2, ");
		sql.append(" c.BENEFITNAME3, ");
		sql.append(" c.BENEFITIDNO3, ");
		sql.append(" c.BenefitOccupationClass3 BENEFITOCCUPATIONCLASS3, ");
		sql.append(" c.BENEFITNAME4, ");
		sql.append(" c.BENEFITIDNO4, ");
		sql.append(" c.BenefitOccupationClass4 BENEFITOCCUPATIONCLASS4, ");
		sql.append(" a.POLICYSTATUS,  ");
		sql.append(" h.CNAME,  ");
		sql.append(" a.POLICY_NO,  ");
		sql.append(" a.PRODUCTID,  ");
		sql.append(" a.POLICY_TYPE POLICY_TYPE_2, ");
		sql.append(" f1.BRA_NBR, ");
		sql.append(" f1.AO_CODE, ");
		sql.append(" i.BRANCH_AREA_ID, ");
		sql.append(" i.REGION_CENTER_ID ");
		sql.append(" from TBJSB_INS_SALES a,  ");
		sql.append(" TBJSB_INS_Policy2 b, ");
		sql.append(" TBJSB_INS_Coverage2 c,  ");
		sql.append(" TBJSB_INS_PROD_LIFEITEM d, ");
		sql.append(" TBJSB_INS_PROD_MAIN e,  ");
		sql.append(" TBJSB_INS_PROD_COMPANY h, ");
		sql.append(" TBCRM_CUST_MAST f1,  ");
		sql.append(" tbcrm_cust_contact g1, ");
		sql.append(" TBCRM_CUST_MAST f2,  ");
		sql.append(" tbcrm_cust_contact g2, ");
		sql.append(" TBORG_MEMBER o, ");
		sql.append(" VWORG_DEFN_INFO i ");
		sql.append(" where a.ACCEPTID = b.AcceptIDSN(+) and nvl(a.ADD_SN, ' ') = nvl(b.ADD_SN(+), ' ')  "); // --
																											// 受理編號,
																											// 追加序號互串
		sql.append(" and b.PolicySerialNum = c.PolicySerialNum(+) "); // -- 保單序號
																		// 互串
		sql.append(" and c.ProductSerialNum = d.ProductSerialNum(+) and c.ItemSerialNum = d.ItemSerialNum(+) "); // --
																													// 產品序號,繳費方式互串
		sql.append(" and c.ProductSerialNum = e.ProductSerialNum (+) ");
		sql.append(" and e.INSURANCECOSERIALNUM = h.SERIALNUM(+) ");
		sql.append(" and a.APPL_ID = f1.cust_id(+) and f1.cust_id = g1.cust_id(+) ");
		sql.append(" and a.INSURED_ID = f2.cust_id(+) and f2.cust_id = g2.cust_id(+) ");
		sql.append(" and a.SALES_ID = o.cust_id(+) ");
		sql.append(" and f1.BRA_NBR = i.BRANCH_NBR(+) ");

		// 限制條件

		/*
		 * 可視範圍四個層級 業務處>營運區>分行>理專 1. 理專身分: 只考慮有無選擇理專來決定限制條件 2. 其他身分:
		 * 從理專往上看,有選擇的當限制條件 3. 其他身分若四個層級都沒有,則用業務處清單當限制條件
		 */
		// 理專
		boolean conditionEnd = false;
		if (fcMap.containsKey(roleID)) {
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append(" AND f1.AO_CODE = :AO_CODE ");
				condition.setObject("AO_CODE", inputVO.getAo_code());
			} else {
				List<String> aoCodeList = new ArrayList();
				for (Map map : inputVO.getAoCodeList()) {
					if (!checkIsNull(map, "DATA").equals("")) {
						aoCodeList.add(checkIsNull(map, "DATA"));
					}
				}
				sql.append(" AND f1.AO_CODE IN (:AO_CODE) ");
				condition.setObject("AO_CODE", aoCodeList);
			}
			conditionEnd = true;
		} else { // 其他身分
			// 理專
			if (!conditionEnd && StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append(" AND f1.AO_CODE = :AO_CODE ");
				condition.setObject("AO_CODE", inputVO.getAo_code());
				conditionEnd = true;
			}
			// 分行
			if (!conditionEnd && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append(" and f1.BRA_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				conditionEnd = true;
			}
			// 區
			if (!conditionEnd && StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append(" and i.BRANCH_AREA_ID = :branch_area_id ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
				conditionEnd = true;
			}
			// 處
			if (!conditionEnd && StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append(" and i.REGION_CENTER_ID = :region_center_id ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
				conditionEnd = true;
			}
		}
		// 非理專且四項都沒選就用業務處清單
		if (!conditionEnd && inputVO.getRegionList() != null) {
			List<String> regionList = new ArrayList();
			for (Map map : inputVO.getRegionList()) {
				if (!checkIsNull(map, "DATA").equals("")) {
					regionList.add(checkIsNull(map, "DATA"));
				}
			}
			sql.append("and i.REGION_CENTER_ID IN (:region_center_list) ");
			condition.setObject("region_center_list", regionList);
		}

		// *案件類型
		if (StringUtils.isNotBlank(inputVO.getCASETYPE())) {
			sql.append(" and a.JOR  = :CASETYPE ");
			condition.setObject("CASETYPE", inputVO.getCASETYPE().trim());
		}

		// 受理編號
		if (StringUtils.isNotBlank(inputVO.getACCEPTID())) {
			sql.append("and a.ACCEPTID = :ACCEPTID ");
			condition.setObject("ACCEPTID", inputVO.getACCEPTID());
		}

		// 保險產品類型
		if (StringUtils.isNotBlank(inputVO.getPOLICY_TYPE_2())) {
			sql.append(" and a.POLICY_TYPE = :POLICY_TYPE ");
			condition.setObject("POLICY_TYPE", inputVO.getPOLICY_TYPE_2().trim());
		}

		// 保單號碼
		if (StringUtils.isNotBlank(inputVO.getPOLICY_NO())) {
			sql.append("and a.POLICY_NO = :POLICY_NO ");
			condition.setObject("POLICY_NO", inputVO.getPOLICY_NO());
		}

		// 保單狀況
		if (StringUtils.isNotBlank(inputVO.getPOLICYSTATUS())) {
			sql.append(" and a.POLICYSTATUS = :POLICYSTATUS ");
			condition.setObject("POLICYSTATUS", inputVO.getPOLICYSTATUS().trim());
		}
		// *保險公司
		if (StringUtils.isNotBlank(inputVO.getCNAME())) {
			sql.append(" and h.CNAME = :CNAME ");
			condition.setObject("CNAME", inputVO.getCNAME().trim());
		}

		// 招攬人ID
		if (StringUtils.isNotBlank(inputVO.getSALES_ID())) {
			sql.append("and a.SALES_ID = :SALES_ID ");
			condition.setObject("SALES_ID", inputVO.getSALES_ID());
		}
		// 要保人ID
		if (StringUtils.isNotBlank(inputVO.getPAYER_APPL_ID())) {
			sql.append("and a.APPL_ID = :APPL_ID ");
			condition.setObject("APPL_ID", inputVO.getPAYER_APPL_ID());
		}
		// 產品代號
		if (StringUtils.isNotBlank(inputVO.getPRODUCTID())) {
			sql.append("and a.PRODUCTID = :PRODUCTID ");
			condition.setObject("PRODUCTID", inputVO.getPRODUCTID());
		}

		// 專案代號
		if (StringUtils.isNotBlank(inputVO.getPROJECTID())) {
			sql.append("and a.PROJECTID = :PROJECTID ");
			condition.setObject("PROJECTID", inputVO.getPROJECTID());
		}
		// 被保人ID
		if (StringUtils.isNotBlank(inputVO.getINSURED_ID())) {
			sql.append("and a.INSURED_ID = :INSURED_ID ");
			condition.setObject("INSURED_ID", inputVO.getINSURED_ID());
		}
		// 要保書填寫日
		if (StringUtils.isNotBlank(inputVO.getSAPPLY_DATE())) {
			sql.append("and NVL(a.APPLY_DATE, TRUNC(a.ACCEPTDATE)) >= :sDate ");
			condition.setObject("sDate", new Timestamp(changeStringToLong(inputVO.getSAPPLY_DATE())));
		}

		// 停止銷售日
		if (StringUtils.isNotBlank(inputVO.getEAPPLY_DATE())) {
			long long1 = Long.parseLong(inputVO.getEAPPLY_DATE());
			sql.append("and NVL(a.APPLY_DATE, TRUNC(a.ACCEPTDATE)) <= :eDate ");
			condition.setObject("eDate", new Timestamp(long1));
		}
		// 排序

		condition.setQueryString(sql.toString());
		return dam.exeQuery(condition);
	}

	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 匯出
	public void downloadCSV(Object body, IPrimitiveMap header) throws Exception {

		PRD179InputVO inputVO = (PRD179InputVO) body;
		String fileName = "原日盛保代壽險保單管理_分行/總行端" + ".csv";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		 XmlInfo xmlInfo = new XmlInfo();
		 Map<String, String> comm_yn = xmlInfo.doGetVariable("COMMON.YES_NO",FormatHelper.FORMAT_3);
		 Map<String, String> insutedType = xmlInfo.doGetVariable("PRD.INSURED_TYPE",FormatHelper.FORMAT_3);
		 Map<String, String> payerType = xmlInfo.doGetVariable("PRD.PAYER_TYPE",FormatHelper.FORMAT_3);
		 Map<String, String> occupationClass = xmlInfo.doGetVariable("PRD.OCCUPATION_CLASS",FormatHelper.FORMAT_3);
		 Map<String, String> paymethodRelation = xmlInfo.doGetVariable("PRD.PAYMETHOD_RELATION",FormatHelper.FORMAT_3);


		String[] csvHeader = getCsvHeader();
		String[] csvMain = getCsvMain();
		List<Object[]> csvData = new ArrayList<Object[]>();

		for (Map<String, Object> map : inputVO.getExportList()) {

			String[] records = new String[csvHeader.length];

			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
				 case "PAYERTYPESERIALNUM":
				 records[i] = payerType.get(checkIsNull(map, csvMain[i]).replace(".0",""));
				 break;
				 case "INSUREDTYPESERIALNUM":
					 records[i] = insutedType.get(checkIsNull(map, csvMain[i]).replace(".0",""));
					 break;
				 case "INSUREDOCCUPATIONCLASS":
					 records[i] = occupationClass.get(checkIsNull(map, csvMain[i]).replace(".0",""));
					 break;
				 case "PAYMETHOD_RELATION":
					 records[i] = paymethodRelation.get(checkIsNull(map, csvMain[i]).replace(".0",""));
					 break;
				 case "BENEFITOCCUPATIONCLASS1":
					 records[i] = occupationClass.get(checkIsNull(map, csvMain[i]).replace(".0",""));
					 break;
				 case "BENEFITOCCUPATIONCLASS2":
					 records[i] = occupationClass.get(checkIsNull(map, csvMain[i]).replace(".0",""));
					 break;
				 case "BENEFITOCCUPATIONCLASS3":
					 records[i] = occupationClass.get(checkIsNull(map, csvMain[i]).replace(".0",""));
					 break;
				 case "BENEFITOCCUPATIONCLASS4":
					 records[i] = occupationClass.get(checkIsNull(map, csvMain[i]).replace(".0",""));
					 break;
				default:
					records[i] = checkIsNull(map, csvMain[i]);
					break;
				}
			}
			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);
	}

	private String[] getCsvHeader() {
		String[] str = { "分行", "業務員姓名", "業務員(招攬人)ID", "推薦人員ID", "建檔人員姓名", "提醒日", "提醒事項", "要保人身分", "要保人姓名", "要保人生日", "要保人家庭收入", "要保人ID", "要保人類別", "要保人個人年收入", "要保人戶籍地址", "要保人聯絡地址", "要保人手機", "要保人電話", "要保人E-MAIL", "高齡錄音序號", "要保人風險等級", "被保人姓名", "被保人ID", "被保人生日", "被保人性別", "被保人類別", "被保人與要保人關係", "被保人個人年收入", "被保人家庭收入", "被保人戶籍地址", "被保人聯絡地址", "被保人手機", "被保人電話", "被保人E-MAIL", "輸入日", "受理日", "受理編號", "要保書填寫日", "商品幣別", "匯率", "繳別", "原幣總保費", "台幣總保費", "專案代號", "產品名稱", "保障年期", "產品年限", "設定幣別", "產品中分類", "風險等級", "保額(原幣)", "目標保費(原幣)", "首期繳費方式", "續期保費", "保費繳交與要保人關係", "保費來源", "放行者", "放行時間", "平台", "附加費用率", "折扣率", "年收入差異說明", "受益人1", "受益人1身份證字號", "受益人1與要保人關係", "受益人2", "受益人2身份證字號", "受益人2與要保人關係", "受益人3", "受益人3身份證字號", "受益人3與要保人關係", "受益人4", "受益人4身份證字號", "受益人4與要保人關係", "狀態", "保險公司", "保單號碼", "產品代號", "保險產品類型" };
		return str;
	}

	private String[] getCsvMain() {
		String[] str = { "BRANCH_NAME", "SALES_NAME", "SALES_ID", "RECOMMENDER_ID", "RECOMMENDER_NAME", "MEMODATE", "MEMOSTR", "PAYER_IC", "PAYER_APPL_NAME", "PAYER_BIRTH_DATE", "PAYER_FINCOME", "PAYER_APPL_ID", "PAYERTYPESERIALNUM", "PAYER_ANNUAL_INCOME_AMT", "PAYER_CEN_ADDRESS", "PAYER_COM_ADDRESS", "PAYER_MOBILE_NO", "PAYER_TEL_NO", "PAYER_EMAIL", "OLDAAG_INSURED", "PAYER_RISK", "INSURED_NAME", "INSURED_ID", "INSURE_BIRTH_DATE", "INSUTE_GENDER", "INSUREDTYPESERIALNUM", "INSUREDOCCUPATIONCLASS", "INSURED_ANNUAL_INCOME_AMT", "INSURED_FINCOME", "INSURED_CEN_ADDRESS", "INSURED_COM_ADDRESS", "INSURED_MOBILE_NO", "INSURED_TEL_NO", "INSURED_EMAIL", "NEWADDDATE", "ACCEPTDATE", "ACCEPTID", "APPLY_DATE", "CURRENCY", "EXACT_MONEYRATE", "POLICYMODE", "DUEPREMIUM_ORI", "DUEPREMIUM", "PROJECTID", "PRODUCTNAME", "REM_YEAR", "PRODUCTPUD", "SETTING_CURRENCY", "POLICY_TYPE", "RISK_TYPE", "SA_UNIT", "PERM", "FIRST_PAY_WAY", "CONTINUE_PREMIUN", "PAYMETHOD_RELATION", "PREMIUN_SOURCE", "FUSER", "FDATE", "PREMIUMTABLE", "ADFEERATE", "DISCOUNT", "INCOME_DIFFMEMO", "BENEFITNAME1", "BENEFITIDNO1", "BENEFITOCCUPATIONCLASS1", "BENEFITNAME2", "BENEFITIDNO2", "BENEFITOCCUPATIONCLASS2", "BENEFITNAME3", "BENEFITIDNO3", "BENEFITOCCUPATIONCLASS3", "BENEFITNAME4", "BENEFITIDNO4", "BENEFITOCCUPATIONCLASS4", "POLICYSTATUS", "CNAME", "POLICY_NO", "PRODUCTID", "POLICY_TYPE_2" };
		return str;
	}

	private long changeStringToLong(String str) {
		BigDecimal bd = new BigDecimal(str);
		return bd.longValue();
	}

}

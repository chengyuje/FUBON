package com.systex.jbranch.app.server.fps.prd120;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.comutil.collection.CustomComparator;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.fubon.commons.fitness.ProdFitnessOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * prd120
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("prd120")
@Scope("request")
public class PRD120 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD120.class);

	public void inquire_etf(Object body, IPrimitiveMap header) throws Exception {
		PRD120OutputVO return_VO = new PRD120OutputVO();
		return_VO = this.inquire_etf(body);

		this.sendRtnObject(return_VO);
	}

	public PRD120OutputVO inquire_etf(Object body) throws Exception {
		return inquire(body, "ETF");
	}

	public void inquire_stock(Object body, IPrimitiveMap header) throws Exception {
		PRD120OutputVO return_VO = new PRD120OutputVO();
		return_VO = this.inquire_stock(body);

		this.sendRtnObject(return_VO);
	}

	public PRD120OutputVO inquire_stock(Object body) throws Exception {
		return inquire(body, "STOCK");
	}

	/**
	 * 各商品都有專案 PROJECT 欄位（除了基金目前是 PROJECT1、PROJECT2 兩個欄位）
	 * PM 可以維護參數的名稱與順序，商品將貼標並排序來區分重要性：
	 * 優先權（重要性）： PARAM_ORDER 小至大 →  商品貼標數量（參數對應數量）多至少。
	 */
	public static Comparator<Map<String, Object>> projComparator(final Map<String, Object> tagsMap) {
		return new CustomComparator<>(
				new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Object obj1 = o1.get("PROJECT");
						Object obj2 = o2.get("PROJECT");

						if (obj1 == null && obj2 == null) {
							return 0;
						} else if (obj1 == null) {
							return 1;
						} else if (obj2 == null) {
							return -1;
						} else {
							String[] tags1 = obj1.toString().split(";");
							String[] tags2 = obj2.toString().split(";");

							Number num1 = (Number) tagsMap.get(tags1[0]);
							Number num2 = (Number) tagsMap.get(tags2[0]);

							// 若標籤不存在共用參數的判斷
							if (num1 == null && num2 == null) {
								return 0;
							} else if (num1 == null) {
								return 1;
							} else if (num2 == null) {
								return -1;
							} else {
								int compareNum = Integer.compare(num1.intValue(), num2.intValue());
								if (compareNum != 0)
									return compareNum;

								int count1 = tags1.length;
								int count2 = tags2.length;
								return count2 - count1;
							}
						}
					}
				}
		);
	}

	public void orderProjectTags(Map<String, Object> rawData, final Map<String, Object> tagsMap) {
		Object value = rawData.get("PROJECT");
		if (value == null)
			return;

		String tagsValue = (String) value;
		String[] tagsArr = tagsValue.split(";");
		// 照 PARAM_ORDER 進行排序
		Arrays.sort(tagsArr, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return ObjectUtils.toString(tagsMap.get(o1), "" + Integer.MAX_VALUE)
						.compareTo(ObjectUtils.toString(tagsMap.get(o2), "" + Integer.MAX_VALUE));
			}
		});
		// 最後再將他合併起來，複寫原本的 PROJECT VALUE。
		rawData.put("PROJECT", StringUtils.join(tagsArr, ";"));
	}

	/**
	 * 取得商品資料並適配
	 * @param body
	 * @param prodType
	 * @return
	 * @throws Exception
	 */
	private PRD120OutputVO inquire(Object body, String prodType) throws Exception {
		PRD120InputVO inputVO = (PRD120InputVO) body;
		PRD120OutputVO outputVO = new PRD120OutputVO();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if(StringUtils.equals("ETF", prodType))
			list = getETFList(inputVO);
		else
			list = getStockList(inputVO);

		//客戶可申購商品
		if (inputVO.getType().matches("1|4")) {
			if(CollectionUtils.isEmpty(list)) {
				// 查無資料，Exception整段跳出
				throw new APException("ehl_01_common_009");
			} else {
				//初始化適配檢核
				ProdFitness prodFitness = (ProdFitness) PlatformContext.getBean("ProdFitness");

				//客戶資料適配檢核
				ProdFitnessOutputVO fOutputVO = null;
				fOutputVO = prodFitness.validFundETFCustRiskAttr(inputVO.getCust_id());//客戶風險屬性檢核
				if(fOutputVO.getIsError()) {
					// 客戶資料適配檢核失敗，Exception整段跳出
					throw new APException(fOutputVO.getErrorID());
				}
				fOutputVO = prodFitness.validFundETFCustFATCA(inputVO.getCust_id());//客戶FATCA註記檢核
				if(fOutputVO.getIsError()) {
					// 客戶資料適配檢核失敗，Exception整段跳出
					throw new APException(fOutputVO.getErrorID());
				}

				//逐筆檢核商品適配
				List<Map<String, Object>> resultlist = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> product : list) {
					//傳入商品適配資料
					Boolean fromSOT = StringUtils.equals("4", inputVO.getType()) ? Boolean.TRUE : Boolean.FALSE;
					fOutputVO = prodFitness.validProdETF(product, fromSOT, inputVO);

					product.put("errorID", fOutputVO.getErrorID());
					product.put("warningMsg", fOutputVO.getWarningMsg().toString());

					if(BooleanUtils.isFalse(fOutputVO.getIsError()) || StringUtils.equals("4", inputVO.getType())) {
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

	/**
	 * 取得ETF商品資料
	 * #1404 加入標籤限制條件以及ORDER BY邏輯
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> getETFList(PRD120InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBPRD_ETF where 1=1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getEtf_code())) {
			sql.append("and PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getEtf_code() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getEtf_name())) {
			sql.append("and (ETF_CNAME like :name or ETF_ENAME like :name)");
			queryCondition.setObject("name", "%" + inputVO.getEtf_name() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getRisk_level())) {
			sql.append("and RISKCATE_ID in ( :risk) ");
			queryCondition.setObject("risk", inputVO.getRisk_level().split(";"));
		}
		if (!StringUtils.isBlank(inputVO.getCurrency())) {
			sql.append("and CURRENCY_STD_ID = :curr ");
			queryCondition.setObject("curr", inputVO.getCurrency());
		}
		if (!StringUtils.isBlank(inputVO.getPi_YN())) {
			sql.append("and PI_BUY = :pi ");
			queryCondition.setObject("pi", inputVO.getPi_YN());
		}
		if (!StringUtils.isBlank(inputVO.getCountry())) {
			sql.append("and LISTED_COUNTRY = :country ");
			queryCondition.setObject("country", inputVO.getCountry());
		}
		if (!StringUtils.isBlank(inputVO.getTactics())) {
			sql.append("and STRATEGY = :strategy ");
			queryCondition.setObject("strategy", inputVO.getTactics());
		}
		if (!StringUtils.isBlank(inputVO.getInvest_type())) {
			sql.append("and INVESTMENT_TYPE = :invest ");
			queryCondition.setObject("invest", inputVO.getInvest_type());
		}
		if (!StringUtils.isBlank(inputVO.getCompany())) {
			sql.append("and CORPORATION = :corpo ");
			queryCondition.setObject("corpo", inputVO.getCompany());
		}
		if (inputVO.getType().matches("1|4")) {
			sql.append("and IS_SALE is null ");

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

				sql.append("("+kycStr+") ");
			}
		}
		// 若修改請同時修改prd240 java etf_checkID // sort
		else if ("2".equals(inputVO.getType()))
			/*sql.append("and IS_SALE = 'Y' ");*/
			sql.append("and IS_SALE is null ");
		else if ("3".equals(inputVO.getType()))
			/*sql.append("and (IS_SALE is null or IS_SALE != 'Y') ");*/
			sql.append("and (IS_SALE in ('1','2','3')) ");

		if (!StringUtils.isBlank(inputVO.getStock_bond_type())) {
			sql.append("and STOCK_BOND_TYPE = :stock_bond_type ");
			queryCondition.setObject("stock_bond_type", inputVO.getStock_bond_type());
		}

		if (!StringUtils.isBlank(inputVO.getCustomer_level())) {
			sql.append("and REGEXP_LIKE(CUSTOMER_LEVEL, :customer_level) ");
			queryCondition.setObject("customer_level", inputVO.getCustomer_level().replace(";", "|"));
		}
		if (!StringUtils.isBlank(inputVO.getProject())) {
			sql.append("and REGEXP_LIKE(PROJECT, :project) ");
			queryCondition.setObject("project", inputVO.getProject().replace(";", "|"));
		}
//		sql.append("ORDER BY CUSTOMER_LEVEL, PRD_ID"); // 改用 Java 進行排序，避免 SQL 太繁冗

		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		Map<String, Object> tagsMap = getProjectTagsMap("PRD.ETF_PROJECT");

		for (Map<String, Object> eachRaw: list) {
			orderProjectTags(eachRaw, tagsMap);
		}
		if(queryCondition.getQueryString().indexOf(" order by ") == -1){
			Collections.sort(list, new CustomComparator<>(
					PRD120.projComparator(tagsMap),
					CustomComparator.byField("CUSTOMER_LEVEL", CustomComparator.ORDER.ASC),
					CustomComparator.byField("PRD_ID", CustomComparator.ORDER.ASC)
			));
		}


		return list;
	}

	public Map<String, Object> getProjectTagsMap(String type) throws JBranchException {
		List<Map<String, Object>> data = Manager.manage(this.getDataAccessManager())
				.append("select PARAM_CODE, PARAM_ORDER from TBSYSPARAMETER where PARAM_TYPE = :type ")
				.put("type", type)
				.query();

		final Map<String, Object> params = new HashMap<>();
		for (Map<String, Object> each: data) {
			params.put((String) each.get("PARAM_CODE"), each.get("PARAM_ORDER"));
		}
		return params;
	}

	/**
	 * 取得股票商品資料
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> getStockList(PRD120InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBPRD_STOCK where 1=1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getStock_code())) {
			sql.append("and PRD_ID like :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getStock_code() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getStock_name())) {
			sql.append("and (STOCK_CNAME like :name or STOCK_ENAME like :name)");
			queryCondition.setObject("name", "%" + inputVO.getStock_name() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCurrency())) {
			sql.append("and CURRENCY_STD_ID = :curr ");
			queryCondition.setObject("curr", inputVO.getCurrency());
		}
		if (!StringUtils.isBlank(inputVO.getStock_type())) {
			sql.append("and STOCK_TYPE = :stock_type ");
			queryCondition.setObject("stock_type", inputVO.getStock_type());
		}
		if (!StringUtils.isBlank(inputVO.getIndustry_type())) {
			sql.append("and INDUSTRY_TYPE = :industry ");
			queryCondition.setObject("industry", inputVO.getIndustry_type());
		}
		if (inputVO.getType().matches("1|4")){
			sql.append("and IS_SALE is null ");

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

				sql.append("("+kycStr+") ");
			}
		}

		if ("2".equals(inputVO.getType()))
			/*sql.append("and IS_SALE = 'Y' ");*/
			sql.append("and IS_SALE is null ");
		else if ("3".equals(inputVO.getType()))
			/*sql.append("and (IS_SALE is null or IS_SALE != 'Y') ");*/
			sql.append("and (IS_SALE in ('1','2','3')) ");

		queryCondition.setQueryString(sql.toString());

		return dam.exeQuery(queryCondition);
	}


	public void getDownload(Object body, IPrimitiveMap header) throws Exception {
		PRD120InputVO inputVO = (PRD120InputVO) body;
		PRD120OutputVO return_VO = new PRD120OutputVO();
		dam = this.getDataAccessManager();

		List<Map<String, Object>> fin = new ArrayList<Map<String,Object>>();
		// TBSYS_PRD_LINK
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_DUE_DATE,c.FILE_NAME, UPPER(substr(c.FILE_NAME, -3)) AS PDFNAME from ");
		sql.append("(select DOC_ID from TBSYS_PRD_LINK where 1=1 ");
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and PRD_ID = :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getPrd_id());
		}
		if (!StringUtils.isBlank(inputVO.getPtype())) {
			sql.append("and PTYPE = :ptype ");
			queryCondition.setObject("ptype", inputVO.getPtype());
		}
		sql.append(") a ");
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where 1=1 ");
		if (!StringUtils.isBlank(inputVO.getSubsystem_type())) {
			sql.append("and SUBSYSTEM_TYPE = :subtype ");
			queryCondition.setObject("subtype", inputVO.getSubsystem_type());
		}
		if (!StringUtils.isBlank(inputVO.getDoc_type())) {
			sql.append("and DOC_TYPE = :doctype ");
			queryCondition.setObject("doctype", inputVO.getDoc_type());
		}
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_DUE_DATE,FILE_NAME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and sysdate BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		fin.addAll(list);

		// TBSYS_PRD_SHARED_LINK
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_DUE_DATE,c.FILE_NAME, UPPER(substr(c.FILE_NAME, -3)) AS PDFNAME from ");
		sql.append("(select DOC_ID from TBSYS_PRD_SHARED_LINK where 1=1 ");
		if (!StringUtils.isBlank(inputVO.getPtype())) {
			sql.append("and PTYPE = :ptype ");
			queryCondition.setObject("ptype", inputVO.getPtype());
		}
		sql.append(") a ");
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where 1=1 ");
		if (!StringUtils.isBlank(inputVO.getSubsystem_type())) {
			sql.append("and SUBSYSTEM_TYPE = :subtype ");
			queryCondition.setObject("subtype", inputVO.getSubsystem_type());
		}
		if (!StringUtils.isBlank(inputVO.getDoc_type())) {
			sql.append("and DOC_TYPE = :doctype ");
			queryCondition.setObject("doctype", inputVO.getDoc_type());
		}
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_DUE_DATE,FILE_NAME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and sysdate BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		fin.addAll(list2);

		return_VO.setResultList(fin);
		this.sendRtnObject(return_VO);
	}

	public void getCombo(Object body, IPrimitiveMap header) throws Exception {
		PRD120OutputVO return_VO = new PRD120OutputVO();
		dam = this.getDataAccessManager();

		// 掛牌國家選擇
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_CODE as DATA, TRIM(PRD_DESC) AS LABEL from TBPRD_NRCOM4P where PRD_TYPE = 'A4'");
		return_VO.setCountryList(dam.exeQuery(queryCondition));

		// 操作策略選擇
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_CODE as DATA, TRIM(PRD_DESC) AS LABEL from TBPRD_NRCOM4P where PRD_TYPE = 'A5'");
		return_VO.setTacticsList(dam.exeQuery(queryCondition));

		// 投資類型選擇
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_CODE as DATA, TRIM(PRD_DESC) AS LABEL from TBPRD_NRCOM4P where PRD_TYPE = 'A6'");
		return_VO.setInvestList(dam.exeQuery(queryCondition));

		// 發行公司
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_CODE as DATA, TRIM(PRD_DESC) AS LABEL from TBPRD_NRCOM4P where PRD_TYPE = 'A3'");
		return_VO.setCompanyList(dam.exeQuery(queryCondition));

		// 股票類型
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_CODE as DATA, TRIM(PRD_DESC) AS LABEL from TBPRD_NRCOM4P where PRD_TYPE = 'A7'");
		return_VO.setStockList(dam.exeQuery(queryCondition));

		// 產業類型
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_CODE as DATA, TRIM(PRD_DESC) AS LABEL from TBPRD_NRCOM4P where PRD_TYPE = 'A8'");
		return_VO.setIndustryList(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO);
	}
}
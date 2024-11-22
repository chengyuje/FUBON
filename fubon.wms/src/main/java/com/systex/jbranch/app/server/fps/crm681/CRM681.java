package com.systex.jbranch.app.server.fps.crm681;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.SI_ASSETS;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.TRUST_ASSETS;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.trim;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_CUST_LISTVO;
import com.systex.jbranch.app.server.fps.crm830.CRM830;
import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.SC120100Service;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdprc09a.SDPRC09AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdprc09a.SDPRC09AOutputDetailVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdprc09a.SDPRC09AOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/09/21
 * 
 */
@Component("crm681")
@Scope("request")
public class CRM681 extends EsbUtil {
	
	@Autowired
	private CBSService cbsservice;
	
	@Autowired
	private SC120100Service sc120100Service;
	
	private DataAccessManager dam = null;
	
	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	private String thisClaz = this.getClass().getSimpleName() + ".";

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM681InputVO inputVO = (CRM681InputVO) body;
		CRM681OutputVO return_VO = inquire(inputVO.getCust_id());
		this.sendRtnObject(return_VO);
	}

	public CRM681OutputVO inquire(String custId) throws Exception {
		CRM681OutputVO outputVO = new CRM681OutputVO();
		dam = this.getDataAccessManager();

		//抓取客戶保險總AUM 
		CRM830 crm830 = (CRM830) PlatformContext.getBean("crm830");

		//富壽保險庫存資料
		List<Map<String, Object>> ins_list = crm830.getCustInsAUM(custId);
		BigDecimal insurance = new BigDecimal(0);
		if (CollectionUtils.isNotEmpty(ins_list)) {
			for (Map<String, Object> map : ins_list) {
				if (map.get("TOTAL_INS_AMT_TWD") != null)
					insurance = insurance.add((BigDecimal) map.get("TOTAL_INS_AMT_TWD"));
			}
		}
		//日盛保險庫存資料
		ins_list = crm830.getCustJSBInsAUM(custId);
		if (CollectionUtils.isNotEmpty(ins_list)) {
			for (Map<String, Object> map : ins_list) {
				if (map.get("TOTAL_INS_AMT_TWD") != null)
					insurance = insurance.add((BigDecimal) map.get("TOTAL_INS_AMT_TWD"));
			}
		}
		
		outputVO.setInsurance(insurance);

		return outputVO;
	}

	/**
	 * 投資總覽庫存資料查詢
	 *
	 * 使用電文: NFVIPA(基債股.指單).SDPRC09(SI)
	 * 
	 */
	public void getCustAssetInvestData(Object body, IPrimitiveMap header) throws Exception {
		
		CRM681OutputVO outputVO = new CRM681OutputVO();
		CRM681InputVO inputVO = (CRM681InputVO) body;
		dam = this.getDataAccessManager();
		
		String custID = inputVO.getCust_id();

		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID");
		}

		// parameter
		XMLInfo xmlinfo = (XMLInfo) PlatformContext.getBean("xmlinfo");
		HashMap<String, BigDecimal> ex_map = xmlinfo.getExchangeRate(); //取得最新匯率
		
		boolean isObu = isObu(inputVO, custID);

		// 所有投資庫存清單
		List<CustInvestAsset> assetList = new ArrayList<CustInvestAsset>();
		
		// 基債股
		assetList.addAll(getETF_ASSETS(custID, isObu, ex_map));
		
		// 指單類資產
		assetList.addAll(getTRUST_ASSETS(custID, isObu, ex_map));
		
		// SI類資產
		assetList.addAll(getSI_ASSETS(custID, isObu, ex_map));
		
		// DCI類資產
		assetList.addAll(getDCI_ASSETS(custID, isObu, ex_map));
		
		// 證券類資產
		List<Map<String, Object>> list = getSecCustData(inputVO);
		if (list.size() > 0) {
			assetList.addAll(getSEC_ASSETS(custID, isObu, ex_map));
		} 
		
		outputVO.setSecCustCrossSelling(list.size() > 0 ? "Y" : "N");
		
		// return
		outputVO.setAssetList(assetList);

		BigDecimal investment = new BigDecimal(0);
		for (CustInvestAsset tmpVO : assetList) {
			investment = investment.add(tmpVO.getTOTAL_SUM_TWD());
		}
		outputVO.setInvestment(investment);

		this.sendRtnObject(outputVO);
	}
	
	/**
	 * #1913
	 * 查詢[ 除了 ]基金、海外ETF/股票、海外債、SI、SN以外
	 * 的資產總攬
	 */
	public void getCustAssetInvestData2(Object body, IPrimitiveMap header) throws Exception {
		
		CRM681OutputVO outputVO = new CRM681OutputVO();
		CRM681InputVO inputVO = (CRM681InputVO) body;
		dam = this.getDataAccessManager();
		
		String custID = inputVO.getCust_id();

		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID");
		}

		// parameter
		XMLInfo xmlinfo = (XMLInfo) PlatformContext.getBean("xmlinfo");
		HashMap<String, BigDecimal> ex_map = xmlinfo.getExchangeRate(); //取得最新匯率
		
		boolean isObu = isObu(inputVO, custID);

		// 所有投資庫存清單
		List<CustInvestAsset> assetList = new ArrayList<CustInvestAsset>();
		
		// 指單類資產
		assetList.addAll(getTRUST_ASSETS(custID, isObu, ex_map));
		
		// DCI類資產
		assetList.addAll(getDCI_ASSETS(custID, isObu, ex_map));
		
		// 證券類資產
		List<Map<String, Object>> list = getSecCustData(inputVO);
		if (list.size() > 0) {
			assetList.addAll(getSEC_ASSETS(custID, isObu, ex_map));
		} 
		
		outputVO.setSecCustCrossSelling(list.size() > 0 ? "Y" : "N");
		
		// return
		outputVO.setAssetList(assetList);

		BigDecimal investment = new BigDecimal(0);
		for (CustInvestAsset tmpVO : assetList) {
			investment = investment.add(tmpVO.getTOTAL_SUM_TWD());
		}
		outputVO.setInvestment(investment);

		this.sendRtnObject(outputVO);
	}

	public void getFu(Object body, IPrimitiveMap header) throws Exception {
		CRM681InputVO inputVO = (CRM681InputVO) body;
		CRM681OutputVO outputVO = getFu(inputVO);
		sendRtnObject(outputVO);
	}
	
	public CRM681OutputVO getFu(CRM681InputVO inputVO) throws Exception {
		CRM681OutputVO return_VO = new CRM681OutputVO();
		List<CBSUtilOutputVO> acctData = inputVO.getAcctData();
		BigDecimal fuTotal = BigDecimal.ZERO; //貸款總金額

		//發送電文
		List<SC120100OutputVO> vos = (acctData == null ? sc120100Service.searchAll(inputVO.getCust_id().trim()) : sc120100Service.searchAllByCustAcctList(acctData));

		List<SC120100DetailOutputVO> results = new ArrayList<>();
		List<SC120100DetailOutputVO> results2 = new ArrayList<>();
		List<SC120100DetailOutputVO> results3 = new ArrayList<>();
		List<SC120100DetailOutputVO> results4 = new ArrayList<>();
		List<SC120100DetailOutputVO> results5 = new ArrayList<>();
		List<SC120100DetailOutputVO> results6 = new ArrayList<>();
		List<SC120100DetailOutputVO> results7 = new ArrayList<>();
		List<SC120100DetailOutputVO> results8 = new ArrayList<>();
		//個人週轉金性質別判斷
		//20200303_CBS_#79018_分行業管系統_就貸戶的資產負債總覽多了個人週轉金貸款
		//尚有AH21，AR01~AR04，AR00，RA06，RA08 但因為是4碼先不加上去
		String[] naturePersonTypeChekList = { "482", "446", "463", "469", "CN1", "CN2", "C17", "C28" };
		//企業貸款判斷
		String[] corporationTypeChekList = { "BP1", "TD1", "T01", "IF2", "IF1", "CB1", "EF1", "EF2", "GP1" };

		for (SC120100OutputVO sc120100OutputVO : vos) {
			//			System.out.println(sc120100OutputVO);
			List<SC120100DetailOutputVO> details = sc120100OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<SC120100DetailOutputVO>() : details;

			for (SC120100DetailOutputVO data : details) {

				//2018.09.12 改用新邏輯判斷	

				//分期型房貸
				if (cbsservice.isInstallment(data.getWA_X_ATYPE())) {
					//					String strRATE = new EsbUtil().decimalPoint(data.getRATE(), 4).toString();
					//					data.setRATE(strRATE.substring(0,strRATE.length()-2));
					data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
					//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
					//					data.setINS_AMT(new EsbUtil().decimalPoint(data.getINS_AMT(), 2).toString());
					results.add(data);
					
					fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
				}
				//循環性貸款
				// 綜定質借短擔=5301
				else {
					String acctType = defaultString(trim(data.getWA_X_ATYPE()));

					if (cbsservice.isCreditLoan1(acctType)) {
						//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
						//					data.setINS_AMT(new EsbUtil().decimalPoint(data.getINS_AMT(), 2).toString());
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						results2.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//循環型房貸(額度式)
					else if (cbsservice.isHomeLoan(acctType)) {
						//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
						//					data.setINS_AMT(new EsbUtil().decimalPoint(data.getINS_AMT(), 2).toString());
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						results2.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//綜存質借
					else if (cbsservice.isCreditLoan(acctType)) {
						//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
						//					data.setINS_AMT(new EsbUtil().decimalPoint(data.getINS_AMT(), 2).toString());
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						results2.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//信貸
					else if (cbsservice.isCredit(acctType)) {
						//					String strRATE = new EsbUtil().decimalPoint(data.getRATE(), 4).toString();
						//					data.setRATE(strRATE.substring(0,strRATE.length()-2));
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
						//					data.setINS_AMT(new EsbUtil().decimalPoint(data.getINS_AMT(), 2).toString());
						results3.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//就學貸款
					else if (cbsservice.isStudentLoan(acctType) && cbsservice.fitYearTermPattern(data.getDOC_NO())) {
						//					String strRATE = new EsbUtil().decimalPoint(data.getRATE(), 4).toString();
						//					data.setRATE(strRATE.substring(0,strRATE.length()-2));
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
						//					data.setINS_AMT(new EsbUtil().decimalPoint(data.getINS_AMT(), 2).toString());
						results4.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//留學貸款
					else if (cbsservice.isStudentForeignLoan(acctType)) {
						//					String strRATE = new EsbUtil().decimalPoint(data.getRATE(), 4).toString();
						//					data.setRATE(strRATE.substring(0,strRATE.length()-2));
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
						//					data.setINS_AMT(new EsbUtil().decimalPoint(data.getINS_AMT(), 2).toString());
						results5.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//存單質借
					else if (cbsservice.isMortgage1(acctType)) {
						//					String strRATE = new EsbUtil().decimalPoint(data.getRATE(), 4).toString();
						//					data.setRATE(strRATE.substring(0,strRATE.length()-2));
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						//					data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
						results6.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//信託質借
					//                    else if (cbsservice.isMortgage2(acctType)) {
					//                        String strRATE = new EsbUtil().decimalPoint(data.getRATE(), 4).toString();
					//                        data.setRATE(strRATE.substring(0,strRATE.length()-2));
					//                        data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
					//                        data.setORI_LOAN_BAL(new EsbUtil().decimalPoint(data.getORI_LOAN_BAL(), 2).toString());
					//                        results6.add(data);
					//                    }
					//個人週轉金
					else if (inputVO.getCust_id().trim().length() == 10 & Arrays.asList(naturePersonTypeChekList).contains(data.getWA_LN_NATURE_TYPE())) {
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						results7.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
					//企業貸款
					else if (inputVO.getCust_id().trim().length() == 8 & !Arrays.asList(corporationTypeChekList).contains(data.getWA_LN_NATURE_TYPE()) & StringUtils.isNotBlank(data.getWA_LN_NATURE_TYPE())) {
						data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL()));
						results8.add(data);
						
						fuTotal = fuTotal.add(new BigDecimal(cbsservice.amountFormat(data.getACT_BAL())));
					}
				}
			}
		}

		//分期型房貸
		return_VO.setResultList(results);
		//循環性貸款
		return_VO.setResultList2(results2);
		//信貸
		return_VO.setResultList3(results3);
		//就學貸款
		return_VO.setResultList4(results4);
		//留學貸款
		return_VO.setResultList5(results5);
		//存單質借
		return_VO.setResultList6(results6);
		//個人週轉金
		return_VO.setResultList7(results7);
		//企業貸款
		return_VO.setResultList8(results8);
		//貸款總金額
		return_VO.setFuTotal(fuTotal);
		
		return return_VO;
	}

	public void fortuna(Object body, IPrimitiveMap header) throws Exception {
		CRM681InputVO inputVO = (CRM681InputVO) body;
		CRM681OutputVO return_VO = new CRM681OutputVO();
		NumberFormat nf = NumberFormat.getInstance();
		dam = this.getDataAccessManager();

		List<Map<String, Object>> ans_list = new ArrayList<Map<String, Object>>();
		// 判斷全部有理專客戶（有AO CODE）
		TBCRM_CUST_MASTVO vo = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCust_id());
		// 5358, 與欣怡確認，CAM210中還是要開客戶首頁，只是開啟客戶首頁後，不要有錯誤訊息出現
		if (vo != null && StringUtils.isNotBlank(vo.getAO_CODE())) {
			// ---------------------------------非特定---------------------------------
			// 預期貸款支出--> 每次要先打電文
			FPS200 fps200 = (FPS200) PlatformContext.getBean("fps200");
			TBFPS_CUST_LISTVO list_vo = (TBFPS_CUST_LISTVO) dam.findByPKey(TBFPS_CUST_LISTVO.TABLE_UID, inputVO.getCust_id());
			if (list_vo != null) {
				fps200.doCustLoan(dam, inputVO.getCust_id(), list_vo);
				fps200.doCustIns(dam, inputVO.getCust_id(), list_vo);
				dam.update(list_vo);
			}
			// 檢查客戶帳上金額:300, 可供規劃金額門檻:50
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("select de.PLAN_AMT_2, de.AVAILABLE_AMT from tbfps_other_para_head he ");
			sql.append("inner join tbfps_other_para de on he.param_no = de.param_no ");
			sql.append("where he.status = 'A' ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> OTHER_PARA_LIST = dam.exeQuery(queryCondition);
			BigDecimal PLAN_AMT_2 = OTHER_PARA_LIST.size() > 0 ? new BigDecimal(OTHER_PARA_LIST.get(0).get("PLAN_AMT_2").toString()).multiply(new BigDecimal(10000)) : new BigDecimal(3000000);
			BigDecimal AVAILABLE_AMT = OTHER_PARA_LIST.size() > 0 ? new BigDecimal(OTHER_PARA_LIST.get(0).get("AVAILABLE_AMT").toString()).multiply(new BigDecimal(10000)) : new BigDecimal(500000);
			// TBFPS_CUST_LIST
			if (list_vo != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				// 客戶帳上金額【存款 – 預期保費支出 – 預期貸款支出】
				BigDecimal curr = (list_vo.getDEPOSIT_AMT() == null ? new BigDecimal(0) : list_vo.getDEPOSIT_AMT()).subtract(list_vo.getINS_YEAR_AMT_1() == null ? new BigDecimal(0) : list_vo.getINS_YEAR_AMT_1()).subtract(list_vo.getINS_YEAR_AMT_2() == null ? new BigDecimal(0) : list_vo.getINS_YEAR_AMT_2()).subtract(list_vo.getLN_YEAR_AMT_1() == null ? new BigDecimal(0) : list_vo.getLN_YEAR_AMT_1()).subtract(list_vo.getLN_YEAR_AMT_2() == null ? new BigDecimal(0) : list_vo.getLN_YEAR_AMT_2()).subtract(list_vo.getLN_YEAR_AMT_3() == null ? new BigDecimal(0) : list_vo.getLN_YEAR_AMT_3());
				// 大於等於300萬(300萬為參數TBFPS_OTHER_PARA.PLAN_AMT_2)
				if (curr.compareTo(PLAN_AMT_2) >= 0) {
					map.put("TYPE", "MONEY_BIG");
					map.put("MONEY", nf.format(curr.divide(new BigDecimal(10000), 0, RoundingMode.DOWN)));
					ans_list.add(map);
				}
				// 小於300萬(300萬為參數TBFPS_OTHER_PARA.PLAN_AMT_2)，且大於可供規劃金額門檻(TBFPS_OTHER_PARA.AVAILABLE_AMT)
				else if (curr.compareTo(PLAN_AMT_2) < 0 && curr.compareTo(AVAILABLE_AMT) > 0) {
					map.put("TYPE", "MONEY_LOW");
					ans_list.add(map);
				}
			}
			// 若於理財規劃書中「執行交易」欄位點選任一筆交易〝GO 〞並完成下單(由交易明細判斷)，
			// 其他列示於「執行交易」欄位的商品標的，如未完成下單且系統日期距該規劃資料建立日期未超過兩周，則顯示〝●客戶有待執行交易GO〞。
			// 若系統判斷同時有特定目的及非特定目的待執行交易時，呈現方式為：〝●客戶有待執行交易，特定目的GO，非特定目的GO。〞
			// 只要TOT_ORDER > 0 && TOT_ORDER < TOT就是有非特定目的待執行交易，導向FPS200，要傳客戶ID, PLAN_ID
			Boolean spec = false;
			Boolean nonspec = false;
			String spec_plan = null;
			String nonspec_plan = null;
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select HE.PLAN_ID, SUM(NVL(DE.PURCHASE_ORG_AMT, 0)) AS TOT, SUM(NVL(DE.PURCHASE_ORG_AMT_ORDER, 0)) AS TOT_ORDER from TBFPS_PORTFOLIO_PLAN_INV_HEAD HE ");
			sql.append("inner join TBFPS_PORTFOLIO_PLAN_INV DE on DE.PLAN_ID = HE.PLAN_ID and DE.PRD_TYPE is not null ");
			sql.append("where NVL(HE.VALID_FLAG, 'Y') = 'Y' ");
			sql.append("and HE.cust_id = :cust_id ");
			sql.append("group by HE.PLAN_ID ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			for (Map<String, Object> map : list) {
				BigDecimal TOT = new BigDecimal(ObjectUtils.toString(map.get("TOT")));
				BigDecimal TOT_ORDER = new BigDecimal(ObjectUtils.toString(map.get("TOT_ORDER")));
				if (TOT_ORDER.compareTo(new BigDecimal(0)) > 0 && TOT.compareTo(TOT_ORDER) > 0) {
					nonspec = true;
					nonspec_plan = ObjectUtils.toString(map.get("PLAN_ID"));
					break;
				}
			}
			// 只要TOT_ORDER > 0 && TOT_ORDER < TOT就是有特定目的待執行交易，導向FPS300，要傳客戶ID, PLAN_ID
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select HE.PLAN_ID, SUM(NVL(DE.PURCHASE_ORG_AMT, 0)) AS TOT, SUM(NVL(DE.PURCHASE_ORG_AMT_ORDER, 0)) AS TOT_ORDER from TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ");
			sql.append("inner join TBFPS_PORTFOLIO_PLAN_SPP DE on DE.PLAN_ID = HE.PLAN_ID ");
			sql.append("where NVL(HE.VALID_FLAG, 'Y') = 'Y' ");
			sql.append("and HE.cust_id = :cust_id ");
			sql.append("group by HE.PLAN_ID ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			for (Map<String, Object> map2 : list2) {
				BigDecimal TOT = new BigDecimal(ObjectUtils.toString(map2.get("TOT")));
				BigDecimal TOT_ORDER = new BigDecimal(ObjectUtils.toString(map2.get("TOT_ORDER")));
				if (TOT_ORDER.compareTo(new BigDecimal(0)) > 0 && TOT.compareTo(TOT_ORDER) > 0) {
					spec = true;
					spec_plan = ObjectUtils.toString(map2.get("PLAN_ID"));
					break;
				}
			}
			// msg
			if (spec || nonspec) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (spec && nonspec) {
					map.put("TYPE", "BOTH_SPEC");
					map.put("PLAN_ID_1", spec_plan);
					map.put("PLAN_ID_2", nonspec_plan);
					ans_list.add(map);
				} else if (spec) {
					map.put("TYPE", "SPEC");
					map.put("PLAN_ID_1", spec_plan);
					ans_list.add(map);
				} else {
					map.put("TYPE", "NON_SPEC");
					map.put("PLAN_ID_2", nonspec_plan);
					ans_list.add(map);
				}
			}
			// ---------------------------------特定---------------------------------
			// 客戶依Lifestage有無抓到〝目的〞決定該客戶財神爺提示訊息
			// 撈該客戶的年齡, vo.getAGE()
			// 撈該客戶有無未成年子女SQL
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("WITH BASE AS ( ");
			sql.append("select * from TBCRM_CUST_REL ");
			sql.append("where cust_id_m = :cust_id ");
			sql.append("and REL_TYPE in ('03','04','05','06') ");
			sql.append(") ");
			sql.append("select count(1) NUM from tbcrm_cust_mast MA ");
			sql.append("inner join BASE on BASE.CUST_ID_S = MA.CUST_ID ");
			sql.append("where MA.AGE < 18 ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			String child_yn = new BigDecimal(ObjectUtils.toString(list3.get(0).get("NUM"))).compareTo(new BigDecimal(0)) > 0 ? "Y" : "N";
			// 撈Lifestage且符合該客戶的年齡、未成年子女
			// TBFPS_CUST_LIST, sa:房貸 = LN_YEAR_AMT_1 > 0
			String house_yn = list_vo != null ? list_vo.getLN_YEAR_AMT_1() != null ? list_vo.getLN_YEAR_AMT_1().compareTo(new BigDecimal(0)) > 0 ? "Y" : "N" : "N" : "N";
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select DE.* from TBFPS_SPP_RECOMMAND_HEAD HE ");
			sql.append("inner join TBFPS_SPP_RECOMMAND DE on de.param_no = he.param_no ");
			sql.append("where HE.STATUS = 'A' ");
			sql.append("and (DE.AGE_START <= :age and DE.AGE_END > :age) ");
			sql.append("and DE.CHILD_YN = :child_yn ");
			sql.append("and DE.LN_HOUSE_YN = :house_yn ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("age", vo.getAGE());
			queryCondition.setObject("child_yn", child_yn);
			queryCondition.setObject("house_yn", house_yn);
			List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
			if (list4.size() > 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("TYPE", "STATUS_GO");

				Map<String, Object> list4Map = list4.get(0);
				// 移除, 特定目的規劃歷史規劃中已有該項
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("select DISTINCT SPP_TYPE from TBFPS_PORTFOLIO_PLAN_SPP_HEAD where NVL(VALID_FLAG, 'Y') = 'Y' AND CUST_ID = :cust_id");
				queryCondition.setObject("cust_id", inputVO.getCust_id());
				List<Map<String, Object>> re_list = dam.exeQuery(queryCondition);
				// sa:先寫死吧
				for (Map<String, Object> re_map : re_list) {
					if ("EDUCATION".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_EDUCATION_YN", "N");
					else if ("RETIRE".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_RETIRE_YN", "N");
					else if ("BUY_HOUSE".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_BUYHOUSE_YN", "N");
					else if ("BUY_CAR".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_BUYCAR_YN", "N");
					else if ("MARRY".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_MARRY_YN", "N");
					else if ("OV_EDU".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_OVERSEA_EDUCATION_YN", "N");
					else if ("TRAVEL".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_TRAVEL_YN", "N");
					else if ("OTHER".equals(re_map.get("SPP_TYPE")))
						list4Map.put("FP_OTHER_YN", "N");
				}
				map.put("MAP_DATA", list4Map);
				ans_list.add(map);
			}
			// 若客戶有做過任一有效特定目的規劃(歷史規劃有資料)，則顯示以下財神爺提示訊息〝●快速進行特定目的理財規劃之績效追蹤或調整。GO 〞
			// 若以下SQL > 0，則導向FPS400
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select count(*) NUM from TBFPS_SPP_PRD_RETURN_HEAD HE ");
			sql.append("inner join TBFPS_SPP_PRD_RETURN DE on DE.PLAN_ID = HE.PLAN_ID ");
			sql.append("where NVL(HE.STATUS, ' ') not in ('C', 'D') ");
			sql.append("and HE.cust_id = :cust_id ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			List<Map<String, Object>> list5 = dam.exeQuery(queryCondition);
			if (list5.size() > 0) {
				if (new BigDecimal(ObjectUtils.toString(list5.get(0).get("NUM"))).compareTo(new BigDecimal(0)) > 0) {
					Map<String, Object> map5 = new HashMap<String, Object>();
					map5.put("TYPE", "SPP_GO");
					ans_list.add(map5);
				}
			} else {
				// 若以下SQL > 0，則導向FPS300
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("select count(*) NUM from TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ");
				sql.append("inner join TBFPS_PORTFOLIO_PLAN_SPP DE on DE.PLAN_ID = HE.PLAN_ID ");
				sql.append("where HE.cust_id = :cust_id ");
				sql.append("and VALID_FLAG <> 'Y' ");
				queryCondition.setQueryString(sql.toString());
				queryCondition.setObject("cust_id", inputVO.getCust_id());
				List<Map<String, Object>> list6 = dam.exeQuery(queryCondition);
				if (list6.size() > 0) {
					if (new BigDecimal(ObjectUtils.toString(list6.get(0).get("NUM"))).compareTo(new BigDecimal(0)) > 0) {
						Map<String, Object> map6 = new HashMap<String, Object>();
						map6.put("TYPE", "PORT_GO");
						ans_list.add(map6);
					}
				}
			}
		}

		return_VO.setResultList(ans_list);
		this.sendRtnObject(return_VO);
	}

	/****************** 處理第一段-基債股 ******************/
	public List<CustInvestAsset> getETF_ASSETS (String custID, Boolean isObu, HashMap<String, BigDecimal> ex_map) throws Exception {
		
		List<CustInvestAsset> assetList = new ArrayList<CustInvestAsset>();
		
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_ASSETS);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		//body
		NFVIPAInputVO txBodyVO = new NFVIPAInputVO();
		esbUtilInputVO.setNfvipaInputVO(txBodyVO);
		txBodyVO.setFUNCTION("01");
		txBodyVO.setCUSID(custID);
		txBodyVO.setUNIT((isObu != null && isObu) ? "O" : "D");

		//發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);

		for (ESBUtilOutputVO vo : esbUtilOutputVO) {
			NFVIPAOutputVO nfvipaOutputVO = vo.getNfvipaOutputVO();
			List<NFVIPAOutputVODetails> details = nfvipaOutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFVIPAOutputVODetails>() : details;
			for (NFVIPAOutputVODetails detail : details) {
				String strTotalSum = StringUtils.substring(detail.getTOTAL_SUM(), 1); //移除第一碼正負號
				BigDecimal totalSum = new EsbUtil().decimalPoint(strTotalSum, 2);
				totalSum = "-".equals(StringUtils.left(detail.getTOTAL_SUM(), 1)) ? totalSum.multiply(new BigDecimal(-1)) : totalSum;
				//detail庫存資料轉換至CustAssetETFVO中
				CustInvestAsset custInvestAsset = new CustInvestAsset();
				custInvestAsset.setBUSINESS_CODE(detail.getBUSINESS_CODE());
				custInvestAsset.setCURRENCY(detail.getCURRENCY());
				custInvestAsset.setTOTAL_SUM(totalSum);
				custInvestAsset.setTOTAL_SUM_TWD(totalSum.multiply(ex_map.get(detail.getCURRENCY())).setScale(2, BigDecimal.ROUND_HALF_UP));

				assetList.add(custInvestAsset);
			}
		}
		
		return assetList;
	}
	
	/****************** 處理第二段-指單類資產 ******************/
	public List<CustInvestAsset> getTRUST_ASSETS (String custID, Boolean isObu, HashMap<String, BigDecimal> ex_map) throws Exception {
		
		List<CustInvestAsset> assetList = new ArrayList<CustInvestAsset>();
		
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, TRUST_ASSETS);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		//body
		NMVIPAInputVO nmvipaVO = new NMVIPAInputVO();
		esbUtilInputVO.setNmvipaInputVO(nmvipaVO);
		nmvipaVO.setFUNCTION("TT");
		nmvipaVO.setCUSID(custID);

		//發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);

		for (ESBUtilOutputVO vo : esbUtilOutputVO) {
			NMVIPAOutputVO nmvipaOutputVO = vo.getNmvipaOutputVO();
			List<NMVIPAOutputDetailsVO> details = nmvipaOutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NMVIPAOutputDetailsVO>() : details;
			for (NMVIPAOutputDetailsVO detail : details) {
				BigDecimal arr04 = new EsbUtil().decimalPoint(detail.getARR04(), 2);
				//detail庫存資料轉換至CustAssetETFVO中
				CustInvestAsset custInvestAsset = new CustInvestAsset();
				custInvestAsset.setBUSINESS_CODE(detail.getARR01());
				custInvestAsset.setCURRENCY(detail.getARR02());
				custInvestAsset.setTOTAL_SUM(arr04);
				custInvestAsset.setTOTAL_SUM_TWD(arr04.multiply(ex_map.get(detail.getARR02())).setScale(2, BigDecimal.ROUND_HALF_UP));

				assetList.add(custInvestAsset);
			}
		}

		return assetList;
	}
	
	/****************** 處理第三段-SI類資產 ******************/
	public List<CustInvestAsset> getSI_ASSETS (String custID, Boolean isObu, HashMap<String, BigDecimal> ex_map) throws Exception {
		
		List<CustInvestAsset> assetList = new ArrayList<CustInvestAsset>();
		
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, SI_ASSETS);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		//body
		SDPRC09AInputVO saprc09aInputVO = new SDPRC09AInputVO();
		esbUtilInputVO.setSaprc09aInputVO(saprc09aInputVO);
		saprc09aInputVO.setID(custID);

		//發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);

		//原寫法:直接抓取電文欄位
		for (ESBUtilOutputVO vo : esbUtilOutputVO) {
			SDPRC09AOutputVO sdprc09aOutputVO = vo.getSdprc09aOutputVO();
			List<SDPRC09AOutputDetailVO> details = sdprc09aOutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<SDPRC09AOutputDetailVO>() : details;
			for (SDPRC09AOutputDetailVO detail : details) {
				BigDecimal ivAmt2 = new EsbUtil().decimalPoint(detail.getIVAMT2(), 2);
				//detail庫存資料轉換至CustAssetETFVO中
				CustInvestAsset custInvestAsset = new CustInvestAsset();
				custInvestAsset.setBUSINESS_CODE(detail.getBUSINESS_CD());
				custInvestAsset.setCURRENCY(detail.getCURRENCY());
				custInvestAsset.setTOTAL_SUM(ivAmt2);
				custInvestAsset.setTOTAL_SUM_TWD(ivAmt2.multiply(ex_map.get(detail.getCURRENCY())).setScale(2, BigDecimal.ROUND_HALF_UP));

				assetList.add(custInvestAsset);
			}
		}
		
		return assetList;
	}
	
	/****************** 處理第四段-DCI類資產 ******************/
	public List<CustInvestAsset> getDCI_ASSETS (String custID, Boolean isObu, HashMap<String, BigDecimal> ex_map) throws Exception {
		
		List<CustInvestAsset> assetList = new ArrayList<CustInvestAsset>();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT VALU_CRCY_TYPE CURRENCY, ");
		sql.append("       SUM(TXN_AMT) AS DCDAMOUNT  ");
		sql.append("FROM TBCRM_AST_INV_DCI_OTH ");
		sql.append("WHERE CUST_ID = :cust_id ");
		sql.append("GROUP BY VALU_CRCY_TYPE ");

		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", custID);
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (!list.isEmpty()) {
			for (Map<String, Object> map : list) {
				//detail庫存資料轉換至CustAssetETFVO中
				CustInvestAsset custInvestAsset = new CustInvestAsset();
				custInvestAsset.setBUSINESS_CODE("SD");
				custInvestAsset.setCURRENCY((String) map.get("CURRENCY"));
				BigDecimal total_sum = (BigDecimal) map.get("DCDAMOUNT");
				
				custInvestAsset.setTOTAL_SUM(total_sum);
				if (total_sum != null) {
					custInvestAsset.setTOTAL_SUM_TWD(total_sum.multiply(ex_map.get((String) map.get("CURRENCY"))).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				
				assetList.add(custInvestAsset);
			}
		}
		
		return assetList;
	}

	/****************** 處理第五段-證券類資產 ******************/
	public List<CustInvestAsset> getSEC_ASSETS (String custID, Boolean isObu, HashMap<String, BigDecimal> ex_map) throws Exception {
		
		List<CustInvestAsset> assetList = new ArrayList<CustInvestAsset>();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT PRODUCT_CODE, ");
		sql.append("       CUST_ID, ");
		sql.append("       CURRENCY, ");
		sql.append("       SUM(INVEST_AMT_FC) OVER (PARTITION BY CUST_ID, PRODUCT_CODE, CURRENCY) AS TOTAL_INVEST_AMT_FC, ");
		sql.append("       SUM(AUM_FC) OVER (PARTITION BY CUST_ID, PRODUCT_CODE, CURRENCY) AS TOTAL_AUM_FC, ");
		sql.append("       SUM(BENEFIT_AMT1) OVER (PARTITION BY CUST_ID, PRODUCT_CODE, CURRENCY) AS TOTAL_BENEFIT_AMT1, ");
		sql.append("       SUM(BENEFIT_AMT2) OVER (PARTITION BY CUST_ID, PRODUCT_CODE, CURRENCY) AS TOTAL_BENEFIT_AMT2 ");
		sql.append("FROM ( ");
		sql.append("  SELECT DATA_DATE, SNAP_DATE, BRANCH_NAME, CUST_ID, ACCT_NBR, PRODUCT_CODE, COUNTRY_CODE, EXCHANGE_CODE, UPPER_COMPANY_CODE, PROD_ID, CURRENCY, INVEST_AMT_FC, AUM_FC, BENEFIT_AMT1, BENEFIT_AMT2 ");
		sql.append("  FROM TBCRM_AST_INV_SEC_STOCK ");
		sql.append("  WHERE DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_STOCK) ");
		sql.append("  UNION ");
		sql.append("  SELECT DATA_DATE, SNAP_DATE, BRANCH_NAME, CUST_ID, ACCT_NBR, PRODUCT_CODE, COUNTRY_CODE, EXCHANGE_CODE, UPPER_COMPANY_CODE, PROD_ID, CURRENCY, INVEST_AMT_FC, AUM_FC, BENEFIT_AMT1, BENEFIT_AMT2 ");
		sql.append("  FROM TBCRM_AST_INV_SEC_BOND ");
		sql.append("  WHERE DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_BOND) ");
		sql.append("  UNION ");
		sql.append("  SELECT DATA_DATE, SNAP_DATE, BRANCH_NAME, CUST_ID, ACCT_NBR, PRODUCT_CODE, COUNTRY_CODE, EXCHANGE_CODE, UPPER_COMPANY_CODE, PROD_ID, CURRENCY, INVEST_AMT_FC, AUM_FC, BENEFIT_AMT1, BENEFIT_AMT2 ");
		sql.append("  FROM TBCRM_AST_INV_SEC_SN ");
		sql.append("  WHERE DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_SN) ");
		sql.append("  UNION ");
		
		// #1345 : WMS-CR-20220930-01_業管系統新增證券之境內結構型商品資訊
		sql.append("  SELECT INS_DATE AS DATA_DATE, ");  								//-- 資料日期
		sql.append("         SNAP_DATE, ");  											//-- 庫存日期
		sql.append("         NULL AS BRANCH_NAME, ");  									//-- 分公司名稱
		sql.append("         CUSTOMER_ID AS CUST_ID, ");  								//-- 客戶ID(PER)(ID)
		sql.append("         ACCT_NBR, ");  											//-- 客戶帳號(PER)(ACCT)
		sql.append("         'DSN' AS PRODUCT_CODE, ");  								//-- 產品類別代碼
		sql.append("         NULL AS COUNTRY_CODE, ");  								//-- 國別代號
		sql.append("         NULL AS EXCHANGE_CODE, ");  								//-- 交易所代號
		sql.append("         NULL AS UPPER_COMPANY_CODE, ");  							//-- 上手券商代號
		sql.append("         STOCK_CODE AS PROD_ID, ");  								//-- 商品代號
		sql.append("         CURRENCY, ");  											//-- 幣別
		sql.append("         INVEST_COST AS INVEST_AMT_FC, ");  						//-- 投資成本(原幣)
		sql.append("         AUM_TW AS AUM_FC, ");  									//-- 庫存市值(原幣) / 帳面價值
		sql.append("         (AUM_TW - INVEST_COST) AS BENEFIT_AMT1, ");  				//-- 損益金額(不含息)(原幣) = 帳面價值-投資成本
		sql.append("         (AUM_TW - INVEST_COST + DIVIDEND_AMT) AS BENEFIT_AMT2 ");  //-- 損益金額(含息)(原幣) = 帳面價值-投資成本+累計配息金額
		sql.append("  FROM TBCRM_AST_INV_SEC_DSN ");
		sql.append("  WHERE INS_DATE = (SELECT MAX(INS_DATE) FROM TBCRM_AST_INV_SEC_DSN) ");
		sql.append(") ");
		
		sql.append("WHERE 1 = 1 ");
		sql.append("AND CUST_ID = :cust_id ");
		
		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", custID);
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (!list.isEmpty()) {
			for (Map<String, Object> map : list) {
				CustInvestAsset custInvestAsset = new CustInvestAsset();
				custInvestAsset.setBUSINESS_CODE((String) map.get("PRODUCT_CODE"));
				custInvestAsset.setCURRENCY((String) map.get("CURRENCY"));
				
				// 總餘額(市值)
				BigDecimal totalSUM = (BigDecimal) map.get("TOTAL_AUM_FC");
				custInvestAsset.setTOTAL_SUM(totalSUM);
				if (totalSUM != null) {
					custInvestAsset.setTOTAL_SUM_TWD(totalSUM.multiply(ex_map.get((String) map.get("CURRENCY"))).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				
				// 投資成本(市值)
				BigDecimal totalInvestSUM = (BigDecimal) map.get("TOTAL_INVEST_AMT_FC");
				custInvestAsset.setTOTAL_INVEST(totalInvestSUM);
				if (totalInvestSUM != null) {
					custInvestAsset.setTOTAL_INVEST_TWD(totalInvestSUM.multiply(ex_map.get((String) map.get("CURRENCY"))).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				
				// 損益金額(不含息)
				BigDecimal totalBenefitAMT1SUM = (BigDecimal) map.get("TOTAL_BENEFIT_AMT1");
				custInvestAsset.setTOTAL_INVEST(totalBenefitAMT1SUM);
				if (totalBenefitAMT1SUM != null) {
					custInvestAsset.setTOTAL_BENEFIT_AMT1_TWD(totalBenefitAMT1SUM.multiply(ex_map.get((String) map.get("CURRENCY"))).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				
				// 損益金額(含息)
				BigDecimal totalBenefitAMT2SUM = (BigDecimal) map.get("TOTAL_BENEFIT_AMT2");
				custInvestAsset.setTOTAL_INVEST(totalBenefitAMT2SUM);
				if (totalBenefitAMT2SUM != null) {
					custInvestAsset.setTOTAL_BENEFIT_AMT2_TWD(totalBenefitAMT2SUM.multiply(ex_map.get((String) map.get("CURRENCY"))).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				
				assetList.add(custInvestAsset);
			}
		}
		
		return assetList;
	}
		
	public void getSecCustData (Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM681OutputVO outputVO = new CRM681OutputVO();
		
		outputVO.setSecCustData(this.getSecCustData(body));
		outputVO.setSecCustDataDSN(this.getSecCustDataDSN(body));
		
		sendRtnObject(outputVO);
	}
	
	/***** 證券客戶資料 *****/
	public List<Map<String, Object>> getSecCustData (Object body) throws JBranchException {
			
		CRM681InputVO inputVO = (CRM681InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT CUST_ID, ");
		sql.append("       SNAP_DATE, ");
		sql.append("       ACCT_NBR, ");
		sql.append("       RISK_LEVEL_DESC AS RISK_LEVEL, ");
		sql.append("       RISK_UPD_DATE, ");
		sql.append("       PI_IND, ");
		sql.append("       PI_EXPIRY_DATE, ");
		sql.append("       TO_BANK_ACCT_NBR, ");
		sql.append("       FO_BANK_ACCT_NBR, ");
		sql.append("       BANK_ACCT_NBR, ");
		sql.append("       CROSS_SELLING_IND, ");
		sql.append("       ACCT_NBR AS LABEL, ");
		sql.append("       ACCT_NBR AS DATA ");
		sql.append("FROM TBCRM_S_TFB_SVIP_ACCT ");
		sql.append("WHERE SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_S_TFB_SVIP_ACCT) ");
		sql.append("AND CROSS_SELLING_IND = 'Y' ");
		sql.append("AND CUST_ID = :cust_id ");

		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		return dam.exeQuery(queryCondition);
	}
	
	/***** 證券客戶資料(DSN) *****/
	public List<Map<String, Object>> getSecCustDataDSN (Object body) throws JBranchException {
			
		CRM681InputVO inputVO = (CRM681InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT CUSTOMER_ID AS CUST_ID, ");
		sql.append("       SNAP_DATE, ");
		sql.append("       ACCT_NBR AS ACCT_NBR_DSN, ");
		sql.append("       KYC_M_DATE, ");
		sql.append("       SALES_BRANCH_NBR, ");
		sql.append("       SALES_BRANCH_NAME, ");
		sql.append("       ACCT_NBR AS LABEL, ");
		sql.append("       ACCT_NBR AS DATA ");
		sql.append("FROM TBCRM_S_TFB_SVIP_ACCT_DSN ");
		sql.append("WHERE SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_S_TFB_SVIP_ACCT_DSN) ");
		sql.append("AND CUSTOMER_ID = :cust_id ");

		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		return dam.exeQuery(queryCondition);
	}
	
	public boolean isObu (CRM681InputVO inputVO, String custID) throws Exception {
		Boolean isObu = false;

		if (StringUtils.isNotBlank(inputVO.getObuFlag())) { // 前端傳入
			isObu = "Y".equals(inputVO.getObuFlag());
		} else { //由電文取得OBU客戶註記
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			SOT701InputVO sot701inputVO = new SOT701InputVO();
			sot701inputVO.setCustID(custID);
			FP032675DataVO fp032675DataVO = sot701.getFP032675Data(sot701inputVO);
			isObu = StringUtils.equals("Y", fp032675DataVO.getObuFlag());
		}
		
		return isObu;
	}
}
package com.systex.jbranch.app.server.fps.ins210;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.mime.content.StringBody;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBINS_PLAN_DTLVO;
import com.systex.jbranch.app.common.fps.table.TBINS_PLAN_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBINS_PLAN_SUG_INSLISTPK;
import com.systex.jbranch.app.common.fps.table.TBINS_PLAN_SUG_INSLISTVO;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.ins200.INS200;
import com.systex.jbranch.app.server.fps.ins450.INS450;
import com.systex.jbranch.app.server.fps.ins810.FubonIns810;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * INS210
 * 
 * @author Jimmy
 * @date 2017/11/14
 * @spec null
 */
 
@Component("ins210")
@Scope("request")
public class INS210 extends FubonWmsBizLogic{
	// 目前保障內容 planType 分類
	enum PlanType{
		P1("L"), P2("P"), P3("H"), P4("C,D,W");
		
		private String mappingPlanType;
		
		PlanType(String mappingPlanType){
			this.mappingPlanType = mappingPlanType;
		}
		
		public String getMappingPlanType(){
			return mappingPlanType;
		}
	}
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS210.class);
	
	@Autowired @Qualifier("ins810")
	private FubonIns810 ins810;
	
	public FubonIns810 getIns810() {
		return ins810;
	}

	public void setIns810(FubonIns810 ins810) {
		this.ins810 = ins810;
	}
	
	@Autowired @Qualifier("ins450")
	private INS450 ins450;
	
	public INS450 getINS450() {
		return ins450;
	}

	public void setINS450(INS450 ins450) {
		this.ins450 = ins450;
	}
	
	//======================== WEB 調用 ========================
	/**
	 * WEB 調用 取得 目前保障內容 & 商品建議
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		INS210InputVO inputVO = (INS210InputVO) body;
		INS210OutputVO outputVO = new INS210OutputVO();

		String loginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		
		String jobGrade = "2".equals(inputVO.getPLAN_TYPE()) ? (StringUtils.isNotEmpty(inputVO.getPROF_GRADE()) ? inputVO.getPROF_GRADE() : "1" ) : "1";

		outputVO.setPlanList(getPlanList(inputVO.getPLAN_TYPE(), inputVO.getCUST_ID(), loginBraNbr, loginAO, jobGrade));
		outputVO.setSuggestList(getSuggestProd(ObjectUtils.toString(inputVO.getPARA_NO()), inputVO.getCurrCD(),inputVO.getAGE()));
		
		this.sendRtnObject(outputVO);
	}

	/**
	 * WEB 調用 假刪除 目前保障內容 (應該可以前端做就好了...)
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void delete_temp(Object body, IPrimitiveMap header){
		INS210InputVO inputVO = (INS210InputVO) body;
		INS210OutputVO outputVO = new INS210OutputVO();

		Map removeMap = CollectionSearchUtils.findMapInColleciton(inputVO.getPlanList(), "index", (double)inputVO.getSelect_data().get("index"));
		if(removeMap != null) inputVO.getPlanList().remove(removeMap); 
		outputVO.setPlanList(inputVO.getPlanList());
		this.sendRtnObject(outputVO);
	}
	
	//取得住院日額
	private List<Map<String, Object>> getHospitalizationDays(INS210InputVO inputVO) throws JBranchException {
		dam = getDataAccessManager();
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("select H_KEYNO, DAY_AMT from TBINS_PARA_HOSPITAL ");
		sb.append("where HOSPITAL_TYPE = :hospital_type and WARD_TYPE = :ward_type ");
		sb.append("and PARA_NO = :para_no ");
		qc.setObject("hospital_type", inputVO.getHOSPITAL_TYPE());
		qc.setObject("ward_type", inputVO.getWARD_TYPE());
		qc.setObject("para_no", inputVO.getTYPE3_PARA_NO());
		qc.setQueryString(sb.toString());
		
		List<Map<String, Object>> dayAmtList = dam.exeQuery(qc);
		return dayAmtList;
	};
	
	//取得長期看護每月給付
	private List<Map<String, Object>> getLtcareMonth(INS210InputVO inputVO) throws JBranchException {
		dam = getDataAccessManager();
		List<Map<String, Object>> monthAmtList = new ArrayList<Map<String,Object>>();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select MONTH_AMT from TBINS_PARA_LTCARE ")
			.append(" where LT_KEYNO = :nurse_fee_pay ");
		
		qc.setObject("nurse_fee_pay", inputVO.getNURSE_FEE_PAY());
		qc.setQueryString(sb.toString());
		
		if(inputVO.getCareWay() != null) {
			sb.append("AND A.CARE_WAY = :careWay ");
			qc.setObject("careWay", inputVO.getCareWay());
		}
		
		if(inputVO.getCareStyle() != null) {
			sb.append("AND A.CARE_STYLE = :careStyle ");
			qc.setObject("careStyle", inputVO.getCareStyle());
		}
		
		monthAmtList = dam.exeQuery(qc);
		return monthAmtList;
	}
	
	// 2018/2/13
	public void getTempDisplayCount(Object body, IPrimitiveMap header) throws JBranchException {
		INS210InputVO inputVO = (INS210InputVO) body;
		INS210OutputVO return_VO = new INS210OutputVO();
		dam = this.getDataAccessManager();
		
		// old code有算 直接用他的
		calculaDemandAnalysis(inputVO, return_VO);
		
		this.sendRtnObject(return_VO);
	}
	
	//需求分析計算
	private void calculaDemandAnalysis(INS210InputVO inputVO,INS210OutputVO outputVO) throws JBranchException {
		BigDecimal LINA = new BigDecimal(0);
		
		if("1".equals(inputVO.getPLAN_TYPE()) || "2".equals(inputVO.getPLAN_TYPE())){//壽險及意外險需求分析
			CACULATION_THOUSAND caculation = new CACULATION_THOUSAND();
			caculation.CaculateMultiplyThousand(inputVO);
			
			LINA = caculation.getLIFE_EXPENSE_AMT().add(caculation.getLOAN_AMT()).add(caculation.getEDU_FEE()).subtract(caculation.getPREPARE_AMT());//被保人壽險需求
			
			if(caculation.getCAREFEES().compareTo(new BigDecimal(0)) >0 && "2".equals(inputVO.getPLAN_TYPE())){
				BigDecimal ACF = caculation.getCAREFEES().multiply(new BigDecimal(12)).multiply(new BigDecimal(inputVO.getSEARCH_WORK().replaceAll(",", "")));//每年看護費
				LINA = LINA.add(ACF);
			}
			
			outputVO.setSHD_PROTECT1(comparisonGap(LINA));
		}else if("3".equals(inputVO.getPLAN_TYPE())){//醫療險
			List<Map<String, Object>> hospitalList = getHospitalizationDays(inputVO);
			if(hospitalList.size() > 0) {
				outputVO.setSICKROOM_FEE(String.valueOf(hospitalList.get(0).get("H_KEYNO")));
				LINA = new BigDecimal(String.valueOf(hospitalList.get(0).get("DAY_AMT")));
			}
			
			outputVO.setSHD_PROTECT2(comparisonGap(LINA));
		}else if("4".equals(inputVO.getPLAN_TYPE())){
			//癌症住院醫療日額
			if(inputVO.getTYPE_CANCER()){
				List<Map<String, Object>> hospitalList = getHospitalizationDays(inputVO);
				if(hospitalList.size() > 0) {
					outputVO.setSICKROOM_FEE(String.valueOf(hospitalList.get(0).get("H_KEYNO")));
					outputVO.setHOSPIHALDAYS(new BigDecimal(String.valueOf(hospitalList.get(0).get("DAY_AMT"))));
				}
			}
			
			//長期看護
			if(inputVO.getTYPE_LT() && inputVO.getNURSE_FEE_PAY() != null) {
				List<Map<String, Object>> LtcareMonthList = getLtcareMonth(inputVO);
				if(LtcareMonthList.size() > 0)
					outputVO.setMONTH_AMT(new BigDecimal(String.valueOf(LtcareMonthList.get(0).get("MONTH_AMT"))));
			}else if(inputVO.getCareWay() != null && inputVO.getCareStyle() != null && inputVO.getPARA_NO() != null){
				List<Map<String, Object>> LtcareMonthList = getLtcareMonth(inputVO);
				if(LtcareMonthList.size() > 0){
					outputVO.setMONTH_AMT(new BigDecimal(String.valueOf(LtcareMonthList.get(0).get("MONTH_AMT"))));
				}
					
			}
		}
	}
	
	//缺口小於等於0時，回傳0
	private BigDecimal comparisonGap(BigDecimal gap){
		if(gap.compareTo(new BigDecimal(0))<=0){
			return new BigDecimal(0);
		}else{
			return gap;
		}
	}
	
	//計算缺口
	public void calculaGap(Object body, IPrimitiveMap header) throws JBranchException {
		INS210InputVO inputVO = (INS210InputVO) body;
		INS210OutputVO outputVO = new INS210OutputVO();
		//需求分析
		calculaDemandAnalysis(inputVO,outputVO);
		
		//目前保障內容
		BigDecimal planList_sum = new BigDecimal(0);
		BigDecimal planList_sum_C = new BigDecimal(0);	//住院日額
		BigDecimal planList_sum_D = new BigDecimal(0);	//一次給付
		BigDecimal planList_sum_W = new BigDecimal(0);	//長期看護
		// 2018/2/13 test
		for(Map<String, Object> map : inputVO.getPlanList()) {
			if("4".equals(inputVO.getPLAN_TYPE())) {
				if(map.get("coverage_C") != null && StringUtils.isNotBlank(ObjectUtils.toString(map.get("coverage_C"))))
					planList_sum_C = planList_sum_C.add(new BigDecimal(String.valueOf(map.get("coverage_C"))));
				if(map.get("coverage_D") != null && StringUtils.isNotBlank(ObjectUtils.toString(map.get("coverage_D"))))
					planList_sum_D = planList_sum_D.add(new BigDecimal(String.valueOf(map.get("coverage_D"))));
				if(map.get("coverage_W") != null && StringUtils.isNotBlank(ObjectUtils.toString(map.get("coverage_W"))))
					planList_sum_W = planList_sum_W.add(new BigDecimal(String.valueOf(map.get("coverage_W"))));
			}
			else {
				if(map.get("coverage") != null && StringUtils.isNotBlank(ObjectUtils.toString(map.get("coverage"))))
					planList_sum = planList_sum.add(new BigDecimal(String.valueOf(map.get("coverage"))));
			}
		}
		String extraProtext = inputVO.getEXTRA_PROTEXT();
		BigDecimal LIP = planList_sum.add(new BigDecimal(extraProtext != null ? extraProtext : "0"));//既有保障+其他行外保單
		
		// 回傳缺口
		switch (inputVO.getPLAN_TYPE()) {
			case "1":
				outputVO.setNOW_PROTECT1(LIP);//已備總金額
				outputVO.setPROJECT_GAP_ONCE1(comparisonGap(outputVO.getSHD_PROTECT1().subtract(LIP)));
				break;
			case "2":
				outputVO.setNOW_PROTECT1(LIP);//已備總金額
				outputVO.setPROJECT_GAP_ONCE2(comparisonGap(outputVO.getSHD_PROTECT1().subtract(LIP)));
				break;
			case "3":
				outputVO.setNOW_PROTECT2(LIP);//已備總金額
				outputVO.setPROJECT_GAP_ONCE3(comparisonGap(outputVO.getSHD_PROTECT2().subtract(LIP)));
				break;
			case "4":
				// 舊CODE calculaDemandAnalysis已經算了
				outputVO.setNOW_PROTECT1(planList_sum_D);
				outputVO.setNOW_PROTECT2(planList_sum_C);
				outputVO.setNOW_PROTECT3(planList_sum_W);
				break;
			default:
				break;
		}

		sendRtnObject(outputVO);
		
	}

	//儲存
	public void Save(Object body, IPrimitiveMap header) throws JBranchException {
		INS210InputVO inputVO = (INS210InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		this.sendRtnObject(saveMain(dam, inputVO));
	}
	
	public INS210OutputVO saveMain(DataAccessManager dam, INS210InputVO inputVO) throws JBranchException {
		INS210OutputVO outputVO = new INS210OutputVO();
		
		if(inputVO.getPLAN_D_KEYNO() != null )
			UpdateThreeTable(inputVO, outputVO);
		// 2018/3/2 有inputVO.getPLAN_KEYNO()要UPDATE TBINS_PLAN_MAIN 我改在INSERT
		else
			AlterMainThenInsertTwoTable(inputVO, outputVO);
		return outputVO;
	}
	
	//確認商品推薦勾選
	public void check_Insured(Object body, IPrimitiveMap header) throws JBranchException {
		INS210InputVO inputVO = (INS210InputVO) body;
		INS210OutputVO outputVO = new INS210OutputVO();
		List<Map<String, Object>> suggest_prod = new ArrayList<Map<String,Object>>();
		
		for(Map<String, Object> map:inputVO.getSuggestList()){
			if("Y".equals(String.valueOf(map.get("choic")))){
				//檢核繳費年期
				if(map.get("INSPRD_ANNUAL_choic") != null && !"".equals(map.get("INSPRD_ANNUAL_choic"))){
					Map<String, Object> suggest_choic = new HashMap<String, Object>();
					suggest_choic.put("CUST_ID", inputVO.getCUST_ID());
					suggest_choic.put("INSPRD_KEYNO", map.get("KEY_NO"));//保險主檔主鍵
					suggest_choic.put("PRD_ID", map.get("PRD_ID"));
					suggest_choic.put("PAY_YEARS", map.get("INSPRD_ANNUAL_choic"));//繳費年期
					suggest_choic.put("CURR_CD", map.get("CURR_CD"));//幣別
					suggest_choic.put("POLICY_ASSURE_AMT", map.get("Insured"));//保額
					suggest_choic.put("UNIT", map.get("PRD_UNIT"));//單位
					suggest_choic.put("Premium", map.get("Premium"));//保費
					// 2018/2/13 test
					suggest_choic.put("SUGGEST_TYPE", map.get("SUGGEST_TYPE"));
					suggest_prod.add(suggest_choic);
				}else{
					suggest_prod.clear();
					break;
				}
			}
		}
		outputVO.setSendInsuredList(suggest_prod);
		sendRtnObject(outputVO);
	}
	
	/**
	 * 把值寫入TBINS_PLAN_MAIN對應欄位*/
	private void sendTBINS_PLAN_MAIN(TBINS_PLAN_MAINVO tpm,INS210InputVO inputVO,CACULATION_THOUSAND caculation){
		tpm.setCUST_ID(inputVO.getCUST_ID());
		Date now = new Date();
		tpm.setPLAN_DATE(new Timestamp(now.getTime()));
		tpm.setPRINT_YN(inputVO.getPRINT_YN());
		
		if("1".equals(inputVO.getPLAN_TYPE()) || "2".equals(inputVO.getPLAN_TYPE())){//壽險及意外險
			tpm.setLIFE_EXPENSE(caculation.getLIFE_EXPENSE());
			tpm.setLIFE_EXPENSE_YEARS(new BigDecimal(inputVO.getLIFE_EXPENSE_YEARS() != null ? inputVO.getLIFE_EXPENSE_YEARS() : "0"));
			tpm.setLIFE_EXPENSE_AMT(caculation.getLIFE_EXPENSE_AMT());
			tpm.setLOAN_AMT(caculation.getLOAN_AMT());
			tpm.setEDU_FFE(caculation.getEDU_FEE());
			tpm.setPREPARE_AMT(caculation.getPREPARE_AMT());
			
			//意外險
			if("2".equals(inputVO.getPLAN_TYPE())){
				if(caculation.getCAREFEES().compareTo(new BigDecimal(0))>0){
					tpm.setNURSE_FEE(caculation.getCAREFEES());
				}
				if(inputVO.getSEARCH_WORK() != null && new BigDecimal(inputVO.getSEARCH_WORK()).compareTo(new BigDecimal(0))>0){
					tpm.setINS_PREPARE_YEARS(new BigDecimal(inputVO.getSEARCH_WORK()));
				}
				if(!"".equals(inputVO.getPROF_GRADE())){
					tpm.setPRO_LEVEL(inputVO.getPROF_GRADE());
				}
			}
		}else if("3".equals(inputVO.getPLAN_TYPE())){//醫療險
			tpm.setSICKROOM_FEE(inputVO.getSICKROOM_FEE() != null ? new BigDecimal(inputVO.getSICKROOM_FEE()) : null);
			tpm.setTTL_FLAG(inputVO.getTTL_FLAG());
		}
		//
		else {
			tpm.setNURSE_FEE_PAY(inputVO.getNURSE_FEE_PAY());
			tpm.setSICKROOM_FEE(inputVO.getSICKROOM_FEE() != null ? new BigDecimal(inputVO.getSICKROOM_FEE()) : null);
			tpm.setDISEASE(TextUtils.join(",", inputVO.getDISEASE()));
			tpm.setMAJOR_DISEASES_PAY(inputVO.getSHD_PROTECT1());
		}
	}
	
	/**把值寫入TBINS_PLAN_DTL對應欄位*/
	private void sendTBINS_PLAN_DTL(TBINS_PLAN_DTLVO tpd,INS210InputVO inputVO){
		tpd.setPLAN_TYPE(inputVO.getPLAN_TYPE());
		// ins200 '1', ins132 is '2' but type 4 still 1
		if(inputVO.getINS200_FROM_INS132() && !StringUtils.equals(inputVO.getPLAN_TYPE(), "4"))
			tpd.setPORTAL_FLAG("2");
		else
			tpd.setPORTAL_FLAG("1");
		
		if("1".equals(inputVO.getPLAN_TYPE()) || "2".equals(inputVO.getPLAN_TYPE())){//壽險及意外險
			tpd.setSHD_PROTECT1(inputVO.getSHD_PROTECT1());
			tpd.setNOW_PROTECT1(inputVO.getNOW_PROTECT1());
			tpd.setPROTECT_GAP1(inputVO.getPROTECT_GAP1());
			tpd.setEXTRA_PROTEXT(new BigDecimal(inputVO.getEXTRA_PROTEXT()));
		}else if("3".equals(inputVO.getPLAN_TYPE())){
			tpd.setSHD_PROTECT2(inputVO.getSHD_PROTECT2());
			tpd.setNOW_PROTECT2(inputVO.getNOW_PROTECT2());
			tpd.setPROTECT_GAP2(inputVO.getPROTECT_GAP2());
			tpd.setEXTRA_PROTEXT(new BigDecimal(inputVO.getEXTRA_PROTEXT()));
		}
		//
		else {
			tpd.setSHD_PROTECT1(inputVO.getSHD_PROTECT1());
			tpd.setNOW_PROTECT1(inputVO.getNOW_PROTECT1());
			tpd.setPROTECT_GAP1(inputVO.getPROTECT_GAP1());
			
			tpd.setSHD_PROTECT2(inputVO.getSHD_PROTECT2());
			tpd.setNOW_PROTECT2(inputVO.getNOW_PROTECT2());
			tpd.setPROTECT_GAP2(inputVO.getPROTECT_GAP2());
			
			tpd.setSHD_PROTECT3(inputVO.getSHD_PROTECT3());
			tpd.setNOW_PROTECT3(inputVO.getNOW_PROTECT3());
			tpd.setPROTECT_GAP3(inputVO.getPROTECT_GAP3());
		}
		
		tpd.setSTATUS(inputVO.getSTATUS());
	}
	
	/**把值寫入TBINS_PLAN_SUG_INSLIST對應欄位*/
	private void sendTBINS_PLAN_SUG_INSLIST(TBINS_PLAN_SUG_INSLISTVO tpsiVO,Map<String, Object> map){
		tpsiVO.setINSPRD_ID(String.valueOf(map.get("PRD_ID")));
		System.out.println(map.get("PRD_ID"));
		tpsiVO.setPAY_YEARS(map.get("PAY_YEARS") != null ? new BigDecimal(ObjectUtils.toString(map.get("PAY_YEARS"))) : null);
		tpsiVO.setCURR_CD(String.valueOf(map.get("CURR_CD")));
		tpsiVO.setUNIT(String.valueOf(map.get("UNIT")));
		if(map.get("SUGGEST_TYPE") != null){
			tpsiVO.setSUGGEST_TYPE(String.valueOf(map.get("SUGGEST_TYPE")));
		}
		if("TWD".equals(map.get("CURR_CD"))) {
			tpsiVO.setPOLICY_ASSURE_AMT_TWD(new BigDecimal(String.valueOf(map.get("POLICY_ASSURE_AMT"))));
			tpsiVO.setPOLICY_FEE_TWD(new BigDecimal(String.valueOf(map.get("Premium"))));
		}
		else {
			tpsiVO.setPOLICY_ASSURE_AMT(new BigDecimal(String.valueOf(map.get("POLICY_ASSURE_AMT"))));
			tpsiVO.setPOLICY_FEE(new BigDecimal(String.valueOf(map.get("Premium"))));
		}
	}
	
	/**update table TBINS_PLAN_MAIN、TBINS_PLAN_DTL、TBINS_PLAN_SUG_INSLIST*/
	private void UpdateThreeTable(INS210InputVO inputVO,INS210OutputVO outputVO) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		
		TBINS_PLAN_MAINVO tpm = new TBINS_PLAN_MAINVO();
		TBINS_PLAN_DTLVO tpd = new TBINS_PLAN_DTLVO();
		tpm = (TBINS_PLAN_MAINVO) dam.findByPKey(TBINS_PLAN_MAINVO.TABLE_UID, inputVO.getPLAN_KEYNO());
		if(tpm != null){
			//壽險保障需求分析數字乘萬
			CACULATION_THOUSAND caculation = new CACULATION_THOUSAND();
			caculation.CaculateMultiplyThousand(inputVO);
			
			sendTBINS_PLAN_MAIN(tpm, inputVO, caculation);
			dam.update(tpm);
		}
		BigDecimal last_PROJECT_GAP_ONCE = new BigDecimal(0);
		int compareGap = 0;
		
		dam = getDataAccessManager();
		tpd = (TBINS_PLAN_DTLVO) dam.findByPKey(TBINS_PLAN_DTLVO.TABLE_UID, new BigDecimal(inputVO.getPLAN_D_KEYNO()));
		if(tpd != null) {
			if("1".equals(inputVO.getPLAN_TYPE()) || "2".equals(inputVO.getPLAN_TYPE())){//壽險及意外險
				last_PROJECT_GAP_ONCE = tpd.getPROTECT_GAP1();
				if(inputVO.getPROTECT_GAP1() == null && last_PROJECT_GAP_ONCE == null) {
					compareGap = 0;
				} else if(inputVO.getPROTECT_GAP1() != null && last_PROJECT_GAP_ONCE != null){
					compareGap = inputVO.getPROTECT_GAP1().compareTo(last_PROJECT_GAP_ONCE);
				} else {
					compareGap = -1;
				}
			}else if("3".equals(inputVO.getPLAN_TYPE())){
				last_PROJECT_GAP_ONCE = tpd.getPROTECT_GAP2();
				if(inputVO.getPROTECT_GAP2() == null && last_PROJECT_GAP_ONCE == null){
					compareGap = 0;
				}else if(inputVO.getPROTECT_GAP2() != null && last_PROJECT_GAP_ONCE != null){
					compareGap = inputVO.getPROTECT_GAP2().compareTo(last_PROJECT_GAP_ONCE);
				}else{
					compareGap = -1;
				}
			}
			else {
				// 2018/2/13 type4 我直接跑舊CODE的"缺口不同刪除後，重新新增"
				compareGap = 1;
			}
			sendTBINS_PLAN_DTL(tpd, inputVO);
			dam.update(tpd);
		}
		
		// 增加沒有的流程 PLAN_AST_INSLIST 要刪除後新增
		if(inputVO.getPLAN_D_KEYNO() != null) {
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			doDeletePlanAstInsList(dam, new BigDecimal(inputVO.getPLAN_D_KEYNO()));
			doInsertPlanAstInsList(inputVO.getPlanList(), dam, loginID, inputVO.getPLAN_TYPE(), new BigDecimal(inputVO.getPLAN_D_KEYNO()));
		}
		
		if(compareGap != 0){
			//缺口不同刪除後，重新新增
			dam = getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" Delete TBINS_PLAN_SUG_INSLIST where PLAN_D_KEYNO = :plan_d_keyno ");
			qc.setObject("plan_d_keyno", inputVO.getPLAN_D_KEYNO());
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
			dam = getDataAccessManager();
			TBINS_PLAN_SUG_INSLISTPK tpsiPK = null;
			TBINS_PLAN_SUG_INSLISTVO tpsiVO = null;
			for(Map<String, Object> map:inputVO.getSendInsuredList()){
				tpsiPK = new TBINS_PLAN_SUG_INSLISTPK();
				tpsiVO = new TBINS_PLAN_SUG_INSLISTVO();
				tpsiPK.setPLAN_D_KEYNO(new BigDecimal(inputVO.getPLAN_D_KEYNO()));
				tpsiPK.setINSPRD_KEYNO(new BigDecimal(String.valueOf(map.get("INSPRD_KEYNO"))));
				tpsiVO.setcomp_id(tpsiPK);
				if(map.get("PRD_ID") == null && map.get("PAY_YEARS") == null 
						&& map.get("CURR_CD") == null && map.get("UNIT") == null){
					Map<String, Object> prdMap = (Map<String, Object>) getINS450().getPrdInfo(tpsiPK.getINSPRD_KEYNO()).get(0);
					map.put("PRD_ID", prdMap.get("PRD_ID"));
					map.put("PAY_YEARS", (BigDecimal)prdMap.get("INSPRD_ANNUAL"));
					map.put("CURR_CD", prdMap.get("CURR_CD"));
					map.put("UNIT", prdMap.get("PRD_UNIT"));
				}
				sendTBINS_PLAN_SUG_INSLIST(tpsiVO, map);
				dam.create(tpsiVO);
			}
		}else{
			
			dam = getDataAccessManager();
			TBINS_PLAN_SUG_INSLISTPK tpsiPK = null;
			
			for(Map<String, Object> map:inputVO.getSendInsuredList()){
				tpsiPK = new TBINS_PLAN_SUG_INSLISTPK();
				TBINS_PLAN_SUG_INSLISTVO tpsiVO = null;
				tpsiPK.setPLAN_D_KEYNO(new BigDecimal(inputVO.getPLAN_D_KEYNO()));
				tpsiPK.setINSPRD_KEYNO(new BigDecimal(String.valueOf(map.get("INSPRD_KEYNO"))));
				tpsiVO = (TBINS_PLAN_SUG_INSLISTVO) dam.findByPKey(TBINS_PLAN_SUG_INSLISTVO.TABLE_UID, tpsiPK);
				if(tpsiVO != null){
					if(map.get("PRD_ID") == null && map.get("PAY_YEARS") == null 
							&& map.get("CURR_CD") == null && map.get("UNIT") == null){
						Map<String, Object> prdMap = (Map<String, Object>) getINS450().getPrdInfo(tpsiPK.getINSPRD_KEYNO()).get(0);
						map.put("PRD_ID", prdMap.get("PRD_ID"));
						map.put("PAY_YEARS", (BigDecimal)prdMap.get("INSPRD_ANNUAL"));
						map.put("CURR_CD", prdMap.get("CURR_CD"));
						map.put("UNIT", prdMap.get("PRD_UNIT"));
					}
					sendTBINS_PLAN_SUG_INSLIST(tpsiVO, map);
					dam.update(tpsiVO);
				}
			}
			
		}
		
		outputVO.setPLAN_KEYNO(inputVO.getPLAN_KEYNO());
		outputVO.setPLAN_D_KEYNO(inputVO.getPLAN_D_KEYNO());
	
	}
	
	/**insert table TBINS_PLAN_MAIN、TBINS_PLAN_DTL、TBINS_PLAN_SUG_INSLIST
	 * @throws JBranchException */
	private void AlterMainThenInsertTwoTable(INS210InputVO inputVO,INS210OutputVO outputVO) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);

		String PLAN_KEYNO = new String();
		List<Map<String, Object>> PLAN_D_KEYNOList = new ArrayList<Map<String,Object>>();

		//取號
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append(" Select TBINS_PLAN_DTL_SEQ.NEXTVAL from dual ");
		qc.setQueryString(sb.toString());
		PLAN_D_KEYNOList = dam.exeQuery(qc);
		
		//壽險保障需求分析數字乘萬
		CACULATION_THOUSAND caculation = new CACULATION_THOUSAND();
		caculation.CaculateMultiplyThousand(inputVO);
		
		//存入TBINS_PLAN_MAIN
		dam = getDataAccessManager();
		
		// 2018/3/2 有inputVO.getPLAN_KEYNO(g)要UPDATE TBINS_PLAN_MAIN 我改在INSERT
		TBINS_PLAN_MAINVO tpm = new TBINS_PLAN_MAINVO();
		if(inputVO.getPLAN_KEYNO() != null) {
			PLAN_KEYNO = inputVO.getPLAN_KEYNO(); 
			
			tpm = (TBINS_PLAN_MAINVO) dam.findByPKey(TBINS_PLAN_MAINVO.TABLE_UID, inputVO.getPLAN_KEYNO());
			System.out.println((TBINS_PLAN_MAINVO) dam.findByPKey(TBINS_PLAN_MAINVO.TABLE_UID, PLAN_KEYNO));
			if(tpm != null) {
				sendTBINS_PLAN_MAIN(tpm, inputVO, caculation);
				dam.update(tpm);
			}
			else
				throw new APException("系統發生錯誤：主檔不存在，請重新操作此交易。");
		}
		// 舊CODE走新增
		else {
			INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
			INS810InputVO ins810inputVO = new INS810InputVO();
			INS810OutputVO ins810outputVO = new INS810OutputVO();
			ins810outputVO = ins810.getInsSeq(ins810inputVO);
			PLAN_KEYNO = "PLAN"+ins810outputVO.getInsSeq();
			
			tpm.setPLAN_KEYNO(PLAN_KEYNO);
			sendTBINS_PLAN_MAIN(tpm, inputVO, caculation);
			dam.create(tpm);
		}
		
		//先刪除TBINS_PLAN_DTL & TBINS_PLAN_SUG_INSLIST中已存在資料		
		if(StringUtils.isNotBlank(inputVO.getPLAN_TYPE()) && StringUtils.isNotBlank(inputVO.getPLAN_KEYNO())) {
			dam = getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			qc.setQueryString("DELETE FROM TBINS_PLAN_SUG_INSLIST WHERE PLAN_D_KEYNO IN (SELECT PLAN_D_KEYNO FROM TBINS_PLAN_DTL WHERE PLAN_KEYNO = :planKeyNo AND PLAN_TYPE = :planType) ");
			qc.setObject("planKeyNo", inputVO.getPLAN_KEYNO());
			qc.setObject("planType", inputVO.getPLAN_TYPE());
			dam.exeUpdate(qc);
			
			dam = getDataAccessManager();
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			qc.setQueryString("DELETE FROM TBINS_PLAN_DTL WHERE PLAN_KEYNO = :planKeyNo AND PLAN_TYPE = :planType ");
			qc.setObject("planKeyNo", inputVO.getPLAN_KEYNO());
			qc.setObject("planType", inputVO.getPLAN_TYPE());
			dam.exeUpdate(qc);
		}
		
		//存入TBINS_PLAN_DTL
		dam = getDataAccessManager();
		TBINS_PLAN_DTLVO tpd = new TBINS_PLAN_DTLVO();
		
		BigDecimal planDKeyNo = new BigDecimal(String.valueOf(PLAN_D_KEYNOList.get(0).get("NEXTVAL"))); 
		tpd.setPLAN_KEYNO(PLAN_KEYNO);
		tpd.setPLAN_D_KEYNO(planDKeyNo);
		
		sendTBINS_PLAN_DTL(tpd, inputVO);
		dam.create(tpd);
		
		//存入TBINS_PLAN_SUG_INSLIST
		dam = getDataAccessManager();
		TBINS_PLAN_SUG_INSLISTPK tpsiPK = null;
		TBINS_PLAN_SUG_INSLISTVO tpsiVO = null;
		for(Map<String, Object> map:inputVO.getSendInsuredList()){
			tpsiPK = new TBINS_PLAN_SUG_INSLISTPK();
			tpsiVO = new TBINS_PLAN_SUG_INSLISTVO();
			tpsiPK.setPLAN_D_KEYNO(planDKeyNo);
			tpsiPK.setINSPRD_KEYNO(new BigDecimal(String.valueOf(map.get("INSPRD_KEYNO"))));
			tpsiVO.setcomp_id(tpsiPK);
			if(map.get("PRD_ID") == null && map.get("PAY_YEARS") == null 
					&& map.get("CURR_CD") == null && map.get("UNIT") == null){
				Map<String, Object> prdMap = (Map<String, Object>) getINS450().getPrdInfo(tpsiPK.getINSPRD_KEYNO()).get(0);
				map.put("PRD_ID", prdMap.get("PRD_ID"));
				map.put("PAY_YEARS", (BigDecimal)prdMap.get("INSPRD_ANNUAL"));
				map.put("CURR_CD", prdMap.get("CURR_CD"));
				map.put("UNIT", prdMap.get("PRD_UNIT"));
			}
			sendTBINS_PLAN_SUG_INSLIST(tpsiVO, map);
			dam.create(tpsiVO);
		}
		
		// save TBINS_PLAN_AST
		doInsertPlanAstInsList(inputVO.getPlanList(), dam, loginID, inputVO.getPLAN_TYPE(), planDKeyNo);



		outputVO.setPLAN_KEYNO(PLAN_KEYNO);
		outputVO.setPLAN_D_KEYNO(String.valueOf(PLAN_D_KEYNOList.get(0).get("NEXTVAL")));
	}
	
	/**
	 * 新增 PLAN_AST_INS_LIST
	 * @param planList
	 * @param dam
	 * @param loginID
	 * @param planType
	 * @param planDKeyNo
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private void doInsertPlanAstInsList(List<Map<String, Object>> planList, DataAccessManager dam, String loginID, String planType, BigDecimal planDKeyNo) throws DAOException, JBranchException {
		for(Map<String, Object> map : planList){
			dam = getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			
			sb.append(" INSERT INTO TBINS_PLAN_AST_INSLIST(PLAN_D_KEYNO, COM_ID, POLICY_NBR, PRD_NAME, CURR_CD, POLICY_ASSURE_AMT, UNIT ");
			
			if(StringUtils.isNotBlank(String.valueOf(map.get("coverage"))) || StringUtils.isNotBlank(String.valueOf(map.get("coverage_D")))){	//保障(壽險/意外險)
				sb.append(" ,POLICY_RETURN_AMT1 ");
			}
			if(StringUtils.isNotBlank(String.valueOf(map.get("coverage_C")))){	//保障(住院日額)
				sb.append(" ,POLICY_RETURN_AMT2 ");
			}
			if(StringUtils.isNotBlank(String.valueOf(map.get("coverage_W")))){	//保障(長看每月給付)
				sb.append(" ,POLICY_RETURN_AMT3 ");
			}
			
			sb.append(" ,INS_FEE ,CREATETIME ,CREATOR ,MODIFIER ,LASTUPDATE) ");
			
			sb.append(" VALUES(:plan_d_keyno, :com_id, :policy_nbr, :prd_name, :curr_cd, :policy_assure_amt, :unit ");
			qc.setObject("plan_d_keyno", planDKeyNo);
			qc.setObject("com_id", map.get("INSCO"));
			qc.setObject("policy_nbr", map.get("INSSEQ"));
			qc.setObject("prd_name", map.get("PRD_NAME"));
			qc.setObject("curr_cd", map.get("CURR_CD"));
			qc.setObject("policy_assure_amt", map.get("INSUREDAMT"));
			qc.setObject("unit", map.get("unit"));
			System.out.println(map.get("unit"));
			
			if(StringUtils.isNotBlank(String.valueOf(map.get("coverage"))) || StringUtils.isNotBlank(String.valueOf(map.get("coverage_D")))){	//保障(壽險/意外險)
				sb.append(" , :policy_return_amt1 ");
				if(StringUtils.isNotBlank(String.valueOf(map.get("coverage"))) && ("1".equals(planType) || "2".equals(planType)))
					qc.setObject("policy_return_amt1", map.get("coverage"));
				else
					qc.setObject("policy_return_amt1", map.get("coverage_D") ==null ? 0.0 :map.get("coverage_D"));
			}
			if(StringUtils.isNotBlank(String.valueOf(map.get("coverage_C")))){	//保障(住院日額)
				sb.append(" , :policy_return_amt2 ");
				qc.setObject("policy_return_amt2", map.get("coverage_C"));
			}
			if(StringUtils.isNotBlank(String.valueOf(map.get("coverage_W")))){	//保障(長看每月給付)
				sb.append(" , :policy_return_amt3 ");
				qc.setObject("policy_return_amt3", map.get("coverage_W"));
			}
			
			sb.append(" , :ins_fee ,sysdate , :creator , :modifier ,sysdate) ");
			qc.setObject("ins_fee", map.get("INSYEARFEE") == null ? 0.0 :map.get("INSYEARFEE"));
			qc.setObject("creator", loginID);
			qc.setObject("modifier", loginID);
			
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
		}
	}
	
	/**
	 * 刪除 PLAN_AST_INS_LIST
	 * @param dam
	 * @param planDKeyNo
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private void doDeletePlanAstInsList(DataAccessManager dam, BigDecimal planDKeyNo) throws DAOException, JBranchException {
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" Delete TBINS_PLAN_AST_INSLIST where PLAN_D_KEYNO = :plan_d_keyno ");
		qc.setObject("plan_d_keyno", planDKeyNo);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);

	}
	
	//======================== WEB 處理 ========================
	//======================== WS 調用 ========================
	
	/**
	 * 保險特定規劃2階API-儲存壽險、意外險、醫療險、重大疾病保險規劃
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public INS210OutputVO saveInsurancePlan(Object body, String sppType) throws JBranchException{
		Map inputMap = (Map)body;
		DataAccessManager dam = this.getDataAccessManager();
		INS210InputVO inputVO = new INS210InputVO();
		inputVO.setCUST_ID(ObjectUtils.toString(inputMap.get("custId")));
		inputVO.setPLAN_KEYNO(ObjectUtils.toString(inputMap.get("planKeyNo")));
		inputVO.setPLAN_TYPE(sppType);
		inputVO.setParaNO(ObjectUtils.toString(inputMap.get("paraNo")));
		inputVO.setINS200_FROM_INS132(inputMap.get("isFinancialTrial") != null ? (Boolean)inputMap.get("isFinancialTrial") : false );
		String paraNo_medical = inputMap.get("paraNo_medical") == null ? null : ObjectUtils.toString(inputMap.get("paraNo_medical"));
		String paraNo_diease = inputMap.get("paraNo_diease") == null ? null : ObjectUtils.toString(inputMap.get("paraNo_diease"));
		
		//儲存時規劃狀態設定為規劃中
		inputVO.setSTATUS("1");
		
		// 如果不是家庭財務安全過來的，如果是的話不需要需求分析
		if(!inputVO.getINS200_FROM_INS132()) {
			GenericMap requiredInfoMap = new GenericMap((Map)inputMap.get("requiredInfo"));
			inputVO.setLIFE_EXPENSE(ObjectUtils.toString(requiredInfoMap.getBigDecimal("lifeExpense")));
			inputVO.setLIFE_EXPENSE_YEARS(ObjectUtils.toString(requiredInfoMap.getBigDecimal("lifeExpenseYears")));
			inputVO.setLIFE_EXPENSE_AMT(requiredInfoMap.getBigDecimal("totalAMT"));
			inputVO.setLOAN_AMT(ObjectUtils.toString(requiredInfoMap.getBigDecimal("loanAmt")));
			inputVO.setEDU_FEE(ObjectUtils.toString(requiredInfoMap.getBigDecimal("etuFee")));
			inputVO.setPREPARE_AMT(ObjectUtils.toString(requiredInfoMap.getBigDecimal("prepareAmt")));
			
			// 意外的
			inputVO.setCAREFEES(ObjectUtils.toString(requiredInfoMap.getBigDecimal("careAmt")));
			inputVO.setSEARCH_WORK(ObjectUtils.toString(requiredInfoMap.getBigDecimal("jobYears")));
			inputVO.setPROF_GRADE(ObjectUtils.toString(requiredInfoMap.getStr("jobGrade")));
			
			inputVO.setTTL_FLAG(ObjectUtils.toString(requiredInfoMap.getStr("ttlFlag")));
		}
		
		//取得所有醫院相關訊息清單
		List<Map<String, Object>> allHospitalList = null;
		if(paraNo_medical != null){
			allHospitalList = getAllHospitalList(ObjectUtils.toString(inputMap.get("paraNo_medical")));		
		}else{
			allHospitalList = getAllHospitalList(ObjectUtils.toString(inputMap.get("paraNo")));
		}
		
		//取得所有照顧方針清單
		List<Map<String, Object>> allCareWayList = null;
		INS200 ins200 = (INS200) PlatformContext.getBean("ins200");
		if(paraNo_diease != null){
			allCareWayList = ins200.getAllCareWayList(ObjectUtils.toString(inputMap.get("paraNo_diease")), null, null);			
		}else{
			allCareWayList = ins200.getAllCareWayList(ObjectUtils.toString(inputMap.get("paraNo")), null, null);			
		}
		
		// 醫療 & 重大共用
		// 如果 requiredInfo 有值 >> 不是家庭財務過來的或是重大疾病
		if(MapUtils.isNotEmpty((Map)inputMap.get("requiredInfo"))) {
			GenericMap requiredInfoMap = new GenericMap((Map)inputMap.get("requiredInfo"));
			inputVO.setHOSPITAL_TYPE(ObjectUtils.toString(requiredInfoMap.getStr("hospitalType")));
			inputVO.setWARD_TYPE(ObjectUtils.toString(requiredInfoMap.getStr("wardType")));
			inputVO.setDISEASE((List<String>) requiredInfoMap.get("diseaseId"));
			inputVO.setCareWay(ObjectUtils.toString(requiredInfoMap.getStr("careWay")));
			inputVO.setCareStyle(ObjectUtils.toString(requiredInfoMap.getStr("careStyle")));
			//醫院與病房種類KEYNO
			String hKeyNo = getKeyNo(allHospitalList, new GenericMap().put("key", "HOSPITAL_TYPE").put("value", ObjectUtils.toString(requiredInfoMap.getStr("hospitalType"))), 
					new GenericMap().put("key", "WARD_TYPE").put("value", ObjectUtils.toString(requiredInfoMap.getStr("wardType"))), "H_KEYNO");
			//長期照護照顧方式KEYNO
			String ltKeyNo = getKeyNo(allCareWayList, new GenericMap().put("key", "CARE_WAY").put("value", ObjectUtils.toString(requiredInfoMap.getStr("careWay"))), 
					new GenericMap().put("key", "CARE_STYLE").put("value", ObjectUtils.toString(requiredInfoMap.getStr("careStyle"))), "DATA");
			
			inputVO.setSICKROOM_FEE(hKeyNo == null ? null : hKeyNo);
			inputVO.setNURSE_FEE_PAY(ltKeyNo == null ? null : new BigDecimal(ltKeyNo));
		}
		
		if("4".equals(sppType)){
			// 應備
			GenericMap shdInfo = new GenericMap((Map)inputMap.get("shdInfo"));
			inputVO.setSHD_PROTECT1(shdInfo.getBigDecimal("shdProtect1"));
			inputVO.setSHD_PROTECT2(shdInfo.getBigDecimal("shdProtect2"));
			inputVO.setSHD_PROTECT3(shdInfo.getBigDecimal("shdProtect3"));
			// 已備
			GenericMap nowInfo = new GenericMap((Map)inputMap.get("nowInfo"));
			inputVO.setNOW_PROTECT1(nowInfo.getBigDecimal("nowProtect1"));
			inputVO.setNOW_PROTECT2(nowInfo.getBigDecimal("nowProtect2"));
			inputVO.setNOW_PROTECT3(nowInfo.getBigDecimal("nowProtect3"));	
			// 缺口
			GenericMap gapInfo = new GenericMap((Map)inputMap.get("gapInfo"));
			inputVO.setPROTECT_GAP1(gapInfo.getBigDecimal("protectGap1"));
			inputVO.setPROTECT_GAP2(gapInfo.getBigDecimal("protectGap2"));
			inputVO.setPROTECT_GAP3(gapInfo.getBigDecimal("protectGap3"));
		}else{
			// 應備
			inputVO.setSHD_PROTECT1(new GenericMap(inputMap).getBigDecimal("shdProtect1"));
			inputVO.setSHD_PROTECT2(new GenericMap(inputMap).getBigDecimal("shdProtect2"));
			inputVO.setSHD_PROTECT3(new GenericMap(inputMap).getBigDecimal("shdProtect3"));
			// 已備
			inputVO.setNOW_PROTECT1(new GenericMap(inputMap).getBigDecimal("nowProtect1"));
			inputVO.setNOW_PROTECT2(new GenericMap(inputMap).getBigDecimal("nowProtect2"));
			inputVO.setNOW_PROTECT3(new GenericMap(inputMap).getBigDecimal("nowProtect3"));	
			// 缺口
			inputVO.setPROTECT_GAP1(new GenericMap(inputMap).getBigDecimal("protectGap1"));
			inputVO.setPROTECT_GAP2(new GenericMap(inputMap).getBigDecimal("protectGap2"));
			inputVO.setPROTECT_GAP3(new GenericMap(inputMap).getBigDecimal("protectGap3"));
		}

		//1.4.5	目前保障內容保單資料
		List<Map<String, Object>> currentList = new ArrayList<Map<String,Object>>();
		if((List<Map<String, Object>>)inputMap.get("current") != null){			
			for(Map<String, Object> currentMap : (List<Map<String, Object>>)inputMap.get("current")) {
				Map<String, Object> newCurrentMap = new HashMap<String, Object>();
				newCurrentMap.put("INSSEQ", currentMap.get("insseq"));
				newCurrentMap.put("INSCO", currentMap.get("insco"));
				newCurrentMap.put("PRD_NAME", currentMap.get("prdName"));
				newCurrentMap.put("CURR_CD", currentMap.get("currCd"));
				newCurrentMap.put("INSUREDAMT", currentMap.get("insuredamt"));
				
				newCurrentMap.put("coverage", inputVO.getPLAN_TYPE() != "4" ? currentMap.get("coverage") : "");
				newCurrentMap.put("coverage_C", inputVO.getPLAN_TYPE() == "4" ? currentMap.get("coverageC") : 0.0);
				newCurrentMap.put("coverage_W", inputVO.getPLAN_TYPE() == "4" ? currentMap.get("coverageW") : 0.0);
				newCurrentMap.put("coverage_D", inputVO.getPLAN_TYPE() == "4" ? currentMap.get("coverageD") : 0.0);
				
				newCurrentMap.put("unit", currentMap.get("unit"));
				newCurrentMap.put("INSYEARFEE", currentMap.get("insyearfee"));
				currentList.add(newCurrentMap);
			}
		}
		inputVO.setPlanList(currentList);
		inputVO.setEXTRA_PROTEXT(ObjectUtils.toString(inputMap.get("extraProtext")));
		
		List<Map<String, Object>> recommandList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> recommandMap : (List<Map<String, Object>>)inputMap.get("recommand")){
			Map<String,Object> newRecommendMap = new HashMap<String, Object>();
			newRecommendMap.put("INSPRD_KEYNO", recommandMap.get("prdKeyNo"));
			newRecommendMap.put("POLICY_ASSURE_AMT", recommandMap.get("policyAssureAmt"));
			newRecommendMap.put("Premium", recommandMap.get("premium"));
			if(recommandMap.get("suggestType") != null){
				int typeValue = new Double((double) recommandMap.get("suggestType")).intValue();				
				String suggestType = null;
				
				if(typeValue == 1){
					suggestType  ="A";
				}else if(typeValue == 2){
					suggestType  ="4";
				}else{
					suggestType  ="B";
				}
				newRecommendMap.put("SUGGEST_TYPE", suggestType);				
			}
			recommandList.add(newRecommendMap);
			
		}
		
		System.out.println(recommandList);
		
		insertIntoTBINS_MAIN_PLAN(inputVO);
		inputVO.setSendInsuredList(recommandList);
		inputVO.setPLAN_D_KEYNO(findplanDkey(inputVO));
		
		INS210OutputVO outVO = saveMain(dam, inputVO);
		return outVO;
	}
	
	public void insertIntoTBINS_MAIN_PLAN(INS210InputVO inputVO) throws JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		
		//壽險保障需求分析數字乘萬
		CACULATION_THOUSAND caculation = new CACULATION_THOUSAND();
		caculation.CaculateMultiplyThousand(inputVO);
		
		TBINS_PLAN_MAINVO tpm = new TBINS_PLAN_MAINVO();
		tpm = (TBINS_PLAN_MAINVO) dam.findByPKey(TBINS_PLAN_MAINVO.TABLE_UID, inputVO.getPLAN_KEYNO());
		if(null==tpm){
			tpm = new TBINS_PLAN_MAINVO();
			tpm.setPLAN_KEYNO(inputVO.getPLAN_KEYNO());
			sendTBINS_PLAN_MAIN(tpm, inputVO, caculation);
			dam.create(tpm);		
		}
	}
	
	/**
	 * 保險特定規劃2階API-查詢明細序號是否存在(TBINS_PLAN_DTL)
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */	
	@SuppressWarnings("unchecked")
	public String findplanDkey (INS210InputVO inputVO) throws JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PLAN_D_KEYNO FROM TBINS_PLAN_DTL ");
		sb.append("WHERE PLAN_KEYNO = :PLANKEYNO ");
		sb.append("AND PLAN_TYPE = :PLANTYPE ");
		qc.setObject("PLANKEYNO", inputVO.getPLAN_KEYNO());
		qc.setObject("PLANTYPE", inputVO.getPLAN_TYPE());

		//查詢結果
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		String plan_Dkey = CollectionUtils.isEmpty(result) ? null : ObjectUtils.toString(result.get(0).get("PLAN_D_KEYNO"), null);
		
		System.out.println(plan_Dkey);
		return plan_Dkey;
	}
	
	/**
	 * 保險特定規劃2階API-儲存壽險保險規劃
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveInsurancePlanByLife(Object body,IPrimitiveMap header) throws JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		
		INS210OutputVO outVO = saveInsurancePlan(body, "1");
		this.sendRtnObject(outVO);
	}
	
	/**
	 * 保險特定規劃2階API-儲存意外險保險規劃
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveInsurancePlanByAccident(Object body,IPrimitiveMap header) throws JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		Map inputMap = (Map)body;
		
		INS210OutputVO outVO = saveInsurancePlan(body, "2");
		this.sendRtnObject(outVO);
	}
	
	/**
	 * 保險特定規劃2階API-儲存醫療險保險規劃
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveInsurancePlanByMedical(Object body,IPrimitiveMap header) throws JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		Map inputMap = (Map)body;
		
		INS210OutputVO outVO = saveInsurancePlan(inputMap, "3");
		this.sendRtnObject(outVO);
	}
	
	/**
	 * 保險特定規劃2階API-儲存重大疾病保險規劃
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveInsurancePlanByDreadDisease(Object body,IPrimitiveMap header) throws JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		Map inputMap = (Map)body;
		
		INS210OutputVO outVO = saveInsurancePlan(inputMap, "4");
		this.sendRtnObject(outVO);
	}
	
	/**
	 * ws調用 重新取得目前保障內容(意外險) reCalculateAccidentPolicy
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void reCalculateAccidentPolicy(Object body,IPrimitiveMap header) throws JBranchException {
		GenericMap inputGMap = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(inputGMap.getParamMap()));
		String custId = inputGMap.getStr("custId");
		String jobGrade = inputGMap.getStr("jobGrade");
		String loginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		
		Map<String, Object> currentMap = new HashMap<String, Object>();
		currentMap.put("current", WSMappingParserUtils.getCurrentPolicyList(getPlanList("2", custId, loginBraNbr, loginAO, jobGrade), 2));
		System.out.println(new Gson().toJson(currentMap));
		this.sendRtnObject(currentMap);
	}

	
	//======================== WS 處理 ========================
	/**
	 * 取得所有醫院相關訊息清單
	 * @param paraNo
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private List<Map<String, Object>> getAllHospitalList(String paraNo) throws DAOException, JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setObject("paraNo", paraNo);
		qc.setQueryString(getHospitalListSql());
		List<Map<String, Object>> allHospitalList = dam.exeQuery(qc);
		return allHospitalList;
	}
	
	/**
	 * 取得醫院相關資訊 SQL
	 * @return
	 */
	private String getHospitalListSql() {
		StringBuffer sql = new StringBuffer();
		sql.append(" select H_KEYNO, PARA_NO, DAY_AMT, HOSPITAL_TYPE, TP1.PARAM_NAME HOSPITAL_NAME, WARD_TYPE, TP2.PARAM_NAME WARD_NAME"); 
		sql.append(" from TBINS_PARA_HOSPITAL TH ");
		sql.append(" left join TBSYSPARAMETER TP1 on TH.HOSPITAL_TYPE = TP1.PARAM_CODE ");
		sql.append(" left join TBSYSPARAMETER TP2 on TH.WARD_TYPE = TP2.PARAM_CODE ");
		sql.append(" where 1=1  ");
		sql.append(" and PARA_NO = :paraNo ");
		sql.append(" and TP1.PARAM_TYPE ='INS.HOSPITAL_TYPE' "); 
		sql.append(" and TP2.PARAM_TYPE ='INS.WARD_TYPE' ");
		sql.append(" order by HOSPITAL_TYPE, WARD_TYPE asc ");
		return sql.toString();
	}
	
	/**
	 * 取得醫院與病房種類和長期照護照顧方式KEYNO
	 * @param fromList
	 * @param bigRangeMap
	 * @param smallRangeMap
	 * @param keyNoStr
	 * @return
	 * @throws JBranchException 
	 */
	public String getKeyNo(List<Map<String,Object>> fromList, GenericMap bigRangeMap, GenericMap smallRangeMap, String keyNoStr) throws JBranchException{
		
		List<Map<String,Object>> transitList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> fromMap : fromList){
			if(bigRangeMap.get("value").equals(ObjectUtils.toString(fromMap.get(bigRangeMap.get("key"))))){
				transitList.add(fromMap);
			}
		}
		
		if(transitList.size() == 0){
			return null;
		}
		
		Map<String,Object> finalMap = null;
		
		for(Map<String, Object> transitMap : transitList){
			if(smallRangeMap.get("value").equals(ObjectUtils.toString(transitMap.get(smallRangeMap.get("key"))))){
				finalMap = transitMap;
			}
		}
		
		if(finalMap == null){
			return null;
		}

		return finalMap.get(keyNoStr).toString();
	}
	
	//======================== 共用 調用 & 處理 ========================
	/**
	 * 共用統一調用取得 DealWithINSJLB 目前保障內容
	 * @param planType
	 * @param custId
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPlanList(String planType, String custId, String loginBraNbr, ArrayList<String> loginAO, String jobGrade) throws JBranchException {
		List<Map<String, Object>> planList = new ArrayList<Map<String,Object>>();
		DealWithINSJLB dwINSJLB = (DealWithINSJLB) PlatformContext.getBean("dealwithinsjlb");
		dwINSJLB.callINSJLB(custId, loginBraNbr, loginAO, PlanType.valueOf("P" + planType).getMappingPlanType(), planList, jobGrade);
		// 需要針對 planType == 4 重大疾病的做處理
		// 年化數字/12 後四捨五入至整數位。 mantis : 5036
		if("4".equals(planType)) {
			for(Map<String, Object> planMap : planList) {
				// 長期
				String coverageW = ObjectUtils.toString(planMap.get("coverage_W"));
				planMap.put("coverage_W", StringUtils.isNotEmpty(coverageW) ? new BigDecimal(coverageW).divide(new BigDecimal(12), 0, BigDecimal.ROUND_HALF_UP) : 0);
				// 一次
				String coverageD = ObjectUtils.toString(planMap.get("coverage_D"));
				planMap.put("coverage_D", StringUtils.isNotEmpty(coverageD) ? new BigDecimal(coverageD).multiply(new BigDecimal(10000)) : 0);
			}
		}
		return planList;
	}
	
	/**
	 * 共用統一調用取得 FubonIns810 建議商品
	 * @param paraNo
	 * @param currCd
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getSuggestProd(String paraNo, String currCd,String insAge) throws DAOException, JBranchException{
		return getIns810().getSuggestDataParser(new PolicySuggestInputVO(null, paraNo, currCd,insAge), this.getDataAccessManager());
	}
	
	// ======================== 待刪除 ========================	
}
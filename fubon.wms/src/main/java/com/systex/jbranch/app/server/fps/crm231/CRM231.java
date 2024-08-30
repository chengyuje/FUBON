package com.systex.jbranch.app.server.fps.crm231;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm230.CRM230;
import com.systex.jbranch.app.server.fps.crm230.CRM230OutputVO;
import com.systex.jbranch.app.server.fps.crm230.CRM230_ALLInputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author walalala
 * @date 2016/11/03
 * @spec null
 */
@Component("crm231")
@Scope("request")
public class CRM231 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM231.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM231InputVO inputVO = (CRM231InputVO) body;
		
		CRM230_ALLInputVO inputVO_all = new CRM230_ALLInputVO();
		inputVO_all.setCrm231inputVO(inputVO);
		inputVO_all.setCrm230inputVO(inputVO);
		inputVO_all.setAvailRegionList(getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		inputVO_all.setAvailAreaList(getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		inputVO_all.setAvailBranchList(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		inputVO_all.setLoginEmpID(ws.getUser().getUserID());
		inputVO_all.setLoginRole(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		CRM230 crm230 = (CRM230) PlatformContext.getBean("crm230");
		CRM230OutputVO outputVO_crm230 = new CRM230OutputVO();
		
		outputVO_crm230 = crm230.inquire_common(inputVO_all, "CRM231");

		this.sendRtnObject(outputVO_crm230);
	}
	
//	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
//		CRM231InputVO inputVO = (CRM231InputVO) body;
//		CRM231OutputVO return_VO = new CRM231OutputVO();
//		dam = this.getDataAccessManager();
//		
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT A.CUST_ID, A.CUST_NAME, A.AUM_AMT, A.CON_DEGREE, A.VIP_DEGREE, B.PRV_BANK_FAMILY, B.COMPLAIN_YN, B.REC_YN, A.AO_CODE, A.GENDER, A.BIRTH_DATE, B.CO_ACCT_YN, A.BRA_NBR, C.PROD_ID, C.PROD_NAME, C.CRCY_TYPE ");
//		sql.append("FROM TBCRM_CUST_MAST A INNER JOIN TBCRM_CUST_NOTE B ON A.CUST_ID = B.CUST_ID INNER JOIN XXXXXXX C ON A.CUST_ID = C.CUST_ID where 1=1 ");
//		// where
//		//客戶篩選
//		if (!StringUtils.isBlank(inputVO.getBra_nbr()))
//			sql.append("and A.BRA_NBR = :bra_nbr ");
//		if (!StringUtils.isBlank(inputVO.getCountry_nbr()))
//			sql.append("and A.COUNTRY_NBR = :country_nbr ");
//		if (!StringUtils.isBlank(inputVO.getGender()))
//			sql.append("and A.GENDER = :gender ");
//		if (!StringUtils.isBlank(inputVO.getMarriage()))
//			sql.append("and A.MARRIAGE_STAT = :marriage ");
//		if (!StringUtils.isBlank(inputVO.getEducation()))
//			sql.append("and A.EDUCATION_STAT = :education ");
//		if (!StringUtils.isBlank(inputVO.getChild()))
//			sql.append("and A.CHILD_NUM = :child ");
//		if (!StringUtils.isBlank(inputVO.getVip_degree()))
//			sql.append("and A.VIP_DEGREE = :vip_degree ");
//		if (!StringUtils.isBlank(inputVO.getAo_code()))
//			sql.append("and A.AO_CODE = :ao_code ");
//		if (!StringUtils.isBlank(inputVO.getCust_risk_atr()))
//			sql.append("and A.CUST_RISK_ATR = :cust_risk_atr ");
//		if (!StringUtils.isBlank(inputVO.getSal_company()))
//			sql.append("and A.SAL_COMPANY = :sal_company ");
//		if (!StringUtils.isBlank(inputVO.getCo_acct()))
//			sql.append("and B.CO_ACCT_YN = :co_acct ");
//		if (!StringUtils.isBlank(inputVO.getComplain()))
//			sql.append("and B.COMPLAIN_YN = :complain ");
//		if (!StringUtils.isBlank(inputVO.getEbank()))
//			sql.append("and B.EBANK_YN = :ebank ");
//		if (!StringUtils.isBlank(inputVO.getVoice()))
//			sql.append("and B.VOICE_YN = :voice ");
//		if (!StringUtils.isBlank(inputVO.getApp()))
//			sql.append("and B.APP_YN = :app ");
//		if (!StringUtils.isBlank(inputVO.getInvestor()))
//			sql.append("and B.PROF_INVESTOR_YN = :investor ");
//		if (!StringUtils.isBlank(inputVO.getRec()))
//			sql.append("and B.REC_YN = :rec ");
//		if (inputVO.getBirth_sDate() != null)
//			sql.append("and TRUNC(A.BIRTH_DATE) >= :start ");
//		if (inputVO.getBirth_eDate() != null)
//			sql.append("and TRUNC(A.BIRTH_DATE) <= :end ");
//		//商品篩選
//		if (!StringUtils.isBlank(inputVO.getCrcy_type()))
//			sql.append("and C.CRCY_TYPE = :crcy_type ");
//		if (!StringUtils.isBlank(inputVO.getProd_id()))
//			sql.append("and C.PROD_ID = :prod_id ");
//		if (!StringUtils.isBlank(inputVO.getProd_name()))
//			sql.append("and C.PROD_NAME = :prod_name ");
//		//信用卡篩選
//		
//		
//		
//		
//		queryCondition.setQueryString(sql.toString());
//		
//		// where 2
//		//客戶篩選
//		if (!StringUtils.isBlank(inputVO.getBra_nbr()))
//			queryCondition.setObject("bra_nbr", inputVO.getBra_nbr());
//		if (!StringUtils.isBlank(inputVO.getCountry_nbr()))
//			queryCondition.setObject("country_nbr", inputVO.getCountry_nbr());
//		if (!StringUtils.isBlank(inputVO.getGender()))
//			queryCondition.setObject("gender", inputVO.getGender());
//		if (!StringUtils.isBlank(inputVO.getMarriage()))
//			queryCondition.setObject("marriage", inputVO.getMarriage());
//		if (!StringUtils.isBlank(inputVO.getEducation()))
//			queryCondition.setObject("education", inputVO.getEducation());
//		if (!StringUtils.isBlank(inputVO.getChild()))
//			queryCondition.setObject("child", inputVO.getChild());
//		if (!StringUtils.isBlank(inputVO.getVip_degree()))
//			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
//		if (!StringUtils.isBlank(inputVO.getAo_code()))
//			queryCondition.setObject("ao_code", inputVO.getAo_code());
//		if (!StringUtils.isBlank(inputVO.getCust_risk_atr()))
//			queryCondition.setObject("cust_risk_atr", inputVO.getCust_risk_atr());
//		if (!StringUtils.isBlank(inputVO.getSal_company()))
//			queryCondition.setObject("sal_company", inputVO.getSal_company());
//		if (!StringUtils.isBlank(inputVO.getCo_acct()))
//			queryCondition.setObject("co_acct", inputVO.getCo_acct());
//		if (!StringUtils.isBlank(inputVO.getComplain()))
//			queryCondition.setObject("complain", inputVO.getComplain());
//		if (!StringUtils.isBlank(inputVO.getEbank()))
//			queryCondition.setObject("ebank", inputVO.getEbank());
//		if (!StringUtils.isBlank(inputVO.getVoice()))
//			queryCondition.setObject("voice", inputVO.getVoice());
//		if (!StringUtils.isBlank(inputVO.getApp()))
//			queryCondition.setObject("app", inputVO.getApp());
//		if (!StringUtils.isBlank(inputVO.getInvestor()))
//			queryCondition.setObject("investor", inputVO.getInvestor());
//		if (!StringUtils.isBlank(inputVO.getRec()))
//			queryCondition.setObject("rec", inputVO.getRec());
//		if (inputVO.getBirth_sDate() != null)
//			queryCondition.setObject("start", new Timestamp(inputVO.getBirth_sDate().getTime()));
//		if (inputVO.getBirth_eDate() != null)
//			queryCondition.setObject("end", new Timestamp(inputVO.getBirth_eDate().getTime()));
//		//商品篩選
//		if (!StringUtils.isBlank(inputVO.getProd_id()))
//			queryCondition.setObject("prod_id", inputVO.getProd_id());
//		if (!StringUtils.isBlank(inputVO.getCrcy_type()))
//			queryCondition.setObject("crcy_type", inputVO.getCrcy_type());
//		if (!StringUtils.isBlank(inputVO.getProd_name()))
//			queryCondition.setObject("prod_name", inputVO.getProd_name());
//		//信用卡篩選
//		
//		
//		
//		
//		ResultIF list = dam.executePaging(queryCondition, inputVO
//				.getCurrentPageIndex() + 1, inputVO.getPageCount());
//		int totalPage_i = list.getTotalPage();
//		int totalRecord_i = list.getTotalRecord();
//		return_VO.setResultList(list);
//		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//		return_VO.setTotalPage(totalPage_i);// 總頁次
//		return_VO.setTotalRecord(totalRecord_i);// 總筆數
//		this.sendRtnObject(return_VO);
//	}
	
	
}
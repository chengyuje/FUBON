package com.systex.jbranch.app.server.fps.prd111;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.prd110.PRD110InputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110OutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703Dyna;
import com.systex.jbranch.app.server.fps.sot703.SOT703InputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703OutputVO;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd111
 * 動態鎖利適配
 */
@Component("prd111")
@Scope("request")
public class PRD111 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD111.class);
	
		
	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {
		PRD111InputVO inputVO = (PRD111InputVO) body;
		PRD111OutputVO outputVO = new PRD111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "NF", "F3")) ? false : true;

		outputVO.setWarningMsg("");
		outputVO.setErrorMsg("");

		boolean isFitnessOK = false;
		if (isFitness) {
			// 1.適配，由商品查詢取得，邏輯需一致
			PRD110OutputVO prdOutputVO = new PRD110OutputVO();
			PRD110InputVO prdInputVO = new PRD110InputVO();
			prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
			prdInputVO.setType("4");
			prdInputVO.setFund_id(inputVO.getProdId());
			prdInputVO.setTrustTS("S");
			//動態鎖利
			prdInputVO.setFromSOTProdYN("Y");
			prdInputVO.setFromPRD111YN("Y");
			prdInputVO.setDynamicType(inputVO.getDynamicType());
			if(StringUtils.equals("C", inputVO.getDynamicType())) {
				//子基金須為母基金同系列商品
				prdInputVO.setSameSerialYN("Y"); 
				prdInputVO.setSameSerialProdId(inputVO.getProdIdM());
				prdInputVO.setDynamicProdCurrM(inputVO.getDynamicProdCurrM());
			}

			PRD110 prd110 = (PRD110) PlatformContext.getBean("prd110");
			prdOutputVO = prd110.inquire(prdInputVO);

			if (CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
				String warningMsg = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
				String errId = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("errorID");

				if (StringUtils.isBlank(errId)) {
					isFitnessOK = true;
				}

				outputVO.setWarningMsg(warningMsg);
				outputVO.setErrorMsg(errId);
			}
		} else {
			logger.debug("SOT.FITNESS_YN 不進行適配 ");
			isFitnessOK = true;
		}

		// 2.查詢商品主檔
		if (isFitnessOK) { // 適配條件符合
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT f.*, i.FUS20, i.FUS40 ");
			sb.append("FROM TBPRD_FUND f ");
			sb.append("LEFT JOIN TBPRD_FUNDINFO i on (f.PRD_ID=i.PRD_ID) ");
			sb.append("WHERE f.PRD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getProdId());
			queryCondition.setQueryString(sb.toString());

			outputVO.setProdDTL(dam.exeQuery(queryCondition));
		}

		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 適配資料儲存&印表
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD111InputVO inputVO = (PRD111InputVO) body;
		PRD111OutputVO outputVO = new PRD111OutputVO();		
		
		//適配儲存
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		PRDFitInputVO fitVO = new PRDFitInputVO(); 
		fitVO = getFitInputVO(inputVO, "M");//母基金
		String msg = sot712.saveFitInfo(fitVO);
		if(StringUtils.isNotBlank(msg)) {
			throw new JBranchException(msg);
		}
		
		fitVO = getFitInputVO(inputVO, "C1"); //子基金1
		msg = sot712.saveFitInfo(fitVO);
		if(StringUtils.isNotBlank(msg)) {
			throw new JBranchException(msg);
		}
		
		if(StringUtils.isNotBlank(inputVO.getProdIdC2())) {
			fitVO = getFitInputVO(inputVO, "C2"); //子基金2
			msg = sot712.saveFitInfo(fitVO);
			if(StringUtils.isNotBlank(msg)) {
				throw new JBranchException(msg);
			}
		}
		
		if(StringUtils.isNotBlank(inputVO.getProdIdC3())) {
			fitVO = getFitInputVO(inputVO, "C3"); //子基金3
			msg = sot712.saveFitInfo(fitVO);
			if(StringUtils.isNotBlank(msg)) {
				throw new JBranchException(msg);
			}
		}
		
		this.sendRtnObject(outputVO);
	}
	
	private PRDFitInputVO getFitInputVO(PRD111InputVO inputVO, String type) {
		PRDFitInputVO fitVO = new PRDFitInputVO(); 
		fitVO.setCaseCode(2); //case2 適配
		fitVO.setCustId(inputVO.getCustID());
		fitVO.setPrdType("MFD");
		fitVO.setPrdName(inputVO.getProdName());
		fitVO.setIsPrintSOT819(inputVO.getIsPrintSOT819()); //印貸款風險預告書
		fitVO.setHnwcBuy("N"); //境外私募基金註記
		fitVO.setHmshacrDataVO(null); //集中度資訊，動態鎖利沒有私募基金
		
		if(type.matches("M")) { //母基金
			fitVO.setPrdId(inputVO.getProdId());
			fitVO.setRiskLevel(inputVO.getProdRiskLv());
			fitVO.setCurrency(inputVO.getProdCurr());
		} else if(type.matches("C1")) { //子基金1
			fitVO.setPrdId(inputVO.getProdIdC1());
			fitVO.setRiskLevel(inputVO.getProdRiskLvC1());
			fitVO.setCurrency(inputVO.getProdCurrC1());
		} else if(type.matches("C2")) { //子基金2
			fitVO.setPrdId(inputVO.getProdIdC2());
			fitVO.setRiskLevel(inputVO.getProdRiskLvC2());
			fitVO.setCurrency(inputVO.getProdCurrC2());
		} else if(type.matches("C3")) { //子基金3
			fitVO.setPrdId(inputVO.getProdIdC3());
			fitVO.setRiskLevel(inputVO.getProdRiskLvC3());
			fitVO.setCurrency(inputVO.getProdCurrC3());
		}
		
		return fitVO;
	}
	
}
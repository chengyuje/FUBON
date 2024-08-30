package com.systex.jbranch.app.server.fps.crm814;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.EB202674Service;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.service.HD00070000Service;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202674.EB202674OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202674.EB202674OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.hd00070000.HD00070000OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.hd00070000.HD00070000OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 一本萬利
 *
 * @author walalala
 * @date 2016/12/06
 */
@Component("crm814")
@Scope("request")
public class CRM814 extends EsbUtil {
	@Autowired
	private CBSService cbsService;

	@Autowired
	private EB202674Service eb202674Service;
	
	@Autowired
	private HD00070000Service hd00070000Service;

	@Autowired
	private SOT701 sot701;
	
	private DataAccessManager dam = null;

	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	

	/**
	 * 查詢客戶一本萬利資訊 // 01:帳務交易 30:凍結查詢 31:圈存/設質查詢 39:止付查詢 40:定存明細查詢 HD00070000
	 * TXTYPE 1-全部(台活+外活) 2-台幣活期 3-台幣定期 4-外幣活期 5-外幣定期
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM814InputVO inputVO = (CRM814InputVO) body;
		List<EB202674OutputDetailsVO> result = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		String chequeAcctSecondPartStartDate = null;
		String livingAcctSecondPartStartDate = null;
		String timedTWDAcctSecondPartStartDate = null;
		String timedNotTWDAcctSecondPartStartDate = null;
        
		// 查活存or支存HD00070000電文
		List<ESBUtilOutputVO> vos = hd00070000Service.search(inputVO); 

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			HD00070000OutputVO outputVO = esbUtilOutputVO.getHd00070000OutputVO();
			if (null != outputVO) {
				if (isNotEmpty(outputVO.getDetails())) {
					for (HD00070000OutputDetailsVO detail : outputVO.getDetails()) {
						livingAcctSecondPartStartDate = detail.getTXDATE();
						EB202674OutputDetailsVO trueDetail = new EB202674OutputDetailsVO();
						dealLivingAcctData(detail, trueDetail);
						result.add(trueDetail);
					}
				}
			}
		}
		
		String livingFinalUsedDate = dealFinalUsedDate(livingAcctSecondPartStartDate, chequeAcctSecondPartStartDate);
		//查活存數字電文 
		EB202674OutputVO livingAcctData = eb202674Service.search(inputVO.getAccount(), livingFinalUsedDate != null ? changeDatePattern(livingFinalUsedDate) : sdf.format(inputVO.getsCreDate()), sdf.format(inputVO.geteCreDate()), "01", livingFinalUsedDate);
		if (null != livingAcctData) {
			if (isNotEmpty(livingAcctData.getDetails()))
				for (EB202674OutputDetailsVO detail : livingAcctData.getDetails()) {
					detail.setTX_TYPE("L");
					formatNumber(detail);
					formatDate(detail);
					result.add(detail);
				}
		}
		
//      400主機沒寫好定期電文的部分 暫時不用
//		// 查台幣定期HD00070000電文  
//		List<ESBUtilOutputVO> vos2 = sendHD00070000(inputVO, "3");
//
//		for (ESBUtilOutputVO esbUtilOutputVO : vos2) {
//			HD00070000OutputVO outputVO = esbUtilOutputVO.getHd00070000OutputVO();
//			if (null != outputVO) {
//				if (isNotEmpty(outputVO.getDetails())) {
//					for (HD00070000OutputDetailsVO detail : outputVO.getDetails()) {
//						timedTWDAcctSecondPartStartDate = detail.getTXDATE();
//						EB202674OutputDetailsVO trueDetail = new EB202674OutputDetailsVO();
//						dealTimedAcctData(detail, trueDetail);
//						result.add(trueDetail);
//					}
//				}
//			}
//		}
//
//		// 查外幣定期HD00070000電文
//		List<ESBUtilOutputVO> vos3 = sendHD00070000(inputVO, "5");
//
//		for (ESBUtilOutputVO esbUtilOutputVO : vos3) {
//			HD00070000OutputVO outputVO = esbUtilOutputVO.getHd00070000OutputVO();
//			if (null != outputVO) {
//				if (isNotEmpty(outputVO.getDetails())) {
//					for (HD00070000OutputDetailsVO detail : outputVO.getDetails()) {
//						timedNotTWDAcctSecondPartStartDate = detail.getTXDATE();
//						EB202674OutputDetailsVO trueDetail = new EB202674OutputDetailsVO();
//						dealTimedAcctData(detail, trueDetail);
//						result.add(trueDetail);
//					}
//				}
//			}
//		}
		String finalUsedDate = dealFinalUsedDate(timedTWDAcctSecondPartStartDate, timedNotTWDAcctSecondPartStartDate);

		// 查定存數字電文
		EB202674OutputVO timedAcctData = eb202674Service.search(inputVO.getAccount(), finalUsedDate != null ? changeDatePattern(finalUsedDate) : sdf.format(inputVO.getsCreDate()), sdf.format(inputVO.geteCreDate()), "40", finalUsedDate);
		if (null != timedAcctData) {
			if (isNotEmpty(timedAcctData.getDetails()))
				for (EB202674OutputDetailsVO detail : timedAcctData.getDetails()) {
					detail.setTX_TYPE("T");
					formatNumber(detail);
					formatDate(detail);
					result.add(detail);
				}
		}
		
		dealBranch(result);

		this.sendRtnObject(result); 
	}

	private void dealBranch(List<EB202674OutputDetailsVO> result) throws DAOException, JBranchException {
		if(result.size() < 1) {
			return;
		}
		dam = this.getDataAccessManager();
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("select BANKID, REPLACE(TRIM(SNAME),'　', '') AS SNAME from TBSYS_BANK_BRANCH_SG WHERE BRANCHID = :BRANCHID ");
		qc.setObject("BRANCHID", "0000");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> outsideBankList = dam.exeQuery(qc);
		
		QueryConditionIF qc2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb2 = new StringBuilder();
		sb2.append("select DEPT_ID, DEPT_NAME from TBORG_DEFN where length(DEPT_ID) = :LENGTH ");
		qc2.setObject("LENGTH", 3);
		qc2.setQueryString(sb2.toString());
		List<Map<String, Object>> branchList = dam.exeQuery(qc2);
		
		String branch = "000";
		String bank = "000";
		for(EB202674OutputDetailsVO detail : result){
			Boolean isMatch = false;
			String rmk = detail.getRMK();
			
			if((null == rmk) || StringUtils.isBlank(rmk)) {
				detail.setRMK("");
				continue;
			}

			if(StringUtils.equals(rmk.substring(0,3), "012")){
				if(rmk.length() > 6){
					branch = rmk.substring(5,8);
				} else {
					branch = rmk.substring(3,6);
				}
				for(Map branchMap : branchList){
					if(StringUtils.equals((String) branchMap.get("DEPT_ID"), branch)){
						detail.setRMK(branchMap.get("DEPT_NAME").toString());
						isMatch = true;
						continue;
					}
				}
			} else {
				bank = rmk.substring(0,3);
				for(Map bankMap : outsideBankList){
					if(StringUtils.equals((String) bankMap.get("BANKID"), bank)){
						detail.setRMK(bankMap.get("SNAME").toString());
						isMatch = true;
						continue;
					}
				}
			}
			if(!isMatch) {
				detail.setRMK("");
			}
		}
		
	}

	/**
	 * 兩個交易日來比較，看哪個比較後面後續給數字電文當起始日
	 * 
	 * @param timedTWDAcctSecondPartStartDate
	 * @param timedNotTWDAcctSecondPartStartDate
	 * @return
	 * @throws ParseException 
	 */
	private String dealFinalUsedDate(String first, String second) throws ParseException {
		
		if(StringUtils.isBlank(first) && StringUtils.isBlank(second)){
			return null;
		}
		if(StringUtils.isBlank(first) && !StringUtils.isBlank(second)){
			return second;
		}
		if(!StringUtils.isBlank(first) && StringUtils.isBlank(second)){
			return first;
		}
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
		Date firstDate = sdf2.parse(first);
		Date secondDate = sdf2.parse(second);
		
		
    	if(firstDate.after(secondDate)){
    		return first;
    	}else{
    		return second;
    	}
	}

	/**
	 * 定期處理
	 * 
	 * @param detail
	 * @param trueDetail
	 */
	private void dealTimedAcctData(HD00070000OutputDetailsVO detail, EB202674OutputDetailsVO trueDetail) {
		trueDetail.setACT_DATE(cbsService.changeDateView(detail.getACT_DATE(),"1"));
		trueDetail.setTX_TYPE("T");
		trueDetail.setCUR(getCurr(detail.getACT_BAL()));
		trueDetail.setTX_DATE(cbsService.changeDateView(detail.getTXDATE(),"1"));
		trueDetail.setINT_TYPE(detail.getMEMO().replaceAll(" ", "").replaceAll("　", ""));
		trueDetail.setAMT1(deleteNotNumber(detail.getACT_BAL()));
		trueDetail.setEND_AGR(detail.getACT_BRH().replaceAll(" ", "").replaceAll("　", ""));
	}

	/**
	 * 活期 支存處理
	 * 
	 * @param detail
	 * @param trueDetail
	 */
	private void dealLivingAcctData(HD00070000OutputDetailsVO detail, EB202674OutputDetailsVO trueDetail) {
		trueDetail.setTX_TYPE("L");
		trueDetail.setACT_DATE(cbsService.changeDateView(detail.getACT_DATE(),"1"));
		trueDetail.setTX_DATE(cbsService.changeDateView(detail.getTXDATE(),"1"));
		trueDetail.setMEMO(detail.getMEMO().replaceAll(" ", "").replaceAll("　", ""));
		trueDetail.setCUR(getCurr(detail.getACT_BAL()));
		trueDetail.setPB_BAL(deleteNotNumber(detail.getACT_BAL()));
		
		if (detail.getDRCR().equals("2")) {
			trueDetail.setTRUST_NO("C");
			trueDetail.setCR_TXT(deleteNotNumber(detail.getCREDIT_AMT()));
			trueDetail.setDR_TXT(detail.getDEBIT_AMT().trim());		
		} else if (detail.getDRCR().equals("1")) {
			trueDetail.setTRUST_NO("D");
			trueDetail.setCR_TXT(detail.getCREDIT_AMT().trim());
			trueDetail.setDR_TXT(deleteNotNumber(detail.getDEBIT_AMT()));
		}
		
		trueDetail.setRMK(detail.getACT_BRH());
	}

	/**
	 * 清除字串非數字的值
	 * 
	 * @param act_BAL
	 * @return
	 */
	private String deleteNotNumber(String act_BAL) {
		String regEx = "[^0-9.-]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matchar = pattern.matcher(act_BAL);

		return matchar.replaceAll("").trim();
	}
	
	/**
	 * 取幣別
	 * 
	 * @param act_BAL
	 * @return
	 */
	private String getCurr(String act_BAL) {
		String regEx = "[0-9.,-]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matchar = pattern.matcher(act_BAL);

		return matchar.replaceAll("").trim().equals("$") ? "TWD" : matchar.replaceAll("").trim() ;
	}

	/**
	 * 日期處理
	 **/
	private void formatDate(EB202674OutputDetailsVO detail) {
		if (detail.getTX_DATE() != null)
			detail.setTX_DATE(cbsService.changeDateView(detail.getTX_DATE(),"2"));
		if (detail.getACT_DATE() != null)
			detail.setACT_DATE(cbsService.changeDateView(detail.getACT_DATE(),"2"));
		if (detail.getBK_DATE() != null)
			detail.setBK_DATE(cbsService.changeDateView(detail.getBK_DATE(),"2"));
		if (detail.getDUE_DATE() != null)
			detail.setDUE_DATE(cbsService.changeDateView(detail.getDUE_DATE(),"2"));
	}

	/**
	 * 三個金額做格式處理
	 **/
	private void formatNumber(EB202674OutputDetailsVO detail) {
		if(detail.getTRUST_NO().equals("D")){
			if (detail.getDR_TXT() != null && StringUtils.isNotBlank(deleteNotNumber(detail.getDR_TXT())))
				detail.setDR_TXT(cbsService.amountFormat(detail.getDR_TXT()));	
		}else if(detail.getTRUST_NO().equals("C")){
			if (detail.getCR_TXT() != null && StringUtils.isNotBlank(deleteNotNumber(detail.getCR_TXT())))
				detail.setCR_TXT(cbsService.amountFormat(detail.getCR_TXT()));		
		}
		
		if (detail.getPB_BAL() != null)
			detail.setPB_BAL(cbsService.amountFormat(detail.getPB_BAL()));		
		
	}

	/**
	 * for HD00070000電文的日期轉換到000454-032081格式 yyyyMMdd >> ddMMyyyy
	 * 並將日期加一天讓後續打數字電文的日期區間不重疊
	 * 
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	private String changeDatePattern(String date) throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
			
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(date));
		cal.add(cal.DATE, 1);
		String returnDate = sdf2.format(cal.getTime());
		
		return returnDate;
	}
	/**
	 * 確認是否為在職員工，若為在職員工不給查明細
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void checkOnJob(Object body, IPrimitiveMap header) throws Exception {
		CRM814InputVO inputVO = (CRM814InputVO) body;
		
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) as COUNT from TBORG_MEMBER WHERE SERVICE_FLAG = :SERVICE_FLAG ");
		sb.append("AND CUST_ID = :CUST_ID");
		qc.setObject("SERVICE_FLAG", "A");
		qc.setObject("CUST_ID", inputVO.getCust_id().toUpperCase());
		qc.setQueryString(sb.toString());
		
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		BigDecimal bd = (BigDecimal) resultList.get(0).get("COUNT");
		int count = bd.intValue();
		boolean result = false;
		if(count < 1){
			result = false;
			this.sendRtnObject(result); 
		} else {
			result = true;
			this.sendRtnObject(result); 
		}
	}
}
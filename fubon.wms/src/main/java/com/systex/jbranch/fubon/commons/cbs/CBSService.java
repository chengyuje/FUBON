package com.systex.jbranch.fubon.commons.cbs;

import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.entity.CBSIDPattern;
import com.systex.jbranch.fubon.commons.cbs.inf.IDPattern;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei003.NFEI003OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfei003.NFEI003OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.trim;
import static org.eclipse.birt.report.model.api.util.StringUtil.isBlank;

/**
 * CBS 相關服務
 *
 * @author Eli
 * @date 20180223
 */
@Service
public class CBSService {
	/** 11 12 13 19為自然人 其他為法人 **/
	public static final String[] NATURAL_PERSON_TYPE = { "11", "12", "13", "19" }; // 法人是
																					// 2x
																					// 31
	private DataAccessManager dam;
	private QueryConditionIF condition;
	private Logger logger = LoggerFactory.getLogger(CBSService.class);

	/**
	 * return ID TYPE CODE
	 *
	 * @param id
	 * @return type code or null(if not match)
	 */
	public String getCBSIDCode(String id) {
		for (IDPattern reg : CBSIDPattern.values())
			if (reg.match(id))
				return reg.getCode();
		return null;
	}

	/**
	 * 金錢格式處理，小數預設第三位
	 *
	 * @param amtStr
	 * @return
	 */
	public String amountFormat(String amtStr) {
		return amountFormat(amtStr, 3, null);
	}

	/**
	 * 金錢格式處理
	 *
	 * @param amtStr
	 * @param digit
	 * @return
	 */
	public String amountFormat(String amtStr, int digit) {
		return amountFormat(amtStr, digit, null);
	}

	/**
	 * 金錢格式處理
	 *
	 * @param amtStr
	 * @param digit
	 *            小數點位置
	 * @param format
	 *            Ex: "#,##0.###"
	 * @return
	 */
	private static String amountFormat(String amtStr, int digit, String format) {
		String amt = defaultString(amtStr, "0")
		/** 去除加號與底線 **/
		.replaceAll("[+_]", "");

		if (amt.indexOf(".") == -1 && digit > 0)
			amt = new BigDecimal(amt).divide(new BigDecimal(Math.pow(10, digit))).setScale(4, BigDecimal.ROUND_HALF_UP).toString();

		if (isBlank(format))
			return amt;
		return new DecimalFormat(format).format(Double.parseDouble(amt));
	}

	/**
	 * opt 1 = yyyyMMdd >> yyyy/MM/dd 2 = ddMMyyyy >> yyyy/MM/dd 3 = MMddyyyy >>
	 * yyyy/MM/dd
	 *
	 * @param date
	 * @return yyyyMMdd 改成 yyyy/MM/dd
	 */
	public String changeDateView(String date, String opt) {
		switch (opt) {
		case "1":
			return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
		case "2":
			return date.substring(4, 8) + "/" + date.substring(2, 4) + "/" + date.substring(0, 2);
		case "3":
			return date.substring(4, 8) + "/" + date.substring(0, 2) + "/" + date.substring(2, 4);
		}
		return null;
	}

	/**
	 * @param data
	 *            適用ddmmyyyy計算年齡
	 * @return
	 */
	public String changeToAge(String data) {
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		int sysyyyy = Integer.parseInt(ft.format(date).toString().substring(0, 4));
		int sysmmdd = Integer.parseInt(ft.format(date).toString().substring(4, 6) + ft.format(date).toString().substring(6, 8));
		int yyyy = Integer.parseInt(data.substring(4, 8));
		int mmdd = Integer.parseInt(data.substring(2, 4) + data.substring(0, 2));

		if (sysmmdd - mmdd >= 0) {
			return String.valueOf(sysyyyy - yyyy);
		} else {
			return String.valueOf(sysyyyy - yyyy - 1);
		}
	}

	/**
	 * @param acct
	 *            確認帳號長度並修改為14碼
	 * @return
	 */
	public String checkAcctLength(String acct) {
		if (acct.length() > 14) {
			acct = acct.substring(acct.length() - 14);
		} else if (acct.length() < 14 & acct.length() >= 12) {
			String include = "";
			for (int i = 14; i > acct.length(); i--) {
				include = include + "0";
			}
			acct = include + acct;
		}
		return acct;
	}

	/**
	 * @param bra
	 *            確認分行長度並修改為3碼
	 * @return
	 */
	public String checkBraLength(String bra) {
		if (bra.length() == 3) {
			return bra;
		}
		if (bra.length() == 5) {
			return bra.substring(2, 5);
		}

		return bra;
	}

	/**
	 * @param id
	 *            判斷是否為法人 11 12 13 19為自然人 其他為法人(伯瑞建議)
	 * @return
	 */
	public boolean checkJuristicPerson(String id) {
		String[] checklist = NATURAL_PERSON_TYPE;
		String type = getCBSIDCode(id);
		if (asList(checklist).contains(type)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判斷客戶 ID Type 是否為法人 Type
	 *
	 * @param type
	 *            客戶 ID Type
	 */
	public boolean isNaturalPerson(String type) {
		return asList(NATURAL_PERSON_TYPE).contains(type);
	}

	/**
	 *
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 *             .23 CBS測試環境測試用，FOR那邊系統的模擬日 若對應TABLE欄位為空值 則會帶回原本new Date()
	 *             20200213 根據需求 應該要能顯示小時分鐘秒 現在回傳的String是 yyyyMMddHHmmss
	 */
	public String getCBSTestDate() throws DAOException, JBranchException {
		String workdate = "";

		StringBuffer sql = new StringBuffer("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'OTH001' and PARAM_CODE = 'ORDER_DATE' ");
		getDam();
		getCondition();
		condition.setQueryString(sql.toString());
		List<Map> list = dam.exeQuery(condition);
		workdate = (String) list.get(0).get("PARAM_NAME");
		if (isSimulatedDate(workdate)) {
			return workdate + new SimpleDateFormat("HHmmss").format(new Date()).toString();
		} else {
			return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		}

	}

	/** 如果模擬日有開啟，則使用模擬日 (for 測試環境)  **/
	public boolean isEnabledSimulatedDate() throws JBranchException {
		getDam();
		List<Map<String, String>> result = Manager.manage(this.dam)
				.append("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'OTH001' and PARAM_CODE = 'ORDER_DATE' ")
				.query();
		if (CollectionUtils.isEmpty(result)) return false;
		return isSimulatedDate(result.get(0).get("PARAM_NAME"));
	}

	private boolean isSimulatedDate(String date) {
		return StringUtils.defaultString(date).length() == 8;
	}

	/**
	 * @param date
	 *
	 * @return case 1 : yyyymmdd 改成 ddmmyyyy case 2 : ddmmyyyy 改成 yyyymmdd
	 */
	public String changeDate(String date, int case1) {
		String newDate = "";
		switch (case1) {

		case 1:
			newDate = date.substring(6, 8) + date.substring(4, 6) + date.substring(0, 4);
			return newDate;
		case 2:
			newDate = date.substring(4, 8) + date.substring(2, 4) + date.substring(0, 2);
			return newDate;
		}
		return newDate;
	}

	public int calDate(Date date1, Date date2) {

		return (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
	}

	/**
	 * 下單交易400主機(非客戶) 上行電文欄位格式 9(08) 要用民國年,如今日: 2016/10/18 => 01051018 EX:目前
	 * NFBRN1Z.TRADE_DATE 和EFF_DATE
	 *
	 * @param date
	 * @throws Exception
	 */
	public String toChineseYearMMdd(String date) throws Exception {
		if (date != null) {

			int chYear = Integer.valueOf(date.substring(0, 4)) - 1911;
			String chyear = (chYear < 1000) ? "0" + String.valueOf(chYear) : String.valueOf(chYear);
			return chyear + date.substring(4, 6) + date.substring(6, 8);
		} else {
			return null;
		}
	}

	/**
	 * 檢查OBU 20200227 根據 CBS_OBU 存款帳號原則.docx做邏輯修正
	 *
	 * @param acct
	 * @return
	 */
	public String checkOBU(String acct) {

		if (StringUtils.isBlank(acct)) {
			return "D";
		}

		String[] identifyNewOBUAcctFirstStep = { "80", "81", "82", "83", "84", "85", "86", "87", "88" };
		String[] identifyNewOBUAcctSecondStep = { "8", "9" };

		acct = checkAcctLength(acct);
		if (acct.substring(0, 1).equals("0")) {

			if (acct.substring(0, 5).equals("00560")) {
				return "O";
			}

		} else if (acct.substring(0, 1).equals("8")) {
			// 第一步判斷前兩碼
			if (asList(identifyNewOBUAcctFirstStep).contains(acct.substring(0, 2))) {
				// 第二步判斷第三碼
				if (asList(identifyNewOBUAcctSecondStep).contains(acct.substring(2, 3))) {
					return "O";
				}
			}

		} else {
			return "D";
		}
		return "D";
	}

	/**
	 * 取得 {@link DataAccessManager} 物件
	 *
	 * @throws JBranchException
	 * @throws DAOException
	 */
	private void getDam() throws DAOException, JBranchException {
		dam = new DataAccessManager();
	}

	/** 取得 {@link QueryConditionIF} 物件 */
	private void getCondition() throws DAOException, JBranchException {
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}

	/** 是否為就學貸款 **/
	public boolean isStudentLoan(String acctType) {
		return "5200".equals(acctType);
	}

	/** 是否為留學貸款 **/
	public boolean isStudentForeignLoan(String acctType) {
		return "5201".equals(acctType);
	}

	/** 是否為分期型房貸 **/
	public boolean isInstallment(String acctType) {
		return defaultString(trim(acctType)).matches("5000|5003");
	}

	/** 是否為循環型房貸 **/
	public boolean isCirculation(String acctType) {
		return isCreditLoan1(acctType) || isHomeLoan(acctType) || isCreditLoan(acctType);
	}

	/** 循環型額度式房貸 = 5001 5002
	 * #0512 加入5101 5102 **/
	public static final String[] HOME_CYCLE_LOAN = new String[] { "5001", "5002", "5101", "5102"};
	/** 循環型回復式房貸 = 5004 **/
	public static final String HOME_REC_LOAN = "5004";

	/** 綜存質借 = 5301 **/
	public static final String CREDIT_LOAN_1 = "5301";
	/** 綜存質借組 = 5301 5302 **/
	public static final String[] checkCreditLON = new String[] { CREDIT_LOAN_1, "5302" };

	/** 是否為綜存質借 1 **/
	public boolean isCreditLoan1(String acctType) {
		return CREDIT_LOAN_1.equals(acctType);
	}

	/** 是否被包含在「綜存質借組」 **/
	public boolean isCreditLoan(String acctType) {
		return asList(checkCreditLON).contains(acctType);
	}

	/** 是否為循環型額度式房貸 **/
	public boolean isHomeCycleLoan(String acctType) {
		return asList(HOME_CYCLE_LOAN).contains(acctType);
	}

	/** 是否為循環型回復式房貸 **/
	public boolean isHomeRecLoan(String acctType) {
		return HOME_REC_LOAN.equals(acctType);
	}

	/** 是否為房貸（循環型額度、循環型回復） **/
	public boolean isHomeLoan(String acctType) {
		return isHomeCycleLoan(acctType) || isHomeRecLoan(acctType);
	}

	/** 學期別 Pattern **/
	public static final String YEAR_TERM = "[0-9]{4}";

	/** 判斷是否符合學期別格式 **/
	public boolean fitYearTermPattern(String yearTerm) {
		return yearTerm.matches(YEAR_TERM);
	}

	/** 信用貸款 acctType 為 51XX
	 * #0512  改為5100**/
	public static final String CREDIT_PATTERN = "5100";

	/** 是否為 信用貸款 **/
	public boolean isCredit(String acctType) {
		return acctType.matches(CREDIT_PATTERN);
	}

	/** 是否為存單質借 5300 或是信託質借 55XX **/
	public boolean isMortgage(String acctType) {
		return isMortgage1(acctType) || isMortgage2(acctType);
	}

	/** 是否為 存單質借 **/
	public boolean isMortgage1(String acctType) {
		return "6800".equals(acctType);
	}

	/** 信託質借 **/
	public static final String MORTGAGE_PATTERN = "55\\d{2}";

	/** 是否為信託質借 **/
	public boolean isMortgage2(String acctType) {
		return acctType.matches(MORTGAGE_PATTERN);
	}

	/**
	 * 取得信託帳號的分行 20200305_CBS_彥德_期間議價E008錯誤_分行送錯
	 * 不管是新舊帳號，統一打NFEI003電文的AR103欄位(分行)
	 *
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	public String getAcctBra(String custID, String trustAcct) throws Exception {
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		NFEI003OutputVO NFEI003OutputVO = new NFEI003OutputVO();

		if (StringUtils.isBlank(trustAcct)) {
			return null;
		}

		List<ESBUtilOutputVO> vos3 = sot701.getTrustAcct(custID);
		try {
			for (ESBUtilOutputVO esbUtilOutputVO : vos3) {
				NFEI003OutputVO = esbUtilOutputVO.getNfei003OutputVO();
				for (NFEI003OutputDetailsVO detail : NFEI003OutputVO.getDetails()) {
					if (trustAcct.trim().equals(detail.getAR101().trim())) {
						return detail.getAR103().trim();
					}
				}
			}
		} catch (Exception e) {
			logger.info("NFEI003無對應信託帳號");
		}
		return "000";
	}

	/**
	 * 判斷新舊帳號是否為168帳號
	 *
	 * @param acct
	 * @return
	 */
	public boolean is168(String acct) {
		if (acct.length() != 14) {
			acct = checkAcctLength(acct);
		}
		if (acct.length() == 14) {
			String flag = acct.substring(0, 1);

			if ((flag.equals("0") && acct.substring(5, 8).equals("168")) // 舊帳號判別
					|| (flag.equals("8") && acct.substring(0, 4).equals("8168")) // 新帳號判別
			)
				return true;
		}
		return false;
	}

	/** 如果模擬日有開啟， 更新 TBSOT_TRADE_MAIN LastUpdate 為 模擬日以讓印出表單之日期顯示為模擬日期 (For CBS 測試用) **/
	public void setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(String seq) throws JBranchException {
		if (isEnabledSimulatedDate()) {
			getDam();
			Manager.manage(this.dam)
					.append("update TBSOT_TRADE_MAIN ")
					.append("set LASTUPDATE = to_date(:fake, 'yyyymmddhh24miss') ")
					.append("where TRADE_SEQ = :seq ")
					.put("fake", getCBSTestDate())
					.put("seq", seq)
					.update();
		}
	}

    /**
     * before右補text到length的長度
     * @param before
     * @param length
     * @param add
     * @return
     */
	public String padRight(String before, int length, String text){
		String add = "";
		for(int i = before.length(); i < length; i++ ){
			add = add + text;
		}
		return before + add;
	}
	
    /**
     * before左補text到length的長度
     * @param before
     * @param length
     * @param add
     * @return
     */
	public String padLeft(String before, int length, String text){
		String add = "";
		for(int i = before.length(); i < length; i++ ){
			add = add + text;
		}
		return add + before;
	}
}

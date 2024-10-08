package com.systex.jbranch.app.server.fps.iot190;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MENU
 * 
 * @author Kevin Hsu
 * @date 2016/09/29
 * @spec null
 */
@Component("iot190")
@Scope("request")
public class IOT190 extends FubonWmsBizLogic {

	public DataAccessManager dam = null;

	SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		initUUID();
		
		IOT190InputVO inputVO = (IOT190InputVO) body;
		IOT190OutputVO outputVO = new IOT190OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
//		try {
			sb.append("SELECT A.REG_TYPE, CASE WHEN A.REG_TYPE = '4' THEN A.REMARK_INS ELSE NULL END PROPOSER_ACCNBR, A.CREATETIME, A.BRANCH_AREA_ID, ");
			sb.append("       A.DELETE_DATE, A.DELETE_OPRID, A.AFT_SIGN_DATE, A.AFT_SIGN_OPRID, ");
			sb.append("       A.BEF_SIGN_OPRID, A.BEF_SIGN_DATE, A.CREATOR, A.INSPRD_NAME, A.OP_BATCH_OPRNAME, A.OP_BATCH_OPRID, ");
			sb.append("       A.INSURED_ID, A.DOC_KEYIN_DATE, A.PROD_PERIOD, A.PROD_NAME, A.TERMINATED_INC, A.BRANCH_NAME, A.BRANCH_NBR, ");
			sb.append("       A.PROPOSER_BIRTH, A.PROPOSER_NAME, A.CUST_ID, A.OTH_TYPE, A.POLICY_NO, A.INS_ID, A.OP_BATCH_NO, A.KEYIN_DATE , A.STATUS, A.RECRUIT_ID, A.RECRUIT_NAME, ");
			sb.append("       A.QC_PROPOSER_CHG, A.PREMATCH_SEQ, A.AML, A.PRECHECK, A.PREMIUM_USAGE, A.PAY_WAY, A.PAYER_ID, A.APPLY_DATE, A.LOAN_SOURCE_YN, A.RLT_BT_PROPAY, ");
			sb.append("       A.LOAN_CHK1_YN, A.LOAN_CHK2_YN, A.CD_CHK_YN, A.PREMIUM_TRANSSEQ, ");
			sb.append("       (CASE WHEN P.STATUS = '3' THEN 'Y' ELSE 'N' END) AS AUTH_YN, ");
			sb.append("		  A.COMPANY_NUM, E.CNAME AS INS_COM_NAME, CASE WHEN A.FB_COM_YN = 'Y' THEN '富壽' ELSE '非富壽' END AS INS_SOURCE, ");
			sb.append("		  (CASE WHEN A.FB_COM_YN = 'Y' THEN A.INS_RCV_OPRID ELSE A.NOT_FB_OP_NAME END) AS INS_RCV_OPRID, ");
			sb.append("		  (CASE WHEN A.FB_COM_YN = 'Y' THEN A.INS_RCV_DATE ELSE A.NOT_FB_BATCH_DATE END) AS INS_RCV_DATE, ");
			sb.append(" 	  A.OTH_FUND_PURPOSE_1, A.OTH_FUND_PURPOSE_2, A.OTH_FUND_PURPOSE_3, A.OTH_FUND_PURPOSE_4, A.OTH_FUND_PURPOSE_5, A.OTH_FUND_PURPOSE_6, ");
			sb.append(" 	  A.OTH_FUND_PURPOSE_RMK_1, A.OTH_FUND_PURPOSE_RMK_2 ");
			sb.append("FROM VWIOT_MAIN A ");
			sb.append("LEFT OUTER JOIN (SELECT PREMATCH_SEQ, STATUS FROM TBIOT_PREMATCH) P ON P.PREMATCH_SEQ = A.PREMATCH_SEQ ");
			sb.append("LEFT JOIN TBJSB_INS_PROD_COMPANY E ON E.SERIALNUM = A.COMPANY_NUM ");	
			sb.append("WHERE 1 = 1 ");
			sb.append("AND A.REG_TYPE IN ('3', '4') ");
			
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
				sb.append("AND EXISTS ( ");
				sb.append("  SELECT 1 ");
				sb.append("  FROM TBCRM_CUST_MAST CM ");
				sb.append("  INNER JOIN VWORG_EMP_UHRM_INFO U ON CM.AO_CODE = U.UHRM_CODE ");
				sb.append("  WHERE A.CUST_ID = CM.CUST_ID ");
				sb.append(") ");
			}
			
			if (!StringUtils.isBlank(inputVO.getBRANCH_AREA_ID())) {
				sb.append("AND BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBRANCH_AREA_ID());
			}
			
			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				sb.append("AND BRANCH_NBR = :BRANCH_NBRR ");
				condition.setObject("BRANCH_NBRR", inputVO.getBRANCH_NBR());
			}
			
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sb.append("AND CUST_ID = :CUST_IDD ");
				condition.setObject("CUST_IDD", inputVO.getCUST_ID().toUpperCase());
			}

			if (!StringUtils.isBlank(inputVO.getFXD_PRODNAME())) {
				sb.append("AND FXD_PRODNAME = :FXD_PRODNAMEE  ");
				condition.setObject("FXD_PRODNAMEE", inputVO.getFXD_PRODNAME());
			}

			if (!StringUtils.isBlank(inputVO.getINS_ID())) {
				sb.append("AND INS_ID = :INS_IDD ");
				condition.setObject("INS_IDD", inputVO.getINS_ID());
			}

			if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
				sb.append("AND INSPRD_ID = :INSPRD_IDD ");
				condition.setObject("INSPRD_IDD", inputVO.getINSPRD_ID());
			}

			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) {
				sb.append("AND INSURED_ID = :INSURED_IDD ");
				condition.setObject("INSURED_IDD", inputVO.getINSURED_ID());
			}

			if (!StringUtils.isBlank(inputVO.getREG_TYPE())) {
				sb.append("AND REG_TYPE = :REG_TYPEE ");
				condition.setObject("REG_TYPEE", inputVO.getREG_TYPE());
			}

			if (!StringUtils.isBlank(inputVO.getSTATUS())) {
				sb.append("AND A.STATUS = :STATUSS ");
				condition.setObject("STATUSS", inputVO.getSTATUS());
			}

			if (inputVO.getKEYIN_DATE_B() != null && !inputVO.getKEYIN_DATE_B().equals("")) {
				sb.append("AND trunc(KEYIN_DATE) >= TO_DATE(:KEYIN_DATE_BB, 'yyyymmdd') ");
				condition.setObject("KEYIN_DATE_BB", sdfYYYYMMDD.format(inputVO.getKEYIN_DATE_B()));
			}

			if (inputVO.getKEYIN_DATE_E() != null && !inputVO.getKEYIN_DATE_E().equals("")) {
				sb.append("AND trunc(KEYIN_DATE) <= TO_DATE(:KEYIN_DATE_EE, 'yyyymmdd') ");
				condition.setObject("KEYIN_DATE_EE", sdfYYYYMMDD.format(inputVO.getKEYIN_DATE_E()));
			}

			if (!StringUtils.isBlank(inputVO.getPOLICY_NO1())) {
				sb.append("AND substr(POLICY_NO1, instr(POLICY_NO1, '-', -1)+1) = :POLICY_NO11 ");
				condition.setObject("POLICY_NO11", inputVO.getPOLICY_NO1());
			}

			if (!StringUtils.isBlank(inputVO.getPOLICY_NO2())) {
				sb.append("AND substr(POLICY_NO, instr(POLICY_NO, '-', 1)+1, instr(POLICY_NO, '-', -1)-instr(POLICY_NO, '-', 1)-1) = :POLICY_NO22 ");
				condition.setObject("POLICY_NO22", inputVO.getPOLICY_NO2());
			}

			if (!StringUtils.isBlank(inputVO.getPOLICY_NO3())) {
				sb.append("AND substr(POLICY_NO, 0, instr(POLICY_NO, '-', 1)-1) = :POLICY_NO33 ");
				condition.setObject("POLICY_NO33", inputVO.getPOLICY_NO3());
			}
			
			if (!StringUtils.isBlank(inputVO.getPROD_PERIOD())) {
				sb.append("AND PROD_PERIOD = :PROD_PERIODD  ");
				condition.setObject("PROD_PERIODD", inputVO.getPROD_PERIOD());
			}

			//進件來源
			if(StringUtils.isNotBlank(inputVO.getFB_COM_YN())) {
				sb.append(" And A.FB_COM_YN = :FB_COM_YN ");
				condition.setObject("FB_COM_YN", inputVO.getFB_COM_YN());
			}
			//保險公司
			if(StringUtils.isNotBlank(inputVO.getCOMPANY_NUM())) {
				sb.append(" And A.COMPANY_NUM = :companyNum ");
				condition.setObject("companyNum", inputVO.getCOMPANY_NUM());
			}	
			
			condition.setQueryString(sb.toString());

			List<Map<String, Object>> ResultList = dam.exeQuery(condition);

			outputVO.setResultList(ResultList); // datas
			outputVO.setList(ResultList);

			this.sendRtnObject(outputVO);
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
	}

	/*** 匯出功能 ***/
	public void print(Object body, IPrimitiveMap header) throws JBranchException {
		
		// 取得畫面資料
		IOT190OutputVO return_VO = (IOT190OutputVO) body;
		String loginID = (String) getCommonVariable(FubonSystemVariableConsts.LOGINID);

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> othType = xmlInfo.doGetVariable("IOT.OTH_TYPE", FormatHelper.FORMAT_3);
		Map<String, String> premiumUsage = xmlInfo.doGetVariable("IOT.PREMIUM_USAGE", FormatHelper.FORMAT_3);
		Map<String, String> payWay = xmlInfo.doGetVariable("IOT.PAY_WAY", FormatHelper.FORMAT_3);
		Map<String, String> payerRel = xmlInfo.doGetVariable("IOT.PAYER_REL_PROPOSER", FormatHelper.FORMAT_3);

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "壽險人件" + sdf.format(new Date()) + "_" + loginID + ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[46];
					int i = 0;
					records[i] = checkIsNull(map, "STATUS"); // 文件簽收狀態
					records[++i] = checkIsNull(map, "CREATETIME"); // 鍵機日
					records[++i] = checkIsNull(map, "OP_BATCH_NO"); // 分行送件批號
					records[++i] = "=" + '"' + checkIsNull(map, "INS_ID") + '"'; // 保險文件編號
					if ("--".equals(map.get("POLICY_NO"))) {
						records[++i] = "";
					} else {
						records[++i] = checkIsNull(map, "POLICY_NO"); // 保單號碼
					}
					if (!checkIsNull(map, "OTH_TYPE").equals("")) {
						records[++i] = othType.get(map.get("OTH_TYPE"));
					} else {
						records[++i] = "";
					} // 文件種類

//					records[++i] = checkIsNull(map, "OTH_TYPE");          // 文件種類
					records[++i] = checkIsNull(map, "CUST_ID"); // 要保人ID
					records[++i] = checkIsNull(map, "PROPOSER_NAME"); // 要保人/立約人姓名
					records[++i] = checkIsNull(map, "PROPOSER_BIRTH"); // 要保人/立約人生日
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNull(map, "TERMINATED_INC"); // 解約
					records[++i] = checkIsNull(map, "PROD_NAME"); // 適用專案
					records[++i] = checkIsNull(map, "PROPOSER_ACCNBR"); // 立約人帳號
					records[++i] = checkIsNull(map, "PROD_PERIOD"); // 商品檔期
					records[++i] = checkIsNull(map, "APPLY_DATE"); // 文件申請日
					records[++i] = checkIsNull(map, "INSURED_ID"); // 被保人/立約人ID
					records[++i] = checkIsNull(map, "RECRUIT_ID"); // 招攬人員編
					records[++i] = checkIsNull(map, "RECRUIT_NAME"); // 招攬人姓名
					records[++i] = checkIsNull(map, "INSPRD_NAME"); // 險種名稱
					records[++i] = checkIsNull(map, "CREATOR"); // 建立人
					records[++i] = checkIsNull(map, "AFT_SIGN_DATE"); // 簽收時間
					records[++i] = checkIsNull(map, "AFT_SIGN_OPRID"); // 簽收人
					records[++i] = checkIsNull(map, "INS_RCV_DATE"); // 送人壽時間
					records[++i] = checkIsNull(map, "INS_RCV_OPRID"); // 送人壽處理人
					records[++i] = checkIsNull(map, "DELETE_DATE"); // 刪除時間
					records[++i] = checkIsNull(map, "DELETE_OPRID"); // 刪除人
					records[++i] = checkIsNull(map, "QC_PROPOSER_CHG"); // 此次有無辦理要保人變更
					records[++i] = checkIsNull(map, "PREMATCH_SEQ"); // 要保人適合度檢核編碼
					records[++i] = checkIsNull(map, "AML"); // AML風險等級
					records[++i] = checkIsNull(map, "PRECHECK"); // Pre-check結果
					records[++i] = checkIsNull(map, "AUTH_YN"); // 覆核否
					if (!checkIsNull(map, "PREMIUM_USAGE").equals("")) {
						records[++i] = premiumUsage.get(map.get("PREMIUM_USAGE"));
					} else {
						records[++i] = "";
					}
					if (!checkIsNull(map, "PAY_WAY").equals("")) {
						records[++i] = payWay.get(map.get("PAY_WAY"));
					} else {
						records[++i] = "";
					}
//					records[++i] = checkIsNull(map, "PREMIUM_USAGE");      	// 繳費用途
//					records[++i] = checkIsNull(map, "PAY_WAY");      		// 繳費管道
					records[++i] = checkIsNull(map, "PAYER_ID"); // 繳款人ID
					records[++i] = checkIsNull(map, "APPLY_DATE"); // 要保書申請日
					records[++i] = checkIsNull(map, "LOAN_SOURCE_YN"); // 保費來源是否為貸款
					if (!checkIsNull(map, "RLT_BT_PROPAY").equals("")) {
						records[++i] = payerRel.get(map.get("RLT_BT_PROPAY"));
					} else {
						records[++i] = "";
					}
//					records[++i] = checkIsNull(map, "RLT_BT_PROPAY");      	// 繳款人與要保人關係
					records[++i] = checkIsNull(map, "LOAN_CHK1_YN"); // 繳款人保單貸款檢核
					records[++i] = checkIsNull(map, "LOAN_CHK2_YN"); // 繳款人行內貸款檢核
					records[++i] = checkIsNull(map, "CD_CHK_YN"); // 繳款人定存不打折檢核
					records[++i] = checkIsNull(map, "PREMIUM_TRANSSEQ"); // 保費來源錄音序號
					records[++i] = checkIsNull(map, "INS_SOURCE"); // 保單來源
					records[++i] = checkIsNull(map, "INS_COM_NAME"); // 保險公司
					records[++i] = checkIsNull(map, "OTH_FUND_PURPOSE_IN"); //辦理契約變更資金用途-本行
					records[++i] = checkIsNull(map, "OTH_FUND_PURPOSE_OUT"); //辦理契約變更資金用途-其他
					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[46];
				int j = 0;
				csvHeader[j] = "文件簽收狀態";
				csvHeader[++j] = "鍵機日";
				csvHeader[++j] = "分行送件批號";
				csvHeader[++j] = "保險文件編號";
				csvHeader[++j] = "保單號碼";
				csvHeader[++j] = "文件種類";
				csvHeader[++j] = "要保人ID";
				csvHeader[++j] = "要保人/立約人姓名";
				csvHeader[++j] = "要保人/立約人生日";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "解約";
				csvHeader[++j] = "適用專案";
				csvHeader[++j] = "立約人帳號";
				csvHeader[++j] = "商品檔期";
				csvHeader[++j] = "文件申請日";
				csvHeader[++j] = "被保人ID";
				csvHeader[++j] = "送件人員編";
				csvHeader[++j] = "送件人姓名";
				csvHeader[++j] = "險種名稱";
				csvHeader[++j] = "建立人";
				csvHeader[++j] = "簽收時間";
				csvHeader[++j] = "簽收人";
				csvHeader[++j] = "送人壽時間";
				csvHeader[++j] = "送人壽處理人";
				csvHeader[++j] = "刪除時間";
				csvHeader[++j] = "刪除人";
				csvHeader[++j] = "此次有無辦理要保人變更";
				csvHeader[++j] = "要保人適合度檢核編碼";
				csvHeader[++j] = "AML風險等級";
				csvHeader[++j] = "Pre-check結果";
				csvHeader[++j] = "覆核否";
				csvHeader[++j] = "繳費用途";
				csvHeader[++j] = "繳費管道";
				csvHeader[++j] = "繳款人ID";
				csvHeader[++j] = "要保書申請日";
				csvHeader[++j] = "保費來源是否為貸款";
				csvHeader[++j] = "繳款人與要保人關係";
				csvHeader[++j] = "繳款人保單貸款檢核";
				csvHeader[++j] = "繳款人行內貸款檢核";
				csvHeader[++j] = "繳款人定存不打折檢核";
				csvHeader[++j] = "保費來源錄音序號";
				csvHeader[++j] = "進件來源";
				csvHeader[++j] = "保險公司";
				csvHeader[++j] = "辦理契約變更資金用途-本行";
				csvHeader[++j] = "辦理契約變更資金用途-其他";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else

				this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if (map.get(key) != null) {
				return String.valueOf(map.get(key));
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	private String checkIsNu(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}
}

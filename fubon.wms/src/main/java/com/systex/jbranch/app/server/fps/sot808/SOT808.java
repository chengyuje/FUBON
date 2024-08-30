package com.systex.jbranch.app.server.fps.sot808;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MENU
 * 
 * @author Lily
 * @date 2016/11/21
 * @spec null
 */
@Component("sot808")
@Scope("request")
public class SOT808 extends SotPdf {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT808.class);
	List<String> url_list = null;

	@Override
	public List<String> printReport() throws JBranchException {
		
		url_list = new ArrayList<String>();
		this.reportLogic();
		return url_list;
	}

	public String reportLogic() throws JBranchException {
		
		String reportURL = null;
		String url = null;
		String txnCode = "SOT808";

		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); //產出pdf

		PRDFitInputVO inputVO = getInputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select M.TRUST_TRADE_TYPE, M.BRANCH_NBR AS BRA,M.GUARDIANSHIP_FLAG, M.CUST_ID, M.CUST_NAME, M.REC_SEQ, M.LASTUPDATE as MLASTUPDATE, D.*, D.ENTRUST_AMT AS ENTRUSTAMT, ");
			sql.append("       to_char(REF_VAL_DATE,'yyyy/mm/dd') AS REFVALDATE, P.PI_BUY ");
			
			if (inputVO.getPrdType().equals("3")) {
				sql.append("from TBSOT_BN_TRADE_D D ");
				sql.append(" left join TBPRD_BOND P ON P.PRD_ID = D.PROD_ID ");
			} else if (inputVO.getPrdType().equals("5")) {
				sql.append("from TBSOT_SN_TRADE_D D ");
				sql.append(" left join TBPRD_SN P ON P.PRD_ID = D.PROD_ID ");
			}
			
			sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
			sql.append("where D.TRADE_SEQ = :trade_seq ");

			if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
				sql.append("and D.BATCH_SEQ = :batch_seq ");
			}
			
			sql.append("order by BATCH_SEQ ");

			queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
			
			if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
				queryCondition.setObject("batch_seq", inputVO.getBatchSeq());
			}

			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = checkList(dam.exeQuery(queryCondition));

			String getMonth = "";
			String getDate = "";
			java.sql.Timestamp ts = (java.sql.Timestamp) list.get(0).get("MLASTUPDATE");
			getMonth = (ts.getMonth() + 1) + "";
			getDate = ts.getDate() + "";
			if (getMonth.length() < 2) {
				getMonth = "0" + getMonth;
			}
			
			if (getDate.length() < 2) {
				getDate = "0" + getDate;
			}

			if (list.size() > 0) {

				String reportID = "";

				switch ((String) list.get(0).get("TRUST_TRADE_TYPE")) {
					case "S":
						reportID = "R1";
						break;
					case "M":
						reportID = "R2";
						break;
				}
				XMLInfo XMLInfo = new XMLInfo();
	            HashMap<String,BigDecimal> ex_map = XMLInfo.getExchangeRate();
				BigDecimal total_twd  = new BigDecimal(0);
				for (Map<String, Object> data_map : list) {
					String tradeSubTypeToString = String.valueOf(data_map.get("TRADE_SUB_TYPE").toString());
					if (data_map.get("TRADE_SUB_TYPE") != null && StringUtils.isNotBlank(data_map.get("TRADE_SUB_TYPE").toString())) {
						data.addParameter("TRADE_SUB_TYPE", data_map.get("TRADE_SUB_TYPE").toString());
						if (tradeSubTypeToString.equals("1")) {
							data_map.put("TRADE_TYPE", inputVO.getPrdType().equals("3") ? "B" : "S");
							XmlInfo xmlInfo = new XmlInfo();

							if (data_map.get("ENTRUST_AMT") != null && StringUtils.isNotBlank(data_map.get("ENTRUST_AMT").toString())) {
								String EntrustType = (String) xmlInfo.getVariable("SOT.ENTRUST_TYPE_PURCHASE", data_map.get("ENTRUST_TYPE").toString(), "F3");
								if (data_map.get("GTC_YN") != null && StringUtils.equals("Y", data_map.get("GTC_YN").toString()))
									data_map.put("ENTRUST_AMT", "限價　" + data_map.get("ENTRUST_AMT").toString()); //長效單固定列印"限價"
								else
									data_map.put("ENTRUST_AMT", EntrustType + "　" + data_map.get("ENTRUST_AMT").toString());
							}

							if (data_map.get("FEE_RATE") != null && StringUtils.isNotBlank(data_map.get("FEE_RATE").toString())) {
								data_map.put("FEE_RATE", data_map.get("FEE_RATE").toString() + "%");
							} else {
								data_map.put("FEE_RATE", "0%");
							}

							//手續費金額
							if (data_map.get("FEE") != null && StringUtils.isNotBlank(data_map.get("FEE").toString())) {
								data_map.put("FEE", data_map.get("FEE").toString());
							} else {
								data_map.put("FEE", 0);
							}
							
							//面額
							if (data_map.get("PURCHASE_AMT") != null && StringUtils.isNotBlank(data_map.get("PURCHASE_AMT").toString())) {
								data_map.put("PURCHASE_AMT", data_map.get("PURCHASE_AMT").toString());
							} else {
								data_map.put("PURCHASE_AMT", 0);
							}
							
							//預估應付海外債券前手息
							if (data_map.get("PAYABLE_FEE") != null && StringUtils.isNotBlank(data_map.get("PAYABLE_FEE").toString())) {
								try {
									DecimalFormat mDecimalFormat = new DecimalFormat("#,##0.00");
									data_map.put("PAYABLE_FEE", mDecimalFormat.format(Double.parseDouble(data_map.get("PAYABLE_FEE").toString())));
								} finally {
									data_map.put("PAYABLE_FEE", data_map.get("PAYABLE_FEE").toString());
								}
							} else {
								data_map.put("PAYABLE_FEE", "0.00");
							}
							
							//長效單：預估應付海外債券前手息為"依成交當日為準"
							if (data_map.get("GTC_YN") != null && StringUtils.equals("Y", data_map.get("GTC_YN").toString()))
								data_map.put("PAYABLE_FEE", "依成交當日為準");

							//合計金額
							if (data_map.get("TOT_AMT") != null && StringUtils.isNotBlank(data_map.get("TOT_AMT").toString())) {
								try {
									DecimalFormat mDecimalFormat = new DecimalFormat("#,##0.00");
									data_map.put("TOT_AMT", mDecimalFormat.format(Double.parseDouble(data_map.get("TOT_AMT").toString())));
								} finally {
									data_map.put("TOT_AMT", data_map.get("TOT_AMT").toString());
								}
							} else {
								data_map.put("TOT_AMT", "0.00");
							}
							total_twd = new BigDecimal(deleteComma(data_map.get("TOT_AMT").toString())).multiply(ex_map.get(data_map.get("PROD_CURR").toString()));
							
							//長效單：合計金額為"依成交當日為準"
							if (data_map.get("GTC_YN") != null && StringUtils.equals("Y", data_map.get("GTC_YN").toString()))
								data_map.put("TOT_AMT", "依成交當日為準");

							//是否為長效單 Y:長效單 N:單日單
							if (data_map.get("GTC_YN") != null && StringUtils.isNotBlank(data_map.get("GTC_YN").toString())) {
								data_map.put("GTC_YN", data_map.get("GTC_YN").toString());
							} else {
								data_map.put("GTC_YN", "N");
							}
							
							//長效單迄日
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
							if (data_map.get("GTC_END_DATE") == null || StringUtils.isBlank(data_map.get("GTC_END_DATE").toString())) {
								data_map.put("GTC_END_DATE", "");
							} else {
								data_map.put("GTC_END_DATE", sdf.format(data_map.get("GTC_END_DATE")));
							}

							data.addRecordList("BUY", list);
						} else if (tradeSubTypeToString.equals("2")) {
							data_map.put("TRADE_TYPE", inputVO.getPrdType().equals("3") ? "B" : "S");
							XmlInfo xmlInfo = new XmlInfo();

							if (data_map.get("ENTRUST_TYPE") != null && StringUtils.isNotBlank(data_map.get("ENTRUST_TYPE").toString())) {
								String EntrustType = (String) xmlInfo.getVariable("SOT.ENTRUST_TYPE_REDEEM_SN", data_map.get("ENTRUST_TYPE").toString(), "F2");
								data_map.put("ENTRUST_TYPE", EntrustType);
							}

							if (data_map.get("ENTRUST_AMT") != null && StringUtils.isNotBlank(data_map.get("ENTRUST_AMT").toString())) {
								data_map.put("ENTRUST_AMT", data_map.get("ENTRUST_AMT").toString());
							} else {
								data_map.put("ENTRUST_AMT", "");
							}

							//是否為長效單 Y:長效單 N:單日單
							if (data_map.get("GTC_YN") != null && StringUtils.isNotBlank(data_map.get("GTC_YN").toString())) {
								data_map.put("GTC_YN", data_map.get("GTC_YN").toString());
							} else {
								data_map.put("GTC_YN", "N");
							}
							
							//長效單迄日
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
							if (data_map.get("GTC_END_DATE") == null || StringUtils.isBlank(data_map.get("GTC_END_DATE").toString())) {
								data_map.put("GTC_END_DATE", "");
							} else {
								data_map.put("GTC_END_DATE", sdf.format(data_map.get("GTC_END_DATE")));
							}
							
							//贖回面額
							if (inputVO.getPrdType().equals("3")) {//海外債
								if (data_map.get("PURCHASE_AMT") != null && StringUtils.isNotBlank(data_map.get("PURCHASE_AMT").toString())) {
									data_map.put("PURCHASE_AMT", data_map.get("PURCHASE_AMT").toString());
								} else {
									data_map.put("PURCHASE_AMT", 0);
								}
							} else if (inputVO.getPrdType().equals("5")) {//SN
								if (data_map.get("REDEEM_AMT") != null && StringUtils.isNotBlank(data_map.get("REDEEM_AMT").toString())) {
									data_map.put("PURCHASE_AMT", data_map.get("REDEEM_AMT").toString());
								} else {
									data_map.put("PURCHASE_AMT", 0);
								}
							}
							data.addRecordList("SELL", list);
						}
					}

					//金錢信託限專投申購商品
					if(StringUtils.equals("M", ObjectUtils.toString(data_map.get("TRUST_TRADE_TYPE"))) && 
							tradeSubTypeToString.equals("1") &&
							StringUtils.equals("Y", ObjectUtils.toString(data_map.get("PI_BUY")))) {
						data.addParameter("PI_BUY", "(本次申購含專投商品)");
					} else {
						data.addParameter("PI_BUY", "");
					}
					
					if (data_map.get("BATCH_SEQ") != null && StringUtils.isNotBlank(data_map.get("BATCH_SEQ").toString())) {
						data.addParameter("BATCH_SEQ", data_map.get("BATCH_SEQ").toString());
					}
					
//					String custID = "";
					if (data_map.get("CUST_ID") != null && StringUtils.isNotBlank(data_map.get("CUST_ID").toString())) {
						data.addParameter("CUST_ID", data_map.get("CUST_ID").toString());
//						custID = data_map.get("CUST_ID").toString();
					}
					
					if (data_map.get("CONTRACT_ID") != null && StringUtils.isNotBlank(data_map.get("CONTRACT_ID").toString())) {
						data.addParameter("CONTRACT_ID", data_map.get("CONTRACT_ID").toString());
					}
					
					if (data_map.get("CUST_NAME") != null && StringUtils.isNotBlank(data_map.get("CUST_NAME").toString())) {
						data.addParameter("CUST_NAME", data_map.get("CUST_NAME").toString() + (StringUtils.equals("Y", (String) data_map.get("TRUST_PEOP_NUM")) ? "*" : ""));
					}
					
					if (data_map.get("TRUST_ACCT") != null && StringUtils.isNotBlank(data_map.get("TRUST_ACCT").toString())) {
						data.addParameter("TRUST_ACCT", data_map.get("TRUST_ACCT").toString());
					}
					
					if (data_map.get("NARRATOR_ID") != null && StringUtils.isNotBlank(data_map.get("NARRATOR_ID").toString())) {
						data.addParameter("NARRATOR_ID", data_map.get("NARRATOR_ID").toString());
					}
					
					if (data_map.get("NARRATOR_NAME") != null && StringUtils.isNotBlank(data_map.get("NARRATOR_NAME").toString())) {
						data.addParameter("NARRATOR_NAME", data_map.get("NARRATOR_NAME").toString());
					}
					
					if (data_map.get("REC_SEQ") != null && StringUtils.isNotBlank(data_map.get("REC_SEQ").toString())) {
						data.addParameter("REC_SEQ", "錄音序號：" + data_map.get("REC_SEQ").toString());
					}
					
					data.addParameter("REPORT_DATE", "中華民國  " + (ts.getYear() - 11) + "  年  " + getMonth + "  月  " + getDate + "  日");
					data.addParameter("REPORT_TIME", "時間： " + ts.getHours() + "  時  " + ts.getMinutes() + "  分  ");
					
					if(inputVO.getPrdType().equals("3") && total_twd.compareTo(new BigDecimal(1000000)) > -1){ 
						data.addParameter("AUTH_FLAG", "主管:_______ 經辦:_______ 驗印:_______ 主管驗印:_______ 親見本人親簽無誤:_______"); 
					}else{
						data.addParameter("AUTH_FLAG", "主管:____________ 經辦:____________ 驗印:____________ 親見本人親簽無誤:____________");
					}
					
					XmlInfo xmlInfo = new XmlInfo();
					data.addParameter("TRUSTTS", "統編: " + xmlInfo.doGetVariable("SOT.TRUST_TS","M_CUSTNO",FormatHelper.FORMAT_3));
					data.addParameter("TRUSTTS_ACCT","帳號: " + xmlInfo.doGetVariable("SOT.TRUST_ACCT","00250368045043",FormatHelper.FORMAT_3));
					
					//0000275: 金錢信託受監護受輔助宣告交易控管調整
					data.addParameter("GUARDIANSHIP_FLAG", "");
					if(StringUtils.equals(data_map.get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
						data.addParameter("GUARDIANSHIP_FLAG", data_map.get("GUARDIANSHIP_FLAG").toString().equals("1") ? "*受監護宣告*" : data_map.get("GUARDIANSHIP_FLAG").toString().equals("2") ? "*受輔助宣告*" : " " );
						
						if(total_twd.compareTo(new BigDecimal(1000000)) > -1){
							data.addParameter("AUTH_FLAG_M", "受理單位:" + data_map.get("BRA").toString() + " 文件覆核主管:_________ 文件覆核經辦:_________ 驗印:_________ 主管驗印:________  親見本人親簽無誤:_______");
						}else{
							data.addParameter("AUTH_FLAG_M", "受理單位:" + data_map.get("BRA").toString() + " 文件覆核主管:_________ 文件覆核經辦:_________ 驗印:_________  親見本人親簽無誤:_______");
						}
					}

					data.addParameter("GTC_YN", data_map.get("GTC_YN").toString());
					data.addParameter("GTC_END_DATE", data_map.get("GTC_END_DATE").toString());
					data.addParameter("TRADE_TYPE", data_map.get("TRADE_TYPE").toString());
					data.addParameter("ENTRUST_AMT", data_map.get("ENTRUST_AMT").toString());

					if (data_map.get("BRA") != null && StringUtils.isNotBlank(data_map.get("BRA").toString())) {
						data.addParameter("BRA", data_map.get("BRA").toString());
					}
					//委託人未成年判斷，法人不判斷
					if (data_map.get("CUST_ID") != null && StringUtils.isNotBlank(data_map.get("CUST_ID").toString()) && data_map.get("CUST_ID").toString().length() >= 10) {
						SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
						SOT712InputVO sot712InputVO = new SOT712InputVO();
						sot712InputVO.setCustID(data_map.get("CUST_ID").toString());

						if (sot712.getCUST_AGE(sot712InputVO) < 18) { //當客戶為未成年時，將印出未成年警語
							data.addParameter("ADULT", "N");
						}
					}

					data.addParameter("H_CUST_ID", "*" + data_map.get("CUST_ID").toString() + "*");
					data.addParameter("RPT_ID", "0");
					data.addParameter("PageFoot", "第一聯:受理單位留存聯");
					report = gen.generateReport(txnCode, reportID, data);
					url = report.getLocation();
					url_list.add(url);

					data.addParameter("H_CUST_ID", "");
					data.addParameter("RPT_ID", "");
					data.addParameter("PageFoot", "第二聯:客戶收執聯");
					report = gen.generateReport(txnCode, reportID, data);
					url = report.getLocation();
					url_list.add(url);
				}
			}

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		return reportURL;
	}
	
	/**
	 * 清除逗點
	 * 
	 * @param act_BAL
	 * @return
	 */
	private String deleteComma(String act_BAL) {
		String regEx = "[,]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matchar = pattern.matcher(act_BAL);

		return matchar.replaceAll("").trim();
	}

}
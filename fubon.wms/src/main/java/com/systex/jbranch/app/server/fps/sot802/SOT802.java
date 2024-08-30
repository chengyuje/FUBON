package com.systex.jbranch.app.server.fps.sot802;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * 下單專區功能
 *
 * @author Jacky Wu
 * @date 2016/12/30
 * @spec 基金定期(不)定額申購表單列印
 */


@Component("sot802")
@Scope("request")
public class SOT802 extends SotPdf{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT802.class);

	@Override
	public List<String> printReport() throws JBranchException {
		String url = null;
		String txnCode = "SOT802";
		String reportID = "R1";
		ReportIF report = null;
		String trustType = ""; //信託類型
		int i =0; //取batchSeq

		PRDFitInputVO inputVO = getInputVO();

		try{
			XmlInfo xmlinfo = new XmlInfo();

			Map<String, String> trustTypeMap = xmlinfo.doGetVariable("SOT.ASSET_TRADE_SUB_TYPE", FormatHelper.FORMAT_3);

			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sql = new StringBuffer();
			sql.append("select BATCH_SEQ,count(1) as BATCH_COUNT from TBSOT_NF_PURCHASE_D ");
			sql.append("where TRADE_SEQ = :trade_seq ");

			if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
				sql.append("and BATCH_SEQ = :batch_seq ");
			}
			sql.append("group by BATCH_SEQ order by BATCH_SEQ ");

			queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
			if (StringUtils.isNotEmpty(inputVO.getBatchSeq())) {
				queryCondition.setObject("batch_seq", inputVO.getBatchSeq());
			}

			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if(list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");
			List<String> url_list = null;
			List<Map<String, Object>> data_list = null;
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO  sot712InputVO = new SOT712InputVO();
			String custID = "";
			String piBuy = "";
			url_list = new ArrayList<String>();
			for(Map<String, Object> map :list){
				sql = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("select M.CUST_ID,M.CUST_NAME,D.PROSPECTUS_TYPE as DPROSPECTUSTYPE,M.BRANCH_NBR AS BRA,M.GUARDIANSHIP_FLAG,M.LASTUPDATE as MLASTUPDATE,M.REC_SEQ AS REC_SEQ,M.TRUST_TRADE_TYPE,D.* "
						+ " ,CASE WHEN TO_CHAR(D.TRADE_DATE,'YYYYMMDD')=TO_CHAR(D.CREATETIME,'YYYYMMDD') THEN 'Y' ELSE 'N' END AS MEMO1, TO_CHAR(D.TRADE_DATE,'YYYY/MM/DD') as TRA_DATE "
						+ " ,P.OBU_PRO AS PI_BUY " //OBU_PRO=1: 限專投申購商品
						+ " from TBSOT_NF_PURCHASE_D D"
						+ " inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ"
						+ " left outer join TBORG_MEMBER E on E.EMP_ID = D.MODIFIER"
						+ " LEFT JOIN TBPRD_FUND P ON P.PRD_ID = D.PROD_ID"
						+ " where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq ");
				queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
				queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
				queryCondition.setQueryString(sql.toString());

				data_list = checkList(dam.exeQuery(queryCondition));


				if(data_list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");

				trustType = data_list.get(0).get("TRADE_SUB_TYPE").toString();

				ReportFactory factory = new ReportFactory();
				ReportDataIF data = new ReportData();
				ReportGeneratorIF gen = factory.getGenerator();  //產出pdf
				data.addParameter("P1_BATCH_NO",list.get(i).get("BATCH_SEQ").toString());
				i++;

				data.addParameter("P1_CUST_ID",data_list.get(0).get("CUST_ID").toString());
				data.addParameter("P1_CUST_NAME",data_list.get(0).get("CUST_NAME").toString() + (StringUtils.equals("Y", data_list.get(0).get("TRUST_PEOP_NUM").toString()) ? "*" : ""));
				if(StringUtils.equals(data_list.get(0).get("TRUST_CURR_TYPE").toString(),"Y")){
					data.addParameter("P1_TRUST_BUSINESS","外幣信託");
				}else{
					data.addParameter("P1_TRUST_BUSINESS","臺幣信託");
				}
				//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-26 add by Jacky
				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
					data.addParameter("P1_CONTRACT_ID", data_list.get(0).get("CONTRACT_ID").toString());
					reportID = "R2";
				}else{//特金交易
					data.addParameter("P1_TRUST_ACCT", data_list.get(0).get("TRUST_ACCT").toString());
					reportID = "R1";
				}
				data.addParameter("P1_AUTH_CHANGE_ID",data_list.get(0).get("CUST_ID").toString());
				data.addParameter("P1_INCOME_ACCT", data_list.get(0).get("CREDIT_ACCT").toString());
				if("4".equals(data_list.get(0).get("TRADE_SUB_TYPE").toString())) {
					data.addParameter("P1_TRUST_TYPE",trustTypeMap.get("8"));
				} else if("5".equals(data_list.get(0).get("TRADE_SUB_TYPE").toString())) {
					data.addParameter("P1_TRUST_TYPE",trustTypeMap.get("9"));
				} else {
					data.addParameter("P1_TRUST_TYPE",trustTypeMap.get(data_list.get(0).get("TRADE_SUB_TYPE").toString()));
				}
				
				data.addParameter("P1_AUTH_CHANGE_ACCT", data_list.get(0).get("DEBIT_ACCT").toString());
				data.addParameter("BRA", data_list.get(0).get("BRA").toString());


				int index = 0;
				String total_curr1="",total_curr2="",total_curr3="";
				for(Map<String, Object> data_map : data_list){
					index++;
//					data.addParameter("P1_AUTO_FX"+index, StringUtils.equals(getString(data_map.get("IS_AUTO_CX")), "Y")
//							? "自動換匯      : 是" : "自動換匯      : 否");  //自動換匯

					//add by Brian
					data.addParameter("P1_AUTO_FX"+index, "自動換匯     : "+data_map.get("IS_AUTO_CX").toString());//自動換匯

					String fund_name = "投資標的名稱 : "+ (String)data_map.get("PROD_ID")
							+ "	"+(String)data_map.get("PROD_CURR")
							+ "	"+(String)data_map.get("PROD_NAME");
					String tpp_slp = String.valueOf(data_map.get("TAKE_PROFIT_PERC")) + "%/"
							+ String.valueOf(data_map.get("STOP_LOSS_PERC")) + "%";

					data.addParameter("P1_FUND_ID"+index, fund_name);
					data.addParameter("P1_RAM_TYPE"+index, "商品風險等級 : "+data_map.get("PROD_RISK_LV").toString());
					data.addParameter("P1_TPP_SLP"+index, "滿足/停損點通知 :" + tpp_slp);

					if (data_map.get("REC_SEQ") != null && StringUtils.isNotBlank(data_map.get("REC_SEQ").toString())) {
						data.addParameter("REC_SEQ","   錄音序號："+data_map.get("REC_SEQ").toString());
					}

					//定期定額
					if(StringUtils.equals(trustType, "2") || StringUtils.equals(trustType, "4")){
						//扣款日期共6個欄位
						String p1_date = "";
						for(int date_seq = 1;date_seq<=6;date_seq++){
							if(StringUtils.isNotBlank(ObjectUtils.toString(data_map.get("CHARGE_DATE_"+date_seq)))){
								p1_date += data_map.get("CHARGE_DATE_"+date_seq).toString() + "　";
							}
						}
						data.addParameter("P1_DATE"+index, "指定扣款日　 : "+p1_date);
//						data.addParameter("P1_DATE1", data_map.get("CHARGE_DATE_1").toString());
						data.addParameter("P1_LAMT_CUR"+index,"信託金額　　 : "+data_map.get("TRUST_CURR").toString());
						data.addParameter("P1_LAMT"+index,getBigDecimal(data_map.get("PURCHASE_AMT")));
						data.addParameter("P1_LFEE_RATE"+index, "手續費率:"+getBigDecimal(data_map.get("FEE_RATE"))+"　　　%");
						data.addParameter("P1_LFEE_CUR"+index, "手續費用 : "+data_map.get("TRUST_CURR").toString());
						data.addParameter("P1_LFEE"+index, getBigDecimal(data_map.get("FEE")));
					}

					//定期不定額套表欄位
					if(StringUtils.equals(trustType, "3") || StringUtils.equals(trustType, "5")){
						//扣款日期共6個欄位
						String p1_date = "";
						for(int date_seq = 1;date_seq<=6;date_seq++){
							if((String)data_map.get("CHARGE_DATE_"+date_seq) != null && StringUtils.isNotBlank((String)data_map.get("CHARGE_DATE_"+date_seq))){
								p1_date += data_map.get("CHARGE_DATE_"+date_seq).toString() + "　";
							}
						}
						data.addParameter("P1_DATE"+index, "指定扣款日　 : "+p1_date);  //扣款日期
						data.addParameter("P1_LAMT_CUR"+index,"低扣款金額　 : "+data_map.get("TRUST_CURR").toString());  //低扣款金額幣別
						data.addParameter("P1_LAMT"+index,getBigDecimal(data_map.get("PURCHASE_AMT_L")));  //低扣款金額
						data.addParameter("P1_LFEE_RATE"+index, "手續費率:"+getBigDecimal(data_map.get("FEE_RATE_L"))+"　　　%");  //低扣款手續費率
						data.addParameter("P1_LFEE_CUR"+index, "手續費用 : "+data_map.get("TRUST_CURR").toString());  //低扣款手續費用幣別
						data.addParameter("P1_LFEE"+index, getBigDecimal(data_map.get("FEE_L")));  //低扣款手續費用

						data.addParameter("P1_MAMT_CUR"+index,"中扣款金額　 : "+data_map.get("TRUST_CURR").toString());  //中扣款金額幣別
						data.addParameter("P1_MAMT"+index,getBigDecimal(data_map.get("PURCHASE_AMT_M")));  //中扣款金額
						data.addParameter("P1_MFEE_RATE"+index, "手續費率:"+getBigDecimal(data_map.get("FEE_RATE_M"))+"　　　%");  //中扣款手續費率

						data.addParameter("P1_MFEE_CUR"+index, "手續費用 : "+data_map.get("TRUST_CURR").toString());  //中扣款手續費用幣別
						data.addParameter("P1_MFEE"+index, getBigDecimal(data_map.get("FEE_M")));  //中扣款手續費用

						data.addParameter("P1_HAMT_CUR"+index,"高扣款金額　 : "+data_map.get("TRUST_CURR").toString());  //高扣款金額幣別
						data.addParameter("P1_HAMT"+index,getBigDecimal(data_map.get("PURCHASE_AMT_H")));  //高扣款金額
						data.addParameter("P1_HFEE_RATE"+index, "手續費率:"+getBigDecimal(data_map.get("FEE_RATE_H"))+"　　　%");  //高扣款手續費率

						data.addParameter("P1_HFEE_CUR"+index, "手續費用 : "+data_map.get("TRUST_CURR").toString());  //高扣款手續費用幣別
						data.addParameter("P1_HFEE"+index, getBigDecimal(data_map.get("FEE_H")));  //高扣款手續費用

					}
					
					//金錢信託限專投申購商品
					if(StringUtils.equals(ObjectUtils.toString(data_list.get(0).get("TRUST_TRADE_TYPE")), "M") &&
							!StringUtils.equals("1", piBuy)){//金錢信託
						piBuy = ObjectUtils.toString(data_list.get(0).get("PI_BUY"));
					}

				}

				//金錢信託限專投申購商品
				if(StringUtils.equals("1", piBuy)) {
					data.addParameter("PI_BUY", "(本次申購含專投商品)");
				} else {
					data.addParameter("PI_BUY", "");
				}
				
				if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
					data.addParameter("P1_MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
				}
				custID = data_list.get(0).get("CUST_ID").toString();

//		    	sot712 = (SOT712) PlatformContext.getBean("sot712");
//		        sot712InputVO = new SOT712InputVO();
		        sot712InputVO.setCustID(custID);
				if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
					data.addParameter("P1_MEMO2", "＊委託人未成年＊");
				}

				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){	//金錢信託
					String p1_openbook1 = getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("2")? "■ 已取得，本次毋須再交付" : "□ 已取得，本次毋須再交付";
					String p1_openbook2 = getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("1")? "■ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得" : "□ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得";
					data.addParameter("P1_OPENBOOK1", p1_openbook1);
					data.addParameter("P1_OPENBOOK2", p1_openbook2);
				} else {
					data.addParameter("P1_OPENBOOK1", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("2")? "■" :"□");
					data.addParameter("P1_OPENBOOK2", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("1")? "■" :"□");
				}

				data.addParameter("P1_EFFECT_DATE",data_list.get(0).get("TRADE_DATE").toString());

				String getMonth = "";
				String getDate = "";
				java.sql.Timestamp ts = (java.sql.Timestamp) data_list.get(0).get("MLASTUPDATE");
				getMonth = (ts.getMonth()+1)+"";
				getDate = ts.getDate()+"";
				if (getMonth.length() < 2){
					getMonth = "0"+getMonth;
				}
				if (getDate.length() < 2){
					getDate = "0"+getDate;
				}

				if ("1".equals(data_list.get(0).get("TRADE_DATE_TYPE").toString())){
					data.addParameter("REPORT_DATE", "中華民國  "+ (ts.getYear()-11) + "  年  " + getMonth + "  月  " + getDate+"  日");
					data.addParameter("REPORT_TIME", "時間： "+ts.getHours()+"  時  "+ts.getMinutes()+"  分  ");
				}else{
					data.addParameter("REPORT_DATE", "中華民國        年     月     日");
					data.addParameter("REPORT_TIME", "時間：      時      分");
					if (StringUtils.isNotBlank(data_list.get(0).get("TRA_DATE").toString())) {
						data.addParameter("TRADE_DATE", "表單導入有效期限至　" + data_list.get(0).get("TRA_DATE").toString());
					}
				}

				XmlInfo xmlInfo = new XmlInfo();
				data.addParameter("TRUSTTS", "統編: " + xmlInfo.doGetVariable("SOT.TRUST_TS","M_CUSTNO",FormatHelper.FORMAT_3));
				data.addParameter("TRUSTTS_ACCT","帳號: " + xmlInfo.doGetVariable("SOT.TRUST_ACCT","00250368045043",FormatHelper.FORMAT_3));

				//0000275: 金錢信託受監護受輔助宣告交易控管調整
				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
					data.addParameter("GUARDIANSHIP_FLAG", data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("1") ? "*受監護宣告*" : data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("2") ? "*受輔助宣告*" : " " );

				    data.addParameter("AUTH_FLAG_M", "受理單位:" + data_list.get(0).get("BRA").toString() + " 文件覆核主管:_________________ 文件覆核經辦:_________________ 驗印:_________________");
				}

				data.addParameter("P1_RECOMMENDER",data_list.get(0).get("NARRATOR_NAME").toString());
				data.addParameter("P1_RECOMMENDER_ID",data_list.get(0).get("NARRATOR_ID").toString());

				data.addParameter("P1_ORDERFLAG","N");
				data.addParameter("PageFoot","第一聯:受理單位留存聯");

				report = gen.generateReport(txnCode, reportID, data);
				url = report.getLocation();
				url_list.add(url);

				data.addParameter("PageFoot","第二聯:客戶收執聯");
				//經信託部提醒，客戶留存聯不用顯示表單有效期限 #0003117
				//邏輯 : 不管交易日期類型是否為預約或是即時，在客戶收執聯TRADE_DATE欄位加入空值
				data.addParameter("TRADE_DATE", "");
				report = gen.generateReport(txnCode, reportID, data);
				url = report.getLocation();
				url_list.add(url);
				if (StringUtils.equals(trustType, "4") || StringUtils.equals(trustType, "5") ||  StringUtils.equals(getString(data_list.get(0).get("IS_AUTO_CX")), "Y")){
					data = new ReportData();
					data.addParameter("CUST_ID",data_list.get(0).get("CUST_ID").toString());

					data.addParameter("PageFoot","第一聯:受理單位留存聯");
//					txnCode = "SOT803";
					report = gen.generateReport("SOT803", reportID, data);
					url = report.getLocation();
					url_list.add(url);

					data.addParameter("PageFoot","第二聯:客戶收執聯");
					report = gen.generateReport("SOT803", reportID, data);
					url = report.getLocation();
					url_list.add(url);
				}
			}

			return url_list;
		}catch(Exception e){
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	private BigDecimal getBigDecimal(Object val){
		if(val == null){
			return new BigDecimal(0);
		}else{
			return new BigDecimal(val.toString());
		}
	}

	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}

}

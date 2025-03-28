package com.systex.jbranch.app.server.fps.sot801;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 下單專區功能
 *
 * @author Jacky Wu
 * @date 2016/12/30
 * @spec 基金單筆申購表單列印
 */


@Component("sot801")
@Scope("request")
public class SOT801 extends SotPdf{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT801.class);

	@Override
	public List<String> printReport() throws JBranchException {
		String url = null;
		String txnCode = "SOT801";
		String reportID = "R1";
		String custID = "";
		ReportIF report = null;

		PRDFitInputVO inputVO = getInputVO();

		try{

			XMLInfo XMLInfo = new XMLInfo();
            HashMap<String,BigDecimal> ex_map = XMLInfo.getExchangeRate();

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

			List<String> url_list = null;
			url_list = new ArrayList<String>();
			int i =0;
			String piBuy = "";
			for(Map<String, Object> map :list){
				sql = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("select M.CUST_ID,M.CUST_NAME,D.PROSPECTUS_TYPE as DPROSPECTUSTYPE,M.BRANCH_NBR AS BRA,M.GUARDIANSHIP_FLAG, M.LASTUPDATE as MLASTUPDATE,M.REC_SEQ AS REC_SEQ,M.TRUST_TRADE_TYPE,D.* "
						+ " ,CASE WHEN TO_CHAR(D.TRADE_DATE,'YYYYMMDD')=TO_CHAR(D.CREATETIME,'YYYYMMDD') THEN 'Y' ELSE 'N' END AS MEMO1, TO_CHAR(D.TRADE_DATE, 'YYYY/MM/DD') as TRA_DATE "
						+ " ,P.OBU_PRO AS PI_BUY, P.OVS_PRIVATE_YN " //OBU_PRO=1: 限專投申購商品
						+ " from TBSOT_NF_PURCHASE_D D"
						+ " inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ"
						+ " left outer join TBORG_MEMBER E on E.EMP_ID = D.MODIFIER"
						+ " LEFT JOIN TBPRD_FUND P ON P.PRD_ID = D.PROD_ID"
						+ " where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq ");
				queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
				queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
				queryCondition.setQueryString(sql.toString());

				List<Map<String, Object>> data_list = checkList(dam.exeQuery(queryCondition));
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
					if(StringUtils.equals(data_list.get(0).get("OVS_PRIVATE_YN").toString(), "Y")) {
						reportID = "R3"; //境外私募基金
						if(StringUtils.equals(data_list.get(0).get("OVER_CENTRATE_YN").toString(), "W")) {
							data.addParameter("CENTRATE_REMARK", "本筆交易觸及集中度限制，需徵提高資產客戶投資產品集中度聲明書。");
						}
					} else {
						reportID = "R1";
					}
					data.addParameter("P1_TRUST_ACCT", data_list.get(0).get("TRUST_ACCT").toString());
				}
				data.addParameter("P1_AUTH_CHANGE_ACCT", data_list.get(0).get("DEBIT_ACCT").toString());
				data.addParameter("P1_INCOME_ACCT", data_list.get(0).get("CREDIT_ACCT").toString());

				int index = 0;
				String total_curr1="",total_curr2="",total_curr3="";
				BigDecimal total_twd = new BigDecimal(0),total1 = new BigDecimal(0),total2 = new BigDecimal(0),total3 = new BigDecimal(0);
				for(Map<String, Object> data_map : data_list){
					index++;
					String fund_name = "投資標的名稱 : "+ (String)data_map.get("PROD_ID")
							+ "	"+(String)data_map.get("PROD_CURR")
							+ "	"+(String)data_map.get("PROD_NAME");
					String tpp_slp = String.valueOf(data_map.get("TAKE_PROFIT_PERC")) + "% / "
									+ String.valueOf(data_map.get("STOP_LOSS_PERC")) + "%";
					data.addParameter("P1_FUND_ID"+index, fund_name);
					data.addParameter("P1_COUPON"+index, data_map.get("FEE_TYPE").toString().equals("B")? "　生日券　:  Y " :"");
					data.addParameter("P1_TPP_SLP"+index, "滿足/停損點通知 : " + tpp_slp);
					data.addParameter("P1_AMT_CUR"+index,"信託金額     : "+data_map.get("TRUST_CURR").toString());
					data.addParameter("P1_AMT"+index, getBigDecimal(data_map.get("PURCHASE_AMT")));
					data.addParameter("P1_FEE_RATE"+index, "手續費率:"+getBigDecimal(data_map.get("FEE_RATE").toString())+"%");
					data.addParameter("P1_FEE_CUR"+index, "手續費用  :  "+data_map.get("TRUST_CURR").toString());
					data.addParameter("P1_FEE"+index, getBigDecimal(data_map.get("FEE")));
					data.addParameter("P1_TOTAL_CUR"+index, "信託總金額　 : "+ data_map.get("TRUST_CURR").toString());
					BigDecimal total = new BigDecimal(data_map.get("PURCHASE_AMT").toString())
									   .add(new BigDecimal(data_map.get("FEE").toString()));

					data.addParameter("P1_TOTAL"+index, total);
					data.addParameter("P1_RAM_TYPE"+index, "             商品風險等級　：  "+data_map.get("PROD_RISK_LV").toString());

					switch(index){
						case 1:
							total_curr1 =  data_map.get("TRUST_CURR").toString();
							break;
						case 2:
							total_curr2 =  data_map.get("TRUST_CURR").toString();
							break;
						case 3:
							total_curr3 =  data_map.get("TRUST_CURR").toString();
							break;
					}
					if(StringUtils.equals(total_curr1, data_map.get("TRUST_CURR").toString())){
						total1 = total1.add(total);
					}else if(!StringUtils.equals(total_curr1,total_curr2) && StringUtils.equals(total_curr2, data_map.get("TRUST_CURR").toString())){
						total2 = total2.add(total);
					}else if(!StringUtils.equals(total_curr1,total_curr3) && !StringUtils.equals(total_curr2,total_curr3) && StringUtils.equals(total_curr3, data_map.get("TRUST_CURR").toString())){
						total3 = total3.add(total);
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
				
				data.addParameter("P1_COUNT", data_list.size());  //申購筆數
				data.addParameter("P1_TOTALCUR1", total_curr1);  //總數幣別1
				data.addParameter("P1_TOTAL_AMT1", total1.toString());  //總數1
				total_twd = total_twd.add(total1.multiply(ex_map.get(total_curr1)));

				if(total2.compareTo(BigDecimal.ZERO) == 0 ){
					data.addParameter("P1_TOTAL_AMT2", "");  //總數2
				}else{
					data.addParameter("P1_TOTALCUR2", total_curr2);  //總數幣別2
					data.addParameter("P1_TOTAL_AMT2", total2.toString());  //總數2
					total_twd = total_twd.add(total2.multiply(ex_map.get(total_curr2)));
				}
				if(total3.compareTo(BigDecimal.ZERO) == 0 ){
					data.addParameter("P1_TOTAL_AMT3", "");  //總數3
				}else{
					data.addParameter("P1_TOTALCUR3", total_curr3);  //總數幣別3
					data.addParameter("P1_TOTAL_AMT3", total3.toString());  //總數3
					total_twd = total_twd.add(total3.multiply(ex_map.get(total_curr3)));
				}


				if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
					data.addParameter("P1_MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
				}

				custID = data_list.get(0).get("CUST_ID").toString();

		    	SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		        SOT712InputVO  sot712InputVO = new SOT712InputVO();
		        sot712InputVO.setCustID(custID);
				if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
					data.addParameter("P1_MEMO2", "＊委託人未成年＊");
				}

				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
					String p1_openbook1 = data_list.get(0).get("DPROSPECTUSTYPE").toString().equals("2")? "■ 已取得，本次毋須再交付" :"□ 已取得，本次毋須再交付";
					String p1_openbook2 = data_list.get(0).get("DPROSPECTUSTYPE").toString().equals("1")? "■ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得" :"□ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得";
					data.addParameter("P1_OPENBOOK1", p1_openbook1);
					data.addParameter("P1_OPENBOOK2", p1_openbook2);
				} else {
					data.addParameter("P1_OPENBOOK1", data_list.get(0).get("DPROSPECTUSTYPE").toString().equals("2")? "■" :"□");
					data.addParameter("P1_OPENBOOK2", data_list.get(0).get("DPROSPECTUSTYPE").toString().equals("1")? "■" :"□");
				}

				if(total_twd.compareTo(new BigDecimal(1000000)) > -1){
					data.addParameter("AUTH_FLAG", "主管:________ 經辦:________ 驗印:________ 主管驗印:_________ 親見本人親簽無誤:_________");
				}else{
					data.addParameter("AUTH_FLAG", "主管:_____________ 經辦:_____________ 驗印:_____________ 親見本人親簽無誤:_____________");
				}
				//0000275: 金錢信託受監護受輔助宣告交易控管調整
				data.addParameter("GUARDIANSHIP_FLAG", "");
				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
					if(total_twd.compareTo(new BigDecimal(1000000)) > -1){
						data.addParameter("AUTH_FLAG_M", "受理單位:" + data_list.get(0).get("BRA").toString() + " 文件覆核主管:_________ 文件覆核經辦:_________ 驗印:_________ 主管驗印:_________");
					}else{
						data.addParameter("AUTH_FLAG_M", "受理單位:" + data_list.get(0).get("BRA").toString() + " 文件覆核主管:__________ 文件覆核經辦:__________ 驗印:__________");
					}

					data.addParameter("GUARDIANSHIP_FLAG", data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("1") ? "*受監護宣告*" : data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("2") ? "*受輔助宣告*" : " " );
				}

				if (data_list.get(0).get("REC_SEQ") != null && StringUtils.isNotBlank(data_list.get(0).get("REC_SEQ").toString())) {
					data.addParameter("REC_SEQ","  錄音序號："+data_list.get(0).get("REC_SEQ").toString());
				}

				data.addParameter("P1_EFFECT_DATE",data_list.get(0).get("TRADE_DATE").toString()); //生效日期

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

				data.addParameter("P1_RECOMMENDER",data_list.get(0).get("NARRATOR_NAME")); //解說理專姓名
				data.addParameter("P1_RECOMMENDER_ID",data_list.get(0).get("NARRATOR_ID")); //解說理專員編

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
}

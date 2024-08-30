package com.systex.jbranch.app.server.fps.sot805;

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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * 下單專區功能
 *
 * @author Jacky Wu
 * @date 2016/12/30
 * @spec 基金轉換交易表單列印
 */

@Component("sot805")
@Scope("request")
public class SOT805 extends SotPdf{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT805.class);

	@Override
	public List<String> printReport() throws JBranchException {
		String url = null;
		String txnCode = "SOT805";
		String reportID = "R1";
		ReportIF report = null;

		PRDFitInputVO inputVO = getInputVO();

		try{
			XmlInfo xmlinfo = new XmlInfo();

			Map<String, String> trustTypeMap = xmlinfo.doGetVariable("SOT.ASSET_TRADE_SUB_TYPE", FormatHelper.FORMAT_3);
			Map<String, String> transferTypeMap = xmlinfo.doGetVariable("SOT.TRANSFER_TYPE", FormatHelper.FORMAT_3);

			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sql = new StringBuffer();
			sql.append("select BATCH_SEQ,count(1) as BATCH_COUNT from TBSOT_NF_TRANSFER_D ");
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

			//資料寫入不完全
			if(list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");
			int i = 0;
			List<Map<String, Object>> data_list = null;
			List<String> url_list = new ArrayList<String>();
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO  sot712InputVO = new SOT712InputVO();
			String custID = "";
			for(Map<String, Object> map :list){
				sql = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("select M.CUST_ID,M.CUST_NAME,M.BRANCH_NBR,M.LASTUPDATE as MLASTUPDATE,D.*,D.PROSPECTUS_TYPE as DPROSPECTUSTYPE,M.REC_SEQ AS REC_SEQ "
						+ " ,CASE WHEN TO_CHAR(D.TRADE_DATE,'YYYYMMDD')=TO_CHAR(M.CREATETIME,'YYYYMMDD') THEN 'Y' ELSE 'N' END AS MEMO1, TO_CHAR(D.TRADE_DATE,'YYYY/MM/DD') as TRA_DATE "
						+ " from TBSOT_NF_TRANSFER_D D"
						+ " inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ"
						+ " left outer join TBORG_MEMBER E on E.EMP_ID = D.MODIFIER"
						+ " where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq ");
				queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
				queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
				queryCondition.setQueryString(sql.toString());

				data_list = checkList(dam.exeQuery(queryCondition));

				//資料寫入不完全
				if(data_list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");

				ReportFactory factory = new ReportFactory();
				ReportDataIF data = new ReportData();
				ReportGeneratorIF gen = factory.getGenerator();  //產出pdf
				data.addParameter("CustNo",getString(data_list.get(0).get("CUST_ID")));
				data.addParameter("BATCH_NO",getString(list.get(i).get("BATCH_SEQ")));
				i++;
				data.addParameter("CUST_ID",getString(data_list.get(0).get("CUST_ID")));
				data.addParameter("CUST_NAME",getString(data_list.get(0).get("CUST_NAME")));
				data.addParameter("BRANCH_NO",getString(data_list.get(0).get("BRANCH_NBR")));
				data.addParameter("PAY_TYPE", "連動扣款");
				data.addParameter("TRUST_ACCT", getString(data_list.get(0).get("OUT_TRUST_ACCT")));
				data.addParameter("PAY_ACCT", getString(data_list.get(0).get("FEE_DEBIT_ACCT")));

				int index = 0;
//				String total_curr1="",total_curr2="",total_curr3="";
//				BigDecimal total1 = new BigDecimal(0),total2 = new BigDecimal(0),total3 = new BigDecimal(0);
				boolean isNotVerify = false; //未核備基金警語判斷
				for(Map<String, Object> data_map : data_list){
					index++;

					data.addParameter("CERTIFICATE_ID"+index, "憑證編號　　　: "+getString(data_map.get("OUT_CERTIFICATE_ID")));
					data.addParameter("TRUST_TYPE"+index,"信託型態: "+trustTypeMap.get(getString(data_map.get("OUT_TRADE_TYPE_D"))));
					data.addParameter("TRANS_TYPE"+index,"轉換方式  ： "+transferTypeMap.get(getString(data_map.get("TRANSFER_TYPE"))));
					if(getString(data_map.get("SHORT_TYPE")).equals("1")){
						data.addParameter("SHORT"+index," ＊符合公開說明書短線認定");  //短線交易
					}else if(getString(data_map.get("SHORT_TYPE")).equals("2")){
						data.addParameter("SHORT"+index," ＊可能符合公開說明書短線認定");  //短線交易
					}

					if (data_map.get("REC_SEQ") != null && StringUtils.isNotBlank(data_map.get("REC_SEQ").toString())) {
						data.addParameter("REC_SEQ","  錄音序號："+data_map.get("REC_SEQ").toString());
					}

					String out_fund_name = "轉出基金　　　: "+ getString(data_map.get("OUT_PROD_ID")) + "	"+getString(data_map.get("OUT_PROD_NAME"));
					data.addParameter("FUND_FROM"+index, out_fund_name);  //轉出基金標的名稱

					if(StringUtils.equals(getString(data_map.get("OUT_NOT_VERTIFY")),"Y")){
						data.addParameter("NOT_VERIFY"+index, "(備註說明)");   //未核備-OBU客戶可申購未核備基金
						isNotVerify = true;
					}


					//缺短線交易欄位
//					if(StringUtils.equals(data_map.get("SHORT_TYPE").toString(), "1")){
//						data.addParameter("SHORT"+index, "＊符合公開說明書短線認定");  //短線交易判斷
//					}else if(StringUtils.equals(data_map.get("SHORT_TYPE").toString(), "2")){
//						data.addParameter("SHORT"+index, "2: ＊可能符合公開說明書短線認定");  //短線交易判斷
//					}
					if(StringUtils.isNotBlank(getString(data_map.get("IN_PROD_ID_1")))){
						data.addParameter("UNIT"+index+"1", "轉出單位數一　: "+getString(data_map.get("IN_UNIT_1")));
						data.addParameter("FUND"+index+"1", "轉入基金一　: "+getString(data_map.get("IN_PROD_ID_1"))+ "	"+getString(data_map.get("IN_PROD_NAME_1")));
						data.addParameter("RISK"+index+"1", "商品風險等級 : "+getString(data_map.get("IN_PROD_RISK_LV_1")));
					}
					if(StringUtils.isNotBlank(getString(data_map.get("IN_PROD_ID_2")))){
						data.addParameter("UNIT"+index+"2", "轉出單位數二　: "+getString(data_map.get("IN_UNIT_2")));
						data.addParameter("FUND"+index+"2", "轉入基金二　: "+getString(data_map.get("IN_PROD_ID_2"))+ "	"+getString(data_map.get("IN_PROD_NAME_2")));
						data.addParameter("RISK"+index+"2", "商品風險等級 : "+getString(data_map.get("IN_PROD_RISK_LV_2")));
					}
					if(StringUtils.isNotBlank(getString(data_map.get("IN_PROD_ID_3")))){
						data.addParameter("UNIT"+index+"3", "轉出單位數二　: "+getString(data_map.get("IN_UNIT_3")));
						data.addParameter("FUND"+index+"3", "轉入基金二　: "+getString(data_map.get("IN_PROD_ID_3"))+ "	"+getString(data_map.get("IN_PROD_NAME_3")));
						data.addParameter("RISK"+index+"3", "商品風險等級 : "+getString(data_map.get("IN_PROD_RISK_LV_3")));
					}
				}

				if(StringUtils.equals(getString(data_list.get(0).get("MEMO1")), "N")){
					data.addParameter("MEMO3", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
				}

				if(isNotVerify){
					data.addParameter("MEM04", "※備註說明：『提醒您！本基金業經暫停或終止在國內募集及銷售，如您執行此交易且交易完成後"
							+ "，依規定本行將不得再受理增加扣款日期、提高扣款金額、重新申購、轉入或變更投資標的為本基金等交易申請。』");
				}

				custID = getString(data_list.get(0).get("CUST_ID"));
//				sot712 = (SOT712) PlatformContext.getBean("sot712");
//		        sot712InputVO = new SOT712InputVO();
		        sot712InputVO.setCustID(custID);
				if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
					data.addParameter("MEMO2", "＊委託人未成年＊");
				}

				data.addParameter("OPENBOOK1", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("2")? "■" :"□");
				data.addParameter("OPENBOOK2", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("1")? "■" :"□");


				//生效日期DB沒有
				data.addParameter("EFFECT_DATE",getString(data_list.get(0).get("TRADE_DATE"))); //生效日期

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
					data.addParameter("REPORT_DATE", "中華民國  "+ (ts.getYear()-11) + "  年  " + getMonth + "  月  " + getDate +"  日");
					data.addParameter("REPORT_TIME", "時間： "+ts.getHours()+"  時  "+ts.getMinutes()+"  分  ");
				}else{
					data.addParameter("REPORT_DATE", "中華民國        年     月     日");
					data.addParameter("REPORT_TIME", "時間：      時      分");
					if (StringUtils.isNotBlank(data_list.get(0).get("TRA_DATE").toString())) {
						data.addParameter("TRADE_DATE", "表單導入有效期限至　" + data_list.get(0).get("TRA_DATE").toString());
					}
				}

				data.addParameter("CREATOR",getString(data_list.get(0).get("NARRATOR_NAME")));
				data.addParameter("CREATOR_ID",getString(data_list.get(0).get("NARRATOR_ID")));

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

	
	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}
}

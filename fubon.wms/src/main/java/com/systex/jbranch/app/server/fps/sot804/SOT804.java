package com.systex.jbranch.app.server.fps.sot804;

import com.ibm.icu.text.NumberFormat;
import com.systex.jbranch.app.server.fps.sot703.FundRule;
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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;



/**
 * 下單專區功能
 *
 * @author Jacky Wu
 * @date 2016/12/30
 * @spec 基金贖回再申購表單列印
 */

@Component("sot804")
@Scope("request")
public class SOT804 extends SotPdf{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT804.class);

	@Override
	public List<String> printReport() throws JBranchException {
		String url = null;
		String txnCode = "SOT804";
		String reportID = "R1";
		ReportIF report = null;

		PRDFitInputVO inputVO = getInputVO();

		try{
			XMLInfo XMLInfo = new XMLInfo();
            HashMap<String,BigDecimal> ex_map = XMLInfo.getExchangeRate();

			XmlInfo xmlinfo = new XmlInfo();

			Map<String, String> trustTypeMap = xmlinfo.doGetVariable("SOT.ASSET_TRADE_SUB_TYPE", FormatHelper.FORMAT_3);

			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sql = new StringBuffer();
			sql.append("select BATCH_SEQ,count(1) as BATCH_COUNT from TBSOT_NF_REDEEM_D ");
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
			List<Map<String, Object>> data_list = null;
			//資料寫入不完全
			if(list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");
			List<String> url_list = new ArrayList<String>();
			int batchNo = 0;
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO  sot712InputVO = new SOT712InputVO();
			String custID = "";
			for(Map<String, Object> map :list){
				sql = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("select M.CUST_ID,M.CUST_NAME,M.BRANCH_NBR,D.*,M.GUARDIANSHIP_FLAG,M.LASTUPDATE as MLASTUPDATE,D.PROSPECTUS_TYPE as DPROSPECTUSTYPE,M.REC_SEQ AS REC_SEQ,M.TRUST_TRADE_TYPE "
						+ " ,CASE WHEN TO_CHAR(D.TRADE_DATE,'YYYYMMDD')=TO_CHAR(D.CREATETIME,'YYYYMMDD') THEN 'Y' ELSE 'N' END AS MEMO1, TO_CHAR(D.TRADE_DATE,'YYYY/MM/DD') as TRA_DATE "
						+ " ,NVL(F.OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN "
						+ " from TBSOT_NF_REDEEM_D D"
						+ " inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ"
						+ " left outer join TBORG_MEMBER E on E.EMP_ID = D.MODIFIER"
						+ " left outer join TBPRD_FUND F on F.PRD_ID = D.RDM_PROD_ID "
						+ " where D.TRADE_SEQ = :trade_seq and D.BATCH_SEQ = :batch_seq ");
				queryCondition.setObject("trade_seq", inputVO.getTradeSeq());
				queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
				queryCondition.setQueryString(sql.toString());

				data_list = checkList(dam.exeQuery(queryCondition));

				if(data_list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");

				ReportFactory factory = new ReportFactory();
				ReportDataIF data = new ReportData();
				ReportGeneratorIF gen = factory.getGenerator();  //產出pdf
				data.addParameter("BATCH_NO", getString(list.get(batchNo).get("BATCH_SEQ")));
				batchNo++;
				data.addParameter("CustNo", getString(data_list.get(0).get("CUST_ID")));
				data.addParameter("CUST_ID", getString(data_list.get(0).get("CUST_ID"))+"  "+getString(data_list.get(0).get("CUST_NAME")) + (StringUtils.equals("Y", data_list.get(0).get("TRUST_PEOP_NUM").toString()) ? "*" : "") );
				data.addParameter("CUST_NAME",getString(data_list.get(0).get("CUST_NAME")));

				//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-26 add by Jacky
				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
					data.addParameter("CONTRACT_ID", getString(data_list.get(0).get("CONTRACT_ID")));
					reportID = "R2";
				}else{//特金交易
					data.addParameter("TRUST_ACCT", getString(data_list.get(0).get("TRUST_ACCT")));
					if(StringUtils.equals("Y", data_list.get(0).get("OVS_PRIVATE_YN").toString())) {
						reportID = "R3"; //境外私募基金
					}
				}
				data.addParameter("BRANCH_NO",getString(data_list.get(0).get("BRANCH_NBR")));

				int index = 0;
//				String total_curr1="",total_curr2="",total_curr3="";
				BigDecimal total_twd = new BigDecimal(0),total1 = new BigDecimal(0),total2 = new BigDecimal(0),total3 = new BigDecimal(0);
				boolean isNotVerify = false;
				for(Map<String, Object> data_map : data_list){
					index++;

					String fund_name = "贖回指定標的"+getSeqString(index)+": "+ (String)data_map.get("RDM_PROD_ID")
							+ "	"+(String)data_map.get("RDM_PROD_NAME");
					if(StringUtils.equals("Y", data_map.get("OVS_PRIVATE_YN").toString())) {
						//境外私募基金只會有一筆，不須顯示index
						fund_name = "贖回指定標的: "+ (String)data_map.get("RDM_PROD_ID")+ "	"+(String)data_map.get("RDM_PROD_NAME");
					}
					data.addParameter("FUND_ID"+index, fund_name);

					if(StringUtils.equals(getString(data_map.get("IS_RE_PURCHASE")), "Y")){
						String tpp_slp = String.valueOf(data_map.get("TAKE_PROFIT_PERC")) + "%/"
								+ String.valueOf(data_map.get("STOP_LOSS_PERC")) + "%";
						data.addParameter("P1_TPP_SLP"+index, "滿足/停損點通知 :" + tpp_slp);
					}

					if(StringUtils.equals(getString(data_map.get("NOT_VERTIFY")),"Y")){
						data.addParameter("NOT_VERIFY"+index, "(備註說明)");
						isNotVerify = true;
					}

					if(StringUtils.equals(getString(data_map.get("SHORT_TYPE")), "1")){
						data.addParameter("SHORT"+index, "＊符合公開說明書短線認定");  //短線交易判斷
					}else if(StringUtils.equals(getString(data_map.get("SHORT_TYPE")), "2")){
//						data.addParameter("SHORT"+index, "2: ＊可能符合公開說明書短線認定");  //短線交易判斷
						data.addParameter("SHORT"+index, "＊可能符合公開說明書短線認定");  //短線交易判斷
					}

					if (data_map.get("REC_SEQ") != null && StringUtils.isNotBlank(data_map.get("REC_SEQ").toString())) {
						data.addParameter("REC_SEQ","  錄音序號："+data_map.get("REC_SEQ").toString());
					}

					data.addParameter("CERTIFICATE_ID"+index, "憑證編號　　　: "+getString(data_map.get("RDM_CERTIFICATE_ID")));
					data.addParameter("TRUST_TYPE"+index,"信託型態: "+trustTypeMap.get(getString(data_map.get("RDM_TRADE_TYPE_D"))));
					data.addParameter("REDEEM_TYPE"+index,StringUtils.equals(getString(data_map.get("REDEEM_TYPE")), "1") ? "贖回方式 : 全部贖回" : "贖回方式: 部份贖回");
					double change_unit = Double.parseDouble(getString(data_map.get("UNIT_NUM")));
//					data.addParameter("UNIT"+index, "贖回單位數　　: "+NumberFormat.getNumberInstance(Locale.US).format(change_unit));
					NumberFormat format = NumberFormat.getInstance(Locale.US);
					format.setMaximumFractionDigits(4);
					data.addParameter("UNIT"+index, "贖回單位數　　: "+ format.format(change_unit));

					data.addParameter("INCOME_ACCT"+index, "贖回入帳帳號　: "+getString(data_map.get("CREDIT_ACCT")));

					if (FundRule.isMultipleOrFundProject(getString(data_map.get("RDM_TRADE_TYPE")))) {
						//主推基金且全部贖回才需顯示此欄位於報表上 20170906add
						//【主推基金-全部贖回】IS_END_CERTIFICATE 才會有值(Y or N)，其餘為null
						if (getString(data_map.get("IS_END_CERTIFICATE")).matches("Y|N")) {
							data.addParameter("DEDUCT"+index, StringUtils.equals(getString(data_map.get("IS_END_CERTIFICATE")), "Y") ?
									"是否續扣 : 否" : " 是否續扣 : 是");
						}
					}

					if(StringUtils.equals(getString(data_map.get("IS_RE_PURCHASE")), "Y")){
						data.addParameter("BACK_BUY"+index, "■ 本次贖回指定標的款項入帳後，再申購下項投資標的，並授權自上列贖回入帳帳號扣款：");
						String back_fund_name = "投資標的名稱　: "+ getString(data_map.get("PCH_PROD_ID"))
								+ "	"+getString(data_map.get("PCH_PROD_NAME"));
						data.addParameter("BACK_FUND"+index, back_fund_name);
						data.addParameter("RAM_TYPE"+index, "商品風險等級 : "+getString(data_map.get("PCH_PROD_RISK_LV")));
						data.addParameter("BACK_ACCT"+index, "收益入帳帳號　: "+getString(data_map.get("CREDIT_ACCT")));
						data.addParameter("BACK_RATE"+index, "手續費率 : "+getString(data_map.get("FEE_RATE"))+"%");

						total_twd = total_twd.add(new BigDecimal(getString(data_map.get("PRESENT_VAL"))).multiply(ex_map.get(getString(data_map.get("RDM_TRUST_CURR")))));
					}
				}

				if(StringUtils.equals(getString(data_list.get(0).get("MEMO1")), "N")){
					data.addParameter("MEMO3", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
				}

				if(isNotVerify){
					data.addParameter("MEMO4", "※備註說明：『提醒您！本基金業經暫停或終止在國內募集及銷售，如您執行此交易且交易完成後"
							+ "，依規定本行將不得再受理增加扣款日期、提高扣款金額、重新申購、轉入或變更投資標的為本基金等交易申請。』");
				}
				custID = getString(data_list.get(0).get("CUST_ID"));
//				sot712 = (SOT712) PlatformContext.getBean("sot712");
//		        sot712InputVO = new SOT712InputVO();
		        sot712InputVO.setCustID(custID);
				if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
					data.addParameter("MEMO2", "＊委託人未成年＊");
					data.addParameter("MEMO5", "＊委託人未成年＊");
					data.addParameter("MEMO6", "＊委託人未成年＊");
				}
				data.addParameter("OPENBOOK2", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("2")? "■" :"□");
				data.addParameter("OPENBOOK1", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("1")? "■" :"□");

				if(total_twd.compareTo(new BigDecimal(1000000)) > -1){
					data.addParameter("AUTH_FLAG", "主管:_________ 經辦:_________ 驗印:_________ 主管驗印:_________ 親見本人親簽無誤:_________");
				}else{
					data.addParameter("AUTH_FLAG", "主管:______________ 經辦:______________ 驗印:______________ 親見本人親簽無誤:______________");
				}
				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
					if(total_twd.compareTo(new BigDecimal(1000000)) > -1){
						data.addParameter("AUTH_FLAG_M", "受理單位:" + getString(data_list.get(0).get("BRANCH_NBR")) + " 文件覆核主管:____________ 文件覆核經辦:____________ 驗印:____________ 主管驗印:____________");
					}else{
						data.addParameter("AUTH_FLAG_M", "受理單位:" + getString(data_list.get(0).get("BRANCH_NBR")) + " 文件覆核主管:_________________ 文件覆核經辦:_________________ 驗印:_________________");
					}
				}
				data.addParameter("EFFECT_DATE",getString(data_list.get(0).get("TRADE_DATE"))); //生效日期

				String getMonth = "";
				String getDate = "";
				java.sql.Timestamp ts = (java.sql.Timestamp) data_list.get(0).get("MLASTUPDATE");
				getMonth = (ts.getMonth()+1)+"";
				if (getMonth.length() < 2){
					getMonth = "0"+getMonth;
				}
				getDate = String.valueOf(ts.getDate());
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
				data.addParameter("GUARDIANSHIP_FLAG", "");
				if(StringUtils.equals(data_list.get(0).get("TRUST_TRADE_TYPE").toString(), "M")){//金錢信託
					data.addParameter("GUARDIANSHIP_FLAG", data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("1") ? "*受監護宣告*" : data_list.get(0).get("GUARDIANSHIP_FLAG").toString().equals("2") ? "*受輔助宣告*" : " " );
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

			//列印報表時，根據判斷加入[後收型基金申購約定書]	//#5662 後收型贖回不用帶出[後收型基金申購約定書]
//			if(StringUtils.isNotBlank(inputVO.getIsbBackend()) && "Y".equals(inputVO.getIsbBackend())){
//				SOT801 sot801 = new SOT801();
//				url_list.addAll(sot801.getSOT815PDF());
//			}

			return url_list;
		}catch(Exception e){
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	private String getSeqString(int seq) throws JBranchException {
		if(seq < 0 || seq > 10){
			throw new APException("傳入的值不正確");
		}

		String seqStr = "";
	    switch(seq){
	    case 1 : seqStr = "一";break;
	    case 2 : seqStr = "二";break;
	    case 3 : seqStr = "三";break;
	    case 4 : seqStr = "四";break;
	    case 5 : seqStr = "五";break;
	    case 6 : seqStr = "六";break;
	    case 7 : seqStr = "七";break;
	    case 8 : seqStr = "八";break;
	    case 9 : seqStr = "九";break;
	    case 10 : seqStr = "十";break;
		}
		return seqStr;
	}

	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}
}

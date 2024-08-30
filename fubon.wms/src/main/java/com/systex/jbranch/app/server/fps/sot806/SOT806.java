package com.systex.jbranch.app.server.fps.sot806;

import com.systex.jbranch.app.server.fps.sot703.FundRule;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * 下單專區功能
 *
 * @author Jacky Wu
 * @date 2016/12/30
 * @spec 基金事件變更表單列印
 */

@Component("sot806")
@Scope("request")
public class SOT806 extends SotPdf{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT806.class);
	String url = null;
	String txnCode = "SOT806";
	ReportIF report = null;
	ReportFactory factory = new ReportFactory();
	String pattern = "%1$-30s";
	List<String> url_list = null;
	@Override
	public List<String> printReport() throws JBranchException {
		//每當printReoport被呼叫時，必須重新建立一個list，避免重複問題 20170814add
		url_list = new ArrayList<String>();
		this.reportLogic();
		return url_list;
	}
	public void reportLogic() throws JBranchException {
		PRDFitInputVO inputVO = getInputVO();
		List<Map<String, Object>> data_list = null;

		try{
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuffer sql = new StringBuffer();
			sql.append("select BATCH_SEQ,count(1) as BATCH_COUNT ");
			sql.append("from TBSOT_NF_CHANGE_BATCH ");
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
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO  sot712InputVO = new SOT712InputVO();
			String custID = "";
			for(Map<String, Object> map :list){
				sql = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql.append("select M.CUST_ID,M.CUST_NAME,M.BRANCH_NBR,D.PROSPECTUS_TYPE as DPROSPECTUSTYPE,D.*,B.BATCH_SEQ,B.CHANGE_TYPE as ITEM_TYPE,M.LASTUPDATE as MLASTUPDATE,M. REC_SEQ,  "
						+ " CASE WHEN TO_CHAR(D.TRADE_DATE,'YYYYMMDD')=TO_CHAR(D.CREATETIME,'YYYYMMDD') THEN 'Y' ELSE 'N' END AS MEMO1,  TO_CHAR(D.TRADE_DATE,'YYYY/MM/DD') as TRA_DATE "
						+ " from TBSOT_NF_CHANGE_BATCH B "
						+ " inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = B.TRADE_SEQ "
						+ " inner join TBSOT_NF_CHANGE_D D on "
						+ " D.TRADE_SEQ = B.TRADE_SEQ and D.SEQ_NO = B.SEQ_NO "
						+ " where B.BATCH_SEQ = :batch_seq ");
				queryCondition.setObject("batch_seq", (String)map.get("BATCH_SEQ"));
				queryCondition.setQueryString(sql.toString());

				/********************** ITEM_TYPE 呼叫的ReportID
				 * A9：扣款日期變更             ----> R1(CSMFT131.java)
				 * A8：扣款帳號變更             ----> R2(CSMFT132.java)
				 * AX：標的變更                    ----> R3(CSMFT133.java)
				 * A7：金額變更　　　　　　----> R3(CSMFT133.java)
				 * X7：標的與金額變更　　　----> R3(CSMFT133.java)
				 * B2：恢復扣款變更		  ----> R1(CSMFT135.java)
				 * B1：暫停扣款變更		  ----> R1(CSMFT134.java)
				 * B8：終止憑證變更		  ----> R1(CSMFT136.java/CSMFT138.java)
				 * A0：新收益入帳帳號變更	  ----> R1(CSMFT137.java)
				**********************/
				data_list = checkList(dam.exeQuery(queryCondition));

				if(data_list.isEmpty()) throw new APException("系統發生錯誤請洽系統管理員");

				ReportDataIF data = new ReportData();

				data.addParameter("CustNo",data_list.get(0).get("CUST_ID").toString());
				data.addParameter("BATCH_NO",list.get(i).get("BATCH_SEQ").toString());
				i++;
				data.addParameter("CUST_ID",data_list.get(0).get("CUST_ID").toString());
				data.addParameter("CUST_NAME",data_list.get(0).get("CUST_NAME").toString());
				data.addParameter("BRANCH_NBR",data_list.get(0).get("BRANCH_NBR").toString());
				data.addParameter("TRUST_ACCT", data_list.get(0).get("B_TRUST_ACCT").toString());

				if(data_list.get(0).get("REC_SEQ")!= null && StringUtils.isNotBlank(data_list.get(0).get("REC_SEQ").toString())){
					data.addParameter("REC_SEQ","   錄音序號："+data_list.get(0).get("REC_SEQ").toString());
				}
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

				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "A9")){
					this.printR1A9(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "A8")){
					this.printR2A8(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "AX")){
					this.printR3AX(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "X7")){
					this.printR3X7(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "A7")){
					this.printR3A7(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "B2")){
					this.printR1B2(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "B1")){
					this.printR1B1(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "B8")){
					this.printR1B8(data_list, data);
				}
				if(StringUtils.equals(data_list.get(0).get("ITEM_TYPE").toString(), "A0")){
					this.printR1A0(data_list, data);
				}
			}

		}catch(Exception e){
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * A9：扣款日期變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR1A9(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R1";

		int index = 0 , rowcount = 0;

		for(Map<String, Object> data_map : data_list){
			index++;
			rowcount++;
			if(data_map.get("CERTIFICATE_ID").toString() != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("DATA"+index, "0"+rowcount+" "
						+ String.format(pattern,"信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString())
						+" 扣款標的："+ data_map.get("B_PROD_ID").toString() +" "+ data_map.get("B_PROD_NAME").toString());
			}
			index++;
			String before1 = "",after1="";
			for(int i=1;i<7;i++){ //交易日期可以選擇六筆，原先只能報表只能顯示五筆 ，20170814add
				if(data_map.get("B_CHARGE_DATE_"+i) != null && StringUtils.isNotBlank(data_map.get("B_CHARGE_DATE_"+i).toString())){
					before1 += data_map.get("B_CHARGE_DATE_"+i).toString()+"　" ;
				}
				if(data_map.get("F_CHARGE_DATE_"+i) != null && StringUtils.isNotBlank(data_map.get("F_CHARGE_DATE_"+i).toString())){
					after1 += data_map.get("F_CHARGE_DATE_"+i).toString()+"　" ;
				}
			}
			data.addParameter("DATA"+index, "   "+String.format(pattern,"原指定扣款日："+ before1) + "變更後指定扣款日：" +after1);
		}

		data.addParameter("TITLE", "【扣款日期變更】");

		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}

		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}

		addCreatorParameters(data_list, data);
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

	/**
	 * A8：扣款帳號變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR2A8(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R2";
		int index = 0 , rowcount = 0;
		boolean isNotVerify = false;

		for(Map<String, Object> data_map : data_list){
			index++;
			if(data_map.get("CERTIFICATE_ID")!= null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("CERTIFICATE_ID"+index, "0"+index+" 信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString());
			}
			if(data_map.get("B_PROD_ID") != null && StringUtils.isNotBlank(data_map.get("B_PROD_ID").toString())){
				data.addParameter("FUND_ID"+index, "扣款標的："+ data_map.get("B_PROD_ID").toString() +" "+ data_map.get("B_PROD_NAME").toString());
			}
			if(data_map.get("B_DEBIT_ACCT") != null && StringUtils.isNotBlank(data_map.get("B_DEBIT_ACCT").toString())){
				data.addParameter("BEF1_"+index, "   原授權扣款存款帳號    ："+  data_map.get("B_DEBIT_ACCT").toString());
			}
			if(data_map.get("CUST_ID") != null && StringUtils.isNotBlank(data_map.get("CUST_ID").toString())){
				data.addParameter("BEF2_"+index, "原授權扣款人身分證字號    ："+  data_map.get("CUST_ID").toString());
				data.addParameter("AFT2_"+index, "變更後授權扣款人身分證字號："+  data_map.get("CUST_ID").toString());
			}
			if(data_map.get("F_DEBIT_ACCT") != null && StringUtils.isNotBlank(data_map.get("F_DEBIT_ACCT").toString())){
				data.addParameter("AFT1_"+index, "   變更後授權扣款存款帳號："+  data_map.get("F_DEBIT_ACCT").toString());
			}
		}

		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}

		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}

		addCreatorParameters(data_list, data);

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
	/**
	 * AX：標的變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR3AX(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R3";

		XmlInfo xmlinfo = new XmlInfo();
		Map<String, String> trustTypeMap = xmlinfo.doGetVariable("SOT.ASSET_TRADE_SUB_TYPE", FormatHelper.FORMAT_3);

		int index = 0 , rowcount = 0;
		boolean isNotVerify = false;
		DecimalFormat formatter = new DecimalFormat("#,###.##");
		for(Map<String, Object> data_map : data_list){
			index++;
			if(data_map.get("CERTIFICATE_ID") != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("CERTIFICATE_ID"+index, "【扣款標的變更】　　　　信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString());
			}
			if(data_map.get("TRADE_SUB_TYPE_D") != null && StringUtils.isNotBlank(data_map.get("TRADE_SUB_TYPE_D").toString())){
				data.addParameter("TRUST_TYPE"+index, "信託型態："+trustTypeMap.get(data_map.get("TRADE_SUB_TYPE_D").toString()));
			}
			data.addParameter("MARK1_"+index, "■");
			if(data_map.get("B_PROD_ID") != null && StringUtils.isNotBlank(data_map.get("B_PROD_ID").toString())){
				data.addParameter("BFT1_"+index, "原扣款標的："+data_map.get("B_PROD_ID").toString()+" "+ data_map.get("B_PROD_NAME").toString());
			}
			if(data_map.get("B_NOT_VERTIFY") != null && StringUtils.equals(data_map.get("B_NOT_VERTIFY").toString(),"Y")){
				data.addParameter("NOT_VERIFY"+index, "(備註說明)");   //未核備-OBU客戶可申購未核備基金
				isNotVerify = true;
			}
			if(data_map.get("F_PROD_ID") != null && StringUtils.isNotBlank(data_map.get("F_PROD_ID").toString())){
				data.addParameter("AFT1_"+index, "變更後扣款標的："+data_map.get("F_PROD_ID").toString()+" "+ data_map.get("F_PROD_NAME").toString());
			}
			if(data_map.get("F_PROD_RISK_LV") != null && StringUtils.isNotBlank(data_map.get("F_PROD_RISK_LV").toString())){
				data.addParameter("RAM_TYPE"+index, "商品風險等級："+data_map.get("F_PROD_RISK_LV").toString());
			}
			data.addParameter("MARK2_"+index, "");
			if(data_map.get("B_TRUST_CURR") != null && StringUtils.isNotBlank(data_map.get("B_TRUST_CURR").toString())){
				data.addParameter("BFT2_"+index, "幣別："+  data_map.get("B_TRUST_CURR").toString());
			}

			if(data_map.get("F_TRUST_CURR") != null && StringUtils.isNotBlank(data_map.get("F_TRUST_CURR").toString())){
				data.addParameter("AFT2_"+index, "幣別："+  data_map.get("F_TRUST_CURR").toString());
			}


			String tradeSubType = data_map.get("TRADE_SUB_TYPE").toString();
			if (FundRule.isMultipleN(tradeSubType)){
				data.addParameter("BFT3_"+index, "原每次扣款金額    ：(低)"+  formatter.format(data_map.get("B_PURCHASE_AMT_L"))+"　　(中)"+formatter.format(data_map.get("B_PURCHASE_AMT_M"))+"　　(高)"+formatter.format((data_map.get("B_PURCHASE_AMT_H"))));
				data.addParameter("AFT3_"+index, "變更後每次扣款金額：(低)"+  formatter.format(data_map.get("F_PURCHASE_AMT_L"))+"　　(中)"+formatter.format(data_map.get("F_PURCHASE_AMT_M"))+"　　(高)"+formatter.format(data_map.get("F_PURCHASE_AMT_H")));
			}else if (FundRule.isMultiple(tradeSubType)){
				data.addParameter("BFT3_"+index, "原每次扣款金額　　："+  formatter.format(data_map.get("B_PURCHASE_AMT_L")));
				data.addParameter("AFT3_"+index, "變更後每次扣款金額："+  formatter.format(data_map.get("F_PURCHASE_AMT_L")));
			}

		}

		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}

		if(isNotVerify){
			data.addParameter("MEMO2", "※備註說明：『提醒您！本基金業經暫停或終止在國內募集及銷售，如您執行此交易且交易完成後"
					+ "，依規定本行將不得再受理增加扣款日期、提高扣款金額、重新申購、轉入或變更投資標的為本基金等交易申請。』");
		}

		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}

		if(data_list.get(0).get("DPROSPECTUSTYPE") != null && StringUtils.isNotBlank(data_list.get(0).get("DPROSPECTUSTYPE").toString())){
			data.addParameter("WORDING1_2", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("1")? "□ 已取得，本次毋須再交付　■ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得" :"■ 已取得，本次毋須再交付　□ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得");
		}
		if(data_list.get(0).get("NARRATOR_ID") != null && StringUtils.isNotBlank(data_list.get(0).get("NARRATOR_ID").toString())){
			addCreatorParameters(data_list, data);
		}
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

	/**
	 * A7：金額變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR3A7(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R3";
		XmlInfo xmlinfo = new XmlInfo();
		Map<String, String> trustTypeMap = xmlinfo.doGetVariable("SOT.ASSET_TRADE_SUB_TYPE", FormatHelper.FORMAT_3);
		int index = 0 , rowcount = 0;
		boolean isNotVerify = false;
		DecimalFormat formatter = new DecimalFormat("#,###.##");
		for(Map<String, Object> data_map : data_list){
			index++;
			if(data_map.get("CERTIFICATE_ID") != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("CERTIFICATE_ID"+index, "【扣款金額變更】　　　　信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString());
			}
			if(data_map.get("TRADE_SUB_TYPE_D") != null && StringUtils.isNotBlank(data_map.get("TRADE_SUB_TYPE_D").toString())){
				data.addParameter("TRUST_TYPE"+index, "信託型態："+trustTypeMap.get(data_map.get("TRADE_SUB_TYPE_D").toString()));
			}
			data.addParameter("MARK1_"+index, "");
			if(data_map.get("B_PROD_ID") != null && StringUtils.isNotBlank(data_map.get("B_PROD_ID").toString())){
				data.addParameter("BFT1_"+index, "原扣款標的："+data_map.get("B_PROD_ID").toString()+" "+ data_map.get("B_PROD_NAME").toString());
			}
			if(data_map.get("B_NOT_VERTIFY") != null && StringUtils.equals(data_map.get("B_NOT_VERTIFY").toString(),"Y")){
				data.addParameter("NOT_VERIFY"+index, "(備註說明)");
				isNotVerify = true;
			}
			if(data_map.get("F_PROD_ID") != null && StringUtils.isNotBlank(data_map.get("F_PROD_ID").toString())){
				data.addParameter("AFT1_"+index, "變更後扣款標的："+data_map.get("F_PROD_ID").toString()+" "+ data_map.get("F_PROD_NAME").toString());
			}
			if(data_map.get("F_PROD_RISK_LV") != null && StringUtils.isNotBlank(data_map.get("F_PROD_RISK_LV").toString())){
				data.addParameter("RAM_TYPE"+index, "商品風險等級："+data_map.get("F_PROD_RISK_LV").toString());
			}
			data.addParameter("MARK2_"+index, "■");
			if(data_map.get("B_TRUST_CURR") != null && StringUtils.isNotBlank(data_map.get("B_TRUST_CURR").toString())){
				data.addParameter("BFT2_"+index, "幣別："+  data_map.get("B_TRUST_CURR").toString());
			}

			if(data_map.get("F_TRUST_CURR") != null && StringUtils.isNotBlank(data_map.get("F_TRUST_CURR").toString())){
				data.addParameter("AFT2_"+index, "幣別："+  data_map.get("F_TRUST_CURR").toString());
			}


			String tradeSubType = data_map.get("TRADE_SUB_TYPE").toString();
			if (FundRule.isMultipleN(tradeSubType)){
				data.addParameter("BFT3_"+index, "原每次扣款金額    ：(低)"+  formatter.format(data_map.get("B_PURCHASE_AMT_L"))+"　　(中)"+formatter.format(data_map.get("B_PURCHASE_AMT_M"))+"　　(高)"+formatter.format((data_map.get("B_PURCHASE_AMT_H"))));
				data.addParameter("AFT3_"+index, "變更後每次扣款金額：(低)"+  formatter.format(data_map.get("F_PURCHASE_AMT_L"))+"　　(中)"+formatter.format(data_map.get("F_PURCHASE_AMT_M"))+"　　(高)"+formatter.format(data_map.get("F_PURCHASE_AMT_H")));
			}else if (FundRule.isMultiple(tradeSubType)){
				data.addParameter("BFT3_"+index, "原每次扣款金額　　："+  formatter.format(data_map.get("B_PURCHASE_AMT_L")));
				data.addParameter("AFT3_"+index, "變更後每次扣款金額："+  formatter.format(data_map.get("F_PURCHASE_AMT_L")));
			}

		}
		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}
		if(isNotVerify){
			data.addParameter("MEMO2", "※備註說明：『提醒您！本基金業經暫停或終止在國內募集及銷售，如您執行此交易且交易完成後"
					+ "，依規定本行將不得再受理增加扣款日期、提高扣款金額、重新申購、轉入或變更投資標的為本基金等交易申請。』");
		}
		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}
		if(data_list.get(0).get("DPROSPECTUSTYPE") != null && StringUtils.isNotBlank(data_list.get(0).get("DPROSPECTUSTYPE").toString())){
			data.addParameter("WORDING1_2", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("1")? "　□ 已取得，本次毋須再交付　　　■ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得" :"　■ 已取得，本次毋須再交付　　　□ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得");
		}
		if(data_list.get(0).get("NARRATOR_ID") != null && StringUtils.isNotBlank(data_list.get(0).get("NARRATOR_ID").toString())){
			addCreatorParameters(data_list, data);
		}
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
	/**
	 * X7：標的與金額變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR3X7(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R3";
		XmlInfo xmlinfo = new XmlInfo();
		Map<String, String> trustTypeMap = xmlinfo.doGetVariable("SOT.ASSET_TRADE_SUB_TYPE", FormatHelper.FORMAT_3);
		int index = 0 , rowcount = 0;
		boolean isNotVerify = false;
		DecimalFormat formatter = new DecimalFormat("#,###.##");
		for(Map<String, Object> data_map : data_list){
			index++;
			if(data_map.get("CERTIFICATE_ID") != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("CERTIFICATE_ID"+index, "【扣款標的 / 金額變更】　　　　信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString());
			}
			if(data_map.get("TRADE_SUB_TYPE_D") != null && StringUtils.isNotBlank(data_map.get("TRADE_SUB_TYPE_D").toString())){
				data.addParameter("TRUST_TYPE"+index, "信託型態："+trustTypeMap.get(data_map.get("TRADE_SUB_TYPE_D").toString()));
			}
			data.addParameter("MARK1_"+index, "■");
			if(data_map.get("B_PROD_ID") != null && StringUtils.isNotBlank(data_map.get("B_PROD_ID").toString())){
				data.addParameter("BFT1_"+index, "原扣款標的："+data_map.get("B_PROD_ID").toString()+" "+ data_map.get("B_PROD_NAME").toString());
			}
			if(data_map.get("B_NOT_VERTIFY") != null && StringUtils.equals(data_map.get("B_NOT_VERTIFY").toString(),"Y")){
				data.addParameter("NOT_VERIFY"+index, "(備註說明)");
				isNotVerify = true;
			}
			if(data_map.get("F_PROD_ID") != null && StringUtils.isNotBlank(data_map.get("F_PROD_ID").toString())){
				data.addParameter("AFT1_"+index, "變更後扣款標的："+data_map.get("F_PROD_ID").toString()+" "+ data_map.get("F_PROD_NAME").toString());
			}
			if(data_map.get("F_PROD_RISK_LV") != null && StringUtils.isNotBlank(data_map.get("F_PROD_RISK_LV").toString())){
				data.addParameter("RAM_TYPE"+index, "商品風險等級："+data_map.get("F_PROD_RISK_LV").toString());
			}
			data.addParameter("MARK2_"+index, "■");
			if(data_map.get("B_TRUST_CURR") != null && StringUtils.isNotBlank(data_map.get("B_TRUST_CURR").toString())){
				data.addParameter("BFT2_"+index, "幣別："+  data_map.get("B_TRUST_CURR").toString());
			}

			if(data_map.get("F_TRUST_CURR") != null && StringUtils.isNotBlank(data_map.get("F_TRUST_CURR").toString())){
				data.addParameter("AFT2_"+index, "幣別："+  data_map.get("F_TRUST_CURR").toString());
			}
			String tradeSubType = data_map.get("TRADE_SUB_TYPE").toString();
			if (FundRule.isMultipleN(tradeSubType)){
				data.addParameter("BFT3_"+index, "原每次扣款金額    ：(低)"+  formatter.format(data_map.get("B_PURCHASE_AMT_L"))+"　　(中)"+formatter.format(data_map.get("B_PURCHASE_AMT_M"))+"　　(高)"+formatter.format((data_map.get("B_PURCHASE_AMT_H"))));
				data.addParameter("AFT3_"+index, "變更後每次扣款金額：(低)"+  formatter.format(data_map.get("F_PURCHASE_AMT_L"))+"　　(中)"+formatter.format(data_map.get("F_PURCHASE_AMT_M"))+"　　(高)"+formatter.format(data_map.get("F_PURCHASE_AMT_H")));
			}else if (FundRule.isMultiple(tradeSubType)){
				data.addParameter("BFT3_"+index, "原每次扣款金額　　："+  formatter.format(data_map.get("B_PURCHASE_AMT_L")));
				data.addParameter("AFT3_"+index, "變更後每次扣款金額："+  formatter.format(data_map.get("F_PURCHASE_AMT_L")));
			}

		}
		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}
		if(isNotVerify){
			data.addParameter("MEMO2", "※備註說明：『提醒您！本基金業經暫停或終止在國內募集及銷售，如您執行此交易且交易完成後"
					+ "，依規定本行將不得再受理增加扣款日期、提高扣款金額、重新申購、轉入或變更投資標的為本基金等交易申請。』");
		}
		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}
		if(data_list.get(0).get("DPROSPECTUSTYPE") != null && StringUtils.isNotBlank(data_list.get(0).get("DPROSPECTUSTYPE").toString())){
			data.addParameter("WORDING1_2", getString(data_list.get(0).get("DPROSPECTUSTYPE")).equals("1")? "　□ 已取得，本次毋須再交付　　　■ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得" :"　■ 已取得，本次毋須再交付　　　□ 已自行透過基金經理公司網站、貴行官網或境外基金資訊觀測站取得");
		}
		if(data_list.get(0).get("NARRATOR_ID") != null && StringUtils.isNotBlank(data_list.get(0).get("NARRATOR_ID").toString())){
			addCreatorParameters(data_list, data);
		}
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

	/**
	 * B2：恢復扣款變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR1B2(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R1";
		int index = 0 , rowcount = 0;
		for(Map<String, Object> data_map : data_list){
			index++;
			rowcount++;
			if(data_map.get("CERTIFICATE_ID") != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("DATA"+index, "0"+rowcount+" "
						+ String.format(pattern,"信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString())
						+"扣款標的："+ data_map.get("B_PROD_ID").toString() +" "+ data_map.get("B_PROD_NAME").toString());
			}
			index++;
			String getMonth = "";
			String getDate = "";
			java.sql.Timestamp ts = (java.sql.Timestamp) data_list.get(0).get("F_RESUME_DATE");
			getMonth = (ts.getMonth()+1)+"";
			getDate = ts.getDate()+"";
			if (getMonth.length() < 2){
				getMonth = "0"+getMonth;
			}
			if (getDate.length() < 2){
				getDate = "0"+getDate;
			}

			if(data_map.get("F_RESUME_DATE") != null && StringUtils.isNotBlank(data_map.get("F_RESUME_DATE").toString())){
				data.addParameter("DATA"+index, "恢復扣款起始日(民國年)："+ (ts.getYear()-11) + "/" + getMonth + "/" + getDate);
			}
		}
		data.addParameter("TITLE", "【恢復扣款】");
		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}
		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}
		if(data_list.get(0).get("NARRATOR_ID") != null && StringUtils.isNotBlank(data_list.get(0).get("NARRATOR_ID").toString())){
			addCreatorParameters(data_list, data);
		}
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
	/**
	 * B1：暫停扣款變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR1B1(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R1";
		int index = 0 , rowcount = 0;
		boolean isNotVerify = false;
		for(Map<String, Object> data_map : data_list){
			index++;
			rowcount++;
			String notVerify = "";
			if(data_map.get("B_NOT_VERTIFY")!= null && StringUtils.equals(data_map.get("B_NOT_VERTIFY").toString(), "Y")){
				notVerify = "(備註說明)";
				isNotVerify = true;
			}
			if(data_map.get("CERTIFICATE_ID") != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("DATA"+index, "0"+rowcount+" "+ String.format(pattern," 信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString())
						+"扣款標的："+ data_map.get("B_PROD_ID").toString() +" "+ data_map.get("B_PROD_NAME").toString()+ notVerify);
			}
			index++;
			String getMonth = "";
			String getDate = "";
			java.sql.Timestamp ts = (java.sql.Timestamp) data_list.get(0).get("F_HOLD_START_DATE");
			getMonth = (ts.getMonth()+1)+"";
			getDate = ts.getDate()+"";
			if (getMonth.length() < 2){
				getMonth = "0"+getMonth;
			}
			if (getDate.length() < 2){
				getDate = "0"+getDate;
			}
			if(data_map.get("F_HOLD_START_DATE") != null && StringUtils.isNotBlank(data_map.get("F_HOLD_START_DATE").toString())){
				data.addParameter("DATA"+index, "暫停扣款起始日(民國年)："+ (ts.getYear()-11) + "/" + getMonth + "/" + getDate);
			}
		}
		data.addParameter("TITLE", "【暫停扣款】");
		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}
		if(isNotVerify){
			data.addParameter("MEMO2", "※備註說明：『提醒您！本基金業經暫停或終止在國內募集及銷售，如您執行此交易且交易完成後"
					+ "，依規定本行將不得再受理增加扣款日期、提高扣款金額、重新申購、轉入或變更投資標的為本基金等交易申請。』");
		}
		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}
		addCreatorParameters(data_list, data);
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
	/**
	 * B8：終止憑證變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR1B8(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();
		String reportID = "R1";
		int index = 0 , rowcount = 0;
		boolean isNotVerify = false;
		for(Map<String, Object> data_map : data_list){
			index++;rowcount++;
			if(data_map.get("CERTIFICATE_ID") != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("DATA"+index, "0"+rowcount+" "+ String.format(pattern," 信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString())
						+"扣款標的："+ data_map.get("B_PROD_ID").toString() +" "+ data_map.get("B_PROD_NAME").toString());
			}
			index++;
		}
		data.addParameter("TITLE", "【契約終止】");
		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}

		if(isNotVerify){
			data.addParameter("MEMO2", "※備註說明：『提醒您！本基金業經暫停或終止在國內募集及銷售，如您執行此交易且交易完成後"
					+ "，依規定本行將不得再受理增加扣款日期、提高扣款金額、重新申購、轉入或變更投資標的為本基金等交易申請。』");
		}

		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}


		addCreatorParameters(data_list, data);

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

	/** 加入員工姓名及編號 **/
	private void addCreatorParameters(List<Map<String, Object>> data_list, ReportDataIF data) {
		data.addParameter("CREATOR", data_list.get(0).get("NARRATOR_NAME").toString());
		data.addParameter("CREATOR_ID", data_list.get(0).get("NARRATOR_ID").toString());
	}

	/**
	 * A0：新收益入帳帳號變更
	 * @param data_list
	 * @param data
	 * @throws JBranchException
	 */
	private void printR1A0(List<Map<String,Object>> data_list,ReportDataIF data) throws JBranchException {
		ReportGeneratorIF gen = factory.getGenerator();  //產出pdf
		String reportID = "R1";
		int index = 0 , rowcount = 0;

		for(Map<String, Object> data_map : data_list){
			index++;
			rowcount++;

			if(data_map.get("CERTIFICATE_ID") != null && StringUtils.isNotBlank(data_map.get("CERTIFICATE_ID").toString())){
				data.addParameter("DATA"+index, "0"+rowcount+" "+ String.format(pattern," 信託憑證號碼："+data_map.get("CERTIFICATE_ID").toString())
						+"扣款標的："+ data_map.get("B_PROD_ID").toString() +" "+ data_map.get("B_PROD_NAME").toString());
			}
			index++;
			if(data_map.get("B_CREDIT_ACCT") != null && StringUtils.isNotBlank(data_map.get("B_CREDIT_ACCT").toString())){
				data.addParameter("DATA"+index, "   "+String.format(pattern," 原收益入帳存款帳號    ："+ data_map.get("B_CREDIT_ACCT").toString())
							+ "   變更後收益入帳存款帳號："+ data_map.get("F_CREDIT_ACCT").toString()	);
			}
		}
		data.addParameter("TITLE", "【新收益入帳帳號變更】");

		if(StringUtils.equals(data_list.get(0).get("MEMO1").toString(), "N")){
			data.addParameter("MEMO1", "＊＊如生效日期遇颱風、地震等不可抗力因素致暫停營業，委託人同意順延至次一營業日辦理。");
		}

		String custID = data_list.get(0).get("CUST_ID").toString();
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
        SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(custID);
		if(StringUtils.length(custID) > 8 && sot712.getCUST_AGE(sot712InputVO) < 18){
			data.addParameter("MEMO3", "＊委託人未成年＊");
		}

		addCreatorParameters(data_list, data);

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

	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}
}

package com.systex.jbranch.app.server.fps.crm614;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 27/5/2016
 * 
 */
@Component("crm614")
@Scope("request")
public class CRM614 extends FubonWmsBizLogic {

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM614InputVO inputVO = (CRM614InputVO) body;
		CRM614OutputVO outputVO = new CRM614OutputVO();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DATA_YEAR, CASE WHEN LENGTH(DATA_MONTH)<2 THEN '0'||DATA_MONTH ");
		sql.append("ELSE DATA_MONTH END DATA_MONTH , AVG_AUM_AMT, SAV_AUM, CHK_AUM, CD_AUM, FSAV_AUM, ");
		sql.append("FCD_AUM, RP_AUM, INS_AUM, BFOND_AUM, DOM_FUND_AUM, OVS_FUND_AUM, OVS_COMM_AUM, ");
		sql.append("FDCD_AUM, OVS_STK_AUM, CMA_AUM, ENTRUST_AUM, PORTFOLIO_AUM, MNY_TRST_AUM, ");
		sql.append("LN_AUM, GOLD_BOOK_AUM, NAMIS_AUM, VBOND_AUM, LDAY_AUM_AMT ");
		sql.append("FROM TBCRM_CUST_AUM_MONTHLY_HIST ");
		sql.append("WHERE 1=1 ");
		sql.append("AND CUST_ID = :cust_id ");
		sql.append("AND ROWNUM <= 18 ");
		sql.append("ORDER BY DATA_YEAR DESC, DATA_MONTH DESC ");
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(sql.toString());
		condition.setObject("cust_id", inputVO.getCust_id());
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List list2 = dam.exeQueryWithoutSort(condition);
		outputVO.setResultList2(list2);	
		outputVO.setResultList4(list2);	
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		outputVO.setResultList(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());	// 當前頁次
		outputVO.setTotalPage(totalPage_i);								// 總頁次
		outputVO.setTotalRecord(totalRecord_i);							// 總筆數
		
		//PDF用 : 姓名 ID 查詢時間
		StringBuffer sql2 = new StringBuffer();
		sql2.append(" SELECT CUST_ID, CUST_NAME, TO_CHAR(SYSDATE,'YYYY/MM/DD') AS DATA_TIME ");
		sql2.append(" FROM TBCRM_CUST_MAST ");
		sql2.append(" WHERE CUST_ID = :cust_id ");
		DataAccessManager dam2 = this.getDataAccessManager();
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition2.setQueryString(sql2.toString());
		condition2.setObject("cust_id", inputVO.getCust_id());
		List list3 = dam2.exeQuery(condition2);
		outputVO.setResultList3(list3);	
		this.sendRtnObject(outputVO);
	}
	
	// 列印預覽
	public void report(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException {
		String url = null;
		String txnCode = "CRM614";
		String reportID = "R1";
		ReportIF report = null;
		CRM614OutputVO return_VO = (CRM614OutputVO) body;

		List<Map<String, Object>> list = return_VO.getResultList2();
		List<Map<String, Object>> title = return_VO.getResultList3();
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		if(title.size()>0 ){
			//增加判斷條件
			if(!StringUtils.isBlank(title.get(0).get("CUST_ID")+"")){
				data.addParameter("CUST_ID", title.get(0).get("CUST_ID"));
			}else{
				//新增判斷cust_id null問題 空白
				return_VO.setMsgCust("IsNull");
				this.sendRtnObject(return_VO);
				return;
			}
				
			//增加判斷條件
			if(title.get(0).get("CUST_NAME")!=null)
				data.addParameter("CUST_NAME", title.get(0).get("CUST_NAME"));
			else //避免 null 出錯
				data.addParameter("CUST_NAME", "");
			//增加判斷條件
			if(title.get(0).get("DATA_TIME")!=null)
				data.addParameter("DATA_TIME", title.get(0).get("DATA_TIME"));
			else //避免 null 出錯
				data.addParameter("DATA_TIME", "");
			
			for(int i = 0 ; i < list.size(); i++){
				String num = "";
				if(i < 9){
					num = "0" + (i + 1);
				}else{
					num = ObjectUtils.toString(i+1);
				}
				data.addParameter("DATA_MONTH"+num, list.get(i).get("DATA_YEAR") + "" +list.get(i).get("DATA_MONTH"));
				data.addParameter("AVG_AUM_AMT"+num, list.get(i).get("AVG_AUM_AMT"));
				data.addParameter("LDAY_AUM_AMT"+num, list.get(i).get("LDAY_AUM_AMT"));
				data.addParameter("SAV_AUM"+num, list.get(i).get("SAV_AUM"));
				data.addParameter("CHK_AUM"+num, list.get(i).get("CHK_AUM"));
				data.addParameter("CD_AUM"+num, list.get(i).get("CD_AUM"));
				data.addParameter("FSAV_AUM"+num, list.get(i).get("FSAV_AUM"));
				data.addParameter("FCD_AUM"+num, list.get(i).get("FCD_AUM"));
				data.addParameter("RP_AUM"+num, list.get(i).get("RP_AUM"));
				data.addParameter("INS_AUM"+num, list.get(i).get("INS_AUM"));
				data.addParameter("BFOND_AUM"+num, list.get(i).get("BFOND_AUM"));
				data.addParameter("DOM_FUND_AUM"+num, list.get(i).get("DOM_FUND_AUM"));
				data.addParameter("OVS_FUND_AUM"+num, list.get(i).get("OVS_FUND_AUM"));
				data.addParameter("OVS_COMM_AUM"+num, list.get(i).get("OVS_COMM_AUM"));
				data.addParameter("FDCD_AUM"+num, list.get(i).get("FDCD_AUM"));
				data.addParameter("OVS_STK_AUM"+num, list.get(i).get("OVS_STK_AUM"));
//				data.addParameter("CMA_AUM"+num, list.get(i).get("CMA_AUM"));
//				data.addParameter("ENTRUST_AUM"+num, list.get(i).get("ENTRUST_AUM"));
//				data.addParameter("PORTFOLIO_AUM"+num, list.get(i).get("PORTFOLIO_AUM"));
				data.addParameter("MNY_TRST_AUM"+num, list.get(i).get("MNY_TRST_AUM"));
				data.addParameter("GOLD_BOOK_AUM"+num, list.get(i).get("GOLD_BOOK_AUM"));
				data.addParameter("NAMIS_AUM"+num, list.get(i).get("NAMIS_AUM"));
				data.addParameter("VBOND_AUM"+num, list.get(i).get("VBOND_AUM"));
			}
			//=============================================================================	
			report = gen.generateReport(txnCode, reportID, data);

			url = report.getLocation();
			notifyClientToDownloadFile(url, "列印預覽.pdf");
		}else{
			//新增判斷cust_id null問題
			return_VO.setMsgCust("IsNull");
			this.sendRtnObject(return_VO);
		}
		
	}
}
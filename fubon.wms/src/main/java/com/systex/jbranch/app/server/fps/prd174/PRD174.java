package com.systex.jbranch.app.server.fps.prd174;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_INS_ANCDOC_QVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PRD174
 * 
 * @author Jimmy
 * @date 2016/08/30
 * @spec null
 */


@Component("prd174")
@Scope("request")
public class PRD174 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DataAccessManager dam_obj = null;
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		PRD174InputVO inputVO = (PRD174InputVO) body;
		PRD174OutputVO outputVO = new PRD174OutputVO();
		List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select Q_ID,Q_NAME,Q_TYPE, ");
			sb.append(" to_char(EFFECT_DATE,'YYYY/MM/DD') as EFFECT_DATE, ");
			sb.append(" to_char(EXPIRY_DATE,'YYYY/MM/DD') as EXPIRY_DATE, ");
			sb.append(" APPROVER,to_char(APP_DATE,'YYYY/MM/DD') as APP_DATE, ");
			sb.append(" to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME, ");
			sb.append(" CREATOR,to_char(LASTUPDATE,'YYYY/MM/DD') as LASTUPDATE, ");
			sb.append(" MODIFIER, TEXT_STYLE_B, TEXT_STYLE_I, TEXT_STYLE_U, TEXT_STYLE_A ");
			sb.append(" FROM TBPRD_INS_ANCDOC_Q WHERE 1=1 ");
			if(!"".equals(inputVO.getQ_TYPE())){
				sb.append(" and Q_TYPE = :q_type ");
				qc.setObject("q_type", inputVO.getQ_TYPE());
			}
			sb.append(" order by Q_ID ");
			qc.setQueryString(sb.toString());
			tempList = dam_obj.exeQuery(qc);
			for(int a = 0; a<tempList.size(); a++){
				String effect_date = tempList.get(a).get("EFFECT_DATE").toString();
				String expiry_date = tempList.get(a).get("EXPIRY_DATE").toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date dt2 = sdf.parse(effect_date);
				Date dt3 = sdf.parse(expiry_date);
				long tr_effect_date=dt2.getTime();
				long tr_expiry_date = dt3.getTime();
				tempList.get(a).put("EFFECT_DATE_CHANGE", tr_effect_date);
				tempList.get(a).put("EXPIRY_DATE_CHANGE", tr_expiry_date);
			}
			outputVO.setINS_ANCDOCList(tempList);
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
	}
	
	public void addData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		PRD174EDITInputVO inputVO = (PRD174EDITInputVO) body;
		try {
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select 1 from TBPRD_INS_ANCDOC_Q where Q_ID = :q_id");
			qc.setObject("q_id", inputVO.getQ_ID());
			qc.setQueryString(sb.toString());
			List<String> checkQID = new ArrayList<String>();
			checkQID = dam_obj.exeQuery(qc);
			if(checkQID.size()<=0){
				dam_obj = this.getDataAccessManager();
				TBPRD_INS_ANCDOC_QVO insvo = new TBPRD_INS_ANCDOC_QVO();
				insvo.setQ_ID(inputVO.getQ_ID());
				insvo.setQ_NAME(inputVO.getQ_NAME());
				insvo.setQ_TYPE(inputVO.getQ_TYPE());
				Timestamp effect_date = new Timestamp(inputVO.getEFFECT_DATE().getTime());
				insvo.setEFFECT_DATE(effect_date);
				Timestamp expiry_date = new Timestamp(inputVO.getEXPIRY_DATE().getTime());
				insvo.setEXPIRY_DATE(expiry_date);
				insvo.setTEXT_STYLE_B(inputVO.getTEXT_STYLE_B());
				insvo.setTEXT_STYLE_I(inputVO.getTEXT_STYLE_I());
				insvo.setTEXT_STYLE_U(inputVO.getTEXT_STYLE_U());
				insvo.setTEXT_STYLE_A(inputVO.getTEXT_STYLE_A());
				dam_obj.create(insvo);
				sendRtnObject(null);
			}else{
				sendRtnMsg("ehl_01_prd174_001");
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	public void updateData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		PRD174EDITInputVO inputVO = (PRD174EDITInputVO) body;
		try {
				dam_obj = this.getDataAccessManager();
				TBPRD_INS_ANCDOC_QVO insvo = new TBPRD_INS_ANCDOC_QVO();
				insvo = (TBPRD_INS_ANCDOC_QVO) dam_obj.findByPKey(TBPRD_INS_ANCDOC_QVO.TABLE_UID, inputVO.getQ_ID());
				insvo.setQ_NAME(inputVO.getQ_NAME());
				insvo.setQ_TYPE(inputVO.getQ_TYPE());
				Timestamp effect_date = new Timestamp(inputVO.getEFFECT_DATE().getTime());
				insvo.setEFFECT_DATE(effect_date);
				Timestamp expiry_date = new Timestamp(inputVO.getEXPIRY_DATE().getTime());
				insvo.setEXPIRY_DATE(expiry_date);
				insvo.setTEXT_STYLE_B(inputVO.getTEXT_STYLE_B());
				insvo.setTEXT_STYLE_I(inputVO.getTEXT_STYLE_I());
				insvo.setTEXT_STYLE_U(inputVO.getTEXT_STYLE_U());
				insvo.setTEXT_STYLE_A(inputVO.getTEXT_STYLE_A());
				dam_obj.update(insvo);
				sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	public void deleteData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
			PRD174InputVO inputVO = (PRD174InputVO) body;
		try {
				dam_obj = this.getDataAccessManager();
				TBPRD_INS_ANCDOC_QVO insvo = new TBPRD_INS_ANCDOC_QVO();
				insvo = (TBPRD_INS_ANCDOC_QVO) dam_obj.findByPKey(TBPRD_INS_ANCDOC_QVO.TABLE_UID, inputVO.getQ_ID());
				if (insvo != null) {
					dam_obj.delete(insvo);
					sendRtnObject(null);
				}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	public void upload(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PRD174InputVO inputVO = (PRD174InputVO) body;
		PRD174OutputVO outputVO = new PRD174OutputVO();
		dam_obj = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		List<Integer> error = new ArrayList<Integer>();
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				if(i == 0) {
					continue;
				}
				String[] str = dataCsv.get(i);
				boolean isNewData = true;
				TBPRD_INS_ANCDOC_QVO insvo = new TBPRD_INS_ANCDOC_QVO();
				// 題號
				if (StringUtils.isNotBlank(str[0])) {
					insvo = (TBPRD_INS_ANCDOC_QVO) dam_obj.findByPKey(TBPRD_INS_ANCDOC_QVO.TABLE_UID, str[0].replaceAll("　", ""));
					if(insvo == null) {
						insvo = new TBPRD_INS_ANCDOC_QVO();
						insvo.setQ_ID(str[0].replaceAll("　", ""));
						isNewData = true;
					} else {
						isNewData = false;
					}
						
					
				} else {
					error.add(i + 1);
					continue;
				}
				// 題目內容
				if (StringUtils.isNotBlank(str[1])) {
					insvo.setQ_NAME(str[1].replaceAll("　", ""));
				} else {
					error.add(i + 1);
					continue;
				}
				// 題目類型
				if (StringUtils.isNotBlank(str[2])) {
					insvo.setQ_TYPE(str[2].replaceAll("　", ""));
				} else {
					error.add(i + 1);
					continue;
				}
				// 生效時間
				if (StringUtils.isNotBlank(str[3])) {
					Timestamp effect_date = new Timestamp(sdf2.parse(str[3].replaceAll("　", "")).getTime());
					insvo.setEFFECT_DATE(effect_date);
				} else {
					error.add(i + 1);
					continue;
				}
				// 截止時間
				if (StringUtils.isNotBlank(str[4])) {
					Timestamp expiry_date = new Timestamp(sdf2.parse(str[4].replaceAll("　", "")).getTime());
					insvo.setEXPIRY_DATE(expiry_date);
				} else {
					error.add(i + 1);
					continue;
				}
				// 題目文字格式設定_粗體
				if (StringUtils.isNotBlank(str[5])) {
					insvo.setTEXT_STYLE_B(str[5].replaceAll("　", ""));
				} else {
					error.add(i + 1);
					continue;
				}
				// 題目文字格式設定_斜體
				if (StringUtils.isNotBlank(str[6])) {
					insvo.setTEXT_STYLE_I(str[6].replaceAll("　", ""));
				} else {
					error.add(i + 1);
					continue;
				}
				// 題目文字格式設定_底線
				if (StringUtils.isNotBlank(str[7])) {
					insvo.setTEXT_STYLE_U(str[7].replaceAll("　", ""));
				} else {
					error.add(i + 1);
					continue;
				}
				// 題目文字格式設定_陰影
				if (StringUtils.isNotBlank(str[8])) {
					insvo.setTEXT_STYLE_A(str[8].replaceAll("　", ""));
				} else {
					error.add(i + 1);
					continue;
				}
				
				if (isNewData) {
					dam_obj.create(insvo);
				} else {
					dam_obj.update(insvo);
				}
			}
		}
		outputVO.setErrorList(error);
		this.sendRtnObject(outputVO);
	}
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		PRD174InputVO inputVO = (PRD174InputVO) body;
		notifyClientToDownloadFile("doc//PRD//PRD174_EXAMPLE.csv", "權益書題目-上傳範例.csv");
		this.sendRtnObject(null);
	}
	
	// 保險重要權益宣告書空白表單
	public void printBlankRpt(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException {
		String url = null;
		String txnCode = "PRD174";
		String reportID = "R1";
		ReportIF report = null;
		PRD174InputVO inputVO = (PRD174InputVO) body;

		List<Map<String, Object>> list = inputVO.getList();
		List<Map<String, Object>> cust_List = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> insured_list = new ArrayList<Map<String,Object>>();
		
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//要保人題目
		sb.append(" SELECT (CASE WHEN Q_ID = 'Q07' THEN REPLACE(Q_NAME, '[:1]', INSPRD_ANNUAL) ELSE Q_NAME END) AS Q_NAME ")
			.append(" ,TEXT_STYLE_B, TEXT_STYLE_I, TEXT_STYLE_U, TEXT_STYLE_A ")	//宣告題目
				.append(" FROM VWPRD_INS_ANCDOC ")
				.append(" WHERE INSPRD_ID = :INSPRD_ID ")	//保險代號
				.append(" AND INSPRD_ANNUAL = :INSPRD_ANNUAL ")//繳費年期
				.append(" AND Q_TYPE = '1' ")	//要保人
				.append(" AND EFFECT_DATE <= SYSDATE AND EXPIRY_DATE >= SYSDATE ");
		qc.setObject("INSPRD_ID", list.get(0).get("INSPRD_ID"));
		qc.setObject("INSPRD_ANNUAL", list.get(0).get("INSPRD_ANNUAL"));
		qc.setQueryString(sb.toString());
		cust_List = dam_obj.exeQuery(qc);

		//被保人題目
		dam_obj = getDataAccessManager();
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT (CASE WHEN Q_ID = 'Q07' THEN REPLACE(Q_NAME, '[:1]', INSPRD_ANNUAL) ELSE Q_NAME END) AS Q_NAME ")
			.append(" ,TEXT_STYLE_B, TEXT_STYLE_I, TEXT_STYLE_U, TEXT_STYLE_A ")	//宣告題目
				.append(" FROM VWPRD_INS_ANCDOC ")
				.append(" WHERE INSPRD_ID = :INSPRD_ID ")	//保險代號
				.append(" AND INSPRD_ANNUAL = :INSPRD_ANNUAL ")//繳費年期
				.append(" AND Q_TYPE = '2' ")	//被保人
				.append(" AND EFFECT_DATE <= SYSDATE AND EXPIRY_DATE >= SYSDATE ");
		qc.setObject("INSPRD_ID", list.get(0).get("INSPRD_ID"));
		qc.setObject("INSPRD_ANNUAL", list.get(0).get("INSPRD_ANNUAL"));
		qc.setQueryString(sb.toString());
		insured_list = dam_obj.exeQuery(qc);
				
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();

		// 西元轉民國
		Calendar cal = Calendar.getInstance();
		String year = cal.get(Calendar.YEAR) - 1911 + "";
		String mon = cal.get(Calendar.MONTH) + 1 + "";
		if(mon.length() < 2){
			mon = "0" + mon;
		}
		String day = cal.get(Calendar.DAY_OF_MONTH) + "";
		if(day.length() < 2){
			day = "0" + day;
		}
		data.addParameter("DATE", year + mon + day);

		data.addRecordList("Script Mult Data Set", list);
		data.addRecordList("CUST", cust_List);
		data.addRecordList("INSURED", insured_list);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		notifyClientToDownloadFile(url, "保險重要權益宣告書空白表單.pdf");
	}
}

package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("btjsb100")
@Scope("prototype")
public class BTJSB100 extends BizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 1. 匯出資料給集中登打系統, 每一個受理編號為1個xml檔案
	// 2. 匯出要保書文件檔.csv 及 日盛保單主檔.csv
	public void exportFile(Object body, IPrimitiveMap<?> header) throws Exception {
		SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
		// 1. 匯出資料給集中登打系統, 每一個受理編號為1個xml檔案
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT P.N_INSUREDIDNO, F.*, ");
		sb.append(" TO_CHAR(F.CREATEDT, 'YYYYMMDD') AS CREATE_DT, ");
		sb.append(" TO_CHAR(F.CREATEDT, 'HHMMSS') AS CREATE_TM, ");
		sb.append(" TO_CHAR(F.SIGNDT, 'YYYYMMDD') AS SIGN_DT ");
		sb.append(" FROM TBJSB_INS_POLICY_FILES F ");
		sb.append(" LEFT JOIN TBJSB_INS_POLICY2 P ON F.ACCEPTID = P.ACCEPTID ");
		
		qc.setQueryString(sb.toString());
		List<Map<String, String>> list = dam.exeQuery(qc);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports" + File.separator;
		int i = 0;
		int folderNbr = 0;
		for (Map<String, String> map : list) {
			String fileName = map.get("FILESNAME").toUpperCase();
			fileName = fileName.replace(".PDF", ".xml");	// 檔名為 TBJSB_INS_POLICY_FILES.FILENAME 然後把.PDF改成.XML
			String nibarcode = map.get("TYPE") != null ? "53-000" + map.get("TYPE").toString() + "-99" : "";
			
			// 一個資料夾最多可放1萬個XML檔
			if (i % 10000 == 0) {
				folderNbr++;
			}
			// 若此目錄不存在，則建立之
			String filePath = tempPath + "BAtoWMG" + File.separator + "00" + folderNbr;
			File path = new File(filePath); 
			if (!path.exists()) {
				path.mkdir();
			}
			
			File file1 = new File(filePath + File.separator + fileName);
			//若檔案已存在則刪除重新產生
			if(file1.exists()) file1.delete();
			file1.createNewFile();
			
			// 將名單寫到檔案
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true), "UTF-8"));	// UTF-8編碼
			
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.append("\r\n");
			writer.append("<indexattribute>");
			writer.append("\r\n");
			writer.append("<nicdate>" + map.get("CREATE_DT") + "</nicdate>");
			writer.append("\r\n");
			writer.append("<nictime>" + map.get("CREATE_TM") + "</nictime>");
			writer.append("\r\n");
			writer.append("<nibranch>118</nibranch>");
			writer.append("\r\n");
			writer.append("<nibarcode>" + nibarcode + "</nibarcode>");
			writer.append("\r\n");
			writer.append("<niid>" + map.get("N_INSUREDIDNO") + "</niid>");
			writer.append("\r\n");
			writer.append("<niaccno>" + map.get("ACCEPTID") + "</niaccno>");
			writer.append("\r\n");
			writer.append("<niaddition> </niaddition>");
			writer.append("\r\n");
			String memo = StringUtils.isBlank(map.get("MEMO")) ? " " : map.get("MEMO");
			writer.append("<nimemo>" + memo + "</nimemo>");
			writer.append("\r\n");
			writer.append("<nisdate>" + map.get("SIGN_DT") + "</nisdate>");
			writer.append("\r\n");
			writer.append("</indexattribute>");
			
			writer.close();
			i++;
		}
		
		// 2. 匯出『要保書文件檔.csv』（TBJSB_INS_POLICY_FILES）
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT * FROM TBJSB_INS_POLICY_FILES ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> fileList = dam.exeQuery(qc);
		
		String[] order = {"FILESERIALNUM", "ACCEPTID", "FILESNAME", "TYPE", "ADDITION", 
						  "MEMO", "CREATEDT", "CREATEBY", "SIGNDT", "SIGNBY", 
						  "NOTICE", "DELFLG", "ORGCOPYTO", "COPYFLG"};

		toSpecifiedLocation("TBJSB_INS_POLICY_FILES_" + sdfYYYYMMDD.format(new Date()), "CSV", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), fileList, order, ",");
		
		// 2. 匯出『日盛保單主檔.csv』（TBJSB_INS_POLICY2）
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT ACCEPTID, N_INSUREDIDNO FROM TBJSB_INS_POLICY2 ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> insList = dam.exeQuery(qc);
		
		String[] insOrder = {"ACCEPTID", "N_INSUREDIDNO"};

		toSpecifiedLocation("TBJSB_INS_POLICY2_" + sdfYYYYMMDD.format(new Date()), "CSV", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), insList, insOrder, ",");	
	}
	
	// 匯出至指定路徑
	public void toSpecifiedLocation(String file_name, String attached_name, String path, List<Map<String, Object>> list, String[] order, String separated) throws JBranchException, IOException {

		String fileName = file_name + "." + attached_name;

		File file1 = new File(path + "reports" + File.separator + fileName);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, false), "UTF-8"));

		// 印出表頭
		for (String title : order) {
			writer.append(title);
			writer.append(separated);
		}
		writer.append("\r\n");
		
		for (Map<String, Object> datas : list) {
			int j = 1;
			for (Integer i = 0; i < order.length; i++) {
				if (StringUtils.equals(separated, "")) {
					writer.append((datas.get(order[i])).toString());
				} else {
					writer.append(datas.get(order[i]) == null ? "" : datas.get(order[i]).toString());
				}

				if (j++ == order.length) {
				} else {
					writer.append(separated);
				}
			}
			writer.append("\r\n");
		}

		writer.flush();
		writer.close();
	}
	
	private String toUTF8(String str) throws UnsupportedEncodingException {
		return new String(str.getBytes("UTF-8"), "UTF-8");
	}
}
/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .yyyymmdd 2. 將.yyyymmdd 上傳FTP
 * 
 * @author 1600216
 * @date 2019/07/11
 *
 */
@Repository("uhrmbth")
@Scope("prototype")
public class UHRMBTH extends BizLogic {

	private DataAccessManager dam = null;

	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void createFileBth(Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
		ODS_UHRMBRH();
	}

	public void ODS_UHRMBRH() throws JBranchException, IOException {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT UHRM.CUST_ID, ");
		sb.append("       UHRM.UEMP_ID ");
		sb.append("FROM TBORG_CUST_UHRM_PLIST UHRM ");

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(queryCondition);

		
//		toODS("UHRMBRH_400", "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataList, order, "");
		UHRMBRH_400(dataList);
	}
	
	public void UHRMBRH_400 (List<Map<String, Object>> dataList) throws JBranchException, IOException {
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map : dataList) {
			map.put("CUST_ID", addBlankForString((String) map.get("CUST_ID"), 10));
			map.put("UEMP_ID", addZeroForNum((String) map.get("UEMP_ID"), 6));
			
			list.add(map);
		}
		
		String[] order = { "CUST_ID", "UEMP_ID"};
		
		toODS("UHRMBRH_400", "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), list, order, "");
	}

	//匯出ods至指定路徑
	public void toODS(String file_name, String attached_name, String path, List<Map<String, Object>> list, String[] order, String separated) throws JBranchException, IOException {

		String fileName = file_name + "." + attached_name;

		File file1 = new File(path + "reports\\" + fileName);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, false), "MS950"));

		for (Map<String, Object> datas : list) {

			int j = 1;
			for (Integer i = 0; i < order.length; i++) {
				if ("".equals(separated)) {
					writer.append((String) (datas.get(order[i])));
				} else {
					writer.append(StringUtils.isBlank((String) datas.get(order[i])) ? "" : (String) datas.get(order[i]));
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
	
	private String addZeroForNum(String str, int strLength) {
		
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
	
	private String addBlankForString(String str, int strLength) {
		
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append(str).append(" ");//右補空格
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}

}

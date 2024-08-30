/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .yyyymmdd
 * 2. 將.yyyymmdd 上傳FTP
 * 
 * @author 1600216
 * @date 2016/11/16
 *
 */
@Repository("complainlist")
@Scope("prototype")
public class COMPLAINLIST extends BizLogic {
	 
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		GenFileTools gft=new GenFileTools();
		dam = this.getDataAccessManager();
		int dataCount;
		QueryConditionIF queryCondition = dam.getQueryCondition();
		//System.out.println("btpms413_out go!!");
		//定義檔案名稱及輸出的jobname
		String  writeFileName="COMPLAIN_LIST";
		String  attached_name="";
		String  jobName="PS_FP_COMPLAIN_LIST";
		StringBuffer sb = new StringBuffer();
		String  path=(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		//先取得總筆數
		sb.append("SELECT COUNT(1) TOTAL_COUNT FROM ( ");
		sb.append(genSql().toString());
		sb.append(")");
		//System.out.println("SQL:"+sb.toString());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listCount = dam.exeQuery(queryCondition);	
		dataCount=Integer.parseInt(listCount.get(0).get("TOTAL_COUNT").toString());
		String[] order=genOrder();

		System.out.println("totalCount:"+dataCount);
		ResultSet rs=null;
		Connection con=gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
		rs=gft.getRS(genSql(),s);
		gft.writeFile(writeFileName, attached_name, path, order, rs, ",", false, true);
		
		//chk檔格式
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(addBlankForString(jobName, 30));
		chkLayout.append(addZeroForNum(String.valueOf(dataCount),15));
		chkLayout.append(sdfYYYYMMDD.format(new Date(new Date().getTime()-((long)1 * 24 * 60 * 60 * 1000))));
		gft.writeFileByText("Z"+writeFileName, attached_name, path, chkLayout, false);
		System.out.println("job  end");
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
	}

	//產出空檔
	public void genEmptyFile(String file_name, String attached_name, String path) throws IOException
	{
		String fileName = file_name + "." + attached_name;
		FileWriter writer = new FileWriter(path  + "reports\\" + fileName,false);
		writer.flush();
		writer.close();
	}
	
	public List getDataList(StringBuffer sb)throws JBranchException
		{
					dam = this.getDataAccessManager();
					QueryConditionIF queryCondition = dam.getQueryCondition();
					queryCondition.setQueryString(sb.toString());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					return list;
		}
	
	//產出check檔
    public void createZFile (String file_name, String attached_name, String path, String jobName,int totalCount) throws JBranchException, IOException {
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("JOB_NAME", addBlankForString(jobName, 30));
		map.put("CNT", addZeroForNum(String.valueOf(totalCount),15));
		map.put("DATA_DATE", sdfYYYYMMDD.format(new Date()));
		list.add(map);
		
		String[] order = {"JOB_NAME", "CNT", "DATA_DATE"};
		toWriterFile(file_name, attached_name, (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), list, order, "",false);
	}

	//產檔所需要輸出的欄位
	public String[] genOrder()
	{
		String[] order={"COMPLAIN_LIST_ID",
				"CREATE_DATE",
				"END_DATE",
				"BRANCH_ID",
				"GRADE",
				"CUST_ID",
				"CUST_NAME",
				"BIRTHDATE",
				"OCCUP",
				"PHONE",
				"EDUCATION",
				"OPEN_ACC_DATE",
				"TOTAL_ASSET",
				"PERSON_ID",
				"PERSON_NAME",
				"AO_CODE",
				"HANDLE_STEP",
				"PRE_EDITOR",
				"EDITOR_CONDITION1",
				"EDITOR_CONDITION2",
				"EDITOR_CONDITION3",
				"EDITOR_CONDITION4",
				"FLOW",
				"FROM_CS",
				"STATUS"};
		return order;
	}
	//產檔需要的SQL
    public StringBuffer genSql()
	{
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT * FROM ( ");
		sb.append(" SELECT rownum rowN,a.* from( ");
		
		sb.append("SELECT COMPLAIN_LIST_ID AS COMPLAIN_LIST_ID, ");
		sb.append("TO_CHAR(CREATE_DATE,'YYYYMMDD') AS CREATE_DATE, ");
		sb.append("TO_CHAR(END_DATE,'YYYYMMDD') AS END_DATE, ");
		sb.append("BRANCH_NBR AS BRANCH_ID, ");
		sb.append("GRADE, ");
		sb.append("CUST_ID, ");
		sb.append("CUST_NAME, ");
		sb.append("TO_CHAR(BIRTHDATE,'YYYYMMDD') AS BIRTHDATE,");
		sb.append("OCCUP, ");
		sb.append("PHONE, ");
		sb.append("EDUCATION, ");
		sb.append("TO_CHAR(OPEN_ACC_DATE,'YYYYMMDD')AS OPEN_ACC_DATE, ");
		sb.append("TOTAL_ASSET, ");
		sb.append("PERSON_ID, ");
		sb.append("PERSON_NAME, ");
		sb.append("AO_CODE, ");
		sb.append("HANDLE_STEP, ");
		sb.append("PRE_EDITOR,");
		sb.append("EDITOR_CONDITION1, ");
		sb.append("EDITOR_CONDITION2, ");
		sb.append("EDITOR_CONDITION3, ");
		sb.append("EDITOR_CONDITION4, ");
		sb.append("COMPLAIN_FLOW AS FLOW, ");
		sb.append("FROM_CS, ");
		sb.append("CASE_STATUS AS STATUS ");
		sb.append("FROM TBCRM_CUST_COMPLAIN_LIST ");

		
		sb.append(")a ");
		//sb.append("order by  ");
		sb.append(") ");
		return sb;
	}	
	
	

	
	public void updateFileBth (Object body, IPrimitiveMap<?> header) throws JBranchException, IOException {
		
		// 取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);

		// 主檔
		String ftpCode_main = (String) jobParam.get("ftpCode_main");
		String fileName_main = (String) jobParam.get("fileName_main");
		// 明細檔
		String ftpCode_detail = (String) jobParam.get("ftpCode_detail");
		String fileName_detail = (String) jobParam.get("fileName_detail");

		ftpUpload(ftpCode_detail);
		ftpUpload(ftpCode_main);
	}
	
	private void ftpUpload(String ftpCode) {
		try {
			ftpJobUtil.ftpPutFile(ftpCode);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	
	//產檔到指定路徑
	public void toWriterFile (String file_name, String attached_name, String path, List<Map<String, Object>> list, String[] order, String separated,boolean isExtend) throws JBranchException, IOException {

		String fileName = file_name + "." + attached_name;
		FileWriter writer = new FileWriter(path  + "reports\\" + fileName,isExtend);
		

		for(Map<String, Object> datas : list) {
			
			int j = 1;
			for (Integer i = 0; i < order.length; i++) {
				//若固定分格的話空白需忠實呈現
				if("".equals(separated))
				{
					writer.append((String)datas.get(order[i]));
				}
				else
				{
					if("".equals((StringUtils.isBlank((String) datas.get(order[i])) ? "" : (String) datas.get(order[i]))))
					{
						writer.append((StringUtils.isBlank((String) datas.get(order[i])) ? "" : (String) datas.get(order[i])));	
					}
					else
					{
					writer.append("\""+(StringUtils.isBlank((String) datas.get(order[i])) ? "" : (String) datas.get(order[i]))+"\"");
					}
				}
				if (j++ == order.length){
				}else{
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



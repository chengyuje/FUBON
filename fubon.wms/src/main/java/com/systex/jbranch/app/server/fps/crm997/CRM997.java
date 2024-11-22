package com.systex.jbranch.app.server.fps.crm997;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEVO;
import com.systex.jbranch.app.server.fps.crm662.CRM662InputVO;
import com.systex.jbranch.app.server.fps.pms353.PMS353InputVO;
import com.systex.jbranch.app.server.fps.pms407.PMS407InputVO;
import com.systex.jbranch.app.server.fps.pms407.PMS407OutputVO;
import com.systex.jbranch.app.server.fps.pms711.PMS711OutputVO;
import com.systex.jbranch.app.server.fps.pms713.PMS713;
import com.systex.jbranch.app.server.fps.pms713.PMS713InputVO;
import com.systex.jbranch.app.server.fps.pms713.PMS713OutputVO;
import com.systex.jbranch.app.server.fps.prd171.PRD171InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/*
 * #2116: 拆分成TBCRM_CUST_PT_DEGREE_SG以及TBCRM_CUST_XP_DEGREE_SG
 */
@Component("crm997")
@Scope("request")
public class CRM997 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM997.class);
	private static String finalstring;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM997InputVO inputVO = (CRM997InputVO) body;
		CRM997OutputVO outputVO = new CRM997OutputVO();
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("  with ALL_CUST as (                              ");
		sql.append("  	     select cust_id from tbcrm_cust_pt_degree ");
		sql.append("  	    union                                     ");
		sql.append("  	     select cust_id from tbcrm_cust_xp_degree ");
		sql.append("  	    )                                         ");
		sql.append("  SELECT ALL_CUST.CUST_ID,                             ");
		sql.append("  	     PT.POTENTIAL_LEVEL,                      ");
		sql.append("  	     PT.POTENTIAL_BEGIN_DATE,                 ");
		sql.append("  	     PT.POTENTIAL_END_DATE,                   ");
		sql.append("  	     PT.LASTUPDATE AS PT_LASTUPDATE,          ");
		sql.append("  	     PT.MODIFIER AS PT_MODIFIER,                ");
		sql.append("  	     XP.EXPERIENCE_LEVEL,                     ");
		sql.append("  	     XP.EXPERIENCE_BEGIN_DATE,                ");
		sql.append("  	     XP.EXPERIENCE_END_DATE,                  ");
		sql.append("  	     XP.LASTUPDATE AS XP_LASTUPDATE,          ");
		sql.append("  	     XP.MODIFIER AS XP_MODIFIER                ");
		sql.append("  FROM ALL_CUST                            ");
		sql.append("  LEFT JOIN TBCRM_CUST_PT_DEGREE PT ON ALL_CUST.CUST_ID = PT.CUST_ID               ");
		sql.append("  LEFT JOIN TBCRM_CUST_XP_DEGREE XP ON ALL_CUST.CUST_ID = XP.CUST_ID                ");
		sql.append("  WHERE 1=1                                       ");
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sql.append(" AND ALL_CUST.CUST_ID = :cust_id  ");
			queryCondition.setObject("cust_id", inputVO.getCustId());
		}
		if (StringUtils.isNotBlank(inputVO.getPotential_level())) {
			sql.append(" AND PT.POTENTIAL_LEVEL = :potential_level  ");
			queryCondition.setObject("potential_level", inputVO.getPotential_level());
		}
		if (StringUtils.isNotBlank(inputVO.getExperience_level())) {
			sql.append(" AND XP.EXPERIENCE_LEVEL = :experience_level  ");
			queryCondition.setObject("experience_level", inputVO.getExperience_level());
		}
		if (inputVO.getExperience_begin_date() != null) {
			sql.append(" AND TRUNC(XP.EXPERIENCE_BEGIN_DATE) >= TO_DATE(:experience_begin_date, 'yyyyMMdd') ");
			queryCondition.setObject("experience_begin_date", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getExperience_begin_date()));
		}if (inputVO.getExperience_end_date() != null) {
			sql.append(" AND TRUNC(XP.EXPERIENCE_END_DATE) <= TO_DATE(:experience_end_date, 'yyyyMMdd')  ");
			queryCondition.setObject("experience_end_date", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getExperience_end_date()));
		}
		if (inputVO.getPotential_begin_date() != null) {
			sql.append(" AND TRUNC(PT.POTENTIAL_BEGIN_DATE) >= TO_DATE(:potential_begin_date, 'yyyyMMdd') ");
			queryCondition.setObject("potential_begin_date", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getPotential_begin_date()));
		}if (inputVO.getPotential_end_date() != null) {
			sql.append(" AND TRUNC(PT.POTENTIAL_END_DATE) <= TO_DATE(:potential_end_date, 'yyyyMMdd')  ");
			queryCondition.setObject("potential_end_date", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getPotential_end_date()));
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	
	
	/** 儲存CSV檔 **/
	public void insertCSVFile(Object body, IPrimitiveMap header) throws Exception {
		CRM997InputVO inputVO = (CRM997InputVO) body;
		CRM997OutputVO outputVO = new CRM997OutputVO();
		
		String dsql = "";
		//清空TBCRM_CUST_XP_DEGREE_SG
		dam = this.getDataAccessManager();
		QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if(inputVO.getSetting_type().equals("1")) {
			dsql = " TRUNCATE TABLE TBCRM_CUST_PT_DEGREE_SG ";
		} else if(inputVO.getSetting_type().equals("2")){
			dsql = " TRUNCATE TABLE TBCRM_CUST_XP_DEGREE_SG ";
		}
		dcon.setQueryString(dsql.toString());
		dam.exeUpdate(dcon);
		
		Path path = Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH),inputVO.getFileName()).toString());
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        
        List<String> lines = FileUtils.readLines(new File(path.toString()), "big5");
		
        //潛力名單匯入
		if(inputVO.getSetting_type().equals("1")) {
			dam = this.getDataAccessManager();
			QueryConditionIF qc2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
	            for (int i = 1; i < lines.size(); i++) {
	                liesCheck(lines.get(i).toString());
	                String str_line = finalstring;
	                String[] str = str_line.split(",");
	                
	                //檢查匯入欄位資料，空白資料不進行匯入
	                if(str.length == 4 && !StringUtils.isBlank(str[0]) && !StringUtils.isBlank(str[1]) && !StringUtils.isBlank(str[2]) && !StringUtils.isBlank(str[3])) {     	
	                	//#2177
	                	if (!str[1].matches("D|H|T|K|C")) {
							throw new JBranchException("潛力等級請輸入D、H、T、K、C");
						}
	           
	                	//體驗起日需大於系統日
		                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		                Date d1 = new Date(); //取得目前日期時間
		                Date today = new Timestamp(sdf.parse(sdf.format(d1)).getTime());
		                Date startDate = new Timestamp(sdf.parse(str[2].toString()).getTime());
		                if(today.equals(startDate) || today.after(startDate))
		                	throw new APException("檔案第"+ i +"筆資料有誤，體驗開始日應大於系統日期");		
		                
		              //新增資料至TBCRM_CUST_XP_DEGREE_SG
	                    StringBuffer sb = new StringBuffer();
	                    try {
	                    	sb.append("  MERGE INTO TBCRM_CUST_PT_DEGREE_SG TCX ");
	                    	sb.append("  USING( SELECT DISTINCT '" + str[0] + "' as CUST_ID,'" + str[1] + "' as POTENTIAL_LEVEL, ");
	                    	sb.append(" 		TO_DATE(" + "'" + str[2] + "','YYYY/MM/DD') as POTENTIAL_BEGIN_DATE, TO_DATE('" + str[3] + "','YYYY/MM/DD') as POTENTIAL_END_DATE, ");
			                sb.append(" 		'" + loginID + "' as LOGIN_ID FROM DUAL ) SG ");
			                sb.append("  ON (TCX.CUST_ID = SG.CUST_ID) ");
			                sb.append("  WHEN MATCHED THEN ");			                
			                sb.append("     UPDATE SET  TCX.POTENTIAL_LEVEL = SG.POTENTIAL_LEVEL ");
			                sb.append("                ,TCX.POTENTIAL_BEGIN_DATE = SG.POTENTIAL_BEGIN_DATE ");
			                sb.append("                ,TCX.POTENTIAL_END_DATE = SG.POTENTIAL_END_DATE ");
			                sb.append("                ,TCX.MODIFIER = SG.LOGIN_ID ");
			                sb.append("                ,TCX.LASTUPDATE = SYSDATE ");
			                sb.append("  WHEN NOT MATCHED THEN ");			    			
			                sb.append(" 	INSERT ( 			");
		                    sb.append("    	CUST_ID, POTENTIAL_LEVEL,  					");
		                    sb.append("    	POTENTIAL_BEGIN_DATE, POTENTIAL_END_DATE,  ");
		                    sb.append("    	VERSION, CREATETIME, CREATOR, 				");
		                    sb.append("    	MODIFIER, LASTUPDATE ) 						");
		                    sb.append("    	VALUES (SG.CUST_ID, SG.POTENTIAL_LEVEL, SG.POTENTIAL_BEGIN_DATE, SG.POTENTIAL_END_DATE,  ");
		                    sb.append("    			 0, SYSDATE, SG.LOGIN_ID, SG.LOGIN_ID, SYSDATE) ");
		                   
		                    qc2.setQueryString(sb.toString());
		                    dam.exeUpdate(qc2);
		            		
	                    } catch (JBranchException e) {
	                    	throw new APException("上傳檔案資料有誤，CUST_ID=" + str[0] + "; " + e.getMessage());
	                	}
	                } 
	            }
	            
	        //Merge->TBCRM_CUST_XP_DEGREE_SG至TBCRM_CUST_XP_DEGREE & 紀錄體驗活動上傳資料  
            QueryConditionIF qcAdd2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			StringBuffer sbAdd = new StringBuffer();					
			sbAdd.append("  CALL PABTH_BTCRM997.PR_IMPORT_PT_DEGREE() ");
			
			qcAdd2.setQueryString(sbAdd.toString());
			try {
				dam.exeUpdate(qcAdd2);
			} catch (JBranchException e) {
				throw new APException("上傳檔案資料有誤; " + e.getMessage());
			}
			
			outputVO.setAddToList(null);
			sendRtnObject(outputVO);
		}else if(inputVO.getSetting_type().equals("2")){
			dam = this.getDataAccessManager();
			QueryConditionIF qc2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
	            for (int i = 1; i < lines.size(); i++) {
	                liesCheck(lines.get(i).toString());
	                String str_line = finalstring;
	                String[] str = str_line.split(",");
	                
	                //檢查匯入欄位資料，空白資料不進行匯入
	                if(str.length == 4 && !StringUtils.isBlank(str[0]) && !StringUtils.isBlank(str[1]) && !StringUtils.isBlank(str[2]) && !StringUtils.isBlank(str[3])) {
	                	
	                	//體驗起日需大於系統日
		                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		                Date d1 = new Date(); //取得目前日期時間
		                Date today = new Timestamp(sdf.parse(sdf.format(d1)).getTime());
		                Date startDate = new Timestamp(sdf.parse(str[2].toString()).getTime());
		                if(today.equals(startDate) || today.after(startDate))
		                	throw new APException("檔案第"+ i +"筆資料有誤，體驗開始日應大於系統日期");		
		                
		              //新增資料至TBCRM_CUST_XP_DEGREE_SG
	                    StringBuffer sb = new StringBuffer();
	                    try {
	                    	sb.append("  MERGE INTO TBCRM_CUST_XP_DEGREE_SG TCX ");
	                    	sb.append("  USING( SELECT DISTINCT CUST_ID,'" + str[1] + "' as EXPERIENCE_LEVEL, ");
	                    	sb.append(" 		TO_DATE(" + "'" + str[2] + "','YYYY/MM/DD') as EXPERIENCE_BEGIN_DATE, TO_DATE('" + str[3] + "','YYYY/MM/DD') as EXPERIENCE_END_DATE, ");
			                sb.append(" 		VIP_DEGREE,'" + loginID + "' as LOGIN_ID FROM TBCRM_CUST_MAST WHERE CUST_ID = " + "'" + StringUtils.trim(str[0]) + "' ) SG ");
			                sb.append("  ON (TCX.CUST_ID = SG.CUST_ID) ");
			                sb.append("  WHEN MATCHED THEN ");			                
			                sb.append("     UPDATE SET  TCX.EXPERIENCE_LEVEL = SG.EXPERIENCE_LEVEL ");
			                sb.append("                ,TCX.EXPERIENCE_BEGIN_DATE = SG.EXPERIENCE_BEGIN_DATE ");
			                sb.append("                ,TCX.EXPERIENCE_END_DATE = SG.EXPERIENCE_END_DATE ");
			                sb.append("                ,TCX.MODIFIER = SG.LOGIN_ID ");
			                sb.append("                ,TCX.LASTUPDATE = SYSDATE ");
			                sb.append("  WHEN NOT MATCHED THEN ");			    			
			                sb.append(" 	INSERT ( 			");
		                    sb.append("    	CUST_ID, EXPERIENCE_LEVEL,  					");
		                    sb.append("    	EXPERIENCE_BEGIN_DATE, EXPERIENCE_END_DATE,  ");
		                    sb.append("    	ORG_VIP_DEGREE,  ");
		                    sb.append("    	VERSION, CREATETIME, CREATOR, 				");
		                    sb.append("    	MODIFIER, LASTUPDATE ) 						");
		                    sb.append("    	VALUES (SG.CUST_ID, SG.EXPERIENCE_LEVEL, SG.EXPERIENCE_BEGIN_DATE, SG.EXPERIENCE_END_DATE, SG.VIP_DEGREE, ");
		                    sb.append("    			 0, SYSDATE, SG.LOGIN_ID, SG.LOGIN_ID, SYSDATE) ");
		                   
		                    qc2.setQueryString(sb.toString());
		                    dam.exeUpdate(qc2);
		            		
	                    } catch (JBranchException e) {
	                    	throw new APException("上傳檔案資料有誤，CUST_ID=" + str[0] + "; " + e.getMessage());
	                	}
	                } 
	            }
	            
	        //Merge->TBCRM_CUST_XP_DEGREE_SG至TBCRM_CUST_XP_DEGREE & 紀錄體驗活動上傳資料  
            QueryConditionIF qcAdd2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			StringBuffer sbAdd = new StringBuffer();					
			sbAdd.append("  CALL PABTH_BTCRM997.PR_IMPORT_XP_DEGREE() ");
			
			qcAdd2.setQueryString(sbAdd.toString());
			try {
				dam.exeUpdate(qcAdd2);
			} catch (JBranchException e) {
				throw new APException("上傳檔案資料有誤; " + e.getMessage());
			}
			
			outputVO.setAddToList(null);
			sendRtnObject(outputVO);
		}
	}
	
	/**下載範例檔 **/
	public void downloadSample(Object body, IPrimitiveMap header) throws Exception {
		CRM997InputVO inputVO = (CRM997InputVO) body;
		
		if("1".equals(inputVO.getSetting_type())){
			notifyClientToDownloadFile("doc//CRM//CRM997_EXAMPLE1.csv", "行外高資產_潛力名單範例.csv");
		}else if("2".equals(inputVO.getSetting_type())){
			notifyClientToDownloadFile("doc//CRM//CRM997_EXAMPLE2.csv", "行外高資產_體驗名單範例.csv");
		}
		this.sendRtnObject(null);
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	   /**找字串 " \" " 功能   **/
    public String liesCheck(String lines) throws JBranchException {

        if (lines.indexOf("\"") != -1) {
            //抓出第一個"
            int acc = lines.indexOf("\"");
            //原本所有字串
            StringBuffer lines1 = new StringBuffer(lines.toString());
            //切掉前面的字串
            String ori = lines.substring(0, acc);
            String oriline = ori.toString();

            String fist = lines1.substring(acc);
            fist = fist.substring(1, fist.indexOf("\"", 1));
            String[] firstsub = fist.split(",");

            for (int i = 0; i < firstsub.length; i++) {
                oriline += firstsub[i].toString();
            }

            String okfirst = ori + "\"" + fist + "\"";   //前面組好的
            String oriline2 = lines.substring(acc);

            oriline2 = oriline2.replaceFirst("\"" + fist + "\"", "");
            String fistTwo = lines1.substring(0, lines1.indexOf("\""));
            lines = oriline + oriline2;
        }

        if (lines.indexOf("\"") != -1) {
            liesCheck(lines);
        } else {
            finalstring = lines;
        }
        return lines;
    }
	
}

package com.systex.jbranch.app.server.fps.pms310;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.Undefined;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.util.GregorianCalendar;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms306.PMS306OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :房信貸PS生產力周報 Controller <br>
 * Comments Name : PMS310.java<br>
 * Author :<br>
 * Date :2016年07月07日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms310")
@Scope("request")
public class PMS310 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS310.class);
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	private static String yeaMon=sdf.format(new Date());
	private static String finalstring;

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS310InputVO inputVO = (PMS310InputVO) body;
		PMS310OutputVO outputVO = new PMS310OutputVO();
		DataAccessManager dam = this.getDataAccessManager();

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);		
		
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CTS.YEARMON,NVL(CTS.REGION_CENTER_ID,ORG.REGION_CENTER_ID) REGION_CENTER_ID, ");
		sql.append("NVL(CTS.REGION_CENTER_NAME,ORG.REGION_CENTER_NAME) REGION_CENTER_NAME,NVL(CTS.BRANCH_AREA_ID,ORG.BRANCH_AREA_ID) BRANCH_AREA_ID, ");
		sql.append("NVL(CTS.BRANCH_AREA_NAME,ORG.BRANCH_AREA_NAME) BRANCH_AREA_NAME,CTS.BRANCH_NBR,NVL(CTS.BRANCH_NAME,ORG.BRANCH_NAME) BRANCH_NAME, ");
		if (inputVO.getTarType().equals("1")){
			sql.append("CTS.PS_EMP_NAME, ");
			sql.append("CTS.PS_EMP_ID, ");
		}
		sql.append("CTS.LT_CNT,CTS.LT_FND,CTS.LIMIT_CNT,CTS.LIMIT_FND,CTS.MRTG_CNT, ");
		sql.append("CTS.MRTG_FND,CTS.CREDIT_CNT,CTS.CREDIT_FND,CTS.HB_OWN_CNT,CTS.HB_OWN_FND, ");
		sql.append("CTS.HB_NON_CNT,CTS.HB_NON_FND,CTS.HB_CNT,CTS.HB_FND,CTS.NON_HB_CNT,CTS.NON_HB_FND, ");
		sql.append("CTS.CREDIT_N_CNT,CTS.CREDIT_N_FND,CTS.CREDIT_G_CNT,CTS.CREDIT_G_FND, ");
		sql.append("CTS.CREDIT_C_CNT,CTS.CREDIT_C_FND,CTS.SPM_TARGET_FEE,CTS.CREDIT_CARD_CNT ");
		if (inputVO.getTarType().equals("1"))
			sql.append("FROM TBPMS_CFP_TARGET_SET_PS CTS ");
		else
			sql.append("FROM TBPMS_CFP_TARGET_SET_BR CTS ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM ");
		sql.append("ON CTS.MODIFIER = MEM.EMP_ID ");
//		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N REC_N ");
//		sql.append("ON REC_N.EMP_ID=CTS.MODIFIER AND last_day(to_date(CTS.YEARMON,'YYYYMM')) BETWEEN REC_N.START_TIME AND REC_N.END_TIME  ");
		sql.append("LEFT JOIN tbpms_org_rec_n ORG  ");
		sql.append("ON ORG.DEPT_ID = CTS.BRANCH_NBR  ");
		sql.append("AND last_day(to_date(CTS.YEARMON,'YYYYMM')) BETWEEN ORG.START_TIME AND ORG.END_TIME  ");
		sql.append("WHERE 1=1 AND CTS.YEARMON = :yrmn ");
		// 分行
		if (!inputVO.getBranch_nbr().equals("")){
			sql.append("and CTS.BRANCH_NBR = :brid ");
			condition.setObject("brid", inputVO.getBranch_nbr());
		}else{
			//登入非總行人員強制加分行
			if(inputVO.getTypes().equals("1")){
				if(!headmgrMap.containsKey(roleID)) {
					sql.append(" and CTS.BRANCH_NBR IN (:branch_nbr) ");
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}	
			}
		}
			
		// 理專員編
		if (!inputVO.getEmp_id().equals("") && inputVO.getTarType().equals("1")){
			sql.append("and CTS.PS_EMP_ID  =  :psid  ");
			condition.setObject("psid", inputVO.getEmp_id());
		}else{
			//登入為銷售人員強制加EMP_ID
			if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
				if(inputVO.getTarType().equals("1") && inputVO.getTypes().equals("1") ){
					sql.append(" and CTS.PS_EMP_ID IN (:EMP_ID_S) ");
					condition.setObject("EMP_ID_S", pms000outputVO.getV_empList());
				}
			}
		}
			

		sql.append("order by CTS.REGION_CENTER_ID, CTS.BRANCH_AREA_ID, CTS.BRANCH_NBR");
		if (inputVO.getTarType().equals("1"))
			sql.append(", CTS.PS_EMP_ID ");
		
		condition.setQueryString(sql.toString());

		condition.setObject("yrmn", inputVO.getReportDate());
		
		sql.append(" order by CTS.LASTUPDATE,CTS.REGION_CENTER_ID,CTS.BRANCH_AREA_ID,CTS.BRANCH_NBR ");
		ResultIF list = dam.executePaging(condition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setResultList(list);
		if (list.size() > 0) {
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		}
		sendRtnObject(outputVO);
	}
	
	/**檢查CSV檔**/
	public void queryPsNbr(Object body, IPrimitiveMap header) throws Exception{
		PMS310InputVO inputVO = (PMS310InputVO) body;
		PMS310OutputVO OutputVO = new PMS310OutputVO();
		
		try{
			Path path = Paths.get(new File((String) SysInfo
					.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO
					.getFileName()).toString());
			
			List<Map<String, String>> map = null;
			List<String> lines = FileUtils.readLines(new File(path.toString()),"big5");
			
			List<String> new_Line = new ArrayList<String>();
			// 有選營運區而沒選AO時的篩選
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr()) && !StringUtils.isNotBlank(inputVO.getEmp_id())){
				for(int i = 0 ; i < lines.size() ; i++){
					liesCheck(lines.get(i).toString());	
					String str_line=finalstring;
					String[] str = str_line.split(",");
					if(StringUtils.isNotBlank(inputVO.getBranch_nbr()) && inputVO.getBranch_nbr().equals(str[1]))
						new_Line.add(str_line);
					if( i == 0 )
						new_Line.add(str_line);
				}
				lines = new_Line;
			}
			// 多筆資料時篩選單筆出來
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				for (int i = 0; i < lines.size(); i++) {
					liesCheck(lines.get(i).toString());	
					String str_line=finalstring;
					String[] str = str_line.split(",");
					if(inputVO.getEmp_id().equals(str[2]))
						new_Line.add(str_line);
					if(i == 0){
						new_Line.add(str_line);
					}
				}
				lines = new_Line;
			}
			
			for (int i = 1; i < lines.size(); i++) {
				liesCheck(lines.get(i).toString());	
				String str_line=finalstring;
				String[] str = str_line.split(",");
			
				yeaMon=(str[0].equals(""))?yeaMon:str[0];
				if (inputVO.getTarType().equals("1")) {
					if(str[2].length() != 6){
						if(str[2].length() == 5){
							str[2] = "0"+str[2];
						}else if(str[2].length() == 4){
							str[2] = "00"+str[2];
						}else if(str[2].length() == 3){
							str[2] = "000"+str[2];
						}else if(str[2].length() == 2){
							str[2] = "0000"+str[2];
						}else if(str[2].length() == 1){
							str[2] = "00000"+str[2];
						}
						map = getORGInfo(yeaMon, "",(str[2].equals("")?"":str[2]));
					}else{
						map = getORGInfo(yeaMon, "",(str[2].equals("")?"":str[2]));
					}
					if (map.size() == 0) {
						OutputVO.setPS_state("0");
						OutputVO.setPSEmpId(str[2]);
						sendRtnObject(OutputVO);
						return;
					}else if(!map.get(0).get("BRANCH_NBR").equals(str[1])){
						OutputVO.setNBR_state("0");
						OutputVO.setYEARMON(yeaMon);
						OutputVO.setPSEmpId(str[2]);
						OutputVO.setBRANCH_NBR(str[1]);
						sendRtnObject(OutputVO);
						return;
					}
				}
				if (inputVO.getTarType().equals("2")) {
					map = getORGInfo(yeaMon,(str[1].equals("")?"":str[1]), "");
					if(map.size() == 0){
						OutputVO.setNBR_state("0");
						OutputVO.setBRANCH_NBR(str[1]);
						sendRtnObject(OutputVO);
						return;
					}
				}
					
			}
			this.sendRtnObject(OutputVO);
		}catch(Exception e){
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
			
			
	}

	/** 新增CSV檔 **/
	public void insertCSVFile(Object body, IPrimitiveMap header)
			throws Exception {
		PMS310InputVO inputVO = (PMS310InputVO) body;
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		dam = this.getDataAccessManager();
		Path path = Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString());
		StringBuffer sql = new StringBuffer();
		String errMsg = "";
		List<Map<String, String>> map = null;
		List<String> lines = FileUtils.readLines(new File(path.toString()),"big5");
		// PS
		
		List<String> new_Line = new ArrayList<String>();
		if(StringUtils.isNotBlank(inputVO.getBranch_nbr()) && !StringUtils.isNotBlank(inputVO.getEmp_id())){
			for(int i = 0 ; i < lines.size() ; i++){
				liesCheck(lines.get(i).toString());	
				String str_line=finalstring;
				String[] str = str_line.split(",");
				if(StringUtils.isNotBlank(inputVO.getBranch_nbr()) && inputVO.getBranch_nbr().equals(str[1]))
					new_Line.add(str_line);
				if( i == 0 )
					new_Line.add(str_line);
			}
			lines = new_Line;
			if(lines.size() == 1)
				throw new APException("上傳資料與所選分行不符");
		}
		if(StringUtils.isNotBlank(inputVO.getEmp_id())){
			for (int i = 0; i < lines.size(); i++) {
				liesCheck(lines.get(i).toString());	
				String str_line=finalstring;
				String[] str = str_line.split(",");
				if(str[2].length() != 6){
					if(str[2].length() == 5){
						str[2] = "0"+str[2];
					}else if(str[2].length() == 4){
						str[2] = "00"+str[2];
					}else if(str[2].length() == 3){
						str[2] = "000"+str[2];
					}else if(str[2].length() == 2){
						str[2] = "0000"+str[2];
					}else if(str[2].length() == 1){
						str[2] = "00000"+str[2];
					}
				}
				if(inputVO.getEmp_id().equals(str[2])){
					new_Line.add(str_line);
					
				}
				if( i == 0){
					new_Line.add(str_line);
				}
			}
			lines = new_Line;
			if(lines.size() == 1)
				throw new APException("上傳資料與所選理專不符");
		}
		
		if (inputVO.getTarType().equals("1") && lines.size() > 1) {
			QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append(" delete TBPMS_CFP_TARGET_SET_PS where YEARMON = :yearmon ");
			condition2.setObject("yearmon", inputVO.getReportDate());
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr()) && !StringUtils.isNotBlank(inputVO.getEmp_id()) ){
				sql.append(" and BRANCH_NBR = :branch_nbr ");
				condition2.setObject("branch_nbr", inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" and PS_EMP_ID = :emp_id ");
				condition2.setObject("emp_id", inputVO.getEmp_id());
			}
			condition2.setQueryString(sql.toString());
			dam.exeUpdate(condition2);
		}
		if (inputVO.getTarType().equals("2") && lines.size() > 1) {
			QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("delete TBPMS_CFP_TARGET_SET_BR where YEARMON = :yearmon");
			condition2.setObject("yearmon", inputVO.getReportDate());
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
				sql.append(" and BRANCH_NBR = :branch_nbr ");
				condition2.setObject("branch_nbr", inputVO.getBranch_nbr());
			}
			condition2.setQueryString(sql.toString());
			dam.exeUpdate(condition2);
		}
		
		for (int i = 1; i < lines.size(); i++) {
			liesCheck(lines.get(i).toString());	
			String str_line=finalstring;
			String[] str = str_line.split(",");
			sql = new StringBuffer();
			yeaMon=(str[0].equals(""))?yeaMon:str[0];
			if (inputVO.getTarType().equals("1")) {
				
				if(str[2].length() != 6){
					if(str[2].length() == 5){
						str[2] = "0"+str[2];
					}else if(str[2].length() == 4){
						str[2] = "00"+str[2];
					}else if(str[2].length() == 3){
						str[2] = "000"+str[2];
					}else if(str[2].length() == 2){
						str[2] = "0000"+str[2];
					}else if(str[2].length() == 1){
						str[2] = "00000"+str[2];
					}
					map = getORGInfo(yeaMon, (str[1].equals("")?"":str[1]),(str[2].equals("")?"":str[2]));
				}else{
					map = getORGInfo(yeaMon, (str[1].equals("")?"":str[1]),(str[2].equals("")?"":str[2]));
				}
				sql.append("INSERT INTO TBPMS_CFP_TARGET_SET_PS (");
			}
			if (inputVO.getTarType().equals("2")) {
				map = getORGInfo(yeaMon,(str[1].equals("")?"":str[1]), "");
//				QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//				condition2.setQueryString("delete TBPMS_CFP_TARGET_SET_BR where YEARMON = "+((str[0].equals(""))?yeaMon:str[0])+" and BRANCH_NBR= "+(str[1].equals("")?"1":str[1]));
//				dam.exeUpdate(condition2);
				sql.append("INSERT INTO TBPMS_CFP_TARGET_SET_BR (");
			}
			int x=3;
			if(StringUtils.isNotBlank(inputVO.getEmp_id()) && !inputVO.getEmp_id().equals(str[2])){
				errMsg = "上傳之員工編號與所選員工編號不符";
			}
			if(errMsg.equals("")){
				try {
					//預設年月
					
					sql.append("YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME, ");
					sql.append("BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
					sql.append("BRANCH_NBR, BRANCH_NAME, ");
					if (inputVO.getTarType().equals("1"))
						sql.append("PS_EMP_ID, PS_EMP_NAME, ");
					sql.append("LT_CNT, LT_FND, LIMIT_CNT, LIMIT_FND, ");
					sql.append("MRTG_CNT, MRTG_FND, CREDIT_CNT, CREDIT_FND, ");
					sql.append("HB_OWN_CNT, HB_OWN_FND, HB_NON_CNT, HB_NON_FND, ");
					sql.append("HB_CNT, HB_FND, NON_HB_CNT, NON_HB_FND, ");
					sql.append("CREDIT_N_CNT, CREDIT_N_FND, CREDIT_G_CNT, CREDIT_G_FND, ");
					sql.append("CREDIT_C_CNT, CREDIT_C_FND, CREDIT_CARD_CNT,SPM_TARGET_FEE, ");
					sql.append("CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
					sql.append("VALUES ('" + yeaMon + "', ");
					if(map.size() != 0){
						sql.append("'" + (map.get(0).get("REGION_CENTER_ID")==null?"":map.get(0).get("REGION_CENTER_ID")) + "', ");
						sql.append("'" + (map.get(0).get("REGION_CENTER_NAME")==null?"":map.get(0).get("REGION_CENTER_NAME")) + "', ");
						sql.append("'" + (map.get(0).get("BRANCH_AREA_ID")==null?"":map.get(0).get("BRANCH_AREA_ID")) + "', ");
						sql.append("'" + (map.get(0).get("BRANCH_AREA_NAME")==null?"":map.get(0).get("BRANCH_AREA_NAME")) + "', ");
						sql.append("'" + (map.get(0).get("BRANCH_NBR")==null ? str[1] :map.get(0).get("BRANCH_NBR") ) + "', ");
						sql.append("'" + (map.get(0).get("BRANCH_NAME")==null?"":map.get(0).get("BRANCH_NAME")) + "', ");
					}else{
						sql.append("'" + "',");
						sql.append("'" + "',");
						sql.append("'" + "',");
						sql.append("'" + "',");
						sql.append("'" +str[1] + "',");
						sql.append("'" + "',");
					}
					
					if (inputVO.getTarType().equals("1")) {
						if(map.size() != 0){
							sql.append("'" + (map.get(0).get("EMP_ID")==null?str[2]:map.get(0).get("EMP_ID") )+ "', ");
							sql.append("'" + map.get(0).get("EMP_NAME") + "', ");
						}else{
							sql.append("'" +str[2]+ "',");
							sql.append("'" + "',");
						}
						
					}
					for(int j=2;j<25;j++){
						if(isNumeric(str[j])==1){x+=0;}else{x=j+1;};				
					}
						
					sql.append((str[3].equals("")?"0":str[3]) + ",");				
					sql.append((str[4].equals("")?"0":str[4]) + ",");				
					sql.append((str[5].equals("")?"0":str[5]) + ",");				
					sql.append((str[6].equals("")?"0":str[6]) + ",");				
					sql.append((str[7].equals("")?"0":str[7]) + ","); 
					sql.append((str[8].equals("")?"0":str[8]) + ",");
					sql.append((str[9].equals("")?"0":str[9]) + ",");
					sql.append((str[10].equals("")?"0":str[10]) + ",");
					sql.append((str[11].equals("")?"0":str[11]) + ",");
					sql.append((str[12].equals("")?"0":str[12]) + ",");
					sql.append((str[13].equals("")?"0":str[13]) + ",");							
					sql.append((str[14].equals("")?"0":str[14]) + ",");
					sql.append((str[15].equals("")?"0":str[15]) + ","); 
					sql.append((str[16].equals("")?"0":str[16]) + ","); 
					sql.append((str[17].equals("")?"0":str[17]) + ",");
					sql.append((str[18].equals("")?"0":str[18]) + ",");
					sql.append((str[19].equals("")?"0":str[19]) + ",");
					sql.append((str[20].equals("")?"0":str[20]) + ",");
					sql.append((str[21].equals("")?"0":str[21])	+ ",");
					sql.append((str[22].equals("")?"0":str[22]) + ",");
					sql.append((str[23].equals("")?"0":str[23]) + ",");
					sql.append((str[24].equals("")?"0":str[24]) + ",");
					sql.append((str[25].equals("")?"0":str[25]) + ",");
					sql.append((str[26].equals("")?"0":str[26]) + ",");
					sql.append(" SYSDATE,"+(String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID)+", "
							+ ""+(String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID)+", SYSDATE)");
					
						
					QueryConditionIF condition = dam.getQueryCondition();
					condition.setQueryString(sql.toString());
					try {
						dam.exeUpdate(condition);
					} catch (JBranchException e) {
						throw new APException("上傳檔案資料有誤");
					}
				}catch (JBranchException e) {
					throw new APException("第"+(i+1)+"筆檔案的第"+x+"欄位資料有誤，請檢查分行代碼或員工編號!");
				}
			}
		}
//		this.sendRtnObject(null);
		if (!errMsg.equals(""))
			throw new APException(errMsg);
		else
			this.sendRtnObject(null);

	}
	
	public int  isNumeric(String str){
		 try{
			 if(str.equals(""))
				 str="0";
             Double.parseDouble(str);
             return 1;
            }catch(Exception e){
             return 0;
            }
		}

	/** 取得消金PS區域中心、營運區、分行資訊 **/
	public List<Map<String, String>> getORGInfo(String ym,String br, String ps)
			throws JBranchException {
		try{
			dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("  SELECT EMP.ORG_ID             AS ORG_ID,                                                                             ");
			sql.append("         ORG.ORG_TYPE           AS ORG_TYPE,                                                                           ");
			sql.append("         ORG.HEAD_OFFICE_ID     AS HEAD_OFFICE_ID,                                                                     ");
			sql.append("         ORG.HEAD_OFFICE_NAME   AS HEAD_OFFICE_NAME,                                                                   ");
			sql.append("         ORG.REGION_CENTER_ID   AS REGION_CENTER_ID,                                                                   ");
			sql.append("         ORG.REGION_CENTER_NAME AS REGION_CENTER_NAME,                                                                 ");
			sql.append("         ORG.BRANCH_AREA_ID     AS BRANCH_AREA_ID,                                                                     ");
			sql.append("         ORG.BRANCH_AREA_NAME   AS BRANCH_AREA_NAME,                                                                   ");
			sql.append("         ORG.BA_CLS             AS BA_CLS,                                                                             ");
			sql.append("         ORG.BRANCH_NBR         AS BRANCH_NBR,                                                                         ");
			sql.append("         ORG.BRANCH_NAME        AS BRANCH_NAME,                                                                        ");
			sql.append("         ORG.BRANCH_CLS         AS BRANCH_CLS,                                                                         ");
			sql.append("         ORG.AO_GROUP_ID        AS AO_GROUP_ID,                                                                        ");
			sql.append("         ORG.AO_GROUP_NAME      AS AO_GROUP_NAME,                                                                      ");
			sql.append("         EMP.JOB_TITLE_NAME     AS JOB_TITLE_NAME,                                                                     ");
			sql.append("         EMP.JOB_RANK           AS JOB_RANK,                                                                           ");
			sql.append("         EMP.EMP_ID             AS EMP_ID,                                                                             ");
			sql.append("         EMP.EMP_NAME           AS EMP_NAME,                                                                           ");
			sql.append("         EMP.GROUP_TYPE         AS GROUP_TYPE,                                                                         ");
			sql.append("         EMP.ROLE_ID            AS ROLE_ID,                                                                            ");
			sql.append("         EMP.IS_PRIMARY_ROLE    AS IS_PRIMARY_ROLE,                                                                    ");
			sql.append("         EMP.PS_FLAG            AS PS_FLAG,                                                                            ");
			sql.append("         EMP.AO_JOB_RANK        AS AO_JOB_RANK,                                                                        ");
			sql.append("         EMP.SALES_SUP_EMP_ID   AS SALES_SUP_EMP_ID                                                                    ");
			sql.append("  FROM TBPMS_EMPLOYEE_REC_N EMP                                                                                          ");
			sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORG                                                                                         ");
//			sql.append("    ON EMP.ORG_ID = ORG.ORG_ID                                                                                           ");
			sql.append("    ON EMP.DEPT_ID = ORG.DEPT_ID                                                                                           ");
			if (!br.equals(""))
				sql.append(" AND ORG.BRANCH_NBR = '" + br + "'");
			sql.append("    AND LAST_DAY(TO_DATE(:DATES,'YYYYMM')) BETWEEN ORG.START_TIME AND ORG.END_TIME                                                                  ");
//			sql.append("    AND ORG.REGION_CENTER_ID <> 'xxxxxxxxxx' AND ORG.BRANCH_AREA_ID <> 'xxxxxxxxxx' AND ORG.BRANCH_NBR <> 'xxxxxxxxxx'   ");
			sql.append("  WHERE LAST_DAY(TO_DATE(:DATES,'YYYYMM')) BETWEEN EMP.START_TIME AND EMP.END_TIME                                                                  ");
	//		sql.append("    AND EMP.PS_FLAG='Y'                                                                                                  ");
			
			if (!ps.equals("")){
				sql.append("    AND EMP.PS_FLAG='Y'  ");
				sql.append("    AND EMP.EMP_ID = '" + ps + "'");
			}
			if(br.equals("") && ps.equals(""))
				sql.append("    AND EMP.PS_FLAG='NULL'  ");
			if (!ym.equals("")){
				condition.setObject("DATES", ym);
			}
			condition.setQueryString(sql.toString());
	
			return dam.exeQuery(condition);
		}catch(JBranchException e){
			throw new APException("請確認資料年月是否正確,是否為第一欄位!!");
		}
	}

	/** 刪除資料 **/
	public void delData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS310InputVO inputVO = (PMS310InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		if (inputVO.getTarType().equals("1"))
			condition
					.setQueryString("delete TBPMS_CFP_TARGET_SET_PS where YEARMON = :yrmn");
		if (inputVO.getTarType().equals("2"))
			condition
					.setQueryString("delete TBPMS_CFP_TARGET_SET_BR where YEARMON = :yrmn");
		condition.setObject("yrmn", inputVO.getReportDate());
		dam.exeUpdate(condition);

		this.sendRtnObject(null);
	}
	
	
	/** 找字串"\""功能 **/
	public String liesCheck(String lines)
			throws JBranchException {
		
		if(lines.indexOf("\"")!=-1){
			//抓出第一個"
			int acc=lines.indexOf("\"");
			//原本所有字串
			StringBuffer lines1 = new StringBuffer(lines.toString());
			//切掉前面的字串
			String ori=lines.substring(0,acc);
			//
			String oriline =ori.toString();
			
			String fist=lines1.substring(acc);
			fist=fist.substring(1,fist.indexOf("\"",1));
			String[] firstsub=fist.split(",");
			for(int i=0;i<firstsub.length;i++){
				oriline+=firstsub[i].toString();
			}
			String okfirst=ori+"\""+fist+"\"";   //前面組好的
			String oriline2 = lines.substring(acc);
			
			oriline2=oriline2.replaceFirst("\""+fist+"\"", "");
			String fistTwo=lines1.substring(0,lines1.indexOf("\""));
			lines=oriline+oriline2;

		}
		if(lines.indexOf("\"")!=-1){
			liesCheck(lines);
		}else{
			finalstring=lines;
		}
		return lines;
			
	}
}
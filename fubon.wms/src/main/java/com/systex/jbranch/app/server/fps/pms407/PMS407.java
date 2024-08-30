package com.systex.jbranch.app.server.fps.pms407;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.fubon.jlb.DataFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :庫存資產減損月報<br>
 * Comments Name : PMS407.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms407")
@Scope("request")
public class PMS407 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS407.class);
	
	/**取得可視範圍**/
	/*
	public void getOrgInfo(Object body, IPrimitiveMap header) throws JBranchException, ParseException {	
		PMS407InputVO inputVO = (PMS407InputVO) body;
		PMS407OutputVO outputVO = new PMS407OutputVO();
		Timestamp stamp = new Timestamp(System.currentTimeMillis());		
		String loginID = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);				
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		 old version
		sql.append("SELECT * FROM table( ");
		sql.append("FN_getVISUAL_RANGE( ");
		sql.append(":purview_type, null, :e_dt, :emp_id, ");
		sql.append(":org_id, :v_ao_flag, :v_emp_id, NULL) ");		
		sql.append(") ");
		
		//new version
		sql.append("SELECT V_REGION_CENTER_ID, V_REGION_CENTER_NAME, ");
		sql.append("V_BRANCH_AREA_ID, V_BRANCH_AREA_NAME, ");
		sql.append("V_BRANCH_NBR, V_BRANCH_NAME, ");
		sql.append("V_AO_CODE, V_EMP_ID, V_EMP_NAME,V_ORG_TYPE ");
		sql.append("FROM TABLE ( ");
		sql.append("FC_GET_VRR( ");
		sql.append(":purview_type, null, :e_dt, :emp_id, ");
		sql.append("null, null, null, null) ");
		sql.append(") ");
		
		condition.setQueryString(sql.toString());
		
		condition.setObject("purview_type", "OTHER");   //非業績報表
		condition.setObject("purview_type", "P_PERF");  //個人業績報表
		condition.setObject("purview_type", "ORG_PERF"); //轄下人員業績
		
		condition.setObject("s_dt", null);	
		if(StringUtils.isNotBlank(inputVO.getDataMonth())){	
			Date lastDate = getMonthLastDate(inputVO.getDataMonth());
			condition.setObject("e_dt", lastDate);
		}else
			condition.setObject("e_dt", stamp);
		condition.setObject("emp_id", loginID);
		condition.setObject("org_id", null);
		condition.setObject("v_ao_flag", null);
		condition.setObject("v_emp_id", null);			
		outputVO.setOrgList(dam.exeQuery(condition));	
		
		sendRtnObject(outputVO);		
	}
	*/
	/***查詢資料***/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		PMS407InputVO inputVO = (PMS407InputVO) body;
		PMS407OutputVO outputVO = new PMS407OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer(" SELECT * FROM( ");
//		sql.append(" SELECT A.YEARMON, A.REGION_CENTER_NAME, ");
//		sql.append(" A.BRANCH_AREA_NAME, A.BRANCH_NBR, A.BRANCH_NAME, ");
//		sql.append(" A.AO_CODE, A.CUST_ID, A.CUST_NAME, ");
//		sql.append(" A.FUND_VALU, A.FUND_COST, A.FUND_LOSS_RATE, ");
//		sql.append(" A.ETF_VALU, A.ETF_COST, A.ETF_LOSS_RATE, ");
//		sql.append(" A.STK_VALU, A.STK_COST, A.STK_LOSS_RATE, ");
//		sql.append(" A.SI_VALU, A.SI_COST, A.SI_LOSS_RATE, ");
//		sql.append(" A.SN_VALU, A.SN_COST, A.SN_LOSS_RATE, ");
//		sql.append(" A.BND_VALU, A.BND_COST, A.BND_LOSS_RATE, ");
////		sql.append(" B.AST_TYPE,B.AST_TYPE_NAME, B.PRD_ID, D.INFO_TYPE, D.INFO, ");
//		sql.append(" d.PRD_TYPE as AST_TYPE, pa.param_name As AST_TYPE_NAME, d.prd_id, D.INFO_TYPE, D.INFO, ");
//		sql.append(" A.INTERVIEW_YN, A.CREATETIME AS CDATE   ");
//		sql.append(" FROM TBPMS_MON_AST_LOSS_MAST A ");	
//		sql.append(" LEFT JOIN TBPMS_MON_AST_LOSS_DTL B ");   //2017/01/05  改left join
//		sql.append(" 	ON A.YEARMON = B.YEARMON AND A.CUST_ID = B.CUST_ID ");
////		sql.append(" LEFT JOIN ( ");
////		sql.append(" 	SELECT DISTINCT ");
////		sql.append(" 		PROD_ID,AST_TYPE ");
////		sql.append(" 	FROM TBCRM_AST_ALLPRD_DETAIL_HIST" );
////		sql.append(" 	WHERE AST_TYPE IN ('07','08','09','10','12','15') ");
////		sql.append(" ) C ");
////		sql.append(" 	ON B.PRD_ID = C.PROD_ID ");
//		sql.append(" LEFT JOIN TBPMS_MON_AST_INFO D ");       //2017/01/05  改left join
//		sql.append(" 	ON B.YEARMON = D.YEARMON AND B.PRD_ID = D.PRD_ID ");
//		sql.append(" LEFT JOIN TBSYSPARAMETER PA ON PA.PARAM_TYPE='PMS.PRD_TYPE' AND PA.PARAM_CODE=D.PRD_TYPE ");
//		sql.append(" WHERE 1=1 ");	
		sql.append(" SELECT D.* , ");
		sql.append(" M.INFO_TYPE , ");
		sql.append(" M.INFO , ");
		sql.append(" M.PRD_TYPE AS AST_TYPE, ");
		sql.append(" M.AST_TYPE_NAME, ");
		sql.append(" M.PRD_ID, ");
		sql.append(" CASE WHEN D.FUND_LOSS_RATE <= -10 THEN 'Y' ");
		sql.append(" 	  WHEN D.ETF_LOSS_RATE <= -10 THEN 'Y' ");
		sql.append(" 	  WHEN D.STK_LOSS_RATE <= -10 THEN 'Y' ");
		sql.append(" 	  WHEN D.SI_LOSS_RATE <= -10 THEN 'Y' ");
		sql.append(" 	  WHEN D.SN_LOSS_RATE <= -10 THEN 'Y' ");
		sql.append(" 	  WHEN D.BND_LOSS_RATE <= -10 THEN 'Y' ELSE 'N' END LOSS_FLAG ");
		sql.append(" FROM TBPMS_MON_AST_LOSS_MAST D ");
		sql.append(" LEFT JOIN ( ");
		sql.append(" SELECT A.CUST_ID,A.YEARMON,A.PRD_ID, B.INFO_TYPE, B.INFO ,B.PRD_TYPE , pa.param_name As AST_TYPE_NAME ");
		sql.append(" FROM TBPMS_MON_AST_LOSS_DTL A,TBPMS_MON_AST_INFO B ");
		sql.append(" LEFT JOIN TBSYSPARAMETER PA ON PA.PARAM_TYPE='PMS.PRD_TYPE' AND PA.PARAM_CODE=B.PRD_TYPE ");
		sql.append(" WHERE A.YEARMON = B.YEARMON AND A.PRD_ID = B.PRD_ID ) M ");
		sql.append(" ON D.YEARMON = M.YEARMON  AND M.CUST_ID = D.CUST_ID ");
		sql.append(" WHERE 1=1 ");
		if (!"".equals(inputVO.getsCreDate())){
			sql.append(" AND D.YEARMON = ? ");
			sql_list.add(inputVO.getsCreDate());
		}		
//		//區域中心
//		if(!inputVO.getRegion_center_id().equals("")){
//			sql.append(" AND D.REGION_CENTER_ID = ? ");
//			sql_list.add(inputVO.getRegion_center_id());
//		}
//		//營運區
//		if(!inputVO.getBranch_area_id().equals("")){
//			sql.append(" AND D.BRANCH_AREA_ID = ? ");
//			sql_list.add(inputVO.getBranch_area_id());
//		}
//		//分行
//		if(!inputVO.getBranch_nbr().equals("")){
//			sql.append(" AND D.BRANCH_NBR = ? ");
//			sql_list.add(inputVO.getBranch_nbr());
//		}
		
		// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("and D.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBranch_nbr());
		// 營運區	
		}else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append("and D.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = ? ) ");
			sql_list.add(inputVO.getBranch_area_id());
	    // 區域中心	
		}else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())){
			sql.append("and D.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = ? ) ");
			sql_list.add(inputVO.getRegion_center_id());
		}
		//理專員編
		if(!inputVO.getAo_code().equals("")){
			sql.append("and D.AO_CODE = ? ");
			sql_list.add(inputVO.getAo_code());
		}
//		sql.append(" ORDER BY A.YEARMON, A.REGION_CENTER_ID, ");		
//		sql.append(" A.BRANCH_AREA_ID, A.BRANCH_NBR, A.AO_CODE, A.CUST_ID) t ");
//		sql.append(" group by A.YEARMON, A.REGION_CENTER_NAME, A.BRANCH_AREA_NAME, A.BRANCH_NBR, A.BRANCH_NAME,  ");
//		sql.append(" A.AO_CODE, A.CUST_ID, A.CUST_NAME, A.FUND_VALU, A.FUND_COST,  ");
//		sql.append(" A.FUND_LOSS_RATE, A.ETF_VALU, A.ETF_COST, A.ETF_LOSS_RATE, A.STK_VALU,  ");
//		sql.append(" A.STK_COST, A.STK_LOSS_RATE, A.SI_VALU, A.SI_COST, A.SI_LOSS_RATE,  ");
//		sql.append(" A.SN_VALU, A.SN_COST, A.SN_LOSS_RATE, A.BND_VALU, A.BND_COST,  ");
//		sql.append(" A.BND_LOSS_RATE, pa.param_name, d.prd_id, D.INFO_TYPE, D.INFO,d.PRD_TYPE,  ");
//		sql.append(" A.INTERVIEW_YN, A.CREATETIME  ) t ");
//		sql.append(" WHERE t.FUND_LOSS_RATE <= -10 or t.ETF_LOSS_RATE <= -10 or t.STK_LOSS_RATE <= -10 or t.SI_LOSS_RATE <= -10 or t.SN_LOSS_RATE <= -10 or t.BND_LOSS_RATE <= -10 ");
		sql.append(" )WHERE LOSS_FLAG='Y' ");
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		
		
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		
		int totalPage = list.getTotalPage();
		outputVO.setTotalPage(totalPage);
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
	
		sendRtnObject(outputVO);
	}			
	
	/** 訊息查詢功能 **/
	public void queryMsg(Object body, IPrimitiveMap header) throws JBranchException {
		PMS407InputVO inputVO = (PMS407InputVO) body;
		PMS407OutputVO outputVO = new PMS407OutputVO();		
		DataAccessManager dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT YEARMON, PRD_TYPE, PRD_ID, INFO_TYPE, INFO, ");
		sql.append("TO_CHAR(LASTUPDATE, 'YYYY-MM-DD') AS LASTUPDATE, MODIFIER ");
		sql.append("from TBPMS_MON_AST_INFO ");		
		sql.append("where 1=1 ");
		if (inputVO.getRptYearMon() != null) {
			String DYearMon = new SimpleDateFormat("yyyyMM").format(inputVO.getRptYearMon());
			sql.append("and YEARMON = :yearmon ");
			queryCondition.setObject("yearmon", DYearMon);
		}
		if (!StringUtils.isBlank(inputVO.getPtype())){
			sql.append("and PRD_TYPE = :PRD_TYPE ");
			queryCondition.setObject("PRD_TYPE", inputVO.getPtype());
		}
//		switch(inputVO.getPtype()) {
//			case "SI":
//				sql.append("and PRD_TYPE = '01' ");
//				break;
//			case "SN":
//				sql.append("and PRD_TYPE = '02' ");
//				break;
//			case "BND":
//				sql.append("and PRD_TYPE = '03' ");
//				break;
//		}
		
		sql.append("order by YEARMON DESC ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setMsgList(list);
		this.sendRtnObject(outputVO);
	}
	
	/** 上傳檔案 **/
	public void insertCSVFile(Object body, IPrimitiveMap header) throws Exception{
		PMS407InputVO inputVO = (PMS407InputVO)body;		
		dam = this.getDataAccessManager();
		Path path = Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString());

		String loginID = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);
		List<Map<String, String>> map = null;
		List<String> lines = FileUtils.readLines(new File(path.toString()), "big5");		
		String errMsg = "";
		boolean flag = false;
		
		//2017/6/3 input type date
		String DYearMon = new SimpleDateFormat("yyyyMM").format(inputVO.getRptYearMon());
		//
		//檢查上傳檔案內容年月是否符合「報表年月」
		for(int j = 1; j < lines.size(); j++){
			String[] str0 = lines.get(j).split(",");
			if(!str0[0].equals(DYearMon)){
				flag = true;
			}
		}
		if(flag){
			throw new APException("上傳資料的年月應與輸入的「報表年月」相同！");
		}else{
			QueryConditionIF condition = dam.getQueryCondition();
			StringBuffer sql_0 = new StringBuffer();
			sql_0.append("DELETE FROM TBPMS_MON_AST_INFO ");
			sql_0.append("WHERE YEARMON = '"+ DYearMon +"'");
			condition.setQueryString(sql_0.toString());
			dam.exeUpdate(condition);
			
			PMS407OutputVO outputVO = new PMS407OutputVO();	
			 List<Integer> x=new ArrayList<>();	
			
			for(int i = 1; i < lines.size(); i++){
				String[] str = lines.get(i).split(",");
				StringBuffer sql = new StringBuffer();
				sql.append("INSERT INTO TBPMS_MON_AST_INFO ( ");
				sql.append("YEARMON, PRD_TYPE, PRD_ID, INFO_TYPE, INFO, ");
				sql.append("CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
				sql.append("VALUES ('");
				
				if(Integer.parseInt(str[3])>3)
				{
					Integer  y=new Integer(1);	
					   x.add(y);
					   outputVO.setMsgList(x);
					   sendRtnObject(outputVO);
					  return;
				}
				if(utf_8_length(str[4]) > 150)
				{
				
					Integer  y=new Integer(2);	
				     
				   x.add(y);
				  
				   outputVO.setMsgList(x);
				   sendRtnObject(outputVO);
				  return;
				}
				
				sql.append(str[0]+"','0"+str[1]+"','"+str[2]+"','0"+str[3]+"','"+str[4]+"',");						
				sql.append(" SYSDATE, '"+ loginID + "', '"+ loginID +"',  SYSDATE)");	
				//商品類別為02或03，訊息欄位才可為02;商品類別為01或02，訊息欄位才可為01
				if( (str[3].equals("02") && (!str[1].equals("02") && !str[1].equals("03")) ) ||
						(str[3].equals("01") && (!str[1].equals("01") && !str[1].equals("02")) )	
						){
					errMsg += "第"+i+"筆資料「商品類別」與「訊息類別」資料對應不符規定！\r\n";
				}else{
					condition.setQueryString(sql.toString());
					dam.exeUpdate(condition);				
				}
			}		
			
			Integer  y=new Integer(0);	
			 
			   x.add(y);
			 //  outputVO.setCheck(x);
			   outputVO.setMsgList(x);
			   sendRtnObject(outputVO);
			
			if(!errMsg.equals("")){
				throw new APException(errMsg);
			}			
		}		
	}
	
	
	/*  === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		
	/*	
		int totalPage =0,i=0,j=2000,total=0;
		int cvs1 = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "庫存資產減損月報_" + sdf.format(new Date()) + ".csv";
		List listCSV =  new ArrayList();
		boolean cn=false;
		do	
	  {	
		  PMS407InputVO inputVO = (PMS407InputVO) body;  
		
		  StringBuffer sql = new StringBuffer("select ROWNUM,t.* from ( ");
		sql.append("select A.YEARMON,A.REGION_CENTER_NAME, ");
		sql.append("A.BRANCH_AREA_NAME,A.BRANCH_NBR,A.BRANCH_NAME, ");
		sql.append("A.AO_CODE,A.CUST_ID,A.CUST_NAME,A.FUND_VALU, ");
		sql.append("A.FUND_COST,A.FUND_LOSS_RATE,A.ETF_VALU, ");
		sql.append("A.ETF_COST,A.ETF_LOSS_RATE,A.STK_VALU, ");
		sql.append("A.STK_COST,A.STK_LOSS_RATE, ");
		sql.append("C.PRD_TYPE,B.PRD_ID,C.INFO_TYPE,C.INFO, ");
		sql.append("A.INTERVIEW_YN ");
		sql.append("from TBPMS_MON_AST_LOSS_MAST A ");	
		sql.append("left JOIN TBPMS_MON_AST_LOSS_DTL B ");   //2017/01/05  改left join
		sql.append("ON A.YEARMON=B.YEARMON AND A.CUST_ID=B.CUST_ID ");
		sql.append("left JOIN TBPMS_MON_AST_INFO C ");       //2017/01/05  改left join
		sql.append("ON B.YEARMON=C.YEARMON AND B.PRD_ID=C.PRD_ID)t ");
		sql.append("where ROWNUM<=");
        sql.append(String.valueOf(j));  
        sql.append(" MINUS ");
        sql.append("select ROWNUM,t.* from ( ");
		sql.append("select A.YEARMON,A.REGION_CENTER_NAME, ");
		sql.append("A.BRANCH_AREA_NAME,A.BRANCH_NBR,A.BRANCH_NAME, ");
		sql.append("A.AO_CODE,A.CUST_ID,A.CUST_NAME,A.FUND_VALU, ");
		sql.append("A.FUND_COST,A.FUND_LOSS_RATE,A.ETF_VALU, ");
		sql.append("A.ETF_COST,A.ETF_LOSS_RATE,A.STK_VALU, ");
		sql.append("A.STK_COST,A.STK_LOSS_RATE, ");
		sql.append("C.PRD_TYPE,B.PRD_ID,C.INFO_TYPE,C.INFO, ");
		sql.append("A.INTERVIEW_YN ");
		sql.append("from TBPMS_MON_AST_LOSS_MAST A ");	
		sql.append("left JOIN TBPMS_MON_AST_LOSS_DTL B ");   //2017/01/05  改left join
		sql.append("ON A.YEARMON=B.YEARMON AND A.CUST_ID=B.CUST_ID ");
		sql.append("left JOIN TBPMS_MON_AST_INFO C ");       //2017/01/05  改left join
		sql.append("ON B.YEARMON=C.YEARMON AND B.PRD_ID=C.PRD_ID)t ");
        sql.append(" where ROWNUM<=");
        sql.append(String.valueOf(i)); 
        condition.setQueryString(sql.toString()); 
         
        ResultIF list = dam.executePaging(condition, inputVO
   				.getCurrentPageIndex() + 1, inputVO.getPageCount());
           totalPage = list.getTotalPage();
         int k=1; 
           
       while(k<totalPage &&(cn==false))
       { 
    	  
    	   
    	   
    	   List<Map<String, Object>> list1=list;
        for(Map<String,Object> map:list1)
        {
        System.out.print(map.get("ROWNUM"));	
        if(Integer.valueOf(map.get("ROWNUM").toString())>9000){cn=true;break; }
        System.out.println();	
        }
       
        
        	for(Map<String,Object> map:list1)
        {
        String[] records = new String[23];	
        	int ii=0;
        records[ii] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
		records[++ii] = checkIsNull(map, "YEARMON"); //資料月份
		records[++ii] = checkIsNull(map, "REGION_CENTER_NAME"); //區部名稱
		records[++ii] = checkIsNull(map, "BRANCH_AREA_NAME");  //區名
		records[++ii] = checkIsNull(map, "BRANCH_NBR"); //分行代碼
		records[++ii] = checkIsNull(map, "BRANCH_NAME"); //分行名稱
		records[++ii] = checkIsNull(map, "AO_CODE");  //AO Code
		records[++ii] = checkIsNull(map, "CUST_ID"); //客戶ID
		records[++ii] = checkIsNull(map, "CUST_NAME"); //客戶姓名				
		records[++ii] = currencyFormat(map, "FUND_VALU");  //基金投資市值
		records[++ii] = currencyFormat(map, "FUND_COST");  //基金投資成本
		records[++ii] = checkIsNull(map, "FUND_LOSS_RATE")+"%";  //基金流失比率%
		records[++ii] = currencyFormat(map, "ETF_VALU");  //ETF投資市值
		records[++ii] = currencyFormat(map, "ETF_COST");  //ETF投資成本
		records[++ii] = checkIsNull(map, "ETF_LOSS_RATE")+"%";  //ETF流失比率%
		records[++ii] = currencyFormat(map, "STK_VALU");  //海外股票投資市值
		records[++ii] = currencyFormat(map, "STK_COST");  //海外股票投資成本
		records[++ii] = checkIsNull(map, "STK_LOSS_RATE")+"%";  //海外股票流失比率%				
		records[++ii] = checkIsNull(map, "PRD_TYPE"); //SI配息
		records[++ii] = checkIsNull(map, "PRD_ID"); //SI降評
		records[++ii] = checkIsNull(map, "INFO_TYPE"); //SN配息
		records[++ii] = checkIsNull(map, "INFO"); //SN降息			
		records[++ii] = checkIsNull(map, "INTERVIEW_YN"); //訪談記錄
		
		listCSV.add(records);	
		
        
        }
        k++;
		 list = dam.executePaging(condition, inputVO
	   				.getCurrentPageIndex() + k, inputVO.getPageCount());
       }          
        
       i=j;
	   j+=2000;
	   total+=totalPage*10;
	  }while(totalPage==200 &&(cn==false));
	  
	  
		String [] csvHeader = new String[23];
		int jj = 0;
		csvHeader[jj] = "序號";
		csvHeader[++jj] = "資料月份";
		csvHeader[++jj] = "區部名稱";
		csvHeader[++jj] = "區名";
		csvHeader[++jj] = "分行代碼";
		csvHeader[++jj] = "分行名稱";
		csvHeader[++jj] = "AO Code";
		csvHeader[++jj] = "客戶ID";
		csvHeader[++jj] = "客戶姓名";
		csvHeader[++jj] = "基金投資市值";
		csvHeader[++jj] = "基金投資成本";
		csvHeader[++jj] = "基金流失比率%";
		csvHeader[++jj] = "海外ETF投資市值";
		csvHeader[++jj] = "海外ETF投資成本";
		csvHeader[++jj] = "海外ETF流失比率%";
		csvHeader[++jj] = "海外股票投資市值";
		csvHeader[++jj] = "海外股票投資成本";
		csvHeader[++jj] = "海外股票流失比率%";			
		csvHeader[++jj] = "SI配息";
		csvHeader[++jj] = "SI降評";
		csvHeader[++jj] = "SN配息";
		csvHeader[++jj] = "SN降息";		
		csvHeader[++jj] = "訪談記錄";			
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	 */ 
	  
	  
		
		PMS407OutputVO outputVO = (PMS407OutputVO) body;		
		
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "庫存資產減損月報_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		int rownum = 1; //資料排序序號
		for(Map<String, Object> map : list){
			String[] records = new String[32];
			int i = 0;
			records[i] = Integer.toString(rownum++);
			records[++i] = checkIsNull(map, "YEARMON"); //資料月份
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); //區部名稱
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");  //區名
			records[++i] = checkIsNull(map, "BRANCH_NBR"); //分行代碼
			records[++i] = checkIsNull(map, "BRANCH_NAME"); //分行名稱
			records[++i] = checkIsNull(map, "AO_CODE");  //AO Code
			records[++i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, "CUST_ID")); //客戶ID
			records[++i] = DataFormat.getNameForHighRisk(checkIsNull(map, "CUST_NAME")); //客戶姓名
			records[++i] = currencyFormat(map, "FUND_VALU");  //基金投資市值
			records[++i] = currencyFormat(map, "FUND_COST");  //基金投資成本
			records[++i] = checkIsNull(map, "FUND_LOSS_RATE")+"%";  //基金流失比率%
			records[++i] = currencyFormat(map, "ETF_VALU");  //ETF投資市值
			records[++i] = currencyFormat(map, "ETF_COST");  //ETF投資成本
			records[++i] = checkIsNull(map, "ETF_LOSS_RATE")+"%";  //ETF流失比率%
			records[++i] = currencyFormat(map, "STK_VALU");  //海外股票投資市值
			records[++i] = currencyFormat(map, "STK_COST");  //海外股票投資成本
			records[++i] = checkIsNull(map, "STK_LOSS_RATE")+"%";  //海外股票流失比率%
			records[++i] = currencyFormat(map, "SI_VALU"); //SI投資市值
			records[++i] = currencyFormat(map, "SI_COST"); //SI投資成本
			records[++i] = checkIsNull(map, "SI_LOSS_RATE")+"%"; //SI流失比率
			records[++i] = currencyFormat(map, "SN_VALU"); //SN投資市值
			records[++i] = currencyFormat(map, "SN_COST"); //SN投資成本
			records[++i] = checkIsNull(map, "SN_LOSS_RATE")+"%"; //SN流失比率
			records[++i] = currencyFormat(map, "BND_VALU"); //海外債投資市值
			records[++i] = currencyFormat(map, "BND_COST"); //海外債投資成本
			records[++i] = checkIsNull(map, "BND_LOSS_RATE")+"%"; //海外債流失比率
			records[++i] = checkIsNull(map, "PRD_TYPE"); //SI配息
			records[++i] = checkIsNull(map, "PRD_ID"); //SI降評
			records[++i] = checkIsNull(map, "INFO_TYPE"); //SN配息
			records[++i] = checkIsNull(map, "INFO"); //SN降息			
			records[++i] = checkIsNull(map, "INTERVIEW_YN"); //訪談記錄
			
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[32];
		int j = 0;
		csvHeader[j] = "序號";
		csvHeader[++j] = "資料月份";
		csvHeader[++j] = "業務處名稱";
		csvHeader[++j] = "區名";
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "分行名稱";
		csvHeader[++j] = "AO Code";
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "客戶姓名";
		csvHeader[++j] = "基金投資市值";
		csvHeader[++j] = "基金投資成本";
		csvHeader[++j] = "基金流失比率%";
		csvHeader[++j] = "海外ETF投資市值";
		csvHeader[++j] = "海外ETF投資成本";
		csvHeader[++j] = "海外ETF流失比率%";
		csvHeader[++j] = "海外股票投資市值";
		csvHeader[++j] = "海外股票投資成本";
		csvHeader[++j] = "海外股票流失比率%";
		csvHeader[++j] = "SI投資市值";
		csvHeader[++j] = "SI投資成本";
		csvHeader[++j] = "SI流失比率%";
		csvHeader[++j] = "SN投資市值";
		csvHeader[++j] = "SN投資成本";
		csvHeader[++j] = "SN流失比率%";
		csvHeader[++j] = "海外債投資市值";
		csvHeader[++j] = "海外債投資成本";
		csvHeader[++j] = "海外債流失比率%";
		csvHeader[++j] = "商品類別";
		csvHeader[++j] = "商品代碼";
		csvHeader[++j] = "訊息類別";
		csvHeader[++j] = "訊息內容";		
		csvHeader[++j] = "訪談記錄";			
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	
	return;
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* 
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
	//處理貨幣格式
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));										
		}else
			return "0.00";		
	}
	
	/** 取月份最後一天 
	 * @throws ParseException **/
	private Date getMonthLastDate(String date) throws ParseException{
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		Date rptDate = df.parse(date);
		cal.setTime(rptDate);
		cal.set(cal.DATE, cal.getActualMaximum(cal.DATE));
		return cal.getTime();
	}
	
	/** 下載範例檔 **/
	public void downloadSample(Object body, IPrimitiveMap header) throws Exception {
		PMS407InputVO inputVO = (PMS407InputVO) body;
		notifyClientToDownloadFile("doc//PMS//PMS407_EXAMPLE.csv", "訊息上傳範例.csv");
		
	    this.sendRtnObject(null);
	}
}
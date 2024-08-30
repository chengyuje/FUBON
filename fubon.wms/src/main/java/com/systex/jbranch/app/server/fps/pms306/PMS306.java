package com.systex.jbranch.app.server.fps.pms306;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DateFormat;
import com.systex.jbranch.app.common.fps.table.TBPMS_INS_ACCEPT_TIMEVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_INS_OFFSETVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SETPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_INS_TARGET_SETVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXNPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXNVO;
import com.systex.jbranch.app.server.fps.pms302.PMS302OutputVO;
import com.systex.jbranch.app.server.fps.pms357.PMS357OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdVO;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.util.Arrays;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :保險速報作業 Controller <br>
 * Comments Name : PMS306.java<br>
 * Author :Kevin<br>
 * Date :2016年08月09日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms306")
@Scope("request")
public class PMS306 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	Timestamp currentTM = new Timestamp(System.currentTimeMillis());
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();

	public PMS306() {
		headColumnMap.put("輔銷團隊(IA/FA)", "SUPT_SALES_TEAM_ID");
		headColumnMap.put("員工編號", "EMP_ID");
		headColumnMap.put("員工姓名", "EMP_NAME");
		headColumnMap.put("分行代碼", "BRANCH_NBR");
		headColumnMap.put("分行名稱", "DEPT_NAME");
		headColumnMap.put("最後修改人", "MODIFIER");
		headColumnMap.put("最後修改時間", "LASTUPDATE");
	}

	/******** 註解內容查詢 ********/
	public void noteText(Object body, IPrimitiveMap header) throws Exception {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("select REMARK " + "from " + "TBPMS_INS_NOTE ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			outputVO.setNotelist(list); // data
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/******** 註解內容上傳 ********/
	public void noteTextUp(Object body, IPrimitiveMap header) throws Exception {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			condition.setQueryString("delete TBPMS_INS_NOTE where PKVALUE = 1");
			dam.exeUpdate(condition);
			StringBuffer buf = new StringBuffer();
			buf.append(inputVO.getNOTE().toString());

			String note = inputVO.getNOTE().replace("'", "''");

			QueryConditionIF condition1 = dam
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition1.setQueryString("INSERT INTO " + "TBPMS_INS_NOTE "
					+ "(PKVALUE," + "REMARK," + "VERSION," + "CREATETIME,"
					+ "CREATOR," + "MODIFIER," + "LASTUPDATE) " + "VALUES (1,"
					+ "'" + note + "'" + ",1,SYSDATE,'','',SYSDATE)");
			dam.exeUpdate(condition1);
			Object x = new Object();
			x = true;
			sendRtnObject(x);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/****
	 * @param body
	 * @param header
	 * @throws Exception
	 *             主查詢
	 */
	public void query(Object body, IPrimitiveMap header) throws Exception {
		// 輸入VO
		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
//			XmlInfo xmlInfo = new XmlInfo();
//			String jobId = (String) xmlInfo.getVariable("PMS.PMS306_SCHEDULE", "JOB_ID", "F3");
//			String scheduleId = (String) xmlInfo.getVariable("PMS.PMS306_SCHEDULE", "SCHEDULE_ID", "F3");
//			
//			if(StringUtils.isBlank(jobId)) jobId = "BTPMS303";
//			if(StringUtils.isBlank(scheduleId)) scheduleId = "BTPMS303";
			
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT A.BT_STATE AS SCH_ID,  A.SEQ, A.INS_PROFIT_S, A.INS_PROFIT_E, A.BT_EXEC, B.ENDTIME as BT_FINISH, NVL(B.STATUS, 1) as BT_STATE, B.RESULT ");
			sql.append(" FROM TBPMS_INS_BT_SCHEDULE A ");
			sql.append(" LEFT OUTER JOIN TBSYSSCHDADMASTER B ON B.SCHEDULEID IN ('BTPMS303','BTPMS304_303') ");
			sql.append(" 	AND B.AUDITID = (SELECT MIN(AUDITID) FROM TBSYSSCHDADMASTER where CREATETIME >= A.BT_EXEC AND  SCHEDULEID IN ('BTPMS303','BTPMS304_303'))  AND TYPE = '2' ");
			sql.append(" WHERE 1=1 ");

			if (inputVO.getSDate() != null) {
				sql.append(" and A.BT_EXEC >= :SDATE");
			}
			if (inputVO.getEDate() != null) {
				sql.append(" and A.BT_FINISH <= :EDATE");
			}
			sql.append(" order by A.BT_EXEC desc");

			condition.setQueryString(sql.toString());

//			condition.setObject("jobId", jobId);
//			condition.setObject("scheduleId", scheduleId);
			
			if (inputVO.getSDate() != null) {
				condition.setObject("SDATE", inputVO.getSDate());
			}
			if (inputVO.getEDate() != null) {
				
				Calendar c = Calendar.getInstance(); 
				c.setTime(inputVO.getEDate());
			    int day=c.get(Calendar.DATE);
			    c.set(Calendar.DATE, day+1);
			    
			    condition.setObject("EDATE", c.getTime());
			   
//				if(inputVO.getSDate().equals(inputVO.getEDate()))
//				 {
//					Calendar c = Calendar.getInstance(); 
//					c.setTime(inputVO.getEDate());
//				    int day=c.get(Calendar.DATE);
//				      c.set(Calendar.DATE, day+1);
//				   condition.setObject("EDATE", c.getTime());
//				   System.err.println(c.getTime());
//				 }else{
//				condition.setObject("EDATE", inputVO.getEDate());
//				     }
				}

			ResultIF suptSalesTeamLst = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = suptSalesTeamLst.getTotalPage(); // 分頁用
			int totalRecord_i = suptSalesTeamLst.getTotalRecord(); // 分頁用
			outputVO.setPislist(suptSalesTeamLst); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/****
	 * @param body
	 * @param header
	 * @throws Exception
	 *             月目標上查詢
	 */
	public void mothtar(Object body, IPrimitiveMap header) throws Exception {

		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT * ");
			sql.append("   FROM TBPMS_INS_TARGET_SET	 ");

			sql.append("WHERE 1=1 ");

			if (!StringUtils.isBlank(inputVO.getYEARS())) {
				sql.append(" and YEARMON LIKE :YEARMONNN");

			}
			sql.append(" ORDER BY YEARMON,REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
			condition.setQueryString(sql.toString());

			if (!StringUtils.isBlank(inputVO.getYEARS())) {
				condition.setObject("YEARMONNN", inputVO.getYEARS());
			}

			/***** 分頁 *****/
			ResultIF mothlist = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> csvList = dam.exeQuery(condition); //匯出
			int totalPage_i = mothlist.getTotalPage(); // 分頁用
			int totalRecord_i = mothlist.getTotalRecord(); // 分頁用
			outputVO.setMothlist(mothlist); // data
			outputVO.setCsvlist(csvList);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/****
	 * @param body
	 * @param header
	 * @throws Exception
	 *             月目標上傳匯出
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException{
		PMS306OutputVO outputVO = (PMS306OutputVO) body;
		
		List<Map<String, Object>> list = outputVO.getCsvlist();
		try{
			if(list.size()>0){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "月目標上傳" + sdf.format(new Date())+"-"
						+getUserVariable(FubonSystemVariableConsts.LOGINID)+ ".csv";
				List listCSV = new ArrayList();
				for(Map<String, Object> map : list){
					String[] records = new String[14];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");
					records[++i] = checkIsNull(map,"REGION_CENTER_NAME");
					records[++i] = checkIsNull(map,"BRANCH_AREA_NAME");
					records[++i] = checkIsNull(map,"BRANCH_NBR");
					records[++i] = checkIsNull(map,"BRANCH_NAME");
					records[++i] = checkIsNull(map,"BRANCH_CLS");
					records[++i] = checkIsNull2(map,"OT_TAR_AMT");
					records[++i] = checkIsNull2(map,"OT_TAR_FEE");
					records[++i] = checkIsNull2(map,"SY_TAR_AMT");
					records[++i] = checkIsNull2(map,"SY_TAR_FEE");
					records[++i] = checkIsNull2(map,"LY_TAR_AMT");
					records[++i] = checkIsNull2(map,"LY_TAR_FEE");
					records[++i] = checkIsNull2(map,"IV_TAR_AMT");
					records[++i] = checkIsNull2(map,"IV_TAR_FEE");
					
					listCSV.add(records);
				}
				
				// header
				String[] csvHeader = new String[14];
				int j = 0;
				csvHeader[j] = "目標年月";
				csvHeader[++j] = "業務處";
				csvHeader[++j] = "營運區";
				csvHeader[++j] = "分行代號";
				csvHeader[++j] = "營業單位";
				csvHeader[++j] = "組別";
				csvHeader[++j] = "躉繳保費目標";
				csvHeader[++j] = "躉繳手收目標";
				csvHeader[++j] = "短年繳保費目標";
				csvHeader[++j] = "短年繳手收目標";
				csvHeader[++j] = "長年繳保費目標";
				csvHeader[++j] = "長年繳手收目標";
				csvHeader[++j] = "投資型保費目標";
				csvHeader[++j] = "投資型手收目標";
				
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				notifyClientToDownloadFile(url, fileName);// download
				
			}else{
				
				this.sendRtnObject(null);
			}
			
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
	}

	/****
	 * @param body
	 * @param header
	 * @throws Exception
	 *             人壽時間點維護查詢
	 */
	public void queryperson(Object body, IPrimitiveMap header) throws Exception {

		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT * ");
			sql.append("   FROM TBPMS_INS_ACCEPT_TIME	 ");
			sql.append("WHERE 1=1 ");

			if (!StringUtils.isBlank(inputVO.getYEARS())) {
				sql.append(" and YEARMON LIKE :YEARR");

			}

			condition.setQueryString(sql.toString());

			if (!StringUtils.isBlank(inputVO.getYEARS())) {
				condition.setObject("YEARR", inputVO.getYEARS());
			}
			List<Map<String, Object>> list = dam.exeQuery(condition);
			outputVO.setPersonlist(list); // data

			/***** 分頁 *****/
			// ResultIF mothlist = dam.executePaging(condition,
			// inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			// int totalPage_i = mothlist.getTotalPage(); // 分頁用
			// int totalRecord_i = mothlist.getTotalRecord(); // 分頁用
			// outputVO.setPersonlist(mothlist); // data
			// outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());//
			// 當前頁次
			// outputVO.setTotalPage(totalPage_i);// 總頁次
			// outputVO.setTotalRecord(totalRecord_i);// 總筆數
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/****
	 * @param body
	 * @param header
	 * @throws Exception
	 *             人工調整作業
	 */
	public void querype(Object body, IPrimitiveMap header) throws Exception {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select T.*,M.PRD_RATE,I.SEL_RATE from TBPMS_INS_TXN T ");
			sql.append(" LEFT JOIN  TBPRD_INS_MAIN M ");
			sql.append(" ON M.INSPRD_ID = T.PRD_ID ");
			sql.append(" AND M.INSPRD_ANNUAL = T.PRD_ANNUAL ");
			sql.append(" AND M.SPECIAL_CONDITION = T.SPECIAL_CONDITION ");
			sql.append(" LEFT JOIN TBPMS_IQ053 I ON I.CUR_COD = T.CURR_CD ");
			sql.append("  	AND I.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053 WHERE CUR_COD = T.CURR_CD) ");
			
			if (!StringUtils.isBlank(inputVO.getINSFNO())) {
				sql.append(" where T.INS_ID = :INSFNOo");
				condition.setObject("INSFNOo", inputVO.getINSFNO().toString());
			}
			// 排序
			sql.append("   order by T.INS_ID,T.LASTUPDATE,T.TX_TYPE,T.TX_DATE  ");
			condition.setQueryString(sql.toString());
//			if (!StringUtils.isBlank(inputVO.getINSFNO())) {
//				condition.setObject("INSFNOo", "%"
//						+ inputVO.getINSFNO().toString() + "%");
//			}

//			List<Map<String, Object>> mothlist = dam.exeQuery(condition);
//			outputVO.setPersonlist(mothlist); // data
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			outputVO.setTotalList(dam.exeQuery(condition));
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setPersonlist(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/***
	 * 取得招攬人員相關資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getRecruitInfo(Object body, IPrimitiveMap header) throws Exception {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" SELECT A.*, B.AO_CODE, C.DEPT_NAME FROM TBPMS_EMPLOYEE_REC_N A ");
			sql.append("	LEFT OUTER JOIN TBPMS_SALES_AOCODE_REC B ON ");
			sql.append(" 		B.EMP_ID = :empid AND B.TYPE = '1' ");
			sql.append("		AND TO_DATE(:keyinDate, 'YYYY-MM-DD HH24:MI:SS') BETWEEN TRUNC(B.START_TIME) AND TRUNC(B.END_TIME) ");
			sql.append("	LEFT OUTER JOIN TBORG_DEFN C ON C.DEPT_ID = A.DEPT_ID ");
			sql.append(" WHERE A.DEPT_ID >= '200' AND A.DEPT_ID <= '900' ");
			sql.append(" AND TO_NUMBER(A.DEPT_ID) <> 806 AND TO_NUMBER(A.DEPT_ID) <> 810");
			sql.append(" 	AND A.EMP_ID = :empid ");
			sql.append("  	AND TO_DATE(:keyinDate, 'YYYY-MM-DD HH24:MI:SS') BETWEEN TRUNC(A.START_TIME) AND TRUNC(A.END_TIME) ");
			
			condition.setObject("empid", inputVO.getRecruitId());
			condition.setObject("keyinDate", inputVO.getKeyinDate());
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(condition);
			
			outputVO.setRecruitList(list);
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員" + StringUtil.getStackTraceAsString(e));
		}
	}
	
	/*****************************
	 * @param body
	 * @param header
	 * @throws Exception
	 *             上傳檔案到資料庫
	 *******************************/
	public void uploadmoth(Object body, IPrimitiveMap header) throws Exception {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		dam = this.getDataAccessManager();
		try {

			Path path = Paths.get(new File((String) SysInfo
					.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO
					.getFILE_NAME()).toString());

			/******** 刪除主檔 ********/
			List<String> lines = FileUtils.readLines(new File(path.toString()),
					"big5");
			String[] checkhead=new String[14];
			int k=0;
			checkhead[k]="目標年月"; 
			checkhead[++k]="業務處";
			checkhead[++k]="營運區";
			checkhead[++k]="分行代號";
			checkhead[++k]="營業單位";
			checkhead[++k]="組別";
			checkhead[++k]="躉繳保費目標";
			checkhead[++k]="躉繳手收目標";
			checkhead[++k]="短年繳保費目標";
			checkhead[++k]="短年繳手收目標";
			checkhead[++k]="長年繳保費目標";
			checkhead[++k]="長年繳手收目標";
			checkhead[++k]="投資型保費目標";
			checkhead[++k]="投資型手收目標";
			boolean ch=true;
			PMS306OutputVO output=new PMS306OutputVO();
			for (int i = 0; i < lines.size(); i++) {
				String[] str = lines.get(i).split(",");
				if (i == 0) { // 第一行為表頭
					/******** 防呆處理 ********/
					if(str.length<14)
                     {
                    	 
 						output.setState("lenfail");
 						sendRtnObject(output);
 					   return;
                     }
				 ch=Arrays.equals(str,checkhead);
				 if(ch==false) 
					{
						
						output.setState("linefail");
						sendRtnObject(output);
					   return;
					}

				} else { // 其餘行為資料內容
					//先刪除匯入年月資料
					if( i == 1) {
						QueryConditionIF condition2 = dam
								.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						condition2.setQueryString("delete TBPMS_INS_TARGET_SET where YEARMON = "+str[0]);
						dam.exeUpdate(condition2);
					}
					// 固定欄位資料
					if (i >= 1) {
						if (inputVO.getState().toString().equals("0")) {
							inputVO.setYEARMON(str[0]);
							inputVO.setBRANCH_NBR(str[3]);
							
						}
					}
					output.setState("");
				
					/******** 已通過 ********/
			        TBPMS_INS_TARGET_SETPK PK = new TBPMS_INS_TARGET_SETPK();
					TBPMS_INS_TARGET_SETVO DTL = new TBPMS_INS_TARGET_SETVO();

					dam = this.getDataAccessManager();
					QueryConditionIF condition = dam
							.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

					StringBuffer sql = new StringBuffer();
					sql.append("select * from TBPMS_ORG_REC_N where 1=1 ");
					if (!StringUtils.isBlank(str[1])) {
						sql.append(" and BRANCH_NBR=:BRANCH_NBRR");
						condition.setObject("BRANCH_NBRR", str[3]);
					}
					condition.setQueryString(sql.toString());
					List<Map<String, Object>> list = dam.exeQuery(condition);

					//問題單號0002072
					if(list.size() == 0){
						PK.setBRANCH_NBR(str[3]);
						PK.setYEARMON(str[0]);
						DTL.setREGION_CENTER_NAME(str[1].toString());

						DTL.setBRANCH_AREA_NAME(str[2].toString());
						DTL.setBRANCH_AREA_ID("");
						DTL.setBRANCH_NAME(str[4].toString());

						DTL.setBRANCH_CLS(str[5].toString());
						DTL.setOT_TAR_AMT(new BigDecimal(str[6] + ""));
						DTL.setOT_TAR_FEE(new BigDecimal(str[7] + ""));
						DTL.setSY_TAR_AMT(new BigDecimal(str[8] + ""));
						DTL.setSY_TAR_FEE(new BigDecimal(str[9] + ""));
						DTL.setLY_TAR_AMT(new BigDecimal(str[10] + ""));
						DTL.setLY_TAR_FEE(new BigDecimal(str[11] + ""));
						DTL.setIV_TAR_AMT(new BigDecimal(str[12] + ""));
						DTL.setIV_TAR_FEE(new BigDecimal(str[13] + ""));
						DTL.setcomp_id(PK);
						DTL.setCreatetime(currentTM);
						long ver = 1;
						DTL.setVersion(ver);
						DTL.setLastupdate(currentTM);
					}else{
						PK.setBRANCH_NBR(str[3]);
						PK.setYEARMON(str[0]);
						DTL.setREGION_CENTER_NAME(list.get(0)
								.get("REGION_CENTER_NAME").toString());

						DTL.setBRANCH_AREA_NAME(list.get(0).get("BRANCH_AREA_NAME")
								.toString());
						DTL.setBRANCH_AREA_ID(list.get(0).get("BRANCH_AREA_ID")
								.toString());
						DTL.setBRANCH_NAME(list.get(0).get("BRANCH_NAME")
								.toString());

						DTL.setBRANCH_CLS(str[5].toString());
						DTL.setOT_TAR_AMT(new BigDecimal(str[6] + ""));
						DTL.setOT_TAR_FEE(new BigDecimal(str[7] + ""));
						DTL.setSY_TAR_AMT(new BigDecimal(str[8] + ""));
						DTL.setSY_TAR_FEE(new BigDecimal(str[9] + ""));
						DTL.setLY_TAR_AMT(new BigDecimal(str[10] + ""));
						DTL.setLY_TAR_FEE(new BigDecimal(str[11] + ""));
						DTL.setIV_TAR_AMT(new BigDecimal(str[12] + ""));
						DTL.setIV_TAR_FEE(new BigDecimal(str[13] + ""));
						DTL.setcomp_id(PK);
						DTL.setCreatetime(currentTM);
						long ver = 1;
						DTL.setVersion(ver);
						DTL.setLastupdate(currentTM);
					}
					
					dam.create(DTL);
				}
			}
			
			sendRtnObject(output);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	// savePerson
	/*****************************
	 * @param body
	 * @param header
	 * @throws Exception
	 *             上傳檔案到資料庫
	 *******************************/
	public void savePerson(Object body, IPrimitiveMap header) throws Exception {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS306InputVO inputVO = (PMS306InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		try {

			for (Map<String, Object> map : inputVO.getList()) { // 資料修改後
				String YEARMON = map.get("YEARMON") == null ? "" : map.get(
						"YEARMON").toString();
				for (Map<String, Object> map2 : inputVO.getList2()) { // 資料修改前

					String INS_PROFIT_S = map.get("INS_PROFIT_S") == null ? ""
							: map.get("INS_PROFIT_S").toString();
					String INS_PROFIT_E = map.get("INS_PROFIT_E") == null ? ""
							: map.get("INS_PROFIT_E").toString();
					String ACCEPT_TIME = map.get("ACCEPT_TIME") == null ? ""
							: map.get("ACCEPT_TIME").toString();
					String YEARS = map.get("YEARS") == null ? "" : map.get(
							"YEARS").toString();

					String YEARMON2 = map2.get("YEARMON") == null ? "" : map2
							.get("YEARMON").toString();
					String INS_PROFIT_S2 = map2.get("INS_PROFIT_S") == null ? ""
							: map2.get("INS_PROFIT_S").toString();
					String INS_PROFIT_E2 = map2.get("INS_PROFIT_E") == null ? ""
							: map2.get("INS_PROFIT_E").toString();
					String ACCEPT_TIME2 = map2.get("ACCEPT_TIME") == null ? ""
							: map2.get("ACCEPT_TIME").toString();
					String YEARS2 = map2.get("YEARS") == null ? "" : map2.get(
							"YEARS").toString();

					if (YEARMON.equals(YEARMON2)
							&& (!INS_PROFIT_S.equals(INS_PROFIT_S2)
									|| !INS_PROFIT_E.equals(INS_PROFIT_E2) || !ACCEPT_TIME
										.equals(ACCEPT_TIME2))) {

						// update vo資料到資料表
						TBPMS_INS_ACCEPT_TIMEVO paramVO = (TBPMS_INS_ACCEPT_TIMEVO) dam
								.findByPKey(TBPMS_INS_ACCEPT_TIMEVO.TABLE_UID,
										YEARMON2);
						if (paramVO != null) {
							if (!YEARMON.equals(""))
								paramVO.setYEARMON(YEARMON2);

							if (!INS_PROFIT_S.equals("")) {
								Timestamp INS_PROFIT_ST = Timestamp
										.valueOf(INS_PROFIT_S);
								paramVO.setINS_PROFIT_S(INS_PROFIT_ST);

							}
							if (!INS_PROFIT_E.equals(""))
								paramVO.setINS_PROFIT_E(Timestamp
										.valueOf(INS_PROFIT_E));
							if (!ACCEPT_TIME.equals(""))
								paramVO.setACCEPT_TIME(Timestamp
										.valueOf(ACCEPT_TIME));
							paramVO.setLastupdate(currentTM);

							dam.update(paramVO);
						}
					}
				}
			}
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	// savePe
	/*****************************
	 * @param body
	 * @param header
	 * @throws Exception
	 *             上傳檔案到資料庫人工調整
	 *******************************/
	public void savePe(Object body, IPrimitiveMap header) throws Exception {
		dam = this.getDataAccessManager();
		PMS306InputVO inputVO = (PMS306InputVO) body;
		String userID = getUserVariable(FubonSystemVariableConsts.LOGINID).toString();
		
		try {
			Map<String, Object> voge = (Map<String, Object>) inputVO.getListper();
			
			String INSFNO = voge.get("INSFNO") == null ? "" : voge.get("INSFNO").toString(); //保險文件編號
			String  PRD_ID = voge.get("PRD_ID") == null ? "" : voge.get("PRD_ID").toString();	//險種代碼
			String MOP2 = voge.get("MOP2") == null ? "" : voge.get("MOP2").toString();		//繳別
			BigDecimal REAL_PREMIUM = (voge.get("REAL_PREMIUM") == null || StringUtils.isEmpty(voge.get("REAL_PREMIUM").toString())) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble( voge.get("REAL_PREMIUM").toString()));	//實收保費
			String CURR_CD = voge.get("CURR_CD") == null ? "" : voge.get("CURR_CD").toString();	//幣別
			String RECRUIT_ID = voge.get("RECRUIT_ID") == null ? "" : voge.get("RECRUIT_ID").toString(); //招攬人員員編
			String RECRUIT_IDNBR = voge.get("RECRUIT_IDNBR") == null ? "" : voge.get("RECRUIT_IDNBR").toString(); //招攬人員ID
			String RECRUIT_NAME = voge.get("RECRUIT_NAME") == null ? "" : voge.get("RECRUIT_NAME").toString(); //招攬人員姓名
			String BRANCH_NBR = voge.get("BRANCH_NBR") == null ? "" : voge.get("BRANCH_NBR").toString(); //招攬人員分行
			String BRANCH_NAME = voge.get("BRANCH_NAME") == null ? "" : voge.get("BRANCH_NAME").toString(); //招攬人員分行名稱
			String AO_CODE = voge.get("AO_CODE") == null ? "" : voge.get("AO_CODE").toString(); //招攬人員AO CODE
			
			if("B".equals(voge.get("TX_TYPE").toString())){				
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sql=new StringBuilder();
				
				sql.append(" select * from TBPMS_INS_TXN");
				sql.append(" WHERE INS_ID = :INSFNOo ");
				 
				condition.setObject( "INSFNOo", INSFNO );
				condition.setQueryString(sql.toString());
				
				List<Map<String,Object>> list=dam.exeQuery(condition);
				
				//PRD_ID有改時，取得修改後的商品名稱
				String PRD_NAME = "";
				String PAY_TYPE = "";
				String INSPRD_TYPE = "";
				BigDecimal PRD_RATE = BigDecimal.ZERO;
				BigDecimal CNR_RATE = BigDecimal.ZERO;
				QueryConditionIF condition4 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sql4 = new StringBuilder();
				sql4.append(" select INSPRD_NAME, INSPRD_TYPE, PAY_TYPE, PRD_RATE, CNR_RATE from TBPRD_INS_MAIN ");
				sql4.append(" WHERE INSPRD_ID = :prdId ");
				condition4.setObject( "prdId", PRD_ID );
				condition4.setQueryString(sql4.toString());
				
				List<Map<String,Object>> list4=dam.exeQuery(condition4);
				if(CollectionUtils.isNotEmpty(list4)) {
					PRD_NAME = list4.get(0).get("INSPRD_NAME").toString();
					INSPRD_TYPE = list4.get(0).get("INSPRD_TYPE").toString();
					PAY_TYPE = list4.get(0).get("PAY_TYPE").toString();
					PRD_RATE =  (BigDecimal) list4.get(0).get("PRD_RATE");
					CNR_RATE =  (BigDecimal) list4.get(0).get("CNR_RATE");
				}
				
				String TX_DATE = "";
				QueryConditionIF condition5 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sql5 = new StringBuilder();
				sql5.append(" select max(TX_DATE) AS TX_DATE from TBPMS_INS_TXN ");
				condition5.setQueryString(sql5.toString());
				List<Map<String,Object>> list5 = dam.exeQuery(condition5);
				if(CollectionUtils.isNotEmpty(list5)) {
					TX_DATE = list5.get(0).get("TX_DATE").toString();
				}
				
				//重新計算ANNU_PREMIUM, ANNU_ACT_FEE, ACT_FEE, CNR_FEE
				BigDecimal ANNU_PREMIUM = BigDecimal.ZERO;
				BigDecimal ANNU_ACT_FEE = BigDecimal.ZERO;
				BigDecimal ACT_FEE = BigDecimal.ZERO;
				BigDecimal CNR_FEE = BigDecimal.ZERO;
				
				QueryConditionIF condition1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);				
				StringBuilder sql1=new StringBuilder();
				sql1.append("SELECT ");
				sql1.append("FN_ANNU_PREM(:CURR_CD, :INSPRD_TYPE, :PRD_NAME, :MOP2, :REAL_PREMIUM , FN_INS_GET_Middle_RATE(V.INS_KEYNO)) AS ANNU_PREMIUM, ");
				sql1.append("FN_ANNU_FEE(:CURR_CD, :INSPRD_TYPE, :PRD_NAME, :MOP2, :REAL_PREMIUM * :PRD_RATE * FN_INS_GET_Middle_RATE(V.INS_KEYNO)) AS ANNU_ACT_FEE, ");
				sql1.append("ROUND(( :REAL_PREMIUM * :PRD_RATE * FN_INS_GET_Middle_RATE(V.INS_KEYNO)),4) AS ACT_FEE, ");
				sql1.append("ROUND(( :REAL_PREMIUM * :CNR_RATE * FN_INS_GET_Middle_RATE(V.INS_KEYNO)),4) AS CNR_FEE ");
				sql1.append("FROM VWIOT_MAIN V WHERE V.INS_ID = :INSFNOo ");
				
				condition1.setObject( "INSFNOo", INSFNO );
				condition1.setObject( "CURR_CD", CURR_CD );
				condition1.setObject( "PRD_NAME", PRD_NAME );
				condition1.setObject( "MOP2", MOP2 );
				condition1.setObject( "REAL_PREMIUM", REAL_PREMIUM );
				condition1.setObject( "INSPRD_TYPE", INSPRD_TYPE );
				condition1.setObject( "PRD_RATE", PRD_RATE );
				condition1.setObject( "CNR_RATE", CNR_RATE );
				condition1.setQueryString(sql1.toString());
				
				List<Map<String,Object>> list1=dam.exeQuery(condition1);
				if(CollectionUtils.isNotEmpty(list1)) {
					ANNU_PREMIUM = (BigDecimal) list1.get(0).get("ANNU_PREMIUM");
					ANNU_ACT_FEE = (BigDecimal) list1.get(0).get("ANNU_ACT_FEE");
					ACT_FEE = (BigDecimal) list1.get(0).get("ACT_FEE");
					CNR_FEE = (BigDecimal) list1.get(0).get("CNR_FEE");
				}
				
				QueryConditionIF condition2 = dam.getQueryCondition();
				StringBuilder sql2=new StringBuilder();
				
				sql2.append("INSERT INTO TBPMS_INS_TXN");
				sql2.append("(ACT_FEE,AFT_SIGN_DATE,ANNU_ACT_FEE,ANNU_PREMIUM,AO_CODE,APPLY_DATE,BASE_PREMIUM,BRANCH_NAME,BRANCH_NBR,");
				sql2.append("CNR_FEE,CREATETIME,CREATOR,CURR_CD,CUST_ID,EXCH_RATE,INSURED_ID,INSURED_NAME,INS_ID,INS_RCV_DATE,INS_RCV_OPRID,KEYIN_DATE,");
				sql2.append("LASTUPDATE,MATCH_DATE,MODIFIER,MOP2,OP_BATCH_NO,PAY_TYPE,PRD_ANNUAL,PRD_ID,PRD_NAME,PRD_TYPE,PROPOSER_NAME,QC_ANC_DOC,");
				sql2.append("REAL_PREMIUM,RECRUIT_ID,RECRUIT_IDNBR,RECRUIT_NAME,REF_CON_ID,REF_EMP_ID,REF_EMP_NAME,REG_TYPE,REMARK_BANK,SIGN_DATE,");
			    sql2.append("SPECIAL_CONDITION,STATUS,TX_CNT,TX_DATE,TX_TYPE,VERSION,WRITE_REASON,WRITE_REASON_OTH)");
			    sql2.append("VALUES(");
			    
			    Map<String,Object> map=list.get(0);

			    sql2.append("'"+ACT_FEE);
			    sql2.append("',"); 
			    if(map.get("AFT_SIGN_DATE")!=null){
			    	sql2.append("to_date(substr('"+checkIsNull(map,"AFT_SIGN_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql2.append("'"+checkIsNull(map,"AFT_SIGN_DATE"));
				    sql2.append("',");
			    }
			    sql2.append("'"+ANNU_ACT_FEE);
			    sql2.append("',");
			    sql2.append("'"+ANNU_PREMIUM);
			    sql2.append("',");
			    sql2.append("'"+AO_CODE);
			    sql2.append("',");
			    if(map.get("APPLY_DATE")!=null){
			    	sql2.append("to_date(substr('"+checkIsNull(map,"APPLY_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql2.append("'"+checkIsNull(map,"APPLY_DATE"));
				    sql2.append("',");
			    }
			    sql2.append("'"+checkIsNull(map,"BASE_PREMIUM"));
			    sql2.append("',");
			    sql2.append("'"+BRANCH_NAME);
			    sql2.append("',");
			    sql2.append("'"+BRANCH_NBR);
			    sql2.append("',");
			    sql2.append("'"+CNR_FEE);
			    sql2.append("',");
			    if(map.get("CREATETIME")!=null){
			    	sql2.append("to_date(substr('"+checkIsNull(map,"CREATETIME")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql2.append("'"+checkIsNull(map,"CREATETIME"));
				    sql2.append("',");
			    }
			    sql2.append("'"+checkIsNull(map,"CREATOR"));
			    sql2.append("',");
			    sql2.append("'"+CURR_CD);
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"CUST_ID"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"EXCH_RATE"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"INSURED_ID"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"INSURED_NAME"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"INS_ID"));
			    sql2.append("',");
			    if(map.get("INS_RCV_DATE")!=null){
			    	sql2.append("to_date(substr('"+checkIsNull(map,"INS_RCV_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql2.append("'"+checkIsNull(map,"INS_RCV_DATE"));
				    sql2.append("',");
			    }
			    sql2.append("'"+checkIsNull(map,"INS_RCV_OPRID"));
			    sql2.append("',");
			    if(map.get("KEYIN_DATE")!=null){
			    	sql2.append("to_date(substr('"+checkIsNull(map,"KEYIN_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql2.append("'"+checkIsNull(map,"KEYIN_DATE"));
			    	sql2.append("',");
			    }
			    sql2.append("SYSDATE");
			    sql2.append(",");
			    if(map.get("MATCH_DATE")!=null){
			    	sql2.append("to_date(substr('"+checkIsNull(map,"MATCH_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql2.append("'"+checkIsNull(map,"MATCH_DATE"));
			    	sql2.append("',");
			    }
			    sql2.append("'"+userID);
			    sql2.append("',");
			    sql2.append("'"+MOP2);
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"OP_BATCH_NO"));
			    sql2.append("',");
			    sql2.append("'"+PAY_TYPE);
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"PRD_ANNUAL"));
			    sql2.append("',");
			    sql2.append("'"+PRD_ID);
			    sql2.append("',");
			    sql2.append("'"+PRD_NAME);
			    sql2.append("',");
			    sql2.append("'"+INSPRD_TYPE);
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"PROPOSER_NAME"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"QC_ANC_DOC"));
			    sql2.append("',");
			    sql2.append("'"+REAL_PREMIUM);
			    sql2.append("',");
			    sql2.append("'"+RECRUIT_ID);
			    sql2.append("',");
			    sql2.append("'"+RECRUIT_IDNBR);
			    sql2.append("',");
			    sql2.append("'"+RECRUIT_NAME);
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"REF_CON_ID"));
			    sql2.append("',"); 
			    sql2.append("'"+checkIsNull(map,"REF_EMP_ID"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"REF_EMP_NAME"));
			    sql2.append("',"); 
			    sql2.append("'"+checkIsNull(map,"REG_TYPE"));
			    sql2.append("',"); 
			    sql2.append("'"+checkIsNull(map,"REMARK_BANK"));
			    sql2.append("',");
			    if(map.get("SIGN_DATE")!=null){
			    	sql2.append("to_date(substr('"+checkIsNull(map,"SIGN_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql2.append("'"+checkIsNull(map,"SIGN_DATE"));
			    	sql2.append("',");
			    }
			    sql2.append("'"+checkIsNull(map,"SPECIAL_CONDITION"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"STATUS"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"TX_CNT"));
			    sql2.append("',");
			    sql2.append("'"+TX_DATE);
			    sql2.append("',");
			    sql2.append("'M");
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"VERSION"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"WRITE_REASON"));
			    sql2.append("',");
			    sql2.append("'"+checkIsNull(map,"WRITE_REASON_OTH"));
			    sql2.append("')");
			    condition2.setQueryString(sql2.toString());
			    dam.exeUpdate(condition2);
			    
			    QueryConditionIF condition3 = dam.getQueryCondition();
			    
				StringBuilder sql3=new StringBuilder();
				
				sql3.append("INSERT INTO TBPMS_INS_TXN");
				sql3.append("(ACT_FEE,AFT_SIGN_DATE,ANNU_ACT_FEE,ANNU_PREMIUM,AO_CODE,APPLY_DATE,BASE_PREMIUM,BRANCH_NAME,BRANCH_NBR,");
				sql3.append("CNR_FEE,CREATETIME,CREATOR,CURR_CD,CUST_ID,EXCH_RATE,INSURED_ID,INSURED_NAME,INS_ID,INS_RCV_DATE,INS_RCV_OPRID,KEYIN_DATE,");
				sql3.append("LASTUPDATE,MATCH_DATE,MODIFIER,MOP2,OP_BATCH_NO,PAY_TYPE,PRD_ANNUAL,PRD_ID,PRD_NAME,PRD_TYPE,PROPOSER_NAME,QC_ANC_DOC,");
				sql3.append("REAL_PREMIUM,RECRUIT_ID,RECRUIT_IDNBR,RECRUIT_NAME,REF_CON_ID,REF_EMP_ID,REF_EMP_NAME,REG_TYPE,REMARK_BANK,SIGN_DATE,");
			    sql3.append("SPECIAL_CONDITION,STATUS,TX_CNT,TX_DATE,TX_TYPE,VERSION,WRITE_REASON,WRITE_REASON_OTH)");
			    sql3.append("VALUES(");
			    
			    Map<String,Object> map1=list.get(0);

			    sql3.append("'-"+checkIsNull(map1,"ACT_FEE"));
			    sql3.append("',"); 
			    if(map.get("AFT_SIGN_DATE")!=null){
			    	sql3.append("to_date(substr('"+checkIsNull(map1,"AFT_SIGN_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql3.append("'"+checkIsNull(map1,"AFT_SIGN_DATE"));
				    sql3.append("',");
			    }
			    sql3.append("'-"+checkIsNull(map1,"ANNU_ACT_FEE"));
			    sql3.append("',");
			    sql3.append("'-"+checkIsNull(map1,"ANNU_PREMIUM"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"AO_CODE"));
			    sql3.append("',");
			    if(map1.get("APPLY_DATE")!=null){
			    	sql3.append("to_date(substr('"+checkIsNull(map1,"APPLY_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql3.append("'"+checkIsNull(map1,"APPLY_DATE"));
				    sql3.append("',");
			    }
			    sql3.append("'"+checkIsNull(map1,"BASE_PREMIUM"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"BRANCH_NAME"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"BRANCH_NBR"));
			    sql3.append("',");
			    sql3.append("'-"+checkIsNull(map1,"CNR_FEE"));
			    sql3.append("',");
			    if(map1.get("CREATETIME")!=null){
			    	sql3.append("to_date(substr('"+checkIsNull(map1,"CREATETIME")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql3.append("'"+checkIsNull(map1,"CREATETIME"));
				    sql3.append("',");
			    }
			    sql3.append("'"+checkIsNull(map1,"CREATOR"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"CURR_CD"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"CUST_ID"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"EXCH_RATE"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"INSURED_ID"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"INSURED_NAME"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"INS_ID"));
			    sql3.append("',");
			    if(map1.get("INS_RCV_DATE")!=null){
			    	sql3.append("to_date(substr('"+checkIsNull(map1,"INS_RCV_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql3.append("'"+checkIsNull(map1,"INS_RCV_DATE"));
			    	sql3.append("',");
			    }
			    sql3.append("'"+checkIsNull(map1,"INS_RCV_OPRID"));
			    sql3.append("',");
			    if(map1.get("KEYIN_DATE")!=null){
			    	sql3.append("to_date(substr('"+checkIsNull(map1,"KEYIN_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql3.append(checkIsNull(map1,"KEYIN_DATE"));
			    	sql3.append(",");
			    }
			    sql3.append("SYSDATE");
			    sql3.append(",");
			    if(map1.get("MATCH_DATE")!=null){
			    	sql3.append("to_date(substr('"+checkIsNull(map1,"MATCH_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql3.append("'"+checkIsNull(map1,"MATCH_DATE"));
			    	sql3.append("',");
			    }
			    sql3.append("'"+userID);
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"MOP2"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"OP_BATCH_NO"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"PAY_TYPE"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"PRD_ANNUAL"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"PRD_ID"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"PRD_NAME"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"PRD_TYPE"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"PROPOSER_NAME"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"QC_ANC_DOC"));
			    sql3.append("',");
			    sql3.append("'-"+checkIsNull(map1,"REAL_PREMIUM"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"RECRUIT_ID"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"RECRUIT_IDNBR"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"RECRUIT_NAME"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"REF_CON_ID"));
			    sql3.append("',"); 
			    sql3.append("'"+checkIsNull(map1,"REF_EMP_ID"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"REF_EMP_NAME"));
			    sql3.append("',"); 
			    sql3.append("'"+checkIsNull(map1,"REG_TYPE"));
			    sql3.append("',"); 
			    sql3.append("'"+checkIsNull(map1,"REMARK_BANK"));
			    sql3.append("',");
			    if(map1.get("SIGN_DATE")!=null){
			    	sql3.append("to_date(substr('"+checkIsNull(map1,"SIGN_DATE")+"',1,19),'yyyy-mm-dd hh24:mi:ss'),");
			    }else{
			    	sql3.append("'"+checkIsNull(map1,"SIGN_DATE"));
			    	sql3.append("',");
			    }
			    sql3.append("'"+checkIsNull(map1,"SPECIAL_CONDITION"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"STATUS"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"TX_CNT"));
			    sql3.append("',");
			    sql3.append("'"+TX_DATE);
			    sql3.append("',");
			    sql3.append("'A");
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"VERSION"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"WRITE_REASON"));
			    sql3.append("',");
			    sql3.append("'"+checkIsNull(map1,"WRITE_REASON_OTH"));
			    sql3.append("')");
			    condition3.setQueryString(sql3.toString());
			    dam.exeUpdate(condition3);
			    
			}else{
					
			TBPMS_INS_TXNPK vodepk=new TBPMS_INS_TXNPK();
			TBPMS_INS_TXNVO vode = (TBPMS_INS_TXNVO) dam.findByPKey(
					TBPMS_INS_TXNVO.TABLE_UID, vodepk);
			
			
			if (vodepk!=null&&vode != null) {
				
				  vode.setPRD_ID(PRD_ID);
				  vode.setREAL_PREMIUM(REAL_PREMIUM);
				  vode.setCURR_CD(CURR_CD);

			    }
			     dam.update(vode);
			}
			
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** ====== 檢查有沒有EXCEL======= */
	/**/
	public void checkSql(Object body, IPrimitiveMap header) throws JBranchException {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO OutputVO = new PMS306OutputVO();
		dam = this.getDataAccessManager();
		
		try {
			Path path = Paths.get(new File((String) SysInfo
				.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO
				.getFILE_NAME()).toString());

			List<String> lines = FileUtils.readLines(new File(path.toString()),
				"big5");
			String errMsg = "";
			String errNBR="";
			
			for (int i = 1; i < lines.size(); i++) {
			
				String[] str = lines.get(i).split(",");
				inputVO.setYEARMON(str[0]);
				inputVO.setBRANCH_NBR(str[3]);
			
				QueryConditionIF condition = dam
						.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
				condition.setQueryString("SELECT * from " + "TBPMS_INS_TARGET_SET "
						+ "where YEARMON = :YEARMONN "
						+ "AND BRANCH_NBR = :BRANCH_NBR ");

				condition.setObject("YEARMONN", inputVO.getYEARMON());
				condition.setObject("BRANCH_NBR", inputVO.getBRANCH_NBR());
			
				List<Map<String, Object>> list = dam.exeQuery(condition);
			
				if (list.isEmpty()) {
					OutputVO.setState("1");
				} else {
					OutputVO.setState("0");
				}
				//問題單號0002072
				
				QueryConditionIF condition2 = dam
						.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
				condition2.setQueryString("SELECT * from " + "TBPMS_ORG_REC_N "
						+ "where 1 = 1 "
						+ "AND BRANCH_NBR = :BRANCH_NBR ");

				condition2.setObject("BRANCH_NBR", inputVO.getBRANCH_NBR());
			
				List<Map<String, Object>> list2 = dam.exeQuery(condition2);
				
				
				
				if (list2.isEmpty()) {
					errNBR += inputVO.getBRANCH_NBR() + "、";
				} else {
					OutputVO.setNBR_state("0");
				}
			}
			
			//統整錯誤訊息
			if(!StringUtils.equals(errNBR, "")){
				errMsg ="分行代碼"+errNBR.substring(0,errNBR.length()-1)+"不存在!";
				OutputVO.setBRANCH_NBR(errMsg);
				sendRtnObject(OutputVO);
				return;
			}
			
			this.sendRtnObject(OutputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/** ====== 刪除資料庫主檔資料 ======= */
	/**/
	public void delData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			condition.setQueryString("delete TBPMS_INS_TARGET_SET "
					+ "where YEARMON = :YEARMONN");
			condition.setObject("YEARMONN", inputVO.getYEARMON());
			dam.exeUpdate(condition);
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/** ====== 刪除資料庫人工調整作業資料 ======= */
	public void delPeData(Object body, IPrimitiveMap header) throws JBranchException {
		PMS306InputVO inputVO = (PMS306InputVO) body;
		try {
			
			Map<String, Object> voge = (Map<String, Object>) inputVO.getListper();
			
			String INSFNO = voge.get("INSFNO").toString();

			dam = this.getDataAccessManager();
			
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
			condition.setQueryString(" delete TBPMS_INS_TXN "
					+ " where INS_ID LIKE :INSFNOo AND (TX_TYPE='A' OR TX_TYPE='M') ");
			
			condition.setObject("INSFNOo", "%"+ INSFNO + "%");
			
			dam.exeUpdate(condition);
			
			this.sendRtnObject(null);
			
		} catch (Exception e) {
			
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			
			throw new APException("系統發生錯誤請洽系統管理員");
			
		}
	}
	

	public void reset(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS306OutputVO return_VO = new PMS306OutputVO();
		String userID = getUserVariable(FubonSystemVariableConsts.LOGINID).toString();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try {
			XmlInfo xmlInfo = new XmlInfo();
			String scheduleId = (String) xmlInfo.getVariable("PMS.PMS306_SCHEDULE", "SCHEDULE_ID", "F3");
			if(StringUtils.isBlank(scheduleId)) scheduleId = "BTPMS303";
			
			// 判斷傳入的scheduleid在數據庫是否已經存在 畫面二傳入的scheduleid可能不存在
			TbsysschdVO result_Vo = (TbsysschdVO) dam.findByPKey(TbsysschdVO.TABLE_UID, scheduleId );
			if (result_Vo != null) {
				/*
				 * ScheduleManagement scheduleManagement = new ScheduleManagement();
				 * scheduleManagement.run(cMMGR004QueryInputVO.getHlbscheduleid());
				 */
				result_Vo.setIstriggered("Y");
				dam.update(result_Vo);
			} 
       
			//計算期間為上月26日至當月25日，但需為營業日；由TBPMS_INS_ACCEPT_TIME取得
			StringBuffer sql2 = new StringBuffer(); 
			sql2.append("select TO_CHAR(INS_PROFIT_S, 'YYYYMMDD') as INS_PROFIT_S, TO_CHAR(INS_PROFIT_E, 'YYYYMMDD') as INS_PROFIT_E from TBPMS_INS_ACCEPT_TIME ");
			sql2.append(" where TRUNC(PABTH_UTIL.FC_getBusiDay(TRUNC(SYSDATE),'TWD',-1)) BETWEEN INS_PROFIT_S AND INS_PROFIT_E ");
			
			QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			queryCondition2.setQueryString(sql2.toString());
            List<Map<String,Object>> list = dam.exeQuery(queryCondition2);
            
            String beforedate,afterdate;
            if(CollectionUtils.isNotEmpty(list)) {
            	beforedate = list.get(0).get("INS_PROFIT_S").toString();
            	afterdate = list.get(0).get("INS_PROFIT_E").toString();
            } else {
            	//若TABLE無資料        
                Calendar sg=Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
                         
                int year=sg.get(Calendar.YEAR);
                int month=sg.get(Calendar.MONTH);
                int day=sg.get(Calendar.DAY_OF_MONTH);
                int year2,month1,month2;
                String beforemonth,aftermonth;
              
               if((month==1&&day<25)||(month==12&&day>24)) {
            	   year2=year-1;
                   month1=12;
                   month2=1;
               } else {
            	   year2=year;  
                   if(day<25) {
                	   month1=month;    
                       month2=month+1; 
                   } else {
                	   month1=month-1;    
                       month2=month; 
                   }
               }
               
               beforemonth=(month1<10)?"0"+String.valueOf(month1):String.valueOf(month1); 
    	       aftermonth=(month2<10)?"0"+String.valueOf(month2):String.valueOf(month2); 
    	       beforedate=String.valueOf(year)+beforemonth+"26";
    	       afterdate=String.valueOf(year2)+aftermonth+"25"; 
            }
            
            //寫入TABLE
            String sql3 = "INSERT INTO TBPMS_INS_BT_SCHEDULE"
            			+ " (SEQ,INS_PROFIT_S,INS_PROFIT_E,BT_EXEC,BT_FINISH,BT_STATE,VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE) "
            			+ "  VALUES(TBPMS_INS_BT_SCHEDULE_SEQ.NEXTVAL,'"+beforedate+"','"+afterdate+"',SYSDATE,SYSDATE,'"+ scheduleId + "','1',SYSDATE"+",'"+userID+"','"+userID+"',SYSDATE)";
            QueryConditionIF queryCondition3 = dam.getQueryCondition();
            queryCondition3.setQueryString(sql3);
            dam.exeUpdate(queryCondition3);
	       
            this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	public void resetBase(Object body, IPrimitiveMap header) throws JBranchException {
		PMS306OutputVO return_VO = new PMS306OutputVO();
		String userID = getUserVariable(FubonSystemVariableConsts.LOGINID).toString();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try {
			String scheduleId = "BTPMS304_303";	
			// 判斷傳入的scheduleid在數據庫是否已經存在 畫面二傳入的scheduleid可能不存在
			TbsysschdVO result_Vo = (TbsysschdVO) dam.findByPKey(TbsysschdVO.TABLE_UID, scheduleId );
			if (result_Vo != null) {
				/*
				 * ScheduleManagement scheduleManagement = new ScheduleManagement();
				 * scheduleManagement.run(cMMGR004QueryInputVO.getHlbscheduleid());
				 */
				result_Vo.setIstriggered("Y");
				dam.update(result_Vo);
			} 

			//計算期間為上月26日至當月25日，但需為營業日；由TBPMS_INS_ACCEPT_TIME取得
			StringBuffer sql2 = new StringBuffer(); 
			sql2.append("select TO_CHAR(INS_PROFIT_S, 'YYYYMMDD') as INS_PROFIT_S, TO_CHAR(INS_PROFIT_E, 'YYYYMMDD') as INS_PROFIT_E from TBPMS_INS_ACCEPT_TIME ");
			sql2.append(" where TRUNC(PABTH_UTIL.FC_getBusiDay(TRUNC(SYSDATE),'TWD',-1)) BETWEEN INS_PROFIT_S AND INS_PROFIT_E ");
			
			QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			queryCondition2.setQueryString(sql2.toString());
            List<Map<String,Object>> list = dam.exeQuery(queryCondition2);
            
            String beforedate,afterdate;
            if(CollectionUtils.isNotEmpty(list)) {
            	beforedate = list.get(0).get("INS_PROFIT_S").toString();
            	afterdate = list.get(0).get("INS_PROFIT_E").toString();
            } else {
            	//若TABLE無資料        
                Calendar sg=Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
                         
                int year=sg.get(Calendar.YEAR);
                int month=sg.get(Calendar.MONTH);
                int day=sg.get(Calendar.DAY_OF_MONTH);
                int year2,month1,month2;
                String beforemonth,aftermonth;
              
               if((month==1&&day<25)||(month==12&&day>24)) {
            	   year2=year-1;
                   month1=12;
                   month2=1;
               } else {
            	   year2=year;  
                   if(day<25) {
                	   month1=month;    
                       month2=month+1; 
                   } else {
                	   month1=month-1;    
                       month2=month; 
                   }
               }
               
               beforemonth=(month1<10)?"0"+String.valueOf(month1):String.valueOf(month1); 
    	       aftermonth=(month2<10)?"0"+String.valueOf(month2):String.valueOf(month2); 
    	       beforedate=String.valueOf(year)+beforemonth+"26";
    	       afterdate=String.valueOf(year2)+aftermonth+"25"; 
            }
            
            //寫入TABLE
            String sql3 = "INSERT INTO TBPMS_INS_BT_SCHEDULE"
            			+ " (SEQ,INS_PROFIT_S,INS_PROFIT_E,BT_EXEC,BT_FINISH,BT_STATE,VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE) "
            			+ "  VALUES(TBPMS_INS_BT_SCHEDULE_SEQ.NEXTVAL,'"+beforedate+"','" + afterdate + "',SYSDATE,SYSDATE,'"+ scheduleId + "','1',SYSDATE"+",'" +userID+ "','" +userID+ "',SYSDATE)";
            QueryConditionIF queryCondition3 = dam.getQueryCondition();
            queryCondition3.setQueryString(sql3);
            dam.exeUpdate(queryCondition3);
	       
            this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	/****
	 * @param body
	 * @param header
	 * @throws Exception
	 * 保險設定-折數 查詢
	 */
	public void queryDiscount(Object body, IPrimitiveMap header) throws Exception {

		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" SELECT * ");
			sql.append(" FROM TBSYSPARAMETER ");
			sql.append(" WHERE PARAM_TYPE = 'PMS.INS_DISCOUNT' ");
			sql.append(" ORDER BY PARAM_CODE ");
			
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			outputVO.setDiscountlist(list); // data

			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	/****
	 * @param body
	 * @param header
	 * @throws Exception
	 * 保險設定-折數 儲存
	 */
	public void saveDiscount(Object body, IPrimitiveMap header) throws Exception{
		dam = this.getDataAccessManager();
		PMS306InputVO inputVO = (PMS306InputVO) body;
		PMS306OutputVO outputVO = new PMS306OutputVO();
		
		try{
			
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sql=new StringBuilder();
			
			sql.append(" SELECT * ");
			sql.append(" FROM TBSYSPARAMETER ");
			sql.append(" WHERE PARAM_TYPE = 'PMS.INS_DISCOUNT' ");
			sql.append(" ORDER BY PARAM_CODE ");
			 
			condition.setQueryString(sql.toString());
			List<Map<String,Object>> list=dam.exeQuery(condition);
			
			for(Map<String,Object> map:list){
				TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
				pk.setPARAM_CODE(map.get("PARAM_CODE").toString());
				pk.setPARAM_TYPE(map.get("PARAM_TYPE").toString());
				TBSYSPARAMETERVO vo = (TBSYSPARAMETERVO)dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				Timestamp time = new  Timestamp(sdf.parse(map.get("CREATETIME").toString()).getTime()); 
	 			vo.setCreatetime(time); 
	 			vo.setCreator(map.get("CREATOR").toString()); 			 
	 			Timestamp now = new Timestamp(System.currentTimeMillis());
	 			vo.setLastupdate(now); 
	 			vo.setModifier(map.get("MODIFIER").toString());
	 			vo.setPARAM_DESC(map.get("PARAM_DESC").toString()); 
	 			vo.setPARAM_NAME(map.get("PARAM_NAME").toString());
	 			if(map.get("PARAM_CODE").toString().equals("1")){
	 				vo.setPARAM_NAME_EDIT(inputVO.getDISCOUNTSALE());
	 			}else{
	 				vo.setPARAM_NAME_EDIT(inputVO.getDISCOUNT());
	 			}
	 			vo.setPARAM_STATUS(map.get("PARAM_STATUS").toString());
	 			vo.setVersion(Long.parseLong(map.get("VERSION").toString()));
	 			
	 			SimpleDateFormat sdfA = new SimpleDateFormat("yyyyMMdd");
	 			String PARAM_ORDER = sdfA.format(inputVO.getUpDate());
	 			vo.setPARAM_ORDER(Integer.valueOf(PARAM_ORDER));
	 			
	 			vo.setcomp_id(pk);
	 			dam.update(vo);
			}
			
			sendRtnObject(outputVO);
			
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))  && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	private String checkIsNull2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))  && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}
	
}

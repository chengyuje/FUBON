package com.systex.jbranch.app.server.fps.crm3102;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobclassVO;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm3102")
@Scope("request")
public class CRM3102  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(CRM3102.class);
	private List<Map<String, Object>> scsslist = null ; 
	
	/**下拉式選單:所有專案**/
	public void getAllPRJ(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3102InputVO inputVO = (CRM3102InputVO) body;
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, PRJ_STATUS, PRJ_TYPE FROM TBCRM_TRS_PRJ_MAST ");
		sql.append(" WHERE 1 = 1 "); 
		
//		if(StringUtils.isNotBlank(inputVO.getComboflag())) {
//			sql.append(" AND TRUNC(sysdate) BETWEEN TRUNC(PRJ_DATE_BGN) and TRUNC(PRJ_DATE_END) ");
//		}
		
		if(StringUtils.isNotBlank(inputVO.getPRJ_TYPE())) {
			sql.append(" AND PRJ_TYPE = :prjType ");
			queryCondition.setObject("prjType", inputVO.getPRJ_TYPE());
		} else {
			sql.append(" AND PRJ_TYPE IS NOT NULL "); //輪調換手專案
		}

		sql.append("ORDER BY PRJ_ID DESC ");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setAllPRJ(list);
		sendRtnObject(return_VO);
	}
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3102InputVO inputVO = (CRM3102InputVO) body;
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT M.PRJ_ID, M.PRJ_NAME, M.PRJ_NOTE, M.PRJ_DATE_BGN, M.PRJ_DATE_END, E.IMP_EMP_ID, D.EMP_NAME, M.PRJ_STATUS, ");
		sql.append(" CASE WHEN M.PRJ_TYPE = '2' THEN (CASE WHEN M.PRJ_SUB_TYPE = '2' THEN '加強管控專案(高端理專)' ELSE '加強管控專案(一般理專)' END) ELSE '輪調專案' END AS PRJ_TYPE_STR, ");
		sql.append(" M.PRJ_TYPE, M.PRJ_SUB_TYPE, COUNT(E.EMP_ID) as TOTAL_CNT, ");
		sql.append(" SUM( CASE WHEN E.IMP_SUCCESS_YN = 'Y' THEN 1 ELSE 0 END ) as IMP_S_CNT, ");
		sql.append(" MIN(E.IMP_DATETIME) AS IMP_DATETIME, ");
		sql.append(" S1.STATUS AS S1_SCH_STATUS, S2.STATUS AS S2_SCH_STATUS, S1.RESULT AS S1_SCH_RESULT, S2.RESULT AS S2_SCH_RESULT ");
		sql.append(" FROM TBCRM_TRS_PRJ_MAST M ");
		sql.append(" LEFT JOIN TBCRM_TRS_PRJ_ROTATION_M E ON M.PRJ_ID = E.PRJ_ID ");
		sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D ON E.IMP_EMP_ID=D.EMP_ID ");
		sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D1 ON E.MODIFIER = D1.EMP_ID ");
		sql.append(" LEFT JOIN TBSYSSCHDADMASTER S1 ON S1.SCHEDULEID = ('BTCRM3102_ADDMUSTCUST_' || M.PRJ_ID) ");
		sql.append(" LEFT JOIN TBSYSSCHDADMASTER S2 ON S2.SCHEDULEID = ('BTCRM3102_GET5YCUST_' || M.PRJ_ID) ");
		sql.append(" WHERE M.PRJ_TYPE IS NOT NULL "); //輪調/換手專案
		// where
		if(StringUtils.isNotBlank(inputVO.getPRJ_ID())){
			sql.append(" AND M.PRJ_ID = :prj_id ");
			queryCondition.setObject("prj_id", inputVO.getPRJ_ID());
		}
		if(StringUtils.isNotBlank(inputVO.getPRJ_STATUS())){
			sql.append(" AND M.PRJ_STATUS = :status ");
			queryCondition.setObject("status", inputVO.getPRJ_STATUS());
		}
		if(inputVO.getSdate()!=null){
			sql.append(" AND E.IMP_DATETIME >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getSdate());
		}
		if(inputVO.getEdate()!=null){
			sql.append(" AND E.IMP_DATETIME < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEdate());
		}
		if(StringUtils.isNotBlank(inputVO.getPRJ_TYPE())) {
			sql.append(" AND PRJ_TYPE = :prjType ");
			queryCondition.setObject("prjType", inputVO.getPRJ_TYPE());
		} else {
			sql.append(" AND PRJ_TYPE IS NOT NULL "); //輪調換手專案
		}
		sql.append(" GROUP BY M.PRJ_ID, M.PRJ_NAME, M.PRJ_NOTE, M.PRJ_DATE_BGN, M.PRJ_DATE_END, E.IMP_EMP_ID, D.EMP_NAME, TRUNC(E.IMP_DATETIME), M.PRJ_STATUS, M.PRJ_TYPE, M.PRJ_SUB_TYPE, S1.STATUS, S1.RESULT, S2.STATUS, S2.RESULT ");
		//依照專案建立日排序 20170619 add
		sql.append(" ORDER BY TRUNC(E.IMP_DATETIME) DESC, M.PRJ_ID ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);	
			
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}	
			
	public void getPrjData(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3102InputVO inputVO = (CRM3102InputVO) body;
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, PRJ_STATUS, PRJ_NOTE, PRJ_DATE_BGN, PRJ_DATE_END, PRJ_EXE_DATE, PRJ_TYPE, PRJ_SUB_TYPE, STATEMENT_DATE ");
		sql.append(" FROM TBCRM_TRS_PRJ_MAST ");
		sql.append(" WHERE PRJ_ID = :prjId "); 		
		sql.append(" ORDER BY PRJ_ID DESC ");
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setPRJList(list);
		sendRtnObject(return_VO);
	}
	
	public void doFunction(Object body, IPrimitiveMap header) throws Exception {
		CRM3102InputVO inputVO = (CRM3102InputVO) body;
		
		if("2".equals(inputVO.getActtype())) {
			getNoFailLst(inputVO); //查看成功名單
		} else if("3".equals(inputVO.getActtype())) {
			getFailLst(inputVO); //查看移轉失敗名單
		} else if("4".equals(inputVO.getActtype())) {
			delPRJ(inputVO); //刪除專案
		} else if("5".equals(inputVO.getActtype())) { 
			//取得換手理專轄下經營滿5年客戶名單
			getEmpCust5YR(inputVO);
		} else if("6".equals(inputVO.getActtype())) { 
			//刪除一筆換手理專轄下經營滿5年客戶名單
			deleteEmpCust5YR(inputVO);
		} else if("7".equals(inputVO.getActtype())) { 
			//檢核新增換手理專轄下經營滿5年客戶名單
			chkEmpCust5YR(inputVO);
		} else if("8".equals(inputVO.getActtype())) { 
			//新增一筆換手理專轄下經營滿5年客戶名單
			addEmpCust5YR(inputVO);
		} else if("9".equals(inputVO.getActtype())) { 
			//換手；匯出客戶名單
			exportEmpCust5YR(inputVO);
		}
	}
	
	//查看成功名單
	public void getNoFailLst(CRM3102InputVO body) throws JBranchException {
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.BRANCH_NBR, A.EMP_ID, B.EMP_NAME, C.DEPT_NAME AS BRANCH_NAME, M.PRJ_TYPE, A.PRJ_ID ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_M A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST M ON M.PRJ_ID = A.PRJ_ID ");
		sql.append(" LEFT JOIN TBORG_MEMBER B ON B.EMP_ID = A.EMP_ID ");
		sql.append(" LEFT JOIN TBORG_DEFN C ON C.DEPT_ID = A.BRANCH_NBR ");
		sql.append(" WHERE A.IMP_SUCCESS_YN = 'Y' AND A.PRJ_ID = :PRJ ");
		sql.append(" ORDER BY A.BRANCH_NBR, A.EMP_ID ");
		queryCondition.setObject("PRJ", body.getPRJ_ID());
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setAOScssLst(list);
		sendRtnObject(return_VO);
	}
	
	//查看移轉失敗名單
	public void getFailLst(CRM3102InputVO body) throws JBranchException {
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.BRANCH_NBR, A.EMP_ID, B.EMP_NAME, C.DEPT_NAME AS BRANCH_NAME, A.IMP_STATUS ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_M A ");
		sql.append(" LEFT JOIN TBORG_MEMBER B ON B.EMP_ID = A.EMP_ID ");
		sql.append(" LEFT JOIN TBORG_DEFN C ON C.DEPT_ID = A.BRANCH_NBR ");
		sql.append(" WHERE A.IMP_SUCCESS_YN = 'N' AND A.PRJ_ID = :PRJ ");
		sql.append(" ORDER BY A.BRANCH_NBR, A.EMP_ID ");
		queryCondition.setObject("PRJ", body.getPRJ_ID());
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setAOFailLst(list);
		sendRtnObject(return_VO);
	}
	
	//刪除專案
	private void delPRJ(CRM3102InputVO body) throws JBranchException {
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();

		//TBCRM_TRS_PRJ_MAST
		TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
		vo = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, body.getPRJ_ID());
		if(vo != null) {
			dam.delete(vo);
		}
		
		//TBCRM_TRS_PRJ_ROTATION_M
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBCRM_TRS_PRJ_ROTATION_M where PRJ_ID = :prj_id ");
		condition.setObject("prj_id", body.getPRJ_ID());
		dam.exeUpdate(condition);
		
		//TBCRM_TRS_PRJ_ROTATION_D
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBCRM_TRS_PRJ_ROTATION_D where PRJ_ID = :prj_id ");
		condition.setObject("prj_id", body.getPRJ_ID());
		dam.exeUpdate(condition);
		
		//TBPMS_ROTATION_5YCUST
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBPMS_ROTATION_5YCUST where PRJ_ID = :prj_id ");
		condition.setObject("prj_id", body.getPRJ_ID());
		dam.exeUpdate(condition);
				
		//TBPMS_ROTATION_MAIN
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBPMS_ROTATION_MAIN where PRJ_ID = :prj_id ");
		condition.setObject("prj_id", body.getPRJ_ID());
		dam.exeUpdate(condition);
		
		this.sendRtnObject(return_VO);
	}

	//取得換手理專轄下經營滿5年客戶名單
	public void getEmpCust5YR(CRM3102InputVO inputVO) throws JBranchException {
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.CUST_ID, M.CUST_NAME, A.CON_DEGREE_YN, A.PRFT_LAST_YEAR_NOTE, A.EMP_ID, D.EMP_NAME, ");
		sql.append(" (CASE WHEN EXISTS (SELECT 1 FROM TBPMS_ROTATION_MAIN WHERE PRJ_ID = A.PRJ_ID) THEN 'N' ELSE 'Y' END) AS EDIT_YN "); //若已匯入帳務報表則不可再新增/刪除
		sql.append(" FROM TBPMS_ROTATION_5YCUST A ");
		sql.append(" INNER JOIN TBCRM_CUST_MAST M ON M.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN TBORG_MEMBER D ON D.EMP_ID = A.EMP_ID ");
		sql.append(" WHERE A.PRJ_ID = :prjId AND A.EMP_ID = :empId ");
		sql.append(" ORDER BY A.CUST_ID ");
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		queryCondition.setObject("empId", inputVO.getEMP_ID());
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		sendRtnObject(return_VO);
	}
	
	//刪除換手理專轄下經營滿5年客戶名單
	public void deleteEmpCust5YR(CRM3102InputVO inputVO) throws JBranchException {
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("delete TBPMS_ROTATION_5YCUST where PRJ_ID = :prjId AND EMP_ID = :empId AND CUST_ID = :custId ");
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		queryCondition.setObject("empId", inputVO.getEMP_ID());
		queryCondition.setObject("custId", inputVO.getCUST_ID());
		dam.exeUpdate(queryCondition);
		
		sendRtnObject(return_VO);
	}
	
	//檢核新增的換手理專轄下經營滿5年客戶名單
	public void chkEmpCust5YR(CRM3102InputVO inputVO) throws JBranchException {
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT 1 FROM TBPMS_ROTATION_5YCUST WHERE PRJ_ID = :prjId AND CUST_ID = :custId ");
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		queryCondition.setObject("custId", inputVO.getCUST_ID());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list)) {
			throw new APException("此客戶已在名單中");
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :custId ");
		
		queryCondition.setObject("custId", inputVO.getCUST_ID());
		List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
		if(CollectionUtils.isEmpty(list1)) {
//			throw new APException("此客戶非理專" + inputVO.getEMP_ID() + "的轄下客戶");
			return_VO.setCUST_NAME("");
		} else {
			return_VO.setCUST_NAME(ObjectUtils.toString(list1.get(0).get("CUST_NAME")));
		}	
		
		sendRtnObject(return_VO);
	}
	
	//新增一筆換手理專轄下經營滿5年客戶名單
	public void addEmpCust5YR(CRM3102InputVO inputVO) throws JBranchException {
		chkEmpCust5YR(inputVO);
		
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
				
		sb.append("INSERT INTO TBPMS_ROTATION_5YCUST ( KEY_NO, PRJ_ID, CUST_ID, BRANCH_NBR, AO_CODE, EMP_ID, STATEMENT_DATE, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
		sb.append(" SELECT TBPMS_ROTATION_5YCUST_SEQ.nextval, ");
		sb.append(" 	   A.PRJ_ID, ");
		sb.append(" 	   :custId, ");
		sb.append(" 	   :branchNbr, ");
		sb.append(" 	   '', ");
		sb.append(" 	   :empId, ");
		sb.append(" 	   TO_CHAR(A.STATEMENT_DATE, 'YYYYMMDD') AS STATEMENT_DATE, ");
		sb.append(" 	   1 AS VERSION, ");
		sb.append(" 	   :loginId AS CREATOR, ");
		sb.append(" 	   SYSDATE AS CREATETIME, ");
		sb.append(" 	   :loginId AS MODIFIER,  ");
		sb.append(" 	   SYSDATE AS LASTUPDATE ");
		sb.append(" FROM TBCRM_TRS_PRJ_MAST A ");
		sb.append(" WHERE A.PRJ_ID = :prjId  ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("loginId", ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
		queryCondition.setObject("branchNbr", inputVO.getBRANCH_NBR());
		queryCondition.setObject("empId", inputVO.getEMP_ID());
		queryCondition.setObject("custId", inputVO.getCUST_ID());
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		
		dam.exeUpdate(queryCondition);
		
		sendRtnObject(return_VO);
	}
		
	//上傳名單匯入
	public void editPrj(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3102InputVO inputVO = (CRM3102InputVO) body;
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		Boolean Check1 = false;
		List<String> res1 = new ArrayList<String>();
		
		//檢核加強管控專案中是否有一樣的對帳單基準日
		if(StringUtils.equals("2", inputVO.getPRJ_TYPE()) && isSameStatementDate(inputVO.getPRJ_ID(), inputVO.getPRJ_SUB_TYPE(), inputVO.getSTATEMENT_DATE())) {
			throw new APException("加強管控專案相同加強管控類型不可有相同的對帳單基準日");
		}
		
		//更新專案主檔
		dam.setAutoCommit(true);
		TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
		if(StringUtils.isNotBlank(inputVO.getPRJ_ID())) {			
			vo = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, inputVO.getPRJ_ID());
			vo.setPRJ_NAME(inputVO.getPRJ_NAME());
			vo.setPRJ_DATE_BGN(new Timestamp(inputVO.getSdate().getTime()));
			vo.setPRJ_DATE_END(new Timestamp(inputVO.getEdate().getTime()));
			vo.setPRJ_EXE_DATE(new Timestamp(inputVO.getPRJ_EXE_DATE().getTime()));
			vo.setSTATEMENT_DATE(StringUtils.equals("1", inputVO.getPRJ_TYPE()) ? null : new Timestamp(inputVO.getSTATEMENT_DATE().getTime()));
			vo.setPRJ_NOTE(inputVO.getPRJ_NOTE());
			vo.setPRJ_TYPE(inputVO.getPRJ_TYPE()); 		// 專案類型：1.必輪調專案 2.加強管控專案
			vo.setPRJ_SUB_TYPE(inputVO.getPRJ_SUB_TYPE()); //加強管控專案類型 1:一般理專 2:高端理專
			vo.setPRJ_STATUS("01");						
			/**
			 * TBCRM_TRS_PRJ_MAST.PRJ_STATUS：
			 * 01 資料處理中
			 * 02 已匯入
			 * 03 有失敗名單
			 * 04 發生錯誤
			 * 05 已回收作廢
			 * 06 已執行加入待移轉清單
			 * 07 已執行強制AO移轉
			 * "刪除專案"直接刪掉，不保留資料
			 * **/ 
			dam.update(vo);
		} else {
			BigDecimal seqno = new BigDecimal(getSEQ()); //	產生序號
			vo.setPRJ_ID(seqno.toString());
			vo.setPRJ_NAME(inputVO.getPRJ_NAME());
			vo.setPRJ_DATE_BGN(new Timestamp(inputVO.getSdate().getTime()));
			vo.setPRJ_DATE_END(new Timestamp(inputVO.getEdate().getTime()));
			vo.setPRJ_EXE_DATE(new Timestamp(inputVO.getPRJ_EXE_DATE().getTime()));
			vo.setSTATEMENT_DATE(StringUtils.equals("1", inputVO.getPRJ_TYPE()) ? null : new Timestamp(inputVO.getSTATEMENT_DATE().getTime()));
			vo.setPRJ_NOTE(inputVO.getPRJ_NOTE());
			vo.setPRJ_TYPE(inputVO.getPRJ_TYPE()); 		// 專案類型：1.必輪調專案 2.加強管控專案
			vo.setPRJ_SUB_TYPE(inputVO.getPRJ_SUB_TYPE()); //加強管控專案類型 1:一般理專 2:高端理專
			vo.setPRJ_STATUS("01");
			dam.create(vo);
		}
		dam.setAutoCommit(false);
		
		// fail count
		int fail_count = 0;
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = new ArrayList<String[]>();
		if(StringUtils.isNotBlank(inputVO.getFileName())) {
			dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		}
		
		if(!dataCsv.isEmpty()) {
			dam.setAutoCommit(true);
			
			//先將TBCRM_TRS_PRJ_ROTATION_M & TBCRM_TRS_PRJ_ROTATION_D資料刪除
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("delete TBCRM_TRS_PRJ_ROTATION_M where PRJ_ID = :prj_id ");
			queryCondition.setObject("prj_id", vo.getPRJ_ID());
			dam.exeUpdate(queryCondition);
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("delete TBCRM_TRS_PRJ_ROTATION_D where PRJ_ID = :prj_id ");
			queryCondition.setObject("prj_id", vo.getPRJ_ID());
			dam.exeUpdate(queryCondition);
			
			//理專資料匯入
			for(int i = 0; i < dataCsv.size(); i++) {
				if(i == 0) continue; //Header
				
				String[] str = dataCsv.get(i);
				if(StringUtils.isBlank(str[0]) || StringUtils.isBlank(str[1]))
					continue; //分行及員編為空白
				if(str.length < 2)
					throw new APException("檔案格式錯誤，請依據下載範例");
				
				String imp_status = "";
				//檢查分行員編資料
				if(StringUtils.equals("2", inputVO.getPRJ_SUB_TYPE())) {
					//加強管控專案類型 2:高端理專
					//理專已換分行或已離職
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT 1 FROM TBORG_MEMBER WHERE EMP_ID = :empId AND CHANGE_FLAG IN ('A', 'M', 'P') AND SERVICE_FLAG = 'A' ");
					queryCondition.setObject("empId", str[1].trim());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					if (CollectionUtils.isEmpty(list)) {
						imp_status = "01";
					}
					//非高端理專
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT 1 FROM VWORG_EMP_UHRM_INFO WHERE EMP_ID = :empId AND PRIVILEGEID = 'UHRM002' ");
					queryCondition.setObject("empId", str[1].trim());
					List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
					if (CollectionUtils.isEmpty(list1)) {
						imp_status = "02";
					}
				} else {
					//理專已換分行或已離職
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT 1 FROM TBORG_MEMBER WHERE EMP_ID = :empId AND DEPT_ID = :brhNbr AND CHANGE_FLAG IN ('A', 'M', 'P') AND SERVICE_FLAG = 'A' ");
					queryCondition.setObject("brhNbr", str[0].trim());
					queryCondition.setObject("empId", str[1].trim());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					if (CollectionUtils.isEmpty(list)) {
						imp_status = "01";
					}
				}
				
				// add
				TBCRM_TRS_PRJ_ROTATION_MVO rotMvo = new TBCRM_TRS_PRJ_ROTATION_MVO();
				TBCRM_TRS_PRJ_ROTATION_MPK rotMpk = new TBCRM_TRS_PRJ_ROTATION_MPK();
				rotMpk.setPRJ_ID(vo.getPRJ_ID());
				rotMpk.setBRANCH_NBR(str[0].trim());
				rotMpk.setEMP_ID(str[1].trim());
				
				XmlInfo xmlInfo = new XmlInfo();
				Map<String, String> rgnBranch = xmlInfo.doGetVariable("CRM.TRS_PRJ_ROT_REGION_BRH", FormatHelper.FORMAT_2); //區域分行
				
				rotMvo.setcomp_id(rotMpk);
				rotMvo.setREGION_BRANCH_YN(rgnBranch.containsKey(str[0].trim()) ? "Y" : "N");
				if(StringUtils.isBlank(imp_status))
					rotMvo.setIMP_SUCCESS_YN("Y");
				else {
					rotMvo.setIMP_SUCCESS_YN("N");
					rotMvo.setIMP_STATUS(imp_status);
					fail_count++;
				}
				rotMvo.setIMP_EMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
				rotMvo.setIMP_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				rotMvo.setSTEP_STATUS("1"); //第一階段_理專選取核心客戶
				try {
					dam.create(rotMvo);
				} catch(Exception e) {
					Check1 = true;
					res1.add(str[1].trim());
				}
			}
			dam.setAutoCommit(false);
			
			dam.setAutoCommit(true);
			
			// 專案類型：1.必輪調專案 2.加強管控專案
			if ("1".equals(inputVO.getPRJ_TYPE())) {
				//將必輪調理專轄下客戶資料寫入必輪調明細檔
				//名單上傳時仍在移轉中的客戶該移轉流程作廢
				//依專案代碼新增批次JOB以及排程SCHEDULE(之後可依專案代碼查詢排程狀態)
				addPrjType2Batch(vo.getPRJ_ID(), "ADDMUSTCUST");	
				//手動執行排程
				TbsysschdVO schVO = (TbsysschdVO) dam.findByPKey(TbsysschdVO.TABLE_UID, "BTCRM3102_ADDMUSTCUST_" + vo.getPRJ_ID());
				if (schVO != null) {
					schVO.setIstriggered("Y");
					dam.update(schVO);
				} 
			} else if ("2".equals(inputVO.getPRJ_TYPE())) {				
				//加強管控專案
				//依專案代碼新增批次JOB以及排程SCHEDULE(之後可依專案代碼查詢排程狀態)
				addPrjType2Batch(vo.getPRJ_ID(), "GET5YCUST");	
				//手動執行排程
				TbsysschdVO schVO = (TbsysschdVO) dam.findByPKey(TbsysschdVO.TABLE_UID, "BTCRM3102_GET5YCUST_" + vo.getPRJ_ID());
				if (schVO != null) {
					schVO.setIstriggered("Y");
					dam.update(schVO);
				} 
			}
			
			/**
			 * TBCRM_TRS_PRJ_MAST.PRJ_STATUS：
			 * 01 資料處理中
			 * 02 已匯入
			 * 03 有失敗名單
			 * 04 發生錯誤
			 * 05 已回收作廢
			 * 06 已執行加入待移轉清單
			 * 07 已執行強制AO移轉
			 * "刪除專案"直接刪掉，不保留資料
			 * **/ 
			vo.setPRJ_STATUS(fail_count > 0 ? "03" : "02");
			dam.update(vo);

			//將加強管控專案排程關掉
//			TbsysschdVO schVO = (TbsysschdVO) dam.findByPKey(TbsysschdVO.TABLE_UID, "BTCRM3102_GET5YCUST_" + vo.getPRJ_ID());
//			if (schVO != null) {
//				schVO.setIsuse("N");
//				schVO.setisscheduled("N");
//				dam.update(schVO);
//			}
			
			dam.setAutoCommit(false);
		}
		
		//加入TBCRM_TRS_PRJ_ROTATION_M有誤
		if(Check1) {
			return_VO.setResultList(res1);
			return_VO.setError("ERR1");
		}
			
		this.sendRtnObject(return_VO);
	}	
	
	/***
	 * 依專案代碼新增批次JOB以及排程SCHEDULE(之後可依專案代碼查詢排程狀態)
	 * @param prjID
	 * @throws JBranchException
	 */
	private void addPrjType2Batch(String prjID, String pckName) throws JBranchException {
		dam.setAutoCommit(true);
		
		String batchId = "BTCRM3102_" + pckName + "_" + prjID;
		String decription = StringUtils.equals("GET5YCUST", pckName) ? "加強管控理專轄下經營五年以上客戶及前一年貢獻度為E客戶" : "將輪調專案理專轄下客戶資料寫入輪調明細檔";
		//新增JOB
		TbsysschdjobVO jobVO = (TbsysschdjobVO) dam.findByPKey(TbsysschdjobVO.TABLE_UID, batchId);
		if(jobVO == null) {
			TbsysschdjobVO vo = new TbsysschdjobVO();
			vo.setjobid(batchId);
			vo.setjobname(batchId);
			vo.setdescription(decription);
			vo.setclassid(batchId);
			vo.setbeanid("bthmain2");
			vo.setparameters("method=execute;pckName=PABTH_BTCRM3102_" + pckName + ";pckArg="+prjID+";");
			dam.create(vo);
		}
		//新增JOBCLASS
		TbsysschdjobclassVO jobClassVO = (TbsysschdjobclassVO) dam.findByPKey(TbsysschdjobclassVO.TABLE_UID, batchId);
		if(jobClassVO == null) {
			TbsysschdjobclassVO vo = new TbsysschdjobclassVO();
			vo.setclassid(batchId);
			vo.setclassname(batchId);
			vo.setBeanname("bthmain2");
			dam.create(vo);
		}
		//新增Schedule
		XmlInfo xmlInfo = new XmlInfo();
		String processorId = (String) xmlInfo.getVariable("CRM.3102_" + pckName + "_PROCESSOR", "1", "F3");
		TbsysschdVO schVO = (TbsysschdVO) dam.findByPKey(TbsysschdVO.TABLE_UID, batchId);
		if(schVO == null) {
			TbsysschdVO vo = new TbsysschdVO();
			vo.setscheduleid(batchId);
			vo.setschedulename(batchId);
			vo.setdescription(decription);
			vo.setprocessor(processorId);
			vo.setcronexpression("0 50 4 1 12 ?");
			vo.setlasttry(new Timestamp(Calendar.getInstance().getTime().getTime()));
			vo.setJobstart(new BigDecimal(1));
			vo.setIsuse("Y");
			vo.setisscheduled("N");
			vo.setIstriggered("N");
			vo.setOnetime("Y"); //只執行一次
			dam.create(vo);
		} else {
			schVO.setIsuse("Y");
			schVO.setisscheduled("N");
			schVO.setIstriggered("N");
			schVO.setOnetime("Y"); //只執行一次
			dam.update(schVO);
		}
		//新增JOBACCESS
		TbsysschdjobassPK jobAccPK = new TbsysschdjobassPK();
		jobAccPK.setJobid(batchId);
		jobAccPK.setScheduleid(batchId);
		TbsysschdjobassVO jobAccVO = (TbsysschdjobassVO) dam.findByPKey(TbsysschdjobassVO.TABLE_UID, jobAccPK);
		if(jobAccVO == null) {
			TbsysschdjobassVO vo = new TbsysschdjobassVO();
			vo.setcomp_id(jobAccPK);
			vo.setJoborder(new BigDecimal(1));
			dam.create(vo);
		}
		
		dam.setAutoCommit(false);
	}
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_TRS_PRJ_MAST_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	//下載上傳名單範例
	public void downloadSample(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//CRM//CRM3102_EXAMPLE.csv", "輪調/加強管控專案名單上傳範例.csv");
		this.sendRtnObject(null);
	}
	
	/***
	 * 換手專案中是否有一樣的對帳單基準日
	 * 不同prjSubType可有相同對帳單基準日
	 * @param prjId
	 * @param prjSubType 高端理專/一般理專
	 * @param statementDate
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private boolean isSameStatementDate(String prjId, String prjSubType, Date statementDate) throws DAOException, JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 1 FROM TBCRM_TRS_PRJ_MAST WHERE TRUNC(STATEMENT_DATE) = TRUNC(:statementDate) AND PRJ_TYPE = '2' AND PRJ_SUB_TYPE = :prjSubType ");
		if(StringUtils.isNotBlank(prjId)) {
			sql.append(" AND PRJ_ID <> :prjId ");
			queryCondition.setObject("prjId", prjId);
		}
		queryCondition.setObject("prjSubType", prjSubType);
		queryCondition.setObject("statementDate", statementDate);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isNotEmpty(list) ? true : false;
	}
	
	//換手；匯出客戶名單
	public void exportEmpCust5YR(CRM3102InputVO inputVO) throws JBranchException {
		CRM3102OutputVO return_VO = new CRM3102OutputVO();
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		String fileName = inputVO.getPRJ_NAME() + "_客戶名單.csv";
		
		sql.append("SELECT A.CUST_ID, NVL(M.CUST_NAME, '') AS CUST_NAME, A.CON_DEGREE_YN, A.PRFT_LAST_YEAR_NOTE, ");
		sql.append(" A.BRANCH_NBR || '-' || NVL(C.DEPT_NAME, '') AS BRANCH, A.EMP_ID || '-' || NVL(B.EMP_NAME, '') AS EMP, "); 
		//CON_DEGREE_YN=Y:前一年貢獻度為E但非經營滿五年，所以不須顯示"經營滿五年理專"欄位
		sql.append(" CASE WHEN A.CON_DEGREE_YN = 'Y' THEN '' ELSE A.EMP_ID || '-' || NVL(B.EMP_NAME, '') END AS EMP5Y ");
		sql.append(" FROM TBPMS_ROTATION_5YCUST A ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST M ON M.CUST_ID = A.CUST_ID ");
		sql.append(" LEFT JOIN TBORG_MEMBER B ON B.EMP_ID = A.EMP_ID ");
		sql.append(" LEFT JOIN TBORG_DEFN C ON C.DEPT_ID = A.BRANCH_NBR ");
		sql.append(" WHERE A.PRJ_ID = :prjId ");
		sql.append(" ORDER BY A.EMP_ID, A.CUST_ID ");
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String[] csvHeader = { "理專所屬分行", "理專", "客戶ID", "客戶姓名", "經營滿五年理專", "前一年度貢獻度" };
		String[] csvMain = { "BRANCH", "EMP", "CUST_ID", "CUST_NAME", "EMP5Y", "PRFT_LAST_YEAR_NOTE" };
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		for (Map<String, Object> map : list) {
			String[] records = new String[csvHeader.length];
			for (int i = 0; i < csvHeader.length; i++) {
				records[i] = checkIsNull(map, csvMain[i]);
			}
			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);
		
		sendRtnObject(return_VO);
	}
	
	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}

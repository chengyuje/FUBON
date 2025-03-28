package com.systex.jbranch.app.server.fps.org410;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org410")
@Scope("request")
public class ORG410 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	public HashMap<String, String> changeTypeMap = new HashMap<String, String>(); 
	
	public void query(Object body, IPrimitiveMap header) throws Exception {
		
		ORG410InputVO inputVO = (ORG410InputVO) body;
		ORG410OutputVO outputVO = new ORG410OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT HIS.CHANGE_TYPE, PARAM.PARAM_NAME AS CHANGE_NAME, HIS.EMP_ID, MEMBER.EMP_NAME, ");
		sql.append("       HIS.HIS_BRANCH_AREA_ID, INFO.DEPT_NAME AS HIS_BRANCH_AREA_NAME, ");
		sql.append("       HIS.BRANCH_AREA_ID, INFO2.DEPT_NAME AS BRANCH_AREA_NAME, ");
		sql.append("       HIS.HIS_BRANCH_ID, INFO3.DEPT_NAME AS HIS_BRANCH_NAME, ");
		sql.append("       HIS.BRANCH_ID, INFO4.DEPT_NAME AS BRANCH_NAME, ");
		sql.append("       HIS.HIS_JOB_TITLE_NAME, HIS.JOB_TITLE_NAME, HIS.HIS_JOB_RANK, HIS.JOB_RANK, HIS.EFFDT ");
		sql.append("FROM TBORG_MEMBER_HIS HIS ");
		sql.append("LEFT JOIN TBORG_MEMBER MEMBER ON HIS.EMP_ID = MEMBER.EMP_ID ");
		sql.append("LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y' AND IS_AO = 'Y') HIS_JOB ON HIS_JOB.JOB_TITLE_NAME = HIS.HIS_JOB_TITLE_NAME ");
		sql.append("LEFT JOIN (SELECT ROLE_ID, ROLE_NAME, JOB_TITLE_NAME FROM TBORG_ROLE WHERE REVIEW_STATUS = 'Y' AND IS_AO = 'Y') NOW_JOB ON NOW_JOB.JOB_TITLE_NAME = HIS.JOB_TITLE_NAME ");
		sql.append("LEFT JOIN TBORG_DEFN INFO ON HIS.HIS_BRANCH_AREA_ID = INFO.DEPT_ID ");
		sql.append("LEFT JOIN TBORG_DEFN INFO2 ON HIS.BRANCH_AREA_ID = INFO2.DEPT_ID ");
		sql.append("LEFT JOIN TBORG_DEFN INFO3 ON HIS.HIS_BRANCH_ID = INFO3.DEPT_ID ");
		sql.append("LEFT JOIN TBORG_DEFN INFO4 ON HIS.BRANCH_ID = INFO4.DEPT_ID ");
		sql.append("LEFT JOIN (SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.CHANGE_TYPE') PARAM ON HIS.CHANGE_TYPE = PARAM.PARAM_CODE ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND MEMBER.SERVICE_FLAG = 'A' ");
		sql.append("AND MEMBER.CHANGE_FLAG IN ('A', 'M', 'P') ");

		if (StringUtils.isNotBlank(inputVO.getCHANGE_TYPE())){
			sql.append("AND HIS.CHANGE_TYPE = :CHANGE_TYPE ");
			condition.setObject("CHANGE_TYPE", inputVO.getCHANGE_TYPE());
		}		
		
		if (StringUtils.isNotBlank(inputVO.getEMP_ID())){
			sql.append("AND HIS.EMP_ID LIKE :EMP_ID "); 
			condition.setObject("EMP_ID", "%" + inputVO.getEMP_ID() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getEMP_NAME())){
			sql.append("AND MEMBER.EMP_NAME LIKE :EMP_NAME "); 
			condition.setObject("EMP_NAME", "%" + inputVO.getEMP_NAME() + "%");
		}
		
		//===
		if (StringUtils.isNotBlank(inputVO.getHIS_BRANCH_AREA_ID()) && StringUtils.isNotBlank(inputVO.getBRANCH_AREA_ID())) {
			sql.append("AND (HIS.HIS_BRANCH_AREA_ID = :HIS_BRANCH_AREA_ID OR HIS.BRANCH_AREA_ID = :BRANCH_AREA_ID) "); 
			condition.setObject("HIS_BRANCH_AREA_ID", inputVO.getHIS_BRANCH_AREA_ID());
			condition.setObject("BRANCH_AREA_ID", inputVO.getBRANCH_AREA_ID());
		} else if (StringUtils.isNotBlank(inputVO.getHIS_BRANCH_AREA_ID()) && !"null".equals(inputVO.getHIS_BRANCH_AREA_ID())) {
			sql.append("AND HIS.HIS_BRANCH_AREA_ID = :HIS_BRANCH_AREA_ID "); 
			condition.setObject("HIS_BRANCH_AREA_ID", inputVO.getHIS_BRANCH_AREA_ID());
		} else if (StringUtils.isNotBlank(inputVO.getBRANCH_AREA_ID()) && !"null".equals(inputVO.getBRANCH_AREA_ID())) {
			sql.append(" AND HIS.BRANCH_AREA_ID = :BRANCH_AREA_ID "); 
			condition.setObject("BRANCH_AREA_ID", inputVO.getBRANCH_AREA_ID());
		} 
		
		if (StringUtils.isNotBlank(inputVO.getHIS_BRANCH_ID()) && StringUtils.isNotBlank(inputVO.getBRANCH_ID())) {
			sql.append(" AND (HIS.HIS_BRANCH_ID = :HIS_BRANCH_ID OR HIS.BRANCH_ID = :BRANCH_ID) ");
			condition.setObject("HIS_BRANCH_ID", inputVO.getHIS_BRANCH_ID());
			condition.setObject("BRANCH_ID", inputVO.getBRANCH_ID());
		} else if (StringUtils.isNotBlank(inputVO.getHIS_BRANCH_ID())) {
			sql.append(" AND HIS.HIS_BRANCH_ID = :HIS_BRANCH_ID "); 
			condition.setObject("HIS_BRANCH_ID", inputVO.getHIS_BRANCH_ID());
		} else if (StringUtils.isNotBlank(inputVO.getBRANCH_ID()) && Integer.valueOf(inputVO.getBRANCH_ID()) > 0) {
			sql.append(" AND HIS.BRANCH_ID = :BRANCH_ID "); 
			condition.setObject("BRANCH_ID", inputVO.getBRANCH_ID());
		}
		//===
		
		if (StringUtils.isNotBlank(inputVO.getHIS_JOB_TITLE_NAME())){
			sql.append(" AND HIS_JOB.ROLE_ID = :HIS_JOB_TITLE_NAME "); 
			condition.setObject("HIS_JOB_TITLE_NAME", inputVO.getHIS_JOB_TITLE_NAME());
		}
		
		if (StringUtils.isNotBlank(inputVO.getJOB_TITLE_NAME())){
			sql.append(" AND NOW_JOB.ROLE_ID = :JOB_TITLE_NAME "); 
			condition.setObject("JOB_TITLE_NAME", inputVO.getJOB_TITLE_NAME());
		}
		
		if (StringUtils.isNotBlank(inputVO.getHIS_JOB_RANK())){
			sql.append(" AND HIS.HIS_JOB_RANK = :HIS_JOB_RANK "); 
			condition.setObject("HIS_JOB_RANK", inputVO.getHIS_JOB_RANK());
		}
		
		if (StringUtils.isNotBlank(inputVO.getJOB_RANK())){
			sql.append(" AND HIS.JOB_RANK = :JOB_RANK "); 
			condition.setObject("JOB_RANK", inputVO.getJOB_RANK());
		}
		
		if (StringUtils.isNotBlank(inputVO.getSTART_TIME())){
			sql.append("AND TO_CHAR(EFFDT, 'yyyyMMdd') >= TO_CHAR(:START_TIME, 'yyyyMMdd') ");
			condition.setObject("START_TIME", new Date(Long.parseLong(inputVO.getSTART_TIME())));
		}
		
		if (StringUtils.isNotBlank(inputVO.getEND_TIME())){
			sql.append("AND TO_CHAR(EFFDT, 'yyyyMMdd') <= TO_CHAR(:END_TIME, 'yyyyMMdd') ");
			condition.setObject("END_TIME", new Date(Long.parseLong(inputVO.getEND_TIME())));
		}
		
		sql.append("ORDER BY HIS.EMP_ID, EFFDT DESC ");
				
		condition.setQueryString(sql.toString());
		
		ResultIF empChangeLst = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = empChangeLst.getTotalPage(); // 分頁用
		int totalRecord_i = empChangeLst.getTotalRecord(); // 分頁用
		outputVO.setEmpChangeLst(empChangeLst); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		List<Map<String, String>> eXPORT_LST = dam.exeQuery(condition);
		outputVO.setEXPORT_LST(eXPORT_LST);
		
		sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		ORG410InputVO inputVO = (ORG410InputVO) body;
		
		List<Map<String, String>> exportLst = inputVO.getEXPORT_LST();
		List<Object[]> csv_list = new ArrayList<Object[]>();

		if (exportLst.size() > 0) {

			for (int index_i = 0; index_i < exportLst.size(); index_i++) {
				Map<String, String> map = exportLst.get(index_i);
				
				String CHANGE_NAME               = map.get("CHANGE_NAME") == null ? "" : map.get("CHANGE_NAME").toString();
				String EMP_ID                    = map.get("EMP_ID") == null ? "" : map.get("EMP_ID").toString();
				String EMP_NAME                  = map.get("EMP_NAME") == null ? "" : map.get("EMP_NAME").toString();
				String HIS_BRANCH_AREA_NAME      = map.get("HIS_BRANCH_AREA_NAME") == null ? "" : map.get("HIS_BRANCH_AREA_NAME").toString();
				String BRANCH_AREA_NAME          = map.get("BRANCH_AREA_NAME") == null ? "" : map.get("BRANCH_AREA_NAME").toString();
				String HIS_BRANCH_NAME           = map.get("HIS_BRANCH_NAME") == null ? "" : map.get("HIS_BRANCH_NAME").toString();
				String BRANCH_NAME               = map.get("BRANCH_NAME") == null ? "" : map.get("BRANCH_NAME").toString();
				String HIS_JOB_TITLE_NAME        = map.get("HIS_JOB_TITLE_NAME") == null ? "" : map.get("HIS_JOB_TITLE_NAME").toString();
				String JOB_TITLE_NAME            = map.get("JOB_TITLE_NAME") == null ? "" : map.get("JOB_TITLE_NAME").toString();
				String HIS_JOB_RANK              = map.get("HIS_JOB_RANK") == null ? "" : map.get("HIS_JOB_RANK").toString();
				String JOB_RANK                  = map.get("JOB_RANK") == null ? "" : map.get("JOB_RANK").toString();
				String EFFDT                     = map.get("EFFDT") == null ? "" : map.get("EFFDT").toString();

				Object[] csv_obj = {
						CHANGE_NAME                   ,
						EMP_ID + " " + EMP_NAME       ,
						HIS_BRANCH_AREA_NAME          ,
						BRANCH_AREA_NAME              ,
						HIS_BRANCH_NAME               ,
						BRANCH_NAME                   ,
						HIS_JOB_TITLE_NAME            ,
						JOB_TITLE_NAME                ,
						HIS_JOB_RANK                  ,
						JOB_RANK                      ,
						EFFDT               
				};
				
				csv_list.add(csv_obj);
			}
			
			CSVUtil csv = new CSVUtil();
			
			// 設定表頭
			csv.setHeader(new String[] { "異動類別","員工","區別","異動後區別","分行","異動後分行","職務","異動後職務","職等","異動後職等","異動時間" });
			// 添加明細的List
			csv.addRecordList(csv_list);

			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();
			String startTime = null;
			String endTime   = null;
			
			if (inputVO.getSTART_TIME().trim().length() > 0 && inputVO.getEND_TIME().trim().length() > 0) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				startTime = df.format(Long.parseLong(inputVO.getSTART_TIME()));
				endTime   = df.format(Long.parseLong(inputVO.getEND_TIME()));
			}

			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, "分行人員進出清單" + (startTime == null ? "" : ("_" + startTime + "_" + endTime)) + ".csv");
		}
		
		sendRtnObject(null);
	}
	
	public void getAoJobRankList(Object body, IPrimitiveMap header) throws Exception {
		
		ORG410OutputVO outputVO = new ORG410OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT R.ROLE_ID, R.ROLE_NAME, R.JOB_TITLE_NAME ");
		sb.append("FROM TBSYSSECUROLPRIASS P ");
		sb.append("LEFT JOIN TBORG_ROLE R ON P.ROLEID = R.ROLE_ID ");
		sb.append("WHERE PRIVILEGEID IN ('002', '003', 'UHRM002') ");
		sb.append("ORDER BY ROLE_NAME ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setAoJobRankList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
}

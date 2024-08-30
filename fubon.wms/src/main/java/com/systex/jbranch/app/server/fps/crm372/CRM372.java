package com.systex.jbranch.app.server.fps.crm372;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LISTPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_AO_LISTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_PLAN_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AOCODE_CHGLOGVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 理專十保總行功能
 * @author 0663412
 * 專案進度狀態代碼之定義說明
 * S0:計劃初始  -- 總行建立完計劃尚未上傳名單
 * S1:人管科上傳名單完成 --上傳建議名單
 * S2:業務處/副主管回報中(定義：任一處未完成回報時)
 * S3：業務處長回報完成，待人管科啟動 -- 所有名單均已回報完成
 * S4:已啟動客戶換手名單 --總行進行啟動分行個金主管分派作業
 * S5:個金主管進行名單指派中(定義：任一分行未完成指派)
 * S6：個金主管指派完成 --所有客戶均以分派完成
 * S7：已確認生效日 --總行都已將客戶押上生效日
 * S8:計劃已完成(定義：批次執行完成生效日) 客戶均已經換手成功
 */
@Component("crm372")
@Scope("request")
public class CRM372 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM372.class);
	
	/**
	 * 查詢所有計劃名稱
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		CRM372OutputVO outputVO = new CRM372OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		resultList = dam.exeQuery(setQuerySQL(inputVO, queryCondition));
		
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 查詢所有計劃名稱-設置查詢SQL
	 * 
	 * @param queryCondition
	 * @return queryCondition
	 */
	private QueryConditionIF setQuerySQL(CRM372InputVO inputVO, QueryConditionIF queryCondition) throws JBranchException  {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable(
				"FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		boolean headFlag = headmgrMap.containsKey(roleID);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("select crm.PRJ_CODE, crm.PRJ_NAME, crm.PRJ_STATUS, crm.START_DATE, crm.END_DATE, crm.CREATETIME, crm.CREATOR, emp_info.EMP_NAME ");
		sb.append("from TBCRM_10CMDT_PLAN_MAIN crm ");
		sb.append("left join VWORG_BRANCH_EMP_DETAIL_INFO emp_info on crm.CREATOR = emp_info.EMP_ID ");
		sb.append("where 1=1 ");
		
		//業務處長只需查詢待回報資料
		if(!headFlag){
			//S1:人管科上傳名單完成 --上傳建議名單, S2:業務處/副主管回報中(定義：任一處未完成回報時)
			sb.append("AND PRJ_STATUS IN ('S1','S2') ");
			//加判斷有待回報資料才出現
			sb.append("AND EXISTS(SELECT 'X' FROM TBCRM_10CMDT_PLAN_AO_LIST ao LEFT JOIN VWORG_AO_INFO org ON ao.EMP_ID = org.EMP_ID ");
			sb.append("WHERE crm.PRJ_CODE = ao.PRJ_CODE AND org.BRA_NBR IN (:branchList) )");
			queryCondition.setObject("branchList",  getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		//專案代碼
		if (!StringUtils.isBlank(inputVO.getPrj_code())) {
			sb.append("AND PRJ_CODE like :PRJ_CODE ");
			queryCondition.setObject("PRJ_CODE", "%" +inputVO.getPrj_code() + "%");
		}	
		
		//專案名稱
		if (!StringUtils.isBlank(inputVO.getPrd_name())) {
			sb.append("AND PRJ_NAME like :PRJ_NAME ");
			queryCondition.setObject("PRJ_NAME", "%" +inputVO.getPrd_name() + "%");
		}	
		
		//專案目前進度
		if (!StringUtils.isBlank(inputVO.getPrj_status())) {
			sb.append("AND PRJ_STATUS = :PRJ_STATUS ");
			queryCondition.setObject("PRJ_STATUS", inputVO.getPrj_status());
		}	
		
		//起始日期
		if (inputVO.getStartDate_s() != null) {
			sb.append("and TRUNC(crm.START_DATE) >= :START_DATE_S ");
			queryCondition.setObject("START_DATE_S", inputVO.getStartDate_s());
		}
		
		if (inputVO.getStartDate_e() != null) {
			sb.append("and TRUNC(crm.START_DATE) <= :START_DATE_E ");
			queryCondition.setObject("START_DATE_E", inputVO.getStartDate_e());
		}
		
		//結束日期
		if (inputVO.getEndDate_s() != null) {
			sb.append("and TRUNC(crm.END_DATE) >= :END_DATE_S ");
			queryCondition.setObject("END_DATE_S", inputVO.getEndDate_s());
		}
		
		if (inputVO.getEndDate_e() != null) {
			sb.append("and TRUNC(crm.END_DATE) <= :END_DATE_E ");
			queryCondition.setObject("END_DATE_E", inputVO.getEndDate_e());
		}
		
		//建立日期
		if (inputVO.getCreateDate_s() != null) {
			sb.append("and TRUNC(crm.CREATETIME) >= :CREATETIME_S ");
			queryCondition.setObject("CREATETIME_S", inputVO.getCreateDate_s());
		}
		
		if (inputVO.getCreateDate_e() != null) {
			sb.append("and TRUNC(crm.CREATETIME) <= :CREATETIME_E ");
			queryCondition.setObject("CREATETIME_E", inputVO.getCreateDate_e());
		}
		
		//建立人
		if (!StringUtils.isBlank(inputVO.getCreator())){
			sb.append("and crm.CREATOR = :CREATOR ");
			queryCondition.setObject("CREATOR", inputVO.getCreator());
		}
		
		sb.append("order by createtime desc ");
		queryCondition.setQueryString(sb.toString());
		return queryCondition;
	}
	
	/**
	 * 查詢所有的換手/輪調理專名單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */
	public void inquireAOList (Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		CRM372OutputVO outputVO = new CRM372OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<>();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable(
				"FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		boolean headFlag = headmgrMap.containsKey(roleID);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("select aolist.CHG_TYPE2, emp_info.REGION_CENTER_NAME, emp_info.BRANCH_AREA_NAME, emp_info.BRANCH_NAME, emp_info.EMP_NAME, emp_info.JOB_TITLE_NAME, aolist.PRJ_CODE, aolist.EMP_ID, aolist.CHG_TYPE, aolist.STATUS ");
		sb.append("from TBCRM_10CMDT_PLAN_AO_LIST aolist ");
		sb.append("left join VWORG_BRANCH_EMP_DETAIL_INFO emp_info on aolist.EMP_ID = emp_info.EMP_ID where PRJ_CODE = :PRJ_CODE ");
		queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
				
		//非總行人員, 只能查詢轄下
		if (!headFlag) {
			sb.append("AND emp_info.BRANCH_NBR IN (:branchList) ");
			queryCondition.setObject("branchList",  getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}

	
	/**
	 * 總行計劃建立初始階段
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void confirm(Object body, IPrimitiveMap header) throws Exception {
		dam = this.getDataAccessManager();
		CRM372InputVO inputVO = (CRM372InputVO) body;
		
		TBCRM_10CMDT_PLAN_MAINVO mainVO = (TBCRM_10CMDT_PLAN_MAINVO) dam.findByPKey(TBCRM_10CMDT_PLAN_MAINVO.TABLE_UID, inputVO.getPrj_code());
		if (mainVO != null) {
			//update
			mainVO.setPRJ_NAME(inputVO.getPrd_name());
			mainVO.setSTART_DATE(new Timestamp(inputVO.getStartDate().getTime()));
			mainVO.setEND_DATE(new Timestamp(inputVO.getEndDate().getTime()));
			
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("delete TBCRM_10CMDT_PLAN_AO_LIST where PRJ_CODE = :PRJ_CODE ");
			queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);
			
			if(inputVO.getCsvDataList().size()>0){
				mainVO.setPRJ_STATUS("S1"); //S1:人管科上傳名單完成 --上傳建議名單
				for(Map<String, Object> row : inputVO.getCsvDataList()) {
					TBCRM_10CMDT_PLAN_AO_LISTVO listVO = new TBCRM_10CMDT_PLAN_AO_LISTVO();
					TBCRM_10CMDT_PLAN_AO_LISTPK listPK = new TBCRM_10CMDT_PLAN_AO_LISTPK();
					listPK.setPRJ_CODE(inputVO.getPrj_code());
					listPK.setEMP_ID(row.get("EMP_ID").toString());
					listVO.setcomp_id(listPK);
					if(row.get("CHG_TYPE") != null){
						listVO.setCHG_TYPE(row.get("CHG_TYPE").toString());
						if(!row.get("CHG_TYPE").toString().isEmpty()){
							listVO.setSTATUS("S");
						}
					}
					listVO.setCHG_TYPE2(row.get("CHG_TYPE2").toString());
					listVO.setDATA_DATE(new Timestamp(new Date().getTime()));
					dam.create(listVO);
		        }
			} else {
				mainVO.setPRJ_STATUS("S0"); // S0:計劃初始  -- 總行建立完計劃尚未上傳名單
			}
			dam.update(mainVO);
		} else {
			//建單
			TBCRM_10CMDT_PLAN_MAINVO vo = new TBCRM_10CMDT_PLAN_MAINVO();
			vo.setPRJ_CODE(inputVO.getPrj_code());
			vo.setPRJ_NAME(inputVO.getPrd_name());
			vo.setSTART_DATE(new Timestamp(inputVO.getStartDate().getTime()));
			vo.setEND_DATE(new Timestamp(inputVO.getEndDate().getTime()));
			if(inputVO.getCsvDataList() != null &&inputVO.getCsvDataList().size()>0){
				vo.setPRJ_STATUS("S1"); //S1:人管科上傳名單完成 --上傳建議名單
				
				//create TBCRM_10CMDT_PLAN_AO_LIST
				for(Map<String, Object> row : inputVO.getCsvDataList()) {
					TBCRM_10CMDT_PLAN_AO_LISTVO listVO = new TBCRM_10CMDT_PLAN_AO_LISTVO();
					TBCRM_10CMDT_PLAN_AO_LISTPK listPK = new TBCRM_10CMDT_PLAN_AO_LISTPK();
					listPK.setPRJ_CODE(inputVO.getPrj_code());
					listPK.setEMP_ID(row.get("EMP_ID").toString());
					listVO.setcomp_id(listPK);
					listVO.setCHG_TYPE(row.get("CHG_TYPE").toString());
					listVO.setDATA_DATE(new Timestamp(new Date().getTime()));
					if(!row.get("CHG_TYPE").toString().isEmpty()){
						listVO.setSTATUS("S");
					}
					listVO.setCHG_TYPE2(row.get("CHG_TYPE2").toString());
					dam.create(listVO);
		        }
			} else {
				vo.setPRJ_STATUS("S0"); // S0:計劃初始  -- 總行建立完計劃尚未上傳名單
			}
			dam.create(vo);
		} 
		this.sendRtnObject(null);
	}
	
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		dam = this.getDataAccessManager();
		notifyClientToDownloadFile("doc//CRM//CRM372_EXAMPLE.csv", "人員檔案上傳範例.csv");
	}
	
	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append("delete TBCRM_10CMDT_PLAN_AO_LIST where PRJ_CODE = :PRJ_CODE ");
		queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append("delete TBCRM_10CMDT_PLAN_MAIN where PRJ_CODE = :PRJ_CODE ");
		queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		CRM372OutputVO outputVO = new CRM372OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error = new ArrayList<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		List<String> errorList = new ArrayList<String>();
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"請輸入換手/輪調理專員編".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"類別(換手C/輪調P)".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"參考名單(主要M/參考R)".equals(str[2].trim()))
							throw new Exception(str[2]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				
				//查詢 業務處、營運區、分行名稱、理專姓名
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("select EMP_ID, JOB_TITLE_NAME, REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NAME, EMP_NAME from VWORG_BRANCH_EMP_DETAIL_INFO where EMP_ID = :EMP_ID ");
				queryCondition.setObject("EMP_ID", String.format("%06d", Long.parseLong(str[0].trim())));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
				
			     if (list.size() > 0) {
			    	 if (list.get(0).get("REGION_CENTER_NAME") != null && list.get(0).get("BRANCH_AREA_NAME") != null && list.get(0).get("BRANCH_NAME") != null) {
			    		 list.get(0).put("CHG_TYPE", str[1].trim());
			    		 list.get(0).put("CHG_TYPE2", str[2].trim());
			    		 resultList.addAll(list);
			    	 } else {
			    		 errorList.add(String.format("%06d", Long.parseLong(str[0].trim())) + "組織不存在");
			    	 }
			     }else{
			    	 errorList.add(String.format("%06d", Long.parseLong(str[0].trim())) + "員編不存在");
			     }
			}
		}
		outputVO.setResultList(resultList);
		outputVO.setErrorList(errorList);
		this.sendRtnObject(outputVO);
	}
	
	/**產生專案代碼  年月日加三碼流水號**/
	public void createPrjCode() throws JBranchException {
		CRM372OutputVO outputVO = new CRM372OutputVO();
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMM");
		String date = df.format(new Date());
        
		try{
			seqNum = date + String.format("%03d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_10CMDT_PLAN_MAIN")));
		}
		catch(Exception e){
			sn.createNewSerial("TBCRM_10CMDT_PLAN_MAIN", "000", 1, "d", null, 1, new Long("999"), "y", new Long("0"), null);
			seqNum = date + String.format("%03d", Integer.valueOf(sn.getNextSerialNumber("TBCRM_10CMDT_PLAN_MAIN")));
		}
		outputVO.setPrj_code(seqNum);
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 業務處長進行理專輪調/換手分派作業
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void directorConfirm(Object body, IPrimitiveMap header) throws Exception {
		dam = this.getDataAccessManager();
		CRM372InputVO inputVO = (CRM372InputVO) body;
		
		// S2:業務處/副主管回報中(定義：任一處未完成回報時)
		this.updatePlanStatus("S2", inputVO.getPrj_code());
		
		for(Map<String, Object> row : inputVO.getCsvDataList()) {
			
			//若CHG_TYPE=P(輪調)及CHG_TYPE2=R(參考)，則CHG_TYPE2改為MR.
			//20201120,人管科長指示參考名單不需從此產生客戶移轉清單，應由例行作業即可。
			//String chgType2 = (StringUtils.equals("P", row.get("CHG_TYPE").toString()) && StringUtils.equals("R", row.get("CHG_TYPE2").toString())) ? "MR" : row.get("CHG_TYPE2").toString();
			
			TBCRM_10CMDT_PLAN_AO_LISTPK pk = new TBCRM_10CMDT_PLAN_AO_LISTPK();
			pk.setPRJ_CODE(row.get("PRJ_CODE").toString());
			pk.setEMP_ID(row.get("EMP_ID").toString());
			TBCRM_10CMDT_PLAN_AO_LISTVO vo = (TBCRM_10CMDT_PLAN_AO_LISTVO) dam.findByPKey(TBCRM_10CMDT_PLAN_AO_LISTVO.TABLE_UID, pk);
			if (vo != null) {
				vo.setSTATUS("C");
				vo.setCHG_TYPE(row.get("CHG_TYPE").toString());
				//vo.setCHG_TYPE2(chgType2);
				vo.setCHG_TYPE2(row.get("CHG_TYPE2").toString());
				dam.update(vo);
			} 
        }
		
		//檢查處長是否均已分派完成, 則進入下一關 S3：業務處長回報完成，待人管科啟動 -- 所有名單均已回報完成
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_CODE FROM TBCRM_10CMDT_PLAN_AO_LIST WHERE PRJ_CODE = :PRJ_CODE AND STATUS = 'S' ");
		queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.isEmpty()){
			this.updatePlanStatus("S3", inputVO.getPrj_code());
		}
		this.sendRtnObject(null);
	}
	
	/**
	 * 總行進行啟用分行個金主管分派客戶作業
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void prjEnabled(Object body, IPrimitiveMap header) throws JBranchException {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		//S4:已啟動客戶換手名單 --總行進行啟動分行個金主管分派作業
		this.updatePlanStatus("S4", inputVO.getPrj_code());
		
		//將資料寫入移轉清單內
		getPlist(inputVO);
		
		this.sendRtnObject(null);
	}
	
	//加入移轉
	public void getPlist(CRM372InputVO body) throws JBranchException {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		CRM372OutputVO outputVO = new CRM372OutputVO();
		dam = this.getDataAccessManager();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2));	//理專
		Map<String, String> fchMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2));	//FCH理專
		
		//查詢理專十保換手經營客戶名單 寫入 客戶移轉-待移入清單(輪調)
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" INSERT INTO TBCRM_TRS_AOCHG_PLIST(SEQ, CUST_ID, ORG_AO_CODE, ORG_AO_BRH, NEW_AO_BRH, APL_EMP_ID,");
		sql.append(" APL_EMP_ROLE, APL_DATETIME, APL_REASON, APL_OTH_REASON, TRS_FLOW_TYPE, PROCESS_STATUS, TRS_TYPE, TRS_TXN_SOURCE, PRJ_ID ,VERSION,CREATOR,CREATETIME,MODIFIER,LASTUPDATE)");
		sql.append(" SELECT TBCRM_TRS_AOCHG_PLIST_SEQ.nextval AS SEQ, MAST.CUST_ID AS CUST_ID, MAST.AO_CODE  AS ORG_AO_CODE ,");
		sql.append(" MAST.BRA_NBR  AS ORG_AO_BRH, MAST.BRA_NBR  AS NEW_AO_BRH,");
		sql.append(" :EMP_ID as APL_EMP_ID, :EMP_ROLE as APL_EMP_ROLE, SYSDATE as APL_DATETIME, '98' as APL_REASON,");
		sql.append(" CASE WHEN CMDT_LIST.CUST_ID IS NOT NULL THEN 'Y' ELSE 'N' END as APL_OTH_REASON,"); //是否經營滿七年判斷
		sql.append(" '2' as TRS_FLOW_TYPE, 'L1' as PROCESS_STATUS, ");
		sql.append(" 'A' as TRS_TYPE, AO_LIST.CHG_TYPE as TRS_TXN_SOURCE, PRJ_CODE as PRJ_ID,0,:EMP_ID AS CREATOR,SYSDATE,:EMP_ID AS MODIFIER,SYSDATE ");
		sql.append(" FROM TBCRM_10CMDT_PLAN_AO_LIST AO_LIST  ");
		sql.append(" LEFT JOIN VWORG_AO_INFO INFO ON AO_LIST.EMP_ID = INFO.EMP_ID ");
		sql.append(" INNER JOIN TBCRM_CUST_MAST MAST ON INFO.AO_CODE = MAST.AO_CODE ");
		sql.append(" INNER JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");  //避免個金主管無法分派
		sql.append(" LEFT JOIN TBCRM_10CMDT_CUST_LIST CMDT_LIST ON MAST.CUST_ID = CMDT_LIST.CUST_ID "); //經營滿七年客戶清單
//		sql.append(" LEFT JOIN (SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME                                                                   ");
//		sql.append(" FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO WHERE MR.ROLE_ID = RO.ROLE_ID                                                                     ");
//		sql.append(" AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y' ) RO                                                            ");
//		sql.append(" ON INFO.EMP_ID = RO.EMP_ID LEFT JOIN TBSYSSECUROLPRIASS PRI ON RO.ROLE_ID  = PRI.ROLEID                                                    ");
		sql.append(" WHERE PRJ_CODE = :PRJ_CODE AND CHG_TYPE2 in ('M', 'MR')  AND CHG_TYPE = 'P'");//CHG_TYPE = 'P' --輪調
		queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
		queryCondition.setObject("EMP_ID", (String)getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("EMP_ROLE", (String)getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		dam.exeUpdate(queryCondition);
		
		//查詢理專十保換手經營客戶名單 寫入 客戶移轉-待移入清單(換手)
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append(" INSERT INTO TBCRM_TRS_AOCHG_PLIST(SEQ, CUST_ID, ORG_AO_CODE, ORG_AO_BRH, NEW_AO_BRH, APL_EMP_ID,");
		sql.append(" APL_EMP_ROLE, APL_DATETIME, APL_REASON, APL_OTH_REASON, TRS_FLOW_TYPE, PROCESS_STATUS, TRS_TYPE, TRS_TXN_SOURCE, PRJ_ID ,VERSION,CREATOR,CREATETIME,MODIFIER,LASTUPDATE)");
		sql.append(" SELECT TBCRM_TRS_AOCHG_PLIST_SEQ.nextval AS SEQ, MAST.CUST_ID AS CUST_ID, MAST.AO_CODE  AS ORG_AO_CODE ,");
		sql.append(" MAST.BRA_NBR  AS ORG_AO_BRH, MAST.BRA_NBR  AS NEW_AO_BRH,");
		sql.append(" :EMP_ID as APL_EMP_ID, :EMP_ROLE as APL_EMP_ROLE, SYSDATE as APL_DATETIME, '98' as APL_REASON, 'Y' as APL_OTH_REASON, '2' as TRS_FLOW_TYPE, 'L1' as PROCESS_STATUS, ");
		sql.append(" 'A' as TRS_TYPE, AO_LIST.CHG_TYPE as TRS_TXN_SOURCE, PRJ_CODE as PRJ_ID,0,:EMP_ID AS CREATOR,SYSDATE,:EMP_ID AS MODIFIER,SYSDATE ");
		sql.append(" FROM TBCRM_10CMDT_PLAN_AO_LIST AO_LIST  ");
		sql.append(" LEFT JOIN VWORG_AO_INFO INFO ON AO_LIST.EMP_ID = INFO.EMP_ID ");
		sql.append(" INNER JOIN TBCRM_CUST_MAST MAST ON INFO.AO_CODE = MAST.AO_CODE ");
		sql.append(" INNER JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");  //避免個金主管無法分派
		sql.append(" INNER JOIN TBCRM_10CMDT_CUST_LIST CMDT_LIST ON MAST.CUST_ID = CMDT_LIST.CUST_ID "); //只挑出經營滿七年客戶
		sql.append(" WHERE PRJ_CODE = :PRJ_CODE AND CHG_TYPE2 in ('M', 'MR')  AND CHG_TYPE = 'C'"); //CHG_TYPE = 'C' --換手
		queryCondition.setObject("PRJ_CODE", inputVO.getPrj_code());
		queryCondition.setObject("EMP_ID", (String)getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("EMP_ROLE", (String)getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		dam.exeUpdate(queryCondition);		
				
		this.sendRtnObject(outputVO);
	}
	
	private String getAOCHGSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_TRS_AOCHG_PLIST_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	private Boolean checkID(String pk, String type) throws JBranchException {
		Boolean ans = false;
		if(StringUtils.equals("PLIST", type)) {
			BigDecimal check = new BigDecimal(pk);
			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, check);
			if (vo != null)
				ans = true;
		} else {
			TBCRM_CUST_AOCODE_CHGLOGVO vo = new TBCRM_CUST_AOCODE_CHGLOGVO();
			vo = (TBCRM_CUST_AOCODE_CHGLOGVO) dam.findByPKey(TBCRM_CUST_AOCODE_CHGLOGVO.TABLE_UID, pk);
			if (vo != null)
				ans = true;
		}
		return ans;
	}
	
	/**
	 * 更新計劃狀態
	 * @param status
	 * @throws JBranchException
	 */
	public void updatePlanStatus(String status,String prjCode) throws JBranchException{
		dam = this.getDataAccessManager();
		TBCRM_10CMDT_PLAN_MAINVO mainVO = (TBCRM_10CMDT_PLAN_MAINVO) dam.findByPKey(TBCRM_10CMDT_PLAN_MAINVO.TABLE_UID, prjCode);
		mainVO.setPRJ_STATUS(status); 
		dam.update(mainVO);
	}
	
	/**
	 * 總行退回給業務處長 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void reject(Object body, IPrimitiveMap header) throws Exception {
		CRM372InputVO inputVO = (CRM372InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String, Object> row : inputVO.getCsvDataList()) {
			TBCRM_10CMDT_PLAN_AO_LISTPK listPK = new TBCRM_10CMDT_PLAN_AO_LISTPK();
			listPK.setPRJ_CODE(row.get("PRJ_CODE").toString());
			listPK.setEMP_ID(row.get("EMP_ID").toString());
			
			//更改狀態退回給總行
			TBCRM_10CMDT_PLAN_AO_LISTVO vo = (TBCRM_10CMDT_PLAN_AO_LISTVO) dam.findByPKey(TBCRM_10CMDT_PLAN_AO_LISTVO.TABLE_UID, listPK);
			if (vo != null) {
				vo.setcomp_id(listPK);
				vo.setSTATUS("S");
				dam.update(vo);
			} 
        }
		
		this.updatePlanStatus("S2", inputVO.getPrj_code());
		
		this.sendRtnObject(null);
	}
}

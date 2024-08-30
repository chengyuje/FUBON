package com.systex.jbranch.app.server.fps.pms315;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_VISIT_RECORDVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0VO;
import com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0PK;
import com.systex.jbranch.app.server.fps.crm131.CRM131;
import com.systex.jbranch.app.server.fps.crm671.CRM671InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms315.PMS315InputVO;
import com.systex.jbranch.app.server.fps.pms315.PMS315OutputVO;
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
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms315")
@Scope("request")
public class PMS315 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS315.class);
	
	/**
	 * 年月下拉選單查詢
	 * */
//	public void initialQuery(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
//		
//		PMS315OutputVO outputVO = new PMS315OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sb = new StringBuffer();
//		
//		sb.append("SELECT DISTINCT YEARMON AS DATA, SUBSTR(YEARMON,0,4)||'/'||SUBSTR(YEARMON,5,2) AS LABEL FROM TBPMS_SA_FWL8APD0 ");
//		sb.append("ORDER BY YEARMON DESC FETCH FIRST 12 ROW ONLY");
//		
//		queryCondition.setQueryString(sb.toString());
//		List<Map<String, Object>> ymList = dam.exeQuery(queryCondition);
//		
//		outputVO.setYmList(ymList);
//		sendRtnObject(outputVO);
//	}
	
	
	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		// 輸入VO
		PMS315InputVO inputVO = (PMS315InputVO)body;
		// 輸出VO
		PMS315OutputVO outputVO = new PMS315OutputVO();
		String roleType = "";
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
		
		
		if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				pms000InputVO.setReportDate(inputVO.getsCreDate());
		}
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
	
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select ROWNUM, t.* from ( ");
			sql.append(" select * from TBPMS_SA_FWL8APD0 ");
			sql.append(" where ROWNUM<=2000 ");
			// ==主查詢條件==
			// 日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				sql.append(" and YEARMON =:STARTTIME ");
				condition.setObject("STARTTIME", inputVO.getsCreDate());	
			}
			
			// FST_ACT_DT 預計第一次扣款日
			if (inputVO.getFST_ACT_DTS() != null) {
				sql.append("and TRUNC(FST_ACT_DT) >= TRUNC(TO_DATE(:timesfst, 'YYYY-MM-DD')) ");
				condition.setObject("timesfst",new java.text.SimpleDateFormat("yyyyMMdd")
						.format(inputVO.getFST_ACT_DTS()));
			}
			if (inputVO.getFST_ACT_DTE() != null) {
				sql.append("and TRUNC(FST_ACT_DT) <= TRUNC(TO_DATE(:timeefst, 'YYYY-MM-DD')) ");
				condition.setObject("timeefst",new java.text.SimpleDateFormat("yyyyMMdd")
					.format(inputVO.getFST_ACT_DTE()));
			}
			
			
			// PAID_DATE 應繳日期
			if (inputVO.getPAID_DATES() != null) {
				sql.append("and TRUNC(PAID_DATE) >= TRUNC(TO_DATE(:timespaids, 'YYYY-MM-DD')) ");
				condition.setObject("timespaids",new java.text.SimpleDateFormat("yyyyMMdd")
					.format(inputVO.getPAID_DATES()));
			}
			if (inputVO.getPAID_DATEE() != null) {
				sql.append("and TRUNC(PAID_DATE) <= TRUNC(TO_DATE(:timespaide, 'YYYY-MM-DD')) ");
				condition.setObject("timespaide",new java.text.SimpleDateFormat("yyyyMMdd")
					.format(inputVO.getPAID_DATEE()));
			}
			
			// 客戶ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sql.append(" and CUST_ID =:CUST_ID ");
				condition.setObject("CUST_ID", inputVO.getCUST_ID());	
			}
			
			// 客戶
			if (!StringUtils.isBlank(inputVO.getCUST_NAME())) {
				sql.append(" and CUST_NAME =:CUST_NAME ");
				condition.setObject("CUST_NAME", inputVO.getCUST_NAME());	
			}
			
			// 繳別
			if (!StringUtils.isBlank(inputVO.getMOP_T())) {
				sql.append(" and MOP_T =:MOP_T ");
				condition.setObject("MOP_T", inputVO.getMOP_T());	
			}
			
			// 保單號碼
			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
				sql.append(" and POLICY_NO =:POLICY_NO ");
				condition.setObject("POLICY_NO", inputVO.getPOLICY_NO());	
			}
			
			// 保單序號
			if (!StringUtils.isBlank(inputVO.getPOLICY_SEQ())) {
				sql.append(" and POLICY_SEQ =:POLICY_SEQ ");
				condition.setObject("POLICY_SEQ", inputVO.getPOLICY_SEQ());	
			}
			
			// 繳費狀態
			if (!StringUtils.isBlank(inputVO.getPAID_STAT())) {
				sql.append(" and PAID_STAT =:PAID_STAT ");
				condition.setObject("PAID_STAT", inputVO.getPAID_STAT());	
			}					
		
			// AO_COCE
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append("and AO_CODE = :ao_code ");
				condition.setObject("ao_code", inputVO.getAo_code());
			}
			else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append("and AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("and BRANCH_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}
			else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and BRANCH_NBR IN (:branch_nbr) ");
					//pms000outputVO.getBranchList
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				//sql.append("and BRANCH_AREA_ID = :branch_area_id ");
				sql.append("  and BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID = :branch_area_id ");
				sql.append("  ) ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}
			else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					//sql.append("and BRANCH_AREA_ID IN (:branch_area_id) ");
					sql.append("  and BRANCH_NBR IN ( ");
					sql.append("    SELECT BRANCH_NBR ");
					sql.append("    FROM VWORG_DEFN_BRH ");
					sql.append("    WHERE DEPT_ID IN (:branch_area_id) ");
					sql.append("  ) ");
					//pms000outputVO.getArea_branchList
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				//sql.append("and REGION_CENTER_ID = :region_center_id ");
				sql.append("  and BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID = :region_center_id ");
				sql.append("  ) ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
			}
			else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					//sql.append("and REGION_CENTER_ID IN (:region_center_id) ");
					sql.append("  and BRANCH_NBR IN ( ");
					sql.append("    SELECT BRANCH_NBR ");
					sql.append("    FROM VWORG_DEFN_BRH ");
					sql.append("    WHERE DEPT_ID IN (:region_center_id) ");
					sql.append("  ) ");
					//pms000outputVO.getRegionList
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}

//			sql.append("ORDER BY ");
//			sql.append("REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, AO_CODE, YEARMON)t ");
			//排序共用套件放在裡面會造成錯誤  需要放外面共用元件才會完成
			sql.append(" ) t ");
			sql.append("ORDER BY ");
			sql.append("t.REGION_CENTER_ID, t.BRANCH_AREA_ID, t.BRANCH_NBR, t.AO_CODE, t.YEARMON ");
			condition.setQueryString(sql.toString());
			// 分頁查詢
			ResultIF rlist = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			// CSV全部查詢資訊
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = rlist.getTotalPage(); // 分頁用
			int totalRecord_i = rlist.getTotalRecord(); // 分頁用
			outputVO.setResultList(rlist); // data
			outputVO.setCsvList(csvList); // csv匯出用
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	public void update(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS315InputVO inputVO = (PMS315InputVO) body;
		try {
			for (Map<String, Object> map : inputVO.getList()){
				if(map.get("DESCR254") == null){
					map.put("DESCR254", "");
				}
				//更新PMS315的訪談紀錄
				TBPMS_SA_FWL8APD0PK PK = new TBPMS_SA_FWL8APD0PK();
				TBPMS_SA_FWL8APD0VO VO = new TBPMS_SA_FWL8APD0VO();
				DataAccessManager dam = this.getDataAccessManager();
				
				PK.setW_SEQ((String)map.get("W_SEQ"));
				PK.setYEARMON((String)map.get("YEARMON"));
				VO = (TBPMS_SA_FWL8APD0VO) dam.findByPKey(
						TBPMS_SA_FWL8APD0VO.TABLE_UID, PK);
				if (VO != null) {
					VO.setDESCR254((String)map.get("DESCR254"));
					dam.update(VO);
					//新增CRM671的訪談紀錄-PMS315有輸入訪談紀錄內容才新增
					if(!ObjectUtils.equals(ObjectUtils.toString(map.get("DESCR254")), "")){
						dam = this.getDataAccessManager();
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

						StringBuffer sb = new StringBuffer();
						sb.append("SELECT TO_CHAR(SQ_TBCRM_CUST_VISIT_RECORD.nextval) AS SEQNO FROM DUAL");
						queryCondition.setQueryString(sb.toString());
						List<Map<String, Object>> list = dam.exeQuery(queryCondition);
						String seqNo = addZeroForNum((String) list.get(0).get("SEQNO"), 10);

						TBCRM_CUST_VISIT_RECORDVO vo = new TBCRM_CUST_VISIT_RECORDVO();
						vo.setVISIT_SEQ(seqNo);
						vo.setVISITOR_ROLE((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
						vo.setCUST_ID((String)map.get("CUST_ID"));
						vo.setCMU_TYPE("");
						vo.setVISIT_MEMO((String)map.get("DESCR254"));
						dam.create(vo);
						
						//20200910 有聯繫記錄取消燈號檢核(WMS-CR-20200821-01_業管系統新增理專轄下未聯繫客戶燈號警示)
//						TBCRM_CUST_NOTEVO cnVO = new TBCRM_CUST_NOTEVO();
//						cnVO = (TBCRM_CUST_NOTEVO) dam.findByPKey(TBCRM_CUST_NOTEVO.TABLE_UID, (String)map.get("CUST_ID"));
//						if(cnVO != null) {
//							cnVO.setCONTACT_LIGHT(null);
//							cnVO.setCONTACT_LIGHT_CHKYN("N");
//							cnVO.setCONTACT_LIGHT_DATE(null);
//							dam.update(cnVO);
//						}
						
						//2018-12-14 by Jacky WMS-CR-20181025-02_新增客戶聯繫頻率管理報表
						//排除當日連繫客戶
						CRM131 crm131 = (CRM131) PlatformContext.getBean("crm131");
						crm131.updateUnderservCust((String)map.get("CUST_ID"));
						
					}
					this.sendRtnObject(null);
				} else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_005");
				}
			}
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

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
	
}
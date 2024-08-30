package com.systex.jbranch.app.server.fps.pms107;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms202.PMS202OutputVO;
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

/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :陪訪成效分析<br>
 * Comments Name : PMS107.java<br>
 * Author : Kevin Hsu<br>
 * Date :2016年07月05日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */
@Component("pms107")
@Scope("request")
public class PMS107 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS107.class);
	
	/**
	 * 初使化載入資料
	 * 
	 * @author Jacky
	 * @date 2017/02/9
	 * 
	 */
	public void getFAIA(Object body, IPrimitiveMap header) throws JBranchException {
		//輸入VO
		PMS107InputVO inputVO = (PMS107InputVO) body;
		//輸出VO
		PMS107OutputVO return_VO = new PMS107OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();		
		try {
			//==查詢==
			//版
//			sql.append("SELECT EMP_ID as DATA, EMP_NAME as LABEL FROM TBPMS_EMPLOYEE_REC WHERE ROLE_ID IN ('FA','IA') "
//					+ "AND :DATA_DATE BETWEEN START_TIME AND END_TIME");
			//JACKY && STEVEN版
//			sql.append("    SELECT DISTINCT FAIA.EMP_ID as DATA, EMP.EMP_NAME as LABEL      ");
//			sql.append("    FROM TBPMS_FAIA_REC FAIA                                        ");
//			sql.append("    INNER JOIN TBPMS_EMPLOYEE_REC_N EMP                             ");
//			sql.append("       ON FAIA.EMP_ID = EMP.EMP_ID                                  ");
//			sql.append("      AND :DATA_DATE BETWEEN EMP.START_TIME AND EMP.END_TIME        ");
//			sql.append("    WHERE :DATA_DATE BETWEEN EMP.START_TIME AND EMP.END_TIME        ");
//			sql.append("   ORDER BY FAIA.EMP_ID ");

			if(((String)getCommonVariable(FubonSystemVariableConsts.LOGINROLE)).equals("FA") || 
				((String)getCommonVariable(FubonSystemVariableConsts.LOGINROLE)).equals("IA") ||
				((String)getCommonVariable(FubonSystemVariableConsts.LOGINROLE)).equals("FA9") ||
				((String)getCommonVariable(FubonSystemVariableConsts.LOGINROLE)).equals("IA9")
					){
				sql.append("  select V_STRING1 AS DATA,V_STRING2 LABEL ");
				sql.append("  from table(FN_GET_VRR_D('', to_date(:DATA_DATE,'YYYYMMDD'), :emp_id , :role_id , '','','8')) ");
				sql.append("  order by V_STRING1,V_STRING2  ");
				queryCondition.setQueryString(sql.toString());
				queryCondition.setObject("DATA_DATE", inputVO.getReportDate()+"01"); // 年月
				queryCondition.setObject("role_id",(String)getCommonVariable(FubonSystemVariableConsts.LOGINROLE)); // ro_id			
				queryCondition.setObject("emp_id",(String)getCommonVariable(FubonSystemVariableConsts.LOGINID)); // 登入id
				return_VO.setFaiaList(dam.exeQuery(queryCondition));
			}else{
				//2017/05/17  增加判斷   角色顯示出來roid
				sql.append(" SELECT DISTINCT FAIA.EMP_ID as DATA, EMP.EMP_NAME as LABEL             ");
				sql.append("   FROM TBPMS_FAIA_REC FAIA                                             ");
				sql.append("   INNER JOIN TBPMS_EMPLOYEE_REC_N EMP                                  ");
				sql.append("      ON FAIA.EMP_ID = EMP.EMP_ID                                       ");
				sql.append("     AND :DATA_DATE BETWEEN EMP.START_TIME AND EMP.END_TIME             ");
				sql.append("   WHERE FAIA.SST_ID = ( CASE WHEN :role_id in ('FA','FA9') THEN 'FA'   ");
				sql.append("                              WHEN :role_id in ('IA','IA9') THEN 'IA'   ");
				sql.append("                              ELSE FAIA.SST_ID END )                    ");
				sql.append("    AND FAIA.EMP_ID = ( CASE WHEN :role_id in ('FA','IA') THEN :emp_id  ");
				sql.append("                             ELSE FAIA.EMP_ID END )                     ");
				sql.append("  ORDER BY FAIA.EMP_ID                                                  ");
				queryCondition.setQueryString(sql.toString());
				queryCondition.setObject("DATA_DATE",  PMS000.getMonthLastDate(inputVO.getReportDate())); // 年月
				queryCondition.setObject("role_id",(String)getCommonVariable(FubonSystemVariableConsts.LOGINROLE)); // ro_id			
				queryCondition.setObject("emp_id",(String)getCommonVariable(FubonSystemVariableConsts.LOGINID)); // 登入id
				return_VO.setFaiaList(dam.exeQuery(queryCondition));
			}
			
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 明細查詢1
	 * 
	 * @author Kevin
	 * @date 2016/07/13
	 * 
	 */
	public void getDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		//輸入VO
		PMS107InputVO inputVO = (PMS107InputVO) body;
		//輸出VO
		PMS107OutputVO return_VO = new PMS107OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			//==查詢==
			sql.append("SELECT ROWNUM AS NUM,T.* "
					+ "FROM  "
					+ "TBPMS_SPRT_EFCT_DTL_INV T "
					+ "WHERE 1=1 ");
			sql.append(" AND T.AO_CODE = :id AND T.YEARMON= :YEMON");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("id", inputVO.getAo_code()); // 理專編號ao_code
			queryCondition.setObject("YEMON", inputVO.getYEARMON()); // 年月
			// result 結果
			return_VO.setResultList(dam.exeQuery(queryCondition));
		
			sql = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql.append("SELECT ROWNUM AS NUM,T.* "
					+ "FROM  "
					+ "TBPMS_SPRT_EFCT_DTL_INS T "
					+ "WHERE 1=1 ");
			sql.append(" AND T.AO_CODE = :id AND T.YEARMON= :YEMON");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("id", inputVO.getAo_code());
			queryCondition.setObject("YEMON", inputVO.getYEARMON());
			return_VO.setResultList2(dam.exeQuery(queryCondition));
			
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	

	/**
	 * 查詢
	 * 
	 * @author Kevin
	 * @date 2016/07/13
	 * 
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS107InputVO inputVO = (PMS107InputVO) body;
		PMS107OutputVO outputVO = new PMS107OutputVO();
		dam = this.getDataAccessManager();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			
			//取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(inputVO.getsTime());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			
			//==查詢==
			sql.append("SELECT ROWNUM AS NUM,T.*"
					+ " FROM "
					+ "( SELECT a.*,b.BRANCH_NBR  AS SS_BR"
					+ " FROM "
					+ "TBPMS_SPRT_EFCT_MAST a "
					+ "left join "
					+ "VWORG_BRANCH_EMP_DETAIL_INFO b "
					+ "on a.SS_EMP_ID=b.EMP_ID "
					+ " WHERE 1=1  ");
			//==查詢條件==
			//年月
			sql.append(" and a.YEARMON LIKE :YEARMONN ");
			condition.setObject("YEARMONN", inputVO.getsTime());
			//faia
			//SS_EMP_ID   輔消人員清單
			if (StringUtils.isNotBlank(inputVO.getFaia())) {
				sql.append("  and a.SS_EMP_ID = :SS_EMP_IDD ");
				condition.setObject("SS_EMP_IDD", inputVO.getFaia());
			}else{
				// AO_COCE
				if (StringUtils.isNotBlank(inputVO.getAo_code())) {
					sql.append(" and a.AO_CODE = :ao_code ");
					condition.setObject("ao_code", inputVO.getAo_code());
				}else{
					//登入為銷售人員強制加AO_CODE
					if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
						if(pms000outputVO.getV_aoList().size()!=0){  //判斷ao_code
							sql.append(" and AO_CODE IN (:ao_code) ");
							condition.setObject("ao_code", pms000outputVO.getV_aoList());
						}
					}
				}
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and a.BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}else{
					//登入非總行人員強制加分行
					if(!headmgrMap.containsKey(roleID)) {
						if(pms000outputVO.getV_branchList().size()!=0){
							sql.append(" and a.BRANCH_NBR IN (:branch_nbr) ");
							condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
						}						
					}
				}
//				// 營運區
//				if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
//					sql.append(" and a.BRANCH_AREA_ID = :branch_area_id ");
//					condition.setObject("branch_area_id", inputVO.getBranch_area_id());
//				}else{
//					//登入非總行人員強制加營運區
//					if(!headmgrMap.containsKey(roleID)) {
//						if(pms000outputVO.getV_areaList().size()!=0){
//							sql.append(" and a.BRANCH_AREA_ID IN (:branch_area_id) ");
//							condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
//						}
//					}
//				}
				
				
//				// 區域中心
//				if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
//					sql.append(" and a.REGION_CENTER_ID = :region_center_id ");
//					condition.setObject("region_center_id", inputVO.getRegion_center_id());
//				}else{
//					//登入非總行人員強制加區域中心
//					if(!headmgrMap.containsKey(roleID)) {
//						if(pms000outputVO.getV_regionList().size()!=0){
//							sql.append(" and a.REGION_CENTER_ID IN (:region_center_id) ");
//							condition.setObject("region_center_id", pms000outputVO.getV_regionList());
//						}
//					}
//				}
			}
			
			
			sql.append("   ) T ");
			condition.setQueryString(sql.toString());
			
			//查詢結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
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

	/**
	 * 輔消人員
	 * 
	 * @author Kevin
	 * @date 2016/07/13
	 * 
	 */
	public void sup(Object body, IPrimitiveMap header) throws JBranchException {
		PMS202OutputVO return_VO = new PMS202OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			//==查詢==
			sql.append("select  a.EMP_ID ||'_'|| b.EMP_NAME AS NAME,"
					+ "a.EMP_ID AS EMP_ID,"
					+ "b.BRANCH_NAME AS BRANCH_NAME,"
					+ "b.BRANCH_NBR AS BRANCH_NBR "
					+ "from "
					+ "TBORG_FAIA a "
					+ "left join "
					+ "VWORG_BRANCH_EMP_DETAIL_INFO b "
					+ "on a.EMP_ID=b.EMP_ID "
					+ "AND a.BRANCH_NBR=b.BRANCH_NBR "
					+ "where "
					+ "b.BRANCH_NBR IS NOT NULL ");
			queryCondition.setQueryString(sql.toString());
			// result 結果
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setAolist(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
}
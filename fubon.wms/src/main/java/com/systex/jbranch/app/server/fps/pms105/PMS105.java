package com.systex.jbranch.app.server.fps.pms105;

import java.text.SimpleDateFormat;
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
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :金流已入帳未規劃<br>
 * Comments Name : PMS105.java<br>
 * Author : KevinHsu<br>
 * Date :2016年07月06日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年02月01日<br>
 */
@Component("pms105")
@Scope("request")
public class PMS105 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS105.class);

	/**
	 * 明細資料查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS105InputVO inputVO = (PMS105InputVO) body;
		PMS105OutputVO return_VO = new PMS105OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			//==查詢==
			sql.append(" SELECT ROWNUM AS NUM,T.* FROM ( ");
			sql.append(" SELECT * from TBPMS_UNPLAN_RPT_DTL   ");
			sql.append(" WHERE 1=1   ");
			
			/**0002683:查詢條件 未選擇並非無限制**/
			//員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" AND AO_CODE = :id  ");
				queryCondition.setObject("id", inputVO.getAo_code());
			}else{
				sql.append(" AND AO_CODE IN (:id)  ");
				queryCondition.setObject("id", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST).toString());
			}
			//區域中心
			if (!StringUtils.isBlank(inputVO.getType()) && inputVO.getType().equals("0")) {
				sql.append("	AND (DETAIL_TYPE = :DETAIL_TYPEE OR ");
				sql.append("    DETAIL_TYPE = :DETAIL_TYPEE+1 ) ");
				queryCondition.setObject("DETAIL_TYPEE", inputVO.getType());
			}else if (!StringUtils.isBlank(inputVO.getType())){
				sql.append("	AND DETAIL_TYPE = :DETAIL_TYPEE ");
				queryCondition.setObject("DETAIL_TYPEE", inputVO.getType());
			}
			//年月
			if (!StringUtils.isBlank(inputVO.getReportDate())) {
				sql.append("  AND YEARMON= :YEMON  ");
				queryCondition.setObject("YEMON", inputVO.getReportDate());
			}
			sql.append("   ) T   ");
			queryCondition.setQueryString(sql.toString());
			//==查詢條件設定==
			// result 查詢結果
			ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			return_VO.setResultList(list);
			return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 跳頁頁次
			return_VO.setTotalPage(totalPage_i);// 總頁次
			return_VO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */

	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS105InputVO inputVO = (PMS105InputVO) body;
		PMS105OutputVO outputVO = new PMS105OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			//==查詢==CUST_CNT
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			// 筆數限制
			Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
			
			
			// 取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(inputVO.getReportDate());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			//==查詢==
			sql.append(" SELECT ROWNUM AS NUM, T.* FROM ( ");
			sql.append(" SELECT * ");
			sql.append(" FROM TBPMS_UNPLAN_RPT_MAST ");
			sql.append(" WHERE 1=1  ");
			
			/**0002683:查詢條件 未選擇並非無限制**/
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
				condition.setObject("REGION_CENTER_IDDD","%" + inputVO.getRegion_center_id() + "%");
			}else{
				sql.append(" and REGION_CENTER_ID IN (:REGION_CENTER_IDDD)");
				condition.setObject("REGION_CENTER_IDDD", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
				condition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else{
				sql.append(" and BRANCH_AREA_ID IN (:OP_AREA_IDDD)");
				condition.setObject("OP_AREA_IDDD", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
			
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr() + "%");
			}else{
				sql.append(" and BRANCH_NBR IN (:BRANCH_NBRR)");
				condition.setObject("BRANCH_NBRR", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			
			// 理專ao_code
			if (!StringUtils.isBlank(inputVO.getAo_code())) {				
				sql.append(" AND AO_CODE like :AO_CODEE ");
				condition.setObject("AO_CODEE","%" + inputVO.getAo_code()+ "%");				
								
			}else {
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and AO_CODE IN (:AO_CODEE) ");
					condition.setObject("AO_CODEE", pms000outputVO.getV_aoList());
				}
			}
			
			//年月
			if (!StringUtils.isBlank(inputVO.getReportDate())) {
				sql.append(" and YEARMON LIKE :YEARMONN");
				condition.setObject("YEARMONN", "%" + inputVO.getReportDate() + "%");
			}
			sql.append("   ORDER BY YEARMON,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR ) T   ");
			condition.setQueryString(sql.toString());
			//==查詢條件設定==
			
			//查詢結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			//查詢結果CSV 全部
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(csvList);
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
	 * 主查詢  總計
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */

	public void inquire2(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS105InputVO inputVO = (PMS105InputVO) body;
		PMS105OutputVO outputVO = new PMS105OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			//==查詢==CUST_CNT
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			// 筆數限制
			Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
			
			
			// 取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(inputVO.getReportDate());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
			sql.append(" SELECT T.* FROM ( ");
			sql.append(" SELECT SUM(CUST_CNT) AS CUST_CNT, ");
			sql.append(" SUM(CUST_CNT_RECORDED) AS CUST_CNT_RECORDED, ");
			sql.append(" SUM(CUST_CNT_UNPLAN) AS CUST_CNT_UNPLAN, ");
			sql.append(" SUM(CUST_CNT_CONTACTED) AS CUST_CNT_CONTACTED, ");
			sql.append(" SUM(CUST_CNT_NOT_CONTACT) AS CUST_CNT_NOT_CONTACT ");
			sql.append(" FROM TBPMS_UNPLAN_RPT_MAST ");
			sql.append(" WHERE 1=1  ");
			
			/**0002683:查詢條件 未選擇並非無限制**/
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
				condition.setObject("REGION_CENTER_IDDD","%" + inputVO.getRegion_center_id() + "%");
			}else{
				sql.append(" and REGION_CENTER_ID IN (:REGION_CENTER_IDDD)");
				condition.setObject("REGION_CENTER_IDDD", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
				condition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else{
				sql.append(" and BRANCH_AREA_ID IN (:OP_AREA_IDDD)");
				condition.setObject("OP_AREA_IDDD", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
			
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr() + "%");
			}else{
				sql.append(" and BRANCH_NBR IN (:BRANCH_NBRR)");
				condition.setObject("BRANCH_NBRR", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			// 理專ao_code
			if (!StringUtils.isBlank(inputVO.getAo_code())) {				
				sql.append(" AND AO_CODE like :AO_CODEE ");
				condition.setObject("AO_CODEE","%" + inputVO.getAo_code()+ "%");				
					
			}else {
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and AO_CODE IN (:AO_CODEE) ");
					condition.setObject("AO_CODEE", pms000outputVO.getV_aoList());
				}
			}
			//年月
			if (!StringUtils.isBlank(inputVO.getReportDate())) {
				sql.append(" and YEARMON LIKE :YEARMONN");
				condition.setObject("YEARMONN", "%" + inputVO.getReportDate() + "%");
			}
			
			sql.append("    ORDER BY YEARMON,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR ) T  ");
			condition.setQueryString(sql.toString());
			//==查詢條件設定==
			//查詢結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			//查詢結果CSV 全部
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList2(list); // data
			outputVO.setCsvList(csvList);
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

}
package com.systex.jbranch.app.server.fps.pms336;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.util.Calendar;
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
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :輔銷目標達成報表Controller <br>
 * Comments Name : PMS336.java<br>
 * Author :Kevin<br>
 * Date :2016年06月23日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
@Component("pms336")
@Scope("request")
public class PMS336 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS336.class);

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS336InputVO inputVO = (PMS336InputVO) body;
		PMS336OutputVO outputVO = new PMS336OutputVO();
		dam = this.getDataAccessManager();
		try {

			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            StringBuffer sql = new StringBuffer();
			String roleType = "";
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); //理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); //OP
			Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);//個金主管
			Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運督導
			Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //區域中心主管
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			
		
			//取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(inputVO.getsTime());
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			
			// ==主查詢==
			sql.append("SELECT ROWNUM as num, A.* FROM TBPMS_SALES_SPRT_GOAL A   "); 
			sql.append(" WHERE 1=1  ");
			//權限
			if(roleID.equals("FA")||roleID.equals("FA9")||roleID.equals("B003"))
			{
			sql.append("and SUPT_SALES_TEAM_ID= :sal ");
			condition.setObject("sal", "FA");	
			}else if(roleID.equals("IA")||roleID.equals("IA9")||roleID.equals("B010")||roleID.equals("B009"))
			{
				sql.append("and SUPT_SALES_TEAM_ID= :sal ");
				condition.setObject("sal", "IA");
			}
			
			// 年月
			if (StringUtils.isNotBlank(inputVO.getsTime())) {
				sql.append("and YEARMON = :YEARMONN ");
				condition.setObject("YEARMONN", inputVO.getsTime());
			}
			
			// 員編
			if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				sql.append("and EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}
			
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("and BRANCH_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					if(pms000outputVO.getBranchList()!=null){
						sql.append("and BRANCH_NBR IN (:branch_nbr) ");
						condition.setObject("branch_nbr", pms000outputVO.getBranchList());
					}
				}
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("and BRANCH_AREA_ID = :branch_area_id ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("and BRANCH_AREA_ID = :branch_area_id ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)&&(roleID!="A164")) {
					if(pms000outputVO.getArea_branchList()!=null){
						sql.append("and BRANCH_AREA_ID IN (:branch_area_id) ");
						condition.setObject("branch_area_id", pms000outputVO.getArea_branchList());
					}
				}
			}
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("and REGION_CENTER_ID = :region_center_id ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)&&(roleID!="A164")) {
					if(pms000outputVO.getRegionList()!=null){
						sql.append("and REGION_CENTER_ID IN (:region_center_id) ");
						condition.setObject("region_center_id", pms000outputVO.getRegionList());
					}
				}
			}
			
			sql.append("Order by REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR ");
			// 設定字串
			condition.setQueryString(sql.toString());
			
			//分頁查詢結果
			ResultIF list1 = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			if(!list1.isEmpty()){
				int totalPage_i = list1.getTotalPage(); // 分頁用
				int totalRecord_i = list1.getTotalRecord(); // 分頁用
				outputVO.setResultList(list1); // data
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				outputVO.setTotalPage(totalPage_i);// 總頁次
				outputVO.setTotalRecord(totalRecord_i);// 總筆數
				outputVO.setResultList(list1); // data分頁list	
			}else{
				outputVO.setResultList(new ArrayList()); // data
			}
			
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

}

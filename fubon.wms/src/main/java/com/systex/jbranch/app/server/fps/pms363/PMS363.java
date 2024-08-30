package com.systex.jbranch.app.server.fps.pms363;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("pms363")
@Scope("request")
public class PMS363 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS363InputVO inputVO = (PMS363InputVO) body;
		PMS363OutputVO outputVO = new PMS363OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		StringBuffer sql = new StringBuffer();
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		try{
			
			if("1".equals(inputVO.getReportType())){			
				sql.append(" SELECT * FROM TBPMS_363_EIP_DOWNGRADE ");
			}else{
				sql.append(" SELECT * FROM TBPMS_363_OS_UPGRADE ");
			}
			
			
			sql.append("  WHERE 1 = 1 AND YEARMON = :yearmon ");
			//如果員編有值，查詢條件只取員編；如果員編空白，分行有值，查詢條件只取分行
			//員編
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" and EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}else{
				// 分行
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
					sql.append(" and BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}else{
				//登入非總行人員強制加分行
					if(!headmgrMap.containsKey(roleID)) {
						sql.append(" and BRANCH_NBR IN (:branch_nbr) ");
						condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
					}
					// 區域中心
					if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
						sql.append(" and REGION_CENTER_ID = :region_center_id ");
						condition.setObject("region_center_id", inputVO.getRegion_center_id());
					}else{
					//登入非總行人員強制加區域中心
						if(!headmgrMap.containsKey(roleID)) {
							sql.append(" and REGION_CENTER_ID IN (:region_center_id) ");
							condition.setObject("region_center_id", pms000outputVO.getV_regionList());
						}
					}					
					// 營運區
					if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
						sql.append(" and BRANCH_AREA_ID = :branch_area_id ");
						condition.setObject("branch_area_id", inputVO.getBranch_area_id());
					}else{
					//登入非總行人員強制加營運區
						if(!headmgrMap.containsKey(roleID)) {
							sql.append(" and BRANCH_AREA_ID IN (:branch_area_id) ");
							condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
						}
					}
				}
			}
							
			if (StringUtils.isNotBlank(inputVO.getReportDate())) {
				condition.setObject("yearmon", inputVO.getReportDate());
			}
			
			sql.append(" order by REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
			condition.setQueryString(sql.toString());
			outputVO.setResultList(dam.exeQuery(condition));
	
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
}
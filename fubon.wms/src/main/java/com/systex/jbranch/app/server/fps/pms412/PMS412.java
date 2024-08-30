package com.systex.jbranch.app.server.fps.pms412;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.defaultString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 系統權限查詢報表 
 * creater : 2016/05/17 Frank 
 * modefier: 2017/01/26 Kevin 
 */

@Component("pms412")
@Scope("request")
public class PMS412 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS412InputVO inputVO = (PMS412InputVO) body;
		PMS412OutputVO outputVO = new PMS412OutputVO();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		
		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROWNUM, t.* ");
		sql.append("FROM ( ");
		sql.append("  SELECT TO_CHAR(EMP.CREATETIME, 'YYYY/MM/DD') AS DATA_DATE, ");
		sql.append("         EMP.REGION_CENTER_ID, EMP.REGION_CENTER_NAME, EMP.BRANCH_AREA_NAME, ");
		sql.append("         EMP.BRANCH_AREA_ID,EMP.BRANCH_NBR, EMP.BRANCH_NAME, ");
		sql.append("         EMP.EMP_ID, EMP.EMP_NAME, ");
		sql.append("         EMP.ROLE_ID, RO.ROLE_NAME, ");
		sql.append("         TO_CHAR(EMP.LASTUPDATE, 'YYYY/MM/DD HH:MI:SS') AS CREATETIME ");
		sql.append("  FROM ( ");
		sql.append("    SELECT VWE.REGION_CENTER_ID, VWE.REGION_CENTER_NAME, VWE.BRANCH_AREA_ID, VWE.BRANCH_AREA_NAME, VWE.BRANCH_NBR, VWE.BRANCH_NAME, ");
		sql.append("           VWE.EMP_ID, VWE.EMP_NAME, VWE.ROLE_ID, SYSDATE AS CREATETIME, MAX(VWE.CREATETIME) AS LASTUPDATE ");
		sql.append("    FROM VWORG_EMP_INFO VWE ");
//		sql.append("    WHERE NOT EXISTS (SELECT UNA.EMP_ID FROM TBORG_MEMBER_UNAUTH UNA WHERE UNA.ISUNAUTHED = 'Y' AND VWE.EMP_ID = UNA.EMP_ID) ");
		sql.append("    GROUP BY VWE.REGION_CENTER_ID, VWE.REGION_CENTER_NAME, VWE.BRANCH_AREA_ID, VWE.BRANCH_AREA_NAME, VWE.BRANCH_NBR, VWE.BRANCH_NAME, ");
		sql.append("             VWE.EMP_ID, VWE.EMP_NAME, VWE.ROLE_ID, SYSDATE ");
		sql.append("  ) EMP ");
		sql.append("  LEFT JOIN TBORG_ROLE RO ON RO.ROLE_ID = EMP.ROLE_ID ");
		sql.append("  WHERE 1 = 1 ");

		// 區域中心
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append("  AND EMP.REGION_CENTER_ID = :REGION_CENTER_ID ");
			condition.setObject("REGION_CENTER_ID", inputVO.getRegion_center_id());
		} else {
			// 登入非總行人員強制加區域中心
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("  AND EMP.REGION_CENTER_ID IN (:REGION_CENTER_IDDD) ");
				condition.setObject("REGION_CENTER_IDDD", pms000outputVO.getV_regionList());
			}
		}
		
		// 營運區
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append("  AND EMP.BRANCH_AREA_ID = :BRANCH_AREA_ID ");
			condition.setObject("BRANCH_AREA_ID", inputVO.getBranch_area_id());
		} else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("  AND EMP.BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
				condition.setObject("OP_AREA_IDDD", pms000outputVO.getV_areaList());
			}
		}
		
		// 分行
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql.append("  AND EMP.BRANCH_NBR = :BRANCH_NBR ");
			condition.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
		} else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("  AND EMP.BRANCH_NBR IN (:BRANCH_NBRR) ");
				condition.setObject("BRANCH_NBRR", pms000outputVO.getV_branchList());
			}
		}
		
		// 員編
		if (!StringUtils.isBlank(inputVO.getEmp_id())) {
			sql.append("  AND EMP.EMP_ID = :EMP_ID ");
			condition.setObject("EMP_ID", inputVO.getEmp_id());
		} else {
			// 登入為銷售人員強制加員編
			if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
				sql.append("  AND EMP.EMP_ID IN (:EMP_IDEE) ");
				condition.setObject("EMP_IDEE", pms000outputVO.getEmpList());
			}
		}
		
		sql.append("  ORDER BY EMP.REGION_CENTER_ID, EMP.BRANCH_NBR, EMP.EMP_ID");
		sql.append(") t ");
		
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		
		outputVO.setTotalList(dam.exeQuery(condition));
		
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());

			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	// 產出Excel
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS412OutputVO outputVO = (PMS412OutputVO) body;

		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "系統權限管理報表_" + sdf.format(new Date()) + "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
		
		String[] csvHeader = {"序號", "資料日期", "業務處", "區別", "分行代碼", "分行名稱", "員工編號", "員工姓名", "角色代碼", "角色名稱", "異動日期"};
		String[] csvMain   = {"ROWNUM", "DATA_DATE", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "EMP_ID", "EMP_NAME", "ROLE_ID", "ROLE_NAME", "CREATETIME"};
		
		List listCSV = new ArrayList();
		
		for (Map<String, Object> map : list) {
			String[] records = new String[csvHeader.length];
			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
					case "ROWNUM":
						records[i] = ((int) Double.parseDouble(checkIsNull(map, csvMain[i]).toString())) + "";
						break;
					default :
						records[i] = checkIsNull(map, csvMain[i]);
						break;
				}
			}
			
			for (int index = 0; index < records.length; index++)
				records[index] = format("=\"%s\"", records[index]);

			listCSV.add(records);
		}
		

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		
		notifyClientToDownloadFile(csv.generateCSV(), fileName);
	}

	// 檢查Map取出欄位是否為Null
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}
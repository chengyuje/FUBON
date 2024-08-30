package com.systex.jbranch.app.server.fps.pms900;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2021/01/15
 * 
 */
@Component("pms900")
@Scope("request")
public class PMS900 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	public void getCharData(Object body, IPrimitiveMap header) throws JBranchException {
		PMS900InputVO inputVO = (PMS900InputVO) body;
		PMS900OutputVO outputVO = new PMS900OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT YEARMON, LINE_NAME, SUM(CONTENT) CONTENT FROM( ");
		sb.append(" SELECT ");
		sb.append("     SUBSTR(D.DATA_YEARMON, 0, 4) || '/' || SUBSTR(D.DATA_YEARMON, 5, 2) AS YEARMON, ");
		sb.append("     R.SEQ, ");
		sb.append("     R.COL_SEQ || '-' || H.COL_NAME AS LINE_NAME, ");
		sb.append("     TO_NUMBER(NVL(REGEXP_REPLACE(R.CONTENT,'[^0-9]', ''), '0'), '999999999990.00') AS CONTENT ");
		sb.append(" FROM TBPMS_DYNAMIC_RPT_DTL_REC R ");
		sb.append(" LEFT JOIN TBPMS_DYNAMIC_RPT_DTL D ON R.SEQ = D.SEQ AND R.ROW_SEQ = D.ROW_SEQ ");
		sb.append(" LEFT JOIN TBPMS_DYNAMIC_RPT_HEADER H ON R.SEQ = H.SEQ AND R.COL_SEQ = H.COL_SEQ ");
		sb.append(" WHERE R.SEQ = 8340 AND R.COL_SEQ IN (10, 11, 15) ");
		
		if (inputVO.getsDate() != null) {
			sb.append("AND D.DATA_YEARMON >= TO_CHAR( :sDate , 'YYYYMMDD') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("AND D.DATA_YEARMON <= TO_CHAR( :eDate , 'YYYYMMDD') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append(" AND D.BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) { 
			sb.append(" AND D.BRANCH_AREA_ID = :branch_area_id ");
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sb.append(" AND D.REGION_CENTER_ID = :region_center_id ");
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		}
		sb.append(" ) WHERE 1 = 1 ");
		
		sb.append(" GROUP BY YEARMON, LINE_NAME ");
		sb.append(" ORDER BY YEARMON, LINE_NAME ");
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		Set<String> line_name_set = new HashSet<>();
		for (Map<String,Object> map : resultList) {
			if (map.get("LINE_NAME") != null)
				line_name_set.add(map.get("LINE_NAME").toString());
		}
		if (line_name_set.size() > 0) {
			String[] line_name_array = line_name_set.toArray(new String[line_name_set.size()]);
			outputVO.setLine_name_array(line_name_array);				
		}
		
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
}
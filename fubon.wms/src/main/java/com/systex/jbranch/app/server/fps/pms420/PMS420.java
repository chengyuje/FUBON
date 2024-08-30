package com.systex.jbranch.app.server.fps.pms420;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("pms420")
@Scope("request")
public class PMS420 extends FubonWmsBizLogic {

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS420InputVO inputVO = (PMS420InputVO) body;
		PMS420OutputVO outputVO = new PMS420OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		
		try{			
			
			sql.append(" SELECT * ");
			sql.append(" FROM TBPMS_10CMDT_EBILL_PICK ");
			sql.append(" WHERE 1=1 ");
			
			if(StringUtils.isNotBlank(inputVO.getCre_Date())){
				sql.append(" and DATA_YM = :DATA_YM ");
				condition.setObject("DATA_YM", inputVO.getCre_Date());
			}  
			if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
				//sql.append(" and REGION_CENTER_ID = :region_id  ");
				sql.append("  and BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID = :region_id ");
				sql.append("  ) ");
				condition.setObject("region_id", inputVO.getRegion_center_id());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
				//sql.append(" and BRANCH_AREA_ID = :area_id  ");
				sql.append("  and BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID = :area_id ");
				sql.append("  ) ");
				condition.setObject("area_id", inputVO.getBranch_area_id());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
				sql.append(" and BRANCH_NBR = :branch_nbr ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			} 
			if(StringUtils.isNotBlank(inputVO.getAo_code())){
				sql.append(" and AO_CODE = :AO_CODE ");
				condition.setObject("AO_CODE", inputVO.getAo_code());
			}
			
			sql.append("order by REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(condition);
			
			outputVO.setResultList(list);
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void initQuery(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS420OutputVO outputVO = new PMS420OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT COUNT(1) AS CNT FROM TBPMS_10CMDT_EBILL_BTHLOG WHERE TXN_ID = 'PABTH_BTPMS420' AND DATA_YM = TO_CHAR(ADD_MONTHS(SYSDATE,-1), 'YYYYMM') ");
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
			
		int cnt = 0;
		try {
			if(CollectionUtils.isNotEmpty(list)) {
				cnt = Integer.parseInt(list.get(0).get("CNT").toString());
			}
		} catch(Exception e) {}
		
		outputVO.setCountYN(CollectionUtils.isNotEmpty(list) && cnt > 0 ? "Y" : "N");
		sendRtnObject(outputVO);

	}
	
}
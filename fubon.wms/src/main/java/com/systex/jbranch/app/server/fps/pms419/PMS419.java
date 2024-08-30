package com.systex.jbranch.app.server.fps.pms419;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :電子化通路交易(基金申購、贖回、轉換)通知理專機制<br>
 * Comments Name : PMS419java<br>
 */

@Component("pms419")
@Scope("request")
public class PMS419 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS419InputVO inputVO = (PMS419InputVO) body;
		PMS419OutputVO outputVO = new PMS419OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		
		try{			
			
			sql.append(" SELECT * FROM VWPMS_NFBH86WK WHERE TRAN_TYPE = '01' ");   //單筆
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" and EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}else{
				if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
					sql.append(" and BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}else if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
					sql.append(" and BRANCH_AREA_ID = :area_id  ");
					condition.setObject("area_id", inputVO.getBranch_area_id());
				}else if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
					sql.append(" and REGION_CENTER_ID = :region_id  ");
					condition.setObject("region_id", inputVO.getRegion_center_id());
				}
			}
			
			sql.append("order by BRANCH_NBR, EMP_ID ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list_1 = dam.exeQuery(condition);
			
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" SELECT * FROM VWPMS_NFBH86WK WHERE TRAN_TYPE = '02' ");   //定期(不)定額
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" and EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}else{
				if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
					sql.append(" and BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}else if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
					sql.append(" and BRANCH_AREA_ID = :area_id  ");
					condition.setObject("area_id", inputVO.getBranch_area_id());
				}else if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
					sql.append(" and REGION_CENTER_ID = :region_id  ");
					condition.setObject("region_id", inputVO.getRegion_center_id());
				}
			}
			sql.append("order by BRANCH_NBR, EMP_ID ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list_2 = dam.exeQuery(condition);
			
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" SELECT * FROM VWPMS_NFBH86WK WHERE TRAN_TYPE = '03' ");   // 轉換
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" and EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}else{
				if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
					sql.append(" and BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}else if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
					sql.append(" and BRANCH_AREA_ID = :area_id  ");
					condition.setObject("area_id", inputVO.getBranch_area_id());
				}else if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
					sql.append(" and REGION_CENTER_ID = :region_id  ");
					condition.setObject("region_id", inputVO.getRegion_center_id());
				}
			}
			sql.append("order by BRANCH_NBR, EMP_ID ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list_3 = dam.exeQuery(condition);
			
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" SELECT * FROM VWPMS_NFBH86WK WHERE TRAN_TYPE = '04' ");    //贖回
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" and EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}else{
				if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
					sql.append(" and BRANCH_NBR = :branch_nbr ");
					condition.setObject("branch_nbr", inputVO.getBranch_nbr());
				}else if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
					sql.append(" and BRANCH_AREA_ID = :area_id  ");
					condition.setObject("area_id", inputVO.getBranch_area_id());
				}else if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
					sql.append(" and REGION_CENTER_ID = :region_id  ");
					condition.setObject("region_id", inputVO.getRegion_center_id());
				}
			}
			sql.append("order by BRANCH_NBR, EMP_ID ");
			condition.setQueryString(sql.toString());
			List<Map<String, Object>> list_4 = dam.exeQuery(condition);
			
			outputVO.setResultList_1(list_1);
			outputVO.setResultList_2(list_2);
			outputVO.setResultList_3(list_3);
			outputVO.setResultList_4(list_4);
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	
	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	private String checkIsNull2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "1";
		}
	}


	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}

	// 達成率格式
	private String pcntFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return (int) (Float.parseFloat(map.get(key) + "") + 0.5) + "%";
		} else
			return "";
	}
}
package com.systex.jbranch.app.server.fps.pms312;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :房信貸案件來源統計表 Controller <br>
 * Comments Name : PMS312.java<br>
 * Author :Kevin<br>
 * Date :2016年08月02日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */

@Component("pms312")
@Scope("request")
public class PMS312 extends FubonWmsBizLogic {

//	public DataAccessManager dam = null;

	SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 主查詢
	 * 
	 * @return
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 輸入vo
		PMS312InputVO inputVO = (PMS312InputVO) body;
		// 輸出 vo
		PMS312OutputVO outputVO = new PMS312OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// ===取得理專員額表
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		try {
			
			// ==預先兜資料，主查詢(前段)==
			sb.append("WITH ");
			sb.append("ORIGINAL_VIEW AS ( ");
			sb.append("  SELECT  ");
			sb.append("    S_TYPE,YEARMON,REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,BRANCH_CLS, ");
			sb.append("    NVL(M_OP_CNT,0) AS M_OP_CNT,NVL(M_OP_AMT,0) AS M_OP_AMT,NVL(M_AO_CNT,0) AS M_AO_CNT,NVL(M_AO_AMT,0) AS M_AO_AMT,NVL(M_BO_CNT,0) AS M_BO_CNT,NVL(M_BO_AMT,0) AS M_BO_AMT,NVL(M_BT_CNT,0) AS M_BT_CNT,NVL(M_BT_AMT,0) AS M_BT_AMT,NVL(M_FG_CNT,0) AS M_FG_CNT,NVL(M_FG_AMT,0) AS M_FG_AMT,NVL(M_LI_CNT,0) AS M_LI_CNT,NVL(M_LI_AMT,0) AS M_LI_AMT,NVL(M_S_CNT,0) AS M_S_CNT,NVL(M_S_AMT,0) AS M_S_AMT,NVL(M_PC_CNT,0) AS M_PC_CNT,NVL(M_PC_AMT,0) AS M_PC_AMT,NVL(M_OT_CNT,0) AS M_OT_CNT,NVL(M_OT_AMT,0) AS M_OT_AMT,NVL(M_PS_CNT,0) AS M_PS_CNT,NVL(M_PS_AMT,0) AS M_PS_AMT,NVL(M_NBT_CNT,0) AS M_NBT_CNT,NVL(M_NBT_AMT,0) AS M_NBT_AMT,NVL(M_TT_CNT,0) AS M_TT_CNT,NVL(M_TT_AMT,0) AS M_TT_AMT, ");
			sb.append("    NVL(C_OP_CNT,0) AS C_OP_CNT,NVL(C_OP_AMT,0) AS C_OP_AMT,NVL(C_AO_CNT,0) AS C_AO_CNT,NVL(C_AO_AMT,0) AS C_AO_AMT,NVL(C_BO_CNT,0) AS C_BO_CNT,NVL(C_BO_AMT,0) AS C_BO_AMT,NVL(C_BT_CNT,0) AS C_BT_CNT,NVL(C_BT_AMT,0) AS C_BT_AMT,NVL(C_FG_CNT,0) AS C_FG_CNT,NVL(C_FG_AMT,0) AS C_FG_AMT,NVL(C_LI_CNT,0) AS C_LI_CNT,NVL(C_LI_AMT,0) AS C_LI_AMT,NVL(C_S_CNT,0) AS C_S_CNT,NVL(C_S_AMT,0) AS C_S_AMT,NVL(C_PC_CNT,0) AS C_PC_CNT,NVL(C_PC_AMT,0) AS C_PC_AMT,NVL(C_OT_CNT,0) AS C_OT_CNT,NVL(C_OT_AMT,0) AS C_OT_AMT,NVL(C_PS_CNT,0) AS C_PS_CNT,NVL(C_PS_AMT,0) AS C_PS_AMT,NVL(C_NBT_CNT,0) AS C_NBT_CNT,NVL(C_NBT_AMT,0) AS C_NBT_AMT,NVL(C_TT_CNT,0) AS C_TT_CNT,NVL(C_TT_AMT,0) AS C_TT_AMT ");
			sb.append("  FROM TBPMS_MRTG_CREDIT ");
			sb.append("  where 1=1 ");
			
			// where
			ResultIF list2 = null;
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sb.append(" and REGION_CENTER_ID = :REGION_CENTER_IDDD");
				if (StringUtils.isBlank(inputVO.getBranch_area_id())
						&& StringUtils.isBlank(inputVO.getBranch_nbr())) {
					if (StringUtils.equals(inputVO.getType2().toString(), "2")) {
						QueryConditionIF queryCondition2 = dam
								.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						//區域中心佔比計算用
						sb2.append("  SELECT");
						sb2.append("  SUM(M_OP_CNT) AS M_OP_CNT,");
						sb2.append("  SUM(M_OP_AMT) AS M_OP_AMT,");
						sb2.append("  SUM(M_AO_CNT) AS M_AO_CNT,");
						sb2.append("  SUM(M_AO_AMT) AS M_AO_AMT,");
						sb2.append("  SUM(M_BO_CNT) AS M_BO_CNT,");
						sb2.append("  SUM(M_BO_AMT) AS M_BO_AMT,");
						sb2.append("  SUM(M_BT_CNT) AS M_BT_CNT,");
						sb2.append("  SUM(M_BT_AMT) AS M_BT_AMT,");
						sb2.append("  SUM(M_FG_CNT) AS M_FG_CNT,");
						sb2.append("  SUM(M_FG_AMT) AS M_FG_AMT,");
						sb2.append("  SUM(M_LI_CNT) AS M_LI_CNT,");
						sb2.append("  SUM(M_LI_AMT) AS M_LI_AMT,");
						sb2.append("  SUM(M_S_CNT) AS M_S_CNT,  ");
						sb2.append("  SUM(M_S_AMT) AS M_S_AMT,  ");
						sb2.append("  SUM(M_PC_CNT) AS M_PC_CNT,");
						sb2.append("  SUM(M_PC_AMT) AS M_PC_AMT,");
						sb2.append("  SUM(M_OT_CNT) AS M_OT_CNT,");
						sb2.append("  SUM(M_OT_AMT) AS M_OT_AMT,");
						sb2.append("  SUM(M_PS_CNT) AS M_PS_CNT,");
						sb2.append("  SUM(M_PS_AMT) AS M_PS_AMT,");
						sb2.append("  SUM(M_NBT_CNT) AS M_NBT_CNT,  ");
						sb2.append("  SUM(M_NBT_AMT) AS M_NBT_AMT,  ");
						sb2.append("  SUM(M_TT_CNT) AS M_TT_CNT,");
						sb2.append("  SUM(M_TT_AMT) AS M_TT_AMT,");
						sb2.append("  SUM(C_OP_CNT) AS C_OP_CNT,");
						sb2.append("  SUM(C_OP_AMT) AS C_OP_AMT,");
						sb2.append("  SUM(C_AO_CNT) AS C_AO_CNT,");
						sb2.append("  SUM(C_AO_AMT) AS C_AO_AMT,");
						sb2.append("  SUM(C_BO_CNT) AS C_BO_CNT,");
						sb2.append("  SUM(C_BO_AMT) AS C_BO_AMT,");
						sb2.append("  SUM(C_BT_CNT) AS C_BT_CNT,");
						sb2.append("  SUM(C_BT_AMT) AS C_BT_AMT,");
						sb2.append("  SUM(C_FG_CNT) AS C_FG_CNT,");
						sb2.append("  SUM(C_FG_AMT) AS C_FG_AMT,");
						sb2.append("  SUM(C_LI_CNT) AS C_LI_CNT,");
						sb2.append("  SUM(C_LI_AMT) AS C_LI_AMT,");
						sb2.append("  SUM(C_S_CNT) AS C_S_CNT,  ");
						sb2.append("  SUM(C_S_AMT) AS C_S_AMT,  ");
						sb2.append("  SUM(C_PC_CNT) AS C_PC_CNT,");
						sb2.append("  SUM(C_PC_AMT) AS C_PC_AMT,");
						sb2.append("  SUM(C_OT_CNT) AS C_OT_CNT,");
						sb2.append("  SUM(C_OT_AMT) AS C_OT_AMT,");
						sb2.append("  SUM(C_PS_CNT) AS C_PS_CNT,");
						sb2.append("  SUM(C_PS_AMT) AS C_PS_AMT,");
						sb2.append("  SUM(C_NBT_CNT) AS C_NBT_CNT,  ");
						sb2.append("  SUM(C_NBT_AMT) AS C_NBT_AMT,  ");
						sb2.append("  SUM(C_TT_CNT)   AS C_TT_CNT,  ");
						sb2.append("  SUM(C_TT_AMT)   AS C_TT_AMT   ");
						sb2.append("  FROM TBPMS_MRTG_CREDIT");
						sb2.append("  WHERE YEARMON=:YEARMONN AND REGION_CENTER_ID=:REGION_CENTER_IDDD AND S_TYPE=:S_TYPEEE");
						queryCondition2.setQueryString(sb2.toString());
						queryCondition2.setObject("YEARMONN",inputVO.getsCreDate());
						queryCondition2.setObject("REGION_CENTER_IDDD", inputVO.getRegion_center_id());
						queryCondition2.setObject("S_TYPEEE", inputVO.getChecked());
						list2 = dam.executePaging(queryCondition2,inputVO.getCurrentPageIndex() + 1,inputVO.getPageCount());
					}
				}
			}
			//==主查詢條件
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sb.append(" and BRANCH_AREA_ID = :OP_AREA_IDDD");
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append(" and BRANCH_NBR = :BRANCH_NBRR");
			}
			//年月
			if (inputVO.getsCreDate() != null) {
				sb.append(" and YEARMON = :YEARMONN");
			}
			// S_TYPE
			if (!StringUtils.isBlank(inputVO.getChecked())) {
				sb.append(" and S_TYPE = :S_TYPEEE");

			}
			//員編
			if (!StringUtils.isBlank(inputVO.getEMP_ID())) {
				sb.append(" and EMP_ID = :EMP_IDEE");
			}
			
			// ==預先兜資料，主查詢(後段)==
			sb.append("), ");
			sb.append("BRANCH AS ( ");
			sb.append("  SELECT  ");
			sb.append("    'BRANCH' AS ALL_OF_BRANCH,S_TYPE,YEARMON,REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,BRANCH_CLS, ");
			sb.append("    SUM(M_OP_CNT) AS M_OP_CNT,SUM(M_OP_AMT) AS M_OP_AMT,SUM(M_AO_CNT) AS M_AO_CNT,SUM(M_AO_AMT) AS M_AO_AMT,SUM(M_BO_CNT) AS M_BO_CNT,SUM(M_BO_AMT) AS M_BO_AMT,SUM(M_BT_CNT) AS M_BT_CNT,SUM(M_BT_AMT) AS M_BT_AMT,SUM(M_FG_CNT) AS M_FG_CNT,SUM(M_FG_AMT) AS M_FG_AMT,SUM(M_LI_CNT) AS M_LI_CNT,SUM(M_LI_AMT) AS M_LI_AMT,SUM(M_S_CNT) AS M_S_CNT,SUM(M_S_AMT) AS M_S_AMT,SUM(M_PC_CNT) AS M_PC_CNT,SUM(M_PC_AMT) AS M_PC_AMT,SUM(M_OT_CNT) AS M_OT_CNT,SUM(M_OT_AMT) AS M_OT_AMT,SUM(M_PS_CNT) AS M_PS_CNT,SUM(M_PS_AMT) AS M_PS_AMT,SUM(M_NBT_CNT) AS M_NBT_CNT,SUM(M_NBT_AMT) AS M_NBT_AMT,SUM(M_TT_CNT) AS M_TT_CNT,SUM(M_TT_AMT) AS M_TT_AMT, ");
			sb.append("    SUM(C_OP_CNT) AS C_OP_CNT,SUM(C_OP_AMT) AS C_OP_AMT,SUM(C_AO_CNT) AS C_AO_CNT,SUM(C_AO_AMT) as C_AO_AMT,SUM(C_BO_CNT) AS C_BO_CNT,SUM(C_BO_AMT) AS C_BO_AMT,SUM(C_BT_CNT) AS C_BT_CNT,SUM(C_BT_AMT) AS C_BT_AMT,SUM(C_FG_CNT) AS C_FG_CNT,SUM(C_FG_AMT) AS C_FG_AMT,SUM(C_LI_CNT) AS C_LI_CNT,SUM(C_LI_AMT) AS C_LI_AMT,SUM(C_S_CNT) AS C_S_CNT,SUM(C_S_AMT) AS C_S_AMT,SUM(C_PC_CNT) AS C_PC_CNT,SUM(C_PC_AMT) as C_PC_AMT,SUM(C_OT_CNT) AS C_OT_CNT,SUM(C_OT_AMT) AS C_OT_AMT,SUM(C_PS_CNT) AS C_PS_CNT,SUM(C_PS_AMT) AS C_PS_AMT,SUM(C_NBT_CNT) AS C_NBT_CNT,SUM(C_NBT_AMT) AS C_NBT_AMT,SUM(C_TT_CNT) AS C_TT_CNT,SUM(C_TT_AMT) AS C_TT_AMT ");
			sb.append("  FROM ORIGINAL_VIEW ");
			sb.append("  GROUP BY S_TYPE,YEARMON,REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,BRANCH_CLS ");
			sb.append("), ");
			sb.append("AREA AS ( ");
			sb.append("  SELECT  ");
			sb.append("    'AREA' AS ALL_OF_BRANCH,'AREA' AS S_TYPE,'AREA' AS YEARMON,REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,'AREA' AS BRANCH_NBR,'AREA' AS BRANCH_NAME,'AREA' AS BRANCH_CLS, ");
			sb.append("    SUM(M_OP_CNT) AS M_OP_CNT,SUM(M_OP_AMT) AS M_OP_AMT,SUM(M_AO_CNT) AS M_AO_CNT,SUM(M_AO_AMT) AS M_AO_AMT,SUM(M_BO_CNT) AS M_BO_CNT,SUM(M_BO_AMT) AS M_BO_AMT,SUM(M_BT_CNT) AS M_BT_CNT,SUM(M_BT_AMT) AS M_BT_AMT,SUM(M_FG_CNT) AS M_FG_CNT,SUM(M_FG_AMT) AS M_FG_AMT,SUM(M_LI_CNT) AS M_LI_CNT,SUM(M_LI_AMT) AS M_LI_AMT,SUM(M_S_CNT) AS M_S_CNT,SUM(M_S_AMT) AS M_S_AMT,SUM(M_PC_CNT) AS M_PC_CNT,SUM(M_PC_AMT) AS M_PC_AMT,SUM(M_OT_CNT) AS M_OT_CNT,SUM(M_OT_AMT) AS M_OT_AMT,SUM(M_PS_CNT) AS M_PS_CNT,SUM(M_PS_AMT) AS M_PS_AMT,SUM(M_NBT_CNT) AS M_NBT_CNT,SUM(M_NBT_AMT) AS M_NBT_AMT,SUM(M_TT_CNT) AS M_TT_CNT,SUM(M_TT_AMT) AS M_TT_AMT, ");
			sb.append("    SUM(C_OP_CNT) AS C_OP_CNT,SUM(C_OP_AMT) AS C_OP_AMT,SUM(C_AO_CNT) AS C_AO_CNT,SUM(C_AO_AMT) as C_AO_AMT,SUM(C_BO_CNT) AS C_BO_CNT,SUM(C_BO_AMT) AS C_BO_AMT,SUM(C_BT_CNT) AS C_BT_CNT,SUM(C_BT_AMT) AS C_BT_AMT,SUM(C_FG_CNT) AS C_FG_CNT,SUM(C_FG_AMT) AS C_FG_AMT,SUM(C_LI_CNT) AS C_LI_CNT,SUM(C_LI_AMT) AS C_LI_AMT,SUM(C_S_CNT) AS C_S_CNT,SUM(C_S_AMT) AS C_S_AMT,SUM(C_PC_CNT) AS C_PC_CNT,SUM(C_PC_AMT) as C_PC_AMT,SUM(C_OT_CNT) AS C_OT_CNT,SUM(C_OT_AMT) AS C_OT_AMT,SUM(C_PS_CNT) AS C_PS_CNT,SUM(C_PS_AMT) AS C_PS_AMT,SUM(C_NBT_CNT) AS C_NBT_CNT,SUM(C_NBT_AMT) AS C_NBT_AMT,SUM(C_TT_CNT) AS C_TT_CNT,SUM(C_TT_AMT) AS C_TT_AMT ");
			sb.append("  FROM BRANCH ");
			sb.append("  GROUP BY REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME ");
			sb.append("), ");
			sb.append("REGION AS ( ");
			sb.append("  SELECT  ");
			sb.append("    'REGION' AS ALL_OF_BRANCH,'REGION' AS S_TYPE,'REGION' AS YEARMON,REGION_CENTER_ID,REGION_CENTER_NAME,'REGION' AS BRANCH_AREA_ID,'REGION' AS BRANCH_AREA_NAME,'REGION' AS BRANCH_NBR,'REGION' AS BRANCH_NAME,'REGION' AS BRANCH_CLS, ");
			sb.append("    SUM(M_OP_CNT) AS M_OP_CNT,SUM(M_OP_AMT) AS M_OP_AMT,SUM(M_AO_CNT) AS M_AO_CNT,SUM(M_AO_AMT) AS M_AO_AMT,SUM(M_BO_CNT) AS M_BO_CNT,SUM(M_BO_AMT) AS M_BO_AMT,SUM(M_BT_CNT) AS M_BT_CNT,SUM(M_BT_AMT) AS M_BT_AMT,SUM(M_FG_CNT) AS M_FG_CNT,SUM(M_FG_AMT) AS M_FG_AMT,SUM(M_LI_CNT) AS M_LI_CNT,SUM(M_LI_AMT) AS M_LI_AMT,SUM(M_S_CNT) AS M_S_CNT,SUM(M_S_AMT) AS M_S_AMT,SUM(M_PC_CNT) AS M_PC_CNT,SUM(M_PC_AMT) AS M_PC_AMT,SUM(M_OT_CNT) AS M_OT_CNT,SUM(M_OT_AMT) AS M_OT_AMT,SUM(M_PS_CNT) AS M_PS_CNT,SUM(M_PS_AMT) AS M_PS_AMT,SUM(M_NBT_CNT) AS M_NBT_CNT,SUM(M_NBT_AMT) AS M_NBT_AMT,SUM(M_TT_CNT) AS M_TT_CNT,SUM(M_TT_AMT) AS M_TT_AMT, ");
			sb.append("    SUM(C_OP_CNT) AS C_OP_CNT,SUM(C_OP_AMT) AS C_OP_AMT,SUM(C_AO_CNT) AS C_AO_CNT,SUM(C_AO_AMT) as C_AO_AMT,SUM(C_BO_CNT) AS C_BO_CNT,SUM(C_BO_AMT) AS C_BO_AMT,SUM(C_BT_CNT) AS C_BT_CNT,SUM(C_BT_AMT) AS C_BT_AMT,SUM(C_FG_CNT) AS C_FG_CNT,SUM(C_FG_AMT) AS C_FG_AMT,SUM(C_LI_CNT) AS C_LI_CNT,SUM(C_LI_AMT) AS C_LI_AMT,SUM(C_S_CNT) AS C_S_CNT,SUM(C_S_AMT) AS C_S_AMT,SUM(C_PC_CNT) AS C_PC_CNT,SUM(C_PC_AMT) as C_PC_AMT,SUM(C_OT_CNT) AS C_OT_CNT,SUM(C_OT_AMT) AS C_OT_AMT,SUM(C_PS_CNT) AS C_PS_CNT,SUM(C_PS_AMT) AS C_PS_AMT,SUM(C_NBT_CNT) AS C_NBT_CNT,SUM(C_NBT_AMT) AS C_NBT_AMT,SUM(C_TT_CNT) AS C_TT_CNT,SUM(C_TT_AMT) AS C_TT_AMT ");
			sb.append("  FROM AREA ");
			sb.append("  GROUP BY REGION_CENTER_ID,REGION_CENTER_NAME ");
			sb.append("), ");			
			sb.append("allOfBranch AS ( ");
			sb.append("  SELECT  ");
			sb.append("    'ALL' AS ALL_OF_BRANCH,'ALL' AS S_TYPE,'ALL' AS YEARMON,'ALL' AS REGION_CENTER_ID,'ALL' AS REGION_CENTER_NAME,'ALL' AS BRANCH_AREA_ID,'ALL' AS BRANCH_AREA_NAME,'ALL' AS BRANCH_NBR,'ALL' AS BRANCH_NAME,'ALL' AS BRANCH_CLS, ");
			sb.append("    SUM(M_OP_CNT) AS M_OP_CNT,SUM(M_OP_AMT) AS M_OP_AMT,SUM(M_AO_CNT) AS M_AO_CNT,SUM(M_AO_AMT) AS M_AO_AMT,SUM(M_BO_CNT) AS M_BO_CNT,SUM(M_BO_AMT) AS M_BO_AMT,SUM(M_BT_CNT) AS M_BT_CNT,SUM(M_BT_AMT) AS M_BT_AMT,SUM(M_FG_CNT) AS M_FG_CNT,SUM(M_FG_AMT) AS M_FG_AMT,SUM(M_LI_CNT) AS M_LI_CNT,SUM(M_LI_AMT) AS M_LI_AMT,SUM(M_S_CNT) AS M_S_CNT,SUM(M_S_AMT) AS M_S_AMT,SUM(M_PC_CNT) AS M_PC_CNT,SUM(M_PC_AMT) AS M_PC_AMT,SUM(M_OT_CNT) AS M_OT_CNT,SUM(M_OT_AMT) AS M_OT_AMT,SUM(M_PS_CNT) AS M_PS_CNT,SUM(M_PS_AMT) AS M_PS_AMT,SUM(M_NBT_CNT) AS M_NBT_CNT,SUM(M_NBT_AMT) AS M_NBT_AMT,SUM(M_TT_CNT) AS M_TT_CNT,SUM(M_TT_AMT) AS M_TT_AMT, ");
			sb.append("    SUM(C_OP_CNT) AS C_OP_CNT,SUM(C_OP_AMT) AS C_OP_AMT,SUM(C_AO_CNT) AS C_AO_CNT,SUM(C_AO_AMT) as C_AO_AMT,SUM(C_BO_CNT) AS C_BO_CNT,SUM(C_BO_AMT) AS C_BO_AMT,SUM(C_BT_CNT) AS C_BT_CNT,SUM(C_BT_AMT) AS C_BT_AMT,SUM(C_FG_CNT) AS C_FG_CNT,SUM(C_FG_AMT) AS C_FG_AMT,SUM(C_LI_CNT) AS C_LI_CNT,SUM(C_LI_AMT) AS C_LI_AMT,SUM(C_S_CNT) AS C_S_CNT,SUM(C_S_AMT) AS C_S_AMT,SUM(C_PC_CNT) AS C_PC_CNT,SUM(C_PC_AMT) as C_PC_AMT,SUM(C_OT_CNT) AS C_OT_CNT,SUM(C_OT_AMT) AS C_OT_AMT,SUM(C_PS_CNT) AS C_PS_CNT,SUM(C_PS_AMT) AS C_PS_AMT,SUM(C_NBT_CNT) AS C_NBT_CNT,SUM(C_NBT_AMT) AS C_NBT_AMT,SUM(C_TT_CNT) AS C_TT_CNT,SUM(C_TT_AMT) AS C_TT_AMT ");
			sb.append("  FROM BRANCH ");
			sb.append("  GROUP BY ALL_OF_BRANCH ");
			sb.append("), ");			
			sb.append("BRANCH_AREA AS (  ");
			sb.append("  SELECT *   ");
			sb.append("  FROM BRANCH  ");
			sb.append("  UNION ALL  ");
			sb.append("  SELECT *   ");
			sb.append("  FROM AREA ");   
			sb.append("),   ");
			sb.append("BRANCH_AREA_REGION AS (  ");
			sb.append("  SELECT * ");
			sb.append("  FROM ( ");
			sb.append("    SELECT *  ");
			sb.append("    FROM BRANCH_AREA  ");
			sb.append("    UNION ALL  ");
			sb.append("    SELECT *  ");
			sb.append("    FROM REGION  ");
			sb.append("  ) ");
			sb.append("  ORDER BY ");
			sb.append("  REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, YEARMON ");
			sb.append("), ");
			sb.append("BRANCH_AREA_REGION_allOfBranch AS ( ");
			sb.append("  SELECT * ");
			sb.append("  FROM BRANCH_AREA_REGION ");
			sb.append("  UNION ALL ");
			sb.append("  SELECT * ");
			sb.append("  FROM allOfBranch ");
			sb.append(") ");

			// 依據條件顯示適當的合計
			if (StringUtils.isNotBlank(inputVO.getAo_code()) &&
				StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
				StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
				StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("SELECT * from ORIGINAL_VIEW ");
			} else if (!StringUtils.isNotBlank(inputVO.getAo_code()) &&
					StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
					StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("SELECT * from BRANCH ");
			} else if (!StringUtils.isNotBlank(inputVO.getAo_code()) &&
					!StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
					StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("SELECT * from BRANCH_AREA ");
			} else if (!StringUtils.isNotBlank(inputVO.getAo_code()) &&
					!StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					!StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
					StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("SELECT * from BRANCH_AREA_REGION ");
			} else if (!StringUtils.isNotBlank(inputVO.getAo_code()) &&
					!StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					!StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
					!StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("SELECT * from BRANCH_AREA_REGION_allOfBranch ");
			}			
			queryCondition.setQueryString(sb.toString());
			
			//==主查詢條件設定
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				queryCondition.setObject("REGION_CENTER_IDDD",inputVO.getRegion_center_id());
			}
			// S_TYPE
			if (!StringUtils.isBlank(inputVO.getChecked())) {
				queryCondition.setObject("S_TYPEEE",inputVO.getChecked());
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				queryCondition.setObject("OP_AREA_IDDD",inputVO.getBranch_area_id());
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				queryCondition.setObject("BRANCH_NBRR",inputVO.getBranch_nbr());
			}
			//年月
			if (inputVO.getsCreDate() != null) {
				queryCondition.setObject("YEARMONN", inputVO.getsCreDate());
			}
			
			
			//分頁查詢結果
			ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			if (list.size() > 0){
				outputVO.setResultList(list);
				outputVO.setTotalPage(list.getTotalPage());
				outputVO.setTotalRecord(list.getTotalRecord());
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
				sendRtnObject(outputVO);
			}
			//csv 全部共用
			List<Map<String, Object>> csvList = dam.exeQuery(queryCondition); // 匯出全部用
			outputVO.setCsvList(csvList);
			outputVO.setResultList(list);

			if (list2 != null){
				outputVO.setResultList2(list2);
			}
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	public void export(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		PMS312OutputVO return_VO = (PMS312OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();

		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "房貸轉介來源統計" + sdf.format(new Date())
						+ "_員工編號.csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 41 column
					String[] records = new String[57];
					int i = 0;
					if (!(checkIsNull(map, "YEARMON").equals("REGION") || checkIsNull(map, "YEARMON").equals("AREA"))) {

						records[i] = checkIsNull(map, "YEARMON"); // 資料統計年月
						records[++i] = checkIsNull(map, "S_TYPE").equals("2") ? "YTD"
								: "MTD"; // 統計類型
						records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
						records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNull(map, "BRANCH_CLS"); // 組別
						records[++i] = currencyFormat(map, "M_OP_CNT"); // 房貸作業件數
						records[++i] = currencyFormat(map, "M_OP_AMT"); // 房貸作業金額(百萬元)
						records[++i] = currencyFormat(map, "M_AO_CNT"); // 房貸理專件數
						records[++i] = currencyFormat(map, "M_AO_AMT"); // 房貸理專金額(百萬元)
						records[++i] = currencyFormat(map, "M_BO_CNT"); // 房貸分行其他人員件數
						records[++i] = currencyFormat(map, "M_BO_AMT"); // 房貸分行其他人員金額(百萬元)
						records[++i] = currencyFormat(map, "M_BT_CNT"); // 房貸分行通路小計件數
						records[++i] = currencyFormat(map, "M_BT_AMT"); // 房貸分行通路小計金額(百萬元)
						records[++i] = currencyFormat(map, "M_FG_CNT"); // 房貸法金件數
						records[++i] = currencyFormat(map, "M_FG_AMT"); // 房貸法金金額(百萬元)
						records[++i] = currencyFormat(map, "M_LI_CNT"); // 房貸人壽件數
						records[++i] = currencyFormat(map, "M_LI_AMT"); // 房貸人壽金額(百萬元)
						records[++i] = currencyFormat(map, "M_S_CNT"); // 房貸證劵件數
						records[++i] = currencyFormat(map, "M_S_AMT"); // 房貸證劵金額(百萬元)
						records[++i] = currencyFormat(map, "M_PC_CNT"); // 房貸產險件數
						records[++i] = currencyFormat(map, "M_PC_AMT"); // 房貸產險金額(百萬元)
						records[++i] = currencyFormat(map, "M_OT_CNT"); // 房貸銀行其他單位件數
						records[++i] = currencyFormat(map, "M_OT_AMT"); // 房貸銀行其他單位金額(百萬元)
						records[++i] = currencyFormat(map, "M_PS_CNT"); // 房貸PS自行開發件數
						records[++i] = currencyFormat(map, "M_PS_AMT"); // 房貸PS自行開發金額(百萬元)
						records[++i] = currencyFormat(map, "M_NBT_CNT"); // 房貸非分行通路小計件數
						records[++i] = currencyFormat(map, "M_NBT_AMT"); // 房貸非分行通路小計金額(百萬元)
						records[++i] = currencyFormat(map, "M_TT_CNT"); // 房貸合計件數
						records[++i] = currencyFormat(map, "M_TT_AMT"); // 房貸合計金額(百萬元)
						records[++i] = currencyFormat(map, "C_OP_CNT"); // 信貸作業件數
						records[++i] = currencyFormat(map, "C_OP_AMT"); // 信貸作業金額(百萬元)
						records[++i] = currencyFormat(map, "C_AO_CNT"); // 信貸理專件數
						records[++i] = currencyFormat(map, "C_AO_AMT"); // 信貸理專金額(百萬元)
						records[++i] = currencyFormat(map, "C_BO_CNT"); // 信貸分行其他人員件數
						records[++i] = currencyFormat(map, "C_BO_AMT"); // 信貸分行其他人員金額(百萬元)
						records[++i] = currencyFormat(map, "C_BT_CNT"); // 信貸分行通路小計件數
						records[++i] = currencyFormat(map, "C_BT_AMT"); // 信貸分行通路小計金額(百萬元)
						records[++i] = currencyFormat(map, "C_FG_CNT"); // 信貸法金件數
						records[++i] = currencyFormat(map, "C_FG_AMT"); // 信貸法金金額(百萬元)
						records[++i] = currencyFormat(map, "C_LI_CNT"); // 信貸人壽件數
						records[++i] = currencyFormat(map, "C_LI_AMT"); // 信貸人壽金額(百萬元)
						records[++i] = currencyFormat(map, "C_S_CNT"); // 信貸證劵件數
						records[++i] = currencyFormat(map, "C_S_AMT"); // 信貸證劵金額(百萬元)
						records[++i] = currencyFormat(map, "C_PC_CNT"); // 信貸產險件數
						records[++i] = currencyFormat(map, "C_PC_AMT"); // 信貸產險金額(百萬元)
						records[++i] = currencyFormat(map, "C_OT_CNT"); // 信貸銀行其他單位件數
						records[++i] = currencyFormat(map, "C_OT_AMT"); // 信貸銀行其他單位金額(百萬元)
						records[++i] = currencyFormat(map, "C_PS_CNT"); // 信貸PS自行開發件數
						records[++i] = currencyFormat(map, "C_PS_AMT"); // 信貸PS自行開發金額(百萬元)
						records[++i] = currencyFormat(map, "C_NBT_CNT"); // 信貸非分行通路小計件數
						records[++i] = currencyFormat(map, "C_NBT_AMT"); // 信貸非分行通路小計金額(百萬元)
						records[++i] = currencyFormat(map, "C_TT_CNT"); // 信貸合計件數
						records[++i] = currencyFormat(map, "C_TT_AMT"); // 信貸合計金額(百萬元)
	
						listCSV.add(records);
					}
				}
				// header
				String[] csvHeader = new String[57];
				int j = 0;
				csvHeader[j] = "資料統計年月";
				csvHeader[++j] = "統計類型";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "組別";
				csvHeader[++j] = "房貸作業件數";
				csvHeader[++j] = "房貸作業金額(百萬元)";
				csvHeader[++j] = "房貸理專件數";
				csvHeader[++j] = "房貸理專金額(百萬元)";
				csvHeader[++j] = "房貸分行其他人員件數";
				csvHeader[++j] = "房貸分行其他人員金額(百萬元)";
				csvHeader[++j] = "房貸分行通路小計件數";
				csvHeader[++j] = "房貸分行通路小計金額(百萬元)";
				csvHeader[++j] = "房貸法金件數";
				csvHeader[++j] = "房貸法金金額(百萬元)";
				csvHeader[++j] = "房貸人壽件數";
				csvHeader[++j] = "房貸人壽金額(百萬元)";
				csvHeader[++j] = "房貸證劵件數";
				csvHeader[++j] = "房貸證劵金額(百萬元)";
				csvHeader[++j] = "房貸產險件數";
				csvHeader[++j] = "房貸產險金額(百萬元)";
				csvHeader[++j] = "房貸銀行其他單位件數";
				csvHeader[++j] = "房貸銀行其他單位金額(百萬元)";
				csvHeader[++j] = "房貸PS自行開發件數";
				csvHeader[++j] = "房貸PS自行開發金額(百萬元)";
				csvHeader[++j] = "房貸非分行通路小計件數";
				csvHeader[++j] = "房貸非分行通路小計金額(百萬元)";
				csvHeader[++j] = "房貸合計件數";
				csvHeader[++j] = "房貸合計金額(百萬元)";
				csvHeader[++j] = "信貸作業件數";
				csvHeader[++j] = "信貸作業金額(百萬元)";
				csvHeader[++j] = "信貸理專件數";
				csvHeader[++j] = "信貸理專金額(百萬元)";
				csvHeader[++j] = "信貸分行其他人員件數";
				csvHeader[++j] = "信貸分行其他人員金額(百萬元)";
				csvHeader[++j] = "信貸分行通路小計件數";
				csvHeader[++j] = "信貸分行通路小計金額(百萬元)";
				csvHeader[++j] = "信貸法金件數";
				csvHeader[++j] = "信貸法金金額(百萬元)";
				csvHeader[++j] = "信貸人壽件數";
				csvHeader[++j] = "信貸人壽金額(百萬元)";
				csvHeader[++j] = "信貸證劵件數";
				csvHeader[++j] = "信貸證劵金額(百萬元)";
				csvHeader[++j] = "信貸產險件數";
				csvHeader[++j] = "信貸產險金額(百萬元)";
				csvHeader[++j] = "信貸銀行其他單位件數";
				csvHeader[++j] = "信貸銀行其他單位金額(百萬元)";
				csvHeader[++j] = "信貸PS自行開發件數";
				csvHeader[++j] = "信貸PS自行開發金額(百萬元)";
				csvHeader[++j] = "信貸非分行通路小計件數";
				csvHeader[++j] = "信貸非分行通路小計金額(百萬元)";
				csvHeader[++j] = "信貸合計件數";
				csvHeader[++j] = "信貸合計金額(百萬元)";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else

				this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,###");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
	private String checkIsNu(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}

	/**
	 * 取得temp資料夾絕對路徑
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getTempPath() throws JBranchException {
		String serverPath = (String) getCommonVariable(SystemVariableConsts.SERVER_PATH);
		String seperator = System.getProperties().getProperty("file.separator");
		if (!serverPath.endsWith(seperator)) {
			serverPath += seperator;
		}
		return serverPath + "temp";
	}
}

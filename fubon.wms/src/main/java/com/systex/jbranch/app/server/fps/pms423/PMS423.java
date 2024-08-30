package com.systex.jbranch.app.server.fps.pms423;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOALPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_SALESRPT_WK_GOALVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * 通路業務週報
 */
@Component("pms423")
@Scope("request")
public class PMS423 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
    
	public void getDate(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
        PMS423OutputVO outputVO = new PMS423OutputVO();
        dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("select TO_CHAR(CURRENT_SNAP_DATE, 'YYYY/MM/DD') AS CURRENT_SNAP_DATE ");
		sb.append("from TBPMS_SALESRPT_WK_WEEK ");
		sb.append("WHERE CURRENT_YYYYMM = TO_CHAR(SYSDATE, 'YYYYMM') ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
        sendRtnObject(outputVO);
    }
	
	/**
     * query action
     * @throws ParseException 
     */
    public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
    	
        PMS423InputVO inputVO = (PMS423InputVO) body;
        PMS423OutputVO outputVO = new PMS423OutputVO();
        
        outputVO.setResultList(this.doQuery(inputVO));
        
        sendRtnObject(outputVO);
    }
    
    private List<Map<String, Object>> doQuery (PMS423InputVO inputVO) throws DAOException, JBranchException, ParseException {

    	dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
    	String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
    	if(StringUtils.isNotBlank(inputVO.getReport_type())){
        	String report_type = inputVO.getReport_type();
        	List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>(); 
    		
    		//取得查詢資料可視範圍
    		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
    		PMS000InputVO pms000InputVO = new PMS000InputVO();
    		pms000InputVO.setReportDate(inputVO.getData_date());
    		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
    		
    		if ("1".equals(report_type)) {
        		sb.append("SELECT COALESCE(ORG.REGION_CENTER_ID, ' ')   AS REGION_CENTER_ID, ");
        		sb.append("       COALESCE(ORG.REGION_CENTER_NAME, ' ') AS REGION_CENTER_NAME, ");
        		sb.append("       COALESCE(ORG.BRANCH_AREA_ID, ' ')     AS BRANCH_AREA_ID, ");
        		sb.append("       COALESCE(ORG.BRANCH_AREA_NAME, ' ')   AS BRANCH_AREA_NAME, ");
        		sb.append("       COALESCE(AO.BRANCH_NBR, '000')        AS BRANCH_NBR, ");
        		sb.append("       COALESCE(ORG.BRANCH_NAME, ' ')        AS BRANCH_NAME, ");
        		sb.append("       AO.EMP_ID, ");
        		sb.append("       NVL(EMP.EMP_NAME, 'OOO') AS EMP_NAME, ");
        		sb.append("       AO.AO_CODE, ");
        		sb.append("       CASE WHEN AO_DTL.TYPE = '1' THEN '(計績)' WHEN AO_DTL.TYPE = '2' THEN '(兼職)' WHEN AO_DTL.TYPE = '3' THEN '(維護)' ELSE '' END AS AO_TYPE, ");
        		sb.append("       AO.EMP_ID || '-' || NVL(EMP.EMP_NAME, 'OOO') || '-' || AO.AO_CODE || (CASE WHEN AO_DTL.TYPE = '1' THEN '(計績)' WHEN AO_DTL.TYPE = '2' THEN '(兼職)' WHEN AO_DTL.TYPE = '3' THEN '(維護)' ELSE '' END) AS EMP_INFO, ");
        		sb.append("       NVL(GO.WK_GOAL_DEP, 0) AS WK_GOAL_DEP, ");
        		sb.append("       NVL(GO.WK_GOAL_INV, 0) AS WK_GOAL_INV, ");
        		sb.append("       NVL(GO.WK_GOAL_EIP, 0) AS WK_GOAL_EIP, ");
        		sb.append("       AO.MON_SAVING, ");
        		sb.append("       AO.MON_INVINS, ");
        		sb.append("       AO.MON_EIPCNT, ");
        		sb.append("       AO.MON_LUM, ");
        		sb.append("       AO.DIFF_SAVING, ");
        		sb.append("       AO.DIFF_INVINS, ");
        		sb.append("       AO.DIFF_EIPCNT, ");
        		sb.append("       AO.DIFF_LUM ");
        		sb.append("FROM TBPMS_SALESRPT_WK_AO AO ");
        		sb.append("LEFT JOIN TBPMS_SALESRPT_WK_GOAL GO ON AO.YYYYMM = GO.YYYYMM AND AO.AO_JOB_RANK = GO.ROLE_NAME ");
        		sb.append("LEFT JOIN TBORG_MEMBER EMP ON AO.EMP_ID  = EMP.EMP_ID ");
        		sb.append("LEFT JOIN VWORG_DEFN_INFO ORG ON AO.BRANCH_NBR = ORG.BRANCH_NBR ");
        		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO_DTL ON AO.AO_CODE = AO_DTL.AO_CODE AND AO.EMP_ID = AO_DTL.EMP_ID ");
        		sb.append("WHERE 1 = 1 ");
        		sb.append("AND AO.SNAP_DATE = (SELECT CURRENT_SNAP_DATE FROM TBPMS_SALESRPT_WK_WEEK WHERE CURRENT_YYYYMM = :yyyymm) ");
        		sb.append("AND AO.YYYYMM = :yyyymm ");
        		
        		//如果員編有值，查詢條件只取員編；如果員編空白，分行有值，查詢條件只取分行
    			//員編
    			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
    				sb.append("AND AO.EMP_ID = :emp_id ");
    				queryCondition.setObject("emp_id", inputVO.getEmp_id());
    			} else {
    				// 分行
    				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
    					sb.append("AND AO.BRANCH_NBR = :branch_nbr ");
    					queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
    				}else{
    					//登入非總行人員強制加分行
    					if(!headmgrMap.containsKey(roleID)) {
    						sb.append("AND AO.BRANCH_NBR IN (:branch_nbr) ");
    						queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
    					}
    					
    					// 區域中心
    					if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
    						sb.append("AND ORG.REGION_CENTER_ID = :region_center_id ");
    						queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
    					}else{
    					//登入非總行人員強制加區域中心
    						if(!headmgrMap.containsKey(roleID)) {
    							sb.append("AND ORG.REGION_CENTER_ID IN (:region_center_id) ");
    							queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
    						}
    					}	
    					
    					// 營運區
    					if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
    						sb.append("AND ORG.BRANCH_AREA_ID = :branch_area_id ");
    						queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
    					}else{
    					//登入非總行人員強制加營運區
    						if(!headmgrMap.containsKey(roleID)) {
    							sb.append("AND ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
    							queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
    						}
    					}
    				}
    			}
    			
    			// 0632/0652:排除銀證理專
    			sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE AO.EMP_ID = BS.EMP_ID ) ");
    			
    			sb.append("ORDER BY BRANCH_NBR, EMP_ID, AO_CODE ");
    			
        	} else if ("4".equals(report_type)) {
        		sb.append("WITH DT AS ( ");
        		sb.append("  SELECT * ");
        		sb.append("  FROM TBPMS_SALESRPT_WK_WEEK ");
        		sb.append("  WHERE CURRENT_YYYYMM = :yyyymm ");
        		sb.append(") ");
        		sb.append(", DTL2 AS ( ");
        		sb.append("  SELECT NVL(CUR_CUST.EMP_ID, LAT_CUST.EMP_ID)  AS EMP_ID, ");
        		sb.append("         NVL(CUR_CUST.BRANCH_NBR, LAT_CUST.BRANCH_NBR) AS BRANCH_NBR, ");
        		sb.append("         NVL(CUR_CUST.CUST_ID, LAT_CUST.CUST_ID) AS CUST_ID, ");
        		sb.append("         NVL(CUR_CUST.AO_CODE, '000') AS CUR_AO_CODE, ");
        		sb.append("         NVL(LAT_CUST.AO_CODE, '000') AS LAT_AO_CODE, ");
        		sb.append("         CUR_CUST.CON_DEGREE AS CUR_CON_DEGREE , ");
        		sb.append("         LAT_CUST.CON_DEGREE AS LAT_CON_DEGREE, ");
        		sb.append("         CASE WHEN CUR_CUST.CON_DEGREE IN ('E','I','P') THEN '3' ");
        		sb.append("              WHEN CUR_CUST.CON_DEGREE IS NULL THEN '2' ");
        		sb.append("         ELSE '1' END AS CUR_CON_DEGREE_DI, ");
        		sb.append("         CASE WHEN LAT_CUST.CON_DEGREE IN ('E','I','P') THEN '3' ");
        		sb.append("              WHEN LAT_CUST.CON_DEGREE IS NULL THEN '2' ");
        		sb.append("         ELSE '1' END AS LAT_CON_DEGREE_DI ");
        		sb.append("  FROM ( ");
        		sb.append("    SELECT * ");
        		sb.append("    FROM TBPMS_SALESRPT_WK_CUST A ");
        		sb.append("    INNER JOIN DT ON A.SNAP_DATE = DT.CURRENT_SNAP_DATE AND A.YYYYMM = DT.CURRENT_YYYYMM ");
        		
    			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
    				sb.append("    WHERE A.EMP_ID = :emp_id ");
    				queryCondition.setObject("emp_id", inputVO.getEmp_id());        		
    			}
    			
    			sb.append("  ) CUR_CUST ");
        		sb.append("  FULL OUTER JOIN ( ");
        		sb.append("    SELECT * ");
        		sb.append("    FROM TBPMS_SALESRPT_WK_CUST A ");
        		sb.append("    INNER JOIN DT ON A.SNAP_DATE = DT.LAST_SNAP_DATE AND A.YYYYMM = DT.LAST_YYYYMM ");
        		
    			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
    				sb.append("    WHERE A.EMP_ID = :emp_id ");
    				queryCondition.setObject("emp_id", inputVO.getEmp_id());        		
    			}
    			
    			sb.append("  ) LAT_CUST ON CUR_CUST.EMP_ID = LAT_CUST.EMP_ID AND CUR_CUST.CUST_ID = LAT_CUST.CUST_ID ");
        		sb.append(") ");
        		
        		sb.append("SELECT RTN.EMP_ID,  ");
        		sb.append("       CUR_EMP.EMP_NAME, ");
        		sb.append("       ORG.REGION_CENTER_ID, ");
        		sb.append("       ORG.BRANCH_AREA_ID, ");
        		sb.append("       RTN.BRANCH_NBR, ");
        		sb.append("       RTN.CUST_ID, CUR_MAST.CUST_NAME, ");
        		sb.append("       RTN.CUR_AO_CODE, ");
        		sb.append("       RTN.LAT_AO_CODE, ");
        		sb.append("       RTN.CUR_CON_DEGREE, ");
        		sb.append("       RTN.LAT_CON_DEGREE, ");
        		sb.append("       CASE WHEN RTN.CUR_CON_DEGREE_DI > RTN.LAT_CON_DEGREE_DI THEN '+1' ELSE '-1' END AS DIFF_CON_DEGREE ");
        		sb.append("FROM DTL2 RTN ");
        		sb.append("LEFT JOIN TBORG_MEMBER CUR_EMP ON RTN.EMP_ID  = CUR_EMP.EMP_ID ");
        		sb.append("LEFT JOIN TBCRM_CUST_MAST CUR_MAST ON RTN.CUST_ID  = CUR_MAST.CUST_ID ");
        		sb.append("LEFT JOIN VWORG_DEFN_INFO ORG ON RTN.BRANCH_NBR = ORG.BRANCH_NBR ");
        		sb.append("WHERE 1 = 1 ");
        		sb.append("AND RTN.EMP_ID <> '000000' ");
        		sb.append("AND RTN.CUR_CON_DEGREE_DI <>  RTN.LAT_CON_DEGREE_DI ");
        		sb.append("AND (RTN.CUR_CON_DEGREE_DI = '3' OR RTN.LAT_CON_DEGREE_DI='3') ");
        		
        		//如果員編有值，查詢條件只取員編；如果員編空白，分行有值，查詢條件只取分行
    			//員編
    			if(!StringUtils.isNotBlank(inputVO.getEmp_id())) {
        			// 分行
    				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
    					sb.append("AND RTN.BRANCH_NBR = :branch_nbr ");
    					queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
    				} else {
    					// 登入非總行人員強制加分行
    					if (!headmgrMap.containsKey(roleID)) {
    						sb.append("AND RTN.BRANCH_NBR IN  (:branch_nbr) ");
    						queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
    					}
    					// 區域中心
    					if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
    						sb.append("AND ORG.REGION_CENTER_ID = :region_center_id ");
    						queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
    					} else {
    						// 登入非總行人員強制加區域中心
    						if(!headmgrMap.containsKey(roleID)) {
    							sb.append(" AND ORG.REGION_CENTER_ID IN (:region_center_id) ");
    							queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
    						}
    					}					
    					// 營運區
    					if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
    						sb.append("AND ORG.BRANCH_AREA_ID = :branch_area_id ");
    						queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
    					} else {
    						//登入非總行人員強制加營運區
    						if (!headmgrMap.containsKey(roleID)) {
    							sb.append("AND ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
    							queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
    						}
    					}
    				}
    			}
    			
    			// 0632/0652:排除銀證理專
    			sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE RTN.EMP_ID = BS.EMP_ID ) ");
        	} else {
        		sb.append("WITH DT AS ( ");
        		sb.append("  SELECT * ");
        		sb.append("  FROM TBPMS_SALESRPT_WK_WEEK ");
        		sb.append("  WHERE CURRENT_YYYYMM = :yyyymm ");
        		sb.append(") ");
        		
        		sb.append("SELECT COALESCE(ORG.REGION_CENTER_ID, ' ')   AS REGION_CENTER_ID, ");
        		sb.append("       COALESCE(ORG.REGION_CENTER_NAME, ' ') AS REGION_CENTER_NAME, ");
        		sb.append("       COALESCE(ORG.BRANCH_AREA_ID, ' ')     AS BRANCH_AREA_ID, ");
        		sb.append("       COALESCE(ORG.BRANCH_AREA_NAME, ' ')   AS BRANCH_AREA_NAME, ");
        		sb.append("       NVL(CUR_CUST.BRANCH_NBR, LAT_CUST.BRANCH_NBR)  AS BRANCH_NBR, ");
        		sb.append("       COALESCE(ORG.BRANCH_NAME, ' ')        AS BRANCH_NAME, ");
        		sb.append("       NVL(CUR_CUST.EMP_ID, LAT_CUST.EMP_ID) AS EMP_ID, ");
        		sb.append("       NVL(CUR_EMP.EMP_NAME, LAT_EMP.EMP_NAME) AS EMP_NAM, ");
        		sb.append("       NVL(CUR_CUST.CUST_ID, LAT_CUST.CUST_ID) AS CUST_ID, ");
        		sb.append("       NVL(CUR_MAST.CUST_NAME, LAT_MAST.CUST_NAME) AS CUST_NAME, ");
        		sb.append("       CUR_CUST.AO_CODE AS CUR_AO_CODE, ");
        		sb.append("       LAT_CUST.AO_CODE AS LAT_AO_CODE, ");
        		
        		//--以下是2.存款不含台定AUM增減明細
        		sb.append("       NVL(CUR_CUST.DEP_T_AUM, 0)     AS CUR_DEP_T_AUM, ");
        		sb.append("       NVL(CUR_CUST.DEP_F_AUM, 0)     AS CUR_DEP_F_AUM, ");
        		sb.append("       NVL(CUR_CUST.CT_F_AUM, 0)      AS CUR_CT_F_AUM, ");
        		sb.append("       NVL(LAT_CUST.DEP_T_AUM, 0)     AS LAT_DEP_T_AUM, ");
        		sb.append("       NVL(LAT_CUST.DEP_F_AUM, 0)     AS LAT_DEP_F_AUM, ");
        		sb.append("       NVL(LAT_CUST.CT_F_AUM, 0)      AS LAT_CT_F_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.DEP_T_AUM, 0) - NVL(LAT_CUST.DEP_T_AUM, 0) ) AS DIFF_DEP_T_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.DEP_F_AUM, 0) - NVL(LAT_CUST.DEP_F_AUM, 0) ) AS DIFF_DEP_F_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.CT_F_AUM, 0)  - NVL(LAT_CUST.CT_F_AUM, 0) )  AS DIFF_CT_F_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.DEP_T_AUM, 0) + NVL(CUR_CUST.DEP_F_AUM, 0) + NVL(CUR_CUST.CT_F_AUM, 0) ) - ( NVL(LAT_CUST.DEP_T_AUM, 0) + NVL(LAT_CUST.DEP_F_AUM, 0) + NVL(LAT_CUST.CT_F_AUM, 0) ) AS DIFF_SAVING, ");
        		
        		//--以下是3.投保不含黃金存摺AUM增減明細
        		sb.append("       NVL(CUR_CUST.INV_FUND_AUM, 0)  AS CUR_INV_FUND_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_ETF_AUM, 0)   AS CUR_INV_ETF_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_OVST_AUM, 0)  AS CUR_INV_OVST_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_SI_AUM, 0)    AS CUR_INV_SI_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_DCI_AUM, 0)   AS CUR_INV_DCI_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_SN_AUM, 0)    AS CUR_INV_SN_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_RP_AUM, 0)    AS CUR_INV_RP_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_OVBN_AUM, 0)  AS CUR_INV_OVBN_AUM, ");
        		sb.append("       NVL(CUR_CUST.INV_TRUST_AUM, 0) AS CUR_INV_TRUST_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INS_AUM, 0) + NVL(CUR_CUST.LIKE_SAV_AUM, 0) ) AS CUR_INS_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_FUND_AUM, 0)  AS LAT_INV_FUND_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_ETF_AUM, 0)   AS LAT_INV_ETF_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_OVST_AUM, 0)  AS LAT_INV_OVST_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_SI_AUM, 0)    AS LAT_INV_SI_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_DCI_AUM, 0)   AS LAT_INV_DCI_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_SN_AUM, 0)    AS LAT_INV_SN_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_RP_AUM, 0)    AS LAT_INV_RP_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_OVBN_AUM, 0)  AS LAT_INV_OVBN_AUM, ");
        		sb.append("       NVL(LAT_CUST.INV_TRUST_AUM, 0) AS LAT_INV_TRUST_AUM, ");
        		sb.append("       ( NVL(LAT_CUST.INS_AUM,0) + NVL(LAT_CUST.LIKE_SAV_AUM, 0) ) AS LAT_INS_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_FUND_AUM, 0)  - NVL(LAT_CUST.INV_FUND_AUM, 0) ) AS DIFF_INV_FUND_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_ETF_AUM, 0)   - NVL(LAT_CUST.INV_ETF_AUM, 0)  ) AS DIFF_INV_ETF_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_OVST_AUM, 0)  - NVL(LAT_CUST.INV_OVST_AUM, 0) ) AS DIFF_INV_OVST_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_SI_AUM, 0)    - NVL(LAT_CUST.INV_SI_AUM, 0)   ) AS DIFF_INV_SI_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_DCI_AUM, 0)   - NVL(LAT_CUST.INV_DCI_AUM, 0)  ) AS DIFF_INV_DCI_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_SN_AUM, 0)    - NVL(LAT_CUST.INV_SN_AUM, 0)   ) AS DIFF_INV_SN_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_RP_AUM, 0)    - NVL(LAT_CUST.INV_RP_AUM, 0)   ) AS DIFF_INV_RP_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_OVBN_AUM, 0)  - NVL(LAT_CUST.INV_OVBN_AUM, 0) ) AS DIFF_INV_OVBN_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_TRUST_AUM, 0) - NVL(LAT_CUST.INV_TRUST_AUM, 0)) AS DIFF_INV_TRUST_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INS_AUM, 0) + NVL(CUR_CUST.LIKE_SAV_AUM,0) ) - ( NVL(LAT_CUST.INS_AUM,0) + NVL(LAT_CUST.LIKE_SAV_AUM,0) ) AS DIFF_INS_AUM, ");
        		sb.append("       ( NVL(CUR_CUST.INV_FUND_AUM, 0)  + ");
        		sb.append("         NVL(CUR_CUST.INV_ETF_AUM, 0)   + ");
        		sb.append("         NVL(CUR_CUST.INV_OVST_AUM, 0)  + ");
        		sb.append("         NVL(CUR_CUST.INV_SI_AUM, 0)    + ");
        		sb.append("         NVL(CUR_CUST.INV_DCI_AUM, 0)   + ");
        		sb.append("         NVL(CUR_CUST.INV_SN_AUM, 0)    + ");
        		sb.append("         NVL(CUR_CUST.INV_RP_AUM, 0)    + ");
        		sb.append("         NVL(CUR_CUST.INV_OVBN_AUM, 0)  + ");
        		sb.append("         NVL(CUR_CUST.INV_TRUST_AUM, 0) + ");
        		sb.append("         ( NVL(CUR_CUST.INS_AUM, 0) + NVL(CUR_CUST.LIKE_SAV_AUM, 0) ) ");
        		sb.append("       ) - ");
        		sb.append("       ( NVL(LAT_CUST.INV_FUND_AUM, 0)  + ");
        		sb.append("         NVL(LAT_CUST.INV_ETF_AUM, 0)   + ");
        		sb.append("         NVL(LAT_CUST.INV_OVST_AUM, 0)  + ");
        		sb.append("         NVL(LAT_CUST.INV_SI_AUM, 0)    + ");
        		sb.append("         NVL(LAT_CUST.INV_DCI_AUM, 0)   + ");
        		sb.append("         NVL(LAT_CUST.INV_SN_AUM, 0)    + ");
        		sb.append("         NVL(LAT_CUST.INV_RP_AUM, 0)    + ");
        		sb.append("         NVL(LAT_CUST.INV_OVBN_AUM, 0)  + ");
        		sb.append("         NVL(LAT_CUST.INV_TRUST_AUM, 0) + ");
        		sb.append("         ( NVL(LAT_CUST.INS_AUM, 0) + NVL(LAT_CUST.LIKE_SAV_AUM, 0) ) ");
        		sb.append("       ) AS DIFF_INVINS, ");
        		
        		//--以下是5.LUM房貸增減明細
        		sb.append("       NVL(CUR_CUST.LUM,0)  AS CUR_LUM, ");
        		sb.append("       NVL(LAT_CUST.LUM,0)  AS LAT_LUM, ");
        		sb.append("       ( NVL(CUR_CUST.LUM,0) - NVL(LAT_CUST.LUM,0) ) AS DIFF_LUM ");
        		sb.append("FROM ( ");
        		sb.append("  SELECT * ");
        		sb.append("  FROM TBPMS_SALESRPT_WK_CUST A ");
        		sb.append("  INNER JOIN DT ON A.SNAP_DATE = DT.CURRENT_SNAP_DATE AND A.YYYYMM = DT.CURRENT_YYYYMM ");
    			
        		//員編
    			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
    				sb.append("  WHERE A.EMP_ID = :emp_id ");
    				queryCondition.setObject("emp_id", inputVO.getEmp_id());
    			}  
    			
        		sb.append(") CUR_CUST ");
        		sb.append("FULL OUTER JOIN ( ");
        		sb.append("  SELECT * ");
        		sb.append("  FROM TBPMS_SALESRPT_WK_CUST A ");
        		sb.append("  INNER JOIN DT ON A.SNAP_DATE = DT.LAST_SNAP_DATE AND A.YYYYMM = DT.LAST_YYYYMM ");
        		
    			//員編
    			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
    				sb.append("  WHERE A.EMP_ID = :emp_id ");
    				queryCondition.setObject("emp_id", inputVO.getEmp_id());
    			}          	
    			
        		sb.append(") LAT_CUST ON CUR_CUST.EMP_ID  = LAT_CUST.EMP_ID AND CUR_CUST.CUST_ID = LAT_CUST.CUST_ID AND CUR_CUST.AO_CODE = CUR_CUST.AO_CODE ");
        		sb.append("LEFT JOIN TBORG_MEMBER CUR_EMP ON CUR_CUST.EMP_ID  = CUR_EMP.EMP_ID ");
        		sb.append("LEFT JOIN TBCRM_CUST_MAST CUR_MAST ON CUR_CUST.CUST_ID  = CUR_MAST.CUST_ID ");
        		sb.append("LEFT JOIN TBORG_MEMBER LAT_EMP ON LAT_CUST.EMP_ID  = LAT_EMP.EMP_ID ");
        		sb.append("LEFT JOIN TBCRM_CUST_MAST LAT_MAST ON LAT_CUST.CUST_ID  = LAT_MAST.CUST_ID ");
        		sb.append("LEFT JOIN VWORG_DEFN_INFO ORG ON NVL(CUR_CUST.BRANCH_NBR, LAT_CUST.BRANCH_NBR) = ORG.BRANCH_NBR ");
        		sb.append("WHERE 1 = 1 ");

        		//0473: 通路業務週明細排序
    			if("2".equals(report_type)){
            		sb.append("AND ( ( NVL(CUR_CUST.DEP_T_AUM, 0) + NVL(CUR_CUST.DEP_F_AUM,0) + NVL(CUR_CUST.CT_F_AUM,0) ) - ( NVL(LAT_CUST.DEP_T_AUM,0) + NVL(LAT_CUST.DEP_F_AUM,0) + NVL(LAT_CUST.CT_F_AUM,0) ) ) <> 0 ");
    			} else if ("3".equals(report_type)) {
            		sb.append("AND ( ");
            		sb.append("  ( NVL(CUR_CUST.INV_FUND_AUM, 0)  + ");
            		sb.append("    NVL(CUR_CUST.INV_ETF_AUM, 0)   + ");
            		sb.append("    NVL(CUR_CUST.INV_OVST_AUM, 0)  + ");
            		sb.append("    NVL(CUR_CUST.INV_SI_AUM, 0)    + ");
            		sb.append("    NVL(CUR_CUST.INV_DCI_AUM, 0)   + ");
            		sb.append("    NVL(CUR_CUST.INV_SN_AUM, 0)    + ");
            		sb.append("    NVL(CUR_CUST.INV_RP_AUM, 0)    + ");
            		sb.append("    NVL(CUR_CUST.INV_OVBN_AUM, 0)  + ");
            		sb.append("    NVL(CUR_CUST.INV_TRUST_AUM, 0) + ");
            		sb.append("    ( NVL(CUR_CUST.INS_AUM, 0) + NVL(CUR_CUST.LIKE_SAV_AUM, 0) ) ");
            		sb.append("  ) - ");
            		sb.append("  ( NVL(LAT_CUST.INV_FUND_AUM, 0)  + ");
            		sb.append("    NVL(LAT_CUST.INV_ETF_AUM, 0)   + ");
            		sb.append("    NVL(LAT_CUST.INV_OVST_AUM, 0)  + ");
            		sb.append("    NVL(LAT_CUST.INV_SI_AUM, 0)    + ");
            		sb.append("    NVL(LAT_CUST.INV_DCI_AUM, 0)   + ");
            		sb.append("    NVL(LAT_CUST.INV_SN_AUM, 0)    + ");
            		sb.append("    NVL(LAT_CUST.INV_RP_AUM, 0)    + ");
            		sb.append("    NVL(LAT_CUST.INV_OVBN_AUM, 0)  + ");
            		sb.append("    NVL(LAT_CUST.INV_TRUST_AUM, 0) + ");
            		sb.append("    ( NVL(LAT_CUST.INS_AUM, 0) + NVL(LAT_CUST.LIKE_SAV_AUM, 0) ) ");
            		sb.append("  ) ");
            		sb.append(") <> 0 ");
    			} else if ("5".equals(report_type)) {
    				sb.append("AND ( ( NVL(CUR_CUST.LUM,0) - NVL(LAT_CUST.LUM,0) ) ) <> 0 ");
    			}
        		
        		//如果員編有值，查詢條件只取員編；如果員編空白，分行有值，查詢條件只取分行
    			if(!StringUtils.isNotBlank(inputVO.getEmp_id())){
    				// 分行
    				if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
    					sb.append("AND CUR_CUST.BRANCH_NBR = :branch_nbr ");
    					queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
    				}else{
    				//登入非總行人員強制加分行
    					if(!headmgrMap.containsKey(roleID)) {
    						sb.append("AND CUR_CUST.BRANCH_NBR IN (:branch_nbr) ");
    						queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
    					}
    					// 區域中心
    					if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
    						sb.append("AND ORG.REGION_CENTER_ID = :region_center_id ");
    						queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
    					}else{
    					//登入非總行人員強制加區域中心
    						if(!headmgrMap.containsKey(roleID)) {
    							sb.append("AND ORG.REGION_CENTER_ID IN (:region_center_id) ");
    							queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
    						}
    					}					
    					// 營運區
    					if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
    						sb.append("AND ORG.BRANCH_AREA_ID = :branch_area_id ");
    						queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
    					}else{
    					//登入非總行人員強制加營運區
    						if(!headmgrMap.containsKey(roleID)) {
    							sb.append("AND ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
    							queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
    						}
    					}
    				}
    			}

        		// 0632/0652:排除銀證理專
    			sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO BS WHERE NVL(CUR_CUST.EMP_ID, LAT_CUST.EMP_ID) = BS.EMP_ID ) ");
        	}
    		
    		queryCondition.setObject("yyyymm", inputVO.getData_date());
    		queryCondition.setQueryString(sb.toString());
    		
    		resultList = dam.exeQuery(queryCondition);
    		
        	return resultList;
        } else {
        	throw new APException("系統發生錯誤請洽系統管理員");
        }
    }
    
    //產生CSV範例檔
  	public void getExample(Object body, IPrimitiveMap header) throws Exception {
  		
  		CSVUtil csv = new CSVUtil();
  		String csv_name = "增量目標數.csv";
  		List listCSV = new ArrayList();
  		
  		String[] csvHeader = {"資料年月(YYYYMM)", "角色名稱", "存款不含台定增量目標", "投保不含黃金存摺增量目標", "EIP客戶增量目標"};
		String[] csvMain   = {"YYYYMM", "ROLE_NAME", "WK_GOAL_DEP", "WK_GOAL_INV", "WK_GOAL_EIP"};
		String[] roleNames  = {"FC1", "FC2", "FC3", "FC4", "FC5", "FCH", "FCH2", "FCH3", "FCH4", "FCH5"};
  		
		for(String roleName : roleNames) {
			String[] records = new String[csvMain.length];
			if (StringUtils.equals("ROLE_NAME", csvMain[1])) {
				records[1] = roleName;
			}
			listCSV.add(records);
		}
		
  		// 設定表頭
  		csv.setHeader(csvHeader);
  		csv.addRecordList(listCSV);
  		String url = csv.generateCSV();
  		notifyClientToDownloadFile(url, csv_name);
  		
  		sendRtnObject(null);
  	}
  	
  	//上傳CSV檔案
  	public void uploadFile(Object body, IPrimitiveMap header) throws Exception {
  		
  		PMS423InputVO inputVO = (PMS423InputVO) body;
  		PMS423OutputVO outputVO = new PMS423OutputVO();		
  		dam = this.getDataAccessManager();
  		
  		//顯示上傳成功的訊息
  		String successMsg;
  		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
  		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
  				
  		if(!dataCsv.isEmpty()){	
  			int j = 0;			
  			int columnRow = 0;
  			try {
  				for (int i = 1; i < dataCsv.size(); i++) {    //i=1 (不讀標頭)
  					columnRow = i;
  					TBPMS_SALESRPT_WK_GOALPK pk = new TBPMS_SALESRPT_WK_GOALPK();
  					TBPMS_SALESRPT_WK_GOALVO vo = new TBPMS_SALESRPT_WK_GOALVO();
  					String[] str = dataCsv.get(i);
  					
  					pk.setYYYYMM(str[0]);
  					pk.setROLE_NAME(str[1]);
  					
  					vo = (TBPMS_SALESRPT_WK_GOALVO) dam.findByPKey(TBPMS_SALESRPT_WK_GOALVO.TABLE_UID, pk);
  					if (null == vo) {
  						vo = new TBPMS_SALESRPT_WK_GOALVO();
  						vo.setcomp_id(pk);
  						vo.setWK_GOAL_DEP(new BigDecimal(str[2]));
  						vo.setWK_GOAL_INV(new BigDecimal(str[3]));
  						vo.setWK_GOAL_EIP(new BigDecimal(str[4]));
  						dam.create(vo);
  					} else {
  						vo.setWK_GOAL_DEP(new BigDecimal(str[2]));
						vo.setWK_GOAL_INV(new BigDecimal(str[3]));
						vo.setWK_GOAL_EIP(new BigDecimal(str[4]));
						dam.update(vo);
  					}
  					
  					j++;
  				}
  				
  				successMsg = "資料匯入成功，筆數" + j;
  				
  				outputVO.setSuccessMsg(successMsg);	
  			} catch(Exception e) {
  				e.printStackTrace();
  				throw new APException("案件編號" + columnRow + "有問題，上傳失敗！");
  			}
  		}
  		
  		sendRtnObject(outputVO);
  	}
  	
  	// 取得資料年月下拉
  	public void queryDataDate(Object body, IPrimitiveMap header) throws JBranchException {
  		
  		PMS423OutputVO outputVO = new PMS423OutputVO();		
  		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT DISTINCT YYYYMM FROM TBPMS_SALESRPT_WK_GOAL order by YYYYMM desc");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
  	}
  	
  	// 查詢增量目標數
  	public void queryGoalData(Object body, IPrimitiveMap header) throws JBranchException {
  		
  		PMS423InputVO inputVO = (PMS423InputVO) body;
  		PMS423OutputVO outputVO = new PMS423OutputVO();		
  		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT * FROM TBPMS_SALESRPT_WK_GOAL WHERE YYYYMM = :yyyymm ");
		
		queryCondition.setObject("yyyymm", inputVO.getData_date());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
  	}
  	
  	// 刪除增量目標數
   	public void deleteGoalData(Object body, IPrimitiveMap header) throws JBranchException {
   		
   		PMS423InputVO inputVO = (PMS423InputVO) body;
   		dam = this.getDataAccessManager();
 		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
 		StringBuffer sb = new StringBuffer();

 		sb.append("DELETE FROM TBPMS_SALESRPT_WK_GOAL WHERE YYYYMM = :yyyymm ");
 		
 		queryCondition.setObject("yyyymm", inputVO.getData_date());
 		
 		queryCondition.setQueryString(sb.toString());
 		
 		dam.exeUpdate(queryCondition);
 		
 		sendRtnObject(null);
   	}
  	
    /** 匯出 Csv **/
    public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, IOException {
    	
    	PMS423InputVO inputVO = (PMS423InputVO) body;
    	List<Map<String, Object>> list = this.doQuery(inputVO);
    	
    	if (list.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			//相關資訊設定
			String fileName = String.format("各項目標數%s.xlsx", sdf.format(new Date()));
			String uuid = UUID.randomUUID().toString();
			
			//建置Excel
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("各項目標數");
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);
			
			//header
			String[] headerLine1 = {"", "", "增量目標數", "增量目標數", "增量目標數",
									"當月合計", "當月合計", "當月合計", "當月合計",
									"較上月差異", "較上月差異", "較上月差異", "較上月差異"};
									
			String[] headerLine2 = {"分行別", "理專", "存款不含台定增量目標", "投保不含黃金存摺增量目標", "EIP客戶增量目標",
									"當月存款不含台定AUM", "當月投保不含黃金存摺AUM", "當月EIP客戶數", "當月LUM(房貸放款餘額)",
									"較上月底增減存款不含台定AUM", "較上月底增減投保不含黃金存摺AUM", 
									"較上月底增減EIP客戶數", "較上月底增減LUM(房貸放款餘額)"};
			
			String[] mainLine    = {"BRANCH_NBR", "EMP_INFO", "WK_GOAL_DEP", "WK_GOAL_INV", "WK_GOAL_EIP",
									"MON_SAVING", "MON_INVINS", "MON_EIPCNT", "MON_LUM",
									"DIFF_SAVING", "DIFF_INVINS", "DIFF_EIPCNT", "DIFF_LUM"};
		 	
			// 表頭 CELL型式
			XSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			headingStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);	//填滿顏色
			headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headingStyle.setBorderBottom((short) 1);
			headingStyle.setBorderTop((short) 1);
			headingStyle.setBorderLeft((short) 1);
			headingStyle.setBorderRight((short) 1);
			headingStyle.setWrapText(true);

			Integer index = 0; // first row
			
			//計算合併儲存格欄位數
			int colNbr = headerLine1.length - 1;
			
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); //比對用
			
			XSSFRow row = sheet.createRow(index);
		    
			for (int i = 0; i < headerLine1.length; i++) {
				String headerLine = headerLine1[i];
				if (tempList.indexOf(headerLine) < 0) {
					tempList.add(headerLine);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(headerLine1[i]);

					if (endFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
					}
					startFlag = i;
					endFlag = 0;
				} else {
					endFlag = i;
				}
			}
			
			if (endFlag != 0) { //最後的CELL若需要合併儲存格，則在這裡做
				sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
			}
			
			index++;
			
			row = sheet.createRow(index);
			for (int i = 0; i < headerLine2.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLine2[i]);

				if ("".equals(headerLine2[i])) {
					sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
				}
			}
			
			index++;
			
			// 資料 CELL型式
			XSSFCellStyle mainStyle = workbook.createCellStyle();
			mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			mainStyle.setBorderBottom((short) 1);
			mainStyle.setBorderTop((short) 1);
			mainStyle.setBorderLeft((short) 1);
			mainStyle.setBorderRight((short) 1);
			
			if (list.size() > 0) {				
				for (Map<String, Object> map : list) {
					row = sheet.createRow(index);
					if (map.size() > 0) {
						for (int j = 0; j < mainLine.length; j++) {
							XSSFCell cell = row.createCell(j);
							cell.setCellStyle(mainStyle);
							cell.setCellValue(checkMap(map, mainLine[j]));
						}
						index++;
					} 
				}
			} else { 
				row = sheet.createRow(index);
				XSSFCell cell = row.createCell(0);
				cell.setCellStyle(mainStyle);
				cell.setCellValue("查無資料！");
			}
			
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			
			workbook.write(fos);
			workbook.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		}
    }
    
    /**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkMap (Map map, String key) {
		
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
package com.systex.jbranch.app.server.fps.pms339;
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

import com.systex.jbranch.app.server.fps.pms217.PMS217InputVO;
import com.systex.jbranch.app.server.fps.pms217.PMS217OutputVO;
import com.systex.jbranch.app.server.fps.pms340.PMS340InputVO;
import com.systex.jbranch.app.server.fps.pms340.PMS340OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :房貸壽險獎勵金報表Controller <br>
 * Comments Name : PMS339.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月22日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月22日<br>
 */
@Component("pms339")
@Scope("request")
public class PMS339 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;

	private PMS339InputVO inputVO = null;
	private String roleflag="0";
	private Logger logger = LoggerFactory.getLogger(PMS339.class);

	
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS339InputVO inputVO = (PMS339InputVO) body;
		PMS339OutputVO outputVO = new PMS339OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			sql.append("  SELECT YEARMON					                      ");
			//2017/06/17 補充欄位 START
			sql.append("        , POLICY_NO                                       ");
			sql.append("        , APPL_ID	                                      ");
			sql.append("        , APPL_NAME                                       ");
			//2017/06/17 補充欄位    END
			sql.append("        , REGION_CENTER_ID                                ");
			sql.append("        , REGION_CENTER_NAME                              ");
			sql.append("        , BRANCH_AREA_ID                                  ");
			sql.append("        , BRANCH_AREA_NAME                                ");   
			sql.append("        , BRANCH_NBR                                      ");
			sql.append("        , BRANCH_NAME                                     ");
			sql.append("        , EMP_ID                                          ");
			sql.append("        , EMP_NAME                                        ");
			sql.append("        , WORK_YY                                         ");
			sql.append("        , WORK_MM                                         ");
			sql.append("        , CONTRACT_STATE                                  ");
			sql.append("        , POLICY_YEAR                                     ");
			sql.append("        , POLICY_NOP                                      ");
			sql.append("        , INSURANCE                                       ");
			sql.append("        , INS_FEE                                         ");
			sql.append("        , CMSN_RATE                                       ");
			sql.append("        , CMSN                                            ");
			sql.append("        , CREATETIME                                      ");
			sql.append("  FROM TBPMS_LIFE_INS_BONUS_PF                            ");
			sql.append("  WHERE CONTRACT_STATE='有效'                              ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter                ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                    ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                         ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" AND EMP_ID = :EMP_ID 						         ");
				condition.setObject("EMP_ID", inputVO.getEmp_id());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :sTime                          ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  UNION ALL                                              ");
			sql.append("  SELECT YEARMON                                         ");
			//2017/06/17 補充欄位 START
			sql.append("       , '' AS POLICY_NO                                 ");
			sql.append("       , '' AS APPL_ID	                                 ");
			sql.append("       , '' AS APPL_NAME                                 ");
			//2017/06/17 補充欄位    END
			sql.append(" 	   , '' AS REGION_CENTER_ID						     ");
			sql.append("       , '' AS REGION_CENTER_NAME                        ");
			sql.append("       , '' AS BRANCH_AREA_ID                            ");
			sql.append("       , '' AS BRANCH_AREA_NAME                          ");
			sql.append("       , '' AS BRANCH_NBR                                ");
			sql.append("       , '' AS BRANCH_NAME                               ");
			sql.append("       , '' AS EMP_ID                                    ");
			sql.append("       , '' AS EMP_NAME                                  ");
			sql.append("       , '' AS WORK_YY                                   ");
			sql.append("       , '' AS WORK_MM                                   ");
			sql.append("       , '原始總保費／佣金收入合計' AS CONTRACT_STATE            ");
			sql.append("       , NULL                                            ");
			sql.append("       , NULL                                            ");
			sql.append("       , '' AS INSURANCE                                 ");
			sql.append("       , SUM (INS_FEE)                                   ");
			sql.append("       , NULL                                            ");
			sql.append("       , SUM (CMSN)                                      ");
			sql.append("       , MAX(CREATETIME)                                 ");
			sql.append("  FROM TBPMS_LIFE_INS_BONUS_PF                           ");
			sql.append("  WHERE CONTRACT_STATE='有效'                             ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter                ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                    ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                         ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" AND EMP_ID = :EMP_ID 						         ");
				condition.setObject("EMP_ID", inputVO.getEmp_id());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :sTime                          ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  GROUP BY YEARMON                                        ");
			sql.append("  UNION ALL                                               ");
			sql.append("  SELECT YEARMON					                      ");
			//2017/06/17 補充欄位 START
			sql.append("        , POLICY_NO                                       ");
			sql.append("        , APPL_ID	                                      ");
			sql.append("        , APPL_NAME                                       ");
			//2017/06/17 補充欄位    END
			sql.append("        , REGION_CENTER_ID                                ");
			sql.append("        , REGION_CENTER_NAME                              ");
			sql.append("        , BRANCH_AREA_ID                                  ");
			sql.append("        , BRANCH_AREA_NAME                                ");   
			sql.append("        , BRANCH_NBR                                      ");
			sql.append("        , BRANCH_NAME                                     ");
			sql.append("        , EMP_ID                                          ");
			sql.append("        , EMP_NAME                                        ");
			sql.append("        , WORK_YY                                         ");
			sql.append("        , WORK_MM                                         ");
			sql.append("        , CONTRACT_STATE                                  ");
			sql.append("        , POLICY_YEAR                                     ");
			sql.append("        , POLICY_NOP                                      ");
			sql.append("        , INSURANCE                                       ");
			sql.append("        , INS_FEE                                         ");
			sql.append("        , CMSN_RATE                                       ");
			sql.append("        , CMSN                                            ");
			sql.append("        , CREATETIME                                      ");
			sql.append("  FROM TBPMS_LIFE_INS_BONUS_PF                            ");
			sql.append("  WHERE CONTRACT_STATE='契約撤／取消'                         ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter                 ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                     ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                          ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" AND EMP_ID = :EMP_ID 					    	      ");
				condition.setObject("EMP_ID", inputVO.getEmp_id());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :sTime                           ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  UNION ALL                                              ");
			sql.append("  SELECT YEARMON                                         ");
			//2017/06/17 補充欄位 START
			sql.append("       , '' AS POLICY_NO                                       ");
			sql.append("       , '' AS APPL_ID	                                      ");
			sql.append("       , '' AS APPL_NAME                                       ");
			//2017/06/17 補充欄位    END
			sql.append(" 	   , '' AS REGION_CENTER_ID						     ");
			sql.append("       , '' AS REGION_CENTER_NAME                        ");
			sql.append("       , '' AS BRANCH_AREA_ID                            ");
			sql.append("       , '' AS BRANCH_AREA_NAME                          ");
			sql.append("       , '' AS BRANCH_NBR                                ");
			sql.append("       , '' AS BRANCH_NAME                               ");
			sql.append("       , '' AS EMP_ID                                    ");
			sql.append("       , '' AS EMP_NAME                                  ");
			sql.append("       , '' AS WORK_YY                                   ");
			sql.append("       , '' AS WORK_MM                                   ");
			sql.append("       , '扣除契約撤銷保費／佣金收入合計' AS CONTRACT_STATE        ");
			sql.append("       , NULL                                            ");
			sql.append("       , NULL                                            ");
			sql.append("       , '' AS INSURANCE                                 ");
			sql.append("       , SUM (INS_FEE)                                   ");
			sql.append("       , NULL                                            ");
			sql.append("       , SUM (CMSN)                                      ");
			sql.append("       , MAX(CREATETIME)                                 ");
			sql.append("  FROM TBPMS_LIFE_INS_BONUS_PF                           ");
			sql.append("  WHERE CONTRACT_STATE='契約撤／取消'                        ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter                ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                    ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                        ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" AND EMP_ID = :EMP_ID 					        	");
				condition.setObject("EMP_ID", inputVO.getEmp_id());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :sTime                         ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  GROUP BY YEARMON                                      ");
			sql.append("  UNION ALL                                              ");
			sql.append("  SELECT YEARMON                                         ");
			//2017/06/17 補充欄位 START
			sql.append("       , '' AS POLICY_NO                                       ");
			sql.append("       , '' AS APPL_ID	                                      ");
			sql.append("       , '' AS APPL_NAME                                       ");
			//2017/06/17 補充欄位    END
			sql.append(" 	   , '' AS REGION_CENTER_ID						     ");
			sql.append("       , '' AS REGION_CENTER_NAME                        ");
			sql.append("       , '' AS BRANCH_AREA_ID                            ");
			sql.append("       , '' AS BRANCH_AREA_NAME                          ");
			sql.append("       , '' AS BRANCH_NBR                                ");
			sql.append("       , '' AS BRANCH_NAME                               ");
			sql.append("       , '' AS EMP_ID                                    ");
			sql.append("       , '' AS EMP_NAME                                  ");
			sql.append("       , '' AS WORK_YY                                   ");
			sql.append("       , '' AS WORK_MM                                   ");
			sql.append("       , '總計' AS CONTRACT_STATE        ");
			sql.append("       , NULL                                            ");
			sql.append("       , NULL                                            ");
			sql.append("       , '' AS INSURANCE                                 ");
			sql.append("       , SUM (INS_FEE)                                   ");
			sql.append("       , NULL                                            ");
			sql.append("       , SUM (CMSN)                                      ");
			sql.append("       , MAX(CREATETIME)                                 ");
			sql.append("  FROM TBPMS_LIFE_INS_BONUS_PF                           ");
			sql.append("  WHERE 1=1                                              ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter                ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                    ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                        ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if(StringUtils.isNotBlank(inputVO.getEmp_id())){
				sql.append(" AND EMP_ID = :EMP_ID 					        	");
				condition.setObject("EMP_ID", inputVO.getEmp_id());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :sTime                         ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  GROUP BY YEARMON                                      ");
			
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			outputVO.setResultList(list);
			outputVO.setCsvList(list1);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord()-1;
			
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			//outputVO.setTotalFee(totalFee);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		PMS339OutputVO return_VO2 = (PMS339OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "房貸壽險獎勵金報表報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			roleflag=return_VO2.getFlag();
			int page_count=roleflag.equals("1")?12:11;
			for(int j = 0; j < list.size(); j++){
				int i = 0;
				String[] records = new String[page_count];
				    /**2017  / 06 / 27 新增 三欄位
				 	POLICY_NO		保單號碼
					APPL_ID	     	要保人ID
					APPL_NAME		要保人姓名
  					**/					
					records[i] =   checkIsNullAO(list.get(j), "POLICY_NO");           //保單號碼					
					records[++i] = checkIsNull(list.get(j), "APPL_ID");             //要保人ID	
					records[++i] = checkIsNull(list.get(j), "APPL_NAME");           //要保人姓名
					
					records[++i] = checkIsNull(list.get(j), "WORK_YY");             //工作年						
					records[++i] = checkIsNull(list.get(j), "WORK_MM");             //工作月	
					records[++i] = checkIsNull(list.get(j), "CONTRACT_STATE");      //契約狀態
					records[++i] = checkIsNull(list.get(j), "POLICY_YEAR");         //保單年度
					records[++i] = checkIsNull(list.get(j), "POLICY_NOP");          //保單期數
					records[++i] = checkIsNull(list.get(j), "INSURANCE");           //險種
					records[++i] = checkIsNull(list.get(j), "INS_FEE");             //保費
					if(roleflag.equals("1")){
						String persent=checkIsNull(list.get(j), "CMSN_RATE").toString().equals("")?"":"%";
						records[++i] = checkIsNull(list.get(j), "CMSN_RATE")+persent;           //佣金率
						records[++i] = checkIsNull(list.get(j), "CMSN");                //佣金
					}
				
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[page_count];
			int k = 0;
			/**2017  / 06 / 27 新增 三欄位
		 	POLICY_NO		保單號碼
			APPL_ID	     	要保人ID
			APPL_NAME		要保人姓名
				**/		
			csvHeader[k] = "保單號碼";						
			csvHeader[++k] = "要保人ID";							
			csvHeader[++k] = "要保人姓名";
			
			csvHeader[++k] = "工作年";						
			csvHeader[++k] = "工作月";							
			csvHeader[++k] = "契約狀態";
			csvHeader[++k] = "保單年度";
			csvHeader[++k] = "保單期數";
			csvHeader[++k] = "險種";
			csvHeader[++k] = "保費";
			if(roleflag.equals("1")){
				csvHeader[++k] = "佣金率";
				csvHeader[++k] = "佣金";
			}
					
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else {
			return_VO2.setResultList(list);
			this.sendRtnObject(return_VO2);
	    }
			
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS339OutputVO return_VO = new PMS339OutputVO();
		PMS339InputVO inputVO = (PMS339InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		String roleflag="0";
		try {
			sql.append("  SELECT COUNT(1) AS CNT														");
			sql.append("  FROM TBORG_MEMBER_ROLE                                                        ");
			if(StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINID))){
				sql.append(" WHERE EMP_ID = :user 				                                        ");
				queryCondition.setObject("user", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			}
			sql.append("        AND ROLE_ID IN(SELECT PARAM_CODE                                        ");
			sql.append("  				     FROM TBSYSPARAMETER                                        ");
			sql.append("  				     WHERE PARAM_TYPE = 'FUBONSYS.HEADMGR_ROLE')                ");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>>  roleList= dam.executeQuery(queryCondition);
			return_VO.setRole(roleList);
			roleflag=roleList.get(0).get("CNT").toString();
			this.sendRtnObject(return_VO);
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
	private String checkIsNullAO(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
		}
	}
}
package com.systex.jbranch.app.server.fps.pms704;

import java.text.NumberFormat;
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

import com.systex.jbranch.app.server.fps.pms704.PMS704;
import com.systex.jbranch.app.server.fps.pms704.PMS704InputVO;
import com.systex.jbranch.app.server.fps.pms704.PMS704OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : FCH轉介獎金查詢Controller<br>
 * Comments Name : PMS704.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月15日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月15日<br>
 */
@Component("pms704")
@Scope("request")
public class PMS704 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;

	private PMS704InputVO inputVO = null;

	private Logger logger = LoggerFactory.getLogger(PMS704.class);

	/**
	 * 查詢檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS704InputVO inputVO = (PMS704InputVO) body;
		PMS704OutputVO outputVO = new PMS704OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT RPT_YM                                          ");
			sql.append("  	    ,REGION_CENTER_ID                                ");
			sql.append("  	    ,REGION_CENTER_NAME                              ");
			sql.append("  	    ,BRANCH_AREA_ID                                  ");
			sql.append("  	    ,BRANCH_AREA_NAME                                ");
			sql.append("  	    ,BRANCH_NBR                                      ");
			sql.append("  	    ,BRANCH_NAME                                     ");
			sql.append("  	    ,REF_YM                                          ");
			sql.append("  	    ,CUST_ID                                         ");
			sql.append("  	    ,CUST_NAME                                       ");
			sql.append("  	    ,FCH_AOCODE                                      ");
			sql.append("  	    ,FCH_EMP_ID                                      ");
			sql.append("  	    ,FCH_EMP_NAME                                    ");
			sql.append("  	    ,TO_BRANCH_NBR                                   ");
			sql.append("  	    ,TO_BRANCH_NAME                                  ");
			sql.append("  	    ,TO_AOCODE                                       ");
			sql.append("  	    ,TO_EMP_ID                                       ");
			sql.append("  	    ,TO_EMP_NAME                                     ");
			sql.append("  	    ,AOVER_GAINS                                     ");
			sql.append("  	    ,FCH_BONUS                                       ");
			sql.append("  	    ,VERSION                                         ");
			sql.append("  	    ,CREATETIME                                      ");
			sql.append("  	    ,CREATOR                                         ");
			sql.append("  	    ,MODIFIER                                        ");
			sql.append("  	    ,LASTUPDATE                                      ");
			sql.append("  FROM TBPMS_FCH_REF_BONUS                              ");
			sql.append("  WHERE 1 = 1                                           ");
			
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter     		    ");
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
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" AND FCH_AOCODE = :FCH_AOCODE                                 ");
				condition.setObject("FCH_AOCODE", inputVO.getAo_code());
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(RPT_YM) = :sTime                          ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  order by RPT_YM, CUST_ID ");
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setResultList(list);
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
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
		PMS704OutputVO return_VO2 = (PMS704OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "FCH轉介獎金查詢_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[21];
				int i = 0;
				records[i] = checkIsNull(map, "RPT_YM");                 //計績年月
				records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");       //區域中心ID
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME");     //區域中心
				records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");         //營運區ID
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");       //營運區
				records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");             //分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");            //分行別
				records[++i] = checkIsNull(map, "REF_YM");                 //轉介年月
				records[++i] = checkIsNullAndTrans(map, "CUST_ID");                //客戶ID
				records[++i] = checkIsNull(map, "CUST_NAME");              //客戶姓名
				records[++i] = checkIsNullAndTrans(map, "FCH_AOCODE");             //FCH_AOCODE
				records[++i] = checkIsNullAndTrans(map, "FCH_EMP_ID");             //FCH專員
				records[++i] = checkIsNull(map, "FCH_EMP_NAME");           //FCH專員姓名
				records[++i] = checkIsNullAndTrans(map, "TO_BRANCH_NBR");          //客戶轉介後分行代碼
				records[++i] = checkIsNull(map, "TO_BRANCH_NAME");         //客戶轉介後分行別
				records[++i] = checkIsNullAndTrans(map, "TO_AOCODE");              //轉介後AO CODE
				records[++i] = checkIsNullAndTrans(map, "TO_EMP_ID");              //轉介後理專
				records[++i] = checkIsNull(map, "TO_EMP_NAME");            //轉介後理專名稱
				records[++i] = checkIsNull(map, "AOVER_GAINS");            //當月客戶加減碼後計績收益
				records[++i] = checkIsNull(map, "FCH_BONUS");              //當月FCH獎金
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[21];
			int j = 0;
			csvHeader[j] = "計績年月";
			csvHeader[++j] = "區域中心ID";
			csvHeader[++j] = "區域中心";
			csvHeader[++j] = "營運區ID";
			csvHeader[++j] = "營運區";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行別";
			csvHeader[++j] = "轉介年月";
			csvHeader[++j] = "客戶ID";
			csvHeader[++j] = "客戶姓名";
			csvHeader[++j] = "FCH_AOCODE";
			csvHeader[++j] = "FCH專員";
			csvHeader[++j] = "FCH專員姓名";
			csvHeader[++j] = "客戶轉介後分行代碼";
			csvHeader[++j] = "客戶轉介後分行別";
			csvHeader[++j] = "轉介後AO CODE";
			csvHeader[++j] = "轉介後理專";
			csvHeader[++j] = "轉介後理專名稱";
			csvHeader[++j] = "當月客戶加減碼後計績收益";
			csvHeader[++j] = "當月FCH獎金";
					
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
	* @param map
	* @return String
	*/
	private String checkIsNullAndTrans(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
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
}
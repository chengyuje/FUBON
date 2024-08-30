package com.systex.jbranch.app.server.fps.pms341;
import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms341.PMS341InputVO;
import com.systex.jbranch.app.server.fps.pms341.PMS341OutputVO;
import com.systex.jbranch.app.server.fps.pms341.PMS341;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 房貸火地險佣金明細Controller<br>
 * Comments Name : PMS341.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月16日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月16日<br>
 */
@Component("pms341")
@Scope("request")
public class PMS341 extends FubonWmsBizLogic 
{
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS341.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 員工編號
	 */
	public void empID(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS341OutputVO return_VO = new PMS341OutputVO();
		PMS341InputVO inputVO = (PMS341InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try 
		{
			sql.append("  SELECT 														");
			sql.append("         S.EMP_ID                                               ");
			sql.append("        ,S.EMP_NAME                                             ");
			sql.append("  FROM TBORG_MEMBER S                                           ");
			sql.append("  LEFT JOIN TBORG_MEMBER_ROLE R                                 ");
			sql.append("       ON S.EMP_ID = R.EMP_ID                                   ");
			sql.append("  WHERE R.ROLE_ID IN('ABRF')                                    ");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.executeQuery(queryCondition);
			return_VO.setEmpIDList(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) 
		{
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException 
	{		
		PMS341InputVO inputVO = (PMS341InputVO) body;
		PMS341OutputVO outputVO = new PMS341OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT C.YEARMON,								  ");
			sql.append("  	     C.REGION_CENTER_ID,                      ");
			sql.append("  	     C.REGION_CENTER_NAME,                    ");
			sql.append("  	     C.BRANCH_AREA_ID,                        ");
			sql.append("  	     C.BRANCH_AREA_NAME,                      ");
			sql.append("  	     C.BRANCH_NBR,                            ");
			sql.append("  	     C.BRANCH_NAME,                           ");
			sql.append("  	     C.EMP_ID,                                ");
			sql.append("  	     C.EMP_NAME,                              ");
			sql.append("  	     C.ID_CAT,                                ");
			sql.append("  	     P.PARAM_NAME,                            ");
			sql.append("  	     C.TOTAL,                                 ");
			sql.append("  	     C.BONUS,                                 ");
			sql.append("  	     C.CREATETIME,                            ");
			sql.append("         SUM( C.BONUS ) OVER () AS BONUS_NUM        ");
			sql.append("  FROM TBPMS_MRTG_FIRE_INS_CMSN C                 ");
			sql.append("  LEFT JOIN TBSYSPARAMETER P ON P.PARAM_CODE = C.ID_CAT");
			sql.append("                             AND P.PARAM_TYPE = 'PMS.ID_CAT2' ");
			sql.append("  WHERE 1=1                                     ");
			//資料統計日期
			if (inputVO.getsTime() != null)
			{
				sql.append(" AND YEARMON = :yyyy ");
				condition.setObject("yyyy", inputVO.getsTime());
			}	       
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter       ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea           ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if (!StringUtils.isBlank(inputVO.getEmp_id())) {
				sql.append(" AND EMP_ID = :empId                        ");
				condition.setObject("empId", inputVO.getEmp_id());
			}
			if (!StringUtils.isBlank(inputVO.getIdCat())) {
				sql.append(" AND ID_CAT = :idCat                        ");
				condition.setObject("idCat", inputVO.getIdCat());
			}
			sql.append("  ORDER BY ID_CAT                               ");
			condition.setQueryString(sql.toString());
			
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setResultList(list);
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());  // 當前頁次
			outputVO.setTotalPage(totalPage_i);                           // 總頁次
			outputVO.setTotalRecord(totalRecord_i);                       // 總筆數
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
	public void export(Object body, IPrimitiveMap header) throws JBranchException 
	{
		PMS341OutputVO return_VO2 = (PMS341OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		if(list.size() > 0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "房貸火地險佣金明細_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[5];
				int i = 0;
				records[i] = checkIsNull(map, "ID_CAT")+'-'+checkIsNull(map, "PARAM_NAME"); //身份類別
				records[++i] = checkIsNull(map, "EMP_NAME");                 //招攬人員姓名
				records[++i] = checkIsNullAndTrans(map, "EMP_ID");           //招攬人員員編
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");         //招攬人員區中心
				//records[++i] = checkIsNull(map, "TOTAL");                    //合計
				records[++i] = checkIsNull(map, "BONUS");                    //個人獎金(四捨五入)
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[5];
			int j = 0;
			csvHeader[j] = "身份類別";
			csvHeader[++j] = "姓名";
			csvHeader[++j] = "員編";
			csvHeader[++j] = "營運區";
			//csvHeader[++j] = "合計";
			csvHeader[++j] = "個人獎金(四捨五入)";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName);
			this.sendRtnObject(null);
		} else 
		{
			return_VO2.setResultList(list);
			this.sendRtnObject(return_VO2);
	    }
		
	}
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes", "unused" })
	public void addData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try 
		{
			PMS341InputVO inputVO = (PMS341InputVO) body;
			PMS341OutputVO outputVO = new PMS341OutputVO();
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_MRTG_FIRE_INS_CMSN_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			String lab = null;
			for(int a=0;a<sheet.length;a++)
			{
				for(int i=1;i<sheet[a].getRows();i++)
				{
					for(int j=0;j<sheet[a].getColumns();j++)
					{
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					
					//excel表格記行數
					flag++;
					
					//判斷當前上傳數據欄位個數是否一致
					
					if(list.size()!=4){
						throw new APException("上傳數據欄位個數不一致");
					}
					
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_MRTG_FIRE_INS_CMSN_U (YEARMON,	 ");
					sb.append("  		ID_CAT,            				             ");
					sb.append("  		EMP_NAME,           					     ");
					sb.append("  		EMP_ID,            			                 ");
//					sb.append("  		TOTAL,            					         ");
					sb.append("  		BONUS,            					         ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:ID_CAT,             				         ");
					sb.append("  		:EMP_NAME,             					     ");
					sb.append("  		:EMP_ID,             		                 ");
//					sb.append("  		:TOTAL,             			             ");
					sb.append("  		:BONUS,             			             ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",inputVO.getYearMon().trim()                );
					qc.setObject("ID_CAT",list.get(0).trim()                          );
					qc.setObject("EMP_NAME",list.get(1).trim()                        );
					qc.setObject("EMP_ID",list.get(2).trim()                          );
//					qc.setObject("TOTAL",list.get(3).trim()                           );
					//改為取三
					qc.setObject("BONUS",list.get(3).trim()                           );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)                     );
					qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)                     );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			//文檔上傳成功
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	/**
	 * 調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS341InputVO inputVO = (PMS341InputVO) body;
			PMS341OutputVO outputVO = new PMS341OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS719.SP_TBPMS_MRTG_FIRE_INS_CMSN(? ,? ) ");
			qc.setString(1, inputVO.getYearMon());
			qc.registerOutParameter(2, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(2);
			String[] strs = null;
			if(str!=null){
				strs = str.split("；");
				if(strs!=null&&strs.length>5){
					str = strs[0]+"；"+strs[1]+"；"+strs[2]+"；"+strs[3]+"；"+strs[4]+"...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
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
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNullAndTrans(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf("=\"" + map.get(key) + "\"");
		} else {
			return "";
		}
	}
	
	/**
	 * 下載
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downLoad(Object body, IPrimitiveMap header) throws Exception {
//		PMS713OutputVO return_VO2 = (PMS713OutputVO) body;
//		File filePath = new File(DataManager.getRealPath(), DataManager.getSystem().getPath().get("temp").toString());

		notifyClientToDownloadFile("doc//PMS//PMS341_EXAMPLE.xls", "房貸火地險佣金明細_上傳範例.xls"); //download
//		System.out.println(DataManager.getRealPath()+"doc\\PMS\\業務主管員額清單_上傳範例.xls");
		
		this.sendRtnObject(null);
	}
}
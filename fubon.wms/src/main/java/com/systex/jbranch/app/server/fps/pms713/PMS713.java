package com.systex.jbranch.app.server.fps.pms713;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 業務主管員額清單列表Controller<br>
 * Comments Name : PMS713.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年1月6日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年1月6日<br>
 */
@Component("pms713")
@Scope("request")
public class PMS713 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private PMS713InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS713.class);
	public void query(Object body, IPrimitiveMap header) throws Exception {
		PMS713InputVO inputVO = (PMS713InputVO) body;
		PMS713OutputVO outputVO = new PMS713OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("  SELECT YEARMON,                                      ");
			sql.append("  	     EMP_ID,                                       ");
			sql.append("  	     GROUP_TYPE,                                   ");
			sql.append("  	     FC1_CNT,                                      ");
			sql.append("  	     FC2_CNT,                                      ");
			sql.append("  	     FC3_CNT,                                      ");
			sql.append("  	     FC4_CNT,                                      ");
			sql.append("  	     FC5_CNT,                                      ");
			sql.append("  	     FCH_CNT,                                      ");
			sql.append("  	     VERSION,                                      ");
			sql.append("  	     CREATETIME,                                   ");
			sql.append("  	     CREATOR,                                      ");
			sql.append("  	     MODIFIER,                                     ");
			sql.append("  	     LASTUPDATE                                    ");
			sql.append("  FROM TBORG_SH_MBR_QUOTA                              ");
			sql.append("  WHERE 1=1                                            ");
			if (!StringUtils.isBlank(inputVO.getYearMon())) {
				sql.append(" AND YEARMON = :yearMon                            ");
				condition.setObject("yearMon", inputVO.getYearMon());
			}
			if (!StringUtils.isBlank(inputVO.getEmpId())) {
				sql.append(" AND EMP_ID = :empId                               ");
				condition.setObject("empId", inputVO.getEmpId());
			}
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
	 * 上傳
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		int flag = 0;
		try {
			PMS713InputVO inputVO = (PMS713InputVO) body;
			PMS713OutputVO outputVO = new PMS713OutputVO();
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBORG_SH_MBR_QUOTA_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
			int cell_check = 0;
			int col_cnt = 0; 
			for(int a=0;a<sheet.length;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					cell_check = 0;
					col_cnt = sheet[a].getColumns();
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
						
						//判斷是否有整筆ROW為空值
						if("".equals(lab)){
							cell_check++;
						}
					}
					
					//假如整列值都為空時, 則視為文件END
					if(col_cnt == cell_check){
						break;
					}
					
					//excel表格記行數
					flag++;
					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=8){
						throw new APException("上傳數據欄位個數不一致");
					}
					
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBORG_SH_MBR_QUOTA_U (YEARMON,	     ");
					sb.append("  		EMP_ID,            				             ");
					sb.append("  		GROUP_TYPE,            				         ");
					sb.append("  		FC1_CNT,           					         ");
					sb.append("  		FC2_CNT,            			             ");
					sb.append("  		FC3_CNT,            					     ");
					sb.append("  		FC4_CNT,            					     ");
					sb.append("  		FC5_CNT,            						 ");
					sb.append("  		FCH_CNT,             					     ");
					sb.append("  		RNUM,             					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:EMP_ID,             				         ");
					sb.append("  		NULL,    									 ");
					sb.append("  		:FC1_CNT,             					     ");
					sb.append("  		:FC2_CNT,             		                 ");
					sb.append("  		:FC3_CNT,             			             ");
					sb.append("  		:FC4_CNT,             					     ");
					sb.append("  		:FC5_CNT,           					     ");
					sb.append("  		:FCH_CNT,            					     ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		0,           					             ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",list.get(0).trim()                         );
					qc.setObject("EMP_ID",list.get(1).trim()                          );
//					qc.setObject("GROUP_TYPE",list.get(2).trim()                      );
					qc.setObject("FC1_CNT",list.get(2).trim()                         );
					qc.setObject("FC2_CNT",list.get(3).trim()                         );
					qc.setObject("FC3_CNT",list.get(4).trim()                         );
					qc.setObject("FC4_CNT",list.get(5).trim()                         );
					qc.setObject("FC5_CNT",list.get(6).trim()                         );
					qc.setObject("FCH_CNT", list.get(7).trim()                        );
					qc.setObject("RNUM", flag                                         );
					qc.setObject("CREATOR", inputVO.getUserId()                       );
					qc.setObject("MODIFIER", inputVO.getUserId()                      );
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
			PMS713InputVO inputVO = (PMS713InputVO) body;
			PMS713OutputVO outputVO = new PMS713OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS713.SP_TBORG_SH_MBR_QUOTA(? ,? ) ");
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
	 * 匯出
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downLoad(Object body, IPrimitiveMap header) throws JBranchException {
		PMS713OutputVO return_VO2 = (PMS713OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "業務主管員額清單列表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			String[] csvHeader = null;
			for(Map<String, Object> map : list){
				String[] records = new String[12];
				int i = 0;
				records[i] = checkIsNull(map, "EMP_ID");  				 //員工編號
				records[++i] = checkIsNull(map, "GROUP_TYPE");  		 //組別
				records[++i] = checkIsNull(map, "FC1_CNT");			     //FC1員額數
				records[++i] = checkIsNull(map, "FC2_CNT");			     //FC2員額數
				records[++i] = checkIsNull(map, "FC3_CNT");				 //FC3員額數
				records[++i] = checkIsNull(map, "FC4_CNT");  		     //FC4員額數
				records[++i] = checkIsNull(map, "FC5_CNT");			 	 //FC5員額數
				records[++i] = checkIsNull(map, "FCH_CNT");			     //FCH員額數
				records[++i] = checkIsNull(map, "CREATOR");				 //建立者
				records[++i] = checkIsNull(map, "CREATETIME");			 //建立日期						
				listCSV.add(records);
				//header
				csvHeader = new String[12];
				int j = 0;
				csvHeader[j] = "員工編號";
				csvHeader[++j] = "組別";
				csvHeader[++j] = "FC1員額數";
				csvHeader[++j] = "FC2員額數";
				csvHeader[++j] = "FC3員額數";
				csvHeader[++j] = "FC4員額數";
				csvHeader[++j] = "FC5員額數";
				csvHeader[++j] = "FCH員額數";
				csvHeader[++j] = "建立者";
				csvHeader[++j] = "建立日期";
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
	 * 下載
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downLoad1(Object body, IPrimitiveMap header) throws Exception {
//		PMS713OutputVO return_VO2 = (PMS713OutputVO) body;
//		File filePath = new File(DataManager.getRealPath(), DataManager.getSystem().getPath().get("temp").toString());

		notifyClientToDownloadFile("doc//PMS//PMS713_EXAMPLE.xls", "業務主管員額清單列表_上傳範例.xls"); //download
//		System.out.println(DataManager.getRealPath()+"doc\\PMS\\業務主管員額清單_上傳範例.xls");
		
		this.sendRtnObject(null);
	}

	
	public void del(Object body, IPrimitiveMap header) throws JBranchException {
		PMS713InputVO inputVO = (PMS713InputVO) body;
		PMS713OutputVO outputVO = new PMS713OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("	DELETE FROM TBORG_SH_MBR_QUOTA               ");
		sql.append("	WHERE 1=1                                    ");
		
		if (!StringUtils.isBlank(inputVO.getDelEmpId())) {
			sql.append(" AND EMP_ID = :empId                                              ");
			condition.setObject("empId", inputVO.getDelEmpId());
		}
		condition.setQueryString(sql.toString());
		dam.exeUpdate(condition);
		this.sendRtnObject(null);
	}
	/**
	* 檢查Map取出欄位是否為Null
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

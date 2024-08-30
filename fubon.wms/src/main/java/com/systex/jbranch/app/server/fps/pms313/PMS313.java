package com.systex.jbranch.app.server.fps.pms313;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.exportfile.EXPORTFILE;
import com.systex.jbranch.app.server.fps.pms354.PMS354OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :房信貸分行業績戰報Controller <br>
 * Comments Name : PMS313.java<br>
 * Author :frank<br>
 * Date :2016年07月12日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */

@Component("pms313")
@Scope("request")
public class PMS313 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PMS313.class);
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {		
		PMS313InputVO inputVO = (PMS313InputVO) body;
		PMS313OutputVO outputVO = new PMS313OutputVO();		
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, ");
		sql.append("BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
		sql.append("BRANCH_NBR,BRANCH_NAME, BRANCH_CLS, ");
		
		/** 房信貸分行業績戰報 **/
		if("RPT01".equals(inputVO.getsType())){
			sql.append("NVL(S_MRTG_HB_CNT,0) AS S_MRTG_HB_CNT, NVL(S_MRTG_HB_AMT,0) AS S_MRTG_HB_AMT, ");
			sql.append("NVL(S_MRTG_HB_TAR,0) AS S_MRTG_HB_TAR, NVL(S_MRTG_HB_RATE,0) AS S_MRTG_HB_RATE, ");
			sql.append("NVL(S_MRTG_NHB_CNT,0) AS S_MRTG_NHB_CNT, NVL(S_MRTG_NHB_AMT,0) AS S_MRTG_NHB_AMT, ");
			sql.append("NVL(S_MRTG_NHB_TAR,0) AS S_MRTG_NHB_TAR, NVL(S_MRTG_NHB_RATE,0) AS S_MRTG_NHB_RATE, ");
			sql.append("NVL(S_MRTG_T_HB_CNT,0) AS S_MRTG_T_HB_CNT, NVL(S_MRTG_T_HB_AMT,0) AS S_MRTG_T_HB_AMT, ");
			sql.append("NVL(S_MRTG_T_HB_TAR,0) AS S_MRTG_T_HB_TAR, NVL(S_MRTG_T_HB_RATE,0) AS S_MRTG_T_HB_RATE, ");
			sql.append("NVL(R_MRTG_CNT,0) AS R_MRTG_CNT,NVL(R_MRTG_AMT,0) AS R_MRTG_AMT, NVL(R_MRTG_TAR,0) AS R_MRTG_TAR, ");
			sql.append("NVL(R_MRTG_RATE,0) AS R_MRTG_RATE, NVL(MRTG_T_CNT,0) AS MRTG_T_CNT, NVL(MRTG_T_AMT,0) AS MRTG_T_AMT, ");
			sql.append("NVL(MRTG_T_TAR,0) AS MRTG_T_TAR, NVL(MRTG_T_RATE,0) AS MRTG_T_RATE, MRANK, ");
			sql.append("NVL(CREDIT_N_CNT,0) AS CREDIT_N_CNT, NVL(CREDIT_N_AMT,0) AS CREDIT_N_AMT, ");
			sql.append("NVL(CREDIT_N_TAR,0) AS CREDIT_N_TAR, NVL(CREDIT_N_RATE,0) AS CREDIT_N_RATE, ");
			sql.append("NVL(CREDIT_G_CNT,0) AS CREDIT_G_CNT, NVL(CREDIT_G_AMT,0) AS CREDIT_G_AMT, ");
			sql.append("NVL(CREDIT_G_TAR,0) AS CREDIT_G_TAR, NVL(CREDIT_G_RATE,0) AS CREDIT_G_RATE, ");
			sql.append("NVL(CREDIT_C_CNT,0) AS CREDIT_C_CNT, NVL(CREDIT_C_AMT,0) AS CREDIT_C_AMT, ");
			sql.append("NVL(CREDIT_C_TAR,0) AS CREDIT_C_TAR, NVL(CREDIT_C_RATE,0) AS CREDIT_C_RATE, ");
			sql.append("NVL(W_CREDIT_CNT,0) AS W_CREDIT_CNT, NVL(W_CREDIT_AMT,0) AS W_CREDIT_AMT, ");
			sql.append("NVL(CREDIT_T_CNT,0) AS CREDIT_T_CNT, NVL(CREDIT_T_AMT,0) AS CREDIT_T_AMT, ");
			sql.append("NVL(CREDIT_T_TAR,0) AS CREDIT_T_TAR, NVL(CREDIT_T_RATE,0) AS CREDIT_T_RATE, CRANK, CREATETIME ");
			sql.append("FROM TBPMS_MC_BR_RESULT_MTD ");
		}
		/** 好運貸業績戰報 **/
		if("RPT02".equals(inputVO.getsType())){
			sql.append("NVL(MRTG_RATE_MTD,0) AS MRTG_RATE_MTD, NVL(MTD_CNT,0) AS MTD_CNT, ");
			sql.append("NVL(MTD_FEE,0) AS MTD_FEE, NVL(MTD_TAR,0) AS MTD_TAR, ");
			sql.append("NVL(MTD_RATE,0) AS MTD_RATE, MTD_RANK, ");
			sql.append("NVL(MRTG_RATE_YTM,0) AS MRTG_RATE_YTM, NVL(YTD_CNT,0) AS YTD_CNT, ");
			sql.append("NVL(YTD_FEE,0) AS YTD_FEE, NVL(YTD_TAR,0) AS YTD_TAR, ");
			sql.append("NVL(YTD_RATE,0) AS YTD_RATE, YTD_RANK, CREATETIME ");
			sql.append("FROM TBPMS_SPM_RESULT ");			
		}
		/** 房貸法金+個金報表 **/
		if("RPT03".equals(inputVO.getsType())){
			sql.append("NVL(HB_PG_CNT,0) AS HB_PG_CNT, NVL(HB_PG_AMT,0) AS HB_PG_AMT, NVL(HB_PG_TAR,0) AS HB_PG_TAR, ");
			sql.append("NVL(HB_PG_RATE,0) AS HB_PG_RATE, NVL(HB_FG_CNT,0) AS HB_FG_CNT, NVL(HB_FG_AMT,0) AS HB_FG_AMT, ");
			sql.append("NVL(HB_FG_TAR,0) AS HB_FG_TAR, NVL(HB_FG_RATE,0) AS HB_FG_RATE, NVL(HB_TT_CNT,0) AS HB_TT_CNT, ");
			sql.append("NVL(HB_TT_AMT,0) AS HB_TT_AMT, NVL(HB_TT_TAR,0) AS HB_TT_TAR, NVL(HB_TT_RATE,0) AS HB_TT_RATE, ");
			sql.append("NVL(NHB_PG_CNT,0) AS NHB_PG_CNT, NVL(NHB_PG_AMT,0) AS NHB_PG_AMT, NVL(NHB_PG_TAR,0) AS NHB_PG_TAR, ");
			sql.append("NVL(NHB_PG_RATE,0) AS NHB_PG_RATE, NVL(NHB_FG_CNT,0) AS NHB_FG_CNT, NVL(NHB_FG_AMT,0) AS NHB_FG_AMT, ");
			sql.append("NVL(NHB_FG_TAR,0) AS NHB_FG_TAR, NVL(NHB_FG_RATE,0) AS NHB_FG_RATE, NVL(NHB_TT_CNT,0) AS NHB_TT_CNT, ");
			sql.append("NVL(NHB_TT_AMT,0) AS NHB_TT_AMT, NVL(NHB_TT_TAR,0) AS NHB_TT_TAR, NVL(NHB_TT_RATE,0) AS NHB_TT_RATE, ");
			sql.append("NVL(F_PG_CNT,0) AS F_PG_CNT, NVL(F_PG_AMT,0) AS F_PG_AMT, NVL(F_PG_TAR,0) AS F_PG_TAR, ");
			sql.append("NVL(F_PG_RATE,0) AS F_PG_RATE, NVL(F_FG_CNT,0) AS F_FG_CNT, NVL(F_FG_AMT,0) AS F_FG_AMT, ");
			sql.append("NVL(F_TT_CNT,0) AS F_TT_CNT, NVL(F_TT_AMT,0) AS F_TT_AMT, NVL(F_TT_TAR,0) AS F_TT_TAR, ");
			sql.append("NVL(F_TT_RATE,0) AS F_TT_RATE,CREATETIME ");
			sql.append("FROM TBPMS_MRTG_FG_PG_MTD ");
		}
		sql.append("WHERE YEARMON = :yrmn  AND BRANCH_NBR >= '200' AND BRANCH_NBR <= '900'  ");		
		sql.append("AND TO_NUMBER(BRANCH_NBR) <> 806 AND TO_NUMBER(BRANCH_NBR) <> 810  ");		
		//區域中心
		if(StringUtils.isNotBlank(inputVO.getRegion_center_id())){
			sql.append("and REGION_CENTER_ID = :rcid ");			
		}
		//營運區
		if(StringUtils.isNotBlank(inputVO.getBranch_area_id())){
			sql.append("and BRANCH_AREA_ID = :opid ");		
		}
		//分行
		if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
			sql.append("and BRANCH_NBR = :brid  ");	//2017/05/20增加分行田建	
		}	
		sql.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
			
		condition.setQueryString(sql.toString());
		condition.setObject("yrmn", inputVO.getsCreDate());		
		if(inputVO.getRegion_center_id() != null && !inputVO.getRegion_center_id().equals("")){
			condition.setObject("rcid", inputVO.getRegion_center_id());
		}
		if(inputVO.getBranch_area_id() != null && !inputVO.getBranch_area_id().equals("")){
			condition.setObject("opid", inputVO.getBranch_area_id());
		}
		if(inputVO.getBranch_nbr() != null && !inputVO.getBranch_nbr().equals("")){
			condition.setObject("brid", inputVO.getBranch_nbr());
		}
		
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setResultList(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}
			
	
	/*  === 產出EXCEL==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException, Exception {
		PMS313OutputVO outputVO = (PMS313OutputVO) body;		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "RPT01".equals(outputVO.getRptType()) ? "房信貸分行業績戰報_" + sdf.format(new Date()) : 
						 ("RPT02".equals(outputVO.getRptType()) ? "好運貸業績戰報_" + sdf.format(new Date())     : 
						 ("RPT03".equals(outputVO.getRptType()) ? "房貸法金+個金報表_" + sdf.format(new Date())  : ""));
		List<Map<String, Object>> list = outputVO.getResultList();
		EXPORTFILE test = (EXPORTFILE) PlatformContext.getBean("exportfile");
		Map<String, String> file = new HashMap<String, String>();
        Map<String, String> order = new LinkedHashMap<String, String>();
        
        /** 房信貸分行業績戰報 **/
        if("RPT01".equals(outputVO.getRptType())){
        	order.put("REGION_CENTER_NAME", "業務處"						);	
        	order.put("BRANCH_AREA_NAME"  , "營運區"						);
        	order.put("BRANCH_NBR"        , "分行別"						);
        	order.put("BRANCH_CLS"        , "組別"						    );
        	order.put("BRANCH_NAME"       , "分行名稱"					    );
        	order.put("S_MRTG_HB_CNT"     , "分期型房貸_購屋_件數"		    );
        	order.put("S_MRTG_HB_AMT"     , "分期型房貸_購屋_金額"		    );
        	order.put("S_MRTG_HB_TAR"     , "分期型房貸_購屋_目標"		    );
        	order.put("S_MRTG_HB_RATE"    , "分期型房貸_購屋_達成率"		);
        	order.put("S_MRTG_NHB_CNT"    , "分期型房貸_非購屋_件數"		);
        	order.put("S_MRTG_NHB_AMT"    , "分期型房貸_非購屋_金額"        );
        	order.put("S_MRTG_NHB_TAR"    , "分期型房貸_非購屋_目標"        );
        	order.put("S_MRTG_NHB_RATE"   , "分期型房貸_非購屋_達成率"      );
        	order.put("S_MRTG_T_HB_CNT"   , "分期型房貸合計_件數"			);
        	order.put("S_MRTG_T_HB_AMT"   , "分期型房貸合計_金額"           );
        	order.put("S_MRTG_T_HB_TAR"   , "分期型房貸合計_目標"           );
        	order.put("S_MRTG_T_HB_RATE"  , "分期型房貸合計_達成率"         );
        	order.put("R_MRTG_CNT"        , "循環型房貸_件數"				);
        	order.put("R_MRTG_AMT"        , "循環型房貸_金額"               );
        	order.put("R_MRTG_TAR"        , "循環型房貸_目標"               );
        	order.put("R_MRTG_RATE"       , "循環型房貸_達成率"             );
        	order.put("MRTG_T_CNT"        , "房貸合計_件數"				    );
        	order.put("MRTG_T_AMT"        , "房貸合計_金額"                 );
        	order.put("MRTG_T_TAR"        , "房貸合計_目標"                 );
        	order.put("MRTG_T_RATE"       , "房貸合計_達成率"               );
        	order.put("MRANK"             , "房貸分行分組排名"			    );
        	order.put("CREDIT_N_CNT"      , "信貸_一般_件數"				);
        	order.put("CREDIT_N_AMT"      , "信貸_一般_金額"				);
        	order.put("CREDIT_N_TAR"      , "信貸_一般_目標"				);
        	order.put("CREDIT_N_RATE"     , "信貸_一般_達成率"			    );
        	order.put("CREDIT_G_CNT"      , "信貸_職團_件數"				);
        	order.put("CREDIT_G_AMT"      , "信貸_職團_金額"                );
        	order.put("CREDIT_G_TAR"      , "信貸_職團_目標"                );
        	order.put("CREDIT_G_RATE"     , "信貸_職團_達成率"              );
        	order.put("CREDIT_C_CNT"      , "信貸_卡友_件數"				);
        	order.put("CREDIT_C_AMT"      , "信貸_卡友_金額"                );
        	order.put("CREDIT_C_TAR"      , "信貸_卡友_目標"                );
        	order.put("CREDIT_C_RATE"     , "信貸_卡友_達成率"              );
        	order.put("W_CREDIT_CNT"      , "認股信貸_件數"				    );
        	order.put("W_CREDIT_AMT"      , "認股信貸_金額"				    );
        	order.put("CREDIT_T_CNT"      , "信貸(不含認股信貸)合計_件數"	);
        	order.put("CREDIT_T_AMT"      , "信貸(不含認股信貸)合計_金額"   );
        	order.put("CREDIT_T_TAR"      , "信貸(不含認股信貸)合計_目標"   );
        	order.put("CREDIT_T_RATE"     , "信貸(不含認股信貸)合計_達成率" );
        	order.put("CRANK"             , "信貸分行分組排名"			    );  
        }
        
        /** 好運貸業績戰報 **/
        if("RPT02".equals(outputVO.getRptType())){
        	order.put("REGION_CENTER_NAME"	,"業務處"						);
        	order.put("BRANCH_AREA_NAME"    ,"營運區"						);
        	order.put("BRANCH_NBR"          ,"分行別"                       );
        	order.put("BRANCH_CLS"          ,"組別"                         );
        	order.put("BRANCH_NAME"         ,"分行名稱"                     );
        	order.put("MRTG_RATE_MTD"       ,"MTD_房貸中長期撥款達成率(MTD)");
        	order.put("MTD_CNT"             ,"MTD_好運貸_件數"              );
        	order.put("MTD_FEE"             ,"MTD_好運貸_保費"              );
        	order.put("MTD_TAR"             ,"MTD_好運貸_目標"              );
        	order.put("MTD_RATE"            ,"MTD_好運貸_MTD達成率"         );
        	order.put("MTD_RANK"            ,"MTD分行分組排名"              );
        	order.put("MRTG_RATE_YTM"       ,"YTD_房貸分期型撥款達成率(YTM)");
        	order.put("YTD_CNT"             ,"YTD_好運貸_件數"              );
        	order.put("YTD_FEE"             ,"YTD_好運貸_保費"              );
        	order.put("YTD_TAR"             ,"YTD_好運貸_目標"              );
        	order.put("YTD_RATE"            ,"YTD_好運貸_YTD達成率"         );
        	order.put("YTD_RANK"            ,"YTD分行分組排名"              );
        }
        
        /** 房貸法金+個金報表 **/
        if("RPT03".equals(outputVO.getRptType())){
        	order.put("REGION_CENTER_NAME", "業務處"				);
        	order.put("BRANCH_AREA_NAME"  , "營運區"			    );
        	order.put("BRANCH_NBR"        , "分行別"                );
        	order.put("BRANCH_CLS"        , "組別"                  );
        	order.put("BRANCH_NAME"       , "分行名稱"              );
        	order.put("HB_PG_CNT"         , "購屋_個金_件數"        );
        	order.put("HB_PG_AMT"         , "購屋_個金_金額"        );
        	order.put("HB_PG_TAR"         , "購屋_個金_目標"        );
        	order.put("HB_PG_RATE"        , "購屋_個金_達成率"      );
        	order.put("HB_FG_CNT"         , "購屋_法金_件數"        );
        	order.put("HB_FG_AMT"         , "購屋_法金_金額"        );
        	order.put("HB_FG_TAR"         , "購屋_法金_目標"        );
        	order.put("HB_FG_RATE"        , "購屋_法金_達成率"      );
        	order.put("HB_TT_CNT"         , "購屋合計_件數"         );
        	order.put("HB_TT_AMT"         , "購屋合計_金額"         );
        	order.put("HB_TT_TAR"         , "購屋合計_目標"         );
        	order.put("HB_TT_RATE"        , "購屋合計_達成率"       );
        	order.put("NHB_PG_CNT"        , "非購屋_個金_件數"      );
        	order.put("NHB_PG_AMT"        , "非購屋_個金_金額"      );
        	order.put("NHB_PG_TAR"        , "非購屋_個金_目標"      );
        	order.put("NHB_PG_RATE"       , "非購屋_個金_達成率"    );
        	order.put("NHB_FG_CNT"        , "非購屋_法金_件數"      );
        	order.put("NHB_FG_AMT"        , "非購屋_法金_金額"      );
        	order.put("NHB_FG_TAR"        , "非購屋_法金_目標"      );
        	order.put("NHB_FG_RATE"       , "非購屋_法金_達成率"    );
        	order.put("NHB_TT_CNT"        , "非購屋合計_件數"       );
        	order.put("NHB_TT_AMT"        , "非購屋合計_金額"       );
        	order.put("NHB_TT_TAR"        , "非購屋合計_目標"       );
        	order.put("NHB_TT_RATE"       , "非購屋合計_達成率"     );
        	order.put("F_PG_CNT"		  , "額度式_個金_件數"      );
        	order.put("F_PG_AMT"          , "額度式_個金_金額"      );
        	order.put("F_PG_TAR"          , "額度式_個金_目標"      );
        	order.put("F_PG_RATE"         , "額度式_個金_達成率"    );
        	order.put("F_FG_CNT"          , "額度式_法金_件數"      );
        	order.put("F_FG_AMT"          , "額度式_法金_金額"      );
        	order.put("F_TT_CNT"          , "額度式合計_件數"       );
        	order.put("F_TT_AMT"          , "額度式合計_金額"       );
        	order.put("F_TT_TAR"          , "額度式合計_目標"       );
        	order.put("F_TT_RATE"         , "額度式合計_達成率"     );
        }
        
        file = test.exportxlsx_cname(fileName, list, order);
		this.sendRtnObject("downloadFile", file);
	}
	
//	/**
//	* 檢查Map取出欄位是否為Null
//	* 
//	* @param map
//	* @return String
//	*/
//	private String checkIsNull(Map map, String key) {
//		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
//			return String.valueOf(map.get(key));
//		}else{
//			return "";
//		}
//	}
//	//處理貨幣格式
//	private String currencyFormat(Map map, String key){		
//		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
//			NumberFormat nf = NumberFormat.getCurrencyInstance();
//			return nf.format(map.get(key));										
//		}else
//			return "$0.00";		
//	}
//
//	//達成率格式
//	private String pcntFormat(Map map, String key){		
//		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){				
//			return (int)(Float.parseFloat(map.get(key)+"")+0.5)+"%";										
//		}else
//			return "";		
//	}
}
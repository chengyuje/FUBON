package com.systex.jbranch.app.server.fps.pms210;

import java.io.File;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms210.PMS210InputVO;
import com.systex.jbranch.app.server.fps.pms210.PMS210OutputVO;
import com.systex.jbranch.app.server.fps.pms226.PMS226OutputVO;
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
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : pms210.java<br>
 * Author :KXJ<br>
 * Date :2016年11月14日 <br>
 * Version : 1.01 <br>
 * Editor : KXJ<br>
 * Editor Date : 2016年11月14日<br>
 */
@Component("pms210")
@Scope("request")
public class PMS210 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS210.class);

	/**
	 * 查詢當月份參數維護（主表）
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT SP_ASS,                ");
		sb.append("         SP_BEN,                ");
		sb.append("         SP_LEVD,               ");
		sb.append("         BS_GOAL,               ");
		sb.append("         SP_BOUNS,              ");
		sb.append("         NEW_CUST_RATE,         ");
		sb.append("         NEW_SP_BEN,            ");
		sb.append("         NEW_BS_GOAL,           ");
		sb.append("         SP_SAL_BEN,            ");
		sb.append("         NEW_BOUNTY,            ");
		sb.append("         BS_ASS,                ");
		sb.append("         BS_AUM,                ");
		sb.append("         BS_POINT,              ");
		sb.append("         BS_PRIZE,              ");
		sb.append("         BS_GOAL_TAR,           ");
		sb.append("         BS_BONUS_INSE,         ");
		sb.append("         BONUS_DATE,            ");
		sb.append("         SHORT_TRAN,            ");
		sb.append("         SP_MAR,                ");
		sb.append("         SP_UN_LACK_IND,        ");
		sb.append("         INS_DATE,              ");
		sb.append("         CON_NUM,               ");
		sb.append("         DIS_RATE,              ");
		sb.append("         SUBSTR(FCH_BOUNTY_RATE,1,LENGTH(FCH_BOUNTY_RATE)-1) * 100 || SUBSTR(FCH_BOUNTY_RATE,-1) AS FCH_BOUNTY_RATE,        ");
		sb.append("         SUBSTR(GOODS_BOUNTY_RATE,1,LENGTH(GOODS_BOUNTY_RATE)-1) * 100 || SUBSTR(GOODS_BOUNTY_RATE,-1) AS GOODS_BOUNTY_RATE,     ");
		sb.append("         DEPOSIT_RATE,          ");
		sb.append("         YEARMON                ");

		sb.append("  FROM TBPMS_CNR_MAST");

		sb.append("    WHERE YEARMON = :YEARMON");
		qc.setObject("YEARMON", inputVO.getYearMon());

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setResultList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 儲存主頁面的修改（主表）
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveChange(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  UPDATE TBPMS_CNR_MAST SET                    ");
		sb.append("         SP_ASS=:SP_ASS,                       ");
		sb.append("         SP_BEN=:SP_BEN,                       ");
		sb.append("         SP_LEVD=:SP_LEVD,                     ");
		sb.append("         BS_GOAL=:BS_GOAL,                     ");
		sb.append("         SP_BOUNS=:SP_BOUNS,                   ");
		sb.append("         NEW_CUST_RATE=:NEW_CUST_RATE,         ");
		sb.append("         NEW_SP_BEN=:NEW_SP_BEN,               ");
		sb.append("         NEW_BS_GOAL=:NEW_BS_GOAL,             ");
		sb.append("         SP_SAL_BEN=:SP_SAL_BEN,               ");
		sb.append("         NEW_BOUNTY=:NEW_BOUNTY,               ");
		sb.append("         FCH_BOUNTY_RATE=:FCH_BOUNTY_RATE,     ");
		sb.append("         GOODS_BOUNTY_RATE=:GOODS_BOUNTY_RATE, ");
		sb.append("         BS_ASS=:BS_ASS,                       ");
		sb.append("         BS_AUM=:BS_AUM,                       ");
		sb.append("         BS_POINT=:BS_POINT,                   ");
		sb.append("         BS_PRIZE=:BS_PRIZE,                   ");
		sb.append("         BS_GOAL_TAR=:BS_GOAL_TAR,             ");
		sb.append("         BS_BONUS_INSE=:BS_BONUS_INSE,         ");
		sb.append("         BONUS_DATE=:BONUS_DATE, 			  ");
		sb.append("         SHORT_TRAN=:SHORT_TRAN,               ");
		sb.append("         SP_MAR=:SP_MAR,                       ");
		sb.append("         SP_UN_LACK_IND=:SP_UN_LACK_IND,       ");
		sb.append("         INS_DATE=LPAD(:INS_DATE, 3, '0'),  	  ");
		sb.append("         CON_NUM=:CON_NUM,                     ");
		sb.append("         DIS_RATE=:DIS_RATE,                   ");
		sb.append("         DEPOSIT_RATE=:DEPOSIT_RATE            ");
		String str = inputVO.getFCH_BOUNTY_RATE();
		float fch = Float.parseFloat(str.substring(0, str.length()-1)) / 100;
		String fchStr = fch + str.substring(str.length()-1,str.length());
		
		String str1 = inputVO.getGOODS_BOUNTY_RATE();
		float goods = Float.parseFloat(str1.substring(0, str1.length()-1)) / 100;
		String goodsStr = goods + str1.substring(str1.length()-1,str1.length());
		qc.setObject("SP_ASS", inputVO.getSP_ASS());
		qc.setObject("SP_BEN", inputVO.getSP_BEN());
		qc.setObject("SP_LEVD", inputVO.getSP_LEVD());
		qc.setObject("BS_GOAL", inputVO.getBS_GOAL());
		qc.setObject("SP_BOUNS", inputVO.getSP_BOUNS());
		qc.setObject("NEW_CUST_RATE", inputVO.getNEW_CUST_RATE());
		qc.setObject("NEW_SP_BEN", inputVO.getNEW_SP_BEN());
		qc.setObject("NEW_BS_GOAL", inputVO.getNEW_BS_GOAL());
		qc.setObject("SP_SAL_BEN", inputVO.getSP_SAL_BEN());
		qc.setObject("NEW_BOUNTY", inputVO.getNEW_BOUNTY());
		qc.setObject("FCH_BOUNTY_RATE", fchStr);
		qc.setObject("GOODS_BOUNTY_RATE", goodsStr);
		qc.setObject("BS_ASS", inputVO.getBS_ASS());
		qc.setObject("BS_AUM", inputVO.getBS_AUM());
		qc.setObject("BS_POINT", inputVO.getBS_POINT());
		qc.setObject("BS_PRIZE", inputVO.getBS_PRIZE());
		qc.setObject("BS_GOAL_TAR", inputVO.getBS_GOAL_TAR());
		qc.setObject("BS_BONUS_INSE", inputVO.getBS_BONUS_INSE());
		qc.setObject("BONUS_DATE", inputVO.getBONUS_DATE());
		qc.setObject("SHORT_TRAN", inputVO.getSHORT_TRAN());
		qc.setObject("SP_MAR", inputVO.getSP_MAR());
		qc.setObject("SP_UN_LACK_IND", inputVO.getSP_UN_LACK_IND());
		qc.setObject("INS_DATE", inputVO.getINS_DATE());
		qc.setObject("CON_NUM", inputVO.getCON_NUM());
		qc.setObject("DIS_RATE", inputVO.getDIS_RATE());
		qc.setObject("DEPOSIT_RATE", inputVO.getDEPOSIT_RATE());

		sb.append("    WHERE YEARMON = :YEARMON");
		qc.setObject("YEARMON", inputVO.getYearMon());

		qc.setQueryString(sb.toString());
		int result = dam.exeUpdate(qc);
		outputVO.setBackResult(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢及設置理專考核目標及配分(理專 參數1)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void querySpAss(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT YEARMON                                       ");
		sb.append("         ,AO_JOB_RANK                                  ");
		sb.append("         ,STAT_GOAL                                    ");
		sb.append("         ,STAT_GOAL_UP*100 AS STAT_GOAL_UP             ");
		sb.append("         ,STAT_GOAL_SC                                 ");
		sb.append("         ,DEPOSIT_TH                                   ");
		sb.append("         ,DEPOSIT_TD                                   ");
		sb.append("         ,DEPOSIT_WH                                   ");
		sb.append("         ,DEPOSIT_WD                                   ");
		sb.append("         ,FIN_IND_EIP                                  ");
		sb.append("         ,FIN_IND_NEW                                  ");
		sb.append("         ,FIN_IND_UP*100 AS FIN_IND_UP                 ");         
		sb.append("         ,FIN_IND_SC                                   ");
		sb.append("         ,FIN_IND_DEP_TAR                              ");
		sb.append("         ,FIN_IND_DEP_AUN_UP*100 AS FIN_IND_DEP_AUN_UP ");
		sb.append("         ,FIN_IND_DEP_AUN_SC                           ");
		sb.append("         ,FIN_IND_INS_TAR                              ");
		sb.append("         ,FIN_IND_INS_AUN_UP*100 AS FIN_IND_INS_AUN_UP ");
		sb.append("         ,FIN_IND_INS_AUN_SC                           ");
		sb.append("         ,FIN_KQ_INS_TAR                               ");
		sb.append("         ,FIN_KQ_INS_AUN_UP                            ");
		sb.append("         ,FIN_KQ_INS_AUN_SC                            ");
		sb.append("         ,FIN_CUST_AUM_UP                              ");
		sb.append("         ,NFIN_IND_SAT                                 ");
		sb.append("         ,NFIN_IND_DECR                                ");
		sb.append("         ,NFIN_IND_CHECK                               ");
		sb.append("         ,NFIN_IND_KNCUST                              ");
		sb.append("         ,NFIN_IND_ABN                                 ");
		sb.append("         ,NFIN_LACK_IND                                ");
		sb.append("  FROM TBPMS_CNR_PD_SP_ASS"                             );
		sb.append("    WHERE YEARMON = :YEARMON"                           );
		qc.setObject("YEARMON", inputVO.getYearMon()                       );
		sb.append("    ORDER BY AO_JOB_RANK"                               );
		qc.setQueryString(sb.toString()                                    );
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setSpAssList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 修改并保存理專考核目標及配分
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveSpAss(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		int rst = 0;

		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("MERGE INTO TBPMS_CNR_PD_SP_ASS MU                                                                                                          ");
		sb.append("USING(SELECT :YEARMON YEARMON,:AO_JOB_RANK AO_JOB_RANK,:STAT_GOAL STAT_GOAL,:STAT_GOAL_UP STAT_GOAL_UP,:STAT_GOAL_SC STAT_GOAL_SC          ");
		sb.append("            ,:DEPOSIT_TH DEPOSIT_TH,:DEPOSIT_TD DEPOSIT_TD,:DEPOSIT_WH DEPOSIT_WH,:DEPOSIT_WD DEPOSIT_WD,:FIN_IND_EIP FIN_IND_EIP,:FIN_IND_NEW FIN_IND_NEW,:FIN_IND_UP FIN_IND_UP,:FIN_IND_SC FIN_IND_SC  ");
		sb.append("            ,:FIN_IND_DEP_TAR FIN_IND_DEP_TAR,:FIN_IND_DEP_AUN_UP FIN_IND_DEP_AUN_UP,:FIN_IND_DEP_AUN_SC FIN_IND_DEP_AUN_SC                ");
		sb.append("            ,:FIN_IND_INS_TAR FIN_IND_INS_TAR,:FIN_IND_INS_AUN_UP FIN_IND_INS_AUN_UP,:FIN_IND_INS_AUN_SC FIN_IND_INS_AUN_SC                ");
		sb.append("            ,:FIN_KQ_INS_TAR FIN_KQ_INS_TAR,:FIN_KQ_INS_AUN_UP FIN_KQ_INS_AUN_UP,:FIN_KQ_INS_AUN_SC FIN_KQ_INS_AUN_SC                      ");
		sb.append("            ,:FIN_CUST_AUM_UP FIN_CUST_AUM_UP,:NFIN_IND_SAT NFIN_IND_SAT,:NFIN_IND_DECR NFIN_IND_DECR,:NFIN_IND_CHECK NFIN_IND_CHECK       ");
		sb.append("            ,:NFIN_IND_KNCUST NFIN_IND_KNCUST,:NFIN_IND_ABN NFIN_IND_ABN,:NFIN_LACK_IND NFIN_LACK_IND,:EMP_ID EMP_ID                       ");
		sb.append("      FROM DUAL)SO                                                                                               ");
		sb.append("      ON (SO.YEARMON = MU.YEARMON AND SO.AO_JOB_RANK = MU.AO_JOB_RANK)                                           ");
		sb.append("WHEN MATCHED THEN                                                                                                ");
		sb.append("     UPDATE SET  MU.STAT_GOAL                        = SO.STAT_GOAL                                              ");
		sb.append("                ,MU.STAT_GOAL_UP                     = SO.STAT_GOAL_UP                                           ");
		sb.append("                ,MU.STAT_GOAL_SC                     = SO.STAT_GOAL_SC                                           ");
		sb.append("                ,MU.DEPOSIT_TH                       = SO.DEPOSIT_TH                                          ");
		sb.append("                ,MU.DEPOSIT_TD                       = SO.DEPOSIT_TD                                          ");
		sb.append("                ,MU.DEPOSIT_WH                       = SO.DEPOSIT_WH                                          ");
		sb.append("                ,MU.DEPOSIT_WD                       = SO.DEPOSIT_WD                                          ");
		sb.append("                ,MU.FIN_IND_EIP                      = SO.FIN_IND_EIP                                            ");
		sb.append("                ,MU.FIN_IND_NEW                      = SO.FIN_IND_NEW                                            ");
		sb.append("                ,MU.FIN_IND_UP                       = SO.FIN_IND_UP                                             ");
		sb.append("                ,MU.FIN_IND_SC                       = SO.FIN_IND_SC                                             ");
		sb.append("                ,MU.FIN_IND_DEP_TAR                  = SO.FIN_IND_DEP_TAR                                        ");
		sb.append("                ,MU.FIN_IND_DEP_AUN_UP               = SO.FIN_IND_DEP_AUN_UP                                     ");
		sb.append("                ,MU.FIN_IND_DEP_AUN_SC               = SO.FIN_IND_DEP_AUN_SC                                     ");
		sb.append("                ,MU.FIN_IND_INS_TAR                  = SO.FIN_IND_INS_TAR                                        ");
		sb.append("                ,MU.FIN_IND_INS_AUN_UP               = SO.FIN_IND_INS_AUN_UP                                     ");
		sb.append("                ,MU.FIN_IND_INS_AUN_SC               = SO.FIN_IND_INS_AUN_SC                                     ");
		sb.append("                ,MU.FIN_KQ_INS_TAR                   = SO.FIN_KQ_INS_TAR                                         ");
		sb.append("                ,MU.FIN_KQ_INS_AUN_UP                = SO.FIN_KQ_INS_AUN_UP                                      ");
		sb.append("                ,MU.FIN_KQ_INS_AUN_SC                = SO.FIN_KQ_INS_AUN_SC                                      ");
		sb.append("                ,MU.FIN_CUST_AUM_UP                  = SO.FIN_CUST_AUM_UP                                        ");
		sb.append("                ,MU.NFIN_IND_SAT                     = SO.NFIN_IND_SAT                                           ");
		sb.append("                ,MU.NFIN_IND_DECR                    = SO.NFIN_IND_DECR                                          ");
		sb.append("                ,MU.NFIN_IND_CHECK                   = SO.NFIN_IND_CHECK                                         ");
		sb.append("                ,MU.NFIN_IND_KNCUST                  = SO.NFIN_IND_KNCUST                                        ");
		sb.append("                ,MU.NFIN_IND_ABN                     = SO.NFIN_IND_ABN                                           ");
		sb.append("                ,MU.NFIN_LACK_IND                    = SO.NFIN_LACK_IND                                          ");
		sb.append("                ,MU.VERSION                          = MU.VERSION + 1                                            ");
		sb.append("                ,MU.MODIFIER                         = SO.EMP_ID                                                 ");
		sb.append("                ,MU.LASTUPDATE                       = SYSDATE                                                   ");
		sb.append("WHEN NOT MATCHED THEN INSERT(                                                                                    ");
		sb.append("        YEARMON,AO_JOB_RANK,STAT_GOAL,STAT_GOAL_UP,STAT_GOAL_SC                                                  ");
		sb.append("        ,DEPOSIT_TH,DEPOSIT_TD,DEPOSIT_WH,DEPOSIT_WD,FIN_IND_EIP,FIN_IND_NEW,FIN_IND_UP,FIN_IND_SC,FIN_IND_DEP_TAR                             ");
		sb.append("        ,FIN_IND_DEP_AUN_UP,FIN_IND_DEP_AUN_SC,FIN_IND_INS_TAR,FIN_IND_INS_AUN_UP                                ");
		sb.append("        ,FIN_IND_INS_AUN_SC,FIN_KQ_INS_TAR,FIN_KQ_INS_AUN_UP,FIN_KQ_INS_AUN_SC,FIN_CUST_AUM_UP                   ");
		sb.append("        ,NFIN_IND_SAT,NFIN_IND_DECR,NFIN_IND_CHECK,NFIN_IND_KNCUST,NFIN_IND_ABN,NFIN_LACK_IND                    ");
		sb.append("        ,VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE)                                                         ");
		sb.append("     VALUES(SO.YEARMON,SO.AO_JOB_RANK,SO.STAT_GOAL,SO.STAT_GOAL_UP,SO.STAT_GOAL_SC                               ");
		sb.append("        ,SO.DEPOSIT_TH,SO.DEPOSIT_TD,SO.DEPOSIT_WH,SO.DEPOSIT_WD,SO.FIN_IND_EIP,SO.FIN_IND_NEW,SO.FIN_IND_UP,SO.FIN_IND_SC,SO.FIN_IND_DEP_TAR           ");
		sb.append("        ,SO.FIN_IND_DEP_AUN_UP,SO.FIN_IND_DEP_AUN_SC,SO.FIN_IND_INS_TAR,SO.FIN_IND_INS_AUN_UP                    ");
		sb.append("        ,SO.FIN_IND_INS_AUN_SC,SO.FIN_KQ_INS_TAR,SO.FIN_KQ_INS_AUN_UP,SO.FIN_KQ_INS_AUN_SC,SO.FIN_CUST_AUM_UP    ");
		sb.append("        ,SO.NFIN_IND_SAT,SO.NFIN_IND_DECR,SO.NFIN_IND_CHECK,SO.NFIN_IND_KNCUST,SO.NFIN_IND_ABN,SO.NFIN_LACK_IND  ");
		sb.append("        ,1,SYSDATE,SO.EMP_ID,SO.EMP_ID,SYSDATE)                                                                  ");

		qc.setQueryString(sb.toString());

		for (int i = 0; i < inputVO.getSpAssList().size(); i++)
		{

			qc.setObject("YEARMON", inputVO.getSpAssList().get(i).get("YEARMON"));
			qc.setObject("AO_JOB_RANK", inputVO.getSpAssList().get(i).get("AO_JOB_RANK"));

			qc.setObject("STAT_GOAL", inputVO.getSpAssList().get(i).get("STAT_GOAL"));
			qc.setObject("STAT_GOAL_UP", Float.parseFloat(String.valueOf(inputVO.getSpAssList().get(i).get("STAT_GOAL_UP")))/100);
			qc.setObject("STAT_GOAL_SC", inputVO.getSpAssList().get(i).get("STAT_GOAL_SC"));
			qc.setObject("DEPOSIT_TH", inputVO.getSpAssList().get(i).get("DEPOSIT_TH"));
			qc.setObject("DEPOSIT_TD", inputVO.getSpAssList().get(i).get("DEPOSIT_TD"));
			qc.setObject("DEPOSIT_WH", inputVO.getSpAssList().get(i).get("DEPOSIT_WH"));
			qc.setObject("DEPOSIT_WD", inputVO.getSpAssList().get(i).get("DEPOSIT_WD"));
			qc.setObject("FIN_IND_EIP", inputVO.getSpAssList().get(i).get("FIN_IND_EIP"));
			qc.setObject("FIN_IND_NEW", inputVO.getSpAssList().get(i).get("FIN_IND_NEW"));
			qc.setObject("FIN_IND_UP", Float.parseFloat(String.valueOf(inputVO.getSpAssList().get(i).get("FIN_IND_UP")))/100);
			qc.setObject("FIN_IND_SC", inputVO.getSpAssList().get(i).get("FIN_IND_SC"));
			qc.setObject("FIN_IND_DEP_TAR", inputVO.getSpAssList().get(i).get("FIN_IND_DEP_TAR"));
			qc.setObject("FIN_IND_DEP_AUN_UP", Float.parseFloat(String.valueOf(inputVO.getSpAssList().get(i).get("FIN_IND_DEP_AUN_UP")))/100);
			qc.setObject("FIN_IND_DEP_AUN_SC", inputVO.getSpAssList().get(i).get("FIN_IND_DEP_AUN_SC"));
			qc.setObject("FIN_IND_INS_TAR", inputVO.getSpAssList().get(i).get("FIN_IND_INS_TAR"));
			qc.setObject("FIN_IND_INS_AUN_UP", Float.parseFloat(String.valueOf(inputVO.getSpAssList().get(i).get("FIN_IND_INS_AUN_UP")))/100);
			qc.setObject("FIN_IND_INS_AUN_SC", inputVO.getSpAssList().get(i).get("FIN_IND_INS_AUN_SC"));
			qc.setObject("FIN_KQ_INS_TAR", inputVO.getSpAssList().get(i).get("FIN_KQ_INS_TAR"));
			qc.setObject("FIN_KQ_INS_AUN_UP", inputVO.getSpAssList().get(i).get("FIN_KQ_INS_AUN_UP"));
			qc.setObject("FIN_KQ_INS_AUN_SC", inputVO.getSpAssList().get(i).get("FIN_KQ_INS_AUN_SC"));
			qc.setObject("FIN_CUST_AUM_UP", inputVO.getSpAssList().get(i).get("FIN_CUST_AUM_UP"));
			qc.setObject("NFIN_IND_SAT", inputVO.getSpAssList().get(i).get("NFIN_IND_SAT"));
			qc.setObject("NFIN_IND_DECR", inputVO.getSpAssList().get(i).get("NFIN_IND_DECR"));
			qc.setObject("NFIN_IND_CHECK", inputVO.getSpAssList().get(i).get("NFIN_IND_CHECK"));
			qc.setObject("NFIN_IND_KNCUST", inputVO.getSpAssList().get(i).get("NFIN_IND_KNCUST"));
			qc.setObject("NFIN_IND_ABN", inputVO.getSpAssList().get(i).get("NFIN_IND_ABN"));
			qc.setObject("NFIN_LACK_IND", inputVO.getSpAssList().get(i).get("NFIN_LACK_IND"));

			qc.setObject("EMP_ID", inputVO.getUserID());
			int result = dam.exeUpdate(qc);
			rst = rst + result;
			outputVO.setBackResult(rst);
		}
		sendRtnObject(outputVO);
	}

	// 修改SpAss中为已设定
	public void changeSet(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		dam = this.getDataAccessManager();

		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("		UPDATE TBPMS_CNR_MAST T 					");
		sb.append("		SET T.SP_ASS = '1'||SUBSTR(T.SP_ASS,2)  	");
		sb.append("		WHERE T.YEARMON = :YEARMON 					");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
	}

	
	/**
	 * 查詢本薪倍數（理專 參數2）
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void querySpBen(Object body, IPrimitiveMap header) throws JBranchException {
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT MULTIPLE1,                 ");
		sb.append("         MULTIPLE2,                 ");
		sb.append("        	MULTIPLE3,                 ");
		sb.append("        	AO_JOB_RANK                ");
		
		sb.append("  FROM TBPMS_CNR_PD_SP_BEN		   ");
		
		sb.append("    WHERE YEARMON = :YEARMON		   ");
		sb.append("    ORDER BY AO_JOB_RANK");
		qc.setObject("YEARMON", inputVO.getYearMon());
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setSpBenList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 插入或更新本薪倍數（理專 參數2）
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void updateSpBen(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			for(int i=0; i < inputVO.getSpBenList().size(); i++){
				StringBuffer sb = new StringBuffer();
				String AO_JOB_RANK = inputVO.getSpBenList().get(i).get("AO_JOB_RANK");
				
				//當數據庫中有該列，則執行更新，否則執行插入
				sb.append("  MERGE INTO TBPMS_CNR_PD_SP_BEN MU            		 ");
				sb.append(" 	 USING(SELECT :YEARMON AS YEARMON,           	 ");
				sb.append("  		:AO_JOB_RANK  AS AO_JOB_RANK ,               ");
				sb.append("  		:MULTIPLE1  AS MULTIPLE1,             		 ");
				sb.append("  		:MULTIPLE2  AS MULTIPLE2,           	     ");
				sb.append("  		:MULTIPLE3  AS MULTIPLE3,           	     ");
				sb.append("  		0 AS VERSION,             					 ");
				sb.append("  		sysdate AS CREATETIME,       		         ");
				sb.append("  		:userId AS CREATOR,             			 ");
				sb.append("  		:userId AS MODIFIER,            		     ");
				sb.append("  		sysdate AS LASTUPDATE           		     ");
				sb.append("  	FROM  DUAL)SO            					     ");
				sb.append("  		ON (SO.YEARMON = MU.YEARMON                  ");
				sb.append(" 		AND SO.AO_JOB_RANK = MU.AO_JOB_RANK)         ");
				sb.append("  WHEN MATCHED THEN            					     ");
				sb.append("  	UPDATE SET  MU.MULTIPLE1 = SO.MULTIPLE1          ");
				sb.append("  		,MU.MULTIPLE2 = SO.MULTIPLE2            	 ");
				sb.append("  		,MU.MULTIPLE3 = SO.MULTIPLE3         	     ");
				sb.append("  		,MU.CREATOR = SO.CREATOR         	 	     ");
				sb.append("  		,MU.MODIFIER = SO.MODIFIER         		     ");
				sb.append("  WHEN NOT MATCHED THEN            				     ");
				sb.append("  	INSERT (YEARMON,	            			     ");
				sb.append("  		AO_JOB_RANK,            				     ");
				sb.append("  		MULTIPLE1,           					     ");
				sb.append("  		MULTIPLE2,            					     ");
				sb.append("  		MULTIPLE3,            						 ");
				sb.append("  		VERSION,           						     ");
				sb.append("  		CREATETIME,             					 ");
				sb.append("  		CREATOR,             						 ");
				sb.append("  		MODIFIER,         						     ");
				sb.append("  		LASTUPDATE )             					 ");
				sb.append("  	VALUES(SO.YEARMON,            				     ");
				sb.append("  		SO.AO_JOB_RANK,             				 ");
				sb.append("  		SO.MULTIPLE1,             					 ");
				sb.append("  		SO.MULTIPLE2,             					 ");
				sb.append("  		SO.MULTIPLE3,            				     ");
				sb.append("  		SO.VERSION,           					     ");
				sb.append("  		SO.CREATETIME,           				     ");
				sb.append("  		SO.CREATOR,            					     ");
				sb.append("  		SO.MODIFIER,         					     ");
				sb.append("  		SO.LASTUPDATE)          				     ");
				
				qc.setObject("YEARMON", inputVO.getYearMon());
				qc.setObject("AO_JOB_RANK", inputVO.getSpBenList().get(i).get("AO_JOB_RANK"));
				qc.setObject("MULTIPLE1", inputVO.getSpBenList().get(i).get("MULTIPLE1"));
				qc.setObject("MULTIPLE2", inputVO.getSpBenList().get(i).get("MULTIPLE2"));
				qc.setObject("MULTIPLE3", inputVO.getSpBenList().get(i).get("MULTIPLE3"));
				qc.setObject("userId", inputVO.getUserID());
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
			}
			
			//更新主表，將SP_BEN第一位（狀態）設置為1
			StringBuffer sb1 = new StringBuffer();
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
			sb1.append("		SET T.SP_BEN = '1'||SUBSTR(T.SP_BEN,2)  	");
			sb1.append("		WHERE T.yearmon = :YEARMON 					");
			
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
			outputVO.setSpBenList(null);
			sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException("更新失敗,系統發生錯誤請洽系統管理員"+e.getMessage());
		}
	
	}
	
	
	/**
	 * 查詢專員休假對應目標折數（理專 參數3）
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void querySpLevd(Object body, IPrimitiveMap header) throws JBranchException {
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("     SELECT START_DAY,                          ");
		sb.append("     	END_DAY,                               ");
		sb.append("     	TAR_DIS                                ");
		sb.append("     FROM TBPMS_CNR_PD_SP_LEVD_TA          	   ");
		sb.append("     	WHERE  YEARMON = :YEARMON              ");
		sb.append("     	ORDER BY START_DAY            		   ");
		qc.setObject("YEARMON", inputVO.getYearMon());
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setSpLevdList(result);
		sendRtnObject(outputVO);
	}
	 
	 /**
	  * 插入或更新專員休假對應目標折數（理專 參數3）
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void updateSpLevd(Object body, IPrimitiveMap header) throws JBranchException {
			try{
				PMS210InputVO inputVO = (PMS210InputVO) body;
				PMS210OutputVO outputVO = new PMS210OutputVO();
				dam = this.getDataAccessManager();
				QueryConditionIF qcDel = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sbDel = new StringBuffer();
				sbDel.append("DELETE FROM TBPMS_CNR_PD_SP_LEVD_TA    ");
				sbDel.append("WHERE YEARMON = :YEARMON               ");
				qcDel.setObject("YEARMON", inputVO.getYearMon());
				qcDel.setQueryString(sbDel.toString());
				dam.exeUpdate(qcDel);
				
				StringBuffer sb = new StringBuffer();
				sb.append("  	INSERT INTO TBPMS_CNR_PD_SP_LEVD_TA (YEARMON,	 ");
				sb.append("  		START_DAY,           					     ");
				sb.append("  		END_DAY,            					     ");
				sb.append("  		TAR_DIS,            						 ");
				sb.append("  		VERSION,           						     ");
				sb.append("  		CREATETIME,             					 ");
				sb.append("  		CREATOR,             						 ");
				sb.append("  		MODIFIER,         						     ");
				sb.append("  		LASTUPDATE )             					 ");
				sb.append("  	VALUES(:YEARMON,            				     ");
				sb.append("  		:START_DAY,             					 ");
				sb.append("  		:END_DAY,             					 	 ");
				sb.append("  		:TAR_DIS,            					     ");
				sb.append("  		0,           					     		 ");
				sb.append("  		sysdate,           				     	 	 ");
				sb.append("  		:userId,            					     ");
				sb.append("  		:userId,         					     	 ");
				sb.append("  		sysdate)          				     		 ");
				for(int i=0; i < inputVO.getSpLevdList().size(); i++){
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					qc.setObject("YEARMON", inputVO.getYearMon());
					qc.setObject("START_DAY", inputVO.getSpLevdList().get(i).get("START_DAY"));
					qc.setObject("END_DAY", inputVO.getSpLevdList().get(i).get("END_DAY"));
					qc.setObject("TAR_DIS", inputVO.getSpLevdList().get(i).get("TAR_DIS"));
					qc.setObject("userId", inputVO.getUserID());
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					
				}
				
				//更新主表，將SP_BEN第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
				sb1.append("		SET T.SP_LEVD = '1'||SUBSTR(T.SP_LEVD,2)  	");
				sb1.append("		WHERE T.yearmon = :YEARMON 					");
				
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
				outputVO.setSpLevdList(null);
				sendRtnObject(outputVO);
			}catch (Exception e) {
				logger.error("更新失敗");
			}
		
	}
	 
	 /**
	  * 查詢當月掛Goal日期對應目標折數（理專 參數4）
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void queryBsGoal(Object body, IPrimitiveMap header) throws JBranchException {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT GOAL_DATE_START,                 ");
			sb.append("         GOAL_DATE_END,                   ");
			sb.append("        	GOAL_DISC              		     ");
			sb.append("  FROM TBPMS_CNR_PD_BS_GOAL_TAR		     ");
			
			sb.append("    WHERE YEARMON = :YEARMON		   ");
			sb.append("    ORDER BY GOAL_DATE_START ");
			qc.setObject("YEARMON", inputVO.getYearMon());
			
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setBsGoalList(result);
			sendRtnObject(outputVO);
	}

	 /**
	  * 插入或更新當月掛Goal日期對應目標折數（理專 參數4）
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void updateBsGoal(Object body, IPrimitiveMap header) throws JBranchException {
			try{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qcDel = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sbDel = new StringBuffer();
			sbDel.append("DELETE FROM TBPMS_CNR_PD_BS_GOAL_TAR    ");
			sbDel.append("WHERE YEARMON = :YEARMON               ");
			qcDel.setObject("YEARMON", inputVO.getYearMon());
			qcDel.setQueryString(sbDel.toString());
			dam.exeUpdate(qcDel);
			
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("  	INSERT INTO TBPMS_CNR_PD_BS_GOAL_TAR (YEARMON,	 ");
			sb.append("  		GOAL_DATE_START,           					 ");
			sb.append("  		GOAL_DATE_END,            					 ");
			sb.append("  		GOAL_DISC,            						 ");
			sb.append("  		VERSION,           						     ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:YEARMON,            				     ");
			sb.append("  		:GOAL_DATE_START,             			 	 ");
			sb.append("  		:GOAL_DATE_END,             				 ");
			sb.append("  		:GOAL_DISC,            					 	 ");
			sb.append("  		0,           					    		 ");
			sb.append("  		SYSDATE,           				     		 ");
			sb.append("  		:userId,            					     ");
			sb.append("  		:userId,         					     	 ");
			sb.append("  		SYSDATE)          				    		 ");
			for(int i=0; i < inputVO.getBsGoalList().size(); i++){					
				//當數據庫中有該列，則執行更新，否則執行插入					
				qc.setObject("YEARMON", inputVO.getYearMon());
				qc.setObject("GOAL_DATE_START", inputVO.getBsGoalList().get(i).get("GOAL_DATE_START"));
				qc.setObject("GOAL_DATE_END", inputVO.getBsGoalList().get(i).get("GOAL_DATE_END"));
				qc.setObject("GOAL_DISC", inputVO.getBsGoalList().get(i).get("GOAL_DISC"));
				qc.setObject("userId", inputVO.getUserID());
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
			}
			
			//更新主表，將BS_GOAL第一位（狀態）設置為1
			StringBuffer sb1 = new StringBuffer();
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
			sb1.append("		SET T.BS_GOAL = '1'||SUBSTR(T.BS_GOAL,2)  	");
			sb1.append("		WHERE T.yearmon = :YEARMON 					");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
			outputVO.setBsGoalList(null);
			sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
		}
		
	}
	
	
	
	
	/**
	 * 查詢專員獎金率(sp_bouns 理專 參數5)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void querySpBouns(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT YEARMON,             ");
		sb.append("         AO_JOB_RANK,         ");
		sb.append("         NET_INC_START,       ");
		sb.append("         NET_INC_END,         ");
		sb.append("         BONUS_RATE           ");

		sb.append("  FROM TBPMS_CNR_PD_SP_BONUS");
		sb.append("  WHERE YEARMON = :YEARMON");
		sb.append("  ORDER BY AO_JOB_RANK, NET_INC_START");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
		ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		
		int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
		int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
		outputVO.setSpBounsList(largeAgrList); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(outputVO);
		
		/*qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setSpBounsList(result);
		sendRtnObject(outputVO);*/
	}

	/**
	 * 文件上傳，并將數據添加到專員獎金率
	 * 
	 * @param body
	 * @param header
	 * @throws APException
	 */
	// 從excel表格中新增數據
	@SuppressWarnings(
	{ "rawtypes" })
	public void addSpBouns(Object body, IPrimitiveMap header) throws APException
	{	
		int flag = 0;
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			List<String> list = new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_PD_SP_BONUS_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			// 有表頭.xls文檔
			String lab = null;
			for (int a = 0; a < sheet.length; a++)
			{
				for (int i = 1; i < sheet[a].getRows(); i++)
				{
					for (int j = 0; j < sheet[a].getColumns(); j++)
					{
						if(j==4){
							if(sheet[a].getCell(j, i).getType() == CellType.NUMBER){
								NumberCell numberCell = (NumberCell)sheet[a].getCell(j, i);
								lab = String.valueOf(numberCell.getValue());
							}else{
								flag++;
								throw new APException("獎金率輸入含非數字");
							}
						}else{
							lab = sheet[a].getCell(j, i).getContents();
						}
						list.add(lab);
					}
					
					//excel表格記行數
					flag++;
					
					//判斷當前上傳數據月份是否一致
					if(!list.get(0).equals(inputVO.getYearMon())){
						throw new APException("上傳數據月份不一致");
					}
					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=5){
						throw new APException("上傳數據欄位個數不一致");
					}

					// SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_CNR_PD_SP_BONUS_U (YEARMON,	     ");
					sb.append("  		AO_JOB_RANK,            				     ");
					sb.append("  		NET_INC_START,           					 ");
					sb.append("  		NET_INC_END,            					 ");
					sb.append("  		BONUS_RATE,            					     ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:AO_JOB_RANK,             				     ");
					sb.append("  		:NET_INC_START,             			     ");
					sb.append("  		:NET_INC_END,             					 ");
					sb.append("  		:BONUS_RATE,             					 ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",list.get(0).trim()                         );
					qc.setObject("AO_JOB_RANK",list.get(1).trim()                     );
					qc.setObject("NET_INC_START",list.get(2).trim()                   );
					qc.setObject("NET_INC_END",list.get(3).trim()                     );
					qc.setObject("BONUS_RATE",list.get(4).trim()                      );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserID()                       );
					qc.setObject("MODIFIER", inputVO.getUserID()                      );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}

	
	
	
	/**
	  * 查詢專員新戶開發獎金設定表（理專 參數6）
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings("rawtypes")
	public void queryNewCustRate(Object body, IPrimitiveMap header) throws JBranchException {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT AUM_INC_START,                		 ");
			sb.append("         AUM_INC_END,                	     ");
			sb.append("        	BONUS              					 ");
			sb.append("  FROM TBPMS_CNR_PD_NEW_BONUS		   	     ");
			sb.append("  WHERE YEARMON = :YEARMON		   		     ");
			sb.append("  ORDER BY AUM_INC_START		   	     		 ");
			qc.setObject("YEARMON", inputVO.getYearMon());
			
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setNewCustRateList(result);
			sendRtnObject(outputVO);
	}
	 /**
	  * 更新專員新戶開發獎金設定表（理專 參數6）
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void updateNewCustRate(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qcDel = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sbDel = new StringBuffer();					//執行刪除語句
		sbDel.append("  DELETE FROM TBPMS_CNR_PD_NEW_BONUS  ");
		sbDel.append("  WHERE YEARMON =:YEARMON 		  ");
		qcDel.setObject("YEARMON", inputVO.getYearMon());
		qcDel.setQueryString(sbDel.toString());
		 dam.exeUpdate(qcDel);
		
			StringBuffer sb = new StringBuffer();						//執行插入語句
			sb.append("  	INSERT INTO TBPMS_CNR_PD_NEW_BONUS(YEARMON,	     ");
			sb.append("  		AUM_INC_START,           					 ");
			sb.append("  		AUM_INC_END,            					 ");
			sb.append("  		BONUS,            							 ");
			sb.append("  		VERSION,           						     ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:YEARMON,            				     ");
			sb.append("  		:AUM_INC_START,             				 ");
			sb.append("  		:AUM_INC_END,             				 	 ");
			sb.append("  		:BONUS,            					 		 ");
			sb.append("  		0,           					    		 ");
			sb.append("  		sysdate,           				    		 ");
			sb.append("  		:userId,            					     ");
			sb.append("  		:userId,         					   	     ");
			sb.append("  		sysdate)          				    		 ");
			
			/*將bsGoalTarList的內容插入數據庫*/
			for(int i=0; i < inputVO.getNewCustRateList().size(); i++){
				QueryConditionIF qcInsert = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qcInsert.setObject("YEARMON", inputVO.getYearMon());
				qcInsert.setObject("AUM_INC_START", inputVO.getNewCustRateList().get(i).get("AUM_INC_START"));
				qcInsert.setObject("AUM_INC_END", inputVO.getNewCustRateList().get(i).get("AUM_INC_END"));
				qcInsert.setObject("BONUS", inputVO.getNewCustRateList().get(i).get("BONUS"));
				qcInsert.setObject("userId", inputVO.getUserID());
				qcInsert.setQueryString(sb.toString());
				dam.exeUpdate(qcInsert);
				
			}
			
		
		//更新主表，將BS_GOAL_TAR第一位（狀態）設置為1
		StringBuffer sb1 = new StringBuffer();
		QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
		sb1.append("		SET T.NEW_CUST_RATE = '1'||SUBSTR(T.NEW_CUST_RATE,2)  	");
		sb1.append("		WHERE T.yearmon = :YEARMON 					");
		
		qc1.setObject("YEARMON", inputVO.getYearMon());
		qc1.setQueryString(sb1.toString());
		dam.exeUpdate(qc1);
		outputVO.setNewCustRateList(null);
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException("更新失敗,系統發生錯誤請洽系統管理員"+e.getMessage());
		}
	
	}
	
	 /**
		 * 查詢人員新任計績對應領獎門檻(本薪倍數)折數表（理專 參數7）
		 * @param body
		 * @param header
		 * @throws JBranchException
		 */
	public void queryNewSpBen(Object body, IPrimitiveMap header) throws JBranchException {
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT MONTH1,                 ");
		sb.append("         MONTH2,                 ");
		sb.append("        	MONTH3,                 ");
		sb.append("        	MONTH4,                 ");
		sb.append("        	MONTH5,                 ");
		sb.append("        	MONTH6,                 ");
		sb.append("        	AO_JOB_RANK             ");
		
		sb.append("  FROM TBPMS_CNR_PD_NEW_LINE_BONUS		   ");
		
		sb.append("    WHERE YEARMON = :YEARMON		   ");
		sb.append("    ORDER BY AO_JOB_RANK ");
		qc.setObject("YEARMON", inputVO.getYearMon());
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setNewSpBenList(result);
		sendRtnObject(outputVO);
	}
			
	/**
	 * 插入或更新人員新任計績對應領獎門檻(本薪倍數)折數表 （理專 參數7）
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void updateNewSpBen(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		//當數據庫中有該列，則執行更新，否則執行插入
		sb.append("  MERGE INTO TBPMS_CNR_PD_NEW_LINE_BONUS MU            		 ");
		sb.append(" 	 USING(SELECT :YEARMON AS YEARMON,           	 ");
		sb.append("  		:AO_JOB_RANK  AS AO_JOB_RANK ,               ");
		sb.append("  		:MONTH1  AS MONTH1,             		 ");
		sb.append("  		:MONTH2  AS MONTH2,           	     ");
		sb.append("  		:MONTH3  AS MONTH3,           	     ");
		sb.append("  		:MONTH4  AS MONTH4,           	     ");
		sb.append("  		:MONTH5  AS MONTH5,           	     ");
		sb.append("  		:MONTH6  AS MONTH6,           	     ");
		sb.append("  		0 AS VERSION,             					 ");
		sb.append("  		sysdate AS CREATETIME,       		         ");
		sb.append("  		:userId AS CREATOR,             			 ");
		sb.append("  		:userId AS MODIFIER,            		     ");
		sb.append("  		sysdate AS LASTUPDATE           		     ");
		sb.append("  	FROM  DUAL)SO            					     ");
		sb.append("  		ON (SO.YEARMON = MU.YEARMON                  ");
		sb.append(" 		AND SO.AO_JOB_RANK = MU.AO_JOB_RANK)         ");
		sb.append("  WHEN MATCHED THEN            					     ");
		sb.append("  	UPDATE SET  MU.MONTH1 = SO.MONTH1          ");
		sb.append("  		,MU.MONTH2 = SO.MONTH2            	 ");
		sb.append("  		,MU.MONTH3 = SO.MONTH3         	     ");
		sb.append("  		,MU.MONTH4 = SO.MONTH4         	     ");
		sb.append("  		,MU.MONTH5 = SO.MONTH5         	     ");
		sb.append("  		,MU.MONTH6 = SO.MONTH6         	     ");
		sb.append("  		,MU.CREATOR = SO.CREATOR         	 	     ");
		sb.append("  		,MU.MODIFIER = SO.MODIFIER         		     ");
		sb.append("  WHEN NOT MATCHED THEN            				     ");
		sb.append("  	INSERT (YEARMON,	            			     ");
		sb.append("  		AO_JOB_RANK,            				     ");
		sb.append("  		MONTH1,           					     ");
		sb.append("  		MONTH2,            					     ");
		sb.append("  		MONTH3,            						 ");
		sb.append("  		MONTH4,            						 ");
		sb.append("  		MONTH5,            						 ");
		sb.append("  		MONTH6,            						 ");
		sb.append("  		VERSION,           						     ");
		sb.append("  		CREATETIME,             					 ");
		sb.append("  		CREATOR,             						 ");
		sb.append("  		MODIFIER,         						     ");
		sb.append("  		LASTUPDATE )             					 ");
		sb.append("  	VALUES(SO.YEARMON,            				     ");
		sb.append("  		SO.AO_JOB_RANK,             				 ");
		sb.append("  		SO.MONTH1,             					 ");
		sb.append("  		SO.MONTH2,             					 ");
		sb.append("  		SO.MONTH3,            				     ");
		sb.append("  		SO.MONTH4,            				     ");
		sb.append("  		SO.MONTH5,            				     ");
		sb.append("  		SO.MONTH6,            				     ");
		sb.append("  		SO.VERSION,           					     ");
		sb.append("  		SO.CREATETIME,           				     ");
		sb.append("  		SO.CREATOR,            					     ");
		sb.append("  		SO.MODIFIER,         					     ");
		sb.append("  		SO.LASTUPDATE)          				     ");
		for(int i=0; i < inputVO.getNewSpBenList().size(); i++){
			
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setObject("AO_JOB_RANK", inputVO.getNewSpBenList().get(i).get("AO_JOB_RANK"));
			qc.setObject("MONTH1", inputVO.getNewSpBenList().get(i).get("MONTH1"));
			qc.setObject("MONTH2", inputVO.getNewSpBenList().get(i).get("MONTH2"));
			qc.setObject("MONTH3", inputVO.getNewSpBenList().get(i).get("MONTH3"));
			qc.setObject("MONTH4", inputVO.getNewSpBenList().get(i).get("MONTH4"));
			qc.setObject("MONTH5", inputVO.getNewSpBenList().get(i).get("MONTH5"));
			qc.setObject("MONTH6", inputVO.getNewSpBenList().get(i).get("MONTH6"));
			qc.setObject("userId", inputVO.getUserID());
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
		}
			
			//更新主表，將SP_BEN第一位（狀態）設置為1
			StringBuffer sb1 = new StringBuffer();
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
			sb1.append("		SET T.NEW_SP_BEN = '1'||SUBSTR(T.NEW_SP_BEN,2)  	");
			sb1.append("		WHERE T.yearmon = :YEARMON 					");
			
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
			outputVO.setNewSpBenList(null);
			sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	
	}
		 
	 /**
	 * 查詢人員新任標準生產力目標折數表（理專 參數8）
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryNewBsGoal(Object body, IPrimitiveMap header) throws JBranchException {
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT MONTH1,                 ");
		sb.append("         MONTH2,                 ");
		sb.append("        	MONTH3,                 ");
		sb.append("        	MONTH4,                 ");
		sb.append("        	MONTH5,                 ");
		sb.append("        	MONTH6,                 ");
		sb.append("        	AO_JOB_RANK             ");
		
		sb.append("  FROM TBPMS_CNR_PD_NEW_GOAL_BONUS		   ");
		
		sb.append("    WHERE YEARMON = :YEARMON		   ");
		sb.append("    ORDER BY AO_JOB_RANK ");
		qc.setObject("YEARMON", inputVO.getYearMon());
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setNewBsGoalList(result);
		sendRtnObject(outputVO);
	}
			
	/**
	 * 插入或更新人員新任標準生產力目標折數表 （理專 參數8）
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void updateNewBsGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		//當數據庫中有該列，則執行更新，否則執行插入
		sb.append("  MERGE INTO TBPMS_CNR_PD_NEW_GOAL_BONUS MU           ");
		sb.append(" 	 USING(SELECT :YEARMON AS YEARMON,           	 ");
		sb.append("  		:AO_JOB_RANK  AS AO_JOB_RANK ,               ");
		sb.append("  		:MONTH1  AS MONTH1,             		 	 ");
		sb.append("  		:MONTH2  AS MONTH2,           	             ");
		sb.append("  		:MONTH3  AS MONTH3,           	             ");
		sb.append("  		:MONTH4  AS MONTH4,           	             ");
		sb.append("  		:MONTH5  AS MONTH5,           	             ");
		sb.append("  		:MONTH6  AS MONTH6,           	             ");
		sb.append("  		0 AS VERSION,             					 ");
		sb.append("  		sysdate AS CREATETIME,       		         ");
		sb.append("  		:userId AS CREATOR,             			 ");
		sb.append("  		:userId AS MODIFIER,            		     ");
		sb.append("  		sysdate AS LASTUPDATE           		     ");
		sb.append("  	FROM  DUAL)SO            					     ");
		sb.append("  		ON (SO.YEARMON = MU.YEARMON                  ");
		sb.append(" 		AND SO.AO_JOB_RANK = MU.AO_JOB_RANK)         ");
		sb.append("  WHEN MATCHED THEN            					     ");
		sb.append("  	UPDATE SET  MU.MONTH1 = SO.MONTH1                ");
		sb.append("  		,MU.MONTH2 = SO.MONTH2            	         ");
		sb.append("  		,MU.MONTH3 = SO.MONTH3         	             ");
		sb.append("  		,MU.MONTH4 = SO.MONTH4         	             ");
		sb.append("  		,MU.MONTH5 = SO.MONTH5         	             ");
		sb.append("  		,MU.MONTH6 = SO.MONTH6         	             ");
		sb.append("  		,MU.CREATOR = SO.CREATOR         	 	     ");
		sb.append("  		,MU.MODIFIER = SO.MODIFIER         		     ");
		sb.append("  WHEN NOT MATCHED THEN            				     ");
		sb.append("  	INSERT (YEARMON,	            			     ");
		sb.append("  		AO_JOB_RANK,            				     ");
		sb.append("  		MONTH1,           					         ");
		sb.append("  		MONTH2,            					         ");
		sb.append("  		MONTH3,            						     ");
		sb.append("  		MONTH4,            						     ");
		sb.append("  		MONTH5,            						     ");
		sb.append("  		MONTH6,            						     ");
		sb.append("  		VERSION,           						     ");
		sb.append("  		CREATETIME,             					 ");
		sb.append("  		CREATOR,             						 ");
		sb.append("  		MODIFIER,         						     ");
		sb.append("  		LASTUPDATE )             					 ");
		sb.append("  	VALUES(SO.YEARMON,            				     ");
		sb.append("  		SO.AO_JOB_RANK,             				 ");
		sb.append("  		SO.MONTH1,             					     ");
		sb.append("  		SO.MONTH2,             					     ");
		sb.append("  		SO.MONTH3,            				         ");
		sb.append("  		SO.MONTH4,            				         ");
		sb.append("  		SO.MONTH5,            				         ");
		sb.append("  		SO.MONTH6,            				         ");
		sb.append("  		SO.VERSION,           					     ");
		sb.append("  		SO.CREATETIME,           				     ");
		sb.append("  		SO.CREATOR,            					     ");
		sb.append("  		SO.MODIFIER,         					     ");
		sb.append("  		SO.LASTUPDATE)          				     ");
		for(int i=0; i < inputVO.getNewBsGoalList().size(); i++){
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setObject("AO_JOB_RANK", inputVO.getNewBsGoalList().get(i).get("AO_JOB_RANK"));
			qc.setObject("MONTH1", inputVO.getNewBsGoalList().get(i).get("MONTH1"));
			qc.setObject("MONTH2", inputVO.getNewBsGoalList().get(i).get("MONTH2"));
			qc.setObject("MONTH3", inputVO.getNewBsGoalList().get(i).get("MONTH3"));
			qc.setObject("MONTH4", inputVO.getNewBsGoalList().get(i).get("MONTH4"));
			qc.setObject("MONTH5", inputVO.getNewBsGoalList().get(i).get("MONTH5"));
			qc.setObject("MONTH6", inputVO.getNewBsGoalList().get(i).get("MONTH6"));
			qc.setObject("userId", inputVO.getUserID());
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
		}
			
			//更新主表，將SP_BEN第一位（狀態）設置為1
			StringBuffer sb1 = new StringBuffer();
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
			sb1.append("		SET T.NEW_BS_GOAL = '1'||SUBSTR(T.NEW_BS_GOAL,2)  	");
			sb1.append("		WHERE T.yearmon = :YEARMON 					");
			
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
			outputVO.setNewBsGoalList(null);
			sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	
	}
			
			
			
	/**
	 * 查詢當月掛Goal員工本薪和本薪倍數(Sp_Sal_Ben 理專 參數9)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void querySpSalBen(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT A.SNAP_YYYYMM,            ");
		sb.append("         A.EMP_NO,                 ");
		sb.append("         B.EMP_NAME,               ");
		sb.append("         CASE WHEN B.AO_JOB_RANK LIKE 'FCH%' THEN 'FCH' ELSE B.AO_JOB_RANK END AS AO_JOB_RANK, ");
		sb.append("         A.SAL_MULTIPLE,           ");
		sb.append("         A.GOAL_DIS_RT,            ");
		sb.append("         A.GOAL_REST_RT,           ");
		sb.append("         A.GOAL_NEW_RT,            ");
		sb.append("         A.GOAL_DIS_RT_FINAL,      ");
		sb.append("         C.PROFIT_TGT              ");
		sb.append("  FROM TBPMS_WMG_AO_GOAL A         ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N B ");
		sb.append("  	ON A.EMP_NO = B.EMP_ID        ");
		sb.append("  	AND LAST_DAY(TO_DATE(A.SNAP_YYYYMM,'YYYYMM')) BETWEEN B.START_TIME AND B.END_TIME  ");
		sb.append("  	AND B.ROLE_ID IN(SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FUBONSYS.FC_ROLE')  ");
		sb.append("  LEFT JOIN TBPMS_WMG_AO_PROFIT C  ");
		sb.append("  	ON A.EMP_NO = C.EMP_NO        ");
		sb.append("  	AND C.SNAP_YYYYMM = A.SNAP_YYYYMM ");		
		sb.append("  WHERE A.SNAP_YYYYMM = :YEARMON   ");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
		ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		
		int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
		int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
		outputVO.setSpSalBenList(largeAgrList); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void exportSpSalBen(Object body, IPrimitiveMap header) throws JBranchException 
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT A.SNAP_YYYYMM,            ");
		sb.append("         A.EMP_NO,                 ");
		sb.append("         B.EMP_NAME,               ");
		sb.append("         CASE WHEN B.AO_JOB_RANK LIKE 'FCH%' THEN 'FCH' ELSE B.AO_JOB_RANK END AS AO_JOB_RANK, ");
		sb.append("         A.SAL_MULTIPLE,           ");
		sb.append("         A.GOAL_DIS_RT,            ");
		sb.append("         A.GOAL_REST_RT,           ");
		sb.append("         A.GOAL_NEW_RT,            ");
		sb.append("         A.GOAL_DIS_RT_FINAL,      ");
		sb.append("         C.PROFIT_TGT              ");
		sb.append("  FROM TBPMS_WMG_AO_GOAL A         ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N B ");
		sb.append("  	ON A.EMP_NO = B.EMP_ID        ");
		sb.append("  	AND LAST_DAY(TO_DATE(A.SNAP_YYYYMM,'YYYYMM')) BETWEEN B.START_TIME AND B.END_TIME  ");
		sb.append("  	AND B.ROLE_ID IN(SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FUBONSYS.FC_ROLE')  ");
		sb.append("  LEFT JOIN TBPMS_WMG_AO_PROFIT C  ");
		sb.append("  	ON A.EMP_NO = C.EMP_NO        ");
		sb.append("  	AND C.SNAP_YYYYMM = A.SNAP_YYYYMM ");		
		sb.append("  WHERE A.SNAP_YYYYMM = :YEARMON   ");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "本月掛GOAL專員本薪*本薪倍數_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[10];
				int i = 0;
				records[i] = checkIsNull(map, "SNAP_YYYYMM");//資料年月
				records[++i] = checkIsNullAndTrans(map, "EMP_NO");//員工編號
				records[++i] = checkIsNull(map, "EMP_NAME");//員工姓名
				records[++i] = checkIsNull(map, "AO_JOB_RANK");//理專職級
				records[++i] = checkIsNull(map, "SAL_MULTIPLE");//本薪倍數
				records[++i] = checkIsNull(map, "GOAL_DIS_RT"); //專員目標折數
				records[++i] = checkIsNull(map, "GOAL_REST_RT");//專員休假天數
				records[++i] = checkIsNull(map, "GOAL_NEW_RT");//專員新任折數
				records[++i] = checkIsNull(map, "GOAL_DIS_RT_FINAL");//收益目標總折數
				records[++i] = checkIsNull(map, "PROFIT_TGT");//收益目標
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[10];
			int j = 0;
			csvHeader[j] = "資料年月";
			csvHeader[++j] = "員工編號";
			csvHeader[++j] = "員工姓名";
			csvHeader[++j] = "理專職級";
			csvHeader[++j] = "本薪倍數";
			csvHeader[++j] = "專員目標折數";
			csvHeader[++j] = "專員休假天數";
			csvHeader[++j] = "專員新任折數";
			csvHeader[++j] = "收益目標總折數";
			csvHeader[++j] = "收益目標";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else {
			outputVO.setResultList(list);
			this.sendRtnObject(outputVO);
	    }
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
			return String.valueOf(map.get(key));
		}else{
			return "";
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
	 * 文件上傳，上傳當月掛Goal員工本薪
	 * 
	 * @param body
	 * @param header
	 * @throws APException
	 */
	// 從excel表格中新增數據
	@SuppressWarnings(
	{ "rawtypes" })
	public void addSpSalBen(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			List<String> list = new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_PD_SP_SAL_BEN_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);;
			// 有表頭.xls文檔
			String lab = null;
			for (int a = 0; a < sheet.length; a++)
			{
				for (int i = 1; i < sheet[a].getRows(); i++)
				{
					for (int j = 0; j < sheet[a].getColumns(); j++)
					{
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					
					//excel表格記行數
					flag++;
					
					//判斷當前上傳數據月份是否一致
					if(!list.get(0).equals(inputVO.getYearMon())){
						throw new APException("上傳數據月份不一致");
					}
					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=5){
						throw new APException("上傳數據欄位個數不一致");
					}

					// SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_CNR_PD_SP_SAL_BEN_U (YEARMON,	 ");
					sb.append("  		EMP_ID,            				             ");
					sb.append("  		AO_CODE,           					         ");
					sb.append("  		GRADE,            					         ");
					sb.append("  		BEN,            					         ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:EMP_ID,             				         ");
					sb.append("  		:AO_CODE,             					     ");
					sb.append("  		:GRADE,             					     ");
					sb.append("  		:BEN,             					         ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",list.get(0).trim()                         );
					qc.setObject("EMP_ID",list.get(1).trim()                          );
					qc.setObject("AO_CODE",list.get(2).trim()                         );
					qc.setObject("GRADE",list.get(3).trim()                           );
					qc.setObject("BEN",list.get(4).trim()                             );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserID()                       );
					qc.setObject("MODIFIER", inputVO.getUserID()                      );
					qc.setQueryString(sb.toString());
					flag++;
					dam.exeUpdate(qc);
					list.clear();
				}
			}
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	/**
	 * 理專參數9調用存儲過程取資料
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void getData(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			//產生理專折數
			if(1 == inputVO.getFlag()){
				sb.append(" CALL PABTH_BTPMS732.SP_GET_TBPMS_WMG_AO_GOAL(? ) ");
			}
			//取得計算後收益目標
			if(2 == inputVO.getFlag()){
				sb.append(" CALL PABTH_BTPMS732.SP_GET_TBPMS_WMG_AO_PROFIT(? ) ");
			}
			qc.setString(1, inputVO.getYearMon());
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 查詢新戶轉介獎金(new_bounty 理專 參數10)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryNewBounty(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT ACC_BEG,             ");
		sb.append("         YEARMON,             ");
		sb.append("         ACC_END,             ");
		sb.append("         BOUNTY               ");
		sb.append("  FROM TBPMS_CNR_PD_NEW_BOUNTY");
		sb.append("  WHERE YEARMON = :YEARMON");
		sb.append("  ORDER BY BOUNTY");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
		ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
		int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
		outputVO.setNewBountyList(largeAgrList); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(outputVO);
		/*List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setNewBountyList(result);
		sendRtnObject(outputVO);*/
	}

	/**
	 * 文件上傳，并將數據添加到新戶轉介獎金
	 * 
	 * @param body
	 * @param header
	 * @throws APException
	 */
	// 從excel表格中新增數據
	@SuppressWarnings(
	{ "rawtypes" })
	public void addNewBounty(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			List<String> list = new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			
			// 有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_PD_NEW_BOUNTY_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
			for (int a = 0; a < sheet.length; a++)
			{
				for (int i = 1; i < sheet[a].getRows(); i++)
				{
					for (int j = 0; j < sheet[a].getColumns(); j++)
					{
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					
					//excel表格記行數
					flag++;

					//判斷當前上傳數據月份是否一致
					if(!list.get(0).equals(inputVO.getYearMon())){
						throw new APException("上傳數據選擇月份不一致");
					}
					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=4){
						throw new APException("上傳數據欄位個數不一致");
					}
					
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_CNR_PD_NEW_BOUNTY_U (YEARMON,	 ");
					sb.append("  		ACC_BEG,            				         ");
					sb.append("  		ACC_END,           					         ");
					sb.append("  		BOUNTY,            					         ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:ACC_BEG,             				         ");
					sb.append("  		:ACC_END,             					     ");
					sb.append("  		:BOUNTY,             					     ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",list.get(0).trim()                         );
					qc.setObject("ACC_BEG",list.get(1).trim()                         );
					qc.setObject("ACC_END",list.get(2).trim()                         );
					qc.setObject("BOUNTY",list.get(3).trim()                          );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserID()                       );
					qc.setObject("MODIFIER", inputVO.getUserID()                      );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error("發生錯誤:%s",StringUtil.getStackTraceAsString(e));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	
	
	
	
	/**
	 * 查詢主管考核目標及配分(Bs_Ass 主管 參數1)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryBsAss(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT YEARMON,                               ");
		sb.append("  		MANA_TYPE,                             ");
		sb.append("  		FIN_INS_UP*100 AS FIN_INS_UP,          ");
		sb.append("  		FIN_INS_SC,                            ");
		sb.append("  		FIN_IND_UP*100 AS FIN_IND_UP,          ");
		sb.append("  		FIN_IND_SC,                            ");
		sb.append("  		FIN_DEP_UP*100 AS FIN_DEP_UP,          ");
		sb.append("  		FIN_DEP_SC,                            ");
		sb.append("  		FIN_INS_AUM_UP*100 AS FIN_INS_AUM_UP,  ");
		sb.append("  		FIN_INS_AUM_SC,                        ");
		sb.append("  		FIN_IND_AUM_UP,                        ");
		sb.append("  		NFIN_IND_SAT,                          ");
		sb.append("  		NFIN_IND_DECR,                         ");
		sb.append("  		NFIN_IND_CHECK,                        ");
		sb.append("  		NFIN_IND_KNCUST,                       ");
		sb.append("  		NFIN_IND_ABN,                          ");
		sb.append("  		NFIN_IND_MISS                          ");
		sb.append("  FROM TBPMS_CNR_PD_BS_ASS                      ");
		sb.append("  WHERE YEARMON = :YEARMON                      ");
		sb.append("  ORDER BY DECODE(MANA_TYPE,                    ");
		sb.append("  		  'SH-FC',   1 ,                       ");
		sb.append("  		  'SH-FCH',  2 ,                       ");
		sb.append("  		  'BH',      3 ,                       ");
		sb.append("  		  'RD',      4)                        ");
		qc.setObject("YEARMON", inputVO.getYearMon());

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setBsAssList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 修改和儲存主管考核目標及配分(Bs_Ass)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveBsAss(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int rst = 0;

		StringBuffer sb = new StringBuffer();
		sb.append("  MERGE INTO TBPMS_CNR_PD_BS_ASS MU                                                                                    ");
		sb.append("  USING (SELECT :YEARMON YEARMON, :MANA_TYPE MANA_TYPE, :FIN_INS_UP FIN_INS_UP, :FIN_INS_SC FIN_INS_SC,                ");
		sb.append("  	      :FIN_IND_UP FIN_IND_UP, :FIN_IND_SC FIN_IND_SC, :FIN_DEP_UP FIN_DEP_UP, :FIN_DEP_SC FIN_DEP_SC,             ");
		sb.append("  	      :FIN_INS_AUM_UP FIN_INS_AUM_UP, :FIN_INS_AUM_SC FIN_INS_AUM_SC, :FIN_IND_AUM_UP FIN_IND_AUM_UP,             ");
		sb.append("  	      :NFIN_IND_SAT NFIN_IND_SAT, :NFIN_IND_DECR NFIN_IND_DECR, :NFIN_IND_CHECK NFIN_IND_CHECK,                   ");
		sb.append("  	      :NFIN_IND_KNCUST NFIN_IND_KNCUST, :NFIN_IND_ABN NFIN_IND_ABN, :NFIN_IND_MISS NFIN_IND_MISS, :EMP_ID EMP_ID  ");
		sb.append("  	   FROM DUAL)SO                                                                                                   ");
		sb.append("  	   ON (SO.YEARMON = MU.YEARMON AND SO.MANA_TYPE = MU.MANA_TYPE)                                                   ");
		sb.append("  WHEN MATCHED THEN                                                                                                    ");
		sb.append("  	 UPDATE SET 	MU.FIN_INS_UP        = SO.FIN_INS_UP,          ");
		sb.append("  					MU.FIN_INS_SC        = SO.FIN_INS_SC,          ");
		sb.append("  					MU.FIN_IND_UP        = SO.FIN_IND_UP,          ");
		sb.append("  					MU.FIN_IND_SC        = SO.FIN_IND_SC,          ");
		sb.append("  					MU.FIN_DEP_UP        = SO.FIN_DEP_UP,          ");
		sb.append("  					MU.FIN_DEP_SC        = SO.FIN_DEP_SC,          ");
		sb.append("  					MU.FIN_INS_AUM_UP    = SO.FIN_INS_AUM_UP,      ");
		sb.append("  					MU.FIN_INS_AUM_SC    = SO.FIN_INS_AUM_SC,      ");
		sb.append("  					MU.FIN_IND_AUM_UP    = SO.FIN_IND_AUM_UP,      ");
		sb.append("  					MU.NFIN_IND_SAT      = SO.NFIN_IND_SAT,        ");
		sb.append("  					MU.NFIN_IND_DECR     = SO.NFIN_IND_DECR,       ");
		sb.append("  					MU.NFIN_IND_CHECK    = SO.NFIN_IND_CHECK,      ");
		sb.append("  					MU.NFIN_IND_KNCUST   = SO.NFIN_IND_KNCUST,     ");
		sb.append("  					MU.NFIN_IND_ABN      = SO.NFIN_IND_ABN,        ");
		sb.append("  					MU.NFIN_IND_MISS     = SO.NFIN_IND_MISS,       ");
		sb.append("  					MU.VERSION           = MU.VERSION + 1,         ");
		sb.append("  					MU.MODIFIER          = SO.EMP_ID,              ");
		sb.append("  					MU.LASTUPDATE        = SYSDATE                 ");
		sb.append("  WHEN NOT MATCHED THEN                                                                                                             ");
		sb.append("  	 INSERT (YEARMON, MANA_TYPE, FIN_INS_UP, FIN_INS_SC, FIN_IND_UP, FIN_IND_SC, FIN_DEP_UP,                                       ");
		sb.append("  			 FIN_DEP_SC, FIN_INS_AUM_UP, FIN_INS_AUM_SC, FIN_IND_AUM_UP, NFIN_IND_SAT, NFIN_IND_DECR,                              ");
		sb.append("  			 NFIN_IND_CHECK, NFIN_IND_KNCUST, NFIN_IND_ABN, NFIN_IND_MISS, VERSION, CREATETIME,                                    ");
		sb.append("  			 CREATOR, MODIFIER, LASTUPDATE)                                                                                        ");
		sb.append("  	 VALUES (SO.YEARMON, SO.MANA_TYPE, SO.FIN_INS_UP, SO.FIN_INS_SC, SO.FIN_IND_UP, SO.FIN_IND_SC, SO.FIN_DEP_UP,                  ");
		sb.append("  			 SO.FIN_DEP_SC, SO.FIN_INS_AUM_UP, SO.FIN_INS_AUM_SC, SO.FIN_IND_AUM_UP, SO.NFIN_IND_SAT, SO.NFIN_IND_DECR,            ");
		sb.append("  			 SO.NFIN_IND_CHECK, SO.NFIN_IND_KNCUST, SO.NFIN_IND_ABN, SO.NFIN_IND_MISS, 1, SYSDATE, SO.EMP_ID, SO.EMP_ID, SYSDATE)  ");

		qc.setQueryString(sb.toString());

		for (int i = 0; i < 4; i++)
		{
			qc.setObject("YEARMON", inputVO.getBsAssList().get(i).get("YEARMON"));
			qc.setObject("MANA_TYPE", inputVO.getBsAssList().get(i).get("MANA_TYPE"));

			qc.setObject("FIN_INS_UP", Float.parseFloat(String.valueOf(inputVO.getBsAssList().get(i).get("FIN_INS_UP")))/100);
			qc.setObject("FIN_INS_SC", inputVO.getBsAssList().get(i).get("FIN_INS_SC"));
			qc.setObject("FIN_IND_UP", Float.parseFloat(String.valueOf(inputVO.getBsAssList().get(i).get("FIN_IND_UP")))/100);
			qc.setObject("FIN_IND_SC", inputVO.getBsAssList().get(i).get("FIN_IND_SC"));
			qc.setObject("FIN_DEP_UP", Float.parseFloat(String.valueOf(inputVO.getBsAssList().get(i).get("FIN_DEP_UP")))/100);
			qc.setObject("FIN_DEP_SC", inputVO.getBsAssList().get(i).get("FIN_DEP_SC"));
			qc.setObject("FIN_INS_AUM_UP", Float.parseFloat(String.valueOf(inputVO.getBsAssList().get(i).get("FIN_INS_AUM_UP")))/100);
			qc.setObject("FIN_INS_AUM_SC", inputVO.getBsAssList().get(i).get("FIN_INS_AUM_SC"));
			qc.setObject("FIN_IND_AUM_UP", inputVO.getBsAssList().get(i).get("FIN_IND_AUM_UP"));
			qc.setObject("NFIN_IND_SAT", inputVO.getBsAssList().get(i).get("NFIN_IND_SAT"));
			qc.setObject("NFIN_IND_DECR", inputVO.getBsAssList().get(i).get("NFIN_IND_DECR"));
			qc.setObject("NFIN_IND_CHECK", inputVO.getBsAssList().get(i).get("NFIN_IND_CHECK"));
			qc.setObject("NFIN_IND_KNCUST", inputVO.getBsAssList().get(i).get("NFIN_IND_KNCUST"));
			qc.setObject("NFIN_IND_ABN", inputVO.getBsAssList().get(i).get("NFIN_IND_ABN"));
			qc.setObject("NFIN_IND_MISS", inputVO.getBsAssList().get(i).get("NFIN_IND_MISS"));

			qc.setObject("EMP_ID", inputVO.getUserID());
			int result = dam.exeUpdate(qc);
			rst = rst + result;
		}

		// 修改設定為已設定
		if(rst == 4)
		{
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					    ");
			sb1.append("		SET T.BS_ASS = '1'||SUBSTR(T.BS_ASS,2)  	    ");
			sb1.append("		WHERE T.YEARMON = :YEARMON 					    ");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
		}
		outputVO.setBackResult(rst);
		sendRtnObject(outputVO);
	}

	
	 /**
	  * 查詢存投保收益目標表（主管 參數2）
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void queryBsAum(Object body, IPrimitiveMap header) throws JBranchException {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT BRANCH_NBR,                 ");
			sb.append("         PERSONNEL_CATEGORY,                   ");
			sb.append("         INCOME_MONTH,                   ");
			sb.append("        	SPREADS_MONTH              		     ");
			sb.append("  FROM TBPMS_CNR_PD_CTS_GOAL		     ");
			
			sb.append("    WHERE YEARMON = :YEARMON		   ");
			sb.append("    ORDER BY BRANCH_NBR ");
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setQueryString(sb.toString());
			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setBsAumList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
			
			/*
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setBsAumList(result);
			sendRtnObject(outputVO);*/
	}

	 /**
	  * 插入或更新存投保收益目標表（主管 參數2）
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings("rawtypes")
	public void addBsAum(Object body, IPrimitiveMap header) throws JBranchException {
		int flag = 0;
		try {
				PMS210InputVO inputVO = (PMS210InputVO) body;
				PMS210OutputVO outputVO = new PMS210OutputVO();
				List<String> list =  new ArrayList<String>();
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
				Sheet sheet[] = workbook.getSheets();
				//有表頭.xls文檔
				//清空臨時表
				dam = this.getDataAccessManager();
				QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
				String dsql = " TRUNCATE TABLE TBPMS_CNR_PD_CTS_GOAL_U ";
				dcon.setQueryString(dsql.toString());
				dam.exeUpdate(dcon);
				String lab = null;
				
					for(int a=0;a<sheet.length;a++){
						for(int i=1;i<sheet[a].getRows();i++){
							for(int j=0;j<sheet[a].getColumns();j++){
								lab = sheet[a].getCell(j, i).getContents();
								list.add(lab);
							}
							
							//excel表格記行數
							flag++;
							
							//判斷當前上傳數據月份是否一致
							if(!list.get(0).equals(inputVO.getYearMon())){
								throw new APException("上傳數據選擇月份不一致");
							}
							
							//判斷當前上傳數據欄位個數是否一致
							if(list.size()!=5){
								throw new APException("上傳數據欄位個數不一致");
							}
							
							//SQL指令
							StringBuffer sb = new StringBuffer();
							dam = this.getDataAccessManager();
							QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb.append("   INSERT INTO TBPMS_CNR_PD_CTS_GOAL_U (YEARMON,	     ");
							sb.append("  		BRANCH_NBR,            				         ");
							sb.append("  		PERSONNEL_CATEGORY,           			     ");
							sb.append("  		INCOME_MONTH,            					 ");
							sb.append("  		SPREADS_MONTH,            					 ");
							sb.append("  		RNUM,            					         ");
							sb.append("  		VERSION,            						 ");
							sb.append("  		CREATETIME,             					 ");
							sb.append("  		CREATOR,             						 ");
							sb.append("  		MODIFIER,         						     ");
							sb.append("  		LASTUPDATE )             					 ");
							sb.append("  	VALUES(:YEARMON,            				     ");
							sb.append("  		:BRANCH_NBR,             				     ");
							sb.append("  		:PERSONNEL_CATEGORY,             		     ");
							sb.append("  		:INCOME_MONTH,             					 ");
							sb.append("  		:SPREADS_MONTH,             			     ");
							sb.append("  		:RNUM,             					         ");
							sb.append("  		:VERSION,           					     ");
							sb.append("  		SYSDATE,           				             ");
							sb.append("  		:CREATOR,            					     ");
							sb.append("  		:MODIFIER,         					         ");
							sb.append("  		SYSDATE)          				             ");
							qc.setObject("YEARMON",list.get(0).trim()                         );
							qc.setObject("BRANCH_NBR",list.get(1).trim()                      );
							qc.setObject("PERSONNEL_CATEGORY",list.get(2).trim()              );
							qc.setObject("INCOME_MONTH",list.get(3).trim()                    );
							qc.setObject("SPREADS_MONTH",list.get(4).trim()                   );
							qc.setObject("RNUM",flag                                          );
							qc.setObject("VERSION","0"                                        );
							qc.setObject("CREATOR", inputVO.getUserID()                       );
							qc.setObject("MODIFIER", inputVO.getUserID()                      );
							qc.setQueryString(sb.toString());
							dam.exeUpdate(qc);
							list.clear();
						}
					}		
				this.sendRtnObject(outputVO);
		} catch (Exception e) {
				logger.error("文檔上傳失敗");
				throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
		
	}
	
	
	/**
	 * 查詢主管請領分(Bs_Point 主管 參數3)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryBsPoint(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT YEARMON,          ");
		sb.append("         MANA_LEVEL,       ");
		sb.append("         PLE_SCORE         ");

		sb.append("  FROM TBPMS_CNR_PD_BS_POINT");
		sb.append("  WHERE YEARMON = :YEARMON");
		qc.setObject("YEARMON", inputVO.getYearMon());

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setBsPointList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 修改和儲存主管請領分(Bs_Point)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveBsPoint(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int rst = 0;

		StringBuffer sb = new StringBuffer();
		sb.append("  MERGE INTO TBPMS_CNR_PD_BS_POINT MU                                                           ");
		sb.append("  USING(SELECT :YEARMON YEARMON, :MANA_LEVEL MANA_LEVEL, :PLE_SCORE PLE_SCORE, :EMP_ID EMP_ID   ");
		sb.append("  	FROM DUAL)SO                                                                               ");
		sb.append("  	ON (SO.YEARMON = MU.YEARMON AND SO.MANA_LEVEL = MU.MANA_LEVEL)                             ");
		sb.append("  WHEN MATCHED THEN                                                                             ");
		sb.append("  	UPDATE SET MU.PLE_SCORE                             = SO.PLE_SCORE                         ");
		sb.append("                    ,MU.VERSION                          = MU.VERSION + 1                       ");
		sb.append("                    ,MU.MODIFIER                         = SO.EMP_ID                            ");
		sb.append("                    ,MU.LASTUPDATE                       = SYSDATE                              ");
		sb.append("  WHEN NOT MATCHED THEN                                                                         ");
		sb.append("  	INSERT (YEARMON, MANA_LEVEL, PLE_SCORE, VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE)    ");
		sb.append("  	VALUES (SO.YEARMON, SO.MANA_LEVEL, SO.PLE_SCORE, 1,SYSDATE,SO.EMP_ID,SO.EMP_ID,SYSDATE)    ");

		qc.setQueryString(sb.toString());

		for (int i = 0; i < 4; i++)
		{

			qc.setObject("YEARMON", inputVO.getBsPointList().get(i).get("YEARMON"));
			qc.setObject("MANA_LEVEL", inputVO.getBsPointList().get(i).get("MANA_LEVEL"));
			qc.setObject("PLE_SCORE", inputVO.getBsPointList().get(i).get("PLE_SCORE"));

			qc.setObject("EMP_ID", inputVO.getUserID());

			int result = dam.exeUpdate(qc);
			rst = rst + result;
		}

		// 修改設定為已設定
		if(rst == 4)
		{
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					    ");
			sb1.append("		SET T.BS_POINT = '1'||SUBSTR(T.BS_POINT,2)  	");
			sb1.append("		WHERE T.YEARMON = :YEARMON 					    ");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
		}
		outputVO.setBackResult(rst);
		sendRtnObject(outputVO);
	}

	
	
	 /**
	  * 查詢主管獎金表 (主管表 參數4)
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void queryBsPrize(Object body, IPrimitiveMap header) throws JBranchException {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT BEGIN_VALUES||BEGIN_FLAG||'-'||END_VALUES||END_FLAG  AS PERF_VALUES,   ");
			sb.append("         BRANCH_AREA_A,                                             ");
			sb.append("        	BRANCH_AREA_B,              							   ");
			sb.append("        	BRANCH_A,            		     						   ");
			sb.append("        	BRANCH_B,              		   							   ");
			sb.append("        	BRANCH_C             		    						   ");
			sb.append("  FROM TBPMS_CNR_PD_BS_PRIZE		   	   							   ");
			sb.append("  WHERE YEARMON = :YEARMON		        						   ");
			sb.append("  ORDER BY BEGIN_VALUES                   						   ");
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setQueryString(sb.toString());
			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setBsPrizeList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
			
			/*
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setBsPrizeList(result);
			sendRtnObject(outputVO);*/
	}
	 
	 /**
	  * 文件上傳，并將數據添加到主管獎金表(主管表 參數4)
	  * @param body
	  * @param header
	  * @throws APException
	  */
 	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes" })
	public void addBsPrize(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_PD_BS_PRIZE_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
			
			for(int a=0;a<sheet.length;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					
					//excel表格記行數
					flag++;
					
					//判斷當前上傳數據月份是否一致
					if(!list.get(0).equals(inputVO.getYearMon())){
						throw new APException("上傳數據選擇月份不一致");
					}
					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=10){
						throw new APException("上傳數據欄位個數不一致");
					}
					
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_CNR_PD_BS_PRIZE_U (YEARMON,	     ");
					sb.append("  		BEGIN_VALUES,            				     ");
					sb.append("  		BEGIN_FLAG,            				     ");
					sb.append("  		END_VALUES,            				     ");
					sb.append("  		END_FLAG,            				     ");
					sb.append("  		BRANCH_AREA_A,           					 ");
					sb.append("  		BRANCH_AREA_B,            					 ");
					sb.append("  		BRANCH_A,            					     ");
					sb.append("  		BRANCH_B,            					     ");
					sb.append("  		BRANCH_C,            					     ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:BEGIN_VALUES,             				     ");
					sb.append("  		:BEGIN_FLAG,             				     ");
					sb.append("  		:END_VALUES,             				     ");
					sb.append("  		:END_FLAG,             				     ");
					sb.append("  		:BRANCH_AREA_A,             				 ");
					sb.append("  		:BRANCH_AREA_B,             				 ");
					sb.append("  		:BRANCH_A,             					     ");
					sb.append("  		:BRANCH_B,             					     ");
					sb.append("  		:BRANCH_C,             					     ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",list.get(0).trim()                         );
					qc.setObject("BEGIN_VALUES",list.get(1).trim()                     );
					qc.setObject("BEGIN_FLAG",list.get(2).trim()                     );
					qc.setObject("END_VALUES",list.get(3).trim()                     );
					qc.setObject("END_FLAG",list.get(4).trim()                     );
					qc.setObject("BRANCH_AREA_A",list.get(5).trim()                   );
					qc.setObject("BRANCH_AREA_B",list.get(6).trim()                   );
					qc.setObject("BRANCH_A",list.get(7).trim()                        );
					qc.setObject("BRANCH_B",list.get(8).trim()                        );
					qc.setObject("BRANCH_C",list.get(9).trim()                        );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserID()                       );
					qc.setObject("MODIFIER", inputVO.getUserID()                      );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}		
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
		
	/**
	 * 查詢當月轄下專員達Goal率對應折數(主管表 參數5)
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryBsGoalTar(Object body, IPrimitiveMap header) throws JBranchException {
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT GOAL_RATE_START,                 ");
		sb.append("         GOAL_RATE_END,                   ");
		sb.append("        	BONUS_DISC              		     ");
		sb.append("  FROM TBPMS_CNR_PD_BS_GOAL		     ");
		
		sb.append("    WHERE YEARMON = :YEARMON		   ");
		sb.append("    ORDER BY GOAL_RATE_START ");
		qc.setObject("YEARMON", inputVO.getYearMon());
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setBsGoalTarList(result);
		sendRtnObject(outputVO);
	}
		 
	 /**
	  * 更新當月轄下專員達Goal率對應折數表(主管表 參數5)
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void updateBsGoalTar(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qcDel = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sbDel = new StringBuffer();					//執行刪除語句
		sbDel.append("  DELETE FROM TBPMS_CNR_PD_BS_GOAL  ");
		sbDel.append("  WHERE YEARMON =:YEARMON 		  ");
		qcDel.setObject("YEARMON", inputVO.getYearMon());
		qcDel.setQueryString(sbDel.toString());
		dam.exeUpdate(qcDel);
			StringBuffer sb = new StringBuffer();						//執行插入語句
			sb.append("  	INSERT INTO TBPMS_CNR_PD_BS_GOAL(YEARMON,	     ");
			sb.append("  		GOAL_RATE_START,           					 ");
			sb.append("  		GOAL_RATE_END,            					 ");
			sb.append("  		BONUS_DISC,            						 ");
			sb.append("  		VERSION,           						     ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:YEARMON,            				     ");
			sb.append("  		:GOAL_RATE_START,             				 ");
			sb.append("  		:GOAL_RATE_END,             				 ");
			sb.append("  		:BONUS_DISC,            					 ");
			sb.append("  		0,           					    		 ");
			sb.append("  		sysdate,           				    		 ");
			sb.append("  		:userId,            					     ");
			sb.append("  		:userId,         					   	     ");
			sb.append("  		sysdate)          				    		 ");
			
			/*將bsGoalTarList的內容插入數據庫*/
			for(int i=0; i < inputVO.getBsGoalTarList().size(); i++){
				QueryConditionIF qcInsert = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qcInsert.setObject("YEARMON", inputVO.getYearMon());
				qcInsert.setObject("GOAL_RATE_START", inputVO.getBsGoalTarList().get(i).get("GOAL_RATE_START"));
				qcInsert.setObject("GOAL_RATE_END", inputVO.getBsGoalTarList().get(i).get("GOAL_RATE_END"));
				qcInsert.setObject("BONUS_DISC", inputVO.getBsGoalTarList().get(i).get("BONUS_DISC"));
				qcInsert.setObject("userId", inputVO.getUserID());
				qcInsert.setQueryString(sb.toString());
				dam.exeUpdate(qcInsert);
				
			}
		//更新主表，將BS_GOAL_TAR第一位（狀態）設置為1
		StringBuffer sb1 = new StringBuffer();
		QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
		sb1.append("		SET T.BS_GOAL_TAR = '1'||SUBSTR(T.BS_GOAL_TAR,2)  	");
		sb1.append("		WHERE T.yearmon = :YEARMON 					");
		
		qc1.setObject("YEARMON", inputVO.getYearMon());
		qc1.setQueryString(sb1.toString());
		dam.exeUpdate(qc1);
		outputVO.setBsGoalTarList(null);
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	
	}
	
	
	
	/**
	 * 查詢主管調任新舊分行獎金成數(Bs_Bonus 主管 參數6)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryBsBonus(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT N_MONTH,    ");
		sb.append("  		NEW_JURI,   ");
		sb.append("  		NOW_JURI    ");

		sb.append("  FROM TBPMS_CNR_PD_BS_BONUS_INSE");
		sb.append("  WHERE YEARMON = :YEARMON");
		sb.append("  ORDER BY N_MONTH");
		
		qc.setObject("YEARMON", inputVO.getYearMon());

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setBsBonusList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 修改和儲存主管調任新舊分行獎金成數(Bs_Bonus)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveBsBonus(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();

		//刪除當月已存在數據
		QueryConditionIF qc2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb2 = new StringBuffer();
		sb2.append("  DELETE TBPMS_CNR_PD_BS_BONUS_INSE");
		sb2.append("  WHERE YEARMON = :YEARMON1");
		qc2.setObject("YEARMON1", inputVO.getYearMon());
		qc2.setQueryString(sb2.toString());
		dam.exeUpdate(qc2);

		//將頁面上的數據添加到數據庫
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("  INSERT INTO TBPMS_CNR_PD_BS_BONUS_INSE (                 ");
		sb.append("      YEARMON, N_MONTH, NEW_JURI, NOW_JURI, VERSION,       ");
		sb.append("      CREATETIME, CREATOR, MODIFIER, LASTUPDATE)           ");
		sb.append("  VALUES (:YEARMON, :N_MONTH, :NEW_JURI, :NOW_JURI, 1,     ");
		sb.append("  	 SYSDATE, :EMPID, :EMPID, SYSDATE )                   ");
		qc.setQueryString(sb.toString());

		int rst = 0;
		for (int i = 0; i < inputVO.getBsBonusList().size(); i++)
		{
			qc.setObject("N_MONTH", inputVO.getBsBonusList().get(i).get("N_MONTH"));
			qc.setObject("NEW_JURI", inputVO.getBsBonusList().get(i).get("NEW_JURI"));
			qc.setObject("NOW_JURI", inputVO.getBsBonusList().get(i).get("NOW_JURI"));
			qc.setObject("EMPID", inputVO.getUserID());

			qc.setObject("YEARMON", inputVO.getYearMon());
			int result = dam.exeUpdate(qc);
			rst = rst + result;
		}

		// 修改設定為已設定
		if(rst == inputVO.getBsBonusList().size())
		{
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					            ");
			sb1.append("		SET T.BS_BONUS_INSE = '1'||SUBSTR(T.BS_BONUS_INSE,2)  	");
			sb1.append("		WHERE T.YEARMON = :YEARMON 					            ");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
		}
		outputVO.setBackResult(rst);
		sendRtnObject(outputVO);
	}
	/**
	 * 查詢基金短Trade參數 共用參數2
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryShortTran(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT YEARMON,    ");
		sb.append("  		TSDATE,     ");
		sb.append("  		TEDATE,     ");
		sb.append("  		NSDATE,     ");
		sb.append("  		NEDATE,     ");
		sb.append("  		TRADES,     ");
		sb.append("  		TRADEE      ");

		sb.append("  FROM TBPMS_CNR_PD_SHORT_TRAN");
		sb.append("  WHERE YEARMON = :YEARMON");
		
		qc.setObject("YEARMON", inputVO.getYearMon());

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setShortTranList(result);
		sendRtnObject(outputVO);
	}
	
	/**
	 * 修改和儲存基金短Trade參數 共用參數2
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveShortTran(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb2 = new StringBuffer();
		sb2.append("  DELETE TBPMS_CNR_PD_SHORT_TRAN");
		sb2.append("  WHERE YEARMON = :YEARMON");
		qc2.setObject("YEARMON", inputVO.getYearMon());
		qc2.setQueryString(sb2.toString());
		dam.exeUpdate(qc2);
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("   	INSERT INTO TBPMS_CNR_PD_SHORT_TRAN(       ");
		sb.append("   		YEARMON     ,                          ");
		sb.append("   	    TSDATE      ,                          ");
		sb.append("   	    TEDATE      ,                          ");
		sb.append("   	    NSDATE      ,                          ");
		sb.append("   	    NEDATE      ,                          ");
		sb.append("   	    TRADES      ,                          ");
		sb.append("   	    TRADEE      ,                          ");
		sb.append("   	    VERSION     ,                          ");
		sb.append("   	    CREATETIME  ,                          ");
		sb.append("   	    CREATOR     ,                          ");
		sb.append("   	    MODIFIER    ,                          ");
		sb.append("   	    LASTUPDATE                             ");
		sb.append("   	) VALUES (                                 ");
		sb.append("   		:YEARMON     ,                         ");
		sb.append("   	    :TSDATE      ,                         ");
		sb.append("   	    :TEDATE      ,                         ");
		sb.append("   	    :NSDATE      ,                         ");
		sb.append("   	    :NEDATE      ,                         ");
		sb.append("   	    :TRADES      ,                         ");
		sb.append("   	    :TRADEE      ,                         ");
		sb.append("   	    '1'          ,                         ");
		sb.append("   	    SYSDATE      ,                         ");
		sb.append("   	    :userId      ,                         ");
		sb.append("   	    :userId      ,                         ");
		sb.append("   	    SYSDATE                                ");
		sb.append("   	)                                          ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setObject("TSDATE", sdf.format(new Date(Long.parseLong(inputVO.getTSDate()))));
		qc.setObject("TEDATE", sdf.format(new Date(Long.parseLong(inputVO.getTEDate()))));
		if(null!=inputVO.getNSDate() && !"".equals(inputVO.getNSDate())
				&& null!=inputVO.getNEDate() && !"".equals(inputVO.getNEDate())){
			qc.setObject("NSDATE", sdf.format(new Date(Long.parseLong(inputVO.getNSDate()))));
			qc.setObject("NEDATE", sdf.format(new Date(Long.parseLong(inputVO.getNEDate()))));			
		}else{
			qc.setObject("NSDATE", "");
			qc.setObject("NEDATE", "");
		}
		qc.setObject("TRADES", inputVO.getTradeS());
		qc.setObject("TRADEE", inputVO.getTradeE());
		qc.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		qc.setQueryString(sb.toString());
		int rst = dam.exeUpdate(qc);
		
		
			
		
		// 修改設定為已設定
		if(rst > 0)
		{
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					                ");
			sb1.append("		SET T.SHORT_TRAN = '1'||SUBSTR(T.SHORT_TRAN,2)  	");
			sb1.append("		WHERE T.YEARMON = :YEARMON 					                ");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
		}
		
		outputVO.setBackResult(rst);
		sendRtnObject(outputVO);
	}
	
	
	/**
	  * 查詢非財務扣分明細表(共用參數表 參數3)
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void querySpMar(Object body, IPrimitiveMap header) throws JBranchException {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT YEARMON,BRANCH_NBR,BRANCH_NAME,                      			");
			sb.append("  	MISS_CONT,PERSONNEL_CATEGORY,EMP_NAME,EMP_ID,CUST_DIS,   			");
			sb.append("     REG_COM_EXT_SPEC,AUD_MISS,OBT_LICEN,KYC,SATISFACTION,    			");
			sb.append("     UN_TRANS,LACK_IND_RATE,HA_MONTH,PERIOD_TIME,CUST_TYPE,EMP_GROUPS    ");
			sb.append("  FROM TBPMS_CNR_PD_SP_MAR		                             			");
			sb.append("    WHERE YEARMON = :YEARMON		                             			");
			sb.append("    ORDER BY BRANCH_NBR                                       			");
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setQueryString(sb.toString());
			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setSpMarList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
			/*List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setSpMarList(result);
			sendRtnObject(outputVO);*/
	}
		
	 /**
	  * 文件上傳，并將數據添加到非財務扣分明細(共用參數表 參數3)
	  * @param body
	  * @param header
	  * @throws APException
	  */
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes" })
	public void addSpMar(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_PD_SP_MAR_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
			for(int a=0;a<sheet.length;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					
					//excel表格記行數
					flag++;
					
					//判斷當前上傳數據月份是否一致
					if(!list.get(0).equals(inputVO.getYearMon())){
						throw new APException("上傳數據選擇月份不一致");
					}
					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=19){
						throw new APException("上傳數據欄位個數不一致");
					}
					
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_CNR_PD_SP_MAR_U (YEARMON,	     ");
					sb.append("  		BRANCH_NBR,            				         ");
					sb.append("  		BRANCH_NAME,           					     ");
					sb.append("  		MISS_CONT,            					     ");
					sb.append("  		PERSONNEL_CATEGORY,            			     ");
					sb.append("  		EMP_NAME,            					     ");
					sb.append("  		EMP_ID,            					         ");
					sb.append("  		CUST_DIS,            					     ");
					sb.append("  		REG_COM_EXT_SPEC,            				 ");
					sb.append("  		AUD_MISS,            					     ");
					sb.append("  		OBT_LICEN,            					     ");
					sb.append("  		KYC,            					         ");
					sb.append("  		SATISFACTION,            					 ");
					sb.append("  		UN_TRANS,            					     ");
					sb.append("  		LACK_IND_RATE,            					 ");
					sb.append("  		HA_MONTH,            					     ");
					sb.append("  		PERIOD_TIME,            					 ");
					sb.append("  		CUST_TYPE,            					     ");
					sb.append("  		EMP_GROUPS,            					     ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:YEARMON,            				     ");
					sb.append("  		:BRANCH_NBR,             				     ");
					sb.append("  		:BRANCH_NAME,             				     ");
					sb.append("  		:MISS_CONT,             				     ");
					sb.append("  		:PERSONNEL_CATEGORY,             			 ");
					sb.append("  		:EMP_NAME,             					     ");
					sb.append("  		:EMP_ID,             					     ");
					sb.append("  		:CUST_DIS,             					     ");
					sb.append("  		:REG_COM_EXT_SPEC,             				 ");
					sb.append("  		:AUD_MISS,             					     ");
					sb.append("  		:OBT_LICEN,             					 ");
					sb.append("  		:KYC,             					         ");
					sb.append("  		:SATISFACTION,             					 ");
					sb.append("  		:UN_TRANS,             					     ");
					sb.append("  		:LACK_IND_RATE,             				 ");
					sb.append("  		:HA_MONTH,             					     ");
					sb.append("  		:PERIOD_TIME,             					 ");
					sb.append("  		:CUST_TYPE,             					 ");
					sb.append("  		:EMP_GROUPS,             					 ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("YEARMON",list.get(0).trim()                         );
					qc.setObject("BRANCH_NBR",list.get(1).trim()                      );
					qc.setObject("BRANCH_NAME",list.get(2).trim()                     );
					qc.setObject("MISS_CONT",list.get(3).trim()                       );
					qc.setObject("PERSONNEL_CATEGORY",list.get(4).trim()              );
					qc.setObject("EMP_NAME",list.get(5).trim()                        );
					qc.setObject("EMP_ID",list.get(6).trim()                          );
					qc.setObject("CUST_DIS",list.get(7).trim()                        );
					qc.setObject("REG_COM_EXT_SPEC",list.get(8).trim()                );
					qc.setObject("AUD_MISS",list.get(9).trim()                        );
					qc.setObject("OBT_LICEN",list.get(10).trim()                      );
					qc.setObject("KYC",list.get(11).trim()                            );
					qc.setObject("SATISFACTION",list.get(12).trim()                   );
					qc.setObject("UN_TRANS",list.get(13).trim()                       );
					qc.setObject("LACK_IND_RATE",list.get(14).trim()                  );
					qc.setObject("HA_MONTH",list.get(15).trim()                       );
					qc.setObject("PERIOD_TIME",list.get(16).trim()                    );
					qc.setObject("CUST_TYPE",list.get(17).trim()                      );
					qc.setObject("EMP_GROUPS",list.get(18).trim()                      );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", inputVO.getUserID()                       );
					qc.setObject("MODIFIER", inputVO.getUserID()                      );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}		
		this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	
	/**
	 * 查詢非財務指標及獨立列示重大缺失最低扣減金額(Sp_UnLack_Ind 共用參數 參數4)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void querySpUnLackInd(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT DECODE(ROLE_ID,0,'理専',1,'一階主管',2,'二階主管') AS ROLE_ID, ");
		sb.append("         MIN_DED,                            					");
		sb.append("         DEF_DED_RATE*100 AS DEF_DED_RATE,   					");
		sb.append("         MIN_DEF_DED                         					");
		
		sb.append("  FROM TBPMS_CNR_PD_SP_DEF_DED T             					");
		sb.append("  WHERE YEARMON = :YEARMON                   					");
		qc.setObject("YEARMON", inputVO.getYearMon());
		sb.append("  ORDER BY T.ROLE_ID                         					");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setSpUnLackIndList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 修改和儲存非財務指標及獨立列示重大缺失最低扣減金額(Sp_UnLack_Ind)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveSpUnLackInd(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  MERGE  INTO TBPMS_CNR_PD_SP_DEF_DED MU                                                          ");
		sb.append("  USING (SELECT :YEARMON YEARMON, DECODE(:ROLE_ID,'理専',0,'一階主管',1,'二階主管',2) ROLE_ID,           ");
		sb.append("     		   :MIN_DED MIN_DED, :DEF_DED_RATE DEF_DED_RATE,                                     ");
		sb.append("     		   :MIN_DEF_DED MIN_DEF_DED, :EMP_ID EMP_ID                                          ");
		sb.append("  		FROM DUAL) SO                                                                            ");
		sb.append("  		ON (SO.YEARMON = MU.YEARMON AND SO.ROLE_ID = MU.ROLE_ID)                                 ");
		sb.append("  WHEN MATCHED THEN                                                                               ");
		sb.append("  		UPDATE SET MU.MIN_DED = SO.MIN_DED                                                       ");
		sb.append("  				  ,MU.DEF_DED_RATE = SO.DEF_DED_RATE                                             ");
		sb.append("  				  ,MU.MIN_DEF_DED = SO.MIN_DEF_DED                                               ");
		sb.append("  				  ,MU.VERSION                          = MU.VERSION + 1                          ");
		sb.append("                   ,MU.MODIFIER                         = SO.EMP_ID                               ");
		sb.append("                   ,MU.LASTUPDATE                       = SYSDATE                                 ");
		sb.append("  WHEN NOT MATCHED THEN                                                                           ");
		sb.append("  		INSERT (YEARMON, ROLE_ID, MIN_DED, DEF_DED_RATE, MIN_DEF_DED,                            ");
		sb.append("		    		VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE)                                  ");
		sb.append("  		VALUES (SO.YEARMON, SO.ROLE_ID, SO.MIN_DED, SO.DEF_DED_RATE, SO.MIN_DEF_DED,             ");
		sb.append("		    		1,SYSDATE,SO.EMP_ID,SO.EMP_ID,SYSDATE)                                           ");
		qc.setQueryString(sb.toString());
		
		int rst = 0;
		for(int i=0; i < inputVO.getSpUnLackIndList().size(); i++){
			qc.setObject("YEARMON", inputVO.getYearMon());
			//DECODE(roleStr,"理専",0,"一階主管",1,"二階主管",2)
			qc.setObject("ROLE_ID", inputVO.getSpUnLackIndList().get(i).get("ROLE_ID"));
			qc.setObject("MIN_DED", inputVO.getSpUnLackIndList().get(i).get("MIN_DED"));
			qc.setObject("DEF_DED_RATE", Float.parseFloat(String.valueOf(inputVO.getSpUnLackIndList().get(i).get("DEF_DED_RATE")))/100);
			qc.setObject("MIN_DEF_DED", inputVO.getSpUnLackIndList().get(i).get("MIN_DEF_DED"));
			qc.setObject("EMP_ID", inputVO.getUserID());
			
			int result = dam.exeUpdate(qc);
			rst = rst + result;
		}
		
		// 修改設定為已設定
		if(rst == inputVO.getSpUnLackIndList().size())
		{
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					                ");
			sb1.append("		SET T.SP_UN_LACK_IND = '1'||SUBSTR(T.SP_UN_LACK_IND,2)  	");
			sb1.append("		WHERE T.YEARMON = :YEARMON 					                ");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
		}
		
		outputVO.setBackResult(rst);
		sendRtnObject(outputVO);
	}
	
	/**
	  * 查詢信託不計績契約編號表(共用參數表 參數6)
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void queryConNum(Object body, IPrimitiveMap header) throws JBranchException {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT CON_NUM,               ");
			sb.append("         YEARMON,               ");
			sb.append("         CUST_ID                ");
			sb.append("  FROM TBPMS_CNR_CON_NUM		   ");
			sb.append("    WHERE YEARMON = :YEARMON	   ");
			sb.append("    ORDER BY CUST_ID ");
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setQueryString(sb.toString());
			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setConNumList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
			/*List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setConNumList(result);
			sendRtnObject(outputVO);*/
	}
		
	 /**
	  * 信託不計績契約編號上傳(共用參數表 參數6)
	  * @param body
	  * @param header
	  * @throws APException
	  */
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes" })
	public void addConNum(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_CNR_CON_NUM_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
			
				for(int a=0;a<sheet.length;a++){
					for(int i=1;i<sheet[a].getRows();i++){
						for(int j=0;j<sheet[a].getColumns();j++){
							lab = sheet[a].getCell(j, i).getContents();
							list.add(lab);
						}
						
						//excel表格記行數
						flag++;
						
						//判斷當前上傳數據月份是否一致
						if(!list.get(0).equals(inputVO.getYearMon())){
							throw new APException("上傳數據選擇月份不一致");
						}
						
						//判斷當前上傳數據欄位個數是否一致
						if(list.size()!=3){
							throw new APException("上傳數據欄位個數不一致");
						}
						
						//SQL指令
						StringBuffer sb = new StringBuffer();
						dam = this.getDataAccessManager();
						QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb.append("   INSERT INTO TBPMS_CNR_CON_NUM_U (YEARMON,	         ");
						sb.append("  		CON_NUM,            				         ");
						sb.append("  		CUST_ID,           					         ");
						sb.append("  		RNUM,            					         ");
						sb.append("  		VERSION,            						 ");
						sb.append("  		CREATETIME,             					 ");
						sb.append("  		CREATOR,             						 ");
						sb.append("  		MODIFIER,         						     ");
						sb.append("  		LASTUPDATE )             					 ");
						sb.append("  	VALUES(:YEARMON,            				     ");
						sb.append("  		:CON_NUM,             				         ");
						sb.append("  		:CUST_ID,             					     ");
						sb.append("  		:RNUM,             					         ");
						sb.append("  		:VERSION,           					     ");
						sb.append("  		SYSDATE,           				             ");
						sb.append("  		:CREATOR,            					     ");
						sb.append("  		:MODIFIER,         					         ");
						sb.append("  		SYSDATE)          				             ");
						qc.setObject("YEARMON",list.get(0).trim()                         );
						qc.setObject("CON_NUM",list.get(1).trim()                         );
						qc.setObject("CUST_ID",list.get(2).trim()                         );
						qc.setObject("RNUM",flag                                          );
						qc.setObject("VERSION","0"                                        );
						qc.setObject("CREATOR", inputVO.getUserID()                       );
						qc.setObject("MODIFIER", inputVO.getUserID()                      );
						qc.setQueryString(sb.toString());
						dam.exeUpdate(qc);
						list.clear();
					}
				}		
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("發生錯誤:%s",StringUtil.getStackTraceAsString(e));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	/**
	 * 各商品預設CNR分配率(DIS_RATE 共用參數 參數7)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryDisRate(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT YEARMON,           ");
		sb.append("         GOOD_TYPE,        ");
		sb.append("         DIS_RATE   ");
		sb.append("  FROM TBPMS_CNR_DIS_RATE");
		sb.append("  WHERE YEARMON = :YEARMON");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setDisRateList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 修改和儲各商品預設CNR分配率(DIS_RATE 共用參數 參數7)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveDisRate(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  UPDATE TBPMS_CNR_DIS_RATE                   ");
		sb.append("  	SET	DIS_RATE = :DIS_RATE,                ");
		sb.append("  		MODIFIER = :MODIFIER,                ");
		sb.append("  		LASTUPDATE = SYSDATE                 ");
		sb.append("  	WHERE GOOD_TYPE = :GOOD_TYPE             ");
		sb.append("  	AND	YEARMON = :YEARMON                   ");
		qc.setQueryString(sb.toString());
		
		int rst = 0;
		for(int i=0; i < inputVO.getDisRateList().size(); i++){
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setObject("DIS_RATE", inputVO.getDisRateList().get(i).get("DIS_RATE"));
			qc.setObject("GOOD_TYPE", inputVO.getDisRateList().get(i).get("GOOD_TYPE"));
			qc.setObject("MODIFIER", inputVO.getUserID());
			
			int result = dam.exeUpdate(qc);
			rst = rst + result;
		}
		
		// 修改設定為已設定
		if(rst == inputVO.getDisRateList().size())
		{
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					                ");
			sb1.append("		SET T.DIS_RATE = '1'||SUBSTR(T.DIS_RATE,2)  	");
			sb1.append("		WHERE T.YEARMON = :YEARMON 					                ");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
		}
		
		outputVO.setBackResult(rst);
		sendRtnObject(outputVO);
	}
	
	/**
	 * 查詢四大類存款預算利差率(DEPOSIT_RATE 共用參數 參數8)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryDepositRate(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT DEPOSIT_TYPE,          ");
		sb.append("         DEPOSIT_RATE           ");
		sb.append("  FROM TBPMS_CNR_DEPOSIT_RATE"   );
		sb.append("  WHERE YEARMON = :YEARMON"      );
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setDepositRateList(result);
		sendRtnObject(outputVO);
	}

	/**
	 * 修改四大類存款預算利差率(DEPOSIT_RATE 共用參數 參數8)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveDepositRate(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS210InputVO inputVO = (PMS210InputVO) body;
		PMS210OutputVO outputVO = new PMS210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("  UPDATE TBPMS_CNR_DEPOSIT_RATE               ");
		sb.append("  	SET	DEPOSIT_RATE = :DEPOSIT_RATE,        ");
		sb.append("  		MODIFIER = :MODIFIER,                ");
		sb.append("  		LASTUPDATE = SYSDATE                 ");
		sb.append("  	WHERE DEPOSIT_TYPE = :DEPOSIT_TYPE       ");
		sb.append("  	AND	YEARMON = :YEARMON                   ");
		qc.setQueryString(sb.toString());
		
		int rst = 0;
		for(int i=0; i < inputVO.getDepositRateList().size(); i++){
			qc.setObject("YEARMON", inputVO.getYearMon());
			qc.setObject("DEPOSIT_RATE", inputVO.getDepositRateList().get(i).get("DEPOSIT_RATE"));
			qc.setObject("DEPOSIT_TYPE", inputVO.getDepositRateList().get(i).get("DEPOSIT_TYPE"));
			qc.setObject("MODIFIER", inputVO.getUserID());
			
			int result = dam.exeUpdate(qc);
			rst = rst + result;
		}
		
		// 修改設定為已設定
		if(rst == inputVO.getDepositRateList().size())
		{
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("		UPDATE TBPMS_CNR_MAST T 					                ");
			sb1.append("		SET T.DEPOSIT_RATE = '1'||SUBSTR(T.DEPOSIT_RATE,2)  	    ");
			sb1.append("		WHERE T.YEARMON = :YEARMON 					                ");
			qc1.setObject("YEARMON", inputVO.getYearMon());
			qc1.setQueryString(sb1.toString());
			dam.exeUpdate(qc1);
		}
		
		outputVO.setBackResult(rst);
		sendRtnObject(outputVO);
	}
	
	/**
	 * 理專參數5調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callSpBouns(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS716.SP_TBPMS_CNR_PD_SP_BONUS(? ,? ) ");
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
			}else{
				// 更新主表，將SP_BOUNS第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
				sb1.append("		SET T.SP_BOUNS = '1'||SUBSTR(T.SP_BOUNS,2), ");
				sb1.append("			T.MODIFIER = :MODIFIER, 				");
				sb1.append("			T.LASTUPDATE = SYSDATE 				    ");
				sb1.append("		WHERE T.YEARMON = :YEARMON 					");
				qc1.setObject("MODIFIER", inputVO.getUserID());
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
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
	 * 理專參數9調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callSpSalBen(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS716.SP_TBPMS_CNR_PD_SP_SAL_BEN(? ,? ) ");
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
			}else{
				// 更新主表，將SP_BOUNS第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 				     	");
				sb1.append("		SET T.SP_SAL_BEN = '1'||SUBSTR(T.SP_SAL_BEN,2), ");
				sb1.append("			T.MODIFIER = :MODIFIER, 				    ");
				sb1.append("			T.LASTUPDATE = SYSDATE 				        ");
				sb1.append("		WHERE T.YEARMON = :YEARMON 					    ");
				qc1.setObject("MODIFIER", inputVO.getUserID());
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
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
	 * 理專參數10調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callNewBounty(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS716.SP_TBPMS_CNR_PD_NEW_BOUNTY(? ,? ) ");
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
			}else{
				// 更新主表，將NEW_BOUNTY第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 					   ");
				sb1.append("		SET T.NEW_BOUNTY = '1'||SUBSTR(T.NEW_BOUNTY,2),");
				sb1.append("			T.MODIFIER = :MODIFIER, 				   ");
				sb1.append("			T.LASTUPDATE = SYSDATE 				       ");
				sb1.append("		WHERE T.YEARMON = :YEARMON 					   ");
				qc1.setObject("MODIFIER", inputVO.getUserID());
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
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
	 * 主管參數2調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callBsAum(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS716.SP_TBPMS_CNR_PD_CTS_GOAL(? ,? ) ");
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
			}else{
				//更新主表，將BS_AUM第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
				sb1.append("		SET T.BS_AUM = '1'||SUBSTR(T.BS_AUM,2),  	");
				sb1.append("			T.MODIFIER = :MODIFIER, 				");
				sb1.append("			T.LASTUPDATE = SYSDATE 				    ");
				sb1.append("		WHERE T.YEARMON = :YEARMON 					");
				qc1.setObject("MODIFIER", inputVO.getUserID());
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
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
	 * 主管參數4調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callBsPrize(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS716.SP_TBPMS_CNR_PD_BS_PRIZE(? ,? ) ");
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
			}else{
				//更新主表，將BS_PRIZE第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
				sb1.append("		SET T.BS_PRIZE = '1'||SUBSTR(T.BS_PRIZE,2), ");
				sb1.append("			T.MODIFIER = :MODIFIER, 				");
				sb1.append("			T.LASTUPDATE = SYSDATE 				    ");
				sb1.append("		WHERE T.YEARMON = :YEARMON 					");
				qc1.setObject("MODIFIER", inputVO.getUserID());
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
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
	 * 共用參數3調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callSpMar(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS716.SP_TBPMS_CNR_PD_SP_MAR(? ,? ) ");
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
			}else{
				//更新主表，將SP_MAR第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
				sb1.append("		SET T.SP_MAR = '1'||SUBSTR(T.SP_MAR,2),  	");
				sb1.append("			T.MODIFIER = :MODIFIER, 				");
				sb1.append("			T.LASTUPDATE = SYSDATE 				    ");
				sb1.append("		WHERE T.YEARMON = :YEARMON 					");
				qc1.setObject("MODIFIER", inputVO.getUserID());
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
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
	 * 共用參數6調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callConNum(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS716.SP_TBPMS_CNR_CON_NUM(? ,? ) ");
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
			}else{
				//更新主表，將SP_MAR第一位（狀態）設置為1
				StringBuffer sb1 = new StringBuffer();
				QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb1.append("		UPDATE TBPMS_CNR_MAST T 					");
				sb1.append("		SET T.CON_NUM = '1'||SUBSTR(T.CON_NUM,2),  	");
				sb1.append("			T.MODIFIER = :MODIFIER, 				");
				sb1.append("			T.LASTUPDATE = SYSDATE 				    ");
				sb1.append("		WHERE T.YEARMON = :YEARMON 					");
				qc1.setObject("MODIFIER", inputVO.getUserID());
				qc1.setObject("YEARMON", inputVO.getYearMon());
				qc1.setQueryString(sb1.toString());
				dam.exeUpdate(qc1);
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
	 * 遞延至次月
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callCNRMast(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS210InputVO inputVO = (PMS210InputVO) body;
			PMS210OutputVO outputVO = new PMS210OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS736.SP_EXEC_CNR_MAST(?,?) ");
			qc.setString(1, inputVO.getYearMon());
			qc.registerOutParameter(2, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(2);		
			if (str == null || "".equals(str)){
				outputVO.setBackResult(1);
			}else{
				outputVO.setBackResult(0);
			}
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
}

package com.systex.jbranch.app.server.fps.pms210;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : pms210InputVO.java<br>
 * Author :KXJ<br>
 * Date :2016年11月14日 <br>
 * Version : 1.01 <br>
 * Editor : KXJ<br>
 * Editor Date : 2016年11月14日<br>
 */
public class PMS210InputVO extends PagingInputVO
{
	//公用
	private String yearMon;
	private String userID;
	private String fileName;
	
	//理專參數
	private String SP_ASS;
	private String SP_BEN;
	private String SP_LEVD;
	private String BS_GOAL;
	private String SP_BOUNS;
	private String NEW_CUST_RATE;
	private String NEW_SP_BEN;
	private String NEW_BS_GOAL;
	private String SP_SAL_BEN;
	private int flag;                     //理專參數9調用存儲過程標誌位
	private String NEW_BOUNTY;
	private String FCH_BOUNTY_RATE;
	private String GOODS_BOUNTY_RATE;
	
	//主管參數
	private String BS_ASS;
	private String BS_AUM;
	private String BS_POINT;
	private String BS_PRIZE;
	private String BS_GOAL_TAR;
	private String BS_BONUS_INSE;
	
	//共用參數
	private String BONUS_DATE;
	private String SHORT_TRAN;
	private String SP_MAR;
	private String SP_UN_LACK_IND;
	private String INS_DATE;
	private String CON_NUM;
	private String DIS_RATE;
	private String DEPOSIT_RATE;
	//共用參數2
	private String TSDate;
	private String TEDate;
	private String NSDate;
	private String NEDate;
	private String tradeS;
	private String tradeE;
	
	//理專參數INSERT時
	private List<Map<String, Object>> spAssList;
	private List<Map<String, String>> spBenList;
	private List<Map<String, String>> spLevdList;
	private List<Map<String, String>> bsGoalList;
	private List<Map<String, String>> newCustRateList;
	private List<Map<String, String>> newSpBenList;
	private List<Map<String, String>> newBsGoalList;

	//主管參數INSERT時
	private List<Map<String, Object>> bsAssList;
	private List<Map<String, Object>> bsPointList;
	private List<Map<String, String>> bsGoalTarList;
	private List<Map<String, Object>> bsBonusList;
	
	//共用參數INSERT時
	private List<Map<String, Object>> spUnLackIndList;
	private List<Map<String, Object>> disRateList;
	private List<Map<String, Object>> depositRateList;

	public String getYearMon()
	{
		return yearMon;
	}

	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getSP_ASS()
	{
		return SP_ASS;
	}

	public void setSP_ASS(String sP_ASS)
	{
		SP_ASS = sP_ASS;
	}

	public String getSP_BEN()
	{
		return SP_BEN;
	}

	public void setSP_BEN(String sP_BEN)
	{
		SP_BEN = sP_BEN;
	}

	public String getSP_LEVD()
	{
		return SP_LEVD;
	}

	public void setSP_LEVD(String sP_LEVD)
	{
		SP_LEVD = sP_LEVD;
	}

	public String getBS_GOAL()
	{
		return BS_GOAL;
	}

	public void setBS_GOAL(String bS_GOAL)
	{
		BS_GOAL = bS_GOAL;
	}

	public String getSP_BOUNS()
	{
		return SP_BOUNS;
	}

	public void setSP_BOUNS(String sP_BOUNS)
	{
		SP_BOUNS = sP_BOUNS;
	}

	public String getNEW_CUST_RATE()
	{
		return NEW_CUST_RATE;
	}

	public void setNEW_CUST_RATE(String nEW_CUST_RATE)
	{
		NEW_CUST_RATE = nEW_CUST_RATE;
	}

	public String getNEW_SP_BEN()
	{
		return NEW_SP_BEN;
	}

	public void setNEW_SP_BEN(String nEW_SP_BEN)
	{
		NEW_SP_BEN = nEW_SP_BEN;
	}

	public String getNEW_BS_GOAL()
	{
		return NEW_BS_GOAL;
	}

	public void setNEW_BS_GOAL(String nEW_BS_GOAL)
	{
		NEW_BS_GOAL = nEW_BS_GOAL;
	}

	public String getSP_SAL_BEN()
	{
		return SP_SAL_BEN;
	}

	public void setSP_SAL_BEN(String sP_SAL_BEN)
	{
		SP_SAL_BEN = sP_SAL_BEN;
	}
	
	public int getFlag()
	{
		return flag;
	}

	public void setFlag(int flag)
	{
		this.flag = flag;
	}

	public String getNEW_BOUNTY()
	{
		return NEW_BOUNTY;
	}

	public void setNEW_BOUNTY(String nEW_BOUNTY)
	{
		NEW_BOUNTY = nEW_BOUNTY;
	}
	
	public String getFCH_BOUNTY_RATE()
	{
		return FCH_BOUNTY_RATE;
	}

	public void setFCH_BOUNTY_RATE(String fCH_BOUNTY_RATE)
	{
		FCH_BOUNTY_RATE = fCH_BOUNTY_RATE;
	}

	public String getGOODS_BOUNTY_RATE()
	{
		return GOODS_BOUNTY_RATE;
	}

	public void setGOODS_BOUNTY_RATE(String gOODS_BOUNTY_RATE)
	{
		GOODS_BOUNTY_RATE = gOODS_BOUNTY_RATE;
	}

	public String getBS_ASS()
	{
		return BS_ASS;
	}

	public void setBS_ASS(String bS_ASS)
	{
		BS_ASS = bS_ASS;
	}

	public String getBS_AUM()
	{
		return BS_AUM;
	}

	public void setBS_AUM(String bS_AUM)
	{
		BS_AUM = bS_AUM;
	}

	public String getBS_POINT()
	{
		return BS_POINT;
	}

	public void setBS_POINT(String bS_POINT)
	{
		BS_POINT = bS_POINT;
	}

	public String getBS_PRIZE()
	{
		return BS_PRIZE;
	}

	public void setBS_PRIZE(String bS_PRIZE)
	{
		BS_PRIZE = bS_PRIZE;
	}

	public String getBS_GOAL_TAR()
	{
		return BS_GOAL_TAR;
	}

	public void setBS_GOAL_TAR(String bS_GOAL_TAR)
	{
		BS_GOAL_TAR = bS_GOAL_TAR;
	}

	public String getBS_BONUS_INSE()
	{
		return BS_BONUS_INSE;
	}

	public void setBS_BONUS_INSE(String bS_BONUS_INSE)
	{
		BS_BONUS_INSE = bS_BONUS_INSE;
	}

	public String getBONUS_DATE()
	{
		return BONUS_DATE;
	}

	public void setBONUS_DATE(String bONUS_DATE)
	{
		BONUS_DATE = bONUS_DATE;
	}

	public String getSHORT_TRAN()
	{
		return SHORT_TRAN;
	}

	public void setSHORT_TRAN(String sHORT_TRAN)
	{
		SHORT_TRAN = sHORT_TRAN;
	}

	public String getSP_MAR()
	{
		return SP_MAR;
	}

	public void setSP_MAR(String sP_MAR)
	{
		SP_MAR = sP_MAR;
	}

	public String getSP_UN_LACK_IND()
	{
		return SP_UN_LACK_IND;
	}

	public void setSP_UN_LACK_IND(String sP_UN_LACK_IND)
	{
		SP_UN_LACK_IND = sP_UN_LACK_IND;
	}

	public String getINS_DATE()
	{
		return INS_DATE;
	}

	public void setINS_DATE(String iNS_DATE)
	{
		INS_DATE = iNS_DATE;
	}
	
	public String getCON_NUM()
	{
		return CON_NUM;
	}

	public void setCON_NUM(String cON_NUM)
	{
		CON_NUM = cON_NUM;
	}

	public String getDIS_RATE()
	{
		return DIS_RATE;
	}

	public void setDIS_RATE(String dIS_RATE)
	{
		DIS_RATE = dIS_RATE;
	}

	public String getDEPOSIT_RATE()
	{
		return DEPOSIT_RATE;
	}

	public void setDEPOSIT_RATE(String dEPOSIT_RATE)
	{
		DEPOSIT_RATE = dEPOSIT_RATE;
	}
	
	public String getTSDate()
	{
		return TSDate;
	}

	public void setTSDate(String tSDate)
	{
		TSDate = tSDate;
	}

	public String getTEDate()
	{
		return TEDate;
	}

	public void setTEDate(String tEDate)
	{
		TEDate = tEDate;
	}

	public String getNSDate()
	{
		return NSDate;
	}

	public void setNSDate(String nSDate)
	{
		NSDate = nSDate;
	}

	public String getNEDate()
	{
		return NEDate;
	}

	public void setNEDate(String nEDate)
	{
		NEDate = nEDate;
	}

	public String getTradeS()
	{
		return tradeS;
	}

	public void setTradeS(String tradeS)
	{
		this.tradeS = tradeS;
	}

	public String getTradeE()
	{
		return tradeE;
	}

	public void setTradeE(String tradeE)
	{
		this.tradeE = tradeE;
	}

	public List<Map<String, Object>> getSpAssList()
	{
		return spAssList;
	}

	public void setSpAssList(List<Map<String, Object>> spAssList)
	{
		this.spAssList = spAssList;
	}

	public List<Map<String, String>> getSpBenList()
	{
		return spBenList;
	}

	public void setSpBenList(List<Map<String, String>> spBenList)
	{
		this.spBenList = spBenList;
	}

	public List<Map<String, String>> getSpLevdList()
	{
		return spLevdList;
	}

	public void setSpLevdList(List<Map<String, String>> spLevdList)
	{
		this.spLevdList = spLevdList;
	}

	public List<Map<String, String>> getBsGoalList()
	{
		return bsGoalList;
	}

	public void setBsGoalList(List<Map<String, String>> bsGoalList)
	{
		this.bsGoalList = bsGoalList;
	}

	public List<Map<String, String>> getNewCustRateList()
	{
		return newCustRateList;
	}

	public void setNewCustRateList(List<Map<String, String>> newCustRateList)
	{
		this.newCustRateList = newCustRateList;
	}

	public List<Map<String, String>> getNewSpBenList()
	{
		return newSpBenList;
	}

	public void setNewSpBenList(List<Map<String, String>> newSpBenList)
	{
		this.newSpBenList = newSpBenList;
	}

	public List<Map<String, String>> getNewBsGoalList()
	{
		return newBsGoalList;
	}

	public void setNewBsGoalList(List<Map<String, String>> newBsGoalList)
	{
		this.newBsGoalList = newBsGoalList;
	}

	public List<Map<String, Object>> getBsAssList()
	{
		return bsAssList;
	}

	public void setBsAssList(List<Map<String, Object>> bsAssList)
	{
		this.bsAssList = bsAssList;
	}

	public List<Map<String, Object>> getBsPointList()
	{
		return bsPointList;
	}

	public void setBsPointList(List<Map<String, Object>> bsPointList)
	{
		this.bsPointList = bsPointList;
	}

	public List<Map<String, String>> getBsGoalTarList()
	{
		return bsGoalTarList;
	}

	public void setBsGoalTarList(List<Map<String, String>> bsGoalTarList)
	{
		this.bsGoalTarList = bsGoalTarList;
	}

	public List<Map<String, Object>> getBsBonusList()
	{
		return bsBonusList;
	}

	public void setBsBonusList(List<Map<String, Object>> bsBonusList)
	{
		this.bsBonusList = bsBonusList;
	}

	public List<Map<String, Object>> getSpUnLackIndList()
	{
		return spUnLackIndList;
	}

	public void setSpUnLackIndList(List<Map<String, Object>> spUnLackIndList)
	{
		this.spUnLackIndList = spUnLackIndList;
	}

	public List<Map<String, Object>> getDisRateList()
	{
		return disRateList;
	}

	public void setDisRateList(List<Map<String, Object>> disRateList)
	{
		this.disRateList = disRateList;
	}

	public List<Map<String, Object>> getDepositRateList()
	{
		return depositRateList;
	}

	public void setDepositRateList(List<Map<String, Object>> depositRateList)
	{
		this.depositRateList = depositRateList;
	}
}
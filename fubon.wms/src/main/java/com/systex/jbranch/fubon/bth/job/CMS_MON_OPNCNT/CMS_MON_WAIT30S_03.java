package com.systex.jbranch.fubon.bth.job.CMS_MON_OPNCNT;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 201310310276-01 請協助每月固定撈取每日及當月份各分行及全行，等待時間每間隔半分鐘的來客數 20131112 SYC  ==> 分行
 */
@Component("CMS_MON_WAIT30S_03")
public class CMS_MON_WAIT30S_03 implements IPrepareStatementInfo, IGenerateCsvInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select a.YEARMONTH, a.UNITID, SUM(a.CNT) CNT, a.WAIT30CNT ")
			.append("from ( ")
			.append("  select SUBSTRING(DATADATE,1,4)+SUBSTRING(DATADATE,6,2) YEARMONTH, ")
			.append("  DATADATE, UNITID, 1 CNT, (WAITTIME/30)+1 WAIT30CNT, WAITTIME ")
			.append("  from DBO.NUMBERDATA ")
			.append("  where SUBSTRING(DATADATE,1,7)= SUBSTRING(convert(char(10),GETDATE()-15,20),1,7) ")
			.append("  and SERVGROUPTYPE in ('001','002','003','004')) a ")
			.append("group by a.YEARMONTH, a.UNITID, a.WAIT30CNT ")
			.append("order by a.YEARMONTH, a.UNITID, a.WAIT30CNT ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return AccessContext.sdf.format(new Date()) + "等待30秒筆數_分行";
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"YEARMONTH", "UNITID", "CNT", "WAIT30CNT"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"YEARMONTH", "UNITID", "CNT", "WAIT30CNT"};
	}

}

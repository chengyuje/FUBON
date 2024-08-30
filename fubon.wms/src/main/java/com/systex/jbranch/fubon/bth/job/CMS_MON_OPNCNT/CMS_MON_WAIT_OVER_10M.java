package com.systex.jbranch.fubon.bth.job.CMS_MON_OPNCNT;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 201312050131-00 每月產出每分行客戶等候時間超過10min  20131224 SYC  
 */
@Component("CMS_MON_WAIT_OVER_10M")
public class CMS_MON_WAIT_OVER_10M implements IPrepareStatementInfo, IGenerateCsvInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select DATADATE, UNITID, number, USERID, GETDATETIME, CALLDATETIME, WAITTIME, ")
			.append("PROCTIME, WAITCNT ")
			.append("from DBO.NUMBERDATA ")
			.append("where SUBSTRING(DATADATE,1,7)=SUBSTRING(convert(char(10),GETDATE()-15,20),1,7) ")
			.append("and WAITTIME>600 ")
			.append("and SERVGROUPTYPE in ('001','002','003','004') ")
			.append("order by DATADATE, UNITID, number ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return AccessContext.sdf.format(new Date()) + "等待超過10分鐘明細";
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"DATADATE", "UNITID", "NUMBER", "USERID", "GETDATETIME", "CALLDATETIME", "WAITTIME", "PROCTIME", "WAITCNT"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"DATADATE", "UNITID", "NUMBER", "USERID", "GETDATETIME", "CALLDATETIME", "WAITTIME", "PROCTIME", "WAITCNT"};
	}

}

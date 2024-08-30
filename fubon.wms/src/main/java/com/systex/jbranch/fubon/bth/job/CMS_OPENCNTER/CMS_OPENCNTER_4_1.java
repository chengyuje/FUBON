package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateFileInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;


@Component("CMS_OPENCNTER_4_1")
public class CMS_OPENCNTER_4_1 implements IPrepareStatementInfo, IGenerateFileInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select a.UNITID, SUBSTR(a.DATADATE,1,4) || SUBSTR(a.DATADATE,6,2) || SUBSTR(a.DATADATE,9,2) DATADATEHR, ")
			.append("a.GETHR, a.CNT_IN, NVL(B.CNT_SRV,0) CNT_SRV, NVL(C.WAITCNT_AVG,0) WAITCNT_AVG, NVL(C.WAITCNT_MAX,0) WAITCNT_MAX, ")
			.append("NVL(D.TELLERCNT_AVG,0) TELLERCNT_AVG, SUBSTR('00' || TRUNC(C.WAITTIME_MAX/3600 ), -2, 2) || ':' || ")
			.append("SUBSTR('00' || TRUNC((C.WAITTIME_MAX - TRUNC(C.WAITTIME_MAX/3600 )*3600 ) /60 ), -2, 2) || ")
			.append("':' || SUBSTR('00' || MOD((C.WAITTIME_MAX - TRUNC(C.WAITTIME_MAX/3600 )*3600 ), 60), -2, 2) WAITTIME_MAX , ")
			.append("ROUND(C.WAITTIME_MAX/60,2) WAITTIME_MAX_MIN, ROUND(C.WAITTIME_AVG/60,2) WAITTIME_AVG ")
			.append("from ( ")
			.append("  select DATADATE, UNITID, GEThr, COUNT(*) CNT_IN ")
			.append("  from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("  where DATADATE = TO_CHAR(sysdate, 'yyyy-mm-dd') ")
			.append("  group by DATADATE, UNITID, GEThr) a ")
			.append("left outer join ( ")
			.append("  select DATADATE, UNITID, CALLHR, COUNT(*) CNT_SRV ")
			.append("  from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("  where PROCSTATUS = '9' ")
			.append("  group by DATADATE, UNITID, CALLHR) B ")
			.append("on (a.DATADATE = B.DATADATE and a.UNITID = B.UNITID and a.GEThr = B.CALLHR) ")
			.append("left outer join ( ")
			.append("  select DATADATE, UNITID, GEThr, ROUND(SUM(WAITCNT)/COUNT(*),2) WAITCNT_AVG, ")
			.append("  max(WAITCNT) WAITCNT_MAX, max(WAITTIME) WAITTIME_MAX, SUM(WAITTIME)/COUNT(WAITTIME) WAITTIME_AVG ")
			.append("  from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("  group by DATADATE,UNITID,GEThr) C ")
			.append("on (a.DATADATE = C.DATADATE and a.UNITID = C.UNITID and a.GEThr = C.GEThr) ")
			.append("left outer join ( ")
			.append("  select DATADATE, UNITID, HOURSESSION||H30 HOURSESSION, ROUND(SUM(TELLERCNT)/COUNT(*),2) TELLERCNT_AVG ")
			.append("  from ( ")
			.append("    select DATADATE, UNITID, HOURSESSION, MINSESSION, H30, max(TELLERCNT) TELLERCNT ")
			.append("    from  TBCMS_PS_SA_MACHCNTSERVST_TMP ")
			.append("    where SERVGROUPTYPE in ('001','002','003','004') ")
			.append("    group by DATADATE, UNITID, HOURSESSION, MINSESSION, H30) ")
			.append("  group by DATADATE, UNITID, HOURSESSION, H30) D ")
			.append("on (a.DATADATE = D.DATADATE and a.UNITID = D.UNITID and a.GEThr = D.HOURSESSION) ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getFileName() {
		return "WMG_FA_DSK_SQI." + AccessContext.sdf.format(new Date());
	}

	@Override
	public String[] getFileColumns() {
//		return new String[]{"單位","日期時段","時段","來客數","服務人數","平均等待人數","最大等待人數","平均開櫃數","最大等待分鐘","平均等待分鐘"};
		return new String[]{"UNITID","DATADATEhr","GEThr","CNT_IN","CNT_SRV","WAITCNT_AVG","WAITCNT_MAX","TELLERCNT_AVG","WAITTIME_MAX_MIN","WAITTIME_AVG_MIN"};
	}

	@Override
	public int[] getFileColWidth() {
		return null;
	}
}

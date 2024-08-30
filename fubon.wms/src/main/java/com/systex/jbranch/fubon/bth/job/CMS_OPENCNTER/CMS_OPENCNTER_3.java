package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 組,分行,日期,時間,來客數,服務人數, 平均等待人數,最大等待人數, 平均開櫃數 20130320
 */
@Component("CMS_OPENCNTER_3")
public class CMS_OPENCNTER_3 implements IPrepareStatementInfo, IGenerateCsvInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select * from ( ")
			.append("  select a.UNITID, SUBSTR(a.DATADATE,6,5) || ' ' || a.GETHR DATADATEHR, a.CNT_IN, ")
			.append("  NVL(B.CNT_SRV,0) CNT_SRV, NVL(C.WAITCNT_AVG,0) WAITCNT_AVG, ")
			.append("  NVL(C.WAITCNT_MAX,0) WAITCNT_MAX, NVL(D.TELLERCNT_AVG,0) TELLERCNT_AVG, ")
			.append("  SUBSTR('00' || TRUNC(C.WAITTIME_MAX/3600 ) ,-2,2)||':'|| SUBSTR('00' || ")
			.append("  TRUNC((C.WAITTIME_MAX - TRUNC(C.WAITTIME_MAX/3600 )*3600 ) /60 ), -2, 2) || ")
			.append("  ':' || SUBSTR('00' || MOD((C.WAITTIME_MAX - TRUNC(C.WAITTIME_MAX/3600 )*3600 ), 60), -2, 2) WAITTIME_MAX ")
			.append("  from ( ")
			.append("    select DATADATE, UNITID, GETHR, COUNT(*) CNT_IN ")
			.append("    from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("    group by DATADATE, UNITID, GETHR) a ")
			.append("  left outer join ( ")
			.append("    select DATADATE, UNITID, CALLHR, COUNT(*) CNT_SRV ")
			.append("    from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("    where PROCSTATUS = '9' ")
			.append("    group by DATADATE, UNITID, CALLHR) B ")
			.append("  on (a.DATADATE = B.DATADATE and a.UNITID = B.UNITID and a.GETHR = B.CALLHR) ")
			.append("  left outer join ( ")
			.append("    select DATADATE, UNITID, GETHR, ROUND(SUM(WAITCNT)/COUNT(*),2) WAITCNT_AVG, ")
			.append("    max(WAITCNT) WAITCNT_MAX, max(WAITTIME) WAITTIME_MAX ")
			.append("    from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("    group by DATADATE,UNITID,GETHR) C ")
			.append("  on (a.DATADATE = C.DATADATE and a.UNITID = C.UNITID and a.GETHR = C.GETHR) ")
			.append("  left outer join ( ")
			.append("    select DATADATE, UNITID, HOURSESSION||H30 HOURSESSION, ")
			.append("    ROUND(SUM(TELLERCNT)/COUNT(*),2) TELLERCNT_AVG ")
			.append("    from ( ")
			.append("      select DATADATE, UNITID, HOURSESSION, MINSESSION, H30, max(TELLERCNT) TELLERCNT ")
			.append("      from TBCMS_PS_SA_MACHCNTSERVST_TMP ")
			.append("      where SERVGROUPTYPE in ('001','002','003','004') ")
			.append("      group by DATADATE, UNITID, HOURSESSION, MINSESSION, H30) ")
			.append("    group by DATADATE, UNITID, HOURSESSION, H30) D ")
			.append("  on (a.DATADATE = D.DATADATE and a.UNITID = D.UNITID and a.GETHR = D.HOURSESSION)) ")
			.append("order by UNITID, DATADATEhr ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return  "來客服務等待開櫃數統計表" + AccessContext.sdf.format(new Date());
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"單位","日期時段","來客數","服務人數","平均等待人數","最大等待人數","平均開櫃數","最大等待時間"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"UNITID","DATADATEhr","CNT_IN","CNT_SRV","WAITCNT_AVG","WAITCNT_MAX","TELLERCNT_AVG","WAITTIME_MAX"};
	}

}

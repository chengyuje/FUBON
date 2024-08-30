package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;

public abstract class CMS_OPENCNTER_4_34_SOURCE {
	protected abstract String getJobNameCol();
	
	protected String getSQL() {
		return new StringBuffer()
			.append("select " + this.getJobNameCol() + ", ")
			.append("substr('000000000000000000' || trim(COUNT(*)), -15) CNT, ")
			.append("substr(trim(max(DATADATE)), -8) Data_date1 ")
			.append("from ( ")
			.append("  select a.UNITID, SUBSTR(a.DATADATE,1,4) || SUBSTR(a.DATADATE,6,2) || SUBSTR(a.DATADATE,9,2) DATADATE, ")
			.append("  a.GETHR, a.CNT_IN, NVL(B.CNT_SRV,0) CNT_SRV, NVL(C.WAITCNT_AVG,0) WAITCNT_AVG, ")
			.append("  NVL(C.WAITCNT_MAX,0) WAITCNT_MAX,  NVL(D.TELLERCNT_AVG,0) TELLERCNT_AVG, ")
			.append("  SUBSTR('00' || TRUNC(C.WAITTIME_MAX   /3600 ), -2, 2) || ':' || SUBSTR('00' || ")
			.append("  TRUNC((C.WAITTIME_MAX - TRUNC(C.WAITTIME_MAX/3600 )*3600 ) /60), -2, 2) || ")
			.append("  ':' || SUBSTR('00' || MOD((C.WAITTIME_MAX - TRUNC(C.WAITTIME_MAX/3600 )*3600 ), 60), -2, 2) WAITTIME_MAX, ")
			.append("  ROUND(C.WAITTIME_MAX/60,2) WAITTIME_MAX_MIN, ROUND(C.WAITTIME_AVG/60,2) WAITTIME_AVG ")
			.append("  from ( ")
			.append("    select DATADATE, UNITID, GETHR, COUNT(*) CNT_IN ")
			.append("    from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("    where DATADATE = TO_CHAR(sysdate, 'yyyy-mm-dd') ")
			.append("    group by DATADATE, UNITID, GETHR) a ")
			.append("  left outer join ( ")
			.append("    select DATADATE, UNITID, CALLHR, COUNT(*) CNT_SRV ")
			.append("    from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("    where PROCSTATUS = '9' ")
			.append("    group by DATADATE, UNITID, CALLHR) B ")
			.append("  on (a.DATADATE = B.DATADATE and a.UNITID = B.UNITID and a.GETHR = B.CALLHR) ")
			.append("  left outer join ( ")
			.append("    select DATADATE, UNITID, GETHR, ROUND(SUM(WAITCNT)/COUNT(*),2) WAITCNT_AVG, ")
			.append("    max(WAITCNT) WAITCNT_MAX, max(WAITTIME) WAITTIME_MAX, ")
			.append("    SUM(WAITTIME)/COUNT(WAITTIME) WAITTIME_AVG ")
			.append("    from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("    group by DATADATE,UNITID,GETHR ) C ")
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
			.toString();
	}
}

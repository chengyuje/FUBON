package com.systex.jbranch.fubon.bth.job.CMS_MON_OPNCNT;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 開櫃數_by分行by時段_峰日
 */
@Component("CMS_MON_OPNCNT_3")
public class CMS_MON_OPNCNT_3 implements IPrepareStatementInfo, IGenerateCsvInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select UNITID, CALLHR, ROUND(SUM(CNT)/6 ,2) AVGCNT ")
			.append("from ( ")
			.append("  select DATADATE, UNITID, CALLHR ,COUNT(*) CNT ")
			.append("  from ( ")
			.append("    select distinct DATADATE, UNITID, CALLHR, USERID ")
			.append("    from TBCMS_PS_SA_NUMBERDATA_TMP ")
			.append("    where UNITID <> '689' and DATADATE in ( ")
			.append("      select DATADATE ")
			.append("      from ( ")
			.append("        select DATADATE, CNT ")
			.append("        from TBCMS_PS_CMS_BUSYDAY ")
			.append("        where CNT > 1000 and YEARMONTH = TO_CHAR(ADD_MONTHS(sysdate,-1), 'YYYYMM') ")
			.append("        order by CNT desc ) ")
			.append("      where rownum<=6)) ")
			.append("  group by DATADATE, UNITID, CALLHR) ")
			.append("group by  UNITID, CALLHR ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return AccessContext.sdf.format(new Date()) + "開櫃數_by分行by時段_峰日";
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"UNITID", "CALLHR", "AVGCNT"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"UNITID", "CALLHR", "AVGCNT"};
	}

}

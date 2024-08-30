package com.systex.jbranch.fubon.bth.job.CMS_MON_OPNCNT;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 全行開戶平均等待服務時間
 */
@Component("CMS_OPENACC_3")
public class CMS_OPENACC_3 implements IPrepareStatementInfo, IGenerateCsvInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select A.UNITID, B.AVG_WAIT PB平均等待時間, B.AVG_PROC PB平均服務時間, C.AVG_WAIT 高櫃平均等待時間, ")
			.append("C.AVG_PROC 高櫃平均服務時間, D.AVG_WAIT OP平均等待時間, D.AVG_PROC OP平均服務時間  ")
			.append("from ( ")
			.append("  select distinct UNITID ")
			.append("  from TBCMS_PS_SA_OPENACC_SOURCE) A ")
			.append("left outer join ( ")
			.append("  select UNITID, TYPENAME, ROUND(SUM(WAITTIME)/COUNT(*),2) AVG_WAIT, ")
			.append("  ROUND(SUM(PROCTIME)/COUNT(*) ,2) AVG_PROC ")
			.append("  from TBCMS_PS_SA_OPENACC_SOURCE ")
			.append("  where TYPENAME = 'PB理專' ")
			.append("  group by UNITID, TYPENAME) B ")
			.append("on (A.UNITID = B.UNITID) ")
			.append("left outer join ( ")
			.append("  select UNITID, TYPENAME, ROUND(SUM(WAITTIME)/COUNT(*),2) AVG_WAIT, ")
			.append("  ROUND(SUM(PROCTIME)/COUNT(*),2) AVG_PROC ")
			.append("  from TBCMS_PS_SA_OPENACC_SOURCE ")
			.append("  where TYPENAME = '一般櫃員' ")
			.append("  group by UNITID, TYPENAME) C ")
			.append("on (A.UNITID = C.UNITID) ")
			.append("left outer join ( ")
			.append("  select UNITID, ROUND(SUM(WAITTIME)/COUNT(*),2) AVG_WAIT, ")
			.append("  ROUND(SUM(PROCTIME)/COUNT(*),2) AVG_PROC ")
			.append("  from TBCMS_PS_SA_OPENACC_SOURCE ")
			.append("  where TYPENAME like '%助理' ")
			.append("  group by UNITID ) D ")
			.append("on (A.UNITID = D.UNITID) ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return AccessContext.sdf.format(new Date()) + "全行開戶平均等待服務時間";
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"UNITID","PB平均等待時間","PB平均服務時間","高櫃平均等待時間","高櫃平均服務時間","OP平均等待時間","OP平均服務時間"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"UNITID","PB平均等待時間","PB平均服務時間","高櫃平均等待時間","高櫃平均服務時間","OP平均等待時間","OP平均服務時間"};
	}

}

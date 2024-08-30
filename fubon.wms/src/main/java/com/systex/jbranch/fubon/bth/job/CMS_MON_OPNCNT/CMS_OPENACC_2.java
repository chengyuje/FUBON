package com.systex.jbranch.fubon.bth.job.CMS_MON_OPNCNT;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 201306100379-00 所有身份等待服務時間合計及次數
 */
@Component("CMS_OPENACC_2")
public class CMS_OPENACC_2 implements IPrepareStatementInfo, IGenerateCsvInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select UNITID, TYPENAME, SUM(WAITTIME) WAIT_TIME, SUM(PROCTIME) PROC_TIME, COUNT(*) CNT ")
			.append("from TBCMS_PS_SA_OPENACC_SOURCE ")
			.append("group by UNITID, TYPENAME ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return AccessContext.sdf.format(new Date()) + "所有身份等待服務時間合計及次數";
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"UNITID","TYPENAME","WAIT_TIME","PROC_TIME","CNT"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"UNITID","TYPENAME","WAIT_TIME","PROC_TIME","CNT"};
	}

}

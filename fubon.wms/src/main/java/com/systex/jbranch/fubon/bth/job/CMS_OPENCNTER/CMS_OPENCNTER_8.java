package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 201409170105-02 每日/每月固定產出每家分行個人金融,就學貸款,之來客數,平均服務時間及平均等待時間報表-產日統計  20141029  syc  
 */
@Component("CMS_OPENCNTER_8")
public class CMS_OPENCNTER_8 implements IPrepareStatementInfo, IGenerateCsvInfo
{

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select a.DATADATE, a.UNITID, a.KIND, SUM(a.CNT) CNT, SUM(a.PROCTIME)/SUM(a.CNT) PROCTIME_AVG, ")
			.append("SUM(a.WAITTIME)/SUM(a.CNT) WAITTIME_AVG ")
			.append("from ( ")
			.append("  select DATADATE, UNITID, 'NUMBER', '個人貸款' KIND, 1 CNT, PROCTIME, WAITTIME ")
			.append("  from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("  where 'NUMBER' >='7500' and  'NUMBER' <='7999' ")
			.append("  union ")
			.append("  select DATADATE, UNITID, 'NUMBER', '就學貸款' KIND, 1 CNT, PROCTIME, WAITTIME ")
			.append("  from TBCMS_PS_SA_NUMBERDATA_TMP1 ")
			.append("  where 'NUMBER' >= '9000' and 'NUMBER' <= '9499') a ")
			.append("group by a.DATADATE, a.UNITID, a.KIND ")
			.append("order by a.DATADATE, a.UNITID, a.KIND")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return "個人金融就貸日統計表" + AccessContext.sdf.format(new Date());
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"日期", "單位", "種類", "筆數", "平均服務時間", "平均等待時間"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"datadate", "UNITID", "KIND", "CNT", "PROCTIME", "WAITTIME"};
	}

}

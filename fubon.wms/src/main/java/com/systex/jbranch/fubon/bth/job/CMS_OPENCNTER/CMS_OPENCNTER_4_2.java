package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 
 */
@Component("CMS_OPENCNTER_4_2")
public class CMS_OPENCNTER_4_2 extends CMS_OPENCNTER_4_1						   
{
	
	@Override
	public String getFileName() {
		return "WMG_FA_DSK_SQI_BAK." + AccessContext.sdf.format(new Date());
	}

	@Override
	public String[] getFileColumns() {
//		return new String[]{"單位","日期時段","時段","來客數","服務人數","平均等待人數","最大等待人數","平均開櫃數","最大等待時間","最大等待分鐘","平均等待分鐘"};
		return new String[]{"UNITID","DATADATEhr","GEThr","CNT_IN","CNT_SRV","WAITCNT_AVG","WAITCNT_MAX","TELLERCNT_AVG","WAITTIME_MAX","WAITTIME_MAX_MIN","WAITTIME_AVG_MIN"};
	}
}

package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateFileInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/*
 * 
 */
@Component("CMS_OPENCNTER_4_3")
public class CMS_OPENCNTER_4_3  extends CMS_OPENCNTER_4_34_SOURCE
								implements IPrepareStatementInfo, IGenerateFileInfo
{
	@Override
	public String getJobNameCol() {
		return "'WMG_FA_DSK_SQI                ' Job_name";
	}
	@Override
	public String getSQL() {
		return super.getSQL();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getFileName() {
		return "ZWMG_FA_DSK_SQI." + AccessContext.sdf.format(new Date());
	}

	@Override
	public String[] getFileColumns() {
		return new String[]{"Job_name","CNT","Data_date1"};
	}

	@Override
	public int[] getFileColWidth() {
		return null;
	}

}

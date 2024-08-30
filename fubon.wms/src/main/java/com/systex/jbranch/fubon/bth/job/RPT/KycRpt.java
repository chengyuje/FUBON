package com.systex.jbranch.fubon.bth.job.RPT;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/**
 * 
 * @author Eli
 * @since V1.1版
 * @date 2018/01/04
 * 
 */
@Component
public class KycRpt implements IPrepareStatementInfo, IGenerateCsvInfo{
	private String[] cols = {
		"ROWSEQ",
		"CUST_ID",
		"CUST_NAME",
		"CUST_RISK_AFR",
		"TEST_DATE"
	};
	
	@Override
	public String getCsvFileName() {
		return "KycRpt" + AccessContext.sdf.format(new Date());
	}

	@Override
	public String[] getCsvHeader() {
		return cols;
	}

	@Override
	public String[] getCsvColumn() {
		return cols;
	}

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select * from ( ")
			.append("  select ROW_NUMBER() over(partition by a.CUST_ID order by a.CUST_ID,a.CREATE_DATE desc) as ROWSEQ, ")
			.append("  a.CUST_ID, ")
			.append("  b.CUST_NAME, ")
			.append("  a.CUST_RISK_AFR, ")
			.append("  TO_CHAR(a.CREATE_DATE,'YYYY/MM/DD HH24:MI:SS') TEST_DATE ")
			.append("  from WMSUSER.TBKYC_INVESTOREXAM_M_HIS a ")
			.append("  left join WMSUSER.TBCRM_CUST_MAST b on a.CUST_ID = b.CUST_ID ")
			.append("  where a.STATUS = '03' ")
			.append("  and TO_CHAR(a.CREATE_DATE,'YYYYMM') = TO_CHAR(ADD_MONTHS(sysdate, -1), 'YYYYMM') ")
			.append("  and length(a.CUST_ID)<10 ")
			.append(") where ROWSEQ = '1' ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

}

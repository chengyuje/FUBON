package com.systex.jbranch.app.server.fps.kyc999;

import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/**
 * 儲存KYC報表資訊
 * @author Eli
 *
 */
@Component
public class KYC999Stored implements IPrepareStatementInfo, IGenerateCsvInfo {

	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select * ")
			.append("from ")
			.append("  (select ROW_NUMBER() over(partition by A.CUST_ID order by A.CUST_ID, A.CREATE_DATE desc) as ROWSEQ, ")
			.append("   A.CUST_ID, ")
			.append("   B.CUST_NAME,")
			.append("   A.CUST_RISK_AFR,")
			.append("   TO_CHAR(A.CREATE_DATE,'YYYY/MM/DD HH24:MI:SS') TEST_DATE ")
			.append("   from TBKYC_INVESTOREXAM_M_HIS A" )
			.append("   left join TBCRM_CUST_MAST B ")
			.append("   on A.CUST_ID = B.CUST_ID ")
			.append("   where A.STATUS = '03' ")
			.append("   and A.CREATE_DATE between :dateStart and :dateEnd ")
			.append("   and length(A.CUST_ID) < 10 ")
			.append(" )")
			.append("where ROWSEQ = '1' ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return null;
	}

	@Override
	public String getCsvFileName() {
		return AccessContext.sdf.format(new Date()) + "_KYC";
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"客戶ID", "客戶姓名", "風險屬性", "KYC承作日"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"CUST_ID", "CUST_NAME", "CUST_RISK_AFR", "TEST_DATE"};
	}
}

package com.systex.jbranch.fubon.bth.job.RPT;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.inf.IGenerateFileInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;
import com.systex.jbranch.fubon.bth.job.inf.Proxy;
import com.systex.jbranch.fubon.bth.job.proxy.FileByFixOracle;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

/**
 * 客戶與ProjectT專員關係檔
 * @author Eli
 * @date 20180322
 *
 */
@Component("FC017")
public class FC017 extends BizLogic implements IPrepareStatementInfo, IGenerateFileInfo {
	/**固定寬TXT*/
	private Proxy proxy = new FileByFixOracle();
	
	/**
	 * 產生報表
	 * @throws Exception
	 */
	public void execute() throws Exception {
		proxy.execute(this);
	}
	
	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select FN_GET_CBS_ID_TYPE(A.CUST_ID) CUST_TYPE, ")
			.append("       A.CUST_ID CUST_NO, ")
			.append("       nvl(C.BRA_NBR,' ') BRH_NBR, ")
			.append("       '  ' DB_APP_NO, ")
			.append("       nvl(B.EMP_CUST_ID,'           ') FINANCIER_ID, ")
			.append("       nvl(VIP_DEGREE,' ') VIP_DEGREE, ")
			.append("       nvl(A.AO_CODE,' ') AO_CODE, ")
			.append("       '    ' FINANCIER_ADR, ")
			.append("       case when A.VIP_DEGREE='V' or A.VIP_DEGREE='A' then 'Y' else ' ' end CHARGE, ")
			.append("       '         ' REVENUE, ")
			.append("       to_char(sysdate,'YYYYMMDD') REGISTER_DATE, ")
			.append("       A.BRA_NBR BRANCH_NBR_DISP, ")
			.append("       nvl(B.EMP_NAME,' ') FINANCIER_NAME ")
			.append("from ( ")
			.append("    select * ")
			.append("    from TBCRM_CUST_MAST ")
			.append("    where AO_CODE <> ' ' ) A ")
			.append("left outer join ( ")
			.append("    select M.*, N.CUST_ID EMP_CUST_ID ")
			.append("    from VWORG_AO_INFO M ")
			.append("    left outer join TBORG_MEMBER N ")
			.append("    on (M.EMP_ID = N.EMP_ID)) B ")
			.append("on (A.AO_CODE = B.AO_CODE) ")
			.append("left outer join ( ")
			.append("    select distinct CUST_ID, BRA_NBR ")
			.append("    from TBCRM_ACCT_MAST) C ")
			.append("on (a.CUST_ID = C.CUST_ID) ")
			.append("where ")
			.append("A.CUST_ID in (select CUST_ID from TBCRM_CUST_AOCODE_CHGLOG where TRUNC(CHG_DATE) = TRUNC(SYSDATE-1)) ")
			.append("or ")
			.append("A.CUST_ID in (select CUST_ID from TBCRM_CUST_VIP_DEGREE_CHGLOG where TRUNC(CHG_DATE) = TRUNC(SYSDATE-1) ")
			.toString();
		
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}
	
	@Override
	public String getFileName() {
		return "FC017QSP";
	}

	@Override
	public String[] getFileColumns() {
		return new String[]{"CUST_TYPE", "CUST_NO", "BRH_NBR", "DB_APP_NO", "FINANCIER_ID", "VIP_DEGREE", 
				"AO_CODE", "FINANCIER_ADR", "CHARGE", "REVENUE", "REGISTER_DATE", "BRANCH_NBR_DISP", "FINANCIER_NAME"};
	}

	@Override
	public int[] getFileColWidth() {
		return new int[]{2, 11, 3, 4, 11, 1, 3, 4, 1, 9, 8, 5, 3};
	}
	
	public static void main(String args[]){
		FC017 getsql = new FC017();
		
		String sql = getsql.getSQL();
		System.out.println(sql);
		
	}

}

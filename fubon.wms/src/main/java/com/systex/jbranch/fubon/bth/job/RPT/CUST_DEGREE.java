package com.systex.jbranch.fubon.bth.job.RPT;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.inf.IGenerateFileInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;
import com.systex.jbranch.fubon.bth.job.inf.Proxy;
import com.systex.jbranch.fubon.bth.job.proxy.FileByDotOracle;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Component("CUST_DEGREE")
public class CUST_DEGREE extends BizLogic implements IPrepareStatementInfo, IGenerateFileInfo {
	/** 固定寬TXT */
	private Proxy proxy = new FileByDotOracle();

	/**
	 * 產生報表
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception {
		proxy.execute(this);
	}

	@Override
	public String getSQL() {
	
		return new StringBuffer()
		.append("SELECT ")
		.append("RPAD(' ',2,' ') || ")// --證件類型  
				.append("RPAD(NVL(M.CUST_ID,' '),11,' ')  || ")// --客戶統一編號
				.append("RPAD(NVL(M.BRA_NBR,' '),5,' ')  || ")// --分行別           
				.append("LPAD('0',2,'0') || ")// --主檔核准序號
				.append("RPAD(NVL(I.CUST_ID,' '),11,' ')  || ")// --理財專員統編
				.append("NVL(NVL(M.VIP_DEGREE,' '),' ') || ")// --客戶等級
				.append("RPAD(NVL(M.AO_CODE,' '),3,' ') || ")// --理專代號
				.append("RPAD(' ',4,' ') || ")// --取消改讀 TFC0
				.append("(CASE WHEN M.VIP_DEGREE='V' OR M.VIP_DEGREE='A' THEN 'Y' ELSE ' ' END) || ")// --優惠註記
				.append("LPAD('0',9,'0') || ")// --年收入 ( 仟元)
				.append("RPAD(NVL(TO_CHAR(M.CREATETIME,'YYYYMMDD'),' '),8,' ') || ")// --登錄日       
				.append("RPAD(NVL(M.BRA_NBR,' '),5,' ') || ")// --歸屬行 
				.append("RPAD(NVL(I.EMP_NAME,' '),30,' ') ")// --理專姓名
				.append("AS OUTPUT_DATA ")
				.append("FROM TBCRM_CUST_MAST M ")
				.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO I ON M.AO_CODE=I.AO_CODE ")
				.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[] {};
	}

	@Override
	public String getFileName() {
		return "CUST_DEGREE.txt";
	}

	@Override
	public String[] getFileColumns() {
		return new String[] { "OUTPUT_DATA" };
	}

	@Override
	public int[] getFileColWidth() {
		return new int[] {};
	}

}

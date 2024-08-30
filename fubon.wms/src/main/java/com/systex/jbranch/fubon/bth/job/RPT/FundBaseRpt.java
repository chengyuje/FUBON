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
public class FundBaseRpt implements IPrepareStatementInfo, IGenerateCsvInfo{
	private String[] cols = {
			"可銷售",	
			"TIERLEVEL",
			"LIPPERID",
			"基金代碼",
			"基金名稱",
			"基金全稱",
			"發行公司名稱",
			"總體市場分類",
			"資產分類",
			"地區分類",
			"幣別",
			"風險屬性",
			"RAM商品等級",
			"ISIN_CODE",
			"承作定期不定額",
			"未核備",
			"基金投資標的",
			"註記",
			"除權息基金",
			"不可電子申購",
			"不可電子轉出",
			"不可電子轉入",
			"不可電子贖回",
			"標的代號",
			"標的名稱",
			"GLOBALLIPPER",
			"配息頻率",
			"適用警語",
			"不可申購原因",
			"不可申購原因"
	};
	
	@Override
	public String getCsvFileName() {
		return "FundBaseRpt" + AccessContext.sdf.format(new Date());
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
			.append("select ")
			.append("case when FUND.IS_SALE='1' then 'Y' WHEN FUND.IS_SALE='2' then 'N' else '' end 可銷售, ")
			.append("'PS_FP_FUNDOTHERINFO.INFO4' TIERLEVEL, ")
			.append("FUND.LIPPER_ID LIPPERID, ")
			.append("FUND.PRD_ID 基金代碼, ")
			.append("FUND.FUND_CNAME 基金名稱, ")
			.append("FUND.FUND_CNAME_A 基金全稱, ")
			.append("'ps_fp_company.CompanyLipperCName' 發行公司名稱, ")
			.append("GL.GLOBAL_LIPPER_CID 總體市場分類, ")
			.append("'ps_fp_assetclass.AssetLipperCID' 資產分類, ")
			.append("'ps_fp_areaclass.AreaLipperCID' 地區分類, ")
			.append("FUND.CURRENCY_STD_ID 幣別, ")
			.append("BONUS.RRNOTES 風險屬性, ")
			.append("FUND.RISKCATE_ID RAM商品等級, ")
			.append("SUBSTR(NFFUMSMP0.FUSM99,1,12) ISIN_CODE, ")
			.append("case when SUBSTR(NFFUMSMP0.FUSM98,1,1)='Y' then SUBSTR(NFFUMSMP0.FUSM99,25,1) else 'N' end 承作定期不定額, ")
			.append("INFO.FUS40 未核備, ")
			.append("SUBSTR(NFFUMSMP0.FUSM99,13,2) 基金投資標的, ")
			.append("FUSFIL.FUS22||FUSFIL.FUS23 註記, ")
			.append("SUBSTR(FUSFIL.FUS98,34,1)  除權息基金, ")
			.append("NFFUMSMP0.FUSM11 不可電子申購, ")
			.append("NFFUMSMP0.FUSM12 不可電子轉出, ")
			.append("NFFUMSMP0.FUSM13 不可電子轉入, ")
			.append("NFFUMSMP0.FUSM14 不可電子贖回, ")
			.append("NFS061.S6101 標的代號, ")
			.append("NFS061.S6104 標的名稱, ")
			.append("GL.GLOBAL_LIPPER_EID GLOBALLIPPER, ")
			.append("SUBSTR(NFFUMSMP0.FUSM98,22,1) 配息頻率, ")
			.append("SUBSTR(NFFUMSMP0.FUSM98,23,1) 適用警語, ")
			.append("SUBSTR(NFFUMSMP0.FUSM98,24,1) 不可申購原因, ")
			.append("LIPFUND.DOMICILECODE c ")
			.append("from TBPRD_FUND FUND ")
			.append("left join TBPRD_GLOBAL_CLASS GL on FUND.GLOBAL_ID=GL.GLOBAL_ID ")
			.append("left join TBPMS_NFFUSMP0 NFFUMSMP0 on FUND.PRD_ID=NFFUMSMP0.FUS01||NFFUMSMP0.FUS02 ")
			.append("left join TBPRD_FUND_BONUSINFO BONUS on FUND.PRD_ID=BONUS.PRD_ID ")
			.append("left join TBPMS_FUSFIL FUSFIL on FUND.PRD_ID=FUSFIL.FUS01||FUSFIL.FUS02 ")
			.append("left join TBPRD_FUNDINFO INFO on FUND.PRD_ID=INFO.PRD_ID ")
			.append("left join WMSUSER.TBPRD_NFS062_SG NFS062 on FUND.PRD_ID=NFS062.S6201 ")
			.append("left join WMSUSER.TBPRD_NFS061_SG NFS061 on NFS061.S6101=NFS062.S6202 ")	
			.append("left join WMSUSER.TBPRD_LIP_FUND_SG LIPFUND on FUND.LIPPER_ID=LIPFUND.LIPPERID ")
			.append("order by FUND.PRD_ID ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

}

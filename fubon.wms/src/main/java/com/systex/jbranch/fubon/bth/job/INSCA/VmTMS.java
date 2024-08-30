package com.systex.jbranch.fubon.bth.job.INSCA;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.inf.IGenerateCsvInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/**
 * 201605270168-00 保險證照系統產生 E學院TMS檔案   VWTMS.csv       SYC  20160527
 * 201701050104-00 提供E學苑證照名單需求異動  SYC 20170113
 * 
 * @author Eli
 * @since V1.1版
 * @date 2017/11/13
 * 
 */
@Component("VmTMS")
public class VmTMS implements IPrepareStatementInfo, IGenerateCsvInfo {
	
	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select  a.ID_NO, ")
			.append("        case when a.CRTF_CODE = '01' then '人身保險業務員資格測驗合格' ")
			.append("        when a.CRTF_CODE = '02' then '投資型保險商品業務員資格測驗合格' ")
			.append("        when a.CRTF_CODE = '03' then '財產保險業務員資格測驗合格' ")
			.append("        when a.CRTF_CODE = '04' then '人身保險業務員銷售外幣收付非投資型保險商品測驗合格' ")
			.append("        else a.CRTF_CODE end as LIC_NAME,  ''  REG_FIRST_DATE , ")
			.append("        case when ISNULL(a.QUALIFIED_YR, '') <> '' ")
			.append("        then SUBSTRING(a.QUALIFIED_YR,1,4)+'/'+SUBSTRING(a.QUALIFIED_YR,5,2)+'/01' ")
			.append("        else ' ' end as QUALIFIED_YR,' ' REGISTER_NUM,")
			.append("        case when ISNULL(a.MUST_CHG_LIC_DATE , '') <> '' ")
			.append("        then convert(varchar, convert(date, a.MUST_CHG_LIC_DATE, 112), 111) ")
			.append("        else '' end as MUST_CHG_LIC_DATE, ' ' LIC_CAN_DATE, ' ' LIC_REJ_DATE, ")
			.append("        ' ' MEMO, '' REGISTER_NUM2, ")
			.append("        case when DATEDIFF(day, GETDATE(), convert(date, a.PUNH_DATE_STR, 112)) <= 0 ")
			.append("        and DATEDIFF(day, GETDATE(), convert(date, a.PUNH_DATE_END, 112)) >= 0 then '停招' ")
			.append("        when a.STATUS in ('01', '04', '05', '06', '07', '08', '11', '12', '13', '14', '15') then '登錄中' ")
			.append("        when a.STATUS in ('09', '17') then '已註銷' end as STATUS, ")
			.append("        case convert(varchar, convert(date, a.REG_SUCC_DATE, 112), 111) when '1900/01/01' then '' ")
			.append("        else convert(varchar, convert(date, a.REG_SUCC_DATE, 112), 111) end as REG_SUCC_DATE, ")
			.append("        ISNULL(convert(varchar, DATEADD(day, - 1, DATEADD(year, 5, convert(date, NULLIF (a.REG_SUCC_DATE, '')))), 111), '') as VALID_REG_SUCC_DATE, ")
			.append("        case convert(varchar, convert(date, a.CHG_LIC_DATE, 112), 111) ")
			.append("        when '1900/01/01' then '' else convert(varchar, convert(date, a.CHG_LIC_DATE, 112), 111) end as CHG_LIC_DATE, ")
			.append("        case when ISNULL(a.CANCEL_DATE, '') <> '' then convert(varchar, convert(date, a.CANCEL_DATE, 112), 111) ")
			.append("        when (ISNULL(a.CANCEL_DATE, '') = '' and ISNULL(C.CANCEL_DATE, '') <> '') ")
			.append("        then convert(varchar, convert(date, C.CANCEL_DATE, 112), 111) else '' end as CANCEL_DATE, ")
			.append("        case when ISNULL(a.CANCEL_DATE , '') <> '' then convert(varchar, convert(date, a.CANCEL_DATE, 112), 111) ")
			.append("        else '' end as CANCEL_DATE2, ' ' LW_TRD_END_DATE ")
			.append("from EXA.TBIAS_LIC_REGI as a ")
			.append("left outer join COM.VWIAS_CODE_LOOKUP as B ")
			.append("on a.CRTF_CODE = B.CODE_ID ")
			.append("left outer join EXA.TBIAS_LIC_REGI as C ")
			.append("on C.EMP_NO = a.EMP_NO and C.ID_NO = a.ID_NO ")
			.append("and a.CRTF_CODE in ('02', '04') and C.CRTF_CODE = '01' ")
			.append("where (B.CODE_TYPE = '049') and (a.STATUS in ('01', '04', '05', '06', '07', '08', '09', '11', '12', '13', '14', '15', '17')) ")
			.append("and (a.CRTF_CODE <> '05') or (B.CODE_TYPE = '049') and (a.CRTF_CODE <> '05') ")
			.append("and (a.PUNH_DATE_STR <> '') and (a.PUNH_DATE_END <> '') ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getCsvFileName() {
		return "VWTMS";
	}

	@Override
	public String[] getCsvHeader() {
		return new String[]{"ID_NO", "LIC_NAME", "REG_FIRST_DATE", "QUALIFIED_YR", "REGISTER_NUM",
				"MUST_CHG_LIC_DATE", "LIC_CAN_DATE", "LIC_REJ_DATE", "MEMO", "REGISTER_NUM2", 
				"STATUS", "REG_SUCC_DATE", "VALID_REG_SUCC_DATE", "CHG_LIC_DATE", "CANCEL_DATE", 
				"CANCEL_DATE2", "LW_TRD_END_DATE"};
	}

	@Override
	public String[] getCsvColumn() {
		return new String[]{"ID_NO", "LIC_NAME", "REG_FIRST_DATE", "QUALIFIED_YR", "REGISTER_NUM",
				"MUST_CHG_LIC_DATE", "LIC_CAN_DATE", "LIC_REJ_DATE", "MEMO", "REGISTER_NUM2", 
				"STATUS", "REG_SUCC_DATE", "VALID_REG_SUCC_DATE", "CHG_LIC_DATE", "CANCEL_DATE", 
				"CANCEL_DATE2", "LW_TRD_END_DATE"};
	}
	
}

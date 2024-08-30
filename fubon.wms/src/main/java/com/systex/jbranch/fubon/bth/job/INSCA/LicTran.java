package com.systex.jbranch.fubon.bth.job.INSCA;

import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.bth.job.inf.IGenerateFileInfo;
import com.systex.jbranch.fubon.bth.job.inf.IPrepareStatementInfo;

/**
 * 保險證照系統產生 新理專理規  證照訓練資料   LicTran.csv       SYC  20160923
 * 
 * @author Eli
 * @since V1.1版
 * @date 2017/10/12
 * 
 */
@Component("LicTran")
public class LicTran implements IPrepareStatementInfo, IGenerateFileInfo{
	
	@Override
	public String getSQL() {
		return new StringBuffer()
			.append("select A.CRTF_CODE, A.ID_NO, A.REG_SUCC_DATE ")
			.append("from (")
			.append("	select CRTF_CODE,ID_NO, REG_SUCC_DATE,STATUS, ")
			.append(" 	case when isnull(PUNH_DATE_STR, '') <> '' then PUNH_DATE_STR else ' ' end as PUNH_DATE_STR, ")
			.append("	case when isnull(PUNH_DATE_END, '') <> '' then PUNH_DATE_END else ' ' end as PUNH_DATE_END ")
			.append(" 	from EXA.TBIAS_LIC_REGI) A ")
			.append("where ")
			.append("	(A.STATUS in ('01', '04', '05', '06', '07', '08', '09', '11', '12', '13', '14', '15', '17')) ")
			.append("	and not ((A.PUNH_DATE_STR <= substring(convert(char(10),getdate()-155,112),1,8)) ")
			.append("	and (A.PUNH_DATE_END >= substring(convert(char(10),getdate()-155,112),1,8))) ")
			
			.append("union ")
			.append("select '06' AS CRTF_CODE, ID_NO, ")
			.append("case when isnull(TRAD_ANNUITY_DATE, '') = '*' then LIC_SEN_DATE else TRAD_ANNUITY_DATE end as TRAD_ANNUITY_DATE ")
			.append("from EXA.TBIAS_TRAN_MSTR ")
			.append("where isnull(TRAD_ANNUITY_DATE, '') <> '' ")
			
			.append("union ")
			.append("select '07' AS CRTF_CODE, ID_NO, ")
			.append("case when isnull(RATIO_ANNUITY_DATE, '') = '*' then LIC_SEN_DATE else RATIO_ANNUITY_DATE end as RATIO_ANNUITY_DATE ")
			.append("from EXA.TBIAS_TRAN_MSTR ")
			.append("where isnull(RATIO_ANNUITY_DATE, '') <> '' ")
			
			.append("union ")
			.append("select '08' CRTF_CODE, ID_NO, STRUCT_PROD_DATE ")
			.append("from EXA.TBIAS_TRAN_MSTR ")
			.append("where STRUCT_PROD_DATE <> ' ' ")
			.append("order by CRTF_CODE, ID_NO ")
			.toString();
	}

	@Override
	public Object[] getParams() {
		return new Object[]{};
	}

	@Override
	public String getFileName() {
		return "LicTran.txt";
	}

	@Override
	public String[] getFileColumns() {
		return new String[]{"CRTF_CODE", "ID_NO", "REG_SUCC_DATE"};
	}

	@Override
	public int[] getFileColWidth() {
		return null;
	}
}

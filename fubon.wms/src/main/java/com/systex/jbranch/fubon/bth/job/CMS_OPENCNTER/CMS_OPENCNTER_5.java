package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;
/*
 * 201409170105-02 每日/每月固定產出每家分行個人金融,就學貸款,之來客數,平均服務時間及平均等待時間報表 - 產出lowdata   20141029  syc  
 * */
public class CMS_OPENCNTER_5 {

	private final String PS_SA_NUMBERDATA_TMP1 = 
			new StringBuffer()
			.append("select z.*,")
			//trans
			.append("case when z.GETHR1 <='09:0' then '0900' ")
			.append("when z.GETHR1 >='15:3' then '1530' ")
			.append("else SUBSTRING(z.GETHR1,1,2) + ':' + z.GH30 end GETHR, ")
			
			.append("case when z.CALLHR1 <='09:0' then '0900' ")
			.append("when z.CALLHR1 >='15:3' then '1530' ")
			.append("else substring(z.CALLHR1,1,2) + ':' + z.CH30 end CALLHR ")
			.append("from ( ")
			//trans end
			.append("	select top 100 DATADATE, UNITID, USERID, number, PROCSTATUS, SUBSTRING(GETDATETIME,12,4) GETHR1, ")
			.append("	SUBSTRING(CALLDATETIME,12,4) CALLHR1, 1 GETCNT, 1 CALLCNT, WAITCNT, SERVGROUPTYPE, WAITTIME, ")
			.append("	case when SUBSTRING(GETDATETIME,15,1)='0' ")
			.append("	or SUBSTRING(GETDATETIME,15,1)='1' ")
			.append("	or SUBSTRING(GETDATETIME,15,1)='2' ")
			.append("	then '00' else '30' end  GH30, ")
			.append("	case when SUBSTRING(CALLDATETIME,15,1)='0' ")
			.append("	or SUBSTRING(CALLDATETIME,15,1)='1' ")
			.append("	or SUBSTRING(CALLDATETIME,15,1)='2' ")
			.append("	then '00' else '30' end CH30, PROCTIME ")
			.append("	from DBO.NUMBERDATA ")
			.append("	where SUBSTRING(DATADATE,1,7)=SUBSTRING(convert(char(10),GETDATE(),20),1,7) ")
			.append("	order by DATADATE, UNITID, number ")
			.append(") z ")
			.toString();

	
	private String getPS_SA_NUMBERDATA_TMP1() {
		return PS_SA_NUMBERDATA_TMP1;
	}

	//暫存表
	public String withTmpTable() {
		return new StringBuffer()
		.append("with PS_SA_NUMBERDATA_TMP1 as ( " + getPS_SA_NUMBERDATA_TMP1() + " ) ")
		.toString();
	}
	
}

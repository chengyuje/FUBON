package com.systex.jbranch.fubon.bth.job.CMS_OPENCNTER;

public class CmsOpncnterStoredSQL {
	/*
	 * CMS_OPENCNTER_1
	 */
	public static final String introduceSourceToPsSaNumberdataTmp1SQL
	= new StringBuffer()
		.append("select TOP 100 PERCENT	z.DATADATE OKEY, z.*,")
		//trans
		.append("case when z.GETHR1 <='09:0' then '0900' ")
		.append("when z.GETHR1 >='15:3' then '1530' ")
		.append("else SUBSTRING(z.GETHR1,1,2) + z.GH30 end GETHR, ")
		
		.append("case when z.CALLHR1 <='09:0' then '0900' ")
		.append("when z.CALLHR1 >='15:3' then '1530' ")
		.append("else substring(z.CALLHR1,1,2) + z.CH30 end CALLHR ")
		.append("from ( ")
		//trans end
		.append("	select TOP 100 PERCENT DATADATE, UNITID, USERID, number, PROCSTATUS, SUBSTRING(GETDATETIME,12,4) GETHR1, ")
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
		.append("	and SERVGROUPTYPE in ('001','002','003','004') ")
		.append("	order by DATADATE, UNITID, number ")
		.append(") z ")
		.toString();
	
	public static final String insertPsSaNumberdataTmp1SQL
	= new StringBuffer()
		.append("insert into TBCMS_PS_SA_NUMBERDATA_TMP1 ")
		//col
		.append("(DATADATE,UNITID,USERID,NUMBR,PROCSTATUS,GETHR,CALLHR,GETCNT,CALLCNT,")
		.append(" GETHR1,CALLHR1,WAITCNT,SERVGROUPTYPE,WAITTIME,GH30,CH30,PROCTIME) ")
		//param
		.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ")
		.toString();
	
	public static final String[] insertPsSaNumberdataTmp1Param
	= {"DATADATE","UNITID","USERID","number","PROCSTATUS","GETHR","CALLHR","GETCNT","CALLCNT",
		"GETHR1","CALLHR1","WAITCNT","SERVGROUPTYPE","WAITTIME","GH30","CH30","PROCTIME"};
	
	public static final String truncatePsSaNumberdataTmp1SQL
	= "truncate table TBCMS_PS_SA_NUMBERDATA_TMP1 ";
	
	/*
	 * CMS_OPENCNTER_2
	 */
	public static final String introduceSourcePsSaMachcntservstTmpSQL
	= new StringBuffer()
		.append("select TOP 100 PERCENT DATADATE OKEY, DATADATE, UNITID, SERVGROUPTYPE, MINSESSION, TELLERCNT, STATUS, ")
		.append("right('00'+ ltrim(rtrim(HOURSESSION)), 2) HOURSESSION, ")
		.append("case when MINSESSION<='2' then '00' else '30' end H30 ")
		.append("from DBO.MACHCNTSERVST ")
		.append("where SUBSTRING(DATADATE,1,7)=SUBSTRING(convert(char(10),GETDATE(),20),1,7) ")
		.append("and SERVGROUPTYPE in ('001','002','003','004') ")
		.append("order by DATADATE, UNITID ")
		.toString();

	public static final String insertPsSaMachcntservstTmpSQL
	= new StringBuffer()
		.append("insert into TBCMS_PS_SA_MACHCNTSERVST_TMP ")
		//col
		.append("(DATADATE,UNITID,SERVGROUPTYPE,HOURSESSION,MINSESSION,TELLERCNT,STATUS,H30) ")
		//param
		.append("values (?, ?, ?, ?, ?, ?, ?, ?) ")
		.toString();
	
	public static final String[] insertPsSaMachcntservstTmpParam
	= {"DATADATE","UNITID","SERVGROUPTYPE","HOURSESSION","MINSESSION","TELLERCNT","STATUS","H30"};
	
	public static final String truncatePsSaMachcntservstTmpSQL
	= "truncate table TBCMS_PS_SA_MACHCNTSERVST_TMP "; 
	
	/*
	 * CMS_OPENCNTER_5
	 */
	public static final String introduceSourcePsSaNumberdataTmp2SOL
	= new StringBuffer()
		.append("select TOP 100 PERCENT z.DATADATE OKEY, z.*, ")
		//trans
		.append("case when z.GETHR1 <='09:0' then '0900' ")
		.append("when z.GETHR1 >='15:3' then '1530' ")
		.append("else SUBSTRING(z.GETHR1,1,2) + z.GH30 end GETHR, ")
		
		.append("case when z.CALLHR1 <='09:0' then '0900' ")
		.append("when z.CALLHR1 >='15:3' then '1530' ")
		.append("else substring(z.CALLHR1,1,2) + z.CH30 end CALLHR ")
		.append("from ( ")
		//trans end
		.append("	select top 100 PERCENT DATADATE, UNITID, USERID, number, PROCSTATUS, SUBSTRING(GETDATETIME,12,4) GETHR1, ")
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
	
	public static final String insertPsSaNumberdataTmp2SQL
	= new StringBuffer()
		.append("insert into TBCMS_PS_SA_NUMBERDATA_TMP1 ")
		//col
		.append("(DATADATE,UNITID,USERID,NUMBR,PROCSTATUS,GETHR,CALLHR,GETCNT,CALLCNT, ")
		.append(" GETHR1,CALLHR1,WAITCNT,SERVGROUPTYPE,WAITTIME,GH30,CH30,PROCTIME) ")
		//param
		.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ")
		.toString();
	
	public static final String[] insertPsSaNumberdataTmp2Param
	= {"DATADATE","UNITID","USERID","number","PROCSTATUS","GETHR","CALLHR","GETCNT","CALLCNT",
		"GETHR1","CALLHR1","WAITCNT","SERVGROUPTYPE","WAITTIME","GH30","CH30","PROCTIME"};
	
	
}

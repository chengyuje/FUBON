package com.systex.jbranch.fubon.bth.job.CMS_MON_OPNCNT;

public class CmsMonStoredSQL {
	/*
	 * CMS_MON_OPNCNT_00 
	 */
	public static final String introduceSourceToPsCmsBusyDaySQL
	= new StringBuffer()
		.append("select TOP 100 PERCENT DATADATE OKEY, SUBSTRING(DATADATE,1,4) + SUBSTRING(DATADATE,6,2) YEARMONTH, DATADATE, COUNT(*) CNT ")
		.append("from DBO.NUMBERDATA ")
		.append("where UNITID <> '689' ")
		.append("and SUBSTRING(DATADATE,1,7) = SUBSTRING(convert(char(10),GETDATE()-10,20),1,7) ")
		.append("and SERVGROUPTYPE in ('001','002','003','004') ")
		.append("group by DATADATE ")
		.append("order by YEARMONTH, CNT desc ")
		.toString();
	
	public static final String insertPsCmsBusyDaySQL
	= new StringBuffer()
		.append("insert into TBCMS_PS_CMS_BUSYDAY (YEARMONTH,DATADATE,CNT) values (?, ?, ?) ")
		.toString();
	
	public static final String truncatePsCmsBusyDaySQL 
	= "truncate table TBCMS_PS_CMS_BUSYDAY ";
	
	public static final String[] insertPsCmsBusyDayParam
	= {"YEARMONTH", "DATADATE", "CNT"};

	/*
	 * CMS_MON_OPNCNT_01
	 */
	public static final String introduceSourceToPsSaNumberDataTmpSQL
	= new StringBuffer()
		.append("select TOP 100 PERCENT DATADATE OKEY, DATADATE, UNITID, USERID, NUMBER, PROCSTATUS, ")
		//--tran
		.append("case when SUBSTRING(GETDATETIME,12,2)<='08' then '08' when SUBSTRING(GETDATETIME,12,2)>='16' then '16' else SUBSTRING(SUBSTRING(GETDATETIME,12,2),1,2) end GETHR, ")
		.append("case when SUBSTRING(CALLDATETIME,12,2)<='08' then '08' when SUBSTRING(CALLDATETIME,12,2)>='16' then '16' else SUBSTRING(SUBSTRING(CALLDATETIME,12,2),1,2) end CALLHR, ")
		//tran end
		.append("SUBSTRING(GETDATETIME,12,2) GETHR1, SUBSTRING(CALLDATETIME,12,2) CALLHR1, 1 GETCNT, 1 CALLCNT , WAITCNT , SERVGROUPTYPE, WAITTIME , ")
		.append("case when SUBSTRING(GETDATETIME,15,1)='0' or SUBSTRING(GETDATETIME,15,1)='1' or SUBSTRING(GETDATETIME,15,1)='2' then '00' else '30' end GH30, ")
		.append("case when SUBSTRING(CALLDATETIME,15,1)='0' or SUBSTRING(CALLDATETIME,15,1)='1' or SUBSTRING(CALLDATETIME,15,1)='2' then '00' else '30' end CH30 ")
		.append("from DBO.NUMBERDATA ")
		.append("where SUBSTRING(DATADATE,1,7)=SUBSTRING(convert(char(10),GETDATE()-10,20),1,7) ")
		.append("and SERVGROUPTYPE in ('001','002','003','004') ")
		.append("order by DATADATE, UNITID, NUMBER ")
		.toString();
	
	public static final String insertPsSaNumberDataTmpSQL
	= new StringBuffer()
		.append("insert into TBCMS_PS_SA_NUMBERDATA_TMP ")
		//col
		.append("(DATADATE,UNITID,USERID,NUMBR,PROCSTATUS,GETHR,CALLHR,GETCNT, ")
		.append("CALLCNT,GETHR1,CALLHR1,WAITCNT,SERVGROUPTYPE,WAITTIME,GH30,CH30) ")
		//param
		.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ")
		.toString();
	
	public static final String truncatePsSaNumberDataTmpSQL 
	= "truncate table TBCMS_PS_SA_NUMBERDATA_TMP ";
	
	public static final String[] insertPsSaNumberDataTmpParam
	= {"DATADATE","UNITID","USERID","NUMBER","PROCSTATUS","GETHR","CALLHR","GETCNT","CALLCNT","GETHR1","CALLHR1","WAITCNT","SERVGROUPTYPE","WAITTIME","GH30","CH30"};
	
	/*
	 * CMS_OPENACC_1
	 */
	public static final String introduceSourceToPsSaOpenaccSourceSQL
	= new StringBuffer()
		.append("select TOP 100 PERCENT X.DATADATE OKEY, X.DATADATE, X.number NUMBER1, X.UNITID, X.USERID, ")
		.append("isnull(Y.value ,'MASS') TYPENAME, X.WAITTIME, ")
		.append("X.PROCTIME from( ")
		.append("  select a.DATADATE, a.number, a.UNITID, a.USERID, isnull(B.level, 99) level, ")
		.append("  a.WAITTIME, a.PROCTIME from( ")
		.append("    select DATADATE, number, UNITID, USERID, WAITTIME, PROCTIME ")
		.append("    from DBO.NUMBERDATA ")
		.append("    where substring(datadate,1,7) = substring(convert(char(10),getdate()-10,20),1,7) ")
		.append("    and ( PROCSTATUS='9' or PROCSTATUS='7' ) ")
		.append("    and number like '7%' and GNMACHID like 'G%') a ")
		.append("  left outer join  DBO.USERDATA B ")
		.append("  on (a.USERID=B.USERID)) X ")
		.append("  left outer join ( ")
		.append("    select * from MAPPINGDESC ")
		.append("    where BELONGNAME='UserData' and session='Level') Y ")
		.append("  on (X.level = Y.IDENT) ")
		.append("order by DATADATE, UNITID, number ")
		.toString();
	
	public static final String insertPsSaOpenaccSourceSQL
	= new StringBuffer()
		.append("insert into TBCMS_PS_SA_OPENACC_SOURCE ")
		//col
		.append("(DATADATE,UNITID,USERID,NUMBER1,TYPENAME,WAITTIME,PROCTIME) ")
		//param
		.append("values (?, ?, ?, ?, ?, ?, ?) ")
		.toString();
	
	public static final String truncatePsSaOpenaccSourceSQL
	= "truncate table TBCMS_PS_SA_OPENACC_SOURCE ";
	
	public static final String[] insertPsSaOpenaccSourceParam
	= {"DATADATE","UNITID","USERID","NUMBER1","TYPENAME","WAITTIME","PROCTIME"};

}

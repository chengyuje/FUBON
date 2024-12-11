--**************************************************************************
--PROJECT_CODE> TBPIM
--DATE>2 024/12/10
--AUTHOR> Jeff Cheng
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_PIMDATA
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBCRM_PIMDATA
fields terminated by ',' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
(
	OU_ID_NO "TRIM(:OU_ID_NO)",
	OU_ACCT_NO,
	OU_STATUS,
	CREATETIME    SYSDATE,
	CREATOR       constant "TBCRM_PIMDATA.ctl",
	LASTUPDATE    SYSDATE,
	MODIFIER      constant "TBCRM_PIMDATA.ctl" 		
)
--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2022/06/14
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>CUMI_FOR_FS
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table CUMI_FOR_FS
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' -- OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID			"TRIM(:CUST_ID)"
,SICK_TYPE				
,DEGRADE			
,DEGRADE_DATE				"TO_DATE(TRIM(:DEGRADE_DATE),'YYYYMMDD')"
,VERSION "0"
,CREATETIME SYSDATE
,CREATOR constant "CUMI_FOR_FS"
)       
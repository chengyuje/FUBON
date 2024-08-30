--**************************************************************************
--PROJECT_CODE> TBJSB
--DATE>20240508
--AUTHOR> Jeff Cheng
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBJSB_INS_VALID_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table PEOPSOFT.CUST_DEGREE_TMP
fields terminated by ',' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
	(
		PERSON_ID,
		CHANGEDTTM "TO_DATE(:CHANGEDTTM,'yyyy-mm-dd hh24:mi:ss')",
		PERSON_DEGREE,
		FAMILY_DEGREE,
		REALVALUE,
		FINISHDTTM "TO_DATE(:FINISHDTTM, 'yyyy-mm-dd hh24:mi:ss')",
		OPEN_KIND			
	)
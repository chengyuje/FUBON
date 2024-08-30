--**************************************************************************
--PROJECT_CODE> TBJSB
--DATE>20240522
--AUTHOR> Jeff Cheng
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_CUST_COMPLAINT_ID
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBCRM_CUST_COMPLAINT_ID
fields terminated by ',' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
	(
		ID		
	)
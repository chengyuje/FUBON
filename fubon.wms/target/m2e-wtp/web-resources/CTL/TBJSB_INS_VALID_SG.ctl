--**************************************************************************
--PROJECT_CODE> TBJSB
--DATE>20220711
--AUTHOR> Jeff Cheng
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBJSB_INS_VALID_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBJSB_INS_VALID_SG
fields terminated by ',' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
	(
		POLICY_NBR			
	)
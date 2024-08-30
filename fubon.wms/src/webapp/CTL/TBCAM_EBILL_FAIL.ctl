--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2023/11/15
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_EBILL_FAIL
--TRUNCATE_BEFORE_INSERT>N
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
APPEND
into table	TBCAM_EBILL_FAIL
FIELDS TERMINATED BY ';' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
CUST_ID			    "TRIM(:CUST_ID)"	
,EMAIL				"TRIM(:EMAIL)"	
,ERROR              "TRIM(:EMAIL)"
,DATA_DATE			"to_char(SYSDATE, 'yyyyMMdd')"	                                           
)                                  
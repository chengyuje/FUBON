--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/06/13
--AUTHOR> BIBBY YEH
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_FC010_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_FC010_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
ACT_NO_FB	POSITION(1:20) "RTRIM(:ACT_NO_FB)",
CUR_COD_FB	POSITION(21:23),
REF_NO_FB	POSITION(24:33),
SEQ			POSITION(34:34),	
ACT_NO_TB	POSITION(35:54),
REF_NO_TB	POSITION(55:76),
CUST_NO	 POSITION(77:87)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_FC010_SG"                                              
)                                                                                      
                                                                                       
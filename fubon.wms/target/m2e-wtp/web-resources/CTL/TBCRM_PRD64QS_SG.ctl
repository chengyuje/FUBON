--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/06/13
--AUTHOR> BIBBY YEH
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_PRD64QS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_PRD64QS_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
ACTNO	POSITION(1:14) "RTRIM(:ACTNO)",
DRAC	POSITION(15:21),
LAST_DATE	POSITION(22:28),
CDATM_CNT	POSITION(29:30),	
TRNAMT_CNT	POSITION(31:32),
CAN_FLG	POSITION(33:33),
EBATM_CNT	 POSITION(34:36),
NAME	 POSITION(37:52) "LTRIM(:NAME)",
FILLER	 POSITION(53:62)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_PRD64QS_SG"                                              
)                                                                                      
                                                                                       
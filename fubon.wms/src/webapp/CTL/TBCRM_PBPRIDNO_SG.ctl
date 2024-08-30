--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2017/06/13
--AUTHOR> BIBBY YEH
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_PBPRIDNO_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE       
into table TBCRM_PBPRIDNO_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
PERSON_ID	POSITION(1:11) "RTRIM(:PERSON_ID)",
TX_FLG	POSITION(12:15),
ACTIVE	POSITION(16:37)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_PBPRIDNO_SG"                                              
)                                                                                      
                                                                                       
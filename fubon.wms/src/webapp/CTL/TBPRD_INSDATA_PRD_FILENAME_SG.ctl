--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2017/08/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INSDATA_PRD_FILENAME_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INSDATA_PRD_FILENAME_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID
,CLAUSE_FILENAME
,CLAUSE_VERSION
,CONTENT_FILENAME
,CONTENT_VERSION
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_INSDATA_PRD_FILENAME_SG"                                                
)                                                                                                                                     
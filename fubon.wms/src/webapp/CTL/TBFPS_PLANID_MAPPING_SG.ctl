--**************************************************************************
--PROJECT_CODE> TBFPS
--DATE>2018/05/22
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBFPS_PLANID_MAPPING_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBFPS_PLANID_MAPPING_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
BATCH_SEQ	POSITION(1:11)
,CERTIFICATE_ID POSITION(12:22)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBFPS_PLANID_MAPPING_SG"                                                
)                                                                                      
                                                                                       
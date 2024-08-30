--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/10/21
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_LEADS_INS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCAM_LEADS_INS_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
POLI_YM 
,AREA_CODE 
,BRANCH_NBR		
,AO_CODE
,ANNL_TYPE 
,POLICY_NO
,PERSON_ID
,APPL_NAME
,INS_NAME
,ITEM
,PAID_AMT_C
,EFF_DATE_N 
,POLI_DATE_N
,TRUST_NAME
,DUE_DATE_N 
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCAM_LEADS_INS_SG"                                                
)                                                                                      
                                                                                       
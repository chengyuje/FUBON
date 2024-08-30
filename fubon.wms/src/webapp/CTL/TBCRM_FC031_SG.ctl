--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2016/10/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_FC031_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBCRM_FC031_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
RM_EMPID POSITION(1:6)   
,CUST_NO  POSITION(7:17)
,DATA_STS POSITION(18:19) "NVL(:DATA_STS,'  ')"
,DB_APP_NO POSITION(20:21) 
,DATA_ACT POSITION(22:23)
,END_DATE POSITION(24:31) "TO_DATE(:END_DATE,'YYYYMMDD')"
,CHANG_DATA POSITION(32:37)
,CLASS POSITION(38:39)
,SUB_CLASS POSITION(40:41)
,MTN_BRH POSITION(42:44) 
,EMP_NO POSITION(45:50)
,AUT_NO POSITION(51:56)
,MTN_DATE POSITION(57:64)
,MTN_TIME POSITION(65:70)
,REST_TYP POSITION(71:72)
,REST_NO POSITION(73:83)
,CUST_TYPE POSITION(84:85)
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBCRM_FC031_SG"                                                
)                                                                                      
                                                                                       
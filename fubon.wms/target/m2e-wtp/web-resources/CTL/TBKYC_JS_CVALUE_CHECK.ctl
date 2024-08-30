--**************************************************************************
--PROJECT_CODE> TBKYC
--DATE>2022/08/17
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBKYC_JS_CVALUE_CHECK
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBKYC_JS_CVALUE_CHECK
FIELDS TERMINATED BY '#!!#' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID "TRIM(:CUST_ID)", 
CUST_NAME "TRIM(:CUST_NAME)", 
BIRTH_DT "TO_DATE(:BIRTH_DT,'YYYYMMDD')", 
AGE "TRIM(:AGE)", 
EDUCATION "TRIM(:EDUCATION)", 
INVESTER_FLG "TRIM(:INVESTER_FLG)", 
RISK_TYPE "TRIM(:RISK_TYPE)", 
SIGN_DT "TO_DATE(:SIGN_DT,'YYYYMMDD')", 
END_DT "TO_DATE(:END_DT,'YYYYMMDD')",
VERSION constant 0,  
CREATETIME SYSDATE,  
CREATOR constant "JIHSUN_KYC_C_CHECK1",  
LASTUPDATE SYSDATE,  
MODIFIER constant "JIHSUN_KYC_C_CHECK1" 
)
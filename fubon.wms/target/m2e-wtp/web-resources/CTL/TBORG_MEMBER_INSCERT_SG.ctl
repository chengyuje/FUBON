--**************************************************************************
--PROJECT_CODE> TBORG
--DATE>2016/10/04
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_MEMBER_INSCERT_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBORG_MEMBER_INSCERT_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CRTF_CODE    
,CUST_ID      
,REG_SUCC_DATE "TO_DATE(:REG_SUCC_DATE,'YYYY/MM/DD')" 
,CERTIFICATE_EX_DATE "TO_DATE(:CERTIFICATE_EX_DATE,'YYYY/MM/DD')"
,UNREG_DATE "TO_DATE(:UNREG_DATE,'YYYY/MM/DD')"
,TRAIN_DATE "TO_DATE(:TRAIN_DATE,'YYYY/MM/DD')"
,TRAIN_DATE_BEFORE "TO_DATE(:TRAIN_DATE_BEFORE,'YYYY/MM/DD')"
,LIC
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBORG_MEMBER_INSCERT_SG"
)
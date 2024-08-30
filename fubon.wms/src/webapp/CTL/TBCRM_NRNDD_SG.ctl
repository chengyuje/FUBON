--**************************************************************************
--PROJECT_CODE> TBIOT
--DATE>2016/09/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_NRNDD_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCRM_NRNDD_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
POLICY_NO
,POLICY_SEQ
,ID_DUP "NVL(:ID_DUP,' ')"
,APPL_ID
,APPL_NAME
,ID_NO
,INS_NAME
,PAYABLE_DATE "TO_DATE(:PAYABLE_DATE,'YYYYMMDD')"
,DEDUCT_DATE "TO_DATE(:DEDUCT_DATE,'YYYYMMDD')"
,IMP_INTEREST
,EX_GRATIA_PAY
,ACCU_CUR_PREM
,LOAN_INTEREST 
,DD_BANK_CODE
,CREDIT_CARD_NUM
,DEDUCT_ID
,DEDUCT_NAME
,RECRUIT_ID 
,BRANCH_NBR
,INS_NBR
,DEPT_CODE
,PAY_TYPE
,POLI_YEAR
,TIMES
,PAY_WAY
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBCRM_NRNDD_SG"       
)
       
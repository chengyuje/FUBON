--**************************************************************************
--PROJECT_CODE>TBCRM_PR3ATM_SG
--DATE>2018/11/13
--AUTHOR> Kitty Chiang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_PR3ATM_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBCRM_PR3ATM_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
ACCOUNT POSITION(1:12)   
,DEPARTMENT_CODE POSITION(13:19)    
,SALARY_LASTDATE POSITION(20:26)   
,DISCOUNT_FEE POSITION(27:29)    
,DISCOUNT_DEVICE POSITION(30:32)   
,NOTE POSITION(33:33)   
,DISCOUNT_TRANSFEE POSITION(34:36)
,SALARY_LASTAMOUNT POSITION(37:51)
,NOTE_PASSBOOK POSITION(52:59)
,CUST_ID POSITION(60:70)
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCRM_PR3ATM_SG"
)
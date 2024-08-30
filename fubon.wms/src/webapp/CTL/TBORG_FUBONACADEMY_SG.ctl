--**************************************************************************
--PROJECT_CODE> TBORG
--DATE>2018/09/17
--AUTHOR> Kitty Chiang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_FUBONACADEMY_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBORG_FUBONACADEMY_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
COURSE_CODE   
,COURSE_NAME    
,CLASS_CODE  
,SESSION_NAME    
,EMPLOYEE_NUMBER 
,RESULT
,CREATE_DATE
,PERSON_ID 
,FULL_NAME     
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBORG_FUBONACADEMY_SG"
)
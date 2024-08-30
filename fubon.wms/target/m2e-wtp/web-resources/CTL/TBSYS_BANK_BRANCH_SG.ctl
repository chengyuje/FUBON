--**************************************************************************
--PROJECT_CODE> TBSYS
--DATE>2021/03/17
--AUTHOR> KITTY
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBSYS_BANK_BRANCH_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE
into table TBSYS_BANK_BRANCH_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
BANKID 
,BRANCHID 
,SNAME      
,LNAME   
,VERSION    constant"0"
,CREATETIME sysdate
,CREATOR    constant "TBSYS_BANK_BRANCH_SG"
,MODIFIER   constant "TBSYS_BANK_BRANCH_SG"
,LASTUPDATE sysdate )
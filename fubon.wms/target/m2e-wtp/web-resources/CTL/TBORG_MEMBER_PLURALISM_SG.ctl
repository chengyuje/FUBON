--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2016/05/23
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_MEMBER_PLURALISM_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
--CHARACTERSET AL32UTF8
TRUNCATE
into table TBORG_MEMBER_PLURALISM_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
EFFDT "TO_DATE(:EFFDT,'YYYY/MM/DD AM HH:MI:ss')"
,ACTION
,EMPLID
,EMPL_RCD
,TW_PRER_NUM_BU
,BGSETID
,BGDEPTID
,BGCODE
,BGJOBDESCR
,BUSETID
,BUDEPTID
,BUSINESS_UNIT2
,BUCODE
,BUHRGWCODE
,BUJOBDESCR 
,TW_TITLE_NUM
,TW_TITLE
,LOCATION
,LOCNAME 
,BUORGMANAGER
,BGORGMANAGER
,EXPECTED_END_DATE "TO_DATE(:EXPECTED_END_DATE,'YYYY/MM/DD AM HH:MI:ss')"
,JOINDTE "TO_DATE(:JOINDTE,'YYYY/MM/DD AM HH:MI:ss')"
,TW_TITLE_CODE
,TERDTE "TO_DATE(:TERDTE,'YYYY/MM/DD AM HH:MI:ss')"
,VERSION "0"
,CREATETIME sysdate
)       
--**************************************************************************
--PROJECT_CODE> TBIOT
--DATE>2016/09/01
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_NPOAT_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBCRM_NPOAT_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
POLICY_NO
,POLICY_SEQ
,ID_DUP "NVL(:ID_DUP,' ')" 
,CUST_ID
,NAME 
,BIRTHDATE "TO_DATE(:BIRTHDATE,'YYYYMMDD')"
,SEX
,RES_ZC 
,RES_ADDRESS
,CONTACT_ZC
,CONTACT_ADDRESS
,DAYTIME_PHONE
,NIGHT_PHONE
,CELL_PHONE 
,EMAIL1
,EMAIL2
,BODY_POSITION
,ID_TYPE
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBCRM_NPOAT_SG"       
)
       
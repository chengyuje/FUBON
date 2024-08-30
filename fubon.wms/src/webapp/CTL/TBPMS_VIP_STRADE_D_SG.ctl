--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/08/26
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_VIP_STRADE_D_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPMS_VIP_STRADE_D_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
YEARMONTH
,BRANCH_NBR
,EMP_KIND
,PERSON_ID
,TRUST_TYPE
,FUND_CODE
,FUND_KIND
,TR102 
,PAY_DATE
,PAY_AMT
,B_CHARGE
,TR_ACC
,TRN_BRANCH
,ITD_GRPNO
,ITD_PERSON
,AO_CODE
,CURRENCY
,ORG_PAY_AMT
,B_CHG_RATE
,PROJECT_NO
,ACT_NO
,NEWC_MARK
,S_DATE 
,TRANS_DAYS
,B_DATE 
,S_PRJ_NO
,REC_AMT
,CTX_KIND
,SFUND_MARK
,CUST_KIND
,RNR_MARK
,S_NETAMT
,S_UNIT
,B_NETAMT
,B_UNIT
,B_ORG_CHG
,REWARD
,RW_FLAG
,S_D_DATE
,S_ORG_AMT
,ACT_SRC
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_VIP_STRADE_D_SG"
)
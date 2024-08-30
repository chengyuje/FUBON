--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2021/12/20
--AUTHOR>Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCAM_SFA_LNBMT_LIMIT1_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (SKIP 1)
LOAD DATA
TRUNCATE
INTO TABLE TBCAM_SFA_LNBMT_LIMIT1_SG
FIELDS TERMINATED BY '#' OPTIONALLY ENCLOSED BY '\n'
TRAILING NULLCOLS
(
 REP_COD
,BRH_COD
,DATA_DATE
,REC_TYPE
,PROD_COD
,USG
,DUE_EDATE
,NAME
,CUST_ID
,LN_ACNO
,PB_ACNO	
,CRL_AMT
,ACT_BAL
,TEL_NO
,GL_CLASS_CODE
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCAM_SFA_LNBMT_LIMIT1_SG"
,MODIFIER  	constant "TBCAM_SFA_LNBMT_LIMIT1_SG"
,LASTUPDATE	sysdate
)
--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2021/03/29
--AUTHOR> Ocean Lin
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_PY2011O_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBCRM_PY2011O_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
 BUSINESS_NO	
,REL_DATE	
,IDNO		
,BRANCH		
,ACCT_NO 	
,PAY_CURR 	
,PAY_AMT		
,DEBT_ACCT1	
,CURR_ACCT1	
,COST_AMT1	
,DEBT_ACCT2	
,CURR_ACCT2	
,COST_AMT2	
,VERSION   	constant"0"
,CREATETIME	sysdate
,CREATOR   	constant "TBCRM_PY2011O_SG"
,MODIFIER  	constant "TBCRM_PY2011O_SG"
,LASTUPDATE	sysdate
)
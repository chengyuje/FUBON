--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2017/12/12
--AUTHOR> Willis Cheng
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_NRBRQ11P_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBCRM_NRBRQ11P_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
CUST_ID POSITION(1:11)   
,ACCT_NBR							POSITION(12:26)    
,DEAL_DATE							POSITION(27:34)   
,TYPE								POSITION(35)    
,ORG_AMT							POSITION(36:48)	  "TO_NUMBER(:ORG_AMT/100)"
,ORG_CUR							POSITION(49:51)   
,ORG_PRIFIT_AND_LOSS				POSITION(52)	  
,DELIVERY_AMT						POSITION(53:65)   "TO_NUMBER(:DELIVERY_AMT/100)"
,DELIVERY_CUR						POSITION(66:68)   "NVL(:DELIVERY_CUR,' ')"
,DELIVERY_PRIFIT_AND_LOSS		    POSITION(69)   
,TRUSTS_TYPE						POSITION(70)   
,RATE								POSITION(71:83)   "TO_NUMBER(:RATE/1000000)"
,RATE_DATE							POSITION(84:91)    
,RATE_YN							POSITION(92)   
,DELIVERY_DATE						POSITION(93:100)
,PURCHASING_POWER_YN				POSITION(101:105)  
,DIF_DATE							POSITION(106:113)   
,DIF_TIME							POSITION(114:119)
 
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBCRM_NRBRQ11P_SG"
)
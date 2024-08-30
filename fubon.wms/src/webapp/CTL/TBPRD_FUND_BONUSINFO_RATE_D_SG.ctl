--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2019/08/29
--AUTHOR> Cathy Tang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_FUND_BONUSINFO_RATE_D_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (SKIP=1, DIRECT=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE TBPRD_FUND_BONUSINFO_RATE_D_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
UNI_SERIAL_NO	
,ISIN_CODE		
,LIPPER_CODE		
,BLOOMBERG_CODE	
,FUND_TXN_CODE	
,FUND_C_NAME		
,FUND_E_NAME		
,FEE_RATE_1		
,FEE_RATE_2		
,FEE_RATE_3		
,FEE_RATE_4		
,FEE_RATE_5		
,ROI_1			
,ROI_2			
,ROI_3			
,ROI_4			
,ROI_5			
,ROI_SOURCE
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPRD_FUND_BONUSINFO_RATE_D_SG"
,MODIFIER constant "TBPRD_FUND_BONUSINFO_RATE_D_SG"
,LASTUPDATE sysdate
) 
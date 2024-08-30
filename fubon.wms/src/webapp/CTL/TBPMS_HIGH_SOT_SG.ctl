--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>20240710
--AUTHOR> Jeff Cheng
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_HIGH_SOT_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBPMS_HIGH_SOT_SG
fields terminated by ',' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
(
	TRADE_DATE "TO_DATE(:TRADE_DATE, 'MM/DD/YYYY HH24:MI:SS')",
	CUST_ID,
	BRA_NBR,
	AGE,
	TRADE_SOURCE "LPAD(:TRADE_SOURCE, '2', '0')",
	TX_TYPE,
	PRD_TYPE "LPAD(:PRD_TYPE, '2', '0')", 
	PRD_ID,
	CUR_COD,
	TRADE_TYPE,
	AMOUNT,
	CREATETIME    SYSDATE,
	CREATOR       constant "TBPMS_HIGH_SOT_SG",
	LASTUPDATE    SYSDATE,
	MODIFIER      constant "TBPMS_HIGH_SOT_SG" 		
)
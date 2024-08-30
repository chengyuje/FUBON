--**************************************************************************
--PROJECT_CODE> TBPRD
--DATE>2017/08/10
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPRD_INS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE       
into table TBPRD_INS_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID "TRIM(:PRD_ID)"
,INSPRD_NAME
,INSPRD_ANNUAL
,GUARANTEE_ANNUAL
,PRD_UNIT
,CURR_CD
,IS_LIFELONG
,SALE_SDATE "TO_DATE(:SALE_SDATE,'YYYYMMDD')"
,SALE_EDATE "TO_DATE(:SALE_EDATE,'YYYYMMDD')"
,IS_ANNUITY
,IS_INCREASING
,IS_REPAY
,IS_RATE_CHANGE
,OBU_BUY
,NO_VALUE_YEAR
,MAIN_RIDER
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBPRD_INS_SG"                                                
)                                                                                                                                     
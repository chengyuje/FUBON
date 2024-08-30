--**************************************************************************
--PROJECT_CODE> PEOPSOFT
--DATE>2017/08/24
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>PS_FP_ACTIVITY_2017VIP_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE
into table	PS_FP_ACTIVITY_2017VIP_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
SEC_EMP,		
AREA_EMP,	
BRH_DESC_EMP,
BRA_ID ,  	
AO_CODE ,		
CUST_ID ,		
PROFIT_TOTAL,
PROFIT_LEVEL,
PROFIT_POINT,
CARD "TRIM(:CARD)",
YYYYMM "to_char(sysdate,'YYYYMM')",
VERSION "0",
CREATETIME sysdate,
CREATOR constant "PS_FP_ACTIVITY_2017VIP_SG"
)

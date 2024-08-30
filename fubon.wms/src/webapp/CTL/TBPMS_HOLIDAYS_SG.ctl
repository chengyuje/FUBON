--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2017/04/28
--AUTHOR> BIBBY
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_HOLIDAYS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
CHARACTERSET UTF16
TRUNCATE       
into table TBPMS_HOLIDAYS_SG
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
( 
DATA_DATE SYSDATE,
EMP_ID "TRIM(:EMP_ID)",
EMP_FLAG,
BU_CODE,
BU_EMP_ID,
JNAL_NO,
HOLI_CODE,
HOLI_NAME,
HOLI_REASON,
HOLI_DATE,
WORK_FLOW_STATE,
AGENT_EMP_ID,
--UPDATEDATE "decode(trim(:UPDATEDATE),null,null,TO_DATE(SUBSTR(:UPDATEDATE,1,19),'YYYY-MM-DD-HH24.MI.SS'))",
UPDATEDATE,
HOLI_CNT "decode(trim(:HOLI_CNT),null,null,to_number(:HOLI_CNT))",
HOLI_UNIT,
REMARK1,
REMARK2,
VERSION "0",
CREATETIME sysdate,
CREATOR constant "TBPMS_HOLIDAYS_SG"
)                                                                                      
                                                                                       
--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/11/16
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_FE012QS_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPMS_FE012QS_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
BRH_COD POSITION(1:3)
,TX_DATE POSITION(4:11)
,TX_SRL POSITION(12:19)
,CUST_NO POSITION(20:30)
,DR_CUR POSITION(31:33)
,DR_AMT POSITION(34:48)
,CR_CUR POSITION(49:51)
,CR_AMT POSITION(52:66)
,TX_X_RATE POSITION(67:76)
,CO_X_RATE POSITION(77:86)
,REC_BRH POSITION(87:89)
,CLASS_COD POSITION(90:91)
,SUP_NO POSITION(92:97)
,EMP_NO POSITION(98:103)
,VERSION "0"       
,CREATETIME sysdate       
,CREATOR constant "TBPMS_FE012QS_SG"       
)
       
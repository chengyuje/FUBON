--**************************************************************************
--PROJECT_CODE> TBCRM
--DATE>2020/06/04
--AUTHOR> Cathy Tang
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBCRM_TDBD040_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBCRM_TDBD040_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' -- OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
TX_BRH			POSITION(1:4)		"TRIM(:TX_BRH)"
,TX_DATE		POSITION(5:12)		"TO_DATE(:TX_DATE,'YYYYMMDD')"
,ACNO_SA		POSITION(13:26)		"TRIM(:ACNO_SA)"
,SLIP_NO		POSITION(33:39)		"TRIM(:SLIP_NO)"
,CUST_ID		POSITION(40:50)		"TRIM(:CUST_ID)"
,CUST_NAME	    POSITION(51:110)	"TRIM(:CUST_NAME)"
,CLASS		    POSITION(111:117)	"TRIM(:CLASS)"
,CUR			POSITION(118:120)	"TRIM(:CUR)"
,OPN_DPR_AMT	POSITION(121:140)	"TO_NUMBER(REPLACE(:OPN_DPR_AMT, ',', ''))"
,DPR_PRD_MM	    POSITION(141:143)	"TRIM(:DPR_PRD_MM)"
,DPR_PRD_DD	    POSITION(144:146)	"TRIM(:DPR_PRD_DD)"
,BK_VALUE	    POSITION(147:154)	"TO_DATE(:BK_VALUE,'YYYYMMDD')"
,DUE_DTE		POSITION(155:162)	"TO_DATE(:DUE_DTE,'YYYYMMDD')"
,INT			POSITION(163:182)	"TO_NUMBER(REPLACE(:INT, ',', ''))"
,VERSION "0"
,CREATETIME SYSDATE
,CREATOR constant "TBCRM_TDBD040_SG"
)       
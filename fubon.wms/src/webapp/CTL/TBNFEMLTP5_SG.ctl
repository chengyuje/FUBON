--**************************************************************************
--PROJECT_CODE> TBCAM
--DATE>2022/09/21
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBNFEMLTP5_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBNFEMLTP5_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' -- OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
ET001			POSITION(1:11)     "TRIM(:ET001)"
,ET002			POSITION(12:73)    "TRIM(:ET002)"
,ET003			POSITION(74:113)   "TRIM(:ET003)"
,ET004			POSITION(114:135)  "TRIM(:ET004)"
,ET005          POSITION(136:146)  "TRIM(:ET005)"
,ET006          POSITION(147:154)  "TRIM(:ET006)"
,ET007          POSITION(155:161)  "TRIM(:ET007)"
,ET008          POSITION(162:162)  "TRIM(:ET008)"
,ET009          POSITION(163:163)  "TRIM(:ET009)"
,ET010          POSITION(164:170)  "TRIM(:ET010)"
,ET011          POSITION(171:171)  "TRIM(:ET011)"
,ET012          POSITION(172:175)  "TRIM(:ET012)"
,ET013          POSITION(176:177)  "TRIM(:ET013)"
,VERSION "0"                                                                           
,CREATETIME sysdate                                                                    
,CREATOR constant "TBNFEMLTP5_SG"        
)       
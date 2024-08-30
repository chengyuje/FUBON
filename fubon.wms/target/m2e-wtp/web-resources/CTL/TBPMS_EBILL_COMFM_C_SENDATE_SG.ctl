--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2023/06/07
--AUTHOR> Cathy
--PURPOSE> WMS-CR-20230417-01
--**************************************************************************

--**************************************************************************
--SOURCE> WMS_EBILL_COMFM_C_SENDATE.TXT
--TARGET> TBPMS_EBILL_COMFM_C_SENDATE_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBPMS_EBILL_COMFM_C_SENDATE_SG
--FIELDS TERMINATED BY WHITESPACE
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(   CUST_ID       	 POSITION(1:11)         --Length:11	客戶ID
  , BRANCH_NBR		 POSITION(12:16)		--Length:5	客戶對帳單寄送分行代碼
  , SEND_DATE        POSITION(17:35)    	--Length:19	函證寄送日期 YYYY/MM/DD HH:MM:SS
  , RECV_DATE        POSITION(36:54)   		--Length:19	函證回函日期 YYYY/MM/DD HH:MM:SS
  , RTN_STATUS_AST   POSITION(55:55)   		--Length:1	回函結果_帳務 Y:相符 N:不相符
  , RTN_STATUS_NP    POSITION(56:56)   		--Length:1	回函結果_不當情事 Y:有 N:無
  , BASE_DATE        POSITION(57:64)   		--Length:8	對帳單基準日 YYYYMMDD
  , VERSION          "0"
  , CREATOR          CONSTANT "TBPMS_EBILL_COMFM_C_SENDATE_SG"
  , CREATETIME       SYSDATE
  , MODIFIER         CONSTANT "TBPMS_EBILL_COMFM_C_SENDATE_SG"
  , LASTUPDATE       SYSDATE  )
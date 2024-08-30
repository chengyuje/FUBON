--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2023/06/07
--AUTHOR> Cathy
--PURPOSE> WMS-CR-20230417-01
--**************************************************************************

--**************************************************************************
--SOURCE> WMS_MBILL_COMFM_C_SENDATE.TXT
--TARGET> TBPMS_MBILL_COMFM_C_SENDATE_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPMS_MBILL_COMFM_C_SENDATE_SG
--FIELDS TERMINATED BY WHITESPACE
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(   CUST_ID       	 POSITION(1:11)         --Length:11	客戶ID
  , BRANCH_NBR		 POSITION(12:16)		--Length:5	客戶對帳單寄送分行代碼
  , BASE_DATE        POSITION(17:24)   		--Length:8	對帳單基準日 YYYYMMDD
  , SEND_DATE        POSITION(25:32)    	--Length:8	函證寄送日期 YYYYMMDD

  , VERSION          "0"
  , CREATOR          CONSTANT "TBPMS_MBILL_COMFM_C_SENDATE_SG"
  , CREATETIME       SYSDATE
  , MODIFIER         CONSTANT "TBPMS_MBILL_COMFM_C_SENDATE_SG"
  , LASTUPDATE       SYSDATE  )
       
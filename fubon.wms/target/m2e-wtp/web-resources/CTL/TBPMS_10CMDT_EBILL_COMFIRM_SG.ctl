--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2019/12/06
--AUTHOR> Jolene
--PURPOSE> WMS-CR-20191115-01
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_10CMDT_EBILL_COMFIRM_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBPMS_10CMDT_EBILL_COMFIRM_SG
--FIELDS TERMINATED BY WHITESPACE
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(   CUST_ID       	 POSITION(1:11)         --Length:11
  , SEND_DATE        POSITION(12:30)    	--Length:19
  , RECV_DATE        POSITION(31:49)   		--Length:19
  , VERSION          "0"
  , CREATOR          CONSTANT "TBPMS_10CMDT_EBILL_COMFIRM_SG"
  , CREATETIME       SYSDATE
  , MODIFIER         CONSTANT "TBPMS_10CMDT_EBILL_COMFIRM_SG"
  , LASTUPDATE       SYSDATE  )
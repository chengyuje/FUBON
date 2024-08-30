--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2019/11/28
--AUTHOR> Steven
--PURPOSE> 銀行對帳單-客戶資料
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_SDBM184_R_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE, SKIP=1)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE TBPMS_SDBM184_R_SG
WHEN (01) <> '9' --除去最後一筆為9開頭資料

TRAILING NULLCOLS
(   DATA01      POSITION(1:3000)
  , VERSION      "0"
  , CREATOR      CONSTANT "TBPMS_SDBM184_R_SG"
  , CREATETIME   SYSDATE
  , MODIFIER     CONSTANT "TBPMS_SDBM184_R_SG"
  , LASTUPDATE   SYSDATE  )

--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2023/08/08
--AUTHOR> Steven
--PURPOSE> 境外私募基金商品購買人數檔
--**************************************************************************

--**************************************************************************
--TARGET> TBSOT_FUND_OVSPRI_CNT_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE)
LOAD DATA
TRUNCATE
INTO TABLE TBSOT_FUND_OVSPRI_CNT_SG
WHEN (1:4) <> '0000' AND  (1:4) <> '9999'
-- 將欄位都去掉空白
TRAILING NULLCOLS
(   PRD_ID      POSITION(1:4) "trim(:PRD_ID)"
  , CUST_ID     POSITION(5:15) "trim(:CUST_ID)"
  , VERSION      "0"
  , CREATOR      CONSTANT "TBSOT_FUND_OVSPRI_CNT_SG"
  , CREATETIME   SYSDATE
  , MODIFIER     CONSTANT "TBSOT_FUND_OVSPRI_CNT_SG"
  , LASTUPDATE   SYSDATE  )

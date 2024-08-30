--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2022/11/25
--AUTHOR> Eli
--PURPOSE> 富盛案資轉 ─ 富邦臨櫃交易次數
--**************************************************************************

--**************************************************************************
--TARGET> FUBON_TR_COUNT_JSB
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE AUM_JSB
FIELDS TERMINATED BY ','

-- 將欄位都去掉空白
TRAILING NULLCOLS
(
  CUST_ID
  , AMOUNT
)

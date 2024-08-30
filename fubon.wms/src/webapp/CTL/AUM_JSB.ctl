--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2022/04/06
--AUTHOR> Eli
--PURPOSE> 富盛案資轉 ─ 日盛 AUM 資料
--**************************************************************************

--**************************************************************************
--TARGET> AUM_JSB
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE AUM_JSB
FIELDS TERMINATED BY '#!!#' -- 以 # 分隔資料

-- 將欄位都去掉空白
TRAILING NULLCOLS
(
    DATA_DATE "TO_DATE(:DATA_DATE, 'YYYYMMDD')"
  , CUST_ID
  , TYPE
  , AMOUNT
)

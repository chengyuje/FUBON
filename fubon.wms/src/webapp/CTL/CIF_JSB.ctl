--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2022/04/06
--AUTHOR> Eli
--PURPOSE> 富盛案資轉 ─ 日盛客戶檔
--**************************************************************************

--**************************************************************************
--TARGET> CIF_JSB
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE CIF_JSB
FIELDS TERMINATED BY '#!!#' -- 以 # 分隔資料

-- 將欄位都去掉空白
TRAILING NULLCOLS
(
    CUST_ID
  , CUST_NAME
  , EMP
  , BRA_NBR
  , REJM
  , BIRTH_DATE "to_date(:BIRTH_DATE, 'yyyymmdd')"
)

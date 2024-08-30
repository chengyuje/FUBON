--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2022/04/06
--AUTHOR> Eli
--PURPOSE> 富盛案資轉 ─ 日盛員工統編 AO 對應檔
--**************************************************************************

--**************************************************************************
--TARGET> ID_AO_MAP_JSB
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE ID_AO_MAP_JSB
FIELDS TERMINATED BY '#!!#' -- 以 # 分隔資料

-- 將欄位都去掉空白
TRAILING NULLCOLS
(
    AO_ID,
    AO_NAME,
    AO_BNR "substr(:AO_BNR,3,3)" -- 五碼 00XXX，去掉前兩碼
)

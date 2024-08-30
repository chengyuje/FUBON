--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2019/11/28
--AUTHOR> Steven
--PURPOSE> 銀行對帳單-客戶資料
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_10CMDT_EBILL_R_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (DIRECT=TRUE, SKIP=1)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
INTO TABLE TBPMS_10CMDT_EBILL_R_SG
WHEN (01) <> '9' --除去最後一筆為9開頭資料
FIELDS TERMINATED BY '||' -- 以 || 分隔資料

-- 將欄位都去掉空白
TRAILING NULLCOLS
(   CUST_NO "trim(:CUST_NO)"
  , TYPE "trim(:TYPE)"
  , CUST_ID "trim(:CUST_ID)"
  , BRH_COD "trim(:BRH_COD)"
  , ADDR_COD "trim(:ADDR_COD)"
  , RMK "trim(:RMK)"
  , DATA_YM "trim(:DATA_YM)"
  , BILL_BRH "trim(:BILL_BRH)"
  , ZIP_COD "trim(:ZIP_COD)"
  , ADDR_1 "trim(:ADDR_1)"
  , ADDR_2 "trim(:ADDR_2)"
  , NAME "trim(:NAME)"
  , TITLE "trim(:TITLE)"
  , STR_DATE "trim(:STR_DATE)"
  , END_DATE "trim(:END_DATE)"
  , AO_PLACE "trim(:AO_PLACE)"
  , AO_NAME "trim(:AO_NAME)"
  , BRH_TEL "trim(:BRH_TEL)"
  , VIEW_01 "trim(:VIEW_01)"
  , VIEW_A01 "trim(:VIEW_A01)"
  , VIEW_A02 "trim(:VIEW_A02)"
  , VIEW_02 "trim(:VIEW_02)"
  , VIEW_B01 "trim(:VIEW_B01)"
  , VIEW_C01 "trim(:VIEW_C01)"
  , VIEW_D01 "trim(:VIEW_D01)"
  , VIEW_E01 "trim(:VIEW_E01)"
  , VIEW_F01 "trim(:VIEW_F01)"
  , VIEW_G01 "trim(:VIEW_G01)"
  , VIEW_O01 "trim(:VIEW_O01)"
  , VIEW_Q01 "trim(:VIEW_Q01)"
  , VIEW_H "trim(:VIEW_H)"
  , VIEW_I "trim(:VIEW_I)"
  , VIEW_I01 "trim(:VIEW_I01)"
  , VIEW_I02 "trim(:VIEW_I02)"
  , VIEW_03 "trim(:VIEW_03)"
  , VIEW_K01 "trim(:VIEW_K01)"
  , VIEW_L01 "trim(:VIEW_L01)"
  , VIEW_K02 "trim(:VIEW_K02)"
  , VIEW_K03 "trim(:VIEW_K03)"
  , VIEW_K04 "trim(:VIEW_K04)"
  , VIEW_L02 "trim(:VIEW_L02)"
  , VIEW_M "trim(:VIEW_M)"
  , VIEW_N "trim(:VIEW_N)"
  , VIEW_P "trim(:VIEW_P)"
  , VIEW_04 "trim(:VIEW_04)"
  , MSG "trim(:MSG)"
  , AD "trim(:AD)"
  , NOTE "trim(:NOTE)"
  , CLASS "trim(:CLASS)"
  , BLO_BRH "trim(:BLO_BRH)"
  , SEQ_NO "trim(:SEQ_NO)"
  , CUS_CLASS "trim(:CUS_CLASS)"
  , FILLER "trim(:FILLER)"
  , VERSION      "0"
  , CREATOR      CONSTANT "TBPMS_10CMDT_EBILL_R_SG"
  , CREATETIME   SYSDATE
  , MODIFIER     CONSTANT "TBPMS_10CMDT_EBILL_R_SG"
  , LASTUPDATE   SYSDATE  )

--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2019/08/05
--AUTHOR> Steven
--PURPOSE> WMS-CR-20190710-03
--**************************************************************************

--**************************************************************************
--TARGET> TBPMS_NFBH86WK_SG
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table TBPMS_NFBH86WK_SG
--FIELDS TERMINATED BY WHITESPACE
--FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(   TRADE_DATE       POSITION(1:8)     --Length:8
  , PERSON_ID        POSITION(9:19)    --Length:11
  , TRAN_TYPE        POSITION(20:21)   --Length:2
  , ACCT_NO          POSITION(22:36)   --Length:15
  , CERTIFICATE_NUM  POSITION(37:47)   --Length:11
  , FUND_NAME        POSITION(48:69)   --Length:22
  , CURRENCY         POSITION(70:72)   --Length:3
  , TRUST_AMT_L      POSITION(73:85)   "to_number(:TRUST_AMT_L/100)"  --Length:13
  , TRUST_FEE_L      POSITION(86:96)   "to_number(:TRUST_FEE_L/100)"  --Length:11
  , TRUST_AMT_M      POSITION(97:109)  "to_number(:TRUST_AMT_M/100)"  --Length:13
  , TRUST_FEE_M      POSITION(110:120) "to_number(:TRUST_FEE_M/100)"  --Length:11
  , TRUST_AMT_H      POSITION(121:133) "to_number(:TRUST_AMT_H/100)"  --Length:13
  , TRUST_FEE_H      POSITION(134:144) "to_number(:TRUST_FEE_H/100)"  --Length:11
  , DATE_1           POSITION(145:146) --Length:2
  , DATE_2           POSITION(147:148) --Length:2
  , DATE_3           POSITION(149:150) --Length:2
  , DATE_4           POSITION(151:152) --Length:2
  , DATE_5           POSITION(153:154) --Length:2
  , DATE_6           POSITION(155:156) --Length:2
  , QTY              POSITION(157:168) --Length:12
  , FUND_NAME1       POSITION(169:190) --Length:22
  , INS_TYPE         POSITION(191:191) --Length:1
  , PRD_ID           POSITION(192:195) --Length:4
  , PRD_ID_2         POSITION(196:199) --Length:4
  , VERSION          "0"
  , CREATETIME       SYSDATE
  , CREATOR          CONSTANT "TBPMS_NFBH86WK_SG"
  , MODIFIER         CONSTANT "TBPMS_NFBH86WK_SG"
  , LASTUPDATE       SYSDATE  )

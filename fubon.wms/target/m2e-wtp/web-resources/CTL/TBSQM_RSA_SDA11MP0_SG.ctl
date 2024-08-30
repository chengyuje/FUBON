--**************************************************************************
--PROJECT_CODE> TBSQM_RSA
--DATE>2020/06/05
--AUTHOR> Steven Bai
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBSQM_RSA_SDA11MP0_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
INTO TABLE TBSQM_RSA_SDA11MP0_SG
--FIELDS TERMINATED BY X'09'
--FIELDS TERMINATED BY ',' -- OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(   CUST_ID       POSITION(  1: 11) "TRIM(:CUST_ID)"
  , CUST_NAME     POSITION( 12: 73) "TRIM(:CUST_NAME)"
  , SIGN_DOC      POSITION( 74: 74)
  , SEQ_NUMBER    POSITION( 75: 76) "TRIM(:SEQ_NUMBER)"
  , SIGN_DATE     POSITION( 77: 84) "TRIM(:SIGN_DATE)"
  , EFFT_DATE     POSITION( 85: 92) "TRIM(:EFFT_DATE)"
  , TERM_DATE     POSITION( 93:100) "TRIM(:TERM_DATE)"
  , STATUS        POSITION(101:101)
  , APPLY_BRANCH  POSITION(102:104)
  , APPLY_MEMBER  POSITION(105:114)
  , TERM_BRANCH   POSITION(115:117)
  , TERM_MEMBER   POSITION(118:127)
  , FAIL_REASON   POSITION(128:128)
  , PRESERVE_1    POSITION(129:178)
  , PRESERVE_2    POSITION(179:278)
  , VERSION       "0"
  , CREATOR       CONSTANT "TBSQM_SDA11MP0_SG"
  , CREATETIME    SYSDATE  )
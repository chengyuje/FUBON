--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2019/11/28
--AUTHOR> Steven
--PURPOSE> �Ȧ��b��-�Ȥ���
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
WHEN (01) <> '9' --���h�̫�@����9�}�Y���

TRAILING NULLCOLS
(   DATA01      POSITION(1:3000)
  , VERSION      "0"
  , CREATOR      CONSTANT "TBPMS_SDBM184_R_SG"
  , CREATETIME   SYSDATE
  , MODIFIER     CONSTANT "TBPMS_SDBM184_R_SG"
  , LASTUPDATE   SYSDATE  )

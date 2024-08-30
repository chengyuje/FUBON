--**************************************************************************
--PROJECT_CODE> WMS
--DATE> 2022/09/06
--AUTHOR> Cathy
--PURPOSE> WMS-CR-20220722-04_高齡客戶投資商品申購控管
--**************************************************************************

--**************************************************************************
--SOURCE> NFS100.TXT
--TARGET> TBPRD_NFS100
--TRUNCATE_BEFORE_INSERT> Y
--**************************************************************************
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
TRUNCATE
into table	TBPRD_NFS100
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY '|' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
PRD_ID      POSITION(1:4)
,VERSION      "0"
,CREATOR      CONSTANT "TBPRD_NFS100.CTL"
,CREATETIME   SYSDATE
,MODIFIER     CONSTANT "TBPRD_NFS100.CTL"
,LASTUPDATE   SYSDATE
)
       
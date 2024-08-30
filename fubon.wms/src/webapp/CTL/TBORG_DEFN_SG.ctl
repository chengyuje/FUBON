--**************************************************************************
--PROJECT_CODE> TBORG
--DATE>2016/05/24
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBORG_DEFN_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1)
LOAD DATA
TRUNCATE       
into table TBORG_DEFN_SG
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
EFFDT	"TO_DATE(:EFFDT,'YYYY/MM/DD AM HH:MI:ss')"
,CHANGEMARK
,SETID
,DEPTID
,TW_BU_DEPT_PRER_CD
,TW_DEPT_LEVEL
,DEPT_LVLNAME
,DEPT_NAME
,LOCATION
,LOCNAME
,BU
,COMPANY_NAME
,PSMGR_EMPLID
,MGR_EMPLID
,MGR_NAME
,PARENT_NODE_NAME
,UP_BU_DEPT_PRER_CD
,LEVEL_DETERMINER
,UPDEPT_LVLNAME
,UPDEPT_NAME
,PSUPMGR_EMPLID
,UPMGR_EMPLID
,UPMGR_NAME
,BUHRGWCODE
,DEPT_LONGNAME
,TW_PLATE
,TW_AC_PT
,TW_AC_UN
,TW_INTFC
,TW_FR_CATG
)
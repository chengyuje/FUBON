--**************************************************************************
--PROJECT_CODE> TBKYC
--DATE>2022/04/12
--AUTHOR> Sam Tu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>JIHSUN_ORG_AOCODE
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=0)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	JIHSUN_ORG_AOCODE
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY '#!!#' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
C_EMP_ID
,C_EMP_NO
,C_SYS_NO
,C_PRO_TYPE
,C_PRO_NO
,C_AREA_NO
,C_BRANCH_NO
,C_TEAM_NO
,C_EMP_LV
,C_EMP_NAME
,C_EMP_TITLE
,C_EMP_JOB
,C_EMP_JOB_LV
,B_MERIT
,D_IN_DATE "TO_DATE(:D_IN_DATE,'YYYYMMDD')"
,D_OUT_DATE "TO_DATE(:D_OUT_DATE,'YYYYMMDD')"
,D_JOB_DATE "TO_DATE(:D_JOB_DATE,'YYYYMMDD')"
,C_TEL
,C_TEL_EXT
,C_ADDR
,C_AGENT
,C_AGENT_TEL
,C_AGENT_TEL_EXT
,C_eMail
,N_EMP_DISCOUNT
,B_isAS
,C_onSite_NO
,C_AGENT2
,C_AGENT3
,C_Certifcate
,CreateBy 
,CreateDT "TO_DATE(:CreateDT,'YYYYMMDD')"
,LSMTBy
,LSMTDT "TO_DATE(:LSMTDT,'YYYYMMDD')"
,C_HOME_TEL
,C_CELLPHONE
,D_JOB_OUT "TO_DATE(:D_JOB_OUT,'YYYYMMDD')"
,OFFICIAL_DATE "TO_DATE(:OFFICIAL_DATE,'YYYYMMDD')"
,C_EMP_PIC
,C_EMP_RPT_LV
)
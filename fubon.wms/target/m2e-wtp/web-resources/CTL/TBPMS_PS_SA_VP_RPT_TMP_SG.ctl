--**************************************************************************
--PROJECT_CODE> WMS
--DATE>20221208
--AUTHOR> Betty
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_PS_SA_VP_RPT_TMP_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBPMS_PS_SA_VP_RPT_TMP_SG
fields terminated by ',' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
           (	IDNO
				,CF21D
				,CCY
				,BRH_COD
				,TX_DATE "TO_DATE(:TX_DATE,'YYYYMMDD HH24:MI:SS')"
				,DRCR
				,TX_AMT1
				,TX_AMT2
				,EMP_NO
				,TX_BRH
				,SEQ_NO "SUBSTR(:SEQ_NO,2,8)"
				,TX_SRC
				,BRANCH_LONG_NAME
				,MEMO
				,VERSION "0"                                                                           
				,CREATETIME sysdate                                                                    
				,CREATOR constant "TBPMS_PS_SA_VP_RPT_TMP_SG" 
           )
                                                                                       
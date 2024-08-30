--**************************************************************************
--PROJECT_CODE> TBJSB
--DATE>20220621
--AUTHOR> Betty
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBJSB_INS_PROFIT_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBJSB_INS_PROFIT_SG
fields terminated by '|' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
           ( FILENAME,
             POLICY_NO,
             EFF_DATE "CASE WHEN :EFF_DATE = 'NULL' THEN NULL ELSE TO_DATE(:EFF_DATE,'YYYY-MM-DD HH24:MI:SS') END",
             APPL_ID,
             APPL_NAME,
             INSURED_ID,
             INSURED_NAME,
             PROD_NAME,
             CURRENCY,
             ACUM_PAID_ORGD "CASE WHEN :ACUM_PAID_ORGD = 'NULL' THEN NULL ELSE :ACUM_PAID_ORGD END",
             ACUM_DRAW_ORGD "CASE WHEN :ACUM_DRAW_ORGD = 'NULL' THEN NULL ELSE :ACUM_DRAW_ORGD END",
             MANAGEMENT_FEE "CASE WHEN :MANAGEMENT_FEE = 'NULL' THEN NULL ELSE :MANAGEMENT_FEE END",
             ACUM_RISK_PREM "CASE WHEN :ACUM_RISK_PREM = 'NULL' THEN NULL ELSE :ACUM_RISK_PREM END",
             OTHER_FEE "CASE WHEN :OTHER_FEE = 'NULL' THEN NULL ELSE :OTHER_FEE END",
             ACUM_INT_ORGD "CASE WHEN :ACUM_INT_ORGD = 'NULL' THEN NULL ELSE :ACUM_INT_ORGD END",
             TERM_YEAR "CASE WHEN :TERM_YEAR = 'NULL' THEN NULL ELSE :TERM_YEAR END",
             TERM_FEE "CASE WHEN :TERM_FEE = 'NULL' THEN NULL ELSE :TERM_FEE END",
             TERM_DATE "CASE WHEN :TERM_DATE = 'NULL' THEN NULL ELSE TO_DATE(:TERM_DATE,'YYYY-MM-DD HH24:MI:SS') END",
             ACC_VALUE_ORGD "CASE WHEN :ACC_VALUE_ORGD = 'NULL' THEN NULL ELSE :ACC_VALUE_ORGD END",
             ACC_VALUE_DATE "CASE WHEN :ACC_VALUE_DATE = 'NULL' THEN NULL ELSE TO_DATE(:ACC_VALUE_DATE,'YYYY-MM-DD HH24:MI:SS') END",
             POLICYSTATUS,
             TERM_AMT "CASE WHEN :TERM_AMT = 'NULL' THEN NULL ELSE :TERM_AMT END",
             IMP_DATE "TO_TIMESTAMP(:IMP_DATE,'YYYY-MM-DD HH24:MI:SS.FF3')",
             DEATH_BENF_AMT "CASE WHEN :DEATH_BENF_AMT = 'NULL' THEN NULL ELSE :DEATH_BENF_AMT END"
           )
                                                                                       
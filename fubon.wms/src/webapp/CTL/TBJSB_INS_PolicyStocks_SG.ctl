--**************************************************************************
--PROJECT_CODE> TBJSB
--DATE>20220621
--AUTHOR> Betty
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBJSB_INS_PolicyStocks_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBJSB_INS_PolicyStocks_SG
fields terminated by '|' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
           ( POLICY_NO,
             INS_ID,
             POLICY_STATUS,
             APPLY_DATE "CASE WHEN :APPLY_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:APPLY_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
             ACCU_PREM_COMM "CASE WHEN :ACCU_PREM_COMM = 'NULL' THEN NULL ELSE :ACCU_PREM_COMM END",
             ACCU_PREM_POLY "CASE WHEN :ACCU_PREM_POLY = 'NULL' THEN NULL ELSE :ACCU_PREM_POLY END",
             APPL_ID,
             APPL_NAME,
             ITEM,
             PROD_NAME,
             PROD_TYPE,
             JOR,
             SERVANT_ID,
             CHANNEL,
             SA "CASE WHEN :SA = 'NULL' THEN NULL ELSE :SA END",
             SA_UNIT,
             PROJECTID,
             PROJECTNAME,
             MOP,
             PREM_YEAR "CASE WHEN :PREM_YEAR = 'NULL' THEN NULL ELSE :PREM_YEAR END",
             INSURED_ID,
             INSURED_NAME,
             CURRENCY,
             VALUE_PRE_AMT "CASE WHEN :VALUE_PRE_AMT = 'NULL' THEN NULL ELSE :VALUE_PRE_AMT END",
             PREM_POLY_COM "CASE WHEN :PREM_POLY_COM = 'NULL' THEN NULL ELSE :PREM_POLY_COM END",
             PREM_DATE "CASE WHEN :PREM_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:PREM_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
             PAY_WAY,
             PAY_YEARS "CASE WHEN :PAY_YEARS = 'NULL' THEN NULL ELSE :PAY_YEARS END",
             SECRET_YN,
             ACUM_PAID_ORGD "CASE WHEN :ACUM_PAID_ORGD = 'NULL' THEN NULL ELSE :ACUM_PAID_ORGD END",
             ACUM_PAID_TWD "CASE WHEN :ACUM_PAID_TWD = 'NULL' THEN NULL ELSE :ACUM_PAID_TWD END",
             ACUM_DRAW_ORGD "CASE WHEN :ACUM_DRAW_ORGD = 'NULL' THEN NULL ELSE :ACUM_DRAW_ORGD END",
             ACUM_DRAW_TWD "CASE WHEN :ACUM_DRAW_TWD = 'NULL' THEN NULL ELSE :ACUM_DRAW_TWD END",
             ACUM_INT_ORGD "CASE WHEN :ACUM_INT_ORGD = 'NULL' THEN NULL ELSE :ACUM_INT_ORGD END",
             ACUM_INT_TWD "CASE WHEN :ACUM_INT_TWD = 'NULL' THEN NULL ELSE :ACUM_INT_TWD END",
             ACC_VALUE_ORGD "CASE WHEN :ACC_VALUE_ORGD = 'NULL' THEN NULL ELSE :ACC_VALUE_ORGD END",
             EXCHANGE_RATE "CASE WHEN :EXCHANGE_RATE = 'NULL' THEN NULL ELSE :EXCHANGE_RATE END",
             COM_NAME,
             BRANCH_ID,
             BRANCH_NAME,
             LOAN_PRD_YN
           )                                                                                     
                                                                                       
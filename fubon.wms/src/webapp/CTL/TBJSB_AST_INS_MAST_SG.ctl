--**************************************************************************
--PROJECT_CODE> TBJSB
--DATE>20220711
--AUTHOR> Betty
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBJSB_AST_INS_MAST_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true,skip=1) 
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE       
into table TBJSB_AST_INS_MAST_SG
fields terminated by '|' --OPTIONALLY ENCLOSED BY '"'
trailing nullcols
           (	CUST_ID                       "CASE WHEN :ACUM_INS_AMT_ORGD = 'NULL' THEN NULL ELSE RTRIM(:CUST_ID) END",
				UNIT_NBR                      "CASE WHEN :UNIT_NBR = 'NULL' THEN NULL ELSE :UNIT_NBR END",
				COM_ID                        "CASE WHEN :COM_ID = 'NULL' THEN NULL ELSE :COM_ID END",
				COM_NAME                      "CASE WHEN :COM_NAME = 'NULL' THEN NULL ELSE RTRIM(:COM_NAME) END",
				POLICY_NBR                    "CASE WHEN :POLICY_NBR = 'NULL' THEN NULL ELSE RTRIM(:POLICY_NBR) END",
				ID_DUP                        "CASE WHEN :ID_DUP = 'NULL' THEN NULL ELSE :ID_DUP END",
				POLICY_SEQ                    "CASE WHEN :POLICY_SEQ = 'NULL' THEN NULL ELSE :POLICY_SEQ END",
				APPL_NAME                     "CASE WHEN :APPL_NAME = 'NULL' THEN NULL ELSE RTRIM(:APPL_NAME) END",
				INS_TYPE                      "CASE WHEN :INS_TYPE = 'NULL' THEN NULL ELSE RTRIM(:INS_TYPE) END",
				POLICY_SIMP_NAME              "CASE WHEN :POLICY_SIMP_NAME = 'NULL' THEN NULL ELSE RTRIM(:POLICY_SIMP_NAME) END",
				POLICY_FULL_NAME              "CASE WHEN :POLICY_FULL_NAME = 'NULL' THEN NULL ELSE RTRIM(:POLICY_FULL_NAME) END",
				POLICY_ASSURE_AMT_DIV10K      "CASE WHEN :POLICY_ASSURE_AMT_DIV10K = 'NULL' THEN NULL ELSE :POLICY_ASSURE_AMT_DIV10K END",
				POLICY_ASSURE_AMT             "CASE WHEN :POLICY_ASSURE_AMT = 'NULL' THEN NULL ELSE :POLICY_ASSURE_AMT END",
				UNIT                          "CASE WHEN :UNIT = 'NULL' THEN NULL ELSE :UNIT END",
				INS_NAME                      "CASE WHEN :INS_NAME = 'NULL' THEN NULL ELSE RTRIM(:INS_NAME) END",
				INS_ID                        "CASE WHEN :INS_ID = 'NULL' THEN NULL ELSE :INS_ID END",
				PAY_TYPE                      "CASE WHEN :PAY_TYPE = 'NULL' THEN NULL ELSE :PAY_TYPE END",
				POLICY_FEE                    "CASE WHEN :POLICY_FEE = 'NULL' THEN NULL ELSE :POLICY_FEE END",
				POLICY_OVER_FEE               "CASE WHEN :POLICY_OVER_FEE = 'NULL' THEN NULL ELSE :POLICY_OVER_FEE END",
				ACUM_INS_AMT_ORGD             "CASE WHEN :ACUM_INS_AMT_ORGD = 'NULL' THEN NULL ELSE :ACUM_INS_AMT_ORGD END",
				ACUM_PAID_POLICY_FEE          "CASE WHEN :ACUM_PAID_POLICY_FEE = 'NULL' THEN NULL ELSE :ACUM_PAID_POLICY_FEE END",
				ACUM_WDRAW_POLICY_FEE         "CASE WHEN :ACUM_WDRAW_POLICY_FEE = 'NULL' THEN NULL ELSE :ACUM_WDRAW_POLICY_FEE END",
				INV_INS_AMT                   "CASE WHEN :INV_INS_AMT = 'NULL' THEN NULL ELSE :INV_INS_AMT END",
				POLICY_YEAR                   "CASE WHEN :POLICY_YEAR = 'NULL' THEN NULL ELSE :POLICY_YEAR END",
				PAY_TERM_YEAR                 "CASE WHEN :PAY_TERM_YEAR = 'NULL' THEN NULL ELSE :PAY_TERM_YEAR END",
				POLICY_ACTIVE_DATE            "CASE WHEN :POLICY_ACTIVE_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:POLICY_ACTIVE_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
				APPL_DATE                     "CASE WHEN :APPL_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:APPL_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
				SERVICE_EMP_ID                "CASE WHEN :SERVICE_EMP_ID = 'NULL' THEN NULL ELSE :SERVICE_EMP_ID END",
				EMP_NAME                      "CASE WHEN :EMP_NAME = 'NULL' THEN NULL ELSE :EMP_NAME END",
				CONTRACT_STATUS               "CASE WHEN :CONTRACT_STATUS = 'NULL' THEN NULL ELSE :CONTRACT_STATUS END",
				CONTRACT_TEXT                 "CASE WHEN :CONTRACT_TEXT = 'NULL' THEN NULL ELSE :CONTRACT_TEXT END",
				CONTRACT_DATE                 "CASE WHEN :CONTRACT_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:CONTRACT_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
				PAID_TIMES                    "CASE WHEN :PAID_TIMES = 'NULL' THEN NULL ELSE :PAID_TIMES END",
				SYS_YEAR                      "CASE WHEN :SYS_YEAR = 'NULL' THEN NULL ELSE :SYS_YEAR END",
				SYS_MONTH                     "CASE WHEN :SYS_MONTH = 'NULL' THEN NULL ELSE :SYS_MONTH END",
				SYS_DAY                       "CASE WHEN :SYS_DAY = 'NULL' THEN NULL ELSE :SYS_DAY END",
				CAL_VALUE_YEAR                "CASE WHEN :CAL_VALUE_YEAR = 'NULL' THEN NULL ELSE :CAL_VALUE_YEAR END",
				CAL_VALUE_MONTH               "CASE WHEN :CAL_VALUE_MONTH = 'NULL' THEN NULL ELSE :CAL_VALUE_MONTH END",
				CAL_VALUE_DAY                 "CASE WHEN :CAL_VALUE_DAY = 'NULL' THEN NULL ELSE :CAL_VALUE_DAY END",
				ACC_VALUE                     "CASE WHEN :ACC_VALUE = 'NULL' THEN NULL ELSE :ACC_VALUE END",
				LOAN_QUOTA                    "CASE WHEN :LOAN_QUOTA = 'NULL' THEN NULL ELSE :LOAN_QUOTA END",
				LOANED_AMT                    "CASE WHEN :LOANED_AMT = 'NULL' THEN NULL ELSE :LOANED_AMT END",
				LOAN_RATE                     "CASE WHEN :LOAN_RATE = 'NULL' THEN NULL ELSE :LOAN_RATE END",
				ACUM_COST_TWD                 "CASE WHEN :ACUM_COST_TWD = 'NULL' THEN NULL ELSE :ACUM_COST_TWD END",
				EXCH_RATE                     "CASE WHEN :EXCH_RATE = 'NULL' THEN NULL ELSE :EXCH_RATE END",
				TERMI_AMT                     "CASE WHEN :TERMI_AMT = 'NULL' THEN NULL ELSE :TERMI_AMT END",
				SECRET_YN                     "CASE WHEN :SECRET_YN = 'NULL' THEN NULL ELSE :SECRET_YN END",
				DATA_DATE                     "CASE WHEN :DATA_DATE = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:DATA_DATE,'YYYY-MM-DD HH24:MI:SS.FF3') END",
				MAST_SLA_TYPE                 "CASE WHEN :MAST_SLA_TYPE = 'NULL' THEN NULL ELSE :MAST_SLA_TYPE END",
				INV_DATE_START                "CASE WHEN :INV_DATE_START = 'NULL' THEN NULL ELSE :INV_DATE_START END",
				SP_POLICY_NOTE                "CASE WHEN :SP_POLICY_NOTE = 'NULL' THEN NULL ELSE :SP_POLICY_NOTE END",
				COMM_PRJ_NAME                 "CASE WHEN :COMM_PRJ_NAME = 'NULL' THEN NULL ELSE :COMM_PRJ_NAME END",
				TERM_CNT                      "CASE WHEN :TERM_CNT = 'NULL' THEN NULL ELSE :TERM_CNT END",
				CRCY_TYPE                     "CASE WHEN :CRCY_TYPE = 'NULL' THEN NULL ELSE :CRCY_TYPE END",
				AGMT_CRCY_TYPE_FORD           "CASE WHEN :AGMT_CRCY_TYPE_FORD = 'NULL' THEN NULL ELSE :AGMT_CRCY_TYPE_FORD END",
				AGMT_AMT_FORD                 "CASE WHEN :AGMT_AMT_FORD = 'NULL' THEN NULL ELSE :AGMT_AMT_FORD END",
				SEQ                           "CASE WHEN :SEQ = 'NULL' THEN NULL ELSE :SEQ END",
				PRD_ID                        "CASE WHEN :PRD_ID = 'NULL' THEN NULL ELSE RTRIM(:PRD_ID) END",
				PROJECT_CODE                  "CASE WHEN :PROJECT_CODE = 'NULL' THEN NULL ELSE :PROJECT_CODE END",
				REL_TYPE                      "CASE WHEN :REL_TYPE = 'NULL' THEN NULL ELSE :REL_TYPE END",
				ACCU_PREM                     "CASE WHEN :ACCU_PREM = 'NULL' THEN NULL ELSE :ACCU_PREM END",
				INS_COMP_NAME                 "CASE WHEN :INS_COMP_NAME = 'NULL' THEN NULL ELSE RTRIM(:INS_COMP_NAME) END",
				CONSENT_YN                    "CASE WHEN :CONSENT_YN = 'NULL' THEN NULL ELSE :CONSENT_YN END",
				ACUM_INT_AMT                  "CASE WHEN :ACUM_INT_AMT = 'NULL' THEN NULL ELSE :ACUM_INT_AMT END",
				TX_SRC                        "CASE WHEN :TX_SRC = 'NULL' THEN NULL ELSE :TX_SRC END",
				PROPOSER_BIRTH                "CASE WHEN :PROPOSER_BIRTH = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:PROPOSER_BIRTH,'YYYY-MM-DD HH24:MI:SS.FF3') END",
				INSURED_BIRTH                 "CASE WHEN :INSURED_BIRTH = 'NULL' THEN NULL ELSE TO_TIMESTAMP(:INSURED_BIRTH,'YYYY-MM-DD HH24:MI:SS.FF3') END",
				BILL_REMRK                        "CASE WHEN :BILL_REMRK = 'NULL' THEN NULL ELSE :BILL_REMRK END",
				BILL_DATE                        "CASE WHEN :BILL_DATE = 'NULL' THEN NULL ELSE :BILL_DATE END",
				ACCEPTID                        "CASE WHEN :ACCEPTID = 'NULL' THEN NULL ELSE :ACCEPTID END"
           )
                                                                                       
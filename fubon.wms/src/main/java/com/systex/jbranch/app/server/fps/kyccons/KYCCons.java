package com.systex.jbranch.app.server.fps.kyccons;


public class KYCCons {
	/**列印空白問卷**/
	public static final String PRINT_QUESTIONNAIRE = "print";
	/**列印英文空白問卷**/
	public static final String PRINT_QUESTIONNAIRE_ENG = "print_ENG";
	/**填寫問卷**/
	public static final String FILL_QUESTIONNAIRE_OVER = "submit";
	/**在職且為ARM的組織人員**/
	public static final String ARM = "060";
	/**在職且為交易員**/
	public static final String TRADER = "TRADER";
	/**個金OP**/
	public static final String PERSONAL_FINANCE_OP = "005";
	/**法金OP**/
	public static final String LEGAL_PERSON_FINANCE_OP = "059";
	/**個金主管**/
	public static final String[] PERSONAL_FINANCE_BOSS = {"008" , "007" , "006" , "009" , "010" , "011" , "012"};
	/**法金主管**/
	public static final String LEGAL_PERSON_FINANCE_BOSS = "058";
	/**法金、個金副總處長／總處長**/
	public static final String[] COMMERCIAL_PERSONAL_FINANCE_DIRECTOR = {"032" , "055" , "056"};
	/**分行有權簽章**/
	public static final String BRANCH_SIGNATURE = "007";
	/**分行覆核人員**/
	public static final String BRANCH_AUTHORIZED = "008";
}
